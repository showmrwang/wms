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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CancelPattern;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.WhContainerCategoryType;
import com.baozun.scm.primservice.whoperation.constant.WhContainerType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaPutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.statis.InventoryStatisticManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationRecommendManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

/**
 * @author lichuan
 *
 */
@Service("pdaPutawayManager")
@Transactional
public class PdaPutawayManagerImpl extends BaseManagerImpl implements PdaPutawayManager {
    protected static final Logger log = LoggerFactory.getLogger(PdaPutawayManagerImpl.class);

    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private WhLocationRecommendManager whLocationRecommendManager;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private SysDictionaryDao sysDictionaryDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private WhCartonDao whCartonDao;
    @SuppressWarnings("unused")
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private ContainerAssistDao containerAssistDao;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private WhFunctionPutAwayManager whFunctionPutAwayManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    PdaPutawayCacheManager pdaPutawayCacheManager;
    @Autowired
    WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    InventoryStatisticManager inventoryStatisticManager;


    /**
     * @author lichuan
     * @param containerCode
     * @param funcId
     * @param putawayPatternType
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public ScanResultCommand sysGuideScanContainer(String containerCode, String insideContainerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanContainer start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        ContainerCommand insideContainerCmd;
        if (!StringUtils.isEmpty(insideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == insideContainerCmd) {
                // 内部容器信息不存在
                log.error("inside container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            }
        } else {
            insideContainerCmd = null;
        }
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuidePalletPutawayScanContainer(containerCmd, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideContainerPutawayScanContainer(containerCmd, insideContainerCmd, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideSplitContainerPutawayScanContainer(containerCmd, insideContainerCmd, funcId, ouId, userId, logId);
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanContainer end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        return srCmd;
    }

    /**
     * 系统指导整托上架
     * 
     * @author lichuan
     * @param containerCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuidePalletPutawayScanContainer(ContainerCommand containerCmd, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
        Integer containerStatus = containerCmd.getStatus();
        // 0.判断是外部容器还是内部容器
        int count1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
        int count2 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, containerId);
        int count3 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerId);
        int count4 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, containerId);
        if (0 < count2 || 0 < count4) {
            // 整托上架只能扫外部容器号
            log.error("sys guide pallet putaway scan container is insideContainer error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_IS_INSIDE_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        if (0 < count1 || 0 < count3) {
            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
            srCmd.setHasOuterContainer(true);// 有外部容器
        } else {
            // 无收货库存
            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        // 1.修改容器状态为：上架中，且占用中
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerStatus) {
            Container container = new Container();
            BeanUtils.copyProperties(containerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
            containerDao.saveOrUpdateByVersion(container);
            srCmd.setOuterContainerCode(containerCode);// 外部容器号
            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
        }
        // 2.判断是否已经缓存所有库存信息
        // pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(containerCmd, logId);
        List<WhSkuInventoryCommand> cacheInvs = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        List<WhSkuInventoryCommand> invList = null;
        if (null == cacheInvs || 0 == cacheInvs.size()) {
            // 缓存所有库存
            invList = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventory(containerCmd, ouId, logId);
        } else {
            invList = cacheInvs;
        }
        // 3.库存信息统计
        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        /*Long outerContainerId = containerCmd.getId();
        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器
        Set<Long> caselevelContainerIds = new HashSet<Long>();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = new HashSet<Long>();// 所有非caselevel内部容器
        Set<Long> skuIds = new HashSet<Long>();// 所有sku种类
        Long skuQty = 0L;// sku总件数
        Set<String> skuAttrIds = new HashSet<String>();// 所有唯一sku
        Set<Long> storeIds = new HashSet<Long>();// 所有店铺
        Set<Long> locationIds = new HashSet<Long>();// 所有推荐库位
        Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();// 内部容器所有sku种类
        Map<Long, Long> insideContainerSkuQty = new HashMap<Long, Long>();// 内部容器所有sku总件数
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();// 内部容器单个sku总件数
        Map<Long, Set<String>> insideContainerSkuAttrIds = new HashMap<Long, Set<String>>();// 内部容器唯一sku种类
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();// 内部容器唯一sku总件数
        Map<Long, Set<Long>> insideContainerStoreIds = new HashMap<Long, Set<Long>>();// 内部容器所有店铺
        Double outerContainerWeight = 0.0;
        Double outerContainerVolume = 0.0;
        Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();// 内部容器重量
        Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds;
        List<UomCommand> weightUomCmds;
        SimpleCubeCalculator cubeCalculator = new SimpleCubeCalculator(lenUomConversionRate);
        SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
        if (null == isrCmd) {
            lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : lenUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    lenUomConversionRate.put(uomCode, uomRate);
                }
            }
            weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : weightUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    weightUomConversionRate.put(uomCode, uomRate);
                }
            }
            try {
                for (WhSkuInventoryCommand invCmd : invList) {
                    String asnCode = invCmd.getOccupationCode();
                    if (StringUtils.isEmpty(asnCode)) {
                        log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                    }
                    WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                    if (null == asn) {
                        log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                    }
                    if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus() && PoAsnStatus.ASN_RCVD != asn.getStatus()) {
                        log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                    }
                    Long poId = asn.getPoId();
                    WhPo po = whPoDao.findWhPoById(poId, ouId);
                    if (null == po) {
                        log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.PO_NULL);
                    }
                    String poCode = po.getPoCode();
                    if (PoAsnStatus.PO_RCVD != po.getStatus() && PoAsnStatus.PO_RCVD_FINISH != po.getStatus()) {
                        log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                    }
                    Long icId = invCmd.getInsideContainerId();
                    Container ic;
                    if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
                        log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    } else {
                        insideContainerIds.add(icId);
                        srCmd.setHasInsideContainer(true);
                    }
                    // 验证容器状态是否可用
                    if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                        log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    // 获取容器状态
                    Integer icStatus = ic.getStatus();
                    if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                        log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, icStatus, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
                    }
                    Long insideContainerCate = ic.getTwoLevelType();
                    Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
                    if (null == insideContainer2) {
                        log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", icId, insideContainerCate, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                    }
                    if (1 != insideContainer2.getLifecycle()) {
                        log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", icId, insideContainer2.getId(), logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    Double icLength = insideContainer2.getLength();
                    Double icWidth = insideContainer2.getWidth();
                    Double icHeight = insideContainer2.getHigh();
                    Double icWeight = insideContainer2.getWeight();
                    if (null == icLength || null == icWidth || null == icHeight) {
                        log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    if (null == icWeight) {
                        log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    Double icVolume = cubeCalculator.calculateStuffVolume(icLength, icWidth, icHeight);
                    insideContainerVolume.put(icId, icVolume);
                    WhCarton carton = whCartonDao.findWhCaselevelCartonByContainerId(icId,ouId);
                    if (null != carton) {
                        caselevelContainerIds.add(icId);
                    } else {
                        notcaselevelContainerIds.add(icId);
                    }
                    String invType = invCmd.getInvType();
                    // if (StringUtils.isEmpty(invType)) {
                    // pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId,
                    // logId);
                    // log.error("inv type is null error, logId is:[{}]", logId);
                    // throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                    // }
                    if (!StringUtils.isEmpty(invType)) {
                        List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_TYPE, invType, BaseModel.LIFECYCLE_NORMAL);
                        if (null == invTypeList || 0 == invTypeList.size()) {
                            log.error("inv type is not defined error, invType is:[{}], logId is:[{}]", invType, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                        }
                    }
                    Long invStatus = invCmd.getInvStatus();
                    if (null == invStatus) {
                        log.error("inv status is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    InventoryStatus status = new InventoryStatus();
                    status.setId(invStatus);
                    List<InventoryStatus> invStatusList = inventoryStatusManager.findInventoryStatusList(status);
                    if (null == invStatusList || 0 == invStatusList.size()) {
                        log.error("inv status is not defined error, invStatusId is:[{}], logId is:[{}]", invStatus, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    String invAttr1 = invCmd.getInvAttr1();
                    if (!StringUtils.isEmpty(invAttr1)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_1, invAttr1, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr1 is not defined error, invAttr1 is:[{}], logId is:[{}]", invAttr1, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr1});
                        }
                    }
                    String invAttr2 = invCmd.getInvAttr2();
                    if (!StringUtils.isEmpty(invAttr2)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_2, invAttr2, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr2 is not defined error, invAttr2 is:[{}], logId is:[{}]", invAttr2, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr2});
                        }
                    }
                    String invAttr3 = invCmd.getInvAttr3();
                    if (!StringUtils.isEmpty(invAttr3)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_3, invAttr3, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr3 is not defined error, invAttr3 is:[{}], logId is:[{}]", invAttr3, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR3_NOT_FOUND_ERROR, new Object[] {invAttr3});
                        }
                    }
                    String invAttr4 = invCmd.getInvAttr4();
                    if (!StringUtils.isEmpty(invAttr4)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_4, invAttr4, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr4 is not defined error, invAttr4 is:[{}], logId is:[{}]", invAttr4, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR4_NOT_FOUND_ERROR, new Object[] {invAttr4});
                        }
                    }
                    String invAttr5 = invCmd.getInvAttr5();
                    if (!StringUtils.isEmpty(invAttr5)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_5, invAttr5, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr5 is not defined error, invAttr5 is:[{}], logId is:[{}]", invAttr5, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR5_NOT_FOUND_ERROR, new Object[] {invAttr5});
                        }
                    }
                    Long skuId = invCmd.getSkuId();
                    Double toBefillQty = invCmd.getToBeFilledQty();
                    Double onHandQty = invCmd.getOnHandQty();
                    Double curerntSkuQty = 0.0;
                    Long locationId = invCmd.getLocationId();
                    if (null != locationId) {
                        locationIds.add(locationId);
                        if (null != toBefillQty) {
                            curerntSkuQty = toBefillQty;
                            skuQty += toBefillQty.longValue();
                        }
                    } else {
                        if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
                            log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
                        }
                        if (null != onHandQty) {
                            curerntSkuQty = onHandQty;
                            skuQty += onHandQty.longValue();
                        }
                    }
                    if (null != skuId) {
                        skuIds.add(skuId);
                        SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
                        if(null == cacheSku){
                            log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                        }
                        Sku sku = cacheSku.getSku();
//                        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//                        if (null == skuCmd) {
//                            log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
//                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//                        }
                        // String skuLenUom = skuCmd.getLengthUom();
                        Double skuLength = sku.getLength();
                        Double skuWidth = sku.getWidth();
                        Double skuHeight = sku.getHeight();
                        // String skuWeightUom = skuCmd.getWeightUom();
                        Double skuWeight = sku.getWeight();
                        if (null == skuLength || null == skuWidth || null == skuHeight) {
                            log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {sku.getBarCode()});
                        }
                        if (null == skuWeight) {
                            log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {sku.getBarCode()});
                        }
                        // totalSkuVolume = cubeCalculator.accumulationStuffVolume(skuLength,
                        // skuWidth,
                        // skuHeight);
                        // totalSkuWeight = weightCalculator.accumulationStuffWeight(skuWeight);
                        if (null != insideContainerWeight.get(icId)) {
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        } else {
                            // 先计算容器自重
                            insideContainerWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
                            // 再计算当前商品重量
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        }
                    }
                    skuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                    Long stroeId = invCmd.getStoreId();
                    if (null != stroeId) {
                        storeIds.add(stroeId);
                    }
                    if (null != insideContainerSkuIds.get(icId)) {
                        Set<Long> icSkus = insideContainerSkuIds.get(icId);
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    } else {
                        Set<Long> icSkus = new HashSet<Long>();
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuQty.get(icId)) {
                        insideContainerSkuQty.put(icId, insideContainerSkuQty.get(icId) + curerntSkuQty.longValue());
                    } else {
                        insideContainerSkuQty.put(icId, curerntSkuQty.longValue());
                    }
                    if (null != insideContainerSkuIdsQty.get(icId)) {
                        Map<Long, Long> skuIdsQty = insideContainerSkuIdsQty.get(icId);
                        if (null != skuIdsQty.get(skuId)) {
                            skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
                        } else {
                            skuIdsQty.put(skuId, curerntSkuQty.longValue());
                        }
                    } else {
                        Map<Long, Long> sq = new HashMap<Long, Long>();
                        sq.put(skuId, curerntSkuQty.longValue());
                        insideContainerSkuIdsQty.put(icId, sq);
                    }
                    if (null != insideContainerSkuAttrIds.get(icId)) {
                        Set<String> icSkus = insideContainerSkuAttrIds.get(icId);
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);
                    } else {
                        Set<String> icSkus = new HashSet<String>();
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuAttrIdsQty.get(icId)) {
                        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);
                        if (null != skuAttrIdsQty.get(skuId)) {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), skuAttrIdsQty.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd)) + curerntSkuQty.longValue());
                        } else {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        }
                    } else {
                        Map<String, Long> saq = new HashMap<String, Long>();
                        saq.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        insideContainerSkuAttrIdsQty.put(icId, saq);
                    }
                    if (null != insideContainerStoreIds.get(icId)) {
                        Set<Long> icStores = insideContainerStoreIds.get(icId);
                        icStores.add(stroeId);
                        insideContainerStoreIds.put(icId, icStores);
                    } else {
                        Set<Long> icStores = new HashSet<Long>();
                        icStores.add(stroeId);
                        insideContainerStoreIds.put(icId, icStores);
                    }

                    if (null == insideContainerAsists.get(icId)) {
                        ContainerAssist containerAssist = new ContainerAssist();
                        containerAssist.setContainerId(icId);
                        containerAssist.setSysLength(icLength);
                        containerAssist.setSysWidth(icWidth);
                        containerAssist.setSysHeight(icHeight);
                        containerAssist.setSysVolume(icVolume);
                        containerAssist.setCartonQty(1L);
                        containerAssist.setCreateTime(new Date());
                        containerAssist.setLastModifyTime(new Date());
                        containerAssist.setOperatorId(userId);
                        containerAssist.setOuId(ouId);
                        containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
                        insideContainerAsists.put(icId, containerAssist);
                    }
                }
            } catch (Exception e) {
                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                throw e;
            }
        }*/
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds;
        List<UomCommand> weightUomCmds;
        lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }
        SimpleCubeCalculator cubeCalculator = new SimpleCubeCalculator(lenUomConversionRate);
        SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
        InventoryStatisticResultCommand invStatisticCmd = isrCmd;
        if(null == isrCmd){
            invStatisticCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.PALLET_PUTAWAY, lenUomCmds, weightUomCmds, containerCmd, ouId, userId, logId);
            if(null == invStatisticCmd){
                log.error("sys guide putaway inv statistic is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
        }
        Long outerContainerId = containerCmd.getId();
        Set<Long> insideContainerIds = invStatisticCmd.getInsideContainerIds();// 所有内部容器
        Set<Long> caselevelContainerIds = invStatisticCmd.getCaselevelContainerIds();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = invStatisticCmd.getNotcaselevelContainerIds();// 所有非caselevel内部容器
        Set<Long> skuIds = invStatisticCmd.getSkuIds();// 所有sku种类
        Long skuQty = invStatisticCmd.getSkuQty();// sku总件数
        Set<String> skuAttrIds = invStatisticCmd.getSkuAttrIds();// 所有唯一sku
        Set<Long> storeIds = invStatisticCmd.getStoreIds();// 所有店铺
        Set<Long> locationIds = invStatisticCmd.getLocationIds();// 所有推荐库位
        Map<Long, Set<Long>> insideContainerSkuIds = invStatisticCmd.getInsideContainerSkuIds();// 内部容器所有sku种类
        Map<Long, Long> insideContainerSkuQty = invStatisticCmd.getInsideContainerSkuQty();// 内部容器所有sku总件数
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = invStatisticCmd.getInsideContainerSkuIdsQty();// 内部容器单个sku总件数
        Map<Long, Set<String>> insideContainerSkuAttrIds = invStatisticCmd.getInsideContainerSkuAttrIds();// 内部容器唯一sku种类
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = invStatisticCmd.getInsideContainerSkuAttrIdsQty();// 内部容器唯一sku总件数
        //Map<Long, Map<Long, Set<String>>> insideContainerSkuAndSkuAttrIds = invStatisticCmd.getInsideContainerSkuAndSkuAttrIds();// 内部容器sku对应所有唯一sku
        Map<Long, Set<Long>> insideContainerStoreIds = invStatisticCmd.getInsideContainerStoreIds();// 内部容器所有店铺
        Map<Long, Double> insideContainerWeight = invStatisticCmd.getInsideContainerWeight();// 内部容器重量
        //Map<Long, Double> insideContainerVolume = invStatisticCmd.getInsideContainerVolume();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = invStatisticCmd.getInsideContainerAsists();
        Double outerContainerWeight = 0.0;
        Double outerContainerVolume = 0.0;
        // 4.判断是否已推荐库位
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
        if (null != isrCmd) {
            locationIds = isrCmd.getLocationIds();
            if (null == locationIds || 0 == locationIds.size()) {
                pdaPutawayCacheManager.sysGuidePutawayRemoveInventoryStatistic(containerCmd, ouId, logId);
                log.error("sys guide pallet putaway cache is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }
        if (0 < locationIds.size()) {
            srCmd.setRecommendLocation(true);// 已推荐库位
            if (1 < locationIds.size()) {
                log.error("sys guide pallet putaway location is more than one error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
            Long locId = null;
            for (Long locationId : locationIds) {
                if (null == locId) {
                    locId = locationId;
                    if (null != locId) break;
                }
            }
            Location loc = locationDao.findByIdExt(locId, ouId);
            if (null == loc) {
                log.error("location is null error, locId is:[{}], logId is:[{}]", locId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
            srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
            if(null != warehouse){
                if(true == warehouse.getIsInboundLocationBarcode()){
                    srCmd.setValidateLocation(true);
                }else{
                    srCmd.setValidateLocation(false);
                }
            }
            srCmd.setNeedTipLocation(true);// 提示库位
            return srCmd;
        }
        // 5.判断并修改内部容器状态
        List<String> icCodeList = new ArrayList<String>();
        if (0 == insideContainerIds.size()) {
            log.error("sys guide pallet putaway inside container is null error, invs is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RCVD_INV_NOT_HAS_INSIDE_CONTAINER_ERROR);
        } else {
            srCmd.setHasInsideContainer(true);
        }
        for (Long icId : insideContainerIds) {
            Container insideContainer = containerDao.findByIdExt(icId, ouId);
            if (null == insideContainer) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(insideContainer.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != insideContainer.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer insideContainerStatus = insideContainer.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != insideContainerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != insideContainerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", insideContainerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {insideContainer.getCode()});
            }

            // 修改内部容器状态为：上架中，且占用中
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == insideContainerStatus) {
                insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                containerDao.saveOrUpdateByVersion(insideContainer);
                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
            }
            icCodeList.add(insideContainer.getCode());
        }
        // 6.计算外部容器体积重量
        Long outerContainerCate = containerCmd.getTwoLevelType();
        Container2ndCategory outerContainer2 = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
        if (null == outerContainer2) {
            log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, outerContainerCate, logId);
            throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
        }
        if (1 != outerContainer2.getLifecycle()) {
            log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, outerContainer2.getId(), logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Double ocLength = outerContainer2.getLength();
        Double ocWidth = outerContainer2.getWidth();
        Double ocHeight = outerContainer2.getHigh();
        Double ocWeight = outerContainer2.getWeight();
        if (null == ocLength || null == ocWidth || null == ocHeight) {
            log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {containerCode});
        }
        if (null == ocWeight) {
            log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {containerCode});
        }
        outerContainerWeight = weightCalculator.calculateStuffWeight(ocWeight);
        outerContainerVolume = cubeCalculator.calculateStuffVolume(ocLength, ocWidth, ocHeight);
        // 7.删除并更新容器辅助表
        List<Long> deleteContainerIds = new ArrayList<Long>();
        CollectionUtils.addAll(deleteContainerIds, insideContainerIds.iterator());
        deleteContainerIds.add(outerContainerId);
        containerAssistDao.deleteByContainerIds(ouId, deleteContainerIds);
        // 内部容器辅助表信息
        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
        Double icTotalWeight = 0.0;
        for (Long insideId : insideContainerIds) {
            ContainerAssist containerAssist = insideContainerAsists.get(insideId);
            icTotalWeight += insideContainerWeight.get(insideId);
            containerAssist.setSysWeight(insideContainerWeight.get(insideId));
            containerAssist.setSkuCategory(insideContainerSkuIds.get(insideId).size() + 0L);
            containerAssist.setSkuAttrCategory(insideContainerSkuAttrIds.get(insideId).size() + 0L);
            containerAssist.setSkuQty(insideContainerSkuQty.get(insideId));
            containerAssist.setStoreQty(insideContainerStoreIds.get(insideId).size() + 0L);
            containerAssistDao.insert(containerAssist);
            insertGlobalLog(GLOBAL_LOG_INSERT, containerAssist, ouId, userId, null, null);
            caMap.put(insideId, containerAssist);// 所有的容器辅助信息
        }
        // 外部容器辅助表信息
        ContainerAssist containerAssist = new ContainerAssist();
        containerAssist.setContainerId(containerId);
        containerAssist.setSysLength(ocLength);
        containerAssist.setSysWidth(ocWidth);
        containerAssist.setSysHeight(ocHeight);
        containerAssist.setSysVolume(outerContainerVolume);
        containerAssist.setCartonQty(1L);
        containerAssist.setCreateTime(new Date());
        containerAssist.setLastModifyTime(new Date());
        containerAssist.setOperatorId(userId);
        containerAssist.setOuId(ouId);
        containerAssist.setCartonQty(insideContainerIds.size() + 0L);
        containerAssist.setSysWeight(outerContainerWeight + icTotalWeight);
        containerAssist.setSkuCategory(skuIds.size() + 0L);
        containerAssist.setSkuAttrCategory(skuAttrIds.size() + 0L);
        containerAssist.setSkuQty(skuQty);
        containerAssist.setStoreQty(storeIds.size() + 0L);
        containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        containerAssistDao.insert(containerAssist);
        insertGlobalLog(GLOBAL_LOG_INSERT, containerAssist, ouId, userId, null, null);
        caMap.put(containerId, containerAssist);// 所有的容器辅助信息
        // 8.匹配上架规则
        List<Long> storeList = new ArrayList<Long>();
        CollectionUtils.addAll(storeList, storeIds.iterator());
        List<Long> icIdList = new ArrayList<Long>();
        CollectionUtils.addAll(icIdList, insideContainerIds.iterator());
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(containerCode);
        ruleAffer.setContainerId(outerContainerId);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setAfferInsideContainerIdList(icIdList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        ruleAffer.setStoreIdList(storeList);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则
        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        // 9.推荐库位
        if (null == caMap || 0 == caMap.size()) {
            log.error("container assist info generate error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        // 判断是否需要排队
        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerId, logId);
        if (false == isRecommend) {
            if (log.isInfoEnabled()) {
                log.info("need queue up, current containerId is:[{}], logId is:[{}]", containerId, logId);
            }
            srCmd.setNeedQueueUp(true);
            return srCmd;
        }
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, lenUomConversionRate);
        uomMap.put(WhUomType.WEIGHT_UOM, weightUomConversionRate);
        List<LocationRecommendResultCommand> lrrList = null;
        try {
            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.PALLET_PUTAWAY, caMap, invList, uomMap, logId);
        } catch (Exception e1) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
            throw e1;
        }
        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
            srCmd.setRecommendFail(true);
            // 将内部容器状态修改为待上架
            for (Long icId : insideContainerIds) {
                Container insideContainer = containerDao.findByIdExt(icId, ouId);
                if (null == insideContainer) {
                    // 容器信息不存在
                    log.error("container is not exists, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                }
                // 获取容器状态
                Integer insideContainerStatus = insideContainer.getStatus();
                // 修改内部容器状态为：上架中，且占用中
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == insideContainerStatus) {
                    insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(insideContainer);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                }
            }
            if (null != containerCmd) {
                // 包含外部容器，需要将外部容器状态也修改为待上架
                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(containerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
            }
            // log.error("location recommend fail! containerCode is:[{}], logId is:[{}]",
            // containerCode, logId);
            // throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
            return srCmd;
        }
        LocationRecommendResultCommand lrr = lrrList.get(0);
        Long lrrLocId = lrr.getLocationId();
        String lrrLocCode = lrr.getLocationCode();
        locationIds = new HashSet<Long>();
        locationIds.add(lrrLocId);
        // 10.缓存容器库存统计信息
        InventoryStatisticResultCommand isCmd = new InventoryStatisticResultCommand();
        isCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        isCmd.setHasOuterContainer(true);
        isCmd.setOuterContainerId(outerContainerId);
        isCmd.setInsideContainerIds(insideContainerIds);
        isCmd.setCaselevelContainerIds(caselevelContainerIds);
        isCmd.setNotcaselevelContainerIds(notcaselevelContainerIds);
        isCmd.setSkuIds(skuIds);
        isCmd.setSkuQty(skuQty);
        isCmd.setSkuAttrIds(skuAttrIds);
        isCmd.setStoreIds(storeIds);
        isCmd.setLocationIds(locationIds);
        isCmd.setInsideContainerSkuIds(insideContainerSkuIds);
        isCmd.setInsideContainerSkuQty(insideContainerSkuQty);
        isCmd.setInsideContainerSkuIdsQty(insideContainerSkuIdsQty);
        isCmd.setInsideContainerSkuAttrIds(insideContainerSkuAttrIds);
        isCmd.setInsideContainerSkuAttrIdsQty(insideContainerSkuAttrIdsQty);
        isCmd.setInsideContainerStoreIds(insideContainerStoreIds);
        // cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC,
        // containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isCmd, ouId, logId);
        // 11.绑定库位(一入一出)
        // 先待移入库位库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
//                inv.setOnHandQty(0.0);
//                inv.setLocationId(lrrLocId);
//                try {
//                    inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                inv.setLastModifyTime(new Date());
//                whSkuInventoryDao.insert(inv);
//                insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                // 记录待移入库位库存日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), 0.0, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
//                inv.setOnHandQty(0.0);
//                inv.setLocationId(lrrLocId);
//                try {
//                    inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                inv.setLastModifyTime(new Date());
//                whSkuInventoryDao.insert(inv);
//                insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                // 记录待移入库位库存日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), 0.0, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    sn.setId(null);
//                    try {
//                        sn.setUuid(SkuInventoryUuid.invUuid(inv));
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
//                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                    }
//                    whSkuInventorySnDao.insert(sn);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                }
//            }
//        }
//        // 再出容器库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                // 记录容器移出日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                // 记录容器移出日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    whSkuInventorySnDao.delete(sn.getId());
//                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                }
//            }
//
//        }
        try {
            whSkuInventoryManager.execBinding(invList, warehouse, lrrList, WhPutawayPatternDetailType.PALLET_PUTAWAY, ouId, userId, logId); 
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
        } catch (Exception e) {
            // 绑定库位或弹出队列出错，清理库存统计信息缓存
            pdaPutawayCacheManager.sysGuidePutawayRemoveInventoryStatistic(containerCmd, ouId, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_BINDING_ERROR);
        }
        // 12.提示库位
        srCmd.setRecommendLocation(true);// 已推荐库位
        srCmd.setTipLocationCode(lrrLocCode);// 提示库位编码
        srCmd.setTipLocBarCode(lrr.getLocBarcode());// 库位条码
        if(null != warehouse){
            if(true == warehouse.getIsInboundLocationBarcode()){
                srCmd.setValidateLocation(true);
            }else{
                srCmd.setValidateLocation(false);
            }
        }
        srCmd.setNeedTipLocation(true);// 提示库位
        return srCmd;
    }

    /**
     * 系统指导整托箱
     * 
     * @author lichuan
     * @param containerCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideContainerPutawayScanContainer(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long funcId, Long ouId, Long userId, String logId) {
    	ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
        Integer containerStatus = containerCmd.getStatus();
        Long insideContainerId = null;
        String insideContainerCode = null;
        Integer insideContainerStatus = null;
        boolean hasOuterContainer = (null != containerCmd && null != insideContainerCmd);
        // 0.判断是外部容器还是内部容器
        if (null == insideContainerCmd) {
            int count1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
            int count2 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, containerId);
            int count3 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerId);
            int count4 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, containerId);
            if (0 < count2 || 0 < count4) {
                // 整箱上架判断是是否有外部容器号
                int ocCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId1(ouId, containerId);
                int ocLocCount = whSkuInventoryDao.findLocTobefilledInventoryCountsByInsideContainerId1(ouId, containerId);
                if (0 < ocCount || 0 < ocLocCount) {
                    log.error("sys guide container putaway scan container has outer container, should scan outer container first, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST, new Object[] {containerCode});
                } else {
                    srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);// 内部容器,无外部容器，无需循环提示容器
                    insideContainerId = containerId;
                    insideContainerCode = containerCode;
                    insideContainerStatus = containerStatus;
                    insideContainerCmd = containerCmd;
                }
            }
            if (0 < count1 || 0 < count3) {
                srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
                srCmd.setHasOuterContainer(true);// 有外部容器，需要循环提示容器
                ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());
                ContainerStatisticResultCommand csrCmd;
                if (null == cacheCsr) {
                    // 缓存所有内部容器统计信息
                    csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                } else {
                    csrCmd = cacheCsr;
                }
                // 获取所有内部容器id
                Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
                Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
                // 提示一个容器
                Long tipContainerId = pdaPutawayCacheManager.sysGuideContainerPutawayTipContainer0(containerCmd, insideContainerIds, logId);
                srCmd.setNeedTipContainer(true);
                String tipContainerCode = null;
                if (null != insideContainerIdsCode) {
                    tipContainerCode = insideContainerIdsCode.get(tipContainerId);
                    if (StringUtils.isEmpty(tipContainerCode)) {
                        Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                        if (null == tipContainer) {
                            log.error("container is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                        }
                        tipContainerCode = tipContainer.getCode();
                    }
                }
                srCmd.setTipContainerCode(tipContainerCode);
                // 1.修改容器状态为：上架中，且占用中
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(containerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    srCmd.setOuterContainerCode(containerCode);// 外部容器号
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
                return srCmd;
            }
            if ((0 >= count2 && 0 >= count1) && (0 >= count3 && 0 >= count4)) {
                // 无收货库存
                log.error("sys guide container putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
            }
        } else {
            // 判断是否是当前提示的容器
            insideContainerId = insideContainerCmd.getId();
            insideContainerCode = insideContainerCmd.getCode();
            insideContainerStatus = insideContainerCmd.getStatus();
            srCmd.setInsideContainerCode(insideContainerCode);
        }
        // 1.修改内部容器状态为：上架中，且占用中
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == insideContainerStatus) {
            Container container = new Container();
            BeanUtils.copyProperties(insideContainerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
            containerDao.saveOrUpdateByVersion(container);
            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
        }
        // 2.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> cacheInvs = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, insideContainerId.toString());
        List<WhSkuInventoryCommand> invList = null;
        if (null == cacheInvs || 0 == cacheInvs.size()) {
            // // 缓存所有库存
            // List<String> ocCodelist = new ArrayList<String>();
            // ocCodelist.add(containerCode);
            // // 查询所有对应容器号的库存信息
            // invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
            // if (null == invList || 0 == invList.size()) {
            // log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!,
            // logId is:[{}]", containerCode, logId);
            // throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new
            // Object[] {containerCode});
            // }
            // cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(),
            // invList, CacheConstants.CACHE_ONE_MONTH);
            invList = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventory(insideContainerCmd, ouId, logId);
        } else {
            invList = cacheInvs;
        }
        // 3.库存信息统计
        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerId.toString());
        /*Long outerContainerId = containerCmd.getId();
        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器
        Set<Long> caselevelContainerIds = new HashSet<Long>();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = new HashSet<Long>();// 所有非caselevel内部容器
        Set<Long> skuIds = new HashSet<Long>();// 所有sku种类
        Long skuQty = 0L;// sku总件数
        Set<String> skuAttrIds = new HashSet<String>();// 所有唯一sku
        Set<Long> storeIds = new HashSet<Long>();// 所有店铺
        Set<Long> locationIds = new HashSet<Long>();// 所有推荐库位
        Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();// 内部容器所有sku种类
        Map<Long, Long> insideContainerSkuQty = new HashMap<Long, Long>();// 内部容器所有sku总件数
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();// 内部容器单个sku总件数
        Map<Long, Set<String>> insideContainerSkuAttrIds = new HashMap<Long, Set<String>>();// 内部容器唯一sku种类
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();// 内部容器唯一sku总件数
        Map<Long, Set<Long>> insideContainerStoreIds = new HashMap<Long, Set<Long>>();// 内部容器所有店铺
        Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();// 内部容器重量
        Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        if (null == isrCmd) {
            List<UomCommand> lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : lenUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    lenUomConversionRate.put(uomCode, uomRate);
                }
            }
            List<UomCommand> weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : weightUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    weightUomConversionRate.put(uomCode, uomRate);
                }
            }
            SimpleCubeCalculator cubeCalculator = new SimpleCubeCalculator(lenUomConversionRate);
            SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
            try {
                for (WhSkuInventoryCommand invCmd : invList) {
                    String asnCode = invCmd.getOccupationCode();
                    if (StringUtils.isEmpty(asnCode)) {
                        log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                    }
                    WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                    if (null == asn) {
                        log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                    }
                    if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus() && PoAsnStatus.ASN_RCVD != asn.getStatus()) {
                        log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                    }
                    Long poId = asn.getPoId();
                    WhPo po = whPoDao.findWhPoById(poId, ouId);
                    if (null == po) {
                        log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.PO_NULL);
                    }
                    String poCode = po.getPoCode();
                    if (PoAsnStatus.PO_RCVD != po.getStatus() && PoAsnStatus.PO_RCVD_FINISH != po.getStatus()) {
                        log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                    }
                    Long icId = invCmd.getInsideContainerId();
                    Container ic;
                    if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
                        log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    } else {
                        insideContainerIds.add(icId);
                        srCmd.setHasInsideContainer(true);
                    }
                    // 验证容器状态是否可用
                    if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                        log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    // 获取容器状态
                    Integer icStatus = ic.getStatus();
                    if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                        log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, icStatus, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
                    }
                    Long insideContainerCate = ic.getTwoLevelType();
                    Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
                    if (null == insideContainer2) {
                        log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", icId, insideContainerCate, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                    }
                    if (1 != insideContainer2.getLifecycle()) {
                        log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", icId, insideContainer2.getId(), logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    Double icLength = insideContainer2.getLength();
                    Double icWidth = insideContainer2.getWidth();
                    Double icHeight = insideContainer2.getHigh();
                    Double icWeight = insideContainer2.getWeight();
                    if (null == icLength || null == icWidth || null == icHeight) {
                        log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    if (null == icWeight) {
                        log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    Double icVolume = cubeCalculator.calculateStuffVolume(icLength, icWidth, icHeight);
                    insideContainerVolume.put(icId, icVolume);
                    WhCarton carton = whCartonDao.findWhCaselevelCartonById(icId, ouId);
                    if (null != carton) {
                        caselevelContainerIds.add(icId);
                    } else {
                        notcaselevelContainerIds.add(icId);
                    }
                    String invType = invCmd.getInvType();
                    // if (StringUtils.isEmpty(invType)) {
                    // pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(insideContainerCmd,
                    // ouId, logId);
                    // log.error("inv type is null error, logId is:[{}]", logId);
                    // throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                    // }
                    if (!StringUtils.isEmpty(invType)) {
                        List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_TYPE, invType, BaseModel.LIFECYCLE_NORMAL);
                        if (null == invTypeList || 0 == invTypeList.size()) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(insideContainerCmd, ouId, logId);
                            log.error("inv type is not defined error, invType is:[{}], logId is:[{}]", invType, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                        }
                    }
                    Long invStatus = invCmd.getInvStatus();
                    if (null == invStatus) {
                        log.error("inv status is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    InventoryStatus status = new InventoryStatus();
                    status.setId(invStatus);
                    List<InventoryStatus> invStatusList = inventoryStatusManager.findInventoryStatusList(status);
                    if (null == invStatusList || 0 == invStatusList.size()) {
                        log.error("inv status is not defined error, invStatusId is:[{}], logId is:[{}]", invStatus, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    String invAttr1 = invCmd.getInvAttr1();
                    if (!StringUtils.isEmpty(invAttr1)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_1, invAttr1, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr1 is not defined error, invAttr1 is:[{}], logId is:[{}]", invAttr1, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr1});
                        }
                    }
                    String invAttr2 = invCmd.getInvAttr2();
                    if (!StringUtils.isEmpty(invAttr2)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_2, invAttr2, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr2 is not defined error, invAttr2 is:[{}], logId is:[{}]", invAttr2, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr2});
                        }
                    }
                    String invAttr3 = invCmd.getInvAttr3();
                    if (!StringUtils.isEmpty(invAttr3)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_3, invAttr3, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr3 is not defined error, invAttr3 is:[{}], logId is:[{}]", invAttr3, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR3_NOT_FOUND_ERROR, new Object[] {invAttr3});
                        }
                    }
                    String invAttr4 = invCmd.getInvAttr4();
                    if (!StringUtils.isEmpty(invAttr4)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_4, invAttr4, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr4 is not defined error, invAttr4 is:[{}], logId is:[{}]", invAttr4, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR4_NOT_FOUND_ERROR, new Object[] {invAttr4});
                        }
                    }
                    String invAttr5 = invCmd.getInvAttr5();
                    if (!StringUtils.isEmpty(invAttr5)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_5, invAttr5, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr5 is not defined error, invAttr5 is:[{}], logId is:[{}]", invAttr5, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR5_NOT_FOUND_ERROR, new Object[] {invAttr5});
                        }
                    }
                    Long skuId = invCmd.getSkuId();
                    Double toBefillQty = invCmd.getToBeFilledQty();
                    Double onHandQty = invCmd.getOnHandQty();
                    Double curerntSkuQty = 0.0;
                    Long locationId = invCmd.getLocationId();
                    if (null != locationId) {
                        locationIds.add(locationId);
                        if (null != toBefillQty) {
                            curerntSkuQty = toBefillQty;
                            skuQty += toBefillQty.longValue();
                        }
                    } else {
                        if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
                            log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
                        }
                        if (null != onHandQty) {
                            curerntSkuQty = onHandQty;
                            skuQty += onHandQty.longValue();
                        }
                    }
                    if (null != skuId) {
                        skuIds.add(skuId);
                        SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
                        if(null == cacheSku){
                            log.error("sys guide container putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                        }
                        Sku sku = cacheSku.getSku();
//                        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//                        if (null == skuCmd) {
//                            log.error("sys guide container putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
//                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//                        }
                        // String skuLenUom = skuCmd.getLengthUom();
                        Double skuLength = sku.getLength();
                        Double skuWidth = sku.getWidth();
                        Double skuHeight = sku.getHeight();
                        // String skuWeightUom = skuCmd.getWeightUom();
                        Double skuWeight = sku.getWeight();
                        if (null == skuLength || null == skuWidth || null == skuHeight) {
                            log.error("sys guide container putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {sku.getBarCode()});
                        }
                        if (null == skuWeight) {
                            log.error("sys guide container putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {sku.getBarCode()});
                        }
                        // totalSkuVolume = cubeCalculator.accumulationStuffVolume(skuLength,
                        // skuWidth,
                        // skuHeight);
                        // totalSkuWeight = weightCalculator.accumulationStuffWeight(skuWeight);
                        if (null != insideContainerWeight.get(icId)) {
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        } else {
                            // 先计算容器自重
                            insideContainerWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
                            // 再计算当前商品重量
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        }
                    }
                    skuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                    Long stroeId = invCmd.getStoreId();
                    if (null != stroeId) {
                        storeIds.add(stroeId);
                    }
                    if (null != insideContainerSkuIds.get(icId)) {
                        Set<Long> icSkus = insideContainerSkuIds.get(icId);
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    } else {
                        Set<Long> icSkus = new HashSet<Long>();
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuQty.get(icId)) {
                        insideContainerSkuQty.put(icId, insideContainerSkuQty.get(icId) + curerntSkuQty.longValue());
                    } else {
                        insideContainerSkuQty.put(icId, curerntSkuQty.longValue());
                    }
                    if (null != insideContainerSkuIdsQty.get(icId)) {
                        Map<Long, Long> skuIdsQty = insideContainerSkuIdsQty.get(icId);
                        if (null != skuIdsQty.get(skuId)) {
                            skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
                        } else {
                            skuIdsQty.put(skuId, curerntSkuQty.longValue());
                        }
                    } else {
                        Map<Long, Long> sq = new HashMap<Long, Long>();
                        sq.put(skuId, curerntSkuQty.longValue());
                        insideContainerSkuIdsQty.put(icId, sq);
                    }
                    if (null != insideContainerSkuAttrIds.get(icId)) {
                        Set<String> icSkus = insideContainerSkuAttrIds.get(icId);
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);
                    } else {
                        Set<String> icSkus = new HashSet<String>();
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuAttrIdsQty.get(icId)) {
                        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);
                        if (null != skuAttrIdsQty.get(skuId)) {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), skuAttrIdsQty.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd)) + curerntSkuQty.longValue());
                        } else {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        }
                    } else {
                        Map<String, Long> saq = new HashMap<String, Long>();
                        saq.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        insideContainerSkuAttrIdsQty.put(icId, saq);
                    }
                    if (null != insideContainerStoreIds.get(icId)) {
                        Set<Long> icStores = insideContainerStoreIds.get(icId);
                        icStores.add(stroeId);
                        insideContainerStoreIds.put(icId, icStores);
                    } else {
                        Set<Long> icStores = new HashSet<Long>();
                        icStores.add(stroeId);
                        insideContainerStoreIds.put(icId, icStores);
                    }

                    if (null == insideContainerAsists.get(icId)) {
                        ContainerAssist containerAssist = new ContainerAssist();
                        containerAssist.setContainerId(icId);
                        containerAssist.setSysLength(icLength);
                        containerAssist.setSysWidth(icWidth);
                        containerAssist.setSysHeight(icHeight);
                        containerAssist.setSysVolume(icVolume);
                        containerAssist.setCartonQty(1L);
                        containerAssist.setCreateTime(new Date());
                        containerAssist.setLastModifyTime(new Date());
                        containerAssist.setOperatorId(userId);
                        containerAssist.setOuId(ouId);
                        containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
                        insideContainerAsists.put(icId, containerAssist);
                    }
                }
            } catch (Exception e) {
                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(insideContainerCmd, ouId, logId);
                throw e;
            }
        }*/
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds;
        List<UomCommand> weightUomCmds;
        lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }
        InventoryStatisticResultCommand invStatisticCmd = isrCmd;
        if(null == isrCmd){
            invStatisticCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, lenUomCmds, weightUomCmds, insideContainerCmd, ouId, userId, logId);
            if(null == invStatisticCmd){
                log.error("sys guide putaway inv statistic is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
        }
        Long outerContainerId = containerCmd.getId();
        Set<Long> insideContainerIds = invStatisticCmd.getInsideContainerIds();// 所有内部容器
        Set<Long> caselevelContainerIds = invStatisticCmd.getCaselevelContainerIds();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = invStatisticCmd.getNotcaselevelContainerIds();// 所有非caselevel内部容器
        Set<Long> skuIds = invStatisticCmd.getSkuIds();// 所有sku种类
        Long skuQty = invStatisticCmd.getSkuQty();// sku总件数
        Set<String> skuAttrIds = invStatisticCmd.getSkuAttrIds();// 所有唯一sku
        Set<Long> storeIds = invStatisticCmd.getStoreIds();// 所有店铺
        Set<Long> locationIds = invStatisticCmd.getLocationIds();// 所有推荐库位
        Map<Long, Set<Long>> insideContainerSkuIds = invStatisticCmd.getInsideContainerSkuIds();// 内部容器所有sku种类
        Map<Long, Long> insideContainerSkuQty = invStatisticCmd.getInsideContainerSkuQty();// 内部容器所有sku总件数
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = invStatisticCmd.getInsideContainerSkuIdsQty();// 内部容器单个sku总件数
        Map<Long, Set<String>> insideContainerSkuAttrIds = invStatisticCmd.getInsideContainerSkuAttrIds();// 内部容器唯一sku种类
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = invStatisticCmd.getInsideContainerSkuAttrIdsQty();// 内部容器唯一sku总件数
        //Map<Long, Map<Long, Set<String>>> insideContainerSkuAndSkuAttrIds = invStatisticCmd.getInsideContainerSkuAndSkuAttrIds();// 内部容器sku对应所有唯一sku
        Map<Long, Set<Long>> insideContainerStoreIds = invStatisticCmd.getInsideContainerStoreIds();// 内部容器所有店铺
        Map<Long, Double> insideContainerWeight = invStatisticCmd.getInsideContainerWeight();// 内部容器重量
        //Map<Long, Double> insideContainerVolume = invStatisticCmd.getInsideContainerVolume();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = invStatisticCmd.getInsideContainerAsists();
        // 4.判断是否已推荐库位
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
        if (null != isrCmd) {
            locationIds = isrCmd.getLocationIds();
            if (null == locationIds || 0 == locationIds.size()) {
                pdaPutawayCacheManager.sysGuidePutawayRemoveInventoryStatistic(insideContainerCmd, ouId, logId);
                log.error("sys guide container putaway cache is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }
        if (0 < locationIds.size()) {
            srCmd.setRecommendLocation(true);// 已推荐库位
            if (1 < locationIds.size()) {
                log.error("sys guide pallet putaway location is more than one error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
            Long locId = null;
            for (Long locationId : locationIds) {
                if (null == locId) {
                    locId = locationId;
                    if (null != locId) break;
                }
            }
            Location loc = locationDao.findByIdExt(locId, ouId);
            if (null == loc) {
                log.error("location is null error, locId is:[{}], logId is:[{}]", locId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
            srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
            if(null != warehouse){
                if(true == warehouse.getIsInboundLocationBarcode()){
                    srCmd.setValidateLocation(true);
                }else{
                    srCmd.setValidateLocation(false);
                }
            }
            srCmd.setNeedTipLocation(true);// 提示库位
            return srCmd;
        }
        // 5.删除并更新容器辅助表
        List<Long> deleteContainerIds = new ArrayList<Long>();
        CollectionUtils.addAll(deleteContainerIds, insideContainerIds.iterator());
        containerAssistDao.deleteByContainerIds(ouId, deleteContainerIds);
        // 内部容器辅助表信息
        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
        Double icTotalWeight = 0.0;
        for (Long insideId : insideContainerIds) {
            ContainerAssist containerAssist = insideContainerAsists.get(insideId);
            icTotalWeight += insideContainerWeight.get(insideId);
            containerAssist.setSysWeight(insideContainerWeight.get(insideId));
            containerAssist.setSkuCategory(insideContainerSkuIds.get(insideId).size() + 0L);
            containerAssist.setSkuAttrCategory(insideContainerSkuAttrIds.get(insideId).size() + 0L);
            containerAssist.setSkuQty(insideContainerSkuQty.get(insideId));
            containerAssist.setStoreQty(insideContainerStoreIds.get(insideId).size() + 0L);
            containerAssistDao.insert(containerAssist);
            insertGlobalLog(GLOBAL_LOG_INSERT, containerAssist, ouId, userId, null, null);
            caMap.put(insideId, containerAssist);// 所有的容器辅助信息
        }
        // 6.匹配上架规则
        List<String> icCodeList = new ArrayList<String>();
        icCodeList.add(insideContainerCode);
        List<Long> storeList = new ArrayList<Long>();
        CollectionUtils.addAll(storeList, storeIds.iterator());
        List<Long> icIdList = new ArrayList<Long>();
        CollectionUtils.addAll(icIdList, insideContainerIds.iterator());
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(insideContainerCode);
        ruleAffer.setAfferInsideContainerIdList(icIdList);
        ruleAffer.setContainerId(insideContainerId);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        ruleAffer.setStoreIdList(storeList);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则
        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        // 7.推荐库位
        if (null == caMap || 0 == caMap.size()) {
            log.error("container assist info generate error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        // 判断是否需要排队
        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(insideContainerId, logId);
        if (false == isRecommend) {
            if (log.isInfoEnabled()) {
                log.info("need queue up, current containerId is:[{}], logId is:[{}]", containerId, logId);
            }
            srCmd.setNeedQueueUp(true);
            return srCmd;
        }
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, lenUomConversionRate);
        uomMap.put(WhUomType.WEIGHT_UOM, weightUomConversionRate);
        List<LocationRecommendResultCommand> lrrList = null;
        try {
            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, caMap, invList, uomMap, logId);
        } catch (Exception e1) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
            throw e1;
        }
        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
            log.error("location recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
            srCmd.setRecommendFail(true);
            Boolean result = pdaPutawayCacheManager.sysGuideContainerPutawayNeedTipContainer((true == hasOuterContainer ? containerCmd : null), insideContainerCmd, insideContainerIds, logId);
            if (true == result) {
                srCmd.setAfterRecommendTipContainer(true);
            }
            if (true == srCmd.isAfterRecommendTipContainer()) {
                // 将当前内部容器状态修改为待上架
                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == insideContainerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(insideContainerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
                // 提示下一个容器
                String tipContainerCode = sysGuideTipContainer((null == containerCmd ? null : containerCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                srCmd.setTipContainerCode(tipContainerCode);
            } else {
                // 将当前内部容器状态修改为待上架
                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == insideContainerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(insideContainerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
                if (null != containerCmd) {
                    // 包含外部容器，需要将外部容器状态也修改为待上架
                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                        Container container = new Container();
                        BeanUtils.copyProperties(containerCmd, container);
                        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                        container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                        containerDao.saveOrUpdateByVersion(container);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                    }
                }
                // log.error("location recommend fail! containerCode is:[{}], logId is:[{}]",
                // insideContainerCode, logId);
                // throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
            }
            return srCmd;
        }
        LocationRecommendResultCommand lrr = lrrList.get(0);
        Long lrrLocId = lrr.getLocationId();
        String lrrLocCode = lrr.getLocationCode();
        locationIds = new HashSet<Long>();
        locationIds.add(lrrLocId);
        // 8.缓存容器库存统计信息
        InventoryStatisticResultCommand isCmd = new InventoryStatisticResultCommand();
        isCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
        isCmd.setHasOuterContainer(true);
        isCmd.setOuterContainerId(outerContainerId);
        isCmd.setInsideContainerId(insideContainerId);
        isCmd.setInsideContainerIds(insideContainerIds);
        isCmd.setCaselevelContainerIds(caselevelContainerIds);
        isCmd.setNotcaselevelContainerIds(notcaselevelContainerIds);
        isCmd.setSkuIds(skuIds);
        isCmd.setSkuQty(skuQty);
        isCmd.setSkuAttrIds(skuAttrIds);
        isCmd.setStoreIds(storeIds);
        isCmd.setLocationIds(locationIds);
        isCmd.setInsideContainerSkuIds(insideContainerSkuIds);
        isCmd.setInsideContainerSkuQty(insideContainerSkuQty);
        isCmd.setInsideContainerSkuIdsQty(insideContainerSkuIdsQty);
        isCmd.setInsideContainerSkuAttrIds(insideContainerSkuAttrIds);
        isCmd.setInsideContainerSkuAttrIdsQty(insideContainerSkuAttrIdsQty);
        isCmd.setInsideContainerStoreIds(insideContainerStoreIds);
        // cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC,
        // containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(insideContainerCmd, isCmd, ouId, logId);
        // 9.绑定库位(一入一出)
//        // 先待移入库位库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
//                inv.setOnHandQty(0.0);
//                inv.setLocationId(lrrLocId);
//                try {
//                    inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                inv.setLastModifyTime(new Date());
//                whSkuInventoryDao.insert(inv);
//                insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                // 记录待移入库位库存日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), 0.0, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
//                inv.setOnHandQty(0.0);
//                inv.setLocationId(lrrLocId);
//                try {
//                    inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                inv.setLastModifyTime(new Date());
//                whSkuInventoryDao.insert(inv);
//                insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                // 记录待移入库位库存日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), 0.0, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    sn.setId(null);
//                    try {
//                        sn.setUuid(SkuInventoryUuid.invUuid(inv));
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
//                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                    }
//                    whSkuInventorySnDao.insert(sn);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                }
//            }
//        }
//        // 再出容器库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                // 记录容器移出日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                // 记录容器移出日志
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    whSkuInventorySnDao.delete(sn.getId());
//                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                }
//            }
//
//        }
        try {
            whSkuInventoryManager.execBinding(invList, warehouse, lrrList, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
        } catch (Exception e) {
            // 绑定库位或弹出队列出错，清理库存统计信息缓存
            pdaPutawayCacheManager.sysGuidePutawayRemoveInventoryStatistic(insideContainerCmd, ouId, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_BINDING_ERROR);
        }
        // 10.提示库位
        srCmd.setRecommendLocation(true);// 已推荐库位
        srCmd.setTipLocationCode(lrrLocCode);// 提示库位编码
        srCmd.setTipLocBarCode(lrr.getLocBarcode());// 库位条码
        if(null != warehouse){
            if(true == warehouse.getIsInboundLocationBarcode()){
                srCmd.setValidateLocation(true);
            }else{
                srCmd.setValidateLocation(false);
            }
        }
        srCmd.setNeedTipLocation(true);// 提示库位
        return srCmd;
    }

    /**
     * 系统指导拆箱上架
     * 
     * @author lichuan
     * @param containerCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideSplitContainerPutawayScanContainer(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
        Integer containerStatus = containerCmd.getStatus();
        Long insideContainerId = null;
        String insideContainerCode = null;
        Integer insideContainerStatus = null;
        boolean hasOuterContainer = (null != containerCmd && null != insideContainerCmd);
        // 0.判断是外部容器还是内部容器
        if (null == insideContainerCmd) {
            int count1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
            int count2 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, containerId);
            int count3 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerId);
            int count4 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, containerId);
            if (0 < count2 || 0 < count4) {
                // 整箱上架判断是是否有外部容器号
                int ocCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId1(ouId, containerId);
                int ocLocCount = whSkuInventoryDao.findLocTobefilledInventoryCountsByInsideContainerId1(ouId, containerId);
                if (0 < ocCount || 0 < ocLocCount) {
                    log.error("sys guide container putaway scan container has outer container, should scan outer container first, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST, new Object[] {containerCode});
                } else {
                    srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);// 内部容器,无外部容器，无需循环提示容器
                    insideContainerId = containerId;
                    insideContainerCode = containerCode;
                    insideContainerStatus = containerStatus;
                    insideContainerCmd = containerCmd;
                }
            }
            if (0 < count1 || 0 < count3) {
                srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
                srCmd.setHasOuterContainer(true);// 有外部容器，需要循环提示容器
                ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());
                ContainerStatisticResultCommand csrCmd;
                if (null == cacheCsr) {
                    // 缓存所有内部容器统计信息
                    csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                } else {
                    csrCmd = cacheCsr;
                }
                // 获取所有内部容器id
                Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
                Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
                // 提示一个容器
                Long tipContainerId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer0(containerCmd, insideContainerIds, logId);
                srCmd.setNeedTipContainer(true);
                String tipContainerCode = null;
                if (null != insideContainerIdsCode) {
                    tipContainerCode = insideContainerIdsCode.get(tipContainerId);
                    if (StringUtils.isEmpty(tipContainerCode)) {
                        Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                        if (null == tipContainer) {
                            log.error("container is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                        }
                        tipContainerCode = tipContainer.getCode();
                    }
                }
                srCmd.setTipContainerCode(tipContainerCode);
                // 1.修改容器状态为：上架中，且占用中
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(containerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    srCmd.setOuterContainerCode(containerCode);// 外部容器号
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
                return srCmd;
            }
            if ((0 >= count2 && 0 >= count1) && (0 >= count3 && 0 >= count4)) {
                // 无收货库存
                log.error("sys guide container putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
            }
        } else {
            // 判断是否是当前提示的容器
            insideContainerId = insideContainerCmd.getId();
            insideContainerCode = insideContainerCmd.getCode();
            insideContainerStatus = insideContainerCmd.getStatus();
            srCmd.setInsideContainerCode(insideContainerCode);
        }
        // 1.修改内部容器状态为：上架中，且占用中
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == insideContainerStatus) {
            Container container = new Container();
            BeanUtils.copyProperties(insideContainerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
            containerDao.saveOrUpdateByVersion(container);
            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
        }
        // 2.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> cacheInvs = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, insideContainerId.toString());
        List<WhSkuInventoryCommand> invList = null;
        if (null == cacheInvs || 0 == cacheInvs.size()) {
            // // 缓存所有库存
            // List<String> ocCodelist = new ArrayList<String>();
            // ocCodelist.add(containerCode);
            // // 查询所有对应容器号的库存信息
            // invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
            // if (null == invList || 0 == invList.size()) {
            // log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!,
            // logId is:[{}]", containerCode, logId);
            // throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new
            // Object[] {containerCode});
            // }
            // cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(),
            // invList, CacheConstants.CACHE_ONE_MONTH);
            invList = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventory(insideContainerCmd, ouId, logId);
        } else {
            invList = cacheInvs;
        }
        // 3.库存信息统计
        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerId.toString());
        /*Long outerContainerId = containerCmd.getId();
        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器
        Set<Long> caselevelContainerIds = new HashSet<Long>();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = new HashSet<Long>();// 所有非caselevel内部容器
        Set<Long> skuIds = new HashSet<Long>();// 所有sku种类
        Long skuQty = 0L;// sku总件数
        Set<String> skuAttrIds = new HashSet<String>();// 所有唯一sku
        Set<Long> storeIds = new HashSet<Long>();// 所有店铺
        Set<Long> locationIds = new HashSet<Long>();// 所有推荐库位
        Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();// 内部容器所有sku种类
        Map<Long, Long> insideContainerSkuQty = new HashMap<Long, Long>();// 内部容器所有sku总件数
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();// 内部容器单个sku总件数
        Map<Long, Set<String>> insideContainerSkuAttrIds = new HashMap<Long, Set<String>>();// 内部容器唯一sku种类
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();// 内部容器唯一sku总件数
        Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();// 内部容器唯一sku对应所有SN残次条码
        Map<Long, Set<Long>> insideContainerStoreIds = new HashMap<Long, Set<Long>>();// 内部容器所有店铺
        Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();// 内部容器重量
        Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        if (null == isrCmd) {
            List<UomCommand> lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : lenUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    lenUomConversionRate.put(uomCode, uomRate);
                }
            }
            List<UomCommand> weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : weightUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    weightUomConversionRate.put(uomCode, uomRate);
                }
            }
            SimpleCubeCalculator cubeCalculator = new SimpleCubeCalculator(lenUomConversionRate);
            SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
            try {
                for (WhSkuInventoryCommand invCmd : invList) {
                    String asnCode = invCmd.getOccupationCode();
                    if (StringUtils.isEmpty(asnCode)) {
                        log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                    }
                    WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                    if (null == asn) {
                        log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                    }
                    if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus() && PoAsnStatus.ASN_RCVD != asn.getStatus()) {
                        log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                    }
                    Long poId = asn.getPoId();
                    WhPo po = whPoDao.findWhPoById(poId, ouId);
                    if (null == po) {
                        log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.PO_NULL);
                    }
                    String poCode = po.getPoCode();
                    if (PoAsnStatus.PO_RCVD != po.getStatus() && PoAsnStatus.PO_RCVD_FINISH != po.getStatus()) {
                        log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                    }
                    Long icId = invCmd.getInsideContainerId();
                    Container ic;
                    if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
                        log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    } else {
                        insideContainerIds.add(icId);
                        srCmd.setHasInsideContainer(true);
                    }
                    // 验证容器状态是否可用
                    if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                        log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    // 获取容器状态
                    Integer icStatus = ic.getStatus();
                    if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                        log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, icStatus, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
                    }
                    Long insideContainerCate = ic.getTwoLevelType();
                    Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
                    if (null == insideContainer2) {
                        log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", icId, insideContainerCate, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                    }
                    if (1 != insideContainer2.getLifecycle()) {
                        log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", icId, insideContainer2.getId(), logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    Double icLength = insideContainer2.getLength();
                    Double icWidth = insideContainer2.getWidth();
                    Double icHeight = insideContainer2.getHigh();
                    Double icWeight = insideContainer2.getWeight();
                    if (null == icLength || null == icWidth || null == icHeight) {
                        log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    if (null == icWeight) {
                        log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    Double icVolume = cubeCalculator.calculateStuffVolume(icLength, icWidth, icHeight);
                    insideContainerVolume.put(icId, icVolume);
                    WhCarton carton = whCartonDao.findWhCaselevelCartonById(icId, ouId);
                    if (null != carton) {
                        caselevelContainerIds.add(icId);
                    } else {
                        notcaselevelContainerIds.add(icId);
                    }
                    String invType = invCmd.getInvType();
                    // if (StringUtils.isEmpty(invType)) {
                    // pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(insideContainerCmd,
                    // ouId, logId);
                    // log.error("inv type is null error, logId is:[{}]", logId);
                    // throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                    // }
                    if (!StringUtils.isEmpty(invType)) {
                        List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_TYPE, invType, BaseModel.LIFECYCLE_NORMAL);
                        if (null == invTypeList || 0 == invTypeList.size()) {
                            log.error("inv type is not defined error, invType is:[{}], logId is:[{}]", invType, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                        }
                    }
                    Long invStatus = invCmd.getInvStatus();
                    if (null == invStatus) {
                        log.error("inv status is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    InventoryStatus status = new InventoryStatus();
                    status.setId(invStatus);
                    List<InventoryStatus> invStatusList = inventoryStatusManager.findInventoryStatusList(status);
                    if (null == invStatusList || 0 == invStatusList.size()) {
                        log.error("inv status is not defined error, invStatusId is:[{}], logId is:[{}]", invStatus, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    String invAttr1 = invCmd.getInvAttr1();
                    if (!StringUtils.isEmpty(invAttr1)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_1, invAttr1, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr1 is not defined error, invAttr1 is:[{}], logId is:[{}]", invAttr1, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr1});
                        }
                    }
                    String invAttr2 = invCmd.getInvAttr2();
                    if (!StringUtils.isEmpty(invAttr2)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_2, invAttr2, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr2 is not defined error, invAttr2 is:[{}], logId is:[{}]", invAttr2, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr2});
                        }
                    }
                    String invAttr3 = invCmd.getInvAttr3();
                    if (!StringUtils.isEmpty(invAttr3)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_3, invAttr3, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr3 is not defined error, invAttr3 is:[{}], logId is:[{}]", invAttr3, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR3_NOT_FOUND_ERROR, new Object[] {invAttr3});
                        }
                    }
                    String invAttr4 = invCmd.getInvAttr4();
                    if (!StringUtils.isEmpty(invAttr4)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_4, invAttr4, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr4 is not defined error, invAttr4 is:[{}], logId is:[{}]", invAttr4, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR4_NOT_FOUND_ERROR, new Object[] {invAttr4});
                        }
                    }
                    String invAttr5 = invCmd.getInvAttr5();
                    if (!StringUtils.isEmpty(invAttr5)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_5, invAttr5, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            log.error("inv attr5 is not defined error, invAttr5 is:[{}], logId is:[{}]", invAttr5, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR5_NOT_FOUND_ERROR, new Object[] {invAttr5});
                        }
                    }
                    Long skuId = invCmd.getSkuId();
                    Double toBefillQty = invCmd.getToBeFilledQty();
                    Double onHandQty = invCmd.getOnHandQty();
                    Double curerntSkuQty = 0.0;
                    Long locationId = invCmd.getLocationId();
                    if (null != locationId) {
                        locationIds.add(locationId);
                        if (null != toBefillQty) {
                            curerntSkuQty = toBefillQty;
                            skuQty += toBefillQty.longValue();
                        }
                    } else {
                        if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
                            log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
                        }
                        if (null != onHandQty) {
                            curerntSkuQty = onHandQty;
                            skuQty += onHandQty.longValue();
                        }
                    }
                    if (null != skuId) {
                        skuIds.add(skuId);
                        SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
                        if(null == cacheSku){
                            log.error("sys guide container putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                        }
                        Sku sku = cacheSku.getSku();
//                        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//                        if (null == skuCmd) {
//                            log.error("sys guide  splitContainer putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
//                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//                        }
                        // String skuLenUom = skuCmd.getLengthUom();
                        Double skuLength = sku.getLength();
                        Double skuWidth = sku.getWidth();
                        Double skuHeight = sku.getHeight();
                        // String skuWeightUom = skuCmd.getWeightUom();
                        Double skuWeight = sku.getWeight();
                        if (null == skuLength || null == skuWidth || null == skuHeight) {
                            log.error("sys guide splitContainer putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {sku.getBarCode()});
                        }
                        if (null == skuWeight) {
                            log.error("sys guide splitContainer putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {sku.getBarCode()});
                        }
                        // totalSkuVolume = cubeCalculator.accumulationStuffVolume(skuLength,
                        // skuWidth,
                        // skuHeight);
                        // totalSkuWeight = weightCalculator.accumulationStuffWeight(skuWeight);
                        if (null != insideContainerWeight.get(icId)) {
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        } else {
                            // 先计算容器自重
                            insideContainerWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
                            // 再计算当前商品重量
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        }
                    }
                    skuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                    Long stroeId = invCmd.getStoreId();
                    if (null != stroeId) {
                        storeIds.add(stroeId);
                    }
                    if (null != insideContainerSkuIds.get(icId)) {
                        Set<Long> icSkus = insideContainerSkuIds.get(icId);
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    } else {
                        Set<Long> icSkus = new HashSet<Long>();
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuQty.get(icId)) {
                        insideContainerSkuQty.put(icId, insideContainerSkuQty.get(icId) + curerntSkuQty.longValue());
                    } else {
                        insideContainerSkuQty.put(icId, curerntSkuQty.longValue());
                    }
                    if (null != insideContainerSkuIdsQty.get(icId)) {
                        Map<Long, Long> skuIdsQty = insideContainerSkuIdsQty.get(icId);
                        if (null != skuIdsQty.get(skuId)) {
                            skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
                        } else {
                            skuIdsQty.put(skuId, curerntSkuQty.longValue());
                        }
                    } else {
                        Map<Long, Long> sq = new HashMap<Long, Long>();
                        sq.put(skuId, curerntSkuQty.longValue());
                        insideContainerSkuIdsQty.put(icId, sq);
                    }
                    if (null != insideContainerSkuAttrIds.get(icId)) {
                        Set<String> icSkus = insideContainerSkuAttrIds.get(icId);
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);
                    } else {
                        Set<String> icSkus = new HashSet<String>();
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuAttrIdsQty.get(icId)) {
                        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);
                        if (null != skuAttrIdsQty.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd))) {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), skuAttrIdsQty.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd)) + curerntSkuQty.longValue());
                        } else {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        }
                    } else {
                        Map<String, Long> saq = new HashMap<String, Long>();
                        saq.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        insideContainerSkuAttrIdsQty.put(icId, saq);
                    }
                    // 统计残次品
                    List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();
                    Set<String> snDefects = null;
                    if (null != snCmdList && 0 < snCmdList.size()) {
                        snDefects = new HashSet<String>();
                        for (WhSkuInventorySnCommand snCmd : snCmdList) {
                            if (null != snCmd) {
                                String defectBar = snCmd.getDefectWareBarcode();
                                String sn = snCmd.getSn();
                                snDefects.add(SkuCategoryProvider.concatSkuAttrId(sn, defectBar));
                            }
                        }
                    }
                    if (null != snDefects) {
                        if (null != insideContainerSkuAttrIdsSnDefect.get(icId)) {
                            Map<String, Set<String>> skuAttrIdsDefect = insideContainerSkuAttrIdsSnDefect.get(icId);
                            if (null != skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd))) {
                                Set<String> defects = skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                defects.addAll(snDefects);
                                skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), defects);
                                insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
                            } else {
                                skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
                                insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
                            }
                        } else {
                            Map<String, Set<String>> skuAttrIdsDefect = new HashMap<String, Set<String>>();
                            skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
                            insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
                        }
                    }
                    if (null != insideContainerStoreIds.get(icId)) {
                        Set<Long> icStores = insideContainerStoreIds.get(icId);
                        icStores.add(stroeId);
                        insideContainerStoreIds.put(icId, icStores);
                    } else {
                        Set<Long> icStores = new HashSet<Long>();
                        icStores.add(stroeId);
                        insideContainerStoreIds.put(icId, icStores);
                    }

                    if (null == insideContainerAsists.get(icId)) {
                        ContainerAssist containerAssist = new ContainerAssist();
                        containerAssist.setContainerId(icId);
                        containerAssist.setSysLength(icLength);
                        containerAssist.setSysWidth(icWidth);
                        containerAssist.setSysHeight(icHeight);
                        containerAssist.setSysVolume(icVolume);
                        containerAssist.setCartonQty(1L);
                        containerAssist.setCreateTime(new Date());
                        containerAssist.setLastModifyTime(new Date());
                        containerAssist.setOperatorId(userId);
                        containerAssist.setOuId(ouId);
                        containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
                        insideContainerAsists.put(icId, containerAssist);
                    }
                }
            } catch (Exception e) {
                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(insideContainerCmd, ouId, logId);
                throw e;
            }
        }*/
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds;
        List<UomCommand> weightUomCmds;
        lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }
        InventoryStatisticResultCommand invStatisticCmd = isrCmd;
        if(null == isrCmd){
            invStatisticCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, lenUomCmds, weightUomCmds, insideContainerCmd, ouId, userId, logId);
            if(null == invStatisticCmd){
                log.error("sys guide putaway inv statistic is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
        }
        Long outerContainerId = containerCmd.getId();
        Set<Long> insideContainerIds = invStatisticCmd.getInsideContainerIds();// 所有内部容器
        Set<Long> caselevelContainerIds = invStatisticCmd.getCaselevelContainerIds();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = invStatisticCmd.getNotcaselevelContainerIds();// 所有非caselevel内部容器
        Set<Long> skuIds = invStatisticCmd.getSkuIds();// 所有sku种类
        Long skuQty = invStatisticCmd.getSkuQty();// sku总件数
        Set<String> skuAttrIds = invStatisticCmd.getSkuAttrIds();// 所有唯一sku
        Set<Long> storeIds = invStatisticCmd.getStoreIds();// 所有店铺
        Set<Long> locationIds = invStatisticCmd.getLocationIds();// 所有推荐库位
        Map<Long, Set<Long>> insideContainerSkuIds = invStatisticCmd.getInsideContainerSkuIds();// 内部容器所有sku种类
        Map<Long, Long> insideContainerSkuQty = invStatisticCmd.getInsideContainerSkuQty();// 内部容器所有sku总件数
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = invStatisticCmd.getInsideContainerSkuIdsQty();// 内部容器单个sku总件数
        Map<Long, Set<String>> insideContainerSkuAttrIds = invStatisticCmd.getInsideContainerSkuAttrIds();// 内部容器唯一sku种类
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = invStatisticCmd.getInsideContainerSkuAttrIdsQty();// 内部容器唯一sku总件数
        //Map<Long, Map<Long, Set<String>>> insideContainerSkuAndSkuAttrIds = invStatisticCmd.getInsideContainerSkuAndSkuAttrIds();// 内部容器sku对应所有唯一sku
        Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = invStatisticCmd.getInsideContainerSkuAttrIdsSnDefect();// 内部容器唯一sku对应所有残次条码
        Map<Long, List<Long>> insideContainerLocSort = invStatisticCmd.getInsideContainerLocSort();// 内部容器所有排序后库位
        Map<Long, Set<Long>> insideContainerStoreIds = invStatisticCmd.getInsideContainerStoreIds();// 内部容器所有店铺
        Map<Long, Double> insideContainerWeight = invStatisticCmd.getInsideContainerWeight();// 内部容器重量
        //Map<Long, Double> insideContainerVolume = invStatisticCmd.getInsideContainerVolume();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = invStatisticCmd.getInsideContainerAsists();
        // 4.判断是否已推荐库位
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
        if (null != isrCmd) {
            locationIds = isrCmd.getLocationIds();
            /*if (null == locationIds || 0 == locationIds.size()) {
                pdaPutawayCacheManager.sysGuidePutawayRemoveInventoryStatistic(insideContainerCmd, ouId, logId);
                log.error("sys guide container putaway cache is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }*/
        }
        if (0 < locationIds.size()) {
            srCmd.setRecommendLocation(true);// 已推荐库位
            List<Long> sortLocIds = insideContainerLocSort.get(insideContainerCmd.getId());
            Long locId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipLocation0(insideContainerCmd, sortLocIds, logId);
            Location loc = locationDao.findByIdExt(locId, ouId);
            if (null == loc) {
                log.error("location is null error, locId is:[{}], logId is:[{}]", locId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
            srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
            if(null != warehouse){
                if(true == warehouse.getIsInboundLocationBarcode()){
                    srCmd.setValidateLocation(true);
                }else{
                    srCmd.setValidateLocation(false);
                }
            }
            srCmd.setNeedTipLocation(true);// 提示库位
            return srCmd;
        }
        // 5.删除并更新容器辅助表
        List<Long> deleteContainerIds = new ArrayList<Long>();
        CollectionUtils.addAll(deleteContainerIds, insideContainerIds.iterator());
        containerAssistDao.deleteByContainerIds(ouId, deleteContainerIds);
        // 内部容器辅助表信息
        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
        Double icTotalWeight = 0.0;
        if (null == insideContainerAsists) {
            // 重新计算容器辅助信息
            invStatisticCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, lenUomCmds, weightUomCmds, insideContainerCmd, ouId, userId, logId);
            insideContainerIds = invStatisticCmd.getInsideContainerIds();// 所有内部容器
            caselevelContainerIds = invStatisticCmd.getCaselevelContainerIds();// 所有caselevel内部容器
            notcaselevelContainerIds = invStatisticCmd.getNotcaselevelContainerIds();// 所有非caselevel内部容器
            skuIds = invStatisticCmd.getSkuIds();// 所有sku种类
            skuQty = invStatisticCmd.getSkuQty();// sku总件数
            skuAttrIds = invStatisticCmd.getSkuAttrIds();// 所有唯一sku
            storeIds = invStatisticCmd.getStoreIds();// 所有店铺
            locationIds = invStatisticCmd.getLocationIds();// 所有推荐库位
            insideContainerSkuIds = invStatisticCmd.getInsideContainerSkuIds();// 内部容器所有sku种类
            insideContainerSkuQty = invStatisticCmd.getInsideContainerSkuQty();// 内部容器所有sku总件数
            insideContainerSkuIdsQty = invStatisticCmd.getInsideContainerSkuIdsQty();// 内部容器单个sku总件数
            insideContainerSkuAttrIds = invStatisticCmd.getInsideContainerSkuAttrIds();// 内部容器唯一sku种类
            insideContainerSkuAttrIdsQty = invStatisticCmd.getInsideContainerSkuAttrIdsQty();// 内部容器唯一sku总件数
            //Map<Long, Map<Long, Set<String>>> insideContainerSkuAndSkuAttrIds = invStatisticCmd.getInsideContainerSkuAndSkuAttrIds();// 内部容器sku对应所有唯一sku
            insideContainerSkuAttrIdsSnDefect = invStatisticCmd.getInsideContainerSkuAttrIdsSnDefect();// 内部容器唯一sku对应所有残次条码
            insideContainerStoreIds = invStatisticCmd.getInsideContainerStoreIds();// 内部容器所有店铺
            insideContainerWeight = invStatisticCmd.getInsideContainerWeight();// 内部容器重量
            //Map<Long, Double> insideContainerVolume = invStatisticCmd.getInsideContainerVolume();// 内部容器体积
            insideContainerAsists = invStatisticCmd.getInsideContainerAsists();
        }
        if (null != insideContainerAsists) {
            for (Long insideId : insideContainerIds) {
                ContainerAssist containerAssist = insideContainerAsists.get(insideId);
                if (null != containerAssist) {
                    icTotalWeight += insideContainerWeight.get(insideId);
                    containerAssist.setSysWeight(insideContainerWeight.get(insideId));
                    containerAssist.setSkuCategory(insideContainerSkuIds.get(insideId).size() + 0L);
                    containerAssist.setSkuAttrCategory(insideContainerSkuAttrIds.get(insideId).size() + 0L);
                    containerAssist.setSkuQty(insideContainerSkuQty.get(insideId));
                    containerAssist.setStoreQty(insideContainerStoreIds.get(insideId).size() + 0L);
                    containerAssistDao.insert(containerAssist);
                    insertGlobalLog(GLOBAL_LOG_INSERT, containerAssist, ouId, userId, null, null);
                    caMap.put(insideId, containerAssist);// 所有的容器辅助信息
                }
            }
        }
        if (null == caMap || 0 == caMap.size()) {
            log.error("container assist info generate error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        // 6.匹配上架规则
        List<String> icCodeList = new ArrayList<String>();
        icCodeList.add(insideContainerCode);
        List<Long> storeList = new ArrayList<Long>();
        CollectionUtils.addAll(storeList, storeIds.iterator());
        List<Long> icIdList = new ArrayList<Long>();
        CollectionUtils.addAll(icIdList, insideContainerIds.iterator());
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(insideContainerCode);
        ruleAffer.setAfferInsideContainerIdList(icIdList);
        ruleAffer.setContainerId(insideContainerId);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE);// 拆箱上架规则
        ruleAffer.setStoreIdList(storeList);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则
        List<WhSkuInventoryCommand> ruleList = export.getShelveSkuInvCommandList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        // 7.推荐库位
        // 判断是否需要排队
        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(insideContainerId, logId);
        if (false == isRecommend) {
            if (log.isInfoEnabled()) {
                log.info("need queue up, current containerId is:[{}], logId is:[{}]", containerId, logId);
            }
            srCmd.setNeedQueueUp(true);
            return srCmd;
        }
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, lenUomConversionRate);
        uomMap.put(WhUomType.WEIGHT_UOM, weightUomConversionRate);
        List<LocationRecommendResultCommand> lrrList = null;
        boolean isNoLocRecommend = true;
        try {
            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, caMap, invList, uomMap, logId);
            if (null != lrrList) {
                for (LocationRecommendResultCommand lrrCmd : lrrList) {
                    if (!StringUtils.isEmpty(lrrCmd.getLocationCode())) {
                        isNoLocRecommend = false;
                        break;
                    }
                }
            }
        } catch (Exception e1) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
            throw e1;
        }
        if (null == lrrList || 0 == lrrList.size() || isNoLocRecommend) {
            log.error("location recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
            srCmd.setRecommendFail(true);
            Boolean result = pdaPutawayCacheManager.sysGuideSplitContainerPutawayNeedTipContainer((true == hasOuterContainer ? containerCmd : null), insideContainerCmd, insideContainerIds, logId);
            if (true == result) {
                srCmd.setAfterRecommendTipContainer(true);
            }
            if (true == srCmd.isAfterRecommendTipContainer()) {
                // 将当前内部容器状态修改为待上架
                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == insideContainerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(insideContainerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
                // 提示下一个容器
                String tipContainerCode = sysGuideTipContainer((null == containerCmd ? null : containerCmd.getCode()), funcId, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, userId, logId);
                srCmd.setTipContainerCode(tipContainerCode);
            } else {
                // 将当前内部容器状态修改为待上架
                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == insideContainerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(insideContainerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
                if (null != containerCmd) {
                    // 包含外部容器，需要将外部容器状态也修改为待上架
                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                        Container container = new Container();
                        BeanUtils.copyProperties(containerCmd, container);
                        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                        container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                        containerDao.saveOrUpdateByVersion(container);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                    }
                }
                // log.error("location recommend fail! containerCode is:[{}], logId is:[{}]",
                // insideContainerCode, logId);
                // throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
            }
            return srCmd;
        }
        // 8.分析推荐结果
        Map<String, Long> invRecommendLocId = new HashMap<String, Long>();
        Map<String, String> invRecommendLocCode = new HashMap<String, String>();
        Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
        Map<Long, Map<String, Long>> locSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();
        Map<Long, Map<Long, Map<String, Long>>> insideContainerLocSkuAttrIdsQty = new HashMap<Long, Map<Long, Map<String, Long>>>();
        List<Long> sortLocationIds = new ArrayList<Long>(); // 所有排序后库位
        for (LocationRecommendResultCommand lrrCmd : lrrList) {
            Long locationId = lrrCmd.getLocationId();
            if (null != locationId) {
                locationIds.add(locationId);
                if (null != locSkuAttrIds.get(locationId)) {
                    Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                    allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                } else {
                    Set<String> allSkuAttrIds = new HashSet<String>();
                    allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                }
                if (null != locSkuAttrIdsQty.get(locationId)) {
                    Map<String, Long> allSkuAttrIds = locSkuAttrIdsQty.get(locationId);
                    if (null != allSkuAttrIds.get(SkuCategoryProvider.getSkuAttrId(lrrCmd.getSkuAttrId()))) {
                        Long qty = allSkuAttrIds.get(SkuCategoryProvider.getSkuAttrId(lrrCmd.getSkuAttrId()));
                        allSkuAttrIds.put(SkuCategoryProvider.getSkuAttrId(lrrCmd.getSkuAttrId()), qty + new Long(lrrCmd.getQty().longValue()));
                    } else {
                        allSkuAttrIds.put(SkuCategoryProvider.getSkuAttrId(lrrCmd.getSkuAttrId()), new Long(lrrCmd.getQty().longValue()));
                    }
                    locSkuAttrIdsQty.put(locationId, allSkuAttrIds);
                } else {
                    Map<String, Long> allSkuAttrIds = new HashMap<String, Long>();
                    allSkuAttrIds.put(SkuCategoryProvider.getSkuAttrId(lrrCmd.getSkuAttrId()), new Long(lrrCmd.getQty().longValue()));
                    locSkuAttrIdsQty.put(locationId, allSkuAttrIds);
                }
            }
            String locationCode = lrrCmd.getLocationCode();
            invRecommendLocId.put(lrrCmd.getSkuAttrId(), locationId);
            invRecommendLocCode.put(lrrCmd.getSkuAttrId(), locationCode);
        }
        List<Location> sortLocs = new ArrayList<Location>();
        if (null != locSkuAttrIds && !locSkuAttrIds.isEmpty()) {
            for (Long lId : locSkuAttrIds.keySet()) {
                Location loc = locationDao.findByIdExt(lId, ouId);
                if (null == loc) {
                    log.error("location is null error, locId is:[{}], logId is:[{}]", lId, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                sortLocs.add(loc);
            }
            Collections.sort(sortLocs, new LocationShelfSorter());
            for (Location sortLoc : sortLocs) {
                sortLocationIds.add(sortLoc.getId());
            }
        }
        if (0 == locSkuAttrIds.size()) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
            log.error("location recommend failure! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
        } else {
            insideContainerLocSkuAttrIds.put(insideContainerId, locSkuAttrIds);
            insideContainerLocSkuAttrIdsQty.put(insideContainerId, locSkuAttrIdsQty);
            insideContainerLocSort.put(insideContainerId, sortLocationIds);
        }
        // 9.缓存容器库存统计信息
        InventoryStatisticResultCommand isCmd = new InventoryStatisticResultCommand();
        isCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
        isCmd.setHasOuterContainer(true);
        isCmd.setOuterContainerId(outerContainerId);
        isCmd.setInsideContainerId(insideContainerId);
        isCmd.setInsideContainerIds(insideContainerIds);
        isCmd.setCaselevelContainerIds(caselevelContainerIds);
        isCmd.setNotcaselevelContainerIds(notcaselevelContainerIds);
        isCmd.setSkuIds(skuIds);
        isCmd.setSkuQty(skuQty);
        isCmd.setSkuAttrIds(skuAttrIds);
        isCmd.setStoreIds(storeIds);
        isCmd.setLocationIds(locationIds);
        isCmd.setInsideContainerSkuIds(insideContainerSkuIds);
        isCmd.setInsideContainerSkuQty(insideContainerSkuQty);
        isCmd.setInsideContainerSkuIdsQty(insideContainerSkuIdsQty);
        isCmd.setInsideContainerSkuAttrIds(insideContainerSkuAttrIds);
        isCmd.setInsideContainerSkuAttrIdsQty(insideContainerSkuAttrIdsQty);
        isCmd.setInsideContainerSkuAttrIdsSnDefect(insideContainerSkuAttrIdsSnDefect);
        isCmd.setInsideContainerLocSkuAttrIds(insideContainerLocSkuAttrIds);
        isCmd.setInsideContainerLocSkuAttrIdsQty(insideContainerLocSkuAttrIdsQty);
        isCmd.setInsideContainerLocSort(insideContainerLocSort);
        isCmd.setInsideContainerStoreIds(insideContainerStoreIds);
        isCmd.setInsideContainerAsists(insideContainerAsists);
        // cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC,
        // containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryStatistic(insideContainerCmd, isCmd, ouId, logId);
        // 10.绑定库位(一入一出)
//        // 先待移入库位库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
//                inv.setOnHandQty(0.0);
//                Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
//                if (null == recommendLocId) {
//                    continue;
//                }
//                inv.setLocationId(recommendLocId);
//                try {
//                    inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                inv.setLastModifyTime(new Date());
//                whSkuInventoryDao.insert(inv);
//                insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                // 记录待移入库位库存日志
//                insertSkuInventoryLog(invCmd.getId(), 0.0, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//            } else {
//                Map<Long, Set<String>> allLocSkuAttrIds = new HashMap<Long, Set<String>>();
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    String defectBarcode = snCmd.getDefectWareBarcode();
//                    String snCode = snCmd.getSn();
//                    Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
//                    if (null == recommendLocId) {
//                        continue;
//                    }
//                    if (null != allLocSkuAttrIds.get(recommendLocId)) {
//                        Set<String> allSkuAttrIds = allLocSkuAttrIds.get(recommendLocId);
//                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
//                        allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
//                    } else {
//                        Set<String> allSkuAttrIds = new HashSet<String>();
//                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
//                        allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
//                    }
//                }
//                for (Long locId : allLocSkuAttrIds.keySet()) {
//                    Set<String> allSkuAttrIds = allLocSkuAttrIds.get(locId);
//                    if (null != allSkuAttrIds && 0 < allSkuAttrIds.size()) {
//                        int qty = allSkuAttrIds.size();
//                        WhSkuInventory inv = new WhSkuInventory();
//                        BeanUtils.copyProperties(invCmd, inv);
//                        inv.setId(null);
//                        inv.setToBeFilledQty(new Double(qty));// 待移入
//                        inv.setOnHandQty(0.0);
//                        inv.setLocationId(locId);
//                        try {
//                            inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
//                        } catch (Exception e) {
//                            log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                        }
//                        inv.setLastModifyTime(new Date());
//                        whSkuInventoryDao.insert(inv);
//                        insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                        // 判断是否需要拆库存
//                        if (qty != invCmd.getOnHandQty().intValue()) {
//                            WhSkuInventory splitInv = new WhSkuInventory();
//                            BeanUtils.copyProperties(invCmd, splitInv);
//                            splitInv.setId(null);
//                            splitInv.setToBeFilledQty(null);
//                            splitInv.setOnHandQty(new Double(invCmd.getOnHandQty().intValue() - qty));// 剩余在库数量
//                            splitInv.setLocationId(null);
//                            whSkuInventoryDao.insert(splitInv);
//                            insertSkuInventoryLog(invCmd.getId(), new Double(invCmd.getOnHandQty().intValue() - qty), 0.0, warehouse.getIsTabbInvTotal(), ouId, userId);
//                            insertGlobalLog(GLOBAL_LOG_INSERT, splitInv, ouId, userId, null, null);
//                        }
//                        for (WhSkuInventorySnCommand snCmd : snList) {
//                            String defectBarcode = snCmd.getDefectWareBarcode();
//                            String snCode = snCmd.getSn();
//                            if (allSkuAttrIds.contains(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode))) {
//                                WhSkuInventorySn sn = new WhSkuInventorySn();
//                                BeanUtils.copyProperties(snCmd, sn);
//                                sn.setId(null);
//                                try {
//                                    sn.setUuid(SkuInventoryUuid.invUuid(inv));
//                                } catch (NoSuchAlgorithmException e) {
//                                    log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
//                                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                                }
//                                whSkuInventorySnDao.insert(sn);
//                                insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
//                                insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                            }
//                        }
//                        // 记录待移入库位库存日志(有可能拆库)
//                        Double oldQty = 0.0;
//                        if (true == warehouse.getIsTabbInvTotal()) {
//                            try {
//                                oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                            } catch (NoSuchAlgorithmException e) {
//                                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                            }
//                        } else {
//                            oldQty = 0.0;
//                        }
//                        insertSkuInventoryLog(invCmd.getId(), 0.0, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                    }
//                }
//            }
//        }
//        // 再出容器库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            // 记录容器移出日志
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                Double oldQty = 0.0;
//                if (true == warehouse.getIsTabbInvTotal()) {
//                    try {
//                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                } else {
//                    oldQty = 0.0;
//                }
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
//                if (null == recommendLocId) {
//                    continue;
//                }
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            } else {
//                Map<Long, Set<String>> allLocSkuAttrIds = new HashMap<Long, Set<String>>();
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    String defectBarcode = snCmd.getDefectWareBarcode();
//                    String snCode = snCmd.getSn();
//                    Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
//                    if (null == recommendLocId) {
//                        continue;
//                    }
//                    if (null != allLocSkuAttrIds.get(recommendLocId)) {
//                        Set<String> allSkuAttrIds = allLocSkuAttrIds.get(recommendLocId);
//                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
//                        allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
//                    } else {
//                        Set<String> allSkuAttrIds = new HashSet<String>();
//                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
//                        allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
//                    }
//                }
//                if (null != allLocSkuAttrIds && 0 < allLocSkuAttrIds.size()) {
//                    WhSkuInventory inv = new WhSkuInventory();
//                    BeanUtils.copyProperties(invCmd, inv);
//                    Double oldQty = 0.0;
//                    if (true == warehouse.getIsTabbInvTotal()) {
//                        try {
//                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(SkuInventoryUuid.invUuid(inv), ouId);
//                        } catch (NoSuchAlgorithmException e) {
//                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                        }
//                    } else {
//                        oldQty = 0.0;
//                    }
//                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
//                    whSkuInventoryDao.delete(inv.getId());
//                    insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//                    for (WhSkuInventorySnCommand snCmd : snList) {
//                        String defectBarcode = snCmd.getDefectWareBarcode();
//                        String snCode = snCmd.getSn();
//                        for (Long locId : allLocSkuAttrIds.keySet()) {
//                            Set<String> allSkuAttrIds = allLocSkuAttrIds.get(locId);
//                            if (null != allSkuAttrIds && 0 < allSkuAttrIds.size()) {
//                                if (allSkuAttrIds.contains(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode))) {
//                                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                                    BeanUtils.copyProperties(snCmd, sn);
//                                    whSkuInventorySnDao.delete(sn.getId());
//                                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
        try {
            // 库位绑定（一待入一待出）
            whSkuInventoryManager.execBinding(invList, warehouse, lrrList, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, userId, logId);
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(insideContainerId, logId);
        } catch (Exception e) {
            // 绑定库位或弹出队列出错，清理库存统计信息缓存
            pdaPutawayCacheManager.sysGuidePutawayRemoveInventoryStatistic(insideContainerCmd, ouId, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_BINDING_ERROR);
        }
        // 11.提示库位
        srCmd.setRecommendLocation(true);// 已推荐库位
        /*Long locId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipLocation0(insideContainerCmd, locationIds, logId);
        Location loc = locationDao.findByIdExt(locId, ouId);
        if (null == loc) {
            log.error("location is null error, locId is:[{}], logId is:[{}]", locId, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
        }*/
        Location loc = sortLocs.get(0);// 取到排序后第一个上架库位
        srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
        srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
        if (null != warehouse) {
            if (true == warehouse.getIsInboundLocationBarcode()) {
                srCmd.setValidateLocation(true);
            } else {
                srCmd.setValidateLocation(false);
            }
        }
        srCmd.setNeedTipLocation(true);// 提示库位
        return srCmd;
    }
    
    /**
     * 系统指导上架扫库位
     * 
     * @author lichuan
     * @param containerCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public ScanResultCommand sysGuideScanLocConfirm(String containerCode, String insideContainerCode, String locationCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanLocConfirm start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        ContainerCommand insideContainerCmd;
        if (!StringUtils.isEmpty(insideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == insideContainerCmd) {
                // 内部容器信息不存在
                log.error("inside container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            }
        } else {
            insideContainerCmd = null;
        }
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuidePalletPutawayScanLocConfirm(containerCmd, locationCode, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideContainerPutawayScanLocConfirm(containerCmd, insideContainerCmd, locationCode, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideSplitContainerPutawayScanLocConfirm(containerCmd, insideContainerCmd, locationCode, funcId, ouId, userId, logId);
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanLocConfirm end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        return srCmd;
    }


    /**
     * 系统指导上架整托扫库位
     * 
     * @author lichuan
     * @param containerCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuidePalletPutawayScanLocConfirm(ContainerCommand containerCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        Long containerId = containerCmd.getId();
        // 0.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        if (null == invList || 0 == invList.size()) {
            srCmd.setCacheExists(false);// 缓存信息不存在
            invList = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryAndStatistic(containerCmd, ouId, logId);
        }
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, ouId, logId);
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        // 2.判断是继续扫描内部容器还是直接上架
        Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
        // Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
        Set<Long> caselevelContainerIds = isCmd.getCaselevelContainerIds();
        Set<Long> notcaselevelContainerIds = isCmd.getNotcaselevelContainerIds();
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            Long tipContainerId = pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, insideContainerIds, logId);
            srCmd.setNeedTipContainer(true);
            Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
            if (null == tipContainer) {
                log.error("container is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            srCmd.setTipContainerCode(tipContainer.getCode());
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                sysGuidePalletPutaway(containerCmd.getCode(), locationCode, funcId, ouId, userId, logId);
            } else {
                Long tipContainerId = pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, caselevelContainerIds, logId);
                srCmd.setNeedTipContainer(true);
                Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                if (null == tipContainer) {
                    log.error("container is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                }
                srCmd.setTipContainerCode(tipContainer.getCode());
            }
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 只扫非caselvel货箱
            if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                sysGuidePalletPutaway(containerCmd.getCode(), locationCode, funcId, ouId, userId, logId);
            } else {
                Long tipContainerId = pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, notcaselevelContainerIds, logId);
                if (null == tipContainerId) {
                    // 没有新内部容器提示，则认为全部已复核，可上架
                    srCmd.setPutaway(true);
                    sysGuidePalletPutaway(containerCmd.getCode(), locationCode, funcId, ouId, userId, logId);
                } else {
                    srCmd.setNeedTipContainer(true);
                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    srCmd.setTipContainerCode(tipContainer.getCode());
                }
            }
        } else {
            // 直接上架
            srCmd.setPutaway(true);
            sysGuidePalletPutaway(containerCmd.getCode(), locationCode, funcId, ouId, userId, logId);
            // try {
            // sysGuidePalletPutaway(containerCmd.getCode(), locationCode, funcId, ouId, userId,
            // logId);
            // srCmd.setPutaway(true);
            // } catch (Exception e) {
            // log.error(getLogMsg("sys guide pallet putaway throw exception, logId is:[{}]",
            // logId), e);
            // srCmd.setPutaway(false);// 执行上架失败
            // }
        }
        return srCmd;
    }

    /**
     * 系统指导上架整箱扫库位
     * 
     * @author lichuan
     * @param containerCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideContainerPutawayScanLocConfirm(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
        if (null == insideContainerCmd) {
            insideContainerCmd = containerCmd;
            containerCmd = null;
        }
        Long containerId = insideContainerCmd.getId();
        // 0.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        if (null == invList || 0 == invList.size()) {
            srCmd.setCacheExists(false);// 缓存信息不存在
            invList = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryAndStatistic(insideContainerCmd, ouId, logId);
        }
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(insideContainerCmd, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, logId);
        }
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != containerCmd) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
            if (null == csrCmd) {
                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
            }
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        // 2.判断是继续扫描内部容器还是直接上架
        Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
        // Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
        Set<Long> caselevelContainerIds = isCmd.getCaselevelContainerIds();
        Set<Long> notcaselevelContainerIds = isCmd.getNotcaselevelContainerIds();
        if (null != containerCmd) {
            insideContainerIds = csrCmd.getInsideContainerIds();
            caselevelContainerIds = csrCmd.getCaselevelContainerIds();
            notcaselevelContainerIds = csrCmd.getNotcaselevelContainerIds();
        }
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            srCmd.setNeedScanSku(true);
            srCmd.setScanPattern(scanPattern);// 扫描模式
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                if (null != containerCmd) {
                    // 判断当前箱上架以后是否需要提示下一个容器
                    Boolean result = pdaPutawayCacheManager.sysGuideContainerPutawayNeedTipContainer(containerCmd, insideContainerCmd, insideContainerIds, logId);
                    if (true == result) {
                        srCmd.setAfterPutawayTipContianer(true);
                    }
                }
                sysGuideContainerPutaway((null == containerCmd ? null : containerCmd.getCode()), insideContainerCmd.getCode(), srCmd.isAfterPutawayTipContianer(), locationCode, funcId, ouId, userId, logId);
                if (true == srCmd.isAfterPutawayTipContianer()) {
                    // 提示下一个容器
                    String tipContainerCode = sysGuideTipContainer((null == containerCmd ? null : containerCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                    srCmd.setTipContainerCode(tipContainerCode);
                }
            } else {
                srCmd.setNeedScanSku(true);// 直接复核商品
                srCmd.setScanPattern(scanPattern);// 扫描模式
            }
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 只扫非caselvel货箱
            if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                if (null != containerCmd) {
                    // 判断当前箱上架以后是否需要提示下一个容器
                    Boolean result = pdaPutawayCacheManager.sysGuideContainerPutawayNeedTipContainer(containerCmd, insideContainerCmd, insideContainerIds, logId);
                    if (true == result) {
                        srCmd.setAfterPutawayTipContianer(true);
                    }
                }
                sysGuideContainerPutaway((null == containerCmd ? null : containerCmd.getCode()), insideContainerCmd.getCode(), srCmd.isAfterPutawayTipContianer(), locationCode, funcId, ouId, userId, logId);
                if (true == srCmd.isAfterPutawayTipContianer()) {
                    // 提示下一个容器
                    String tipContainerCode = sysGuideTipContainer((null == containerCmd ? null : containerCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                    srCmd.setTipContainerCode(tipContainerCode);
                }
            } else {
                srCmd.setNeedScanSku(true);// 直接复核商品
                srCmd.setScanPattern(scanPattern);// 扫描模式
            }
        } else {
            // 直接上架
            srCmd.setPutaway(true);
            if (null != containerCmd) {
                // 判断当前箱上架以后是否需要提示下一个容器
                Boolean result = pdaPutawayCacheManager.sysGuideContainerPutawayNeedTipContainer(containerCmd, insideContainerCmd, insideContainerIds, logId);
                if (true == result) {
                    srCmd.setAfterPutawayTipContianer(true);
                }
            }
            sysGuideContainerPutaway((null == containerCmd ? null : containerCmd.getCode()), insideContainerCmd.getCode(), srCmd.isAfterPutawayTipContianer(), locationCode, funcId, ouId, userId, logId);
            if (true == srCmd.isAfterPutawayTipContianer()) {
                // 提示下一个容器
                String tipContainerCode = sysGuideTipContainer((null == containerCmd ? null : containerCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                srCmd.setTipContainerCode(tipContainerCode);
            }
        }
        return srCmd;
    }

    /**
     * 系统指导上架拆箱扫库位
     * 
     * @author lichuan
     * @param containerCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideSplitContainerPutawayScanLocConfirm(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
        if (null == insideContainerCmd) {
            insideContainerCmd = containerCmd;
            containerCmd = null;
        }
        Long containerId = insideContainerCmd.getId();
        // 0.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        if (null == invList || 0 == invList.size()) {
            srCmd.setCacheExists(false);// 缓存信息不存在
            invList = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryAndStatistic(insideContainerCmd, ouId, logId);
        }
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryStatistic(insideContainerCmd, ouId, logId);
        }
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != containerCmd) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
            if (null == csrCmd) {
                csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
            }
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        // 2.提示商品并判断是否需要扫描属性
        // Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();
        // Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect =
        // isCmd.getInsideContainerSkuAttrIdsSnDefect();
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();
        Location loc = locationDao.findLocationByCode(locationCode, ouId);
        if (null == loc) {
            log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
        }
        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(containerId);
        Map<Long, Map<Long, Map<String, Long>>> insideContainerLocSkuAttrIdsQty = isCmd.getInsideContainerLocSkuAttrIdsQty();
        Map<Long, Map<String, Long>> locSkuAttrIdsQty = insideContainerLocSkuAttrIdsQty.get(containerId);
        // Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(containerId);
        Map<String, Long> skuAttrIdsQty = locSkuAttrIdsQty.get(loc.getId());
        String tipSkuAttrId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSku0(insideContainerCmd, loc.getId(), locSkuAttrIds, logId);
        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
        SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
        if (null == cacheSku) {
            log.error("sku is not found error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        Sku sku = cacheSku.getSku();
//        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//        if (null == skuCmd) {
//            log.error("sku is not found error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//        }
        srCmd.setScanPattern(scanPattern);
        tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
        srCmd.setNeedTipSku(true);
        srCmd.setTipSkuBarcode(sku.getBarCode());
        return srCmd;
    }

    private void tipSkuDetailAspect(ScanResultCommand srCmd, String tipSkuAttrId, Map<Long, Set<String>> locSkuAttrIds, Map<String, Long> skuAttrIdsQty, String logId) {
        boolean isTipSkuDetail = TipSkuDetailProvider.isTipSkuDetail(locSkuAttrIds, tipSkuAttrId);
        isTipSkuDetail = true;
        srCmd.setNeedTipSkuDetail(isTipSkuDetail);
        String skuAttrId = SkuCategoryProvider.getSkuAttrId(tipSkuAttrId);
        Long qty = skuAttrIdsQty.get(skuAttrId);
        if (null == qty) {
            log.error("sku qty is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        if (WhScanPatternType.ONE_BY_ONE_SCAN == srCmd.getScanPattern()) {
            srCmd.setTipSkuQty(1);
        } else {
            srCmd.setTipSkuQty(qty.intValue());
        }
        if (true == isTipSkuDetail) {
            srCmd.setNeedTipSkuInvType(TipSkuDetailProvider.isTipSkuInvType(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvType()) {
                String skuInvType = TipSkuDetailProvider.getSkuInvType(tipSkuAttrId);
                List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : invTypeList) {
                    if (sd.getDicValue().equals(skuInvType)) {
                        srCmd.setTipSkuInvType(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv type is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvType("");
            }
            srCmd.setNeedTipSkuInvStatus(TipSkuDetailProvider.isTipSkuInvStatus(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvStatus()) {
                String skuInvStatus = TipSkuDetailProvider.getSkuInvStatus(tipSkuAttrId);
                List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
                boolean isExists = false;
                for (InventoryStatus is : invStatusList) {
                    if (is.getId().toString().equals(skuInvStatus)) {
                        srCmd.setTipSkuInvStatus(is.getName());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv status is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvStatus("");
            }
            srCmd.setNeedTipSkuBatchNumber(TipSkuDetailProvider.isTipSkuBatchNumber(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuBatchNumber()) {
                String skuBatchNumber = TipSkuDetailProvider.getSkuBatchNumber(tipSkuAttrId);
                srCmd.setTipSkuBatchNumber(skuBatchNumber);
            } else {
                srCmd.setTipSkuBatchNumber("");
            }
            srCmd.setNeedTipSkuCountryOfOrigin(TipSkuDetailProvider.isTipSkuCountryOfOrigin(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuCountryOfOrigin()) {
                String skuCountryOfOrigin = TipSkuDetailProvider.getSkuCountryOfOrigin(tipSkuAttrId);
                srCmd.setTipSkuCountryOfOrigin(skuCountryOfOrigin);
            } else {
                srCmd.setTipSkuCountryOfOrigin("");
            }
            srCmd.setNeedTipSkuMfgDate(TipSkuDetailProvider.isTipSkuMfgDate(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuMfgDate()) {
                String skuMfgDate = TipSkuDetailProvider.getSkuMfgDate(tipSkuAttrId);
                srCmd.setTipSkuMfgDate(skuMfgDate);
            } else {
                srCmd.setTipSkuMfgDate("");
            }
            srCmd.setNeedTipSkuExpDate(TipSkuDetailProvider.isTipSkuExpDate(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuExpDate()) {
                String skuExpDate = TipSkuDetailProvider.getSkuExpDate(tipSkuAttrId);
                srCmd.setTipSkuExpDate(skuExpDate);
            } else {
                srCmd.setTipSkuExpDate("");
            }
            srCmd.setNeedTipSkuInvAttr1(TipSkuDetailProvider.isTipSkuInvAttr1(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr1()) {
                String skuInvAttr1 = TipSkuDetailProvider.getSkuInvAttr1(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr1)) {
                        srCmd.setTipSkuInvAttr1(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr1 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr1("");
            }
            srCmd.setNeedTipSkuInvAttr2(TipSkuDetailProvider.isTipSkuInvAttr2(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr2()) {
                String skuInvAttr2 = TipSkuDetailProvider.getSkuInvAttr2(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr2)) {
                        srCmd.setTipSkuInvAttr2(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr2 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr2("");
            }
            srCmd.setNeedTipSkuInvAttr3(TipSkuDetailProvider.isTipSkuInvAttr3(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr3()) {
                String skuInvAttr3 = TipSkuDetailProvider.getSkuInvAttr3(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr3)) {
                        srCmd.setTipSkuInvAttr3(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr3 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr3("");
            }
            srCmd.setNeedTipSkuInvAttr4(TipSkuDetailProvider.isTipSkuInvAttr4(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr4()) {
                String skuInvAttr4 = TipSkuDetailProvider.getSkuInvAttr4(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr4)) {
                        srCmd.setTipSkuInvAttr4(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr4 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr4("");
            }
            srCmd.setNeedTipSkuInvAttr5(TipSkuDetailProvider.isTipSkuInvAttr5(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr5()) {
                String skuInvAttr5 = TipSkuDetailProvider.getSkuInvAttr5(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr5)) {
                        srCmd.setTipSkuInvAttr5(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr5 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr5("");
            }
            srCmd.setNeedTipSkuSn(TipSkuDetailProvider.isTipSkuSn(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuSn()) {
                String skuSn = TipSkuDetailProvider.getSkuSn(tipSkuAttrId);
                srCmd.setTipSkuSn(skuSn);
            } else {
                srCmd.setTipSkuSn("");
            }
            srCmd.setNeedTipSkuDefect(TipSkuDetailProvider.isTipSkuDefect(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuDefect()) {
                String skuDefect = TipSkuDetailProvider.getSkuDefect(tipSkuAttrId);
                srCmd.setTipSkuDefect(skuDefect);
            } else {
                srCmd.setTipSkuDefect("");
            }
        }
    }

    /**
     * 系统指导上架核对扫容器号
     * 
     * @author lichuan
     * @param containerCode
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public ScanResultCommand sysGuideCheckScanContainerConfirm(String containerCode, String insideContainer, String locationCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideCheckScanContainerConfirm start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        ContainerCommand ocCmd = null;
        ContainerCommand icCmd = null;
        if (!StringUtils.isEmpty(containerCode)) {
            ocCmd = containerDao.getContainerByCode(containerCode, ouId);
            if (null == ocCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(ocCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ocCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = ocCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
        }
        if (!StringUtils.isEmpty(insideContainer)) {
            icCmd = containerDao.getContainerByCode(insideContainer, ouId);
            if (null == ocCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(icCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != icCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = icCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
        }
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuidePalletPutawayCheckScanContainerConfirm(ocCmd, icCmd, locationCode, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideContainerPutawayCheckScanContainerConfirm(ocCmd, icCmd, locationCode, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideSplitContainerPutawayCheckScanContainerConfirm(ocCmd, icCmd, locationCode, funcId, ouId, userId, logId);
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideCheckScanContainerConfirm start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        return srCmd;
    }

    /**
     * 系统指导上架整托核对扫容器号
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param
     * @param locationCode
     * @param funcId
     * @param
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuidePalletPutawayCheckScanContainerConfirm(ContainerCommand ocCmd, ContainerCommand icCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (null == ocCmd) {
            log.error("sys guide pallet putaway check san container, outer container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS);
        }
        if (null == icCmd) {
            log.error("sys guide pallet putaway check san container, inside container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            srCmd.setNeedScanSku(true);// 直接复核商品
            srCmd.setScanPattern(scanPattern);// 扫描模式
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            srCmd.setNeedScanSku(true);// 直接复核商品
            srCmd.setScanPattern(scanPattern);// 扫描模式
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 只扫非caselvel货箱
            srCmd.setNeedScanSku(true);// 直接复核商品
            srCmd.setScanPattern(scanPattern);// 扫描模式
        } else {
            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return srCmd;
    }

    /**
     * 系统指导上架整箱核对扫容器号
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideContainerPutawayCheckScanContainerConfirm(ContainerCommand ocCmd, ContainerCommand icCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        if (null == icCmd) {
            log.error("sys guide pallet putaway check san container, inside container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
        }

        return srCmd;
    }

    /**
     * 系统指导上架拆箱核对扫容器号
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideSplitContainerPutawayCheckScanContainerConfirm(ContainerCommand ocCmd, ContainerCommand icCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        if (null == icCmd) {
            log.error("sys guide pallet putaway check san container, inside container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
        }

        return srCmd;
    }

    /**
     * 系统指导上架核对扫描商品
     * 
     * @author lichuan
     * @param containerCode
     * @param insideContainerCode
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public ScanResultCommand sysGuideCheckScanSkuConfirm(String containerCode, String insideContainerCode, WhSkuCommand skuCmd, String locationCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideCheckScanSkuConfirm start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        ContainerCommand ocCmd = null;
        ContainerCommand icCmd = null;
        if (!StringUtils.isEmpty(containerCode)) {
            ocCmd = containerDao.getContainerByCode(containerCode, ouId);
            if (null == ocCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(ocCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ocCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = ocCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
        }
        if (!StringUtils.isEmpty(insideContainerCode)) {
            icCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == ocCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(icCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != icCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = icCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
        }
        String barcode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(barcode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
//        WhSkuCommand sCmd = whSkuDao.findWhSkuByBarcodeExt(barcode, ouId);
//        if (null == sCmd) {
//            log.error("sku is null error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//        } else {
//            if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY != putawayPatternDetailType) {
//                BeanUtils.copyProperties(sCmd, skuCmd);
//                skuCmd.setScanSkuQty(scanQty);
//            }
//        }
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuidePalletPutawayCheckScanSkuConfirm(ocCmd, icCmd, skuCmd, locationCode, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideContainerPutawayCheckScanSkuConfirm(ocCmd, icCmd, skuCmd, locationCode, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            srCmd = sysGuideSplitContainerPutawayCheckScanSkuConfirm(ocCmd, icCmd, skuCmd, locationCode, funcId, ouId, userId, logId);
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideCheckScanSkuConfirm start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        return srCmd;
    }

    /**
     * 系统指导上架整托核对扫商品
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuidePalletPutawayCheckScanSkuConfirm(ContainerCommand ocCmd, ContainerCommand icCmd, WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (null == ocCmd) {
            log.error("sys guide pallet putaway check san sku, outer container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS);
        }
        if (null == icCmd) {
            log.error("sys guide pallet putaway check san sku, inside container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
        }
        // 0.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, ocCmd.getId().toString());
        if (null == invList || 0 == invList.size()) {
            srCmd.setCacheExists(false);// 缓存信息不存在
            invList = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryAndStatistic(ocCmd, ouId, logId);
        }
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, ocCmd.getId().toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(ocCmd, ouId, logId);
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        // 2.复核扫描的商品并判断是否切换内部容器
        Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
        Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
        Set<Long> caselevelContainerIds = isCmd.getCaselevelContainerIds();
        Set<Long> notcaselevelContainerIds = isCmd.getNotcaselevelContainerIds();
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();
        // 商品校验
        Long skuId = null;
        String skuBarcode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);
        Set<Long> icSkuIds = insideContainerSkuIds.get(icCmd.getId());
        Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(icCmd.getId());
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for(Long cacheId : cacheSkuIdsQty.keySet()){
            if(icSkuIds.contains(cacheId)){
                isSkuExists = true;
            }
            if(true == isSkuExists){
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if(false == isSkuExists){
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocCmd.getId(), icCmd.getId(), skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
        }
        if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
            if(0 != (icSkuQty%cacheSkuQty)){
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern){
            if(0 != new Double("1").compareTo(scanQty)){
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(ocCmd, icCmd, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
            if (cssrCmd.isNeedScanSku()) {
                srCmd.setNeedScanSku(true);// 直接复核商品
                srCmd.setScanPattern(scanPattern);// 扫码模式
            } else if (cssrCmd.isNeedTipContainer()) {
                srCmd.setNeedTipContainer(true);
                Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                if (null == tipContainer) {
                    log.error("container is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                }
                srCmd.setTipContainerCode(tipContainer.getCode());
            } else {
                srCmd.setPutaway(true);
                sysGuidePalletPutaway(ocCmd.getCode(), locationCode, funcId, ouId, userId, logId);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                sysGuidePalletPutaway(ocCmd.getCode(), locationCode, funcId, ouId, userId, logId);
            } else {
                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(ocCmd, icCmd, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    srCmd.setNeedScanSku(true);// 直接复核商品
                    srCmd.setScanPattern(scanPattern);// 扫码模式
                } else if (cssrCmd.isNeedTipContainer()) {
                    srCmd.setNeedTipContainer(true);
                    Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    srCmd.setTipContainerCode(tipContainer.getCode());
                } else {
                    srCmd.setPutaway(true);
                    sysGuidePalletPutaway(ocCmd.getCode(), locationCode, funcId, ouId, userId, logId);
                }
            }

        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 只扫非caselvel货箱
            if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                sysGuidePalletPutaway(ocCmd.getCode(), locationCode, funcId, ouId, userId, logId);
            } else {
                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(ocCmd, icCmd, notcaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    srCmd.setNeedScanSku(true);// 直接复核商品
                    srCmd.setScanPattern(scanPattern);// 扫码模式
                } else if (cssrCmd.isNeedTipContainer()) {
                    srCmd.setNeedTipContainer(true);
                    Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    srCmd.setTipContainerCode(tipContainer.getCode());
                } else {
                    srCmd.setPutaway(true);
                    sysGuidePalletPutaway(ocCmd.getCode(), locationCode, funcId, ouId, userId, logId);
                }
            }
        } else {
            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
        }
        return srCmd;
    }

    /**
     * 系统指导上架整箱核对扫商品
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideContainerPutawayCheckScanSkuConfirm(ContainerCommand ocCmd, ContainerCommand icCmd, WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
        if (null == icCmd) {
            // 无外部容器
            icCmd = ocCmd;
            ocCmd = null;
        }
        if (null == icCmd) {
            log.error("sys guide container putaway check san sku, inside container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
        }
        // 0.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, icCmd.getId().toString());
        if (null == invList || 0 == invList.size()) {
            srCmd.setCacheExists(false);// 缓存信息不存在
            invList = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryAndStatistic(icCmd, ouId, logId);
        }
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icCmd.getId().toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(icCmd, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, logId);
        }
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != ocCmd) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocCmd.getId().toString());
            if (null == csrCmd) {
                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(ocCmd, ouId, logId);
            }
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        // 2.复核扫描的商品并判断是否切换内部容器
        Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
        Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
        Set<Long> caselevelContainerIds = isCmd.getCaselevelContainerIds();
        Set<Long> notcaselevelContainerIds = isCmd.getNotcaselevelContainerIds();
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();
        if (null != ocCmd) {
            insideContainerIds = csrCmd.getInsideContainerIds();
            caselevelContainerIds = csrCmd.getCaselevelContainerIds();
            notcaselevelContainerIds = csrCmd.getNotcaselevelContainerIds();
        }
        // 商品校验
        Long skuId = null;
        String skuBarcode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);
        Set<Long> icSkuIds = insideContainerSkuIds.get(icCmd.getId());
        Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(icCmd.getId());
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for(Long cacheId : cacheSkuIdsQty.keySet()){
            if(icSkuIds.contains(cacheId)){
                isSkuExists = true;
            }
            if(true == isSkuExists){
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if(false == isSkuExists){
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocCmd.getId(), icCmd.getId(), skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
        }
        if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
            if(0 != (icSkuQty%cacheSkuQty)){
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern){
            if(0 != new Double("1").compareTo(scanQty)){
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(ocCmd, icCmd, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
            if (cssrCmd.isNeedScanSku()) {
                srCmd.setNeedScanSku(true);// 直接复核商品
                srCmd.setScanPattern(scanPattern);// 扫码模式
            } else if (cssrCmd.isNeedTipContainer()) {
                srCmd.setPutaway(true);
                sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), true, locationCode, funcId, ouId, userId, logId);
                srCmd.setAfterPutawayTipContianer(true);
                if (true == srCmd.isAfterPutawayTipContianer()) {
                    // 提示下一个容器
                    String tipContainerCode = sysGuideTipContainer((null == ocCmd ? null : ocCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                    srCmd.setTipContainerCode(tipContainerCode);
                }
            } else {
                srCmd.setPutaway(true);
                sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, locationCode, funcId, ouId, userId, logId);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                if (null != ocCmd) {
                    // 判断当前箱上架以后是否需要提示下一个容器
                    Boolean result = pdaPutawayCacheManager.sysGuideContainerPutawayNeedTipContainer(ocCmd, icCmd, insideContainerIds, logId);
                    if (true == result) {
                        srCmd.setAfterPutawayTipContianer(true);
                    }
                }
                sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, locationCode, funcId, ouId, userId, logId);
                if (true == srCmd.isAfterPutawayTipContianer()) {
                    // 提示下一个容器
                    String tipContainerCode = sysGuideTipContainer((null == ocCmd ? null : ocCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                    srCmd.setTipContainerCode(tipContainerCode);
                }
            } else {
                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(ocCmd, icCmd, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    srCmd.setNeedScanSku(true);// 直接复核商品
                    srCmd.setScanPattern(scanPattern);// 扫码模式
                } else if (cssrCmd.isNeedTipContainer()) {
                    srCmd.setPutaway(true);
                    sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), true, locationCode, funcId, ouId, userId, logId);
                    srCmd.setAfterPutawayTipContianer(true);
                    if (true == srCmd.isAfterPutawayTipContianer()) {
                        // 提示下一个容器
                        String tipContainerCode = sysGuideTipContainer((null == ocCmd ? null : ocCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                        srCmd.setTipContainerCode(tipContainerCode);
                    }
                } else {
                    srCmd.setPutaway(true);
                    sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, locationCode, funcId, ouId, userId, logId);
                }
            }

        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 只扫非caselvel货箱
            if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                srCmd.setPutaway(true);
                if (null != ocCmd) {
                    // 判断当前箱上架以后是否需要提示下一个容器
                    Boolean result = pdaPutawayCacheManager.sysGuideContainerPutawayNeedTipContainer(ocCmd, icCmd, insideContainerIds, logId);
                    if (true == result) {
                        srCmd.setAfterPutawayTipContianer(true);
                    }
                }
                sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, locationCode, funcId, ouId, userId, logId);
                if (true == srCmd.isAfterPutawayTipContianer()) {
                    // 提示下一个容器
                    String tipContainerCode = sysGuideTipContainer((null == ocCmd ? null : ocCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                    srCmd.setTipContainerCode(tipContainerCode);
                }
            } else {
                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(ocCmd, icCmd, notcaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    srCmd.setNeedScanSku(true);// 直接复核商品
                    srCmd.setScanPattern(scanPattern);// 扫码模式
                } else if (cssrCmd.isNeedTipContainer()) {
                    srCmd.setPutaway(true);
                    sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), true, locationCode, funcId, ouId, userId, logId);
                    srCmd.setAfterPutawayTipContianer(true);
                    if (true == srCmd.isAfterPutawayTipContianer()) {
                        // 提示下一个容器
                        String tipContainerCode = sysGuideTipContainer((null == ocCmd ? null : ocCmd.getCode()), funcId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                        srCmd.setTipContainerCode(tipContainerCode);
                    }
                } else {
                    srCmd.setPutaway(true);
                    sysGuideContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, locationCode, funcId, ouId, userId, logId);
                }
            }
        } else {
            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
        }
        return srCmd;
    }

    /**
     * 系统指导上架拆箱核对扫商品
     * 
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand sysGuideSplitContainerPutawayCheckScanSkuConfirm(ContainerCommand ocCmd, ContainerCommand icCmd, WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
        if (null == icCmd) {
            // 无外部容器
            icCmd = ocCmd;
            ocCmd = null;
        }
        if (null == icCmd) {
            log.error("sys guide splitContainer putaway check san sku, inside container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
        }
        // 0.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, icCmd.getId().toString());
        if (null == invList || 0 == invList.size()) {
            srCmd.setCacheExists(false);// 缓存信息不存在
            invList = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryAndStatistic(icCmd, ouId, logId);
        }
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icCmd.getId().toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(icCmd, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, logId);
        }
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != ocCmd) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocCmd.getId().toString());
            if (null == csrCmd) {
                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(ocCmd, ouId, logId);
            }
        }
        
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;

        // 2.判断当前商品是否扫完、是否提示下一个库位、容器或上架
        Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
        if (null != ocCmd) {
            insideContainerIds = csrCmd.getInsideContainerIds();
        }
        Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();
        Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = isCmd.getInsideContainerSkuAttrIdsSnDefect();
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();
        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icCmd.getId());
        Map<Long, Map<Long, Map<String, Long>>> insideContainerLocSkuAttrIdsQty = isCmd.getInsideContainerLocSkuAttrIdsQty();
        Map<Long, Map<String, Long>> locSkuAttrIdsQty = insideContainerLocSkuAttrIdsQty.get(icCmd.getId());
        Map<Long, List<Long>> insideContainerLocSort = isCmd.getInsideContainerLocSort();
        Set<Long> locationIds = locSkuAttrIds.keySet();
        // Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icCmd.getId());
        Location loc = locationDao.findLocationByCode(locationCode, ouId);
        if (null == loc) {
            log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
        }
        Map<String, Long> skuAttrIdsQty = locSkuAttrIdsQty.get(loc.getId());
//        String barcode = skuCmd.getBarCode();
        // 商品校验
        Long sId = null;
        String skuBarcode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);
        Set<Long> icSkuIds = insideContainerSkuIds.get(icCmd.getId());
        Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(icCmd.getId());
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for(Long cacheId : cacheSkuIdsQty.keySet()){
            if(icSkuIds.contains(cacheId)){
                isSkuExists = true;
            }
            if(true == isSkuExists){
                sId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if(false == isSkuExists){
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocCmd.getId(), icCmd.getId(), sId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
        }
        if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
            if(0 != (icSkuQty%cacheSkuQty)){
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        skuCmd.setId(sId);
        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
        SkuRedisCommand cacheSkuCmd = skuRedisManager.findSkuMasterBySkuId(sId, ouId, logId);
        if (null == cacheSkuCmd) {
            log.error("sku is not found error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        Sku s = cacheSkuCmd.getSku();
        WhSkuCommand sku = new WhSkuCommand();
        BeanUtils.copyProperties(s, sku);
        sku.setId(skuCmd.getId());
        sku.setScanSkuQty(skuCmd.getScanSkuQty());
        sku.setIsNeedTipSkuDetail(null == skuCmd.getIsNeedTipSkuDetail() ? false : skuCmd.getIsNeedTipSkuDetail());
        sku.setIsNeedTipSkuSn(null == skuCmd.getIsNeedTipSkuSn() ? false : skuCmd.getIsNeedTipSkuSn());
        sku.setIsNeedTipSkuDefect(null == skuCmd.getIsNeedTipSkuDefect() ? false : skuCmd.getIsNeedTipSkuDefect());
        sku.setInvType(StringUtils.isEmpty(skuCmd.getInvType()) ? "" : skuCmd.getInvType());
        sku.setInvStatus(StringUtils.isEmpty(skuCmd.getInvStatus()) ? "" : skuCmd.getInvStatus());
        sku.setInvBatchNumber(StringUtils.isEmpty(skuCmd.getInvBatchNumber()) ? "" : skuCmd.getInvBatchNumber());
        sku.setInvCountryOfOrigin(StringUtils.isEmpty(skuCmd.getInvCountryOfOrigin()) ? "" : skuCmd.getInvCountryOfOrigin());
        sku.setInvMfgDate(StringUtils.isEmpty(skuCmd.getInvMfgDate()) ? "" : skuCmd.getInvMfgDate());
        sku.setInvExpDate(StringUtils.isEmpty(skuCmd.getInvExpDate()) ? "" : skuCmd.getInvExpDate());
        sku.setInvAttr1(StringUtils.isEmpty(skuCmd.getInvAttr1()) ? "" : skuCmd.getInvAttr1());
        sku.setInvAttr2(StringUtils.isEmpty(skuCmd.getInvAttr2()) ? "" : skuCmd.getInvAttr2());
        sku.setInvAttr3(StringUtils.isEmpty(skuCmd.getInvAttr3()) ? "" : skuCmd.getInvAttr3());
        sku.setInvAttr4(StringUtils.isEmpty(skuCmd.getInvAttr4()) ? "" : skuCmd.getInvAttr4());
        sku.setInvAttr5(StringUtils.isEmpty(skuCmd.getInvAttr5()) ? "" : skuCmd.getInvAttr5());
        sku.setSkuSn(StringUtils.isEmpty(skuCmd.getSkuSn()) ? "" : skuCmd.getSkuSn());
        sku.setSkuDefect(StringUtils.isEmpty(skuCmd.getSkuDefect()) ? "" : skuCmd.getSkuDefect());
        CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSkuOrLocOrContainer(ocCmd, icCmd, insideContainerIds, insideContainerSkuAttrIdsQty, insideContainerSkuAttrIdsSnDefect, insideContainerLocSkuAttrIds,
                insideContainerLocSkuAttrIdsQty, insideContainerLocSort, loc.getId(), sku, scanPattern, logId);
        if (cssrCmd.isNeedTipSkuSn()) {
            // 当前商品还未扫描，继续扫sn残次信息
            srCmd.setNeedScanSkuSn(true);// 继续扫sn
            srCmd.setScanPattern(scanPattern);
            String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
            Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
            SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
            if (null == cacheSku) {
                log.error("sku is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Sku tipSku = cacheSku.getSku();
//            WhSkuCommand tipSkuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//            if (null == tipSkuCmd) {
//                log.error("sku is not found error, logId is:[{}]", logId);
//                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//            }
            tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
            srCmd.setTipSkuBarcode(tipSku.getBarCode());
            if (false == cssrCmd.isTipSameSkuAttrId()) {
                pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSku(icCmd, loc.getId(), locSkuAttrIds, tipSkuAttrId, logId);
            }
        } else if (cssrCmd.isNeedTipSku()) {
            // 当前商品复核完毕，提示下一个商品
            srCmd.setNeedTipSku(true);// 提示下一个sku
            srCmd.setScanPattern(scanPattern);
            String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
            Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
            SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
            if (null == cacheSku) {
                log.error("sku is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Sku tipSku = cacheSku.getSku();
//            WhSkuCommand tipSkuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//            if (null == tipSkuCmd) {
//                log.error("sku is not found error, logId is:[{}]", logId);
//                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//            }
            tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
            srCmd.setTipSkuBarcode(tipSku.getBarCode());
            if (false == cssrCmd.isTipSameSkuAttrId()) {
                pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSku(icCmd, loc.getId(), locSkuAttrIds, tipSkuAttrId, logId);
            }
        } else if (cssrCmd.isNeedTipLoc()) {
            // 当前库位对应的商品已扫描完毕，可上架，并提示下一个库位
            srCmd.setPutaway(true);
            sysGuideSplitContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, true, locationCode, funcId, ouId, userId, logId);
            srCmd.setAfterPutawayTipLoc(true);
            Long tipLocId = cssrCmd.getTipLocId();
            Location tipLoc = locationDao.findByIdExt(tipLocId, ouId);
            if (null == tipLoc) {
                log.error("location is null error, locId is:[{}], logId is:[{}]", tipLocId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            srCmd.setTipLocationCode(tipLoc.getCode());
            pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipLocation(icCmd, locationIds, loc.getId(), logId);
        } else if (cssrCmd.isNeedTipContainer()) {
            // 当前容器已扫描完毕，可上架，并提示下一个容器
            srCmd.setPutaway(true);
            sysGuideSplitContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), true, false, locationCode, funcId, ouId, userId, logId);
            srCmd.setAfterPutawayTipContianer(true);
            Long tipContainerId = cssrCmd.getTipContainerId();
            Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
            if (null == tipContainer) {
                log.error("tip container is null error, containerId is:[{}], logId is:[{}]", tipContainerId, logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            srCmd.setTipContainerCode(tipContainer.getCode());
            pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer(ocCmd, insideContainerIds, tipContainerId, logId);
        } else {
            // 执行上架
            srCmd.setPutaway(true);
            sysGuideSplitContainerPutaway((null == ocCmd ? null : ocCmd.getCode()), icCmd.getCode(), false, false, locationCode, funcId, ouId, userId, logId);
        }
        return srCmd;
    }

    /**
     * 系统指导整托上架执行
     * 
     * @author lichuan
     * @param containerCode
     * @param locationCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void sysGuidePalletPutaway(String containerCode, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        // 0.修改外部容器状态
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
//        Long containerId = null;
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
//        containerId = containerCmd.getId();
        // 验证容器状态是否可用
        if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        // 1.执行上架
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
//        boolean isTV = true;// 是否跟踪容器
//        boolean isBM = true;// 是否批次管理
//        boolean isVM = true;// 是否管理效期
//        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
//        Location loc = locationDao.findLocationByCode(locationCode, ouId);
//        if (null == loc) {
//            log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//        }
//        isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
//        isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
//        isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
//        List<String> cclist = new ArrayList<String>();
//        cclist.add(containerCode);
//        List<WhSkuInventoryCommand> invList = null;
//        List<String> ocCodelist = new ArrayList<String>();
//        ocCodelist.add(containerCode);
//        // 查询所有对应容器号的库存信息
//        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
//        if (null == invList || 0 == invList.size()) {
//            log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
//        }
//        // 2.执行上架(一入一出)
//        // 先入库位库存
//        Set<Long> locIds = new HashSet<Long>();
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            String uuid = "";
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
//                inv.setToBeFilledQty(0.0);
//                if (!StringUtils.isEmpty(containerCode)) {
//                    if (0 != containerId.compareTo(inv.getOuterContainerId())) {
//                        log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
//                        throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
//                    }
//                }
//                if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
//                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
//                } else {
//                    locIds.add(inv.getLocationId());
//                }
//                if (locIds.size() > 1) {
//                    log.error("binding location is more than one error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
//                }
//                if (false == isTV) {
//                    inv.setOuterContainerId(null);
//                    inv.setInsideContainerId(null);
//                }
//                if (false == isBM) {
//                    inv.setBatchNumber(null);
//                }
//                if (false == isVM) {
//                    inv.setExpDate(null);
//                }
//                inv.setOccupationCode(null);
//                Long icId = invCmd.getInsideContainerId();
//                if (null != icId) {
//                    insideContainerIds.add(icId);
//                }
//                try {
//                    uuid = SkuInventoryUuid.invUuid(inv);
//                    inv.setUuid(uuid);// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                WhSkuInventory oldSkuInv = null;
//                if(!uuid.equals(invCmd.getUuid())){
//                    oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(ouId, uuid);
//                }
//                if (null == oldSkuInv) {
//                    // uuid不存在则插入新的库存记录
//                    inv.setLastModifyTime(new Date());
//                    whSkuInventoryDao.insert(inv);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), inv.getOnHandQty(), 0.0, true, ouId, userId);
//                } else {
//                    // uuid已存在则合并库存
//                    WhSkuInventory newSkuInv = new WhSkuInventory();
//                    BeanUtils.copyProperties(oldSkuInv, newSkuInv);
//                    newSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + inv.getOnHandQty());
//                    newSkuInv.setLastModifyTime(new Date());
//                    int count = whSkuInventoryDao.saveOrUpdateByVersion(newSkuInv);
//                    if (count == 0) {
//                        log.error("update inventory error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), newSkuInv.getOnHandQty(), oldSkuInv.getOnHandQty(), true, ouId, userId);
//                }
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
//                inv.setToBeFilledQty(0.0);
//                if (!StringUtils.isEmpty(containerCode)) {
//                    if (0 != containerId.compareTo(inv.getOuterContainerId())) {
//                        log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
//                        throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
//                    }
//                }
//                if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
//                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
//                } else {
//                    locIds.add(inv.getLocationId());
//                }
//                if (locIds.size() > 1) {
//                    log.error("binding location is more than one error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
//                }
//                if (false == isTV) {
//                    inv.setOuterContainerId(null);
//                    inv.setInsideContainerId(null);
//                }
//                if (false == isBM) {
//                    inv.setBatchNumber(null);
//                }
//                if (false == isVM) {
//                    inv.setExpDate(null);
//                }
//                inv.setOccupationCode(null);
//                Long icId = invCmd.getInsideContainerId();
//                if (null != icId) {
//                    insideContainerIds.add(icId);
//                }
//                try {
//                    uuid = SkuInventoryUuid.invUuid(inv);
//                    inv.setUuid(uuid);// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                WhSkuInventory oldSkuInv = null;
//                if(!uuid.equals(invCmd.getUuid())){
//                    oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(ouId, uuid);
//                }
//                if (null == oldSkuInv) {
//                    // uuid不存在则插入新的库存记录
//                    inv.setLastModifyTime(new Date());
//                    whSkuInventoryDao.insert(inv);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), inv.getOnHandQty(), 0.0, true, ouId, userId);
//                } else {
//                    // uuid已存在则合并库存
//                    WhSkuInventory newSkuInv = new WhSkuInventory();
//                    BeanUtils.copyProperties(oldSkuInv, newSkuInv);
//                    newSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + inv.getOnHandQty());
//                    newSkuInv.setLastModifyTime(new Date());
//                    int count = whSkuInventoryDao.saveOrUpdateByVersion(newSkuInv);
//                    if (count == 0) {
//                        log.error("update inventory error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), newSkuInv.getOnHandQty(), oldSkuInv.getOnHandQty(), true, ouId, userId);
//                }
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    sn.setId(null);
//                    sn.setOccupationCode(null);
//                    try {
//                        sn.setUuid(SkuInventoryUuid.invUuid(inv));
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
//                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                    }
//                    whSkuInventorySnDao.insert(sn);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                }
//            }
//        }
//        // 再出待移入容器库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            // 记录出容器库存日志
//            // 记录容器移出日志
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getToBeFilledQty(), 0.0, true, ouId, userId);
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            } else {
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getToBeFilledQty(), 0.0, true, ouId, userId);
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    whSkuInventorySnDao.delete(sn.getId());
//                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                }
//            }
//        }
        whSkuInventoryManager.execPutaway(containerCmd, null, locationCode, funcId, warehouse, WhPutawayPatternDetailType.PALLET_PUTAWAY, ouId, userId, logId);
        // 2.清除redis缓存
        pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(containerCmd, logId);
    }

    /**
     * @author lichuan
     * @param containerCode
     * @param locationCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void sysGuideContainerPutaway(String containerCode, String insideContainerCode, Boolean isAfterPutawayTipContainer, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        // 0.修改外部容器状态
        ContainerCommand containerCmd = null;
//        Long containerId = null;
        if (!StringUtils.isEmpty(containerCode)) {
            containerCmd = containerDao.getContainerByCode(containerCode, ouId);
            if (null == containerCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = containerCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
//            containerId = containerCmd.getId();
//            if (false == isAfterPutawayTipContainer) {
//                // 所有容器已上架完毕，修改外部容器状态
//                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
//                    Container container = new Container();
//                    BeanUtils.copyProperties(containerCmd, container);
//                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
//                    container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
//                    containerDao.saveOrUpdateByVersion(container);
//                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//                }
//            }
        }
        // 1.判断内部容器状态
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if (null == insideContainerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!BaseModel.LIFECYCLE_NORMAL.equals(insideContainerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != insideContainerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer insideContainerStatus = insideContainerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != insideContainerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != insideContainerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", insideContainerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {insideContainerCode});
        }
        // 2.执行上架
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
//        boolean isTV = true;// 是否跟踪容器
//        boolean isBM = true;// 是否批次管理
//        boolean isVM = true;// 是否管理效期
//        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
//        Location loc = locationDao.findLocationByCode(locationCode, ouId);
//        if (null == loc) {
//            log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//        }
//        isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
//        isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
//        isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
//        List<WhSkuInventoryCommand> invList = null;
//        List<String> codelist = new ArrayList<String>();
//        codelist.add(insideContainerCode);
//        // 查询所有对应容器号的库存信息
//        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCode(ouId, codelist);
//        if (null == invList || 0 == invList.size()) {
//            log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
//        }
//        // 2.执行上架(一入一出)
//        // 先入库位库存
//        Set<Long> locIds = new HashSet<Long>();
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            String uuid = "";
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
//                inv.setToBeFilledQty(0.0);
//                if (!StringUtils.isEmpty(containerCode)) {
//                    if (0 != containerId.compareTo(inv.getOuterContainerId())) {
//                        log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
//                        throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
//                    }
//                }
//                if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
//                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
//                } else {
//                    locIds.add(inv.getLocationId());
//                }
//                if (locIds.size() > 1) {
//                    log.error("binding location is more than one error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
//                }
//                if (false == isTV) {
//                    inv.setOuterContainerId(null);
//                    inv.setInsideContainerId(null);
//                }
//                if (false == isBM) {
//                    inv.setBatchNumber(null);
//                }
//                if (false == isVM) {
//                    inv.setExpDate(null);
//                }
//                inv.setOccupationCode(null);
//                Long icId = invCmd.getInsideContainerId();
//                if (null != icId) {
//                    insideContainerIds.add(icId);
//                }
//                try {
//                    uuid = SkuInventoryUuid.invUuid(inv);
//                    inv.setUuid(uuid);// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                WhSkuInventory oldSkuInv = null;
//                if(!uuid.equals(invCmd.getUuid())){
//                    oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(ouId, uuid);
//                }
//                if (null == oldSkuInv) {
//                    // uuid不存在则插入新的库存记录
//                    inv.setLastModifyTime(new Date());
//                    whSkuInventoryDao.insert(inv);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), inv.getOnHandQty(), 0.0, true, ouId, userId);
//                } else {
//                    // uuid已存在则合并库存
//                    WhSkuInventory newSkuInv = new WhSkuInventory();
//                    BeanUtils.copyProperties(oldSkuInv, newSkuInv);
//                    newSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + inv.getOnHandQty());
//                    newSkuInv.setLastModifyTime(new Date());
//                    int count = whSkuInventoryDao.saveOrUpdateByVersion(newSkuInv);
//                    if (count == 0) {
//                        log.error("update inventory error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), newSkuInv.getOnHandQty(), oldSkuInv.getOnHandQty(), true, ouId, userId);
//                }
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
//                inv.setToBeFilledQty(0.0);
//                if (!StringUtils.isEmpty(containerCode)) {
//                    if (0 != containerId.compareTo(inv.getOuterContainerId())) {
//                        log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
//                        throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
//                    }
//                }
//                if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
//                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
//                } else {
//                    locIds.add(inv.getLocationId());
//                }
//                if (locIds.size() > 1) {
//                    log.error("binding location is more than one error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
//                }
//                if (false == isTV) {
//                    inv.setOuterContainerId(null);
//                    inv.setInsideContainerId(null);
//                }
//                if (false == isBM) {
//                    inv.setBatchNumber(null);
//                }
//                if (false == isVM) {
//                    inv.setExpDate(null);
//                }
//                inv.setOccupationCode(null);
//                Long icId = invCmd.getInsideContainerId();
//                if (null != icId) {
//                    insideContainerIds.add(icId);
//                }
//                try {
//                    uuid = SkuInventoryUuid.invUuid(inv);
//                    inv.setUuid(uuid);// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                WhSkuInventory oldSkuInv = null;
//                if(!uuid.equals(invCmd.getUuid())){
//                    oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(ouId, uuid);
//                }
//                if (null == oldSkuInv) {
//                    // uuid不存在则插入新的库存记录
//                    inv.setLastModifyTime(new Date());
//                    whSkuInventoryDao.insert(inv);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), inv.getOnHandQty(), 0.0, true, ouId, userId);
//                } else {
//                    // uuid已存在则合并库存
//                    WhSkuInventory newSkuInv = new WhSkuInventory();
//                    BeanUtils.copyProperties(oldSkuInv, newSkuInv);
//                    newSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + inv.getOnHandQty());
//                    newSkuInv.setLastModifyTime(new Date());
//                    int count = whSkuInventoryDao.saveOrUpdateByVersion(newSkuInv);
//                    if (count == 0) {
//                        log.error("update inventory error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), newSkuInv.getOnHandQty(), oldSkuInv.getOnHandQty(), true, ouId, userId);
//                }
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    sn.setId(null);
//                    sn.setOccupationCode(null);
//                    try {
//                        sn.setUuid(SkuInventoryUuid.invUuid(inv));
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
//                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                    }
//                    whSkuInventorySnDao.insert(sn);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                }
//            }
//        }
//        // 再出待移入容器库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            // 记录出容器库存日志
//            // 记录容器移出日志
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getToBeFilledQty(), 0.0, true, ouId, userId);
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            } else {
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getToBeFilledQty(), 0.0, true, ouId, userId);
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    whSkuInventorySnDao.delete(sn.getId());
//                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                }
//            }
//        }
//        // 3.修改内部容器状态为可用
//        Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
//        if (null != insideContainer) {
//            // 获取容器状态
//            Integer iContainerStatus = insideContainer.getStatus();
//            // 修改内部容器状态为：上架中，且占用中
//            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
//                insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
//                insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
//                containerDao.saveOrUpdateByVersion(insideContainer);
//                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
//            }
//        }
        whSkuInventoryManager.execPutaway(containerCmd, insideContainerCmd, locationCode, funcId, warehouse, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
        // 3.清除redis缓存
        pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(containerCmd, insideContainerCmd, isAfterPutawayTipContainer, logId);
    }


    public void sysGuideSplitContainerPutaway(String containerCode, String insideContainerCode, Boolean isAfterPutawayTipContainer, Boolean isAfterPutawayTipLoc, String locationCode, Long funcId, Long ouId, Long userId, String logId) {
        // 0.修改外部容器状态
        ContainerCommand containerCmd = null;
//        Long containerId = null;
        if (!StringUtils.isEmpty(containerCode)) {
            containerCmd = containerDao.getContainerByCode(containerCode, ouId);
            if (null == containerCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = containerCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
//            containerId = containerCmd.getId();
//            if (false == isAfterPutawayTipContainer) {
//                // 所有容器已上架完毕，修改外部容器状态
//                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
//                    Container container = new Container();
//                    BeanUtils.copyProperties(containerCmd, container);
//                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
//                    container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
//                    containerDao.saveOrUpdateByVersion(container);
//                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//                }
//            }
        }
        // 1.判断内部容器状态
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if (null == insideContainerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!BaseModel.LIFECYCLE_NORMAL.equals(insideContainerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != insideContainerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer insideContainerStatus = insideContainerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != insideContainerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != insideContainerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", insideContainerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {insideContainerCode});
        }
        // 2.执行上架
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
        Location loc = locationDao.findLocationByCode(locationCode, ouId);
        if (null == loc) {
            log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
        }
//        boolean isTV = true;// 是否跟踪容器
//        boolean isBM = true;// 是否批次管理
//        boolean isVM = true;// 是否管理效期
//        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
//        isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
//        isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
//        isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
//        List<WhSkuInventoryCommand> invList = null;
//        List<String> codelist = new ArrayList<String>();
//        codelist.add(insideContainerCode);
//        // 查询所有对应容器号的库存信息
//        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCodeAndLoc(ouId, codelist, loc.getId());
//        if (null == invList || 0 == invList.size()) {
//            log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
//        }
//        // 2.执行上架(一入一出)
//        // 先入库位库存
//        Set<Long> locIds = new HashSet<Long>();
//        for (WhSkuInventoryCommand invCmd : invList) {
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            String uuid = "";
//            if (null == snList || 0 == snList.size()) {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
//                inv.setToBeFilledQty(0.0);
//                if (!StringUtils.isEmpty(containerCode)) {
//                    if (0 != containerId.compareTo(inv.getOuterContainerId())) {
//                        log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
//                        throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
//                    }
//                }
//                if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
//                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
//                } else {
//                    locIds.add(inv.getLocationId());
//                }
//                if (locIds.size() > 1) {
//                    log.error("binding location is more than one error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
//                }
//                if (false == isTV) {
//                    inv.setOuterContainerId(null);
//                    inv.setInsideContainerId(null);
//                }
//                if (false == isBM) {
//                    inv.setBatchNumber(null);
//                }
//                if (false == isVM) {
//                    inv.setExpDate(null);
//                }
//                inv.setOccupationCode(null);
//                Long icId = invCmd.getInsideContainerId();
//                if (null != icId) {
//                    insideContainerIds.add(icId);
//                }
//                try {
//                    uuid = SkuInventoryUuid.invUuid(inv);
//                    inv.setUuid(uuid);// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                WhSkuInventory oldSkuInv = null;
//                if(!uuid.equals(invCmd.getUuid())){
//                    oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(ouId, uuid);
//                }
//                if (null == oldSkuInv) {
//                    inv.setLastModifyTime(new Date());
//                    whSkuInventoryDao.insert(inv);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), inv.getOnHandQty(), 0.0, true, ouId, userId);
//                } else {
//                    WhSkuInventory newSkuInv = new WhSkuInventory();
//                    BeanUtils.copyProperties(oldSkuInv, newSkuInv);
//                    newSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + inv.getOnHandQty());
//                    newSkuInv.setLastModifyTime(new Date());
//                    int count = whSkuInventoryDao.saveOrUpdateByVersion(newSkuInv);
//                    if (count == 0) {
//                        log.error("update inventory error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), newSkuInv.getOnHandQty(), oldSkuInv.getOnHandQty(), true, ouId, userId);
//                }
//            } else {
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                inv.setId(null);
//                inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
//                inv.setToBeFilledQty(0.0);
//                if (!StringUtils.isEmpty(containerCode)) {
//                    if (0 != containerId.compareTo(inv.getOuterContainerId())) {
//                        log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
//                        throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
//                    }
//                }
//                if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
//                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
//                } else {
//                    locIds.add(inv.getLocationId());
//                }
//                if (locIds.size() > 1) {
//                    log.error("binding location is more than one error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
//                }
//                if (false == isTV) {
//                    inv.setOuterContainerId(null);
//                    inv.setInsideContainerId(null);
//                }
//                if (false == isBM) {
//                    inv.setBatchNumber(null);
//                }
//                if (false == isVM) {
//                    inv.setExpDate(null);
//                }
//                inv.setOccupationCode(null);
//                Long icId = invCmd.getInsideContainerId();
//                if (null != icId) {
//                    insideContainerIds.add(icId);
//                }
//                try {
//                    uuid = SkuInventoryUuid.invUuid(inv);
//                    inv.setUuid(uuid);// UUID
//                } catch (Exception e) {
//                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                }
//                WhSkuInventory oldSkuInv = null;
//                if(!uuid.equals(invCmd.getUuid())){
//                    oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(ouId, uuid);
//                }
//                if (null == oldSkuInv) {
//                    // uuid不存在则插入新的库存记录
//                    inv.setLastModifyTime(new Date());
//                    whSkuInventoryDao.insert(inv);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), inv.getOnHandQty(), 0.0, true, ouId, userId);
//                } else {
//                    // uuid已存在则合并库存
//                    WhSkuInventory newSkuInv = new WhSkuInventory();
//                    BeanUtils.copyProperties(oldSkuInv, newSkuInv);
//                    newSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + inv.getOnHandQty());
//                    newSkuInv.setLastModifyTime(new Date());
//                    int count = whSkuInventoryDao.saveOrUpdateByVersion(newSkuInv);
//                    if (count == 0) {
//                        log.error("update inventory error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                    }
//                    // 记录待移入库位库存日志
//                    insertSkuInventoryLog(invCmd.getId(), newSkuInv.getOnHandQty(), oldSkuInv.getOnHandQty(), true, ouId, userId);
//                }
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    sn.setId(null);
//                    sn.setOccupationCode(null);
//                    try {
//                        sn.setUuid(SkuInventoryUuid.invUuid(inv));
//                    } catch (NoSuchAlgorithmException e) {
//                        log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
//                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//                    }
//                    whSkuInventorySnDao.insert(sn);
//                    insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                }
//            }
//        }
//        // 再出待移入容器库存
//        for (WhSkuInventoryCommand invCmd : invList) {
//            // 记录出容器库存日志
//            // 记录容器移出日志
//            List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//            if (null == snList || 0 == snList.size()) {
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getToBeFilledQty(), 0.0, true, ouId, userId);
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            } else {
//                insertSkuInventoryLog(invCmd.getId(), -invCmd.getToBeFilledQty(), 0.0, true, ouId, userId);
//                WhSkuInventory inv = new WhSkuInventory();
//                BeanUtils.copyProperties(invCmd, inv);
//                whSkuInventoryDao.delete(inv.getId());
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//                for (WhSkuInventorySnCommand snCmd : snList) {
//                    insertSkuInventorySnLog(snCmd.getUuid(), ouId);
//                    WhSkuInventorySn sn = new WhSkuInventorySn();
//                    BeanUtils.copyProperties(snCmd, sn);
//                    whSkuInventorySnDao.delete(sn.getId());
//                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                }
//            }
//        }
//        // 3.修改内部容器状态为可用
//        Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
//        if (null != insideContainer) {
//            // 获取容器状态
//            Integer iContainerStatus = insideContainer.getStatus();
//            // 修改内部容器状态为：上架中，且占用中
//            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
//                insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
//                insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
//                containerDao.saveOrUpdateByVersion(insideContainer);
//                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
//            }
//        }
        whSkuInventoryManager.execPutaway(containerCmd, insideContainerCmd, locationCode, funcId, warehouse, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, userId, logId);
        // 3.清除redis缓存
        pdaPutawayCacheManager.sysGuideSplitContainerPutawayRemoveAllCache(containerCmd, insideContainerCmd, isAfterPutawayTipContainer, isAfterPutawayTipLoc, loc.getId(), logId);
    }

    /**
     * @author lichuan
     * @param containerCode
     * @param locationCode
     * @param funcId
     * @param asnId
     * @param putawayPatternDetailType
     * @param caseMode
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void sysGuidePutaway(String containerCode, String locationCode, Long funcId, Integer putawayPatternDetailType, Integer caseMode, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuidePutaway start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!containerCmd.getLifecycle().equals(BaseModel.LIFECYCLE_NORMAL)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Long containerCate = containerCmd.getTwoLevelType();
        Container2ndCategory container2 = container2ndCategoryDao.findByIdExt(containerCate, ouId);
        if (null == container2) {
            log.error("container2ndCategory is null error, 2endCategoryId is:[{}], logId is:[{}]", containerCate, logId);
            throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
        }
        if (1 != container2.getLifecycle()) {
            log.error("container2ndCategory lifecycle is not normal error, containerId is:[{}], logId is:[{}]", container2.getId(), logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        String containerCateidTemp = containerCmd.getOneLevelType();
        Long containerCateId = Long.parseLong(containerCateidTemp);

        SysDictionary dic = sysDictionaryDao.findById(containerCateId);
        if (!WhContainerCategoryType.PALLET.equals(dic.getDicValue())) {
            log.error("container2ndCategory is not pallet error!, LogId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_IS_NOT_PALLET_ERROR);
        }
        List<String> cclist = new ArrayList<String>();
        cclist.add(containerCode);
        List<WhSkuInventoryCommand> invList = null;
        // 查询所有对应容器号的库存信息
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, cclist);
        if (null == invList) {
            log.error("container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        List<LocationCommand> locExistsList = whSkuInventoryDao.findWhSkuInventoryLocByOuterContainerCode(ouId, cclist);
        if (null != locExistsList && 0 < locExistsList.size()) {
            for (LocationCommand lc : locExistsList) {
                String locCode = lc.getCode();
                String asnCode = lc.getOccupationCode();
                if (StringUtils.isEmpty(locCode)) {
                    log.error("location not recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_NOT_RECOMMEND_ERROR);
                }
                if (StringUtils.isEmpty(asnCode)) {
                    log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                }
                WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                if (null == asn) {
                    log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                }
                if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus()) {
                    log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                }
                Long poId = asn.getPoId();
                WhPo po = whPoDao.findWhPoById(poId, ouId);
                if (null == po) {
                    log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.PO_NULL);
                }
                String poCode = po.getPoCode();
                if (PoAsnStatus.PO_RCVD != po.getStatus()) {
                    log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                }
            }
        }

        if (StringUtils.isEmpty(locationCode)) {

        }
        // 执行上架
        for (WhSkuInventoryCommand invCmd : invList) {
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            if (null == inv.getLocationId()) {
                throw new BusinessException(ErrorCodes.RCVD_INV_NOT_HAS_LOCATION_ERROR);
            }
            inv.setOccupationCode(null);
            inv.setOnHandQty(inv.getToBeFilledQty());// 在库
            inv.setToBeFilledQty(null);
            whSkuInventoryDao.saveOrUpdateByVersion(inv);
            // 生成库存日志

        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuidePutaway end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
    }

    /**
     * @author lichuan
     * @param containerCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public String sysGuideTipContainer(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        String tipContainerCode = "";
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway tip container start, containerCode is:[{}], putawayPatternDetailType is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", containerCode, putawayPatternDetailType, ouId, userId, logId);
        }
        // 0.判断容器状态
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        // 1.获取容器统计信息
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != containerCmd) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
            if (null == csrCmd) {
                if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                }
            }
        }
        if (null == csrCmd) {
            log.error("container statistic cache is error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
        Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
        Long tipContainerId = null;
        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            tipContainerId = pdaPutawayCacheManager.sysGuideContainerPutawayTipContainer(containerCmd, insideContainerIds, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            tipContainerId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer(containerCmd, insideContainerIds, logId);
        }
        tipContainerCode = insideContainerIdsCode.get(tipContainerId);
        if (StringUtils.isEmpty(tipContainerCode)) {
            log.error("sys guide putaway tip container is error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.TIP_NEXT_CONTAINER_IS_ERROR, new Object[] {containerCode});
        }
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway tip container end, containerCode is:[{}], putawayPatternDetailType is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], tipContainer is:[{}]", containerCode, putawayPatternDetailType, ouId, userId, logId,
                    tipContainerCode);
        }
        return tipContainerCode;
    }

    /**
     * @author lichuan
     * @param
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public int findCaselevelCartonNumsByOuterContainerCode(String containerCode, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findCaselevelCartonNumsByOuterContainerCode start, containerCode is:[{}], ouId is:[{}], logId is:[{}]", containerCode, ouId, logId);
        }
        int nums = whCartonDao.findCartonNumsByOuterContainerCode(containerCode, ouId);
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findCaselevelCartonNumsByOuterContainerCode end, containerCode is:[{}], ouId is:[{}], nums is:[{}], logId is:[{}]", containerCode, ouId, nums, logId);
        }
        return nums;
    }


    /**
     * @author lichuan
     * @param
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public int findNotCaselevelCartonNumsByOuterContainerCode(String containerCode, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findNotCaselevelCartonNumsByOuterContainerCode start, containerCode is:[{}], ouId is:[{}], logId is:[{}]", containerCode, ouId, logId);
        }
        int nums = whCartonDao.findNoneCartonNumsByOuterContainerCode(containerCode, ouId);
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findNotCaselevelCartonNumsByOuterContainerCode end, containerCode is:[{}], ouId is:[{}], nums is:[{}], logId is:[{}]", containerCode, ouId, nums, logId);
        }
        return nums;
    }
    
    /**
     * @author lichuan
     * @param containerCode
     * @param insideContainerCode
     * @param skuCmd
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public ScanResultCommand sysGuidePutawayCancel(String containerCode, String insideContainerCode, WhSkuCommand skuCmd, String locationCode, Long funcId, Integer putawayPatternDetailType, Integer cancelPattern, Long ouId, Long userId, String logId) {
        ScanResultCommand srCmd = new ScanResultCommand();
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            if (StringUtils.isEmpty(containerCode)) {
                log.error("containerCode is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
            }
            ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
            if (null == containerCmd) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = containerCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }
            if (CancelPattern.PUTAWAY_TIP_LOCATION_CANCEL == cancelPattern) {
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, null, skuCmd, loc.getId(), funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
                whSkuInventoryManager.execUnbinding(containerCmd, null, locationCode, putawayPatternDetailType, ouId, userId, logId);
            }
            if (CancelPattern.PUTAWAY_INSIDECONTAINER_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                srCmd.setTipLocationCode(locationCode);// 提示库位编码
                srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
                if (null != warehouse) {
                    if (true == warehouse.getIsInboundLocationBarcode()) {
                        srCmd.setValidateLocation(true);
                    } else {
                        srCmd.setValidateLocation(false);
                    }
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, loc.getId(), funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
            }
            if (CancelPattern.PUTAWAY_SKU_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, null, funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
            }
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            ContainerCommand containerCmd = null;
            if (!StringUtils.isEmpty(containerCode)) {
                containerCmd = containerDao.getContainerByCode(containerCode, ouId);
                if (null == containerCmd) {
                    // 容器信息不存在
                    log.error("container is not exists, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                }
                // 验证容器状态是否可用
                if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
                    log.error("container lifecycle is not normal, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                }
                // 获取容器状态
                Integer containerStatus = containerCmd.getStatus();
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                    log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
                }
            }
            if (CancelPattern.PUTAWAY_TIP_LOCATION_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd = null;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, loc.getId(), funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
                whSkuInventoryManager.execUnbinding(containerCmd, insideContainerCmd, locationCode, putawayPatternDetailType, ouId, userId, logId); 
            }
            if (CancelPattern.PUTAWAY_INSIDECONTAINER_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, null, funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
            }
            if (CancelPattern.PUTAWAY_SKU_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                srCmd.setTipLocationCode(locationCode);// 提示库位编码
                srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
                if (null != warehouse) {
                    if (true == warehouse.getIsInboundLocationBarcode()) {
                        srCmd.setValidateLocation(true);
                    } else {
                        srCmd.setValidateLocation(false);
                    }
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, null, funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
            }
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            ContainerCommand containerCmd = null;
            if (!StringUtils.isEmpty(containerCode)) {
                containerCmd = containerDao.getContainerByCode(containerCode, ouId);
                if (null == containerCmd) {
                    // 容器信息不存在
                    log.error("container is not exists, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                }
                // 验证容器状态是否可用
                if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
                    log.error("container lifecycle is not normal, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                }
                // 获取容器状态
                Integer containerStatus = containerCmd.getStatus();
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                    log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
                }
            }
            if (CancelPattern.PUTAWAY_INSIDECONTAINER_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, null, funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
            }
            if (CancelPattern.PUTAWAY_TIP_LOCATION_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, loc.getId(), funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
                whSkuInventoryManager.execUnbinding(containerCmd, insideContainerCmd, locationCode, putawayPatternDetailType, ouId, userId, logId);
            }
            if (CancelPattern.PUTAWAY_SKU_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    insideContainerCmd = null;
                }
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
                srCmd.setTipLocationCode(locationCode);// 提示库位编码
                srCmd.setTipLocBarCode(loc.getBarCode());// 库位条码
                if (null != warehouse) {
                    if (true == warehouse.getIsInboundLocationBarcode()) {
                        srCmd.setValidateLocation(true);
                    } else {
                        srCmd.setValidateLocation(false);
                    }
                }
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, skuCmd, loc.getId(), funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
            }
            if (CancelPattern.PUTAWAY_SKU_SN_CANCEL == cancelPattern) {
                ContainerCommand insideContainerCmd;
                if (!StringUtils.isEmpty(insideContainerCode)) {
                    insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                    if (null == insideContainerCmd) {
                        // 内部容器信息不存在
                        log.error("inside container is not exists, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    // 内部容器信息不存在
                    log.error("inside container is not exists, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                Location loc = locationDao.findLocationByCode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }

                InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCmd.getId().toString());
                if (null == isCmd) {
                    isCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(insideContainerCmd, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, logId);
                }
                Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
                Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();
                Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(insideContainerCmd.getId());
                Map<Long, Map<Long, Map<String, Long>>> insideContainerLocSkuAttrIdsQty = isCmd.getInsideContainerLocSkuAttrIdsQty();
                Map<Long, Map<String, Long>> locSkuAttrIdsQty = insideContainerLocSkuAttrIdsQty.get(insideContainerCmd.getId());
                Map<String, Long> skuAttrIdsQty = locSkuAttrIdsQty.get(loc.getId());
                WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
                if (null == putawyaFunc) {
                    log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
                }
                Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;

                // 商品校验
                Long sId = null;
                Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuCmd.getBarCode(), logId);
                Set<Long> icSkuIds = insideContainerSkuIds.get(insideContainerCmd.getId());
                boolean isSkuExists = false;
                for (Long cacheId : cacheSkuIdsQty.keySet()) {
                    if (icSkuIds.contains(cacheId)) {
                        isSkuExists = true;
                    }
                    if (true == isSkuExists) {
                        sId = cacheId;
                        break;
                    }
                }
                if (false == isSkuExists) {
                    log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", (null != containerCmd ? containerCmd.getId() : ""), insideContainerCmd.getId(), sId, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
                }
                skuCmd.setId(sId);
                SkuRedisCommand cacheSkuCmd = skuRedisManager.findSkuMasterBySkuId(sId, ouId, logId);
                if (null == cacheSkuCmd) {
                    log.error("sku is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
                Sku s = cacheSkuCmd.getSku();
                WhSkuCommand sku = new WhSkuCommand();
                BeanUtils.copyProperties(s, sku);
                sku.setId(skuCmd.getId());
                sku.setIsNeedTipSkuDetail(null == skuCmd.getIsNeedTipSkuDetail() ? false : skuCmd.getIsNeedTipSkuDetail());
                sku.setIsNeedTipSkuSn(null == skuCmd.getIsNeedTipSkuSn() ? false : skuCmd.getIsNeedTipSkuSn());
                sku.setIsNeedTipSkuDefect(null == skuCmd.getIsNeedTipSkuDefect() ? false : skuCmd.getIsNeedTipSkuDefect());
                sku.setInvType(StringUtils.isEmpty(skuCmd.getInvType()) ? "" : skuCmd.getInvType());
                sku.setInvStatus(StringUtils.isEmpty(skuCmd.getInvStatus()) ? "" : skuCmd.getInvStatus());
                sku.setInvBatchNumber(StringUtils.isEmpty(skuCmd.getInvBatchNumber()) ? "" : skuCmd.getInvBatchNumber());
                sku.setInvCountryOfOrigin(StringUtils.isEmpty(skuCmd.getInvCountryOfOrigin()) ? "" : skuCmd.getInvCountryOfOrigin());
                sku.setInvMfgDate(StringUtils.isEmpty(skuCmd.getInvMfgDate()) ? "" : skuCmd.getInvMfgDate());
                sku.setInvExpDate(StringUtils.isEmpty(skuCmd.getInvExpDate()) ? "" : skuCmd.getInvExpDate());
                sku.setInvAttr1(StringUtils.isEmpty(skuCmd.getInvAttr1()) ? "" : skuCmd.getInvAttr1());
                sku.setInvAttr2(StringUtils.isEmpty(skuCmd.getInvAttr2()) ? "" : skuCmd.getInvAttr2());
                sku.setInvAttr3(StringUtils.isEmpty(skuCmd.getInvAttr3()) ? "" : skuCmd.getInvAttr3());
                sku.setInvAttr4(StringUtils.isEmpty(skuCmd.getInvAttr4()) ? "" : skuCmd.getInvAttr4());
                sku.setInvAttr5(StringUtils.isEmpty(skuCmd.getInvAttr5()) ? "" : skuCmd.getInvAttr5());
                sku.setSkuSn(StringUtils.isEmpty(skuCmd.getSkuSn()) ? "" : skuCmd.getSkuSn());
                sku.setSkuDefect(StringUtils.isEmpty(skuCmd.getSkuDefect()) ? "" : skuCmd.getSkuDefect());
                pdaPutawayCacheManager.sysGuidePutawayCancel(containerCmd, insideContainerCmd, sku, loc.getId(), funcId, putawayPatternDetailType, cancelPattern, ouId, userId, logId);
                // 提示下一个商品
                TipScanSkuCacheCommand tipSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + insideContainerCmd.toString() + loc.getId().toString());
                ArrayDeque<String> tipSkuAttrIds = null;
                if (null != tipSkuCmd) {
                    tipSkuAttrIds = tipSkuCmd.getScanSkuAttrIds();
                }
                Set<String> skuAttrIds = locSkuAttrIds.get(loc.getId());
                String tipSkuAttrId = "";
                boolean isAllCache = isCacheAllExists2(skuAttrIds, tipSkuAttrIds);
                if (false == isAllCache) {
                    // 提示下个商品
                    for (String saId : skuAttrIds) {
                        Set<String> tempSkuAttrIds = new HashSet<String>();
                        tempSkuAttrIds.add(saId);
                        boolean isExists = isCacheAllExists2(tempSkuAttrIds, tipSkuAttrIds);
                        if (true == isExists) {
                            continue;
                        } else {
                            tipSkuAttrId = saId;
                            break;
                        }
                    }
                }
                srCmd.setScanPattern(scanPattern);
                srCmd.setNeedTipSku(true);
                Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
                SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
                if (null == cacheSku) {
                    log.error("sku is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
                Sku tipSku = cacheSku.getSku();
                tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
                srCmd.setTipSkuBarcode(tipSku.getBarCode());
                pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSku(insideContainerCmd, loc.getId(), locSkuAttrIds, tipSkuAttrId, logId);
            }
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        return srCmd;
    }
    
    private boolean isCacheAllExists2(Set<String> ids, ArrayDeque<String> cacheKeys) {
        boolean allExists = true;
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                Iterator<String> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    String value = iter.next();
                    if (null == value) value = "-1";
                    if (value.equals(cId)) {
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    allExists = false;
                }
            }
        } else {
            allExists = false;
        }
        return allExists;
    }

}
