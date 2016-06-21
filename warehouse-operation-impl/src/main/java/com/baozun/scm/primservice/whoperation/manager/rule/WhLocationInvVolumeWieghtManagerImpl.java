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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationInvVolumeWeightCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

/**
 * @author lichuan
 *
 */
@Service("whLocationInvVolumeWieghtManager")
@Transactional
public class WhLocationInvVolumeWieghtManagerImpl extends BaseManagerImpl implements WhLocationInvVolumeWieghtManager {
    protected static final Logger log = LoggerFactory.getLogger(WhLocationInvVolumeWieghtManagerImpl.class);
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private WhSkuDao whSkuDao;

    /**
     * @author lichuan
     * @param locationId
     * @param ouId
     * @return
     */
    @Override
    public LocationInvVolumeWeightCommand calculateLocationInvVolumeAndWeight(Long locationId, Long ouId, Map<String, Map<String, Double>> uomMap, String logId) {
        if (log.isInfoEnabled()) {
            log.info("whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight start, locationId is:[{}], ouId is:[{}], logId is:[{}]", locationId, ouId, logId);
        }
        LocationInvVolumeWeightCommand livw = new LocationInvVolumeWeightCommand();
        Map<String, Double> lenUomConversionRate = uomMap.get(WhUomType.LENGTH_UOM);
        Map<String, Double> weihgtUomConversionRate = uomMap.get(WhUomType.WEIGHT_UOM);
        // 1.获取该库位上的所有库存
        List<WhSkuInventoryCommand> locInvs = whSkuInventoryDao.findWhSkuInventoryByLocIdAndOuId(ouId, locationId);
        // 2.分析库存并计算体积重量
        SimpleCubeCalculator cubeCal = new SimpleCubeCalculator(lenUomConversionRate);
        SimpleWeightCalculator weightCal = new SimpleWeightCalculator(weihgtUomConversionRate);
        Set<Long> outerContainerIds = new HashSet<Long>();
        Set<Long> insideContainerIds = new HashSet<Long>();
        Set<Long> skuIds = new HashSet<Long>();
        for (WhSkuInventoryCommand invCmd : locInvs) {
            Long outerContainerId = invCmd.getOuterContainerId();
            Long insideContainerId = invCmd.getInsideContainerId();
            Long skuId = invCmd.getSkuId();
            if (null != outerContainerId && !outerContainerIds.contains(outerContainerId)) {
                // 有外部容器
                Container container = containerDao.findByIdExt(outerContainerId, ouId);
                Long outerContainerCate = container.getTwoLevelType();
                Container2ndCategory outerContainer2 = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
                if (null == outerContainer2) {
                    log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", outerContainerId, outerContainerCate, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                }
                if (1 != outerContainer2.getLifecycle()) {
                    log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", outerContainerId, outerContainer2.getId(), logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                }
                Double ocLength = outerContainer2.getLength();
                Double ocWidth = outerContainer2.getWidth();
                Double ocHeight = outerContainer2.getHigh();
                Double ocWeight = outerContainer2.getWeight();
                if (null == ocLength || null == ocWidth || null == ocHeight) {
                    log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", outerContainerId, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                }
                if (null == ocWeight) {
                    log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", outerContainerId, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                }
                cubeCal.accumulationStuffVolume(ocLength, ocWidth, ocHeight);
                weightCal.accumulationStuffWeight(ocWeight);
            } else {
                if (null != insideContainerId && !insideContainerIds.contains(insideContainerId)) {
                    // 无外部容器但有内部容器
                    Container container = containerDao.findByIdExt(outerContainerId, ouId);
                    Long outerContainerCate = container.getTwoLevelType();
                    Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
                    if (null == insideContainer2) {
                        log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", outerContainerId, outerContainerCate, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                    }
                    if (1 != insideContainer2.getLifecycle()) {
                        log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", outerContainerId, insideContainer2.getId(), logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    Double icLength = insideContainer2.getLength();
                    Double icWidth = insideContainer2.getWidth();
                    Double icHeight = insideContainer2.getHigh();
                    Double icWeight = insideContainer2.getWeight();
                    if (null == icLength || null == icWidth || null == icHeight) {
                        log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", outerContainerId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                    }
                    if (null == icWeight) {
                        log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", outerContainerId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {container.getCode()});
                    }
                    cubeCal.accumulationStuffVolume(icLength, icWidth, icHeight);
                    weightCal.accumulationStuffWeight(icWeight);
                } else {
                    if (null != skuId && !skuIds.contains(skuId)) {
                        // 既无外部容器也无内部容器
                        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                        if (null == skuCmd) {
                            log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                        }
                        Double skuLength = skuCmd.getLength();
                        Double skuWidth = skuCmd.getWidth();
                        Double skuHeight = skuCmd.getHeight();
                        if (null == skuLength || null == skuWidth || null == skuHeight) {
                            log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                        }
                        Double onHandQty = invCmd.getOnHandQty();
                        Double freezeQty = invCmd.getFrozenQty();
                        Double toBeFillQty = invCmd.getToBeFilledQty();
                        Double sVolume = cubeCal.calculateStuffVolume(skuLength, skuWidth, skuHeight);
                        cubeCal.addStuffCubage(sVolume * (onHandQty + freezeQty + toBeFillQty));
                    }
                }
            }
            // 计算库位上所有商品的重量
            Double onHandQty = invCmd.getOnHandQty();
            Double freezeQty = invCmd.getFrozenQty();
            Double toBeFillQty = invCmd.getToBeFilledQty();
            WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if (null == skuCmd) {
                log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Double skuWeight = skuCmd.getWeight();
            if (null == skuWeight) {
                log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
            }
            Double sWeight = weightCal.calculateStuffWeight(skuWeight);
            weightCal.addStuffWeight(sWeight * (onHandQty + freezeQty + toBeFillQty));
            outerContainerIds.add(outerContainerId);
            insideContainerIds.add(insideContainerId);
            skuIds.add(skuId);
        }
        livw.setVolume(cubeCal.getStuffCubage());
        livw.setWeight(weightCal.getStuffWeight());
        if (log.isInfoEnabled()) {
            log.info("whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight end, locationId is:[{}], ouId is:[{}], logId is:[{}]", locationId, ouId, logId);
        }
        return livw;
    }
}
