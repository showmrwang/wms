package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.AllocateStrategy;

public interface AllocateStrategyManager extends BaseManager {
	
	/**
	 * [通用方法] 根据规则Id获取规则List
	 */
	List<AllocateStrategy>  findAllocateStrategyListByRuleId(Long ruleId, Long ouId);
	
}
