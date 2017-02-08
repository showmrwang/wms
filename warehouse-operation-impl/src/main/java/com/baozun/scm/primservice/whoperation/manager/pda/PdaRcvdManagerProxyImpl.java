package com.baozun.scm.primservice.whoperation.manager.pda;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdWorkFlow;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
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
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnSnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Uom;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.utilities.DateUtil;

@Service("pdaRcvdManagerProxy")
public class PdaRcvdManagerProxyImpl extends BaseManagerImpl implements PdaRcvdManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(PdaRcvdManagerProxy.class);

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
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private PkManager pkManager;
    @Autowired
    private AsnSnManager asnSnManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;

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

        String cacheRate = this.initAsnOverchargeRate(po.getOverChageRate(), cacheAsn.getOverChageRate(), cacheAsn.getStoreId(), cacheAsn.getOuId());
        this.cacheManager.setMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString(), cacheRate, 365 * 24 * 60 * 60);
        try {
            // 加锁
            int updateCount = this.asnManager.updateByVersionForLock(cacheAsn.getId(), ouId, cacheAsn.getLastModifyTime());
            if (1 == updateCount) {
                List<WhAsnLine> asnlineList = this.asnLineManager.findWhAsnLineByAsnIdOuIdToShard(cacheAsn.getId(), ouId);
                if (null == asnlineList || asnlineList.size() == 0) {
                    throw new BusinessException(ErrorCodes.ASN_NULL);
                }
                // ASN和PO
                WhAsn asn = this.asnManager.findWhAsnByIdToShard(occupationId, ouId);
                WhPo savePo = this.poManager.findWhPoByIdToShard(asn.getPoId(), ouId);
                // 缓存明细的可用数量
                Map<Long, Integer> skuMap = new HashMap<Long, Integer>();
                for (WhAsnLine asnline : asnlineList) {// 缓存ASN明细信息
                    // @mender yimin.lu 只缓存非caseLEVEL的明细
                    // @mender yimin.lu 2016/9/8 SN号缓存
                    WhCartonCommand cartonCommand = new WhCartonCommand();
                    cartonCommand.setAsnId(occupationId);
                    cartonCommand.setOuId(ouId);
                    cartonCommand.setAsnLineId(asnline.getId());
                    cartonCommand.setIsCaselevel(Constants.BOOLEAN_TRUE);
                    List<WhCartonCommand> cartonList = this.generalRcvdManager.findWhCartonByParamExt(cartonCommand);
                    int quantity = Constants.DEFAULT_INTEGER;
                    int qtyRcvd = Constants.DEFAULT_INTEGER;
                    for (WhCartonCommand c : cartonList) {
                        quantity += c.getQuantity();
                        qtyRcvd += c.getQtyRcvd();
                    }
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, asnline.getId().toString(), asnline, 24 * 60 * 60);
                    int count = (asnline.getQtyPlanned().intValue() - quantity) - (asnline.getQtyRcvd().intValue() - qtyRcvd);// 未收货数量
                    int overchargeCount = (int) ((asnline.getQtyPlanned().intValue() - quantity) * Double.valueOf(cacheRate) / 100);// 可超收数量
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, asnline.getId().toString(), overchargeCount, 24 * 60 * 60);
                    // 缓存ASN-商品数量
                    long asnLineSku = cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + asnline.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + asnline.getSkuId());
                    log.info(this.getClass().getSimpleName() + "initAsnCacheForGeneralReceiving PARAM asnlineSKU:{}", asnLineSku);
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + asnline.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + asnline.getSkuId(), (int) asnLineSku);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + asnline.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + asnline.getSkuId(), count);
                    if (skuMap.containsKey(asnline.getSkuId())) {
                        skuMap.put(asnline.getSkuId(), skuMap.get(asnline.getSkuId()) + count);
                    } else {
                        skuMap.put(asnline.getSkuId(), count);
                    }
                    // 缓存Asn的SN号
                    // @mender yimin.lu 2016/9/8
                    WhAsnSn sn = new WhAsnSn();
                    sn.setAsnLineId(asnline.getId());
                    sn.setSkuId(asnline.getSkuId());
                    sn.setOuId(ouId);
                    List<WhAsnSn> snList = this.asnSnManager.findListByParamToShard(sn);
                    if (snList != null && snList.size() > 0) {
                        String[] snArray = new String[snList.size()];
                        for (int i = 0; i < snList.size(); i++) {
                            snArray[i] = snList.get(i).getSn();
                        }
                        this.cacheManager.addSet(CacheKeyConstant.CACHE_ASNLINE_SN + asnline.getId(), snArray);
                    }
                }
                // Asn商品缓存列表
                Iterator<Entry<Long, Integer>> it = skuMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<Long, Integer> skuEntry = it.next();
                    long asnSku = cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + skuEntry.getKey());
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + skuEntry.getKey(), (int) asnSku);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + CacheKeyConstant.CACHE_KEY_SPLIT + skuEntry.getKey(), skuEntry.getValue());

                }
                // 缓存ASN头信息
                cacheManager.setObject(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId, cacheAsn, 24 * 60 * 60);
                // 缓存PO头信息
                cacheManager.setObject(CacheKeyConstant.CACHE_PO_PREFIX + po.getId(), po, 24 * 60 * 60);
                // 解锁
                this.asnManager.updateByVersionForUnLock(occupationId, ouId);
                // 设置PO和ASN的开始收货时间
                if (null == asn.getStartTime()) {
                    asn.setStartTime(new Date());
                    this.asnManager.saveOrUpdateByVersionToShard(asn);
                }
                if (null == po.getStartTime()) {
                    po.setStartTime(new Date());
                    this.poManager.saveOrUpdateByVersionToShard(savePo);
                }
            } else {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
        } catch (Exception e) {
            // 解锁
            this.asnManager.updateByVersionForUnLock(occupationId, ouId);
            cacheManager.remove(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
            throw e;
        }

    }

    /**
     * 初始化Asn的超收比例
     * 
     * @param overChargeRatePo
     * @param overChargeRateAsn
     * @param storeId
     * @param ouId
     * @return
     */
    private String initAsnOverchargeRate(Double overChargeRatePo, Double overChargeRateAsn, Long storeId, Long ouId) {
        log.info("initAsnOverchargeRate params overChargeRatePo:{},overChargeRateAsn:{},storeId:{},ouId:{}", overChargeRatePo, overChargeRateAsn, storeId, ouId);
        try {

            Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
            // #TODO 从缓存中读取----修正
            // Map<Long, Store> storeMap = this.findStoreByRedis(Arrays.asList(new Long[]
            // {storeId}));
            // Store store = storeMap.get(storeId);
            Store store = this.storeManager.findStoreById(storeId);
            log.info("store:{}", store);
            if (null == overChargeRatePo) {
                Integer storePo = store.getIsPoOvercharge() ? store.getPoOverchargeProportion() : null;
                if (storePo != null) {
                    overChargeRatePo = (Double) storePo.doubleValue();
                } else {
                    Integer whPo = wh.getIsPoOvercharge() ? wh.getPoOverchargeProportion() : null;
                    if (whPo != null) {
                        overChargeRatePo = whPo.doubleValue();
                    }
                }
            }
            if (null == overChargeRatePo) {
                overChargeRatePo = Constants.DEFAULT_DOUBLE;
            }
            if (null == overChargeRateAsn) {
                Integer storeAsn = store.getIsAsnOvercharge() ? store.getAsnOverchargeProportion() : null;
                if (storeAsn != null) {
                    overChargeRateAsn = storeAsn.doubleValue();
                } else {
                    Integer whAsn = wh.getIsAsnOvercharge() ? wh.getAsnOverchargeProportion() : null;
                    if (whAsn != null) {
                        overChargeRateAsn = whAsn.doubleValue();
                    }
                }
            }

            if (null == overChargeRateAsn) {
                overChargeRateAsn = Constants.DEFAULT_DOUBLE;
            }
        } catch (BusinessException ex) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        return String.valueOf(overChargeRateAsn > overChargeRatePo ? overChargeRatePo : overChargeRateAsn);
    }

    @Override
    public void saveScanedSkuWhenGeneralRcvdForPda(Long userId, Long ouId) {

        // 逻辑:
        // 1.插入库存记录
        // 2.更新ASN明细
        // 3.更新ASN头信息
        // 4.更新PO明细
        // 5.更新PO头信息
        // 准备更新的数据
        // 获取所有的库存状态数据
        List<RcvdCacheCommand> commandList = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
        if (commandList == null || commandList.size() == 0) {
            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_FINISH_ERROR);
        }
        Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
        List<InventoryStatus> invStatusList = this.inventoryStatusManager.findAllInventoryStatus();
        Map<Long, String> invStatusMap = new HashMap<Long, String>();
        if (invStatusList != null && invStatusList.size() > 0) {
            for (InventoryStatus invStatus : invStatusList) {
                invStatusMap.put(invStatus.getId(), invStatus.getName());
            }
        }

        List<WhSkuInventorySnCommand> saveSnList = new ArrayList<WhSkuInventorySnCommand>();
        List<WhSkuInventory> saveInvList = new ArrayList<WhSkuInventory>();
        List<WhAsnRcvdLogCommand> saveInvLogList = new ArrayList<WhAsnRcvdLogCommand>();
        List<WhAsnLine> saveAsnLineList = new ArrayList<WhAsnLine>();
        List<WhCarton> saveWhCartonList = new ArrayList<WhCarton>();
        WhAsn asn = new WhAsn();
        List<WhPoLine> savePoLineList = new ArrayList<WhPoLine>();
        WhPo po = new WhPo();
        Map<Long, String> storeDeReasonMap = new HashMap<Long, String>();
        Map<Long, String> whDeReasonMap = new HashMap<Long, String>();


        Long asnId = commandList.get(0).getOccupationId();// ASN头ID
        String insideContainerCode = commandList.get(0).getInsideContainerCode();
        Long insideContainerId = commandList.get(0).getInsideContainerId();// 容器ID
        Long outerContainerId = commandList.get(0).getOuterContainerId();// 托盘ID
        // 获取ASN
        asn = this.asnManager.findWhAsnByIdToShard(asnId, ouId);
        if (null == asn) {
            throw new BusinessException(ErrorCodes.OCCUPATION_RCVD_GET_ERROR);
        }
        // 将数据按照明细ID筛选，统计数目，放到MAP集合中
        Map<Long, Double> lineMap = new HashMap<Long, Double>();
        Map<String, WhAsnRcvdLogCommand> rcvdLogMap = new HashMap<String, WhAsnRcvdLogCommand>();
        Map<String, WhSkuInventory> skuInvMap = new HashMap<String, WhSkuInventory>();
        Map<String, WhCarton> whCartonMap = new HashMap<String, WhCarton>();

        // 1.保存库存
        // 2.筛选ASN明细数据集合
        for (RcvdCacheCommand cacheInv : commandList) {
            List<WhAsnRcvdSnLog> saveSnLogList = new ArrayList<WhAsnRcvdSnLog>();
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

            // SN或残次商品
            if (null != cacheInv.getSnList()) {
                List<RcvdSnCacheCommand> rcvdCacheSnList = cacheInv.getSnList();
                if (rcvdCacheSnList != null && rcvdCacheSnList.size() > 0) {
                    RcvdSnCacheCommand sc = rcvdCacheSnList.get(0);
                    // @mender yimin.lu 2016/10/31 一件商品对应一条SN收货记录
                    // @mender yimin.lu 2016/10/28 序列号商品 则会有多条数据；残次品非序列号商品只有一条数据
                    for (int i = 0; i < rcvdCacheSnList.size(); i++) {
                        RcvdSnCacheCommand rcvdSn = rcvdCacheSnList.get(i);

                        if (rcvdSn.getDefectTypeId() != null) {

                            // 插入日志表
                            WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
                            whAsnRcvdSnLog.setSn(rcvdSn.getSn());
                            whAsnRcvdSnLog.setDefectWareBarcode(rcvdSn.getDefectWareBarCode());
                            whAsnRcvdSnLog.setOuId(ouId);
                            // #取得残次类型残次原因的名称。
                            if (Constants.SKU_SN_DEFECT_SOURCE_STORE.equals(rcvdSn.getDefectSource())) {
                                StoreDefectType storeDefectType = this.generalRcvdManager.findStoreDefectTypeByIdToGlobal(rcvdSn.getDefectTypeId());
                                if (storeDefectType != null) {
                                    whAsnRcvdSnLog.setDefectType(storeDefectType.getName());
                                    if (storeDeReasonMap.containsKey(rcvdSn.getDefectReasonsId())) {
                                        whAsnRcvdSnLog.setDefectReasons(storeDeReasonMap.get(rcvdSn.getDefectReasonsId()));
                                    } else {
                                        StoreDefectReasons storeDefectReasons = this.generalRcvdManager.findStoreDefectReasonsByIdToGlobal(rcvdSn.getDefectReasonsId());
                                        if (storeDefectReasons != null) {
                                            whAsnRcvdSnLog.setDefectReasons(storeDefectReasons.getName());
                                            storeDeReasonMap.put(rcvdSn.getDefectReasonsId(), storeDefectReasons.getName());
                                        }
                                    }
                                }

                            } else if (Constants.SKU_SN_DEFECT_SOURCE_WH.equals(rcvdSn.getDefectSource())) {
                                WarehouseDefectType warehouseDefectType = this.generalRcvdManager.findWarehouseDefectTypeByIdToShard(rcvdSn.getDefectTypeId(), ouId);
                                if (warehouseDefectType != null) {
                                    whAsnRcvdSnLog.setDefectType(warehouseDefectType.getName());
                                    if (whDeReasonMap.containsKey(rcvdSn.getDefectReasonsId())) {
                                        whAsnRcvdSnLog.setDefectReasons(storeDeReasonMap.get(rcvdSn.getDefectReasonsId()));
                                    } else {
                                        WarehouseDefectReasons warehouseDefectReasons = this.generalRcvdManager.findWarehouseDefectReasonsByIdToShard(rcvdSn.getDefectReasonsId(), ouId);
                                        if (warehouseDefectReasons != null) {
                                            whAsnRcvdSnLog.setDefectReasons(warehouseDefectReasons.getName());
                                            whDeReasonMap.put(rcvdSn.getDefectReasonsId(), warehouseDefectReasons.getName());
                                        }
                                    }
                                }
                            }
                            saveSnLogList.add(whAsnRcvdSnLog);

                            WhSkuInventorySnCommand skuInvSn = new WhSkuInventorySnCommand();
                            if (Constants.SERIAL_NUMBER_TYPE_ALL.equals(sc.getSerialNumberType())) {
                                skuInvSn.setSn(rcvdSn.getSn());
                                skuInvSn.setSerialNumberType(rcvdSn.getSerialNumberType());
                            }
                            skuInvSn.setDefectTypeId(rcvdSn.getDefectTypeId());
                            skuInvSn.setDefectReasonsId(rcvdSn.getDefectReasonsId());
                            skuInvSn.setOccupationCode(occupationCode);
                            skuInvSn.setStatus(Constants.INVENTORY_SN_STATUS_ONHAND);
                            skuInvSn.setDefectWareBarcode(rcvdSn.getDefectWareBarCode());
                            skuInvSn.setOuId(ouId);
                            skuInvSn.setUuid(uuid);
                            skuInvSn.setDefectReasonsName(whAsnRcvdSnLog.getDefectReasons());
                            skuInvSn.setDefectTypeName(whAsnRcvdSnLog.getDefectType());
                            skuInvSn.setDefectSource(rcvdSn.getDefectSource());
                            saveSnList.add(skuInvSn);
                        } else {
                            // 插入收货记录表
                            WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
                            whAsnRcvdSnLog.setSn(rcvdSn.getSn());
                            whAsnRcvdSnLog.setOuId(ouId);
                            saveSnLogList.add(whAsnRcvdSnLog);

                            if (Constants.SERIAL_NUMBER_TYPE_ALL.equals(sc.getSerialNumberType())) {
                                WhSkuInventorySnCommand skuInvSn = new WhSkuInventorySnCommand();
                                skuInvSn.setSn(rcvdSn.getSn());
                                skuInvSn.setSerialNumberType(rcvdSn.getSerialNumberType());
                                skuInvSn.setOccupationCode(occupationCode);
                                skuInvSn.setStatus(Constants.INVENTORY_SN_STATUS_ONHAND);
                                skuInvSn.setOuId(ouId);
                                skuInvSn.setUuid(uuid);
                                saveSnList.add(skuInvSn);
                            }
                        }

                    }
                }


            }
            String asnRcvdLogMaoKey = lineId + uuid;
            WhAsnRcvdLogCommand asnRcvdLog = new WhAsnRcvdLogCommand();
            if (rcvdLogMap.containsKey(asnRcvdLogMaoKey)) {
                asnRcvdLog = rcvdLogMap.get(asnRcvdLogMaoKey);
                asnRcvdLog.setQuantity(asnRcvdLog.getQuantity() + cacheInv.getSkuBatchCount().longValue());
                asnRcvdLog.setQtyRcvd(asnRcvdLog.getQtyRcvd() + cacheInv.getSkuBatchCount().doubleValue());
                if (saveSnLogList.size() > Constants.DEFAULT_INTEGER) {
                    if (null == asnRcvdLog.getWhAsnRcvdSnLogList()) {
                        asnRcvdLog.setWhAsnRcvdSnLogList(saveSnLogList);
                    } else {
                        asnRcvdLog.getWhAsnRcvdSnLogList().addAll(saveSnLogList);
                    }
                }
            } else {
                asnRcvdLog.setAsnId(cacheInv.getOccupationId());
                asnRcvdLog.setAsnLineId(cacheInv.getLineId());
                asnRcvdLog.setAsnCode(cacheInv.getOccupationCode());
                Sku sku = this.generalRcvdManager.findSkuByIdToShard(cacheInv.getSkuId(), ouId);
                asnRcvdLog.setSkuCode(sku.getCode());
                asnRcvdLog.setSkuName(sku.getName());
                asnRcvdLog.setQuantity(cacheInv.getSkuBatchCount().longValue());
                // @mender yimin.lu 实际收货数量
                asnRcvdLog.setQtyRcvd(cacheInv.getSkuBatchCount().doubleValue());
                Container container = this.generalRcvdManager.findContainerByIdToShard(cacheInv.getInsideContainerId(), ouId);
                asnRcvdLog.setContainerCode(container.getCode());
                asnRcvdLog.setContainerName(container.getName());
                asnRcvdLog.setMfgDate(cacheInv.getMfgDate());
                asnRcvdLog.setExpDate(cacheInv.getExpDate());
                asnRcvdLog.setBatchNo(cacheInv.getBatchNumber());
                asnRcvdLog.setCountryOfOrigin(cacheInv.getCountryOfOrigin());
                if (cacheInv.getInvStatus() != null) {
                    asnRcvdLog.setInvStatus(invStatusMap.get(cacheInv.getInvStatus()));
                }
                // 字典表转换
                Map<String, List<String>> sysDictionaryList = new HashMap<String, List<String>>();
                if (StringUtils.hasText(cacheInv.getInvAttr1())) {

                    sysDictionaryList.put(Constants.INVENTORY_ATTR_1, Arrays.asList(cacheInv.getInvAttr1()));
                }
                if (StringUtils.hasText(cacheInv.getInvAttr2())) {

                    sysDictionaryList.put(Constants.INVENTORY_ATTR_2, Arrays.asList(cacheInv.getInvAttr2()));
                }
                if (StringUtils.hasText(cacheInv.getInvAttr3())) {

                    sysDictionaryList.put(Constants.INVENTORY_ATTR_3, Arrays.asList(cacheInv.getInvAttr3()));
                }
                if (StringUtils.hasText(cacheInv.getInvAttr4())) {

                    sysDictionaryList.put(Constants.INVENTORY_ATTR_4, Arrays.asList(cacheInv.getInvAttr4()));
                }
                if (StringUtils.hasText(cacheInv.getInvAttr5())) {

                    sysDictionaryList.put(Constants.INVENTORY_ATTR_5, Arrays.asList(cacheInv.getInvAttr5()));
                }
                if (StringUtils.hasText(cacheInv.getInvType())) {

                    sysDictionaryList.put(Constants.INVENTORY_TYPE, Arrays.asList(cacheInv.getInvType()));
                }
                Map<String, SysDictionary> dicMap = this.generalRcvdManager.findSysDictionaryByRedisExt(sysDictionaryList);
                if (StringUtils.hasText(cacheInv.getInvType())) {
                    SysDictionary dic = dicMap.get(Constants.INVENTORY_TYPE + "_" + cacheInv.getInvType());
                    asnRcvdLog.setInvType(dic == null ? cacheInv.getInvType() : dic.getDicLabel());
                }
                if (StringUtils.hasText(cacheInv.getInvAttr1())) {
                    SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_1 + "_" + cacheInv.getInvAttr1());
                    asnRcvdLog.setInvAttr1(dic == null ? cacheInv.getInvAttr1() : dic.getDicLabel());
                }
                if (StringUtils.hasText(cacheInv.getInvAttr2())) {
                    SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_2 + "_" + cacheInv.getInvAttr2());
                    asnRcvdLog.setInvAttr2(dic == null ? cacheInv.getInvAttr1() : dic.getDicLabel());
                }
                if (StringUtils.hasText(cacheInv.getInvAttr3())) {
                    SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_3 + "_" + cacheInv.getInvAttr3());
                    asnRcvdLog.setInvAttr3(dic == null ? cacheInv.getInvAttr3() : dic.getDicLabel());
                }
                if (StringUtils.hasText(cacheInv.getInvAttr4())) {
                    SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_4 + "_" + cacheInv.getInvAttr4());
                    asnRcvdLog.setInvAttr4(dic == null ? cacheInv.getInvAttr4() : dic.getDicLabel());
                }
                if (StringUtils.hasText(cacheInv.getInvAttr1())) {
                    SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_5 + "_" + cacheInv.getInvAttr5());
                    asnRcvdLog.setInvAttr5(dic == null ? cacheInv.getInvAttr5() : dic.getDicLabel());
                }
                asnRcvdLog.setOuId(ouId);
                asnRcvdLog.setCreateTime(new Date());
                asnRcvdLog.setLastModifyTime(new Date());
                asnRcvdLog.setOperatorId(userId);
                if (saveSnLogList.size() > Constants.DEFAULT_INTEGER) {
                    asnRcvdLog.setWhAsnRcvdSnLogList(saveSnLogList);
                }
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
        }
        // 更新库存表
        Iterator<WhSkuInventory> skuInvMapIt = skuInvMap.values().iterator();
        while (skuInvMapIt.hasNext()) {
            WhSkuInventory s = skuInvMapIt.next();
            saveInvList.add(s);
        }
        // 更新收货日志表
        Iterator<WhAsnRcvdLogCommand> rcvdLogMapIt = rcvdLogMap.values().iterator();
        while (rcvdLogMapIt.hasNext()) {
            WhAsnRcvdLogCommand whAsnRcvdLogCommand = rcvdLogMapIt.next();
            saveInvLogList.add(whAsnRcvdLogCommand);
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
            asnCount += entry.getValue();
        }
        // 1.更新ASN明细
        // 2.筛选PO明细数据集合
        asn.setQtyRcvd(asn.getQtyRcvd() + asnCount);
        asn.setModifiedId(userId);
        if (asn.getDeliveryTime() == null) {
            asn.setDeliveryTime(new Date());
        }
        if (asn.getStartTime() == null) {
            asn.setStartTime(new Date());
        }
        asn.setStopTime(new Date());
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
        po = this.poManager.findWhPoByIdToShard(poId, ouId);
        if (null == po) {
            throw new BusinessException(ErrorCodes.PO_RCVD_GET_ERROR);
        }
        po.setModifiedId(userId);
        po.setQtyRcvd(po.getQtyRcvd() + asnCount);
        if (null == po.getDeliveryTime()) {
            po.setDeliveryTime(new Date());
        }
        if (null == po.getStartTime()) {
            po.setStartTime(new Date());
        }
        po.setStopTime(new Date());
        // 更新容器
        Container container = null;
        container = this.generalRcvdManager.findContainerByIdToShard(insideContainerId, ouId);
        if (null == container) {
            throw new BusinessException(ErrorCodes.CONTAINER_RCVD_GET_ERROR);
        }
        container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
        container.setOperatorId(userId);

        try {
            this.generalRcvdManager.saveScanedSkuWhenGeneralRcvdForPda(saveSnList, saveInvList, saveInvLogList, saveAsnLineList, asn, savePoLineList, po, container, saveWhCartonList, wh);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
        // 释放容器缓存:如果外部容器为空，则释放缓存；否则不释放
        if (outerContainerId == null) {
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + insideContainerId);
            this.cacheManager.remonKeys(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + insideContainerId + "$*");
        }
        // 释放库存
        // 释放SN缓存
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
        // 释放收货数据缓存
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
    }

    private void cacheScanedSkuWhenGeneralRcvd(WhSkuInventoryCommand command, List<RcvdSnCacheCommand> cacheSn) {
        // 逻辑:
        // 将数据按照格式放到缓存中：RcvdCacheCommand
        /**
         * 匹配到明细，具体做法是：每次设置ASN属性的时候，就进行过滤； 而且，功能菜单上有一个属性：是否允许库存差异收货，如果允许，则随机匹配；如果不允许，则需要进行完全匹配
         */
        // rcvdCacheCommand.setLineId(Long.parseLong(command.getLineIdListString().split(",")[0]));
        if (null == command.getOuId()) {
            throw new BusinessException("error");
        }
        Integer batchCount = command.getSkuBatchCount() * command.getQuantity();
        Long occupationId = command.getOccupationId();
        Long skuId = command.getSkuId();
        String userId = command.getUserId().toString();
        // 先占用可用库存
        List<String> lineIdStrList = Arrays.asList(command.getLineIdListString().split(","));
        for (String lineIdStr : lineIdStrList) {
            Long lineId = Long.parseLong(lineIdStr);
            RcvdCacheCommand rcvdCacheCommand = this.initRcvdCacheCommand(command);
            Integer lineCount = Integer.parseInt(this.cacheManager.getValue(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId));
            Integer divCount = lineCount;
            if (lineCount > 0) {
                if (lineCount > batchCount) {
                    divCount = batchCount;
                }
                // 扣减明细SKU数量
                try {
                    long lessCount = cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    Integer overchargeCount = cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, lineId.toString());
                    if (null == overchargeCount) {
                        overchargeCount = Constants.DEFAULT_INTEGER;
                    }
                    if (lessCount + overchargeCount < 0) {
                        throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                    }
                } catch (Exception e) {
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                // 扣减SKU总数
                try {
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                try {
                    rcvdCacheCommand.setLineId(lineId);
                    rcvdCacheCommand.setSkuBatchCount(divCount);
                    if (cacheSn != null && cacheSn.size() > 0) {
                        // @mender yimin.lu 不在需要校验
                        List<RcvdSnCacheCommand> subSn = cacheSn.subList(0, divCount);
                        // 序列化问题
                        List<RcvdSnCacheCommand> subCacheSn = new ArrayList<RcvdSnCacheCommand>();
                        subCacheSn.addAll(subSn);
                        rcvdCacheCommand.setSnList(subCacheSn);
                        cacheSn.removeAll(subSn);

                    }
                    /*
                     * 测试用 cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PREFIX+userId);
                     * Thread.sleep(1000);
                     */
                    List<RcvdCacheCommand> list = cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
                    if (null == list) {
                        list = new ArrayList<RcvdCacheCommand>();
                    }
                    list.add(rcvdCacheCommand);
                    cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId, list, 60 * 60);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
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
                int lessCount = Integer.parseInt(cacheManager.getValue(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId));
                Integer overchargeCount = cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, lineId.toString());
                if (lessCount + overchargeCount < 0) {
                    throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
                }
                if (batchCount > lessCount + overchargeCount) {
                    divCount = (int) (lessCount + overchargeCount);
                }
                // 扣减明细SKU数量
                try {
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                } catch (Exception e) {
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                // 扣减SKU总数
                try {
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                try {
                    rcvdCacheCommand.setLineId(lineId);
                    rcvdCacheCommand.setSkuBatchCount(divCount);
                    if (cacheSn != null && cacheSn.size() > 0) {
                        List<RcvdSnCacheCommand> subSn = cacheSn.subList(0, divCount);
                        // 序列化问题
                        List<RcvdSnCacheCommand> subCacheSn = new ArrayList<RcvdSnCacheCommand>();
                        subCacheSn.addAll(subSn);
                        rcvdCacheCommand.setSnList(subCacheSn);
                        cacheSn.removeAll(subSn);
                    }
                    List<RcvdCacheCommand> list = cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
                    if (null == list) {
                        list = new ArrayList<RcvdCacheCommand>();
                    }
                    list.add(rcvdCacheCommand);
                    cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId, list, 60 * 60);
                } catch (Exception e) {
                    this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
                    throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
                }
                batchCount -= divCount;
                if (batchCount == 0) {
                    break;
                }
            }
        }
        // 手动销毁；或者自动销毁
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId());
        // 初始化容器商品库存属性缓存
        this.cacheContainerSkuAttr(command);
    }

    /**
     * 将数据推送到缓存
     */
    @Override
    public void cacheScanedSkuWhenGeneralRcvd(WhSkuInventoryCommand command) {
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId());
        this.cacheScanedSkuWhenGeneralRcvd(command, cacheSn);
    }

    private RcvdCacheCommand initRcvdCacheCommand(WhSkuInventoryCommand command) {
        RcvdCacheCommand rcvdCacheCommand = new RcvdCacheCommand();
        WhAsn cacheAsn = this.cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + command.getOccupationId());
        BeanUtils.copyProperties(command, rcvdCacheCommand);
        rcvdCacheCommand.setOccupationCode(cacheAsn.getAsnCode());
        rcvdCacheCommand.setCreatedId(command.getUserId());
        rcvdCacheCommand.setLastModifyTime(new Date());
        rcvdCacheCommand.setOuId(command.getOuId());
        // 默认为良品
        if (null == command.getInvStatus()) {
            rcvdCacheCommand.setInvStatus(Constants.INVENTORY_STATUS_GOOD);
        }
        rcvdCacheCommand.setInsideContainerCode(command.getInsideContainerCode());
        // 占用单据来源
        rcvdCacheCommand.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ASN);
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
    public void freshAsnCacheForGeneralReceiving(Long occupationId, Long ouId) {
        try {
            // 刷新缓存逻辑：
            // 如果检测到超收比例被更改，则需要刷新超收比例
            Double overchargeRate = this.getOverChargeRate(occupationId, ouId);
            String cacheRate = cacheManager.getMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString());
            if (StringUtils.isEmpty(cacheRate)) {
                cacheManager.setMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString(), overchargeRate.toString(), 24 * 60 * 60);
            }
            if (!overchargeRate.equals(Double.parseDouble(cacheRate))) {
                cacheManager.setMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString(), overchargeRate.toString(), 24 * 60 * 60);
                Map<String, String> asnlineMap = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId);
                if (null == asnlineMap) {
                    throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
                }
                Iterator<Entry<String, String>> it = asnlineMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, String> entry = it.next();
                    WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, entry.getKey());
                    Integer overchargeCount = (int) (line.getQtyPlanned() * overchargeRate / 100);
                    this.cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, entry.getKey(), overchargeCount, 24 * 60 * 60);
                }
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
        String lineIdListStr = "";
        try {
            if (StringUtils.isEmpty(command.getLineIdListString())) {
                // 扫描商品初始化明细行
                lineIdListStr = initMatchedLineIdStr(command);
            } else {
                // 匹配可用的明细行
                lineIdListStr = getMatchedLineIdStrForSkuAttr(command.getSkuUrlOperator(), command, command.getLineIdListString());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.RCVD_MATCH_ERROR);
        }
        return lineIdListStr;
    }

    /**
     * 初始化匹配的明细行ID集合;
     * 
     */
    public String initMatchedLineIdStr(WhSkuInventoryCommand command) {
        int skuPlannedCount = command.getSkuBatchCount() * command.getQuantity();// PDA扫描收货数量
        // 可用数量
        Integer asnSkuCount = Integer.parseInt(cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + command.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + command.getSkuId()));// ASN中此商品的数目
        String lineIdListStr = "";// 满足条件的明细行的ID集合
        Map<String, String> lineIdSet = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId());// 获取所有的ASN明细
        if (null == lineIdSet || lineIdSet.size() == 0) {
            throw new BusinessException(ErrorCodes.RCVD_SKU_ASNLINE_NOTFOUND_ERROR);
        }
        Iterator<String> it = lineIdSet.keySet().iterator();
        while (it.hasNext()) {
            String entry = it.next();
            WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId(), entry);
            if (command.getSkuId().equals(line.getSkuId())) {// 如果明细的SKUID匹配此次扫描的商品

                Integer lineSkuOverchargeCount = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + command.getOccupationId(), entry);
                if (null == lineSkuOverchargeCount) {
                    lineSkuOverchargeCount = Constants.DEFAULT_INTEGER;
                }
                asnSkuCount = asnSkuCount + lineSkuOverchargeCount;
                lineIdListStr += entry + ",";// 将LINEID添加到满足条件的明细行ID集合中
            }
        }
        lineIdListStr = lineIdListStr.substring(0, lineIdListStr.length() - 1);
        if (asnSkuCount < skuPlannedCount) {
            throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
        }
        // @mender yimin.lu 2016/6/22添加逻辑：当功能菜单上指定了库存状态和类型之后
        WhFunctionRcvd rcvd = command.getRcvd();
        // 1.库存状态
        if (command.getSkuUrl().charAt(RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS) == '1' && rcvd.getInvStatus() != null) {
            command.setInvStatus(rcvd.getInvStatus());
            lineIdListStr = this.getMatchedLineIdStrForSkuAttr(RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS, command, lineIdListStr);
        }
        // 2.库存类型
        // 如果库存类型为管控的属性的时候
        if (command.getSkuUrl().charAt(RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE) == '1' && StringUtils.hasText(rcvd.getInvType())) {
            command.setInvType(rcvd.getInvType());
            lineIdListStr = this.getMatchedLineIdStrForSkuAttr(RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE, command, lineIdListStr);
        }
        return lineIdListStr;
    }


    /**
     * 查询商品中某个商品的缓存
     * 
     * @param command
     * @param ouId
     * @return
     */
    private void initSkuAttrFromInventoryForCacheContainer(WhSkuInventoryCommand command, Long ouId) {
        // 商品
        SkuRedisCommand sku = this.skuRedisManager.findSkuMasterBySkuId(command.getSkuId(), ouId, command.getLogId());
        // 功能菜单参数
        WhFunctionRcvd functionRcvd = command.getRcvd();
        // 容器缓存
        RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId(), command.getSkuId().toString());



        Object containerCache = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId());
        this.cacheManager.getAllMap(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId());



        // RcvdContainerCacheCommand rcvdContainerCacheCommand = null;
        // 2.容器缓存
        long invContainerCount = this.generalRcvdManager.findContainerListCountByInsideContainerIdFromSkuInventory(command.getInsideContainerId(), ouId);
        if (Constants.DEFAULT_INTEGER != invContainerCount) {
            // 校验是否允许混放
            if (!functionRcvd.getIsMixingSku()) {
                long invContanierSkuCount = this.generalRcvdManager.getUniqueSkuAttrCountFromWhSkuInventory(command.getInsideContainerId(), null, ouId);
                if (invContanierSkuCount > 0) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                }
            }
            // 将容器数据推送到缓存
            List<RcvdContainerCacheCommand> rcvdContainerCacheCommandList = this.generalRcvdManager.getUniqueSkuAttrFromWhSkuInventory(command.getInsideContainerId(), command.getSkuId(), ouId);
            // 如果此商品已有库存
            if (rcvdContainerCacheCommandList == null || rcvdContainerCacheCommandList.size() == 0) {
                return;
            }
            rcvdContainerCacheCommand = rcvdContainerCacheCommandList.get(0);
            if (functionRcvd.getIsLimitUniqueBatch()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getBatchNumber())) {
                    if (rcvdContainerCacheCommand.getBatchNumber().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setBatchNumber(null);
            }
            if (functionRcvd.getIsLimitUniqueDateOfManufacture()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getMfgDate())) {
                    if (rcvdContainerCacheCommand.getMfgDate().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setMfgDate(null);
            }
            if (functionRcvd.getIsLimitUniqueExpiryDate()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getExpDate())) {
                    if (rcvdContainerCacheCommand.getExpDate().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setExpDate(null);
            }
            if (functionRcvd.getIsLimitUniqueInvAttr1()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr1())) {
                    if (rcvdContainerCacheCommand.getInvAttr1().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvAttr1(null);
            }
            if (functionRcvd.getIsLimitUniqueInvAttr2()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr2())) {
                    if (rcvdContainerCacheCommand.getInvAttr2().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvAttr2(null);
            }
            if (functionRcvd.getIsLimitUniqueInvAttr3()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr3())) {
                    if (rcvdContainerCacheCommand.getInvAttr3().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvAttr3(null);
            }
            if (functionRcvd.getIsLimitUniqueInvAttr4()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr4())) {
                    if (rcvdContainerCacheCommand.getInvAttr4().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvStatus(null);
            }
            if (functionRcvd.getIsLimitUniqueInvAttr5()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr5())) {
                    if (rcvdContainerCacheCommand.getInvAttr5().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvAttr5(null);
            }
            if (functionRcvd.getIsLimitUniqueInvStatus()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvStatus())) {
                    if (rcvdContainerCacheCommand.getInvStatus().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvStatus(null);
            }
            if (functionRcvd.getIsLimitUniqueInvType()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getInvType())) {
                    if (rcvdContainerCacheCommand.getInvType().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setInvType(null);
            }
            if (functionRcvd.getIsLimitUniquePlaceoforigin()) {
                if (StringUtils.hasText(rcvdContainerCacheCommand.getCountryOfOrigin())) {
                    if (rcvdContainerCacheCommand.getCountryOfOrigin().contains(",")) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                    }
                }
            } else {
                rcvdContainerCacheCommand.setCountryOfOrigin(null);
            }
            // 用户ID
            rcvdContainerCacheCommand.setUserId(command.getUserId());
            rcvdContainerCacheCommand.setOuId(command.getOuId());
            // 时长一小时
            this.cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId().toString(), rcvdContainerCacheCommand.getSkuId(), rcvdContainerCacheCommand, 60 * 60);
        }
    }

    /**
     * 从明细中挑选出满足属性和数量的MAX明细ID集合，并toString()
     * 
     * @param skuUrlOperator
     * @param command
     * @param lineIdListString
     * @return
     */
    private String getMatchedLineIdStrForSkuAttr(Integer skuUrlOperator, WhSkuInventoryCommand command, String lineIdListString) {
        int skuPlannedCount = command.getSkuBatchCount() * command.getQuantity();
        WhFunctionRcvd functionRcvd = command.getRcvd();
        // 可用数量
        // @mender yimin.lu 2016/11/14 将下句注释掉
        // Integer asnSkuCount =
        // Integer.parseInt(cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX +
        // command.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + command.getSkuId()));
        Integer asnlineSkuCount = Constants.DEFAULT_INTEGER;
        String lineIdListStr = "";
        List<String> matchLineList = this.matchLineList(skuUrlOperator, command, lineIdListString);// 匹配行明细
        for (String lineId : matchLineList) {
            Integer lineSkuOverchargeCount = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + command.getOccupationId(), lineId);
            if (null == lineSkuOverchargeCount) {
                lineSkuOverchargeCount = 0;
            }
            String asnlineskuQtyCount = cacheManager.getValue(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + command.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + command.getSkuId());
            asnlineSkuCount += Integer.parseInt(asnlineskuQtyCount) + lineSkuOverchargeCount;
            lineIdListStr += lineId + ",";
        }
        lineIdListStr = lineIdListStr.substring(0, lineIdListStr.length() - 1);
        if (asnlineSkuCount < skuPlannedCount) {
            if (!functionRcvd.getIsInvattrDiscrepancyAllowrcvd()) {
                throw new BusinessException(ErrorCodes.SKU_OVERCHARGE_ERROR);
            } else {
                lineIdListStr = lineIdListString;
            }
        }
        return lineIdListStr;
    }

    /**
     * 匹配明细行逻辑:从明细中挑选出满足SKU属性的明细ID集合
     * 
     * @param operator
     * @param isInvattrDiscrepancyAllowrcvd
     * @param command
     * @throws ParseException
     */
    private List<String> matchLineList(int operator, WhSkuInventoryCommand command, String lineIdListString) {
        // 容器限定的商品库存属性
        RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId() + "$" + command.getSkuId());
        boolean flag = null == rcvdContainerCacheCommand ? false : true;
        // 匹配可用的明细
        String[] lineIdArray = lineIdListString.split(",");
        List<String> lineList = new ArrayList<String>();
        WhFunctionRcvd functionRcvd = command.getRcvd();

        // PDA输入属性校验
        switch (operator) {
            case RcvdWorkFlow.GENERAL_RECEIVING_ISVALID:
                // @mender yimin.lu 2016/6/24
                String mfgDateStr = command.getMfgDateStr();
                if (StringUtils.isEmpty(mfgDateStr)) {
                    throw new BusinessException(ErrorCodes.RCVD_SKU_VALIDDATE);
                }
                String expDateStr = command.getExpDateStr();
                if (StringUtils.isEmpty(expDateStr)) {
                    throw new BusinessException(ErrorCodes.RCVD_SKU_VALIDDATE);
                }
                Date mfgDate = null;
                Date expDate = null;
                try {
                    mfgDate = DateUtil.parse(mfgDateStr, Constants.DATE_PATTERN_YMD);
                } catch (ParseException e) {
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                }
                try {
                    expDate = DateUtil.parse(expDateStr, Constants.DATE_PATTERN_YMD);
                } catch (ParseException e) {
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                }
                // 校验效期
                if (null != command.getDayOfValidDate()) {
                    Date expDateAcl = DateUtil.addDays(mfgDate, command.getDayOfValidDate());
                    String expDateAclStr = DateUtil.format(expDateAcl, Constants.DATE_PATTERN_YMD);
                    if (!expDateAclStr.equals(expDateStr)) {
                        throw new BusinessException(ErrorCodes.RCVD_SKU_VALIDDATE_DIFFERENT);
                    }
                }
                // @mender yimin.lu 2016/9/8
                // 校验过期商品以及最大失效日期最小失效日期
                SkuRedisCommand sku = this.skuRedisManager.findSkuMasterBySkuId(command.getSkuId(), command.getOuId(), command.getLogId());
                if (sku == null || sku.getSkuMgmt() == null) {
                    throw new BusinessException("商品数据异常！");
                }
                SkuMgmt skuMgmt = sku.getSkuMgmt();
                if (null != skuMgmt.getIsExpiredGoodsReceive() && !skuMgmt.getIsExpiredGoodsReceive()) {
                    if (expDate.before(new Date())) {
                        throw new BusinessException(ErrorCodes.SKU_EXPIRE_ERROR);
                    }
                    int vd = DateUtil.getInterval(expDate, new Date());
                    if (skuMgmt.getMaxValidDate() != null) {
                        if (skuMgmt.getMaxValidDate() < vd) {
                            throw new BusinessException(ErrorCodes.RCVD_SKU_VALIDDATE_MAX_ERROR);
                        }
                    }
                    if (skuMgmt.getMinValidDate() != null) {
                        if (skuMgmt.getMinValidDate() > vd) {
                            throw new BusinessException(ErrorCodes.RCVD_SKU_VALIDDATE_MIN_ERROR);
                        }
                    }
                }

                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4:
                break;

            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT:
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER:
                // @mender yimin.lu 2016/9/8 添加SN校验;
                boolean isScan = false;
                boolean isExists = false;
                String sn = command.getSn().getSn();
                long count = Constants.DEFAULT_LONG;
                for (String lineId : lineIdArray) {
                    long lineCount = this.cacheManager.findSetCount(CacheKeyConstant.CACHE_ASNLINE_SN + lineId);
                    if (lineCount > 0) {
                        if (this.cacheManager.existsInSet(CacheKeyConstant.CACHE_ASNLINE_SN + lineId, sn)) {
                            isExists = true;
                        }
                    }
                    count += lineCount;
                }
                // 校验SN是否存在
                if (count > 0 && !isExists) {
                    throw new BusinessException(ErrorCodes.RCVD_SN_NO_EXISTS_ERROR);
                }
                // 校验SN是否被扫描过
                List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId());
                if (cacheSn != null && cacheSn.size() > 0) {
                    for (int i = 0; i < cacheSn.size(); i++) {
                        if (sn.equals(cacheSn.get(i).getSn())) {
                            isScan = true;
                            break;
                        }
                    }

                }
                if (isScan) {
                    throw new BusinessException(ErrorCodes.RCVD_SN_DUP_ERROR);
                }
                break;

        }


        // 与原ASN单数据匹配
        for (String lineId : lineIdArray) {
            WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId(), lineId + "");
            switch (operator) {
                case RcvdWorkFlow.GENERAL_RECEIVING_ISVALID:
                    // @mender yimin.lu 2016/6/24 修改日期校验逻辑
                    String lineMfgDateStr = null == line.getMfgDate() ? null : DateUtil.format(line.getMfgDate(), Constants.DATE_PATTERN_YMD);
                    String lineExpDateStr = null == line.getExpDate() ? null : DateUtil.format(line.getExpDate(), Constants.DATE_PATTERN_YMD);
                    // @mender yimin.lu 2016/6/24
                    String mfgDateStr = command.getMfgDateStr();
                    String expDateStr = command.getExpDateStr();
                    if ((StringUtils.isEmpty(lineMfgDateStr) || lineMfgDateStr.equals(mfgDateStr)) && (StringUtils.isEmpty(lineExpDateStr) || lineExpDateStr.equals(expDateStr))) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO:
                    if (StringUtils.isEmpty(line.getBatchNo()) || line.getBatchNo().equals(command.getBatchNumber())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN:
                    if (StringUtils.isEmpty(line.getCountryOfOrigin()) || line.getCountryOfOrigin().equals(command.getCountryOfOrigin())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE:
                    if (StringUtils.isEmpty(line.getInvType()) || line.getInvType().equals(command.getInvType())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1:
                    if (StringUtils.isEmpty(line.getInvAttr1()) || line.getInvAttr1().equals(command.getInvAttr1())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2:
                    if (StringUtils.isEmpty(line.getInvAttr2()) || line.getInvAttr2().equals(command.getInvAttr2())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3:
                    if (StringUtils.isEmpty(line.getInvAttr3()) || line.getInvAttr3().equals(command.getInvAttr3())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4:
                    if (StringUtils.isEmpty(line.getInvAttr4()) || line.getInvAttr4().equals(command.getInvAttr4())) {
                        lineList.add(lineId);
                    }
                    break;

                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5:
                    if (StringUtils.isEmpty(line.getInvAttr5()) || line.getInvAttr5().equals(command.getInvAttr5())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS:
                    if (null == line.getInvStatus() || line.getInvStatus().equals(command.getInvStatus())) {
                        lineList.add(lineId);
                    }
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT:
                    lineList.add(lineId);

                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER:
                    lineList.add(lineId);
                    break;

            }
        }
        if (null == lineList || lineList.size() == 0) {
            if (!functionRcvd.getIsInvattrDiscrepancyAllowrcvd()) {
                throw new BusinessException(ErrorCodes.RCVD_DISCREPANCY_ERROR);
            }
            lineList = Arrays.asList(lineIdArray);
        }
        // 校验容器限定属性
        List<String> returnLineList = new ArrayList<String>();
        for (String lineId : lineList) {
            switch (operator) {
                case RcvdWorkFlow.GENERAL_RECEIVING_ISVALID:
                    // @mender yimin.lu 2016/6/24 修改日期校验逻辑
                    // @mender yimin.lu 2016/6/24
                    String mfgDateStr = command.getMfgDateStr();
                    String expDateStr = command.getExpDateStr();

                    if (functionRcvd.getIsLimitUniqueDateOfManufacture() && flag) {
                        if (!mfgDateStr.equals(rcvdContainerCacheCommand.getMfgDate())) {
                            break;
                        }
                    }
                    if (functionRcvd.getIsLimitUniqueExpiryDate() && flag) {
                        if (!expDateStr.equals(rcvdContainerCacheCommand.getExpDate())) {
                            break;
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO:
                    if (functionRcvd.getIsLimitUniqueBatch() && flag) {
                        if (!command.getBatchNumber().equals(rcvdContainerCacheCommand.getBatchNumber())) {
                            break;
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN:
                    if (functionRcvd.getIsLimitUniquePlaceoforigin() && flag) {
                        if (!command.getCountryOfOrigin().equals(rcvdContainerCacheCommand.getCountryOfOrigin())) {
                            break;
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE:
                    if (functionRcvd.getIsLimitUniqueInvType() && flag) {
                        if (!command.getInvType().equals(rcvdContainerCacheCommand.getInvType())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1:
                    if (functionRcvd.getIsLimitUniqueInvAttr1() && flag) {
                        if (!command.getInvAttr1().equals(rcvdContainerCacheCommand.getInvAttr1())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2:
                    if (functionRcvd.getIsLimitUniqueInvAttr2() && flag) {
                        if (!command.getInvAttr2().equals(rcvdContainerCacheCommand.getInvAttr2())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3:
                    if (functionRcvd.getIsLimitUniqueInvAttr3() && flag) {
                        if (!command.getInvAttr3().equals(rcvdContainerCacheCommand.getInvAttr3())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4:
                    if (functionRcvd.getIsLimitUniqueInvAttr4() && flag) {
                        if (!command.getInvAttr4().equals(rcvdContainerCacheCommand.getInvAttr4())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;

                case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5:
                    if (functionRcvd.getIsLimitUniqueInvAttr5() && flag) {
                        if (!command.getInvAttr5().equals(rcvdContainerCacheCommand.getInvAttr5())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS:
                    if (functionRcvd.getIsLimitUniqueInvStatus() && flag) {
                        if (!command.getInvStatus().toString().equals(rcvdContainerCacheCommand.getInvStatus())) {
                            break;
                            // throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                    returnLineList.add(lineId);
                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT:
                    returnLineList.add(lineId);

                    break;
                case RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER:
                    returnLineList.add(lineId);
                    break;

            }
        }
        if (returnLineList == null || returnLineList.size() == 0) {
            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
        }
        return returnLineList;
    }

    @Override
    public void cacheScanedSkuSnWhenGeneralRcvd(WhSkuInventoryCommand command, Integer snCount, boolean isCacheSkuSn) {
        // 逻辑：
        // 1.管理序列号的时候，snCount为1；
        // 2.管理序列号时候，需要获得他的序列号管理类型

        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId());
        if (null == cacheSn) {
            cacheSn = new ArrayList<RcvdSnCacheCommand>();
        }
        RcvdSnCacheCommand rcvdSn = command.getSn();
        // @mender yimin.lu 2016/10/31扫描残次原因时候，需要生成残次条码
        // @mender yimin.lu 2016/10/28
        // @mender yimin.lu 2016/9/8
        List<String> barCodeList = this.codeManager.generateCodeList(Constants.WMS, Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null, snCount).toArray();
        for (int i = 0; i < snCount; i++) {// 此处For循环主要针对非SN的残次品，如果是SN商品，snCount=1
            RcvdSnCacheCommand newSn = new RcvdSnCacheCommand();
            newSn.setDefectReasonsId(rcvdSn.getDefectReasonsId());
            newSn.setDefectSource(command.getSnSource());
            newSn.setDefectTypeId(rcvdSn.getDefectTypeId());
            // 残次条码 调用条码生成器
            if (null != rcvdSn.getDefectTypeId()) {
                // String barCode = this.codeManager.generateCode(Constants.WMS,
                // Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null);
                newSn.setDefectWareBarCode(barCodeList.get(i));
            }
            // 设置序列号及序列号管理类型
            if (StringUtils.hasText(rcvdSn.getSn())) {
                newSn.setSn(rcvdSn.getSn());
                SkuRedisCommand sku = this.skuRedisManager.findSkuMasterBySkuId(command.getSkuId(), command.getOuId(), command.getLogId());
                if (null != sku && null != sku.getSkuMgmt()) {

                    SkuMgmt skuMgmt = sku.getSkuMgmt();
                    newSn.setSerialNumberType(skuMgmt.getSerialNumberType());
                }
            }
            cacheSn.add(newSn);
        }

        if (isCacheSkuSn) {
            this.cacheScanedSkuWhenGeneralRcvd(command, cacheSn);
        } else {
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId(), cacheSn);
        }
        this.cacheContainerSkuAttr(command);

    }

    /**
     * 容器商品库存属性缓存
     * 
     * @param command
     */
    private void cacheContainerSkuAttr(WhSkuInventoryCommand command) {
        // @mender yimin.lu 2016/11/14 调整缓存
        // 容器限定的商品库存属性
        RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId() + "$" + command.getSkuId());
        if (null == rcvdContainerCacheCommand) {
            // 初始化
            rcvdContainerCacheCommand = new RcvdContainerCacheCommand();
            rcvdContainerCacheCommand.setInsideContainerId(command.getInsideContainerId());
            rcvdContainerCacheCommand.setInvAttr1(command.getInvAttr1());
            rcvdContainerCacheCommand.setInvAttr2(command.getInvAttr2());
            rcvdContainerCacheCommand.setInvAttr3(command.getInvAttr3());
            rcvdContainerCacheCommand.setInvAttr4(command.getInvAttr4());
            rcvdContainerCacheCommand.setInvAttr5(command.getInvAttr5());
            rcvdContainerCacheCommand.setOuId(command.getOuId());
            rcvdContainerCacheCommand.setUserId(command.getUserId());
            rcvdContainerCacheCommand.setInvType(command.getInvType());
            if (null != command.getInvStatus()) {
                rcvdContainerCacheCommand.setInvStatus(command.getInvStatus().toString());
            }
            rcvdContainerCacheCommand.setMfgDate(null == command.getMfgDateStr() ? "" : command.getMfgDateStr());
            rcvdContainerCacheCommand.setExpDate(null == command.getExpDateStr() ? "" : command.getExpDateStr());
            rcvdContainerCacheCommand.setBatchNumber(command.getBatchNumber());
            rcvdContainerCacheCommand.setCountryOfOrigin(command.getCountryOfOrigin());
            rcvdContainerCacheCommand.setSkuId(command.getSkuId().toString());
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId() + "$" + command.getSkuId(), rcvdContainerCacheCommand);

        }
    }


    /**
     * 从库存中初始化容器商品属性缓存
     * 
     * @param command
     */
    @Override
    public void checkContainer(WhSkuInventoryCommand command, Long ouId) {
        Long userId = command.getUserId();
        /***/
        // 逻辑：
        // 1.校验容器；不存在则新建
        // 2.缓存容器
        // 2.1托盘容器缓存
        // 2.2容器缓存
        // 2.3容器商品属性缓存
        if (null != command.getOuterContainerId()) {
            if (command.getOuterContainerCode().equals(command.getInsideContainerCode())) {
                throw new BusinessException(ErrorCodes.RCVD_CONTAINER_NO_DUPLICATION);
            }
        }
        // 测试用
        // this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_CONTAINER, "14100017");
        ContainerCommand containerCommand = this.generalRcvdManager.findContainerByCode(command.getInsideContainerCode(), ouId);
        if (null == containerCommand) {
            ContainerCommand saveContainer = new ContainerCommand();
            saveContainer.setCode(command.getInsideContainerCode());
            saveContainer.setOuId(command.getOuId());
            saveContainer.setOneLevelTypeValue(Constants.CONTAINER_TYPE_BOX);
            saveContainer.setTwoLevelTypeValue(Constants.CONTAINER_TYPE_2ND_BOX);
            saveContainer.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
            saveContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            Container container = this.generalRcvdManager.insertByCode(saveContainer, userId, ouId);
            command.setInsideContainerId(container.getId());
            containerCommand = new ContainerCommand();
            containerCommand.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
            containerCommand.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
            // 初始化容器-用户缓存
            RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
            cacheContainer.setIsMixAttr(false);
            cacheContainer.setUserId(userId);
            cacheContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
            cacheContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + command.getInsideContainerId(), cacheContainer);
            if (null != command.getOuterContainerId()) {
                // 1.托盘容器缓存
                this.cacheManager.pushToListHead(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + command.getOuterContainerId(), command.getInsideContainerId().toString());
            }

        } else {
            Long insideContainerId = containerCommand.getId();
            command.setInsideContainerId(insideContainerId);
            // @mender yimin.lu 2016/7/13
            if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCommand.getTwoLevelTypeLifecycle())) {
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
                // 初始化容器-用户缓存
                RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
                cacheContainer.setIsMixAttr(false);
                cacheContainer.setUserId(userId);
                cacheContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                cacheContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + command.getInsideContainerId(), cacheContainer);

            } else if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED == containerCommand.getLifecycle()) {

                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCommand.getStatus()) {
                    List<Long> skuIdList = this.generalRcvdManager.findSkuIdListFromInventory(containerCommand.getId(), ouId);
                    if (skuIdList == null || skuIdList.size() == 0) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_STATUS_ERROR);
                    }

                    // 容器是否混放
                    RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.addAll(skuIdList);
                    Long skuId = skuIdList.get(0);
                    SkuRedisCommand sku = this.skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
                    SkuMgmt mgmt = sku.getSkuMgmt();
                    if (mgmt == null) {
                        throw new BusinessException(ErrorCodes.RCVD_SKU_DATA_ERROR);
                    }
                    // 混放属性
                    cacheContainer.setIsMixAttr(true);
                    cacheContainer.setMixAttr(mgmt.getMixAttr());
                    cacheContainer.setUserId(userId);
                    cacheContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    cacheContainer.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    cacheContainer.setSkuIdSet(skuIdSet);
                    // 更新容器
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                    container.setOperatorId(command.getUserId());
                    int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                    // 插入容器-用户缓存
                    this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + command.getInsideContainerId(), cacheContainer);

                } else if (ContainerStatus.CONTAINER_STATUS_RCVD == containerCommand.getStatus()) {
                    RcvdContainerCacheCommand cacheContainer = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + insideContainerId);
                    if (StringUtils.isEmpty(cacheContainer)) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                    if (!command.getUserId().toString().equals(cacheContainer.getUserId())) {

                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                } else {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                }

            }
        }

    }


    @Override
    public void checkPallet(WhSkuInventoryCommand command, Long ouId) {
        /***/
        // 逻辑：
        // 1.扫描托盘号，如果没有，则新建托盘
        // 2.校验托盘的状态是否可以使用：1.可用标志，2.未被他人使用，3.收货中和可上架状态
        // 2.1.更新逻辑：更新为占用中、收货中
        // 3.生成托盘占用缓存
        /***/
        // 查询托盘
        Long userId = command.getUserId();
        ContainerCommand palletCommand = this.generalRcvdManager.findContainerByCode(command.getOuterContainerCode(), ouId);
        // 找不到托盘，则新建托盘
        if (null == palletCommand) {
            // 新建LIFECYCLE为占用，STATUS为收货中的托盘
            ContainerCommand saveContainer = new ContainerCommand();
            saveContainer.setCode(command.getOuterContainerCode());
            saveContainer.setOuId(ouId);
            saveContainer.setOneLevelTypeValue(Constants.CONTAINER_TYPE_PALLET);
            saveContainer.setTwoLevelTypeValue(Constants.CONTAINER_TYPE_2ND_PALLET);
            saveContainer.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
            saveContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            Container c = this.generalRcvdManager.insertByCode(saveContainer, userId, ouId);
            palletCommand = new ContainerCommand();
            palletCommand.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
            palletCommand.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
            // 缓存托盘
            // 初始化容器-用户缓存
            RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
            cacheContainer.setUserId(userId);
            cacheContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
            cacheContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + c.getId(), cacheContainer);
            // 否则，进行更新托盘
        } else {
            Long outerContainerId = palletCommand.getId();
            // 校验状态
            if (!BaseModel.LIFECYCLE_NORMAL.equals(palletCommand.getTwoLevelTypeLifecycle())) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
            }
            Container container = this.generalRcvdManager.findContainerByIdToShard(outerContainerId, ouId);
            // 如果为禁用，抛出异常
            if (ContainerStatus.CONTAINER_LIFECYCLE_FORBIDDEN == palletCommand.getLifecycle()) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
                // 可用
            } else if (ContainerStatus.CONTAINER_LIFECYCLE_USABLE == palletCommand.getLifecycle()) {
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                container.setOperatorId(userId);
                int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                }
                // 缓存托盘
                // 初始化容器-用户缓存
                RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
                cacheContainer.setUserId(userId);
                cacheContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                cacheContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + outerContainerId, cacheContainer);

                // 占用中：如果为收货中，则校验是否为他人所使用，否则不为上架中则抛出异常
            } else if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED == palletCommand.getLifecycle()) {
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == palletCommand.getStatus()) {
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                    container.setOperatorId(command.getUserId());
                    int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                    // 缓存托盘
                    // 初始化容器-用户缓存
                    RcvdContainerCacheCommand cacheContainer = new RcvdContainerCacheCommand();
                    cacheContainer.setUserId(userId);
                    cacheContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    cacheContainer.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + outerContainerId, cacheContainer);
                    // 占用中，需要校验：同一个收货容器只能一个人操作
                } else if (ContainerStatus.CONTAINER_STATUS_RCVD == palletCommand.getStatus()) {
                    RcvdContainerCacheCommand cacheContainer = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + outerContainerId);
                    if (null == cacheContainer) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }
                    if (!command.getUserId().toString().equals(cacheContainer.getUserId())) {
                        throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                    }

                } else {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_OCCUPATIED_ERROR);
                }

            }
        }

    }


    @Override
    public WhSkuInventoryCommand initAttrWhenScanningSku(Boolean isInvattrAsnPointoutUser, Integer nextOpt, WhSkuInventoryCommand command) {
        // 匹配可用的明细
        String[] lineIdArray = command.getLineIdListString().split(",");
        String lineId = lineIdArray[0];
        WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + command.getOccupationId(), lineId);

        // 指针列表：
        // 0：是否管理效期
        // 1:是否管理批次号
        // 2:是否管理原产地
        // 3:是否管理库存类型
        // 4:是否管理库存属性1
        // 5:是否管理库存属性2
        // 6:是否管理库存属性3
        // 7:是否管理库存属性4
        // 8:是否管理库存属性5
        // 9:是否管理库存状态
        // 10:残次品类型及残次原因
        // 11:是否管理序列号
        if (isInvattrAsnPointoutUser) {
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISVALID == nextOpt) {
                if (null != line.getExpDate()) {
                    command.setExpDateStr(DateUtil.format(line.getExpDate(), Constants.DATE_PATTERN_YMD));
                }
                if (null != line.getMfgDate()) {
                    command.setMfgDateStr(DateUtil.format(line.getMfgDate(), Constants.DATE_PATTERN_YMD));
                }
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO == nextOpt) {
                command.setBatchNumber(line.getBatchNo());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN == nextOpt) {
                command.setCountryOfOrigin(line.getCountryOfOrigin());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE == nextOpt) {
                command.setInvType(line.getInvType());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1 == nextOpt) {
                command.setInvAttr1(line.getInvAttr1());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2 == nextOpt) {
                command.setInvAttr2(line.getInvAttr2());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3 == nextOpt) {
                command.setInvAttr3(line.getInvAttr3());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4 == nextOpt) {
                command.setInvAttr4(line.getInvAttr4());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5 == nextOpt) {
                command.setInvAttr5(line.getInvAttr5());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS == nextOpt) {
                command.setInvStatus(line.getInvStatus());
            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT == nextOpt) {

            }
            if (RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER == nextOpt) {}// 序列号不用提示
        }
        // @mender yimin.lu 初始化容器缓存
        if (command.getRcvdSkuContainerCache() != null) {
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + command.getInsideContainerId() + "$" + command.getSkuId(), command.getRcvdSkuContainerCache());
        }
        if (command.getRcvdUserContainerCache() != null) {
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + command.getInsideContainerId(), command.getRcvdUserContainerCache());
        }
        return command;
    }



    @Override
    public void initOrFreshCacheForScanningAsn(WhSkuInventoryCommand command) {
        log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn begin!");
        Long occupationId = command.getOccupationId();
        Long ouId = command.getOuId();
        log.debug(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn params:[occupationId:{},ouId:{}]", occupationId, ouId);
        // 逻辑：
        // 初始化仓库店铺的超收比例
        // 初始化缓存
        // 刷新缓存
        // 尝试初始化店铺仓库的超收比例
        log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.initWhStoreOverchargeRate begin!");
        log.debug(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.initWhStoreOverchargeRate params:[occupationId:{},ouId:{}]", occupationId, ouId);

        // initWhStoreOverchargeRate(occupationId, ouId);

        log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.initWhStoreOverchargeRate end!");
        // 如果没有缓存，则尝试初始化缓存
        // 如果已有缓存，则尝试刷新缓存
        // 刷新缓存逻辑：
        // 下面这个IF-ELSE逻辑：
        // 如果没有缓存数据，则初始化缓存
        // 如果有的话，则刷新缓存
        // this.cacheManager.remove(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        WhAsn cacheAsn = cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
        log.info(this.getClass().getSimpleName() + "initOrFreshCacheForScanningAsn PARAM:[cahceAsn:{}]", cacheAsn);
        if (null == cacheAsn) {
            // 初始化缓存
            log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.initAsnCacheForGeneralReceiving begin!");
            log.debug(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.initAsnCacheForGeneralReceiving params:[occupationId:{},ouId:{}]", occupationId, ouId);

            initAsnCacheForGeneralReceiving(occupationId, ouId);

            log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.initAsnCacheForGeneralReceiving end!");
        } else {
            // 刷新缓存逻辑：
            // 如果检测到超收比例被更改，则需要刷新超收比例
            log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.freshAsnCacheForGeneralReceiving begin!");
            log.debug(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.freshAsnCacheForGeneralReceiving params:[occupationId:{},ouId:{}]", occupationId, ouId);
            freshAsnCacheForGeneralReceiving(occupationId, ouId);
            log.info(this.getClass().getSimpleName() + ".initOrFreshCacheForScanningAsn->this.freshAsnCacheForGeneralReceiving end!");
        }
    }


    /**
     * 获取超收比例
     * 
     * @param occupationId
     * @return
     */
    private double getOverChargeRate(Long occupationId, Long ouId) {
        WhAsn asn = this.cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
        WhPo po = this.cacheManager.getObject(CacheKeyConstant.CACHE_PO_PREFIX + asn.getPoId());
        Map<Long, Store> storeMap = this.findStoreByRedis(Arrays.asList(new Long[] {asn.getStoreId()}));
        Store store = storeMap.get(asn.getStoreId());
        Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
        Double minAsnOverChargeRate = null;
        Double minPoOverChargeRate = null;
        if (wh.getIsPoOvercharge() && wh.getPoOverchargeProportion() != null) {
            minPoOverChargeRate = wh.getPoOverchargeProportion().doubleValue();
        }
        if (store.getIsPoOvercharge() && store.getPoOverchargeProportion() != null) {
            minPoOverChargeRate = store.getPoOverchargeProportion().doubleValue();
        }
        if (null != po.getOverChageRate()) {
            minPoOverChargeRate = po.getOverChageRate();
        }
        if (wh.getIsAsnOvercharge() && wh.getAsnOverchargeProportion() != null) {
            minAsnOverChargeRate = wh.getAsnOverchargeProportion().doubleValue();
        }
        if (store.getIsAsnOvercharge() && store.getAsnOverchargeProportion() != null) {
            minAsnOverChargeRate = store.getAsnOverchargeProportion().doubleValue();
        }
        if (null != asn.getOverChageRate()) {
            minAsnOverChargeRate = asn.getOverChageRate();
        }
        if (null == minAsnOverChargeRate) {
            return null == minPoOverChargeRate ? Constants.DEFAULT_DOUBLE : minPoOverChargeRate;
        }
        if (null == minPoOverChargeRate) {
            return null == minAsnOverChargeRate ? Constants.DEFAULT_DOUBLE : minAsnOverChargeRate;
        }
        return minAsnOverChargeRate.doubleValue() > minPoOverChargeRate.doubleValue() ? minPoOverChargeRate : minAsnOverChargeRate;
    }

    @Override
    public void revokeContainer(Long containerId, Long ouId, Long userId) {
        try {
            // @mender yimin.lu 2016/11/1 容器状态会缓存到容器缓存中去，回滚时候从缓存中取出
            Container container = this.generalRcvdManager.findContainerByIdToShard(containerId, ouId);
            RcvdContainerCacheCommand cacheContainer = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + containerId);
            if (null != cacheContainer) {
                // long invCount =
                // this.generalRcvdManager.findContainerListCountByInsideContainerIdFromSkuInventory(containerId,
                // ouId);
                container.setLifecycle(cacheContainer.getLifecycle());
                container.setStatus(cacheContainer.getStatus());
                container.setOperatorId(userId);
                int updateCount = this.generalRcvdManager.updateContainerByVersion(container);
                if (updateCount < 1) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_REVOKE_ERROR);
                }
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_REVOKE_ERROR);
        }
    }

    @Override
    public void rcvdPallet(Long outerContainerId, Long insideContainerId, Long ouId, Long userId) {
        Container pallet = this.generalRcvdManager.findContainerByIdToShard(outerContainerId, ouId);
        pallet.setOperatorId(userId);
        pallet.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
        this.generalRcvdManager.rcvdPallet(outerContainerId, insideContainerId, ouId, userId);
        // 释放托盘缓存
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + outerContainerId);
        // 释放托盘-货箱缓存
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId);

        // 清除容器-商品缓存
        List<String> containerList = this.cacheManager.findLists(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId, 0, this.cacheManager.listLen(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId));
        if (containerList != null) {
            for (String containerId : containerList) {
                this.cacheManager.remonKeys(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + containerId + "$*");
            }
        }
        // 清除托盘-货箱缓存
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId);
    }

    @Override
    public void rcvdAsn(Long occupationId, Long ouId, Long userId) {
        // 释放用户占用者缓存
        // this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_USER_OCCUPATION,
        // userId.toString());
        // 释放月台 不必强制事务，可以手动释放月台
        try {
            checkInManagerProxy.releasePlatformByRcvdFinish(occupationId, ouId, userId, null);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.RCVD_PLATFORM_REALEASE_ERROR);
        }
    }

    /**
     * 通过功能主表ID查询对应收货参数
     */
    @Override
    public WhFunctionRcvd findwFunctionRcvdByFunctionId(Long id, Long ouid) {
        return generalRcvdManager.findwFunctionRcvdByFunctionId(id, ouid);
    }

    /**
     * 用来初始化商品相关扫描属性的 1.多条码数量 2.skuId 3.效期 4.商品扫描序列
     */
    @Override
    public WhSkuInventoryCommand initSkuWhenScanning(WhSkuInventoryCommand command) {
        // LOGID
        String logId = command.getLogId();
        Long insideContainerId = command.getInsideContainerId();
        Long skuId = null;
        Integer quantity = null;
        Long ouId = command.getOuId();
        Long occupationId = command.getOccupationId();
        WhFunctionRcvd rcvd = command.getRcvd();

        // 累计数量
        if (null == command.getSkuAddUpCount()) {
            command.setSkuAddUpCount(0);
        }
        // 如果已经扫描了商品，则不需要再缓存
        if (StringUtils.hasText(command.getSkuUrl())) {
            return command;
        }
        // 校验是否已经扫描过此商品，不然就生成商品的信息序列
        // 取得扫描的商品
        // 以及商品的多条码
        WhAsn asn = this.cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
        if (asn == null) {
            throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
        }
        Map<Long, Integer> skuMap = this.skuRedisManager.findSkuByBarCode(command.getSkuCode(), logId);
        Iterator<Entry<Long, Integer>> skuIt = skuMap.entrySet().iterator();
        String asnSkuCount;
        while (skuIt.hasNext()) {
            Entry<Long, Integer> entry = skuIt.next();
            asnSkuCount = cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + entry.getKey());
            if (StringUtils.hasText(asnSkuCount)) {
                skuId = entry.getKey();
                quantity = entry.getValue();
            }

        }
        if (null == skuId) {
            throw new BusinessException(ErrorCodes.SKU_CACHE_ERROR);
        }

        SkuRedisCommand sku = this.skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
        if (null == sku) {
            throw new BusinessException(ErrorCodes.RCVD_SKU_EXPRIED_ERROR);
        }
        command.setQuantity(quantity);
        command.setSkuId(skuId);
        // 校验扫描的商品是否在缓存中，如果不在，推出错误
        // 校验商品数量是否超过超收数量，此处不校验，放到匹配明细逻辑校验
        // @mender yimin.lu 2016/11/7 校验容器是否允许混放，如果容器允许混放，还需要校验混放属性是否一致
        SkuMgmt mgmt = sku.getSkuMgmt();
        if (null == mgmt) {
            throw new BusinessException(ErrorCodes.RCVD_SKU_DATA_ERROR);
        }
        command.setMixAttr(mgmt.getMixAttr());
        // @mender yimin.lu 缓存商品辅助表信息，不需要再进行效期的计算
        // 缓存商品辅助表信息
        // @mender yimin.lu 2016/9/8
        // @mender yimin.lu 2016/6/24 效期
        if (null != mgmt.getValidDate()) {
            int day = mgmt.getValidDate();
            if (Constants.TIME_UOM_YEAR.equals(mgmt.getGoodShelfLifeUnit())) {
                Uom uom = this.generalRcvdManager.findUomByCode(Constants.TIME_UOM_YEAR, Constants.TIME_UOM);
                day = (int) (day * uom.getConversionRate());
            } else if (Constants.TIME_UOM_MONTH.equals(mgmt.getGoodShelfLifeUnit())) {
                Uom uom = this.generalRcvdManager.findUomByCode(Constants.TIME_UOM_MONTH, Constants.TIME_UOM);
                day = (int) (day * uom.getConversionRate());
            }
            // 效期
            command.setDayOfValidDate(day);
        }
        // 校验容器是否允许放入此商品
        RcvdContainerCacheCommand cacheContainer = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + insideContainerId);
        if (cacheContainer == null) {
            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_CACHE_ERROR);
        }
        Set<Long> skuIdSet = cacheContainer.getSkuIdSet();
        boolean isCacheSkuAttrFlag = (skuIdSet.contains(skuId) && ContainerStatus.CONTAINER_STATUS_PUTAWAY == cacheContainer.getStatus()) ? true : false;
        skuIdSet.add(skuId);
        if (mgmt.getIsMixAllowed()) {
            if (rcvd.getIsMixingSku()) {
                if (cacheContainer.getIsMixAttr()) {
                    if ((StringUtils.isEmpty(mgmt.getMixAttr()) && StringUtils.isEmpty(cacheContainer.getMixAttr())) || (StringUtils.hasText(mgmt.getMixAttr()) && mgmt.getMixAttr().equals(cacheContainer.getMixAttr()))) {

                    } else {
                        throw new BusinessException(ErrorCodes.RCVD_SKU_MIXING_ATTR_ERROR);
                    }
                }
            } else {
                if (skuIdSet.size() > 1) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_MIXING_ERROR);
                }
            }
        } else {
            if (skuIdSet.size() > 1) {
                throw new BusinessException(ErrorCodes.RCVD_CONTAINER_MIXING_ERROR);
            }
        }
        cacheContainer.setSkuIdSet(skuIdSet);
        cacheContainer.setIsMixAttr(true);
        cacheContainer.setMixAttr(mgmt.getMixAttr());
        command.setRcvdUserContainerCache(cacheContainer);

        // @mender yimin.lu 2016/11/14
        // 如果容器需要初始化容器限定属性缓存
        if (isCacheSkuAttrFlag) {
            RcvdContainerCacheCommand rcvdContainerCacheCommand = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + insideContainerId + "$" + skuId);
            if (rcvdContainerCacheCommand == null) {
                // 需要开启查询数据库事务；并且，当SKUID不为空的时候，仅返回一条
                List<RcvdContainerCacheCommand> rcvdContainerCacheCommandList = this.generalRcvdManager.getUniqueSkuAttrFromWhSkuInventory(command.getInsideContainerId(), command.getSkuId(), ouId);
                if (rcvdContainerCacheCommandList == null || rcvdContainerCacheCommandList.size() == 0) {
                    throw new BusinessException(ErrorCodes.RCVD_CONTAINER_HAS_SKU_DATA_ERROR);
                }
                rcvdContainerCacheCommand = rcvdContainerCacheCommandList.get(0);
                if (rcvd.getIsLimitUniqueBatch()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getBatchNumber())) {
                        if (rcvdContainerCacheCommand.getBatchNumber().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setBatchNumber(null);
                }
                if (rcvd.getIsLimitUniqueDateOfManufacture()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getMfgDate())) {
                        if (rcvdContainerCacheCommand.getMfgDate().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setMfgDate(null);
                }
                if (rcvd.getIsLimitUniqueExpiryDate()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getExpDate())) {
                        if (rcvdContainerCacheCommand.getExpDate().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setExpDate(null);
                }
                if (rcvd.getIsLimitUniqueInvAttr1()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr1())) {
                        if (rcvdContainerCacheCommand.getInvAttr1().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr1(null);
                }
                if (rcvd.getIsLimitUniqueInvAttr2()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr2())) {
                        if (rcvdContainerCacheCommand.getInvAttr2().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr2(null);
                }
                if (rcvd.getIsLimitUniqueInvAttr3()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr3())) {
                        if (rcvdContainerCacheCommand.getInvAttr3().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr3(null);
                }
                if (rcvd.getIsLimitUniqueInvAttr4()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr4())) {
                        if (rcvdContainerCacheCommand.getInvAttr4().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvStatus(null);
                }
                if (rcvd.getIsLimitUniqueInvAttr5()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvAttr5())) {
                        if (rcvdContainerCacheCommand.getInvAttr5().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvAttr5(null);
                }
                if (rcvd.getIsLimitUniqueInvStatus()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvStatus())) {
                        if (rcvdContainerCacheCommand.getInvStatus().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvStatus(null);
                }
                if (rcvd.getIsLimitUniqueInvType()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getInvType())) {
                        if (rcvdContainerCacheCommand.getInvType().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setInvType(null);
                }
                if (rcvd.getIsLimitUniquePlaceoforigin()) {
                    if (StringUtils.hasText(rcvdContainerCacheCommand.getCountryOfOrigin())) {
                        if (rcvdContainerCacheCommand.getCountryOfOrigin().contains(",")) {
                            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_LIMIT_ERROR);
                        }
                    }
                } else {
                    rcvdContainerCacheCommand.setCountryOfOrigin(null);
                }
                rcvdContainerCacheCommand.setUserId(command.getUserId());
                rcvdContainerCacheCommand.setOuId(ouId);

                command.setRcvdSkuContainerCache(rcvdContainerCacheCommand);
                // @mender yimin.lu 2016/11/23 放到流程最后进行
                // this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX +
                // insideContainerId + "$" + skuId, rcvdContainerCacheCommand);
            }

        }

        String optList = RcvdWorkFlow.getOptMapStr(sku);
        command.setSkuUrl(optList);

        return command;
    }

    @Override
    public List<WhCartonCommand> findWhCartonByParamExt(WhCartonCommand cartonCommand) {
        return this.generalRcvdManager.findWhCartonByParamExt(cartonCommand);
    }

    @Override
    public WhAsn getCacheAsnByOccupationId(String occupationId) {
        return this.cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
    }

    @Override
    public void cacheOperUserWhenRcvd(String userId) {
        // 缓存操作者
        boolean userFlag = this.cacheManager.existsInSet(CacheKeyConstant.CACHE_OPERATOR_USER, userId);
        if (!userFlag) {
            this.cacheManager.addSet(CacheKeyConstant.CACHE_OPERATOR_USER, new String[] {userId});
        }
    }

    @Override
    public void removeInsideContainerCacheWhenScanSkuNoRcvd(Long inside, Long skuId, Long ouId, Long userId, String logId) {
        RcvdContainerCacheCommand cacheContainer = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + inside);
        if (skuId != null) {
            cacheContainer.getSkuIdSet().remove(skuId);
            if (cacheContainer.getSkuIdSet().size() == 0) {
                cacheContainer.setIsMixAttr(false);
                cacheContainer.setMixAttr("");
            }
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + inside, cacheContainer);
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
        }
    }

    @Override
    public void removeInsideContainerCacheWhenScanSkuHasRcvd(Long inside, Long outside, Long ouId, Long userId, String logId) {
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
        List<RcvdCacheCommand> list = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
        try {
            // 撤销缓存
            // 1.SN缓存
            // this.cacheManager.removeMapValue(CacheKeyConstant.CACHE_RCVD_SN, userId);
            if (null != list) {
                // 发生异常抛出。回滚数据。
                for (RcvdCacheCommand rcvd : list) {
                    try {
                        this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getLineId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                    } catch (Exception e) {
                        this.cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getLineId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                        throw e;
                    }
                    try {
                        this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                    } catch (Exception e) {
                        this.cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getLineId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                        this.cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvd.getOccupationId() + CacheKeyConstant.CACHE_KEY_SPLIT + rcvd.getSkuId(), rcvd.getSkuBatchCount());
                        throw e;
                    }
                }

            }
            // 释放容器
            this.revokeContainer(inside, ouId, userId);
            // 2.CACHE_RCVD中UserId对应缓存
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
            // 清除容器-用户缓存
            this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + inside);
            // 清除容器-商品缓存
            // @mender yimin.lu 2016/11/14
            this.cacheManager.remonKeys(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + inside + "$*");
            // 清除托盘-货箱缓存
            if (outside != null) {
                this.cacheManager.popListHead(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outside);
            }
        } catch (Exception e) {
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId, cacheSn, CacheKeyConstant.CACHE_ONE_HOUR);
            this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId, list, CacheKeyConstant.CACHE_ONE_HOUR);
            throw new BusinessException(ErrorCodes.RCVD_CANCEL_ERROR);
        }

    }

    @Override
    public String getCacheKeyPrefixWhenRcvd(String cacheKey) {
        return null;
    }

    @Override
    public void cacheScanedDefeatSkuNoSnWhenGeneralRcvd(WhSkuInventoryCommand command) {
        Integer ctnCount = command.getSkuBatchCount() * command.getQuantity();// 批量收货数量
        List<RcvdSnCacheCommand> cacheSn = this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId());
        if (null == cacheSn) {
            cacheSn = new ArrayList<RcvdSnCacheCommand>();
        }
        RcvdSnCacheCommand rcvdSn = command.getSn();
        // @mender yimin.lu 2016/10/31扫描残次原因时候，需要生成残次条码
        // @mender yimin.lu 2016/10/28
        // @mender yimin.lu 2016/9/8
        for (int i = 0; i < command.getSnCount(); i++) {// 此处For循环主要针对非SN的残次品，如果是SN商品，snCount=1
            RcvdSnCacheCommand newSn = new RcvdSnCacheCommand();
            newSn.setDefectReasonsId(rcvdSn.getDefectReasonsId());
            newSn.setDefectSource(command.getSnSource());
            newSn.setDefectTypeId(rcvdSn.getDefectTypeId());
            // 残次条码 调用条码生成器
            if (null != rcvdSn.getDefectTypeId()) {
                String barCode = this.codeManager.generateCode(Constants.WMS, Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null);
                newSn.setDefectWareBarCode(barCode);
            }
            // 设置序列号及序列号管理类型
            if (StringUtils.hasText(rcvdSn.getSn())) {
                newSn.setSn(rcvdSn.getSn());
                SkuRedisCommand sku = this.skuRedisManager.findSkuMasterBySkuId(command.getSkuId(), command.getOuId(), command.getLogId());
                if (null != sku && null != sku.getSkuMgmt()) {

                    SkuMgmt skuMgmt = sku.getSkuMgmt();
                    newSn.setSerialNumberType(skuMgmt.getSerialNumberType());
                }
            }
            cacheSn.add(newSn);
        }

        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + command.getUserId(), cacheSn, 60 * 60);
        this.cacheContainerSkuAttr(command);

    }
}
