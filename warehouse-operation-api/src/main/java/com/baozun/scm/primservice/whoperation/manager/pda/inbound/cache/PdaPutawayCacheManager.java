/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * @author lichuan
 *
 */
public interface PdaPutawayCacheManager extends BaseManager {
    
    /**
     * pda系统指导上架库位推荐队列
     * @author lichuan
     * @param containerId
     * @param sysDate
     * @param logId
     * @return
     */
    boolean sysGuidePutawayLocRecommendQueue(Long containerId, String logId);
    
    /**
     * pda系统指导上架移除队列中第一个值
     * @author lichuan
     * @param containerId
     * @param logId
     */
    void sysGuidePutawayLocRecommendPopQueue(Long containerId, String logId);

    /**
     * pda整托上架缓存库存信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuidePalletPutawayCacheInventory(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda整托上架删除库存缓存信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     */
    void sysGuidePutawayRemoveInventory(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda整托上架缓存库存统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param isCmd
     * @param ouId
     * @return
     */
    InventoryStatisticResultCommand sysGuidePalletPutawayCacheInventoryStatistic(ContainerCommand containerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId);

    /**
     * pda整托上架缓存库存统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    InventoryStatisticResultCommand sysGuidePalletPutawayCacheInventoryStatistic(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda整托上架缓存库存信息及库存统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuidePalletPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId);


    /**
     * pda整托上架提示一个容器
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    Long sysGuidePalletPutawayCacheTipContainer0(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId);

    /**
     * pda整托上架缓存已操作容器
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @return
     */
    Long sysGuidePalletPutawayCacheTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, String logId);

    /**
     * pda整托上架复核sku并缓存及提示容器容器判断
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param skuCmd
     * @return
     */
    CheckScanSkuResultCommand sysGuidePalletPutawayCacheSkuOrTipContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, Map<Long, Map<Long, Long>> insideContainerSkuIdsQty,
            WhSkuCommand skuCmd, String logId);

    /**
     * pda整托上架复核sku并缓存及提示容器容器判断
     * @author lichuan
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
    CheckScanSkuResultCommand sysGuidePalletPutawayCacheSkuOrTipContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, Map<Long, Map<Long, Long>> insideContainerSkuIdsQty,
            WhSkuCommand skuCmd, Integer scanPattern, String logId);

    /**
     * pda上架清理所有缓存
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     */
    void sysGuidePalletPutawayRemoveAllCache(ContainerCommand containerCmd, String logId);

    /**
     * pda整箱上架缓存所有内部容器
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<ContainerCommand> sysGuideContainertPutawayCacheInsideContainer(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda整箱上架缓存所有内部容器统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    ContainerStatisticResultCommand sysGuideContainerPutawayCacheInsideContainerStatistic(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda整箱上架提示容器
     * 
     * @author lichuan
     * @param icList
     * @param logId
     * @return
     */
    Long sysGuideContainerPutawayTipContainer0(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId);

    /**
     * pda整箱上架提示下一个容器
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    Long sysGuideContainerPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId);

    /**
     * pda整箱上架缓存库存信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuideContainerPutawayCacheInventory(ContainerCommand insideContainerCmd, Long ouId, String logId);


    /**
     * pda整箱上架缓存库存统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param isCmd
     * @param ouId
     * @param logId
     * @return
     */
    InventoryStatisticResultCommand sysGuideContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId);

    /**
     * pda整箱上架缓存库存和统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    InventoryStatisticResultCommand sysGuideContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, Long ouId, String logId);

    /**
     * pda整箱上架缓存库存和统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuideContainerPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda整箱上架判断库位推荐失败或上架以后是否需要提示下一个容器
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param logId
     * @return
     */
    Boolean sysGuideContainerPutawayNeedTipContainer(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Set<Long> insideContainerIds, String logId);

    /**
     * pda整箱上架复核sku并缓存及提示容器容器判断
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param insideContainerSkuIdsQty
     * @param skuCmd
     * @param logId
     * @return
     */
    CheckScanSkuResultCommand sysGuideContainerPutawayCacheSkuAndCheckContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, Map<Long, Map<Long, Long>> insideContainerSkuIdsQty,
            WhSkuCommand skuCmd, String logId);

    /**
     * pda整箱上架
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param logId
     */
    void sysGuideContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, String logId);

    /**
     * pda拆箱上架内部容器信息统计
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    ContainerStatisticResultCommand sysGuideSplitContainerPutawayCacheInsideContainerStatistic(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda拆箱上架提示一个容器号
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    Long sysGuideSplitContainerPutawayTipContainer0(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId);

    /**
     * pda拆箱上架缓内部容器存库存信息
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuideSplitContainerPutawayCacheInventory(ContainerCommand insideContainerCmd, Long ouId, String logId);

    /**
     * pda拆箱上架缓存库存统计信息
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param isCmd
     * @param ouId
     * @param logId
     * @return
     */
    InventoryStatisticResultCommand sysGuideSplitContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId);

    /**
     * pda拆箱上架缓存库存统计信息
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param ouId
     * @param logId
     * @return
     */
    InventoryStatisticResultCommand sysGuideSplitContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, Long ouId, String logId);

    /**
     * pda拆箱上架缓存库存和统计信息
     * 
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuideSplitContainerPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId);

    /**
     * pda拆箱上架提示库位
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param locationIds
     * @param logId
     * @return
     */
    Long sysGuideSplitContainerPutawayTipLocation0(ContainerCommand insideContainerCmd, Set<Long> locationIds, String logId);

    /**
     * pda拆箱上架提示商品
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param locationId
     * @param locSkuAttrIds
     * @param logId
     * @return
     */
    String sysGuideSplitContainerPutawayTipSku0(ContainerCommand insideContainerCmd, Long locationId, Map<Long, Set<String>> locSkuAttrIds, String logId);

    /**
     * pda拆箱上架提示商品、库位、容器、上架判断
     * 
     * @author lichuan
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
    CheckScanSkuResultCommand sysGuideSplitContainerPutawayTipSkuOrLocOrContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty,
            Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect, Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds, Long locationId, WhSkuCommand skuCmd, String logId);


    /**
     * pda拆箱上架清除缓存
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param isAfterPutawayTipContainer
     * @param isAfterPutawayTipLoc
     * @param locationId
     * @param logId
     */
    void sysGuideSplitContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, Boolean isAfterPutawayTipLoc, Long locationId, String logId);

    /**
     * pda拆箱上架提示sn
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param locationId
     * @param locSkuAttrIds
     * @param skuAttrId
     * @param logId
     * @return
     */
    String sysGuideSplitContainerPutawayTipSku(ContainerCommand insideContainerCmd, Long locationId, Map<Long, Set<String>> locSkuAttrIds, String skuAttrId, String logId);

    /**
     * pda拆箱上架提示库位
     * 
     * @author lichuan
     * @param insideContainerCmd
     * @param locationIds
     * @param logId
     * @return
     */
    Long sysGuideSplitContainerPutawayTipLocation(ContainerCommand insideContainerCmd, Set<Long> locationIds, Long locationId, String logId);
    
    /**
     * pda拆箱上架判断库位推荐失败以后是否需要提示下一个容器
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    Boolean sysGuideSplitContainerPutawayNeedTipContainer(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Set<Long> insideContainerIds, String logId);
    
    /**
     * pda拆箱上架提示容器
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    Long sysGuideSplitContainerPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId);

    /**
     * pda拆箱上架提示指定容器
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param containerId
     * @param logId
     * @return
     */
    Long sysGuideSplitContainerPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Long containerId, String logId);

}
