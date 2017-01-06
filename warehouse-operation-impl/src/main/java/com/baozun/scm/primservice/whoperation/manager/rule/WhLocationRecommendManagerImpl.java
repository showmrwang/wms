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
package com.baozun.scm.primservice.whoperation.manager.rule;

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
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendShelveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
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
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.putaway.AttrParams;
import com.baozun.scm.primservice.whoperation.manager.rule.putaway.PutawayCondition;
import com.baozun.scm.primservice.whoperation.manager.rule.putaway.PutawayConditionFactory;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

/**
 * @author lichuan
 *
 */
@Service("whLocationRecommandManager")
@Transactional
public class WhLocationRecommendManagerImpl extends BaseManagerImpl implements WhLocationRecommendManager {
    protected static final Logger log = LoggerFactory.getLogger(WhLocationRecommendManagerImpl.class);

    @Autowired
    private StoreManager storeManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhFunctionPutAwayManager whFunctionPutAwayManager;
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
    @Autowired
    private SkuRedisManager skuRedisManager;

    /**
     * @author lichuan
     * @param ruleList
     * @return
     */
    @SuppressWarnings("unused")
    @Override
    public List<LocationCommand> recommendLocationByShevleRule(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, int putawayPatternDetail, String logId) {
        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule start, logId is:[{}]", logId);
        }
        List<LocationCommand> list = new ArrayList<LocationCommand>();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        Long funcId = ruleAffer.getFuncId();
        Long ouId = ruleAffer.getOuid();
        boolean isTV = false;// 是否跟踪容器
        boolean isBM = false;// 是否批次管理
        boolean isVM = false;// 是否管理效期
        boolean isMS = false;// 是否允许混放
        String containerCode = ruleAffer.getAfferContainerCode();
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            log.error("container is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        if (1 != containerCmd.getLifecycle()) {
            log.error("container lifecycle is not normal error, containerId is:[{}], logId is:[{}]", containerCmd.getId(), logId);
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
        Double length = container2.getLength();
        Double height = container2.getHigh();
        Double width = container2.getWidth();
        //String lenUom = container2.getLengthUom();
        Double weight = container2.getWeight();
        //String weightUom = container2.getWeightUom();
        List<WhSkuInventoryCommand> invList = null;
        WhFunctionPutAway putawayFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);;
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetail) {
            // 查询所有对应容器号的库存信息
            invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ruleAffer.getOuid(), ruleAffer.getAfferContainerCodeList());
            isTV = true;
        }

        if (null == invList) {
            log.error("container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }

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
                if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetail) {
                    AttrParams attrParams = new AttrParams();
                    attrParams.setIsTrackVessel(isTV);
                    List<LocationCommand> avaliableLocs = null;
                    if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                        attrParams.setLrt(WhLocationRecommendType.EMPTY_LOCATION);
                        PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                        if (null != putawayCondition) {
                            cSql = putawayCondition.getCondition(attrParams);
                        }
                        avaliableLocs = locationDao.findAllEmptyLocsByAreaId(area.getId(), ouId, cSql);
                    } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
                        attrParams.setLrt(WhLocationRecommendType.STATIC_LOCATION);
                        attrParams.setContainerId(containerCmd.getId());
                        PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                        if (null != putawayCondition) {
                            cSql = putawayCondition.getCondition(attrParams);
                        }
                        avaliableLocs = locationDao.findAllStaticLocsByAreaId(area.getId(), ouId, cSql);
                    } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                        // 不考虑
                        avaliableLocs = null;
                        continue;
                    } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                        // 不考虑
                        avaliableLocs = null;
                        continue;
                    } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
                        attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
                        PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                        if (null != putawayCondition) {
                            cSql = putawayCondition.getCondition(attrParams);
                        }
                        avaliableLocs = locationDao.findAllAvailableLocsByAreaId(area.getId(), ouId, cSql);
                    } else {
                        avaliableLocs = null;
                    }
                    if (null == avaliableLocs || 0 == avaliableLocs.size()) {
                        continue;// 如果没有可用的库位，则遍历下一个上架规则
                    }
                    for (LocationCommand al : avaliableLocs) {
                        Long locId = al.getId();
                        // String templetCode = al.getTempletCode();
                        // LocationTemplet locTemplet =
                        // locationTempletDao.findLocationTempletByCodeAndOuId(templetCode, ouId);
                        // Double locLength = locTemplet.getLength();
                        // Double locHeight = locTemplet.getHigh();
                        // Double locWidth = locTemplet.getWidth();
                        // String locLenUom = locTemplet.getLengthUom();
                        // Double locWeight = locTemplet.getWeight();
                        // String locWeightUom = locTemplet.getWeightUom();
                        if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                            // // 计算体积
                            // SimpleStandardCubeCalculator calc = new
                            // SimpleStandardCubeCalculator(locLength, locWidth, locHeight,
                            // locLenUom, 0.8);
                            // calc.initStuffCube(length, width, height, lenUom);
                            // boolean cubageAvailable = calc.calculateAvailable();
                            // // 计算重量
                            // SimpleStandardWeightCalculator weightCal = new
                            // SimpleStandardWeightCalculator(locWeight, locWeightUom);
                            // weightCal.initStuffWeight(weight, weightUom);
                            // boolean weightAvailable = weightCal.calculateAvailable();
                            // if (cubageAvailable & weightAvailable) {
                            // list.add(al);
                            // }
                        } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
                            int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                            if (count > 0) {
                                // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                continue;
                            }
                        } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                            break;
                        } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                            break;
                        } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
                            // // 计算体积
                            // SimpleStandardCubeCalculator calc = new
                            // SimpleStandardCubeCalculator(locLength, locWidth, locHeight,
                            // locLenUom, 0.8);
                            // calc.initStuffCube(length, width, height, lenUom);
                            // boolean cubageAvailable = calc.calculateAvailable();
                            // // 计算重量
                            // SimpleStandardWeightCalculator weightCal = new
                            // SimpleStandardWeightCalculator(locWeight, locWeightUom);
                            // weightCal.initStuffWeight(weight, weightUom);
                            // boolean weightAvailable = weightCal.calculateAvailable();
                            // if (cubageAvailable & weightAvailable) {
                            // list.add(al);
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
            }
            if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetail) {
                if (1 == list.size()) {
                    break;
                }
            }
        }

        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule end, logId is:[{}]", logId);
        }
        return list;
    }

    /**
     * @author lichuan
     * @param ruleAffer
     * @param
     * @param
     * @param logId
     * @return
     */
    @Override
    public List<LocationRecommendResultCommand> recommendLocationByShevleRule(RuleAfferCommand ruleAffer, RuleExportCommand export, int putawayPatternDetailType, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList,
            Map<String, Map<String, Double>> uomMap, String logId) {
        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule start, logId is:[{}]", logId);
        }
        List<LocationRecommendResultCommand> list = new ArrayList<LocationRecommendResultCommand>();
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            // 判断该容器是否有符合的上架规则
            List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
            if (null == ruleList || 0 == ruleList.size()) {
                log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
            }
            list = sysGuidePalletPutawayRecommendLocation(ruleAffer, ruleList, caMap, invList, uomMap, logId);
        } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 判断该容器是否有符合的上架规则
            List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
            if (null == ruleList || 0 == ruleList.size()) {
                log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
            }
            list = sysGuideContainerPutawayRecommendLocation(ruleAffer, ruleList, caMap, invList, uomMap, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 判断该容器是否有符合的上架规则
            List<WhSkuInventoryCommand> ruleList = export.getShelveSkuInvCommandList();
            if (null == ruleList || 0 == ruleList.size()) {
                log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
            }
            list = sysGuideSplitContainerPutawayRecommendLocation(ruleAffer, ruleList, caMap, invList, uomMap, logId);
        } else {
            log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule end, logId is:[{}]", logId);
        }
        return list;
    }

    private List<LocationRecommendResultCommand> sysGuidePalletPutawayRecommendLocation(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList,
            Map<String, Map<String, Double>> uomMap, String logId) {
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
                    /*attrParams.setIsMixStacking(false);// 库位不允许混放
                    String invAttrMgmt = "";
                    if (1 == storeIds.size()) {
                        // 获取店铺上配置的库存关键属性
                        Store store = storeManager.getStoreById(storeIds.get(0));
                        if (null == store) {
                            log.error("store is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
                        }
                        invAttrMgmt = store.getInvAttrMgmt();
                    }
                    if (StringUtils.isEmpty(invAttrMgmt)) {
                        // 获取仓库上配置的库存关键属性
                        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                        if (null == warehouse) {
                            log.error("warehouse is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
                        }
                        invAttrMgmt = warehouse.getInvAttrMgmt();
                    }
                    if(!StringUtils.isEmpty(invAttrMgmt)){
                        // 店铺或仓库管理库存关键属性
                        if(1L != skuAttrCategory.longValue()){
                            // 商品库存属性不唯一,不考虑推荐库位
                            avaliableLocs = null;
                            continue;
                        }
                    }*/
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
//                    attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
//                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
//                    if (null != putawayCondition) {
//                        cSql = putawayCondition.getCondition(attrParams);
//                    }
//                    if (StringUtils.isEmpty(cSql)) {
//                        cSql = null;
//                    }
//                    avaliableLocs = locationDao.findAllAvailableLocsByAreaId(area.getId(), ouId, cSql);
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
                            if (false == isMixStack) {
                                // 不允许混放则库存属性要保持一致
                                WhSkuInventoryCommand invCmd = invList.get(0);// 取一条库存信息
                                AttrParams invAttr1 = new AttrParams();
                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                // 解析库存关键属性
                                invAttrMgmtAspect(invAttr1, invCmd);
                                WhSkuInventoryCommand invCmd2 = whSkuInventoryDao.findFirstWhSkuInvCmdByLocation(ouId, locId);
                                AttrParams invAttr2 = new AttrParams();
                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                if (null != invCmd2) {
                                    // 解析库存关键属性
                                    invAttrMgmtAspect(invAttr2, invCmd2);
                                }
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
                                if (!(invAttr1InvType.equals(invAttr2InvType)) && invAttr1InvStatus.equals(invAttr2InvStatus) && inv1BatchNumber.equals(inv2BatchNumber) && inv1CountryOfOrigin.equals(inv2CountryOfOrigin) && inv1MfgDate.equals(inv2MfgDate)
                                        && inv1ExpDate.equals(inv2ExpDate) && inv1InvAttr1.equals(inv2InvAttr1) && inv1InvAttr2.equals(inv2InvAttr2) && inv1InvAttr3.equals(inv2InvAttr3) && inv1InvAttr4.equals(inv2InvAttr4)
                                        && inv1InvAttr5.equals(inv2InvAttr5)) {
                                    continue;
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
                        if (null != palletCount) {
                            Boolean isStatic = al.getIsStatic();
                            if (null != isStatic && true == isStatic) {
                                int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                                if (count > 0) {
                                    // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                    continue;
                                }
                            }
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
                            Boolean isStatic = al.getIsStatic();
                            if (true == isStatic) {
                                int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                                if (count > 0) {
                                    // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                    continue;
                                }
                            }
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
//                        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
//                        Double livwVolume = livw.getVolume();// 库位上已有货物总体积
//                        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
//                        // 计算体积
//                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
//                        calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
//                        calc.addStuffVolume(livwVolume);
//                        boolean cubageAvailable = calc.calculateAvailable();
//                        // 计算重量
//                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
//                        weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
//                        weightCal.addStuffWeight(livwWeight);
//                        boolean weightAvailable = weightCal.calculateAvailable();
//                        if (cubageAvailable & weightAvailable) {
//                            LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
//                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
//                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
//                            lrrc.setLocationCode(al.getCode());
//                            lrrc.setLocationId(al.getId());
//                            lrrc.setOuterContainerCode(containerCode);
//                            lrrc.setOuterContainerId(outerContainerId);
//                            list.add(lrrc);
//                        }
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

    private void invAttrMgmtAspect(AttrParams attrParams, WhSkuInventoryCommand invCmd) {
        String invAttrMgmt = attrParams.getInvAttrMgmt();
        attrParams.setSkuId(invCmd.getSkuId());
        if (!StringUtils.isEmpty(invAttrMgmt)) {
            String[] invAttrs = invAttrMgmt.split(",");
            if (null != invAttrs && 0 < invAttrs.length) {
                for (String attr : invAttrs) {
                    switch (attr) {
                        case InvAttrMgmtType.INV_TYPE:
                            attrParams.setInvType(invCmd.getInvType());
                            break;
                        case InvAttrMgmtType.INV_STATUS:
                            attrParams.setInvStatus(invCmd.getInvStatus());
                            break;
                        case InvAttrMgmtType.BATCH_NUMBER:
                            attrParams.setBatchNumber(invCmd.getBatchNumber());
                            break;
                        case InvAttrMgmtType.MFG_DATE:
                            attrParams.setMfgDate(invCmd.getMfgDate());
                            break;
                        case InvAttrMgmtType.EXP_DATE:
                            attrParams.setExpDate(invCmd.getExpDate());
                            break;
                        case InvAttrMgmtType.COUNTRY_OF_ORIGIN:
                            attrParams.setCountryOfOrigin(invCmd.getCountryOfOrigin());
                            break;
                        case InvAttrMgmtType.INV_ATTR1:
                            attrParams.setInvAttr1(invCmd.getInvAttr1());
                            break;
                        case InvAttrMgmtType.INV_ATTR2:
                            attrParams.setInvAttr2(invCmd.getInvAttr2());
                            break;
                        case InvAttrMgmtType.INV_ATTR3:
                            attrParams.setInvAttr3(invCmd.getInvAttr3());
                            break;
                        case InvAttrMgmtType.INV_ATTR4:
                            attrParams.setInvAttr4(invCmd.getInvAttr4());
                            break;
                        case InvAttrMgmtType.INV_ATTR5:
                            attrParams.setInvAttr5(invCmd.getInvAttr5());
                            break;
                    }
                }
            }
        }
    }

    private List<LocationRecommendResultCommand> sysGuideContainerPutawayRecommendLocation(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList,
            Map<String, Map<String, Double>> uomMap, String logId) {
        List<LocationRecommendResultCommand> list = new ArrayList<LocationRecommendResultCommand>();
        // Long funcId = ruleAffer.getFuncId();
        Long ouId = ruleAffer.getOuid();
        Long containerId = ruleAffer.getContainerId();
        // List<Long> storeIds = (null == ruleAffer.getStoreIdList() ? new ArrayList<Long>() :
        // ruleAffer.getStoreIdList());
        // 获取内部容器辅助信息
        ContainerAssist containerAssist = caMap.get(containerId);// 获取内部容器辅助信息
        if (null == containerAssist) {
            log.error("container assist info is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        String containerCode = ruleAffer.getAfferContainerCode();
        Container container = containerDao.findByIdExt(containerId, ouId);
        if (null == container) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != container.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = container.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        Long containerCate = container.getTwoLevelType();
        Container2ndCategory container2 = container2ndCategoryDao.findByIdExt(containerCate, ouId);
        if (null == container2) {
            log.error("container2ndCategory is null error, 2endCategoryId is:[{}], logId is:[{}]", containerCate, logId);
            throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
        }
        if (1 != container2.getLifecycle()) {
            log.error("container2ndCategory lifecycle is not normal error, containerId is:[{}], logId is:[{}]", container2.getId(), logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Double length = containerAssist.getSysLength();// 长（系统基准）
        Double width = containerAssist.getSysWidth();// 宽（系统基准）
        Double height = containerAssist.getSysHeight();// 高（系统基准）
        // Double volume = outerContainerAssist.getSysVolume();// 体积（系统基准）
        Double weight = containerAssist.getSysWeight();// 重量（系统基准）
        Long skuCategory = containerAssist.getSkuCategory();// sku种类数
        Long skuAttrCategory = containerAssist.getSkuAttrCategory();// 唯一sku数
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
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, logId);
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
                    attrParams.setContainerId(containerId);
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, logId);
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
                    /*attrParams.setIsMixStacking(false);// 库位不允许混放
                    String invAttrMgmt = "";
                    if (1 == storeIds.size()) {
                        // 获取店铺上配置的库存关键属性
                        Store store = storeManager.getStoreById(storeIds.get(0));
                        if (null == store) {
                            log.error("store is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
                        }
                        invAttrMgmt = store.getInvAttrMgmt();
                    }
                    if (StringUtils.isEmpty(invAttrMgmt)) {
                        // 获取仓库上配置的库存关键属性
                        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                        if (null == warehouse) {
                            log.error("warehouse is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
                        }
                        invAttrMgmt = warehouse.getInvAttrMgmt();
                    }
                    if(!StringUtils.isEmpty(invAttrMgmt)){
                        // 店铺或仓库管理库存关键属性
                        if(1L != skuAttrCategory.longValue()){
                            // 商品库存属性不唯一,不考虑推荐库位
                            avaliableLocs = null;
                            continue;
                        }
                    }*/
                    WhSkuInventoryCommand invCmd = invList.get(0);// 取一条库存信息
                    attrParams.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                    // 解析库存关键属性
                    invAttrMgmtAspect(attrParams, invCmd);
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, logId);
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
                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, logId);
                    if (null != putawayCondition) {
                        cSql = putawayCondition.getCondition(attrParams);
                    }
                    if (StringUtils.isEmpty(cSql)) {
                        cSql = null;
                    }
                    avaliableLocs = locationDao.findAllInvLocsByAreaIdAndDiffAttrs(area.getId(), ouId, cSql);
                } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
//                    attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
//                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, logId);
//                    if (null != putawayCondition) {
//                        cSql = putawayCondition.getCondition(attrParams);
//                    }
//                    if (StringUtils.isEmpty(cSql)) {
//                        cSql = null;
//                    }
//                    avaliableLocs = locationDao.findAllAvailableLocsByAreaId(area.getId(), ouId, cSql);
                } else {
                    avaliableLocs = null;
                }
                if (null == avaliableLocs || 0 == avaliableLocs.size()) {
                    continue;// 如果没有可用的库位，则遍历下一个上架规则
                }
                for (LocationCommand al : avaliableLocs) {
                    Long locId = al.getId();
                    // String templetCode = al.getTempletCode();
                    // LocationTemplet locTemplet =
                    // locationTempletDao.findLocationTempletByCodeAndOuId(templetCode, ouId);
                    Double locLength = al.getLength();
                    Double locHeight = al.getHigh();
                    Double locWidth = al.getWidth();
                    Double locWeight = al.getWeight();
                    Double locVolumeRate = (null == al.getVolumeRate() ? 1.0 : al.getVolumeRate());
                    if (null == locLength || null == locLength || null == locLength) {
                        log.error("sys guide container putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                        throw new BusinessException(ErrorCodes.LOCATION_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                    }
                    if (null == locWeight) {
                        log.error("sys guide container putaway sku weight is null error, skuId is:[{}], logId is:[{}]", locId, logId);
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
                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                            lrrc.setLocationCode(al.getCode());
                            lrrc.setLocBarcode(al.getBarCode());
                            lrrc.setLocationId(al.getId());
                            lrrc.setInsideContainerCode(containerCode);
                            lrrc.setInsideContainerId(containerId);
                            list.add(lrrc);
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
                            if (false == isMixStack) {
                                // 不允许混放则库存属性要保持一致
                                WhSkuInventoryCommand invCmd = invList.get(0);// 取一条库存信息
                                AttrParams invAttr1 = new AttrParams();
                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                // 解析库存关键属性
                                invAttrMgmtAspect(invAttr1, invCmd);
                                WhSkuInventoryCommand invCmd2 = whSkuInventoryDao.findFirstWhSkuInvCmdByLocation(ouId, locId);
                                AttrParams invAttr2 = new AttrParams();
                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                if (null != invCmd2) {
                                    // 解析库存关键属性
                                    invAttrMgmtAspect(invAttr2, invCmd2);
                                }
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
                                if (!(invAttr1InvType.equals(invAttr2InvType)) && invAttr1InvStatus.equals(invAttr2InvStatus) && inv1BatchNumber.equals(inv2BatchNumber) && inv1CountryOfOrigin.equals(inv2CountryOfOrigin) && inv1MfgDate.equals(inv2MfgDate)
                                        && inv1ExpDate.equals(inv2ExpDate) && inv1InvAttr1.equals(inv2InvAttr1) && inv1InvAttr2.equals(inv2InvAttr2) && inv1InvAttr3.equals(inv2InvAttr3) && inv1InvAttr4.equals(inv2InvAttr4)
                                        && inv1InvAttr5.equals(inv2InvAttr5)) {
                                    continue;
                                }
                            }
                        }
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
                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                            lrrc.setLocationCode(al.getCode());
                            lrrc.setLocBarcode(al.getBarCode());
                            lrrc.setLocationId(al.getId());
                            lrrc.setInsideContainerCode(containerCode);
                            lrrc.setInsideContainerId(containerId);
                            list.add(lrrc);
                        }
                    } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
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
                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                            lrrc.setLocationCode(al.getCode());
                            lrrc.setLocBarcode(al.getBarCode());
                            lrrc.setLocationId(al.getId());
                            lrrc.setInsideContainerCode(containerCode);
                            lrrc.setInsideContainerId(containerId);
                            list.add(lrrc);
                        }
                    } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                        Boolean isStatic = al.getIsStatic();
                        if (null != isStatic && true == isStatic) {
                            int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
                            if (count > 0) {
                                // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
                                continue;
                            }
                        }
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
                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                            lrrc.setLocationCode(al.getCode());
                            lrrc.setLocBarcode(al.getBarCode());
                            lrrc.setLocationId(al.getId());
                            lrrc.setInsideContainerCode(containerCode);
                            lrrc.setInsideContainerId(containerId);
                            list.add(lrrc);
                        }
                    } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
