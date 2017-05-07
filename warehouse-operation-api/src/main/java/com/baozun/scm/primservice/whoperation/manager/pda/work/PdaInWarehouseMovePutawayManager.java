package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionInventoryMove;

public interface PdaInWarehouseMovePutawayManager extends BaseManager {

    
    /***
     * 提示库位
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand putawayTipLocation(ReplenishmentPutawayCommand command);
    
    /***
     * 扫描库位
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand putawayScanLocation(ReplenishmentPutawayCommand command,Boolean isTabbInvTotal);
    
    /**
     * [通用方法] 统计分析作业执行明细并缓存
     * 
     * @author qiming.liu
     * @param ReplenishmentPutawayCommand
     * @return
     */
    OperationExecStatisticsCommand getOperationExecForGroup(ReplenishmentPutawayCommand replenishmentPutawayCommand);
    
}
