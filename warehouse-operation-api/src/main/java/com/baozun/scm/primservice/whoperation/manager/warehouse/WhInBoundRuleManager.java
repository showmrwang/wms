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
package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhInBoundRule;


public interface WhInBoundRuleManager extends BaseManager {

    List<WhInBoundRule> findBoundRulesList(Long ouid);

    /**
     * 通过参数查询入库分拣规则列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<WhInBoundRuleCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 根据id查找入库分拣规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    WhInBoundRuleCommand findWhInBoundRuleCommandById(Long id, Long ouId);

    /**
     * 通过规则名称和编号是否存在
     *
     * @author mingwei.xie
     * @param ouId
     * @param whInBoundRule
     * @return
     */
    Boolean checkUnique(WhInBoundRule whInBoundRule, Long ouId);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param ouId
     * @param whInBoundRule
     * @param originInventoryId
     * @return
     */
    WhInBoundRuleCommand testRuleSql(WhInBoundRule whInBoundRule, Long ouId, Long originInventoryId);


    /**
     * 新建/修改入库分拣规则
     *
     * @author mingwei.xie
     * @param ruleCommand
     * @param userId
     * @param ouId
     * @return
     */
    WhInBoundRule saveOrUpdate(WhInBoundRuleCommand ruleCommand, Long userId, Long ouId);

    /**
     * 启用/停用入库分拣规则
     *
     * @author mingwei.xie
     * @param ids
     * @param lifeCycle
     * @param userId
     * @param ouId
     * @return
     */
    void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId);


    /**
     * 根据待分拣的商品获取分拣条件值
     *
     * @author mingwei.xie
     * @param inventoryId
     * @param ouId
     * @param selectColumnsPropertyStr
     * @return
     */
    WhInBoundRuleResultCommand findResultConditionByInventoryId(Long inventoryId, Long ouId, String selectColumnsPropertyStr);


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
    List<WhInBoundRuleResultCommand> findResultConditionByContainerCode(String containerCode, Long ouId, String selectColumnsPropertyStr, String selectColumnsStr);

}
