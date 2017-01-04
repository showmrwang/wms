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
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilitySortorderCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacilitySortorder;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhOutboundFacilitySortorderDao extends BaseDao<WhOutboundFacilitySortorder, Long>{

	@QueryPage("findListCountByQueryMap")
	Pagination<WhOutboundFacilitySortorder> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);
	
	@QueryPage("findListCountByQueryMapExt")
	Pagination<WhOutboundFacilitySortorderCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhOutboundFacilitySortorder o);
	
	/**
	 * 判断顺序是否存在
	 * @param type 
	 * @param id 
	 */
	int checkPriorityUnique(@Param("id") Long id, @Param("type") String type, @Param("priority") Integer priority, @Param("ouId") Long ouId);
	
	/**
	 * 根据出库设施id和type查找顺序
	 * @param id
	 * @param type
	 * @param ouId
	 * @return
	 */
	WhOutboundFacilitySortorder findByFacilityIdAndType(@Param("id") Long id, @Param("type") String type, @Param("ouId") Long ouId);
	
	/**
	 * 删除数据
	 * @param id
	 * @param type
	 * @param ouId
	 * @return
	 */
	int deleteByFacilityIdAndType(@Param("id") Long id, @Param("type") String type, @Param("ouId") Long ouId);
	
	/**
	 * 获取下一个顺序值
	 * @param ouId
	 * @return
	 */
	Integer getNextPriority(@Param("ouId") Long ouId);
	
}
