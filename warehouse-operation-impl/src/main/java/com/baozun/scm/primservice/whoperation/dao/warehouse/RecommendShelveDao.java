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

import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendShelveCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.RecommendShelve;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface RecommendShelveDao extends BaseDao<RecommendShelve, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<RecommendShelve> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<RecommendShelve> query(Page page, Sort[] sorts, QueryCondition cond);

    List<RecommendShelve> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(RecommendShelve o);


    /**
     * 根据规则编号查找规则
     *
     * @author mingwei.xie
     * @param shelveRecommendRuleId
     * @return
     */
    List<RecommendShelveCommand> findCommandByRuleId(@Param("shelveRecommendRuleId") Long shelveRecommendRuleId, @Param("ouId") Long ouId);
    
    /**
     * 根据规则编号查找规则
     * @author lichuan
     * @param shelveRecommendRuleId
     * @param ouId
     * @return
     */
    List<RecommendShelveCommand> findCommandByRuleIdOrderByPriority(@Param("shelveRecommendRuleId") Long shelveRecommendRuleId, @Param("ouId") Long ouId);


    /**
     * 根据规则ID删除相关的库位推荐规则
     *
     * @author mingwei.xie
     * @param shelveRuleId
     * @param ouId
     * @return
     */
    int deleteByRuleId(@Param("shelveRuleId") Long shelveRuleId, @Param("ouId") Long ouId);

    /**
     * 统计规则下配置的库位推荐规则数量
     *
     * @author mingwei.xie
     * @param shelveRuleId
     * @param ouId
     * @return
     */
    Long findListCountByRuleId(@Param("shelveRuleId") Long shelveRuleId, @Param("ouId") Long ouId);
}
