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

import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;

public interface WhOdoVasDao extends BaseDao<WhOdoVas, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoVas> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdoVas> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdoVas> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdoVas o);

    /**
     * [通用方法]查找出库单增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param vasType
     * @param ouId
     * @return
     */
    List<WhOdoVas> findOdoVasByOdoIdOdoLineIdType(@Param("odoId") Long odoId, @Param("odoLineId") Long odoLineId, @Param("vasType") String vasType, @Param("ouId") Long ouId);

    /**
     * [通用方法]查找出库单仓库增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @return
     */
    List<WhOdoVasCommand> findOdoOuVasCommandByOdoIdOdoLineIdType(@Param("odoId") Long odoId, @Param("odoLineId") Long odoLineId, @Param("ouId") Long ouId);

    /**
     * [通用方法]删除
     * 
     * @param id
     * @param ouId
     */
    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [通用方法]查找出库单快递增值
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @return
     */
    List<WhOdoVasCommand> findOdoExpressVasCommandByOdoIdOdoLineId(@Param("odoId") Long odoId, @Param("odoLineId") Long odoLineId, @Param("ouId") Long ouId);

}
