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

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;

public interface BiPoDao extends BaseDao<BiPo,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<BiPo> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@QueryPage("queryCount")
	Pagination<BiPo> query(Page page,Sort[] sorts, QueryCondition cond);
	
	List<BiPo> query(QueryCondition cond);
	
	Long queryCount(QueryCondition cond);
	
	@CommonQuery
	int saveOrUpdate(BiPo o);
	
    /**
     * @deprecated
     * @param extCode
     * @param storeId
     * @param ouId
     * @return
     */
    long findBiPoByCodeAndStore(@Param("extCode") String extCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId);

    BiPo findbyPoCode(@Param("code") String code);

    /**
     * 根据pocode关联查询 关联 customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code
     * @return
     */
    BiPoCommand findCommandbyPoCode(@Param("code") String code);

    /**
     * 根据id关联查询 关联 customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code
     * @return
     */
    BiPoCommand findCommandbyId(@Param("id") Long id);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<BiPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

}
