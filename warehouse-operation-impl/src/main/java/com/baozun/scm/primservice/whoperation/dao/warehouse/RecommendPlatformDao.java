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

import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.RecommendPlatform;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface RecommendPlatformDao extends BaseDao<RecommendPlatform, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<RecommendPlatform> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<RecommendPlatform> query(Page page, Sort[] sorts, QueryCondition cond);

    List<RecommendPlatform> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(RecommendPlatform o);


    /**
     * 根据规则编号查找推荐的月台
     *
     * @author mingwei.xie
     * @param platformRecommendRuleId
     * @param ouId
     * @return
     */
    List<RecommendPlatformCommand> findCommandByRuleId(@Param("platformRecommendRuleId") Long platformRecommendRuleId, @Param("ouId") Long ouId);

    /**
     * 根据规则ID删除相关的推荐月台
     *
     * @author mingwei.xie
     * @param platformRecommendRuleId
     * @param ouId
     * @return
     */
    int deleteByRuleId(@Param("platformRecommendRuleId") Long platformRecommendRuleId, @Param("ouId") Long ouId);

    /**
     * 统计规则下配置的月台数量
     *
     * @author mingwei.xie
     * @param platformRecommendRuleId
     * @param ouId
     * @return
     */
    Long findListCountByRuleId(@Param("platformRecommendRuleId") Long platformRecommendRuleId, @Param("ouId") Long ouId);
    
    /**
     * 根据规则编号查找推荐的月台按照优先级排序并且月台必须可用 
     *
     * @author mingwei.xie
     * @param platformRecommendRuleId
     * @param ouId
     * @return
     */
    RecommendPlatformCommand findCommandByRuleIdOrderByPriority(@Param("platformRecommendRuleId") Long platformRecommendRuleId, @Param("ouId") Long ouId);
}
