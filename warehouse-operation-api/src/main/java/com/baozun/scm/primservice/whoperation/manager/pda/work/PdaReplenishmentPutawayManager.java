package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionReplenishment;

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
    
    /**
     * 获取补货功能参数
     * @param ouId
     * @param functionId
     * @return
     */
    public WhFunctionReplenishment findWhFunctionReplenishmentByfunctionId(Long ouId,Long functionId);
    
    /**
     * 扫描sku 
     * @param operationId
     * @param locationId
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand scanSku(ReplenishmentPutawayCommand command,WhSkuCommand skuCmd,Boolean isTabbInvTotal);
    
    public ReplenishmentPutawayCommand judgeSkuAttrIdsIsUnique(ReplenishmentPutawayCommand command);
 
//    /***
//     * 扫sku抛出异常时,删除sku缓存
//     * @param locationId
//     * @param turnoverBoxId
//     * @param skuId
//     */
//    public void removeCacheSku(Long locationId,Long turnoverBoxId,Long skuId);
    
    /***
     * 删除缓存(如果存在s)
     * @param operationId
     * @param ouId
     */
     public void removeCache(Long operationId);
     
     /**
      * 判断是否是单库位
      * @param operationId
      * @return
      */
     public Boolean judgeIsOnlyLocation(Long operationId);
}
