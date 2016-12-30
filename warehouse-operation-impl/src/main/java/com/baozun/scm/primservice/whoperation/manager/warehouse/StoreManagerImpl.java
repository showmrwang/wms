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
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.auth.OperationUnitDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;

@Service("storeManager")
@Transactional
public class StoreManagerImpl extends BaseManagerImpl implements StoreManager {
    public static final Logger log = LoggerFactory.getLogger(StoreManagerImpl.class);
    
    @Autowired
    private OperationUnitDao operationUnitDao;

    @Autowired
    private StoreDao storeDao;
    
    @Autowired
    private PkManager pkManager;
    
    @Autowired
    private GlobalLogManager globalLogManager;

    @Override
    public Store getStoreById(Long id) {
        log.info("StoreManagerImpl getStoreById is start");
        if(log.isDebugEnabled()){
            log.debug("Param id is {}", id);
        }
        log.info("StoreManagerImpl getStoreById is end");
        return storeDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    //全局表用new Date（）
    public Store saveOrUpdate(Store store, Long userId) {
        log.info("StoreManagerImpl saveOrUpdate is start");
        log.info("Switch to use data source {}", DbDataSource.MOREDB_GLOBALSOURCE);
        if(log.isDebugEnabled()){
            log.debug("Param store is {}", store.toString());
        }
        if(log.isDebugEnabled()){
            log.debug("Param userId is {}", userId);
        }
        // 更新
        Store s = null;
        int count = 0;
        if(log.isDebugEnabled()){
            log.debug("if condition [null != store.getId()] is", null != store.getId());
        }
        if (null != store.getId()) {
            s = storeDao.findById(store.getId());
            store.setCreateTime(s.getCreateTime());
            store.setOperatorId(userId);
            store.setLifecycle(store.getLifecycle());
            store.setLastModifyTime(s.getLastModifyTime());
            //用于全局表最后修改时间统一
            store.setGlobalLastModifyTime(new Date());
            count = storeDao.saveOrUpdateByVersion(store);
            // 修改失败
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR, new Object[] {"store"});
            }
            return store;
        } else {
            s = new Store();
            s.setCreateTime(new Date());
            s.setDescription(store.getDescription());
            s.setInvoiceType(store.getInvoiceType());
            s.setLifecycle(store.getLifecycle());
            s.setPaymentTerm(store.getPaymentTerm());
            s.setIsMandatorilyReserved(store.getIsMandatorilyReserved());
            s.setIsPoOvercharge(store.getIsPoOvercharge());
            s.setPoOverchargeProportion(store.getPoOverchargeProportion());
            s.setIsAsnOvercharge(store.getIsAsnOvercharge());
            s.setAsnOverchargeProportion(store.getAsnOverchargeProportion());
            s.setIsPoAutoVerify(store.getIsPoAutoVerify());
            s.setIsAsnAutoVerify(store.getIsAsnAutoVerify());
            s.setGoodsReceiptMode(store.getGoodsReceiptMode());
            s.setIsAutoPrintBintag(store.getIsAutoPrintBintag());
            s.setIsAutoGenerationCn(store.getIsAutoGenerationCn());
            s.setIsAllowBlocked(store.getIsAllowBlocked());
            s.setInvAttrMgmt(store.getInvAttrMgmt());
            s.setIsAllowCollectDiff(store.getIsAllowCollectDiff());
            s.setIsAutoPrintDiff(store.getIsAutoPrintDiff());
            s.setIsHintQualityTesting(store.getIsHintQualityTesting());
            s.setIsAutoPrintGoodsReceipt(store.getIsAutoPrintGoodsReceipt());
            s.setCustomerId(store.getCustomerId());
            s.setPic(store.getPic());
            s.setPicContact(store.getPicContact());
            s.setStoreCode(store.getStoreCode());
            s.setStoreName(store.getStoreName());
            s.setOperatorId(userId);
            s.setLastModifyTime(new Date());
            storeDao.insert(s);
        }
        log.info("StoreManagerImpl saveOrUpdate is end");
        return s;
    }

    @Override
    public Pagination<StoreCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param) {
        log.info("StoreManagerImpl getListByParams is start");
        if(log.isDebugEnabled()){
            log.info("Param param is {} ", param.toString());
        }
        Pagination<StoreCommand> sList = storeDao.findListByQueryMapWithPageExt(page, sorts, param);
        log.info("StoreManagerImpl getListByParams is end");
        return sList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid) {
        log.info("StoreManagerImpl updateLifeCycle is start");
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
        int result = storeDao.updateLifeCycle(ids, lifeCycle, userid, new Date());
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
        log.info("StoreManagerImpl updateLifeCycle is end");
        return result;
    }

    @Override
    public List<Store> findStoreListByCustomerId(Long customerId) {
        log.info("StoreManagerImpl findStoreListByCustomerId is start");
        if(log.isDebugEnabled()){
            log.debug("Param customerId is {}", customerId);
        }
        Store store = new Store();
        store.setCustomerId(customerId);
        store.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<Store> storeList = storeDao.findListByParam(store);
        log.info("StoreManagerImpl findStoreListByCustomerId is end");
        return storeList;
    }

    @Override
    public List<Store> findStoreListByUserIdCustomerId(Long customerId, Long userId) {
        log.info("StoreManagerImpl findStoreListByUserIdCustomerId is start");
        if(log.isDebugEnabled()){
            log.debug("Param customerId is {}", customerId);
        }
        if(log.isDebugEnabled()){
            log.debug("Param userId is {}", userId);
        }
        StoreCommand command = new StoreCommand();
        command.setCustomerId(customerId);
        command.setUserId(userId);
        command.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        List<Store> sList = this.storeDao.findStoreListByParams(command);
        log.info("StoreManagerImpl findStoreListByUserIdCustomerId is end");
        return sList;
    }

    @Override
    public List<StoreCommand> findDataPrivilegeListByParams(Long userId) {
        log.info("StoreManagerImpl findDataPrivilegeListByParams is start");
        if(log.isDebugEnabled()){
            log.debug("Param userId is {}", userId);
        }
        List<StoreCommand> sCommandList = this.storeDao.findDataPrivilegeListByParams(userId);
        log.info("StoreManagerImpl findDataPrivilegeListByParams is end");
        return sCommandList;
    }
    
    @Override
    public Map<Long, String> getNameIdMapByIds(List<Long> ids) {
        log.info("StoreManagerImpl getNameIdMapByIds is start");
        if(log.isDebugEnabled()){
            log.debug("Param ids is {}", Arrays.asList(ids));
        }
        Map<Long, String> map = new HashMap<Long, String>();
        List<Store> stores = this.storeDao.getByIds(ids);
        if(log.isDebugEnabled()){
            log.debug("List Store size is {}", stores.size());
        }
        for (Store s : stores) {
            if(log.isDebugEnabled()){
                log.debug("Store object String is {}", s.toString());
            }
            map.put(s.getId(), s.getStoreName());
        }
        log.info("StoreManagerImpl getNameIdMapByIds is end");
        return map;
    }

    @Override
    public Boolean uniqueCodeOrName(Store store) {
        log.info("StoreManagerImpl uniqueCodeOrName is start");
        if(log.isDebugEnabled()){
            log.debug("Param store is {}",store.toString());
        }
        Store queryStore = new Store();
        queryStore.setId(store.getId());
        if(log.isDebugEnabled()){
            log.debug("if condition [null != store.getStoreCode()] is", null != store.getStoreCode());
        }
        if (null != store.getStoreCode()) {
            queryStore.setStoreCode(store.getStoreCode());
            queryStore.setStoreName(null);
            long count = storeDao.uniqueCodeOrName(queryStore);
            if (0 != count) {
                return false;
            }
        }
        if(log.isDebugEnabled()){
            log.debug("if condition [null != store.getStoreName()] is", null != store.getStoreName());
        }
        if (null != store.getStoreName()) {
            queryStore.setStoreCode(null);
            queryStore.setStoreName(store.getStoreName());
            long count = storeDao.uniqueCodeOrName(queryStore);
            if (0 != count) {
                return false;
            }
        }
        log.info("StoreManagerImpl uniqueCodeOrName is end");
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
    public List<Store> findStoreListByIds(List<Long> ids) {
        return storeDao.getByIds(ids);
    }

    @Override
    public Store findStoreById(Long id) {
        return storeDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Store findStoreByCode(String storeCode) {
        return this.storeDao.findByCode(storeCode);
    }

    @Override
    public Boolean checkCustomerStoreUser(Long customerId, Long storeId, Long userId) {
        try {

            StoreCommand command = new StoreCommand();
            command.setCustomerId(customerId);
            command.setUserId(userId);
            command.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
            command.setId(storeId);
            List<Store> sList = this.storeDao.findStoreListByParams(command);
            if (sList == null || sList.size() == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(e + "");
            return false;
        }
    }
}
