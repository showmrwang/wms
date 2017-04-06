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
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundConfirmCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
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
     * 库位解绑（生成容器库存及删除待移入库存）
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param locationCode
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    void execUnbinding(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);
    
    
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

	void allocationInventoryByLineList(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules, Long skuId, Long storeId, Long invStatusId, Warehouse wh, String logId);
	
	/**
	 * 根据出库单Id清除库存
	 * @author kai.zhu
	 * @version 2017年3月14日
	 */
	void releaseInventoryByOdoId(Long odoId, Warehouse wh);
	
	/**
	 * 根据占用码清除库存
	 * @author kai.zhu
	 * @version 2017年3月14日
	 */
	void releaseInventoryByOccupyCode(String occupyCode, Warehouse wh);
	
	/**
     * 根据参数查询出库存信息
     * @author qiming.liu
     * @param whSkuInventory
     * @return
     */
    List<WhSkuInventory> findWhSkuInventoryListByPramas(WhSkuInventory whSkuInventory);
    
	void replenishmentToLines(List<WhWaveLine> lines, Long odoId, String bhCode, Map<String, List<ReplenishmentRuleCommand>> ruleMap, Map<String, String> map, Warehouse wh);
	
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
     * @param ruleList
     * @param wh
     */
    void replenishmentToLocation(ReplenishmentMsg msg, List<ReplenishmentRuleCommand> ruleList, Warehouse wh);
    
    /***
     * pda拣货生成容器库存
     * @param operationId
     * @param ouId
     */
    public void pickingAddContainerInventory(Long operationId,Long ouId,Boolean isTabbInvTotal,Long userId,Integer pickingWay,Integer scanPattern);
    
    
    /**
     * 建议上架推荐库位失败的情况下走人工分支,库位绑定（分配容器库存及生成待移入库位库存）
     * @author tangming
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    public void manMadeBinding(Long outerContainerId,Long insideContainerId, Warehouse warehouse, Long locationId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId,Double scanSkuQty);

    
    /****
     * 建议上架走人工流程,上架
     * @param outerContainerCmd
     * @param insideConatinerCmd
     * @param locationId
     * @param putawayPatternDetailType
     * @param ouId
     * @param skuAttrId
     */
    public void sysManPutaway(Double skuScanQty,Warehouse warehouse,Long userId,ContainerCommand outerContainerCmd,ContainerCommand insideContainerCmd,String locationCode,Integer putawayPatternDetailType,Long ouId,String skuAttrId);
    
    /***
     * 补货上架
     * @param operationId
     * @param ouId
     * @param isTabbInvTotal
     * @param userId
     * @param workCode
     */
    public void replenishmentPutaway(Long operationId,Long ouId,Boolean isTabbInvTotal,Long userId,String workCode);


    
    /***
     * 补货中的拣货由库位库存生成容器库存
     * @param operationId
     * @param ouId
     * @param outerContainerId
     * @param insideContainerId
     * @param turnoverBoxId
     */
    public void replenishmentContainerInventory(Long operationId,Long ouId,Long outerContainerId,Long insideContainerId,Long turnoverBoxId,Boolean isTabbInvTotal,Long userId,String workCode);
    
    /**
     * 获取入库反馈信息实体
     * @author kai.zhu
     * @version 2017年3月13日
     */
	WhInboundConfirmCommand findInventoryByPo(WhPo po, List<WhPoLine> lineList, Long ouId);

	void allocationInventoryByLineListNew(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules,
			Long skuId, Long storeId, Long invStatusId, Warehouse wh, String logId);

	Map<String, String> replenishmentToLinesNew(List<WhWaveLine> lines, Long odoId, String bhCode,
			Map<String, List<ReplenishmentRuleCommand>> ruleMap, Map<String, String> map, Warehouse wh);
	
    List<WhSkuInventory> findWhSkuInventoryByPramas(WhSkuInventory inventory);
	
    /**
     * 生成出库箱库存
     * 
     * @param WhSkuInventoryCommand
     * @return
     */
    void saveOrUpdate(WhSkuInventoryCommand whSkuInventoryCommand);
    
    /**
     * 删除原有的库存（小车货格库存，小车出库箱库存，播种墙货格库存，播种墙出库箱库存，周转箱库存）
     * 
     * @param outId
     * @return
     */
    void deleteSkuInventory(Long id, Long ouid);
    
    /**
     * 根据容器查询库存
     * 
     * @param whSkuInventory
     * @return
     */
    List<WhSkuInventory> findSkuInventoryByContainer(WhSkuInventory whSkuInventory);
    
    /**
     * 根据参数查询库存信息
     * 
     * @author qiming.liu
     * @param id
     * @param uuid
     * @param ouid
     * @return
     */
    WhSkuInventoryCommand findWhSkuInventoryByIdAndUuidAndOuid(Long id, String uuid, Long ouid);
}
