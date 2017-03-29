package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;

@Transactional
@Service("replenishmentTaskManager")
public class ReplenishmentTaskManagerImpl extends BaseManagerImpl implements ReplenishmentTaskManager {
	
	@Autowired
	private ReplenishmentTaskDao replenishmentTaskDao;
	
	@Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
	public int insertReplenishmentTask(ReplenishmentTask task) {
		int insert = (int) replenishmentTaskDao.insert(task);
		return insert;
	}

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ReplenishmentTask findTaskByWaveWithStatus(Long waveId, Long ouId, Integer status) {
        ReplenishmentTask task = new ReplenishmentTask();
        task.setStatus(status);
        task.setOuId(ouId);
        task.setWaveId(waveId);
        List<ReplenishmentTask> result = this.replenishmentTaskDao.findListByParam(task);
        if (result == null || result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<ReplenishmentTask> findTaskByWave(Long waveId, Long ouId) {
        ReplenishmentTask task = new ReplenishmentTask();
        task.setOuId(ouId);
        task.setWaveId(waveId);
        List<ReplenishmentTask> result = this.replenishmentTaskDao.findListByParam(task);
        return result;
    }


}
