package com.baozun.scm.primservice.whoperation.manager;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionInBoundDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPutAwayDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionRcvdDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunction;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionInBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;


@Service("whFunctionManager")
@Transactional
public class WhFunctionManagerImpl extends BaseManagerImpl implements WhFunctionManager {

    public static final Logger log = LoggerFactory.getLogger(WhFunctionManagerImpl.class);

    @Autowired
    private WhFunctionDao whFunctionDao;
    @Autowired
    private WhFunctionInBoundDao whFunctionInBoundDao;
    @Autowired
    private WhFunctionRcvdDao whFunctionRcvdDao;
    @Autowired
    private WhFunctionPutAwayDao whFunctionPutAwayDao;

    /**
     * 查询功能维护列表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhFunctionCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        return whFunctionDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 根据ID查找功能信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunction findWhFunctionById(Long id, Long ouid) {
        return whFunctionDao.findWhFunctionById(id, ouid);
    }

    /**
     * 验证名称和编码
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionCommand checkNameOrCode(String name, String code, String templet, Long ouid) {
        return whFunctionDao.checkNameOrCode(name, code, templet, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhFunction> findWhFunctionByParam(WhFunction whFunction) {
        return whFunctionDao.findListByParam(whFunction);
    }

    /**
     * 查询所有功能 可用并且不是系统级数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhFunction> findWhFunctionListNotIsSys(Long ouid) {
        return whFunctionDao.findWhFunctionListNotIsSys(ouid);
    }

    /**
     * 批量操作功能状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid, Long ouid) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".updateLifeCycle" + " start...");
        }
        if (log.isDebugEnabled()) {
            log.debug("PARAM: ids: " + ids.size() + " lifeCycle: " + lifeCycle + " userid: " + userid + "ouid: " + ouid);
        }
        int result = 0;
        for (Long id : ids) {
            WhFunction wf = whFunctionDao.findWhFunctionById(id, ouid);
            if (!wf.getIsSys()) {
                // 不是系统默认参数的才允许修改状态
                wf.setLifecycle(lifeCycle);
                wf.setOperatorId(userid);
                int count = whFunctionDao.saveOrUpdateByVersion(wf);
                if (count <= 0) {
                    if (log.isErrorEnabled()) {
                        log.error("FAILED: " + this.getClass().getSimpleName() + "updateLifeCycle, WhFunction id:[{}]" + id);
                    }
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入系统日志表
                insertGlobalLog(GLOBAL_LOG_UPDATE, wf, ouid, userid, null, null);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".updateLifeCycle" + " end...");
        }
        return result;
    }

    /**
     * 删除功能
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteFunction(List<Long> ids, Long userid, Long ouid) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".deleteFunction" + " start...");
        }
        if (log.isDebugEnabled()) {
            log.debug("PARAM: ids: " + ids.size() + " userid: " + userid + "ouid: " + ouid);
        }
        for (Long id : ids) {
            WhFunction wf = whFunctionDao.findWhFunctionById(id, ouid);
            if (!wf.getIsSys()) {
                // 不是系统默认参数的才允许删除
                // 插入系统日志表
                insertGlobalLog(GLOBAL_LOG_DELETE, wf, ouid, userid, null, null);
                if (wf.getFunctionTemplet().equals(Constants.FUNCTION_TEMPLET_INBOUND)) {
                    // 入库分拣 删除对应子表
                    WhFunctionInBound inBound = whFunctionInBoundDao.findwFunctionInBoundByFunctionId(id, ouid);
                    // 插入系统日志表
                    insertGlobalLog(GLOBAL_LOG_DELETE, inBound, ouid, userid, wf.getFunctionCode(), null);
                    whFunctionInBoundDao.deleteWhFunctionInBoundByFunctionId(id, ouid);
                }
                if (wf.getFunctionTemplet().equals(Constants.FUNCTION_TEMPLET_RECEIVE)) {
                    // 收货功能 删除对应子表
                    WhFunctionRcvd rcvd = whFunctionRcvdDao.findwFunctionRcvdByFunctionId(id, ouid);
                    // 插入系统日志表
                    insertGlobalLog(GLOBAL_LOG_DELETE, rcvd, ouid, userid, wf.getFunctionCode(), null);
                    whFunctionRcvdDao.deleteWhFunctionRcvdByFunctionId(id, ouid);
                }
                if (wf.getFunctionTemplet().equals(Constants.FUNCTION_TEMPLET_SHELF)) {
                    // 上架功能 删除对应子表
                    WhFunctionPutAway putAway = whFunctionPutAwayDao.findWhFunctionPutAwayByFunctionId(id, ouid);
                    // 插入系统日志表
                    insertGlobalLog(GLOBAL_LOG_DELETE, putAway, ouid, userid, wf.getFunctionCode(), null);
                    whFunctionPutAwayDao.deleteWhFunctionPutAwayByFunctionId(id, ouid);
                }
                // 删除主表
                whFunctionDao.deleteFunction(id, ouid);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".deleteFunction" + " end...");
        }
    }
}
