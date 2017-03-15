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
package com.baozun.scm.primservice.whinterface.dao.inbound;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInbound;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhInboundDao extends BaseDao<WhInbound, Long> {

	@QueryPage("findListCountByQueryMap")
	Pagination<WhInbound> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

	@CommonQuery
	int saveOrUpdate(WhInbound o);
	
	@CommonQuery
	int saveOrUpdateByVersion(WhInbound o);
	
	/**
	 * 根据uuid或者extPoCode查询入库单数据是否在中间表
	 * @author kai.zhu
	 * @version 2017年2月21日
	 */
	long countWhInboundByUuid(@Param("uuid") String uuid);
	
	/**
	 * 检查此店铺是否在这客户下
	 * @author kai.zhu
	 * @version 2017年2月22日
	 */
	long checkStoreIsInCustomer(@Param("storeCode") String storeCode, @Param("customerCode") String customerCode);
	
	/**
	 * 查找可生成入库数据
	 * @author kai.zhu
	 * @version 2017年3月2日
	 */
	List<WhInbound> findWhInBoudData();
	
}
