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
 */

package com.baozun.scm.primservice.whoperation.manager.pda.caseLevel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.CheckInManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd.CaseLevelRcvdManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.SelectPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuExtattr;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Uom;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.Md5Util;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("caseLevelManagerProxy")
public class CaseLevelManagerProxyImpl extends BaseManagerImpl implements CaseLevelManagerProxy {
    public static final Logger log = LoggerFactory.getLogger(CaseLevelManagerProxyImpl.class);

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PoLineManager poLineManager;

    @Autowired
    private AsnLineManager asnLineManager;

    @Autowired
    private SkuRedisManager skuRedisManager;

    @Autowired
    private CaseLevelRcvdManager caseLevelRcvdManager;

    @Autowired
    private SelectPoAsnManagerProxy selectPoAsnManagerProxy;

    @Autowired
    private CheckInManagerProxy checkInManagerProxy;

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat dateFormatUn = new SimpleDateFormat("MM/dd/yyyy");
    private final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据Id获取asn信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public WhAsn getWhAsnById(Long asnId, Long ouId, String logId) {
        WhAsn whAsn = selectPoAsnManagerProxy.findWhAsnById(asnId, ouId);
        if (null == whAsn || null == whAsn.getId() || StringUtil.isEmpty(whAsn.getAsnCode()) || StringUtil.isEmpty(whAsn.getAsnExtCode()) || null == whAsn.getCustomerId() || null == whAsn.getStoreId()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_ASN_NULL_ERROR);
        }
        return whAsn;
    }

    /**
     * 根据容器号查询容器信息
     *
     * @author mingwei.xie
     * @param containerCode
     * @param ouId
     * @return
     */
    @Override
    public ContainerCommand getContainerByCode(String containerCode, Long ouId) {
        ContainerCommand containerCommand = caseLevelRcvdManager.getContainerByCode(containerCode, ouId);
        if (null == containerCommand || null == containerCommand.getId() || StringUtil.isEmpty(containerCommand.getCode()) || StringUtil.isEmpty(containerCommand.getName()) || null == containerCommand.getLifecycle()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CONTAINER_NULL_ERROR);
        }
        return containerCommand;
    }

    /**
     * 根据容器号查询容器信息
     *
     * @author mingwei.xie
     * @param containerId
     * @param ouId
     * @return
     */
    @Override
    public Container getContainerById(Long containerId, Long ouId) {
        Container container = caseLevelRcvdManager.getContainerById(containerId, ouId);
        if (null == container || null == container.getId() || StringUtil.isEmpty(container.getCode()) || StringUtil.isEmpty(container.getName()) || null == container.getLifecycle()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CONTAINER_NULL_ERROR);
        }
        return container;
    }

    /**
     * 从缓存中获取caseLevel货箱操作人
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @return
     */
    @Override
    public String getContainerOptUserFromCache(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_KEY_ERROR);
        }
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_OPT_USER_PREFIX + userId + "-" + asnId + "-" + containerId;
        String cacheUserId = null;
        try {
            cacheUserId = cacheManager.getValue(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        log.debug("CaseLevelManagerProxyImpl getContainerOptUserFromCache param cacheKey:[{}], result userId:[{}]", cacheKey, userId);
        return cacheUserId;
    }

    /**
     * 占用caseLevel货箱操作
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param userId
     * @param ouId
     * @param logId
     */
    @Override
    public void occupiedContainerByOptUser(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == userId || null == ouId) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_KEY_ERROR);
        }

        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_OPT_USER_PREFIX + userId + "-" + asnId + "-" + containerId;
        try {
            // 保存货箱操作人到缓存
            cacheManager.setValue(cacheKey, userId.toString());
            try {
                Container container = this.getContainerById(containerId, ouId);
                // 容器必须是可用状态
                if (ContainerStatus.CONTAINER_LIFECYCLE_USABLE != container.getLifecycle() || ContainerStatus.CONTAINER_STATUS_USABLE != container.getStatus()) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CONTAINER_UNAVAILABLE);
                }
                // 容器lifecycle修改为3占用，容器状态修改为收货中
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                container.setOperatorId(userId);
                // 从数据库占用货箱
                caseLevelRcvdManager.updateContainerStatus(container);
            } catch (Exception e) {
                // 数据库修改失败，回滚缓存
                cacheManager.remove(cacheKey);
                throw e;
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CONTAINER_OCCUPIED_FAILED);
        }

        // 清除容器收货缓存
        this.clearRcvdCacheForOccupiedContainer(asnId, containerId, userId, ouId, logId);
    }

    public void clearRcvdCacheForOccupiedContainer(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        // 释放该货箱缓存
        this.clearRcvdCache(asnId, containerId, userId, ouId, logId);
        try {
            // 清除该货箱前次收货数据
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-*");
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
    }

    /**
     * 取消当前容器扫描，释放容器占用，删除所有相关缓存
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param userId
     * @param ouId
     * @param logId
     */
    @Override
    public void cancelCurrentContainerRcvd(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 操作人缓存需要配合数据库操作
        this.releaseContainerByOptUser(asnId, containerId, userId, ouId, logId);
        // 释放本次收货缓存
        this.clearRcvdCache(asnId, containerId, userId, ouId, logId);
        try {
            // 清除该货箱前次收货数据
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-*");
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
    }

    /**
     * 返回通用收货/容器收货，释放该用户下所有占用的容器，清除相关容器的收货缓存
     *
     * @author mingwei.xie
     * @param asnId
     * @param userId
     * @param ouId
     * @param logId
     */
    public void clearCacheForForwardGeneralRcvd(Long asnId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String cacheKeyPattern = CacheKeyConstant.WMS_CACHE_CL_OPT_USER_PREFIX + userId + "-" + asnId + "-*";
        List<String> optUserCacheKeyList = null;
        try {
            optUserCacheKeyList = cacheManager.Keys(cacheKeyPattern);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        if (null != optUserCacheKeyList) {
            for (String optUserCacheKey : optUserCacheKeyList) {
                String containerId = optUserCacheKey.substring(optUserCacheKey.lastIndexOf("-") + 1);
                if (StringUtil.isEmpty(containerId)) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
                }

                Container container = this.getContainerById(Long.parseLong(containerId), ouId);
                if (null == container) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_NULL);
                }

                if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED == container.getLifecycle() && ContainerStatus.CONTAINER_STATUS_RCVD == container.getStatus()) {
                    // 容器如果是占用的收货中状态，缓存释放和数据库释放
                    this.releaseContainerByOptUser(asnId, container.getId(), userId, ouId, logId);
                } else {
                    // 容器不是占用的收货中状态，释放缓存
                    String cacheKey = CacheKeyConstant.WMS_CACHE_CL_OPT_USER_PREFIX + userId + "-" + asnId + "-" + containerId;
                    cacheManager.remove(cacheKey);
                }

                // 释放本次收货缓存
                this.clearRcvdCache(asnId, Long.parseLong(containerId), userId, ouId, logId);
                try {
                    // 清除该货箱前次收货数据
                    cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-*");
                } catch (Exception e) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
                }
            }
        }

    }

    /**
     * 获取caseLevel货箱指定商品的装箱信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public List<WhCartonCommand> getWhCartonListByContainer(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhCartonCommand> whCartonCommandList = this.getCartonByContainerFromCache(asnId, containerId, userId, ouId, logId);
        if (null == whCartonCommandList || whCartonCommandList.isEmpty()) {
            Container container = this.getContainerById(containerId, ouId);
            // 提示无对应caseLevel箱信息
            throw new BusinessException(ErrorCodes.CASELEVEL_NULL, new Object[] {container.getCode()});
        }
        return whCartonCommandList;
    }

    /**
     * 获取caseLevel货箱指定商品的装箱信息
     * 
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public List<WhCartonCommand> getWhCartonListBySku(Long asnId, Long containerId, Long skuId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == skuId || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhCartonCommand> whCartonCommandList = this.getCartonByContainerFromCache(asnId, containerId, userId, ouId, logId);
        List<WhCartonCommand> returnWhCartonCommandList = new ArrayList<>();
        if (null != whCartonCommandList) {
            for (WhCartonCommand whCartonCommand : whCartonCommandList) {
                if (null == whCartonCommand.getQuantity()) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CARTON_PLAN_QTY_NULL_ERROR);
                }
                if (skuId.equals(whCartonCommand.getSkuId())) {
                    returnWhCartonCommandList.add(whCartonCommand);
                }
            }
        }
        return returnWhCartonCommandList;
    }

    /**
     * 根据商品条码获取skuId及默认数量
     *
     * @author mingwei.xie
     * @param barCode
     * @param logId
     * @return
     */
    @Override
    public Map<Long, Integer> getSkuByBarCode(String barCode, String logId) {
        if (null == barCode || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        Map<Long, Integer> skuQtyMap = skuRedisManager.findSkuByBarCode(barCode, logId);
        if (null == skuQtyMap || skuQtyMap.isEmpty()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_DEFAULT_RCVD_QTY_ERROR, new Object[] {barCode});
        }
        return skuQtyMap;
    }

    /**
     * 根据skuId获取商品信息
     *
     * @author mingwei.xie
     * @param skuId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public SkuRedisCommand getSkuMasterBySkuId(Long skuId, Long ouId, String logId) {
        if (null == skuId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        SkuRedisCommand skuRedisCommand = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);

        if (null == skuRedisCommand) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_INFO_NULL_ERROR);
        }
        Sku sku = skuRedisCommand.getSku();
        if (null == sku) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_INFO_NULL_ERROR);
        }

        if (StringUtil.isEmpty(sku.getName()) || StringUtil.isEmpty(sku.getCode()) || null == sku.getLifecycle()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL_ERROR);
        }
        SkuMgmt skuMgmt = skuRedisCommand.getSkuMgmt();
        if (null == skuMgmt) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_INFO_NULL_ERROR);
        }
        if (StringUtil.isEmpty(skuMgmt.getSerialNumberType()) || null == skuMgmt.getIsValid() || null == skuMgmt.getIsBatchNo() || null == skuMgmt.getIsCountryOfOrigin() || null == skuMgmt.getIsInvType()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL_ERROR);
        }
        if (skuMgmt.getIsValid() && null == skuMgmt.getIsExpiredGoodsReceive()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL_ERROR);
        }
        if (null != skuMgmt.getValidDate() && StringUtil.isEmpty(skuMgmt.getGoodShelfLifeUnit())) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL_ERROR);
        }
        SkuExtattr skuExtattr = skuRedisCommand.getSkuExtattr();
        if (null == skuExtattr) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_INFO_NULL_ERROR);
        }
        if (null == skuExtattr.getInvAttr1() || null == skuExtattr.getInvAttr2() || null == skuExtattr.getInvAttr3() || null == skuExtattr.getInvAttr4() || null == skuExtattr.getInvAttr5()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL_ERROR);
        }
        return skuRedisCommand;
    }

    /**
     * 将功能信息作为get请求的参数传往下一个页面，防止功能在收货过程中被修改
     *
     * @param rcvdFun
     * @return
     */
    @Override
    public String getFunctionInfoStr(WhFunctionRcvd rcvdFun) {
        if (null == rcvdFun) {
            throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_FUN_NULL);
        }
        StringBuilder stringBuilder = new StringBuilder();
        Method[] methodArray = rcvdFun.getClass().getMethods();
        if (methodArray != null) {
            for (Method method : methodArray) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    try {
                        String tempFiledName = method.getName().replaceFirst("get", "");
                        char[] buffer = tempFiledName.toCharArray();
                        buffer[0] = Character.toLowerCase(tempFiledName.charAt(0));
                        String filedName = new String(buffer);

                        Object value = method.invoke(rcvdFun);
                        if (null != value) {
                            stringBuilder.append("&rcvd.").append(filedName).append('=');
                            stringBuilder.append(value);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                        throw new BusinessException(ErrorCodes.CASELEVEL_SERIALIZE_ERROR);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 将已确认的商品信息作为get请求的参数传往下一个页面
     * 
     * @author mingwei.xie
     * @param whCartonCommand
     * @return
     */
    @Override
    public String getWhCartonInfoStr(WhCartonCommand whCartonCommand) {
        if (null == whCartonCommand) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        StringBuilder stringBuilder = new StringBuilder();
        Method[] methodArray = whCartonCommand.getClass().getMethods();
        if (methodArray != null) {
            for (Method method : methodArray) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass") && !method.getName().equals("getSkuInventorySnList")) {
                    try {
                        String tempFiledName = method.getName().replaceFirst("get", "");
                        char[] buffer = tempFiledName.toCharArray();
                        buffer[0] = Character.toLowerCase(tempFiledName.charAt(0));
                        String filedName = new String(buffer);

                        Object value = method.invoke(whCartonCommand);
                        if (null != value) {
                            // if (method.getName().equals("getSkuInventorySnList")) {
                            // List skuInventorySnList = (List) value;
                            // for (int index = 0, len = skuInventorySnList.size(); index < len;
                            // index++) {
                            // WhSkuInventorySn whSkuInventorySn = (WhSkuInventorySn)
                            // skuInventorySnList.get(index);
                            // if (!StringUtil.isEmpty(whSkuInventorySn.getSn())) {
                            // stringBuilder.append('&').append("skuInventorySnList[").append(index).append("].sn").append('=').append(whSkuInventorySn.getSn());
                            // }
                            // stringBuilder.append('&').append("skuInventorySnList[").append(index).append("].defectSource").append('=').append(whSkuInventorySn.getDefectSource());
                            // stringBuilder.append('&').append("skuInventorySnList[").append(index).append("].defectTypeId").append('=').append(whSkuInventorySn.getDefectTypeId());
                            // stringBuilder.append('&').append("skuInventorySnList[").append(index).append("].defectReasonsId").append('=').append(whSkuInventorySn.getDefectReasonsId());
                            // }
                            // }
                            stringBuilder.append('&').append(filedName).append('=');
                            if (value instanceof Date) {
                                stringBuilder.append(dateFormatUn.format((Date) value));
                            } else {
                                stringBuilder.append(value);
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                        throw new BusinessException(ErrorCodes.CASELEVEL_SERIALIZE_ERROR);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void saveScanRcvdSnDefectInfoCache(WhCartonCommand whCartonCommand, List<WhSkuInventorySn> whSkuInventorySnList, Long userId, Long ouId, String logId) {
        if (null == whCartonCommand || null == whSkuInventorySnList || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String uuid = this.getWhCartonUUID(whCartonCommand, logId);
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_SCAN_RECD_SN_DEFECT_INFO_PREFIX + userId + "-" + whCartonCommand.getAsnId() + "-" + whCartonCommand.getContainerId() + "-" + whCartonCommand.getSkuId() + "-" + uuid;
        List<WhSkuInventorySn> skuInventorySnListCache = null;
        try {
            skuInventorySnListCache = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        if (null == skuInventorySnListCache) {
            try {
                cacheManager.setObject(cacheKey, whSkuInventorySnList, CacheKeyConstant.CACHE_ONE_DAY);
            } catch (Exception e) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SN_DEFECT_INFO_CACHE_ERROR);
            }
        } else {
            skuInventorySnListCache.addAll(whSkuInventorySnList);
            try {
                cacheManager.setObject(cacheKey, skuInventorySnListCache, CacheKeyConstant.CACHE_ONE_DAY);
            } catch (Exception e) {
                throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
            }
        }
    }

    @Override
    public List<WhSkuInventorySn> getScanRcvdSnDefectInfoCache(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId) {
        String uuid = this.getWhCartonUUID(whCartonCommand, logId);
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_SCAN_RECD_SN_DEFECT_INFO_PREFIX + userId + "-" + whCartonCommand.getAsnId() + "-" + whCartonCommand.getContainerId() + "-" + whCartonCommand.getSkuId() + "-" + uuid;
        List<WhSkuInventorySn> skuInventorySnListCache = null;
        try {
            skuInventorySnListCache = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        return skuInventorySnListCache;

    }

    @Override
    public List<String> getScanRcvdSnCache(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId) {
        List<WhSkuInventorySn> scanRcvdSnDefectList = this.getScanRcvdSnDefectInfoCache(whCartonCommand, userId, ouId, logId);
        List<String> scanRcvdSnListCache = new ArrayList<>();
        if (null != scanRcvdSnDefectList) {
            for (WhSkuInventorySn whSkuInventorySn : scanRcvdSnDefectList) {
                if (!StringUtil.isEmpty(whSkuInventorySn.getSn())) {
                    scanRcvdSnListCache.add(whSkuInventorySn.getSn());
                }
            }
        }
        return scanRcvdSnListCache;
    }

    /**
     * 主对象保存的时候会清除，在SN/残次页面退出该的时候会清除，商品检验完属性的时候会清除， 其他时间不清除缓存，在最后一次收入SN/残次信息的时候未存到缓存中
     * 
     * @author mingwei.xie
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    @Override
    public void clearScanRcvdSnDefectInfoCache(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId) {
        if (null == whCartonCommand || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String uuid = this.getWhCartonUUID(whCartonCommand, logId);
        String rcvdSnDefectInfoCacheKey = CacheKeyConstant.WMS_CACHE_CL_SCAN_RECD_SN_DEFECT_INFO_PREFIX + userId + "-" + whCartonCommand.getAsnId() + "-" + whCartonCommand.getContainerId() + "-" + whCartonCommand.getSkuId() + "-" + uuid;
        try {
            cacheManager.remove(rcvdSnDefectInfoCacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
    }

    /**
     * @author mingwei.xie
     * @return
     */
    @Override
    public String generateDefectWareBarcode() {
        String defectWareBarcode = this.codeManager.generateCode(Constants.WMS, Constants.INVENTORY_DEFECT_WARE_BARCODE, null, null, null);
        if (StringUtil.isEmpty(defectWareBarcode)) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SN_DEFECT_WARE_BARCODE_ERROR);
        }
        return defectWareBarcode;
    }

    /**
     * 扫描的SN号是否存在
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param snCode
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public boolean isCaseLevelSnExist(Long asnId, Long containerId, Long skuId, String snCode, Long userId, Long ouId, String logId) {
        boolean isExist = false;
        List<WhAsnSn> caseLevelWhAsnSnList = this.getCaseLevelWhAsnSnFromCache(asnId, containerId, userId, ouId, logId);
        if (null != caseLevelWhAsnSnList) {
            for (WhAsnSn whAsnSn : caseLevelWhAsnSnList) {
                if (snCode.equals(whAsnSn.getSn())) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }

    /**
     * 收货的的SN号是否已经收入
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param snCode
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public boolean isRcvdSnCacheExist(Long asnId, Long containerId, Long skuId, String snCode, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == skuId || StringUtil.isEmpty(snCode) || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhCartonCommand> rcvdCartonList = this.getRcvdCartonBySkuFromCache(asnId, containerId, skuId, userId, ouId, logId);
        boolean cacheResult = false;
        if (null != rcvdCartonList) {
            for (WhCartonCommand rcvdCarton : rcvdCartonList) {
                List<WhSkuInventorySn> rcvdSkuInventorySnList = rcvdCarton.getSkuInventorySnList();
                if (null != rcvdSkuInventorySnList && !rcvdSkuInventorySnList.isEmpty()) {
                    for (WhSkuInventorySn rcvdInvSn : rcvdSkuInventorySnList) {
                        if (snCode.equals(rcvdInvSn.getSn())) {
                            cacheResult = true;
                            break;
                        }
                    }
                }
                if (cacheResult) {
                    break;
                }
            }
        }

        return cacheResult;
    }

    /**
     * 保存已收商品的缓存，SN/残次信息跟随主对象保存
     *
     * @author mingwei.xie
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    @Override
    public void saveRcvdCartonCache(WhCartonCommand whCartonCommand, WhFunctionRcvd whFunctionRcvd, Long userId, Long ouId, String logId) {
        // 检查待缓存的数据
        this.checkRcvdCarton(whCartonCommand, whFunctionRcvd, userId, ouId, logId);
        // 缓存标识
        String uuid = this.getWhCartonUUID(whCartonCommand, logId);
        if (null == uuid) {
            throw new BusinessException(ErrorCodes.CASELEVEL_UUID_ERROR);
        }
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX + userId + "-" + whCartonCommand.getAsnId() + "-" + whCartonCommand.getContainerId() + "-" + whCartonCommand.getSkuId() + "-" + uuid;
        WhCartonCommand rcvdWhCarton = null;
        try {
            rcvdWhCarton = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        if (null == rcvdWhCarton) {
            if (null == whCartonCommand.getSkuQty() || whCartonCommand.getSkuQty() <= 0) {
                throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_QTY_ERROR);
            }
            // 如果未缓存过该属性的商品，则设置新的缓存
            try {
                whCartonCommand.setUuid(uuid);
                cacheManager.setObject(cacheKey, whCartonCommand, CacheKeyConstant.CACHE_ONE_DAY);
            } catch (Exception e) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_CACHE_ERROR);
            }
        } else {
            // 如果已有缓存，则更新收货数量
            Double rcvdSkuTotal = new BigDecimal(rcvdWhCarton.getSkuQty().toString()).add(new BigDecimal(whCartonCommand.getSkuQty().toString())).doubleValue();
            if (rcvdSkuTotal <= 0) {
                throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_QTY_ERROR);
            }
            rcvdWhCarton.setSkuQty(rcvdSkuTotal);
            rcvdWhCarton.setQtyRcvd(rcvdSkuTotal);
            if (null != rcvdWhCarton.getSkuInventorySnList()) {
                rcvdWhCarton.getSkuInventorySnList().addAll(whCartonCommand.getSkuInventorySnList());
            } else {
                rcvdWhCarton.setSkuInventorySnList(whCartonCommand.getSkuInventorySnList());
            }
            try {
                cacheManager.setObject(cacheKey, rcvdWhCarton, CacheKeyConstant.CACHE_ONE_DAY);
            } catch (Exception e) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_CACHE_ERROR);
            }
        }

        // 清除本次扫描的SN/残次信息，在主对象保存成功的时候再清除，因为最后一次收入的SN/残次信息未保存到缓存中
        this.clearScanRcvdSnDefectInfoCache(whCartonCommand, userId, ouId, logId);
    }

    @Override
    public List<WhCartonCommand> getRcvdCartonBySkuFromCache(Long asnId, Long containerId, Long skuId, Long userId, Long ouId, String logId) {
        String cacheKeyPattern = CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId + "-" + skuId + "-*";
        List<String> cartonCacheKeyList = null;
        try {
            cartonCacheKeyList = cacheManager.Keys(cacheKeyPattern);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        List<WhCartonCommand> rcvdCartonList = new ArrayList<>();
        if (null != cartonCacheKeyList) {
            for (String cartonCacheKey : cartonCacheKeyList) {

                WhCartonCommand rcvdCarton = null;
                try {
                    rcvdCarton = cacheManager.getObject(cartonCacheKey.substring(cartonCacheKey.indexOf(CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX)));
                } catch (Exception e) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
                }
                if (null == rcvdCarton) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_GET_CACHE_ERROR);
                }
                rcvdCartonList.add(rcvdCarton);
            }
        }
        return rcvdCartonList;
    }

    /**
     * 获取本次收货指定sku已收的数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public Double getRcvdSkuQtyFromCache(Long asnId, Long containerId, Long skuId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == skuId || null == userId || null == ouId) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_KEY_ERROR);
        }
        List<WhCartonCommand> rcvdCartonList = this.getRcvdCartonBySkuFromCache(asnId, containerId, skuId, userId, ouId, logId);
        BigDecimal skuRcvdQty = new BigDecimal(0);
        if (null != rcvdCartonList) {
            for (WhCartonCommand rcvdCarton : rcvdCartonList) {
                skuRcvdQty = skuRcvdQty.add(new BigDecimal(rcvdCarton.getSkuQty().toString()));
            }
        }
        return skuRcvdQty.doubleValue();
    }

    /**
     * 获取本次收货的商品数量, 安装skuId-收货数量存储的map
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param logId
     * @return
     */
    @Override
    public Map<Long, Double> getCurrentRcvdSkuQtyMap(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        String cacheKeyPattern = CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId + "-*";
        List<String> currentRcvdSkuQtyCacheKeyList = null;
        try {
            currentRcvdSkuQtyCacheKeyList = cacheManager.Keys(cacheKeyPattern);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        Map<Long, Double> currentRcvdSkuQty = new HashMap<>();
        if (null != currentRcvdSkuQtyCacheKeyList) {
            for (String cacheKey : currentRcvdSkuQtyCacheKeyList) {
                WhCartonCommand rcvdCarton = null;
                try {
                    rcvdCarton = cacheManager.getObject(cacheKey.substring(cacheKey.indexOf(CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX)));
                } catch (Exception e) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
                }

                if (null != rcvdCarton) {
                    Double skuRcvdQty = currentRcvdSkuQty.get(rcvdCarton.getSkuId());
                    if (null == skuRcvdQty) {
                        skuRcvdQty = 0d;
                    }
                    skuRcvdQty = new BigDecimal(skuRcvdQty.toString()).add(new BigDecimal(rcvdCarton.getSkuQty().toString())).doubleValue();
                    currentRcvdSkuQty.put(rcvdCarton.getSkuId(), skuRcvdQty);
                }
            }
        }
        return currentRcvdSkuQty;
    }

    /**
     * 根据UUID更新收货数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param uuid
     * @param alterQty
     * @param userId
     * @param ouId
     * @param logId
     */
    @Override
    public void updateRcvdCartonQtyByUUID(Long asnId, Long containerId, Long skuId, String uuid, Double alterQty, Long userId, Long ouId, String logId) {
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId + "-" + skuId + "-" + uuid;
        WhCartonCommand rcvdWhCarton = null;
        try {
            rcvdWhCarton = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        if (null == rcvdWhCarton) {
            throw new BusinessException(ErrorCodes.CASELEVEL_UUID_RCVD_INFO_NULL_ERROR);
        }
        Double rcvdQty = rcvdWhCarton.getSkuQty();
        rcvdQty = new BigDecimal(rcvdQty.toString()).add(new BigDecimal(alterQty.toString())).doubleValue();
        if (rcvdQty <= 0) {
            throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_QTY_ERROR);
        }
        rcvdWhCarton.setSkuQty(rcvdQty);
        rcvdWhCarton.setQtyRcvd(rcvdQty);
        try {
            cacheManager.setObject(cacheKey, rcvdWhCarton, CacheKeyConstant.CACHE_ONE_DAY);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }

    }

    /**
     * 重新收货，缓存当前收货数，清除缓存数据
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param logId
     */
    @Override
    public void reRcvd(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        // 清除上次收货数据
        try {
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-*");
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }

        Map<Long, Double> skuRcvdQtyMap = this.getCurrentRcvdSkuQtyMap(asnId, containerId, userId, ouId, logId);
        if (null != skuRcvdQtyMap) {
            for (Long skuId : skuRcvdQtyMap.keySet()) {
                // 创建上次收货数缓存key
                String lastRcvdCacheKey = CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-" + skuId;
                // 保存上次商品收货数缓存
                try {
                    cacheManager.setObject(lastRcvdCacheKey, skuRcvdQtyMap.get(skuId), CacheKeyConstant.CACHE_ONE_DAY);
                } catch (Exception e) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
                }
            }
        }
        // 释放本次收货缓存
        this.clearRcvdCache(asnId, containerId, userId, ouId, logId);
    }

    /**
     * 获取上次收货的商品数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param logId
     * @return
     */
    @Override
    public Map<Long, Double> getLastRcvdSkuQty(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        String cacheKeyPattern = CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-*";
        List<String> lastRcvdSkuQtyCacheKeyList = null;
        try {
            lastRcvdSkuQtyCacheKeyList = cacheManager.Keys(cacheKeyPattern);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        Map<Long, Double> lastRcvdSkuQty = null;
        if (null != lastRcvdSkuQtyCacheKeyList) {
            lastRcvdSkuQty = new HashMap<>();
            for (String cacheKey : lastRcvdSkuQtyCacheKeyList) {
                Double skuQty = null;
                try {
                    skuQty = cacheManager.getObject(cacheKey.substring(cacheKey.indexOf(CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX)));
                } catch (Exception e) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
                }
                Long skuId = Long.parseLong(cacheKey.substring(cacheKey.lastIndexOf("-") + 1, cacheKey.length()));
                lastRcvdSkuQty.put(skuId, skuQty);
            }
        }
        return lastRcvdSkuQty;
    }

    /**
     * 获取指定sku的上次收货数
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param logId
     * @return
     */
    @Override
    public Double getLastRcvdSkuQtyBySkuId(Long asnId, Long containerId, Long skuId, Long userId, Long ouId, String logId) {
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + asnId + "-" + containerId + "-" + skuId;
        Double skuQty = null;
        try {
            skuQty = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        return skuQty;
    }

    /**
     * 根据店铺ID获取店铺信息
     *
     * @author mingwei.xie
     * @param storeId
     * @param logId
     * @return
     */
    @Override
    public Store getStoreById(Long storeId, String logId) {
        Map<Long, Store> storeMap = this.findStoreByRedis(Collections.singletonList(storeId));
        Store store = storeMap.get(storeId);
        if (null == store || null == store.getIsAllowCollectDiff()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_STORE_ATTR_NULL_ERROR);
        }
        return store;
    }

    /**
     * 获取指定系统参数
     *
     * @param groupValue
     * @param dicValue
     * @return
     */
    @Override
    public SysDictionary getSysDictionary(String groupValue, String dicValue) {
        List<SysDictionary> sysDictionaryList = this.findSysDictionaryByGroupValueAndRedis(groupValue, BaseModel.LIFECYCLE_NORMAL);
        SysDictionary sysDictionary = null;
        for (SysDictionary sysDic : sysDictionaryList) {
            if (sysDic.getDicValue().equals(dicValue)) {
                sysDictionary = sysDic;
                break;
            }
        }
        return sysDictionary;
    }

    /**
     * 获取系统参数
     *
     * @author mingwei.xie
     * @param groupValue
     * @param lifecycle
     * @return
     */
    @Override
    public List<SysDictionary> getSysDictionaryByGroupValue(String groupValue, Integer lifecycle) {
        List<SysDictionary> sysDictionaryList = this.findSysDictionaryByGroupValueAndRedis(groupValue, lifecycle);
        return sysDictionaryList;
    }

    /**
     * 根据残次类型查询仓库的残次原因
     *
     * @author mingwei.xie
     * @param typeId
     * @param ouId
     * @return
     */
    @Override
    public List<WarehouseDefectReasonsCommand> getWarehouseDefectReasonsListByDefectTypeId(Long typeId, Long ouId) {
        List<WarehouseDefectReasonsCommand> warehouseDefectReasonsCommandList = caseLevelRcvdManager.findWarehouseDefectReasonsListByDefectTypeId(typeId, ouId);
        return warehouseDefectReasonsCommandList;
    }

    /**
     * 查询仓库配置的残次类型
     *
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @return
     */
    @Override
    public List<WarehouseDefectTypeCommand> getWarehouseDefectTypeByOuId(Long ouId, Integer lifecycle) {
        List<WarehouseDefectTypeCommand> warehouseDefectTypeCommandList = caseLevelRcvdManager.findWarehouseDefectTypeListByOuId(ouId, lifecycle);
        return warehouseDefectTypeCommandList;
    }

    @Override
    public List<StoreDefectReasonsCommand> getStoreDefectReasonsByDefectTypeIds(List<Long> storeDefectTypeIdList) {
        List<StoreDefectReasonsCommand> storeDefectReasonsCommandList = caseLevelRcvdManager.findStoreDefectReasonsListByDefectTypeIds(storeDefectTypeIdList);
        return storeDefectReasonsCommandList;
    }

    /**
     *
     * 根据店铺ID查询对应残次类型
     *
     * @return
     */
    @Override
    public List<StoreDefectTypeCommand> getStoreDefectTypeListByStoreId(Long storeId) {
        List<StoreDefectTypeCommand> storeDefectTypeCommandList = caseLevelRcvdManager.findStoreDefectTypeListByStoreId(storeId);
        return storeDefectTypeCommandList;
    }

    /**
     * 店铺的残次类型
     */
    @Override
    public StoreDefectType getStoreDefectTypeById(Long id) {
        return caseLevelRcvdManager.findStoreDefectTypeById(id);
    }

    /**
     * 店铺的残次原因
     */
    @Override
    public StoreDefectReasons getStoreDefectReasonsById(Long id) {
        return caseLevelRcvdManager.findStoreDefectReasonsById(id);
    }

    /**
     * 仓库的残次类型
     */
    @Override
    public WarehouseDefectType getWarehouseDefectTypeById(Long id, Long ouId) {
        return caseLevelRcvdManager.findWarehouseDefectTypeById(id, ouId);
    }

    /**
     * 仓库的残次原因
     */
    @Override
    public WarehouseDefectReasons getWarehouseDefectReasonsById(Long id, Long ouId) {
        return caseLevelRcvdManager.findWarehouseDefectReasonsById(id, ouId);
    }

    /**
     * 根据ID查询库存状态信息
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    @Override
    public InventoryStatus getInventoryStatusById(Long id) {
        InventoryStatus inventoryStatus = caseLevelRcvdManager.getInventoryStatusById(id);
        if (null == inventoryStatus || null == inventoryStatus.getIsDefective()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_INVENTORY_ISDEFECT_NULL_ERROR);
        }
        return inventoryStatus;
    }

    @Override
    public List<InventoryStatus> getAllInventoryStatus() {
        return caseLevelRcvdManager.findAllInventoryStatus();
    }

    /**
     * 根据uomCode查找数据。逻辑：uomCode具有唯一性
     *
     * @param uomCode
     * @return
     */
    @Override
    public Uom getUomByCode(String uomCode, String groupCode) {
        return caseLevelRcvdManager.getUomByCode(uomCode, groupCode);
    }


    /**
     * caseLevel箱收货完成
     *
     * @author mingwei.xie
     * @param whFunctionRcvd
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    public void caseLevelReceivingCompleted(WhFunctionRcvd whFunctionRcvd, WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId) {
        // 收货缓存数据，已汇总成装箱信息行对应的对象
        List<WhCartonCommand> rcvdCartonList = this.getRcvdCartonFromCache(whCartonCommand.getAsnId(), whCartonCommand.getContainerId(), userId, ouId, logId);
        if (null == rcvdCartonList || rcvdCartonList.isEmpty()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_DATA_NULL_ERROR, new Object[] {whCartonCommand.getContainerCode()});
        }
        this.caseLevelReceivingCompleted(whFunctionRcvd, whCartonCommand, rcvdCartonList, userId, ouId, logId);
    }

    /**
     * caseLevel箱收货完成
     *
     * @author mingwei.xie
     * @param whFunctionRcvd
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    public void caseLevelReceivingCompleted(WhFunctionRcvd whFunctionRcvd, WhCartonCommand whCartonCommand, List<WhCartonCommand> rcvdCartonList, Long userId, Long ouId, String logId) {
        if (null == rcvdCartonList || rcvdCartonList.isEmpty()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_DATA_NULL_ERROR, new Object[] {whCartonCommand.getContainerCode()});
        }

        // 库存信息列表
        List<WhSkuInventory> toSaveSkuInventoryList = new ArrayList<>();
        // 按照序列号管理类型分组的SN/残次库存信息
        List<WhSkuInventorySn> toSaveSkuInventorySnList = new ArrayList<>();
        // ASN收货日志对应的的SN/残次信息日志列表
        List<WhAsnRcvdLogCommand> whAsnRcvdLogCommandList = new ArrayList<>();

        //已更新的原装箱信息ID列表
        Set<Long> originUpdateCartonIdList = new HashSet<>();
        for (WhCartonCommand rcvdCartonCache : rcvdCartonList) {
            // 存入数据库之前数据校验
            this.checkRcvdCarton(rcvdCartonCache, whFunctionRcvd, userId, ouId, logId);

            // 配置库存记录信息，从仓库判断是否需要记录库存数量变化
            WhSkuInventory skuInventory = this.createWhSkuInventory(rcvdCartonCache, ouId, logId);
            // 保存新建的库存信息
            toSaveSkuInventoryList.add(skuInventory);

            // 创建ASN收货日志
            WhAsnRcvdLogCommand whAsnRcvdLogCommand = this.createWhAsnRcvdLog(rcvdCartonCache, userId, ouId, logId);
            whAsnRcvdLogCommandList.add(whAsnRcvdLogCommand);
            if(null != rcvdCartonCache.getId()){
                //记录哪些原装箱记录已更新，为了记录ASN收货日志
                originUpdateCartonIdList.add(rcvdCartonCache.getId());
            }

            // SN/残次信息库存表,待保存到数据库，需要根据序列号管理类型判断是否保存到其他表
            List<WhSkuInventorySn> skuInventorySnList = rcvdCartonCache.getSkuInventorySnList();
            // 配置SN/残次库存信息
            if (null != skuInventorySnList && !skuInventorySnList.isEmpty()) {
                // 创建ASN收货SN/残次日志,需要用到asnRcvdLog的id
                List<WhAsnRcvdSnLog> asnRcvdSnLogList = this.createWhAsnRcvdSnLog(skuInventorySnList, ouId, logId);

                // asn收货SN/残次日志需要用到asn收货日志的ID，按照asn收货日志-asn收货SN/残次日志分组保存
                whAsnRcvdLogCommand.setWhAsnRcvdSnLogList(asnRcvdSnLogList);

                // 创建SN/残次库存信息并且按照商品序列号管理类型分组保存
                this.getSkuInventorySnList(skuInventory, skuInventorySnList, toSaveSkuInventorySnList, ouId, logId);
            }// end-if 存在SN/残次信息
        }// end-for 遍历待保存的收货carton

        // ASN收货日志，在全都是新增的情况下，添加原计划收货数记录
        List<WhCartonCommand> originCartonList = this.getWhCartonListByContainer(whCartonCommand.getAsnId(), whCartonCommand.getContainerId(), userId, ouId, logId);
        for(WhCartonCommand originCarton : originCartonList){
            if(!originUpdateCartonIdList.contains(originCarton.getId())){
                // 创建ASN收货日志
                WhAsnRcvdLogCommand whAsnRcvdLogCommand = this.createWhAsnRcvdLog(originCarton, userId, ouId, logId);
                whAsnRcvdLogCommandList.add(whAsnRcvdLogCommand);
            }
        }

        // 统计asnLine收货数量
        Map<Long, BigDecimal> asnLineRcvdQtyMap = this.getAsnLineRcvdQtyMap(rcvdCartonList);

        // 更新ASN
        WhAsn toUpdateWhAsn = this.getWhAsnById(whCartonCommand.getAsnId(), ouId, logId);
        this.editWhAsn(asnLineRcvdQtyMap, toUpdateWhAsn, userId);

        // 更新ASN_line
        List<WhAsnLine> toUpdateAsnLineList = this.editWhAsnLine(asnLineRcvdQtyMap, userId, ouId);

        // 统计poLine的收货数量
        Map<Long, BigDecimal> poLineRcvdQtyMap = this.getPoLineRcvdQtyMap(asnLineRcvdQtyMap, toUpdateAsnLineList, logId);

        // 更新PO_line
        List<WhPoLine> toUpdatePoLineList = this.editWhPoLine(poLineRcvdQtyMap, userId, ouId);

        Map<Long, BigDecimal> poRcvdQtyMap = this.getWhPoRcvdQtyMap(toUpdatePoLineList, poLineRcvdQtyMap, logId);
        // 更新PO
        List<WhPo> toUpdateWhPoList = this.editWhPo(poRcvdQtyMap, userId, ouId);

        // 更新容器
        Container toUpdateContainer = this.getContainerById(whCartonCommand.getContainerId(), ouId);
        this.editContainer(toUpdateContainer, userId);

        // 创建容器辅助表信息
        ContainerAssist toSaveContainerAssist = this.createContainerAssist(toUpdateContainer, rcvdCartonList, userId, ouId, logId);

        // 根据仓库配置是否需要记录库存数量的变动
        Warehouse warehouse = caseLevelRcvdManager.getWarehouseById(ouId);
        // 在库存日志是否记录交易前后库存总数
        Boolean isTabbInvTotal = warehouse.getIsTabbInvTotal();

        try {
            // 在一个事务中保存所有数据到数据库
            caseLevelRcvdManager.caseLevelReceivingCompleted(rcvdCartonList, toSaveSkuInventoryList, toSaveSkuInventorySnList, whAsnRcvdLogCommandList, toUpdateAsnLineList, toUpdateWhAsn, toUpdatePoLineList, toUpdateWhPoList, toUpdateContainer,
                    toSaveContainerAssist, isTabbInvTotal, userId, ouId, logId);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_DATABASE_SAVE_ERROR);
        }
        // 清除所有缓存
        this.clearRcvdCache(whCartonCommand.getAsnId(), whCartonCommand.getContainerId(), userId, ouId, logId);
        try {
            // 释放容器占用缓存
            cacheManager.remove(CacheKeyConstant.WMS_CACHE_CL_OPT_USER_PREFIX + userId + "-" + whCartonCommand.getAsnId() + "-" + whCartonCommand.getContainerId());
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_RELEASE_CONTAINER_ERROR);
        }
        // 清除该货箱前次收货数据
        cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_LAST_RECD_QTY_PERFIX + userId + "-" + whCartonCommand.getAsnId() + "-" + whCartonCommand.getContainerId() + "-*");

        boolean isAsnRcvdFinished = caseLevelRcvdManager.checkIsAsnRcvdFinished(whCartonCommand.getAsnId(), ouId, logId);
        if (isAsnRcvdFinished) {
            try {
                checkInManagerProxy.releasePlatformByRcvdFinish(whCartonCommand.getAsnId(), ouId, userId, logId);
            } catch (Exception e) {
                // 释放月台，不需要和收货在一个事务，可以手工释放
                throw new BusinessException(ErrorCodes.CASELEVEL_RELEASE_PLATFORM_ERROR);
            }
        }
    }

    /**
     * 判断ASN的caseLevel收货是否完成
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @return
     */
    @Override
    public boolean isAsnCaseLevelNeedToRcvd(Long asnId, Long ouId) {
        return caseLevelRcvdManager.isAsnCaseLevelNeedToRcvd(asnId, ouId);
    }

    private ContainerAssist createContainerAssist(Container container, List<WhCartonCommand> rcvdCartonList, Long userId, Long ouId, String logId) {
        if (null == container || null == rcvdCartonList || null == userId || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        Set<Long> skuSet = new HashSet<>();
        BigDecimal containerSkuQty = new BigDecimal(0);
        BigDecimal containerSkuWeight = new BigDecimal(0);
        for (WhCartonCommand rcvdCarton : rcvdCartonList) {
            containerSkuQty = containerSkuQty.add(new BigDecimal(rcvdCarton.getSkuQty()));
            SkuRedisCommand skuRedisCommand = this.getSkuMasterBySkuId(rcvdCarton.getSkuId(), ouId, logId);
            BigDecimal cartonSkuRcvdQty = new BigDecimal(rcvdCarton.getSkuQty());

            containerSkuWeight = containerSkuWeight.add(cartonSkuRcvdQty.multiply(new BigDecimal(skuRedisCommand.getSku().getWeight())));
            skuSet.add(rcvdCarton.getSkuId());
        }

        // 创建容器辅助表对象
        Container2ndCategory container2ndCategory = this.getContainer2ndCategoryById(container.getTwoLevelType(), ouId);
        ContainerAssist containerAssist = new ContainerAssist();
        containerAssist.setContainerId(container.getId());
        containerAssist.setSysWeight(new BigDecimal(container2ndCategory.getWeight()).add(containerSkuWeight).doubleValue());
        containerAssist.setSysVolume(container2ndCategory.getVolume());
        containerAssist.setSysLength(container2ndCategory.getLength());
        containerAssist.setSysWidth(container2ndCategory.getWidth());
        containerAssist.setSysHeight(container2ndCategory.getHigh());
        containerAssist.setOuId(ouId);
        containerAssist.setCreateTime(new Date());
        containerAssist.setLastModifyTime(new Date());
        containerAssist.setOperatorId(userId);
        containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        containerAssist.setCartonQty(1L);
        containerAssist.setSkuCategory((long) skuSet.size());
        containerAssist.setSkuQty(containerSkuQty.longValue());
        containerAssist.setStoreQty(1L);
        containerAssist.setSkuAttrCategory((long) rcvdCartonList.size());

        return containerAssist;
    }

    private Container2ndCategory getContainer2ndCategoryById(Long container2ndCategoryId, Long ouId) {
        Container2ndCategory container2ndCategory = caseLevelRcvdManager.getContainer2ndCategoryById(container2ndCategoryId, ouId);
        return container2ndCategory;
    }

    private void getSkuInventorySnList(WhSkuInventory skuInventory, List<WhSkuInventorySn> skuInventorySnList, List<WhSkuInventorySn> toSaveSkuInventorySnList, Long ouId, String logId) {
        // 创建SN/残次库存信息
        this.createWhSkuInventorySn(skuInventory, skuInventorySnList, ouId, logId);
        // 商品主档信息
        SkuRedisCommand skuRedisCommand = this.getSkuMasterBySkuId(skuInventory.getSkuId(), ouId, logId);
        // 库存状态
        InventoryStatus inventoryStatus = this.getInventoryStatusById(skuInventory.getInvStatus());
        // 如果仅是入库管，非残次品，不保存InventorySn及log记录
        // 非残次品，不是入库管和全部管的类型，skuInventorySnList不会有数据
        if (inventoryStatus.getIsDefective() || Constants.SERIAL_NUMBER_TYPE_ALL.equals(skuRedisCommand.getSkuMgmt().getSerialNumberType())) {
            // 仅入库管的残次品，不保存SN号
            if (Constants.SERIAL_NUMBER_TYPE_IN.equals(skuRedisCommand.getSkuMgmt().getSerialNumberType())) {
                for (WhSkuInventorySn rcvdInventorySn : skuInventorySnList) {
                    WhSkuInventorySn invSn = new WhSkuInventorySn();
                    BeanUtils.copyProperties(rcvdInventorySn, invSn);
                    invSn.setSn(null);
                    toSaveSkuInventorySnList.add(invSn);
                }
            } else {
                // 添加到已保存的SN/残次信息分组中
                toSaveSkuInventorySnList.addAll(skuInventorySnList);
            }
        }
    }

    private Map<Long, BigDecimal> getAsnLineRcvdQtyMap(List<WhCartonCommand> whCartonCommandList) {
        // asnLine收货数据
        Map<Long, BigDecimal> asnLineRcvdQtyMap = new HashMap<>();
        for (WhCartonCommand rcvdCartonCache : whCartonCommandList) {
            // 已记录的asnLine收货总量
            BigDecimal asnLineRcvdQtyTotal = asnLineRcvdQtyMap.get(rcvdCartonCache.getAsnLineId());
            if (null == asnLineRcvdQtyTotal) {
                // 尚未记录任何一个asnLine的收货量
                asnLineRcvdQtyTotal = new BigDecimal(0);
            }
            // 装箱信息行的asnLine收货数量
            BigDecimal asnLineRcvdQty = new BigDecimal(rcvdCartonCache.getSkuQty().toString());
            // 保存asnLine的收货数量：记录的总量 + 此装箱信息行的收货量
            asnLineRcvdQtyMap.put(rcvdCartonCache.getAsnLineId(), asnLineRcvdQtyTotal.add(asnLineRcvdQty));
        }
        return asnLineRcvdQtyMap;
    }

    private Map<Long, BigDecimal> getPoLineRcvdQtyMap(Map<Long, BigDecimal> asnLineRcvdQtyMap, List<WhAsnLine> asnLineList, String logId) {
        Map<Long, BigDecimal> poLineRcvdQtyMap = new HashMap<>();
        for (WhAsnLine whAsnLine : asnLineList) {
            // asnLine收入总量
            BigDecimal asnLineRcvdQtyTotal = asnLineRcvdQtyMap.get(whAsnLine.getId());
            if (null == asnLineRcvdQtyTotal || BigDecimal.ZERO.equals(asnLineRcvdQtyTotal)) {
                log.error("CaseLevelManagerProxyImpl getPoLineRcvdQtyMap Error, asnLineRcvdQtyTotal is null or zero, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            // 已记录的poLine收货数量
            BigDecimal poLineRcvdQtyTotal = poLineRcvdQtyMap.get(whAsnLine.getPoLineId());
            if (null == poLineRcvdQtyTotal) {
                // 尚未记录的poLine收货数量
                poLineRcvdQtyTotal = new BigDecimal(0);
            }
            // 保存poLine的收货数量：已记录的poLine收货数量 + asnLine收入总量
            poLineRcvdQtyMap.put(whAsnLine.getPoLineId(), poLineRcvdQtyTotal.add(asnLineRcvdQtyTotal));
        }
        return poLineRcvdQtyMap;
    }

    /**
     * 装配asnLine的收货数
     * 
     * @author mingwei.xie
     * @param asnLineRcvdQtyMap asnLine的收货数集合
     * @param userId 用户ID
     * @param ouId 组织ID
     * @return 装配完成的asnLine列表
     */
    private List<WhAsnLine> editWhAsnLine(Map<Long, BigDecimal> asnLineRcvdQtyMap, Long userId, Long ouId) {
        List<WhAsnLine> toUpdateAsnLineList = new ArrayList<>();
        for (Long asnLineId : asnLineRcvdQtyMap.keySet()) {
            // 根据ID获取asnLine
            WhAsnLine whAsnLine = this.getWhAsnLineById(asnLineId, ouId);
            // asnLine收入总量
            BigDecimal asnLineRcvdQtyTotal = asnLineRcvdQtyMap.get(asnLineId);
            // asnLine原有收货量
            BigDecimal asnLineRcvdQtyOrg = new BigDecimal(whAsnLine.getQtyRcvd().toString());
            // 设置asnLine的总收货量： asnLine收入总量 + asnLine原有收货量
            whAsnLine.setQtyRcvd(asnLineRcvdQtyTotal.add(asnLineRcvdQtyOrg).doubleValue());
            whAsnLine.setCtnRcvd(whAsnLine.getCtnRcvd() + 1);
            whAsnLine.setModifiedId(userId);
            if (whAsnLine.getQtyRcvd() >= whAsnLine.getQtyPlanned()) {
                whAsnLine.setStatus(PoAsnStatus.ASNLINE_RCVD_FINISH);
            } else {
                whAsnLine.setStatus(PoAsnStatus.ASNLINE_RCVD);
            }
            // 待保存的asnLine列表
            toUpdateAsnLineList.add(whAsnLine);
        }
        return toUpdateAsnLineList;
    }

    private WhAsn editWhAsn(Map<Long, BigDecimal> asnLineRcvdQtyMap, WhAsn whAsn, Long userId) {
        if (null == asnLineRcvdQtyMap || null == whAsn || null == userId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        for (Long asnLineId : asnLineRcvdQtyMap.keySet()) {
            // asnLine收入总量
            BigDecimal asnLineRcvdQtyTotal = asnLineRcvdQtyMap.get(asnLineId);
            // 统计ASN的收货数据：asn原有收货量 + asnLine总的收货量
            whAsn.setQtyRcvd(new BigDecimal(whAsn.getQtyRcvd().toString()).add(asnLineRcvdQtyTotal).doubleValue());
        }
        whAsn.setCtnRcvd(whAsn.getCtnRcvd() + 1);
        whAsn.setModifiedId(userId);
        return whAsn;
    }

    private List<WhPoLine> editWhPoLine(Map<Long, BigDecimal> poLineRcvdQtyMap, Long userId, Long ouId) {
        List<WhPoLine> toUpdatePoLineList = new ArrayList<>();
        for (Long poLineId : poLineRcvdQtyMap.keySet()) {
            // 更具ID获取poLine
            WhPoLine whPoLine = this.getWhPoLineById(poLineId, ouId);
            // poLine总的收货量
            BigDecimal poLineRcvdQtyTotal = poLineRcvdQtyMap.get(poLineId);
            // 设置poLine的收货数量：poLine总的收货量 + 原有收货量
            whPoLine.setQtyRcvd(poLineRcvdQtyTotal.add(new BigDecimal(whPoLine.getQtyRcvd().toString())).doubleValue());
            if (whPoLine.getQtyRcvd() >= whPoLine.getQtyPlanned()) {
                whPoLine.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
            } else {
                whPoLine.setStatus(PoAsnStatus.POLINE_RCVD);
            }
            whPoLine.setCtnRcvd(whPoLine.getCtnRcvd() + 1);
            whPoLine.setModifiedId(userId);
            // 待保存的poLine
            toUpdatePoLineList.add(whPoLine);
        }
        return toUpdatePoLineList;
    }

    private Map<Long, BigDecimal> getWhPoRcvdQtyMap(List<WhPoLine> whPoLineList, Map<Long, BigDecimal> poLineRcvdQtyMap, String logId) {
        Map<Long, BigDecimal> poRcvdQtyMap = new HashMap<>();
        for (WhPoLine whPoLine : whPoLineList) {
            // asnLine收入总量
            BigDecimal poLineRcvdQtyTotal = poLineRcvdQtyMap.get(whPoLine.getId());
            if (null == poLineRcvdQtyTotal || BigDecimal.ZERO.equals(poLineRcvdQtyTotal)) {
                log.error("CaseLevelManagerProxyImpl poRcvdQtyMap Error, poLineRcvdQtyTotal is null or zero, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            // 已记录的po收货数量
            BigDecimal poRcvdQtyTotal = poRcvdQtyMap.get(whPoLine.getPoId());
            if (null == poRcvdQtyTotal) {
                // 尚未记录的poLine收货数量
                poRcvdQtyTotal = new BigDecimal(0);
            }
            // 保存po的收货数量：已记录的po收货数量 + poLine收入总量
            poRcvdQtyMap.put(whPoLine.getPoId(), poRcvdQtyTotal.add(poLineRcvdQtyTotal));
        }
        return poRcvdQtyMap;
    }

    private List<WhPo> editWhPo(Map<Long, BigDecimal> poRcvdQtyMap, Long userId, Long ouId) {
        if (null == poRcvdQtyMap || null == userId || null == ouId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        List<WhPo> toUpdatePoList = new ArrayList<>();
        for (Long poId : poRcvdQtyMap.keySet()) {
            // 更具ID获取p
            WhPo whPo = this.getWhPoById(poId, ouId);
            // po总的收货量
            BigDecimal poRcvdQtyTotal = poRcvdQtyMap.get(poId);
            // 设置po的收货数量：po总的收货量 + 原有收货量
            whPo.setQtyRcvd(poRcvdQtyTotal.add(new BigDecimal(whPo.getQtyRcvd().toString())).doubleValue());
            whPo.setCtnRcvd(whPo.getCtnRcvd() + 1);
            whPo.setModifiedId(userId);
            // 待保存的poLine
            toUpdatePoList.add(whPo);
        }
        return toUpdatePoList;

    }

    private void editContainer(Container container, Long userId) {
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
        container.setOperatorId(userId);
    }

    /**
     * 配置库存记录信息
     *
     * @author mingwei.xie
     * @param rcvdCartonCache 收货的装箱信息缓存
     * @param ouId 组织ID
     * @param logId 日志ID
     */
    private WhSkuInventory createWhSkuInventory(WhCartonCommand rcvdCartonCache, Long ouId, String logId) {
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setSkuId(rcvdCartonCache.getSkuId());
        if (!StringUtil.isEmpty(rcvdCartonCache.getExtContainerCode())) {
            ContainerCommand outContainer = this.getContainerByCode(rcvdCartonCache.getExtContainerCode(), ouId);
            skuInventory.setOuterContainerId(outContainer.getId());
        }
        skuInventory.setInsideContainerId(rcvdCartonCache.getContainerId());
        WhAsn whAsn = this.getWhAsnById(rcvdCartonCache.getAsnId(), ouId, logId);
        skuInventory.setCustomerId(whAsn.getCustomerId());
        skuInventory.setStoreId(whAsn.getStoreId());
        // 占用编码是内部编码
        skuInventory.setOccupationCode(whAsn.getAsnCode());
        skuInventory.setOnHandQty(rcvdCartonCache.getSkuQty());
        skuInventory.setAllocatedQty(0d);
        skuInventory.setToBeFilledQty(0d);
        skuInventory.setFrozenQty(0d);
        skuInventory.setInvStatus(rcvdCartonCache.getInvStatus());
        skuInventory.setInvType(rcvdCartonCache.getInvType());
        skuInventory.setBatchNumber(rcvdCartonCache.getBatchNo());
        skuInventory.setMfgDate(rcvdCartonCache.getMfgDate());
        skuInventory.setExpDate(rcvdCartonCache.getExpDate());
        skuInventory.setCountryOfOrigin(rcvdCartonCache.getCountryOfOrigin());
        skuInventory.setInvAttr1(rcvdCartonCache.getInvAttr1());
        skuInventory.setInvAttr2(rcvdCartonCache.getInvAttr2());
        skuInventory.setInvAttr3(rcvdCartonCache.getInvAttr3());
        skuInventory.setInvAttr4(rcvdCartonCache.getInvAttr4());
        skuInventory.setInvAttr5(rcvdCartonCache.getInvAttr5());
        String uuid = null;
        try {
            uuid = SkuInventoryUuid.invUuid(skuInventory);
        } catch (NoSuchAlgorithmException e) {
            log.error("caseLevelReceivingCompleted createWhSkuInventory error, throw NoSuchAlgorithmException, skuInventory is:[{}], logId is:[{}]", skuInventory, logId);
            throw new BusinessException(ErrorCodes.CASELEVEL_UUID_ERROR);
        }
        skuInventory.setUuid(uuid);
        skuInventory.setIsLocked(false);
        skuInventory.setOuId(rcvdCartonCache.getOuId());
        skuInventory.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ASN);
        skuInventory.setLastModifyTime(new Date());

        return skuInventory;
    }

    /**
     * 配置SN/残次库存信息
     *
     * @author mingwei.xie
     * @param skuInventory
     * @param skuInventorySnList
     * @param ouId
     * @param logId
     */
    private void createWhSkuInventorySn(WhSkuInventory skuInventory, List<WhSkuInventorySn> skuInventorySnList, Long ouId, String logId) {
        for (WhSkuInventorySn skuInventorySn : skuInventorySnList) {
            // 占用单据号
            skuInventorySn.setOccupationCode(skuInventory.getOccupationCode());
            // 状态 1:在库2:已分配3:冻结
            skuInventorySn.setStatus(Constants.INVENTORY_SN_STATUS_ONHAND);
            // 内部对接码
            skuInventorySn.setUuid(skuInventory.getUuid());
            skuInventorySn.setOuId(ouId);
        }
    }

    /**
     * 创建ASN收货日志
     *
     * @author mingwei.xie
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    private WhAsnRcvdLogCommand createWhAsnRcvdLog(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId) {
        WhAsnRcvdLogCommand whAsnRcvdLogCommand = new WhAsnRcvdLogCommand();
        whAsnRcvdLogCommand.setAsnId(whCartonCommand.getAsnId());
        whAsnRcvdLogCommand.setAsnLineId(whCartonCommand.getAsnLineId());
        WhAsn whAsn = this.getWhAsnById(whCartonCommand.getAsnId(), ouId, logId);
        SkuRedisCommand skuRedisCommand = this.getSkuMasterBySkuId(whCartonCommand.getSkuId(), ouId, logId);
        whAsnRcvdLogCommand.setAsnCode(whAsn.getAsnCode());
        whAsnRcvdLogCommand.setSkuCode(skuRedisCommand.getSku().getCode());
        whAsnRcvdLogCommand.setSkuName(skuRedisCommand.getSku().getName());
        whAsnRcvdLogCommand.setQuantity(whCartonCommand.getQuantity().longValue());
        whAsnRcvdLogCommand.setQtyRcvd(whCartonCommand.getSkuQty());
        Container container = this.getContainerById(whCartonCommand.getContainerId(), ouId);
        whAsnRcvdLogCommand.setContainerCode(container.getCode());
        whAsnRcvdLogCommand.setContainerName(container.getName());
        whAsnRcvdLogCommand.setMfgDate(whCartonCommand.getMfgDate());
        whAsnRcvdLogCommand.setExpDate(whCartonCommand.getExpDate());
        whAsnRcvdLogCommand.setBatchNo(whCartonCommand.getBatchNo());
        whAsnRcvdLogCommand.setCountryOfOrigin(whCartonCommand.getCountryOfOrigin());
        InventoryStatus inventoryStatus = this.getInventoryStatusById(whCartonCommand.getInvStatus());
        whAsnRcvdLogCommand.setInvStatus(inventoryStatus.getName());
        if (!StringUtil.isEmpty(whCartonCommand.getInvType())) {
            whAsnRcvdLogCommand.setInvType(this.getSysDictionary(Constants.INVENTORY_TYPE, whCartonCommand.getInvType()).getDicLabel());
        }
        if (!StringUtil.isEmpty(whCartonCommand.getInvAttr1())) {
            whAsnRcvdLogCommand.setInvAttr1(this.getSysDictionary(Constants.INVENTORY_ATTR_1, whCartonCommand.getInvAttr1()).getDicLabel());
        }
        if (!StringUtil.isEmpty(whCartonCommand.getInvAttr2())) {
            whAsnRcvdLogCommand.setInvAttr2(this.getSysDictionary(Constants.INVENTORY_ATTR_2, whCartonCommand.getInvAttr2()).getDicLabel());
        }
        if (!StringUtil.isEmpty(whCartonCommand.getInvAttr3())) {
            whAsnRcvdLogCommand.setInvAttr3(this.getSysDictionary(Constants.INVENTORY_ATTR_3, whCartonCommand.getInvAttr3()).getDicLabel());
        }
        if (!StringUtil.isEmpty(whCartonCommand.getInvAttr4())) {
            whAsnRcvdLogCommand.setInvAttr4(this.getSysDictionary(Constants.INVENTORY_ATTR_4, whCartonCommand.getInvAttr4()).getDicLabel());
        }
        if (!StringUtil.isEmpty(whCartonCommand.getInvAttr5())) {
            whAsnRcvdLogCommand.setInvAttr5(this.getSysDictionary(Constants.INVENTORY_ATTR_5, whCartonCommand.getInvAttr5()).getDicLabel());
        }
        whAsnRcvdLogCommand.setOuId(ouId);
        whAsnRcvdLogCommand.setCreateTime(new Date());
        whAsnRcvdLogCommand.setLastModifyTime(new Date());
        whAsnRcvdLogCommand.setOperatorId(userId);

        return whAsnRcvdLogCommand;
    }

    /**
     * 配置ASN收货SN/残次日志
     *
     * @author mingwei.xie
     * @param skuInventorySnList
     * @param ouId
     * @param logId
     * @return
     */
    private List<WhAsnRcvdSnLog> createWhAsnRcvdSnLog(List<WhSkuInventorySn> skuInventorySnList, Long ouId, String logId) {
        if (null == skuInventorySnList || null == ouId || null == logId) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhAsnRcvdSnLog> asnRcvdSnLogList = new ArrayList<>();
        for (WhSkuInventorySn skuInventorySn : skuInventorySnList) {
            // ASN收货SN/残次日志
            WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
            // 设置asn收货日志ID，保存到数据库之后返回的主键
            // whAsnRcvdSnLog.setAsnRcvdId();
            whAsnRcvdSnLog.setSn(skuInventorySn.getSn());
            whAsnRcvdSnLog.setDefectWareBarcode(skuInventorySn.getDefectWareBarcode());
            whAsnRcvdSnLog.setOuId(ouId);
            if (Constants.SKU_SN_DEFECT_SOURCE_STORE.equals(skuInventorySn.getDefectSource())) {
                try {
                    StoreDefectType storeDefectType = this.getStoreDefectTypeById(skuInventorySn.getDefectTypeId());
                    StoreDefectReasons storeDefectReasons = this.getStoreDefectReasonsById(skuInventorySn.getDefectReasonsId());
                    whAsnRcvdSnLog.setDefectType(storeDefectType.getName());
                    whAsnRcvdSnLog.setDefectReasons(storeDefectReasons.getName());
                } catch (Exception e) {
                    log.error("createWhAsnRcvdSnLog throw exception");
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
            } else if (Constants.SKU_SN_DEFECT_SOURCE_WH.equals(skuInventorySn.getDefectSource())) {
                try {
                    WarehouseDefectType warehouseDefectType = this.getWarehouseDefectTypeById(skuInventorySn.getDefectTypeId(), ouId);
                    WarehouseDefectReasons warehouseDefectReasons = this.getWarehouseDefectReasonsById(skuInventorySn.getDefectReasonsId(), ouId);
                    whAsnRcvdSnLog.setDefectType(warehouseDefectType.getName());
                    whAsnRcvdSnLog.setDefectReasons(warehouseDefectReasons.getName());
                } catch (Exception e) {
                    log.error("createWhAsnRcvdSnLog throw exception");
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
            }
            asnRcvdSnLogList.add(whAsnRcvdSnLog);
        }
        return asnRcvdSnLogList;
    }

    private WhAsnLine getWhAsnLineById(Long id, Long ouId) {
        return asnLineManager.findWhAsnLineByIdToShard(id, ouId);
    }

    private WhPoLine getWhPoLineById(Long id, Long ouId) {
        return poLineManager.findWhPoLineByIdOuIdToShard(id, ouId);
    }

    private WhPo getWhPoById(Long id, Long ouId) {
        WhPo whPo = selectPoAsnManagerProxy.findWhPoById(id, ouId);
        if (null == whPo) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        return whPo;
    }


    private List<WhCartonCommand> getRcvdCartonFromCache(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        String cacheKeyPattern = CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId + "-*";
        List<String> cartonCacheKeyList = null;
        try {
            cartonCacheKeyList = cacheManager.Keys(cacheKeyPattern);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        List<WhCartonCommand> rcvdCartonList = new ArrayList<>();
        for (String cartonCacheKey : cartonCacheKeyList) {
            WhCartonCommand rcvdCarton = null;
            try {
                rcvdCarton = cacheManager.getObject(cartonCacheKey.substring(cartonCacheKey.indexOf(CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX)));
            } catch (Exception e) {
                throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
            }
            if (null == rcvdCarton) {
                throw new BusinessException(ErrorCodes.CASELEVEL_GET_CACHE_ERROR);
            }
            rcvdCartonList.add(rcvdCarton);
        }
        return rcvdCartonList;
    }

    /**
     * 释放操作的caseLevel货箱
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param userId
     * @param ouId
     * @param logId
     */
    private void releaseContainerByOptUser(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        if (null == asnId || null == containerId || null == userId || null == ouId) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_KEY_ERROR);
        }
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_OPT_USER_PREFIX + userId + "-" + asnId + "-" + containerId;
        try {
            Container container = this.getContainerById(containerId, ouId);
            // 容器lifecycle修改为可用
            if (null == container) {
                throw new BusinessException(ErrorCodes.CASELEVEL_NULL);
            }
            // 容器必须是收货中的占用状态
            if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != container.getLifecycle() || ContainerStatus.CONTAINER_STATUS_RCVD != container.getStatus()) {
                throw new BusinessException(ErrorCodes.CASELEVEL_CONTAINER_UNAVAILABLE);
            }
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
            container.setOperatorId(userId);
            // 从数据库释放货箱
            caseLevelRcvdManager.updateContainerStatus(container);

            try {
                // 从缓存删除货箱操作人
                cacheManager.remove(cacheKey);
            } catch (Exception e) {
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_RCVD);
                container.setOperatorId(userId);
                // 回滚释放货箱操作
                caseLevelRcvdManager.updateContainerStatus(container);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_RELEASE_CONTAINER_ERROR);
        }
    }

    private void clearRcvdCache(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        try {
            // caseLevel本次扫描已收SN/残次品信息缓存前缀
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_SCAN_RECD_SN_DEFECT_INFO_PREFIX + userId + "-" + asnId + "-" + containerId + "-*");
            // caseLevel本次收货已收carton缓存前缀
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_RECD_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId + "-*");
            // caseLevel收货装箱信息表缓存前缀
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_ORIGIN_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId + "-*");
            // caseLevel货箱SN缓存前缀
            cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_CL_ORIGIN_SN_PREFIX + userId + "-" + asnId + "-" + containerId + "-*");
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
    }

    /**
     * 获取确认收货的商品的序列化信息作为标识
     *
     * @author mingwei.xie
     * @param whCarton
     * @param logId
     * @return
     */
    private String getWhCartonUUID(WhCartonCommand whCarton, String logId) {

        // int hashCode =
        // new
        // HashCodeBuilder().append(whCarton.getAsnId()).append(whCarton.getSkuId()).append(whCarton.getContainerId()).append(whCarton.getMfgDate()).append(whCarton.getExpDate()).append(whCarton.getBatchNo())
        // .append(whCarton.getCountryOfOrigin()).append(whCarton.getInvAttr1()).append(whCarton.getInvAttr2()).append(whCarton.getInvAttr3()).append(whCarton.getInvAttr4()).append(whCarton.getInvAttr5()).append(whCarton.getInvType())
        // .append(whCarton.getInvStatus()).toHashCode();

        if (null == whCarton) {
            return null;
        }
        String uuid = null;
        // 拼接库存对应字段值
        String forMatString =
                whCarton.getAsnId() + "" + whCarton.getContainerId() + "" + whCarton.getSkuId() + "" + (whCarton.getMfgDate() == null ? "" : whCarton.getMfgDate()) + "" + (whCarton.getExpDate() == null ? "" : whCarton.getExpDate()) + ""
                        + (whCarton.getBatchNo() == null ? "" : whCarton.getBatchNo()) + "" + "" + (whCarton.getCountryOfOrigin() == null ? "" : whCarton.getCountryOfOrigin()) + "" + (whCarton.getInvAttr1() == null ? "" : whCarton.getInvAttr1()) + ""
                        + (whCarton.getInvAttr2() == null ? "" : whCarton.getInvAttr2()) + "" + (whCarton.getInvAttr3() == null ? "" : whCarton.getInvAttr3()) + "" + (whCarton.getInvAttr4() == null ? "" : whCarton.getInvAttr4()) + ""
                        + (whCarton.getInvAttr5() == null ? "" : whCarton.getInvAttr5()) + "" + (whCarton.getInvType() == null ? "" : whCarton.getInvType()) + whCarton.getInvStatus();
        try {
            uuid = Md5Util.getMd5(forMatString);
        } catch (NoSuchAlgorithmException e) {
            log.error("getWhCartonUUID error, getWhCartonUUID throw NoSuchAlgorithmException, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CASELEVEL_UUID_ERROR);
        }
        return uuid;
    }

    /**
     * 从缓存获取caseLevel货箱的装箱信息，缓存中没有则从数据库加载到缓存中 数据为null不抛出异常
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param ouId
     * @param logId
     * @return
     */
    private List<WhCartonCommand> getCartonByContainerFromCache(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        List<WhCartonCommand> whCartonCommandList = null;

        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_ORIGIN_CARTON_PREFIX + userId + "-" + asnId + "-" + containerId;
        try {
            whCartonCommandList = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            log.error("getWhCartonListBySku cacheManager.getObject error logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CASELEVEL_SN_CACHE_ERROR);
        }
        if (null == whCartonCommandList || whCartonCommandList.isEmpty()) {
            whCartonCommandList = caseLevelRcvdManager.getCaseLevelWhCartonListByContainer(asnId, containerId, ouId);
            try {
                cacheManager.setObject(cacheKey, whCartonCommandList, CacheKeyConstant.CACHE_ONE_DAY);

                whCartonCommandList = cacheManager.getObject(cacheKey);
                if (null == whCartonCommandList || whCartonCommandList.isEmpty()) {
                    log.warn("cache whCartonCommandList by container, whCartonCommandList is:[{}], logId is:[{}]", whCartonCommandList, logId);
                }
            } catch (Exception e) {
                log.error("getWhCartonListBySku cacheManager.setMapObject error logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.CASELEVEL_SN_CACHE_ERROR);
            }
        }
        return whCartonCommandList;
    }

    /**
     * 从缓存中获取caseLevel货箱涉及的asnLine的所有SN号
     * 
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    private List<WhAsnSn> getCaseLevelWhAsnSnFromCache(Long asnId, Long containerId, Long userId, Long ouId, String logId) {
        String cacheKey = CacheKeyConstant.WMS_CACHE_CL_ORIGIN_SN_PREFIX + userId + "-" + asnId + "-" + containerId;
        List<WhAsnSn> whAsnSnList = null;
        try {
            whAsnSnList = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            log.error("getWhAsnSnFromCache cacheManager.getObject error logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CASELEVEL_SN_CACHE_ERROR);
        }
        if (null == whAsnSnList || whAsnSnList.isEmpty()) {
            List<WhCartonCommand> whCartonCommandList = this.getCartonByContainerFromCache(asnId, containerId, userId, ouId, logId);
            Set<Long> asnLineSet = new HashSet<>();
            if (null != whCartonCommandList) {
                for (WhCartonCommand whCartonCommand : whCartonCommandList) {
                    asnLineSet.add(whCartonCommand.getAsnLineId());
                }
            }
            for (Long asnLineId : asnLineSet) {
                if (null == whAsnSnList) {
                    whAsnSnList = new ArrayList<>();
                }
                whAsnSnList.addAll(caseLevelRcvdManager.findSkuSnByAsnLine(asnLineId, ouId));
            }
        }
        try {
            cacheManager.setObject(cacheKey, whAsnSnList, CacheKeyConstant.CACHE_ONE_DAY);
        } catch (Exception e) {
            log.error("getWhAsnSnFromCache cacheManager.setMapObject error logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CASELEVEL_SN_CACHE_ERROR);
        }
        try {
            whAsnSnList = cacheManager.getObject(cacheKey);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CASELEVEL_CACHE_ERROR);
        }
        if (null == whAsnSnList || whAsnSnList.isEmpty()) {
            log.warn("cache whAsnSnList by skuId, whAsnSnList is empty, logId is:[{}]", logId);
        }
        return whAsnSnList;
    }

    /**
     * 比较日期相差天数，正数表示比第二个日期大的天数 负数表示比第二个日期小的天数
     *
     * @author mingwei.xie
     * @param date 第一个日期
     * @param otherDate 第二个日期
     * @return 比较日期相差天数，正数表示比第二个日期大的天数，负数表示比第二个日期小的天数
     */
    private int getIntervalDays(Date date, Date otherDate) {
        int num = 0;
        try {
            Date dateTmp = dateFormat.parse(dateFormat.format(date));
            Date otherDateTmp = dateFormat.parse(dateFormat.format(otherDate));
            if (dateTmp != null && otherDateTmp != null) {
                long time = dateTmp.getTime() - otherDateTmp.getTime();
                num = (int) (time / (24 * 60 * 60 * 1000));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.CASELEVEL_PARSE_DATE_ERROR);
        }
        return num;
    }

    /**
     * 比较两个日期大小，到天 第一个日期小返回-1，相等返回0，第一个日期大返回1
     *
     * @author mingwei.xie
     * @param date 第一个日期
     * @param otherDate 第二个日期
     * @return 第一个日期小返回-1，相等返回0，第一个日期大返回1
     */
    private int compareDate(Date date, Date otherDate) {
        int result = 0;
        try {
            Date dateTmp = dateFormat.parse(dateFormat.format(date));
            Date otherDateTmp = dateFormat.parse(dateFormat.format(otherDate));
            result = dateTmp.compareTo(otherDateTmp);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.CASELEVEL_PARSE_DATE_ERROR);
        }
        return result;
    }

    /**
     * 检查待缓存的数据是否符合要求
     * 
     * @author mingwei.xie
     * @param whCartonCommand 待缓存的信息
     * @param whFunctionRcvd 收货功能信息
     * @param ouId 组织ID
     * @param logId 日志ID
     */
    private void checkRcvdCarton(WhCartonCommand whCartonCommand, WhFunctionRcvd whFunctionRcvd, Long userId, Long ouId, String logId) {
        // 商品主档信息
        SkuRedisCommand skuRedisCommand = this.getSkuMasterBySkuId(whCartonCommand.getSkuId(), ouId, logId);
        // 商品必须为可用状态
        if (null == skuRedisCommand || !BaseModel.LIFECYCLE_NORMAL.equals(skuRedisCommand.getSku().getLifecycle())) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_UNAVAILABLE, new Object[] {whCartonCommand.getSkuBarcode()});
        }

        // 检查维护的属性是否都维护了
        this.checkSkuMaintainedAttr(whCartonCommand, skuRedisCommand, ouId, logId);
        // 检查商品效期、库存类型、库存状态
        this.checkAttrValid(whFunctionRcvd, whCartonCommand, skuRedisCommand, ouId, logId);

        // 不允许差异收货，验证当前属性的装箱信息是否存在
        if (!whFunctionRcvd.getIsInvattrDiscrepancyAllowrcvd()) {
            // 获取caseLevel装箱中该sku信息
            List<WhCartonCommand> whCartonCommandList = this.getWhCartonListBySku(whCartonCommand.getAsnId(), whCartonCommand.getContainerId(), whCartonCommand.getSkuId(), userId, ouId, logId);
            // 获取匹配上的装箱信息行
            List<WhCartonCommand> matchWhCartonInfoList = this.getMatchWhCartonInfoList(whFunctionRcvd, skuRedisCommand, whCartonCommand, whCartonCommandList, ouId, logId);
            if (null == matchWhCartonInfoList || matchWhCartonInfoList.isEmpty()) {
                // 所选的属性无匹配的装箱信息
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_ERROR);
            }

        }
        // 库存状态是残次类型
        InventoryStatus cartonInvStatus = this.getInventoryStatusById(whCartonCommand.getInvStatus());
        if (cartonInvStatus.getIsDefective()) {
            if (null == whCartonCommand.getSkuInventorySnList() || whCartonCommand.getSkuInventorySnList().size() != whCartonCommand.getSkuQty()) {
                throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_SN_DEFECT_ERROR);
            }
            List<WhSkuInventorySn> skuInventorySnList = whCartonCommand.getSkuInventorySnList();
            Set<String> defectCodeSet = new HashSet<>();
            for (WhSkuInventorySn skuInventorySn : skuInventorySnList) {
                if (StringUtil.isEmpty(skuInventorySn.getDefectWareBarcode())) {
                    // 残次条码不存在
                    throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_DEFECT_CODE_ERROR);
                } else if (defectCodeSet.contains(skuInventorySn.getDefectWareBarcode())) {
                    // 残次条码重复
                    throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_DEFECT_CODE_REPEAT_ERROR, new Object[] {skuInventorySn.getDefectWareBarcode()});
                } else {
                    defectCodeSet.add(skuInventorySn.getDefectWareBarcode());
                }
                if (null == skuInventorySn.getDefectReasonsId()) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_DEFECT_REASONS_ID_NULL_ERROR);
                }
                if (null == skuInventorySn.getDefectTypeId()) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_DEFECT_TYPE_ID_NULL_ERROR);
                }
                if (StringUtil.isEmpty(skuInventorySn.getDefectSource())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_DEFECT_SOURCE_NULL_ERROR);
                }
            }
        }
        // 管理序列号
        if (Constants.SERIAL_NUMBER_TYPE_IN.equals(skuRedisCommand.getSkuMgmt().getSerialNumberType()) || Constants.SERIAL_NUMBER_TYPE_ALL.equals(skuRedisCommand.getSkuMgmt().getSerialNumberType())) {
            List<WhSkuInventorySn> skuInventorySnList = whCartonCommand.getSkuInventorySnList();
            if (null == skuInventorySnList || skuInventorySnList.size() != whCartonCommand.getSkuQty()) {
                throw new BusinessException(ErrorCodes.CASELEVEL_RCVD_SN_DEFECT_ERROR);
            }
            Set<String> snSet = new HashSet<>();
            for (WhSkuInventorySn skuInventorySn : skuInventorySnList) {
                // 验证SN号是否存在caseLevel货箱中（此处检测的是caseLevel货箱所涉及的asnLine所包含的所有SN号，也许存有其他货箱的SN号）
                // boolean isSnCodeExist = this.isCaseLevelSnExist(whCartonCommand.getAsnId(),
                // whCartonCommand.getContainerId(), whCartonCommand.getSkuId(),
                // whCartonCommand.getSnCode(), null, ouId, logId);
                // if (!isSnCodeExist) {
                // // SN号不在货箱内
                // throw new BusinessException(ErrorCodes.CASELEVEL_SN_NOT_EXIST_ERROR, new Object[]
                // {skuInventorySn.getSn()});
                // } else
                if (snSet.contains(skuInventorySn.getSn())) {
                    // SN号重复收入
                    throw new BusinessException(ErrorCodes.CASELEVEL_SN_EXIST_ERROR, new Object[] {skuInventorySn.getSn()});
                } else if (StringUtil.isEmpty(skuInventorySn.getSn())) {
                    // SN号不存在
                    throw new BusinessException(ErrorCodes.CASELEVEL_SN_CODE_NULL_ERROR);
                } else {
                    snSet.add(skuInventorySn.getSn());
                }
            }
        }
    }

    /**
     * caseLevel货箱商品是否维护了所有属性
     *
     * @author mingwei.xie
     * @param whCartonCommand
     * @param ouId
     * @param logId
     * @return
     */
    private void checkSkuMaintainedAttr(WhCartonCommand whCartonCommand, SkuRedisCommand skuRedisCommand, Long ouId, String logId) {
        // 管理效期，装箱表没有维护生产日期或失效日期
        if (skuRedisCommand.getSkuMgmt().getIsValid()) {
            if (null == whCartonCommand.getMfgDate() || null == whCartonCommand.getExpDate()) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            }
        }

        // 管理批次号
        if (skuRedisCommand.getSkuMgmt().getIsBatchNo()) {
            if (StringUtil.isEmpty(whCartonCommand.getBatchNo())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            }
        }

        // 管理原产地
        if (skuRedisCommand.getSkuMgmt().getIsCountryOfOrigin()) {
            if (StringUtil.isEmpty(whCartonCommand.getCountryOfOrigin())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            }
        }

        // 库存属性1
        if (skuRedisCommand.getSkuExtattr().getInvAttr1()) {
            if (StringUtil.isEmpty(whCartonCommand.getInvAttr1())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            } else {
                SysDictionary targetSysDictionaryInvAttr1 = this.getSysDictionary(Constants.INVENTORY_ATTR_1, whCartonCommand.getInvAttr1());
                if (null == targetSysDictionaryInvAttr1) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR);
                }
            }
        }

        // 库存属性2
        if (skuRedisCommand.getSkuExtattr().getInvAttr2()) {
            if (StringUtil.isEmpty(whCartonCommand.getInvAttr2())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            } else {
                SysDictionary targetSysDictionaryInvAttr2 = this.getSysDictionary(Constants.INVENTORY_ATTR_2, whCartonCommand.getInvAttr2());
                if (null == targetSysDictionaryInvAttr2) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR);
                }
            }
        }

        // 库存属性3
        if (skuRedisCommand.getSkuExtattr().getInvAttr3()) {
            if (StringUtil.isEmpty(whCartonCommand.getInvAttr3())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            } else {
                SysDictionary targetSysDictionaryInvAttr3 = this.getSysDictionary(Constants.INVENTORY_ATTR_3, whCartonCommand.getInvAttr3());
                if (null == targetSysDictionaryInvAttr3) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR);
                }
            }
        }

        // 库存属性4
        if (skuRedisCommand.getSkuExtattr().getInvAttr4()) {
            if (StringUtil.isEmpty(whCartonCommand.getInvAttr4())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            } else {
                SysDictionary targetSysDictionaryInvAttr4 = this.getSysDictionary(Constants.INVENTORY_ATTR_4, whCartonCommand.getInvAttr4());
                if (null == targetSysDictionaryInvAttr4) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR);
                }
            }
        }

        // 库存属性5
        if (skuRedisCommand.getSkuExtattr().getInvAttr5()) {
            if (StringUtil.isEmpty(whCartonCommand.getInvAttr5())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            } else {
                SysDictionary targetSysDictionaryInvAttr5 = this.getSysDictionary(Constants.INVENTORY_ATTR_5, whCartonCommand.getInvAttr5());
                if (null == targetSysDictionaryInvAttr5) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR);
                }
            }
        }

        // 管理库存类型
        if (skuRedisCommand.getSkuMgmt().getIsInvType()) {
            if (StringUtil.isEmpty(whCartonCommand.getInvType())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            } else {
                SysDictionary targetSysDictionaryInvType = this.getSysDictionary(Constants.INVENTORY_TYPE, whCartonCommand.getInvType());
                if (null == targetSysDictionaryInvType) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR);
                }
            }
        }

        // 库存状态是否为null
        if (null == whCartonCommand.getInvStatus()) {
            throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
        } else {
            InventoryStatus inventoryStatus = this.getInventoryStatusById(whCartonCommand.getInvStatus());
            if (null == inventoryStatus) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_NULL);
            }
        }
    }

    /**
     * 校验商品效期、库存类型、库存状态，不合法则抛出异常 商品管理效期，已过期，不收过期商品 未过保质期，有效天数达不到系统要求 或者，有效天数大于系统要求
     *
     * 管理库存类型，不允许差异收货，功能维护了库存类型，单据库存类型必须和功能的一致 不允许差异收货，功能维护了库存状态，单据库存状态必须和功能的一致
     *
     * @author mingwei.xie
     * @param whFunctionRcvd 收货功能配置信息
     * @param whCartonCommand 已确认的商品信息
     * @param logId
     */
    private void checkAttrValid(WhFunctionRcvd whFunctionRcvd, WhCartonCommand whCartonCommand, SkuRedisCommand skuRedisCommand, Long ouId, String logId) {
        // 商品管理效期，需要检查效期是否合法
        if (skuRedisCommand.getSkuMgmt().getIsValid()) {
            if (this.compareDate(new Date(), whCartonCommand.getMfgDate()) == -1) {
                // 生产日期大于当前日期
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_MFGDATE_ERROR);
            } else if (this.compareDate(new Date(), whCartonCommand.getExpDate()) == 1) {
                // 失效日期小于当前日期
                // 已过保质期且不收过期商品
                if (!skuRedisCommand.getSkuMgmt().getIsExpiredGoodsReceive()) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_EXPIRED);
                }
            } else {
                // 未过保质期，有效天数达不到系统要求
                if (null != skuRedisCommand.getSkuMgmt().getMinValidDate() && skuRedisCommand.getSkuMgmt().getMinValidDate() > this.getIntervalDays(whCartonCommand.getExpDate(), new Date())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_VALID_LT);
                }
                // 未过保质期，有效天数大于系统要求
                if (null != skuRedisCommand.getSkuMgmt().getMaxValidDate() && skuRedisCommand.getSkuMgmt().getMaxValidDate() < this.getIntervalDays(whCartonCommand.getExpDate(), new Date())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_VALID_GT);
                }
            }// end-if 商品过期
        }// end-if 商品效期
        if (!whFunctionRcvd.getIsInvattrDiscrepancyAllowrcvd()) {
            // 不允许差异收货
            // 当前属性是库存类型
            // 管理库存类型，不允许差异收货，功能维护了库存类型，单据库存类型必须和功能的一致
            if (skuRedisCommand.getSkuMgmt().getIsInvType() && !StringUtil.isEmpty(whFunctionRcvd.getInvType()) && !whFunctionRcvd.getInvType().equals(whCartonCommand.getInvType())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_INV_TYPE_DIFF);
            }
            // 当前属性是库存状态
            // 不允许差异收货，功能维护了库存状态，单据库存状态必须和功能的一致
            if (null != whFunctionRcvd.getInvStatus() && !whFunctionRcvd.getInvStatus().equals(whCartonCommand.getInvStatus())) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_INV_STATUS_DIFF);
            }
        }
    }

    /**
     * 与属性扫描顺序相反 判断当前扫描的商品信息是否在装箱信息内
     *
     * @author mingwei.xie
     * @param whFunctionRcvd 收货功能信息
     * @param whCartonCommand 已确认的商品属性
     * @param whCartonCommandList 商品的装箱信息
     * @param ouId 组织ID
     * @param logId 日志ID
     * @return 当前确认的商品信息在装箱信息中有匹配的行
     */
    private List<WhCartonCommand> getMatchWhCartonInfoList(WhFunctionRcvd whFunctionRcvd, SkuRedisCommand skuRedisCommand, WhCartonCommand whCartonCommand, List<WhCartonCommand> whCartonCommandList, Long ouId, String logId) {
        if (null == whFunctionRcvd || null == skuRedisCommand || null == whCartonCommand || null == whCartonCommandList) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhCartonCommand> matchWhCartonInfoList = new ArrayList<>();
        for (WhCartonCommand originCommand : whCartonCommandList) {
            // 是否管理库存状态
            if (null == whCartonCommand.getInvStatus()) {
                throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
            } else {
                boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_ISINVSTATUS);
                if (isAllSkuScanAttrMaintained && !whFunctionRcvd.getIsInvattrDiscrepancyAllowrcvd()) {
                    if (!originCommand.getInvStatus().equals(whCartonCommand.getInvStatus())) {
                        continue;
                    }
                }
            }
            // 是否管理库存类型
            if (skuRedisCommand.getSkuMgmt().getIsInvType()) {
                if (StringUtil.isEmpty(whCartonCommand.getInvType())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_ISINVTYPE);
                    if (isAllSkuScanAttrMaintained && !whFunctionRcvd.getIsInvattrDiscrepancyAllowrcvd()) {
                        if (!originCommand.getInvType().equals(whCartonCommand.getInvType())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理库存属性5
            if (skuRedisCommand.getSkuExtattr().getInvAttr5()) {
                if (StringUtil.isEmpty(whCartonCommand.getInvAttr5())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_INVATTR5);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getInvAttr5().equals(whCartonCommand.getInvAttr5())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理库存属性4
            if (skuRedisCommand.getSkuExtattr().getInvAttr4()) {
                if (StringUtil.isEmpty(whCartonCommand.getInvAttr4())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_INVATTR4);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getInvAttr4().equals(whCartonCommand.getInvAttr4())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理库存属性3
            if (skuRedisCommand.getSkuExtattr().getInvAttr3()) {
                if (StringUtil.isEmpty(whCartonCommand.getInvAttr3())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_INVATTR3);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getInvAttr3().equals(whCartonCommand.getInvAttr3())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理库存属性2
            if (skuRedisCommand.getSkuExtattr().getInvAttr2()) {
                if (StringUtil.isEmpty(whCartonCommand.getInvAttr2())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_INVATTR2);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getInvAttr2().equals(whCartonCommand.getInvAttr2())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理库存属性1
            if (skuRedisCommand.getSkuExtattr().getInvAttr1()) {
                if (StringUtil.isEmpty(whCartonCommand.getInvAttr1())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_INVATTR1);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getInvAttr1().equals(whCartonCommand.getInvAttr1())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理原产地
            if (skuRedisCommand.getSkuMgmt().getIsCountryOfOrigin()) {
                if (StringUtil.isEmpty(whCartonCommand.getCountryOfOrigin())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_ISCOUNTRYOFORIGIN);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getCountryOfOrigin().equals(whCartonCommand.getCountryOfOrigin())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理批次号
            if (skuRedisCommand.getSkuMgmt().getIsBatchNo()) {
                if (StringUtil.isEmpty(whCartonCommand.getBatchNo())) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_ISBATCHNO);
                    if (isAllSkuScanAttrMaintained) {
                        if (!originCommand.getBatchNo().equals(whCartonCommand.getBatchNo())) {
                            continue;
                        }
                    }
                }
            }
            // 是否管理效期
            if (skuRedisCommand.getSkuMgmt().getIsValid()) {
                if (null == whCartonCommand.getMfgDate() || null == whCartonCommand.getExpDate()) {
                    throw new BusinessException(ErrorCodes.CASELEVEL_SKU_ATTR_INPUT_NULL);
                } else {
                    boolean isAllSkuScanAttrMaintained = this.isAllSkuScanAttrMaintained(skuRedisCommand, whCartonCommandList, Constants.CASELEVEL_SCAN_SIGN_ISVALID);
                    if (isAllSkuScanAttrMaintained) {
                        if (this.compareDate(originCommand.getMfgDate(), whCartonCommand.getMfgDate()) != 0 || this.compareDate(originCommand.getExpDate(), whCartonCommand.getExpDate()) != 0) {
                            continue;
                        }
                    }
                }
            }

            // 已扫描的属性都相等，加入匹配的列表
            matchWhCartonInfoList.add(originCommand);
        }
        return matchWhCartonInfoList;
    }

    /**
     * 检查所有商品扫描的属性是否已维护
     *
     * @author mingwei.xie
     * @param skuRedisCommand 商品主档信息
     * @param whCartonCommandList 需检查的商品
     * @param scanAttrSign 扫描的属性
     * @return 不需维护或者所有的商品都已维护返回true, 否则返回false
     */
    private boolean isAllSkuScanAttrMaintained(SkuRedisCommand skuRedisCommand, List<WhCartonCommand> whCartonCommandList, String scanAttrSign) {
        if (null == skuRedisCommand || null == whCartonCommandList || StringUtil.isEmpty(scanAttrSign)) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        boolean checkResult = true;
        for (WhCartonCommand command : whCartonCommandList) {
            WhCartonCommand whCartonCommand = new WhCartonCommand();
            BeanUtils.copyProperties(command, whCartonCommand);
            checkResult = this.isSkuScanAttrMaintained(skuRedisCommand, whCartonCommand, scanAttrSign);
            if (!checkResult) {
                // 有一个没维护则失败
                break;
            }
        }
        return checkResult;
    }

    /**
     * 检查商品扫描的属性是否已维护
     *
     * @author mingwei.xie
     * @param skuRedisCommand 商品主档信息
     * @param whCartonCommand 需检查的商品信息
     * @param scanAttrSign 扫描的属性
     * @return 不需维护或者已维护返回true, 否则返回false
     */
    private boolean isSkuScanAttrMaintained(SkuRedisCommand skuRedisCommand, WhCartonCommand whCartonCommand, String scanAttrSign) {
        boolean checkResult = true;
        switch (scanAttrSign) {
            case Constants.CASELEVEL_SCAN_SIGN_ISVALID:
                // 是否管理效期
                if (skuRedisCommand.getSkuMgmt().getIsValid() && (null == whCartonCommand.getMfgDate() || null == whCartonCommand.getExpDate())) {
                    checkResult = false;
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_ISBATCHNO:
                // 是否管理批次号
                if (skuRedisCommand.getSkuMgmt().getIsBatchNo() && StringUtil.isEmpty(whCartonCommand.getBatchNo())) {
                    checkResult = false;
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_ISCOUNTRYOFORIGIN:
                // 是否管理原产地
                if (skuRedisCommand.getSkuMgmt().getIsCountryOfOrigin() && StringUtil.isEmpty(whCartonCommand.getCountryOfOrigin())) {
                    checkResult = false;
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_INVATTR1:
                // 是否管理库存属性1
                if (skuRedisCommand.getSkuExtattr().getInvAttr1()) {
                    if (StringUtil.isEmpty(whCartonCommand.getInvAttr1())) {
                        checkResult = false;
                    } else {
                        SysDictionary targetSysDictionaryInvAttr1 = this.getSysDictionary(Constants.INVENTORY_ATTR_1, whCartonCommand.getInvAttr1());
                        if (null == targetSysDictionaryInvAttr1) {
                            checkResult = false;
                        }
                    }
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_INVATTR2:
                // 是否管理库存属性2
                if (skuRedisCommand.getSkuExtattr().getInvAttr2()) {
                    if (StringUtil.isEmpty(whCartonCommand.getInvAttr2())) {
                        checkResult = false;
                    } else {
                        SysDictionary targetSysDictionaryInvAttr2 = this.getSysDictionary(Constants.INVENTORY_ATTR_2, whCartonCommand.getInvAttr2());
                        if (null == targetSysDictionaryInvAttr2) {
                            checkResult = false;
                        }
                    }
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_INVATTR3:
                // 是否管理库存属性3
                if (skuRedisCommand.getSkuExtattr().getInvAttr3()) {
                    if (StringUtil.isEmpty(whCartonCommand.getInvAttr3())) {
                        checkResult = false;
                    } else {
                        SysDictionary targetSysDictionaryInvAttr3 = this.getSysDictionary(Constants.INVENTORY_ATTR_3, whCartonCommand.getInvAttr3());
                        if (null == targetSysDictionaryInvAttr3) {
                            checkResult = false;
                        }
                    }
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_INVATTR4:
                // 是否管理库存属性4
                if (skuRedisCommand.getSkuExtattr().getInvAttr4()) {
                    if (StringUtil.isEmpty(whCartonCommand.getInvAttr4())) {
                        checkResult = false;
                    } else {
                        SysDictionary targetSysDictionaryInvAttr4 = this.getSysDictionary(Constants.INVENTORY_ATTR_4, whCartonCommand.getInvAttr4());
                        if (null == targetSysDictionaryInvAttr4) {
                            checkResult = false;
                        }
                    }
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_INVATTR5:
                // 是否管理库存属性5
                if (skuRedisCommand.getSkuExtattr().getInvAttr5()) {
                    if (StringUtil.isEmpty(whCartonCommand.getInvAttr5())) {
                        checkResult = false;
                    } else {
                        SysDictionary targetSysDictionaryInvAttr5 = this.getSysDictionary(Constants.INVENTORY_ATTR_5, whCartonCommand.getInvAttr5());
                        if (null == targetSysDictionaryInvAttr5) {
                            checkResult = false;
                        }
                    }
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_ISINVTYPE:
                // 是否管理库存类型
                if (skuRedisCommand.getSkuMgmt().getIsInvType()) {
                    if (StringUtil.isEmpty(whCartonCommand.getInvType())) {
                        checkResult = false;
                    } else {
                        SysDictionary targetSysDictionaryInvType = this.getSysDictionary(Constants.INVENTORY_TYPE, whCartonCommand.getInvType());
                        if (null == targetSysDictionaryInvType) {
                            checkResult = false;
                        }
                    }
                }
                break;
            case Constants.CASELEVEL_SCAN_SIGN_ISINVSTATUS:
                // 是否管理库存状态
                if (null == whCartonCommand.getInvStatus()) {
                    checkResult = false;
                } else {
                    InventoryStatus inventoryStatus = this.getInventoryStatusById(whCartonCommand.getInvStatus());
                    if (null == inventoryStatus) {
                        checkResult = false;
                    }
                }
                break;
            default:
                throw new BusinessException(ErrorCodes.CASELEVEL_SCAN_SQE_ERROR);
        }

        return checkResult;
    }

}
