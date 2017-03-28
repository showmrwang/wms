package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ma.TransportProviderDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.TransportProvider;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("biPoManager")
@Transactional
public class BiPoManagerImpl extends BaseManagerImpl implements BiPoManager {
    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private BiPoTransportMgmtDao biPoTransportMgmtDao;
    @Autowired
    private BiPoLineDao biPoLineDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoTransportMgmtDao whPoTransportMgmtDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private TransportProviderDao transportProviderDao;
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private CodeManager codeManager;
    
    protected static final Logger log = LoggerFactory.getLogger(BiPoManager.class);

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<BiPo> findListByParam(BiPo biPo) {
        return this.biPoDao.findListByParam(biPo);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoById(Long id) {
        return this.biPoDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoByPoCode(String poCode) {
        return this.biPoDao.findbyPoCode(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoCommand findBiPoCommandById(Long id) {
        return this.biPoDao.findCommandbyId(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoCommand findBiPoCommandByPoCode(String poCode) {
        return this.biPoDao.findCommandbyPoCode(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<BiPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<BiPoCommand> pages = this.biPoDao.findListByQueryMapWithPageExt(page, sorts, params);
        if (null != pages) {
            List<BiPoCommand> list = pages.getItems();
            Set<String> dic1 = new HashSet<String>();
            Set<String> dic2 = new HashSet<String>();
            List<Long> customerIdList = new ArrayList<Long>();
            List<Long> storeIdList = new ArrayList<Long>();
            boolean b = false;
            if (null != list && list.size() > 0) {
                for (BiPoCommand command : list) {
                    if (StringUtils.hasText(command.getPoType().toString())) {
                        dic1.add(command.getPoType().toString());
                    }
                    if (StringUtils.hasText(command.getStatus().toString())) {
                        dic2.add(command.getStatus().toString());
                    }
                    // 客户
                    b = customerIdList.contains(command.getCustomerId());
                    if (!b) {
                        customerIdList.add(command.getCustomerId());
                    }
                    // 店铺
                    b = storeIdList.contains(command.getStoreId());
                    if (!b) {
                        storeIdList.add(command.getStoreId());
                    }
                }
                Map<String, List<String>> map = new HashMap<String, List<String>>();
                map.put(Constants.PO_TYPE, new ArrayList<String>(dic1));
                map.put(Constants.BIPO_STATUS, new ArrayList<String>(dic2));
                // 调用系统参数redis缓存方法获取对应数据
                Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
                // 调用客户redis缓存方法获取对应数据
                Map<Long, Customer> customer = findCustomerByRedis(customerIdList);
                Map<Long, Store> store = findStoreByRedis(storeIdList);
                // 封装数据放入List
                for (BiPoCommand command : list) {
                    Customer c = null;
                    Store s = null;
                    if (StringUtils.hasText(command.getPoType().toString())) {
                        // 通过groupValue+divValue获取对应系统参数对象
                        SysDictionary sys = dicMap.get(Constants.PO_TYPE + "_" + command.getPoType());
                        command.setPoTypeName(sys == null ? command.getPoType().toString() : sys.getDicLabel());
                    }
                    if (StringUtils.hasText(command.getStatus().toString())) {
                        // 通过groupValue+divValue获取对应系统参数对象
                        SysDictionary sys = dicMap.get(Constants.BIPO_STATUS + "_" + command.getStatus());
                        command.setStatusName(sys == null ? command.getStatus().toString() : sys.getDicLabel());
                    }
                    // 客户                    
                    c = customer.get(command.getCustomerId());
                    if (null == c) {
                        command.setCustomerName(command.getCustomerId().toString());
                    } else {
                        command.setCustomerName(c.getCustomerName());
                    }
                    // 商铺                   
                    s = store.get(command.getStoreId());
                    if (null == s) {
                        command.setStoreName(command.getStoreId().toString());
                    } else {
                        command.setStoreName(s.getStoreName());
                    }
                }
                pages.setItems(list);
            }
        }
        return pages;
        
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createPoAndLineToInfo(WhPo po, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines) {
        // create-po分支：
        // 逻辑:
        // 1.没有指定仓库，则只需要插入BIPO
        // 2.指定仓库,则需要插入BIPO，并将数据同步到INFO的WHPO中；
        // 3.指定了仓库，状态为已分配仓库状态。而仓库为新建状态
        log.info("BiPoManager.createPoAndLineToInfo method begin! params:[po:{},whPoLines:{}]", po, whPoLines);
        if (log.isDebugEnabled()) {
            log.debug("BiPoManager.createPoAndLineToInfo start inserting data to info.bipo/info.whpo!orgin:[data:{}]", po);
        }
        
        // 插入BIPO
        BiPo biPo = new BiPo();// biPo为插入到BIPO的实体对象;po为插入到INFO.WHPO的对象
        BeanUtils.copyProperties(po, biPo);
        biPo.setStatus(null == po.getOuId() ? PoAsnStatus.BIPO_NEW : PoAsnStatus.BIPO_ALLOT);
        long bipocount = biPoDao.insert(biPo);
        if (bipocount <= Constants.DEFAULT_LONG) {
            log.error("BiPoManager.createPoAndLineToInfo method insert into BIPO error:[insertCount:{},insertData:{}]", bipocount, biPo);
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_INSERT, biPo, po.getOuId(), po.getCreatedId(), null, null);
        
        // 插入BiPoTransportMgmt
        if (null != whPoTm) {
        	BiPoTransportMgmt biPoTransportMgmt = new BiPoTransportMgmt();
			BeanUtils.copyProperties(whPoTm, biPoTransportMgmt);
			biPoTransportMgmt.setPoId(biPo.getId());
			long insertCount = biPoTransportMgmtDao.insert(biPoTransportMgmt);
			if (insertCount <= 0) {
	            log.error("BiPoManager.createPoAndLineToInfo method insert into BiPoTransportMgmt error:[insertCount:{},insertData:{}]", insertCount, biPoTransportMgmt);
	            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
	        }
	        this.insertGlobalLog(GLOBAL_LOG_INSERT, biPoTransportMgmt, po.getOuId(), po.getCreatedId(), null, null);
		}
        
        if (po.getOuId() != null) {
            // 插入WHPO
            po.setStatus(PoAsnStatus.PO_NEW);
            // 插入info.whpo
            long whpocount = whPoDao.insert(po);
            if (whpocount <= Constants.DEFAULT_LONG) {
                log.error("BiPoManager.createPoAndLineToInfo method insert into BIPO error:[insertCount:{},insertData:{}]", whpocount, po);
                throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_INSERT, po, po.getOuId(), po.getCreatedId(), null, null);
        }
        // 有line信息保存
        if (whPoLines != null && whPoLines.size() > Constants.DEFAULT_INTEGER) {
            if (log.isDebugEnabled()) {
                log.debug("BiPoManager.createPoAndLineToInfo start iterating lines to insert to info.bipoLine/info.whpoLine. origin:[data:{}]", whPoLines);
            }
            for (WhPoLine whPoLine : whPoLines) {
                // 插入BIPOLINE
                whPoLine.setPoId(biPo.getId());
                BiPoLine biPoLine = new BiPoLine();
                BeanUtils.copyProperties(whPoLine, biPoLine);
                if (po.getOuId() != null) {
                    biPoLine.setAvailableQty(Constants.DEFAULT_DOUBLE);
                    biPoLine.setStatus(PoAsnStatus.BIPOLINE_ALLOT);
                } else {
                    biPoLine.setAvailableQty(biPoLine.getQtyPlanned());
                    biPoLine.setStatus(PoAsnStatus.BIPOLINE_NEW);
                }
                long bipoLineCount = biPoLineDao.insert(biPoLine);
                if (bipoLineCount <= Constants.DEFAULT_LONG) {
                    log.error("BiPoManager.createPoAndLineToInfo method insert into BIPOLINE error:[insertCount:{},insertData:{}]", bipoLineCount, biPoLine);
                    throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_INSERT, biPoLine, po.getOuId(), po.getCreatedId(), po.getPoCode(), null);
                if (po.getOuId() != null) {
                    // 插入WHPOLINE
                    whPoLine.setId(null);
                    whPoLine.setPoId(po.getId());
                    whPoLine.setPoLineId(biPoLine.getId());
                    whPoLine.setStatus(PoAsnStatus.POLINE_NEW);
                    whPoLine.setAvailableQty(whPoLine.getQtyPlanned());
                    long whPoLineCount = whPoLineDao.insert(whPoLine);
                    if (whPoLineCount <= Constants.DEFAULT_LONG) {
                        log.error("BiPoManager.createPoAndLineToInfo method insert into INFO.WHPOLINE error:[insertCount:{},insertData:{}]", whPoLineCount, whPoLine);
                        throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                    }
                    this.insertGlobalLog(GLOBAL_LOG_INSERT, whPoLine, po.getOuId(), po.getCreatedId(), po.getPoCode(), null);

                }
            }
        }
        log.info("BiPoManager.createPoAndLineToInfo method end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createPoAndLineToShared(WhPo po, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines) {
        // 逻辑:
        // 将INFO.WHPO数据同步到SHARD.WHPO中
        // 1.头信息同步：po
        // 2.明细信息同步：whpoLines
        log.info("BiPoManagerImpl.createPoAndLineToShared method begin!");
        // 插入SHARD.WHPO
        po.setId(null);
        po.setStatus(PoAsnStatus.PO_NEW);
        long i = whPoDao.insert(po);
        if (i <= 0) {
            log.error("BiPoManagerImpl.createPoAndLineToShared method insert data to shard.whpoerror!message:[insertCount:{},insertData:{}]", i, po);
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_INSERT, po, po.getOuId(), po.getModifiedId(), null, DbDataSource.MOREDB_SHARDSOURCE);
        
        // 插入BiPoTransportMgmt
        if (null != whPoTm) {
        	whPoTm.setPoId(po.getId());
			long insertCount = whPoTransportMgmtDao.insert(whPoTm);
			if (insertCount <= 0) {
	            log.error("BiPoManager.createPoAndLineToShared method insert into WhPoTransportMgmt error:[insertCount:{},insertData:{}]", insertCount, whPoTm);
	            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
	        }
	        this.insertGlobalLog(GLOBAL_LOG_INSERT, whPoTm, po.getOuId(), po.getCreatedId(), null, null);
		}
        
        // 插入SHARD.WHPOLINE
        if (whPoLines != null && whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setId(null);
                whPoLine.setPoId(po.getId());
                whPoLine.setStatus(PoAsnStatus.POLINE_NEW);
                whPoLine.setAvailableQty(whPoLine.getQtyPlanned());
                long il = whPoLineDao.insert(whPoLine);
                if (il <= 0) {
                    log.error("BiPoManagerImpl.createPoAndLineToShared method iterate data for inserting data to shard.whpoline error!message:[insertCount:{},insertData:{}]", il, whPoLine);
                    throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_INSERT, whPoLine, whPoLine.getOuId(), whPoLine.getModifiedId(), null, DbDataSource.MOREDB_SHARDSOURCE);
            }
        }
        // 上位系统传入入库单据,直接创建Asn
        if (null != po.getIsVmi() && po.getIsVmi()) {
        	log.info("BiPoManagerImpl.createAsnByVmi");
        	WhAsnCommand asn = new WhAsnCommand();
        	asn.setOuId(po.getOuId());
        	asn.setUserId(po.getCreatedId());
        	asn.setModifiedId(po.getModifiedId());
        	// WMS单据号 调用HUB编码生成器获得
            String asnCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_INNER, null, null);
            if (StringUtil.isEmpty(asnCode)) {
                log.warn("CreateAsnBatch warn asnCode generateCode is null");
                throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
            }
            asn.setAsnCode(asnCode);
            String asnExtCode = this.getAsnExtCode(po.getStoreId(), po.getOuId());
            asn.setAsnExtCode(asnExtCode);
			asnManager.createAsnBatch(asn, po, whPoLines);
		}
        log.info("BiPoManagerImpl.createPoAndLineToShared method end!");
    }
    
    /**
     * 生成AsnExtCode
     * 
     * @param ouId
     * @param storeId
     * @return
     */
    private String getAsnExtCode(Long storeId, Long ouId) {
        String asnExtCode = null;
        boolean isSuccess = false;
        // 验证asnextcode是否存在 最多调用接口5次
        for (int i = 0; i <= 5; i++) {
            if (true == isSuccess) {
                break;
            }
            asnExtCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_EXT, null, null);
            if (StringUtil.isEmpty(asnExtCode)) {
                log.warn("CreateAsnBatch warn asnExtCode generateCode is null");
                throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
            }

            WhAsnCommand search = new WhAsnCommand();
            search.setAsnExtCode(asnExtCode);
            search.setOuId(ouId);
            search.setStoreId(storeId);
            List<WhAsnCommand> searchList = this.asnManager.findListByParamsExt(search);
            // 如果没有 直接结束
            if (searchList == null || searchList.size() == 0) {
                isSuccess = true;
            }
        }
        if (!isSuccess) {
            // 如果5次获取都失败了 直接返回失败
            log.warn("CreateAsnBatch warn asnExtCode generateCode CheckAsnCode is not null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
        }
        return asnExtCode;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deleteBiPoAndLine(Long id, Long userId) {
        List<BiPoLine> lines = this.biPoLineDao.findBiPoLineByPoIdAndUuid(id, null);
        if (lines != null) {
            for (BiPoLine line : lines) {
                int dpl = this.biPoLineDao.delete(line.getId());
                if (dpl < 0) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_DELETE, line, null, userId, line.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
            }
        }
        BiPo bipo = this.biPoDao.findById(id);
        int dp = this.biPoDao.delete(id);
        if (dp < 0) {
            throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_DELETE, bipo, null, userId, null, DbDataSource.MOREDB_INFOSOURCE);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void cancelBiPo(Long id, Long userId) {
        List<BiPoLine> lines = this.biPoLineDao.findBiPoLineByPoIdAndUuid(id, null);
        if (lines != null) {
            for (BiPoLine line : lines) {
                line.setStatus(PoAsnStatus.BIPOLINE_CANCELED);
                line.setModifiedId(userId);
                int dpl = this.biPoLineDao.saveOrUpdateByVersion(line);
                if (dpl < 0) {
                    throw new BusinessException(ErrorCodes.CANCEL_PO_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, line, null, userId, line.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
            }
        }
        BiPo bipo = this.biPoDao.findById(id);
        if (null == bipo) {
            throw new BusinessException(ErrorCodes.CANCEL_PO_ERROR);
        }
        bipo.setStatus(PoAsnStatus.BIPO_CANCELED);
        bipo.setModifiedId(userId);
        int dp = this.biPoDao.saveOrUpdateByVersion(bipo);
        if (dp < 0) {
            throw new BusinessException(ErrorCodes.CANCEL_PO_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, bipo, null, userId, null, DbDataSource.MOREDB_INFOSOURCE);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editBiPo(BiPo updatePo) {
        int updatecount = this.biPoDao.saveOrUpdateByVersion(updatePo);
        if (updatecount < 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, updatePo, null, updatePo.getModifiedId(), null, DbDataSource.MOREDB_INFOSOURCE);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createSubPoToInfo(WhPo po, List<WhPoLine> whPoLineList) {
        if (null == po.getId()) {
            this.whPoDao.insert(po);
        } else {
            this.whPoDao.saveOrUpdateByVersion(po);
        }
        for (WhPoLine line : whPoLineList) {
            if (null == line.getId()) {
                line.setPoId(po.getId());
                this.whPoLineDao.insert(line);
            } else {
                this.whPoLineDao.saveOrUpdateByVersion(line);
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveSubPoToInfo(Long id, String extCode, Long storeId, Long ouId, String uuid, Long userId) {
        // 逻辑：
        // 1.删除可用数量为0的临时数据
        // 2.1保存临时数据【info.whpo.polineId 不为空，则合并明细 并清除数据；如果为空则清除UUID】
        // 2.2占用对应明细的可用数量和修改状态
        // 3.修改BIPO头信息【状态】
        WhPo whpo = this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
        List<WhPoLine> whpoLineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(whpo.getId(), ouId, uuid);
        Double count = Constants.DEFAULT_DOUBLE;
        for (WhPoLine whpoline : whpoLineList) {
            if (Constants.DEFAULT_DOUBLE >= whpoline.getQtyPlanned()) {
                this.whPoLineDao.deletePoLineByIdOuId(whpoline.getId(), ouId);
            } else {
                double lineQtyPlanned=whpoline.getQtyPlanned();

                BiPoLine biPoLine = this.biPoLineDao.findById(whpoline.getPoLineId());
                if (biPoLine.getAvailableQty() < lineQtyPlanned) {
                    throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
                }
                count += lineQtyPlanned;
                WhPoLine savedPoLine =
                        this.whPoLineDao
                                .findPoLineByPolineIdAndStatusListAndPoIdAndOuId(whpoline.getPoLineId(), Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD}), whpoline.getPoId(), ouId, null);
                if (null == savedPoLine) {
                    whpoline.setUuid(null);
                    whpoline.setModifiedId(userId);
                    this.whPoLineDao.saveOrUpdateByVersion(whpoline);
                } else {
                    savedPoLine.setQtyPlanned(savedPoLine.getQtyPlanned() + whpoline.getQtyPlanned());
                    savedPoLine.setAvailableQty(savedPoLine.getAvailableQty() + whpoline.getQtyPlanned());
                    savedPoLine.setModifiedId(userId);
                    this.whPoLineDao.saveOrUpdateByVersion(savedPoLine);
                    this.whPoLineDao.delete(whpoline.getId());
                }
                biPoLine.setAvailableQty(biPoLine.getAvailableQty() - lineQtyPlanned);
                biPoLine.setModifiedId(userId);
                biPoLine.setStatus(PoAsnStatus.BIPOLINE_ALLOT);
                this.biPoLineDao.saveOrUpdateByVersion(biPoLine);
            }
        }
        whpo.setModifiedId(userId);
        whpo.setQtyPlanned(whpo.getQtyPlanned() + count);
        whpo.setUuid(null);
        if (PoAsnStatus.PO_RCVD_FINISH == whpo.getStatus()) {
            whpo.setStatus(PoAsnStatus.PO_RCVD);
        } else if (PoAsnStatus.PO_CLOSE == whpo.getStatus()) {
            //这边的逻辑：
            //如果有实际收货数量的时候，则回滚到收货中状态，否则新建状态
            if (Constants.DEFAULT_DOUBLE == whpo.getQtyRcvd()) {
                whpo.setStatus(PoAsnStatus.PO_NEW);
            } else {
                whpo.setStatus(PoAsnStatus.PO_RCVD);
            }
        }
        // #自动关单逻辑
        BiPo bipo = this.biPoDao.findById(id);
        if (bipo.getIsAutoClose() != null && bipo.getIsAutoClose()) {
            Warehouse wh = this.getWhToRedis(ouId);
            if (wh.getPoOverchargeProportion() != null && wh.getPoOverchargeProportion() != 0) {
                whpo.setIsAutoClose(false);
                bipo.setIsAutoClose(false);
            } else {
                if (wh.getIsPoAutoVerify() != null && wh.getIsPoAutoVerify()) {

                } else {
                    whpo.setIsAutoClose(false);
                    bipo.setIsAutoClose(false);
                }
            }
        }

        this.whPoDao.saveOrUpdateByVersion(whpo);
        if (PoAsnStatus.BIPO_NEW == bipo.getStatus()) {
            bipo.setStatus(PoAsnStatus.BIPO_ALLOT);
            this.biPoDao.saveOrUpdateByVersion(bipo);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void closeSubPoToInfo(String extCode, Long storeId, Long ouId, Long userId) {
        WhPo infoPo = this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
        if (infoPo != null) {
            WhPoLineCommand searchCommand = new WhPoLineCommand();
            searchCommand.setPoId(infoPo.getId());
            searchCommand.setOuId(ouId);
            WhPoLine searchLine = new WhPoLine();
            BeanUtils.copyProperties(searchCommand, searchLine);
            List<WhPoLine> lineList = this.whPoLineDao.findListByParam(searchLine);
            boolean flag = true;
            if (lineList != null && lineList.size() > 0) {
                for (WhPoLine line : lineList) {
                    if (StringUtils.hasText(line.getUuid())) {
                        this.whPoLineDao.delete(line.getId());
                    } else {
                        if (flag) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag) {
                this.whPoDao.delete(infoPo.getId());
            } else {
                if (StringUtils.hasText(infoPo.getUuid())) {
                    infoPo.setUuid(null);
                    infoPo.setModifiedId(userId);
                    this.whPoDao.saveOrUpdateByVersion(infoPo);
                }
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<BiPo> findListByStoreIdExtCode(Long storeId, String extCode) {
        return this.biPoDao.findListByStoreIdExtCode(storeId, extCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<BiPo> findListByExtCode(String extCode) {
        return this.biPoDao.findListByExtCode(extCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoByExtCodeStoreId(String extCode, Long storeId) {
        return this.biPoDao.findBiPoByExtCodeStoreId(extCode, storeId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public TransportProvider findTransportProviderByCode(String logisticsProviderCode) {
        return transportProviderDao.findByCode(logisticsProviderCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Boolean calIsAutoClose(Long storeId, Long ouId) {
        Store store = this.getStoreByRedis(storeId);
        boolean flag = true;
        if ((store.getAsnOverchargeProportion() == null || store.getAsnOverchargeProportion().intValue() == 0) && (store.getPoOverchargeProportion() == null || store.getPoOverchargeProportion().intValue() == 0)) {
            if (ouId == null) {
                if (store.getIsPoAutoVerify() == null || store.getIsPoAutoVerify()) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                Warehouse wh = this.getWhToRedis(ouId);
                if ((wh.getAsnOverchargeProportion() == null || wh.getAsnOverchargeProportion().intValue() == 0) && (wh.getPoOverchargeProportion() == null || wh.getPoOverchargeProportion().intValue() == 0)) {
                    if (store.getIsPoAutoVerify() == null || store.getIsPoAutoVerify()) {
                        if (wh.getIsPoAutoVerify() == null || wh.getIsPoAutoVerify()) {
                            flag = true;
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

}
