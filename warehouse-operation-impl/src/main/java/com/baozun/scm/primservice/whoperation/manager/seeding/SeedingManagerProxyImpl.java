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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSeedingCollectionManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkManager;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeObb;
import com.baozun.scm.primservice.whoperation.model.warehouse.WarehouseMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;

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
    public void initFacilityRedis(Long facilityId, String batchNo, Long ouId, Long userId, String logId) {

        // 播种设施
        WhOutboundFacilityCommand facilityCommand = seedingManager.getOutboundFacilityById(facilityId, ouId);

        // 集货信息
        List<WhSeedingCollectionCommand> seedingCollectionList = whSeedingCollectionManager.getSeedingCollectionByFacilityId(facilityId, ouId);
        if (null == seedingCollectionList || seedingCollectionList.isEmpty()) {
            throw new BusinessException("播种墙集货信息不存在");
        }
        // TODO 保存集货表的周转箱列表缓存
        this.saveSeedingBatchTurnoverBoxListCache(seedingCollectionList, facilityCommand.getFacilityCode(), batchNo, ouId, logId);

        // 仓库信息
        WarehouseMgmt warehouseMgmt = warehouseManager.findWhMgmtByOuId(ouId);
        // 出库单信息
        List<WhSeedingWallLattice> seedingBatchOdoInfoList = whWorkManager.getSeedingBatchOdoInfo(batchNo, ouId);
        if(seedingBatchOdoInfoList.size() > warehouseMgmt.getSeedingOdoQty()){
            throw new BusinessException("批次出库单数大于播种墙货格数");
        }
        for (int i = 0; i < seedingBatchOdoInfoList.size(); i++) {
            WhSeedingWallLattice whSeedingWallLattice = seedingBatchOdoInfoList.get(i);
            // TODO 货格绑定出库单
            this.saveSeedingOdoBindGridToCache(whSeedingWallLattice, facilityCommand.getFacilityCode(), batchNo, ouId, String.valueOf(i), logId);

            List<WhSeedingWallLatticeLine> seedingBatchOdoLineInfoList = seedingManager.getSeedingOdoLineInfo(whSeedingWallLattice.getOdoId(), ouId);
            Map<String, WhSeedingWallLatticeLine> odoLineMap = new HashMap<>();
            for(WhSeedingWallLatticeLine seedingWallLatticeLine : seedingBatchOdoLineInfoList){
                //odoLineMap.put(seedingWallLatticeLine.getSkuId() + "_" + seedingWallLatticeLine.getUuid(), seedingWallLatticeLine);
            }
            //TODO 保存出库单明细缓存
            this.saveSeedingWallLatticeLineToCache(facilityCommand.getFacilityCode(), batchNo, String.valueOf(i), whSeedingWallLattice.getOdoCode(), odoLineMap, ouId, logId);
        }
        /**
         * 扫描周转箱，得到周转箱的集货信息getTurnoverBoxInfoFromCache
         * 扫描sku，得到skuId
         * 获取周转箱集货信息的keySet，拆分key，得到周转箱内的skuId，判断相同的skuId得到对应的uuId
         * 通过skuId判断商品是否在周转箱内，得到商品uuid
         * 根据uuid判断哪些出库单需要该商品，提示放入哪个货格
         *
         * 出库单明细如果占用了多个库存记录，无法知道拣货时各个uuid的库存拣了多少
         * // TODO 绑定出库箱逻辑
         * 
         */




    }

    /**
     * 保存集货表的周转箱列表缓存
     *
     * @param seedingCollectionList
     * @param facilityCode
     * @param batchNo
     * @param ouId
     * @param logId
     */
    public void saveSeedingBatchTurnoverBoxListCache(List<WhSeedingCollectionCommand> seedingCollectionList, String facilityCode, String batchNo, Long ouId, String logId) {
        // Key：SEEDING-COLLECTION-仓库ID-播种墙CODE-批次号
        // Value：List<WhSeedingCollectionCommand>
        String cacheKey = CacheConstants.CACHE_SEEDING_COLLECTION + "-" + ouId + "-" + facilityCode + "-" + batchNo;
        cacheManager.setObject(cacheKey, seedingCollectionList, CacheConstants.CACHE_ONE_WEEK);
    }

    /**
     * 获取集货表的周转箱列表缓存
     *
     * @param facilityCode
     * @param batchNo
     * @param ouId
     * @param logId
     * @return
     */
    public List<WhSeedingCollectionCommand> getSeedingBatchTurnoverBoxListFromCache(String facilityCode, String batchNo, Long ouId, String logId) {
        // Key：SEEDING-COLLECTION-仓库ID-播种墙CODE-批次号
        // Value：List<WhSeedingCollectionCommand>
        String cacheKey = CacheConstants.CACHE_SEEDING_COLLECTION + "-" + ouId + "-" + facilityCode + "-" + batchNo;
        return cacheManager.getObject(cacheKey);
    }


    /**
     * 周转箱缓存在周转箱移动到播种墙区域的时候就已保存， 在扫描播种墙时通过该方法获取周转箱的缓存信息，
     * 如果移动到播种墙时没有保存，此时再重新保存一次
     * 
     * @param facilityCode
     * @param batchNo
     * @param containerCode
     * @param ouId
     * @param logId
     * @return
     */
    public Map<String, WhSeedingCollectionLine> getTurnoverBoxInfoFromCache(String facilityCode, String batchNo, String containerCode, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-周转箱CODE
        // Value：Map<skuId,WhSeedingCollectionLine>
        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + containerCode;
        Map<String, WhSeedingCollectionLine> boxSkuMap = boxSkuMap = cacheManager.getObject(cacheKey);

        if (null == boxSkuMap || boxSkuMap.isEmpty()) {
            ContainerCommand containerCommand = seedingManager.getContainerByCode(containerCode, ouId);
            // 查找容器库存
            List<WhSeedingCollectionLine> dataList = seedingManager.findSeedingDataByContainerId(containerCommand.getId(), ouId);
            if (null != dataList && !dataList.isEmpty()) {
                // 封装集货sku数据
                Map<String, WhSeedingCollectionLine> seedingDataMap = new HashMap<>();
                for (WhSeedingCollectionLine data : dataList) {
                    String mapKey = data.getSkuId() + "_" + data.getUuid();
                    seedingDataMap.put(mapKey, data);
                }
                cacheManager.setObject(cacheKey, seedingDataMap, CacheConstants.CACHE_ONE_WEEK);
            }

            boxSkuMap = cacheManager.getObject(cacheKey);

        }

        if (null == boxSkuMap || boxSkuMap.isEmpty()) {
            throw new BusinessException("周转箱缓存数据为空");
        }
        return boxSkuMap;
    }


    /**
     * 货格对应出库单信息
     *
     * @param seedingWallLattice
     * @param facilityCode
     * @param batchNo
     * @param ouId
     * @param gridNo
     * @param logId
     */
    public void saveSeedingOdoBindGridToCache(WhSeedingWallLattice seedingWallLattice, String facilityCode, String batchNo, Long ouId, String gridNo, String logId){
        //Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号
        //Value：WhSeedingWallLattice

        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_BIND_GRID + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridNo;
        cacheManager.setObject(cacheKey, seedingWallLattice, CacheConstants.CACHE_ONE_WEEK);
    }

    /**
     * 货格对应出库单信息
     *
     * @param facilityCode
     * @param batchNo
     * @param ouId
     * @param gridNo
     * @param logId
     */
    public WhSeedingWallLattice getSeedingOdoBindGridFromCache(String facilityCode, String batchNo, Long ouId, String gridNo, String logId){
        //Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号
        //Value：WhSeedingWallLattice

        String cacheKey = CacheConstants.CACHE_SEEDING_ODO_BIND_GRID + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridNo;
        return  cacheManager.getObject(cacheKey);
    }




    /**
     * 将小批次下的出库单绑定货格，保存出库单的明细信息，扫描播种墙进入操作台之后，初始化方法中调用，完成出库单明细和货格的绑定
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param latticeLineMap
     * @param ouId
     * @param logId
     */
    public void saveSeedingWallLatticeLineToCache(String facilityCode, String batchNo, String gridCode, String odoCode, Map<String, WhSeedingWallLatticeLine> latticeLineMap, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE【WMS出库单号】
        // Value：Map<skuid_uuid,WhSeedingWallLatticeLine>

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode;
        cacheManager.setObject(cacheKey, latticeLineMap, CacheConstants.CACHE_ONE_WEEK);
    }


    /**
     * 小批次下的出库单明细绑定了货格，扫描货格之后先拿到出库单头信息，再通过该方法获得出库单明细信息
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param ouId
     * @param logId
     * @return
     */
    public Map<String, WhSeedingWallLatticeLine> getSeedingWallLatticeLineFromCache(String facilityCode, String batchNo, String gridCode, String odoCode, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE【WMS出库单号】
        // Value：Map<skuid_uuid,WhSeedingWallLatticeLine>

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode;
        Map<String, WhSeedingWallLatticeLine> latticeLineMap = cacheManager.getObject(cacheKey);
        return latticeLineMap;
    }


    /**
     * 如果播种墙设置了出库箱，一个出库单可能使用多个出库箱，记录每个出库箱分别装了哪些商品，点击出库箱已满的时候调用该方法
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param outboundBoxCode
     * @param seedingWallLatticeObbMap
     * @param ouId
     * @param logId
     */
    public void saveSeedingWallLatticeObbToCache(String facilityCode, String batchNo, String gridCode, String odoCode, String outboundBoxCode, Map<String, WhSeedingWallLatticeObb> seedingWallLatticeObbMap, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE-出库箱CODE
        // Value：Map<skuid_uuid,WhSeedingWallLatticeObb>

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode + "-" + outboundBoxCode;
        cacheManager.setObject(cacheKey, seedingWallLatticeObbMap, CacheConstants.CACHE_ONE_WEEK);
    }

    /**
     * 如果播种墙设置了出库箱，一个出库单可能使用多个出库箱，通过此方法获取指定出库箱的已装入的商品信息
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param outboundBoxCode
     * @param ouId
     * @param logId
     * @return
     */
    public Map<String, WhSeedingWallLatticeObb> getSeedingWallLatticeObbFromCache(String facilityCode, String batchNo, String gridCode, String odoCode, String outboundBoxCode, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE-出库箱CODE
        // Value：Map<skuid_uuid,WhSeedingWallLatticeObb>

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode + "-" + outboundBoxCode;
        return cacheManager.getObject(cacheKey);
    }
















    /**
     * 分别记录周转箱中每个商品已播种数量，扫描sku放入货格后，通过该方法记录周转箱中该sku的播种总数
     * 
     * @param facilityCode
     * @param batchNo
     * @param containerCode
     * @param skuId
     * @param uuid
     * @param ouId
     * @param count
     * @param logId
     * @return
     */
    public Long saveBoxSkuSownCountToCache(String facilityCode, String batchNo, String containerCode, Long skuId, String uuid, Long ouId, Long count, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-周转箱CODE-SKUID-UUID
        // Value：数量

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + containerCode + "-" + skuId + "-" + uuid;
        cacheManager.incrBy(cacheKey, count.intValue());

        Long sownSkuCount = cacheManager.getObject(cacheKey);
        if (null == sownSkuCount || sownSkuCount < 0) {
            throw new BusinessException("商品已播种数缓存异常");
        }
        return sownSkuCount;
    }

    /**
     * 分别记录周转箱中每个商品已播种数量，用于判断sku待播种数量
     *
     * @param facilityCode
     * @param batchNo
     * @param containerCode
     * @param skuId
     * @param uuid
     * @param ouId
     * @param logId
     * @return
     */
    public Long getBoxSkuSownCountFromCache(String facilityCode, String batchNo, String containerCode, Long skuId, String uuid, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-周转箱CODE-SKUID-UUID
        // Value：数量

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + containerCode + "-" + skuId + "-" + uuid;
        Long sownSkuCount = cacheManager.getObject(cacheKey);
        if (null == sownSkuCount || sownSkuCount < 0) {
            throw new BusinessException("商品已播种数缓存异常");
        }
        return sownSkuCount;
    }




    /**
     * 分别记录出库单中不同属性的sku播种数，扫描sku放入货格后，调用此方法记录对应出库单sku的播种数量
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param skuId
     * @param uuid
     * @param count
     * @param ouId
     * @param logId
     */
    public void saveOdoSkuSownCountToCache(String facilityCode, String batchNo, String gridCode, String odoCode, Long skuId, String uuid, Long count, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE-SKUID-UUID
        // Value：数量

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode + "-" + skuId + "-" + uuid;
        cacheManager.incrBy(cacheKey, count.intValue());
        Long sownSkuCount = cacheManager.getObject(cacheKey);
        if (null == sownSkuCount || sownSkuCount < 0) {
            throw new BusinessException("odo商品已播种数缓存异常");
        }
    }

    /**
     * 获取出库单sku已播种数量缓存
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param skuId
     * @param uuid
     * @param ouId
     * @param logId
     * @return
     */
    public Long getOdoSkuSownCountFromCache(String facilityCode, String batchNo, String gridCode, String odoCode, Long skuId, String uuid, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE-SKUID-UUID
        // Value：数量

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode + "-" + skuId + "-" + uuid;
        Long sownSkuCount = cacheManager.getObject(cacheKey);
        return sownSkuCount;
    }



    /**
     * 播种墙一个货格对应一个出库单，一个货格可能会需要使用多个出库箱 该缓存分别记录各个出库箱的某个商品数量，扫描sku放入货格后调用此方法，记录出库箱中sku的数量
     * 
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param outboundBoxCode
     * @param skuId
     * @param uuid
     * @param count
     * @param ouId
     * @param logId
     */
    public void saveOdoSkuOutbountBoxSownCountToCache(String facilityCode, String batchNo, String gridCode, String odoCode, String outboundBoxCode, Long skuId, String uuid, Long count, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE-出库箱CODE-SKUID-UUID
        // Value：数量

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode + "-" + outboundBoxCode + "-" + skuId + "-" + uuid;
        Long sownSkuCount = cacheManager.getObject(cacheKey);
        // 已播种商品数
        if (null == sownSkuCount) {
            sownSkuCount = count;
        } else {
            sownSkuCount = sownSkuCount + count;
        }
        cacheManager.setObject(cacheKey, sownSkuCount, CacheConstants.CACHE_ONE_WEEK);

        sownSkuCount = cacheManager.getObject(cacheKey);
        if (null == sownSkuCount || sownSkuCount < 0) {
            throw new BusinessException("odo商品已播种数缓存异常");
        }
    }

    /**
     * 播种墙一个货格对应一个出库单，一个货格可能会需要使用多个出库箱 该缓存分别记录各个出库箱的某个商品数量，通过该方法获取出库箱中sku的数量
     *
     * @param facilityCode
     * @param batchNo
     * @param gridCode
     * @param odoCode
     * @param outboundBoxCode
     * @param skuId
     * @param uuid
     * @param ouId
     * @param logId
     * @return
     */
    public Long getOdoSkuOutbountBoxSownCountFromCache(String facilityCode, String batchNo, String gridCode, String odoCode, String outboundBoxCode, Long skuId, String uuid, Long ouId, String logId) {
        // Key：SEEDING-仓库ID-播种墙CODE-批次号-货格号-ODOCODE-出库箱CODE-SKUID-UUID
        // Value：数量

        String cacheKey = CacheConstants.CACHE_SEEDING + "-" + ouId + "-" + facilityCode + "-" + batchNo + "-" + gridCode + "-" + odoCode + "-" + outboundBoxCode + "-" + skuId + "-" + uuid;
        Long sownSkuCount = cacheManager.getObject(cacheKey);
        return sownSkuCount;
    }


    // TODO 3、播种墙正在进行播种显示Redis数据

    // TODO 4 当出库单分成多个出库箱时，按照货格记录总数量

    // TODO 5、播种墙货格绑定出库单单号Redis

    // TODO 6、播种墙货格对应正在使用的出库箱Redis数据

    // TODO 7、播种墙对应批次正常结束所有播种后，调整对应的库存记录，直接删除所有对应的Redis数据
}
