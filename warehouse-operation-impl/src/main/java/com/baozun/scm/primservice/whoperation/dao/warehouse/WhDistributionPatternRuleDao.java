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
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;



public interface WhDistributionPatternRuleDao extends BaseDao<WhDistributionPatternRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhDistributionPatternRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhDistributionPatternRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhDistributionPatternRule o);

    @CommonQuery
    int saveOrUpdateByVersion(WhDistributionPatternRule o);

    /**
     * 根据id查找配货模式规则
     *
     * @author qiming.liu
     * @param id
     * @param ouId
     * @return
     */
    WhDistributionPatternRuleCommand findDistributionPatternRuleCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 根据id查找配货模式规则
     *
     * @author qiming.liu
     * @param id
     * @param ouId
     * @return
     */
    WhDistributionPatternRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 删除配货模式规则
     *
     * @author qiming.liu
     * @param id
     * @param ouId
     * @return
     */
    public int deleteById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
    * 校验配货模式规则
    *
    * @param ouId
    * @param ruleSql
    * @param waveId
    * @return
    */
    public List<Long> testRuleSql(@Param("ruleSql") String ruleSql, @Param("ouId") Long ouId, @Param("waveId") Long waveId);

    /**
     * 检查规则名称或编码是否唯一
     *
     * @author qiming.liu
     * @param distributionPatternRule
     * @return
     */
    long checkUnique(WhDistributionPatternRule distributionPatternRule);

    /**
     * 获取所有可用的配货模式规则，并按照优先级从高到低排序
     *
     * @author qiming.liu
     * @param ouId
     * @return
     */
    List<WhDistributionPatternRuleCommand> findRuleByOuIdOrderByPriorityAsc(@Param("ouId") Long ouId);

    /**
     * 根据code查找配货模式规则
     *
     * @author qiming.liu
     * @param distributionPatternCode
     * @param ouId
     * @return
     */
    WhDistributionPatternRuleCommand findRuleByCode(@Param("distributionPatternCode") String distributionPatternCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 通过出库单id和组织id查找配货模式
     * @param odoId
     * @param ouId
     * @return
     */
    WhDistributionPatternRule findByOdoIdAndOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

}
