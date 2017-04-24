package com.baozun.scm.primservice.whoperation.manager.seeding;


import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.seeding.SeedingLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionSeedingWall;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

public interface SeedingManagerProxy extends BaseManager {




    /**
     * 获取功能缓存，功能信息按照播种墙保存缓存，各个播种墙保存自己的功能缓存
     *
     * @param functionId
     * @param facilityId
     * @param ouId
     * @param logId
     */
    public WhFunctionSeedingWall getFunctionFromCache(Long functionId, Long facilityId, String batchNo, Long ouId, String logId);


    /**
     * 保存用于播种提示亮灯的货格信息
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeMap
     * @param logId
     */
    public void saveLatticeMapToCache(Long facilityId, String batchNo, Long ouId, Map<Long, SeedingLattice> latticeMap, String logId);

    /**
     * 获取播种货格信息用于提示亮灯
     *
     * @param facilityId
     * @param ouId
     * @param logId
     * @return
     */
    public Map<Long, SeedingLattice> getLatticeMapFromCache(Long facilityId, String batchNo, Long ouId, String logId);

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
    public void saveSeedingOdoBindLatticeToCache(WhSeedingWallLattice seedingWallLattice, Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId);

    /**
     * 获取货格对应出库单信息
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param logId
     */
    public WhSeedingWallLattice getSeedingOdoBindLatticeFromCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId);

    /**
     * 保存货格当前绑定的出库箱到缓存
     * 
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param outboundBoxCode
     * @param logId
     */
    public void saveLatticeCurrentSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String outboundBoxCode, String logId);

    /**
     * 获取货格当前绑定的出库箱
     * 
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param logId
     */
    public String getLatticeCurrentSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId);


    /**
     * 保存货格当前绑定的出库箱到缓存，在出库箱已满时操作
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param outboundBoxCode
     * @param logId
     */
    public void saveLatticeSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String outboundBoxCode, String logId);

    /**
     * 获取货格已绑定的所有出库箱，已包含当前播种的出库箱
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param logId
     */
    public List<String> getLatticeSeedingOutboundBoxCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId);

    /**
     * 获取播种墙所有货格绑定的出库箱，用于验证是否完成货格绑定出库箱的操作
     * 
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param logId
     * @return
     */
    public List<String> getFacilityCurrentSeedingOutboundBoxCode(Long facilityId, String batchNo, Long ouId, String logId);


    /**
     * 播种墙已缓存的所有出库单信息
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param logId
     * @return
     */
    public List<WhSeedingWallLattice> getFacilityBatchOdoCache(Long facilityId, String batchNo, Long ouId, String logId);

    /**
     * 有出库箱时按照出库箱保存货格播种明细缓存，不再按照货格保存
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param outboundBoxCode
     * @param collectionLineMap
     * @param logId
     */
    public void saveOutboundBoxCollectionLineCache(Long facilityId, String batchNo, Long ouId, String outboundBoxCode, Map<Long, WhSeedingCollectionLineCommand> collectionLineMap, String logId);

    /**
     * 换箱后置逻辑，保存上次播种的明细，在确认不换箱之后清除该缓存，在换箱之后，将该缓存存入新出库箱，原出库箱里取消上次播种的数据
     *
     * @author mingwei.xie
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param outboundBoxCode
     * @param seedingCollectionLine
     * @param logId
     */
    public void saveLastTimeOutboundBoxSeedingCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String outboundBoxCode, WhSeedingCollectionLineCommand seedingCollectionLine, String logId);

    /**
     * 获取上次播种的缓存明细，换箱逻辑的操作使用
     *
     * @author mingwei.xie
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param logId
     */
    public Map<Long, Map<String, WhSeedingCollectionLineCommand>>  getLastTimeOutboundBoxSeedingCache(Long facilityId, String batchNo, Long ouId, String logId);

    /**
     * 删除上次播种的缓存，未进行换箱操作，货格换箱结束
     *
     * @author mingwei.xie
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param logId
     */
    public void removeLastTimeOutboundBoxSeedingCache(Long facilityId, String batchNo, Long ouId, String logId);



    /**
     * 有出库箱时按照出库箱保存货格播种明细缓存，不再按照货格保存
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param outboundBoxCode
     * @param logId
     * @return
     */
    public Map<Long, WhSeedingCollectionLineCommand> getOutboundBoxCollectionLineCache(Long facilityId, String batchNo, Long ouId, String outboundBoxCode, String logId);


    /**
     * 没有出库箱时直接保存货格缓存，有出库箱的该缓存没有数据
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param collectionLineMap
     * @param logId
     */
    public void saveLatticeCollectionLineCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, Map<Long, WhSeedingCollectionLineCommand> collectionLineMap, String logId);

    /**
     * 没有出库箱时保存的货格缓存，有出库箱的该缓存没有数据
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param latticeNo
     * @param logId
     * @return
     */
    public Map<Long, WhSeedingCollectionLineCommand> getLatticeCollectionLineCache(Long facilityId, String batchNo, Long ouId, Long latticeNo, String logId);

    /**
     * 获取周转箱所有的明细缓存，如果缓存未初始化，从数据库获取并保存到缓存
     *
     * @param seedingCollectionId
     * @param ouId
     * @return
     */
    public List<WhSeedingCollectionLineCommand> getSeedingCollectionLineByCollectionFromCache(Long facilityId, String batchNo, Long ouId, Long seedingCollectionId, String logId);

    /**
     * 播种中对明细的修改，保存到缓存
     *
     * @param seedingCollectionId
     * @param ouId
     * @return
     */
    public void saveSeedingCollectionLineByCollectionToCache(Long facilityId, String batchNo, Long ouId, Long seedingCollectionId, List<WhSeedingCollectionLineCommand> collectionLineList, String logId);


    /**
     * 按照出库单获取所有明细缓存中的明细
     *
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param odoId
     * @param logId
     * @return
     */
    public List<WhSeedingCollectionLineCommand> getSeedingCollectionLineByOdoFromCache(Long facilityId, String batchNo, Long ouId, Long odoId, String logId);

    public void facilityBatchFinishedSeeding(Long functionId, Long facilityId,  Long userId, Long ouId, String logId);

    public void releaseFacilityBatchRedis(Long facilityId, String batchNo, Long ouId, String logId);

    public WhSkuInventory createWhSkuInventory(WhSeedingCollectionLineCommand collectionSeedingLine, Long ouId, String logId);
}
