/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWavePhaseDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.CustomerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WarehouseDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryFlowManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventorySnLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;
import com.baozun.scm.primservice.whoperation.util.JsonUtil;
import com.baozun.scm.primservice.whoperation.util.LogUtil;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

/**
 * @author lichuan
 *
 */
public abstract class BaseManagerImpl implements BaseManager {
    // log不支持继承
    private static final Logger log = LoggerFactory.getLogger(BaseManagerImpl.class);
    protected static final String GLOBAL_LOG_UPDATE = Constants.GLOBAL_LOG_UPDATE;
    protected static final String GLOBAL_LOG_INSERT = Constants.GLOBAL_LOG_INSERT;
    protected static final String GLOBAL_LOG_DELETE = Constants.GLOBAL_LOG_DELETE;

    protected String logId = "";
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;
    @Autowired
    private WhSkuInventorySnLogManager whSkuInventorySnLogManager;
    @Autowired
    private SysDictionaryDao sysDictionaryDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Autowired
    private WhWavePhaseDao whWavePhaseDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private WhSkuInventoryFlowManager whSkuInventoryFlowManager;


    /**
     * 全局日志
     * 
     * @author lichuan
     * @param dml
     * @param model
     * @param ouId
     * @param userId
     */
    protected void insertGlobalLog(String dml, BaseModel model, Long ouId, Long userId, String parentCode, String dataSource) {
        if (null == model) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, "model");
        }
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setOuId(ouId);
        gl.setModifiedId(userId);
        gl.setObjectType(model.getClass().getSimpleName());
        gl.setModifiedValues(model);
        gl.setParentCode(parentCode);
        if (Constants.GLOBAL_LOG_UPDATE.equals(dml)) {
            gl.setType(Constants.GLOBAL_LOG_UPDATE);
        } else if (Constants.GLOBAL_LOG_INSERT.equals(dml)) {
            gl.setType(Constants.GLOBAL_LOG_INSERT);
        } else if (Constants.GLOBAL_LOG_DELETE.equals(dml)) {
            gl.setType(Constants.GLOBAL_LOG_DELETE);
        } else {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("save globalLog, dataSource is:[{}], model is:[{}], param globalLogCommand is:[{}]", dataSource, gl.getObjectType(), ParamsUtil.bean2String(gl, false));
        }
        if (null == dataSource) {
            globalLogManager.insertGlobalLog(gl);
        } else {
            globalLogManager.insertGlobalLog(gl, dataSource);
        }
    }

    protected void insertSkuInventoryLog(Long skuInvId, Double qty, Double oldQty, Boolean isTabbInvTotal, Long ouid, Long userid) {}

    protected void insertSkuInventoryLog(Long skuInvId, String occupyCode, String occupySource, Double qty, Double oldQty, Boolean isTabbInvTotal, Long ouid, Long userid) {}

    /**
     * 库存日志插入
     * 
     * @param skuInvId 变动的库存ID
     * @param qty 调整数量如果为50.0 入库=50.0 出库=-50.0
     * @param oldQty 修改前库存数量 无需记录交易前后库存总数 = null
     * @param isTabbInvTotal 在库存日志是否记录交易前后库存总数
     * @param ouid 仓库组织ID
     * @param userid 操作人ID
     */
    protected void insertSkuInventoryLog(Long skuInvId, Double qty, Double oldQty, Boolean isTabbInvTotal, Long ouid, Long userid, String invTransactionType) {
        if (!StringUtil.isEmpty(invTransactionType)) {
            // 如果有库存事务类型 需要验证数据正确性
            if (!InvTransactionType.RECEIVING.equals(invTransactionType) && !InvTransactionType.OUTBOUND_SORTING.equals(invTransactionType) && !InvTransactionType.SHELF.equals(invTransactionType) && !InvTransactionType.ASSEMBLY.equals(invTransactionType)
                    && !InvTransactionType.INTRA_WH_MOVE.equals(invTransactionType) && !InvTransactionType.REPLENISHMENT.equals(invTransactionType) && !InvTransactionType.PICKING.equals(invTransactionType)
                    && !InvTransactionType.FACILITY_GOODS_COLLECTION.equals(invTransactionType) && !InvTransactionType.CHECK.equals(invTransactionType) && !InvTransactionType.HANDOVER.equals(invTransactionType)
                    && !InvTransactionType.HANDOVER_OUTBOUND.equals(invTransactionType) && !InvTransactionType.SPLIT_MOVE_OUTBOUND_BOX.equals(invTransactionType) && !InvTransactionType.SPLIT_MOVE_PACKING_CASE.equals(invTransactionType)
                    && !InvTransactionType.INTRA_WH_ADJUSTMENT.equals(invTransactionType) && !InvTransactionType.WH_TO_WH_FLITTING.equals(invTransactionType) && !InvTransactionType.INTRA_WH_MACHINING.equals(invTransactionType)&& !InvTransactionType.SEEDING.equals(invTransactionType)) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
        }
        insertSkuInventoryLog(skuInvId, null, null, qty, oldQty, isTabbInvTotal, ouid, userid, invTransactionType);
    }

    protected void insertSkuInventoryLog(Long skuInvId, String occupyCode, String occupySource, Double qty, Double oldQty, Boolean isTabbInvTotal, Long ouid, Long userid, String invTransactionType) {
        if (null == skuInvId) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"skuInvId"});
        }
        // 通过库存ID封装库存日志对象
        WhSkuInventoryLog log = whSkuInventoryLogManager.findInventoryLogBySkuInvId(skuInvId, ouid);
        if (null == log) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"skuInvId"});
        }
        // 调整数量
        log.setRevisionQty(qty);
        log.setModifiedId(userid);
        log.setModifyTime(new Date());
        log.setInvTransactionType(invTransactionType);
        if (null != occupyCode && null != occupySource) {
            log.setOccupationCode(occupyCode);
            log.setOccupationCodeSource(occupySource);
        }
        // 判断是否要计算库存修改前后数量
        if (isTabbInvTotal) {
            if (null == oldQty) {
                log.setOldQty(0.0);
                log.setNewQty(qty);
            } else {
                log.setOldQty(oldQty);
                log.setNewQty(oldQty + qty);
            }
        }
        whSkuInventoryLogManager.insertSkuInventoryLog(log);
        if (!StringUtil.isEmpty(invTransactionType)) {
            // 存在库存事务类型 上架 交接出库 库内调整 库内调拨 库内加工需要生成库存流水数据
            if (invTransactionType.equals(InvTransactionType.SHELF) || invTransactionType.equals(InvTransactionType.HANDOVER_OUTBOUND) || invTransactionType.equals(InvTransactionType.INTRA_WH_ADJUSTMENT)
                    || invTransactionType.equals(InvTransactionType.INTRA_WH_MACHINING) || invTransactionType.equals(InvTransactionType.WH_TO_WH_FLITTING)) {
                whSkuInventoryFlowManager.insertWhSkuInventoryFlow(log);
            }
        }
    }

    /**
     * 通过groupValue+dicValue查询对应系统参数信息 redis=null 查询数据库
     * 
     * @param sysDictionaryList Map<分组编码groupValue,List<数据值dicValue>>
     * @return Map<分组编码groupValue_数据值dicValue, 系统参数>
     */
    protected Map<String, SysDictionary> findSysDictionaryByRedis(Map<String, List<String>> sysDictionaryList) {
        String redisKey = CacheKeyConstant.WMS_CACHE_SYS_DICTIONARY;
        Map<String, SysDictionary> returnMap = new HashMap<String, SysDictionary>();
        // 遍历系统参数Map
        for (String groupValue : sysDictionaryList.keySet()) {
            // 通过key = groupValue 获取 dicValue
            List<String> divValueList = sysDictionaryList.get(groupValue);
            for (String dicValue : divValueList) {
                SysDictionary sys = null;
                String sysDictionary = null;
                try {
                    // 先查询Redis是否存在对应数据
                    sysDictionary = cacheManager.getValue(redisKey + groupValue + "$" + dicValue);
                    // sys = cacheManager.getObject(redisKey + groupValue + "$" + dicValue);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findSysDictionaryByRedis cacheManager.getObject(" + redisKey + groupValue + "$" + dicValue + ") error");
                }
                if (StringUtil.isEmpty(sysDictionary)) {
                    // 缓存无对应数据 查询数据库
                    sys = sysDictionaryDao.getGroupbyGroupValueAndDicValue(groupValue, dicValue);
                    try {
                        cacheManager.setValue(redisKey + groupValue + "$" + dicValue, JsonUtil.beanToJson(sys));
                        // cacheManager.setObject(redisKey + groupValue + "$" + dicValue, sys);
                    } catch (Exception e) {
                        // redis出错只记录log
                        log.error("findSysDictionaryByRedis cacheManager.setObject(" + redisKey + groupValue + "$" + dicValue + ") error");
                    }
                } else {
                    sys = (SysDictionary) JsonUtil.jsonToBean(sysDictionary, SysDictionary.class);
                }
                // 放入returnMap 格式key = groupValue_dicValue value SysDictionary
                returnMap.put(groupValue + "_" + dicValue, sys);
            }
        }
        return returnMap;
    }


    /**
     * 通过customserIdList查询对应系统参数信息 redis=null 查询数据库
     * 
     * @param customerIdList List<客户ID>
     * @return Map<客户ID, 客户信息>
     */
    protected Map<Long, Customer> findCustomerByRedis(List<Long> customerIdList) {
        String redisKey = CacheKeyConstant.WMS_CACHE_CUSTOMER;
        Map<Long, Customer> returnMap = new HashMap<Long, Customer>();
        // 遍历所有客户ID
        for (Long id : customerIdList) {
            Customer c = null;
            String customer = null;
            try {
                // 先查询Redis是否存在对应数据
                customer = cacheManager.getValue(redisKey + id);
                // c = cacheManager.getObject(redisKey + id);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("findCustomerByRedis cacheManager.getObject(" + redisKey + id + ") error");
            }
            if (StringUtil.isEmpty(customer)) {
                // 缓存无对应数据 查询数据库
                c = customerDao.findById(id);
                try {
                    cacheManager.setValue(redisKey + id, JsonUtil.beanToJson(c));
                    // cacheManager.setObject(redisKey + id, c);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findCustomerByRedis cacheManager.setObject(" + redisKey + id + ") error");
                }
            } else {
                c = (Customer) JsonUtil.jsonToBean(customer, Customer.class);
            }
            returnMap.put(id, c);
        }
        return returnMap;
    }

    /**
     * 通过storeIdList查询对应系统参数信息 redis=null 查询数据库
     * 
     * @param storeIdList List<店铺ID>
     * @return Map<店铺ID, 店铺信息>
     */
    protected Map<Long, Store> findStoreByRedis(List<Long> storeIdList) {
        String redisKey = CacheKeyConstant.WMS_CACHE_STORE;
        Map<Long, Store> returnMap = new HashMap<Long, Store>();
        // 遍历storeIdList 获取storeId
        for (Long id : storeIdList) {
            List<String> redis = new ArrayList<String>();
            Store s = null;
            String store = null;
            try {
                // 先查询Redis是否存在对应数据 店铺redis缓存格式前缀+customerId+storeId
                redis = cacheManager.Keys(redisKey + "*-" + id);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("findStoreByRedis cacheManager.getObject(" + redisKey + id + ") error");
            }
            if (redis.size() > 0) {
                // 获取对应Redis数据
                store = cacheManager.getValue(redis.get(0).split("_")[1]);
                // s = cacheManager.getObject(redis.get(0).split("_")[1]);
                if (StringUtil.isEmpty(store)) {
                    // 查询数据库
                    s = storeDao.findById(id);
                    try {
                        cacheManager.setValue(redisKey + s.getCustomerId() + "-" + id, JsonUtil.beanToJson(s));
                        // cacheManager.setObject(redisKey + s.getCustomerId() + "-" + id, s);
                    } catch (Exception e) {
                        // redis出错只记录log
                        log.error("findStoreByRedis cacheManager.setObject(" + redisKey + s.getCustomerId() + "-" + id + ") error");
                    }
                } else {
                    s = (Store) JsonUtil.jsonToBean(store, Store.class);
                }
            } else {
                s = storeDao.findById(id);
                try {
                    cacheManager.setValue(redisKey + s.getCustomerId() + "-" + id, JsonUtil.beanToJson(s));
                    // cacheManager.setObject(redisKey + s.getCustomerId() + "-" + id, s);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findStoreByRedis cacheManager.setObject(" + redisKey + s.getCustomerId() + "-" + id + ") error");
                }
            }
            returnMap.put(id, s);
        }
        return returnMap;
    }

    /**
     * 获取所有客户信息 redis = null查询数据库
     * 
     * @return
     */
    protected Map<Long, Customer> findCustomerAllByRedis() {
        String redisKey = CacheKeyConstant.WMS_CACHE_CUSTOMER;
        Map<Long, Customer> returnList = new HashMap<Long, Customer>();
        List<String> redis = new ArrayList<String>();
        try {
            // 先查询Redis是否存在对应数据 客户redis缓存格式前缀+customerId
            redis = cacheManager.Keys(redisKey + "*");
        } catch (Exception e) {
            // redis出错只记录log
            log.error("findCustomerAllByRedis cacheManager.Keys(" + redisKey + "*) error");
        }
        if (redis.size() > 0) {
            for (String s : redis) {
                Customer c = null;
                String customer = null;
                // 获取对应Redis数据
                try {
                    customer = cacheManager.getValue(s.split("_")[1]);
                    // c = cacheManager.getObject(s.split("_")[1]);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findCustomerAllByRedis cacheManager.getObject(" + s.split("_")[1] + ") error");
                }
                if (StringUtil.isEmpty(customer)) {
                    // 缓存无对应数据 查询数据库
                    c = customerDao.findById(Long.parseLong(s.substring(s.lastIndexOf("-") + 1, s.length())));
                    if (null != c) {
                        try {
                            cacheManager.setValue(redisKey + c.getId(), JsonUtil.beanToJson(c));
                            // cacheManager.setObject(redisKey + c.getId(), c);
                        } catch (Exception e) {
                            // redis出错只记录log
                            log.error("findCustomerAllByRedis cacheManager.setObject(" + redisKey + c.getId() + ") error");
                        }
                    }
                } else {
                    c = (Customer) JsonUtil.jsonToBean(customer, Customer.class);
                }
                if (null != c) {
                    returnList.put(c.getId(), c);
                }
            }
        } else {
            // 没有数据 查询数据库 然后封装数据
            List<Customer> customers = customerDao.findCustomerAllList();
            for (Customer customer : customers) {
                try {
                    cacheManager.setValue(redisKey + customer.getId(), JsonUtil.beanToJson(customer));
                    // cacheManager.setObject(redisKey + customer.getId(), customer);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findCustomerAllByRedis cacheManager.setObject(" + redisKey + customer.getId() + ") error");
                }
                returnList.put(customer.getId(), customer);
            }
        }
        return returnList;
    }

    /**
     * 通过客户ID获取所有店铺信息 redis = null查询数据库
     * 
     * @param customerId 客户ID
     * @return Map<店铺ID, 店铺信息>
     */
    protected Map<Long, Store> findStoreAllByRedis(Long customerId) {
        String redisKey = CacheKeyConstant.WMS_CACHE_STORE + customerId + "-";
        Map<Long, Store> returnList = new HashMap<Long, Store>();
        List<String> redis = new ArrayList<String>();
        try {
            // 先查询Redis是否存在对应数据 店铺redis缓存格式前缀+customerId+storeId
            redis = cacheManager.Keys(redisKey + "*");
        } catch (Exception e) {
            // redis出错只记录log
            log.error("findStoreAllByRedis cacheManager.Keys(" + redisKey + "*) error");
        }
        if (redis.size() > 0) {
            for (String s : redis) {
                Store store = null;
                String storeJson = null;
                // 获取对应Redis数据
                try {
                    storeJson = cacheManager.getValue(s.split("_")[1]);
                    // store = cacheManager.getObject(s.split("_")[1]);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findStoreAllByRedis cacheManager.getObject(" + s.split("_")[1] + ") error");
                }
                if (StringUtil.isEmpty(storeJson)) {
                    // redis没有 查询数据库
                    store = storeDao.findById(Long.parseLong(s.substring(s.lastIndexOf("-") + 1, s.length())));
                    if (null != store) {
                        try {
                            cacheManager.setValue(redisKey + store.getId(), JsonUtil.beanToJson(store));
                            // cacheManager.setObject(redisKey + store.getId(), store);
                        } catch (Exception e) {
                            // redis出错只记录log
                            log.error("findStoreAllByRedis cacheManager.setObject(" + redisKey + store.getId() + ") error");
                        }
                    }
                } else {
                    store = (Store) JsonUtil.jsonToBean(storeJson, Store.class);
                }
                if (null != store) {
                    returnList.put(store.getId(), store);
                }
            }
        } else {
            // 没有数据 查询数据库 然后封装数据 查询客户下所有的店铺信息
            List<Store> storeList = storeDao.findStoreByCustomerId(customerId);
            for (Store store : storeList) {
                try {
                    cacheManager.setValue(redisKey + store.getId(), JsonUtil.beanToJson(store));
                    // cacheManager.setObject(redisKey + store.getId(), store);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findStoreAllByRedis cacheManager.setObject(" + redisKey + store.getId() + ") error");
                }
                returnList.put(store.getId(), store);
            }
        }
        return returnList;
    }

    /**
     * 通过groupValue+lifecycle 查询对应Redis系统参数信息 redis = null查询数据库
     * 
     * @param groupValue 分组编码
     * @param lifecycle 数据值
     * @return List<SysDictionary>
     */
    protected List<SysDictionary> findSysDictionaryByGroupValueAndRedis(String groupValue, Integer lifecycle) {
        String redisKey = CacheKeyConstant.WMS_CACHE_SYS_DICTIONARY + groupValue + "$";
        List<String> redis = new ArrayList<String>();
        List<SysDictionary> returnList = new ArrayList<SysDictionary>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupValue", groupValue);
        params.put("lifecycle", lifecycle);
        try {
            // 先查询Redis是否存在对应数据 系统redis缓存格式前缀+groupValue+divValue
            redis = cacheManager.Keys(redisKey + "*");
        } catch (Exception e) {
            // redis出错只记录log
            log.error("findSysDictionaryByGroupValueAndRedis cacheManager.Keys(" + redisKey + "*) error");
        }
        if (redis.size() > 0) {
            // 有对应缓存信息
            for (String s : redis) {
                SysDictionary sys = null;
                String sysDictionary = null;
                // 获取对应Redis数据
                try {
                    sysDictionary = cacheManager.getValue("%" + s.split("%")[1]);
                    // sys = cacheManager.getObject("%" + s.split("%")[1]);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findSysDictionaryByGroupValueAndRedis cacheManager.getObject(" + s.split("%")[1] + ") error");
                }
                if (StringUtil.isEmpty(sysDictionary)) {
                    // 缓存无对应数据 查询数据库
                    String dicValue = s.substring(s.lastIndexOf("$") + 1, s.length());
                    sys = sysDictionaryDao.getGroupbyGroupValueAndDicValue(groupValue, dicValue);
                    try {
                        cacheManager.setValue(redisKey + dicValue, JsonUtil.beanToJson(sys));
                        // cacheManager.setObject(redisKey + dicValue, sys);
                    } catch (Exception e) {
                        // redis出错只记录log
                        log.error("findSysDictionaryByGroupValueAndRedis cacheManager.setObject(" + redisKey + dicValue + ") error");
                    }
                } else {
                    sys = (SysDictionary) JsonUtil.jsonToBean(sysDictionary, SysDictionary.class);
                }
                if (null != sys) {
                    // 判断系统参数的lifecycle是否=传入的lifecycle
                    if (null == lifecycle || lifecycle.equals(sys.getLifecycle())) {
                        returnList.add(sys);
                    }
                }
            }
        } else {
            // redis 没有数据 查询数据库
            List<SysDictionary> sysList = sysDictionaryDao.findListByQueryMap(params);
            for (SysDictionary sys : sysList) {
                try {
                    // 放入redis
                    cacheManager.setValue(redisKey + sys.getDicValue(), JsonUtil.beanToJson(sys));
                    // cacheManager.setObject(redisKey + sys.getDicValue(), sys);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findSysDictionaryByGroupValueAndRedis cacheManager.setObject(" + redisKey + sys.getDicValue() + ") error");
                }
                returnList.add(sys);
            }
        }
        return returnList;
    }


    /**
     * 通过客户ID获取客户信息 redis = null查询数据库
     * 
     * @return
     */
    protected Customer getCustomerByRedis(Long customerId) {
        String redisKey = CacheKeyConstant.WMS_CACHE_CUSTOMER;
        Customer c = null;
        String customer = null;
        // 获取对应Redis数据
        try {
            customer = cacheManager.getValue(redisKey + customerId);
            // c = cacheManager.getObject(redisKey + customerId);
        } catch (Exception e) {
            // redis出错只记录log
            log.error("getCustomerByRedis cacheManager.getObject(" + redisKey + customerId + ") error");
        }
        if (StringUtil.isEmpty(customer)) {
            // 缓存无对应数据 查询数据库
            c = customerDao.findById(customerId);
            try {
                cacheManager.setValue(redisKey + c.getId(), JsonUtil.beanToJson(c));
                // cacheManager.setObject(redisKey + c.getId(), c);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("getCustomerByRedis cacheManager.setObject(" + redisKey + c.getId() + ") error");
            }
        } else {
            c = (Customer) JsonUtil.jsonToBean(customer, Customer.class);
        }
        return c;
    }

    /**
     * 通过店铺ID获取店铺信息 redis = null查询数据库
     * 
     * @return
     */
    protected Store getStoreByRedis(Long storeId) {
        List<String> redis = new ArrayList<String>();
        Store store = null;
        String s = null;
        // 店铺KEY前缀
        String redisKey = CacheKeyConstant.WMS_CACHE_STORE;
        // 先查询Redis是否存在对应数据 店铺redis缓存格式前缀+customerId+storeId
        redis = cacheManager.Keys(redisKey + "*-" + storeId);
        if (redis.size() > 0) {
            // 获取对应Redis数据
            String key = redis.get(0).split("_")[1];
            try {
                s = cacheManager.getValue(key);
                // store = cacheManager.getObject(key);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("getStoreByRedis cacheManager.getObject(" + key + ") error");
            }
            if (StringUtil.isEmpty(s)) {
                // 如果Redis缓存不存在对应店铺数据 直接新增
                store = storeDao.findById(storeId);
                try {
                    cacheManager.setValue(redisKey + store.getCustomerId() + "-" + store.getId(), JsonUtil.beanToJson(store));
                    // cacheManager.setObject(redisKey + store.getCustomerId() + "-" +
                    // store.getId(), store);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("getStoreByRedis cacheManager.setObject(" + redisKey + store.getCustomerId() + "-" + store.getId() + ") error");
                }
            } else {
                store = (Store) JsonUtil.jsonToBean(s, Store.class);
            }
        } else {
            // 如果Redis缓存不存在对应店铺数据 直接新增
            store = storeDao.findById(storeId);
            try {
                cacheManager.setValue(redisKey + store.getCustomerId() + "-" + store.getId(), JsonUtil.beanToJson(store));
                // cacheManager.setObject(redisKey + store.getCustomerId() + "-" + store.getId(),
                // store);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("getStoreByRedis cacheManager.setObject(" + redisKey + store.getCustomerId() + "-" + store.getId() + ") error");
            }
        }
        return store;
    }

    /**
     * 库存SN/残次日志插入
     * 
     * @param uuid 库存对应UUID
     * @param ouid 组织仓库ID
     */
    protected void insertSkuInventorySnLog(String uuid, Long ouid) {
        if (StringUtil.isEmpty(uuid)) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"uuid"});
        }
        whSkuInventorySnLogManager.insertSkuInventorySnLog(uuid, ouid);
    }

    /**
     * 插入库存SN/残次日志
     * 
     * @author lichuan
     * @param snId
     * @param uuId
     * @param ouId
     */
    protected void insertSkuInventorySnLog(Long snId, Long ouId) {
        if (null == snId) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"snId"});
        }
        whSkuInventorySnLogManager.insertSkuInventorySnLog(snId, ouId);
    }

    /**
     * 获取格式化的日志信息
     * 
     * @author lichuan
     * @param format
     * @param argArray
     * @return
     */
    protected String getLogMsg(String format, Object... argArray) {
        return LogUtil.getLogMsg(format, argArray);
    }

    /**
     * 通过当前波次阶段CODE+波次模板ID获取下一个波次阶段CODE
     * 
     * @param phaseCode 当前处于波次阶段CODE 为空系统默认获取配置的第一个波次阶段
     * @param waveTemplateId 波次模板ID
     * @param ouid 仓库组织ID
     * @return returnPhaseCode 下一个波次阶段CODE 如果返回为空就是没有下一个波次阶段
     */
    protected String getWavePhaseCode(String phaseCode, Long waveTemplateId, Long ouid) {
        String returnPhaseCode = null;
        // 波次模板ID为空 抛错
        if (null == waveTemplateId) {
            log.error("getWavePhaseCode waveTemplateId is null error logId: " + logId);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        returnPhaseCode = whWavePhaseDao.getWavePhaseCode(phaseCode, waveTemplateId, ouid);
        return returnPhaseCode;
    }

    /**
     * 读取仓库基础信息 redis
     * 
     * @return
     */
    protected Warehouse getWhToRedis(Long id) {
        // 仓库KEY前缀
        String redisKey = CacheKeyConstant.CACHE_WAREHOSUE;
        Warehouse wh = null;
        String w = null;
        try {
            // 读取仓库基础信息 redis
            w = cacheManager.getValue(redisKey + id);
            // wh = cacheManager.getObject(redisKey + id);
        } catch (Exception e) {
            // redis出错只记录log
            log.error("getWhToRedis cacheManager.setObject(" + redisKey + id + ") error");
        }
        if (StringUtil.isEmpty(w)) {
            // 如果redis没有对应数据 查询数据库
            wh = warehouseDao.findWarehouseById(id);
            try {
                // 放入redis缓存
                cacheManager.setValue(redisKey + id, JsonUtil.beanToJson(wh));
                // cacheManager.setObject(redisKey + id, wh);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("getWhToRedis cacheManager.setObject(" + redisKey + id + ") error");
            }
        } else {
            wh = (Warehouse) JsonUtil.jsonToBean(w, Warehouse.class);
        }
        return wh;
    }

    protected void removeWaveLineWhole(Long waveId, Long odoId, Long ouId) {
        whWaveLineDao.removeWaveLineWhole(waveId, odoId, ouId);
    }

    /**
     * @return the logId
     */
    public String getLogId() {
        return logId;
    }

    /**
     * @param logId the logId to set
     */
    public void setLogId(String logId) {
        this.logId = logId;
    }

}