//                        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
//                        Double livwVolume = livw.getVolume();// 库位上已有货物总体积
//                        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
//                        // 计算体积
//                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
//                        calc.initStuffCube(length, width, height, SimpleCubeCalculator.SYS_UOM);
//                        calc.addStuffVolume(livwVolume);
//                        boolean cubageAvailable = calc.calculateAvailable();
//                        // 计算重量
//                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
//                        weightCal.initStuffWeight(weight, SimpleWeightCalculator.SYS_UOM);
//                        weightCal.addStuffWeight(livwWeight);
//                        boolean weightAvailable = weightCal.calculateAvailable();
//                        if (cubageAvailable & weightAvailable) {
//                            LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
//                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
//                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
//                            lrrc.setLocationCode(al.getCode());
//                            lrrc.setLocationId(al.getId());
//                            lrrc.setInsideContainerCode(containerCode);
//                            lrrc.setInsideContainerId(containerId);
//                            list.add(lrrc);
//                        }
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

    private List<LocationRecommendResultCommand> sysGuideSplitContainerPutawayRecommendLocation(RuleAfferCommand ruleAffer, List<WhSkuInventoryCommand> invRuleList, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList,
            Map<String, Map<String, Double>> uomMap, String logId) {
        List<LocationRecommendResultCommand> list = new ArrayList<LocationRecommendResultCommand>();
        // Long funcId = ruleAffer.getFuncId();
        Long ouId = ruleAffer.getOuid();
        Long containerId = ruleAffer.getContainerId();
        List<Long> storeIds = (null == ruleAffer.getStoreIdList() ? new ArrayList<Long>() : ruleAffer.getStoreIdList());
        // 获取内部容器辅助信息
        ContainerAssist containerAssist = caMap.get(containerId);// 获取内部容器辅助信息
        if (null == containerAssist) {
            log.error("container assist info is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        String containerCode = ruleAffer.getAfferContainerCode();
        Container container = containerDao.findByIdExt(containerId, ouId);
        if (null == container) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != container.getLifecycle()) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        // 获取容器状态
        Integer containerStatus = container.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        Long containerCate = container.getTwoLevelType();
        Container2ndCategory container2 = container2ndCategoryDao.findByIdExt(containerCate, ouId);
        if (null == container2) {
            log.error("container2ndCategory is null error, 2endCategoryId is:[{}], logId is:[{}]", containerCate, logId);
            throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
        }
        if (1 != container2.getLifecycle()) {
            log.error("container2ndCategory lifecycle is not normal error, containerId is:[{}], logId is:[{}]", container2.getId(), logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Double length = containerAssist.getSysLength();// 长（系统基准）
        Double width = containerAssist.getSysWidth();// 宽（系统基准）
        Double height = containerAssist.getSysHeight();// 高（系统基准）
        // Double volume = outerContainerAssist.getSysVolume();// 体积（系统基准）
        Double weight = containerAssist.getSysWeight();// 重量（系统基准）
        Long skuCategory = containerAssist.getSkuCategory();// sku种类数
        Long skuAttrCategory = containerAssist.getSkuAttrCategory();// 唯一sku数
        // Long skuQty = outerContainerAssist.getSkuQty();// sku总件数
        // Long storeQty = outerContainerAssist.getStoreQty();// 店铺数
        Double onHandQty = 0.0;
        Map<String, Double> lenUomConversionRate = uomMap.get(WhUomType.LENGTH_UOM);
        Map<String, Double> weightUomConversionRate = uomMap.get(WhUomType.WEIGHT_UOM);
        for (WhSkuInventoryCommand invRule : invRuleList) {
            // 商品
            Long skuId = invRule.getSkuId();
            SkuRedisCommand skuCmd = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
            //WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if (null == skuCmd) {
                log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Sku sku = skuCmd.getSku();
            length = sku.getLength();
            width = sku.getWidth();
            height = sku.getHeight();
            weight = sku.getWeight();
            SkuMgmt skuMgmt = skuCmd.getSkuMgmt();
            boolean skuIsMixAllowed = (null == skuMgmt.getIsMixAllowed() ? false : skuMgmt.getIsMixAllowed());
            String skuMixAttr = (null == skuMgmt.getMixAttr() ? "" : skuMgmt.getMixAttr());
            List<WhSkuInventorySnCommand> invSnRuleList = invRule.getWhSkuInventorySnCommandList();
            if (null == invSnRuleList || 0 == invSnRuleList.size()) {
                // 非残次品，上架规则匹配到sku库存行上
                onHandQty = invRule.getOnHandQty();
                // 获取当前sku行匹配到的所有上架规则
                List<ShelveRecommendRuleCommand> ruleList = invRule.getShelveRecommendRuleCommandList();
                if (null == ruleList || 0 == ruleList.size()) {
                    // 当前sku库存行没有匹配到任何可用上架规则
                    LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                    lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                    lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                    lrrc.setLocationCode(null);
                    lrrc.setLocBarcode(null);
                    lrrc.setLocationId(null);
                    lrrc.setInsideContainerCode(containerCode);
                    lrrc.setInsideContainerId(containerId);
                    lrrc.setSkuId(skuId);
                    lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                    lrrc.setDefectBarcode(null);
                    lrrc.setSn(null);
                    list.add(lrrc);
                } else {
                    LocationRecommendResultCommand lrrc = null;
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
                                PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
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
                                attrParams.setContainerId(containerId);
                                PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
                                if (null != putawayCondition) {
                                    cSql = putawayCondition.getCondition(attrParams);
                                }
                                if (StringUtils.isEmpty(cSql)) {
                                    cSql = null;
                                }
                                avaliableLocs = locationDao.findAllStaticLocsByAreaId(area.getId(), ouId, cSql);
                            } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                                attrParams.setLrt(WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS);
                                attrParams.setIsMixStacking(false);// 库位不允许混放
                               /* String invAttrMgmt = "";
                                if (1 == storeIds.size()) {
                                    // 获取店铺上配置的库存关键属性
                                    Store store = storeManager.getStoreById(storeIds.get(0));
                                    if (null == store) {
                                        log.error("store is null error, logId is:[{}]", logId);
                                        throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
                                    }
                                    invAttrMgmt = store.getInvAttrMgmt();
                                }
                                if (StringUtils.isEmpty(invAttrMgmt)) {
                                    // 获取仓库上配置的库存关键属性
                                    Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                                    if (null == warehouse) {
                                        log.error("warehouse is null error, logId is:[{}]", logId);
                                        throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
                                    }
                                    invAttrMgmt = warehouse.getInvAttrMgmt();
                                }*/
                                WhSkuInventoryCommand invCmd = invRule;// 取到库存信息
                                attrParams.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                // 解析库存关键属性
                                invAttrMgmtAspect(attrParams, invCmd);
                                PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
                                if (null != putawayCondition) {
                                    cSql = putawayCondition.getCondition(attrParams);
                                }
                                if (StringUtils.isEmpty(cSql)) {
                                    cSql = null;
                                }
                                avaliableLocs = locationDao.findAllInvLocsByAreaIdAndSameAttrs(area.getId(), ouId, cSql);
                            } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                                attrParams.setLrt(WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS);
                                WhSkuInventoryCommand invCmd = invRule;// 取到库存信息
                                String invAttrMgmt = "";
                                if (1 == storeIds.size()) {
                                    // 获取店铺上配置的库存关键属性
                                    Store store = storeManager.getStoreById(storeIds.get(0));
                                    if (null == store) {
                                        log.error("store is null error, logId is:[{}]", logId);
                                        throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
                                    }
                                    invAttrMgmt = store.getInvAttrMgmt();
                                }
                                if (StringUtils.isEmpty(invAttrMgmt)) {
                                    // 获取仓库上配置的库存关键属性
                                    Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                                    if (null == warehouse) {
                                        log.error("warehouse is null error, logId is:[{}]", logId);
                                        throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
                                    }
                                    invAttrMgmt = warehouse.getInvAttrMgmt();
                                }
                                attrParams.setInvAttrMgmt(invAttrMgmt);
                                // 解析库存关键属性
                                invAttrMgmtAspect(attrParams, invCmd);
                                PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
                                if (null != putawayCondition) {
                                    cSql = putawayCondition.getCondition(attrParams);
                                }
                                if (StringUtils.isEmpty(cSql)) {
                                    cSql = null;
                                }
                                avaliableLocs = locationDao.findAllInvLocsByAreaIdAndDiffAttrs(area.getId(), ouId, cSql);
                            } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
//                                attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
//                                PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
//                                if (null != putawayCondition) {
//                                    cSql = putawayCondition.getCondition(attrParams);
//                                }
//                                if (StringUtils.isEmpty(cSql)) {
//                                    cSql = null;
//                                }
//                                avaliableLocs = locationDao.findAllAvailableLocsByAreaId(area.getId(), ouId, cSql);
                            } else {
                                avaliableLocs = null;
                            }
                            if (null == avaliableLocs || 0 == avaliableLocs.size()) {
                                continue;// 如果没有可用的库位，则遍历下一个上架规则
                            }
                            for (LocationCommand al : avaliableLocs) {
                                Long locId = al.getId();
                                // String templetCode = al.getTempletCode();
                                // LocationTemplet locTemplet =
                                // locationTempletDao.findLocationTempletByCodeAndOuId(templetCode,
                                // ouId);
                                Double locLength = al.getLength();
                                Double locHeight = al.getHigh();
                                Double locWidth = al.getWidth();
                                Double locWeight = al.getWeight();
                                Double locVolumeRate = (null == al.getVolumeRate() ? 1.0 : al.getVolumeRate());
                                if (null == locLength || null == locLength || null == locLength) {
                                    log.error("sys guide container putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                                    throw new BusinessException(ErrorCodes.LOCATION_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                                }
                                if (null == locWeight) {
                                    log.error("sys guide container putaway sku weight is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                                    throw new BusinessException(ErrorCodes.LOCATION_WEIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                                }
                                // Double volumeRate = al.getVolumeRate();
                                if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                                    // 判断当前空库位是否为静态库位
                                    Boolean isStatic = al.getIsStatic();
                                    if (null != isStatic && true == isStatic) {
                                        int count = whSkuLocationDao.findSkuCountInSkuLocation(ouId, locId, skuId);
                                        if (count <= 0) {
                                            // 此静态库位不可用，商品当前静态库位没有绑定
                                            continue;
                                        }
                                    }
                                    // 计算体积
                                    SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                    calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                    boolean cubageAvailable = calc.calculateAvailable();
                                    // 计算重量
                                    SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                    weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                    boolean weightAvailable = weightCal.calculateAvailable();
                                    if (cubageAvailable & weightAvailable) {
                                        lrrc = new LocationRecommendResultCommand();
                                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                        lrrc.setLocationCode(al.getCode());
                                        lrrc.setLocBarcode(al.getBarCode());
                                        lrrc.setLocationId(al.getId());
                                        lrrc.setInsideContainerCode(containerCode);
                                        lrrc.setInsideContainerId(containerId);
                                        lrrc.setSkuId(skuId);
                                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                                        lrrc.setDefectBarcode(null);
                                        lrrc.setSn(null);
                                        list.add(lrrc);
                                    }
                                } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
                                    int count = whSkuLocationDao.findSkuCountInSkuLocation(ouId, locId, skuId);
                                    if (count <= 0) {
                                        // 此静态库位不可用，商品当前静态库位没有绑定
                                        continue;
                                    }else{
                                        if(true == skuIsMixAllowed){
                                            // 商品允许混放，判断混放属性
                                            if(!StringUtils.isEmpty(skuMixAttr)){
                                                
                                            }
                                        }else{
                                            //商品不允许混放
                                            int allCount = whSkuLocationDao.findAllSkuCountInSkuLocation(ouId, locId);
                                            if(1 < allCount){
                                                continue;
                                            }
                                        }
                                    }
                                    // 当静态库位只绑定一个商品并且不允许混放，校验库存属性
                                    int allCount = whSkuLocationDao.findAllSkuCountInSkuLocation(ouId, locId);
                                    if (1 == allCount && 1L == skuCategory.longValue() && 1L == skuAttrCategory.longValue()) {
                                        Boolean isMixStack = al.getIsMixStacking();
                                        if (false == isMixStack) {
                                            // 不允许混放则库存属性要保持一致
                                            WhSkuInventoryCommand invCmd = invRule;// 取到库存信息
                                            AttrParams invAttr1 = new AttrParams();
                                            invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                            // 解析库存关键属性
                                            invAttrMgmtAspect(invAttr1, invCmd);
                                            WhSkuInventoryCommand invCmd2 = whSkuInventoryDao.findFirstWhSkuInvCmdByLocation(ouId, locId);
                                            AttrParams invAttr2 = new AttrParams();
                                            invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                            if (null != invCmd2) {
                                                // 解析库存关键属性
                                                invAttrMgmtAspect(invAttr2, invCmd2);
                                            }
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
                                            if (!(invAttr1InvType.equals(invAttr2InvType)) && invAttr1InvStatus.equals(invAttr2InvStatus) && inv1BatchNumber.equals(inv2BatchNumber) && inv1CountryOfOrigin.equals(inv2CountryOfOrigin)
                                                    && inv1MfgDate.equals(inv2MfgDate) && inv1ExpDate.equals(inv2ExpDate) && inv1InvAttr1.equals(inv2InvAttr1) && inv1InvAttr2.equals(inv2InvAttr2) && inv1InvAttr3.equals(inv2InvAttr3)
                                                    && inv1InvAttr4.equals(inv2InvAttr4) && inv1InvAttr5.equals(inv2InvAttr5)) {
                                                continue;
                                            }
                                        }
                                    }
                                    LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                    Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                    Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                    // 计算体积
                                    SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                    calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                    calc.addStuffVolume(livwVolume);
                                    boolean cubageAvailable = calc.calculateAvailable();
                                    // 计算重量
                                    SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                    weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                    weightCal.addStuffWeight(livwWeight);
                                    boolean weightAvailable = weightCal.calculateAvailable();
                                    if (cubageAvailable & weightAvailable) {
                                        lrrc = new LocationRecommendResultCommand();
                                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                        lrrc.setLocationCode(al.getCode());
                                        lrrc.setLocBarcode(al.getBarCode());
                                        lrrc.setLocationId(al.getId());
                                        lrrc.setInsideContainerCode(containerCode);
                                        lrrc.setInsideContainerId(containerId);
                                        lrrc.setSkuId(skuId);
                                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                                        lrrc.setDefectBarcode(null);
                                        lrrc.setSn(null);
                                        list.add(lrrc);
                                    }
                                } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                                    LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                    Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                    Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                    // 计算体积
                                    SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                    calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                    calc.addStuffVolume(livwVolume);
                                    boolean cubageAvailable = calc.calculateAvailable();
                                    // 计算重量
                                    SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                    weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                    weightCal.addStuffWeight(livwWeight);
                                    boolean weightAvailable = weightCal.calculateAvailable();
                                    if (cubageAvailable & weightAvailable) {
                                        lrrc = new LocationRecommendResultCommand();
                                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                        lrrc.setLocationCode(al.getCode());
                                        lrrc.setLocBarcode(al.getBarCode());
                                        lrrc.setLocationId(al.getId());
                                        lrrc.setInsideContainerCode(containerCode);
                                        lrrc.setInsideContainerId(containerId);
                                        lrrc.setSkuId(skuId);
                                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                                        lrrc.setDefectBarcode(null);
                                        lrrc.setSn(null);
                                        list.add(lrrc);
                                    }
                                } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                                    Boolean isStatic = al.getIsStatic();
                                    if (null != isStatic && true == isStatic) {
                                        int count = whSkuLocationDao.findSkuCountInSkuLocation(ouId, locId, skuId);
                                        if (count <= 0) {
                                            // 此静态库位不可用，商品当前静态库位没有绑定
                                            continue;
                                        }
                                    }
                                    LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                    Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                    Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                    // 计算体积
                                    SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                    calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                    calc.addStuffVolume(livwVolume);
                                    boolean cubageAvailable = calc.calculateAvailable();
                                    // 计算重量
                                    SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                    weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                    weightCal.addStuffWeight(livwWeight);
                                    boolean weightAvailable = weightCal.calculateAvailable();
                                    if (cubageAvailable & weightAvailable) {
                                        lrrc = new LocationRecommendResultCommand();
                                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                        lrrc.setLocationCode(al.getCode());
                                        lrrc.setLocBarcode(al.getBarCode());
                                        lrrc.setLocationId(al.getId());
                                        lrrc.setInsideContainerCode(containerCode);
                                        lrrc.setInsideContainerId(containerId);
                                        lrrc.setSkuId(skuId);
                                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                                        lrrc.setDefectBarcode(null);
                                        lrrc.setSn(null);
                                        list.add(lrrc);
                                    }
                                } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
                                    LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                    Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                    Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                    // 计算体积
                                    SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                    calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                    calc.addStuffVolume(livwVolume);
                                    boolean cubageAvailable = calc.calculateAvailable();
                                    // 计算重量
                                    SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                    weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                    weightCal.addStuffWeight(livwWeight);
                                    boolean weightAvailable = weightCal.calculateAvailable();
                                    if (cubageAvailable & weightAvailable) {
                                        lrrc = new LocationRecommendResultCommand();
                                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                        lrrc.setLocationCode(al.getCode());
                                        lrrc.setLocBarcode(al.getBarCode());
                                        lrrc.setLocationId(al.getId());
                                        lrrc.setInsideContainerCode(containerCode);
                                        lrrc.setInsideContainerId(containerId);
                                        lrrc.setSkuId(skuId);
                                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                                        lrrc.setDefectBarcode(null);
                                        lrrc.setSn(null);
                                        list.add(lrrc);
                                    }
                                } else {
                                    break;
                                }

                                if (null != lrrc) {
                                    break;
                                }
                            }
                            if (null != lrrc) {
                                break;
                            }
                        }
                        if (null != lrrc) {
                            break;
                        }
                    }
                    if(null == lrrc){
                        lrrc = new LocationRecommendResultCommand();
                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                        lrrc.setLocationCode(null);
                        lrrc.setLocBarcode(null);
                        lrrc.setLocationId(null);
                        lrrc.setInsideContainerCode(containerCode);
                        lrrc.setInsideContainerId(containerId);
                        lrrc.setSkuId(skuId);
                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                        lrrc.setDefectBarcode(null);
                        lrrc.setSn(null);
                        list.add(lrrc);
                    }
                }
            } else {
                // 残次品，上架规则匹配到sn库存行上
                onHandQty = 1.0;
                for (WhSkuInventorySnCommand invSnRule : invSnRuleList) {
                    // 残次条码
                    String defectBarcode = invSnRule.getDefectWareBarcode();
                    // 序列号
                    String sn = invSnRule.getSn();
                    // 获取当前sn行匹配到的所有上架规则
                    List<ShelveRecommendRuleCommand> ruleList = invSnRule.getShelveRecommendRuleCommandList();
                    if (null == ruleList || 0 == ruleList.size()) {
                        // 当前sn库存行没有匹配到任何可用规则
                        LocationRecommendResultCommand lrrc = new LocationRecommendResultCommand();
                        lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                        lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                        lrrc.setLocationCode(null);
                        lrrc.setLocBarcode(null);
                        lrrc.setLocationId(null);
                        lrrc.setInsideContainerCode(containerCode);
                        lrrc.setInsideContainerId(containerId);
                        lrrc.setSkuId(skuId);
                        lrrc.setSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule));
                        lrrc.setDefectBarcode(defectBarcode);
                        lrrc.setSn(sn);
                        list.add(lrrc);
                    } else {
                        LocationRecommendResultCommand lrrc = null;
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
                                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
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
                                    attrParams.setContainerId(containerId);
                                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
                                    if (null != putawayCondition) {
                                        cSql = putawayCondition.getCondition(attrParams);
                                    }
                                    if (StringUtils.isEmpty(cSql)) {
                                        cSql = null;
                                    }
                                    avaliableLocs = locationDao.findAllStaticLocsByAreaId(area.getId(), ouId, cSql);
                                } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                                    attrParams.setLrt(WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS);
                                    attrParams.setIsMixStacking(false);// 库位不允许混放
                                   /* String invAttrMgmt = "";
                                    if (1 == storeIds.size()) {
                                        // 获取店铺上配置的库存关键属性
                                        Store store = storeManager.getStoreById(storeIds.get(0));
                                        if (null == store) {
                                            log.error("store is null error, logId is:[{}]", logId);
                                            throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
                                        }
                                        invAttrMgmt = store.getInvAttrMgmt();
                                    }
                                    if (StringUtils.isEmpty(invAttrMgmt)) {
                                        // 获取仓库上配置的库存关键属性
                                        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                                        if (null == warehouse) {
                                            log.error("warehouse is null error, logId is:[{}]", logId);
                                            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
                                        }
                                        invAttrMgmt = warehouse.getInvAttrMgmt();
                                    }*/
                                    WhSkuInventoryCommand invCmd = invRule;// 取到库存信息
                                    attrParams.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                    // 解析库存关键属性
                                    invAttrMgmtAspect(attrParams, invCmd);
                                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
                                    if (null != putawayCondition) {
                                        cSql = putawayCondition.getCondition(attrParams);
                                    }
                                    if (StringUtils.isEmpty(cSql)) {
                                        cSql = null;
                                    }
                                    avaliableLocs = locationDao.findAllInvLocsByAreaIdAndSameAttrs(area.getId(), ouId, cSql);
                                } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                                    attrParams.setLrt(WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS);
                                    WhSkuInventoryCommand invCmd = invRule;// 取到库存信息
                                    String invAttrMgmt = "";
                                    if (1 == storeIds.size()) {
                                        // 获取店铺上配置的库存关键属性
                                        Store store = storeManager.getStoreById(storeIds.get(0));
                                        if (null == store) {
                                            log.error("store is null error, logId is:[{}]", logId);
                                            throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
                                        }
                                        invAttrMgmt = store.getInvAttrMgmt();
                                    }
                                    if (StringUtils.isEmpty(invAttrMgmt)) {
                                        // 获取仓库上配置的库存关键属性
                                        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                                        if (null == warehouse) {
                                            log.error("warehouse is null error, logId is:[{}]", logId);
                                            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
                                        }
                                        invAttrMgmt = warehouse.getInvAttrMgmt();
                                    }
                                    attrParams.setInvAttrMgmt(invAttrMgmt);
                                    // 解析库存关键属性
                                    invAttrMgmtAspect(attrParams, invCmd);
                                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
                                    if (null != putawayCondition) {
                                        cSql = putawayCondition.getCondition(attrParams);
                                    }
                                    if (StringUtils.isEmpty(cSql)) {
                                        cSql = null;
                                    }
                                    avaliableLocs = locationDao.findAllInvLocsByAreaIdAndDiffAttrs(area.getId(), ouId, cSql);
                                } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
