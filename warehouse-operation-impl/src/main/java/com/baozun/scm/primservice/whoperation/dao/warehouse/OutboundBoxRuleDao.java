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

import com.baozun.scm.primservice.whoperation.command.warehouse.OutboundBoxRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutboundBoxRuleSplitRequireCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutboundBoxRule;

public interface OutboundBoxRuleDao extends BaseDao<OutboundBoxRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<OutboundBoxRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<OutboundBoxRule> query(Page page, Sort[] sorts, QueryCondition cond);

    List<OutboundBoxRule> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(OutboundBoxRule o);

    @CommonQuery
    int saveOrUpdateByVersion(OutboundBoxRule o);

    /**
     * 通过条件查询出库箱装箱规则分页数据
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findCountByQueryMapWithPageExt")
    Pagination<OutboundBoxRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找出库箱装箱规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    OutboundBoxRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 根据id查找出库箱装箱规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    OutboundBoxRuleCommand findCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 验证规则名称、编号、优先级是否唯一
     *
     * @author mingwei.xie
     * @param outboundBoxRuleCommand
     * @return
     */
    int checkUnique(OutboundBoxRuleCommand outboundBoxRuleCommand);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param ouId
     * @param outboundboxRuleSql
     * @return
     */
    List<Long> executeRuleSql(@Param("outboundboxRuleSql") String outboundboxRuleSql, @Param("ouId") Long ouId);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param ouId
     * @param odoId
     * @param outboundBoxTacticsSql
     * @return
     */
    List<OutboundBoxRuleSplitRequireCommand> executeOutboundBoxTacticsSql(@Param("outboundBoxTacticsSql") String outboundBoxTacticsSql, @Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * 获取所有可用的出库箱装箱规则，并按照优先级从高到低排序
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    List<OutboundBoxRuleCommand> findRuleByOuIdOrderByPriorityAsc(Long ouId);

}
