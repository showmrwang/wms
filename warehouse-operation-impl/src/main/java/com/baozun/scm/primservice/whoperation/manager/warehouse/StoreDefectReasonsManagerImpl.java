package com.baozun.scm.primservice.whoperation.manager.warehouse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectReasonsDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;

@Service("storeDefectReasonsManager")
@Transactional
public class StoreDefectReasonsManagerImpl implements StoreDefectReasonsManager {
    public static final Logger log = LoggerFactory.getLogger(StoreDefectReasonsManagerImpl.class);

    @Autowired
    private StoreDefectReasonsDao storeDefectReasonsDao;

    @Autowired
    private GlobalLogManager globalLogManager;


    @Override
    public Boolean uniqueCodeOrName(StoreDefectReasons storeDefectReasons) {
        log.info(this.getClass().getSimpleName() + ".uniqueCodeOrName method begin");
        if (log.isDebugEnabled()) {
            log.debug("Param storeDefectReasons is {}", storeDefectReasons.toString());
        }
        if (null == storeDefectReasons.getDefectTypeId()) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"defectTypeId"});
        }
        StoreDefectReasons querySdr = new StoreDefectReasons();
        querySdr.setId(storeDefectReasons.getId());
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != storeDefectReasons.getCode()] is", null != storeDefectReasons.getCode());
        }
        if (null != storeDefectReasons.getCode()) {
            querySdr.setCode(storeDefectReasons.getCode());
            querySdr.setDefectTypeId(storeDefectReasons.getDefectTypeId());
            querySdr.setName(null);
            long count = storeDefectReasonsDao.uniqueCodeOrName(querySdr);
            if (0 != count) {
                return false;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != storeDefectReasons.getName()] is", null != storeDefectReasons.getName());
        }
        if (null != storeDefectReasons.getName()) {
            querySdr.setCode(null);
            querySdr.setDefectTypeId(storeDefectReasons.getDefectTypeId());
            querySdr.setName(storeDefectReasons.getName());
            long count = storeDefectReasonsDao.uniqueCodeOrName(querySdr);
            if (0 != count) {
                return false;
            }
        }
        log.info(this.getClass().getSimpleName() + ".uniqueCodeOrName method end");
        return true;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public StoreDefectReasons saveOrUpdate(StoreDefectReasons storeDefectReasons, Long userId) {
        log.info(this.getClass().getSimpleName() + ".saveOrUpdate method begin");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_GLOBALSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param storeDefectReasons is {},userId is {}", storeDefectReasons.toString(), userId);
        }
        // 更新
        StoreDefectReasons s = null;
        int count = 0;
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != storeDefectReasons.getId()] is", null != storeDefectReasons.getId());
        }
        if (null != storeDefectReasons.getId()) {
            s = storeDefectReasonsDao.findById(storeDefectReasons.getId());
            storeDefectReasons.setCreateTime(s.getCreateTime());
            storeDefectReasons.setModifiedId(userId);
            storeDefectReasons.setCreatedId(s.getCreatedId());
            storeDefectReasons.setLastModifyTime(s.getLastModifyTime());
            // 用于全局表最后修改时间统一
            storeDefectReasons.setGlobalLastModifyTime(new Date());
            count = storeDefectReasonsDao.saveOrUpdateByVersion(storeDefectReasons);
            // 修改失败
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return storeDefectReasons;
        } else {
            s = new StoreDefectReasons();
            s.setCreateTime(new Date());
            s.setCreatedId(userId);
            if (null == storeDefectReasons.getDefectTypeId()) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"DefectTypeId"});
            }
            s.setDefectTypeId(storeDefectReasons.getDefectTypeId());
            s.setDescription(storeDefectReasons.getDescription());
            s.setCode(storeDefectReasons.getCode());
            s.setName(storeDefectReasons.getName());
            s.setModifiedId(userId);
            s.setLastModifyTime(new Date());
            storeDefectReasonsDao.insert(s);
        }
        log.info(this.getClass().getSimpleName() + ".saveOrUpdate method end");
        return s;
    }

    @Override
    public List<StoreDefectReasonsCommand> findStoreDefectReasonsByDefectTypeIds(List<Long> storeDefectTypeIds) {
        log.info(this.getClass().getSimpleName() + ".findStoreDefectReasonsByParam method begin");
        if (log.isDebugEnabled()) {
            log.debug("PARAM: storeDefectTypeIds:[{}]", storeDefectTypeIds.toString());
        }
        if (null == storeDefectTypeIds) {
            if (log.isErrorEnabled()) {
                log.error("FAILED: " + this.getClass().getSimpleName() + ".findStoreDefectReasonsByDefectTypeIds, storeDefectTypeIds:[{}]", storeDefectTypeIds);
            }
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"storeDefectTypeIds"});
        }
        List<StoreDefectReasonsCommand> storeDefectReasonsList = new ArrayList<StoreDefectReasonsCommand>();
        if (storeDefectTypeIds.size() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("IF: storeDefectTypeIds.size() > 0");
            }
            storeDefectReasonsList = storeDefectReasonsDao.findStoreDefectReasonsByDefectTypeIds(storeDefectTypeIds);
        }
        log.info(this.getClass().getSimpleName() + ".findStoreDefectReasonsByParam method end");
        return storeDefectReasonsList;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void insertGlobalLog(Long userId, Date modifyTime, String objectType, String modifiedValues, String type, Long ouId) {
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(userId);
        gl.setModifyTime(modifyTime);
        gl.setObjectType(objectType);
        gl.setModifiedValues(modifiedValues);
        gl.setType(type);
        gl.setOuId(ouId);
        globalLogManager.insertGlobalLog(gl);
    }
}
