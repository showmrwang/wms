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
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ManMadeContainerStatisticCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentStrategyCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentMsgDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentStrategyDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaPutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.AllocateStrategy;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.utilities.type.StringUtil;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("whSkuInventoryManager")
public class WhSkuInventoryManagerImpl extends BaseInventoryManagerImpl implements WhSkuInventoryManager {
    protected static final Logger log = LoggerFactory.getLogger(WhSkuInventoryManagerImpl.class);
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;
    @Autowired
    private WhSkuInventoryAllocatedDao whSkuInventoryAllocatedDao;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private PdaPutawayCacheManager pdaPutawayCacheManager;
    @Autowired
    private InventoryOccupyManager inventoryOccupyManager;
    @Autowired
    private WhSkuDao skuDao;
    @Autowired
    private WhWaveLineManager whWaveLineManager;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhWaveManager whWaveManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private ReplenishmentStrategyDao replenishmentStrategyDao;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    @Autowired
    private ReplenishmentMsgDao replenishmentMsgDao;
    @Autowired
    private ReplenishmentTaskDao replenishmentTaskDao;
    /**
     * 库位绑定（分配容器库存及生成待移入库位库存）
     * 
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    @Override
    public void binding(List<WhSkuInventoryCommand> invList, Warehouse warehouse, List<LocationRecommendResultCommand> lrrList, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 拆箱上架
            Map<String, Long> invRecommendLocId = new HashMap<String, Long>();
            Map<String, String> invRecommendLocCode = new HashMap<String, String>();
            Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
            for (LocationRecommendResultCommand lrrCmd : lrrList) {
                Long locationId = lrrCmd.getLocationId();
                if (null != locationId) {
                    if (null != locSkuAttrIds.get(locationId)) {
                        Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                        allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                    } else {
                        Set<String> allSkuAttrIds = new HashSet<String>();
                        allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                    }
                }
                String locationCode = lrrCmd.getLocationCode();
                invRecommendLocId.put(lrrCmd.getSkuAttrId(), locationId);
                invRecommendLocCode.put(lrrCmd.getSkuAttrId(), locationCode);
            }
            // 插入待移入库位库存
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
                    inv.setOnHandQty(0.0);
                    Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                    if (null == recommendLocId) {
                        continue;
                    }
                    inv.setLocationId(recommendLocId);
                    try {
                        inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                } else {
                    Map<Long, Set<String>> allLocSkuAttrIds = new HashMap<Long, Set<String>>();
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        String defectBarcode = snCmd.getDefectWareBarcode();
                        String snCode = snCmd.getSn();
                        Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
                        if (null == recommendLocId) {
                            continue;
                        }
                        if (null != allLocSkuAttrIds.get(recommendLocId)) {
                            Set<String> allSkuAttrIds = allLocSkuAttrIds.get(recommendLocId);
                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
                            allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
                        } else {
                            Set<String> allSkuAttrIds = new HashSet<String>();
                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
                            allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
                        }
                    }
                    for (Long locId : allLocSkuAttrIds.keySet()) {
                        Set<String> allSkuAttrIds = allLocSkuAttrIds.get(locId);
                        if (null != allSkuAttrIds && 0 < allSkuAttrIds.size()) {
                            int qty = allSkuAttrIds.size();
                            WhSkuInventory inv = new WhSkuInventory();
                            BeanUtils.copyProperties(invCmd, inv);
                            inv.setId(null);
                            inv.setToBeFilledQty(new Double(qty));// 待移入
                            inv.setOnHandQty(0.0);
                            inv.setLocationId(locId);
                            try {
                                inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                            } catch (Exception e) {
                                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                            }
                            inv.setLastModifyTime(new Date());
                            whSkuInventoryDao.insert(inv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                            // 判断是否需要拆库存
                            if (qty != invCmd.getOnHandQty().intValue()) {
                                WhSkuInventory splitInv = new WhSkuInventory();
                                BeanUtils.copyProperties(invCmd, splitInv);
                                splitInv.setId(null);
                                splitInv.setToBeFilledQty(0.0);
                                splitInv.setOnHandQty(new Double(invCmd.getOnHandQty().intValue() - qty));// 剩余在库数量
                                splitInv.setLocationId(null);
                                whSkuInventoryDao.insert(splitInv);
                                insertGlobalLog(GLOBAL_LOG_INSERT, splitInv, ouId, userId, null, null);
                            }
                        }
                    }
                }
            }
            // 待出容器库存（分配容器库存）
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setAllocatedQty(inv.getOnHandQty());// 已分配
                    inv.setOnHandQty(0.0);
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                } else {
                    Map<Long, Set<String>> allLocSkuAttrIds = new HashMap<Long, Set<String>>();
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        String defectBarcode = snCmd.getDefectWareBarcode();
                        String snCode = snCmd.getSn();
                        Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
                        if (null == recommendLocId) {
                            continue;
                        }
                        if (null != allLocSkuAttrIds.get(recommendLocId)) {
                            Set<String> allSkuAttrIds = allLocSkuAttrIds.get(recommendLocId);
                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
                            allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
                        } else {
                            Set<String> allSkuAttrIds = new HashSet<String>();
                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snCode, defectBarcode));
                            allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
                        }
                    }
                    if (null != allLocSkuAttrIds && 0 < allLocSkuAttrIds.size()) {
                        WhSkuInventory inv = new WhSkuInventory();
                        int qty = allLocSkuAttrIds.size();
                        BeanUtils.copyProperties(invCmd, inv);
                        inv.setAllocatedQty(new Double(qty));// 已分配
                        inv.setOnHandQty(0.0);
                        whSkuInventoryDao.saveOrUpdateByVersion(inv);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    }
                }
            }
        } else {
            // 整托上架、整箱上架
            LocationRecommendResultCommand lrr = lrrList.get(0);
            Long lrrLocId = lrr.getLocationId();
            // String lrrLocCode = lrr.getLocationCode();
            // 插入待移入库位库存
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
                    inv.setOnHandQty(0.0);
                    inv.setLocationId(lrrLocId);
                    try {
                        inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
                    inv.setOnHandQty(0.0);
                    inv.setLocationId(lrrLocId);
                    try {
                        inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        try {
                            sn.setUuid(SkuInventoryUuid.invUuid(inv));
                        } catch (NoSuchAlgorithmException e) {
                            log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
                            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                        }
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                    }
                }
            }
            // 待出容器库存（分配容器库存）
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setAllocatedQty(inv.getOnHandQty());// 已分配
                    inv.setOnHandQty(0.0);
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setAllocatedQty(inv.getOnHandQty());// 已分配
                    inv.setOnHandQty(0.0);
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                }
            }
        }
    }

    /**
     * 库位绑定（扣减容器库存及生成待移入库位库存）
     * 
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    @Override
    public void execBinding(List<WhSkuInventoryCommand> invList, Warehouse warehouse, List<LocationRecommendResultCommand> lrrList, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 拆箱上架
            Map<String, Long> invRecommendLocId = new HashMap<String, Long>();
            Map<String, String> invRecommendLocCode = new HashMap<String, String>();
            Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
            for (LocationRecommendResultCommand lrrCmd : lrrList) {
                Long locationId = lrrCmd.getLocationId();
                if (null != locationId) {
                    if (null != locSkuAttrIds.get(locationId)) {
                        Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                        allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                    } else {
                        Set<String> allSkuAttrIds = new HashSet<String>();
                        allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                        locSkuAttrIds.put(locationId, allSkuAttrIds);
                    }
                }
                String locationCode = lrrCmd.getLocationCode();
                invRecommendLocId.put(lrrCmd.getSkuAttrId(), locationId);
                invRecommendLocCode.put(lrrCmd.getSkuAttrId(), locationCode);
            }
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    // 插入待移入库位库存
                    Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                    if (null == recommendLocId) {
                        continue;
                    }
                    WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setQty(invCmd.getOnHandQty());// 待移入
                    inv.setLocationId(recommendLocId);
                    try {
                        inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryTobefilledDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 可能要拆分库存，推荐逻辑明细行数量(目前整行处理)
                    // 待出容器库存（分配容器库存）
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(invCmd.getUuid(), ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    // 记录出库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    WhSkuInventory invDelete = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, invDelete);
                    whSkuInventoryDao.delete(invDelete.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                } else {
                    String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                    Map<Long, Set<String>> allLocSkuAttrIds = new HashMap<Long, Set<String>>();
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        String defectBarcode = snCmd.getDefectWareBarcode();
                        String snCode = snCmd.getSn();
                        Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(skuAttrId, snCode, defectBarcode));
                        if (null == recommendLocId) {
                            continue;
                        }
                        if (null != allLocSkuAttrIds.get(recommendLocId)) {
                            Set<String> allSkuAttrIds = allLocSkuAttrIds.get(recommendLocId);
                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(skuAttrId, snCode, defectBarcode));
                            allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
                        } else {
                            Set<String> allSkuAttrIds = new HashSet<String>();
                            allSkuAttrIds.add(SkuCategoryProvider.concatSkuAttrId(skuAttrId, snCode, defectBarcode));
                            allLocSkuAttrIds.put(recommendLocId, allSkuAttrIds);
                        }
                    }
                    int totalQty = 0;// 已处理数量
                    for (Long locId : allLocSkuAttrIds.keySet()) {
                        Set<String> allSkuAttrIds = allLocSkuAttrIds.get(locId);
                        if (null != allSkuAttrIds && 0 < allSkuAttrIds.size()) {
                            // 插入待移入库位库存
                            int qty = allSkuAttrIds.size();
                            totalQty += qty;
                            WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                            BeanUtils.copyProperties(invCmd, inv);
                            inv.setId(null);
                            inv.setQty(new Double(qty));// 待移入
                            inv.setLocationId(locId);
                            try {
                                inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                            } catch (Exception e) {
                                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                            }
                            inv.setLastModifyTime(new Date());
                            whSkuInventoryTobefilledDao.insert(inv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                            for (WhSkuInventorySnCommand snCmd : snList) {
                                Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(skuAttrId, snCmd.getSn(), snCmd.getDefectWareBarcode()));
                                if (null == recommendLocId) {
                                    continue;
                                }
                                WhSkuInventorySn sn = new WhSkuInventorySn();
                                BeanUtils.copyProperties(snCmd, sn);
                                sn.setId(null);
                                try {
                                    sn.setUuid(SkuInventoryUuid.invUuid(inv));
                                } catch (NoSuchAlgorithmException e) {
                                    log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
                                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                                }
                                whSkuInventorySnDao.insert(sn);
                                insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                            }
                            // 待出容器库存（分配容器库存）
                            WhSkuInventory invUpdate = new WhSkuInventory();
                            BeanUtils.copyProperties(invCmd, invUpdate);
                            invUpdate.setOnHandQty(new Double(invCmd.getOnHandQty().intValue() - totalQty));// 剩余在库数量
                            whSkuInventoryDao.saveOrUpdateByVersion(invUpdate);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, invUpdate, ouId, userId, null, null);
                            for (WhSkuInventorySnCommand cSnCmd : snList) {
                                Long recommendLocId = invRecommendLocId.get(SkuCategoryProvider.concatSkuAttrId(skuAttrId, cSnCmd.getSn(), cSnCmd.getDefectWareBarcode()));
                                if (null == recommendLocId) {
                                    continue;
                                }
                                WhSkuInventorySn sn = new WhSkuInventorySn();
                                BeanUtils.copyProperties(cSnCmd, sn);
                                whSkuInventorySnDao.delete(sn.getId());
                                insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                            }
                        }
                    }
                    // 判断是否需要拆库存
                    if (totalQty == invCmd.getOnHandQty().intValue()) {
                        Double oldQty = 0.0;
                        if (true == warehouse.getIsTabbInvTotal()) {
                            try {
                                oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(invCmd.getUuid(), ouId);
                            } catch (Exception e) {
                                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                            }
                        } else {
                            oldQty = 0.0;
                        }
                        // 记录出库库存日志(这个实现的有问题)
                        insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                        WhSkuInventory invDelete = new WhSkuInventory();
                        BeanUtils.copyProperties(invCmd, invDelete);
                        whSkuInventoryDao.delete(invDelete.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                    } else {
                        Double oldQty = 0.0;
                        if (true == warehouse.getIsTabbInvTotal()) {
                            try {
                                oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(invCmd.getUuid(), ouId);
                            } catch (Exception e) {
                                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                            }
                        } else {
                            oldQty = 0.0;
                        }
                        // 记录出库库存日志(这个实现的有问题)
                        insertSkuInventoryLog(invCmd.getId(), -(new Double(totalQty)), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                        WhSkuInventory invDelete = new WhSkuInventory();
                        BeanUtils.copyProperties(invCmd, invDelete);
                        whSkuInventoryDao.delete(invDelete.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                        WhSkuInventory splitInv = new WhSkuInventory();
                        BeanUtils.copyProperties(invCmd, splitInv);
                        splitInv.setId(null);
                        splitInv.setOnHandQty(new Double(invCmd.getOnHandQty().intValue() - totalQty));// 剩余在库数量
                        splitInv.setLocationId(null);
                        whSkuInventoryDao.insert(splitInv);
                        insertGlobalLog(GLOBAL_LOG_INSERT, splitInv, ouId, userId, null, null);
                    }
                }
            }

            // 校验待移入库存是否正确
            // TODO
        } else {
            // 整托上架、整箱上架
            LocationRecommendResultCommand lrr = lrrList.get(0);
            Long lrrLocId = lrr.getLocationId();
            // String lrrLocCode = lrr.getLocationCode();
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    // 插入待移入库位库存
                    WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setQty(invCmd.getOnHandQty());// 待移入
                    inv.setLocationId(lrrLocId);
                    try {
                        inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryTobefilledDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 待出容器库存（分配容器库存）
                    Double oldSkuInvOnHandQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(invCmd.getUuid(), ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldSkuInvOnHandQty = 0.0;
                    }
                    // 记录出库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    WhSkuInventory invDelete = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, invDelete);
                    whSkuInventoryDao.delete(invDelete.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                } else {
                    // 插入待移入库位库存
                    WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setQty(invCmd.getOnHandQty());// 待移入
                    inv.setLocationId(lrrLocId);
                    try {
                        inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryTobefilledDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        try {
                            sn.setUuid(SkuInventoryUuid.invUuid(inv));
                        } catch (NoSuchAlgorithmException e) {
                            log.error(getLogMsg("invSn uuid error, logId is:[{}]", new Object[] {logId}), e);
                            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                        }
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                    }
                    // 待出容器库存（分配容器库存）
                    Double oldSkuInvOnHandQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(invCmd.getUuid(), ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldSkuInvOnHandQty = 0.0;
                    }
                    // 记录出库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    WhSkuInventory invDelete = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, invDelete);
                    whSkuInventoryDao.delete(invDelete.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                    for (WhSkuInventorySnCommand cSnCmd : snList) {
                        // insertSkuInventorySnLog(cSnCmd.getUuid(), ouId);
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(cSnCmd, sn);
                        whSkuInventorySnDao.delete(sn.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                    }
                }
            }

            // 校验待移入库存是否正确
            // TODO
        }
    }



    /**
     * 执行上架（已分配容器库存出库及待移入库位库存入库）
     * 
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void putaway(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        Long containerId = null;
        String containerCode = null;
        Long insideContainerId = null;
        String insideContainerCode = null;
        if (null != containerCmd) {
            containerId = containerCmd.getId();
            containerCode = containerCmd.getCode();
        }
        if (null != insideContainerCmd) {
            insideContainerId = insideContainerCmd.getId();
            insideContainerCode = insideContainerCmd.getCode();
        }
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 拆箱上架
            // 1.获取所有待移入库存
            boolean isTV = true;// 是否跟踪容器
            boolean isBM = true;// 是否批次管理
            boolean isVM = true;// 是否管理效期
            Location loc = locationDao.findLocationByCode(locationCode, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            isTV = false;// 拆箱上架默认不跟踪容器号，不管库位是否跟踪容器号
            isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
            isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
            List<WhSkuInventoryCommand> invList = null;
            // 查询所有对应容器号的库存信息
            if (null == insideContainerId) {
                log.error("insideContainerId is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            }
            invList = whSkuInventoryDao.findLocToBeFilledInventoryByInsideContainerIdAndLocId(ouId, insideContainerId, loc.getId());
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", insideContainerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {insideContainerCode});
            }
            // 2.执行上架(一入一出)
            Set<Long> locIds = new HashSet<Long>();
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setToBeFilledQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);

                    Double outboundQty = inv.getOnHandQty();
                    String cUuid = "";
                    WhSkuInventory containerInv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, containerInv);
                    containerInv.setLocationId(null);// 将库位置空
                    try {
                        cUuid = SkuInventoryUuid.invUuid(containerInv);// 得到容器库存对应的uuid
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // 根据uuid查询所有已分配容器库存
                    List<WhSkuInventoryCommand> cInvList = whSkuInventoryDao.findAllocatedContainerInventorysByUuid(ouId, cUuid);
                    Double totalQty = 0.0;
                    for (WhSkuInventoryCommand cInvCmd : cInvList) {
                        Double qty = cInvCmd.getAllocatedQty();
                        totalQty += qty;
                        if (0 <= totalQty.compareTo(outboundQty)) {
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            if (0 == totalQty.compareTo(outboundQty)) {
                                break;
                            }
                        } else {
                            Double remainAllocatedQty = totalQty - outboundQty;
                            Double actualOutboundQty = qty - remainAllocatedQty;
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            // 记录操作日志
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            // 剩余已分配容器库存插入新的库存份
                            WhSkuInventory remainInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, remainInv);
                            remainInv.setAllocatedQty(remainAllocatedQty);
                            remainInv.setId(null);
                            remainInv.setLastModifyTime(new Date());
                            whSkuInventoryDao.insert(remainInv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, remainInv, ouId, userId, null, null);
                            break;
                        }
                    }
                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setToBeFilledQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    // 拆箱上架默认不跟踪容器号
                    inv.setOuterContainerId(null);
                    inv.setInsideContainerId(null);
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    if (!uuid.equals(invCmd.getUuid())) {
                        // uuid发生变更,重新插入sn
                        for (WhSkuInventorySnCommand cSnCmd : snList) {
                            WhSkuInventorySn sn = new WhSkuInventorySn();
                            BeanUtils.copyProperties(cSnCmd, sn);
                            sn.setUuid(inv.getUuid());
                            whSkuInventorySnDao.saveOrUpdate(sn);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                        }
                        // 记录SN日志
                        insertSkuInventorySnLog(inv.getUuid(), ouId);
                    }
                    Double outboundQty = inv.getOnHandQty();
                    String cUuid = "";
                    WhSkuInventory containerInv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, containerInv);
                    containerInv.setLocationId(null);// 将库位置空
                    try {
                        cUuid = SkuInventoryUuid.invUuid(containerInv);// 得到容器库存对应的uuid
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // 根据uuid查询所有已分配容器库存
                    List<WhSkuInventoryCommand> cInvList = whSkuInventoryDao.findAllocatedContainerInventorysByUuid(ouId, cUuid);
                    Double totalQty = 0.0;
                    for (WhSkuInventoryCommand cInvCmd : cInvList) {
                        Double qty = cInvCmd.getAllocatedQty();
                        totalQty += qty;
                        if (0 <= totalQty.compareTo(outboundQty)) {
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            List<WhSkuInventorySnCommand> cSnList = whSkuInventorySnDao.findWhSkuInventoryByUuid(ouId, cInvCmd.getUuid());
                            Double count = 0.0;
                            for (WhSkuInventorySnCommand cSnCmd : cSnList) {
                                count++;
                                if (0 <= count.compareTo(qty)) {
                                    // insertSkuInventorySnLog(cSnCmd.getUuid(), ouId);
                                    WhSkuInventorySn sn = new WhSkuInventorySn();
                                    BeanUtils.copyProperties(cSnCmd, sn);
                                    whSkuInventorySnDao.delete(sn.getId());
                                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                                    if (0 == count.compareTo(qty)) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (0 == totalQty.compareTo(outboundQty)) {
                                break;
                            }
                        } else {
                            Double remainAllocatedQty = totalQty - outboundQty;
                            Double actualOutboundQty = qty - remainAllocatedQty;
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            // 记录操作日志
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            // 剩余已分配容器库存插入新的库存份
                            WhSkuInventory remainInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, remainInv);
                            remainInv.setAllocatedQty(remainAllocatedQty);
                            remainInv.setId(null);
                            remainInv.setLastModifyTime(new Date());
                            whSkuInventoryDao.insert(remainInv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, remainInv, ouId, userId, null, null);
                            List<WhSkuInventorySnCommand> cSnList = whSkuInventorySnDao.findWhSkuInventoryByUuid(ouId, cInvCmd.getUuid());
                            Double count = 0.0;
                            for (WhSkuInventorySnCommand cSnCmd : cSnList) {
                                count++;
                                if (0 <= count.compareTo(actualOutboundQty)) {
                                    // insertSkuInventorySnLog(cSnCmd.getUuid(), ouId);
                                    WhSkuInventorySn sn = new WhSkuInventorySn();
                                    BeanUtils.copyProperties(cSnCmd, sn);
                                    whSkuInventorySnDao.delete(sn.getId());
                                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                                    if (0 == count.compareTo(qty)) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            // 3.上架库存校验
            // TODO

            // 4.如果不跟踪容器号，则上架后需判断是否释放容器
            if (false == isTV) {
                // 判断修改内部容器状态
                int allCounts = whSkuInventoryDao.findAllInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                if (0 == allCounts) {
                    // 找不到库存记录，则认为容器可以释放
                    Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
                    if (null != insideContainer) {
                        // 获取容器状态
                        Integer iContainerStatus = insideContainer.getStatus();
                        // 修改内部容器状态为：上架中，且占用中
                        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                            insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                            insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                            containerDao.saveOrUpdateByVersion(insideContainer);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                        }
                    }
                } else {
                    int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                    int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                    if (0 < rcvdCounts && 0 == toBeFilledCounts) {
                        // 库位待移入库存已全部上架，但还有容器库存未上架
                        Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
                        if (null != insideContainer) {
                            // 获取容器状态
                            Integer iContainerStatus = insideContainer.getStatus();
                            // 修改内部容器状态为：待上架，且占用中
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                                insideContainer.setLifecycle(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                insideContainer.setStatus(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                containerDao.saveOrUpdateByVersion(insideContainer);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                            }
                        }
                    }
                }
                // 判断修改外部容器状态
                // 拆箱上架需要判断所有内部容器全部都已上架才能释放外部容器
                ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
                if (null != containerCmd) {
                    csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
                    if (null == csrCmd) {
                        csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                    }
                    Set<Long> cacheInsideContainerIds = csrCmd.getInsideContainerIds();
                    // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                    TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
                    ArrayDeque<Long> cacheContainerIds = null;
                    if (null != cacheContainerCmd) {
                        cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
                    }
                    boolean isAllTip = isCacheAllExists(cacheInsideContainerIds, cacheContainerIds);
                    if (true == isAllTip) {
                        boolean isUpdateOuterStatusUsable = true;
                        boolean isUpdateOuterStatusCanPutaway = false;
                        for (Long icId : cacheInsideContainerIds) {
                            int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, icId);
                            int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, icId);
                            if (0 < rcvdCounts && 0 == toBeFilledCounts) {
                                // 库位待移入库存已全部上架，但还有容器库存未上架
                                isUpdateOuterStatusUsable = false;
                                isUpdateOuterStatusCanPutaway = true;
                                break;
                            } else if (0 < rcvdCounts && 0 < toBeFilledCounts) {
                                isUpdateOuterStatusUsable = false;
                                break;
                            }
                        }
                        Integer containerStatus = containerCmd.getStatus();
                        if (true == isUpdateOuterStatusUsable) {
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                Container container = new Container();
                                BeanUtils.copyProperties(containerCmd, container);
                                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                containerDao.saveOrUpdateByVersion(container);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                            }
                        } else {
                            if (true == isUpdateOuterStatusCanPutaway) {
                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                    Container container = new Container();
                                    BeanUtils.copyProperties(containerCmd, container);
                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                    containerDao.saveOrUpdateByVersion(container);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // 整托上架、整箱上架
            // 1.获取所有待移入库存
            boolean isTV = true;// 是否跟踪容器
            boolean isBM = true;// 是否批次管理
            boolean isVM = true;// 是否管理效期
            Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
            Location loc = locationDao.findLocationByCode(locationCode, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
            isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
            isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
            List<WhSkuInventoryCommand> invList = null;
            // 根据容器号查询所有库位待移入库存信息
            if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
                if (null == containerId) {
                    log.error("containerId is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                invList = whSkuInventoryDao.findLocToBeFilledInventoryByOuterContainerIdAndLocId(ouId, containerId, loc.getId());
            } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                if (null == insideContainerId) {
                    log.error("insideContainerId is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                invList = whSkuInventoryDao.findLocToBeFilledInventoryByInsideContainerIdAndLocId(ouId, insideContainerId, loc.getId());
            } else {
                log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
            }
            // 2.执行上架(一入一出)
            Set<Long> locIds = new HashSet<Long>();
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setToBeFilledQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    } else {
                        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                            inv.setOuterContainerId(null);// 整箱上架不跟踪外部容器号
                        }
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    Long icId = invCmd.getInsideContainerId();
                    if (null != icId) {
                        insideContainerIds.add(icId);
                    }
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // Double oldSkuInvOnHandQty =
                    // whSkuInventoryDao.findInventorysAllOnHandQtysByUuid(ouId, uuid);
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);

                    Double outboundQty = inv.getOnHandQty();
                    String cUuid = "";
                    WhSkuInventory containerInv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, containerInv);
                    containerInv.setLocationId(null);// 将库位置空
                    try {
                        cUuid = SkuInventoryUuid.invUuid(containerInv);// 得到容器库存对应的uuid
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // 根据uuid查询所有已分配容器库存
                    List<WhSkuInventoryCommand> cInvList = whSkuInventoryDao.findAllocatedContainerInventorysByUuid(ouId, cUuid);
                    Double totalQty = 0.0;
                    for (WhSkuInventoryCommand cInvCmd : cInvList) {
                        Double qty = cInvCmd.getAllocatedQty();
                        totalQty += qty;
                        if (0 <= totalQty.compareTo(outboundQty)) {
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            if (0 == totalQty.compareTo(outboundQty)) {
                                break;
                            }
                        } else {
                            Double remainAllocatedQty = totalQty - outboundQty;
                            Double actualOutboundQty = qty - remainAllocatedQty;
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            // 记录操作日志
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            // 剩余已分配容器库存插入新的库存份
                            WhSkuInventory remainInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, remainInv);
                            remainInv.setAllocatedQty(remainAllocatedQty);
                            remainInv.setId(null);
                            remainInv.setLastModifyTime(new Date());
                            whSkuInventoryDao.insert(remainInv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, remainInv, ouId, userId, null, null);
                            break;
                        }
                    }
                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setToBeFilledQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    Long icId = invCmd.getInsideContainerId();
                    if (null != icId) {
                        insideContainerIds.add(icId);
                    }
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // Double oldSkuInvOnHandQty =
                    // whSkuInventoryDao.findInventorysAllOnHandQtysByUuid(ouId, uuid);
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    if (!uuid.equals(invCmd.getUuid())) {
                        // uuid发生变更,重新插入sn
                        for (WhSkuInventorySnCommand cSnCmd : snList) {
                            WhSkuInventorySn sn = new WhSkuInventorySn();
                            BeanUtils.copyProperties(cSnCmd, sn);
                            sn.setUuid(inv.getUuid());
                            whSkuInventorySnDao.saveOrUpdate(sn);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                        }
                        // 记录SN日志
                        insertSkuInventorySnLog(inv.getUuid(), ouId);
                    }
                    Double outboundQty = inv.getOnHandQty();
                    String cUuid = "";
                    WhSkuInventory containerInv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, containerInv);
                    containerInv.setLocationId(null);// 将库位置空
                    try {
                        cUuid = SkuInventoryUuid.invUuid(containerInv);// 得到容器库存对应的uuid
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // 根据uuid查询所有已分配容器库存
                    List<WhSkuInventoryCommand> cInvList = whSkuInventoryDao.findAllocatedContainerInventorysByUuid(ouId, cUuid);
                    Double totalQty = 0.0;
                    for (WhSkuInventoryCommand cInvCmd : cInvList) {
                        Double qty = cInvCmd.getAllocatedQty();
                        totalQty += qty;
                        if (0 <= totalQty.compareTo(outboundQty)) {
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            List<WhSkuInventorySnCommand> cSnList = whSkuInventorySnDao.findWhSkuInventoryByUuid(ouId, cInvCmd.getUuid());
                            Double count = 0.0;
                            for (WhSkuInventorySnCommand cSnCmd : cSnList) {
                                count++;
                                if (0 <= count.compareTo(qty)) {
                                    // insertSkuInventorySnLog(cSnCmd.getUuid(), ouId);
                                    WhSkuInventorySn sn = new WhSkuInventorySn();
                                    BeanUtils.copyProperties(cSnCmd, sn);
                                    whSkuInventorySnDao.delete(sn.getId());
                                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                                    if (0 == count.compareTo(qty)) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (0 == totalQty.compareTo(outboundQty)) {
                                break;
                            }
                        } else {
                            Double remainAllocatedQty = totalQty - outboundQty;
                            Double actualOutboundQty = qty - remainAllocatedQty;
                            Double oldSkuInvOnHandQty = 0.0;
                            if (true == warehouse.getIsTabbInvTotal()) {
                                try {
                                    oldSkuInvOnHandQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(cUuid, ouId);
                                } catch (Exception e) {
                                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                }
                            } else {
                                oldSkuInvOnHandQty = 0.0;
                            }
                            // 记录出库库存日志
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                            WhSkuInventory cInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, cInv);
                            whSkuInventoryDao.delete(cInv.getId());
                            // 记录操作日志
                            insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                            // 剩余已分配容器库存插入新的库存份
                            WhSkuInventory remainInv = new WhSkuInventory();
                            BeanUtils.copyProperties(cInvCmd, remainInv);
                            remainInv.setAllocatedQty(remainAllocatedQty);
                            remainInv.setId(null);
                            remainInv.setLastModifyTime(new Date());
                            whSkuInventoryDao.insert(remainInv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, remainInv, ouId, userId, null, null);
                            List<WhSkuInventorySnCommand> cSnList = whSkuInventorySnDao.findWhSkuInventoryByUuid(ouId, cInvCmd.getUuid());
                            Double count = 0.0;
                            for (WhSkuInventorySnCommand cSnCmd : cSnList) {
                                count++;
                                if (0 <= count.compareTo(qty)) {
                                    // insertSkuInventorySnLog(cSnCmd.getUuid(), ouId);
                                    WhSkuInventorySn sn = new WhSkuInventorySn();
                                    BeanUtils.copyProperties(cSnCmd, sn);
                                    whSkuInventorySnDao.delete(sn.getId());
                                    insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                                    if (0 == count.compareTo(qty)) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                // 3.上架库存校验
                // TODO

                // 4.上架成功后需要释放容器
                // 修改内部容器状态(整托、整箱上架均修改内部容器状态为已上架且占用中)
                for (Long icId : insideContainerIds) {
                    Container insideContainer = containerDao.findByIdExt(icId, ouId);
                    if (null != insideContainer) {
                        // 获取容器状态
                        Integer iContainerStatus = insideContainer.getStatus();
                        // 修改内部容器状态为：上架中，且占用中
                        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                            insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                            insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                            containerDao.saveOrUpdateByVersion(insideContainer);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                        }
                    }
                }
                // 修改外部容器状态(整托上架不能修改外部容器状态)
                if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    // 整箱上架需要判断所有内部容器全部都已上架即可释放外部容器
                    ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
                    if (null != containerCmd) {
                        csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
                        if (null == csrCmd) {
                            csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                        }
                        Set<Long> cacheInsideContainerIds = csrCmd.getInsideContainerIds();
                        // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                        TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
                        ArrayDeque<Long> cacheContainerIds = null;
                        if (null != cacheContainerCmd) {
                            cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
                        }
                        boolean isAllTip = isCacheAllExists(cacheInsideContainerIds, cacheContainerIds);
                        if (true == isAllTip) {
                            boolean isUpdateOuterStatusUsable = true;
                            boolean isUpdateOuterStatusCanPutaway = false;
                            for (Long icId : cacheInsideContainerIds) {
                                int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, icId);
                                int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, icId);
                                if (0 < rcvdCounts && 0 == toBeFilledCounts) {
                                    // 库位待移入库存已全部上架，但还有容器库存未上架
                                    isUpdateOuterStatusUsable = false;
                                    isUpdateOuterStatusCanPutaway = true;
                                    break;
                                } else if (0 < rcvdCounts && 0 < toBeFilledCounts) {
                                    isUpdateOuterStatusUsable = false;
                                    break;
                                }
                            }
                            Integer containerStatus = containerCmd.getStatus();
                            if (true == isUpdateOuterStatusUsable) {
                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                    Container container = new Container();
                                    BeanUtils.copyProperties(containerCmd, container);
                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                    containerDao.saveOrUpdateByVersion(container);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                }
                            } else {
                                if (true == isUpdateOuterStatusCanPutaway) {
                                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                        Container container = new Container();
                                        BeanUtils.copyProperties(containerCmd, container);
                                        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                        container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                        containerDao.saveOrUpdateByVersion(container);
                                        insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 整托上架，外部容器状态修改为已上架且占用中
                    Integer containerStatus = containerCmd.getStatus();
                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                        Container container = new Container();
                        BeanUtils.copyProperties(containerCmd, container);
                        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                        container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                        containerDao.saveOrUpdateByVersion(container);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                    }
                }
            }
        }
    }
    
    /**
     * 执行上架（已分配容器库存出库及待移入库位库存入库）
     * 
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void execPutaway(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        Long containerId = null;
        String containerCode = null;
        Long insideContainerId = null;
        String insideContainerCode = null;
        if (null != containerCmd) {
            containerId = containerCmd.getId();
            containerCode = containerCmd.getCode();
        }
        if (null != insideContainerCmd) {
            insideContainerId = insideContainerCmd.getId();
            insideContainerCode = insideContainerCmd.getCode();
        }
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 拆箱上架
            // 1.获取所有待移入库存
            boolean isTV = true;// 是否跟踪容器
            boolean isBM = true;// 是否批次管理
            boolean isVM = true;// 是否管理效期
            Location loc = locationDao.findLocationByCode(locationCode, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            isTV = false;// 拆箱上架默认不跟踪容器号，不管库位是否跟踪容器号
            isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
            isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
            List<WhSkuInventoryCommand> invList = null;
            // 查询所有对应容器号的库存信息
            if (null == insideContainerId) {
                log.error("insideContainerId is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            }
            invList = whSkuInventoryDao.findLocToBeFilledInventoryByInsideContainerIdAndLocId(ouId, insideContainerId, loc.getId());
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", insideContainerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {insideContainerCode});
            }
            // 2.执行上架(一入一出)
            Set<Long> locIds = new HashSet<Long>();
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setOnHandQty(invCmd.getToBeFilledQty());// 在库库存
                    inv.setFrozenQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    // 拆箱上架默认不跟踪容器号
                    inv.setOuterContainerId(null);
                    inv.setInsideContainerId(null);
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    inv.setInboundTime(new Date());
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    // 删除待移入库存
                    WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, cInv);
                    whSkuInventoryTobefilledDao.delete(cInv.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);

                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setFrozenQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    // 拆箱上架默认不跟踪容器号
                    inv.setOuterContainerId(null);
                    inv.setInsideContainerId(null);
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    inv.setInboundTime(new Date());
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    // 插入sn
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                    }
                    // 记录SN日志(这个实现的有问题)
                    insertSkuInventorySnLog(inv.getUuid(), ouId);
                    WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, cInv);
                    whSkuInventoryTobefilledDao.delete(cInv.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                    for (WhSkuInventorySnCommand cSnCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(cSnCmd, sn);
                        whSkuInventorySnDao.delete(sn.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                    }
                }
            }
            // 3.上架库存校验
            // TODO

            // 4.如果不跟踪容器号，则上架后需判断是否释放容器
            if (false == isTV) {
                // 判断修改内部容器状态
                int allCounts = whSkuInventoryDao.findAllInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                if (0 == allCounts) {
                    // 找不到库存记录，则认为容器可以释放
                    Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
                    if (null != insideContainer) {
                        // 获取容器状态
                        Integer iContainerStatus = insideContainer.getStatus();
                        // 修改内部容器状态为：上架中，且占用中
                        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                            insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                            insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                            containerDao.saveOrUpdateByVersion(insideContainer);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                        }
                    }
                } else {
                    int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                    int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                    if (0 < rcvdCounts && 0 == toBeFilledCounts) {
                        // 库位待移入库存已全部上架，但还有容器库存未上架
                        Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
                        if (null != insideContainer) {
                            // 获取容器状态
                            Integer iContainerStatus = insideContainer.getStatus();
                            // 修改内部容器状态为：待上架，且占用中
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                                insideContainer.setLifecycle(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                insideContainer.setStatus(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                containerDao.saveOrUpdateByVersion(insideContainer);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                            }
                        }
                    }
                }
                // 判断修改外部容器状态
                // 拆箱上架需要判断所有内部容器全部都已上架才能释放外部容器
                ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
                if (null != containerCmd) {
                    csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
                    if (null == csrCmd) {
                        csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                    }
                    Set<Long> cacheInsideContainerIds = csrCmd.getInsideContainerIds();
                    // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                    TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
                    ArrayDeque<Long> cacheContainerIds = null;
                    if (null != cacheContainerCmd) {
                        cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
                    }
                    boolean isAllTip = isCacheAllExists(cacheInsideContainerIds, cacheContainerIds);
                    if (true == isAllTip) {
                        boolean isUpdateOuterStatusUsable = true;
                        boolean isUpdateOuterStatusCanPutaway = false;
                        for (Long icId : cacheInsideContainerIds) {
                            int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, icId);
                            int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, icId);
                            if (0 < rcvdCounts && 0 == toBeFilledCounts) {
                                // 库位待移入库存已全部上架，但还有容器库存未上架
                                isUpdateOuterStatusUsable = false;
                                isUpdateOuterStatusCanPutaway = true;
                                break;
                            } else if (0 < rcvdCounts && 0 < toBeFilledCounts) {
                                isUpdateOuterStatusUsable = false;
                                break;
                            }
                        }
                        Integer containerStatus = containerCmd.getStatus();
                        if (true == isUpdateOuterStatusUsable) {
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                Container container = new Container();
                                BeanUtils.copyProperties(containerCmd, container);
                                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                containerDao.saveOrUpdateByVersion(container);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                            }
                        } else {
                            if (true == isUpdateOuterStatusCanPutaway) {
                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                    Container container = new Container();
                                    BeanUtils.copyProperties(containerCmd, container);
                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                    containerDao.saveOrUpdateByVersion(container);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // 整托上架、整箱上架
            // 1.获取所有待移入库存
            boolean isTV = true;// 是否跟踪容器
            boolean isBM = true;// 是否批次管理
            boolean isVM = true;// 是否管理效期
            Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
            Location loc = locationDao.findLocationByCode(locationCode, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
            isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
            isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
            List<WhSkuInventoryCommand> invList = null;
            // 根据容器号查询所有库位待移入库存信息
            if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
                if (null == containerId) {
                    log.error("containerId is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                invList = whSkuInventoryDao.findLocToBeFilledInventoryByOuterContainerIdAndLocId(ouId, containerId, loc.getId());
            } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                if (null == insideContainerId) {
                    log.error("insideContainerId is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                invList = whSkuInventoryDao.findLocToBeFilledInventoryByInsideContainerIdAndLocId(ouId, insideContainerId, loc.getId());
            } else {
                log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
            }
            // 2.执行上架(一入一出)
            Set<Long> locIds = new HashSet<Long>();
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setFrozenQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    } else {
                        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                            inv.setOuterContainerId(null);// 整箱上架不跟踪外部容器号
                        }
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    Long icId = invCmd.getInsideContainerId();
                    if (null != icId) {
                        insideContainerIds.add(icId);
                    }
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // Double oldSkuInvOnHandQty =
                    // whSkuInventoryDao.findInventorysAllOnHandQtysByUuid(ouId, uuid);
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    inv.setInboundTime(new Date());
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    // 删除待移入库存
                    WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, cInv);
                    whSkuInventoryTobefilledDao.delete(cInv.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setOnHandQty(inv.getToBeFilledQty());// 在库库存
                    inv.setFrozenQty(0.0);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_LINE_NOT_BINDING_LOC_ERROR);
                    } else {
                        locIds.add(inv.getLocationId());
                    }
                    if (locIds.size() > 1) {
                        log.error("binding location is more than one error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC);
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    Long icId = invCmd.getInsideContainerId();
                    if (null != icId) {
                        insideContainerIds.add(icId);
                    }
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    // Double oldSkuInvOnHandQty =
                    // whSkuInventoryDao.findInventorysAllOnHandQtysByUuid(ouId, uuid);
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    inv.setInboundTime(new Date());
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    // 插入sn
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                    }
                    // 记录SN日志(这个实现的有问题)
                    insertSkuInventorySnLog(inv.getUuid(), ouId);
                    WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                    BeanUtils.copyProperties(invCmd, cInv);
                    whSkuInventoryTobefilledDao.delete(cInv.getId());
                    insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                    for (WhSkuInventorySnCommand cSnCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(cSnCmd, sn);
                        whSkuInventorySnDao.delete(sn.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                    }
                }
            }
            // 3.上架库存校验
            // TODO 

            // 4.上架成功后需要释放容器
            // 修改内部容器状态(整托、整箱上架均修改内部容器状态为已上架且占用中)
            for (Long icId : insideContainerIds) {
                Container insideContainer = containerDao.findByIdExt(icId, ouId);
                if (null != insideContainer) {
                    // 获取容器状态
                    Integer iContainerStatus = insideContainer.getStatus();
                    // 修改内部容器状态为：上架中，且占用中
                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                        insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                        insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                        containerDao.saveOrUpdateByVersion(insideContainer);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                    }
                }
            }
            // 修改外部容器状态(整托上架不能修改外部容器状态)
            if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                // 整箱上架需要判断所有内部容器全部都已上架即可释放外部容器
                ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
                if (null != containerCmd) {
                    csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
                    if (null == csrCmd) {
                        csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                    }
                    Set<Long> cacheInsideContainerIds = csrCmd.getInsideContainerIds();
                    // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                    TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
                    ArrayDeque<Long> cacheContainerIds = null;
                    if (null != cacheContainerCmd) {
                        cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
                    }
                    boolean isAllTip = isCacheAllExists(cacheInsideContainerIds, cacheContainerIds);
                    if (true == isAllTip) {
                        boolean isUpdateOuterStatusUsable = true;
                        boolean isUpdateOuterStatusCanPutaway = false;
                        for (Long icId : cacheInsideContainerIds) {
                            int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, icId);
                            int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, icId);
                            if (0 < rcvdCounts && 0 == toBeFilledCounts) {
                                // 库位待移入库存已全部上架，但还有容器库存未上架
                                isUpdateOuterStatusUsable = false;
                                isUpdateOuterStatusCanPutaway = true;
                                break;
                            } else if (0 < rcvdCounts && 0 < toBeFilledCounts) {
                                isUpdateOuterStatusUsable = false;
                                break;
                            }
                        }
                        Integer containerStatus = containerCmd.getStatus();
                        if (true == isUpdateOuterStatusUsable) {
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                Container container = new Container();
                                BeanUtils.copyProperties(containerCmd, container);
                                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                containerDao.saveOrUpdateByVersion(container);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                            }
                        } else {
                            if (true == isUpdateOuterStatusCanPutaway) {
                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                    Container container = new Container();
                                    BeanUtils.copyProperties(containerCmd, container);
                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                    containerDao.saveOrUpdateByVersion(container);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                }
                            }
                        }
                    }
                }
            } else {
                // 整托上架，外部容器状态修改为已上架且占用中
                Integer containerStatus = containerCmd.getStatus();
                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                    Container container = new Container();
                    BeanUtils.copyProperties(containerCmd, container);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
            }
        }
    }

    private boolean isCacheAllExists(Set<Long> ids, ArrayDeque<Long> cacheKeys) {
        boolean allExists = true;
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (Long id : ids) {
                Long cId = id;
                boolean isExists = false;
                Iterator<Long> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    Long value = iter.next();
                    if (null == value) value = -1L;
                    if (0 == value.compareTo(cId)) {
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

    
    private void manPutwayExistLoc(List<WhSkuInventorySn> skuSnlist,Warehouse warehouse,Double scanSkuQty,WhSkuInventoryCommand whSkuInve,Boolean isBM,Boolean isVM,Long locationId,
                                   Long userId,Long ouId,WhSkuInventoryCommand invCmd){
        String uuid = null;
        List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
        if (null == snList || 0 == snList.size()) {
            Long whskuInventoryId = invCmd.getId(); 
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(whSkuInve, inv);
            if (false == isBM) {
                inv.setBatchNumber(null);
            }
            if (false == isVM) {
                inv.setMfgDate(null);
                inv.setExpDate(null);
            }
            inv.setOccupationCode(null);
            inv.setLocationId(locationId);
            inv.setInboundTime(new Date());
            inv.setIsLocked(false);
            inv.setOnHandQty(scanSkuQty+inv.getOnHandQty());
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == warehouse.getIsTabbInvTotal()) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.saveOrUpdateByVersion(inv); // 更新已经上架的库存记录
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            // 记录入库库存日志
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            Long skuInvId = invCmd.getId();
            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(skuInvId, ouId);
            if(null == whSkuInventory) { 
                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+skuInvId);
                throw new BusinessException(ErrorCodes.NO_SKU_INVENTORY);
            }
            //修改在库库存数量
            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
            // 记录入库库存日志
            insertSkuInventoryLog(whSkuInventory.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            if(skuInvOnHandQty == 0.0) {
                whSkuInventoryDao.deleteWhSkuInventoryById(whskuInventoryId, ouId);   //上架成功后删除，原来的库存记录
                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
            }else{
                whSkuInventory.setOnHandQty(skuInvOnHandQty);
                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory); // 更新库存记录
                insertGlobalLog(GLOBAL_LOG_UPDATE, whSkuInventory, ouId, userId, null, null);
            }
        } else {
            WhSkuInventory inv = new WhSkuInventory();
            Long whskuInventoryId = invCmd.getId();
            BeanUtils.copyProperties(whSkuInve, inv);
            // 拆箱上架默认不跟踪容器号
            inv.setOuterContainerId(null);
            inv.setInsideContainerId(null);
            inv.setLocationId(locationId);
            inv.setOnHandQty(inv.getOnHandQty()+scanSkuQty);
            inv.setInboundTime(new Date());
            inv.setIsLocked(false);
            if (false == isBM) {
                inv.setBatchNumber(null);
            }
            if (false == isVM) {
                inv.setMfgDate(null);
                inv.setExpDate(null);
            }
            inv.setOccupationCode(null);
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == warehouse.getIsTabbInvTotal()) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.saveOrUpdateByVersion(inv);
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            // 记录入库库存日志
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            //修改在库库存数量
            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(whskuInventoryId, ouId);
            if(null == whSkuInventory) {
                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+whskuInventoryId);
                throw new BusinessException(ErrorCodes. NO_SKU_INVENTORY);
            }
            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
            // 记录入库库存日志
            insertSkuInventoryLog(inv.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            if(skuInvOnHandQty == 0.0) {
                whSkuInventoryDao.deleteWhSkuInventoryById(whskuInventoryId, ouId);   //上架完毕后删除原来的库存数据
                insertGlobalLog(GLOBAL_LOG_DELETE, whSkuInventory, ouId, userId, null, null);
            }else{
                whSkuInventory.setOnHandQty(skuInvOnHandQty);
                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory); // 更新库存记录
                insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            }
            if (!uuid.equals(invCmd.getUuid())) {
                    // uuid发生变更,重新插入sn
                    for(WhSkuInventorySn snCmd:skuSnlist) { //已经扫描过的sn
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.saveOrUpdate(sn);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                    }
                    // 记录SN日志
                    insertSkuInventorySnLog(inv.getUuid(), ouId);
           }
        }
    }

    
    public void manPutwayNoLoc(List<WhSkuInventorySn> skuSnlist,Warehouse warehouse,Double scanSkuQty,WhSkuInventoryCommand invCmd,Boolean isBM,Boolean isVM,Long locationId,
                               Long userId,Long ouId){
        String uuid = null;
        List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
        if (null == snList || 0 == snList.size()) {
            Long whskuInventoryId = invCmd.getId();
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            inv.setOuterContainerId(null);
            inv.setInsideContainerId(null);
            if (false == isBM) {
                inv.setBatchNumber(null);
            }
            if (false == isVM) {
                inv.setMfgDate(null);
                inv.setExpDate(null);
            }
            inv.setOccupationCode(null);
            inv.setLocationId(locationId);
            inv.setInboundTime(new Date());
            inv.setIsLocked(false);
            inv.setOnHandQty(scanSkuQty);
            inv.setId(null);
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == warehouse.getIsTabbInvTotal()) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.insert(inv); //插入已上架成功的库存记录
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            // 记录入库库存日志
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            //修改在库库存数量
            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(whskuInventoryId, ouId);
            if(null == whSkuInventory) {
                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+whskuInventoryId);
                throw new BusinessException(ErrorCodes. NO_SKU_INVENTORY);
            }
            // 记录入库库存日志
            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
            insertSkuInventoryLog(whSkuInventory.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            if(skuInvOnHandQty == 0.0) {
                whSkuInventoryDao.deleteWhSkuInventoryById(whskuInventoryId , ouId);   //上架完毕后，删除原来的库存记录
                insertGlobalLog(GLOBAL_LOG_DELETE, whSkuInventory, ouId, userId, null, null);
            }else{
                whSkuInventory.setOnHandQty(skuInvOnHandQty);
                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory); // 更新库存记录
                insertGlobalLog(GLOBAL_LOG_UPDATE, whSkuInventory, ouId, userId, null, null);
            }
           
        } else {
            WhSkuInventory inv = new WhSkuInventory();
            Long whskuInventoryId = invCmd.getId();
            BeanUtils.copyProperties(invCmd, inv);
            // 拆箱上架默认不跟踪容器号
            inv.setOuterContainerId(null);
            inv.setInsideContainerId(null);
            inv.setLocationId(locationId);
            inv.setInboundTime(new Date());
            inv.setIsLocked(false);
            inv.setOnHandQty(scanSkuQty);
            inv.setId(null);
            if (false == isBM) {
                inv.setBatchNumber(null);
            }
            if (false == isVM) {
                inv.setMfgDate(null);
                inv.setExpDate(null);
            }
            inv.setOccupationCode(null);
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == warehouse.getIsTabbInvTotal()) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.insert(inv);
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            // 记录入库库存日志
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            //修改在库库存数量
            Long skuInvId = invCmd.getId();
            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(skuInvId, ouId);
            if(null == whSkuInventory) {
                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+skuInvId);
                throw new BusinessException(ErrorCodes. NO_SKU_INVENTORY);
            }
            // 记录入库库存日志
            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
            insertSkuInventoryLog(whSkuInventory.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
            if(skuInvOnHandQty == 0.0) {
                whSkuInventoryDao.deleteWhSkuInventoryById(whskuInventoryId, ouId);   //上架完毕后删除，原来的库存记录
                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
            }else{
                whSkuInventory.setOnHandQty(skuInvOnHandQty);
                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory); // 更新库存记录
                insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            }
            
            if (!uuid.equals(invCmd.getUuid())) {
                    // uuid发生变更,重新插入sn
                for(WhSkuInventorySn snCmd:skuSnlist) { //已经扫描过的sn
                    WhSkuInventorySn sn = new WhSkuInventorySn();
                    BeanUtils.copyProperties(snCmd, sn);
                    sn.setUuid(inv.getUuid());
                    whSkuInventorySnDao.saveOrUpdate(sn);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                }
               // 记录SN日志
               insertSkuInventorySnLog(inv.getUuid(), ouId);
           }
        }
    }
    /**
     * 人工上架:执行上架
     * 
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void manMadePutaway(Boolean isOuterContainer,Double scanSkuQty,WhSkuInventoryCommand invSkuCmd,ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long locationId, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        Long containerId = null;
        String containerCode = null;
        Long insideContainerId = null;
        if (null != containerCmd) {
                containerId = containerCmd.getId();
            containerCode = containerCmd.getCode();
        }
        if (null != insideContainerCmd) {
            insideContainerId = insideContainerCmd.getId();
        }
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 拆箱上架
            // 1.获取所有待移入库存
            boolean isTV = true;// 是否跟踪容器
            boolean isBM = true;// 是否批次管理
            boolean isVM = true;// 是否管理效期
            Long skuId = invSkuCmd.getSkuId();
            Location loc = locationDao.findByIdExt(locationId, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            isTV = false;// 拆箱上架默认不跟踪容器号，不管库位是否跟踪容器号
            isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
            isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
            // 查询所有对应容器号的库存信息
            if (null == insideContainerId) {
                log.error("insideContainerId is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            }
            List<WhSkuInventoryCommand> invLocList = whSkuInventoryDao.findWhSkuInvCmdByLocationContainerIdIsNull(ouId,locationId);
            List<WhSkuInventorySn> skuSnlist = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_SN, insideContainerId.toString() + skuId.toString());
            // 2.执行上架
            Boolean result = false;
            String  putwaySkuAttrsId = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
            WhSkuInventoryCommand whSkuInvLoc = null;
            for(WhSkuInventoryCommand whSkuInve:invLocList){
                    String  skuAttrsIdLoc = SkuCategoryProvider.getSkuAttrIdByInv(whSkuInve);  
                    if(invLocList.size() != 0) {   //判断是否有库位
                          if(whSkuInve.getSkuId().longValue() == invSkuCmd.getSkuId().longValue() && putwaySkuAttrsId.equals(skuAttrsIdLoc)) {
                                result = true;
                                whSkuInvLoc = whSkuInve;
                                break;
                          }else{
                                result = false;     
                          }
                     }else{
                          result = false;        
                     }
            }
            if(result) {
                this.manPutwayExistLoc(skuSnlist,warehouse, scanSkuQty, whSkuInvLoc, isBM, isVM, locationId, userId, ouId,invSkuCmd);
            }else{
                this.manPutwayNoLoc(skuSnlist,warehouse, scanSkuQty, invSkuCmd, isBM, isVM, locationId, userId, ouId);
            }
            // 如果不跟踪容器号，则上架后需判断是否释放容器
            if (false == isTV) {
                // 判断修改内部容器状态
                int allCounts = whSkuInventoryDao.findAllInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                if (0 == allCounts) {
                    // 找不到库存记录，则认为容器可以释放
                    Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
                    if (null != insideContainer) {
                        // 获取容器状态
                            insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                            insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                            containerDao.saveOrUpdateByVersion(insideContainer);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                    }
                } else {
                    int count = whSkuInventoryDao.findWhSkuInventoryCountByInsideContainerId(ouId, insideContainerCmd.getId());
                    if (0 < count) {
                        // 还有没有上架的内部容器
                        Container insideContainer = containerDao.findByIdExt(insideContainerCmd.getId(), ouId);
                        if (null != insideContainer) {
                            // 获取容器状态
//                            Integer iContainerStatus = insideContainer.getStatus();
                            // 修改内部容器状态为：待上架，且占用中
                                insideContainer.setLifecycle(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                insideContainer.setStatus(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                containerDao.saveOrUpdateByVersion(insideContainer);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                        }
                    }
                }
                // 判断修改外部容器状态
                // 拆箱上架需要判断所有内部容器全部都已上架才能释放外部容器
                ManMadeContainerStatisticCommand manMadeContainer = new ManMadeContainerStatisticCommand();
                if (null != containerCmd) {
                    Container c = new Container();
                    BeanUtils.copyProperties(insideContainerCmd, c);
                    c.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                    c.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                    containerDao.saveOrUpdateByVersion(c);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, c, ouId, userId, null, null);
                    manMadeContainer = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
                    Set<Long> cacheInsideContainerIds = manMadeContainer.getInsideContainerIds();
                    // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                    TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.PDA_MAN_MANDE_SCAN_CONTAINER_QUEUE + containerId.toString());
                    ArrayDeque<Long> cacheContainerIds = null;
                    if (null != cacheContainerCmd) {
                        cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
                    }
                    boolean isAllTip = isCacheAllExists(cacheInsideContainerIds, cacheContainerIds);
                    if (true == isAllTip) {
                        boolean isAllPutaway = false;
                        for (Long icId : cacheInsideContainerIds) {
                            int count = whSkuInventoryDao.findWhSkuInventoryCountByInsideContainerId(ouId, icId);
                            if (0 < count) {
                                // 内部容器还有没有上架的
                                isAllPutaway = true;
                            }
                        }
                        if (false == isAllPutaway) {
                                Container container = new Container();
                                BeanUtils.copyProperties(containerCmd, container);
                                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                containerDao.saveOrUpdateByVersion(container);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                        }
                    }
                    //只扫货箱不扫托盘的情况
                    int outerCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
                    if(0 == outerCount) {   //修改外部容器状态
                            Container container = new Container();
                            BeanUtils.copyProperties(containerCmd, container);
                            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                            container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                            containerDao.saveOrUpdateByVersion(container);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                    }
                }
            }
        } else {
            // 整托上架、整箱上架
            boolean isTV = true;// 是否跟踪容器
            boolean isBM = true;// 是否批次管理
            boolean isVM = true;// 是否管理效期
            Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
            Location loc = locationDao.findByIdExt(locationId, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
            isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
            isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
            List<WhSkuInventoryCommand> invList = null;
            if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
                if (null == containerId) {
                    log.error("containerId is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                invList = whSkuInventoryDao.findContainerOnHandInventoryByOuterContainerId(ouId, containerId);
            } else if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                if (null == insideContainerId) {
                    log.error("insideContainerId is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                }
                invList = whSkuInventoryDao.findContainerOnHandInventoryByInsideContainerId(ouId, insideContainerId);
                if (null == invList || 0 == invList.size()) {
                    invList = whSkuInventoryDao.findContainerOnHandInventoryByOuterContainerId(ouId, containerId);
                }
            } else {
                log.error("param putawayPatternDetailType is invalid, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
            }
            // 2.执行上架
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setLocationId(locationId);
                    if (!StringUtils.isEmpty(containerCode) && isOuterContainer) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    } else {
                        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                            inv.setOuterContainerId(null);// 整箱上架不跟踪外部容器号
                        }
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    inv.setInboundTime(new Date());
                    inv.setIsLocked(false);
                    inv.setLocationId(locationId);
                    Long icId = invCmd.getInsideContainerId();
                    if (null != icId) {
                        insideContainerIds.add(icId);
                    }
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// 更新UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    whSkuInventoryDao.saveOrUpdateByVersion(inv); // 更新库存
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                } else { // 存在SN,残次库存信息
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setLocationId(locationId);
                    if (!StringUtils.isEmpty(containerCode) && isOuterContainer) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (false == isTV) {
                        inv.setOuterContainerId(null);
                        inv.setInsideContainerId(null);
                    }
                    if (false == isBM) {
                        inv.setBatchNumber(null);
                    }
                    if (false == isVM) {
                        inv.setMfgDate(null);
                        inv.setExpDate(null);
                    }
                    inv.setOccupationCode(null);
                    inv.setInboundTime(new Date());
                    inv.setIsLocked(false);
                    inv.setLocationId(locationId);
                    Long icId = invCmd.getInsideContainerId();
                    if (null != icId) {
                        insideContainerIds.add(icId);
                    }
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    Double oldQty = 0.0;
                    if (true == warehouse.getIsTabbInvTotal()) {
                        try {
                            oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                        } catch (Exception e) {
                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    } else {
                        oldQty = 0.0;
                    }
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);
                    if (!uuid.equals(invCmd.getUuid())) {
                        // uuid发生变更,重新插入sn
                        for (WhSkuInventorySnCommand cSnCmd : snList) {
                            WhSkuInventorySn sn = new WhSkuInventorySn();
                            BeanUtils.copyProperties(cSnCmd, sn);
                            sn.setUuid(inv.getUuid());
                            whSkuInventorySnDao.saveOrUpdate(sn); // 更新sn
                            insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                        }
                        insertSkuInventorySnLog(inv.getUuid(), ouId); // 记录sn日志
                    }
                }

                // 上架成功后需要释放容器
                // //整托上架修容器状态
                if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType){
                    // 修改内部容器状态(整托上架均修改内部容器状态为已上架且占用中)
                    for (Long icId : insideContainerIds) {
                        Container insideContainer = containerDao.findByIdExt(icId, ouId);
                        if (null != insideContainer) {
                            // 获取容器状态
                            Integer iContainerStatus = insideContainer.getStatus();
                            // 修改内部容器状态为：上架中，且占用中
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == iContainerStatus) {
                                insideContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                                insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                                containerDao.saveOrUpdateByVersion(insideContainer);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, insideContainer, ouId, userId, null, null);
                            }
                        }
                    }
                    //修改外部容器状态
                    // 整托上架，外部容器状态修改为已上架且占用中
                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerCmd.getStatus()) {
                        Container container = new Container();
                        BeanUtils.copyProperties(containerCmd, container);
                        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                        container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                        containerDao.saveOrUpdateByVersion(container);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                    }
                }
                // 修改外部容器状态(整托上架不能修改外部容器状态)
                if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    // 整箱上架需要判断所有内部容器全部都已上架即可释放外部容器
                    ManMadeContainerStatisticCommand manMadeContainer = new ManMadeContainerStatisticCommand();
                    if (null != containerCmd) {
                       //修改内部容器状态
                        Container c = new Container();
                        BeanUtils.copyProperties(insideContainerCmd, c);
                        c.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                        c.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                        containerDao.saveOrUpdateByVersion(c);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, c, ouId, userId, null, null);
                        manMadeContainer = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
                        Set<Long> cacheInsideContainerIds = manMadeContainer.getInsideContainerIds(); // 整箱上架:存在托盘,所有内部容器id集合
                        // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                        TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.PDA_MAN_MANDE_SCAN_CONTAINER_QUEUE + containerId.toString());
                        ArrayDeque<Long> cacheContainerIds = null;
                        if (null != cacheContainerCmd) {
                            cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds(); // 已经扫描的内部容器,并且上架的内部容器
                        }
                        boolean isAllTip = isCacheAllExists(cacheInsideContainerIds, cacheContainerIds);
                        if (true == isAllTip) {
                            boolean isAllPutaway = false;
                            for (Long icId : cacheInsideContainerIds) {
                                int insideCount = whSkuInventoryDao.findWhSkuInventoryCountByInsideContainerId(ouId, icId);
                                if (0 < insideCount) {
                                    // 内部容器还有没有上架的
                                    isAllPutaway = true;
                                }
                                if (false == isAllPutaway) {
                                    if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerCmd.getStatus()) {
                                        Container container = new Container();
                                        BeanUtils.copyProperties(containerCmd, container);
                                        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                        container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                        containerDao.saveOrUpdateByVersion(container);
                                        insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                    }
                                } 
                            }
                        }
                        //只扫货箱不扫托盘的情况
                        int outerCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
                        if(0 == outerCount) {   //修改外部容器状态
                                Container container = new Container();
                                BeanUtils.copyProperties(containerCmd, container);
                                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                containerDao.saveOrUpdateByVersion(container);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                        }
                       } else {
                        // 外部容器为空,只有一个货箱的情况
                        if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == insideContainerCmd.getStatus()) {
                            Container container = new Container();
                            BeanUtils.copyProperties(insideContainerCmd, container);
                            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                            container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                            containerDao.saveOrUpdateByVersion(container);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                        }
                    }
                }
            }
        }
    }
    
	@Override
	public void allocationInventoryByLine(WhWaveLineCommand whWaveLineCommand, List<AllocateStrategy> rules, Double qty, Warehouse wh, String logId) {
		if (log.isInfoEnabled()) {
			log.info("allocationInventoryByLine start,waveLineId:{},qty:{},logId:{}", whWaveLineCommand.getId(), qty, logId);
		}
		Double actualQty = 0.0;		// 实际占用数量
		Double containerQty = 0.0;	// 容器占用数量
		Double occupyQty = qty;		// 需要占用数量
		String occupyCode = whWaveLineCommand.getOdoCode(); // 占用编码
		Long odoLineId = whWaveLineCommand.getOdoLineId();	// 占用odoLineId
		// 记录静态库位可超分配的库位id
		Boolean isStaticLocation = false;
		Set<String> staticLocationIds = new HashSet<String>();
		// 记录整托占用库位ids
		Set<String> trayIds = new HashSet<String>();
		// 记录整箱占用库位ids
		Set<String> packingCaseIds = new HashSet<String>();
		Long areaId = null;
		// 封装查询库存条件
		WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
		skuCommand.setSkuId(whWaveLineCommand.getSkuId());
		skuCommand.setStoreId(whWaveLineCommand.getStoreId());
		skuCommand.setInvStatus(whWaveLineCommand.getInvStatus());
		skuCommand.setInvType(whWaveLineCommand.getInvType());
		skuCommand.setBatchNumber(whWaveLineCommand.getBatchNumber());
		skuCommand.setMfgDate(whWaveLineCommand.getMfgDate());
		skuCommand.setExpDate(whWaveLineCommand.getExpDate());
		skuCommand.setCountryOfOrigin(whWaveLineCommand.getCountryOfOrigin());
		skuCommand.setInvAttr1(whWaveLineCommand.getInvAttr1());
		skuCommand.setInvAttr2(whWaveLineCommand.getInvAttr2());
		skuCommand.setInvAttr3(whWaveLineCommand.getInvAttr3());
		skuCommand.setInvAttr4(whWaveLineCommand.getInvAttr4());
		skuCommand.setInvAttr5(whWaveLineCommand.getInvAttr5());
	    skuCommand.setMaxExpDate(whWaveLineCommand.getMaxExpDate());
	    skuCommand.setMinExpDate(whWaveLineCommand.getMinExpDate());
	    skuCommand.setOuId(wh.getId());
		for (AllocateStrategy as : rules) {
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			} else {
				isStaticLocation = false;
			}
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));
			skuCommand.setAreaId(as.getAreaId());
			// 策略分配单位中包含托盘出
			if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_TP) && -1 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
				skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_TP);
				List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(as.getStrategyCode(), skuCommand, occupyQty);
				Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, occupyQty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, Constants.ALLOCATE_UNIT_TP, null, isStaticLocation, staticLocationIds, trayIds, packingCaseIds);
				occupyQty -= num;
				actualQty += num;
				containerQty += num;
			}
			// 策略分配单位中包含货箱出
			if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_HX) && -1 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
				skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_HX);
				List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(as.getStrategyCode(), skuCommand, occupyQty);
				Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, occupyQty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, Constants.ALLOCATE_UNIT_HX, null, isStaticLocation, staticLocationIds, trayIds, packingCaseIds);
				occupyQty -= num;
				actualQty += num;
				containerQty += num;
			}
			// 按件查出库存并占用
			if (-1 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
				skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
				List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(as.getStrategyCode(), skuCommand, occupyQty);
				Double num = inventoryOccupyManager.hardAllocateOccupy(skuInvs, occupyQty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh);
				occupyQty -= num;
				actualQty += num;
				if (0 == occupyQty.compareTo(Constants.DEFAULT_DOUBLE)) {
					break;
				}
			}
		}
		// 数量全部占完回写波次明细分配数量
		whWaveLineManager.updateWaveLineByAllocateQty(whWaveLineCommand.getId(), actualQty, containerQty, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, wh.getId(), logId);
		if (log.isInfoEnabled()) {
			log.info("allocationInventoryByLine end,waveLineId:{},actualQty:{},logId:{}", whWaveLineCommand.getId(), actualQty, logId);
		}
	}
	
	@Override
	public void allocationInventoryByLineList(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules, Warehouse wh, String logId) {
		if (null == rules || rules.isEmpty()) {
			log.error("AllocateStrategy is null logId:[{}]", logId);
			return;
		}
		List<WhSkuInventoryCommand> allSkuInvs = new ArrayList<WhSkuInventoryCommand>();
		// 记录静态库位可超分配的库位id
		Set<String> staticLocationIds = new HashSet<String>();
		// 记录整托占用容器ids
		Set<String> trayIds = new HashSet<String>();
		// 记录整箱占用容器ids
		Set<String> packingCaseIds = new HashSet<String>();
		Long waveLineId = null;
		Map<Long, Boolean> replenishedMap = new HashMap<Long, Boolean>();
		for (AllocateStrategy as : rules) {
			Boolean isStaticLocation = false;
			Long areaId = null;
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));	// 分配单位
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			}
			if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(as.getStrategyCode())) {
				areaId = as.getAreaId();
			}
			WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
			for (int j = 0; j < notHaveInvAttrLines.size(); j++) {
				WhWaveLine line = notHaveInvAttrLines.get(j);
				waveLineId = line.getId();
				Long skuId = line.getSkuId();
				Long storeId = line.getStoreId();
				Long invStatus = line.getInvStatus();
				Long ouId = wh.getId();
				Double qty = line.getQty();				// 需要占用数量
				String strategyCode = as.getStrategyCode();
				log.info("Occupy Inventory, skuId:[{}], storeId:[{}], invStutas:[{}], allocateStrategyId:[{}], qty:[{}], ouId:[{}], logId:[{}]", skuId, storeId, invStatus, as.getId(), qty, ouId, logId);
				if (line.getQty().equals(line.getAllocateQty())) {
					continue;
				}
				// 不同的明细行清空上次记录的整托整箱占用库位ids
				if (!line.getId().equals(waveLineId)) {
					if (!trayIds.isEmpty()) {
						trayIds.clear();
					}
					if (!packingCaseIds.isEmpty()) {
						packingCaseIds.clear();
					}
					if (!staticLocationIds.isEmpty()) {
						staticLocationIds.clear();
					}
				}
				// 先到期先出,先到期后出验证是否是有效期商品
				if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(strategyCode)
						|| Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(strategyCode)) {
					Boolean isExpirationSku = skuDao.checkIsExpirationSku(line.getSkuId(), line.getOuId());
					if (isExpirationSku == null || !isExpirationSku) {
						break;
					}
				}
				// 静态库位超分配判断是否有补货阶段, 没有补货阶段跳过
				if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
					Boolean flag = replenishedMap.get(line.getWaveId());
					if (null == flag) {
						String phaseCode = whWaveManager.getNextParseCode(line.getWaveId(), ouId);
						if (!WavePhase.REPLENISHED.equals(phaseCode)) {
							replenishedMap.put(line.getWaveId(), false);
							continue;
						} else {
							replenishedMap.put(line.getWaveId(), true);
						}
					} else if (!flag) {
						continue;
					}
				}
				// 空库位 
				if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(strategyCode)) {
					whWaveLineManager.updateWaveLineByAllocateQty(waveLineId, 0.0, 0.0, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, ouId, logId);
					continue;
				}
				// 封装查询库存条件
				skuCommand.setSkuId(skuId);
				skuCommand.setStoreId(storeId);
				skuCommand.setInvStatus(invStatus);
				skuCommand.setOuId(ouId);
				Double occupyQty = 0.0;					// 实际占用数量
				String occupyCode = line.getOdoCode();	// 占用编码
				Long odoLineId = line.getOdoLineId();	// 占用odoLineId
				Double containerQty = 0.0;
				// 查出此策略下的所有件库存
				if (null == allSkuInvs || allSkuInvs.isEmpty()) {
					skuCommand.setAreaId(as.getAreaId());
					skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
					allSkuInvs = findInventorysByAllocateStrategy(strategyCode, skuCommand, qty);
				}
				// 策略分配单位中包含托盘出
				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_TP) && -1 == Constants.DEFAULT_DOUBLE.compareTo(qty) && null != allSkuInvs && !allSkuInvs.isEmpty()) {
					skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_TP);
					// 查询出是托盘容器的库存份
					List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(strategyCode, skuCommand, qty);
					// 占用库存并把策略下的所有件库存集合(allSkuInvs)扣减
					Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, qty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, Constants.ALLOCATE_UNIT_TP, allSkuInvs, isStaticLocation, staticLocationIds, trayIds, packingCaseIds);
					qty -= num;
					occupyQty += num;
					containerQty += num;
				}
				// 策略分配单位中包含货箱出
				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_HX) && -1 == Constants.DEFAULT_DOUBLE.compareTo(qty) && null != allSkuInvs && !allSkuInvs.isEmpty()) {
					skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_HX);
					// 查询出是货箱容器的库存份
					List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(strategyCode, skuCommand, qty);
					// 占用库存并把策略下的所有件库存集合(allSkuInvs)扣减
					Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, qty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, Constants.ALLOCATE_UNIT_HX, allSkuInvs, isStaticLocation, staticLocationIds, trayIds, packingCaseIds);
					qty -= num;
					occupyQty += num;
					containerQty += num;
				}
				Double num = 0.0;
				// 分配策略为数量最佳匹配
				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_PIECE) && -1 == Constants.DEFAULT_DOUBLE.compareTo(qty) && null != allSkuInvs && !allSkuInvs.isEmpty()) {
					if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(strategyCode)) {
						List<WhSkuInventoryCommand> uuids = findInventoryUuidByBestMatchAndPiece(skuCommand, qty);
						num = inventoryOccupyManager.occupyInvUuids(uuids, qty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, Constants.ALLOCATE_UNIT_HX, allSkuInvs, isStaticLocation, staticLocationIds);
						qty -= num;
						occupyQty += num;
					} else {
						// 按件占用
						num = inventoryOccupyManager.hardAllocateListOccupy(allSkuInvs, qty, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, isStaticLocation, staticLocationIds);
						qty -= num;
						occupyQty += num;
					}
				}
				if (-1 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
					line.setQty(qty);
				} else if (0 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
					notHaveInvAttrLines.remove(j--);
				}
				whWaveLineManager.updateWaveLineByAllocateQty(line.getId(), occupyQty, containerQty, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, ouId, logId);
				if ((null == allSkuInvs || allSkuInvs.isEmpty()) && !Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
					break;
				}
			}
			if (notHaveInvAttrLines.isEmpty()) {
				break;
			}
		}
	}
	
	@Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
	public List<WhSkuInventoryCommand> findInventorysByAllocateStrategy(String strategyCode, WhSkuInventoryCommand whSkuInventoryCommand, Double qty) {
		if (null == whSkuInventoryCommand.getAreaId() || null == whSkuInventoryCommand.getAllocateUnitCodes()) {
			log.error("findInventorysByAllocateStrategy error, param AreaId or AllocateUnitCodes is null");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
		}
		List<WhSkuInventoryCommand> list = new ArrayList<WhSkuInventoryCommand>();
		// 先入先出
		if (Constants.ALLOCATE_STRATEGY_FIRSTINFIRSTOUT.equals(strategyCode)) {
			list = findInventoryUuidByInBoundTime(whSkuInventoryCommand, true);
		}
		// 先入后出
		else if (Constants.ALLOCATE_STRATEGY_FIRSTINLASTOUT.equals(strategyCode)) {
			list = findInventoryUuidByInBoundTime(whSkuInventoryCommand, false);
		}
		// 先到期先出
		else if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(strategyCode)) {
			list = findInventoryUuidByExpTime(whSkuInventoryCommand, true);
		}
		// 后到期先出
		else if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(strategyCode)) {
			list = findInventoryUuidByExpTime(whSkuInventoryCommand, false);
		}
		// 数量最佳匹配
		else if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(strategyCode)) {
			list = findInventoryUuidByBestMatch(whSkuInventoryCommand, qty);
		}
		// 最大存储空间
		else if (Constants.ALLOCATE_STRATEGY_MAXIMUMSTORAGESPACE.equals(strategyCode)) {
			list = findInventoryUuidByAmount(whSkuInventoryCommand, true);
		}
		// 最小拣货次数
		else if (Constants.ALLOCATE_STRATEGY_MINIMUMORDERPICKINGTIMES.equals(strategyCode)) {
			list = findInventoryUuidByAmount(whSkuInventoryCommand, false);
		}
		// 静态库位可超分配
		else if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
			list = findInventoryUuidByStaticLocation(whSkuInventoryCommand, true);
		}
		// 静态库位不可超分配
		else if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONNOTCANASSIGNMENT.equals(strategyCode)) {
			list = findInventoryUuidByStaticLocation(whSkuInventoryCommand, true);
		}
		// 混SKU库位
		else if (Constants.ALLOCATE_STRATEGY_MIXEDSKUSLOCATION.equals(strategyCode)) {
			list = findInventoryUuidByMixedLocation(whSkuInventoryCommand, true);
		}
		// 空库位
		else if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(strategyCode)) {
			
		}
		return list;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByInBoundTime(WhSkuInventoryCommand whSkuInventoryCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
		whSkuInventoryCommand.setPriority(new Boolean(flag));
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByInBoundTime(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByInBoundTime(whSkuInventoryCommand);
		}
		return skuInvs;
	}
    
	public List<WhSkuInventoryCommand> findInventoryUuidByExpTime(WhSkuInventoryCommand whSkuInventoryCommand, boolean flag) {
    	List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
    	whSkuInventoryCommand.setPriority(new Boolean(flag));
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByExpTime(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByExpTime(whSkuInventoryCommand);
		}
		return skuInvs;
	}
    
	public List<WhSkuInventoryCommand> findInventoryUuidByBestMatch(WhSkuInventoryCommand whSkuInventoryCommand, Double qty) {
		List<WhSkuInventoryCommand> skuInvs = null;
		whSkuInventoryCommand.setPriority(Boolean.TRUE);
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whSkuInventoryCommand);
			// 数量最佳计算
			return bestMatchSkuInvs(skuInvs, qty);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			whSkuInventoryCommand.setPriority(Boolean.FALSE);
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whSkuInventoryCommand);
		}
		return skuInvs;
	}

	public List<WhSkuInventoryCommand> findInventoryUuidByAmount(WhSkuInventoryCommand whSkuInventoryCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
		whSkuInventoryCommand.setPriority(new Boolean(flag));
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whSkuInventoryCommand);
		}
		return skuInvs;
	}

	public List<WhSkuInventoryCommand> findInventoryUuidByStaticLocation(WhSkuInventoryCommand whSkuInventoryCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 顺序 true:升序 false:降序
		whSkuInventoryCommand.setPriority(new Boolean(flag));
		whSkuInventoryCommand.setIsStatic(Boolean.TRUE);
		whSkuInventoryCommand.setIsMixStacking(null);
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whSkuInventoryCommand);
		}
		return skuInvs;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByMixedLocation(WhSkuInventoryCommand whSkuInventoryCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 顺序 true:升序 false:降序
		whSkuInventoryCommand.setPriority(new Boolean(flag));
		whSkuInventoryCommand.setIsStatic(null);
		whSkuInventoryCommand.setIsMixStacking(Boolean.TRUE);
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whSkuInventoryCommand);
		}
		return skuInvs;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByBestMatchAndPiece(WhSkuInventoryCommand whSkuInventoryCommand, Double qty) {
		whSkuInventoryCommand.setPriority(Boolean.TRUE);
		List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findInventoryUuid(whSkuInventoryCommand);
		List<WhSkuInventoryCommand> bestMatchList = bestMatchSkuInvs(skuInvs, qty);
		return bestMatchList;
	}
	
	/**
	 * 数量最佳匹配,返回最佳匹配的库存集合
	 */
	private List<WhSkuInventoryCommand> bestMatchSkuInvs(List<WhSkuInventoryCommand> skuInvs, Double qty) {
		List<WhSkuInventoryCommand> invBestList = new ArrayList<WhSkuInventoryCommand>();
		Double num = 0.0;
		for (int i = 0; i < skuInvs.size(); i++) {
			WhSkuInventoryCommand skuInv = skuInvs.get(i);
			if (0 == skuInv.getSumOnHandQty().compareTo(qty)) {
				invBestList.add(skuInv);
				return invBestList;
			}
			num += skuInv.getSumOnHandQty();
		}
		if (-1 == num.compareTo(qty)) {
			return skuInvs;
		}
		num = 0.0;
		for (int i = 0; i < skuInvs.size(); i++) {
			WhSkuInventoryCommand skuInv = skuInvs.get(i);
			if (-1 == skuInv.getSumOnHandQty().compareTo(qty)) {
				invBestList.add(0, skuInv);
				num += skuInv.getSumOnHandQty();
				if (0 == num.compareTo(qty)) {
					return invBestList;
				} else if (1 == num.compareTo(qty)) {
					break;
				}
			} else if (1 == skuInv.getSumOnHandQty().compareTo(qty)) {
				break;
			}
		}
		invBestList.clear();
		for (int i = skuInvs.size() - 1; i >= 0; i--) {
			invBestList.add(skuInvs.get(i));
		}
		return invBestList;
	}

