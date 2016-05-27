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
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunction;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhFunctionDao extends BaseDao<WhFunction, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhFunction> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhFunctionCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhFunction> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhFunction> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhFunction o);

    @CommonQuery
    int saveOrUpdateByVersion(WhFunction o);

    /**
     * 通过ID+OUID查询对应数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhFunction findWhFunctionById(@Param("id") Long id, @Param("ouid") Long ouid);

    WhFunctionCommand checkNameOrCode(@Param("name") String name, @Param("code") String code, @Param("templet") String templet, @Param("ouid") Long ouid);

    /**
     * 查询所有功能 可用并且不是系统级数据
     * 
     * @param ouid
     * @return
     */
    List<WhFunction> findWhFunctionListNotIsSys(Long ouid);

    void deleteFunction(@Param("id") Long id, @Param("ouid") Long ouid);

}
