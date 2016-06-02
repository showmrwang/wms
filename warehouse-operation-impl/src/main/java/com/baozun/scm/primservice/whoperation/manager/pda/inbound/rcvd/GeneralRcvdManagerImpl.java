package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lark.common.annotation.MoreDB;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("generalRcvdManager")
@Transactional
public class GeneralRcvdManagerImpl extends BaseManagerImpl implements GeneralRcvdManager {
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commandList) {
        // 逻辑:
        // 1.插入库存记录
        // 2.更新ASN明细
        // 3.更新ASN头信息
        // 4.更新PO明细
        // 5.更新PO头信息
        if (commandList != null && commandList.size() > 0) {
            Long asnId = commandList.get(0).getOccupationId();// ASN头ID
            Long ouId = null == commandList.get(0).getOuId() ? 119L : commandList.get(0).getOuId();// OUID
            Long userId = commandList.get(0).getCreatedId();// 用户ID
            // 获取ASN
            WhAsn asn = this.whAsnDao.findWhAsnById(asnId, ouId);
            if (null == asn) {
                throw new BusinessException("1");
            }
            // 将数据按照明细ID筛选，统计数目，放到MAP集合中
            Map<Long, Double> lineMap = new HashMap<Long, Double>();
            // 1.保存库存
            // 2.筛选ASN明细数据集合
            for (RcvdCacheCommand cacheInv : commandList) {
                List<RcvdSnCacheCommand> cacheSn = cacheInv.getSnList();
                if (lineMap.containsKey(cacheInv.getLineId())) {
                    lineMap.put(cacheInv.getLineId(), lineMap.get(cacheInv.getLineId()) + 1d);
                } else {
                    lineMap.put(cacheInv.getLineId(), 1d);
                }
                WhSkuInventory skuInv = new WhSkuInventory();
                BeanUtils.copyProperties(cacheInv, skuInv);

                skuInv.setCustomerId(asn.getCustomerId());
                skuInv.setStoreId(asn.getStoreId());
                skuInv.setOnHandQty(1d);
                skuInv.setOuId(cacheInv.getOuId());
                // 测试用
                // skuInv.setId((long) Math.random() * 1000000);
                try {
                    skuInv.setUuid(SkuInventoryUuid.invUuid(skuInv));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                skuInv.setAllocatedQty(Constants.DEFAULT_DOUBLE);
                skuInv.setToBeFilledQty(Constants.DEFAULT_DOUBLE);
                skuInv.setFrozenQty(Constants.DEFAULT_DOUBLE);
                this.whSkuInventoryDao.insert(skuInv);
            }
            // 更新ASN明细
            Iterator<Entry<Long, Double>> it = lineMap.entrySet().iterator();
            Double asnCount=Constants.DEFAULT_DOUBLE;
            Map<Long,Double> polineMap=new HashMap<Long,Double>();
            while (it.hasNext()) {
                Entry<Long, Double> entry = it.next();
                WhAsnLine asnLine = this.whAsnLineDao.findWhAsnLineById(entry.getKey(), ouId);
                if (null == asnLine) {
                    throw new BusinessException("1");
                }
                asnLine.setQtyRcvd(asnLine.getQtyRcvd() + entry.getValue());
                asnLine.setModifiedId(userId);
                if (asnLine.getQtyRcvd() >= asnLine.getQtyPlanned()) {
                    asnLine.setStatus(PoAsnStatus.ASNLINE_RCVD_FINISH);
                } else {
                    asnLine.setStatus(PoAsnStatus.ASNLINE_RCVD);
                }
                int updateAsnLineCount = this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
                if (updateAsnLineCount <= 0) {
                    throw new BusinessException("2");
                }
                if (polineMap.containsKey(asnLine.getPoLineId())) {
                    polineMap.put(asnLine.getPoLineId(), lineMap.get(asnLine.getPoLineId()) + entry.getValue());
                } else {
                    polineMap.put(asnLine.getPoLineId(), entry.getValue());
                }
                asnCount+=entry.getKey();
            }
            // 1.更新ASN明细
            // 2.筛选PO明细数据集合
            asn.setQtyRcvd(asn.getQtyRcvd()+asnCount);
            WhAsnLineCommand searchAsnLineCommand=new WhAsnLineCommand();
            searchAsnLineCommand.setAsnId(asn.getId());
            searchAsnLineCommand.setOuId(ouId);
            searchAsnLineCommand.setStatus(PoAsnStatus.ASNLINE_RCVD);
            WhAsnLine searchAsnLine=new WhAsnLine();
            BeanUtils.copyProperties(searchAsnLineCommand, searchAsnLine);
            long rcvdlineCount=this.whAsnLineDao.findListCountByParam(searchAsnLine);
            if(rcvdlineCount>0){
                asn.setStatus(PoAsnStatus.ASN_RCVD_FINISH);
            }else{
                asn.setStatus(PoAsnStatus.ASN_RCVD);
            }
            asn.setModifiedId(userId);
            int updateAsnCount=this.whAsnDao.saveOrUpdateByVersion(asn);
            if(updateAsnCount<=0){
                throw new BusinessException("2");
            }
            Iterator<Entry<Long, Double>> poIt = polineMap.entrySet().iterator();
            Long poId = null;
            // 更新PO明细数据集合
            while(poIt.hasNext()){
                Entry<Long, Double> entry = poIt.next();
                WhPoLine poline=this.whPoLineDao.findWhPoLineByIdWhPoLine(entry.getKey(), ouId);
                poline.setQtyRcvd(poline.getQtyRcvd()+entry.getValue());
                if(poline.getQtyRcvd()>poline.getQtyPlanned()){
                    poline.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
                }else{
                    poline.setStatus(PoAsnStatus.POLINE_RCVD);
                }
                poline.setModifiedId(userId);
                int updatePoCount=this.whPoLineDao.saveOrUpdateByVersion(poline);
                if(updatePoCount<=0){
                    throw new BusinessException("2");
                }
                if (null == poId) {
                    poId = poline.getPoId();
                }
            }
            // 更新Po数据集合
            WhPo po = this.whPoDao.findWhPoById(poId, ouId);
            if (null == po) {
                throw new BusinessException("1");
            }
            po.setModifiedId(userId);
            po.setQtyRcvd(po.getQtyRcvd() + asnCount);
            WhPoLineCommand polineCommand = new WhPoLineCommand();
            polineCommand.setOuId(ouId);
            polineCommand.setStatus(PoAsnStatus.POLINE_RCVD);
            polineCommand.setPoId(poId);
            WhPoLine searchPoLine = new WhPoLine();
            BeanUtils.copyProperties(polineCommand, searchPoLine);
            long polinecount = this.whPoLineDao.findListCountByParam(searchPoLine);
            if (polinecount > 0) {
                po.setStatus(PoAsnStatus.PO_RCVD);
            } else {
                po.setStatus(PoAsnStatus.PO_RCVD_FINISH);
            }
            int updatePoCount = this.whPoDao.saveOrUpdateByVersion(po);
            if (updatePoCount <= 0) {
                throw new BusinessException("2");
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public long findContainerListCountByInsideContainerIdFromSkuInventory(Long insideContainerId, Long ouId) {
        WhSkuInventory search = new WhSkuInventory();
        search.setInsideContainerId(insideContainerId);
        search.setOuId(ouId);
        return this.whSkuInventoryDao.findListCountByParam(search);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public RcvdContainerCacheCommand getUniqueSkuAttrFromWhSkuInventory(Long insideContainerId, Long ouId) {
        return this.whSkuInventoryDao.getUniqueSkuAttrFromWhSkuInventory(insideContainerId, ouId);
    }

}
