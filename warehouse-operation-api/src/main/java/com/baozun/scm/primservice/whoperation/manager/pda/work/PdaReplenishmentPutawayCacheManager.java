package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaReplenishmentPutawayCacheManager extends BaseManager{

    /**
     * 缓存库位（用完就删）
     * @param locationIds
     * @param operationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipLocation(List<Long> locationIds,Long operationId);
    
    /***
     * 缓存周转箱
     * @param turnoverBoxIds
     * @param operationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipTurnoverBox(Set<Long> turnoverBoxIds,Long operationId,Long locationId);
    
    /***
     * 缓存货箱
     * @param turnoverBoxIds
     * @param operationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipContainer(Set<Long> outerContainerIds,Long operationId,Long locationId);
    
    
    /***
     * 提示托盘
     * @param outerContainerIds
     * @param operationId
     * @param locationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipOutContainer(Set<Long> outerContainerIds,Long operationId,Long locationId);
    
    /****
     * 清楚补货上架缓存
     * @param operationId
     */
    public void pdaReplenishPutwayRemoveAllCache(Long operationId,Long turnoverBoxId,Long locationId,Boolean isPutaway,Integer replenishWay);
    

    /**
     * 补货上架缓存周转箱
     * @param operationId
     * @param locationId
     */
    public void pdaReplenishPutwayCacheTurnoverBox(Long operationId,Long turnoverBoxId,Long locationId,Long ouId,Boolean isOnlyLocation);
    
    /**
     * 补货缓存托盘
     * @param operationId
     * @param outerContainerId
     * @param locationId
     * @param ouId
     * @param isOnlyLocation
     */
    public void pdaReplenishPutwayCacheOuterContainer(Long operationId,Long outerContainerId,Long locationId,Long ouId,Boolean isOnlyLocation);
    
    /***
     * 提示sku
     * @param skuIds
     * @param skuAttrIdsQty
     * @param skuAttrIdsSnDefect
     */
    public String pdaReplenishPutWayTipSku(Set<Long> skuIds, Map<Long, Map<String, Long>> skuAttrIdsQty,Map<String, Set<String>> skuAttrIdsSnDefect,Long locationId,Long turnoverBoxId);
    
    
    /***
     * 补货中上架提示sku或周转箱
     * @param turnoverIds
     * @param skuIds
     * @param skuAttrIdsQty
     * @param skuAttrIdsSnDefect
     * @param locationId
     * @return
     */
    public CheckScanResultCommand pdaReplenishPutWayTipSkuTurnoverBox(List<Long> locationIds,Long skuId,Double scanSkuQty,String skuAttrId,String skuAttrIdNoSn,Boolean isSnLine,Long operationId,Long turnoverId,Set<Long> turnoverIds,Set<Long> skuIds, Map<Long, Map<String, Long>> skuAttrIdsQty,Map<String, Set<String>> skuAttrIdsSnDefect,Long locationId);
}
