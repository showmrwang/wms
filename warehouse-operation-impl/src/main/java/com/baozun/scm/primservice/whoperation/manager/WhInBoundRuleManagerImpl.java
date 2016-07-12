package com.baozun.scm.primservice.whoperation.manager;

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

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhInBoundRuleDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhInBoundRuleManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhInBoundRule;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

@Service("whInBoundRuleManager")
@Transactional
public class WhInBoundRuleManagerImpl implements WhInBoundRuleManager {
    public static final Logger log = LoggerFactory.getLogger(WhInBoundRuleManagerImpl.class);

    @Autowired
    private WhInBoundRuleDao whInBoundRuleDao;

    @Autowired
    private GlobalLogManager globalLogManager;

    /**
     * 获取状态为可用的入库分拣规则列表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhInBoundRule> findBoundRulesList(Long ouid) {
        return whInBoundRuleDao.findBoundRulesList(ouid);
    }

    /**
     * 通过参数查询入库分拣规则列表
     *
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhInBoundRuleCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl getListByParams is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("getListByParams param [page:{}, sorts:{}, param:{}] ", ParamsUtil.page2String(page), ParamsUtil.sorts2String(sorts), param);
        }
        Pagination<WhInBoundRuleCommand> pagination = whInBoundRuleDao.findListByQueryMapWithPageExt(page, sorts, param);
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl getListByParams is end");
        }
        return pagination;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhInBoundRuleCommand findWhInBoundRuleCommandById(Long id, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl findWhInBoundRuleCommandById is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findWhInBoundRuleCommandById param [id:{}, ouId:{}] ", id, ouId);
        }
        WhInBoundRuleCommand whInBoundRuleCommand = whInBoundRuleDao.findWhInBoundRuleCommandById(id, ouId);
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl findWhInBoundRuleCommandById is end");
        }
        return whInBoundRuleCommand;
    }

    /**
     * 验证规则名称或编码、优先级是否唯一
     *
     * @author mingwei.xie
     * @param ouId
     * @param whInBoundRule
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkUnique(WhInBoundRule whInBoundRule, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl checkUnique is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("checkUnique param [whInBoundRule:{}, ouId:{}] ", whInBoundRule, ouId);
        }
        if (null == whInBoundRule || null == ouId) {
            log.error("WhInBoundRuleManagerImpl checkUnique failed, param is  null, param [whInBoundRule:{}, ouId:{}] ", whInBoundRule, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        whInBoundRule.setOuId(ouId);
        long count = whInBoundRuleDao.checkUnique(whInBoundRule);
        boolean result = true;
        if (0 != count) {
            result = false;
        }
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl checkUnique is end");
        }
        return result;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhInBoundRuleCommand testRuleSql(WhInBoundRule whInBoundRule, Long ouId, Long originInventoryId) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl testRuleSql is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("testRuleSql param [whInBoundRule:{}, ouId:{], originInventoryId:{} ", whInBoundRule, ouId, originInventoryId);
        }
        if (null == whInBoundRule || null == whInBoundRule.getRuleSql() || null == ouId || null == originInventoryId) {
            log.error("WhInBoundRuleManagerImpl testRuleSql failed, param is null, param [whInBoundRule:{}, ouId:{], originInventoryId:{} ", whInBoundRule, ouId, originInventoryId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        WhInBoundRuleCommand command = new WhInBoundRuleCommand();
        Long resultInventoryId = null;
        Boolean testResult = true;
        try {
            resultInventoryId = whInBoundRuleDao.executeRuleSql(whInBoundRule.getRuleSql(), ouId, originInventoryId);
        } catch (Exception e) {
            log.error("WhInBoundRuleManagerImpl testRuleSql failed, param [whInBoundRule:{}, ouId:{}, originInventoryId:{}, exception:{}]", whInBoundRule, ouId, originInventoryId, e.getMessage());
            testResult = false;
        }
        command.setInventoryId(resultInventoryId);
        command.setRuleSqlTestResult(testResult);
        if (log.isDebugEnabled()) {
            log.debug("testRuleSql result, param [whInBoundRule:{}, ouId:{}, originInventoryId:{}, resultInventoryId:{}]", whInBoundRule, ouId, originInventoryId, resultInventoryId);
        }
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl testRuleSql is end");
        }
        return command;
    }

    /**
     * 新建/修改入库分拣规则
     *
     * @author mingwei.xie
     * @param ruleCommand
     * @param userId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhInBoundRule saveOrUpdate(WhInBoundRuleCommand ruleCommand, Long userId, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl saveOrUpdate is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("saveOrUpdate param [ruleCommand:{}, userId:{}, ouId:{}] ", ruleCommand, userId, ouId);
        }
        if (null == ruleCommand || null == userId || null == ouId) {
            log.error("WhInBoundRuleManagerImpl saveOrUpdate failed, param is null, param [ruleCommand:{}, userId:{}, ouId:{}] ", ruleCommand, userId, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }

        WhInBoundRule updateRule = new WhInBoundRule();
        if (null != ruleCommand.getId()) {
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate, originRuleId  is exists,update to sharedDB, param [ruleCommand:{}, userId:{}, ouId:{}] ", ruleCommand, userId, ouId);
            }
            WhInBoundRule originalRule = whInBoundRuleDao.findByIdExt(ruleCommand.getId(), ouId);
            if (null == originalRule) {
                log.error("WhInBoundRuleManagerImpl saveOrUpdate failed, originalRule is null, param [ruleCommand:{}, userId:{}, ouId:{}] ", ruleCommand, userId, ouId);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originalRule.setInboundRuleCode(ruleCommand.getInboundRuleCode());
            originalRule.setInboundRuleName(ruleCommand.getInboundRuleName());
            originalRule.setDescription(ruleCommand.getDescription());
            originalRule.setPriority(ruleCommand.getPriority());
            originalRule.setRule(ruleCommand.getRule());
            originalRule.setRuleSql(ruleCommand.getRuleSql());
            originalRule.setSortingConditionIds(ruleCommand.getSortingConditionIds());
            originalRule.setSortingSql(ruleCommand.getSortingSql());
            originalRule.setLifecycle(ruleCommand.getLifecycle());
            originalRule.setModifiedId(userId);
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate,update to sharedDB, param [ruleCommand:{}, userId:{}, ouId:{}, originRule:{}] ", ruleCommand, userId, ouId, originalRule);
            }
            int count = whInBoundRuleDao.saveOrUpdateByVersion(originalRule);
            if (1 != count) {
                log.error("WhInBoundRuleManagerImpl saveOrUpdate failed,updateCount != 1, param [ruleCommand:{}, userId:{}, ouId:{}, originalRule:{}, count:{}] ", ruleCommand, userId, ouId, originalRule, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            updateRule = originalRule;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate, originalRule  is null,insert to sharedDB, param [ruleCommand:{}, userId:{}, ouId:{}] ", ruleCommand, userId, ouId);
            }
            updateRule.setOuId(ouId);
            updateRule.setInboundRuleName(ruleCommand.getInboundRuleName());
            updateRule.setInboundRuleCode(ruleCommand.getInboundRuleCode());
            updateRule.setPriority(ruleCommand.getPriority());
            updateRule.setDescription(ruleCommand.getDescription());
            updateRule.setLifecycle(ruleCommand.getLifecycle());
            updateRule.setRule(ruleCommand.getRule());
            updateRule.setRuleSql(ruleCommand.getRuleSql());
            updateRule.setSortingConditionIds(ruleCommand.getSortingConditionIds());
            updateRule.setSortingSql(ruleCommand.getSortingSql());
            updateRule.setCreateTime(new Date());
            updateRule.setCreatedId(userId);
            updateRule.setLastModifyTime(new Date());
            updateRule.setModifiedId(userId);
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate,insert to sharedDB, param [ruleCommand:{}, userId:{}, ouId:{}, updateRule:{}] ", ruleCommand, userId, ouId, updateRule);
            }
            whInBoundRuleDao.insert(updateRule);
        }
        WhInBoundRule returnRule = whInBoundRuleDao.findByIdExt(updateRule.getId(), ouId);
        if (null == returnRule) {
            log.error("WhInBoundRuleManagerImpl saveOrUpdate failed, param [ruleCommand:{}, userId:{}, ouId:{}, updateRule:{}] ", ruleCommand, userId, ouId, updateRule);
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(returnRule.getModifiedId());
        gl.setObjectType(returnRule.getClass().getSimpleName());
        gl.setModifiedValues(returnRule);
        gl.setOuId(returnRule.getOuId());
        if (null != ruleCommand.getId()) {
            gl.setType(Constants.GLOBAL_LOG_UPDATE);
        } else {
            gl.setType(Constants.GLOBAL_LOG_INSERT);
        }
        if (log.isDebugEnabled()) {
            log.debug("WhInBoundRuleManagerImpl saveOrUpdate, save globalLog to sharedDB, param [globalLogCommand:{}]", gl);
        }
        globalLogManager.insertGlobalLog(gl);
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl saveOrUpdate is end");
        }
        return returnRule;
    }

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
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl updateLifeCycle is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("updateLifeCycle param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}] ", ids, lifeCycle, userId, ouId);
        }
        if (null == ids || ids.contains(null) || null == lifeCycle || null == userId || null == ouId) {
            log.error("WhInBoundRuleManagerImpl updateLifeCycle failed, param is null, param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}] ", ids, lifeCycle, userId, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        if (log.isDebugEnabled()) {
            log.debug("updateLifeCycle loop ids, param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}] ", ids, lifeCycle, userId, ouId);
        }
        for (Long id : ids) {
            WhInBoundRule originalRule = whInBoundRuleDao.findByIdExt(id, ouId);
            if (null == originalRule) {
                log.error("WhInBoundRuleManagerImpl updateLifeCycle failed, originalRule is null, param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}, id:{}] ", ids, lifeCycle, userId, ouId, id);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originalRule.setLifecycle(lifeCycle);
            originalRule.setModifiedId(userId);
            if (log.isDebugEnabled()) {
                log.debug("updateLifeCycle , param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}, originalRule:{}] ", ids, lifeCycle, userId, ouId, originalRule);
            }
            int count = whInBoundRuleDao.saveOrUpdateByVersion(originalRule);
            if (1 != count) {
                log.error("WhInBoundRuleManagerImpl updateLifeCycle failed, update count != 1, param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}, id:{}, count:{}] ", ids, lifeCycle, userId, ouId, id, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            WhInBoundRule returnRule = whInBoundRuleDao.findByIdExt(originalRule.getId(), ouId);
            if (null == returnRule) {
                log.error("WhInBoundRuleManagerImpl updateLifeCycle failed, select result is null, param [ids:{}, lifeCycle:{}, userId:{}, ouId:{}, id:{}, ruleId:{}] ", ids, lifeCycle, userId, ouId, id, originalRule.getId());
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            GlobalLogCommand gl = new GlobalLogCommand();
            gl.setModifiedId(returnRule.getModifiedId());
            gl.setObjectType(returnRule.getClass().getSimpleName());
            gl.setModifiedValues(returnRule);
            gl.setOuId(returnRule.getOuId());
            gl.setType(Constants.GLOBAL_LOG_UPDATE);
            if (log.isDebugEnabled()) {
                log.debug("WhInBoundRuleManagerImpl updateLifeCycle, save globalLog to sharedDB, param [globalLogCommand:{}]", gl);
            }
            globalLogManager.insertGlobalLog(gl);

        }
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl updateLifeCycle is end");
        }
    }

    /**
     * 测试分拣规则sql
     *
     * @author mingwei.xie
     * @param inventoryId
     * @param containerId
     * @param ruleSql
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public Boolean testSortingRule(Long inventoryId, Long containerId, String ruleSql, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl testSortingRule is start, param [inventoryId:{}, containerId:{}, ruleSql:{}, ouId:{}]", inventoryId, containerId, ruleSql, ouId);
        }
        if (null == inventoryId || null == containerId || null == ruleSql || null == ouId) {
            log.error("WhInBoundRuleManagerImpl testSortingRule failed,param is null, param [inventoryId:{}, containerId:{}, ruleSql:{}, ouId:{}]", inventoryId, containerId, ruleSql, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        Long result = whInBoundRuleDao.executeSortingRuleSql(inventoryId, containerId, ruleSql, ouId);
        if (log.isInfoEnabled()) {
            log.info("WhInBoundRuleManagerImpl testSortingRule is end, param [inventoryId:{}, containerId:{}, ruleSql:{}, ouId:{}, result:{}]", inventoryId, containerId, ruleSql, ouId, result);
        }
        return null != result;
    }

}
