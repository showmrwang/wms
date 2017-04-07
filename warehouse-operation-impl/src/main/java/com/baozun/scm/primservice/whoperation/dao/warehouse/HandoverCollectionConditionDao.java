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

import com.baozun.scm.primservice.whoperation.command.warehouse.HandoverCollectionConditionCommand;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollectionCondition;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollectionRule;


public interface HandoverCollectionConditionDao extends BaseDao<HandoverCollectionCondition, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<HandoverCollectionCondition> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<HandoverCollectionCondition> query(Page page, Sort[] sorts, QueryCondition cond);

    List<HandoverCollectionCondition> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(HandoverCollectionCondition o);

    List<HandoverCollectionConditionCommand> findListByRuleConditionIdAndouId(@Param("ruleConditionId") Long ruleConditionId, @Param("ouId") Long ouId);

    int checkUnique(HandoverCollectionRule handoverCollectionRule);

    Long findListCountByRuleId(@Param("ruleConditionId") Long ruleConditionId, @Param("ouId") Long ouId);

    int deleteByRuleId(@Param("ruleConditionId") Long ruleConditionId, @Param("ouId") Long ouId);

}
