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
package com.baozun.scm.primservice.whoperation.dao.odo;

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

import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;

public interface WhOdoLineDao extends BaseDao<WhOdoLine, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdoLine> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdoLine> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdoLine o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdoLine o);

    /**
     * [通用方法]根据ODOLINEID,OUID查找ODOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoLine findOdoLineById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法]出库单明细分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<OdoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]根据ODOID和OUID查找明细数量
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    long findOdoLineListCountByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据ODOID和OUID查找明细
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhOdoLine> findOdoLineListByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * 获取出库单明细列表
     * @param o
     * @return
     */
    List<OdoLineCommand> findObject(WhOdoLine o);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

}
