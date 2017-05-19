package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaPickingWorkCacheManager extends BaseManager{

    /**
     * 缓存拣货统计分析结果
     * 
     * @author qiming.liu
     * @param operatorId
     * @param operatioLineStatisticsCommand
     * @return
     */
    public void operatioLineStatisticsRedis(Long operatorId, OperatioLineStatisticsCommand operatioLineStatisticsCommand);
    
    /**
     * 缓存补货统计分析结果
     * 
     * @author qiming.liu
     * @param operatorId
     * @param operationExecStatisticsCommand
     * @return
     */
    public void operationExecStatisticsRedis(Long operationId, OperationExecStatisticsCommand operationExecStatisticsCommand);
    
    /***
     * 提示小车
     *  @tangming
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipOutContainer(Long operatorId,Long ouId);
    
    
    /***
     * 提示出库箱
     *  @tangming
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipoutboundBox(Long operatorId,Long ouId);
    
    /**
     * 提示周转箱
     *  @tangming
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipTurnoverBox(Long operatorId,Long ouId);
    
    /**
     * 根据作业ID和OUID获取统计分析结果
     * 
     * @author qiming.liu
     * @param operatorId
     * @param ouId
     * @return
     */
    public OperatioLineStatisticsCommand getOperatioLineStatistics(Long operatorId,Long ouId);
    
   /***
    * 提示库位
    *  @tangming
    * @param operatorId
    * @param locationIds
    * @return
    */
    public CheckScanResultCommand tipLocation(Long operation,List<Long> locationIds);
    
    
    /***
     * 缓存库位
     * @param operation
     * @param locationId
     */
    public void cacheLocation(Long operation,Long locationId);
    
    
    /***
     * pda拣货提示托盘
     * @author tangming
     * @param outerContainerIds
     * @param locationId
     * @return
     */
      public CheckScanResultCommand pdaPickingTipOuterContainer(Set<Long> outerContainerIds,Long locationId,Long operationId);
      
      /***
       * pda拣货提示货箱
       * @author tangming
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipInsideContainer(Set<Long> insideContainerIds,Long locationId,Long outerContainerId,Long operationId);
      
      /***
       * pda拣货提示sku
       *  @tangming
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipSku(Long outerContainerId,String operationWay,Set<Long> skuIds,Long operatorId,Long locationId,Long ouId,Long insideContainerId,Map<Long, Map<String, Set<String>>> locskuAttrIdsSnDefect,Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect);
      
      /***
       *  @tangming
       * @param locationIds
       * @param locSkuQty
       * @param locationId
       * @param locSkuIds
       * @param outerContainerIds
       * @param outerContainerCmd
       * @param operatorId
       * @param insideContainerSkuIdsQty
       * @param insideContainerSkuIds
       * @param insideContainerIds
       * @param insideContainerCmd
       * @param skuCmd
       * @return
       */
      public CheckScanResultCommand pdaPickingyCacheSkuAndCheckContainer(Map<String, Set<Integer>>  skuAttrIdsLattice,Map<String, Set<Integer>>  insideSkuAttrIdsLattice,Integer pickingWay,Map<String,Long> latticeSkuQty,Map<String,Long> latticeInsideSkuQty,String operationWay,Long ouId,Map<Long, Set<Long>> locSkuIds, Map<Long, Map<String, Set<String>>>     insideSkuAttrIdsSnDefect, Map<Long, Map<String, Set<String>>>    skuAttrIdsSnDefect,Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds,Map<Long, Map<Long, Map<String, Long>>> locSkuAttrIdsQty,String skuAttrIds,Integer scanPattern,List<Long> locationIds, Map<Long, Long> locSkuQty,Long locationId,Set<Long> icSkuIds,Set<Long> outerContainerIds,ContainerCommand outerContainerCmd,Long operatorId,
                                                                         Map<Long, Set<Long>> insideContainerSkuIds,Set<Long> insideContainerIds,Set<Long> locInsideContainerIds,ContainerCommand insideContainerCmd,WhSkuCommand skuCmd);
    
      public CheckScanResultCommand palletPickingCacheAndCheck(Long locationId, Set<Long> insideContainerIds, Long outerContainerId, Long insideContainerId,Long operationId);
      
      /***
       * 有小车，而且有出库箱的时候，提示出库箱
       *  @tangming
       * @param operatorLineList
       * @param operationId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipOutBounxBoxCode(List<WhOperationLineCommand> operatorLineList,Long operationId, Map<Integer, String> carStockToOutgoingBox);
      
      /***
       * 缓存作业明细
       * @tangming
       * @param operationId
       * @param ouId
       */
      public List<WhOperationLineCommand> cacheOperationLine(Long operationId,Long ouId);
      
     
    /***
     * 清楚缓存
     * @param operationId
     * @param isAfterScanLocation
     * @param skuId
     * @param insideContainerCmd
     * @param outerContainerCmd
     */
     public void pdaPickingRemoveAllCache(Long operationId,Boolean isAfterScanLocation,Long locationId);
     
     
     /***
      * 修改工作/作业状态
      * @param operationId
      * @param workId
      */
     public void pdaPickingUpdateStatus(Long operationId,String workCode,Long ouId,Long userId);
     
     /***
      * 将作业投标识为拣货完成
      * @param operationId
      * @param ouId
      */
     public void pdaReplenishmentUpdateOperation(Long operationId,Long ouId,Long userId);
      
     /***
      * 拣货取消流程
      * @param outerContainerId
      * @param insideContainerId
      * @param cancelPattern
      * @param pickingType
      * @param locationId
      * @param ouId
      */
     public void cancelPattern(Long carId,Long outerContainerId,Long insideContainerId, int cancelPattern,int pickingWay,Long locationId,Long ouId,Long operationId,Long tipSkuId);
     
     /***
      * 缓存唯一sku，sn/残次信息
      * @param locationId
      * @param skuAttrId
      * @param insideContainerId
      */
     public void cacheSkuAttrId(Long locationId,String skuAttrId,Long insideContainerId,Long operationId);
     
     /**
      * 缓存唯一sku
      * @param locationId
      * @param skuAttrId
      * @param insideContainerId
      */
     public void cacheSkuAttrIdNoSn(Long locationId,String skuAttrId,Long insideContainerId,Long operationId);
     
     /***
      * 补货(拣货)取消流程
      * @param outerContainerId
      * @param insideContainerId
      * @param cancelPattern
      * @param pickingType
      * @param locationId
      * @param ouId
      */
     public void replenishmentCancelPattern(Long outerContainerId,Long insideContainerId, int cancelPattern,int pickingWay,Long locationId,Long ouId,Long operationId,Long tipSkuId);
}
