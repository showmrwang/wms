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

import com.baozun.scm.primservice.whoperation.command.warehouse.PlatformRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.PlatformRecommendRule;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface PlatformRecommendRuleDao extends BaseDao<PlatformRecommendRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<PlatformRecommendRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<PlatformRecommendRule> query(Page page, Sort[] sorts, QueryCondition cond);

    List<PlatformRecommendRule> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(PlatformRecommendRule o);

    @CommonQuery
    int saveOrUpdateByVersion(PlatformRecommendRule o);

    /**
     * 通过参数查询月台分页列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<PlatformRecommendRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 通过月台名称和编号检验月台是否存在
     *
     * @author mingwei.xie
     * @param platformRecommendRule
     * @return
     */
    long checkUnique(PlatformRecommendRule platformRecommendRule);

    /**
     * 执行规则sql
     *
     * @author mingwei.xie
     * @param ouId
     * @param ruleSql
     * @return
     */
    String executeRuleSql(@Param("ruleSql") String ruleSql, @Param("ouId") Long ouId, @Param("asnReserveCode") String asnReserveCode);

    /**
     * 根据id查找月台推荐规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    PlatformRecommendRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 根据id查找月台推荐规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    PlatformRecommendRuleCommand findCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 根据仓库ID查询所有可用的月台规则并且按照优先级排序
     * 
     * @param ouid
     * @return
     */
    List<PlatformRecommendRuleCommand> findPlatformRecommendRuleByOuId(Long ouid);
}
