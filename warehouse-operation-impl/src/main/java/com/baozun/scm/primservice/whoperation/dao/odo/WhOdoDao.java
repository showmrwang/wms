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

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

public interface WhOdoDao extends BaseDao<WhOdo, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdo> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdo> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdo o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdo o);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdo findByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    int existsSkuInOdo(@Param("odoId") Long odoId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);

    String findOdoMergableIds(@Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType, @Param("store") String store,
            @Param("deliverGoodsTime") String deliverGoodsTime);

    List<OdoMergeCommand> odoMerge(@Param("odoIdString") String odoIdString, @Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType,
            @Param("store") String store, @Param("deliverGoodsTime") String deliverGoodsTime);

    List<OdoCommand> findOdoListByIdOuId(@Param("ids") String idString, @Param("ouId") Long ouId, @Param("odoStatus") String odoStatus);

}