	@Override
	public void releaseInventoryByOdoId(Long odoId, Warehouse wh) {
		Long ouId = wh.getId();
		WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
		String occupyCode = odo.getOdoCode();
		List<WhSkuInventory> occupyInventory = whSkuInventoryDao.findOccupyInventory(occupyCode, ouId);
		// 还原库存日志
		for (WhSkuInventory skuInv : occupyInventory) {
			Long invId = skuInv.getId();
			Double qty = skuInv.getOnHandQty();
			Double oldQty = 0.0;
            if (true == wh.getIsTabbInvTotal()) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(skuInv.getUuid(), ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
			insertSkuInventoryLog(invId, qty, oldQty, wh.getIsTabbInvTotal(), ouId, 1L);
		}
		// 清除库存占用编码
		whSkuInventoryDao.releaseInventoryOccupyCode(occupyCode, ouId);
	}
	
	/**
     * 根据参数查询出库存信息
     * @author qiming.liu
     * @param whSkuInventory
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventory> findWhSkuInventoryListByPramas(WhSkuInventory whSkuInventory) {
        List<WhSkuInventory> whSkuInventoryList = whSkuInventoryDao.getSkuInvListByPramas(whSkuInventory);
        return whSkuInventoryList;
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
	public void replenishmentToLines(List<WhWaveLine> lines, String bhCode, Map<String, List<ReplenishmentRuleCommand>> ruleMap, Map<String, String> map, Warehouse wh) {
    	Long ouId = wh.getId();
    	// tempMap用来存储这一组明细优化数据, 当这一组明细全部补货成功之后再回传给map
    	Map<String, String> tempMap = new HashMap<String, String>(map);
    	for (WhWaveLine line : lines) {
    		Long skuId = line.getSkuId();
			Long areaId = line.getAreaId();
			Double occupyQty = line.getQty() - line.getAllocateQty();		// 需要占用数量
    		String occupyCode = line.getOdoCode();
    		Long occupyLineId = line.getOdoLineId();
			Boolean isStatic = (null == line.getIsStaticLocationAllocate()) ? false : line.getIsStaticLocationAllocate();
			List<ReplenishmentRuleCommand> repRules = ruleMap.get(skuId + "_" + areaId + "_" + isStatic);
			
			FLAG : for (ReplenishmentRuleCommand rule : repRules) {
				Long ruleId = rule.getId();
				Long targetLocation = rule.getLocationId();
				List<ReplenishmentStrategyCommand> replenishmentStrategyList = rule.getReplenishmentStrategyCommandList();
				String key = skuId + "_" + rule.getId();
				String data = tempMap.get(key);
				Double moreQty = 0.0;	// 补货超出数量
	    		// 得到匹配补货数据优化条件
	    		String startRepStrategy = Constants.ALLOCATE_STRATEGY_FIRSTINFIRSTOUT;
	    		Double bhMoreQty = 0.0;
	    		if (null != data) {
					String[] condition = data.split("_");
					startRepStrategy = condition[0];
					bhMoreQty = new Double(condition[1]);
					if (1 != occupyQty.compareTo(bhMoreQty)) {
						Double tempQty = occupyQty;
						List<WhSkuInventoryTobefilled> tobefilledList = whSkuInventoryTobefilledDao.findNotOccupyListBySkuIdAndBhCode(skuId, bhCode, ouId);
						for (WhSkuInventoryTobefilled tobefilled : tobefilledList) {
							if (-1 == tempQty.compareTo(tobefilled.getQty())) {
								tobefilled.setQty(tobefilled.getQty() - tempQty);
								whSkuInventoryTobefilledDao.saveOrUpdate(tobefilled);
								WhSkuInventoryTobefilled newTobefilled = new WhSkuInventoryTobefilled();
								BeanUtils.copyProperties(tobefilled, newTobefilled);
								newTobefilled.setId(null);
								newTobefilled.setOccupationCode(occupyCode);
								newTobefilled.setOccupationLineId(occupyLineId);
								newTobefilled.setQty(tempQty);
								whSkuInventoryTobefilledDao.insert(newTobefilled);
								break;
							} else {
								tobefilled.setOccupationCode(occupyCode);
								tobefilled.setOccupationLineId(occupyLineId);
								whSkuInventoryTobefilledDao.saveOrUpdate(tobefilled);
								tempQty -= tobefilled.getQty();
							}
						}
						tempQty = occupyQty;
						List<WhSkuInventoryAllocated> allocatedList = whSkuInventoryAllocatedDao.findNotOccupyListBySkuIdAndBhCode(skuId, bhCode, ouId);
						for (WhSkuInventoryAllocated allocated : allocatedList) {
							if (-1 == tempQty.compareTo(allocated.getQty())) {
								allocated.setQty(allocated.getQty() - tempQty);
								whSkuInventoryAllocatedDao.saveOrUpdate(allocated);
								WhSkuInventoryAllocated newAllocated = new WhSkuInventoryAllocated();
								BeanUtils.copyProperties(allocated, newAllocated);
								newAllocated.setId(null);
								newAllocated.setOccupationCode(occupyCode);
								newAllocated.setOccupationLineId(occupyLineId);
								newAllocated.setQty(tempQty);
								whSkuInventoryAllocatedDao.insert(newAllocated);
								break;
							} else {
								allocated.setOccupationCode(occupyCode);
								allocated.setOccupationLineId(occupyLineId);
								whSkuInventoryAllocatedDao.saveOrUpdate(allocated);
								tempQty -= allocated.getQty();
							}
						}
						bhMoreQty -= occupyQty;
						occupyQty = 0.0;
						tempMap.put(key, startRepStrategy + "_" + bhMoreQty);
						break;
					}
				}
	    		if (null == replenishmentStrategyList || replenishmentStrategyList.isEmpty()) {
					continue;
				}
    			for (ReplenishmentStrategyCommand rsc : replenishmentStrategyList) {
    				// 匹配补货数据优化条件中得到  从开始补货策略ID开始匹配
    				if (StringUtils.hasText(startRepStrategy) && Integer.parseInt(rsc.getStrategyCode()) < Integer.parseInt(startRepStrategy)) {
    					continue;
					}
    				if (StringUtils.isEmpty(rsc.getAllocateUnitCodes())) {
    					continue;
    				}
    				// 不支持静态库位超分配和空库位策略
    				if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(rsc.getStrategyCode())
    						|| Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(rsc.getStrategyCode())) {
    					continue;
					}
    				// 先到期先出,先到期后出验证是否是有效期商品
    				if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(rsc.getStrategyCode())
    						|| Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(rsc.getStrategyCode())) {
    					Boolean isExpirationSku = skuDao.checkIsExpirationSku(line.getSkuId(), line.getOuId());
    					if (isExpirationSku == null || !isExpirationSku) {
    						continue;
    					}
    				}
    				List<String> allocateUnitCodes = Arrays.asList(rsc.getAllocateUnitCodes().split(","));
    				// 封装查询条件
    				WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
    				skuCommand.setSkuId(line.getSkuId());
    				skuCommand.setStoreId(line.getStoreId());
    				skuCommand.setInvStatus(line.getInvStatus());
    				skuCommand.setAreaId(rsc.getAreaId());
    				skuCommand.setOuId(ouId);
    				// 策略分配单位中包含托盘出
    				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_TP)) {
    					skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_TP);
    					List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, occupyQty);
    					if (null != uuids && !uuids.isEmpty()) {
    						// 向上补货
    						if (Constants.REPLENISHMENT_UP.equals(rsc.getReplenishmentCode())) {
    							for (WhSkuInventoryCommand invCmd : uuids) {
    								Double onHandQty = invCmd.getSumOnHandQty();
    								// 占用数量 >= 在库数量
    								if (-1 != occupyQty.compareTo(onHandQty)) {
    									List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
    									for (WhSkuInventoryCommand invCommand : invs) {
    										// 创建已分配库存,待移入库存
    										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    									}
    									occupyQty -= onHandQty;
    								} else {
    									List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
    									for (WhSkuInventoryCommand invCommand : invs) {
    										if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    											// 创建已分配库存,待移入库存
    											createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    											moreQty += invCommand.getOnHandQty();
    										} else {
    											if (-1 != occupyQty.compareTo(invCommand.getOnHandQty())) {
    												// 创建已分配库存,待移入库存
    												createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    												occupyQty -= invCommand.getOnHandQty();
    											} else {
    												// 创建已分配库存,待移入库存
    												createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
    												invCommand.setOnHandQty(invCommand.getOnHandQty() - occupyQty);
    												createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    												moreQty += invCommand.getOnHandQty();
    												occupyQty = 0.0;
    											}
    										}
    									}
    								}
    								if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    									startRepStrategy = rsc.getStrategyCode();
    									tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    									break FLAG;
    								}
    							}
    							// 向下补货和严格按照需求量补货
    						} else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
    							for (WhSkuInventoryCommand invCmd : uuids) {
    								Double onHandQty = invCmd.getSumOnHandQty();
    								// 占用数量 >= 在库数量
    								if (-1 != occupyQty.compareTo(onHandQty)) {
    									List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
    									for (WhSkuInventoryCommand invCommand : invs) {
    										// 创建已分配库存,待移入库存
    										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    									}
    									occupyQty -= onHandQty;
    								}
    								if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    									startRepStrategy = rsc.getStrategyCode();
    									tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    									break FLAG;
    								}
    							}
    						}
    					}
    				}
    				// 策略分配单位中包含货箱出
    				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_HX)) {
    					skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_HX);
    					List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, occupyQty);
    					if (null != uuids && !uuids.isEmpty()) {
    						// 向上补货
    						if (Constants.REPLENISHMENT_UP.equals(rsc.getReplenishmentCode())) {
    							for (WhSkuInventoryCommand invCmd : uuids) {
    								Double onHandQty = invCmd.getSumOnHandQty();
    								// 占用数量 >= 在库数量
    								if (-1 != occupyQty.compareTo(onHandQty)) {
    									invCmd.setOuId(wh.getId());
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
    									for (WhSkuInventoryCommand invCommand : invs) {
    										// 创建已分配库存,待移入库存
    										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    									}
    									occupyQty -= onHandQty;
    								} else {
    									invCmd.setOuId(wh.getId());
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
    									for (WhSkuInventoryCommand invCommand : invs) {
    										if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    											// 创建待移入库存
    											createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    											moreQty += invCommand.getOnHandQty();
    										} else {
    											if (-1 != occupyQty.compareTo(invCommand.getOnHandQty())) {
    												// 创建已分配库存,待移入库存
    												createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    												occupyQty -= invCommand.getOnHandQty();
    											} else {
    												// 创建已分配库存,待移入库存
    												createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
    												invCommand.setOnHandQty(invCommand.getOnHandQty() - occupyQty);
    												createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    												moreQty += invCommand.getOnHandQty();
    												occupyQty = 0.0;
    											}
    										}
    									}
    								}
    								if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    									startRepStrategy = rsc.getStrategyCode();
    									tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    									break FLAG;
    								}
    							}
    						} 
    						// 向下补货和严格按照需求量补货
    						else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
    							for (WhSkuInventoryCommand invCmd : uuids) {
    								Double onHandQty = invCmd.getSumOnHandQty();
    								// 占用数量 >= 在库数量
    								if (-1 != occupyQty.compareTo(onHandQty)) {
    									List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
    									for (WhSkuInventoryCommand invCommand : invs) {
    										// 创建已分配库存,待移入库存
    										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    									}
    									occupyQty -= onHandQty;
    								}
    								if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    									startRepStrategy = rsc.getStrategyCode();
    									tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    									break FLAG;
    								}
    							}
    						}
    					}
    				}
    				// 策略分配单位中包含件出
    				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_PIECE)) {
    					skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
    					if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(rsc.getStrategyCode())) {
    						List<WhSkuInventoryCommand> invs = findInventoryUuidByBestMatchAndPiece(skuCommand, occupyQty);
    						for (WhSkuInventoryCommand invCmd : invs) {
    							invCmd.setOuId(wh.getId());
    							List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
    							if (null != skuInvs && !skuInvs.isEmpty()) {
    								for (WhSkuInventoryCommand invCommand : skuInvs) {
    									Double onHandQty = invCommand.getOnHandQty();
    									// 占用数量 >= 在库数量
    									if (-1 != occupyQty.compareTo(onHandQty)) {
    										// 创建已分配库存,待移入库存
    										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
    										occupyQty -= onHandQty;
    									} else {
    										// 创建已分配库存,待移入库存
    										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
    										occupyQty = 0.0;
    									}
    									if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    										startRepStrategy = rsc.getStrategyCode();
    										tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    										break FLAG;
    									}
    								}
    							}
							}
						} else {
							List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, occupyQty);
							if (null != skuInvs && !skuInvs.isEmpty()) {
								for (WhSkuInventoryCommand invCommand : skuInvs) {
									Double onHandQty = invCommand.getOnHandQty();
									// 占用数量 >= 在库数量
									if (-1 != occupyQty.compareTo(onHandQty)) {
										// 创建已分配库存,待移入库存
										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_DELETE);
										occupyQty -= onHandQty;
									} else {
										// 创建已分配库存,待移入库存
										createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
										occupyQty = 0.0;
									}
									if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
										startRepStrategy = rsc.getStrategyCode();
										tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
										break FLAG;
									}
								}
							}
						}
    				}
    			}
			}
			if (-1 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
				log.info("replenishmentToLines is error, replenishment qty is not enough, occupyQty:[{}], waveId:[{}], odoId:[{}], skuId:[{}], ouId:[{}], logId:[{}]", occupyQty, line.getWaveId(), line.getOdoId(), skuId, ouId, logId);
				throw new BusinessException(0);
			}
		}
    		
    	map.clear();
    	map.putAll(tempMap);
	}

    private void createSkuInventoryAllocatedAndTobefilled(WhSkuInventoryCommand invCommand, String occupyCode, String bhCode, Long occupyLineId, Long locationId, Warehouse wh, Double qty, Long ruleId, String type) {
    	Long ouId = wh.getId();
    	WhSkuInventoryAllocated allocated = new WhSkuInventoryAllocated();
		BeanUtils.copyProperties(invCommand, allocated);
		allocated.setId(null);
		allocated.setOccupationCode(occupyCode);
		allocated.setOccupationLineId(occupyLineId);
		allocated.setReplenishmentCode(bhCode);
		if (GLOBAL_LOG_UPDATE.equals(type)) {
			allocated.setQty(qty);// 待移入
		} else if (GLOBAL_LOG_DELETE.equals(type)) {
			allocated.setQty(invCommand.getOnHandQty());// 待移入
		}
		allocated.setLastModifyTime(new Date());
		allocated.setReplenishmentRuleId(ruleId);
		whSkuInventoryAllocatedDao.insert(allocated);
		insertGlobalLog(GLOBAL_LOG_INSERT, allocated, ouId, 2L, null, null);
		
		WhSkuInventoryTobefilled tobefilled = new WhSkuInventoryTobefilled();
		BeanUtils.copyProperties(invCommand, tobefilled);
		tobefilled.setId(null);
		tobefilled.setOccupationCode(occupyCode);
		tobefilled.setOccupationLineId(occupyLineId);
		tobefilled.setReplenishmentCode(bhCode);
		if (GLOBAL_LOG_UPDATE.equals(type)) {
			tobefilled.setQty(qty);// 待移入
		} else if (GLOBAL_LOG_DELETE.equals(type)) {
			tobefilled.setQty(invCommand.getOnHandQty());// 待移入
		}
		// 带移入存入新的库位id, 并重新计算uuid
		tobefilled.setLocationId(locationId);
		 try {
             tobefilled.setUuid(SkuInventoryUuid.invUuid(tobefilled));// UUID
         } catch (Exception e) {
             log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
             throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
         }
		whSkuInventoryTobefilledDao.insert(tobefilled);
		insertGlobalLog(GLOBAL_LOG_INSERT, tobefilled, ouId, 2L, null, null);
		
        Double oldQty = 0.0;
        if (true == wh.getIsTabbInvTotal()) {
            try {
                oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(invCommand.getUuid(), ouId);
            } catch (Exception e) {
                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
        } else {
            oldQty = 0.0;
        }
        if (GLOBAL_LOG_UPDATE.equals(type)) {
        	// 记录出库库存日志
            insertSkuInventoryLog(invCommand.getId(), -qty, oldQty, wh.getIsTabbInvTotal(), ouId, 2L);
            // 更改库存记录
            WhSkuInventory invUpdate = whSkuInventoryDao.findWhSkuInventoryById(invCommand.getId(), ouId);
            invUpdate.setOnHandQty(invUpdate.getOnHandQty() - qty);
            whSkuInventoryDao.saveOrUpdateByVersion(invUpdate);
            insertGlobalLog(GLOBAL_LOG_UPDATE, invUpdate, ouId, 2L, null, null);	
		} else if (GLOBAL_LOG_DELETE.equals(type)) {
			// 记录出库库存日志
	        insertSkuInventoryLog(invCommand.getId(), -invCommand.getOnHandQty(), oldQty, wh.getIsTabbInvTotal(), ouId, 2L);
	        // 删除库存记录
	        WhSkuInventory invDelete = new WhSkuInventory();
	        BeanUtils.copyProperties(invCommand, invDelete);
	        whSkuInventoryDao.delete(invDelete.getId());
	        insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, 2L, null, null);
		}
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public double findInventoryByLocation(Long locationId,Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = this.whSkuInventoryDao.findWhSkuInventoryByLocIdAndOuId(ouId, locationId);
        double qty = Constants.DEFAULT_DOUBLE;
        if (skuInvList != null && skuInvList.size() > 0) {
            for (WhSkuInventoryCommand c : skuInvList) {
                qty += c.getOnHandQty();
            }
        }

        List<WhSkuInventoryTobefilled> toBeFilledList = this.whSkuInventoryTobefilledDao.findLocWhSkuInventoryTobefilled(locationId, ouId);
        if (toBeFilledList != null && toBeFilledList.size() > 0) {
            for (WhSkuInventoryTobefilled t : toBeFilledList) {
                qty += t.getQty();
            }
        }
        return qty;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long findSkuInInventoryByLocation(Long locationId, Long ouId) {
        List<WhSkuInventoryCommand> skuInvList = this.whSkuInventoryDao.findWhSkuInventoryByLocIdAndOuId(ouId, locationId);
        if (skuInvList == null || skuInvList.size() == 0) {
            return null;
        }
        return skuInvList.get(0).getSkuId();
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void replenishmentToLocation(ReplenishmentMsg msg, ReplenishmentRuleCommand rule, Warehouse wh) {
        String replenishmentCode = this.codeManager.generateCode(Constants.WMS, Constants.BH_MODEL_URL, null, null, null);// 补货编码
        Long ouId = wh.getId();
        Long skuId = msg.getSkuId();
        Double upperLimitQty = msg.getUpperLimitQty().doubleValue();
        Long locationId = msg.getLocationId();
        Long ruleId = rule.getId();

        List<ReplenishmentStrategyCommand> strategyList = this.replenishmentStrategyDao.findCommandByRuleId(rule.getId(), ouId);
        for (ReplenishmentStrategyCommand rsc : strategyList) {
            if (StringUtils.isEmpty(rsc.getAllocateUnitCodes())) {
                continue;
            }
            // 不支持静态库位超分配和空库位策略
            if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(rsc.getStrategyCode()) || Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(rsc.getStrategyCode())) {
                continue;
            }
            // 先到期先出,先到期后出验证是否是有效期商品
            if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(rsc.getStrategyCode()) || Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(rsc.getStrategyCode())) {
                Boolean isExpirationSku = skuDao.checkIsExpirationSku(skuId, ouId);
                if (isExpirationSku == null || !isExpirationSku) {
                    continue;
                }
            }


            // 策略COde
            String strategyCode = rsc.getStrategyCode();
            // 策略应用对象:托盘，货箱，件
            // 托盘
            String allocateUnitCodes = "," + rsc.getAllocateUnitCodes() + ",";
            if (allocateUnitCodes.contains("," + Constants.ALLOCATE_UNIT_TP + ",")) {
                WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
                skuCommand.setSkuId(skuId);
                skuCommand.setAreaId(rsc.getAreaId());
                skuCommand.setOuId(ouId);
                skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_TP);
                List<WhSkuInventoryCommand> uuids = this.findInventorysByAllocateStrategy(strategyCode, skuCommand, upperLimitQty);
                if (uuids == null || uuids.size() == 0) {

                } else {
                    if (Constants.REPLENISHMENT_UP.equals(rsc.getReplenishmentCode())) {
                        for (WhSkuInventoryCommand invCmd : uuids) {
                            Double onHandQty = invCmd.getSumOnHandQty();
                            if (upperLimitQty > onHandQty) {
                                List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
                                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
                                for (WhSkuInventoryCommand invCommand : invs) {
                                    // 创建已分配库存,待移入库存
                                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                }
                                upperLimitQty -= onHandQty;
                            } else {
                                List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
                                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
                                for (WhSkuInventoryCommand invCommand : invs) {
                                    if (upperLimitQty.doubleValue() == 0) {
                                        // 创建已分配库存,待移入库存
                                        createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                    } else {
                                        if (upperLimitQty > invCommand.getOnHandQty()) {
                                            // 创建已分配库存,待移入库存
                                            createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                            upperLimitQty -= invCommand.getOnHandQty();
                                        } else {
                                            // 创建已分配库存,待移入库存
                                            createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                                            invCommand.setOnHandQty(invCommand.getOnHandQty() - upperLimitQty);
                                            createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                            upperLimitQty = 0.0;
                                        }
                                    }
                                }
                            }
                            if (upperLimitQty == 0) {
                                break;
                            }
                        }
                    } else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
                        for (WhSkuInventoryCommand invCmd : uuids) {
                            Double onHandQty = invCmd.getSumOnHandQty();
                            // 占用数量 >= 在库数量
                            if (upperLimitQty >= onHandQty) {
                                List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
                                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
                                for (WhSkuInventoryCommand invCommand : invs) {
                                    // 创建已分配库存,待移入库存
                                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                }
                                upperLimitQty -= onHandQty;
                            }
                            if (upperLimitQty == 0) {
                                break;
                            }
                        }
                    }


                }
            }
            if (upperLimitQty == 0) {
                break;
            }
            // 货箱
            if (allocateUnitCodes.contains("," + Constants.ALLOCATE_UNIT_HX + ",")) {
                WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
                skuCommand.setSkuId(skuId);
                skuCommand.setAreaId(rsc.getAreaId());
                skuCommand.setOuId(ouId);
                skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_HX);
                List<WhSkuInventoryCommand> uuids = this.findInventorysByAllocateStrategy(strategyCode, skuCommand, upperLimitQty);

                if (null != uuids && !uuids.isEmpty()) {
                    // 向上补货
                    if (Constants.REPLENISHMENT_UP.equals(rsc.getReplenishmentCode())) {
                        for (WhSkuInventoryCommand invCmd : uuids) {
                            Double onHandQty = invCmd.getSumOnHandQty();
                            // 占用数量 >= 在库数量
                            if (upperLimitQty >= onHandQty) {
                                invCmd.setOuId(wh.getId());
                                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
                                for (WhSkuInventoryCommand invCommand : invs) {
                                    // 创建已分配库存,待移入库存
                                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                }
                                upperLimitQty -= onHandQty;
                            } else {
                                invCmd.setOuId(wh.getId());
                                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
                                for (WhSkuInventoryCommand invCommand : invs) {
                                    if (upperLimitQty == 0) {
                                        // 创建待移入库存
                                        createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                    } else {
                                        if (upperLimitQty >= invCommand.getOnHandQty()) {
                                            // 创建已分配库存,待移入库存
                                            createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                            upperLimitQty -= invCommand.getOnHandQty();
                                        } else {
                                            // 创建已分配库存,待移入库存
                                            createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                                            invCommand.setOnHandQty(invCommand.getOnHandQty() - upperLimitQty);
                                            createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                            upperLimitQty = 0.0;
                                        }
                                    }
                                }
                            }
                            if (upperLimitQty == 0) {
                                break;
                            }
                        }
                        // 向下补货和严格按照需求量补货
                    } else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
                        for (WhSkuInventoryCommand invCmd : uuids) {
                            Double onHandQty = invCmd.getSumOnHandQty();
                            // 占用数量 >= 在库数量
                            if (upperLimitQty >= onHandQty) {
                                List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
                                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
                                for (WhSkuInventoryCommand invCommand : invs) {
                                    // 创建已分配库存,待移入库存
                                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                }
                                upperLimitQty -= onHandQty;
                            }
                            if (upperLimitQty == 0) {
                                break;
                            }
                        }
                    }
                }


            }
            if (upperLimitQty == 0) {
                break;
            }
            // 件
            if (allocateUnitCodes.contains("," + Constants.ALLOCATE_UNIT_PIECE + ",")) {

                if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(rsc.getStrategyCode())) {
                    WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
                    skuCommand.setSkuId(skuId);
                    skuCommand.setAreaId(rsc.getAreaId());
                    skuCommand.setOuId(ouId);
                    skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
                    List<WhSkuInventoryCommand> invs = findInventoryUuidByBestMatchAndPiece(skuCommand, upperLimitQty);
                    for (WhSkuInventoryCommand invCmd : invs) {
                        invCmd.setOuId(wh.getId());
                        List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
                        if (null != skuInvs && !skuInvs.isEmpty()) {
                            for (WhSkuInventoryCommand invCommand : skuInvs) {
                                Double onHandQty = invCommand.getOnHandQty();
                                // 占用数量 >= 在库数量
                                if (upperLimitQty >= onHandQty) {
                                    // 创建已分配库存,待移入库存
                                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                    upperLimitQty -= onHandQty;
                                } else {
                                    // 创建已分配库存,待移入库存
                                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                                    upperLimitQty = 0.0;
                                }
                                if (upperLimitQty == 0) {
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
                    skuCommand.setSkuId(skuId);
                    skuCommand.setAreaId(rsc.getAreaId());
                    skuCommand.setOuId(ouId);
                    skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
                    List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, upperLimitQty);
                    if (null != skuInvs && !skuInvs.isEmpty()) {
                        for (WhSkuInventoryCommand invCommand : skuInvs) {
                            Double onHandQty = invCommand.getOnHandQty();
                            // 占用数量 >= 在库数量
                            if (upperLimitQty>=onHandQty) {
                                // 创建已分配库存,待移入库存
                                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                                upperLimitQty -= onHandQty;
                            } else {
                                // 创建已分配库存,待移入库存
                                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                                upperLimitQty = 0.0;
                            }
                            if (upperLimitQty == 0) {
                                break;
                            }
                        }
                    }
                }
            }
            if (upperLimitQty == 0) {
                break;
            }


        }

        if (upperLimitQty > 0) {
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        // 删除库位补货信息
        int count = this.replenishmentMsgDao.deleteByIdOuId(msg.getId(), ouId);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
        }

        ReplenishmentTask task = new ReplenishmentTask();
        task.setReplenishmentCode(replenishmentCode);
        task.setLocationId(locationId);
        task.setStatus(1);
        task.setOuId(ouId);
        task.setCreateTime(new Date());
        task.setLastModifyTime(new Date());
        this.replenishmentTaskDao.insert(task);

    }
    
    
    /***
     * pda拣货生成容器库存
     * @param operationId
     * @param ouId
     */
    public void pickingAddContainerInventory(Long operationId,Long ouId,Integer pickingWay,Boolean isTabbInvTotal,Long userId,List<WhSkuInventoryCommand> allSkuInvList){
        
//        OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
//        if(null == operLineCacheCmd) {
//            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//        }
        List<Long> skuInvCmdList = new ArrayList<Long>();
        List<Long> insideIdList = new ArrayList<Long>();
        List<Integer> containerLatticeNoList = new ArrayList<Integer>();
        List<String> outboundboxCodeList = new ArrayList<String>();
//        Set<String> outBoundBoxs = operLineCacheCmd.getOutBoundxBoxs(); //出库箱集合
//        Set<Long> turnoverBoxs =  operLineCacheCmd.getTurnoverBoxs();   //周转箱集合
//        Map<String, Set<WhOperationExecLine>> outBoundxBoxOpLineExecIdMap = operLineCacheCmd.getOutBoundxBoxOpLineExecIdMap();  //出库箱对应的作业执行明细集合
//        Map<Long, Set<WhOperationExecLine>>  turnoverBoxsOpLineExecIdMap = operLineCacheCmd.getTurnoverBoxsOpLineExecIdMap();
        List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId);
        if(null== operationExecLineList || operationExecLineList.size()==0) {
            throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
        }
        Long outerContainerId = null;
        if(pickingWay == Constants.PICKING_WAY_THREE) { //出库箱
//            for(String outBoundBox:outBoundBoxs) {
//                outboundboxCodeList.add(outBoundBox);  //出库箱
//                Set<WhOperationExecLine> opLineExecSet = outBoundxBoxOpLineExecIdMap.get(outBoundBox);   //当前出库箱对应的作业执行明细集合
                for(WhOperationExecLine opLineExec:operationExecLineList) {
                    String outBoundBox = opLineExec.getUseOutboundboxCode();
                    outboundboxCodeList.add(outBoundBox);  //出库箱
                    String execSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(opLineExec);
                    Double qty = Double.valueOf(opLineExec.getQty());
                    Double sumQty = 0.0;// 库位库存中sku商品的总和(同一个库位上,相同的sku库存属性没有合并的情况)
                    int count = 0;
                    for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                        count++;
                        Double skuQty = skuInvCmd.getQty();
                        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                        if(opLineExec.getFromLocationId() == skuInvCmd.getLocationId() && execSkuAttrIds.equals(skuAttrIds)){
                            sumQty = sumQty+skuQty;
                            skuInvCmdList.add(skuInvCmd.getId());
                            if(opLineExec.getIsShortPicking()) {  //短拣的商品生成库位库存
                                this.addLocInventory(skuInvCmd, skuQty, isTabbInvTotal, ouId, userId);
                            }
                            this.addContianerInventory(skuInvCmd, qty, isTabbInvTotal, ouId, userId, pickingWay, outBoundBox, null, null, null,null);  //出库箱模式,添加容器库存
                            if(count == allSkuInvList.size()) {
                                if(qty < sumQty) {
                                    //1.0先生成一条库位库存记录
                                    Double newQty = sumQty-qty;
                                    if(newQty != 0) {
                                        this.addLocInventory(skuInvCmd, newQty, isTabbInvTotal, ouId, userId);
                                    }
                                    //2.0删除剩下所有的库位库存
                                    for(Long id:skuInvCmdList) {
                                       int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                       if(result < 1) {
                                           throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                       }
                                    }
                                }else{
                                    //直接删除所有的库位库存
                                    for(Long id:skuInvCmdList) {
                                        int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                        if(result < 1) {
                                            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                        }
                                     }
                                }
                            }
                        }
                    }
                }
                
                
//            }
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) { //周转箱
//            for(Long turnoverBoxId:turnoverBoxs) {
//                insideIdList.add(turnoverBoxId);  //周转箱
//                Set<WhOperationExecLine> opLineExecSet = turnoverBoxsOpLineExecIdMap.get(turnoverBoxId);
                for(WhOperationExecLine opLineExec:operationExecLineList) {
                    Long turnoverBoxId = opLineExec.getUseContainerId();
                    insideIdList.add(turnoverBoxId);  //周转箱
                    String execSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(opLineExec);
                    Double qty = Double.valueOf(opLineExec.getQty().toString());
                    Double sumQty = 0.0;// 库位库存中sku商品的总和(同一个库位上,相同的sku库存属性没有合并的情况)
                    int count = 0;
                    for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                        count++;
                        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                        Double skuQty = skuInvCmd.getQty();
                        if(opLineExec.getFromLocationId() == skuInvCmd.getLocationId() && execSkuAttrIds.equals(skuAttrIds)){
                            sumQty = sumQty+skuQty;
                            skuInvCmdList.add(skuInvCmd.getId());
                            if(opLineExec.getIsShortPicking()) {  //短拣的商品生成库位库存
                                this.addLocInventory(skuInvCmd, skuQty, isTabbInvTotal, ouId, userId);
                            }
                            this.addContianerInventory(skuInvCmd, qty, isTabbInvTotal, ouId, userId, pickingWay, null, null, null, turnoverBoxId,null);  //出库箱模式,添加容器库存
                            if(count == allSkuInvList.size()) {
                                if(qty < sumQty) {
                                    //1.0先生成一条库位库存记录
                                    Double newQty = sumQty-qty;
                                    if(newQty != 0) {
                                        this.addLocInventory(skuInvCmd, newQty, isTabbInvTotal, ouId, userId);
                                    }
                                    //2.0删除剩下所有的库位库存
                                    for(Long id:skuInvCmdList) {
                                       int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                       if(result < 1) {
                                           throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                       }
                                    }
                                }else{
                                    //直接删除所有的库位库存
                                    for(Long id:skuInvCmdList) {
                                        int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                        if(result < 1) {
                                            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                        }
                                     }
                                }
                            }
                        }
                    }
                }
//            }
        }
        if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){       //只有小车的情况
            for(WhOperationExecLine opLineExec:operationExecLineList){
                outerContainerId = opLineExec.getUseOuterContainerId();   //小车
                String execSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(opLineExec);
                Double qty = Double.valueOf(opLineExec.getQty().toString());
                Double sumQty = 0.0;// 库位库存中sku商品的总和(同一个库位上,相同的sku库存属性没有合并的情况)
                int count = 0;
                for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                    count++;
                    String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                    Double skuQty = skuInvCmd.getOnHandQty();
                    if(opLineExec.getFromLocationId() == skuInvCmd.getLocationId() && execSkuAttrIds.equals(skuAttrIds)){
                        sumQty = sumQty+skuQty;
                        skuInvCmdList.add(skuInvCmd.getId());
                        if(opLineExec.getIsShortPicking()) {  //短拣的商品生成库位库存
                            this.addLocInventory(skuInvCmd, skuQty, isTabbInvTotal, ouId, userId);
                        }
                        if(pickingWay == Constants.PICKING_WAY_ONE){
                            this.addContianerInventory(skuInvCmd, qty, isTabbInvTotal, ouId, userId, pickingWay, null, outerContainerId, null, null,null);  //出库箱模式,添加容器库存
                        }
                        if(pickingWay == Constants.PICKING_WAY_TWO){
                            Integer useContainerLatticeNo = opLineExec.getUseContainerLatticeNo();   //使用的货格号
                            String outBoundBox = opLineExec.getUseOutboundboxCode();  //使用出库箱
                            containerLatticeNoList.add(useContainerLatticeNo);   //货格号
                            outboundboxCodeList.add(outBoundBox);  //出库箱
                            Long insideContainerId = opLineExec.getUseContainerId();
                            this.addContianerInventory(skuInvCmd, qty, isTabbInvTotal, ouId, userId, pickingWay, null, outerContainerId, useContainerLatticeNo, null,insideContainerId);  //出库箱模式,添加容器库存
                        }
                        if(count == allSkuInvList.size()) {
                            if(qty < sumQty) {
                                //1.0先生成一条库位库存记录
                                Double newQty = sumQty-qty;
                                if(newQty != 0) {
                                    this.addLocInventory(skuInvCmd, newQty, isTabbInvTotal, ouId, userId);
                                }
                                //2.0删除剩下所有的库位库存
                                for(Long id:skuInvCmdList) {
                                   int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                   if(result < 1) {
                                       throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                   }
                                }
                            }else{
                                //直接删除所有的库位库存
                                for(Long id:skuInvCmdList) {
                                    int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                    if(result < 1) {
                                        throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                    }
                                 }
                            }
                        }
                    }
                }
            }
         }
        
        if(pickingWay == Constants.PICKING_WAY_FIVE || pickingWay == Constants.PICKING_WAY_SIX){
            for(WhOperationExecLine opLineExec:operationExecLineList){
                outerContainerId = opLineExec.getUseOuterContainerId();   //小车
                Long insideContainerId = opLineExec.getUseContainerId();
                insideIdList.add(insideContainerId);  //周转箱
                String execSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(opLineExec);
                Double qty = Double.valueOf(opLineExec.getQty().toString());
                Double sumQty = 0.0;// 库位库存中sku商品的总和(同一个库位上,相同的sku库存属性没有合并的情况)
                int count = 0;
                for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                    String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                    Double skuQty = skuInvCmd.getOnHandQty();
                    if(opLineExec.getFromLocationId() == skuInvCmd.getLocationId() && execSkuAttrIds.equals(skuAttrIds) && opLineExec.getFromInsideContainerId() == skuInvCmd.getInsideContainerId()){
                        sumQty = sumQty+skuQty;
                        count++;
                        if(opLineExec.getIsShortPicking()) {  //短拣的商品生成库位库存
                            this.addLocInventory(skuInvCmd, skuQty, isTabbInvTotal, ouId, userId);
                        }else{
                            this.addContianerInventory(skuInvCmd, qty, isTabbInvTotal, ouId, userId, pickingWay, null, outerContainerId, null, null,insideContainerId);  //整托整箱模式
                        }
                        if(count == allSkuInvList.size()) {
                            if(qty < sumQty) {
                                //1.0先生成一条库位库存记录
                                Double newQty = sumQty-qty;
                                if(newQty != 0) {
                                    this.addLocInventory(skuInvCmd, newQty, isTabbInvTotal, ouId, userId);
                                }
                                //2.0删除剩下所有的库位库存
                                for(Long id:skuInvCmdList) {
                                   int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                   if(result < 1) {
                                       throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                   }
                                }
                            }else{
                                //直接删除所有的库位库存
                                for(Long id:skuInvCmdList) {
                                    int result =  whSkuInventoryDao.deleteWhSkuInventoryById(id,ouId);
                                    if(result < 1) {
                                        throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                    }
                                 }
                            }
                        }
                    }
                }
            }
        }
        //校验容器/出库箱库存与删除的拣货库位库存时否一致
        List<WhOperationExecLine> list = whOperationExecLineDao.checkContainerInventory(operationId, ouId, outerContainerId,insideIdList, containerLatticeNoList,outboundboxCodeList);
        for(WhOperationExecLine operationExecline:list) {
            Long skuId = operationExecline.getSkuId();
            Long qty = operationExecline.getQty();
            Integer containerLatticeNo = operationExecline.getUseContainerLatticeNo();
            Long useOuterContainerId = operationExecline.getUseOuterContainerId();
            Long insideContainerId = operationExecline.getUseContainerId();
           String outboundboxCode =  operationExecline.getUseOutboundboxCode();
            if(!(skuId == 0 && qty == 0 && containerLatticeNo == null && useOuterContainerId == null && insideContainerId == null && StringUtil.isEmpty(outboundboxCode))) {
                throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
            }
        }
    }
    
    /***
     * pda拣货短拣的情况下，添加库位库存
     * @param skuInvCmd
     * @param qty
     * @param isTabbInvTotal
     * @param ouId
     * @param userId
     */
    private void addLocInventory(WhSkuInventoryCommand skuInvCmd,Double qty,Boolean isTabbInvTotal,Long ouId,Long userId) {
        log.info("WhSkuInventoryManagerImpl addLocInventory is start");
        List<WhSkuInventorySnCommand> snList = skuInvCmd.getWhSkuInventorySnCommandList();
        if(null == snList || snList.size() == 0) {
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(skuInvCmd.getLocationId());
            inv.setOccupationCode(null);
            inv.setInboundTime(new Date());
            inv.setOnHandQty(qty); 
            inv.setId(null);
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// 更新UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {  //在库存日志是否记录交易前后库存总数 0：否 1：是 
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.insert(inv);
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId);
        }else{
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(skuInvCmd.getLocationId());
            inv.setOccupationCode(null);
            inv.setInboundTime(new Date());
            inv.setOnHandQty(Double.valueOf(qty.toString())); 
            inv.setId(null);
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// 更新UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {  //在库存日志是否记录交易前后库存总数 0：否 1：是 
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.insert(inv);
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId);
            if (!uuid.equals(skuInvCmd.getUuid())) {
                // uuid发生变更,重新插入sn
                for (WhSkuInventorySnCommand cSnCmd : snList) {
                    WhSkuInventorySn sn = new WhSkuInventorySn();
                    BeanUtils.copyProperties(cSnCmd, sn);
                    sn.setUuid(inv.getUuid());
                    whSkuInventorySnDao.saveOrUpdate(sn); // 更新sn
                    insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                }
                insertSkuInventorySnLog(inv.getUuid(), ouId); // 记录sn日志
            }
        }
       
     
        log.info("WhSkuInventoryManagerImpl addLocInventory is end");
    }
    
   /***
    * 添加容器库存
    * @param skuInvCmd
    * @param qty
    * @param isTabbInvTotal
    * @param ouId
    * @param userId
    * @param pickingWay
    * @param outBoundBoxCode
    * @param containerId
    * @param containerLatticeNo
    * @param turnoverBoxId
    */
    private void addContianerInventory(WhSkuInventoryCommand skuInvCmd,Double qty,Boolean isTabbInvTotal,Long ouId,Long userId,Integer pickingWay,String outBoundBoxCode,Long outerContainerId,Integer containerLatticeNo,Long turnoverBoxId,Long insideContainerId) {
        log.info("WhSkuInventoryManagerImpl addContianerInventory is start");
        List<WhSkuInventorySnCommand> snList = skuInvCmd.getWhSkuInventorySnCommandList();
        if(null == snList || snList.size() == 0) {
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(null);
            inv.setOccupationCode(null);
            inv.setInboundTime(new Date()); 
            inv.setOnHandQty(qty);
            inv.setId(null);
            if(pickingWay.equals(Constants.PICKING_WAY_ONE)){
                inv.setOuterContainerId(outerContainerId);     //小车 
                inv.setInsideContainerId(null);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_TWO)){
                inv.setOuterContainerId(outerContainerId);  //小车加出库箱模式
                inv.setContainerLatticeNo(containerLatticeNo);
                inv.setInsideContainerId(null);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_THREE)) {  //出库箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(null);
                inv.setOutboundboxCode(outBoundBoxCode);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_FOUR)){  //周转箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(turnoverBoxId);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_FIVE)) { //整托
                inv.setOuterContainerId(outerContainerId);
                inv.setInsideContainerId(insideContainerId);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_SIX)) { //整箱
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(insideContainerId);
            }
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// 更新UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {  //在库存日志是否记录交易前后库存总数 0：否 1：是 
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.insert(inv);
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId);
        }else{
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(null);
            inv.setOccupationCode(null);
            inv.setInboundTime(new Date()); 
            inv.setOnHandQty(Double.valueOf(qty));
            inv.setId(null);
            if(pickingWay.equals(Constants.PICKING_WAY_ONE)){
                inv.setOuterContainerId(outerContainerId);     //小车 
                inv.setInsideContainerId(null);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_TWO)){
                inv.setOuterContainerId(outerContainerId);  //小车加出库箱模式
                inv.setContainerLatticeNo(containerLatticeNo);
                inv.setInsideContainerId(null);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_THREE)) {  //出库箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(null);
                inv.setOutboundboxCode(outBoundBoxCode);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_FOUR)){  //周转箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(turnoverBoxId);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_FIVE)) { //整托
                inv.setOuterContainerId(outerContainerId);
                inv.setInsideContainerId(insideContainerId);
            }
            if(pickingWay.equals(Constants.PICKING_WAY_SIX)) { //整箱
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(insideContainerId);
            }
            try {
                uuid = SkuInventoryUuid.invUuid(inv);
                inv.setUuid(uuid);// 更新UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {  //在库存日志是否记录交易前后库存总数 0：否 1：是 
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            whSkuInventoryDao.insert(inv);
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId);
            if (!uuid.equals(skuInvCmd.getUuid())) {
                // uuid发生变更,重新插入sn
                for (WhSkuInventorySnCommand cSnCmd : snList) {
                    WhSkuInventorySn sn = new WhSkuInventorySn();
                    BeanUtils.copyProperties(cSnCmd, sn);
                    sn.setUuid(inv.getUuid());
                    whSkuInventorySnDao.saveOrUpdate(sn); // 更新sn
                    insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                }
                insertSkuInventorySnLog(inv.getUuid(), ouId); // 记录sn日志
            }
        }
        log.info("WhSkuInventoryManagerImpl addContianerInventory is end");
    }
}
