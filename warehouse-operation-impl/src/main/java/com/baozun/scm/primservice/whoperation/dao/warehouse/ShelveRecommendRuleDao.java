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

import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.ShelveRecommendRule;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface ShelveRecommendRuleDao extends BaseDao<ShelveRecommendRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<ShelveRecommendRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<ShelveRecommendRule> query(Page page, Sort[] sorts, QueryCondition cond);

    List<ShelveRecommendRule> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(ShelveRecommendRule o);

    @CommonQuery
    int saveOrUpdateByVersion(ShelveRecommendRule o);

    /**
     * 通过条件查询上架推荐规则分页数据
     * 
     * @author lichuan
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findCountByQueryMapWithPageExt")
    Pagination<ShelveRecommendRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找上架规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    ShelveRecommendRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 根据id查找上架规则
     * 
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    ShelveRecommendRuleCommand findCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 验证规则名称、编号、优先级是否唯一
     * 
     * @author mingwei.xie
     * @param shelveRecommendRuleCommand
     * @return
     */
    Long checkUnique(ShelveRecommendRuleCommand shelveRecommendRuleCommand);

    /**
     * 检查规则是否可用
     * 
     * @author mingwei.xie
     * @param ouId
     * @param ruleSql
     * @return
     */
    List<String> executeRuleSql(@Param("ruleSql") String ruleSql, @Param("ouId") Long ouId);

    /***
     * 根据OUID查询所有可用上架规则 并且按照priority排序
     * 
     * @param ouid
     * @return
     */
    List<ShelveRecommendRuleCommand> findShelveRecommendRuleByOuid(Long ouid);
}
