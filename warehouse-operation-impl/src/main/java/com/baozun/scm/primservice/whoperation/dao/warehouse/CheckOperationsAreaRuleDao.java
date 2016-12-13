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

import com.baozun.scm.primservice.whoperation.command.warehouse.CheckOperationsAreaRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.CheckOperationsAreaRule;

public interface CheckOperationsAreaRuleDao extends BaseDao<CheckOperationsAreaRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<CheckOperationsAreaRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<CheckOperationsAreaRule> query(Page page, Sort[] sorts, QueryCondition cond);

    List<CheckOperationsAreaRule> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(CheckOperationsAreaRule o);

    @CommonQuery
    int saveOrUpdateByVersion(CheckOperationsAreaRule o);

    /**
     * 通过条件查询复核台推荐规则分页数据
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findCountByQueryMapWithPageExt")
    Pagination<CheckOperationsAreaRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找复核台推荐规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    CheckOperationsAreaRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 根据id查找复核台推荐规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    CheckOperationsAreaRuleCommand findCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 验证规则名称、编号、优先级是否唯一
     *
     * @author mingwei.xie
     * @param checkOperationsAreaRuleCommand
     * @return
     */
    int checkUnique(CheckOperationsAreaRuleCommand checkOperationsAreaRuleCommand);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param ouId
     * @param ruleSql
     * @return
     */
    List<Long> executeRuleSql(@Param("ruleSql") String ruleSql, @Param("ouId") Long ouId);

    /**
     * 获取所有可用的复核台推荐规则，并按照优先级从高到低排序
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    List<CheckOperationsAreaRuleCommand> findRuleByOuIdOrderByPriorityAsc(Long ouId);
}
