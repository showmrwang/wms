package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
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
import com.baozun.scm.primservice.whoperation.command.warehouse.PlatformCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.PlatformDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

@Service("platformManager")
@Transactional
public class PlatformManagerImpl implements PlatformManager {
    public static final Logger log = LoggerFactory.getLogger(PlatformManagerImpl.class);

    @Autowired
    private PlatformDao platformDao;

    @Autowired
    private GlobalLogManager globalLogManager;

    /**
     * 通过参数查询月台分页列表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<PlatformCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl getListByParams is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("getListByParams param [page:{}, sorts:{}, param:{}] ", ParamsUtil.page2String(page), ParamsUtil.sorts2String(sorts), param);
        }

        Pagination<PlatformCommand> pagination = platformDao.findListByQueryMapWithPageExt(page, sorts, param);
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl getListByParams is end");
        }
        return pagination;
    }

    /**
     * 检验月台名称或编码是否唯一
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkUnique(Platform platform, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl checkUnique is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("checkUnique param [platform:{}, ouId:{}] ", platform, ouId);
        }

        boolean result = true;
        if (null != platform && null != ouId) {
            platform.setOuId(ouId);
            long count = platformDao.checkUnique(platform);
            if (0 != count) {
                result = false;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl checkUnique is end");
        }
        return result;
    }

    /**
     * 创建或修改月台信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Platform saveOrUpdate(Platform platform, Long userId, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl saveOrUpdate is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("saveOrUpdate param [platform:{}, userId:{}, ouId:{}] ", platform, userId, ouId);
        }

        if (null == platform || null == userId || null == ouId) {
            log.error("PlatformManagerImpl saveOrUpdate failed, param is null, param [platform:{}, userId:{}, ouId:{}] ", platform, userId, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }

        if (null != platform.getId()) {
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate is exists, excuse update, param [platform:{}, userId:{}, ouId:{}] ", platform, userId, ouId);
            }
            Platform originPlatform = platformDao.findByIdExt(platform.getId(), ouId);
            if (null == originPlatform) {
                log.error("PlatformManagerImpl saveOrUpdate failed, originPlatform is null, param [platform:{}, userId:{}, ouId:{}] ", platform, userId, ouId);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originPlatform.setPlatformCode(platform.getPlatformCode());
            originPlatform.setPlatformName(platform.getPlatformName());
            originPlatform.setPlatformType(platform.getPlatformType());
            originPlatform.setLifecycle(platform.getLifecycle());
            originPlatform.setModifiedId(userId);
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate, update to sharedDB, param [platform:{}, userId:{}, ouId:{}, originPlatform:{}] ", platform, userId, ouId, originPlatform);
            }
            int count = platformDao.saveOrUpdateByVersion(originPlatform);
            if (count != 1) {
                log.error("PlatformManagerImpl saveOrUpdate failed, update count != 1, param [platform:{}, userId:{}, ouId:{}, originPlatform:{}, count:{}] ", platform, userId, ouId, originPlatform, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            platform = originPlatform;
            // 保存更新数据历史
            saveUpdateLog(platform, Constants.GLOBAL_LOG_UPDATE);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate, originPlatform is null, excuse insert, param [platform:{}, userId:{}, ouId:{}] ", platform, userId, ouId);
            }

            platform.setOuId(ouId);
            platform.setOccupationCode(null);
            platform.setIsOccupied(false);
            platform.setCreateTime(new Date());
            platform.setCreatedId(userId);
            platform.setLastModifyTime(new Date());
            platform.setModifiedId(userId);
            if (log.isDebugEnabled()) {
                log.debug("saveOrUpdate,originPlatform is null, insert to sharedDB, param [platform:{}, userId:{}, ouId:{}] ", platform, userId, ouId);
            }

            platformDao.insert(platform);
            // 保存更新数据历史
            saveUpdateLog(platform, Constants.GLOBAL_LOG_INSERT);
        }

        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl saveOrUpdate is end");
        }
        return platform;
    }


    /**
     * 启用/停用月台
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl updateLifeCycle is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("updateLifeCycle param [ids:{}, lifecycle:{}, userId:{}, ouId:{}] ", ids, lifeCycle, userId, ouId);
        }

        if (null == ids || ids.isEmpty() || null == lifeCycle || null == userId) {
            log.error("PlatformManagerImpl updateLifeCycle failed, param [ids:{}, lifecycle:{}, userId:{}, ouId:{}] ", ids, lifeCycle, userId, ouId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }

        if (log.isDebugEnabled()) {
            log.debug("updateLifeCycle loop param [ids:{}]", ids);
        }
        for (Long id : ids) {
            Platform originPlatform = platformDao.findByIdExt(id, ouId);
            if (null == originPlatform) {
                log.error("PlatformManagerImpl updateLifeCycle failed, originPlatform is null, param [ids:{}, lifecycle:{}, userId:{}, ouId:{}, id:{}] ", ids, lifeCycle, userId, ouId, id);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            originPlatform.setLifecycle(lifeCycle);
            originPlatform.setOuId(ouId);
            originPlatform.setModifiedId(userId);
            int count = platformDao.saveOrUpdateByVersion(originPlatform);
            if (count != 1) {
                log.error("PlatformManagerImpl updateLifeCycle failed, update count != 1, param [ids:{}, lifecycle:{}, userId:{}, ouId:{}, id:{}, count:{}] ", ids, lifeCycle, userId, ouId, id, count);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            // 保存更新数据历史
            saveUpdateLog(originPlatform, Constants.GLOBAL_LOG_UPDATE);
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl updateLifeCycle is end");
        }
    }

    /**
     * 根据id查找月台
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Platform findPlatformById(Long id, Long ouId) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findPlatformById is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findPlatformById param [id:{}, ouId:{}] ", id, ouId);
        }
        Platform platform = null;
        if (null != id && null != ouId) {
            platform = platformDao.findByIdExt(id, ouId);
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findPlatformById is end");
        }
        return platform;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Platform> findListByPlatformType(Long platformType, Long ouId, Integer lifecycle) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findListByPlatformType is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findListByPlatformType param [platformType:{}, ouId:{}, lifecycle:{}] ", platformType, ouId, lifecycle);
        }
        List<Platform> platformList = new ArrayList<>();
        if (null != platformType && null != ouId && null != lifecycle) {
            platformList = platformDao.findListByPlatformType(platformType, ouId, lifecycle);
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findListByPlatformType is end");
        }
        return platformList;
    }

    /**
     * 保存更新数据历史
     *
     * @author mingwei.xie
     * @param platform
     */
    private void saveUpdateLog(Platform platform, String operationType) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl saveUpdateLog is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("saveUpdateLog param [platform:{}]", platform);
        }
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(platform.getModifiedId());
        gl.setObjectType(platform.getClass().getSimpleName());
        gl.setModifiedValues(platform);
        gl.setOuId(platform.getOuId());
        gl.setType(operationType);
        if (log.isDebugEnabled()) {
            log.debug("PlatformManagerImpl saveUpdateLog, insert to sharedDB, param [platform:{}, globalLogCommand:{} ]", platform, gl);
        }
        globalLogManager.insertGlobalLog(gl);

        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl saveUpdateLog is end");
        }
    }

    /**
     * 查找空闲月台
     *
     * @param ouId
     * @param lifecycle
     * @return
     * @author mingwei.xie
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Override
    public List<Platform> findVacantPlatform(Long ouId, Integer lifecycle) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findVacantPlatform is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findVacantPlatform param [ouId:{}, lifecycle:{}]", ouId, lifecycle);
        }

        List<Platform> platformList = new ArrayList<>();
        if (null != ouId && null != lifecycle) {
            Platform platform = new Platform();
            platform.setOuId(ouId);
            platform.setLifecycle(lifecycle);
            platform.setIsOccupied(false);

            platformList = platformDao.findListByParam(platform);
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findVacantPlatform is end");
        }
        return platformList;
    }

    /**
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @param logId
     * @return
     */
    public List<Platform> findOccupiedPlatform(Long ouId, Integer lifecycle, String logId) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findOccupiedPlatform is start");
        }
        if (log.isDebugEnabled()) {
            log.debug("findOccupiedPlatform param [ouId:{}, lifecycle:{}]", ouId, lifecycle);
        }

        List<Platform> platformList = new ArrayList<>();
        if (null != ouId && null != lifecycle) {
            Platform platform = new Platform();
            platform.setOuId(ouId);
            platform.setLifecycle(lifecycle);
            platform.setIsOccupied(true);

            platformList = platformDao.findListByParam(platform);
        }
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findOccupiedPlatform is end");
        }
        return platformList;
    }

    /**
     * 根据占用码查询月台
     *
     * @author mingwei.xie
     * @param occupationCode
     * @param ouId
     * @param lifecycle
     * @return
     */
    @Override
    public Platform findByOccupationCode(String occupationCode, Long ouId, Integer lifecycle) {
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findByOccupationCode is start");
        }
        if(null == occupationCode || null == ouId || null == lifecycle){
            log.error("PlatformManagerImpl findByOccupationCode error, param is null, param [occupationCode:{}, ouId:{}, lifecycle:{}]", occupationCode, ouId, lifecycle);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("findByOccupationCode param [occupationCode:{}, ouId:{}, lifecycle:{}]", occupationCode, ouId, lifecycle);
        }
        Platform platform = platformDao.findByOccupationCode(occupationCode, ouId, lifecycle);
        if (log.isInfoEnabled()) {
            log.info("PlatformManagerImpl findOccupiedPlatform is end");
        }
        return platform;
    }
}
