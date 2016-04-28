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
package com.baozun.scm.primservice.whoperation.dao.poasn;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.model.poasn.BiPoSn;

public interface BiPoSnDao extends BaseDao<BiPoSn,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<BiPoSn> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@QueryPage("queryCount")
	Pagination<BiPoSn> query(Page page,Sort[] sorts, QueryCondition cond);
	
	List<BiPoSn> query(QueryCondition cond);
	
	Long queryCount(QueryCondition cond);
	
	@CommonQuery
	int saveOrUpdate(BiPoSn o);
	
}
