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
 package com.baozun.scm.primservice.whoperation.dao.warehouse.inventory;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;

public interface WhSkuInventoryAllocatedDao extends BaseDao<WhSkuInventoryAllocated,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<WhSkuInventoryAllocated> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@QueryPage("queryCount")
	Pagination<WhSkuInventoryAllocated> query(Page page,Sort[] sorts, QueryCondition cond);
	
	List<WhSkuInventoryAllocated> query(QueryCondition cond);
	
	Long queryCount(QueryCondition cond);
	
	@CommonQuery
	int saveOrUpdate(WhSkuInventoryAllocated o);

	List<WhSkuInventoryAllocated> findNotOccupyListBySkuIdAndBhCode(@Param("skuId") Long skuId, @Param("bhCode") String bhCode, @Param("ouId") Long ouId);

    List<WhSkuInventoryAllocated> findbyOccupationCode(@Param("occupationCode") String odoCode, @Param("ouId") Long ouId);

    int deleteExt(@Param("id") Long id, @Param("ouId") Long ouId);
	
	/**
     * 根据补货工作释放及拆分条件获取所有补货数据
     *
     * @author qiming.liu
     * @param ReplenishmentRuleCommand
     * @return
     */
	List<WhSkuInventoryAllocatedCommand> getAllReplenishmentLst(ReplenishmentRuleCommand replenishmentRuleCommand);
	
	/**
     * 根据条件查询库存数量
     *
     * @author qiming.liu
     * @param ReplenishmentRuleCommand
     * @return
     */
	Double skuInventoryAllocatedQty (WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand);
}
