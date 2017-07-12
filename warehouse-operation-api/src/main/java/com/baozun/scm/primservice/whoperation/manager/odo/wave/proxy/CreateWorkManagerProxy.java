package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * @author qiming.liu
 *
 * 2016年11月02日 上午11:07:20
 */
public interface CreateWorkManagerProxy extends BaseManager {
    
    /**
     * [业务方法] 波次内创建工作和作业
     * 
     * @param waveId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    public void createWorkInWave(Long waveId, Long ouId, Long userId, String logId);
   
    /**
     * [业务方法] 波次外创建工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public void createWorkOutWave(Long ouId, Long userId);
    
    /**
     * 拣货后触发补货工作作业明细生成
     * 
     * @param locationIds
     * @param ouId
     * @param userId
     * @return
     */
    public void createReplenishmentAfterPicking(List<Long> locationIds, Long ouId, Long userId);
    
}
