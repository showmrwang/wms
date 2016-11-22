package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;

public interface ReplenishmentTaskManager extends BaseManager {
	
	int insertReplenishmentTask(ReplenishmentTask task);
}
