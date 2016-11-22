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

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentStrategyCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentRule;

public interface ReplenishmentRuleManager extends BaseManager {


    /**
     * 通过参数查询补货规则分页列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<ReplenishmentRuleCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找补货规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    ReplenishmentRule findReplenishmentRuleById(Long id, Long ouId);


    /**
     * 根据id查找补货规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    ReplenishmentRuleCommand findReplenishmentRuleCommandById(Long id, Long ouId);

    /**
     * 验证规则名称、编号、优先级是否唯一
     *
     * @author mingwei.xie
     * @param ouId
     * @param replenishmentRuleCommand
     * @return
     */
    Boolean checkUnique(ReplenishmentRuleCommand replenishmentRuleCommand, Long ouId);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param skuIdList
     * @param ouId
     * @return
     */
    ReplenishmentRuleCommand testSkuRuleSql(ReplenishmentRuleCommand replenishmentRuleCommand, List<Long> skuIdList, Long ouId);

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param locationIdList
     * @param ouId
     * @return
     */
    ReplenishmentRuleCommand testLocationRuleSql(ReplenishmentRuleCommand replenishmentRuleCommand, List<Long> locationIdList, Long ouId);

    /**
     * 新建/修改补货规则
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param userId
     * @param ouId
     * @return
     */
    ReplenishmentRule saveOrUpdate(ReplenishmentRuleCommand replenishmentRuleCommand, Long userId, Long ouId);

    /**
     * 启用/停用补货规则
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
     * 通过规则找到策略
     * @param ruleId
     * @param ouId
     * @return
     */
    List<ReplenishmentStrategyCommand> getReplenishmentStrategyCommandByRuleId(Long ruleId, Long ouId);
}
