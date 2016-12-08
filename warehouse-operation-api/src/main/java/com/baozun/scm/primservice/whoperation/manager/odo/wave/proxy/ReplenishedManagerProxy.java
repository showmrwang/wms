package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentStrategyCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;

public interface ReplenishedManagerProxy extends BaseManager{
	
	/**
     * [业务方法] 根据WavePhaseCode波次编码查找波次ID集合
     * @param phaseCode
     * @param ouId
     * @return
     */
	List<Long> findWaveIdsByWavePhaseCode(String phaseCode, Long ouId);
	
	/**
     * [业务方法] 补货-查询波次内需要补货的波次明细集合
     * @param waveId
     * @param ouId
     * @return
     */
	List<WhWaveLine> findWaveLineByNotEnoughAllocationQty(Long waveId, Long ouId);
	
	/**
     * [业务方法] 在一个波次中查找包含skuIds的OdoId
     * @param skuIds
     * @param ouId
     * @return
     */
	List<Long> findOdoContainsSkuId(Long waveId, List<Long> skuIds, Long ouId);

	/**
     * 通过groupValue+dicValue查询对应系统参数信息
     * @param groupValue
     * @param dicValue
     * @return
     */
    SysDictionary getGroupbyGroupValueAndDicValue(String groupValue, String dicValue);
    
    /**
     * [业务方法] 通过sku匹配对应规则集合
     * @param skuIds
     * @param ouId
     * @return
     */
    Map<Long, List<ReplenishmentRuleCommand>> getSkuReplenishmentRule(List<Long> skuIds, Long ouId);

    String generateBhCode();

    /**
     * 查找补货规则对应的分配策略
     * 
     * @param id
     * @param ouId
     * @return
     */
    List<ReplenishmentStrategyCommand> findReplenishmentStrategyListByRuleId(Long id, Long ouId);
}
