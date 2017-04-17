/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.seeding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSeedingCollectionManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkManager;
import com.baozun.scm.primservice.whoperation.model.seeding.SeedingLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.warehouse.WarehouseMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionSeedingWall;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

@Service("seedingManagerProxy")
public class SeedingManagerProxyImpl extends BaseManagerImpl implements SeedingManagerProxy {
    public static final Logger log = LoggerFactory.getLogger(SeedingManagerProxyImpl.class);

    @Autowired
    private WhSeedingCollectionManager whSeedingCollectionManager;

    @Autowired
    private SeedingManager seedingManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WhWorkManager whWorkManager;

    @Autowired
    private WhWorkLineManager whWorkLineManager;

    @Autowired
    private WarehouseManager warehouseManager;


    /**
     * 初始化播种墙缓存数据
     *
     * @param facilityId
     * @param ouId
     * @param userId
     * @param logId
     */
    public void initFacilityRedis(Long functionId, Long facilityId, Long ouId, Long userId, String logId) {

        // 播种设施
        WhOutboundFacilityCommand facilityCommand = seedingManager.getOutboundFacilityById(facilityId, ouId);
        if (null == facilityCommand) {
            throw new BusinessException("播种墙不存在");
        }
        if (null == facilityCommand.getBatch()) {
            throw new BusinessException("播种墙不存在");
        }

        WhFunctionSeedingWall function = this.getFunctionFromCache(functionId, facilityId, facilityCommand.getBatch(), ouId, logId);

        // 集货信息
        List<WhSeedingCollectionCommand> seedingCollectionList = whSeedingCollectionManager.getSeedingCollectionByFacilityId(facilityId, ouId);
        if (null == seedingCollectionList || seedingCollectionList.isEmpty()) {
            throw new BusinessException("播种墙集货信息不存在");
        }
        boolean isStartSeeding = false;
        for (WhSeedingCollectionCommand seedingCollection : seedingCollectionList) {
            if (!CollectionStatus.TO_SEED.equals(seedingCollection.getCollectionStatus())) {
                isStartSeeding = true;
                break;
            }
        }
        if (isStartSeeding) {
            // TODO 测试，前面已清除缓存，此处需要初始化缓存
            return;
        }
        // 仓库信息
        WarehouseMgmt warehouseMgmt = warehouseManager.findWhMgmtByOuId(ouId);

        // 批次下的出库单信息
        List<WhSeedingWallLattice> seedingBatchOdoInfoList = whWorkManager.getSeedingBatchOdoInfo(facilityCommand.getBatch(), ouId);
        if (seedingBatchOdoInfoList.size() > warehouseMgmt.getSeedingOdoQty()) {
            throw new BusinessException("批次出库单数大于播种墙货格数");
        }
        Map<Long, SeedingLattice> latticeMap = new HashMap<>();

        for (Long index = 0L; index < seedingBatchOdoInfoList.size(); index++) {
            Long latticeNo = index + 1;
            // 货格号
            WhSeedingWallLattice whSeedingWallLattice = seedingBatchOdoInfoList.get(index.intValue());

            SeedingLattice seedingLattice = new SeedingLattice();
            seedingLattice.setLatticeNo(latticeNo);
            seedingLattice.setSeedQty(0);
            seedingLattice.setOdoId(whSeedingWallLattice.getOdoId());

            switch (function.getShowCode()) {
                case Constants.SHOW_CODE_ODO_CODE:
                    seedingLattice.setShowCode(whSeedingWallLattice.getOdoCode());
                    break;
                case Constants.SHOW_CODE_EC_ORDER_CODE:
                    seedingLattice.setShowCode(whSeedingWallLattice.getEcOrderCode());
                    break;
                case Constants.SHOW_CODE_EXT_CODE:
                    seedingLattice.setShowCode(whSeedingWallLattice.getExtCode());
                    break;
                default:
                    throw new BusinessException("功能显示单据号类型错误");
            }
            latticeMap.put(latticeNo, seedingLattice);

            // 货格绑定出库单
            this.saveSeedingOdoBindLatticeToCache(whSeedingWallLattice, facilityCommand.getId(), facilityCommand.getBatch(), ouId, latticeNo, logId);
        }

        // 保存货格信息，用于刷新显示播种货格
        this.saveLatticeMapToCache(facilityId, facilityCommand.getBatch(), ouId, latticeMap, logId);

    }

