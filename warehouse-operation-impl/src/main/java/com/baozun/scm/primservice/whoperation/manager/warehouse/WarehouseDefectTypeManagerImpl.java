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

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis.WarehouseDefectTypeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

@Service("warehouseDefectTypeManager")
@Transactional
public class WarehouseDefectTypeManagerImpl extends BaseManagerImpl implements WarehouseDefectTypeManager {

    public static final Logger log = LoggerFactory.getLogger(WarehouseDefectTypeManagerImpl.class);

    @Autowired
    private WarehouseDefectTypeDao warehouseDefectTypeDao;
    @Autowired
    private WarehouseDefectReasonsDao warehouseDefectReasonsDao;

    /**
     * 通过识别参数查询仓库残次信息
     * 
     * @author lichuan
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WarehouseDefectTypeCommand findDefectTypeByIdParams(Map<String, Object> params) {
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.findDefectTypeByParams start, params are:[{}]", ParamsUtil.map2String(params));
        }
        if (null == params) {
            log.error("warehouseDefectTypeManager.findDefectTypeByParams, params is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == params.get("ouId")) {
            log.error("warehouseDefectTypeManager.findDefectTypeByParams, params ouId is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.findDefectTypeByParams->warehouseDefectTypeDao.findDefectTypeByIdParams invoke, params are:[{}]", new Object[] {ParamsUtil.map2String(params)});
        }
        // 查询残次信息
        WarehouseDefectTypeCommand wdt = null;
        try {
            wdt = warehouseDefectTypeDao.findDefectTypeByIdParams(params);
        } catch (Exception e) {
            log.error(getLogMsg("warehouseDefectTypeManager.findDefectTypeByParams->warehouseDefectTypeDao.findDefectTypeByIdParams invoke throw exception, params are:[{}]", new Object[] {ParamsUtil.map2String(params)}), e);
            BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
            BusinessException dbe = new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            be.setLinkedException(dbe);
            throw be;
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.findDefectTypeByParams->warehouseDefectTypeDao.findListByQueryMapWithPageExt result, params are:[{}]", new Object[] {ParamsUtil.map2String(params)});
        }
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.findDefectTypeByParams end, params are:[{}]", ParamsUtil.map2String(params));
        }
        return wdt;
    }

    /**
     * @author lichuan
     * @param wdtCmd
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public boolean checkUnique(WarehouseDefectTypeCommand wdtCmd) {
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.checkUnique start: param wdtCmd is:[{}]", ParamsUtil.bean2String(wdtCmd));
        }
        if (null == wdtCmd) {
            log.error("warehouseDefectTypeManager.checkUnique param wdtCmd is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == wdtCmd.getOuId()) {
            log.error("warehouseDefectTypeManager.checkUnique param ouId is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.checkUnique->warehouseDefectTypeDao.checkUnique invoke, param wdtCmd is:[{}]", new Object[] {ParamsUtil.bean2String(wdtCmd)});
        }
        boolean result = true;
        long count = warehouseDefectTypeDao.checkUnique(wdtCmd);
        if (0 != count) {
            result = false;
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.checkUnique->warehouseDefectTypeDao.checkUnique result, param wdtCmd is:[{}], return is:[{}]", new Object[] {ParamsUtil.bean2String(wdtCmd), result});
        }
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.checkUnique end: param wdtCmd is:[{}]", ParamsUtil.bean2String(wdtCmd));
        }
        return result;
    }

    /**
     * 新增或更新仓库残次信息
     * 
     * @author lichuan
     * @param wdtCmd
     * @param ouId
     * @param userId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdate(WarehouseDefectTypeCommand wdtCmd, Long ouId, Long userId, String logId) {
        String wdtCmdLog = ParamsUtil.bean2String(wdtCmd, false);
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.saveOrUpdate start: param wdtCmd is:[{}], param ouId is:[{}], param userId is:[{}], logId is:[{}]", new Object[] {wdtCmdLog, ouId, userId, logId});
        }
        if (null == wdtCmd) {
            log.error("warehouseDefectTypeManager.saveOrUpdate param wdtCmd is null exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == ouId) {
            log.error("warehouseDefectTypeManager.saveOrUpdate param ouId is null exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.OUID_IS_NULL_ERROR);
        }
        WarehouseDefectType wdt = new WarehouseDefectType();
        if (null == wdtCmd.getId()) {
            /* 新增仓库残次信息 */
            boolean result = true;
            // 校验残次编码是否已存在
            WarehouseDefectTypeCommand checkCodeCmd = new WarehouseDefectTypeCommand();
            if(StringUtils.isEmpty(wdtCmd.getCode())){
                log.error("warehouseDefectTypeManager.saveOrUpdate wdtCmd code is null exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
            }
            checkCodeCmd.setCode(wdtCmd.getCode());
            checkCodeCmd.setOuId(ouId);
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.checkUnique invoke, param wdtCmd is:[{}], logId is:[{}]", new Object[] {ParamsUtil.bean2String(checkCodeCmd, false), logId});
            }
            result = checkUnique(checkCodeCmd);
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.checkUnique result, param wdtCmd is:[{}], return is:[{}], logId is:[{}]", new Object[] {ParamsUtil.bean2String(checkCodeCmd, false), result, logId});
            }
            if (false == result) {
                log.error("warehouseDefectTypeManager.saveOrUpdate warehouseDefectTypeCode is exists exception, param wdtCmd is:[{}], logId is:[{}]", new Object[] {ParamsUtil.bean2String(checkCodeCmd, false), logId});
                throw new BusinessException(ErrorCodes.WH_DEFECT_TYPE_CODE_EXISTS, new Object[] {checkCodeCmd.getCode()});
            }
            // 校验残次名称是否已存在
            WarehouseDefectTypeCommand checkNameCmd = new WarehouseDefectTypeCommand();
            checkNameCmd.setName(wdtCmd.getName());
            checkNameCmd.setOuId(ouId);
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->checkUnique invoke, param wdtCmd is:[{}], param logId is:[{}]", new Object[] {ParamsUtil.bean2String(checkNameCmd, false), logId});
            }
            result = checkUnique(checkNameCmd);
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->checkUnique result, param wdtCmd is:[{}], return is:[{}], param logId is:[{}]", new Object[] {ParamsUtil.bean2String(checkNameCmd, false), result, logId});
            }
            if (false == result) {
                log.error("warehouseDefectTypeManager.saveOrUpdate warehouseDefectTypeName is exists exception, param wdtCmd is:[{}], logId is:[{}]", new Object[] {ParamsUtil.bean2String(checkNameCmd, false), logId});
                throw new BusinessException(ErrorCodes.WH_DEFECT_TYPE_NAME_EXISTS, new Object[] {checkNameCmd.getName()});
            }
            // 新增仓库残次类型
            BeanUtils.copyProperties(wdtCmd, wdt);
            wdt.setOuId(ouId);
            wdt.setCreatedId(userId);
            wdt.setModifiedId(userId);
            wdt.setCreateTime(new Date());
            wdt.setLastModifyTime(new Date());
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.insert invoke, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), logId});
            }
            try {
                warehouseDefectTypeDao.insert(wdt);
            } catch (Exception e) {
                log.error(
                        getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.insert invoke throw exception, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]",
                                new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), logId}), e);
                BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
                throw be;
            }
            if (null == wdt.getId()) {
                log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.insert error, dataSuource is:[{}], param wdt is:[{}], result is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt),
                        "fail", logId});
                throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
            }
            // 插入全局日志
            insertGlobalLog(GLOBAL_LOG_INSERT, wdt, ouId, userId, null, null);
            // 新增仓库残次原因
            List<WarehouseDefectReasonsCommand> reasonsCmdList = wdtCmd.getReasonsList();
            for (WarehouseDefectReasonsCommand wdrCmd : reasonsCmdList) {
                WarehouseDefectReasons wdr = new WarehouseDefectReasons();
                BeanUtils.copyProperties(wdrCmd, wdr);
                wdr.setDefectTypeId(wdt.getId());
                wdr.setOuId(ouId);
                wdr.setCreatedId(userId);
                wdr.setModifiedId(userId);
                wdr.setCreateTime(new Date());
                wdr.setLastModifyTime(new Date());
                if (log.isDebugEnabled()) {
                    log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectReasonsDao.insert invoke, dataSuource is:[{}], param wdr is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdr), logId});
                }
                try {
                    warehouseDefectReasonsDao.insert(wdr);
                } catch (Exception e) {
                    log.error(
                            getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectReasonsDao.insert invoke throw exception, dataSuource is:[{}], param wdr is:[{}], logId is:[{}]",
                                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdr), logId}), e);
                    BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
                    throw be;
                }
                if (null == wdr.getId()) {
                    log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectReasonsDao.insert error, dataSuource is:[{}], param wdr is:[{}], result is:[{}], logId is:[{}]",
                            new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdr), "fail", logId});
                    throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                }
                // 插入全局日志
                insertGlobalLog(GLOBAL_LOG_INSERT, wdr, ouId, userId, null, null);
            }
        } else {
            /* 更新仓库残次信息 */
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt invoke, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE, wdtCmd.getId(),
                        ouId, logId});
            }
            try {
                // 获取仓库残次类型
                wdt = warehouseDefectTypeDao.findByIdExt(wdtCmd.getId(), ouId);
            } catch (Exception e) {
                log.error(
                        getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt invoke throw exception, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], logId is:[{}]", new Object[] {
                                DbDataSource.MOREDB_SHARDSOURCE, wdtCmd.getId(), ouId, logId}), e);
                BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
                throw be;
            }
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt result, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], return is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE,
                        wdtCmd.getId(), ouId, ParamsUtil.bean2String(wdt), logId});
            }
            if (null == wdt) {
                log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt error, wdt is not exists, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE,
                        wdtCmd.getId(), ouId, logId});
                throw new BusinessException(ErrorCodes.GET_DATA_ERROR);
            }
            wdt.setCode(wdtCmd.getCode());
            wdt.setName(wdtCmd.getName());
            wdt.setDescription(wdtCmd.getDescription());
            wdt.setLifecycle(wdtCmd.getLifecycle());
            wdt.setModifiedId(userId);
            wdt.setLastModifyTime(new Date());
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion invoke, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt),
                        logId});
            }
            int count = 0;
            try {
                // 更新仓库残次类型
                count = warehouseDefectTypeDao.saveOrUpdateByVersion(wdt);
            } catch (Exception e) {
                log.error(
                        getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion invoke throw exception, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE,
                                ParamsUtil.bean2String(wdt), logId}), e);
                BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
                throw be;
            }
            if (log.isDebugEnabled()) {
                log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion result, dataSuource is:[{}], param wdt is:[{}], return is:[{}], logId is:[{}]",
                        new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), count, logId});
            }
            if (1 != count) {
                log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion error, update wdt fail, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]",
                        new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), logId});
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入全局日志
            insertGlobalLog(GLOBAL_LOG_UPDATE, wdt, ouId, userId, null, null);
            // 删除所有此残次类型对应的残次原因
            warehouseDefectReasonsDao.deleteByTypeIdExt(wdt.getId(), ouId);
            WarehouseDefectReasons wdrs = new WarehouseDefectReasons();
            wdrs.setDefectTypeId(wdtCmd.getId());
            insertGlobalLog(GLOBAL_LOG_DELETE, wdrs, ouId, userId, null, null);
            // 新增仓库残次原因
            List<WarehouseDefectReasonsCommand> reasonsCmdList = wdtCmd.getReasonsList();
            for (WarehouseDefectReasonsCommand wdrCmd : reasonsCmdList) {
                WarehouseDefectReasons wdr = new WarehouseDefectReasons();
                BeanUtils.copyProperties(wdrCmd, wdr);
                wdr.setDefectTypeId(wdt.getId());
                wdr.setOuId(ouId);
                wdr.setCreatedId(userId);
                wdr.setModifiedId(userId);
                wdr.setCreateTime(new Date());
                wdr.setLastModifyTime(new Date());
                if (log.isDebugEnabled()) {
                    log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectReasonsDao.insert invoke, dataSuource is:[{}], param wdr is:[{}], logId is:[{}]", new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdr), logId});
                }
                try {
                    warehouseDefectReasonsDao.insert(wdr);
                } catch (Exception e) {
                    log.error(
                            getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectReasonsDao.insert invoke throw exception, dataSuource is:[{}], param wdr is:[{}], logId is:[{}]",
                                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdr), logId}), e);
                    BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
                    throw be;
                }
                if (null == wdr.getId()) {
                    log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectReasonsDao.insert error, dataSuource is:[{}], param wdr is:[{}], result is:[{}], logId is:[{}]",
                            new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdr), "fail", logId});
                    throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                }
                // 插入全局日志
                insertGlobalLog(GLOBAL_LOG_INSERT, wdr, ouId, userId, null, null);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.saveOrUpdate end: param wdtCmd is:[{}], param ouId is:[{}], param userId is:[{}], logId is:[{}]", new Object[] {wdtCmdLog, ouId, userId, logId});
        }
    }
    
    /**
     * 更新仓库残次类型的生命周期
     * @author lichuan
     * @param wdtCmd
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateDefectTypeLifecycle(WarehouseDefectTypeCommand wdtCmd, Long ouId, Long userId, String logId) {
        String wdtCmdLog = ParamsUtil.bean2String(wdtCmd, false);
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.updateDefectTypeLifecycle start: param wdtCmd is:[{}], param ouId is:[{}], param userId is:[{}], logId is:[{}]", new Object[] {wdtCmdLog, ouId, userId, logId});
        }
        WarehouseDefectType wdt = new WarehouseDefectType();
        try {
            // 获取仓库残次类型
            wdt = warehouseDefectTypeDao.findByIdExt(wdtCmd.getId(), ouId);
        } catch (Exception e) {
            log.error(getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt invoke throw exception, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, wdtCmd.getId(), ouId, logId}), e);
            BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
            throw be;
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt result, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], return is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, wdtCmd.getId(), ouId, ParamsUtil.bean2String(wdt), logId});
        }
        if (null == wdt) {
            log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.findByIdExt error, wdt is not exists, dataSuource is:[{}], param wdtId is:[{}], param ouId is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, wdtCmd.getId(), ouId, logId});
            throw new BusinessException(ErrorCodes.GET_DATA_ERROR);
        }
        wdt.setLifecycle(wdtCmd.getLifecycle());
        wdt.setModifiedId(userId);
        wdt.setLastModifyTime(new Date());
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion invoke, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), logId});
        }
        int count = 0;
        try {
            // 更新仓库残次类型
            count = warehouseDefectTypeDao.saveOrUpdateByVersion(wdt);
        } catch (Exception e) {
            log.error(getLogMsg("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion invoke throw exception, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), logId}), e);
            BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
            throw be;
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion result, dataSuource is:[{}], param wdt is:[{}], return is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), count, logId});
        }
        if (1 != count) {
            log.error("warehouseDefectTypeManager.saveOrUpdate->warehouseDefectTypeDao.saveOrUpdateByVersion error, update wdt fail, dataSuource is:[{}], param wdt is:[{}], logId is:[{}]",
                    new Object[] {DbDataSource.MOREDB_SHARDSOURCE, ParamsUtil.bean2String(wdt), logId});
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入全局日志
        insertGlobalLog(GLOBAL_LOG_UPDATE, wdt, ouId, userId, null, null);
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.updateDefectTypeLifecycle end: param wdtCmd is:[{}], param ouId is:[{}], param userId is:[{}], logId is:[{}]", new Object[] {wdtCmdLog, ouId, userId, logId});
        }
    }

    /**
     * 通过参数查询仓库残次类型分页列表
     * 
     * @author lichuan
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WarehouseDefectTypeCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.getListByParams start, params are:[{}]", (null != params ? params.toString() : null));
        }
        if (null == params) {
            log.error("warehouseDefectTypeManager.getListByParams, params is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.getListByParams->warehouseDefectTypeDao.findListByQueryMapWithPageExt invoke, page is:[{}], sort is:[{}], params are:[{}]", new Object[] {ParamsUtil.page2String(page), ParamsUtil.sorts2String(sorts),
                    ParamsUtil.map2String(params)});
        }
        // 查询分页数据
        Pagination<WarehouseDefectTypeCommand> pagination = null;
        try {
            pagination = warehouseDefectTypeDao.findListByQueryMapWithPageExt(page, sorts, params);
        } catch (Exception e) {
            log.error("warehouseDefectTypeManager.getListByParams->warehouseDefectTypeDao.findListByQueryMapWithPageExt invoke throw exception", e);
            BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
            BusinessException dbe = new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            be.setLinkedException(dbe);
            throw be;
        }
        if (log.isDebugEnabled()) {
            log.debug("warehouseDefectTypeManager.getListByParams->warehouseDefectTypeDao.findListByQueryMapWithPageExt result, page is:[{}], sort is:[{}], params are:[{}], pagination is:[{}]",
                    new Object[] {ParamsUtil.page2String(page), ParamsUtil.sorts2String(sorts), ParamsUtil.map2String(params), ParamsUtil.bean2String(pagination)});
        }
        if (log.isInfoEnabled()) {
            log.info("warehouseDefectTypeManager.getListByParams end, params are:[{}]", (null != params ? params.toString() : null));
        }
        return pagination;
    }

    /**
     * 通过OUID查询对应仓库可用残次类型
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WarehouseDefectTypeCommand> findWarehouseDefectTypeByOuId(Long ouid, Integer lifecycle) {
        return warehouseDefectTypeDao.findWarehouseDefectTypeByOuId(ouid, lifecycle);
    }


}
