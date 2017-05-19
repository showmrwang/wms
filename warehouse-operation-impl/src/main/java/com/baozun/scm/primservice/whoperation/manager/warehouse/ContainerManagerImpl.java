package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;

@Service("containerManager")
@Transactional
public class ContainerManagerImpl extends BaseManagerImpl implements ContainerManager {

    public static final Logger log = LoggerFactory.getLogger(ContainerManagerImpl.class);

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private PkManager pkManager;

    @Autowired
    private GlobalLogManager globalLogManager;

    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<ContainerCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        log.info("ContainerManagerImpl getListByParams is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param param is {}", param.toString());
        }
        Pagination<ContainerCommand> sList = containerDao.findListByQueryMapWithPageExt(page, sorts, param);
        log.info("ContainerManagerImpl getListByParams is end");
        return sList;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Container getContainerById(Long id, Long ouId) {
        log.info("ContainerManagerImpl getContainerById is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_SHARDSOURCE);
        if (log.isDebugEnabled()) {
            log.debug("Param id is {}", id);
        }
        if (log.isDebugEnabled()) {
            log.debug("Param ouId is {}", ouId);
        }
        Container c = containerDao.findByIdExt(id, ouId);
        log.info("ContainerManagerImpl getContainerById is end");
        return c;
    }




    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand getContainerByCode(String code, Long ouId) {
        if (null == code) {
            log.error("ContainerManager.getContainerByCode,params code is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == ouId) {
            log.error("warehouseDefectTypeManager.findDefectTypeByParams, params ouId is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        ContainerCommand cc = null;
        try {
            cc = containerDao.getContainerByCode(code, ouId);
            if (null == cc) {
                throw new BusinessException(ErrorCodes.OBJECT_IS_NULL, new Object[] {"ContainerCommand"});
            }
        } catch (Exception e) {
            log.error(getLogMsg("ContainerManager.getContainerByCode->containerDao.getContainerByCode invoke throw exception, params are:[{}],[{}]", new Object[] {code, ouId}), e);
            BusinessException be = new BusinessException(ErrorCodes.DAO_EXCEPTION);
            BusinessException dbe = new BusinessException(ErrorCodes.PALLET_CODE_NOT_FIND);
            be.setLinkedException(dbe);
            throw be;
        }
        return cc;
    }



    /***
     * 根据容器编码查找容器 无判断
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand findContainerByCode(String code, Long ouId) {
        return containerDao.getContainerByCode(code, ouId);
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int saveOrUpdateByVersion(Container container) {
        int updateCount = this.containerDao.saveOrUpdateByVersion(container);
        if (updateCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        return updateCount;
    }





}
