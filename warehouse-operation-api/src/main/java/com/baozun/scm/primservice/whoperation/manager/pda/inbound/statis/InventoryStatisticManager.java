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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.statis;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * @author lichuan
 *
 */
public interface InventoryStatisticManager extends BaseManager {

    public InventoryStatisticResultCommand sysGuidePutawayInvStatistic(List<WhSkuInventoryCommand> invList, Integer putawayPatternDetailType, Long ouId, String logId);
    
    public InventoryStatisticResultCommand sysGuidePutawayInvStatistic(List<WhSkuInventoryCommand> invList, Integer putawayPatternDetailType, List<UomCommand> lenUomCmds, List<UomCommand> weightUomCmds, ContainerCommand containerCmd, Long ouId, Long userId, String logId);
    
    public ContainerStatisticResultCommand sysGuidePutawayContainerStatistic(List<ContainerCommand> icList, Integer putawayPatternDetailType, Long ouId, String logId);

}
