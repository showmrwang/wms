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

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentRule;

public interface ReplenishmentRuleDao extends BaseDao<ReplenishmentRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<ReplenishmentRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<ReplenishmentRule> query(Page page, Sort[] sorts, QueryCondition cond);

    List<ReplenishmentRule> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(ReplenishmentRule o);

    @CommonQuery
    int saveOrUpdateByVersion(ReplenishmentRule o);

    /**
     * 通过条件查询补货规则分页数据
     *
     * @author lichuan
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findCountByQueryMapWithPageExt")
    Pagination<ReplenishmentRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找补货规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    ReplenishmentRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 根据id查找补货规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    ReplenishmentRuleCommand findCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 验证规则名称、编号、优先级是否唯一
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @return
     */
    int checkUnique(ReplenishmentRuleCommand replenishmentRuleCommand);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param ouId
     * @param skuRuleSql
     * @return
     */
    List<Long> executeSkuRuleSql(@Param("skuRuleSql") String skuRuleSql, @Param("ouId") Long ouId);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param ouId
     * @param locationRuleSql
     * @return
     */
    List<Long> executeLocationRuleSql(@Param("locationRuleSql") String locationRuleSql, @Param("ouId") Long ouId);

    /**
     * 获取所有可用的补货规则，并按照优先级从高到低排序
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    List<ReplenishmentRuleCommand> findRuleByReplenishTypeOuIdOrderByPriorityAsc(@Param("orderReplenish") Boolean orderReplenish, @Param("realTimeReplenish") Boolean realTimeReplenish, @Param("waveReplenish") Boolean waveReplenish, @Param("ouId") Long ouId);

    /**
     * 查询补货工作释放及拆分条件分组
     *
     * @author qiming.liu
     * @param waveId
     * @param ouId
     * @return
     */
    List<ReplenishmentRuleCommand> getInReplenishmentConditionGroup(@Param("waveId") Long waveId, @Param("ouId") Long ouId);
    
    /**
     * 查询补货工作释放及拆分条件分组
     *
     * @author qiming.liu
     * @param ouId
     * @return
     */
    List<ReplenishmentRuleCommand> getOutReplenishmentConditionGroup(@Param("ouId") Long ouId);


}
