package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ManMadeContainerStatisticCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

/**
 * 人工指定上架缓存
 * 
 * @author tangming
 *
 */
public interface PdaManmadePutawayCacheManager extends BaseManager {

    
    /***
     * 整箱上架缓存内部容器
     * @param insideContainerCmd
     * @param outerContainerId
     * @param logId
     */
    public void containerPutawayCacheInsideContainer(ContainerCommand insideContainerCmd, Long outerContainerId, String logId,String outerContainerCode);
    /**
     * 提示货箱容器号
     * 
     * @param icList
     * @param logId
     * @return
     */
    public Long containerPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId);
    /**
     * 缓存容器内部的信息
     * 
     * @param containerId
     * @param ouId
     * @return
     */
   public ManMadeContainerStatisticCommand manMadePutawayCacheContainer(PdaManMadePutawayCommand manMadePutawayCommand,Long containerId);
   
   
   /**
    * 缓存容器内上架sku信息
    * @param containerId
    * @param ouId
    * @return
    */
   public List<WhSkuInventory> manMadePutwayCacheSkuInventory(Long containerId,Long ouId,Boolean isOuterSkuInventory);
   
   
   /***
    * 整托上架:人工上架判断sku和容器是否扫描完毕
    * @param ocCmd
    * @param icCmd
    * @param insideContainerIds
    * @param insideContainerSkuIds
    * @param insideContainerSkuIdsQty
    * @param skuCmd
    * @param scanPattern
    * @param logId
    * @return
    */
   public CheckScanSkuResultCommand manMadePalletPutawayCacheSkuOrTipContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, Map<Long, Map<Long, Long>> insideContainerSkuIdsQty,
                                                                                WhSkuCommand skuCmd, Integer scanPattern, String logId);
   
   /***
    * 整箱上架:人工上架判断容器内部sku是否扫描完毕
    * @param ocCmd
    * @param icCmd
    * @param insideContainerIds
    * @param insideContainerSkuIds
    * @param insideContainerSkuIdsQty
    * @param skuCmd
    * @param scanPattern
    * @param logId
    * @return
    */
   public CheckScanSkuResultCommand manMadeContainerPutawayCacheSkuAndCheckContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds,
                                                                                      Map<Long, Map<Long, Long>> insideContainerSkuIdsQty, WhSkuCommand skuCmd, Integer scanPattern, String logId);
   /**人工上架:整托上架清除缓存
    * @param containerCmd
    * @param ouId
    * @param logId
    */
   public void manMadePalletPutawayRemoveAllCache(ContainerCommand containerCmd, String logId);
   
   /***
    * 人工上架:整箱上架清除缓存
    * @param containerCmd
    * @param insideContainerCmd
    * @param isAfterPutawayTipContainer
    * @param logId
    */
   public void manMadeContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, String logId);
   
   
   /***
    * pda拆箱上架提示商品、容器判断
    * @param ocCmd
    * @param icCmd
    * @param insideContainerIds
    * @param insideContainerSkuAttrIdsQty
    * @param insideContainerSkuAttrIdsSnDefect
    * @param insideContainerLocSkuAttrIds
    * @param locationId
    * @param skuCmd
    * @param logId
    * @return
    */
   public CheckScanSkuResultCommand manMadeSplitContainerPutawayTipSkuOrContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds,
                                                                                  Map<Long, Map<Long, Long>> insideContainerSkuIdsQty, WhSkuCommand skuCmd, Integer scanPattern, String logId);
   
   /***
    * 拆箱上架清除缓存
    * @param outerContainerCmd
    * @param insideContainerCmd
    * @param isAfterPutawayTipContainer
    * @param logId
    */
   public void manMadeSplitContainerPutawayRemoveAllCache(ContainerCommand outerContainerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, String logId,Long scanSkuId);

   
   public CheckScanSkuResultCommand  manMadeContainerCacheContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds,String logId);
}
