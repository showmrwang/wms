package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.CreateWorkResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;

/**
 * @author qiming.liu
 *
 * 2017年03月21日 上午19:02:20
 */
public interface CreateWorkManager extends BaseManager {
    
    /**
     * [业务方法] 波次内创建工作和作业
     * 
     * @param replenishmentRuleCommand
     * @return
     */
    public Map<String, List<WhSkuInventoryAllocatedCommand>> getSkuInventoryAllocatedCommandForGroup(ReplenishmentRuleCommand replenishmentRuleCommand);
    
    /**
     * [业务方法] 波次中创建补货工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public CreateWorkResultCommand createReplenishmentWorkInWave(WhWave whWave, List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst, ReplenishmentRuleCommand replenishmentRuleCommand, Long userId);
    
    /**
     * [业务方法] 波次中创建捡货工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public CreateWorkResultCommand createPickingWorkInWave(WhWave whWave, WhOdoOutBoundBox whOdoOutBoundBoxGroup, WhOdoOutBoundBox whOdoOutBoundBox, CreateWorkResultCommand createPickingWorkResultCommand, Long userId);
    
   
    /**
     * [业务方法] 波次外创建补货工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public void createReplenishmentWorkOutWave(List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst, ReplenishmentRuleCommand replenishmentRuleCommand, Long userId);
   
    /**
     * 拣货后触发补货工作作业明细生成
     * @param locationIds
     * @param ouId
     * @param userId
     * @return
     */
    public void createReplenishmentAfterPicking(Long toLocationId, Long ouId, Long userId);
}
