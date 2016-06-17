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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanContainerResultCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.WhContainerCategoryType;
import com.baozun.scm.primservice.whoperation.constant.WhContainerType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
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
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationRecommendManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
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
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private ContainerAssistDao containerAssistDao;
    @Autowired
    private WhLocationDao locationDao;

    /**
     * 系统指导上架扫托盘号
     * 
     * @author lichuan
     * @param containerCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public LocationCommand sysGuideScanPallet(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        LocationCommand locCmd = new LocationCommand();
        String locationCode = "";
        String asnCode = "";
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanPallet start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
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
        Long containerCateId = containerCmd.getOneLevelType();
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
        boolean isLoc = false;
        Set<String> locs = new HashSet<String>();
        if (null != locExistsList && 0 < locExistsList.size()) {
            for (LocationCommand lc : locExistsList) {
                if (!StringUtils.isEmpty(lc.getCode()) && false == isLoc) {
                    locationCode = lc.getCode();
                    isLoc = true;
                    locs.add(locationCode);
                } else {
                    locs.add(lc.getCode());
                }
                asnCode = lc.getOccupationCode();
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

        if (true == isLoc) {
            if (1 < locs.size()) {
                log.error("");
            }
            if (!StringUtils.isEmpty(locationCode)) {
                // 已经推荐过库位
                locCmd.setCode(locationCode);
                return locCmd;
            }
        }
        // 判断该容器是否有符合的上架规则
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(containerCode);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(cclist);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 推荐库位
        List<LocationCommand> locList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export.getShelveRecommendRuleList(), WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
        if (null == locList || 0 == locList.size()) {
            log.error("location recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
        }
        // 取到库位
        LocationCommand loc = locList.get(0);
        locationCode = loc.getCode();
        Long locationId = loc.getId();
        // 绑定库位
        for (WhSkuInventoryCommand invCmd : invList) {
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            if (null != inv.getLocationId()) {
                throw new BusinessException(ErrorCodes.CONTAINER_RCVD_INV_HAS_LOCATION_ERROR);
            }
            inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
            inv.setOnHandQty(null);
            inv.setLocationId(locationId);
            whSkuInventoryDao.saveOrUpdateByVersion(inv);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanPallet end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], locactionCode is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId, locationCode});
        }
        locCmd.setCode(locationCode);
        return locCmd;
    }

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
    public ScanContainerResultCommand sysGuideScanContainer(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        ScanContainerResultCommand scr = new ScanContainerResultCommand();
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
        if (!BaseModel.LIFECYCLE_NORMAL.equals(containerCmd.getLifecycle()) || ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != containerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus || ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            scr = sysGuidePalletPutawayScanContainer(containerCmd, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            scr = sysGuideContainerPutawayScanContainer(containerCmd, funcId, ouId, userId, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            scr = sysGuideSplitContainerPutawayScanContainer(containerCmd, funcId, ouId, userId, logId);
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanContainer end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        return scr;
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
    private ScanContainerResultCommand sysGuidePalletPutawayScanContainer(ContainerCommand containerCmd, Long funcId, Long ouId, Long userId, String logId) {
        ScanContainerResultCommand scr = new ScanContainerResultCommand();
        // 0.判断是否已经缓存所有库存信息

        Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
        Integer containerStaus = containerCmd.getStatus();
        // 1.判断是外部容器还是内部容器
        int count1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
        int count2 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, containerId);
        if (0 < count2) {
            // 整托上架只能扫外部容器号
            log.error("sys guide pallet putaway scan container is insideContainer error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_IS_INSIDE_ERROR_UNABLE_PUTAWAY);
        }
        if (0 < count1) {
            scr.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
            scr.setHasOuterContainer(true);// 有外部容器
        } else {
            // 无收货库存
            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        // 2.修改容器状态为：上架中，且占用中
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerStaus) {
            Container container = new Container();
            BeanUtils.copyProperties(containerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
            containerDao.saveOrUpdate(container);
            scr.setOuterContainerCode(containerCode);// 外部容器号
            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
        }
        // 3.获取所有库存信息并统计
        List<String> ocCodelist = new ArrayList<String>();
        ocCodelist.add(containerCode);
        List<WhSkuInventoryCommand> invList = null;
        // 查询所有对应容器号的库存信息
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        Long outerContainerId = containerCmd.getId();
        Double outerContainerWeight = 0.0;
        Double outerContainerVolume = 0.0;
        Set<Long> insideContainerIds = new HashSet<Long>();
        Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();
        Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();
        Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();
        Map<Long, Long> insideContainerSkuOnHandQty = new HashMap<Long, Long>();
        Map<Long, Set<Long>> insideContainerStoreIds = new HashMap<Long, Set<Long>>();
        Set<Long> caselevelContainerIds = new HashSet<Long>();
        Set<Long> notcaselevelContainerIds = new HashSet<Long>();
        Set<Long> skuIds = new HashSet<Long>();
        Long skuOnHandQty = 0L;
        // Long skuToBeFillQty = 0L;
        Set<Long> storeIds = new HashSet<Long>();
        Set<Long> locationIds = new HashSet<Long>();
        // Double totalSkuVolume = 0.0;
        // Double totalSkuWeight = 0.0;
        Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
        List<UomCommand> lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
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
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
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
        for (WhSkuInventoryCommand invCmd : invList) {
            Long icId = invCmd.getInsideContainerId();
            if (null != icId) {
                insideContainerIds.add(icId);
            }
            Container ic;
            if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
                log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) || ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer insideContainerStatus = ic.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != insideContainerStatus || ContainerStatus.CONTAINER_STATUS_PUTAWAY != insideContainerStatus) {
                log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, insideContainerStatus, logId);
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

            Long skuId = invCmd.getSkuId();
            Double onHandQty = invCmd.getOnHandQty();
            if (null != onHandQty) {
                skuOnHandQty += onHandQty.longValue();
            }
            // Double toBeFillQty = invCmd.getToBeFilledQty();
            // if (null != toBeFillQty) {
            // skuToBeFillQty += toBeFillQty.longValue();
            // }
            if (null != skuId) {
                skuIds.add(skuId);
                WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                if (null == skuCmd) {
                    log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
                // String skuLenUom = skuCmd.getLengthUom();
                Double skuLength = skuCmd.getLength();
                Double skuWidth = skuCmd.getWidth();
                Double skuHeight = skuCmd.getHeight();
                // String skuWeightUom = skuCmd.getWeightUom();
                Double skuWeight = skuCmd.getWeight();
                if (null == skuLength || null == skuWidth || null == skuHeight) {
                    log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                    throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                }
                if (null == skuWeight) {
                    log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                    throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                }
                // totalSkuVolume = cubeCalculator.accumulationStuffVolume(skuLength, skuWidth,
                // skuHeight);
                // totalSkuWeight = weightCalculator.accumulationStuffWeight(skuWeight);
                if (null != insideContainerWeight.get(icId)) {
                    insideContainerWeight.put(icId, insideContainerWeight.get(icId) + weightCalculator.accumulationStuffWeight(skuWeight));
                } else {
                    // 先计算容器自重
                    insideContainerWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
                    // 再计算当前商品重量
                    insideContainerWeight.put(icId, insideContainerWeight.get(icId) + weightCalculator.accumulationStuffWeight(skuWeight));
                }
            }
            Long locationId = invCmd.getLocationId();
            if (null != locationId) {
                locationIds.add(locationId);
            }
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
            if (null != insideContainerSkuOnHandQty.get(icId)) {
                insideContainerSkuOnHandQty.put(icId, insideContainerSkuOnHandQty.get(icId) + onHandQty.longValue());
            } else {
                insideContainerSkuOnHandQty.put(icId, onHandQty.longValue());
            }
            if (null != insideContainerStoreIds.get(icId)) {
                Set<Long> icStores = insideContainerSkuIds.get(icId);
                icStores.add(stroeId);
                insideContainerStoreIds.put(icId, icStores);
            } else {
                Set<Long> icStores = new HashSet<Long>();
                icStores.add(stroeId);
                insideContainerSkuIds.put(icId, icStores);
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
                insideContainerAsists.put(icId, containerAssist);
            }
        }
        // 4.判断是否已推荐库位
        if (0 < locationIds.size()) {
            scr.setRecommendLocation(true);// 已推荐库位
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
            scr.setTipLocationCode(loc.getCode());// 提示库位编码
            scr.setNeedTipLocation(true);// 提示库位
            return scr;
        }
        // 5.计算外部容器体积重量
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
        // 6.判断并修改内部容器状态
        List<String> icCodeList = new ArrayList<String>();
        if (0 == insideContainerIds.size()) {
            log.error("sys guide pallet putaway inside container is null error, invs is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RCVD_INV_NOT_HAS_INSIDE_CONTAINER_ERROR);
        } else {
            scr.setHasInsideContainer(true);
        }
        for (Long icId : insideContainerIds) {
            Container insideContainer = containerDao.findByIdExt(icId, ouId);
            if (null == insideContainer) {
                // 容器信息不存在
                log.error("container is not exists, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
            }
            // 验证容器状态是否可用
            if (!BaseModel.LIFECYCLE_NORMAL.equals(insideContainer.getLifecycle()) || ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != insideContainer.getLifecycle()) {
                log.error("container lifecycle is not normal, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            // 获取容器状态
            Integer containerStatus = insideContainer.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus || ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
            }

            // 修改内部容器状态为：上架中，且占用中
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerStaus) {
                insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                containerDao.saveOrUpdate(insideContainer);
                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
            }
            icCodeList.add(insideContainer.getCode());
        }
        // 7.删除并更新容器辅助表
        List<Long> deleteContainerIds = new ArrayList<Long>();
        Long[] dcIds = new Long[] {};
        CollectionUtils.addAll(insideContainerIds, dcIds);
        deleteContainerIds = Arrays.asList(dcIds);
        deleteContainerIds.add(outerContainerId);
        containerAssistDao.deleteByContainerIds(ouId, deleteContainerIds);
        // 内部容器辅助表信息
        Double icTotalWeight = 0.0;
        for (Long insideId : insideContainerIds) {
            ContainerAssist containerAssist = insideContainerAsists.get(insideId);
            icTotalWeight += insideContainerWeight.get(insideId);
            containerAssist.setSysWeight(insideContainerWeight.get(insideId));
            containerAssist.setSkuCategory(insideContainerSkuIds.get(insideId).size() + 0L);
            containerAssist.setSkuQty(insideContainerSkuOnHandQty.get(insideId));
            containerAssistDao.saveOrUpdate(containerAssist);
            insertGlobalLog(GLOBAL_LOG_INSERT, containerAssist, ouId, userId, null, null);
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
        containerAssist.setSkuQty(skuOnHandQty);
        containerAssist.setStoreQty(storeIds.size() + 0L);
        containerAssistDao.saveOrUpdate(containerAssist);
        insertGlobalLog(GLOBAL_LOG_INSERT, containerAssist, ouId, userId, null, null);
        // 8.匹配上架规则
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(containerCode);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则
        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        // 9.推荐库位
        List<LocationRecommendResultCommand> lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, WhPutawayPatternDetailType.PALLET_PUTAWAY, ruleList, logId);
        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
            log.error("location recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
        }
        LocationRecommendResultCommand lrr = lrrList.get(0);
        Long lrrLocId = lrr.getLocationId();
        String lrrLocCode = lrr.getLocationCode();
        scr.setRecommendLocation(true);// 已推荐库位
        scr.setTipLocationCode(lrrLocCode);// 提示库位编码
        scr.setNeedTipLocation(true);// 提示库位
        // 10.绑定库位(一入一出)
        //先待移入库位库存
        for (WhSkuInventoryCommand invCmd : invList) {
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            inv.setId(null);
            inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
            inv.setOnHandQty(null);
            inv.setLocationId(lrrLocId);
            whSkuInventoryDao.saveOrUpdateByVersion(inv);
            insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
            // 记录待移入库位库存日志
            // TODO
        }
        // 再出容器库存
        for (WhSkuInventoryCommand invCmd : invList) {
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            whSkuInventoryDao.delete(inv.getId());
            insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
            // 记录出容器库存日志
            // TODO
        }
        return scr;
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
    private ScanContainerResultCommand sysGuideContainerPutawayScanContainer(ContainerCommand containerCmd, Long funcId, Long ouId, Long userId, String logId) {
        ScanContainerResultCommand scr = new ScanContainerResultCommand();
        // 判断是否是外部容器

        // 修改容器状态为：上架中，且占用中
        Container container = new Container();
        BeanUtils.copyProperties(containerCmd, container);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
        containerDao.saveOrUpdate(container);
        return scr;
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
    private ScanContainerResultCommand sysGuideSplitContainerPutawayScanContainer(ContainerCommand containerCmd, Long funcId, Long ouId, Long userId, String logId) {
        ScanContainerResultCommand scr = new ScanContainerResultCommand();
        // 判断是否是外部容器

        // 修改容器状态为：上架中，且占用中
        Container container = new Container();
        BeanUtils.copyProperties(containerCmd, container);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
        containerDao.saveOrUpdate(container);
        return scr;
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
        Long containerCateId = containerCmd.getOneLevelType();
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
     * @param containerode
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
     * @param containerode
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

}
