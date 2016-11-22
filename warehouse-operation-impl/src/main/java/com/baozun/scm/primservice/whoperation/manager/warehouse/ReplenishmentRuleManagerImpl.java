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

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentStrategyCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentStrategyDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentStrategy;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("replenishmentRuleManager")
@Transactional
public class ReplenishmentRuleManagerImpl extends BaseManagerImpl implements ReplenishmentRuleManager {
    public static final Logger log = LoggerFactory.getLogger(ReplenishmentRuleManagerImpl.class);

    @Autowired
    private ReplenishmentRuleDao replenishmentRuleDao;

    @Autowired
    private ReplenishmentStrategyDao replenishmentStrategyDao;

    /**
     * 通过参数查询补货规则分页列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public Pagination<ReplenishmentRuleCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<ReplenishmentRuleCommand> pagination = replenishmentRuleDao.findListByQueryMapWithPageExt(page, sorts, params);
        return pagination;
    }

    /**
     * 根据id查找补货规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public ReplenishmentRule findReplenishmentRuleById(Long id, Long ouId) {
        ReplenishmentRule replenishmentRule = replenishmentRuleDao.findByIdExt(id, ouId);
        return replenishmentRule;
    }

    /**
     * 根据id查找补货规则
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public ReplenishmentRuleCommand findReplenishmentRuleCommandById(Long id, Long ouId) {
        ReplenishmentRuleCommand replenishmentRuleCommand = replenishmentRuleDao.findCommandById(id, ouId);
        log.info("ReplenishmentRuleManagerImpl.findReplenishmentRuleCommandById replenishmentStrategyDao.findCommandByReplenishmentRuleId, id is:[{}], ouId is:[{}]", id, ouId);
        List<ReplenishmentStrategyCommand> replenishmentStrategyCommandList = replenishmentStrategyDao.findCommandByRuleId(id, ouId);
        replenishmentRuleCommand.setReplenishmentStrategyCommandList(replenishmentStrategyCommandList);
        return replenishmentRuleCommand;
    }

    /**
     * 验证规则名称、编号、优先级是否唯一
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public Boolean checkUnique(ReplenishmentRuleCommand replenishmentRuleCommand, Long ouId) {
        replenishmentRuleCommand.setOuId(ouId);
        int count = replenishmentRuleDao.checkUnique(replenishmentRuleCommand);
        boolean isUnique = true;
        if(count > 0){
            isUnique = false;
        }
        return isUnique;
    }

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public ReplenishmentRuleCommand testSkuRuleSql(ReplenishmentRuleCommand replenishmentRuleCommand, List<Long> skuIdList, Long ouId) {
        ReplenishmentRuleCommand command = new ReplenishmentRuleCommand();
        boolean testResult = true;
        List<Long> matchSkuIdList = null;

        String skuIdListStr = StringUtil.listToStringWithoutBrackets(skuIdList, ',');

        try {
            matchSkuIdList = replenishmentRuleDao.executeSkuRuleSql(replenishmentRuleCommand.getSkuRuleSql().replace(Constants.REOLENISHMENT_RULE_SKUID_LIST_PLACEHOLDER, skuIdListStr), ouId);
        } catch (Exception e) {
            testResult = false;
            e.printStackTrace();
        }
        command.setSkuIdList(matchSkuIdList);
        command.setTestResult(testResult);
        return command;
    }

    /**
     * 检查规则是否可用
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public ReplenishmentRuleCommand testLocationRuleSql(ReplenishmentRuleCommand replenishmentRuleCommand, List<Long> locationIdList, Long ouId) {
        ReplenishmentRuleCommand command = new ReplenishmentRuleCommand();
        boolean testResult = true;
        List<Long> matchLocationIdList = null;

        String locationIdListStr = StringUtil.listToStringWithoutBrackets(locationIdList, ',');
        
        try {
            matchLocationIdList = replenishmentRuleDao.executeLocationRuleSql(replenishmentRuleCommand.getLocationRuleSql().replace(Constants.REOLENISHMENT_RULE_LOCATIONID_LIST_PLACEHOLDER, locationIdListStr), ouId);
        } catch (Exception e) {
            testResult = false;
            e.printStackTrace();
        }
        command.setLocationIdList(matchLocationIdList);
        command.setTestResult(testResult);
        return command;
    }

    /**
     * 新建/修改补货规则
     *
     * @author mingwei.xie
     * @param replenishmentRuleCommand
     * @param userId
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public ReplenishmentRule saveOrUpdate(ReplenishmentRuleCommand replenishmentRuleCommand, Long userId, Long ouId) {
        ReplenishmentRule updateRule = new ReplenishmentRule();
        if (null != replenishmentRuleCommand.getId()) {
            ReplenishmentRule originRule = replenishmentRuleDao.findByIdExt(replenishmentRuleCommand.getId(), ouId);
            if (null == originRule) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originRule.setReplenishmentRuleCode(replenishmentRuleCommand.getReplenishmentRuleCode());
            originRule.setReplenishmentRuleName(replenishmentRuleCommand.getReplenishmentRuleName());
            originRule.setDescription(replenishmentRuleCommand.getDescription());
            originRule.setPriority(replenishmentRuleCommand.getPriority());
            originRule.setLifecycle(replenishmentRuleCommand.getLifecycle());
            originRule.setOrderReplenish(replenishmentRuleCommand.getOrderReplenish());
            originRule.setRealTimeReplenish(replenishmentRuleCommand.getRealTimeReplenish());
            originRule.setWaveReplenish(replenishmentRuleCommand.getWaveReplenish());
            originRule.setSkuRule(replenishmentRuleCommand.getSkuRule());
            originRule.setSkuRuleSql(replenishmentRuleCommand.getSkuRuleSql());
            originRule.setLocationRule(replenishmentRuleCommand.getLocationRule());
            originRule.setLocationRuleSql(replenishmentRuleCommand.getLocationRuleSql());
            originRule.setModifiedId(userId);

            int updateCount = replenishmentRuleDao.saveOrUpdateByVersion(originRule);
            if (1 != updateCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            updateRule = originRule;
        } else {
            updateRule.setOuId(ouId);
            updateRule.setReplenishmentRuleCode(replenishmentRuleCommand.getReplenishmentRuleCode());
            updateRule.setReplenishmentRuleName(replenishmentRuleCommand.getReplenishmentRuleName());
            updateRule.setDescription(replenishmentRuleCommand.getDescription());
            updateRule.setPriority(replenishmentRuleCommand.getPriority());
            updateRule.setLifecycle(replenishmentRuleCommand.getLifecycle());
            updateRule.setOrderReplenish(replenishmentRuleCommand.getOrderReplenish());
            updateRule.setRealTimeReplenish(replenishmentRuleCommand.getRealTimeReplenish());
            updateRule.setWaveReplenish(replenishmentRuleCommand.getWaveReplenish());
            updateRule.setSkuRule(replenishmentRuleCommand.getSkuRule());
            updateRule.setSkuRuleSql(replenishmentRuleCommand.getSkuRuleSql());
            updateRule.setLocationRule(replenishmentRuleCommand.getLocationRule());
            updateRule.setLocationRuleSql(replenishmentRuleCommand.getLocationRuleSql());
            updateRule.setCreateTime(new Date());
            updateRule.setCreatedId(userId);
            updateRule.setLastModifyTime(new Date());
            updateRule.setModifiedId(userId);

            replenishmentRuleDao.insert(updateRule);
        }

        ReplenishmentRule returnRule = replenishmentRuleDao.findByIdExt(updateRule.getId(), ouId);
        if (null == returnRule) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }

        if (null != replenishmentRuleCommand.getId()) {
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, returnRule, ouId, userId, null, null);
        } else {
            insertGlobalLog(Constants.GLOBAL_LOG_INSERT, returnRule, ouId, userId, null, null);
        }

        if (null != replenishmentRuleCommand.getId()) {
            Long originCount = replenishmentStrategyDao.findListCountByRuleId(replenishmentRuleCommand.getId(), ouId);
            int deleteCount = replenishmentStrategyDao.deleteByRuleId(replenishmentRuleCommand.getId(), ouId);
            if (originCount != deleteCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        this.saveReplenishmentStrategy(replenishmentRuleCommand.getReplenishmentStrategyCommandList(), returnRule.getId(), userId, ouId);
        return updateRule;
    }

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
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId) {
        for (Long id : ids) {
            ReplenishmentRule originRule = replenishmentRuleDao.findByIdExt(id, ouId);
            if (null == originRule) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originRule.setLifecycle(lifeCycle);
            originRule.setModifiedId(userId);

            int updateCount = replenishmentRuleDao.saveOrUpdateByVersion(originRule);
            if (1 != updateCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            ReplenishmentRule returnRule = replenishmentRuleDao.findByIdExt(originRule.getId(), ouId);
            if (null == returnRule) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, returnRule, ouId, userId, null, null);
        }
    }


    private void saveReplenishmentStrategy(List<ReplenishmentStrategyCommand> replenishmentStrategyCommandList, Long ruleId, Long userId, Long ouId) {
        for (ReplenishmentStrategyCommand replenishmentStrategyCommand : replenishmentStrategyCommandList) {
            ReplenishmentStrategy replenishmentStrategy = new ReplenishmentStrategy();
            replenishmentStrategy.setReplenishmentRuleId(ruleId);
            replenishmentStrategy.setAreaId(replenishmentStrategyCommand.getAreaId());
            replenishmentStrategy.setStrategyCode(replenishmentStrategyCommand.getStrategyCode());
            replenishmentStrategy.setAllocateUnitCodes(replenishmentStrategyCommand.getAllocateUnitCodes());
            replenishmentStrategy.setReplenishmentCode(replenishmentStrategyCommand.getReplenishmentCode());
            replenishmentStrategy.setPriority(replenishmentStrategyCommand.getPriority());
            replenishmentStrategy.setOuId(ouId);

            replenishmentStrategyDao.insert(replenishmentStrategy);

            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, replenishmentStrategy, ouId, userId, null, null);
        }
    }

	@Override
	public List<ReplenishmentStrategyCommand> getReplenishmentStrategyCommandByRuleId(Long ruleId, Long ouId) {
		return replenishmentStrategyDao.findCommandByRuleId(ruleId, ouId);
	}
}
