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
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.AllocateStrategy;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

/**
 * @author lichuan
 *
 */
public interface WhSkuInventoryManager extends BaseManager {
    
    /**
     * 库位绑定（分配容器库存及生成待移入库位库存）
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    @Deprecated
    void binding(List<WhSkuInventoryCommand> invList, Warehouse warehouse, List<LocationRecommendResultCommand> lrrList, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);
    
    /**
     * 库位绑定（分配容器库存及生成待移入库位库存）
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    void execBinding(List<WhSkuInventoryCommand> invList, Warehouse warehouse, List<LocationRecommendResultCommand> lrrList, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);
    
    
    /**
     * 执行上架（已分配容器库存出库及待移入库位库存入库）
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    @Deprecated
    void putaway(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);
    
    /**
     * 执行上架（已分配容器库存出库及待移入库位库存入库）
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    void execPutaway(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);

    /**
     * 执行上架（已分配容器库存出库及待移入库位库存入库）
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
   public void manMadePutaway(Boolean isOuterContainer,Double scanSkuQty,WhSkuInventoryCommand invCmd,ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long locationId, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);

	void allocationInventoryByLine(WhWaveLineCommand whWaveLineCommand, List<AllocateStrategy> rules, Double qty, Warehouse wh, String logId);

	void allocationInventoryByLineList(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules, Warehouse wh, String logId);

	void releaseInventoryByOdoId(Long odoId, Warehouse wh);
	
	/**
     * 根据参数查询出库存信息
     * @author qiming.liu
     * @param whSkuInventory
     * @return
     */
    List<WhSkuInventory> findWhSkuInventoryListByPramas(WhSkuInventory whSkuInventory);
    
	void replenishmentToLines(List<WhWaveLine> lines, String bhCode, Map<String, List<ReplenishmentRuleCommand>> ruleMap, Map<String, String> map, Warehouse wh);
	
	/**
	 * 根据策略和明细找到库存
	 * @param strategyCode
	 * @param whWaveLineCommand
	 * @param qty
	 * @return
	 */
	List<WhSkuInventoryCommand> findInventorysByAllocateStrategy(String strategyCode, WhSkuInventoryCommand invCommand, Double qty);

    /**
     * 查找库位的库存量【库位库存量=库位在库库存+库位待移入库存】
     * 
     * @param id
     * @return
     */
    double findInventoryByLocation(Long locationId, Long ouId);

    /**
     * 查找库存中库位商品
     * 
     * @param locationId
     * @param ouId
     * @return
     */
    Long findSkuInInventoryByLocation(Long locationId, Long ouId);

    /**
     * 库位补货逻辑
     * 
     * @param msg
     * @param rule
     * @param wh
     */
    void replenishmentToLocation(ReplenishmentMsg msg, ReplenishmentRuleCommand rule, Warehouse wh);

}
