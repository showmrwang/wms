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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSeedingCollectionManager;
import com.baozun.scm.primservice.whoperation.model.seeding.SeedingLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionSeedingWall;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

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
    private OdoManager odoManager;



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
     * 货格对应出库单明细信息
     *
     * @param ouId
     * @param facilityCode
     * @param batch
     * @param latticeNo
     * @param odoCode
     */
    public void saveSeedingOdoLineBindLatticeToCache(Long ouId,Long facilityId,String batch,Long latticeNo,Long odoId,Map<String,WhSeedingWallLatticeLine> lineMap){
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE【WMS出库单号】
        // Value：Map<String,WhSeedingWallLatticeLine>
        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_LINE_BIND_LATTICE + "-" + ouId + "-" + facilityId + "-" + batch + "-" + latticeNo + "-" + odoId;
        cacheManager.setObject(cacheKey, lineMap, CacheConstants.CACHE_ONE_WEEK);
    }
    
    /**
     * 获取货格对应出库单明细信息
     *
     * @param ouId
     * @param facilityCode
     * @param batch
     * @param latticeNo
     * @param odoCode
     */
    public Map<String,WhSeedingWallLatticeLine> getSeedingOdoLineBindLatticeToCache(Long ouId,Long facilityId,String batch,Long latticeNo,Long odoId){
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE【WMS出库单号】
        // Value：Map<String,WhSeedingWallLatticeLine>
        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_LINE_BIND_LATTICE + "-" + ouId + "-" + facilityId + "-" + batch + "-" + latticeNo + "-" + odoId;
        Map<String,WhSeedingWallLatticeLine> map = cacheManager.getObject(cacheKey);
        try {
            //如果没有缓存数据就封装数据到redis并返回
            if(map==null){
                //货格对应出库单明细信息
                List<Long> odoLineId=new ArrayList<Long>();
                odoLineId.add(odoId);
                //出库单下的明细列表
                List<OdoLineCommand> odoLineList=odoManager.findOdoLineByOdoId(odoLineId, ouId);
                
                Map<String,WhSeedingWallLatticeLine> lineMap=new HashMap<String,WhSeedingWallLatticeLine>();
                for(OdoLineCommand odoLineCommand:odoLineList){
                    //通过uuid分组
                    List<WhSeedingWallLatticeLine> seedingBatchOdoLineInfoList=whSeedingCollectionManager.getSeedingBatchOdoLineInfo(odoLineCommand.getId(),ouId);
                    if(seedingBatchOdoLineInfoList!=null){
                        //封装订单的明细信息
                        for(WhSeedingWallLatticeLine seedingWallLatticeLine:seedingBatchOdoLineInfoList){
                            String uuid=this.createNewUuid(seedingWallLatticeLine,null);
                            WhSeedingWallLatticeLine latticeLine=lineMap.get(seedingWallLatticeLine.getSkuId()+"_"+uuid);
                            if(latticeLine==null){
                                lineMap.put(seedingWallLatticeLine.getSkuId()+"_"+uuid, seedingWallLatticeLine);
                            }
                        }
                    }
                }
                this.saveSeedingOdoLineBindLatticeToCache(ouId,facilityId,batch,latticeNo,odoId,lineMap);
                return lineMap;
            }
        } catch (Exception e) {
            log.error("saveSeedingOdoLineBindLatticeToCache cache error, facilityId is:[{}], ouId is:[{}], batch is:[{}], latticeNo is:[{}], logId is:[{}], ex is:[{}]", facilityId, ouId, batch, latticeNo, logId, e);
        }
        return map;
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
                throw new BusinessException(ErrorCodes.SEEDING_SEEDING_ODO_CACHE_ERROR);
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
        if(null != currentOutboundBoxCode && !outboundBoxCodeList.contains(currentOutboundBoxCode)) {
            outboundBoxCodeList.add(currentOutboundBoxCode);
        }

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
    public void removeEmptyOutboundBoxCache(Long facilityId, String batchNo, Long ouId, String outboundBoxCode, Long latticeNo) {
        //删除出库箱的明细缓存
        String boxLineCacheKey = CacheConstants.CACHE_SEEDING_OUTBOUNDBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + outboundBoxCode;
        cacheManager.remonKeys(boxLineCacheKey);

        //删除货格绑定的出库箱
        String latticeBoxCacheKey = CacheConstants.CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH + "-" + ouId + "-" + facilityId + "-" + batchNo + "-" + latticeNo + "-" + outboundBoxCode;
        cacheManager.remonKeys(latticeBoxCacheKey);
    }

    @Override
    public void saveLastTimeOutboundBoxSeedingCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String outboundBoxCode, WhSeedingCollectionLineCommand seedingCollectionLine, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LAST_TIME_OUTBOUND_BOX_SEEDING_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo;
        Map<Long, Map<String, WhSeedingCollectionLineCommand>> latticeLastTimeSeedingCache = new HashMap<>();
        Map<String, WhSeedingCollectionLineCommand> lastTimeSeedingCache = new HashMap<>();
        lastTimeSeedingCache.put(outboundBoxCode, seedingCollectionLine);
        latticeLastTimeSeedingCache.put(latticeNo, lastTimeSeedingCache);
        cacheManager.setObject(cacheKey, latticeLastTimeSeedingCache, CacheConstants.CACHE_ONE_WEEK);
    }


    @Override
    public Map<Long, Map<String, WhSeedingCollectionLineCommand>> getLastTimeOutboundBoxSeedingCache(Long facilityId, String batchNo, Long ouId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LAST_TIME_OUTBOUND_BOX_SEEDING_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo;
        return cacheManager.getObject(cacheKey);
    }


    @Override
    public void removeLastTimeOutboundBoxSeedingCache(Long facilityId, String batchNo, Long ouId, String logId) {
        String cacheKey = CacheConstants.CACHE_SEEDING_LAST_TIME_OUTBOUND_BOX_SEEDING_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo;
        cacheManager.remove(cacheKey);
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

        List<WhSeedingCollectionCommand> facilitySeedingCollectionList = whSeedingCollectionManager.getSeedingCollectionByFacilityId(facilityId, ouId, CollectionStatus.TO_SEED);

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

    public void facilityBatchFinishedSeeding(Long functionId, Long facilityId, Long userId, Long ouId, String logId) {
        // 播种设施
        WhOutboundFacilityCommand facilityCommand = seedingManager.getOutboundFacilityById(facilityId, ouId);
        facilityCommand.setStatus(String.valueOf(Constants.WH_FACILITY_STATUS_4));
        facilityCommand.setOperatorId(userId);
        seedingManager.updateOutboundFacility(facilityCommand, logId);

        try {
            // 清除缓存
            this.releaseFacilityBatchRedis(facilityId, facilityCommand.getBatch(), ouId, logId);

        } catch (Exception e) {
            log.error("SeedingManagerProxyImpl facilityBatchFinishedSeeding error, facilityId is:[{}], userId is:[{}], ouId is:[{}], logId is:[{}], ex is:[{}]", facilityId, userId, ouId, logId, e);
        }
    }

    public WhSkuInventory createWhSkuInventory(WhSeedingCollectionLineCommand collectionSeedingLine, Long ouId, String logId) {
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
        // TODO 重新计算UUID，无法确认inventory_sn表的对应
        String uuid = null;
        try {
            uuid = SkuInventoryUuid.invUuid(skuInventory);
        } catch (Exception e) {
            log.error("seeding createWhSkuInventory error, throw NoSuchAlgorithmException, skuInventory is:[{}], logId is:[{}]", skuInventory, logId);
            throw new BusinessException(ErrorCodes.SEEDING_SEEDING_CREATE_UUID_ERROR);
        }
        skuInventory.setUuid(uuid);
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
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE + "-" + ouId + "-" + facilityId + "-" + batchNo + "*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_OUTBOUNDBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LATTICE_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_TURNOVERBOX_COLLECTION_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "-*");
        cacheManager.remonKeys(CacheConstants.CACHE_SEEDING_LAST_TIME_OUTBOUND_BOX_SEEDING_LINE + "-" + ouId + "-" + facilityId + "-" + batchNo + "*");

    }
    
    public String createNewUuid(WhSeedingWallLatticeLine lattice,WhSeedingCollectionLineCommand lineCommand){
        String uuid=null;
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        WhSkuInventory inv = new WhSkuInventory();
        try {
            if(lattice!=null){
                inv.setSkuId(lattice.getSkuId());
                inv.setInvStatus(lattice.getInvStatus() == null ? null : Long.parseLong(lattice.getInvStatus()));
                inv.setInvType(lattice.getInvType());
                //inv.setBatchNumber(lattice.getBatchNumber());
                inv.setMfgDate(lattice.getMfgDate()==null?null:formater.parse(lattice.getMfgDate()));
                inv.setExpDate(lattice.getExpDate()==null?null:formater.parse(lattice.getExpDate()));
                inv.setCountryOfOrigin(lattice.getCountryOfOrigin());
                inv.setInvAttr1(lattice.getInvAttr1());
                inv.setInvAttr2(lattice.getInvAttr2());
                inv.setInvAttr3(lattice.getInvAttr3());
                inv.setInvAttr4(lattice.getInvAttr4());
                inv.setInvAttr5(lattice.getInvAttr5());
            }
            if(lineCommand!=null){
                inv.setSkuId(lineCommand.getSkuId());
                inv.setInvStatus(lineCommand.getInvStatus() == null ? null : Long.parseLong(lineCommand.getInvStatus()));
                inv.setInvType(lineCommand.getInvType());
                //inv.setBatchNumber(lineCommand.getBatchNumber());
                inv.setMfgDate(lineCommand.getMfgDate());
                inv.setExpDate(lineCommand.getExpDate());
                inv.setCountryOfOrigin(lineCommand.getCountryOfOrigin());
                inv.setInvAttr1(lineCommand.getInvAttr1());
                inv.setInvAttr2(lineCommand.getInvAttr2());
                inv.setInvAttr3(lineCommand.getInvAttr3());
                inv.setInvAttr4(lineCommand.getInvAttr4());
                inv.setInvAttr5(lineCommand.getInvAttr5());
            }
            uuid=SkuInventoryUuid.invUuid(inv);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uuid;
    }

}
