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
import com.baozun.scm.primservice.whoperation.dao.warehouse.CustomerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventorySnLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;
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
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private StoreManager storeManager;


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
    protected void insertSkuInventoryLog(Long skuInvId, Double qty, Double oldQty, Boolean isTabbInvTotal, Long ouid, Long userid) {
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
    }

    /**
     * 通过groupValue+dicValue查询对应系统参数信息 redis=null 查询数据库
     * 
     * @return
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
                try {
                    // 先查询Redis是否存在对应数据
                    sys = cacheManager.getObject(redisKey + groupValue + dicValue);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findSysDictionaryByRedis cacheManager.getObject(" + redisKey + groupValue + dicValue + ") error");
                }
                if (null == sys) {
                    // 缓存无对应数据 查询数据库
                    sys = sysDictionaryManager.getGroupbyGroupValueAndDicValue(groupValue, dicValue);
                    try {
                        cacheManager.setObject(redisKey + groupValue + dicValue, sys);
                    } catch (Exception e) {
                        // redis出错只记录log
                        log.error("findSysDictionaryByRedis cacheManager.setObject(" + redisKey + groupValue + dicValue + ") error");
                    }
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
     * @return
     */
    protected Map<Long, Customer> findCustomerByRedis(List<Long> customerIdList) {
        String redisKey = CacheKeyConstant.WMS_CACHE_CUSTOMER;
        Map<Long, Customer> returnMap = new HashMap<Long, Customer>();
        // 遍历所有客户ID
        for (Long id : customerIdList) {
            Customer c = null;
            try {
                // 先查询Redis是否存在对应数据
                c = cacheManager.getObject(redisKey + id);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("findCustomerByRedis cacheManager.getObject(" + redisKey + id + ") error");
            }
            if (null == c) {
                // 缓存无对应数据 查询数据库
                c = customerDao.findById(id);
                try {
                    cacheManager.setObject(redisKey + id, c);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findCustomerByRedis cacheManager.setObject(" + redisKey + id + ") error");
                }
            }
            returnMap.put(id, c);
        }
        return returnMap;
    }

    /**
     * 通过storeIdList查询对应系统参数信息 redis=null 查询数据库
     * 
     * @return
     */
    protected Map<Long, Store> findStoreByRedis(List<Long> storeIdList) {
        String redisKey = CacheKeyConstant.WMS_CACHE_STORE;
        Map<Long, Store> returnMap = new HashMap<Long, Store>();
        // 遍历storeIdList 获取storeId
        for (Long id : storeIdList) {
            List<String> redis = new ArrayList<String>();
            Store s = null;
            try {
                // 先查询Redis是否存在对应数据 店铺redis缓存格式前缀+customerId+storeId
                redis = cacheManager.Keys(redisKey + "*-" + id);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("findStoreByRedis cacheManager.getObject(" + redisKey + id + ") error");
            }
            if (redis.size() != 0) {
                // 获取对应Redis数据
                s = cacheManager.getObject(redis.get(0).split("_")[1]);
                if (null == s) {
                    // 查询数据库
                    s = storeManager.findStoreById(id);
                    try {
                        cacheManager.setObject(redisKey + s.getCustomerId() + "-" + id, s);
                    } catch (Exception e) {
                        // redis出错只记录log
                        log.error("findStoreByRedis cacheManager.setObject(" + redisKey + s.getCustomerId() + "-" + id + ") error");
                    }
                }
            } else {
                s = storeManager.findStoreById(id);
                try {
                    cacheManager.setObject(redisKey + s.getCustomerId() + "-" + id, s);
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
            log.error("findCustomerAllByRedis cacheManager.getObject(" + redisKey + "*) error");
        }
        if (redis.size() != 0) {
            for (String s : redis) {
                Customer c = null;
                // 获取对应Redis数据
                try {
                    c = cacheManager.getObject(redis.get(0).split("_")[1]);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findCustomerAllByRedis cacheManager.getObject(" + redis.get(0).split("_")[1] + ") error");
                }
                if (null == c) {
                    // 缓存无对应数据 查询数据库
                    c = customerDao.findById(Long.parseLong(s.substring(s.lastIndexOf("-") + 1, s.length())));
                    try {
                        cacheManager.setObject(redisKey + c.getId(), c);
                    } catch (Exception e) {
                        // redis出错只记录log
                        log.error("findCustomerAllByRedis cacheManager.setObject(" + redisKey + c.getId() + ") error");
                    }
                }
                returnList.put(c.getId(), c);
            }
        } else {
            // 没有数据 查询数据库 然后封装数据
            List<Customer> customers = customerDao.findCustomerAllList();
            for (Customer customer : customers) {
                try {
                    cacheManager.setObject(redisKey + customer.getId(), customer);
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
