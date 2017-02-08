package com.baozun.scm.primservice.whoperation.manager.seeding;


import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface SeedingManagerProxy extends BaseManager {

    /**
     * 初始化播种墙缓存数据
     * 
     * @param facilityId
     * @param ouId
     * @param userId
     * @param logId
     */
    public void initFacilityRedis(Long facilityId, Long ouId, Long userId, String logId);

}
