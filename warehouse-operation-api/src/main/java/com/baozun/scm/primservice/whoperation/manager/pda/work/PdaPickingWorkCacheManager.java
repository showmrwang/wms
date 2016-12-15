package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;

public interface PdaPickingWorkCacheManager extends BaseManager{

    /**
     * 缓存统计分析结果
     * 
     * @author qiming.liu
     * @param operatorId
     * @param operatioLineStatisticsCommand
     * @return
     */
    public void operatioLineStatisticsRedis(Long operatorId, OperatioLineStatisticsCommand operatioLineStatisticsCommand);
    
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
    * 缓存库位信息
    *  @tangming
    * @param operatorId
    * @param pickingType
    * @param locationIds
    * @return
    */
    public CheckScanResultCommand locationTipcache(Long operatorId,Integer pickingType,List<Long> locationIds);
    
    /***
     * 缓存单个库位的作业明细
     *  @tangming
     * @param operatorId
     * @param locationId
     * @param ouId
     * @return
     */
    public List<WhSkuInventoryCommand> cacheLocationInventory(Long operatorId,Long locationId,Long ouId);
    
    /***
     * pda拣货提示托盘
     * @author tangming
     * @param outerContainerIds
     * @param locationId
     * @return
     */
      public CheckScanResultCommand pdaPickingTipOuterContainer(Set<Long> outerContainerIds,Long locationId);
      
      /***
       * pda拣货提示货箱
       * @author tangming
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipInsideContainer(Set<Long> insideContainerIds,Long locationId);
      
      /***
       * pda拣货提示sku
       *  @tangming
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipSku(Set<Long> skuIds,Long operatorId,Long locationId,Long ouId,Long insideContainerId,Map<Long, Map<String, Set<String>>> locskuAttrIdsSnDefect,Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect);
      
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
      public CheckScanResultCommand pdaPickingyCacheSkuAndCheckContainer(Integer scanPattern,List<Long> locationIds, Map<Long, Long> locSkuQty,Long locationId,Set<Long> locSkuIds,Set<Long> outerContainerIds,ContainerCommand outerContainerCmd,Long operatorId,Map<Long,Long> insideContainerSkuIdsQty,Map<Long, Set<Long>> insideContainerSkuIds,Set<Long> insideContainerIds,Set<Long> locInsideContainerIds,ContainerCommand insideContainerCmd,WhSkuCommand skuCmd);
    
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
      
//      /***
//       * 缓存已经拣货作业id
//       *  @tangming
//       * @param operationId
//       * @param skuAttrIds
//       * @param outerContainerId
//       * @param insideContainerId
//       * @param locationId
//       * @param ouId
//       */
//     public Long cachePickingOperLineId(Long operationId,String skuAttrIds,Long outerContainerId,Long insideContainerId,Long locationId,Long ouId,Boolean isShortPicking,Double scanQty);
//     
//     
//     /***
//      * 缓存周转箱作业明细
//      * @param whoperLinCmd
//      * @param trunOverBoxId(周转箱id)
//      * @param operationId
//      */
//     public void cacheTurnoverBoxPickingWhOperLineCmd(Long trunOverBoxId,Long operationId,Long operationLineId);
//     
//     /***
//      * 缓存出库箱箱作业明细
//      * @param whoperLinCmd
//      * @param trunOverBoxId(周转箱id)
//      * @param operationId
//      */
//     public void cacheOutBoundxBoxPickingWhOperLineCmd(String outBoundxBoxCode,Long operationId,Long operationLineId);
     
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
      
    
}
