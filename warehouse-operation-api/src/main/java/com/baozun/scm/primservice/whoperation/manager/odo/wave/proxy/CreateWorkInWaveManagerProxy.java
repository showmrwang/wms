package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;


/**
 * @author qiming.liu
 *
 * 2016年11月02日 上午11:07:20
 */
public interface CreateWorkInWaveManagerProxy extends BaseManager {

    
    /**
     * [业务方法] 波次中创建捡货工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public void createPickingWorkInWave(Long waveId, Long ouId, Long userId);
    
    /**
     * [业务方法] 波次中创建补货工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public void createReplenishmentWorkInWave(Long waveId, Long ouId, Long userId);
   
    /**
     * [业务方法] 波次外创建工作和作业
     * @param WhOdoOutBoundBox
     * @param userId
     * @return
     */
    public void createReplenishmentWorkOutWave(Long ouId, Long userId);
    
    /**
     * [业务方法] 创建拣货工作-返回小批次列表给上层服务
     * @param waveId
     * @param ouId
     * @return
     */
    public WhWave getWhWaveHead(Long waveId, Long ouId);
    
    /**
     * [业务方法] 创建拣货工作-返回小批次列表给上层服务
     * @param waveId
     * @param ouId
     * @return
     */
    public List<WhOdoOutBoundBox> getBoxBatchsForPicking(Long waveId, Long ouId);
    
    /**
     * [业务方法] 创建拣货工作-返回小批次分组数据列表给上层服务
     * @param WhOdoOutBoundBox
     * @return
     */
    public List<WhOdoOutBoundBox> getOdoOutBoundBoxForGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 创建拣货工作-返回出库箱/容器信息列表给上层服务
     * @param WhOdoOutBoundBox
     * @return
     */
    public List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 创建拣货工作-创建工作头信息
     * @param WhOdoOutBoundBox
     * @param waveId
     * @param ouId
     * @return
     */
    public String savePickingWork(WhOdoOutBoundBox whOdoOutBoundBox, Long userId);
    
    /**
     * [业务方法] 创建拣货工作-查询占用的库存信息
     * @param WhOdoOutBoundBox
     * @return
     */
    public List<WhSkuInventory> getSkuInventory(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand);
    
    /**
     * [业务方法] 创建拣货工作-创建工作明细信息
     * @param WhOdoOutBoundBox
     * @param waveId
     * @param ouId
     * @return
     */
    public int savePickingWorkLine(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand, List<WhSkuInventory> whSkuInventoryList, Long userId, String workCode);
    
    /**
     * [业务方法] 创建拣货工作-更新工作头信息
     * @param WhOdoOutBoundBox
     * @return
     */
    public void updatePickingWork(String workCode, WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 创建拣货工作-创建作业头
     * @param workCode
     * @param whOdoOutBoundBox
     * @return
     */
    public String savePickingOperation(String workCode, WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 创建拣货工作-创建作业明细
     * @param List<WhOdoOutBoundBox>
     * @return
     */
    public int savePickingOperationLine(String workCode, String operationCode, Long ouId);
    
    /**
     * [业务方法] 创建拣货工作-设置出库箱行标识
     * @param WhOdoOutBoundBoxCommand
     * @return
     */
    public void updateWhOdoOutBoundBoxCommand (WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand);
    
    /**
     * [业务方法] 创建补货工作-查询波次内补货工作释放及拆分条件分组
     * @param waveId, 
     * @param ouId
     * @return
     */
    public List<ReplenishmentRuleCommand> getInReplenishmentConditionGroup (Long waveId, Long ouId);
    
    /**
     * [业务方法] 创建补货工作-查询波次外补货工作释放及拆分条件分组
     * @param waveId, 
     * @param ouId
     * @return
     */
    public List<ReplenishmentRuleCommand> getOutReplenishmentConditionGroup (Long ouId);
    
    /**
     * [业务方法] 创建补货工作-根据波次内补货工作释放及拆分条件获取所有补货数据
     * @param ReplenishmentRuleCommand
     * @return
     */
    public List<WhSkuInventoryAllocatedCommand> getInAllReplenishmentLst (ReplenishmentRuleCommand replenishmentRuleCommand);
    
    /**
     * [业务方法] 创建补货工作-根据波次内补货工作释放及拆分条件获取所有补货数据
     * @param ReplenishmentRuleCommand
     * @return
     */
    public List<WhSkuInventoryAllocatedCommand> getOutAllReplenishmentLst (ReplenishmentRuleCommand replenishmentRuleCommand);
    
    /**
     * [业务方法] 创建补货工作-根据补货工作释放及拆分条件获取所有补货数据
     * @param ReplenishmentRuleCommand
     * @return
     */
    public Map<String, List<WhSkuInventoryAllocatedCommand>> getSkuInventoryAllocatedCommandForGroup(ReplenishmentRuleCommand replenishmentRuleCommand);
    
    /**
     * [业务方法] 创建补货工作-创建波次内补货工作头信息
     * @param waveId
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    public String saveReplenishmentWork(Long waveId, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long userId);
    
    /**
     * [业务方法] 创建补货工作-创建波次外补货工作头信息
     * @param waveId
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    public String saveOutReplenishmentWork(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long userId);
    
    /**
     * [业务方法] 创建补货工作-创建工作明细
     * @param WhSkuInventoryAllocatedCommand
     * @return
     */
    public void saveReplenishmentWorkLine(String key, String replenishmentWorkCode, Long userId, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand);
    
    /**
     * [业务方法] 创建补货工作-更新波次内工作头信息
     * @param WhOdoOutBoundBox
     * @return
     */
    public void updateOutReplenishmentWork(String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand);
    
    /**
     * [业务方法] 创建补货工作-更新波次外工作头信息
     * @param WhOdoOutBoundBox
     * @return
     */
    public void updateReplenishmentWork(Long waveId, String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand);
    
    /**
     * [业务方法] 创建补货工作-创建作业头
     * @param workCode
     * @param whOdoOutBoundBox
     * @return
     */
    public String saveReplenishmentOperation(String key, String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand);
    
    /**
     * [业务方法] 创建补货工作-创建作业明细
     * @param List<WhOdoOutBoundBox>
     * @return
     */
    public int saveReplenishmentOperationLine(String replenishmentWorkCode, String replenishmentOperationCode, Long ouId, Double qty);
    
    /**
     * [业务方法] 创建补货工作-计算库位容量
     * @param List<WhOdoOutBoundBox>
     * @return
     */
    public Double locationReplenishmentCalculation(WhSkuInventoryAllocatedCommand siaCommand, Long ouId);
}
