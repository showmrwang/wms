/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.HandoverCollectionConditionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.HandoverCollectionRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.handover.WhHandoverStationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.HandoverCollectionConditionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.HandoverCollectionRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutboundBoxRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutboundBoxSortDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollectionCondition;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollectionRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhHandoverStation;


@Service("handoverCollectionRuleManager")
@Transactional
public class HandoverCollectionRuleManagerImpl extends BaseManagerImpl implements HandoverCollectionRuleManager {
    public static final Logger log = LoggerFactory.getLogger(HandoverCollectionRuleManagerImpl.class);

    @Autowired
    private HandoverCollectionRuleDao handoverCollectionRuleDao;
    @Autowired
    private HandoverCollectionConditionDao handoverCollectionConditionDao;
    @Autowired
    private WhHandoverStationDao whHandoverStationDao;
    @Autowired
    private OutboundBoxRuleDao outboundBoxRuleDao;
    @Autowired
    private OutboundBoxSortDao outboundBoxSortDao;


    /**
     * 通过参数查询集货交接规则分页列表
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public Pagination<HandoverCollectionRule> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<HandoverCollectionRule> pagination = handoverCollectionRuleDao.findListByQueryMapWithPageExt(page, sorts, params);
        return pagination;
    }

    /**
     * 根据id查找出集货交接规则
     * 
     * @param id
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public HandoverCollectionRuleCommand findHandoverCollectionRuleById(Long id, Long ouId) {
        HandoverCollectionRuleCommand handoverCollectionRuleCommand = handoverCollectionRuleDao.findByIdExt(id, ouId);
        List<HandoverCollectionConditionCommand> list = handoverCollectionConditionDao.findListByRuleConditionIdAndouId(id, ouId);
        handoverCollectionRuleCommand.setHandoverCollectionConditionCommandList(list);
        return handoverCollectionRuleCommand;
    }

    /**
     * 查询所有交接工位
     * 
     * @param id
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public List<WhHandoverStation> findhandoverStationList() {
        return whHandoverStationDao.findListByParam(null);
    }

    /**
     * 验证规则名称、编号、优先级是否唯一
     * 
     * @param outboundboxRuleCommand
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public Boolean checkUnique(HandoverCollectionRule handoverCollectionRule, Long ouId) {
        handoverCollectionRule.setOuId(ouId);
        int count = handoverCollectionRuleDao.checkUnique(handoverCollectionRule);
        boolean isUnique = true;
        if (count > 0) {
            isUnique = false;
        }
        return isUnique;
    }

    /**
     * 启用/停用集货交接规则
     * 
     * @param ids
     * @param lifeCycle
     * @param userId
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId) {
        for (Long id : ids) {
            HandoverCollectionRule originRule = handoverCollectionRuleDao.findByIdAndOuId(id, ouId);
            if (null == originRule) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originRule.setLifecycle(lifeCycle);
            // originRule.setModifiedId(userId);

            int updateCount = handoverCollectionRuleDao.UpdatelifeCycle(id, lifeCycle);
            if (1 != updateCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            HandoverCollectionRule findByIdExt = handoverCollectionRuleDao.findByIdAndOuId(originRule.getId(), ouId);
            if (null == findByIdExt) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, findByIdExt, ouId, userId, null, null);
        }
    }



    /**
     * 新建/修改出库箱装箱规则
     * 
     * @param ruleCommand
     * @param userId
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public HandoverCollectionRule saveOrUpdate(HandoverCollectionRule handoverCollectionRule, Long[] sysRecListArray, Long userId, Long ouId) {
        HandoverCollectionRule updateRule = new HandoverCollectionRule();
        if (null != handoverCollectionRule.getId()) {
            // 更新
            HandoverCollectionRule originRule = handoverCollectionRuleDao.findByIdAndOuId(handoverCollectionRule.getId(), ouId);
            if (null == originRule) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originRule.setRuleName(handoverCollectionRule.getRuleName());
            originRule.setRule(handoverCollectionRule.getRule());
            originRule.setRuleSql(handoverCollectionRule.getRuleSql());
            originRule.setApplyType(handoverCollectionRule.getApplyType());
            originRule.setRuleType(handoverCollectionRule.getRuleType());
            originRule.setHandoverStationId(handoverCollectionRule.getHandoverStationId());
            originRule.setPriority(handoverCollectionRule.getPriority());
            originRule.setDescription(handoverCollectionRule.getDescription());
            originRule.setLifecycle(handoverCollectionRule.getLifecycle());
            originRule.setModifiedId(userId);
            originRule.setLastModifyTime(new Date());

            int updateCount = handoverCollectionRuleDao.saveOrUpdate(originRule);
            if (1 != updateCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            updateRule = originRule;
        } else {
            // 添加
            updateRule.setOuId(ouId);
            updateRule.setRuleName(handoverCollectionRule.getRuleName());
            updateRule.setRuleCode(handoverCollectionRule.getRuleCode());
            updateRule.setRule(handoverCollectionRule.getRule());
            updateRule.setRuleSql(handoverCollectionRule.getRuleSql());
            updateRule.setApplyType(handoverCollectionRule.getApplyType());
            updateRule.setRuleType(handoverCollectionRule.getRuleType());
            updateRule.setHandoverStationId(handoverCollectionRule.getHandoverStationId());
            updateRule.setPriority(handoverCollectionRule.getPriority());
            updateRule.setPriority(handoverCollectionRule.getPriority());
            updateRule.setLifecycle(handoverCollectionRule.getLifecycle());
            updateRule.setCreateTime(new Date());
            updateRule.setCreatedId(userId);
            updateRule.setLastModifyTime(new Date());

            handoverCollectionRuleDao.insert(updateRule);

        }

        HandoverCollectionRule returnRule = handoverCollectionRuleDao.findByIdAndOuId(updateRule.getId(), ouId);
        if (null == returnRule) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }



        if (null != handoverCollectionRule.getId()) {
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, returnRule, ouId, userId, null, null);
        } else {
            insertGlobalLog(Constants.GLOBAL_LOG_INSERT, returnRule, ouId, userId, null, null);
        }
        if (null != handoverCollectionRule.getId()) {
            Long originCount = handoverCollectionConditionDao.findListCountByRuleId(handoverCollectionRule.getId(), ouId);
            int deleteCount = handoverCollectionConditionDao.deleteByRuleId(handoverCollectionRule.getId(), ouId);
            if (originCount != deleteCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        this.saveOutboundBoxSort(sysRecListArray, returnRule.getId(), userId, ouId);

        return updateRule;
    }

    private void saveOutboundBoxSort(Long[] sysRecListArray, Long ruleId, Long userId, Long ouId) {
        List<Long> sysRecIdList = new ArrayList<>();
        if (sysRecListArray.length > 0) {
            sysRecIdList = Arrays.asList(sysRecListArray);
        }
        for (Long RuleConditionId : sysRecIdList) {
            HandoverCollectionCondition handoverCollectionCondition = new HandoverCollectionCondition();
            handoverCollectionCondition.setChrId(ruleId);
            handoverCollectionCondition.setOuId(ouId);
            handoverCollectionCondition.setRuleConditionId(RuleConditionId);
            handoverCollectionConditionDao.insert(handoverCollectionCondition);

            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, handoverCollectionCondition, ouId, userId, null, null);
        }
    }

    /**
     * 检查规则是否可用
     * 
     * @param handoverCollectionRuleCommand
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public HandoverCollectionRuleCommand testRuleSql(HandoverCollectionRuleCommand handoverCollectionRuleCommand, String outboundBoxCode, Long ouId) {
        HandoverCollectionRuleCommand command = new HandoverCollectionRuleCommand();
        boolean testResult = true;
        String aaString = "'";
        outboundBoxCode = aaString + outboundBoxCode + aaString;
        List<Long> outboundboxIdList = new ArrayList<>();
        try {
            outboundboxIdList = handoverCollectionRuleDao.executeRuleSql(handoverCollectionRuleCommand.getRuleSql().replace(Constants.HANDOVER_COLLECTION_RULE_PALCEHOLDER, outboundBoxCode), ouId);
        } catch (Exception e) {
            log.error("OutboundBoxRuleManagerImpl testRuleSql failed, param outboundboxRuleCommand is:[{}], odoIdList is:[{}], ouId is:[{}]", handoverCollectionRuleCommand, outboundBoxCode, ouId);
            testResult = false;
            e.printStackTrace();
        }
        command.setOutboundboxIdList(outboundboxIdList);
        command.setTestResult(testResult);
        return command;
    }

    @Override
    public List<HandoverCollectionRuleCommand> findhandoverCollectionRuleList(Long ouId) {
        return handoverCollectionRuleDao.findAllHandoverCollectionRules(ouId);
    }


}
