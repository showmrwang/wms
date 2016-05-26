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

import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendRuleConditionCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.RecommendRuleCondition;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface RecommendRuleConditionDao extends BaseDao<RecommendRuleCondition, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<RecommendRuleCondition> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<RecommendRuleCondition> query(Page page, Sort[] sorts, QueryCondition cond);

    List<RecommendRuleCondition> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(RecommendRuleCondition o);

    /**
     * 根据规则类型获取所有条件
     *
     * @author mingwei.xie
     * @param ruleType
     * @return
     */
    public List<RecommendRuleConditionCommand> getConditionListByRuleType(@Param("ruleType") String ruleType);

    /**
     * 根据id获取条件，用于组成分拣规则结果的查询列
     *
     * @author mingwei.xie
     * @param ids
     * @return
     */
    public List<RecommendRuleConditionCommand> getSelectColumnsByConditionIds(List<Long> ids);

}
