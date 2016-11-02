package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.warehouse.AllocateStrategyDao;
import com.baozun.scm.primservice.whoperation.model.warehouse.AllocateStrategy;

@Service("allocateStrategyManager")
@Transactional
public class AllocateStrategyManagerImpl implements AllocateStrategyManager {
	
	@Autowired
	private AllocateStrategyDao allocateStrategyDao;
	
	@Override
	public List<AllocateStrategy> findAllocateStrategyListByRuleId(Long ruleId, Long ouId) {
		List<AllocateStrategy> lists = allocateStrategyDao.findAllocateStrategyListByRuleId(ruleId, ouId);
		return lists;
	}

}
