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
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionRcvdDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionRcvdManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunction;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

import lark.common.annotation.MoreDB;

@Service("whFunctionRcvdManager")
@Transactional
public class WhFunctionRcvdManagerImpl extends BaseManagerImpl implements WhFunctionRcvdManager {

    public static final Logger log = LoggerFactory.getLogger(WhFunctionRcvdManagerImpl.class);

    @Autowired
    private WhFunctionRcvdDao whFunctionRcvdDao;
    @Autowired
    private WhFunctionDao whFunctionDao;

    /**
     * 通过功能主表ID查询对应收货参数
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionRcvd findwFunctionRcvdByFunctionId(Long id, Long ouid) {
        return whFunctionRcvdDao.findwFunctionRcvdByFunctionId(id, ouid);
    }
    
    /**
     * 通过功能主表ID查询对应收货参数
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionRcvd findwFunctionRcvdByFunctionId(Long id, Long ouid, String logId) {
        if (log.isDebugEnabled()) {
            log.debug("whFunctionPutAwayManager.findwFunctionRcvdByFunctionId start, id is:[{}], ouid is:[{}], logId is:[{}]", new Object[] {id, ouid, logId});
        }
        WhFunctionRcvd rcvdFunc = whFunctionRcvdDao.findwFunctionRcvdByFunctionId(id, ouid);
        if (log.isDebugEnabled()) {
            log.debug("whFunctionPutAwayManager.findwFunctionRcvdByFunctionId end, logId is:[{}], rcvdFunc", new Object[] {logId, ParamsUtil.bean2String(rcvdFunc)});
        }
        return rcvdFunc;
    }


    /**
     * 新建&修改收货功能参数
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunction editFuncitonRcvd(WhFunctionCommand whFunctionCommand) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".editFuncitonRcvd" + " start...");
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
            // 插入收货参数表
            WhFunctionRcvd wRcvd = new WhFunctionRcvd();
            BeanUtils.copyProperties(whFunctionCommand, wRcvd);
            wRcvd.setId(null);
            wRcvd.setFunctionId(wf.getId());
            whFunctionRcvdDao.insert(wRcvd);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_INSERT, wRcvd, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
        } else {
            // 更新数据
            if (log.isDebugEnabled()) {
                log.debug("IF: null != whFunctionCommand.getId()");
            }
            wf = whFunctionDao.findWhFunctionById(whFunctionCommand.getId(), whFunctionCommand.getOuId());
            if (null == wf) {
                // 如果数据为空抛出异常
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonRcvd, WhFunction id:[{}]" + whFunctionCommand.getId());
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
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonRcvd, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, wf, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
            // 修改收货功能参数信息
            WhFunctionRcvd wRcvd = whFunctionRcvdDao.findwFunctionRcvdByFunctionId(wf.getId(), wf.getOuId());
            if (null == wRcvd) {
                // 如果数据为空抛出异常
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "editFuncitonRcvd, WhFunction id:[{}]" + whFunctionCommand.getId());
                }
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            wRcvd.setRcvdPattern(whFunctionCommand.getRcvdPattern());
            wRcvd.setScanPattern(whFunctionCommand.getScanPattern());
            wRcvd.setSkipUrl(whFunctionCommand.getSkipUrl());
            wRcvd.setTagLock(whFunctionCommand.getTagLock());
            wRcvd.setInvType(whFunctionCommand.getInvType());
            wRcvd.setInvStatus(whFunctionCommand.getInvStatus());
            wRcvd.setNormIncPointoutRcvd(whFunctionCommand.getNormIncPointoutRcvd());
            wRcvd.setIsLimitUniqueInvType(whFunctionCommand.getIsLimitUniqueInvType());
            wRcvd.setIsLimitUniqueInvStatus(whFunctionCommand.getIsLimitUniqueInvStatus());
            wRcvd.setIsLimitUniqueInvAttr1(whFunctionCommand.getIsLimitUniqueInvAttr1());
            wRcvd.setIsLimitUniqueInvAttr2(whFunctionCommand.getIsLimitUniqueInvAttr2());
            wRcvd.setIsLimitUniqueInvAttr3(whFunctionCommand.getIsLimitUniqueInvAttr3());
            wRcvd.setIsLimitUniqueInvAttr4(whFunctionCommand.getIsLimitUniqueInvAttr4());
            wRcvd.setIsLimitUniqueInvAttr5(whFunctionCommand.getIsLimitUniqueInvAttr5());
            wRcvd.setIsLimitUniquePlaceoforigin(whFunctionCommand.getIsLimitUniquePlaceoforigin());
            wRcvd.setIsLimitUniqueBatch(whFunctionCommand.getIsLimitUniqueBatch());
            wRcvd.setIsLimitUniqueDateOfManufacture(whFunctionCommand.getIsLimitUniqueDateOfManufacture());
            wRcvd.setIsLimitUniqueExpiryDate(whFunctionCommand.getIsLimitUniqueExpiryDate());
            wRcvd.setIsMixingSku(whFunctionCommand.getIsMixingSku());
            wRcvd.setIsInvattrAsnPointoutUser(whFunctionCommand.getIsInvattrAsnPointoutUser());
            wRcvd.setIsInvattrDiscrepancyAllowrcvd(whFunctionCommand.getIsInvattrDiscrepancyAllowrcvd());
            wRcvd.setIsCaselevelScanSku(whFunctionCommand.getIsCaselevelScanSku());
            whFunctionRcvdDao.update(wRcvd);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, wRcvd, whFunctionCommand.getOuId(), whFunctionCommand.getUserId(), null, null);
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".editFuncitonRcvd" + " end...");
        }
        return wf;
    }
}
