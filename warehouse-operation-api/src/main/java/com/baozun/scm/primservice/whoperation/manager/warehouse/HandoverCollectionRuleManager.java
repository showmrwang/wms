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

import com.baozun.scm.primservice.whoperation.command.warehouse.HandoverCollectionRuleCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollectionRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhHandoverStation;


public interface HandoverCollectionRuleManager extends BaseManager {


    /**
     * 通过参数查询集货交接规则分页列表
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<HandoverCollectionRule> getListByParams(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * 根据id查找集货交接规则
     * 
     * @param id
     * @param ouId
     * @return
     */
    HandoverCollectionRuleCommand findHandoverCollectionRuleById(Long id, Long ouId);



    /**
     * 验证规则名称、编号、优先级是否唯一
     * 
     * @param ouId
     * @param handoverCollectionRule
     * @return
     */
    Boolean checkUnique(HandoverCollectionRule handoverCollectionRule, Long ouId);


    /**
     * 新建/修改集货交接
     * 
     * @param handoverCollectionRule
     * @param userId
     * @param ouId
     * @return
     */
    HandoverCollectionRule saveOrUpdate(HandoverCollectionRule handoverCollectionRule, Long[] sysRecListArray, Long userId, Long ouId);

    /**
     * 启用/停用出库箱装箱规则
     * 
     * @param ids
     * @param lifeCycle
     * @param userId
     * @param ouId
     * @return
     */
    void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId);

    /**
     * 找出所有交接工位
     * 
     * @return
     */
    List<WhHandoverStation> findhandoverStationList();


    /**
     * 检查规则是否可用
     * 
     * @param handoverCollectionRuleCommand
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
    HandoverCollectionRuleCommand testRuleSql(HandoverCollectionRuleCommand handoverCollectionRuleCommand, String outboundBoxCode, Long ouId);


    /**
     * 找出所有规则
     * 
     * @param ouId
     * 
     * @return
     */
    List<HandoverCollectionRuleCommand> findhandoverCollectionRuleList(Long ouId);



}
