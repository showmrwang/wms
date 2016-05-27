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
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPutAwayDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunction;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

import lark.common.annotation.MoreDB;

@Service("whFunctionPutAwayManager")
@Transactional
public class WhFunctionPutAwayManagerImpl extends BaseManagerImpl implements WhFunctionPutAwayManager {

    public static final Logger log = LoggerFactory.getLogger(WhFunctionPutAwayManagerImpl.class);

    @Autowired
    private WhFunctionPutAwayDao whFunctionPutAwayDao;
    @Autowired
    private WhFunctionDao whFunctionDao;

    /**
     * 查询上架功能参数明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionPutAway findWhFunctionPutAwayByFunctionId(Long id, Long ouid) {
        return whFunctionPutAwayDao.findWhFunctionPutAwayByFunctionId(id, ouid);
    }
    
    /**
     * 查询上架功能参数明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionPutAway findWhFunctionPutAwayByFunctionId(Long id, Long ouid, String logId) {
        if (log.isDebugEnabled()) {
            log.debug("whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId start, id is:[{}], ouid is:[{}], logId is:[{}]", new Object[] {id, ouid, logId});
        }
        WhFunctionPutAway putawayFunc = whFunctionPutAwayDao.findWhFunctionPutAwayByFunctionId(id, ouid);
        if (log.isDebugEnabled()) {
            log.debug("whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId end, logId is:[{}], putawayFunc", new Object[] {logId, ParamsUtil.bean2String(putawayFunc)});
        }
        return putawayFunc;
    }

    /**
     * 新建&修改上架功能参数
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunction editFuncitonPutAway(WhFunctionCommand whFunctionCommand) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".editFuncitonPutAway" + " start...");
        }
        if (log.isDebugEnabled()) {
            log.debug("PARAM: " + whFunctionCommand.toString());
        }
        WhFunction wf = new WhFunction();
        if (null == whFunctionCommand.getId()) {
            // 新建上架功能参数
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
            WhFunctionPutAway wfpa = new WhFunctionPutAway();
            BeanUtils.copyProperties(whFunctionCommand, wfpa);
            wfpa.setId(null);
            wfpa.setFunctionId(wf.getId());
            whFunctionPutAwayDao.insert(wfpa);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_INSERT, wfpa, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
        } else {
            // 更新数据
            if (log.isDebugEnabled()) {
                log.debug("IF: null != whFunctionCommand.getId()");
            }
            wf = whFunctionDao.findWhFunctionById(whFunctionCommand.getId(), whFunctionCommand.getOuId());
            if (null == wf) {
                // 如果数据为空抛出异常
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonPutAway, WhFunction id:[{}]" + whFunctionCommand.getId());
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
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonPutAway, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, wf, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
            // 修改上架参数信息
            WhFunctionPutAway wfpa = whFunctionPutAwayDao.findWhFunctionPutAwayByFunctionId(wf.getId(), wf.getOuId());
            if (null == wfpa) {
                // 如果数据为空抛出异常
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonPutAway, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            wfpa.setIsCaselevelScanSku(whFunctionCommand.getIsCaselevelScanSku());
            wfpa.setIsEntireBinPutaway(whFunctionCommand.getIsEntireBinPutaway());
            wfpa.setIsEntireTrayPutaway(whFunctionCommand.getIsEntireTrayPutaway());
            wfpa.setIsNotcaselevelScanSku(whFunctionCommand.getIsNotcaselevelScanSku());
            wfpa.setPutawayPattern(whFunctionCommand.getPutawayPattern());
            wfpa.setScanPattern(whFunctionCommand.getScanPattern());
            whFunctionPutAwayDao.update(wfpa);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, wfpa, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".editFuncitonPutAway" + " end...");
        }
        return wf;
    }

}
