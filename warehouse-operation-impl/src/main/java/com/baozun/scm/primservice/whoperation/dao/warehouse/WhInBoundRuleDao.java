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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleResultCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhInBoundRule;

public interface WhInBoundRuleDao extends BaseDao<WhInBoundRule, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhInBoundRule> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhInBoundRule> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhInBoundRule> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhInBoundRule o);

    @CommonQuery
    int saveOrUpdateByVersion(WhInBoundRule o);

    List<WhInBoundRule> findBoundRulesList(Long ouid);

    /**
     * 通过参数查询入库分拣规则分页列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhInBoundRuleCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 根据id查找入库分拣规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    WhInBoundRuleCommand findWhInBoundRuleCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 通过入库分拣规则名称和编号检验规则是否存在
     *
     * @author mingwei.xie
     * @param whInBoundRule
     * @return
     */
    long checkUnique(WhInBoundRule whInBoundRule);

    /**
     * 执行规则sql
     *
     * @author mingwei.xie
     * @param ouId
     * @param ruleSql
     * @param inventoryId
     * @return
     */
    Long executeRuleSql(@Param("ruleSql") String ruleSql, @Param("ouId") Long ouId, @Param("inventoryId") Long inventoryId);

    /**
     * 根据id查找入库分拣规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    WhInBoundRule findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 根据待分拣的商品获取分拣条件值
     *
     * @author mingwei.xie
     * @param inventoryId
     * @param ouId
     * @param selectColumnsPropertyStr
     * @return
     */
    WhInBoundRuleResultCommand findResultConditionByInventoryId(@Param("inventoryId") Long inventoryId, @Param("ouId") Long ouId, @Param("selectColumnsPropertyStr") String selectColumnsPropertyStr);

    /**
     * 根据待分拣的占用码获取分拣条件值列表
     *
     * @author mingwei.xie
     * @param containerCode
     * @param ouId
     * @param selectColumnsPropertyStr
     * @param selectColumnsStr
     * @return
     */
    List<WhInBoundRuleResultCommand> findResultConditionByContainerCode(@Param("containerCode") String containerCode, @Param("ouId") Long ouId, @Param("selectColumnsPropertyStr") String selectColumnsPropertyStr,
            @Param("selectColumnsStr") String selectColumnsStr);

    /**
     * 根据OUID查询所有可用入库分拣规则 并且按照priority排序
     *
     * @param ouid
     * @return
     */
    List<WhInBoundRuleCommand> findInboundRuleByOuId(Long ouid);

    /**
     * 测试分拣规则sql
     *
     * @author mingwei.xie
     * @param inventoryId
     * @param containerId
     * @param ruleSql
     * @return
     */
    Long executeSortingRuleSql(@Param("inventoryId") Long inventoryId, @Param("containerId") Long containerId, @Param("ruleSql") String ruleSql, @Param("ouId") Long ouId);

}
