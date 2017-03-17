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
package com.baozun.scm.primservice.whoperation.dao.collect;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;

public interface WhOdoArchivIndexDao extends BaseDao<WhOdoArchivIndex, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoArchivIndex> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdoArchivIndex> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdoArchivIndex> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdoArchivIndex o);

    /**
     * 通过电商平台订单号(NOT NULL) or 数据来源(DEFAULT NULL) 查询仓库出库单归档索引数据
     */
    List<WhOdoArchivIndex> findWhOdoArchivIndexByEcOrderCode(@Param("ecOrderCode") String ecOrderCode, @Param("dataSource") String dataSource, @Param("num") String num, @Param("ouid") Long ouid);

}
