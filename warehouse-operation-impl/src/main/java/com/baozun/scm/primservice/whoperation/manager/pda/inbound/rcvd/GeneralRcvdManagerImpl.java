package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuStandardPackingCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skushared.SkuCommand2Shared;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuBarcodeDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuExtattrDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuStandardPackingDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdSnLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionRcvdDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnLogDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuBarcode;
import com.baozun.scm.primservice.whoperation.model.sku.SkuExtattr;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySnLog;
import com.baozun.scm.primservice.whoperation.util.DateUtil;
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
    @Autowired
    private SkuStandardPackingDao skuStandardPackingDao;

    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private PkManager pkManager;
    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private WhFunctionRcvdDao whFunctionRcvdDao;
    @Autowired
    private SkuBarcodeDao skuBarcodeDao;
    @Autowired
    private SkuExtattrDao skuExtattrDao;
    @Autowired
    private SkuMgmtDao skuMgmtDao;
    @Autowired
    private ContainerAssistDao containerAssistDao;
    @Autowired
    private WhSkuInventorySnLogDao whSkuInventorySnLogDao;
    @Autowired
    private CacheManager cacheManager;

    @Override
    @Deprecated
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
                Long lineId = cacheInv.getLineId();
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
                String asnRcvdLogMaoKey = lineId + uuid;
                WhAsnRcvdLog asnRcvdLog = new WhAsnRcvdLog();
                if (rcvdLogMap.containsKey(asnRcvdLogMaoKey)) {
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
                        String barCode = this.codeManager.generateCode(Constants.WMS, Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null);
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
            Double asnCount = Constants.DEFAULT_DOUBLE;
            Map<Long, Double> polineMap = new HashMap<Long, Double>();
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
                asnCount += entry.getKey();
            }
            // 1.更新ASN明细
            // 2.筛选PO明细数据集合
            asn.setQtyRcvd(asn.getQtyRcvd() + asnCount);
            WhAsnLineCommand searchAsnLineCommand = new WhAsnLineCommand();
            searchAsnLineCommand.setAsnId(asn.getId());
            searchAsnLineCommand.setOuId(ouId);
            searchAsnLineCommand.setStatus(PoAsnStatus.ASNLINE_RCVD);
            WhAsnLine searchAsnLine = new WhAsnLine();
            BeanUtils.copyProperties(searchAsnLineCommand, searchAsnLine);
            long rcvdlineCount = this.whAsnLineDao.findListCountByParam(searchAsnLine);
            if (rcvdlineCount > 0) {
                asn.setStatus(PoAsnStatus.ASN_RCVD_FINISH);
            } else {
                asn.setStatus(PoAsnStatus.ASN_RCVD);
            }
            asn.setModifiedId(userId);
            int updateAsnCount = this.whAsnDao.saveOrUpdateByVersion(asn);
            if (updateAsnCount <= 0) {
                throw new BusinessException("2");
            }
            Iterator<Entry<Long, Double>> poIt = polineMap.entrySet().iterator();
            Long poId = null;
            // 更新PO明细数据集合
            while (poIt.hasNext()) {
                Entry<Long, Double> entry = poIt.next();
                WhPoLine poline = this.whPoLineDao.findWhPoLineById(entry.getKey(), ouId);
                poline.setQtyRcvd(poline.getQtyRcvd() + entry.getValue());
                if (poline.getQtyRcvd() > poline.getQtyPlanned()) {
                    poline.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
                } else {
                    poline.setStatus(PoAsnStatus.POLINE_RCVD);
                }
                poline.setModifiedId(userId);
                int updatePoCount = this.whPoLineDao.saveOrUpdateByVersion(poline);
                if (updatePoCount <= 0) {
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
    public List<RcvdContainerCacheCommand> getUniqueSkuAttrFromWhSkuInventory(Long insideContainerId, Long skuId, Long ouId) {
        return this.whSkuInventoryDao.getUniqueSkuAttrFromWhSkuInventory(insideContainerId, skuId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Sku findSkuByIdToShard(Long id, Long ouId) {
        return this.skuDao.findByIdShared(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Container findContainerByIdToShard(Long id, Long ouId) {
        return this.containerDao.findByIdExt(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public StoreDefectType findStoreDefectTypeByIdToGlobal(Long id) {
        return this.storeDefectTypeDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public StoreDefectReasons findStoreDefectReasonsByIdToGlobal(Long id) {
        return this.storeDefectReasonsDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WarehouseDefectType findWarehouseDefectTypeByIdToShard(Long id, Long ouId) {
        return this.warehouseDefectTypeDao.findByIdExt(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WarehouseDefectReasons findWarehouseDefectReasonsByIdToShard(Long id, Long ouId) {
        return this.warehouseDefectReasonsDao.findByIdExt(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveScanedSkuWhenGeneralRcvdForPda(List<WhSkuInventorySnCommand> saveSnList, List<WhSkuInventory> saveInvList, List<WhAsnRcvdLogCommand> saveInvLogList, List<WhAsnLine> saveAsnLineList, WhAsn asn, List<WhPoLine> savePoLineList, WhPo po,
            Container container, List<WhCarton> saveWhCartonList, Warehouse wh) {
        try {
            Long userId = po.getModifiedId();
            // 保存残次信息
            for (WhSkuInventorySnCommand snCommand : saveSnList) {
                WhSkuInventorySn sn = new WhSkuInventorySn();
                BeanUtils.copyProperties(snCommand, sn);
                this.whSkuInventorySnDao.insert(sn);
                // 插入SN日志
                WhSkuInventorySnLog snLog = new WhSkuInventorySnLog();
                snLog.setDefectReasons(snCommand.getDefectReasonsName());
                snLog.setDefectType(snCommand.getDefectTypeName());
                snLog.setDefectWareBarcode(snCommand.getDefectWareBarcode());
                snLog.setOccupationCode(snCommand.getOccupationCode());
                snLog.setOuId(snCommand.getOuId());
                snLog.setStatus(snCommand.getStatus());
                snLog.setSn(snCommand.getSn());
                snLog.setSysDate(DateUtil.getSysDate());
                snLog.setUuid(snCommand.getUuid());
                this.whSkuInventorySnLogDao.insert(snLog);
            }
            // 更新装箱信息表
            for (WhCarton whCarton : saveWhCartonList) {
                this.whCartonDao.insert(whCarton);
            }
            // 保存库存记录
            for (WhSkuInventory inv : saveInvList) {
                WhSkuInventory skuInv = this.whSkuInventoryDao.findWhSkuInventoryByUuid(inv.getOuId(), inv.getUuid());
                if (null == skuInv) {
                    this.whSkuInventoryDao.insert(inv);
                    this.insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), Constants.DEFAULT_DOUBLE, wh.getIsTabbInvTotal(), inv.getOuId(), userId);
                } else {
                    Double oldQty = skuInv.getOnHandQty();
                    skuInv.setOnHandQty(skuInv.getOnHandQty() + inv.getOnHandQty());
                    this.whSkuInventoryDao.saveOrUpdateByVersion(skuInv);
                    // 插入库存日志
                    this.insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty() - oldQty, oldQty, wh.getIsTabbInvTotal(), skuInv.getOuId(), userId);
                }
            }
            // 保存收货日志
            for (WhAsnRcvdLogCommand invLogCommand : saveInvLogList) {
                WhAsnRcvdLog invLog = new WhAsnRcvdLog();
                BeanUtils.copyProperties(invLogCommand, invLog);
                this.whAsnRcvdLogDao.insert(invLog);
                // 保存残次日志
                if (null != invLogCommand.getWhAsnRcvdSnLogList()) {
                    for (WhAsnRcvdSnLog snlog : invLogCommand.getWhAsnRcvdSnLogList()) {
                        snlog.setAsnRcvdId(invLog.getId());
                        this.whAsnRcvdSnLogDao.insert(snlog);
                    }
                }
            }
            // 更新ASN明细
            for (WhAsnLine asnline : saveAsnLineList) {
                int updateCount = this.whAsnLineDao.saveOrUpdateByVersion(asnline);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            // 更新ASN
            boolean isAsnRcvdFinished = this.whAsnDao.checkIsRcvdFinished(asn.getId(), asn.getOuId());
            if (isAsnRcvdFinished) {
                asn.setStatus(PoAsnStatus.ASN_RCVD_FINISH);
                asn.setStopTime(new Date());
            } else {
                asn.setStatus(PoAsnStatus.ASN_RCVD);
            }
            int updateAsnCount = this.whAsnDao.saveOrUpdateByVersion(asn);
            if (updateAsnCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 更新PO明细
            for (WhPoLine poline : savePoLineList) {
                int updateCount = this.whPoLineDao.saveOrUpdateByVersion(poline);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            // 更新PO
            boolean isPoRcvdFinished = this.whPoDao.checkIsRcvdFinished(po.getId(), po.getOuId());
            if (isPoRcvdFinished) {
                po.setStatus(PoAsnStatus.PO_RCVD_FINISH);
                po.setStopTime(new Date());
            } else {
                po.setStatus(PoAsnStatus.PO_RCVD);
            }
            int updatePoCount = this.whPoDao.saveOrUpdateByVersion(po);
            if (updatePoCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 更新容器
            if (container != null) {
                int updateContainerCount = this.containerDao.saveOrUpdateByVersion(container);
                if (updateContainerCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 更新容器辅助表 TODO
                this.updateContainerAssistByVersion(container.getId(), container.getOuId());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }

    }

    private void updateContainerAssistByVersion(Long id, Long ouId) {
        this.containerAssistDao.findByContainerId(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateContainerByVersion(Container container) {
        return this.containerDao.saveOrUpdateByVersion(container);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<SkuStandardPackingCommand> findSkuStandardPacking(String skuBarCode, Long ouId, String logId) {
        return skuStandardPackingDao.findSkuStandardPackingBySkuBarCode(skuBarCode, ouId, BaseModel.LIFECYCLE_NORMAL);
    }

    // @Override
    // @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    // public Long findContainerId(Long skuId, String code, Long ouId, Integer lc, Long
    // containerTypeId) {
    // /* 获取符合条件的更新个数 */
    // // int cnt = containerDao.updateContainerStatusByCode(code, ouId, lifecycle, typeList);
    // ContainerCommand containerCommand = new ContainerCommand();
    // containerCommand.setCode(code);
    // containerCommand.setOuId(ouId);
    // // containerCommand.setTwoLevelType(containerTypeId);
    // // containerCommand.setTwoLevelType(14100000L);
    // // 返回一个command list
    // List<ContainerCommand> list = containerDao.getContainerByCodeAndType(containerCommand);
    //
    // if (null == list || list.isEmpty() || null == list.get(0)) {
    // throw new BusinessException("没有匹配的容器");
    // }
    // if (1 < (list.size())) {
    // throw new BusinessException("查找到多个容器");
    // }
    // ContainerCommand command = list.get(0);
    // Integer lifecycle = command.getLifecycle();
    // if (ContainerStatus.CONTAINER_LIFECYCLE_USABLE == lifecycle/* && 1 == command.getIsUsed() */)
    // {
    // // 实际上是返回容器id
    // return command.getId();
    // } else {
    // throw new BusinessException("找到的容器不符合");
    // }
    // }

    @Override
    public SkuCommand findSkuBySkuCodeOuId(String skuCode, Long ouId, Long customerId) {
        SkuCommand skuCommand = new SkuCommand();
        skuCommand.setBarCode(skuCode);
        skuCommand.setOuId(ouId);
        skuCommand.setCustomerId(customerId);
        List<SkuCommand> skuList = this.skuDao.findListByParamShared(skuCommand);
        if (null != skuList && !skuList.isEmpty()) {
            skuList.get(0).setQuantity(1L);
            // return skuList.get(0);
            skuCommand = skuList.get(0);
        } else {
            skuCommand.setBarCode(null);
            skuCommand.setBatchBarcode(skuCode);
            List<SkuCommand> skuBarcodeList = this.skuDao.findListByBarcode(skuCommand);
            if (null == skuBarcodeList || skuBarcodeList.isEmpty()) {
                throw new BusinessException("结果为空");
            }
            // return skuBarcodeList.get(0);
            skuCommand = skuBarcodeList.get(0);
        }
        String unit = skuCommand.getGoodShelfLifeUnit();
        if (null != unit) {
            Integer day = skuCommand.getValidDate();
            if (null != day) {
                if (Constants.TIME_UOM_YEAR.equals(unit)) {
                    day = day * 365;
                } else if (Constants.TIME_UOM_MONTH.equals(unit)) {
                    day = day * 30;
                }
                skuCommand.setValidDate(day);
            }
        }
        return skuCommand;
    }

    @Override
    public List<SkuStandardPackingCommand> checkSkuStandardPacking(String skuBarCode, Long ouId, String logId) {
        List<SkuStandardPackingCommand> sspList = skuStandardPackingDao.findSkuStandardPackingBySkuBarCode(skuBarCode, ouId, BaseModel.LIFECYCLE_NORMAL);
        if (sspList.size() <= 0) {
            throw new BusinessException("没有找到标准装箱");
        }
        return sspList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Container insertByCode(ContainerCommand container, Long userId, Long ouId) {
        /**
         * 逻辑：1.生成系统预定义容器； 如果预定义容器的二级容器类型不存在，那么生成容器的二级容器
         */
        Container saveContainer = new Container();// 需要保存的容器对象
        saveContainer.setCode(container.getCode());
        saveContainer.setOneLevelType(container.getOneLevelType());
        saveContainer.setCreateTime(new Date());
        saveContainer.setLastModifyTime(new Date());
        saveContainer.setOperatorId(userId);
        saveContainer.setLifecycle(container.getLifecycle());
        saveContainer.setOuId(ouId);
        saveContainer.setStatus(container.getStatus());
        // 一级容器类型，设置容器的一级容器类型 这个字段存储为ID，需要获取系统字典表对应的Id
        SysDictionary searchDictionary = new SysDictionary();
        searchDictionary.setDicValue(container.getOneLevelTypeValue());
        searchDictionary.setGroupValue(Constants.DICTIONARY_CONTAINTER_TYPE);
        List<SysDictionary> dicList = this.sysDictionaryDao.findListByParam(searchDictionary);
        if (null == dicList || dicList.size() == 0) {
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }


        saveContainer.setTwoLevelType(dicList.get(0).getId());
        // 二级容器类型 设置容器的二级容器类型；逻辑：如果不存在系统预定义的对应的二级容器类型，那么生成宇通预定义的二级容器对象
        Container2ndCategory secendContainer = new Container2ndCategory();
        secendContainer.setCategoryCode(container.getTwoLevelTypeValue());
        secendContainer.setOneLevelType(dicList.get(0).getDicValue());
        secendContainer.setOuId(ouId);
        List<Container2ndCategory> secondContainerList = this.container2ndCategoryDao.findListByParam(secendContainer);// 查询是否有系统预定义的容器类型
        if (secondContainerList != null && secondContainerList.size() > 0) {
            secendContainer = secondContainerList.get(0);// 如果有的话，则获得系统预定义的二级容器类型
        } else {// 没有的话，需要生成预定义的二级容器类型
            Long secendContainerId = this.pkManager.generatePk(Constants.WMS, Constants.CONTAINER_MODEL_URL);
            secendContainer.setId(secendContainerId);
            // 获取预定义的二级容器编码对应的名称
            SysDictionary search2Dictionary = new SysDictionary();
            search2Dictionary.setDicValue(container.getTwoLevelTypeValue());
            search2Dictionary.setGroupValue(Constants.DICTIONARY_CONTAINTER_SECOND_TYPE);
            List<SysDictionary> dic2List = this.sysDictionaryDao.findListByParam(search2Dictionary);
            if (null == dic2List || dic2List.size() == 0) {
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
            secendContainer.setCategoryName(dic2List.get(0).getDicLabel());
            // 基础信息设置
            secendContainer.setCreateTime(new Date());
            secendContainer.setLastModifyTime(new Date());
            secendContainer.setOperatorId(userId);
            secendContainer.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
            // 插入二级容器
            long insertSecondCount = this.container2ndCategoryDao.insert(secendContainer);
            if (insertSecondCount == 0) {
                throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
            }
        }

        // 插入容器
        saveContainer.setTwoLevelType(secendContainer.getId());
        saveContainer.setName(secendContainer.getCategoryName());
        long insertCount = this.containerDao.insert(saveContainer);
        if (insertCount == 0) {
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }
        return saveContainer;
    }


    /***
     * 根据容器编码查找容器 无判断
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand findContainerByCode(String code, Long ouId) {
        return containerDao.getContainerByCode(code, ouId);
    }

    /***
     * 根据skuId 获取标准装箱类型
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<SkuStandardPackingCommand> getContainerType(Long skuId, Long ouId) {
        List<SkuStandardPackingCommand> list = this.skuStandardPackingDao.getContainerType(skuId, ouId);
        return list;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand findContainer(Long skuId, String code, Long ouId, Long containerTypeId, Long userId) {
        /* 获取符合条件的更新个数 */
        ContainerCommand containerCommand = new ContainerCommand();
        containerCommand.setCode(code);
        containerCommand.setOuId(ouId);
        containerCommand.setTwoLevelType(containerTypeId);
        containerCommand.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        containerCommand.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
        containerCommand.setSkuId(skuId);
        // 返回一个command list
        List<ContainerCommand> list = containerDao.getContainerByCodeAndType(containerCommand);

        if (null == list || list.isEmpty() || null == list.get(0)) {
            throw new BusinessException("没有匹配的容器");
        }
        if (1 < (list.size())) {
            throw new BusinessException("查找到多个容器");
        }
        ContainerCommand command = list.get(0);
        Integer lifecycle = command.getLifecycle();
        if (ContainerStatus.CONTAINER_LIFECYCLE_USABLE == lifecycle/* && 1 == command.getIsUsed() */) {
            // 实际上是返回容器id
            Container container = new Container();
            BeanUtils.copyProperties(command, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
            this.containerDao.update(container);
            RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
            cacheContainer.setUserId(userId);
            cacheContainer.setLifecycle(command.getLifecycle());
            cacheContainer.setStatus(command.getStatus());
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + command.getId(), cacheContainer);
            // RcvdContainerCacheCommand cacheContainer =
            // this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX +
            // containerId);
            return command;
        } else {
            throw new BusinessException("找到的容器不符合");
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public long getUniqueSkuAttrCountFromWhSkuInventory(Long insideContainerId, Long skuId, Long ouId) {
        return this.whSkuInventoryDao.getUniqueSkuAttrCountFromWhSkuInventory(insideContainerId, skuId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void rcvdPallet(Long outerContainerId, Long insideContainerId, Long ouId, Long userId) {
        try {
            Container pallet = this.containerDao.findByIdExt(outerContainerId, ouId);
            pallet.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
            pallet.setOperatorId(userId);
            int updateCount = this.containerDao.saveOrUpdateByVersion(pallet);
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            /*
             * Container container = this.containerDao.findByIdExt(insideContainerId, ouId);
             * container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
             * container.setOperatorId(userId); int containerCount =
             * this.containerDao.saveOrUpdateByVersion(container); if (containerCount <= 0) { throw
             * new BusinessException(ErrorCodes.UPDATE_DATA_ERROR); }
             */
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionRcvd findwFunctionRcvdByFunctionId(Long id, Long ouid) {
        return this.whFunctionRcvdDao.findwFunctionRcvdByFunctionId(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public SkuCommand2Shared findSkuByBarCodeCustomerIdOuId(String barCode, Long customerId, Long ouId) {
        Sku sku = this.skuDao.findSkuAllByBarCodeCustomerIdOuId(barCode, customerId, ouId);
        if (sku != null) {
            SkuBarcode skuBarcode = skuBarcodeDao.findSkuBarCodeBySkuIdAndBarCode(sku.getId(), ouId, barCode);
            SkuExtattr skuExtattr = skuExtattrDao.findSkuExtattrBySkuIdShared(sku.getId(), ouId);
            SkuMgmt skuMgmt = skuMgmtDao.findSkuMgmtBySkuIdShared(sku.getId(), ouId);

            SkuCommand2Shared skuCommand2Shared = new SkuCommand2Shared();
            skuCommand2Shared.setSku(sku);
            skuCommand2Shared.setSkuBarcode(skuBarcode);
            skuCommand2Shared.setSkuExtattr(skuExtattr);
            skuCommand2Shared.setSkuMgmt(skuMgmt);
            return skuCommand2Shared;
        }
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public SkuMgmt findSkuMgmtBySkuIdOuId(Long skuId, Long ouId) {
        return skuMgmtDao.findSkuMgmtBySkuIdShared(skuId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCartonCommand> findWhCartonByParamExt(WhCartonCommand cartonCommand) {
        return this.whCartonDao.findWhCartonByParamExt(cartonCommand);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean skuDateCheck(Long skuId, Long ouId, String mfgDate, String expDate) {
        Boolean res = false;
        // 1.失效日期必须大于生产日期, 否则返回false
        if (mfgDate.compareTo(expDate) > 0) {
            return false;
        }
        // 2.判断商品是否过期
        SkuMgmt mgmt = skuMgmtDao.findSkuMgmtBySkuIdShared(skuId, ouId);
        // is_expired_goods_receive
        Boolean isExpiredGoodsReceive = mgmt.getIsExpiredGoodsReceive();
        Date currDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 失效日期
            Date expDate1 = sdf.parse(expDate);
            Long timeSub = expDate1.getTime() - currDate.getTime();
            if (timeSub < 0) {
                // 失效日期小于当前日期,即商品已经过期
                if (isExpiredGoodsReceive) {
                    // 如果可以收过期商品 返回true
                    return true;
                } else {
                    // 如果不可以收过期商品 返回false
                    return false;
                }
            } else {
                // 商品没有过期
                res = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        // 3.没有过期商品 判断商品效期是否在最大有效天数与最小有效天数内
        Integer maxValidDate = mgmt.getMaxValidDate();
        Integer minValidDate = mgmt.getMinValidDate();
        try {
            // Date mfgDate1 = sdf.parse(mfgDate);
            Date expDate1 = sdf.parse(expDate);
            Integer timeSub = (int) ((expDate1.getTime() - currDate.getTime()) / 86400000);
            // Integer validDate = mgmt.getValidDate();
            if (null != maxValidDate && null != minValidDate) {
                // 商品属性有最大效期和最小效期
                if (timeSub >= minValidDate && timeSub <= maxValidDate) {
                    // 如果有效天数在效期区间内 返回true
                    res = true;
                } else {
                    // 否则返回false
                    return false;
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public SkuStandardPackingCommand getContainerQty(Long skuId, Long ouId, Long containerType) {
        SkuStandardPackingCommand command = this.skuStandardPackingDao.findSkuStandardPackingBySkuIdAndContainerType(skuId, containerType, ouId);
        return command;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findSkuIdListFromInventory(Long insideContainerId, Long ouId) {
        return this.whSkuInventoryDao.findSkuIdListFromInventory(insideContainerId, ouId);
    }
}
