package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WarehouseCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.auth.OperationUnitDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WarehouseDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.auth.OperationUnit;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;


@Service("warehouseManager")
@Transactional
public class WarehouseManagerImpl extends BaseManagerImpl implements WarehouseManager {

    public static final Logger log = LoggerFactory.getLogger(WarehouseManagerImpl.class);

    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private OperationUnitDao operationUnitDao;

    /**
     * 验证仓库名称/编码是否存在
     */
    @Override
    public WarehouseCommand checkNameOrCode(String name, String code) {
        return warehouseDao.checkNameOrCode(name, code);
    }

    /**
     * 保存/修改仓库信息
     */
    @Override
    public Warehouse saveOrUpdate(Warehouse warehouse, Long userId) {
        // 修改数据
        Warehouse w = warehouseDao.findById(warehouse.getId());
        if (null == w) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (null == warehouse.getRentPrice()) {
            warehouse.setRentPrice(null);
            warehouse.setRentPriceUom(null);
        }
        warehouse.setLastModifyTime(w.getLastModifyTime());
        warehouse.setCreateTime(w.getCreateTime());
        warehouse.setOperatorId(userId);
        int count = warehouseDao.saveOrUpdateByVersion(warehouse);
        // 修改失败
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入系统日志表
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, warehouse, null, userId, null, null);
        return w;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Pagination<WarehouseCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<WarehouseCommand> wList = warehouseDao.findListByQueryMapWithPageExt(page, sorts, params);
        for (WarehouseCommand w : wList.getItems()) {
            // 插入对应运营中心
            OperationUnit ou = operationUnitDao.findById(w.getParentOuId());
            w.setOuName(ou.getName());
        }
        return wList;
    }

    /**
     * 通过ID查询仓库信息
     */
    @Override
    public Warehouse findWarehouseById(Long id) {
        return warehouseDao.findWarehouseById(id);
    }


    /**
     * 修改仓库状态
     */
    @Override
    public void updateWhType(Long userId, Long whId, Integer lifecycle) {
        Warehouse w = warehouseDao.findById(whId);
        if (null == w) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        w.setLifecycle(lifecycle);
        w.setLastModifyTime(new Date());
        w.setOperatorId(userId);
        warehouseDao.update(w);
        // 插入系统日志表
        insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, w, null, userId, null, null);
    }

    /**
     * 批量修改仓库状态
     */
    @Override
    public int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid) {
        int result = warehouseDao.updateLifeCycle(ids, lifeCycle, userid);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != ids.size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {ids.size(), result});
        }
        // 插入系统日志表
        for (Long id : ids) {
            Warehouse w = warehouseDao.findById(id);
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, w, null, userid, null, null);
        }
        return result;
    }

    @Override
    public List<Warehouse> findListByParam(Warehouse warehouse) {
        return warehouseDao.findListByParam(warehouse);
    }

    /**
     * 集团下配置仓库信息
     */
    @Override
    public OperationUnit saveOrUpdateBloc(OperationUnit operationUnit, Long userId) {
        int lifecycle = operationUnit.getLifecycle();
        if (null != operationUnit.getId()) {
            OperationUnit ou = operationUnitDao.findById(operationUnit.getId());
            if (null == ou) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            operationUnit.setOuTypeId(3L);
            operationUnit.setFullName(operationUnit.getName());
            operationUnit.setLastModifyTime(ou.getLastModifyTime());
            operationUnit.setLifecycle(ou.getLifecycle());
            int count = 0;
            count = operationUnitDao.saveOrUpdateByVersion(operationUnit);
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            Warehouse w = warehouseDao.findWarehouseByCode(operationUnit.getCode());
            if (null == w) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            w.setCode(operationUnit.getCode());
            w.setName(operationUnit.getName());
            w.setLastModifyTime(w.getLastModifyTime());
            w.setOperatorId(userId);
            w.setLifecycle(lifecycle);
            count = warehouseDao.saveOrUpdateByVersion(w);
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入系统日志表
            insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, w, null, userId, null, null);
            return operationUnit;
        } else {
            operationUnit.setOuTypeId(3L);
            operationUnit.setFullName(operationUnit.getName());
            operationUnit.setLastModifyTime(new Date());
            operationUnit.setLifecycle(1);
            operationUnitDao.saveOrUpdate(operationUnit);
            Warehouse w = new Warehouse();
            w.setCode(operationUnit.getCode());
            w.setName(operationUnit.getName());
            w.setCreateTime(new Date());
            w.setLastModifyTime(new Date());
            w.setOperatorId(userId);
            w.setLifecycle(lifecycle);
            warehouseDao.saveOrUpdate(w);
            // 插入系统日志表
            insertGlobalLog(Constants.GLOBAL_LOG_INSERT, w, null, userId, null, null);
            return operationUnit;
        }
    }

    /**
     * 通过code查询对应仓库信息
     */
    @Override
    public Warehouse findWarehouseByCode(String code) {
        return warehouseDao.findWarehouseByCode(code);
    }

    @Override
    public List<Warehouse> findListByLifecycle(Integer lifecycle) {
        if (log.isInfoEnabled()) {
            log.info("WarehouseManagerImpl findListByLifecycle start");
        }
        if(log.isDebugEnabled()){
            log.debug("findListByLifecycle param [lifecycle:{}]", lifecycle);
        }
        if(null == lifecycle){
            log.error("WarehouseManagerImpl findListByLifecycle failed, param lifecycle is null");
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        List<Warehouse> warehouseList = warehouseDao.findListByLifecycle(lifecycle);

        if (log.isInfoEnabled()) {
            log.info("WarehouseManagerImpl findListByLifecycle end");
        }
        return warehouseList;
    }

    /**
     * UAC同步仓库信息
     */
    @Override
    public boolean syncWarehouse(OperationUnit ou) {
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".syncWarehouse" + " start...");
        }
        Warehouse wh = new Warehouse();
        BeanUtils.copyProperties(ou, wh);
        if (log.isDebugEnabled()) {
            log.debug("PARAM: Warehouse: " + wh.toString());
        }
        Warehouse warehouse = warehouseDao.findById(wh.getId());
        if (null == warehouse) {
            if (log.isDebugEnabled()) {
                log.debug("IF: warehouse is null");
            }
            // 没有相关仓库信息 新增
            wh.setCreateTime(new Date());
            wh.setLastModifyTime(new Date());
            Long count = warehouseDao.insert(wh);
            if (count.intValue() == 0) {
                // 新增失败
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "syncWarehouse add, Warehouse id:[{}]" + wh.getId());
                }
                return false;
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("IF: warehouse not null");
            }
            // 有相关仓库信息 修改
            warehouse.setCode(wh.getCode());
            warehouse.setName(wh.getName());
            warehouse.setLifecycle(wh.getLifecycle());
            int count = warehouseDao.saveOrUpdate(warehouse);
            if (count == 0) {
                // 修改失败
                if (log.isErrorEnabled()) {
                    log.error("FAILED: " + this.getClass().getSimpleName() + "syncWarehouse edit, Warehouse id:[{}]" + wh.getId());
                }
                return false;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("METHOD: " + this.getClass().getSimpleName() + ".syncWarehouse" + " start...");
        }
        return true;
    }

}