//                                    attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
//                                    PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, logId);
//                                    if (null != putawayCondition) {
//                                        cSql = putawayCondition.getCondition(attrParams);
//                                    }
//                                    if (StringUtils.isEmpty(cSql)) {
//                                        cSql = null;
//                                    }
//                                    avaliableLocs = locationDao.findAllAvailableLocsByAreaId(area.getId(), ouId, cSql);
                                } else {
                                    avaliableLocs = null;
                                }
                                if (null == avaliableLocs || 0 == avaliableLocs.size()) {
                                    continue;// 如果没有可用的库位，则遍历下一个上架规则
                                }
                                for (LocationCommand al : avaliableLocs) {
                                    Long locId = al.getId();
                                    // String templetCode = al.getTempletCode();
                                    // LocationTemplet locTemplet =
                                    // locationTempletDao.findLocationTempletByCodeAndOuId(templetCode,
                                    // ouId);
                                    Double locLength = al.getLength();
                                    Double locHeight = al.getHigh();
                                    Double locWidth = al.getWidth();
                                    Double locWeight = al.getWeight();
                                    Double locVolumeRate = (null == al.getVolumeRate() ? 1.0 : al.getVolumeRate());
                                    if (null == locLength || null == locLength || null == locLength) {
                                        log.error("sys guide container putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                                        throw new BusinessException(ErrorCodes.LOCATION_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                                    }
                                    if (null == locWeight) {
                                        log.error("sys guide container putaway sku weight is null error, skuId is:[{}], logId is:[{}]", locId, logId);
                                        throw new BusinessException(ErrorCodes.LOCATION_WEIGHT_IS_NULL_ERROR, new Object[] {al.getCode()});
                                    }
                                    // Double volumeRate = al.getVolumeRate();
                                    if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                                        // 计算体积
                                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                        calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                        boolean cubageAvailable = calc.calculateAvailable();
                                        // 计算重量
                                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                        weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                        boolean weightAvailable = weightCal.calculateAvailable();
                                        if (cubageAvailable & weightAvailable) {
                                            lrrc = new LocationRecommendResultCommand();
                                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                            lrrc.setLocationCode(al.getCode());
                                            lrrc.setLocBarcode(al.getBarCode());
                                            lrrc.setLocationId(al.getId());
                                            lrrc.setInsideContainerCode(containerCode);
                                            lrrc.setInsideContainerId(containerId);
                                            lrrc.setSkuId(skuId);
                                            lrrc.setSkuAttrId(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule), sn, defectBarcode));
                                            lrrc.setDefectBarcode(defectBarcode);
                                            lrrc.setSn(sn);
                                            list.add(lrrc);
                                        }
                                    } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
