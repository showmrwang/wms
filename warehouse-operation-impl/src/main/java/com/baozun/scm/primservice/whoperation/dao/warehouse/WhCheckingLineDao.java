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

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;

public interface WhCheckingLineDao extends BaseDao<WhCheckingLine, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhCheckingLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhCheckingLine> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhCheckingLine> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhCheckingLine o);

    @CommonQuery
    int saveOrUpdateByVersion(WhCheckingLine o);

    /**
     * 根据复核ID查询复核明细
     * 
     * @param checkingId
     * @param ouId
     * @return
     */
    List<WhCheckingLineCommand> getCheckingLineCommandByCheckingId(@Param("checkingId") Long checkingId, @Param("ouId") Long ouId);

    /**
     * 根据复核ID查询复核明细
     * 
     * @param checkingId
     * @param ouId
     * @return
     */
    List<WhCheckingLine> getCheckingLineByCheckingId(@Param("checkingId") Long checkingId, @Param("ouId") Long ouId);

    public WhCheckingLineCommand findCheckingLineById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过复核明细对象查找复核明细列表
     * 
     * @param o
     * @return
     */
    List<WhCheckingLineCommand> findListByParamExt(WhCheckingLine o);


    public WhCheckingLineCommand judeIsLastBox(@Param("ouId") Long ouId, @Param("odoId") Long odoId);

    /**
     * 
     * @param checkingId
     * @param ouId
     * @return
     */
    public Double countCheckingLine(@Param("checkingId") Long checkingId, @Param("ouId") Long ouId);

    public List<WhChecking> findListByParamWithNoFinish(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    List<WhCheckingLineCommand> findQtyAndOdolineIdByOutboundboxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);



}
