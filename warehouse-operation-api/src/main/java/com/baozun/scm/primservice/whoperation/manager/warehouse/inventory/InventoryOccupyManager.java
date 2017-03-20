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
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.HardAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

/**
 * @author lichuan
 *
 */
public interface InventoryOccupyManager extends BaseManager {
    
    /**
     * 简单库存占用
     * @author lichuan
     * @param invCmds
     */
    void simpleOccupy(List<WhSkuInventoryCommand> invCmds, String occupyCode, String logId);
    
    /**
     * 硬分配占用库存
     * @param skuInvs
     * @param qty
     * @param occupyCode
     * @param odoLineId
     * @param wh
     * @param isPalletContainer 
     * @return
     * @throws Exception 
     */
	Double hardAllocateOccupy(List<WhSkuInventoryCommand> skuInvs, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh);

	Double hardAllocateListOccupy(List<WhSkuInventoryCommand> list, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, Boolean isStaticLocation, Set<String> staticLocationIds);

	Double occupyInvUuidsByPalletContainer(List<WhSkuInventoryCommand> uuids, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, String unitCodes, List<WhSkuInventoryCommand> allSkuInvs, Boolean isStaticLocation, Set<String> staticLocationIds, Set<String> trayIds, Set<String> packingCaseIds);

	Double occupyInvUuids(List<WhSkuInventoryCommand> uuids, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, String allocateUnitContainer, List<WhSkuInventoryCommand> allSkuInvs, Boolean isStaticLocation, Set<String> staticLocationIds);
	
	HardAllocationCommand hardAllocateListOccupyNew(List<WhSkuInventoryCommand> list, List<String> units, Double qty, Long skuId, 
				String occupyCode, Long odoLineId, String occupySource, Warehouse wh, Boolean isStaticLocation, String logId);

}
