package com.baozun.scm.primservice.whoperation.manager.pda;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd.GeneralRcvdManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
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
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.utilities.DateUtil;

@Service("pdaRcvdManagerProxy")
public class PdaRcvdManagerProxyImpl extends BaseManagerImpl implements PdaRcvdManagerProxy {
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private AsnLineManager asnLineManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private GeneralRcvdManager generalRcvdManager;
    @Autowired
    private PoManager poManager;
    @Autowired
    private PoLineManager poLineManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private CheckInManagerProxy checkInManagerProxy;

    /**
     * 扫描ASN时 初始化缓存
     * 
     * @author yimin.lu
     */
    /**
     * 初始化缓存
     * 
     * @param occupationId
     * @param ouId
     */
    public void initAsnCacheForGeneralReceiving(Long occupationId, Long ouId) {
        // 缓存：
        // 1.ASN头信息缓存: asn的ID为key，asn对象为value
        // 2.ASN明细的缓存:asn的ID，明细的ID为key,asnline对象为value
        // 3.ASN明细-商品-数量缓存： asn的ID，明细的ID，商品的ID 为key,可用数量为Value
        // 4.商品-数量缓存：asn的ID，商品的ID 为key，此商品对应明细所有的可用数量为value
        // 5.商品-明细缓存 ：asn的ID，商品的ID为key，此商品对应明细的ID（toString）的SET集合为value
        // 6.商品SN号序列缓存：TODO
        WhAsnCommand searchCommand = new WhAsnCommand();
        searchCommand.setId(occupationId);
        searchCommand.setOuId(ouId);
        WhAsn cacheAsn = this.asnManager.findWhAsnByIdToShard(occupationId, ouId);
        if (null == cacheAsn) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        if (PoAsnStatus.ASN_CANCELED == cacheAsn.getStatus()) {// ASN单状态校验：只校验了是未取消的数据
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        WhPo po = this.poManager.findWhPoByIdToShard(cacheAsn.getPoId(), ouId);
        
        String cacheRate = cacheManager.getMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString());
        if(StringUtils.isEmpty(cacheRate)){
            cacheRate = this.initAsnOverchargeRate(po.getOverChageRate(), cacheAsn.getOverChageRate(), cacheAsn.getStoreId(), cacheAsn.getOuId());
            cacheManager.setMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString(), cacheRate, 365 * 24 * 60 * 60);
        }
        try {
            int updateCount = this.asnManager.updateByVersionForLock(cacheAsn.getId(), ouId, cacheAsn.getLastModifyTime());
            if (1 == updateCount) {
                WhAsnLineCommand command = new WhAsnLineCommand();
                command.setAsnId(cacheAsn.getId());
                command.setOuId(ouId);
                WhAsnLine asnLine = new WhAsnLine();
                BeanUtils.copyProperties(command, asnLine);
                List<WhAsnLine> asnlineList = this.asnLineManager.findListByShard(asnLine);
                if (null == asnlineList || asnlineList.size() == 0) {
                    throw new BusinessException(ErrorCodes.ASN_NULL);
                }
                // 缓存明细的可用数量
                Map<Long, Integer> skuMap = new HashMap<Long, Integer>();
                for (WhAsnLine asnline : asnlineList) {// 缓存ASN明细信息
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, asnline.getId().toString(), asnline, 24 * 60 * 60);
                    int count = asnline.getQtyPlanned().intValue() - asnline.getQtyRcvd().intValue();// 未收货数量
                    int overchargeCount = (int) (asnline.getQtyPlanned().intValue() * Double.valueOf(cacheRate) / 100);// 可超收数量
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, asnline.getId().toString(), overchargeCount, 24 * 60 * 60);
                    // 缓存ASN-商品数量
                    long asnLineSku = cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId());
                    long i = cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), (int) asnLineSku);
                    long l = cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), count);
                    if (skuMap.containsKey(asnline.getSkuId())) {
                        skuMap.put(asnline.getSkuId(), skuMap.get(asnline.getSkuId()) + count);
                    } else {
                        skuMap.put(asnline.getSkuId(), count);
                    }
                }
                // Asn商品缓存列表
                Iterator<Entry<Long, Integer>> it = skuMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<Long, Integer> skuEntry = it.next();
                    long asnSku = cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey());
                    long i = cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey(), (int) asnSku);
                    long l = cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey(), skuEntry.getValue());
                }
                // 缓存ASN头信息
                cacheManager.setMapObject(CacheKeyConstant.CACHE_ASN, occupationId.toString(), cacheAsn, 24 * 60 * 60);
                this.asnManager.updateByVersionForUnLock(occupationId, ouId);
            } else {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
        } catch (Exception e) {
            // cacheManager.removeMapValue(CacheKeyConstant.CACHE_ASN, occupationId.toString());
            throw e;
        }

    }

    /**
     * 初始化Asn的超收比例
     * 
     * @param overChageRate
     * @param overChageRate2
     * @return
     */
    private String initAsnOverchargeRate(Double overChargeRatePo, Double overChargeRateAsn, Long storeId, Long ouId) {
        if (null == overChargeRatePo) {
            String storePo = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_STORE_PO_OVERCHARGE, storeId.toString());
            if (StringUtils.hasText(storePo)) {
                overChargeRatePo = Double.parseDouble(storePo);
            } else {
                String whPo = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_WAREHOUSE_PO_OVERCHARGE, ouId.toString());
                if (StringUtils.hasText(whPo)) {
                    overChargeRatePo = Double.parseDouble(whPo);
                }
            }
        }
        if (null == overChargeRatePo) {
            overChargeRatePo = Constants.DEFAULT_DOUBLE;
        }
        if (null == overChargeRateAsn) {
            String storeAsn = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_STORE_ASN_OVERCHARGE, storeId.toString());
            if (StringUtils.hasText(storeAsn)) {
                overChargeRateAsn = Double.parseDouble(storeAsn);
            } else {
                String whAsn = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_WAREHOUSE_ASN_OVERCHARGE, ouId.toString());
                if (StringUtils.hasText(whAsn)) {
                    overChargeRateAsn = Double.parseDouble(whAsn);
                }
            }
        }

        if (null == overChargeRateAsn) {
            overChargeRateAsn = Constants.DEFAULT_DOUBLE;
        }
        return String.valueOf(overChargeRateAsn > overChargeRatePo ? overChargeRatePo : overChargeRateAsn);
    }

    @Override
    public void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commandList) {
        // 逻辑:
        // 1.插入库存记录
        // 2.更新ASN明细
        // 3.更新ASN头信息
        // 4.更新PO明细
        // 5.更新PO头信息
        // 准备更新的数据


        if (commandList != null && commandList.size() > 0) {
            List<WhSkuInventorySn> saveSnList = new ArrayList<WhSkuInventorySn>();
            List<WhAsnRcvdSnLog> saveSnLogList = new ArrayList<WhAsnRcvdSnLog>();
            List<WhSkuInventory> saveInvList = new ArrayList<WhSkuInventory>();
            List<WhAsnRcvdLog> saveInvLogList = new ArrayList<WhAsnRcvdLog>();
            List<WhAsnLine> saveAsnLineList = new ArrayList<WhAsnLine>();
            List<WhCarton> saveWhCartonList = new ArrayList<WhCarton>();
            WhAsn asn = new WhAsn();
            List<WhPoLine> savePoLineList = new ArrayList<WhPoLine>();
            WhPo po = new WhPo();

            Long asnId = commandList.get(0).getOccupationId();// ASN头ID
            Long ouId = commandList.get(0).getOuId();// OUID
            Long userId = commandList.get(0).getCreatedId();// 用户ID
            String insideContainerCode = commandList.get(0).getInsideContainerCode();
            Long insideContainerId = commandList.get(0).getInsideContainerId();// 容器ID
            // 获取ASN
            asn = this.asnManager.findWhAsnByIdToShard(asnId, ouId);
            if (null == asn) {
                throw new BusinessException("1");
            }
            // 将数据按照明细ID筛选，统计数目，放到MAP集合中
            Map<Long, Double> lineMap = new HashMap<Long, Double>();
            Map<String, WhAsnRcvdLog> rcvdLogMap = new HashMap<String, WhAsnRcvdLog>();
            Map<String, WhSkuInventory> skuInvMap = new HashMap<String, WhSkuInventory>();
            Map<String, WhCarton> whCartonMap = new HashMap<String, WhCarton>();
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
                    Sku sku = this.generalRcvdManager.findSkuByIdToShard(cacheInv.getSkuId(), ouId);
                    asnRcvdLog.setSkuCode(sku.getCode());
                    asnRcvdLog.setSkuName(sku.getName());
                    asnRcvdLog.setQuantity(cacheInv.getSkuBatchCount().longValue());
                    Container container = this.generalRcvdManager.findContainerByIdToShard(cacheInv.getInsideContainerId(), ouId);
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
                // 插入装箱信息表
                WhCarton whCarton = new WhCarton();
                if (whCartonMap.containsKey(asnRcvdLogMaoKey)) {
                    whCarton = whCartonMap.get(asnRcvdLogMaoKey);
                    whCarton.setQtyRcvd(whCarton.getQtyRcvd() + cacheInv.getSkuBatchCount().longValue());
                } else {
                    whCarton.setAsnId(asnId);
                    whCarton.setAsnLineId(lineId);
                    whCarton.setSkuId(cacheInv.getSkuId());
                    whCarton.setContainerId(insideContainerId);
                    whCarton.setExtContainerCode(insideContainerCode);
                    whCarton.setQuantity(cacheInv.getSkuBatchCount().doubleValue());
                    whCarton.setQtyRcvd(cacheInv.getSkuBatchCount().doubleValue());
                    whCarton.setMfgDate(cacheInv.getMfgDate());
                    whCarton.setExpDate(cacheInv.getExpDate());
                    whCarton.setBatchNo(cacheInv.getBatchNumber());
                    whCarton.setCountryOfOrigin(cacheInv.getCountryOfOrigin());
                    whCarton.setInvStatus(cacheInv.getInvStatus());
                    whCarton.setInvAttr1(cacheInv.getInvAttr1());
                    whCarton.setInvAttr2(cacheInv.getInvAttr2());
                    whCarton.setInvAttr3(cacheInv.getInvAttr3());
                    whCarton.setInvAttr4(cacheInv.getInvAttr4());
                    whCarton.setInvAttr5(cacheInv.getInvAttr5());
                    whCarton.setInvType(cacheInv.getInvType());
                    whCarton.setOuId(ouId);
                    whCarton.setIsCaselevel(false);
                    whCarton.setCreateTime(new Date());
                    whCarton.setCreatedId(userId);
                    whCarton.setLastModifyTime(new Date());
                    whCarton.setModifiedId(userId);
                }
                whCartonMap.put(asnRcvdLogMaoKey, whCarton);
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
                        saveSnList.add(skuInvSn);
                        // 插入日志表
                        WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
                        whAsnRcvdSnLog.setAsnRcvdId(asnId);
                        whAsnRcvdSnLog.setSn(RcvdSn.getSn());
                        whAsnRcvdSnLog.setDefectWareBarcode(barCode);
                        whAsnRcvdSnLog.setOuId(ouId);
                        // #取得残次类型残次原因的名称。
                        StoreDefectType storeDefectType = this.generalRcvdManager.findStoreDefectTypeByIdToGlobal(RcvdSn.getDefectTypeId());
                        if (null == storeDefectType) {
                            WarehouseDefectType warehouseDefectType = this.generalRcvdManager.findWarehouseDefectTypeByIdToShard(RcvdSn.getDefectTypeId(), ouId);
                            if (warehouseDefectType != null) {
                                whAsnRcvdSnLog.setDefectType(warehouseDefectType.getName());
                            } else {
                                // whAsnRcvdSnLog.setDefectType(RcvdSn.getDefectTypeId().toString());
                            }
                            WarehouseDefectReasons warehouseDefectReasons = this.generalRcvdManager.findWarehouseDefectReasonsByIdToShard(RcvdSn.getDefectReasonsId(), ouId);
                            if (warehouseDefectReasons != null) {
                                whAsnRcvdSnLog.setDefectReasons(warehouseDefectReasons.getName());
                            } else {
                                // whAsnRcvdSnLog.setDefectReasons(RcvdSn.getDefectReasonsId().toString());
                            }
                        } else {
                            whAsnRcvdSnLog.setDefectType(storeDefectType.getName());
                            StoreDefectReasons storeDefectReasons = this.generalRcvdManager.findStoreDefectReasonsByIdToGlobal(RcvdSn.getDefectReasonsId());
                            if (storeDefectReasons != null) {
                                whAsnRcvdSnLog.setDefectReasons(storeDefectReasons.getName());
                            } else {
                                // whAsnRcvdSnLog.setDefectReasons(RcvdSn.getDefectReasonsId().toString());
                            }
                        }
                        saveSnLogList.add(whAsnRcvdSnLog);
                    }

                }
            }
            // 更新库存表
            Iterator<WhSkuInventory> skuInvMapIt = skuInvMap.values().iterator();
            while (skuInvMapIt.hasNext()) {
                WhSkuInventory s = skuInvMapIt.next();
                saveInvList.add(s);
            }
            // 更新收货日志表
            Iterator<WhAsnRcvdLog> rcvdLogMapIt = rcvdLogMap.values().iterator();
            while (rcvdLogMapIt.hasNext()) {
                WhAsnRcvdLog whAsnRcvdLog = rcvdLogMapIt.next();
                saveInvLogList.add(whAsnRcvdLog);
            }
            // 更新装箱信息
            Iterator<WhCarton> whCartonIt = whCartonMap.values().iterator();
            while (whCartonIt.hasNext()) {
                WhCarton whCarton = whCartonIt.next();
                saveWhCartonList.add(whCarton);
            }
            // 更新ASN明细
            Iterator<Entry<Long, Double>> it = lineMap.entrySet().iterator();
            Double asnCount = Constants.DEFAULT_DOUBLE;
            Map<Long, Double> polineMap = new HashMap<Long, Double>();
            while (it.hasNext()) {
                Entry<Long, Double> entry = it.next();
                WhAsnLine asnLine = this.asnLineManager.findWhAsnLineByIdToShard(entry.getKey(), ouId);
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
                saveAsnLineList.add(asnLine);
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
            long rcvdlineCount = this.asnLineManager.findListCountByParam(searchAsnLine);
            if (rcvdlineCount > 0) {
                asn.setStatus(PoAsnStatus.ASN_RCVD_FINISH);
            } else {
                asn.setStatus(PoAsnStatus.ASN_RCVD);
            }
            asn.setModifiedId(userId);
            Iterator<Entry<Long, Double>> poIt = polineMap.entrySet().iterator();
            Long poId = null;
            // 更新PO明细数据集合
            while (poIt.hasNext()) {
                Entry<Long, Double> entry = poIt.next();
                WhPoLine poline = this.poLineManager.findWhPoLineByIdOuIdToShard(entry.getKey(), ouId);
                poline.setQtyRcvd(poline.getQtyRcvd() + entry.getValue());
                if (poline.getQtyRcvd() > poline.getQtyPlanned()) {
                    poline.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
                } else {
                    poline.setStatus(PoAsnStatus.POLINE_RCVD);
                }
                poline.setModifiedId(userId);
                savePoLineList.add(poline);
                if (null == poId) {
                    poId = poline.getPoId();
                }
            }
            // 更新Po数据集合
            po = this.poManager.findWhAsnByIdToShard(poId, ouId);
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
            long polinecount = this.poLineManager.findListCountByParamToShard(searchPoLine);
            if (polinecount > 0) {
                po.setStatus(PoAsnStatus.PO_RCVD);
            } else {
                po.setStatus(PoAsnStatus.PO_RCVD_FINISH);
            }
            // 更新容器
            Container container = this.generalRcvdManager.findContainerByIdToShard(insideContainerId, ouId);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
            container.setOperatorId(userId);
            try {

                this.generalRcvdManager.saveScanedSkuWhenGeneralRcvdForPda(saveSnList, saveSnLogList, saveInvList, saveInvLogList, saveAsnLineList, asn, savePoLineList, po, container, saveWhCartonList);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception ex) {
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
            // 释放容器缓存
            this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_CONTAINER, userId.toString());
            // 释放SN缓存
            this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_SN, userId.toString());
            // 释放收货数据缓存
            this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD, userId.toString());
            // 释放月台#TODO
            try {
                // checkInManagerProxy.freePlatform(asnId, ouId, userId, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将数据推送到缓存
     */
    @Override
    public void cacheScanedSkuWhenGeneralRcvd(WhSkuInventoryCommand command) {
        // 逻辑:
        // 将数据按照格式放到缓存中：RcvdCacheCommand
        /**
         * 匹配到明细，具体做法是：每次设置ASN属性的时候，就进行过滤； 而且，功能菜单上有一个属性：是否允许库存差异收货，如果允许，则随机匹配；如果不允许，则需要进行完全匹配
         */
        // rcvdCacheCommand.setLineId(Long.parseLong(command.getLineIdListString().split(",")[0]));
        Integer batchCount = command.getSkuBatchCount();
        Long occupationId = command.getOccupationId();
        Long skuId = command.getSkuId();
        String userId = command.getUserId().toString();
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_SN, command.getUserId().toString());
        // 先占用可用库存
        List<String> lineIdStrList = Arrays.asList(command.getLineIdListString().split(","));
        for (String lineIdStr : lineIdStrList) {
            Long lineId = Long.parseLong(lineIdStr);
            RcvdCacheCommand rcvdCacheCommand = this.initRcvdCacheCommand(command);
            Integer lineCount = Integer.parseInt(this.cacheManager.getValue(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId));
            Integer divCount = lineCount;
            if (lineCount > 0) {
                if (lineCount > batchCount) {
                    divCount = batchCount;
                }
                // 扣减明细SKU数量
                try {
                    long lessCount = cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    Integer overchargeCount = cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + skuId, lineId.toString());
                    if (null == overchargeCount) {
                        overchargeCount = Constants.DEFAULT_INTEGER;
                    }
                    if (lessCount + overchargeCount < 0) {
                        throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                    }
                } catch (Exception e) {
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                // 扣减SKU总数
                try {
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + "_" + skuId, divCount);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + "_" + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                try {
                    rcvdCacheCommand.setLineId(lineId);
                    rcvdCacheCommand.setSkuBatchCount(divCount);
                    if (cacheSn.size() > 0) {
                        List<RcvdSnCacheCommand> subSn = cacheSn.subList(0, divCount);
                        // 序列化问题
                        List<RcvdSnCacheCommand> subCacheSn = new ArrayList<RcvdSnCacheCommand>();
                        subCacheSn.addAll(subSn);
                        rcvdCacheCommand.setSnList(subCacheSn);
                        cacheSn.removeAll(subSn);
                    }
                    /*
                     * 测试用 cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD, userId);
                     * Thread.sleep(1000);
                     */
                    List<RcvdCacheCommand> list = cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD, userId);
                    if (null == list) {
                        list = new ArrayList<RcvdCacheCommand>();
                    }
                    list.add(rcvdCacheCommand);
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD, userId, list, 60 * 60);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + "_" + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                batchCount -= divCount;
                if (batchCount == 0) {
                    break;
                }
            }
        }
        // 再占用超收库存
        if (batchCount > 0) {
            for (String lineStr : lineIdStrList) {
                RcvdCacheCommand rcvdCacheCommand = this.initRcvdCacheCommand(command);
                Long lineId = Long.parseLong(lineStr);
                int divCount = batchCount;
                long lessCount = cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                Integer overchargeCount = cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + skuId, lineId.toString());
                if (lessCount + overchargeCount < 0) {
                    throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                }
                if (lessCount + overchargeCount < 0) {
                    throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                }
                if (batchCount > lessCount + overchargeCount) {
                    divCount = (int) (lessCount + overchargeCount);
                }
                // 扣减明细SKU数量
                try {
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                } catch (Exception e) {
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                // 扣减SKU总数
                try {
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + "_" + skuId, divCount);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + "_" + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                try {
                    rcvdCacheCommand.setLineId(lineId);
                    rcvdCacheCommand.setSkuBatchCount(divCount);
                    if (cacheSn.size() > 0) {
                        List<RcvdSnCacheCommand> subSn = cacheSn.subList(0, divCount);
                        // 序列化问题
                        List<RcvdSnCacheCommand> subCacheSn = new ArrayList<RcvdSnCacheCommand>();
                        subCacheSn.addAll(subSn);
                        rcvdCacheCommand.setSnList(subCacheSn);
                        cacheSn.removeAll(subSn);
                    }
                    List<RcvdCacheCommand> list = cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD, userId);
                    if (null == list) {
                        list = new ArrayList<RcvdCacheCommand>();
                    }
                    list.add(rcvdCacheCommand);
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD, userId, list, 60 * 60);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + "_" + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + "_" + lineId + "_" + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                batchCount -= divCount;
                if (batchCount == 0) {
                    break;
                }
            }
        }
        // 手动销毁；或者自动销毁
        this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_SN, command.getUserId().toString());
        // 初始化容器商品库存属性缓存
        this.cacheContainerSkuAttr(command);
    }

    private RcvdCacheCommand initRcvdCacheCommand(WhSkuInventoryCommand command) {
        RcvdCacheCommand rcvdCacheCommand = new RcvdCacheCommand();
        WhAsn cacheAsn = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASN, command.getOccupationId().toString());
        BeanUtils.copyProperties(command, rcvdCacheCommand);
        rcvdCacheCommand.setOccupationCode(cacheAsn.getAsnCode());
        rcvdCacheCommand.setCreatedId(command.getUserId());
        rcvdCacheCommand.setLastModifyTime(new Date());
        rcvdCacheCommand.setOuId(command.getOuId());
        rcvdCacheCommand.setInsideContainerCode(command.getInsideContainerCode());
        try {
            if (StringUtils.hasText(command.getExpDateStr())) {
                rcvdCacheCommand.setExpDate(DateUtil.parse(command.getExpDateStr(), Constants.DATE_PATTERN_YMD));
            }
            if (StringUtils.hasText(command.getMfgDateStr())) {
                rcvdCacheCommand.setMfgDate(DateUtil.parse(command.getMfgDateStr(), Constants.DATE_PATTERN_YMD));
            }

        } catch (ParseException e) {
            throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
        }
        return rcvdCacheCommand;
    }

    /**
     * 刷新缓存操作
     */
    @Override
    public void freshAsnCacheForGeneralReceiving(Long occupationId, Double newChargeRate, Double oldChargeRate) {
        try {
            Map<String, String> asnlineMap = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId);
            if (null == asnlineMap) {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
            Iterator<Entry<String, String>> it = asnlineMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, entry.getKey());
                Integer overchargeCount = (int) (line.getQtyPlanned() * newChargeRate);
                this.cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, entry.getKey(), overchargeCount, 24 * 60 * 60);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
        }
    }

    /**
     * 获取到匹配的明细;并校验匹配的明细行的商品总数是否不满足收货数量
     */
    @Override
    public String getMatchLineListStr(WhSkuInventoryCommand command) {
        int skuPlannedCount = command.getSkuBatchCount();
        // 可用数量
        Integer asnSkuCount = Integer.parseInt(cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + command.getOccupationId() + "_" + command.getSkuId()));
        Integer asnlineSkuCount = Constants.DEFAULT_INTEGER;
        String lineIdListStr = "";
        try {
            if (StringUtils.isEmpty(command.getLineIdListString())) {
                // 容器限定的商品库存属性
                RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER, command.getInsideContainerId().toString());
                
                if (rcvdContainerCacheCommand != null) {
                    if (command.getIsMixingSku()) {
                        if (!command.getSkuId().equals(rcvdContainerCacheCommand.getSkuId())) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                }
                Map<String, String> lineIdSet = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId());
                if (null == lineIdSet || lineIdSet.size() == 0) {
                    throw new BusinessException(ErrorCodes.RCVD_SKU_ASNLINE_NOTFOUND_ERROR);
                }
                Iterator<String> it = lineIdSet.keySet().iterator();
                while (it.hasNext()) {
                    String entry = it.next();
                    WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId(), entry);
                    if (command.getSkuId().equals(line.getSkuId())) {
                        Integer lineSkuOverchargeCount = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + command.getOccupationId(), entry);
                        if (null == lineSkuOverchargeCount) {
                            lineSkuOverchargeCount = Constants.DEFAULT_INTEGER;
                        }
                        asnSkuCount = asnSkuCount + lineSkuOverchargeCount;
                        lineIdListStr += entry + ",";
                    }
                }
                if (asnSkuCount < skuPlannedCount) {
                    throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                }
            } else {
                List<String> matchLineList = this.matchLineList(command.getSkuUrlOperator(), command);// 匹配行明细
                if (null == matchLineList || matchLineList.size() == 0) {
                    if (command.getIsInvattrDiscrepancyAllowrcvd()) {
                        throw new BusinessException(ErrorCodes.RCVD_DISCREPANCY_ERROR);
                    }
                }
                for (String lineId : matchLineList) {
                    Integer lineSkuOverchargeCount = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + command.getOccupationId(), lineId);
                    if (null == lineSkuOverchargeCount) {
                        lineSkuOverchargeCount = 0;
                    }
                    String asnlineskuQtyCount = cacheManager.getValue(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + command.getOccupationId() + "_" + lineId + "_" + command.getSkuId());
                    asnlineSkuCount = Integer.parseInt(asnlineskuQtyCount) + lineSkuOverchargeCount;
                    asnSkuCount = asnSkuCount + lineSkuOverchargeCount;
                    lineIdListStr += lineId + ",";
                }
                if (asnSkuCount < skuPlannedCount) {
                    if (command.getIsInvattrDiscrepancyAllowrcvd()) {
                        throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                    } else {
                        lineIdListStr = command.getLineIdListString();
                    }
                }
                if (asnlineSkuCount < skuPlannedCount) {
                    if (command.getIsInvattrDiscrepancyAllowrcvd()) {
                        throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                    } else {
                        lineIdListStr = command.getLineIdListString();
                    }
                }
            }
            // 校验明细行对应的数量是否超收

            return lineIdListStr;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.RCVD_MATCH_ERROR);
        }
    }

    /**
     * 匹配明细行逻辑
     * 
     * @param operator
     * @param isInvattrDiscrepancyAllowrcvd
     * @param command
     */
    private List<String> matchLineList(int operator, WhSkuInventoryCommand command) {
        // 容器限定的商品库存属性
        RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER, command.getInsideContainerId().toString());
        boolean flag = null == rcvdContainerCacheCommand ? false : true;
        // 匹配可用的明细
        String[] lineIdArray = command.getLineIdListString().split(",");
        // 如果只有一条明细
        List<String> lineList = new ArrayList<String>();
        for (String lineId : lineIdArray) {
            WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId(), lineId + "");
            switch (operator) {
                case Constants.GENERAL_RECEIVING_ISVALID:
                    String mfgDateStr = null == line.getMfgDate() ? "" : DateUtil.format(line.getMfgDate(), Constants.DATE_PATTERN_YMD);
                    String expDateStr = null == line.getExpDate() ? "" : DateUtil.format(line.getExpDate(), Constants.DATE_PATTERN_YMD);
                    if (command.getIsLimitUniqueDateOfManufacture() && flag) {
                        if (!mfgDateStr.equals(rcvdContainerCacheCommand.getMfgDate())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (command.getIsLimitUniqueExpiryDate() && flag) {
                        if (!expDateStr.equals(rcvdContainerCacheCommand.getExpDate())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (mfgDateStr.equals(command.getMfgDateStr()) && expDateStr.equals(command.getExpDateStr())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_ISBATCHNO:
                    if (command.getIsLimitUniqueBatch() && flag) {
                        if (!command.getBatchNumber().equals(rcvdContainerCacheCommand.getBatchNumber())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getBatchNo() || line.getBatchNo().equals(command.getBatchNumber())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN:
                    if (command.getIsLimitUniquePlaceoforigin() && flag) {
                        if (!command.getCountryOfOrigin().equals(rcvdContainerCacheCommand.getCountryOfOrigin())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getCountryOfOrigin() || line.getCountryOfOrigin().equals(command.getCountryOfOrigin())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_ISINVTYPE:
                    if (command.getIsLimitUniqueInvType() && flag) {
                        if (!command.getInvType().equals(rcvdContainerCacheCommand.getInvType())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvType() || line.getInvType().equals(command.getInvType())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_INVATTR1:
                    if (command.getIsLimitUniqueInvAttr1() && flag) {
                        if (!command.getInvAttr1().equals(rcvdContainerCacheCommand.getInvAttr1())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvAttr1() || line.getInvAttr1().equals(command.getInvAttr1())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_INVATTR2:
                    if (command.getIsLimitUniqueInvAttr2() && flag) {
                        if (!command.getInvAttr2().equals(rcvdContainerCacheCommand.getInvAttr2())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvAttr2() || line.getInvAttr2().equals(command.getInvAttr2())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_INVATTR3:
                    if (command.getIsLimitUniqueInvAttr3() && flag) {
                        if (!command.getInvAttr3().equals(rcvdContainerCacheCommand.getInvAttr3())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvAttr3() || line.getInvAttr3().equals(command.getInvAttr3())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_INVATTR4:
                    if (command.getIsLimitUniqueInvAttr4() && flag) {
                        if (!command.getInvAttr4().equals(rcvdContainerCacheCommand.getInvAttr4())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvAttr5() || line.getInvAttr4().equals(command.getInvAttr4())) {
                        lineList.add(lineId);
                    }
                    break;

                case Constants.GENERAL_RECEIVING_INVATTR5:
                    if (command.getIsLimitUniqueInvAttr5() && flag) {
                        if (!command.getInvAttr5().equals(rcvdContainerCacheCommand.getInvAttr5())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvAttr5() || line.getInvAttr5().equals(command.getInvAttr5())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_ISINVSTATUS:
                    if (command.getIsLimitUniqueInvStatus() && flag) {
                        if (!command.getInvStatus().equals(rcvdContainerCacheCommand.getInvStatus())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    if (null == line.getInvStatus() || line.getInvStatus().equals(command.getInvStatus())) {
                        lineList.add(lineId);
                    }
                    break;
                case Constants.GENERAL_RECEIVING_ISDEFEAT:
                    lineList.add(lineId);

                    break;
                case Constants.GENERAL_RECEIVING_ISSERIALNUMBER:
                    // TODO 根据序列号确认lineList
                    lineList.add(lineId);
                    break;

            }
        }
        return lineList;
    }

    @Override
    public void cacheScanedSkuSnWhenGeneralRcvd(WhSkuInventoryCommand command, Integer snCount) {
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_SN, command.getUserId().toString());
        if (null == cacheSn) {
            cacheSn = new ArrayList<RcvdSnCacheCommand>();
        }
        RcvdSnCacheCommand rcvdSn = command.getSn();
        for (int i = 0; i < snCount; i++) {
            cacheSn.add(rcvdSn);
        }
        this.cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD_SN, command.getUserId().toString(), cacheSn, 60 * 60);
        this.cacheContainerSkuAttr(command);
    }

    /**
     * 容器商品库存属性缓存
     * 
     * @param command
     */
    private void cacheContainerSkuAttr(WhSkuInventoryCommand command) {
        // 容器限定的商品库存属性
        RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER, command.getInsideContainerId().toString());
        if (null == rcvdContainerCacheCommand) {
            // 初始化
            rcvdContainerCacheCommand = new RcvdContainerCacheCommand();
            rcvdContainerCacheCommand.setInsideContainerId(command.getInsideContainerId());
            rcvdContainerCacheCommand.setInvAttr1(command.getInvAttr1());
            rcvdContainerCacheCommand.setInvAttr2(command.getInvAttr2());
            rcvdContainerCacheCommand.setInvAttr3(command.getInvAttr3());
            rcvdContainerCacheCommand.setInvAttr4(command.getInvAttr4());
            rcvdContainerCacheCommand.setInvAttr5(command.getInvAttr5());
            if (null != command.getInvStatus()) {// DateUtil.format(line.getExpDate(),
                                                 // Constants.DATE_PATTERN_YMD)
                rcvdContainerCacheCommand.setInvStatus(command.getInvStatus().toString());
            }
            rcvdContainerCacheCommand.setMfgDate(command.getMfgDateStr());
            rcvdContainerCacheCommand.setExpDate(command.getExpDateStr());
            rcvdContainerCacheCommand.setBatchNumber(command.getBatchNumber());
            rcvdContainerCacheCommand.setCountryOfOrigin(command.getCountryOfOrigin());
            rcvdContainerCacheCommand.setSkuId(command.getSkuId().toString());
            this.cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER, command.getInsideContainerId().toString(), rcvdContainerCacheCommand, 60 * 60);

        }
    }
    /**
     * 从库存中初始化容器商品属性缓存 #TODO 有风险
     * 
     * @param command
     */
    @Override
    public void initSkuAttrFromInventoryForCacheContainer(WhSkuInventoryCommand command, Long ouId) {
        /**
         * 逻辑：
         */
        boolean flag = false;
        // 测试用
        this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_CONTAINER, "14100017");
        ContainerCommand containerCommand = this.generalRcvdManager.findContainerByCode(command.getInsideContainerCode(), command.getOuId());
        if (null == containerCommand) {
            ContainerCommand saveContainer = new ContainerCommand();
            saveContainer.setCode(command.getInsideContainerCode());
            saveContainer.setOuId(command.getOuId());
            saveContainer.setOneLevelTypeValue(Constants.CONTAINER_TYPE_BOX);
            saveContainer.setTwoLevelTypeValue(Constants.CONTAINER_TYPE_2ND_BOX);
            saveContainer.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
            saveContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            Container container = this.generalRcvdManager.insertByCode(saveContainer, command.getUserId(), ouId);
            command.setInsideContainerId(container.getId());
            flag = true;

        } else {
            command.setInsideContainerId(containerCommand.getId());
            if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCommand.getOneLevelTypeLifecycle()) || !BaseModel.LIFECYCLE_NORMAL.equals(containerCommand.getTwoLevelTypeLifecycle())) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
            }
            Container container = this.generalRcvdManager.findContainerByIdToShard(containerCommand.getId(), ouId);
            if (ContainerStatus.CONTAINER_LIFECYCLE_FORBIDDEN == containerCommand.getLifecycle()) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
            } else if (ContainerStatus.CONTAINER_LIFECYCLE_USABLE == containerCommand.getLifecycle()) {
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                container.setOperatorId(command.getUserId());
                int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                }
                flag = true;
            } else if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED == containerCommand.getLifecycle()) {
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCommand.getStatus()) {
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                    container.setOperatorId(command.getUserId());
                    int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                    flag = true;
                } else if (ContainerStatus.CONTAINER_STATUS_RCVD == containerCommand.getStatus()) {
                    RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER, command.getInsideContainerId().toString());
                    if (null == rcvdContainerCacheCommand) {
                        flag = true;
                    } else {
                        if (!command.getUserId().equals(rcvdContainerCacheCommand.getUserId())) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                        }
                    }
                } else {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                }

            }
        }
        if (flag) {
            RcvdContainerCacheCommand rcvdContainerCacheCommand = this.generalRcvdManager.getUniqueSkuAttrFromWhSkuInventory(command.getInsideContainerId(), ouId);
            long invContainerCount = this.generalRcvdManager.findContainerListCountByInsideContainerIdFromSkuInventory(command.getInsideContainerId(), ouId);
            if(Constants.DEFAULT_INTEGER!=invContainerCount){
                if (command.getIsLimitUniqueBatch()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getBatchNumber())) {
                        if (rcvdContainerCacheCommand.getBatchNumber().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setBatchNumber(null);
                }
                if (command.getIsLimitUniqueDateOfManufacture()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getMfgDate())) {
                        if (rcvdContainerCacheCommand.getMfgDate().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setMfgDate(null);
                }
                if (command.getIsLimitUniqueExpiryDate()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getExpDate())) {
                        if (rcvdContainerCacheCommand.getExpDate().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setExpDate(null);
                }
                if (command.getIsLimitUniqueInvAttr1()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr1())) {
                        if (rcvdContainerCacheCommand.getInvAttr1().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr1(null);
                }
                if (command.getIsLimitUniqueInvAttr2()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr2())) {
                        if (rcvdContainerCacheCommand.getInvAttr2().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr2(null);
                }
                if (command.getIsLimitUniqueInvAttr3()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr3())) {
                        if (rcvdContainerCacheCommand.getInvAttr3().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr3(null);
                }
                if (command.getIsLimitUniqueInvAttr4()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr4())) {
                        if (rcvdContainerCacheCommand.getInvAttr4().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvStatus(null);
                }
                if (command.getIsLimitUniqueInvAttr5()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr5())) {
                        if (rcvdContainerCacheCommand.getInvAttr5().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr5(null);
                }
                if (command.getIsLimitUniqueInvStatus()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvStatus())) {
                        if (rcvdContainerCacheCommand.getInvStatus().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvStatus(null);
                }
                if (command.getIsLimitUniqueInvType()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvType())) {
                        if (rcvdContainerCacheCommand.getInvType().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvType(null);
                }
                if (command.getIsLimitUniquePlaceoforigin()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getCountryOfOrigin())) {
                        if (rcvdContainerCacheCommand.getCountryOfOrigin().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setCountryOfOrigin(null);
                }
                if (command.getIsMixingSku()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getSkuId())) {
                        if (rcvdContainerCacheCommand.getSkuId().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setSkuId(null);
                }
                // 用户ID
                rcvdContainerCacheCommand.setUserId(command.getUserId());
                // 时长一小时
                this.cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER, command.getInsideContainerId().toString(), rcvdContainerCacheCommand, 60 * 60);
            }
        }
    }

    @Override
    public void checkPallet(WhSkuInventoryCommand command, Long ouId) {
        ContainerCommand palletCommand = this.generalRcvdManager.findContainerByCode(command.getOuterContainerCode(), command.getOuId());
        if (null == palletCommand) {
            ContainerCommand saveContainer = new ContainerCommand();
            saveContainer.setCode(command.getOuterContainerCode());
            saveContainer.setOuId(command.getOuId());
            saveContainer.setOneLevelTypeValue(Constants.CONTAINER_TYPE_PALLET);
            saveContainer.setTwoLevelTypeValue(Constants.CONTAINER_TYPE_2ND_PALLET);
            saveContainer.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
            saveContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            this.generalRcvdManager.insertByCode(saveContainer, command.getUserId(), ouId);
        } else {
            if (!BaseModel.LIFECYCLE_NORMAL.equals(palletCommand.getOneLevelTypeLifecycle()) || !BaseModel.LIFECYCLE_NORMAL.equals(palletCommand.getTwoLevelTypeLifecycle())) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
            }
            Container container = this.generalRcvdManager.findContainerByIdToShard(palletCommand.getId(), ouId);
            if (ContainerStatus.CONTAINER_LIFECYCLE_FORBIDDEN == palletCommand.getLifecycle()) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
            } else if (ContainerStatus.CONTAINER_LIFECYCLE_USABLE == palletCommand.getLifecycle()) {
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                container.setOperatorId(command.getUserId());
                int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                }
            } else if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED == palletCommand.getLifecycle()) {
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == palletCommand.getStatus()) {
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                    container.setOperatorId(command.getUserId());
                    int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                } else {
                    if (ContainerStatus.CONTAINER_STATUS_RCVD != palletCommand.getStatus()) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                }

            }
        }

    }
}
