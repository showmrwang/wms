package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.SupplierCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.SupplierDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Supplier;

@Service("supplierManager")
@Transactional
public class SupplierManagerImpl implements SupplierManager {
    public static final Logger log = LoggerFactory.getLogger(SupplierManagerImpl.class);
    

    @Autowired
    private SupplierDao supplierDao;
    
    @Autowired
    private GlobalLogManager globalLogManager;

    @Override
    public Supplier getSupplierById(Long id) {
        log.info("SupplierManagerImpl getSupplierById is start");
        if(log.isDebugEnabled()){
            log.debug("Param id is {}", id);
        }
        log.info("SupplierManagerImpl getSupplierById is end");
        return supplierDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    //全局表用new Date（）
    public Supplier saveOrUpdate(Supplier supplier, Long userId) {
        log.info("SupplierManagerImpl saveOrUpdate is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_GLOBALSOURCE);
        if(log.isDebugEnabled()){
            log.debug("Param supplier is {}",supplier.toString());
        }
        if(log.isDebugEnabled()){
            log.debug("Param userId is {}", userId);
        }
        // 更新
        Supplier s = null;
        int count = 0;
        if(log.isDebugEnabled()){
            log.debug("if condition [null != supplier.getId()] is", null != supplier.getId());
        }
        if (null != supplier.getId()) {
            s = supplierDao.findById(supplier.getId());
            supplier.setCreateTime(s.getCreateTime());
            supplier.setOperatorId(userId);
            supplier.setLifecycle(supplier.getLifecycle());
            supplier.setLastModifyTime(s.getLastModifyTime());
            //用于全局表最后修改时间统一
            supplier.setGlobalLastModifyTime(new Date());
            count = supplierDao.saveOrUpdateByVersion(supplier);
            // 修改失败
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return supplier;
        } else {
            s = new Supplier();
            s.setCreateTime(new Date());
            s.setCode(supplier.getCode());
            s.setName(supplier.getName());
            s.setType(supplier.getType());
            s.setCustomerId(supplier.getCustomerId());
            s.setLevel(supplier.getLevel());
            s.setUser(supplier.getUser());
            s.setContact(supplier.getContact());
            s.setLifecycle(supplier.getLifecycle());
            s.setOperatorId(userId);
            s.setLastModifyTime(new Date());
            supplierDao.insert(s);
        }
        log.info("SupplierManagerImpl saveOrUpdate is end");
        return s;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Pagination<SupplierCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        log.info("SupplierManagerImpl getListByParams is start");
        if(log.isDebugEnabled()){
            log.info("Param param is {} ", param.toString());
        }
        Pagination<SupplierCommand> sList = supplierDao.findListByQueryMapWithPageExt(page, sorts, param);
        log.info("SupplierManagerImpl getListByParams is end");
        return sList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public void updateSupplierType(Long userId, Long id, Integer lifecycle) {
        log.info("SupplierManagerImpl updateSupplierType is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_GLOBALSOURCE);
        if(log.isDebugEnabled()){
            log.debug("Param userId is {}", userId);
        }
        if(log.isDebugEnabled()){
            log.debug("Param id is {}", id);
        }
        if(log.isDebugEnabled()){
            log.debug("Param lifecycle is {}", lifecycle);
        }
        Supplier s = supplierDao.findById(id);
        s.setLifecycle(lifecycle);
        s.setLastModifyTime(new Date());
        s.setOperatorId(userId);
        supplierDao.update(s);
        log.info("SupplierManagerImpl updateSupplierType is end");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid) {
        log.info("SupplierManagerImpl updateLifeCycle is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_GLOBALSOURCE);
        if(log.isDebugEnabled()){
            log.debug("Param ids is {}", Arrays.asList(ids));
        }
        if(log.isDebugEnabled()){
            log.debug("Param userid is {}", userid);
        }
        if(log.isDebugEnabled()){
            log.debug("Param lifeCycle is {}", lifeCycle);
        }
        int result = supplierDao.updateLifeCycle(ids, lifeCycle, userid, new Date());
        if(log.isDebugEnabled()){
            log.debug("if condition [result <= 0] is", result <= 0);
        }
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if(log.isDebugEnabled()){
            log.debug("if condition [result != ids.size()] is", result != ids.size());
        }
        if (result != ids.size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {ids.size(), result});
        }
        log.info("SupplierManagerImpl updateLifeCycle is end");
        return result;
    }

    @Override
    public List<Supplier> findListByParam(Supplier supplier) {
        log.info("SupplierManagerImpl findListByParam is start");
        if(log.isDebugEnabled()){
            log.debug("Param supplier is {}",supplier.toString());
        }
        log.info("SupplierManagerImpl findListByParam is end");
        return this.supplierDao.findListByParam(supplier);
    }

    @Override
    public List<Supplier> findListByLifecycle(Integer lifecycle) {
        log.info("SupplierManagerImpl findListByLifecycle is start");
        if(log.isDebugEnabled()){
            log.debug("Param lifecycle is {}", lifecycle);
        }
        Supplier supplier = new Supplier();
        supplier.setLifecycle(lifecycle);
        log.info("SupplierManagerImpl findListByLifecycle is end");
        return this.supplierDao.findListByParam(supplier);
    }

    @Override
    public Map<Long, String> getNameIdMapByIds(List<Long> ids) {
        log.info("SupplierManagerImpl getNameIdMapByIds is start");
        if(log.isDebugEnabled()){
            log.debug("Param ids is {}", Arrays.asList(ids));
        }
        Map<Long, String> map = new HashMap<Long, String>();
        List<Supplier> suppliers = this.supplierDao.getByIds(ids);
        if(log.isDebugEnabled()){
            log.debug("List Supplier size is {}",suppliers.size());
        }
        for (Supplier s : suppliers) {
            if(log.isDebugEnabled()){
                log.debug("Supplier object String is {}", s.toString());
            }
            map.put(s.getId(), s.getName());
        }
        log.info("SupplierManagerImpl getNameIdMapByIds is end");
        return map;
    }

    @Override
    public Boolean uniqueCodeOrName(Supplier supplier) {
        log.info("SupplierManagerImpl uniqueCodeOrName is start");
        if(log.isDebugEnabled()){
            log.debug("Param supplier is {}",supplier.toString());
        }
        Supplier querySupplier = new Supplier();
        querySupplier.setId(supplier.getId());
        if(log.isDebugEnabled()){
            log.debug("if condition [null != supplier.getCode()] is", null != supplier.getCode());
        }
        if (null != supplier.getCode()) {
            querySupplier.setCode(supplier.getCode());
            querySupplier.setName(null);
            long count = supplierDao.uniqueCodeOrName(querySupplier);
            if (0 != count) {
                return false;
            }
        }
        if(log.isDebugEnabled()){
            log.debug("if condition [null != supplier.getName()] is", null != supplier.getName());
        }
        if (null != supplier.getName()) {
            querySupplier.setCode(null);
            querySupplier.setName(supplier.getName());
            long count = supplierDao.uniqueCodeOrName(querySupplier);
            if (0 != count) {
                return false;
            }
        }
        log.info("SupplierManagerImpl uniqueCodeOrName is end");
        return true;
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
        log.info("insertGlobalLog");
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(userId);
        gl.setModifyTime(modifyTime);
        gl.setObjectType(objectType);
        gl.setModifiedValues(modifiedValues);
        gl.setType(type);
        gl.setOuId(ouId);
        if (log.isDebugEnabled()) {
            log.debug("insertGlobalLog as {}", gl);
        }
        globalLogManager.insertGlobalLog(gl);
    }

    @Override
    public List<Supplier> findSupplierListByIds(List<Long> ids) {
        return supplierDao.getByIds(ids);
    }

    /**
     * 查询权限下，所有启用的供应商
     * @param customerList
     * @param lifecycle
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<Supplier> getSupplierByIds(List<Long> customerList, Integer lifecycle) {
        // TODO Auto-generated method stub
        log.info("SupplierManagerImpl getSupplierByIds is start");
        if(log.isDebugEnabled()) {
            log.debug("SupplierManagerImpl getSupplierByIds Param is [customerList is{}],[lifecycle is{}]",customerList,lifecycle);
        }
        List<Supplier> list = supplierDao.getSupplierByIds(customerList, lifecycle);
        log.info("SupplierManagerImpl getSupplierByIds is end");
        return list;
    }
}