//                                        int count = whSkuLocationDao.findContainerSkuCountNotInSkuLocation(ouId, locId, ruleAffer.getAfferContainerCodeList());
//                                        if (count > 0) {
//                                            // 此静态库位不可用，容器中包含商品当前静态库位没有绑定
//                                            continue;
//                                        }
                                        int count = whSkuLocationDao.findSkuCountInSkuLocation(ouId, locId, skuId);
                                        if (count <= 0) {
                                            // 此静态库位不可用，商品当前静态库位没有绑定
                                            continue;
                                        }else{
                                            if(true == skuIsMixAllowed){
                                                // 商品允许混放，判断混放属性
                                                if(!StringUtils.isEmpty(skuMixAttr)){
                                                    
                                                }
                                            }else{
                                                //商品不允许混放
                                                int allCount = whSkuLocationDao.findAllSkuCountInSkuLocation(ouId, locId);
                                                if(1 < allCount){
                                                    continue;
                                                }
                                            }
                                        }
                                     // 当静态库位只绑定一个商品并且不允许混放，校验库存属性
                                        int allCount = whSkuLocationDao.findAllSkuCountInSkuLocation(ouId, locId);
                                        if (1 == allCount && 1L == skuCategory.longValue() && 1L == skuAttrCategory.longValue()) {
                                            Boolean isMixStack = al.getIsMixStacking();
                                            if (false == isMixStack) {
                                                // 不允许混放则库存属性要保持一致
                                                WhSkuInventoryCommand invCmd = invRule;// 取到库存信息
                                                AttrParams invAttr1 = new AttrParams();
                                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                                // 解析库存关键属性
                                                invAttrMgmtAspect(invAttr1, invCmd);
                                                WhSkuInventoryCommand invCmd2 = whSkuInventoryDao.findFirstWhSkuInvCmdByLocation(ouId, locId);
                                                AttrParams invAttr2 = new AttrParams();
                                                invAttr1.setInvAttrMgmt(InvAttrMgmtType.ALL_INV_ATTRS);
                                                if (null != invCmd2) {
                                                    // 解析库存关键属性
                                                    invAttrMgmtAspect(invAttr2, invCmd2);
                                                }
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
                                                if (!(invAttr1InvType.equals(invAttr2InvType)) && invAttr1InvStatus.equals(invAttr2InvStatus) && inv1BatchNumber.equals(inv2BatchNumber) && inv1CountryOfOrigin.equals(inv2CountryOfOrigin)
                                                        && inv1MfgDate.equals(inv2MfgDate) && inv1ExpDate.equals(inv2ExpDate) && inv1InvAttr1.equals(inv2InvAttr1) && inv1InvAttr2.equals(inv2InvAttr2) && inv1InvAttr3.equals(inv2InvAttr3)
                                                        && inv1InvAttr4.equals(inv2InvAttr4) && inv1InvAttr5.equals(inv2InvAttr5)) {
                                                    continue;
                                                }
                                            }
                                        }
                                        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                        Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                        // 计算体积
                                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                        calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                        calc.addStuffVolume(livwVolume);
                                        boolean cubageAvailable = calc.calculateAvailable();
                                        // 计算重量
                                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                        weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                        weightCal.addStuffWeight(livwWeight);
                                        boolean weightAvailable = weightCal.calculateAvailable();
                                        if (cubageAvailable & weightAvailable) {
                                            lrrc = new LocationRecommendResultCommand();
                                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                            lrrc.setLocationCode(al.getCode());
                                            lrrc.setLocBarcode(al.getBarCode());
                                            lrrc.setLocationId(al.getId());
                                            lrrc.setInsideContainerCode(containerCode);
                                            lrrc.setInsideContainerId(containerId);
                                            lrrc.setSkuId(skuId);
                                            lrrc.setSkuAttrId(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule), sn, defectBarcode));
                                            lrrc.setDefectBarcode(defectBarcode);
                                            lrrc.setSn(sn);
                                            list.add(lrrc);
                                        }
                                    } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                                        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                        Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                        // 计算体积
                                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                        calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                        calc.addStuffVolume(livwVolume);
                                        boolean cubageAvailable = calc.calculateAvailable();
                                        // 计算重量
                                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                        weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                        weightCal.addStuffWeight(livwWeight);
                                        boolean weightAvailable = weightCal.calculateAvailable();
                                        if (cubageAvailable & weightAvailable) {
                                            lrrc = new LocationRecommendResultCommand();
                                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                            lrrc.setLocationCode(al.getCode());
                                            lrrc.setLocBarcode(al.getBarCode());
                                            lrrc.setLocationId(al.getId());
                                            lrrc.setInsideContainerCode(containerCode);
                                            lrrc.setInsideContainerId(containerId);
                                            lrrc.setSkuId(skuId);
                                            lrrc.setSkuAttrId(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule), sn, defectBarcode));
                                            lrrc.setDefectBarcode(defectBarcode);
                                            lrrc.setSn(sn);
                                            list.add(lrrc);
                                        }
                                    } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                                        Boolean isStatic = al.getIsStatic();
                                        if (null != isStatic && true == isStatic) {
                                            int count = whSkuLocationDao.findSkuCountInSkuLocation(ouId, locId, skuId);
                                            if (count <= 0) {
                                                // 此静态库位不可用，商品当前静态库位没有绑定
                                                continue;
                                            }
                                        }
                                        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
                                        Double livwVolume = livw.getVolume();// 库位上已有货物总体积
                                        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
                                        // 计算体积
                                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
                                        calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
                                        calc.addStuffVolume(livwVolume);
                                        boolean cubageAvailable = calc.calculateAvailable();
                                        // 计算重量
                                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
                                        weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
                                        weightCal.addStuffWeight(livwWeight);
                                        boolean weightAvailable = weightCal.calculateAvailable();
                                        if (cubageAvailable & weightAvailable) {
                                            lrrc = new LocationRecommendResultCommand();
                                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                                            lrrc.setLocationCode(al.getCode());
                                            lrrc.setLocBarcode(al.getBarCode());
                                            lrrc.setLocationId(al.getId());
                                            lrrc.setInsideContainerCode(containerCode);
                                            lrrc.setInsideContainerId(containerId);
                                            lrrc.setSkuId(skuId);
                                            lrrc.setSkuAttrId(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule), sn, defectBarcode));
                                            lrrc.setDefectBarcode(defectBarcode);
                                            lrrc.setSn(sn);
                                            list.add(lrrc);
                                        }
                                    } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