    /**
     * 货格对应出库单信息
     *
     * @param seedingWallLattice
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param logId
     */
    public void saveSeedingOdoBindLatticeToCache(WhSeedingWallLattice seedingWallLattice, Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号
        // Value：WhSeedingWallLattice

        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_BIND_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        cacheManager.setObject(cacheKey, seedingWallLattice, CacheConstants.CACHE_ONE_WEEK);
    }

    /**
     * 货格对应出库单信息
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param logId
     */
    public WhSeedingWallLattice getSeedingOdoBindLatticeFromCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号
        // Value：WhSeedingWallLattice

        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_BIND_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        return cacheManager.getObject(cacheKey);
    }


    public List<WhSeedingWallLattice> getFacilityBatchOdoCache(Long facilityId, String batchNo, Long ouId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_BIND_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo;
        // 所有货格对于的出库单的缓存key
        List<String> cacheKeyList = cacheManager.Keys(cacheKey + "-*");
        List<WhSeedingWallLattice> seedingWallLatticeList = new ArrayList<>();
        for (String key : cacheKeyList) {
            WhSeedingWallLattice seedingWallLattice = cacheManager.getObject(key.substring(key.indexOf(CacheConstants.CACHE_SEEDING_ODO_BIND_LATTICE)));
            if (null == seedingWallLattice) {
                throw new BusinessException("出库单信息缓存错误");
            }
            seedingWallLatticeList.add(seedingWallLattice);
        }

        return seedingWallLatticeList;
    }



    /**
     * 
     * @param functionId
     * @param ouId
     * @param logId
     */
    public WhFunctionSeedingWall getFunctionFromCache(Long functionId, Long facilityId, String batchNo, Long ouId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_FUNCTION + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + functionId;
        WhFunctionSeedingWall function = null;
        try {
            function = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            log.error("get function cache error, functionId is:[{}], ouId is:[{}], logId is:[{}], ex is:[{}]", functionId, ouId, logId, e);
        }
        if (null == function) {
            function = seedingManager.findFunctionById(functionId, ouId);

            if (null != function) {
                try {
                    cacheManager.setObject(cacheKey, function, CacheConstants.CACHE_ONE_WEEK);
                } catch (Exception e) {
                    log.error("save function cache error, functionId is:[{}], ouId is:[{}], logId is:[{}], ex is:[{}]", functionId, ouId, logId, e);
                }
            }
        }
        // TODO 测试 设置功能参数
        // function = new WhFunctionSeedingWall();
        // function.setScanPattern(2);
        // function.setIsScanGoodsLattice(true);
        // function.setSeedingWallPattern(2);
        // function.setShowCode("3");

        return function;
    }



    public void saveLatticeMapToCache(Long facilityId, String batchNo, Long ouId, Map<Long, SeedingLattice> latticeMap, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo;

        cacheManager.setObject(cacheKey, latticeMap, CacheConstants.CACHE_ONE_WEEK);
    }


    /**
     * 获取播种货格信息用于显示
     *
     * @param facilityId
     * @param ouId
     * @param logId
     * @return
     */
    public Map<Long, SeedingLattice> getLatticeMapFromCache(Long facilityId, String batchNo, Long ouId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo;

        return cacheManager.getObject(cacheKey);
    }



    @Override
    public void saveLatticeCurrentSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String outboundBoxCode, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        cacheManager.setObject(cacheKey, outboundBoxCode, CacheConstants.CACHE_ONE_WEEK);
    }

    @Override
    public String getLatticeCurrentSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        return cacheManager.getObject(cacheKey);
    }


    @Override
    public void saveLatticeSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String outboundBoxCode, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo + "-" + outboundBoxCode;
        cacheManager.setObject(cacheKey, outboundBoxCode, CacheConstants.CACHE_ONE_WEEK);
    }

    @Override
    public List<String> getLatticeSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        List<String> cacheKeyList = cacheManager.Keys(cacheKey + "-" + "*");
        List<String> outboundBoxCodeList = new ArrayList<>();
        for (String key : cacheKeyList) {
            String outboundBoxCode = cacheManager.getObject(key.substring(key.indexOf(CacheConstants.CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH)));
            if (null != outboundBoxCode) {
                outboundBoxCodeList.add(outboundBoxCode);
            }
        }

        // 当前播种的出库箱
        String currentOutboundBoxCode = this.getLatticeCurrentSeedingOutboundBoxCache(facilityId, batchNo, ouId, latticeNo, logId);
        outboundBoxCodeList.add(currentOutboundBoxCode);

        return outboundBoxCodeList;
    }

    public List<String> getFacilityCurrentSeedingOutboundBoxCode(Long facilityId, String batchNo, Long ouId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX + "-" + ouId + "-" + facilityId + "-" + batchNo;

        List<String> cacheKeyList = cacheManager.Keys(cacheKey + "-" + "*");
        List<String> outboundBoxCodeList = new ArrayList<>();
        for (String key : cacheKeyList) {
            String outboundBoxCode = cacheManager.getObject(key.substring(key.indexOf(CacheConstants.CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX)));
            if (null != outboundBoxCode) {
                outboundBoxCodeList.add(outboundBoxCode);
            }
        }
        return outboundBoxCodeList;
    }

    @Override
    public void saveOutboundBoxCollectionLineCache(Long facilityId, String batchNo, Long ouId, String outboundBoxCode, Map<Long, WhSeedingCollectionLineCommand> collectionLineMap, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_OUTBOUNDBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + outboundBoxCode;
        cacheManager.setObject(cacheKey, collectionLineMap, CacheConstants.CACHE_ONE_WEEK);
    }

    @Override
    public Map<Long, WhSeedingCollectionLineCommand> getOutboundBoxCollectionLineCache(Long facilityId, String batchNo, Long ouId, String outboundBoxCode, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_OUTBOUNDBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + outboundBoxCode;
        return cacheManager.getObject(cacheKey);
    }

    @Override
    public void saveLatticeCollectionLineCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, Map<Long, WhSeedingCollectionLineCommand> collectionLineMap, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        cacheManager.setObject(cacheKey, collectionLineMap, CacheConstants.CACHE_ONE_WEEK);
    }

    @Override
    public Map<Long, WhSeedingCollectionLineCommand> getLatticeCollectionLineCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LATTICE_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo;
        return cacheManager.getObject(cacheKey);
    }

    /**
     * @param seedingCollectionId
     * @param ouId
     * @return
     */
    @Override
    public List<WhSeedingCollectionLineCommand> getSeedingCollectionLineByCollectionFromCache(Long facilityId, String batchNo, Long ouId, Long seedingCollectionId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_TURNOVERBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + seedingCollectionId;

        // cacheManager.remonKeys(cacheKey);
        List<WhSeedingCollectionLineCommand> seedingCollectionLineCommandList = null;
        try {
            seedingCollectionLineCommandList = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            log.error("getSeedingCollectionLineByCollectionFromCache error, facilityId is:[{}], batchNo is:[{}], ouId is:[{}], seedingCollectionId is:[{}], logId is:[{}], ex is:[{}]", facilityId, batchNo, ouId, seedingCollectionId, logId, e);
        }
        if (null == seedingCollectionLineCommandList || seedingCollectionLineCommandList.isEmpty()) {
            seedingCollectionLineCommandList = seedingManager.getSeedingCollectionLineByCollection(seedingCollectionId, ouId);

            if (null != seedingCollectionLineCommandList) {
                try {
                    cacheManager.setObject(cacheKey, seedingCollectionLineCommandList, CacheConstants.CACHE_ONE_WEEK);
                } catch (Exception e) {
                    log.error("save SeedingCollectionLineByCollection error, facilityId is:[{}], batchNo is:[{}], ouId is:[{}], seedingCollectionId is:[{}], logId is:[{}], ex is:[{}]", facilityId, batchNo, ouId, seedingCollectionId, logId, e);
                }
            }
        }
        return seedingCollectionLineCommandList;
    }

    /**
     *
     * @param seedingCollectionId
     * @param ouId
     * @return
     */
    @Override
    public void saveSeedingCollectionLineByCollectionToCache(Long facilityId, String batchNo, Long ouId, Long seedingCollectionId, List<WhSeedingCollectionLineCommand> collectionLineList, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_TURNOVERBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + seedingCollectionId;

        cacheManager.setObject(cacheKey, collectionLineList, CacheConstants.CACHE_ONE_WEEK);
    }

    @Override
    public List<WhSeedingCollectionLineCommand> getSeedingCollectionLineByOdoFromCache(Long facilityId, String batchNo, Long ouId, Long odoId, String logId) {
        List<WhSeedingCollectionLineCommand> odoLineList = new ArrayList<>();

        List<WhSeedingCollectionCommand> facilitySeedingCollectionList = whSeedingCollectionManager.getSeedingCollectionByFacilityId(facilityId, ouId);

        for (WhSeedingCollectionCommand seedingCollection : facilitySeedingCollectionList) {
            List<WhSeedingCollectionLineCommand> collectionLineList = this.getSeedingCollectionLineByCollectionFromCache(facilityId, batchNo, ouId, seedingCollection.getId(), logId);
            for (WhSeedingCollectionLineCommand line : collectionLineList) {
                if (odoId.equals(line.getOdoId())) {
                    odoLineList.add(line);
                }
            }
        }
        return odoLineList;
    }

    public void facilityBatchFinishedSeeding(Long facilityId, Map<SeedingLattice, List<WhSeedingCollectionLineCommand>> latticeSeedingLineMap, Map<SeedingLattice, Map<String, List<WhSeedingCollectionLineCommand>>> latticeOutboundBoxSeedingLineMap,
            Long userId, Long ouId, String logId) {
        WhOutboundFacilityCommand facilityCommand = seedingManager.getOutboundFacilityById(facilityId, ouId);
        // 仓库信息
        WarehouseMgmt warehouseMgmt = warehouseManager.findWhMgmtByOuId(ouId);
        warehouseMgmt.getIsTabbInvTotal();

        List<WhSeedingWallLattice> facilitySeedingOdoList = this.getFacilityBatchOdoCache(facilityId, facilityCommand.getBatch(), ouId, logId);

        // 播种墙的所有容器库存
        List<WhSkuInventory> facilitySeedingSkuInventoryList = new ArrayList<>();

        if (null != latticeSeedingLineMap) {
            for (Map.Entry<SeedingLattice, List<WhSeedingCollectionLineCommand>> latticeSeedingLineMapEntry : latticeSeedingLineMap.entrySet()) {
                SeedingLattice seedingLattice = latticeSeedingLineMapEntry.getKey();
                List<WhSeedingCollectionLineCommand> latticeSeedingLineList = latticeSeedingLineMapEntry.getValue();
                for (WhSeedingCollectionLineCommand line : latticeSeedingLineList) {
                    if (line.getSeedingQty() > 0) {
                        WhSkuInventory skuInventory = this.createWhSkuInventory(line, ouId, logId);
                        facilitySeedingSkuInventoryList.add(skuInventory);
                    }
                }
            }
        } else if (null != latticeOutboundBoxSeedingLineMap) {
            for (Map.Entry<SeedingLattice, Map<String, List<WhSeedingCollectionLineCommand>>> latticeBoxMapEntry : latticeOutboundBoxSeedingLineMap.entrySet()) {
                SeedingLattice seedingLattice = latticeBoxMapEntry.getKey();
                Map<String, List<WhSeedingCollectionLineCommand>> boxSeedingLineMap = latticeBoxMapEntry.getValue();
                for (Map.Entry<String, List<WhSeedingCollectionLineCommand>> boxSeedingLineMapEntry : boxSeedingLineMap.entrySet()) {
                    String outboundBoxCode = boxSeedingLineMapEntry.getKey();
                    List<WhSeedingCollectionLineCommand> boxSeedingLineList = boxSeedingLineMapEntry.getValue();
                    for (WhSeedingCollectionLineCommand line : boxSeedingLineList) {
                        if (line.getSeedingQty() > 0) {
                            WhSkuInventory skuInventory = this.createWhSkuInventory(line, ouId, logId);
                            facilitySeedingSkuInventoryList.add(skuInventory);
                        }
                    }
                }
            }
        }
        try {
            // TODO 测试 不保存库存
            seedingManager.batchFinishedSeeding(facilityId, facilityCommand.getBatch(), facilitySeedingOdoList, facilitySeedingSkuInventoryList, warehouseMgmt.getIsTabbInvTotal(), userId, ouId, logId);

            // 清除缓存
            this.releaseFacilityBatchRedis(facilityId, facilityCommand.getBatch(), ouId, logId);

        } catch (Exception e) {
            log.error(
                    "SeedingManagerProxyImpl facilityBatchFinishedSeeding error, facilityId is:[{}], facilitySeedingOdoList is:[{}], facilitySeedingSkuInventoryList is:[{}], IsTabbInvTotal is:[{}], userId is:[{}], ouId is:[{}], logId is:[{}], ex is:[{}]",
                    facilityId, facilitySeedingOdoList, facilitySeedingSkuInventoryList, warehouseMgmt.getIsTabbInvTotal(), userId, ouId, logId, e);
        }
    }

    private WhSkuInventory createWhSkuInventory(WhSeedingCollectionLineCommand collectionSeedingLine, Long ouId, String logId) {
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setSkuId(collectionSeedingLine.getSkuId());
        skuInventory.setCustomerId(collectionSeedingLine.getCustomerId());
        skuInventory.setStoreId(collectionSeedingLine.getStoreId());
        // 占用编码是内部编码
        skuInventory.setOccupationCode(collectionSeedingLine.getOdoCode());
        skuInventory.setOccupationLineId(collectionSeedingLine.getOdoLineId());
        skuInventory.setSeedingWallCode(collectionSeedingLine.getFacilityCode());
        skuInventory.setContainerLatticeNo(collectionSeedingLine.getLatticeNo());
        skuInventory.setOutboundboxCode(collectionSeedingLine.getOutboundBoxCode());
        skuInventory.setOnHandQty(collectionSeedingLine.getSeedingQty().doubleValue());
        skuInventory.setAllocatedQty(0d);
        skuInventory.setToBeFilledQty(0d);
        skuInventory.setFrozenQty(0d);
        skuInventory.setInvStatus(Long.valueOf(collectionSeedingLine.getInvStatus()));
        skuInventory.setInvType(collectionSeedingLine.getInvType());
        skuInventory.setBatchNumber(collectionSeedingLine.getBatchNumber());
        skuInventory.setMfgDate(collectionSeedingLine.getMfgDate());
        skuInventory.setExpDate(collectionSeedingLine.getExpDate());
        skuInventory.setCountryOfOrigin(collectionSeedingLine.getCountryOfOrigin());
        skuInventory.setInvAttr1(collectionSeedingLine.getInvAttr1());
        skuInventory.setInvAttr2(collectionSeedingLine.getInvAttr2());
        skuInventory.setInvAttr3(collectionSeedingLine.getInvAttr3());
        skuInventory.setInvAttr4(collectionSeedingLine.getInvAttr4());
        skuInventory.setInvAttr5(collectionSeedingLine.getInvAttr5());
        // TODO 不重新计算UUID，无法确认inventory_sn表的对应
        String uuid = null;
        // try {
        // uuid = SkuInventoryUuid.invUuid(skuInventory);
        // } catch (Exception e) {
        // log.error("caseLevelReceivingCompleted createWhSkuInventory error, throw NoSuchAlgorithmException, skuInventory is:[{}], logId is:[{}]",
        // skuInventory, logId);
        // throw new BusinessException(ErrorCodes.CASELEVEL_UUID_ERROR);
        // }
        skuInventory.setUuid(collectionSeedingLine.getUuid());
        skuInventory.setIsLocked(false);
        skuInventory.setOuId(collectionSeedingLine.getOuId());
        skuInventory.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO);
        skuInventory.setLastModifyTime(new Date());

        return skuInventory;
    }

    public void releaseFacilityBatchRedis(Long facilityId, String batchNo, Long ouId, String logId) {
        WhOutboundFacilityCommand facilityCommand = seedingManager.getOutboundFacilityById(facilityId, ouId);
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCommand.getFacilityCode() + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_ODO_BIND_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_FUNCTION + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_OUTBOUNDBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_TURNOVERBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");

    }

}
