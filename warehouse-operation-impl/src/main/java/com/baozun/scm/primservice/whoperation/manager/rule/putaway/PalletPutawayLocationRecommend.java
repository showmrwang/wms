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
package com.baozun.scm.primservice.whoperation.manager.rule.putaway;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationInvVolumeWeightCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendShelveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.InvAttrMgmtType;
import com.baozun.scm.primservice.whoperation.constant.WhLocationRecommendType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.LocationTempletDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RecommendShelveDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationInvVolumeWieghtManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

/**
 * @author lichuan
 *
 */
@Service("palletPutawayLocationRecommend")
@Transactional
public class PalletPutawayLocationRecommend extends BasePutawayLocationRecommend implements PutawayLocationRecommend {
    protected static final Logger log = LoggerFactory.getLogger(PalletPutawayLocationRecommend.class);

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private RecommendShelveDao recommendShelveDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private PutawayConditionFactory putawayConditionFactory;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @SuppressWarnings("unused")
    @Autowired
    private LocationTempletDao locationTempletDao;
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;
    @Autowired
    private WhLocationInvVolumeWieghtManager whLocationInvVolumeWieghtManager;
    @SuppressWarnings("unused")
    @Autowired
    private WhSkuDao whSkuDao;

    /**
     * 整托上架库位推荐
     * 
     * @author lichuan
     * @param ruleAffer
     * @param export
     * @param caMap
     * @param invList
     * @param uomMap
     * @param logId
     * @return
     */
    @Override
    public List<LocationRecommendResultCommand> recommendLocation(RuleAfferCommand ruleAffer, RuleExportCommand export, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList, Map<String, Map<String, Double>> uomMap, String logId) {
        // 判断该容器是否有符合的上架规则
        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        List<LocationRecommendResultCommand> list = new ArrayList<LocationRecommendResultCommand>();
        // Long funcId = ruleAffer.getFuncId();
        Long ouId = ruleAffer.getOuid();
        Long outerContainerId = ruleAffer.getContainerId();
        // List<Long> storeIds = (null == ruleAffer.getStoreIdList() ? new ArrayList<Long>() :
        // ruleAffer.getStoreIdList());
        // 获取外部容器辅助信息
        ContainerAssist outerContainerAssist = caMap.get(outerContainerId);// 获取外部容器辅助信息
        if (null == outerContainerAssist) {
            log.error("container assist info is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        String containerCode = ruleAffer.getAfferContainerCode();
        Container outerContainer = containerDao.findByIdExt(outerContainerId, ouId);
        if (null == outerContainer) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != outerContainer.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = outerContainer.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        Long containerCate = outerContainer.getTwoLevelType();
        Container2ndCategory container2 = container2ndCategoryDao.findByIdExt(containerCate, ouId);
        if (null == container2) {
            log.error("container2ndCategory is null error, 2endCategoryId is:[{}], logId is:[{}]", containerCate, logId);
            throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
        }
        if (1 != container2.getLifecycle()) {
            log.error("container2ndCategory lifecycle is not normal error, containerId is:[{}], logId is:[{}]", container2.getId(), logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Double length = outerContainerAssist.getSysLength();// 长（系统基准）
        Double width = outerContainerAssist.getSysWidth();// 宽（系统基准）
        Double height = outerContainerAssist.getSysHeight();// 高（系统基准）
        // Double volume = outerContainerAssist.getSysVolume();// 体积（系统基准）
        Double weight = outerContainerAssist.getSysWeight();// 重量（系统基准）
        Long skuCategory = outerContainerAssist.getSkuCategory();// sku种类数
        Long skuAttrCategory = outerContainerAssist.getSkuAttrCategory();// 唯一sku数
        // Long skuQty = outerContainerAssist.getSkuQty();// sku总件数
        // Long storeQty = outerContainerAssist.getStoreQty();// 店铺数
        Map<String, Double> lenUomConversionRate = uomMap.get(WhUomType.LENGTH_UOM);
        Map<String, Double> weightUomConversionRate = uomMap.get(WhUomType.WEIGHT_UOM);
        for (ShelveRecommendRuleCommand rule : ruleList) {
            Long ruleId = rule.getId();
            List<RecommendShelveCommand> rsList = recommendShelveDao.findCommandByRuleIdOrderByPriority(ruleId, ouId);
            if (null == rsList || 0 == rsList.size()) {
                continue;// 继续遍历剩下规则
            }
            for (RecommendShelveCommand rs : rsList) {
                RecommendShelveCommand crs = rs;
                if (null == crs) continue;
                // 上架库区
                Long whAreaId = crs.getShelveAreaId();
                // 库位推荐规则
                String locationRecommendRule = crs.getLocationRule();
                Area area = areaDao.findByIdExt(whAreaId, ouId);
                if (null == area || 1 != area.getLifecycle()) {
                    continue;// 库区不存在或不可用，则当前库区不能推荐
                }
                String cSql = "";
                AttrParams attrParams = new AttrParams();
                attrParams.setOuId(ouId);
                List<LocationCommand> avaliableLocs = null;
                if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                    attrParams.setLrt(WhLocationRecommendType.EMPTY_LOCATION);
                    attrParams.setSkuCategory(skuCategory);
                    attrParams.setSkuAttrCategory(skuAttrCategory);
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                    if (null != putawayCondition) {
                        cSql = putawayCondition.getCondition(attrParams);
                    }
                    if (StringUtils.isEmpty(cSql)) {
                        cSql = null;
                    }
                    List<LocationCommand> aLocs = locationDao.findAllEmptyLocsByAreaId(area.getId(), ouId, cSql);
                    avaliableLocs = new ArrayList<LocationCommand>();
                    for (LocationCommand locCmd : aLocs) {
                        LocationCommand loc = locCmd;
                        if (null == loc) {
                            continue;
                        }
                        if (false == loc.getIsMixStacking()) {
                            if (1L == skuAttrCategory) {// 不允许混放只能是唯一sku
                                avaliableLocs.add(loc);
                            }
                        } else {
                            avaliableLocs.add(loc);
                        }
                    }
                } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
                    attrParams.setLrt(WhLocationRecommendType.STATIC_LOCATION);
                    attrParams.setContainerId(outerContainerId);
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                    if (null != putawayCondition) {
                        cSql = putawayCondition.getCondition(attrParams);
                    }
                    if (StringUtils.isEmpty(cSql)) {
                        cSql = null;
                    }
                    avaliableLocs = locationDao.findAllStaticLocsByAreaId(area.getId(), ouId, cSql);
                } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                    attrParams.setLrt(WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS);
                    if (1L != skuCategory.longValue()) {
                        // 商品不唯一,不考虑推荐库位
                        avaliableLocs = null;
                        continue;
                    }
                    WhSkuInventoryCommand invCmd = invList.get(0);// 取一条库存信息
                    attrParams.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                    // 解析库存关键属性
                    invAttrMgmtAspect(attrParams, invCmd);
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                    if (null != putawayCondition) {
                        cSql = putawayCondition.getCondition(attrParams);
                    }
                    if (StringUtils.isEmpty(cSql)) {
                        cSql = null;
                    }
                    avaliableLocs = locationDao.findAllInvLocsByAreaIdAndSameAttrs(area.getId(), ouId, cSql);
                } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                    attrParams.setLrt(WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS);
                    if (1L != skuCategory.longValue()) {
                        // 商品种类不唯一，不考虑推荐库位
                        avaliableLocs = null;
                        continue;
                    }
                    attrParams.setIsMixStacking(true);// 库位允许混放
                    attrParams.setSkuCategory(skuCategory);
                    attrParams.setSkuAttrCategory(skuAttrCategory);
                    WhSkuInventoryCommand invCmd = invList.get(0);// 取一条库存信息
                    attrParams.setInvAttrMgmt("");
                    // 解析库存关键属性
                    invAttrMgmtAspect(attrParams, invCmd);
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                    if (null != putawayCondition) {
                        cSql = putawayCondition.getCondition(attrParams);
                    }
                    if (StringUtils.isEmpty(cSql)) {
                        cSql = null;
                    }
                    avaliableLocs = locationDao.findAllInvLocsByAreaIdAndDiffAttrs(area.getId(), ouId, cSql);
                } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
                    // attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
                    // PutawayCondition putawayCondition =
                    // putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY,
                    // WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                    // if (null != putawayCondition) {
                    // cSql = putawayCondition.getCondition(attrParams);
                    // }
                    // if (StringUtils.isEmpty(cSql)) {
                    // cSql = null;
                    // }
                    // avaliableLocs = locationDao.findAllAvailableLocsByAreaId(area.getId(), ouId,
                    // cSql);
                } else {
                    avaliableLocs = null;
                }
                if (null == avaliableLocs || 0 == avaliableLocs.size()) {
                    continue;// 如果没有可用的库位，则遍历下一个上架规则
                }
                for (LocationCommand al : avaliableLocs) {
                    Long locId = al.getId();
                    Integer palletCount = al.getTrayCount();
                    // String templetCode = al.getTempletCode();
                    // LocationTemplet locTemplet =
                    // locationTempletDao.findLocationTempletByCodeAndOuId(templetCode, ouId);
                    Double locLength = al.getLength();
                    Double locHeight = al.getHigh();
                    Double locWidth = al.getWidth();
                    Double locWeight = al.getWeight();
                    Double locVolumeRate = (null == al.getVolumeRate() ? 1.0 : al.getVolumeRate());
                    if (null == locLength || null == locLength || null == locLength) {
                        log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                        throw new BusinessException(ErrorCodes.LOCATION_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                    }
                    if (null == locWeight) {
                        log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                        throw new BusinessException(ErrorCodes.LOCATION_WEIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                    }
                    // Double volumeRate = al.getVolumeRate();
                    if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                        // 判断当前空库位是否为静态库位
                        Boolean isStatic = al.getIsStatic();
                        if (null != isStatic && true == isStatic) {
                            int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                            if (count > 0) {
                                // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                continue;
                            }
                        }
                        if (null != palletCount) {
                            if (palletCount >= 1) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        } else {
                            // 计算体积
                            SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                            calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
                            boolean cubageAvailable = calc.calculateAvailable();
                            // 计算重量
                            SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                            weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
                            boolean weightAvailable = weightCal.calculateAvailable();
                            if (cubageAvailable & weightAvailable) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        }
                    } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
                        int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                        if (count > 0) {
                            // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                            continue;
                        }
                        // 当静态库位只绑定一个商品并且不允许混放，校验库存属性
                        int allCount = whSkuLocationDao.findAllSkuCountInSkuLocation(ouId, locId);
                        if (1 == allCount && 1L == skuCategory.longValue() && 1L == skuAttrCategory.longValue()) {
                            Boolean isMixStack = al.getIsMixStacking();
                            if (null != isMixStack && false == isMixStack) {
                                // 不允许混放则库存属性要保持一致
                                WhSkuInventoryCommand invCmd = invList.get(0);// 取一条库存信息
                                AttrParams invAttr1 = new AttrParams();
                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                // 解析库存关键属性
                                invAttrMgmtAspect(invAttr1, invCmd);
                                WhSkuInventoryCommand invCmd2 = whSkuInventoryDao.findFirstWhSkuInvCmdByLocation(ouId, locId);
                                AttrParams invAttr2 = new AttrParams();
                                invAttr2.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                if (null != invCmd2) {
                                    // 解析库存关键属性
                                    invAttrMgmtAspect(invAttr2, invCmd2);
                                    String invAttr1InvType = (null == invAttr1.getInvType() ? "" : invAttr1.getInvType());
                                    String invAttr1InvStatus = (null == invAttr1.getInvStatus() ? "" : invAttr1.getInvStatus().toString());
                                    String inv1BatchNumber = (null == invAttr1.getBatchNumber() ? "" : invAttr1.getBatchNumber());
                                    String inv1CountryOfOrigin = (null == invAttr1.getCountryOfOrigin() ? "" : invAttr1.getCountryOfOrigin());
                                    String inv1MfgDate = (null == invAttr1.getMfgDate() ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invAttr1.getMfgDate()));
                                    String inv1ExpDate = (null == invAttr1.getExpDate() ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invAttr1.getExpDate()));
                                    String inv1InvAttr1 = (null == invAttr1.getInvAttr1() ? "" : invAttr1.getInvAttr1());
                                    String inv1InvAttr2 = (null == invAttr1.getInvAttr2() ? "" : invAttr1.getInvAttr2());
                                    String inv1InvAttr3 = (null == invAttr1.getInvAttr3() ? "" : invAttr1.getInvAttr3());
                                    String inv1InvAttr4 = (null == invAttr1.getInvAttr4() ? "" : invAttr1.getInvAttr4());
                                    String inv1InvAttr5 = (null == invAttr1.getInvAttr5() ? "" : invAttr1.getInvAttr5());
                                    String invAttr2InvType = (null == invAttr2.getInvType() ? "" : invAttr2.getInvType());
                                    String invAttr2InvStatus = (null == invAttr2.getInvStatus() ? "" : invAttr2.getInvStatus().toString());
                                    String inv2BatchNumber = (null == invAttr2.getBatchNumber() ? "" : invAttr2.getBatchNumber());
                                    String inv2CountryOfOrigin = (null == invAttr2.getCountryOfOrigin() ? "" : invAttr2.getCountryOfOrigin());
                                    String inv2MfgDate = (null == invAttr2.getMfgDate() ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invAttr2.getMfgDate()));
                                    String inv2ExpDate = (null == invAttr2.getExpDate() ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(invAttr2.getExpDate()));
                                    String inv2InvAttr1 = (null == invAttr2.getInvAttr1() ? "" : invAttr2.getInvAttr1());
                                    String inv2InvAttr2 = (null == invAttr2.getInvAttr2() ? "" : invAttr2.getInvAttr2());
                                    String inv2InvAttr3 = (null == invAttr2.getInvAttr3() ? "" : invAttr2.getInvAttr3());
                                    String inv2InvAttr4 = (null == invAttr2.getInvAttr4() ? "" : invAttr2.getInvAttr4());
                                    String inv2InvAttr5 = (null == invAttr2.getInvAttr5() ? "" : invAttr2.getInvAttr5());
                                    if (!(invAttr1InvType.equals(invAttr2InvType) && invAttr1InvStatus.equals(invAttr2InvStatus) && inv1BatchNumber.equals(inv2BatchNumber) && inv1CountryOfOrigin.equals(inv2CountryOfOrigin)
                                            && inv1MfgDate.equals(inv2MfgDate) && inv1ExpDate.equals(inv2ExpDate) && inv1InvAttr1.equals(inv2InvAttr1) && inv1InvAttr2.equals(inv2InvAttr2) && inv1InvAttr3.equals(inv2InvAttr3)
                                            && inv1InvAttr4.equals(inv2InvAttr4) && inv1InvAttr5.equals(inv2InvAttr5))) {
                                        continue;
                                    }
                                }
                            }
                        }
                        if (null != palletCount) {
                            int pCount = whSkuLocationDao.findPalletCountByLocation(ouId, locId);
                            if (palletCount >= (pCount + 1)) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        } else {
                            LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                            Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                            Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                            // 计算体积
                            SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                            calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
                            calc.addStuffVolume(livwVolume);
                            boolean cubageAvailable = calc.calculateAvailable();
                            // 计算重量
                            SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                            weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
                            weightCal.addStuffWeight(livwWeight);
                            boolean weightAvailable = weightCal.calculateAvailable();
                            if (cubageAvailable & weightAvailable) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        }
                    } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                        Boolean isStatic = al.getIsStatic();
                        if (null != isStatic && true == isStatic) {
                            int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                            if (count > 0) {
                                // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                continue;
                            }
                        }
                        if (null != palletCount) {
                            int pCount = whSkuLocationDao.findPalletCountByLocation(ouId, locId);
                            if (palletCount >= (pCount + 1)) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        } else {
                            LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                            Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                            Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                            // 计算体积
                            SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                            calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
                            calc.addStuffVolume(livwVolume);
                            boolean cubageAvailable = calc.calculateAvailable();
                            // 计算重量
                            SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                            weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
                            weightCal.addStuffWeight(livwWeight);
                            boolean weightAvailable = weightCal.calculateAvailable();
                            if (cubageAvailable & weightAvailable) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        }
                    } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                        Boolean isStatic = al.getIsStatic();
                        if (true == isStatic) {
                            int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                            if (count > 0) {
                                // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                continue;
                            }
                        }
                        if (null != palletCount) {
                            int pCount = whSkuLocationDao.findPalletCountByLocation(ouId, locId);
                            if (palletCount >= (pCount + 1)) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        } else {
                            LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                            Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                            Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                            // 计算体积
                            SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                            calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
                            calc.addStuffVolume(livwVolume);
                            boolean cubageAvailable = calc.calculateAvailable();
                            // 计算重量
                            SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                            weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
                            weightCal.addStuffWeight(livwWeight);
                            boolean weightAvailable = weightCal.calculateAvailable();
                            if (cubageAvailable & weightAvailable) {
                                LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                                lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                                lrrc.setLocationCode(al.getCode());
                                lrrc.setLocBarcode(al.getBarCode());
                                lrrc.setLocationId(al.getId());
                                lrrc.setOuterContainerCode(containerCode);
                                lrrc.setOuterContainerId(outerContainerId);
                                list.add(lrrc);
                            }
                        }
                    } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
                        // LocationInvVolumeWeightCommand livw =
                        // whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId,
                        // ouId, uomMap, logId);
                        // Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                        // Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                        // // 计算体积
                        // SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth,
                        // locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate,
                        // lenUomConversionRate);
                        // calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
                        // calc.addStuffVolume(livwVolume);
                        // boolean cubageAvailable = calc.calculateAvailable();
                        // // 计算重量
                        // SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight,
                        // SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                        // weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
                        // weightCal.addStuffWeight(livwWeight);
                        // boolean weightAvailable = weightCal.calculateAvailable();
                        // if (cubageAvailable & weightAvailable) {
                        // LocationRecommendResultCommand lrrc = new
                        // LocationRecommendResultCommand();
                        // lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                        // lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                        // lrrc.setLocationCode(al.getCode());
                        // lrrc.setLocationId(al.getId());
                        // lrrc.setOuterContainerCode(containerCode);
                        // lrrc.setOuterContainerId(outerContainerId);
                        // list.add(lrrc);
                        // }
                    } else {
                        break;
                    }

                    if (1 == list.size()) {
                        break;
                    }
                }
                if (1 == list.size()) {
                    break;
                }
            }
            if (1 == list.size()) {
                break;
            }
        }
        return list;
    }

}
