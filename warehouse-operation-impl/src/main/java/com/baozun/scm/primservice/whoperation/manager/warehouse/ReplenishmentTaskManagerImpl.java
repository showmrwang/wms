package com.baozun.scm.primservice.whoperation.manager.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;

@Transactional
@Service("replenishmentTaskManager")
public class ReplenishmentTaskManagerImpl extends BaseManagerImpl implements ReplenishmentTaskManager {
	
	@Autowired
	private ReplenishmentTaskDao replenishmentTaskDao;
	
	@Override
	public int insertReplenishmentTask(ReplenishmentTask task) {
		int insert = (int) replenishmentTaskDao.insert(task);
		return insert;
	}

}
