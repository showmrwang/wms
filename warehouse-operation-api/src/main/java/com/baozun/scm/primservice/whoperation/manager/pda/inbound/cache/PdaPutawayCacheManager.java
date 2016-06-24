/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * @author lichuan
 *
 */
public interface PdaPutawayCacheManager extends BaseManager {
    
    /**
     * pda整托上架缓存库存信息
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuidePalletPutawayCacheInventory(ContainerCommand containerCmd, Long ouId, String logId);
    
    /**
     * pda整托上架缓存库存统计信息
     * @author lichuan
     * @param containerCmd
     * @param isCmd
     * @param ouId
     * @return
     */
    InventoryStatisticResultCommand sysGuidePalletPutawayCacheInventoryStatistic(ContainerCommand containerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId);
    
    /**
     * pda整托上架缓存库存统计信息
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    InventoryStatisticResultCommand sysGuidePalletPutawayCacheInventoryStatistic(ContainerCommand containerCmd, Long ouId, String logId);
    
    /**
     * pda整托上架缓存库存信息及库存统计信息
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    List<WhSkuInventoryCommand> sysGuidePalletPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId);
    
    /**
     * pda整托上架缓存已操作容器
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @return
     */
    Long sysGuidePalletPutawayCacheTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds);

}
