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
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDefectTypeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;

@Service("storeDefectTypeManager")
@Transactional
public class StoreDefectTypeManagerImpl implements StoreDefectTypeManager {
    public static final Logger log = LoggerFactory.getLogger(StoreDefectTypeManagerImpl.class);

    @Autowired
    private StoreDefectTypeDao storeDefectTypeDao;

    @Autowired
    private GlobalLogManager globalLogManager;


    @Override
    public Boolean uniqueCodeOrName(StoreDefectType storeDefectType) {
        log.info(this.getClass().getSimpleName() + ".uniqueCodeOrName method begin");
        if (log.isDebugEnabled()) {
            log.debug("Param storeDefectType is {}", storeDefectType.toString());
        }
        if (null == storeDefectType.getStoreId()) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"storeId"});
        }
        StoreDefectType querySdt = new StoreDefectType();
        querySdt.setId(storeDefectType.getId());
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != storeDefectType.getCode()] is", null != storeDefectType.getCode());
        }
        if (null != storeDefectType.getCode()) {
            querySdt.setCode(storeDefectType.getCode());
            querySdt.setStoreId(storeDefectType.getStoreId());
            querySdt.setName(null);
            long count = storeDefectTypeDao.uniqueCodeOrName(querySdt);
            if (0 != count) {
                return false;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != storeDefectType.getName()] is", null != storeDefectType.getName());
        }
        if (null != storeDefectType.getName()) {
            querySdt.setCode(null);
            querySdt.setStoreId(storeDefectType.getStoreId());
            querySdt.setName(storeDefectType.getName());
            long count = storeDefectTypeDao.uniqueCodeOrName(querySdt);
            if (0 != count) {
                return false;
            }
        }
        log.info(this.getClass().getSimpleName() + ".uniqueCodeOrName method end");
        return true;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    // 全局表用new Date（）
    public StoreDefectType saveOrUpdate(StoreDefectType storeDefectType, Long userId) {
        log.info(this.getClass().getSimpleName() + ".saveOrUpdate method begin");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_GLOBALSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param storeDefectType is {},userId is {}", storeDefectType.toString(), userId);
        }
        // 更新
        StoreDefectType s = null;
        int count = 0;
        if (log.isDebugEnabled()) {
            log.debug("if condition [null != storeDefectType.getId()] is", null != storeDefectType.getId());
        }
        if (null != storeDefectType.getId()) {
            s = storeDefectTypeDao.findById(storeDefectType.getId());
            storeDefectType.setCreateTime(s.getCreateTime());
            storeDefectType.setModifiedId(userId);
            storeDefectType.setCreatedId(s.getCreatedId());
            storeDefectType.setLifecycle(s.getLifecycle());// 该字段暂时保留，不对它做修改
            storeDefectType.setLastModifyTime(s.getLastModifyTime());
            // 用于全局表最后修改时间统一
            storeDefectType.setGlobalLastModifyTime(new Date());
            count = storeDefectTypeDao.saveOrUpdateByVersion(storeDefectType);
            // 修改失败
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return storeDefectType;
        } else {
            s = new StoreDefectType();
            s.setCreateTime(new Date());
            s.setCreatedId(userId);
            s.setStoreId(storeDefectType.getStoreId());
            s.setDescription(storeDefectType.getDescription());
            s.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
            s.setCode(storeDefectType.getCode());
            s.setName(storeDefectType.getName());
            s.setModifiedId(userId);
            s.setLastModifyTime(new Date());
            storeDefectTypeDao.insert(s);
        }
        log.info(this.getClass().getSimpleName() + ".saveOrUpdate method end");
        return s;
    }

    @Override
    public List<StoreDefectTypeCommand> findStoreDefectTypeByParam(StoreDefectType storeDefectType) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".findStoreDefectTypeByParam" + " start...");
        }
        if (log.isDebugEnabled()) {
            log.debug("PARAM: storeDefectType:[{}]", storeDefectType);
        }
        if (null == storeDefectType) {
            if (log.isErrorEnabled()) {
                log.error("FAILED: " + this.getClass().getSimpleName() + ".findStoreDefectTypeByParam, storeDefectType:[{}]", storeDefectType);
            }
            throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"storeDefectType"});
        }
        List<StoreDefectTypeCommand> storeDefectTypeList = new ArrayList<StoreDefectTypeCommand>();
        if (null != storeDefectType.getStoreId()) {
            if (log.isDebugEnabled()) {
                log.debug("IF: null != storeDefectType.getStoreId()");
            }
            storeDefectTypeList = storeDefectTypeDao.findStoreDefectTypeByParam(storeDefectType);
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".findStoreDefectTypeByParam" + " end...");
        }
        return storeDefectTypeList;
    }


    /**
     * 用于插入日志操作
     * 
     * @param userId
     * @param modifyTime
     * @param objectType
     * @param modifiedValues
     * @param type
     * @param ouId
     */

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


    /**
     * 根据店铺ID查询对应残次类型
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<StoreDefectTypeCommand> findStoreDefectTypesByStoreId(Long storeid) {
        log.info(this.getClass().getSimpleName() + ".findStoreDefectTypesByStoreId PARAM:[storeId:{}]", storeid);
        return storeDefectTypeDao.findStoreDefectTypesByStoreId(storeid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<Long> findStoreDefectTypeIdsByStoreId(Long storeId) {
        return storeDefectTypeDao.findStoreDefectTypeIdsByStoreId(storeId);
    }
}
