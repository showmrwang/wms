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

import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

public interface WhSkuInventoryTobefilledDao extends BaseDao<WhSkuInventoryTobefilled,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<WhSkuInventoryTobefilled> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@QueryPage("queryCount")
	Pagination<WhSkuInventoryTobefilled> query(Page page,Sort[] sorts, QueryCondition cond);
	
	List<WhSkuInventoryTobefilled> query(QueryCondition cond);
	
	Long queryCount(QueryCondition cond);
	
	@CommonQuery
	int saveOrUpdate(WhSkuInventoryTobefilled o);
	
	   /**
     * 删除待上架库存
     * @param id
     * @param ouId
     */
    public void deleteByExt(@Param("id") Long id,@Param("ouId") Long ouId);
    
    /**
     * 
     * @param outerContainerId
     * @param insideContainerId
     * @param ouId
     * @return
     */
    public List<WhSkuInventoryTobefilled> findWhSkuInventoryTobefilled(@Param("outerContainerId") Long outerContainerId,@Param("insideContainerId") Long insideContainerId,@Param("ouId") Long ouId);
	
}
