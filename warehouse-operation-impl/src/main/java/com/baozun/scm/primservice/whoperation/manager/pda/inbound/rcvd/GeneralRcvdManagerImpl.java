package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
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

import com.baozun.scm.baseservice.sac.manager.CodeManager;
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
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdSnLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("generalRcvdManager")
@Transactional
public class GeneralRcvdManagerImpl extends BaseManagerImpl implements GeneralRcvdManager {
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
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
    @Autowired
    private WhAsnRcvdLogDao whAsnRcvdLogDao;
    @Autowired
    private WhAsnRcvdSnLogDao whAsnRcvdSnLogDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private SysDictionaryDao sysDictionaryDao;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private StoreDefectTypeDao storeDefectTypeDao;
    @Autowired
    private WarehouseDefectTypeDao warehouseDefectTypeDao;
    @Autowired
    private StoreDefectReasonsDao storeDefectReasonsDao;
    @Autowired
    private WarehouseDefectReasonsDao warehouseDefectReasonsDao;
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
            Long ouId = commandList.get(0).getOuId();// OUID
            Long userId = commandList.get(0).getCreatedId();// 用户ID
            // 获取ASN
            WhAsn asn = this.whAsnDao.findWhAsnById(asnId, ouId);
            if (null == asn) {
                throw new BusinessException("1");
            }
            // 将数据按照明细ID筛选，统计数目，放到MAP集合中
            Map<Long, Double> lineMap = new HashMap<Long, Double>();
            Map<String, WhAsnRcvdLog> rcvdLogMap = new HashMap<String, WhAsnRcvdLog>();
            Map<String, WhSkuInventory> skuInvMap = new HashMap<String, WhSkuInventory>();
            // 1.保存库存
            // 2.筛选ASN明细数据集合
            for (RcvdCacheCommand cacheInv : commandList) {
                String occupationCode = cacheInv.getOccupationCode();
                Long lineId=cacheInv.getLineId();
                if (lineMap.containsKey(lineId)) {
                    lineMap.put(cacheInv.getLineId(), lineMap.get(lineId) + cacheInv.getSkuBatchCount());
                } else {
                    lineMap.put(cacheInv.getLineId(), cacheInv.getSkuBatchCount().doubleValue());
                }
                WhSkuInventory skuInv = new WhSkuInventory();
                BeanUtils.copyProperties(cacheInv, skuInv);

                skuInv.setCustomerId(asn.getCustomerId());
                skuInv.setStoreId(asn.getStoreId());
                skuInv.setOuId(cacheInv.getOuId());
                String uuid = "";

                // 测试用
                // skuInv.setId((long) Math.random() * 1000000);
                try {
                    uuid = SkuInventoryUuid.invUuid(skuInv);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (skuInvMap.containsKey(uuid)) {
                    skuInv = skuInvMap.get(uuid);
                    skuInv.setOnHandQty(skuInv.getOnHandQty() + cacheInv.getSkuBatchCount().longValue());
                } else {
                    skuInv.setUuid(uuid);
                    skuInv.setAllocatedQty(Constants.DEFAULT_DOUBLE);
                    skuInv.setToBeFilledQty(Constants.DEFAULT_DOUBLE);
                    skuInv.setFrozenQty(Constants.DEFAULT_DOUBLE);
                    skuInv.setOnHandQty(cacheInv.getSkuBatchCount().doubleValue());
                }
                skuInvMap.put(uuid, skuInv);
                // 插入日志表
                String asnRcvdLogMaoKey=lineId+uuid;
                WhAsnRcvdLog asnRcvdLog = new WhAsnRcvdLog();
                if(rcvdLogMap.containsKey(asnRcvdLogMaoKey)){
                    asnRcvdLog = rcvdLogMap.get(asnRcvdLogMaoKey);
                    asnRcvdLog.setQuantity(asnRcvdLog.getQuantity() + cacheInv.getSkuBatchCount().longValue());
                } else {
                    asnRcvdLog.setAsnId(cacheInv.getOccupationId());
                    asnRcvdLog.setAsnLineId(cacheInv.getLineId());
                    asnRcvdLog.setAsnCode(cacheInv.getOccupationCode());
                    Sku sku = this.skuDao.findByIdShared(cacheInv.getSkuId(), ouId);
                    asnRcvdLog.setSkuCode(sku.getCode());
                    asnRcvdLog.setSkuName(sku.getName());
                    asnRcvdLog.setQuantity(cacheInv.getSkuBatchCount().longValue());
                    Container container = this.containerDao.findByIdExt(cacheInv.getInsideContainerId(), ouId);
                    asnRcvdLog.setContainerCode(container.getCode());
                    asnRcvdLog.setContainerName(container.getName());
                    asnRcvdLog.setMfgDate(cacheInv.getMfgDate());
                    asnRcvdLog.setExpDate(cacheInv.getExpDate());
                    asnRcvdLog.setBatchNo(cacheInv.getBatchNumber());
                    asnRcvdLog.setCountryOfOrigin(cacheInv.getCountryOfOrigin());
                    asnRcvdLog.setInvStatus(cacheInv.getInvStatus().toString());
                    asnRcvdLog.setInvType(cacheInv.getInvType());
                    asnRcvdLog.setInvAttr1(cacheInv.getInvAttr1());
                    asnRcvdLog.setInvAttr2(cacheInv.getInvAttr2());
                    asnRcvdLog.setInvAttr3(cacheInv.getInvAttr3());
                    asnRcvdLog.setInvAttr4(cacheInv.getInvAttr4());
                    asnRcvdLog.setInvAttr5(cacheInv.getInvAttr5());
                    asnRcvdLog.setOuId(ouId);
                    asnRcvdLog.setCreateTime(new Date());
                    asnRcvdLog.setLastModifyTime(new Date());
                    asnRcvdLog.setOperatorId(userId);
                }
                rcvdLogMap.put(asnRcvdLogMaoKey, asnRcvdLog);
                if (null != cacheInv.getSnList()) {
                    for (RcvdSnCacheCommand RcvdSn : cacheInv.getSnList()) {
                        WhSkuInventorySn skuInvSn = new WhSkuInventorySn();
                        skuInvSn.setDefectTypeId(RcvdSn.getDefectTypeId());
                        skuInvSn.setDefectReasonsId(RcvdSn.getDefectReasonsId());
                        skuInvSn.setStatus(Constants.INVENTORY_SN_STATUS_ONHAND);
                        // #条码 调用条码生成器
                        String barCode = this.codeManager.generateCode(Constants.WMS, Constants.INVENTORY_SN_BARCODE, null, Constants.INVENTORY_SN_BARCODE_PREFIX, null);
                        skuInvSn.setDefectWareBarcode(barCode);
                        skuInvSn.setOuId(ouId);
                        skuInvSn.setUuid(uuid);
                        skuInvSn.setOccupationCode(occupationCode);
                        whSkuInventorySnDao.insert(skuInvSn);
                        // 插入日志表
                        WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
                        whAsnRcvdSnLog.setAsnRcvdId(asnId);
                        whAsnRcvdSnLog.setSn(RcvdSn.getSn());
                        whAsnRcvdSnLog.setDefectWareBarcode(barCode);
                        whAsnRcvdSnLog.setOuId(ouId);
                        // #取得残次类型残次原因的名称。
                        StoreDefectType storeDefectType = this.storeDefectTypeDao.findById(RcvdSn.getDefectTypeId());
                        if (null == storeDefectType) {
                            WarehouseDefectType warehouseDefectType = this.warehouseDefectTypeDao.findByIdExt(RcvdSn.getDefectTypeId(), ouId);
                            if (warehouseDefectType != null) {
                                whAsnRcvdSnLog.setDefectType(warehouseDefectType.getName());
                            } else {
                                whAsnRcvdSnLog.setDefectType(RcvdSn.getDefectTypeId().toString());
                            }
                            WarehouseDefectReasons warehouseDefectReasons = this.warehouseDefectReasonsDao.findByIdExt(RcvdSn.getDefectReasonsId(), ouId);
                            if (warehouseDefectReasons != null) {
                                whAsnRcvdSnLog.setDefectReasons(warehouseDefectReasons.getName());
                            } else {
                                whAsnRcvdSnLog.setDefectReasons(RcvdSn.getDefectReasonsId().toString());
                            }
                        } else {
                            whAsnRcvdSnLog.setDefectType(storeDefectType.getName());
                            StoreDefectReasons storeDefectReasons = this.storeDefectReasonsDao.findById(RcvdSn.getDefectReasonsId());
                            if (storeDefectReasons != null) {
                                whAsnRcvdSnLog.setDefectReasons(storeDefectReasons.getName());
                            } else {
                                whAsnRcvdSnLog.setDefectReasons(RcvdSn.getDefectReasonsId().toString());
                            }
                        }
                        this.whAsnRcvdSnLogDao.insert(whAsnRcvdSnLog);
                    }

                }
            }
            // 更新库存表
            Iterator<WhSkuInventory> skuInvMapIt = skuInvMap.values().iterator();
            while (skuInvMapIt.hasNext()) {
                WhSkuInventory s = skuInvMapIt.next();
                this.whSkuInventoryDao.insert(s);
            }
            // 更新收货日志表
            Iterator<WhAsnRcvdLog> rcvdLogMapIt = rcvdLogMap.values().iterator();
            while (rcvdLogMapIt.hasNext()) {
                WhAsnRcvdLog whAsnRcvdLog = rcvdLogMapIt.next();
                this.whAsnRcvdLogDao.insert(whAsnRcvdLog);
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
