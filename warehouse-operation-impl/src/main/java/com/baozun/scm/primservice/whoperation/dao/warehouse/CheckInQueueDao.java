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

import com.baozun.scm.primservice.whoperation.command.warehouse.CheckInQueueCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.CheckInQueue;

public interface CheckInQueueDao extends BaseDao<CheckInQueue, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<CheckInQueue> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<CheckInQueue> query(Page page, Sort[] sorts, QueryCondition cond);

    List<CheckInQueue> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(CheckInQueue o);

    @CommonQuery
    int saveOrUpdateByVersion(CheckInQueue o);

    /**
     * 根据asn预约信息查找等待队列信息
     *
     * @author mingwei.xie
     * @param reserveId
     * @param ouId
     * @return
     */
    CheckInQueueCommand findCheckInQueueByAsnReserveId(@Param("reserveId") Long reserveId, @Param("ouId") Long ouId);

    /**
     * 查询所有排队中的ASN，按照
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    List<CheckInQueueCommand> getListOrderBySequenceAsc(@Param("ouId") Long ouId);

    /**
     * 已分配月台则删除排队信息
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    int deleteById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 使用command更新
     *
     * @author mingwei.xie
     * @param checkInQueue
     * @return
     */
    int updateByVersionExt(CheckInQueue checkInQueue);

    /**
     * 根据id查询签入队列信息
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    CheckInQueue findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
}
