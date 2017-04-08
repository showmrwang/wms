package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaReplenishmentPutawayManager extends BaseManager {

    
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
    public ReplenishmentPutawayCommand putawayScanLocation(ReplenishmentPutawayCommand command);
    
    /***
     * 扫描周转箱
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand putawayScanTurnoverBox(ReplenishmentPutawayCommand command,Boolean isTabbInvTotal);
    
    /***
     * 更新工作及作业状态
     * @param operationId
     * @param workCode
     * @param ouId
     * @param userId
     */
    public void updateStatus(Long operationId,String workCode,Long ouId,Long userId);
    
    /**
     * [通用方法] 统计分析作业执行明细并缓存
     * 
     * @author qiming.liu
     * @param ReplenishmentPutawayCommand
     * @return
     */
    OperationExecStatisticsCommand getOperationExecForGroup(ReplenishmentPutawayCommand replenishmentPutawayCommand);
    
    /***
     * 补货上架取消
     * @param operationId
     */
    public void cancelPattern(Long operationId,Integer cancelPattern);
}
