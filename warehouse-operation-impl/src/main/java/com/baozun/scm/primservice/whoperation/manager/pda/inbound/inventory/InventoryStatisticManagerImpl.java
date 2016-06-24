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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;

/**
 * @author lichuan
 *
 */
@Service("inventoryStatisticManager")
@Transactional
public class InventoryStatisticManagerImpl extends BaseManagerImpl implements InventoryStatisticManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryStatisticManagerImpl.class);

    @Autowired
    private WhCartonDao whCartonDao;

    /**
     * @author lichuan
     * @param invList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public InventoryStatisticResultCommand sysGuidePutawayInvStatistic(List<WhSkuInventoryCommand> invList, Integer putawayPatternDetailType, Long ouId, String logId) {
        InventoryStatisticResultCommand isCmd = new InventoryStatisticResultCommand();
        isCmd.setPutawayPatternDetailType(putawayPatternDetailType);
        Long outerContainerId = 0L;
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
        for (WhSkuInventoryCommand invCmd : invList) {
            Long ocId = invCmd.getOuterContainerId();
            if (null != ocId) {
                outerContainerId = ocId;
                isCmd.setHasOuterContainer(true);
                isCmd.setOuterContainerId(outerContainerId);
            }
            Long icId = invCmd.getInsideContainerId();
            if (null == icId) {
                log.error("sys guide putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            } else {
                insideContainerIds.add(icId);
            }
            WhCarton carton = whCartonDao.findWhCaselevelCartonById(icId, ouId);
            if (null != carton) {
                caselevelContainerIds.add(icId);
            } else {
                notcaselevelContainerIds.add(icId);
            }

            Long skuId = invCmd.getSkuId();
            Double toBefillQty = invCmd.getToBeFilledQty();
            Double onHandQty = invCmd.getOnHandQty();
            Double curerntSkuQty = 0.0;
            Long locationId = invCmd.getLocationId();
            if (null != locationId) {
                locationIds.add(locationId);
                isCmd.setRecommendLocation(true);
                if (null != toBefillQty) {
                    curerntSkuQty = toBefillQty;
                    skuQty += toBefillQty.longValue();
                }
            } else {
                if (null != onHandQty) {
                    curerntSkuQty = onHandQty;
                    skuQty += onHandQty.longValue();
                }
            }
            if (null != skuId) {
                skuIds.add(skuId);
            } else {
                log.error("sys guide putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            skuAttrIds.add(SkuCategoryProvider.getSkuCategoryByInv(invCmd));
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
                icSkus.add(SkuCategoryProvider.getSkuCategoryByInv(invCmd));
                insideContainerSkuAttrIds.put(icId, icSkus);
            } else {
                Set<String> icSkus = new HashSet<String>();
                icSkus.add(SkuCategoryProvider.getSkuCategoryByInv(invCmd));
                insideContainerSkuAttrIds.put(icId, icSkus);
            }
            if (null != insideContainerSkuAttrIdsQty.get(icId)) {
                Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);
                if (null != skuAttrIdsQty.get(skuId)) {
                    skuAttrIdsQty.put(SkuCategoryProvider.getSkuCategoryByInv(invCmd), skuAttrIdsQty.get(SkuCategoryProvider.getSkuCategoryByInv(invCmd)) + curerntSkuQty.longValue());
                } else {
                    skuAttrIdsQty.put(SkuCategoryProvider.getSkuCategoryByInv(invCmd), curerntSkuQty.longValue());
                }
            } else {
                Map<String, Long> saq = new HashMap<String, Long>();
                saq.put(SkuCategoryProvider.getSkuCategoryByInv(invCmd), curerntSkuQty.longValue());
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
        }
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
        return isCmd;
    }


}
