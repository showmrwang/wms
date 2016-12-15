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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.statis;

import java.util.Date;
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
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaPutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

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
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    PdaPutawayCacheManager pdaPutawayCacheManager;
    @Autowired
    WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private SkuRedisManager skuRedisManager;

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
        Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();// 内部容器唯一sku对应所有残次条码
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();// 内部容器推荐库位对应唯一sku及残次条码
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
                insideContainerSkuAttrIdsQty.put(icId, skuAttrIdsQty);
            } else {
                Map<String, Long> saq = new HashMap<String, Long>();
                saq.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                insideContainerSkuAttrIdsQty.put(icId, saq);
            }
            // 统计残次品
            if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
                List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();
                Set<String> snDefects = null;
                if (null != snCmdList && 0 < snCmdList.size()) {
                    snDefects = new HashSet<String>();
                    for (WhSkuInventorySnCommand snCmd : snCmdList) {
                        if (null != snCmd) {
                            String defectBar = snCmd.getDefectWareBarcode();
                            String sn = snCmd.getSn();
                            snDefects.add(SkuCategoryProvider.concatSkuAttrId(sn, defectBar));
                            if (null != locationId) {
                                if (null != insideContainerLocSkuAttrIds.get(icId)) {
                                    Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icId);
                                    Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                                    if (null != allSkuAttrIds) {
                                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), sn, defectBar));
                                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                                        insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                    } else {
                                        allSkuAttrIds = new HashSet<String>();
                                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), sn, defectBar));
                                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                                        insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                    }
                                } else {
                                    Set<String> allSkuAttrIds = new HashSet<String>();
                                    allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), sn, defectBar));
                                    Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
                                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                                    insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                }
                            }
                        }
                    }
                } else {
                    if (null != locationId) {
                        if (null != insideContainerLocSkuAttrIds.get(icId)) {
                            Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icId);
                            Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                            if (null != allSkuAttrIds) {
                                allSkuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                locSkuAttrIds.put(locationId, allSkuAttrIds);
                                insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                            } else {
                                allSkuAttrIds = new HashSet<String>();
                                allSkuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                locSkuAttrIds.put(locationId, allSkuAttrIds);
                                insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                            }
                        } else {
                            Set<String> allSkuAttrIds = new HashSet<String>();
                            allSkuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                            Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
                            locSkuAttrIds.put(locationId, allSkuAttrIds);
                            insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
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
        isCmd.setInsideContainerSkuAttrIdsSnDefect(insideContainerSkuAttrIdsSnDefect);
        isCmd.setInsideContainerLocSkuAttrIds(insideContainerLocSkuAttrIds);
        isCmd.setInsideContainerStoreIds(insideContainerStoreIds);
        return isCmd;
    }

    /**
     * @author lichuan
     * @param invList
     * @param putawayPatternDetailType
     * @param containerCommand
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public InventoryStatisticResultCommand sysGuidePutawayInvStatistic(List<WhSkuInventoryCommand> invList, Integer putawayPatternDetailType, List<UomCommand> lenUomCmds, List<UomCommand> weightUomCmds, ContainerCommand containerCmd, Long ouId,
            Long userId, String logId) {
        InventoryStatisticResultCommand isCmd = new InventoryStatisticResultCommand();
        isCmd.setPutawayPatternDetailType(putawayPatternDetailType);
        // Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
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
        Map<Long, Map<Long, Set<String>>> insideContainerSkuAndSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();// 内部容器sku对应所有唯一sku
        Map<Long, Set<Long>> insideContainerStoreIds = new HashMap<Long, Set<Long>>();// 内部容器所有店铺
        Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();// 内部容器唯一sku对应所有残次条码
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();// 内部容器推荐库位对应唯一sku及残次条码
        Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();// 内部容器重量
        Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();// 内部容器体积
        Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
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
                    // srCmd.setHasInsideContainer(true);
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
                WhCarton carton = whCartonDao.findWhCaselevelCartonByContainerId(icId, ouId);
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
                    boolean isExists = false;
                    for (SysDictionary sd : list) {
                        if (sd.getDicValue().equals(invAttr1)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
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
                    boolean isExists = false;
                    for (SysDictionary sd : list) {
                        if (sd.getDicValue().equals(invAttr2)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
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
                    boolean isExists = false;
                    for (SysDictionary sd : list) {
                        if (sd.getDicValue().equals(invAttr3)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
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
                    boolean isExists = false;
                    for (SysDictionary sd : list) {
                        if (sd.getDicValue().equals(invAttr4)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
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
                    boolean isExists = false;
                    for (SysDictionary sd : list) {
                        if (sd.getDicValue().equals(invAttr5)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
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
                    if (null == cacheSku) {
                        log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                        throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                    }
                    Sku sku = cacheSku.getSku();
                    // WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                    // if (null == skuCmd) {
                    // log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}],
                    // logId is:[{}]", skuId, logId);
                    // throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                    // }
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
                if (null != insideContainerSkuAndSkuAttrIds.get(icId)) {
                    Map<Long, Set<String>> skuAndSkuAttrIds = insideContainerSkuAndSkuAttrIds.get(icId);
                    Set<String> icSkus = skuAndSkuAttrIds.get(skuId);
                    if (null != icSkus) {
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        skuAndSkuAttrIds.put(skuId, icSkus);
                        insideContainerSkuAndSkuAttrIds.put(icId, skuAndSkuAttrIds);
                    } else {
                        icSkus = new HashSet<String>();
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        skuAndSkuAttrIds.put(skuId, icSkus);
                        insideContainerSkuAndSkuAttrIds.put(icId, skuAndSkuAttrIds);
                    }
                } else {
                    Set<String> icSkus = new HashSet<String>();
                    icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                    Map<Long, Set<String>> skuAndSkuAttrIds = new HashMap<Long, Set<String>>();
                    skuAndSkuAttrIds.put(skuId, icSkus);
                    insideContainerSkuAndSkuAttrIds.put(icId, skuAndSkuAttrIds);
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
                // 统计残次品
                if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();
                    Set<String> snDefects = null;
                    if (null != snCmdList && 0 < snCmdList.size()) {
                        snDefects = new HashSet<String>();
                        for (WhSkuInventorySnCommand snCmd : snCmdList) {
                            if (null != snCmd) {
                                String defectBar = snCmd.getDefectWareBarcode();
                                String sn = snCmd.getSn();
                                snDefects.add(SkuCategoryProvider.concatSkuAttrId(sn, defectBar));
                                if (null != locationId) {
                                    if (null != insideContainerLocSkuAttrIds.get(icId)) {
                                        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icId);
                                        Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                                        if (null != allSkuAttrIds) {
                                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), sn, defectBar));
                                            locSkuAttrIds.put(locationId, allSkuAttrIds);
                                            insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                        } else {
                                            allSkuAttrIds = new HashSet<String>();
                                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), sn, defectBar));
                                            locSkuAttrIds.put(locationId, allSkuAttrIds);
                                            insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                        }
                                    } else {
                                        Set<String> allSkuAttrIds = new HashSet<String>();
                                        allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), sn, defectBar));
                                        Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
                                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                                        insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                    }
                                }
                            }
                        }
                    } else {
                        if (null != locationId) {
                            if (null != insideContainerLocSkuAttrIds.get(icId)) {
                                Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icId);
                                Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                                if (null != allSkuAttrIds) {
                                    allSkuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                                    insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                } else {
                                    allSkuAttrIds = new HashSet<String>();
                                    allSkuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                                    insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
                                }
                            } else {
                                Set<String> allSkuAttrIds = new HashSet<String>();
                                allSkuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
                                locSkuAttrIds.put(locationId, allSkuAttrIds);
                                insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
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
        isCmd.setInsideContainerSkuAndSkuAttrIds(insideContainerSkuAndSkuAttrIds);
        isCmd.setInsideContainerStoreIds(insideContainerStoreIds);
        isCmd.setInsideContainerSkuAttrIdsSnDefect(insideContainerSkuAttrIdsSnDefect);
        isCmd.setInsideContainerLocSkuAttrIds(insideContainerLocSkuAttrIds);
        isCmd.setInsideContainerVolume(insideContainerVolume);
        isCmd.setInsideContainerWeight(insideContainerWeight);
        isCmd.setInsideContainerAsists(insideContainerAsists);
        return isCmd;
    }

    /**
     * @author lichuan
     * @param cList
     * @param putawayPatternDetailType
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public ContainerStatisticResultCommand sysGuidePutawayContainerStatistic(List<ContainerCommand> icList, Integer putawayPatternDetailType, Long ouId, String logId) {
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        csrCmd.setPutawayPatternDetailType(putawayPatternDetailType);
        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器
        Set<Long> caselevelContainerIds = new HashSet<Long>();// 所有caselevel内部容器
        Set<Long> notcaselevelContainerIds = new HashSet<Long>();// 所有非caselevel内部容器
        Map<Long, String> insideContainerIdsCode = new HashMap<Long, String>();// 所有内部容器及容器号
        // 判断所有内部容器状态
        for (ContainerCommand ic : icList) {
            Long icId = ic.getId();
            Container icCmd = containerDao.findByIdExt(icId, ouId);
            if (null == icCmd) {
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
            Integer icStatus = icCmd.getStatus();
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", icStatus, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
            }
            insideContainerIds.add(icId);
            if (null == insideContainerIdsCode.get(icId)) {
                insideContainerIdsCode.put(icId, icCmd.getCode());
            }
            WhCarton carton = whCartonDao.findWhCaselevelCartonById(icId, ouId);
            if (null != carton) {
                caselevelContainerIds.add(icId);
            } else {
                notcaselevelContainerIds.add(icId);
            }
        }
        csrCmd.setInsideContainerIds(insideContainerIds);
        csrCmd.setCaselevelContainerIds(caselevelContainerIds);
        csrCmd.setNotcaselevelContainerIds(notcaselevelContainerIds);
        csrCmd.setInsideContainerIdsCode(insideContainerIdsCode);
        return csrCmd;
    }


}
