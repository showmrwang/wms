package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;

public interface ReplenishmentTaskManager extends BaseManager {
	
	int insertReplenishmentTask(ReplenishmentTask task);

    /**
     * 根据波次查找可以补货的任务【任务状态为新建】
     * 
     * @param waveId
     * @param ouId
     * @param status
     * @return
     */
    ReplenishmentTask findTaskByWaveWithStatus(Long waveId, Long ouId, Integer status);
}
