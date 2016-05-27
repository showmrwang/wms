package com.baozun.scm.primservice.whoperation.manager;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionInBoundDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionInBoundManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunction;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionInBound;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

import lark.common.annotation.MoreDB;

@Service("whFunctionInBoundManager")
@Transactional
public class WhFunctionInBoundManagerImpl extends BaseManagerImpl implements WhFunctionInBoundManager {

    public static final Logger log = LoggerFactory.getLogger(WhFunctionInBoundManagerImpl.class);

    @Autowired
    private WhFunctionInBoundDao whFunctionInBoundDao;
    @Autowired
    private WhFunctionDao whFunctionDao;

    /**
     * 根据功能主表ID和对应组织ID查询入库分拣功能信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionInBound findwFunctionInBoundByFunctionId(Long id, Long ouid) {
        return whFunctionInBoundDao.findwFunctionInBoundByFunctionId(id, ouid);
    }
    
    /**
     * 根据功能主表ID和对应组织ID查询入库分拣功能信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionInBound findwFunctionInBoundByFunctionId(Long id, Long ouid, String logId) {
        if (log.isDebugEnabled()) {
            log.debug("whFunctionPutAwayManager.findwFunctionInBoundByFunctionId start, id is:[{}], ouid is:[{}], logId is:[{}]", new Object[] {id, ouid, logId});
        }
        WhFunctionInBound inboundFunc = whFunctionInBoundDao.findwFunctionInBoundByFunctionId(id, ouid);
        if (log.isDebugEnabled()) {
            log.debug("whFunctionPutAwayManager.findwFunctionInBoundByFunctionId end, logId is:[{}], inboundFunc", new Object[] {logId, ParamsUtil.bean2String(inboundFunc)});
        }
        return inboundFunc;
    }

    /**
     * 新建&修改入库分拣功能参数
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunction editFuncitonInBound(WhFunctionCommand whFunctionCommand) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".editFuncitonInBound" + " start...");
        }
        if (log.isDebugEnabled()) {
            log.debug("PARAM: " + whFunctionCommand.toString());
        }
        WhFunction wf = new WhFunction();
        if (null == whFunctionCommand.getId()) {
            // 新建入库分拣功能参数
            if (log.isDebugEnabled()) {
                log.debug("IF: null == whFunctionCommand.getId()");
            }
            wf.setFunctionCode(whFunctionCommand.getFunctionCode());
            wf.setFunctionName(whFunctionCommand.getFunctionName());
            wf.setFunctionTemplet(whFunctionCommand.getFunctionTemplet());
            wf.setLifecycle(whFunctionCommand.getLifecycle());
            wf.setOuId(whFunctionCommand.getOuId());
            wf.setPlatformType(whFunctionCommand.getPlatformType());
            wf.setSkuAttrMgmt(whFunctionCommand.getSkuAttrMgmt());
            wf.setCreateTime(new Date());
            wf.setLastModifyTime(new Date());
            wf.setCreateId(whFunctionCommand.getUserId());
            wf.setOperatorId(whFunctionCommand.getUserId());
            whFunctionDao.insert(wf);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_INSERT, wf, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
            WhFunctionInBound wfi = new WhFunctionInBound();
            BeanUtils.copyProperties(whFunctionCommand, wfi);
            wfi.setId(null);
            wfi.setFunctionId(wf.getId());
            whFunctionInBoundDao.insert(wfi);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_INSERT, wfi, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
        } else {
            // 更新数据
            if (log.isDebugEnabled()) {
                log.debug("IF: null != whFunctionCommand.getId()");
            }
            wf = whFunctionDao.findWhFunctionById(whFunctionCommand.getId(), whFunctionCommand.getOuId());
            if (null == wf) {
                // 如果数据为空抛出异常
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonInBound, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            wf.setFunctionName(whFunctionCommand.getFunctionName());
            wf.setSkuAttrMgmt(whFunctionCommand.getSkuAttrMgmt());
            wf.setLifecycle(whFunctionCommand.getLifecycle());
            wf.setOperatorId(whFunctionCommand.getUserId());
            int fcount = whFunctionDao.saveOrUpdateByVersion(wf);
            // 修改失败
            if (fcount == 0) {
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonInBound, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, wf, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
            // 修改入库分拣参数信息
            WhFunctionInBound wfi = whFunctionInBoundDao.findwFunctionInBoundByFunctionId(wf.getId(), wf.getOuId());
            if (null == wfi) {
                // 如果数据为空抛出异常
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonInBound, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            wfi.setInboundRuleId(whFunctionCommand.getInboundRuleId());
            wfi.setIsScanContainer(whFunctionCommand.getIsScanContainer());
            wfi.setScanPattern(whFunctionCommand.getScanPattern());
            whFunctionInBoundDao.update(wfi);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_INSERT, wfi, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".editFuncitonInBound" + " end...");
        }
        return wf;
    }

}