//                                        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
//                                        Double livwVolume = livw.getVolume();// 库位上已有货物总体积
//                                        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
//                                        // 计算体积
//                                        SimpleCubeCalculator calc = new SimpleCubeCalculator(locLength, locWidth, locHeight, SimpleCubeCalculator.SYS_UOM, locVolumeRate, lenUomConversionRate);
//                                        calc.initStuffCube(length, width, height, onHandQty, SimpleCubeCalculator.SYS_UOM);
//                                        calc.addStuffVolume(livwVolume);
//                                        boolean cubageAvailable = calc.calculateAvailable();
//                                        // 计算重量
//                                        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(locWeight, SimpleWeightCalculator.SYS_UOM, weightUomConversionRate);
//                                        weightCal.initStuffWeight(weight, onHandQty, SimpleWeightCalculator.SYS_UOM);
//                                        weightCal.addStuffWeight(livwWeight);
//                                        boolean weightAvailable = weightCal.calculateAvailable();
//                                        if (cubageAvailable & weightAvailable) {
//                                            lrrc = new LocationRecommendResultCommand();
//                                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
//                                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
//                                            lrrc.setLocationCode(al.getCode());
//                                            lrrc.setLocationId(al.getId());
//                                            lrrc.setInsideContainerCode(containerCode);
//                                            lrrc.setInsideContainerId(containerId);
//                                            lrrc.setSkuId(skuId);
//                                            lrrc.setSkuAttrId(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule), sn, defectBarcode));
//                                            lrrc.setDefectBarcode(defectBarcode);
//                                            lrrc.setSn(sn);
//                                            list.add(lrrc);
//                                        }
                                    } else {
                                        break;
                                    }

                                    if (null != lrrc) {
                                        break;
                                    }
                                }
                                if (null != lrrc) {
                                    break;
                                }
                            }
                            if (null != lrrc) {
                                break;
                            }
                        }
                        if(null == lrrc){
                            lrrc = new LocationRecommendResultCommand();
                            lrrc.setPutawayPatternType(WhPutawayPatternType.SYS_GUIDE_PUTAWAY);
                            lrrc.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                            lrrc.setLocationCode(null);
                            lrrc.setLocBarcode(null);
                            lrrc.setLocationId(null);
                            lrrc.setInsideContainerCode(containerCode);
                            lrrc.setInsideContainerId(containerId);
                            lrrc.setSkuId(skuId);
                            lrrc.setSkuAttrId(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invRule), sn, defectBarcode));
                            lrrc.setDefectBarcode(defectBarcode);
                            lrrc.setSn(sn);
                            list.add(lrrc);
                        }
                    }
                }
            }
        }
        return list;
    }


}
