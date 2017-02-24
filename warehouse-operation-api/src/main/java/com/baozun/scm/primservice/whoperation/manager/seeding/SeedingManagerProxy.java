package com.baozun.scm.primservice.whoperation.manager.seeding;


import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface SeedingManagerProxy extends BaseManager {

    /**
     * 初始化播种墙缓存数据
     * 
     * @param facilityId
     * @param batchNo
     * @param ouId
     * @param userId
     * @param logId
     */
    public void initFacilityRedis(Long facilityId, String batchNo, Long ouId, Long userId, String logId);

    /**
     * 获取集货表的周转箱列表缓存
     *
     * @param facilityCode
     * @param batchNo
     * @param ouId
     * @param logId
     * @return
     */
    public List<WhSeedingCollectionCommand> getSeedingBatchTurnoverBoxListFromCache(String facilityCode, String batchNo, Long ouId, String logId);
}
