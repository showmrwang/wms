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

import com.baozun.scm.primservice.whoperation.command.warehouse.WeightingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;

public interface WhCheckingDao extends BaseDao<WhChecking, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhChecking> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhChecking> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhChecking> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhChecking o);

    WeightingCommand findByWaybillCode(@Param("waybillCode") String waybillCode, @Param("ouId") Long ouId);

    WeightingCommand findByOutboundBoxCode(@Param("outboundBoxCode") String outboundBoxCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 按单复核-根据设施查找复核数据
     * @param facilityCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findListByFacilityCode(@Param("facilityCode") String facilityCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 按单复核-根据小车查找复核数据
     * @param outerContainerCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findListByOuterContainerCode(@Param("outerContainerCode") String outerContainerCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 按单复核-根据周转箱查找复核数据
     * @param containerCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findListByContainerCode(@Param("containerCode") String containerCode, @Param("ouId") Long ouId);

}
