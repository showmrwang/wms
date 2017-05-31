package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
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
     * 扫描库位--整托整箱
     * @param command
     * @return
     */
    public ReplenishmentPutawayCommand putawayWholeCaseScanLocation(ReplenishmentPutawayCommand command);
    
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
    public void cancelPattern(Long operationId,Integer cancelPattern,Long locationId,Long turnoverBoxId,Long tipSkuId);
    
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
     public Boolean judgeIsOnlyLocation(Long operationId, Long locationId, String turnoverBoxCode, Long ouId, Integer replenishWay);
     
     
     public ContainerCommand findContainerCmdByCode(String containerCode,Long ouId);
     
     /**
      * 获取库位信息
      * @param id
      * @param ouId
      * @return
      */
     public Location findLocationById(Long id,Long ouId);
     
     /**
      * 判断引入新容器状态
      * @param newTurnoverBox
      * @param locationId
      * @param ouId
      * @return
      */
     public void judgeNewTurnoverBox(String newTurnoverBox,Long locationId,Long ouId,Long userId);
     
     /***
      * 修改周转箱状态
      * @param turnoverBoxCode
      * @param ouId
      */
     public void updateTurnoverBox(String turnoverBoxCode,Long ouId);
     
     
}
