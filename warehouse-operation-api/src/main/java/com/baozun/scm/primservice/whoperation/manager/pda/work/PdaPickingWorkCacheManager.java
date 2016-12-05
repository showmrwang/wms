package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

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
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipOutContainer(Long operatorId,Long ouId);
    
    
    /***
     * 提示出库箱
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipoutboundBox(Long operatorId,Long ouId);
    
    /**
     * 提示周转箱
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipTurnoverBox(Long operatorId,Long ouId);
    
    /**
     * pda拣货整托整箱
     * @param operatorId
     * @return
     */
    public OperatioLineStatisticsCommand pdaPickingWorkTipWholeCase(Long operatorId,Long ouId);
    
   /***
    * 缓存库位信息
    * @param operatorId
    * @param pickingType
    * @param locationIds
    * @return
    */
    public CheckScanResultCommand locationTipcache(Long operatorId,Integer pickingType,List<Long> locationIds);
    
    /***
     * 缓存作业明细
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
      public CheckScanResultCommand pdaPickingTipOuterContainer(Set<Long> outerContainerIds,Long operatorId);
      
      /***
       * pda拣货提示货箱
       * @author tangming
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipInsideContainer(Set<Long> insideContainerIds,Long operatorId);
      
      /***
       * pda拣货提示sku
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipSku(Set<Long> skuIds,Long operatorId,Long locationId,Long ouId,Long insideContainerId);
      
      /***
       * 
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
      public CheckScanResultCommand pdaPickingyCacheSkuAndCheckContainer(List<Long> locationIds, Map<Long, Long> locSkuQty,Long locationId,Set<Long> locSkuIds,Set<Long> outerContainerIds,ContainerCommand outerContainerCmd,Long operatorId,Map<Long,Long> insideContainerSkuIdsQty,Map<Long, Set<Long>> insideContainerSkuIds,Set<Long> insideContainerIds,Set<Long> locInsideContainerIds,ContainerCommand insideContainerCmd,WhSkuCommand skuCmd);
    
    
}
