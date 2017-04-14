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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lark.common.annotation.MoreDB;

import org.apache.ibatis.annotations.Param;
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
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioExecLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentStrategyCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.HardAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundConfirmCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundInvLineConfirmCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundLineConfirmCommand;
import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundSnLineConfirmCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.constant.ReplenishmentTaskStatus;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentMsgDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentStrategyDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
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
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.AllocateStrategy;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

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
    private WhOdoLineDao whOdoLineDao;
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
    @Autowired
    private WhOperationLineDao whOperationLineDao;
    @Autowired
    private WhWorkDao whWorkDao;
    @Autowired
    private WhWorkLineDao  whWorkLineDao;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private CustomerManager customerManager;
    @Autowired
    private StoreManager storeManager;
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
                        } catch (Exception e) {
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
                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                                } catch (Exception e) {
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
                        insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                        insertSkuInventoryLog(invCmd.getId(), -(new Double(totalQty)), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                        } catch (Exception e) {
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
                    insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
     * 库位解绑（生成容器库存及删除待移入库存）
     * 
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param locationCode
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void execUnbinding(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, String locationCode, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
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
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            log.error("warehouse is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_WAREHOUSE_NOT_FOUND_ERROR);
        }
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            // 拆箱上架
            // 1.获取所有待移入库存
            boolean isTV = true;// 是否跟踪容器
            isTV = false;// 拆箱上架默认不跟踪容器号，不管库位是否跟踪容器号
            // 1.获取所有待移入库存
            Location loc = locationDao.findLocationByCode(locationCode, ouId);
            if (null == loc) {
                if (null == loc) {
                    loc = locationDao.getLocationByBarcode(locationCode, ouId);
                }
                if (null == loc) {
                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
            }
            List<WhSkuInventoryCommand> invList = null;
            // 查询所有对应容器号的库存信息
            if (null == insideContainerId) {
                log.error("insideContainerId is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
            }
            invList = whSkuInventoryDao.findLocToBeFilledInventoryByInsideContainerIdAndLocId(ouId, insideContainerId, loc.getId());
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", insideContainerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_TOBEFILLED_INV_ERROR, new Object[] {insideContainerCode});
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
                    inv.setLocationId(null);
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    Double oldQty = 0.0;
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
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
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    inv.setLocationId(null);
                    try {
                        uuid = SkuInventoryUuid.invUuid(inv);
                        inv.setUuid(uuid);// UUID
                    } catch (Exception e) {
                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                    }
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 插入sn
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                        // 记录SN日志
                        insertSkuInventorySnLog(sn.getId(), ouId);
                    }
                    Double oldQty = 0.0;
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
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
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
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
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
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
            Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
            Location loc = locationDao.findLocationByCode(locationCode, ouId);
            if (null == loc) {
                loc = locationDao.getLocationByBarcode(locationCode, ouId);
                if (null == loc) {
                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
            }
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
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_TOBEFILLED_INV_ERROR, new Object[] {containerCode});
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
                    inv.setLocationId(null);
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
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    Double oldQty = 0.0;
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
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
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    inv.setLocationId(null);
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
                    inv.setLastModifyTime(new Date());
                    whSkuInventoryDao.insert(inv);
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    // 插入sn
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                        // 记录SN日志
                        insertSkuInventorySnLog(sn.getId(), ouId);
                    }
                    Double oldQty = 0.0;
                    insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
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
                    // 记录入库库存日志(这个实现的有问题)
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                        insideContainer.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
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
                            int toBeFilledCount = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerId);
                            int rcvdCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
                            if (0 == toBeFilledCount && rcvdCount == 0) {
                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                    Container container = new Container();
                                    BeanUtils.copyProperties(containerCmd, container);
                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                                    containerDao.saveOrUpdateByVersion(container);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                }
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
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);

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
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);

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
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insertSkuInventoryLog(cInvCmd.getId(), -cInvCmd.getAllocatedQty(), oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                            insertSkuInventoryLog(cInvCmd.getId(), -actualOutboundQty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                if(null == loc) {
                    loc = locationDao.getLocationByBarcode(locationCode, ouId);
                }
                if(null == loc) {
                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                    // 插入sn
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                        // 记录SN日志
                        insertSkuInventorySnLog(sn.getId(), ouId);
                    }
                    // 记录SN日志(这个实现的有问题)
                    // insertSkuInventorySnLog(inv.getUuid(), ouId);
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
                loc = locationDao.getLocationByBarcode(locationCode, ouId);
                if(null == loc) {
                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                    // 插入sn
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(snCmd, sn);
                        sn.setId(null);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.insert(sn);
                        insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                        // 记录SN日志
                        insertSkuInventorySnLog(sn.getId(), ouId);
                    }
                    // 记录SN日志(这个实现的有问题)
                    // insertSkuInventorySnLog(inv.getUuid(), ouId);
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
                            int toBeFilledCount = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerId);
                            int rcvdCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId, containerId);
                            if(0 == toBeFilledCount && rcvdCount == 0){
                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                    Container container = new Container();
                                    BeanUtils.copyProperties(containerCmd, container);
                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                    containerDao.saveOrUpdateByVersion(container);
                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                                }
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

    
//    private void manPutwayExistLoc(List<WhSkuInventorySn> skuSnlist,Warehouse warehouse,Double scanSkuQty,WhSkuInventoryCommand whSkuInve,Boolean isBM,Boolean isVM,Long locationId,
//                                   Long userId,Long ouId,WhSkuInventoryCommand invCmd){
//        String uuid = null;
//        List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
//        if (null == snList || 0 == snList.size()) {
//            Long whskuInventoryId = invCmd.getId(); 
//            WhSkuInventory inv = new WhSkuInventory();
//            BeanUtils.copyProperties(whSkuInve, inv);
//            if (false == isBM) {
//                inv.setBatchNumber(null);
//            }
//            if (false == isVM) {
//                inv.setMfgDate(null);
//                inv.setExpDate(null);
//            }
//            inv.setOccupationCode(null);
//            inv.setLocationId(locationId);
//            inv.setInboundTime(new Date());
//            inv.setIsLocked(false);
//            inv.setOnHandQty(scanSkuQty+inv.getOnHandQty());
//            try {
//                uuid = SkuInventoryUuid.invUuid(inv);
//                inv.setUuid(uuid);// UUID
//            } catch (Exception e) {
//                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//            }
//            Double oldQty = 0.0;
//            if (true == warehouse.getIsTabbInvTotal()) {
//                try {
//                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
//                } catch (Exception e) {
//                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                }
//            } else {
//                oldQty = 0.0;
//            }
//            whSkuInventoryDao.saveOrUpdateByVersion(inv); // 更新已经上架的库存记录
//            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
//            // 记录入库库存日志
//            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
//            Long skuInvId = invCmd.getId();
//            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(skuInvId, ouId);
//            if(null == whSkuInventory) { 
//                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+skuInvId);
//                throw new BusinessException(ErrorCodes.NO_SKU_INVENTORY);
//            }
//            //修改在库库存数量
//            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
//            // 记录入库库存日志
//            insertSkuInventoryLog(whSkuInventory.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
//            if(skuInvOnHandQty == 0.0) {
//                whSkuInventoryDao.deleteWhSkuInventoryById(whskuInventoryId, ouId);   //上架成功后删除，原来的库存记录
//                insertGlobalLog(GLOBAL_LOG_DELETE, inv, ouId, userId, null, null);
//            }else{
//                whSkuInventory.setOnHandQty(skuInvOnHandQty);
//                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory); // 更新库存记录
//                insertGlobalLog(GLOBAL_LOG_UPDATE, whSkuInventory, ouId, userId, null, null);
//            }
//        } else {
//            WhSkuInventory inv = new WhSkuInventory();
//            Long whskuInventoryId = invCmd.getId();
//            BeanUtils.copyProperties(whSkuInve, inv);
//            // 拆箱上架默认不跟踪容器号
//            inv.setOuterContainerId(null);
//            inv.setInsideContainerId(null);
//            inv.setLocationId(locationId);
//            inv.setOnHandQty(inv.getOnHandQty()+scanSkuQty);
//            inv.setInboundTime(new Date());
//            inv.setIsLocked(false);
//            if (false == isBM) {
//                inv.setBatchNumber(null);
//            }
//            if (false == isVM) {
//                inv.setMfgDate(null);
//                inv.setExpDate(null);
//            }
//            inv.setOccupationCode(null);
//            try {
//                uuid = SkuInventoryUuid.invUuid(inv);
//                inv.setUuid(uuid);// UUID
//            } catch (Exception e) {
//                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
//                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
//            }
//            Double oldQty = 0.0;
//            if (true == warehouse.getIsTabbInvTotal()) {
//                try {
//                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
//                } catch (Exception e) {
//                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
//                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
//                }
//            } else {
//                oldQty = 0.0;
//            }
//            whSkuInventoryDao.saveOrUpdateByVersion(inv);
//            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
//            // 记录入库库存日志
//            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
//            //修改在库库存数量
//            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(whskuInventoryId, ouId);
//            if(null == whSkuInventory) {
//                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+whskuInventoryId);
//                throw new BusinessException(ErrorCodes. NO_SKU_INVENTORY);
//            }
//            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
//            // 记录入库库存日志
//            insertSkuInventoryLog(inv.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
//            if(skuInvOnHandQty == 0.0) {
//                whSkuInventoryDao.deleteWhSkuInventoryById(whskuInventoryId, ouId);   //上架完毕后删除原来的库存数据
//                insertGlobalLog(GLOBAL_LOG_DELETE, whSkuInventory, ouId, userId, null, null);
//            }else{
//                whSkuInventory.setOnHandQty(skuInvOnHandQty);
//                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory); // 更新库存记录
//                insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
//            }
//            if (!uuid.equals(invCmd.getUuid())) {
//                    // uuid发生变更,重新插入sn
//                    for(WhSkuInventorySn snCmd:skuSnlist) { //已经扫描过的sn
//                        WhSkuInventorySn sn = new WhSkuInventorySn();
//                        BeanUtils.copyProperties(snCmd, sn);
//                        sn.setUuid(inv.getUuid());
//                        whSkuInventorySnDao.saveOrUpdate(sn);
//                        insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
//                    }
//                    // 记录SN日志
//                    insertSkuInventorySnLog(inv.getUuid(), ouId);
//           }
//        }
//    }

    
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
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
            //修改在库库存数量
            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(whskuInventoryId, ouId);
            if(null == whSkuInventory) {
                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+whskuInventoryId);
                throw new BusinessException(ErrorCodes. NO_SKU_INVENTORY);
            }
            // 记录入库库存日志
            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
            insertSkuInventoryLog(whSkuInventory.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
            //修改在库库存数量
            Long skuInvId = invCmd.getId();
            WhSkuInventory whSkuInventory = whSkuInventoryDao.findWhSkuInventoryById(skuInvId, ouId);
            if(null == whSkuInventory) {
                log.error("whskuInventoryImpl manPutwayNoLoc whskuInventory id"+skuInvId);
                throw new BusinessException(ErrorCodes. NO_SKU_INVENTORY);
            }
            // 记录入库库存日志
            Double skuInvOnHandQty = invCmd.getOnHandQty() - scanSkuQty;    //上架后库存
            insertSkuInventoryLog(whSkuInventory.getId(), skuInvOnHandQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                for(WhSkuInventorySn sn:skuSnlist) { //已经扫描过的sn
                    if(sn.getUuid().equals(invCmd.getUuid())) {
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.saveOrUpdate(sn);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                    }
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
//            List<WhSkuInventoryCommand> invLocList = whSkuInventoryDao.findWhSkuInvCmdByLocationContainerIdIsNull(ouId,locationId);
            List<WhSkuInventorySn> skuSnlist = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_SN, insideContainerId.toString() + skuId.toString());
            // 2.执行上架
//            Boolean result = false;
//            String  putwaySkuAttrsId = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
//            WhSkuInventoryCommand whSkuInvLoc = null;
//            for(WhSkuInventoryCommand whSkuInve:invLocList){
//                    String  skuAttrsIdLoc = SkuCategoryProvider.getSkuAttrIdByInv(whSkuInve);  
//                    if(invLocList.size() != 0) {   //判断是否有库位
//                          if(whSkuInve.getSkuId().longValue() == invSkuCmd.getSkuId().longValue() && putwaySkuAttrsId.equals(skuAttrsIdLoc)) {
//                                result = true;
//                                whSkuInvLoc = whSkuInve;
//                                break;
//                          }else{
//                                result = false;     
//                          }
//                     }else{
//                          result = false;        
//                     }
//            }
//            if(result) {
//                this.manPutwayExistLoc(skuSnlist,warehouse, scanSkuQty, whSkuInvLoc, isBM, isVM, locationId, userId, ouId,invSkuCmd);
//            }else{
                this.manPutwayNoLoc(skuSnlist,warehouse, scanSkuQty, invSkuCmd, isBM, isVM, locationId, userId, ouId);
//            }
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
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
	    skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
		for (AllocateStrategy as : rules) {
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			} else {
				isStaticLocation = false;
			}
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));
			// 分配区域
			skuCommand.setAreaId(as.getAreaId());
			// 占用逻辑
			List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(as.getStrategyCode(), skuCommand, occupyQty);
			HardAllocationCommand command = inventoryOccupyManager.hardAllocateListOccupyNew(skuInvs, allocateUnitCodes, occupyQty, whWaveLineCommand.getSkuId(), occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, isStaticLocation, logId);
			occupyQty -= command.getOccupyQty();
			actualQty += command.getOccupyQty();
			containerQty = command.getContainerQty();
			staticLocationIds = command.getStaticLocationIds();
			trayIds = command.getTrayIds();
			packingCaseIds = command.getPackingCaseIds();
			if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
				break;
			}
		}
		// 数量全部占完回写波次明细分配数量
		whWaveLineManager.updateWaveLineByAllocateQty(whWaveLineCommand.getId(), actualQty, containerQty, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, wh.getId(), logId);
		if (log.isInfoEnabled()) {
			log.info("allocationInventoryByLine end,waveLineId:{},actualQty:{},logId:{}", whWaveLineCommand.getId(), actualQty, logId);
		}
	}
	
	@Override
	public void allocationInventoryByLineList(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules, Long skuId, Long storeId, Long invStatusId, Warehouse wh, String logId) {
		if (null == rules || rules.isEmpty()) {
			log.error("AllocateStrategy is null logId:[{}]", logId);
			return;
		}
		Long ouId = wh.getId();
		List<WhSkuInventoryCommand> allSkuInvs = new ArrayList<WhSkuInventoryCommand>();
		// 记录静态库位可超分配的库位id
		Set<String> staticLocationIds = new HashSet<String>();
		// 记录整托占用容器ids
		Set<String> trayIds = new HashSet<String>();
		// 记录整箱占用容器ids
		Set<String> packingCaseIds = new HashSet<String>();
		Map<Long, Boolean> replenishedMap = new HashMap<Long, Boolean>();
		for (AllocateStrategy as : rules) {
			String strategyCode = as.getStrategyCode();	// 策略code值
			Boolean isStaticLocation = false;
			Long areaId = null;	// 超分配和空库位下记录的areaId
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));	// 分配单位
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			}
			if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(as.getStrategyCode())) {
				areaId = as.getAreaId();
			}
			// 封装查询库存条件
			WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
			skuCommand.setSkuId(skuId);
			skuCommand.setStoreId(storeId);
			skuCommand.setInvStatus(invStatusId);
			skuCommand.setOuId(ouId);
			skuCommand.setAreaId(as.getAreaId());
			skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
			allSkuInvs = findInventorysByAllocateStrategy(strategyCode, skuCommand, null);
			for (int j = 0; j < notHaveInvAttrLines.size(); j++) {
				WhWaveLine line = notHaveInvAttrLines.get(j);
				log.info("Occupy Inventory, skuId:[{}], storeId:[{}], invStutas:[{}], allocateStrategyId:[{}], qty:[{}], ouId:[{}], logId:[{}]", skuId, storeId, invStatusId, as.getId(), line.getQty(), ouId, logId);
				if (line.getQty().equals(line.getAllocateQty())) {
					continue;
				}
				// 明细行清空上次记录的整托整箱占用库位ids
				if (!trayIds.isEmpty()) {
					trayIds.clear();
				}
				if (!packingCaseIds.isEmpty()) {
					packingCaseIds.clear();
				}
				if (!staticLocationIds.isEmpty()) {
					staticLocationIds.clear();
				}
				// 先到期先出,先到期后出验证是否是有效期商品
				if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(strategyCode)
						|| Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(strategyCode)) {
					Boolean isExpirationSku = skuDao.checkIsExpirationSku(skuId, ouId);
					if (isExpirationSku == null || !isExpirationSku) {
						log.info("sku is not ExpirationSku, break this allocateStrategy, skuId:[{}], allocateStrategyId:[{}], ouId:[{}], logId:[{}]", skuId, as.getId(), ouId, logId);
						break;
					}
				}
				// 静态库位超分配判断是否有补货阶段, 没有补货阶段跳过
				if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
					Boolean flag = replenishedMap.get(line.getWaveId());
					if (null == flag) {
						String phaseCode = whWaveManager.getNextParseCode(line.getWaveId(), ouId);
						if (!WavePhase.REPLENISHED.equals(phaseCode)) {
							replenishedMap.put(line.getWaveId(), Boolean.FALSE);
							log.info("wave is not have Replenished, break this allocateStrategy, skuId:[{}], allocateStrategyId:[{}], ouId:[{}], logId:[{}]", skuId, as.getId(), ouId, logId);
							continue;
						} else {
							replenishedMap.put(line.getWaveId(), Boolean.TRUE);
						}
					} else if (!flag) {
						log.info("wave is not have Replenished, break this allocateStrategy, skuId:[{}], allocateStrategyId:[{}], ouId:[{}], logId:[{}]", skuId, as.getId(), ouId, logId);
						continue;
					}
				}
				// 空库位 
				if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(strategyCode)) {
					whWaveLineManager.updateWaveLineByAllocateQty(line.getId(), 0.0, 0.0, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, ouId, logId);
					continue;
				}
				Double qty = line.getQty();				// 需要占用数量
				Double occupyQty = 0.0;					// 实际占用数量
				String occupyCode = line.getOdoCode();	// 占用编码
				Long odoLineId = line.getOdoLineId();	// 占用odoLineId
				Double containerQty = 0.0;				// 用容器占用的数量
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
				if (-1 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty) || Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
					whWaveLineManager.updateWaveLineByAllocateQty(line.getId(), occupyQty, containerQty, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, ouId, logId);
				}
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
	public void allocationInventoryByLineListNew(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules, Long skuId, Long storeId, Long invStatusId, Warehouse wh, String logId) {
		if (null == rules || rules.isEmpty()) {
			log.error("AllocateStrategy is null logId:[{}]", logId);
			return;
		}
		Long ouId = wh.getId();
		List<WhSkuInventoryCommand> allSkuInvs = new ArrayList<WhSkuInventoryCommand>();
		Map<Long, Boolean> replenishedMap = new HashMap<Long, Boolean>();
		for (AllocateStrategy as : rules) {
			String strategyCode = as.getStrategyCode();	// 策略code值
			Boolean isStaticLocation = false;
			Long areaId = null;	// 超分配和空库位下记录的areaId
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));	// 分配单位
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			}
			if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(as.getStrategyCode())) {
				areaId = as.getAreaId();
			}
			// 封装查询库存条件
			WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
			skuCommand.setSkuId(skuId);
			skuCommand.setStoreId(storeId);
			skuCommand.setInvStatus(invStatusId);
			skuCommand.setOuId(ouId);
			skuCommand.setAreaId(as.getAreaId());
			skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
			if (!Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(strategyCode)) {
				allSkuInvs = findInventorysByAllocateStrategy(strategyCode, skuCommand, null);
			}
			for (int j = 0; j < notHaveInvAttrLines.size(); j++) {
				WhWaveLine line = notHaveInvAttrLines.get(j);
				log.info("Occupy Inventory, skuId:[{}], storeId:[{}], invStutas:[{}], allocateStrategyId:[{}], qty:[{}], ouId:[{}], logId:[{}]", skuId, storeId, invStatusId, as.getId(), line.getQty(), ouId, logId);
				if (line.getQty().equals(line.getAllocateQty())) {
					continue;
				}
				// 先到期先出,先到期后出验证是否是有效期商品
				if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(strategyCode)
						|| Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(strategyCode)) {
					Boolean isExpirationSku = skuDao.checkIsExpirationSku(skuId, ouId);
					if (isExpirationSku == null || !isExpirationSku) {
						log.info("sku is not ExpirationSku, break this allocateStrategy, skuId:[{}], allocateStrategyId:[{}], ouId:[{}], logId:[{}]", skuId, as.getId(), ouId, logId);
						break;
					}
				}
				// 静态库位超分配判断是否有补货阶段, 没有补货阶段跳过
				if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
					Boolean flag = replenishedMap.get(line.getWaveId());
					if (null == flag) {
						String phaseCode = whWaveManager.getNextParseCode(line.getWaveId(), ouId);
						if (!WavePhase.REPLENISHED.equals(phaseCode)) {
							replenishedMap.put(line.getWaveId(), Boolean.FALSE);
							log.info("wave is not have Replenished, break this allocateStrategy, skuId:[{}], allocateStrategyId:[{}], ouId:[{}], logId:[{}]", skuId, as.getId(), ouId, logId);
							continue;
						} else {
							replenishedMap.put(line.getWaveId(), Boolean.TRUE);
						}
					} else if (!flag) {
						log.info("wave is not have Replenished, break this allocateStrategy, skuId:[{}], allocateStrategyId:[{}], ouId:[{}], logId:[{}]", skuId, as.getId(), ouId, logId);
						continue;
					}
				}
				// 空库位 
				if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(strategyCode)) {
					whWaveLineManager.updateWaveLineByAllocateQty(line.getId(), 0.0, 0.0, isStaticLocation, null, null, null, areaId, ouId, logId);
					continue;
				}
				Double qty = line.getQty();				// 需要占用数量
				Double occupyQty = 0.0;					// 实际占用数量
				String occupyCode = line.getOdoCode();	// 占用编码
				Long odoLineId = line.getOdoLineId();	// 占用odoLineId
				Double containerQty = 0.0;				// 用容器占用的数量
				// 占用逻辑
				HardAllocationCommand command = null;
				// 数量最佳匹配逻辑
				if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(strategyCode)) {
					// 数量最佳匹配逻辑
					allSkuInvs = findInventorysByAllocateStrategy(strategyCode, skuCommand, qty);
				}
				command = inventoryOccupyManager.hardAllocateListOccupyNew(allSkuInvs, allocateUnitCodes, qty, skuId, occupyCode, odoLineId, Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO, wh, isStaticLocation, logId);
				qty -= command.getOccupyQty();
				occupyQty += command.getOccupyQty();
				containerQty = command.getContainerQty();
				Set<String> staticLocationIds = command.getStaticLocationIds();
				Set<String> trayIds = command.getTrayIds();
				Set<String> packingCaseIds = command.getPackingCaseIds();
				if (-1 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
					line.setQty(qty);
				} else if (0 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
					notHaveInvAttrLines.remove(j--);
				}
				if (-1 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty) || Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
					whWaveLineManager.updateWaveLineByAllocateQty(line.getId(), occupyQty, containerQty, isStaticLocation, staticLocationIds, trayIds, packingCaseIds, areaId, ouId, logId);
				}
				if ((null == allSkuInvs || allSkuInvs.isEmpty()) && !Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)
						&& !Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(strategyCode)) {
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
			list = findInventoryUuidByLocation(whSkuInventoryCommand, true);
		}
		// 最小拣货次数
		else if (Constants.ALLOCATE_STRATEGY_MINIMUMORDERPICKINGTIMES.equals(strategyCode)) {
			list = findInventoryUuidByLocation(whSkuInventoryCommand, false);
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
		whSkuInventoryCommand.setIsStatic(null);
		whSkuInventoryCommand.setIsMixStacking(null);
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
    	whSkuInventoryCommand.setIsStatic(null);
		whSkuInventoryCommand.setIsMixStacking(null);
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
		whSkuInventoryCommand.setIsStatic(null);
		whSkuInventoryCommand.setIsMixStacking(null);
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByBestMatch(whSkuInventoryCommand);
			// 数量最佳计算
			return bestMatchSkuInvs(skuInvs, qty);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			// 查找出库位库存数量
			List<WhSkuInventoryCommand> skuInvsList = whSkuInventoryDao.findInventoryUuid(whSkuInventoryCommand);
			// 对库位库存进行筛选出最佳的
			List<WhSkuInventoryCommand> bestMatchInvsList = this.bestMatchSkuInvs(skuInvsList, qty);
			skuInvs = new ArrayList<WhSkuInventoryCommand>();
			for (WhSkuInventoryCommand skuInv : bestMatchInvsList) {
				if (!StringUtils.isEmpty(skuInv.getUuid())) {
					List<String> uuidList = Arrays.asList(skuInv.getUuid().split(","));
					skuInvs.addAll(whSkuInventoryDao.findWhSkuInventoryByUuidList(whSkuInventoryCommand.getOuId(), uuidList));
				}
			}
			
		}
		return skuInvs;
	}

	public List<WhSkuInventoryCommand> findInventoryUuidByLocation(WhSkuInventoryCommand whSkuInventoryCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
		whSkuInventoryCommand.setPriority(new Boolean(flag));
		whSkuInventoryCommand.setIsStatic(null);
		whSkuInventoryCommand.setIsMixStacking(null);
		if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByLocation(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByLocation(whSkuInventoryCommand);
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
			skuInvs = whSkuInventoryDao.findInventoryUuidByLocation(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByLocation(whSkuInventoryCommand);
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
			skuInvs = whSkuInventoryDao.findInventoryUuidByLocation(whSkuInventoryCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByLocation(whSkuInventoryCommand);
		}
		return skuInvs;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByBestMatchAndPiece(WhSkuInventoryCommand whSkuInventoryCommand, Double qty) {
		whSkuInventoryCommand.setPriority(Boolean.TRUE);
		whSkuInventoryCommand.setIsStatic(null);
		whSkuInventoryCommand.setIsMixStacking(null);
		whSkuInventoryCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
		List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findInventoryUuid(whSkuInventoryCommand);
		List<WhSkuInventoryCommand> bestMatchList = bestMatchSkuInvs(skuInvs, qty);
		return bestMatchList;
	}
	
	/**
	 * 数量最佳匹配,返回最佳匹配的库存集合
	 * @param unitCodes 
	 */
	private List<WhSkuInventoryCommand> bestMatchSkuInvs(List<WhSkuInventoryCommand> skuInvs, Double qty) {
		List<WhSkuInventoryCommand> invBestList = null;
		for (int i = 0; i < skuInvs.size(); i++) {
			invBestList = new ArrayList<WhSkuInventoryCommand>();
			if (getPaiLieCheck(skuInvs, 0, i, qty, invBestList, false)) {
				if (null != invBestList && !invBestList.isEmpty()) {
					// 根据库位id找出上面TP或HX的uuid
					/*if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
							|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
						List<Long> locationIds = getLocationIds(invBestList);
						whSkuInventoryCommand.setPriority(Boolean.FALSE);
						invBestList = whSkuInventoryDao.findSkuInvByLocationIds(locationIds, whSkuInventoryCommand.getAllocateUnitCodes(), whSkuInventoryCommand.getOuId());
						
					}*/
					return invBestList;
				}
			}
		}
		invBestList = new ArrayList<WhSkuInventoryCommand>();
		for (WhSkuInventoryCommand command : skuInvs) {
			Double onHandQty = command.getSumOnHandQty();
			if (onHandQty.compareTo(qty) == -1) {
				invBestList.add(0, command);
				qty -= onHandQty;
			} else {
				invBestList.add(0, command);
				break;
			}
		}
		
		/*if (Constants.ALLOCATE_UNIT_TP.equals(whSkuInventoryCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_HX.equals(whSkuInventoryCommand.getAllocateUnitCodes())) {
			List<Long> locationIds = getLocationIds(invBestList);
			whSkuInventoryCommand.setPriority(Boolean.FALSE);
			invBestList = whSkuInventoryDao.findSkuInvByLocationIds(locationIds, whSkuInventoryCommand.getAllocateUnitCodes(), whSkuInventoryCommand.getOuId());
		}*/
		return invBestList;
	}
	
	/**
	 * 递归运算
	 * 筛选出数量等于qty的集合
	 * 从1位到skuInvs.size位数的相加递归运算比对
	 */
	private static boolean getPaiLieCheck(List<WhSkuInventoryCommand> skuInvs, int i, int length, Double qty, List<WhSkuInventoryCommand> invBestList, boolean cheked) {
		if (length < 0) {
			return sum(invBestList).compareTo(qty) == 0;
		}
		if (i >= skuInvs.size()) {
			return sum(invBestList).compareTo(qty) == 0;
		}
		invBestList.add(skuInvs.get(i));
		cheked = getPaiLieCheck(skuInvs, i + 1, length - 1, qty, invBestList, cheked);
		if (cheked) {
			return cheked;
		}
		invBestList.remove(skuInvs.get(i));
		return getPaiLieCheck(skuInvs, i + 1, length, qty, invBestList, cheked);
	}
	
	// 算总库存id
	private static List<Long> getLocationIds(List<WhSkuInventoryCommand> skuInvs) {
		List<Long> locationIds = new ArrayList<Long>();
		for (WhSkuInventoryCommand inv : skuInvs) {
			locationIds.add(inv.getLocationId());
		}
		return locationIds;
	}
	
	// 算总数
	private static Double sum(List<WhSkuInventoryCommand> invBestList) {
		Double sum = Constants.DEFAULT_DOUBLE;
		for (WhSkuInventoryCommand inv : invBestList) {
			sum += inv.getSumOnHandQty();
		}
		return sum;
	}

	@Override
	public void releaseInventoryByOdoId(Long odoId, Warehouse wh) {
		Long ouId = wh.getId();
		WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
		String occupyCode = odo.getOdoCode();
		this.releaseInventoryByOccupyCode(occupyCode, wh);
	}
	
	@Override
	public void releaseInventoryByOccupyCode(String occupyCode, Warehouse wh) {
		Long ouId = wh.getId();
		List<WhSkuInventory> occupyInventory = whSkuInventoryDao.findOccupyInventory(occupyCode, ouId);
		// 还原库存日志
		for (WhSkuInventory skuInv : occupyInventory) {
			Long invId = skuInv.getId();
			Double qty = skuInv.getOnHandQty();
			Double oldQty = 0.0;
            if (true == wh.getIsTabbInvTotal()) {
            	oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(skuInv.getUuid(), ouId);
            }
            // 清除库存占用编码
            skuInv.setOccupationCode(null);
            skuInv.setOccupationLineId(null);
            skuInv.setOccupationCodeSource(null);
            whSkuInventoryDao.saveOrUpdateByVersion(skuInv);
            
            // 还原库存日志
			insertSkuInventoryLog(invId, qty, oldQty, wh.getIsTabbInvTotal(), ouId, 1L, null);
		}
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
    
    // TODO zhukai
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
	public void replenishmentToLines(List<WhWaveLine> lines, Long odoId, String bhCode, Map<String, List<ReplenishmentRuleCommand>> ruleMap, Map<String, String> map, Warehouse wh) {
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
								if (Constants.DEFAULT_DOUBLE.compareTo(tempQty) == 0) {
									break;
								}
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
								if (Constants.DEFAULT_DOUBLE.compareTo(tempQty) == 0) {
									break;
								}
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
    								// Double onHandQty = invCmd.getSumOnHandQty();
    								List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
    								Double useableQty = whSkuInventoryDao.getUseableQtyByUuidList(uuidList, ouId);
    								if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
										continue;
									}
    								List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
    								// 封装成方法获取补货超出数量和占用数量
    								Map<String, Double> mapQty = replenishmentUp(invs, occupyQty, useableQty, bhCode, occupyCode, occupyLineId, targetLocation, ruleId, wh);
    								occupyQty -= mapQty.get("qty");
    								moreQty += mapQty.get("moreQty");
    								if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    									startRepStrategy = rsc.getStrategyCode();
    									tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    									break FLAG;
    								}
    							}
    							// 向下补货和严格按照需求量补货
    						} else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
    							for (WhSkuInventoryCommand invCmd : uuids) {
    								// Double onHandQty = invCmd.getSumOnHandQty();
    								List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
    								Double useableQty = whSkuInventoryDao.getUseableQtyByUuidList(uuidList, ouId);
    								// 占用数量 >= 可用数量
    								if (-1 != occupyQty.compareTo(useableQty)) {
    									List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
    									occupyQty -= useableQty;
    									replenishmentDown(invs, useableQty, bhCode, occupyCode, occupyLineId, targetLocation, ruleId, wh);
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
    								// Double onHandQty = invCmd.getSumOnHandQty();
    								List<String> uuid = Arrays.asList(invCmd.getUuid().split(","));
    								Double useableQty = whSkuInventoryDao.getUseableQtyByUuidList(uuid, ouId);
    								if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
										continue;
									}
	    							List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuid);
    								// 封装成方法获取补货超出数量和占用数量
    								Map<String, Double> mapQty = replenishmentUp(invs, occupyQty, useableQty, bhCode, occupyCode, occupyLineId, targetLocation, ruleId, wh);
    								occupyQty -= mapQty.get("qty");
    								moreQty += mapQty.get("moreQty");
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
    								// Double onHandQty = invCmd.getSumOnHandQty();
    								List<String> uuid = Arrays.asList(invCmd.getUuid().split(","));
    								Double useableQty = whSkuInventoryDao.getUseableQtyByUuidList(uuid, ouId);
    								if (-1 != occupyQty.compareTo(useableQty)) {
    	    							List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuid);
        								occupyQty -= useableQty;
    									replenishmentDown(invs, useableQty, bhCode, occupyCode, occupyLineId, targetLocation, ruleId, wh);
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
    							List<String> uuid = Arrays.asList(invCmd.getUuid().split(","));
    							List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuid);
    							Double useableQty = whSkuInventoryDao.getUseableQtyByUuidList(uuid, ouId);
    							if (null != skuInvs && !skuInvs.isEmpty()) {
    								for (WhSkuInventoryCommand invCommand : skuInvs) {
    									// Double onHandQty = invCommand.getOnHandQty();
    									// OnHandQty <= useableQty
    									if (1 != invCommand.getOnHandQty().compareTo(useableQty)) {
    										if (-1 == occupyQty.compareTo(invCommand.getOnHandQty())) {
    											// 创建已分配库存,待移入库存
    											createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
    											useableQty -= occupyQty;
    											occupyQty = Constants.DEFAULT_DOUBLE;
    										} else {
    											// 创建已分配库存,待移入库存
    											createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, invCommand.getOnHandQty(), ruleId, GLOBAL_LOG_UPDATE);
    											useableQty -= invCommand.getOnHandQty();
    											occupyQty -= invCommand.getOnHandQty();
    										}
    									// OnHandQty > useableQty
    									} else {
    										// occupyQty < useableQty
    										if (-1 == occupyQty.compareTo(useableQty)) {
    											// 创建已分配库存,待移入库存
    											createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
    											useableQty -= occupyQty;
    											occupyQty = Constants.DEFAULT_DOUBLE;
    										} else {
    											// 创建已分配库存,待移入库存
    											createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, useableQty, ruleId, GLOBAL_LOG_UPDATE);
    											occupyQty -= useableQty;
    											useableQty = Constants.DEFAULT_DOUBLE;
    										}
    									}
    									if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
    										startRepStrategy = rsc.getStrategyCode();
    										tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
    										break FLAG;
    									}
    									if (0 == Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
    										break;
										}
    								}
    							}
							}
						} else {
							List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, occupyQty);
							if (null != skuInvs && !skuInvs.isEmpty()) {
								for (WhSkuInventoryCommand invCommand : skuInvs) {
									// Double onHandQty = invCommand.getOnHandQty();
									Double useableQty = whSkuInventoryDao.getUseableQtyByUuid(invCommand.getUuid(), ouId);
									if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
										continue;
									}
									occupyQty = replenishmentPiece(invCommand, occupyQty, useableQty, bhCode,
											occupyCode, occupyLineId, targetLocation, ruleId, wh);
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
			WhOdoLine odoLine = whOdoLineDao.findOdoLineById(line.getOdoLineId(), ouId);
			if (StringUtils.hasText(odoLine.getAssignFailReason()) && null != odoLine.getIsAssignSuccess() && !odoLine.getIsAssignSuccess()) {
				odoLine.setAssignFailReason(null);
				odoLine.setIsAssignSuccess(Boolean.TRUE);
				whOdoLineDao.saveOrUpdate(odoLine);
			}
		}
    	WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
    	if (StringUtils.hasText(odo.getAssignFailReason()) && null != odo.getIsAssignSuccess() && !odo.getIsAssignSuccess()) {
    		odo.setAssignFailReason(null);
    		odo.setIsAssignSuccess(Boolean.TRUE);
    		whOdoDao.saveOrUpdate(odo);
		}
    	map.clear();
    	map.putAll(tempMap);
	}
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Map<String, String> replenishmentToLinesNew(List<WhWaveLine> lines, Long odoId, String bhCode, Map<String, List<ReplenishmentRuleCommand>> ruleMap, Map<String, String> map, Warehouse wh) {
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
								if (Constants.DEFAULT_DOUBLE.compareTo(tempQty) == 0) {
									break;
								}
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
								if (Constants.DEFAULT_DOUBLE.compareTo(tempQty) == 0) {
									break;
								}
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
    					Boolean isExpirationSku = skuDao.checkIsExpirationSku(skuId, ouId);
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
    				skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
    				List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, occupyQty);
    				// 补货逻辑
    				Map<String, Double> replenishmentMap = this.replenishment(skuInvs, occupyQty, allocateUnitCodes, rsc.getReplenishmentCode(), bhCode, occupyCode, occupyLineId, targetLocation, ruleId, wh);
    				occupyQty -= replenishmentMap.get("qty") == null ? 0.0 : replenishmentMap.get("qty");
    				moreQty = replenishmentMap.get("moreQty") == null ? 0.0 : replenishmentMap.get("moreQty");
    				if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
						startRepStrategy = rsc.getStrategyCode();
						tempMap.put(key, startRepStrategy + "_" + (bhMoreQty + moreQty));
						break FLAG;
					}
    			}
			}
			if (-1 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty)) {
				log.info("replenishmentToLines is error, replenishment qty is not enough, occupyQty:[{}], waveId:[{}], odoId:[{}], skuId:[{}], ouId:[{}], logId:[{}]", occupyQty, line.getWaveId(), line.getOdoId(), skuId, ouId, logId);
				throw new BusinessException(0);
			}
			WhOdoLine odoLine = whOdoLineDao.findOdoLineById(line.getOdoLineId(), ouId);
			if (StringUtils.hasText(odoLine.getAssignFailReason()) && null != odoLine.getIsAssignSuccess() && !odoLine.getIsAssignSuccess()) {
				odoLine.setAssignFailReason(null);
				odoLine.setIsAssignSuccess(Boolean.TRUE);
				whOdoLineDao.saveOrUpdate(odoLine);
			}
		}
    	WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
    	if (StringUtils.hasText(odo.getAssignFailReason()) && null != odo.getIsAssignSuccess() && !odo.getIsAssignSuccess()) {
    		odo.setAssignFailReason(null);
    		odo.setIsAssignSuccess(Boolean.TRUE);
    		whOdoDao.saveOrUpdate(odo);
		}
    	map.clear();
    	map.putAll(tempMap);
    	return map;
	}

	private Double replenishmentDown(List<WhSkuInventoryCommand> invs, Double useableQty, String bhCode, String occupyCode, Long occupyLineId, Long targetLocation, Long ruleId, Warehouse wh) {
		for (WhSkuInventoryCommand invCommand : invs) {
			// OnHandQty <= useableQty
			if (1 != invCommand.getOnHandQty().compareTo(useableQty)) {
				// 创建已分配库存,待移入库存
				createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, invCommand.getOnHandQty(), ruleId, GLOBAL_LOG_UPDATE);
				useableQty -= invCommand.getOnHandQty();
			} else {
				// 创建已分配库存,待移入库存
				createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, useableQty, ruleId, GLOBAL_LOG_UPDATE);
				useableQty = Constants.DEFAULT_DOUBLE;
			}
			if (0 == Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
				break;
			}
		}
		return useableQty;
	}
    
    private Map<String, Double> replenishmentUp(List<WhSkuInventoryCommand> invs, Double occupyQty, Double useableQty, String bhCode, String occupyCode, Long occupyLineId, Long targetLocation, Long ruleId, Warehouse wh) {
    	Double moreQty = Constants.DEFAULT_DOUBLE;
    	Double qty = Constants.DEFAULT_DOUBLE;
    	for (WhSkuInventoryCommand invCommand : invs) {
			if (0 == Constants.DEFAULT_DOUBLE.compareTo(occupyQty) && -1 == Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
				if (1 != invCommand.getOnHandQty().compareTo(useableQty)) {
					// OnHandQty <= useableQty
					createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, invCommand.getOnHandQty(), ruleId, GLOBAL_LOG_UPDATE);
					useableQty -= invCommand.getOnHandQty();
					moreQty += invCommand.getOnHandQty();
				} else {
					// OnHandQty > useableQty
					createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, useableQty, ruleId, GLOBAL_LOG_UPDATE);
					moreQty += useableQty;
					useableQty = Constants.DEFAULT_DOUBLE;
				}
				continue;
			}
			// OnHandQty <= useableQty
			if (1 != invCommand.getOnHandQty().compareTo(useableQty)) {
				if (-1 == occupyQty.compareTo(invCommand.getOnHandQty())) {
					// occupyQty < OnHandQty
					// 创建已分配库存,待移入库存
					createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
					createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, invCommand.getOnHandQty() - occupyQty, ruleId, GLOBAL_LOG_UPDATE);
					moreQty += invCommand.getOnHandQty() - occupyQty;
					qty += occupyQty;
					useableQty -= invCommand.getOnHandQty();
					occupyQty = Constants.DEFAULT_DOUBLE;
				} else {
					// occupyQty >= OnHandQty
					// 创建已分配库存,待移入库存
					createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, invCommand.getOnHandQty(), ruleId, GLOBAL_LOG_UPDATE);
					qty += invCommand.getOnHandQty();
					useableQty -= invCommand.getOnHandQty();
					occupyQty -= invCommand.getOnHandQty();
				}
			// OnHandQty > useableQty
			} else {
				if (-1 == occupyQty.compareTo(useableQty)) {
					// occupyQty < useableQty
					// 创建已分配库存,待移入库存
					createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
					createSkuInventoryAllocatedAndTobefilled(invCommand, null, bhCode, null, targetLocation, wh, useableQty - occupyQty, ruleId, GLOBAL_LOG_UPDATE);
					moreQty += useableQty - occupyQty;
					qty += occupyQty;
					useableQty = Constants.DEFAULT_DOUBLE;
					occupyQty = Constants.DEFAULT_DOUBLE;
				} else {
					// occupyQty >= useableQty
					// 创建已分配库存,待移入库存
					createSkuInventoryAllocatedAndTobefilled(invCommand, occupyCode, bhCode, occupyLineId, targetLocation, wh, useableQty, ruleId, GLOBAL_LOG_UPDATE);
					qty += useableQty;
					occupyQty -= useableQty;
					useableQty = Constants.DEFAULT_DOUBLE;
				}
			}
		}
    	Map<String, Double> map = new HashMap<String, Double>();
    	map.put("qty", qty);
    	map.put("moreQty", moreQty);
    	return map;
    }
    
    private void createSkuInventoryAllocatedAndTobefilled(WhSkuInventoryCommand invCommand, String occupyCode, String bhCode, Long occupyLineId, Long locationId, Warehouse wh, Double qty, Long ruleId, String type) {
        if (qty.doubleValue() == 0) {
            return;
        }
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
		insertGlobalLog(GLOBAL_LOG_INSERT, allocated, ouId, 1L, null, null);
		
		WhSkuInventoryTobefilled tobefilled = new WhSkuInventoryTobefilled();
		BeanUtils.copyProperties(invCommand, tobefilled);
		tobefilled.setId(null);
		tobefilled.setInsideContainerId(null);
		tobefilled.setOuterContainerId(null);
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
		insertGlobalLog(GLOBAL_LOG_INSERT, tobefilled, ouId, 1L, null, null);
		
		/*Double oldQty = 0.0;
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
		}*/
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
    public void replenishmentToLocation(ReplenishmentMsg msg, List<ReplenishmentRuleCommand> ruleList, Warehouse wh) {
        // @mender yimin.lu 2017/3/24 调整库位补货逻辑，@mender kai.zhu,调用补货通用接口
        String bhCode = this.codeManager.generateCode(Constants.WMS, Constants.BH_MODEL_URL, null, null, null);// 补货编码
        Long ouId = wh.getId();
        Long skuId = msg.getSkuId();
        double upperLimitQty = msg.getUpperLimitQty().doubleValue();
        Long locationId = msg.getLocationId();
        if(ruleList==null||ruleList.size()==0){
         return;   
        }
        for(ReplenishmentRuleCommand rule:ruleList){
            
            Long ruleId = rule.getId();
            
            List<ReplenishmentStrategyCommand> strategyList = this.replenishmentStrategyDao.findCommandByRuleIdWithPriority(rule.getId(), ouId);
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
                // 分配单位
                List<String> units = Arrays.asList(rsc.getAllocateUnitCodes().split(","));
                // 封装查询条件
                WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
                skuCommand.setSkuId(skuId);
                skuCommand.setAreaId(rsc.getAreaId());
                skuCommand.setOuId(ouId);
                skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
                List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, upperLimitQty);
                // 补货逻辑
                Map<String, Double> replenishmentMap = this.replenishment(skuInvs, upperLimitQty, units, rsc.getReplenishmentCode(), bhCode, null, null, locationId, ruleId, wh);
                upperLimitQty -= replenishmentMap.get("qty") == null ? 0.0 : replenishmentMap.get("qty");

                if (upperLimitQty == 0) {
                    break;
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
        task.setReplenishmentCode(bhCode);
        task.setLocationId(locationId);
        task.setStatus(ReplenishmentTaskStatus.REPLENISHMENT_TASK_NEW);
        task.setOuId(ouId);
        task.setCreateTime(new Date());
        task.setLastModifyTime(new Date());
        task.setIsCreateWork(false);
        this.replenishmentTaskDao.insert(task);

    }
    
    // TODO　zhukai 
    /**
     * 补货方法
     * @author kai.zhu
     * @version 2017年3月21日
     * @param skuInvs	库存集合
     * @param qty		占用数量
     * @param units		占用单位(托盘,货箱,件)
     * @param replenishmentCode	补货策略(向上,向下补货)
     * @param bhCode	补货编码
     * @param occupyCode	占用编码(odoCode)
     * @param occupyLineId	占用行(odoLineId)
     * @param targetLocationId	目标库位Id
     * @param ruleId	补货条件Id
     * @param wh		仓库
     * @return
     */
	public Map<String, Double> replenishment(List<WhSkuInventoryCommand> skuInvs, Double qty, List<String> units,
			String replenishmentCode, String bhCode, String occupyCode, Long occupyLineId, Long targetLocationId, Long ruleId, Warehouse wh) {
		Long ouId = wh.getId();
		Double occupyQty = Constants.DEFAULT_DOUBLE;
		Double moreQty = Constants.DEFAULT_DOUBLE;
    	if (null == skuInvs || skuInvs.isEmpty()) {
			return new HashMap<String, Double>();
		}
    	for (int i = 0; i < skuInvs.size(); i++) {
    		WhSkuInventoryCommand inv = skuInvs.get(i);
    		// 策略中包含托盘
    		if (units.contains(Constants.ALLOCATE_UNIT_TP) && null != inv.getOuterContainerId()) {
    			List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findUseableInventoryByOuterContainerId(inv.getOuterContainerId(), inv.getSkuId(), ouId);
				Double useableQty = this.sumContainerInventoryQuantity(skuInvList, ouId);
				if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
					continue;
				}
				if (Constants.REPLENISHMENT_UP.equals(replenishmentCode)) {
					// 向上补货
					Map<String, Double> mapQty = this.replenishmentUp(skuInvList, qty, useableQty, bhCode, occupyCode, occupyLineId, targetLocationId, ruleId, wh);
					qty -= mapQty.get("qty");
					occupyQty += mapQty.get("qty");
					moreQty += mapQty.get("moreQty");
					this.deleteSkuInvs(skuInvs, skuInvList);
					i--;
					if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
						break;
					}
					continue;
				} else if (Constants.REPLENISHMENT_DOWN.equals(replenishmentCode) || Constants.REPLENISHMENT_ONDEMAND.equals(replenishmentCode)) {
					// 向下补货和严格按需求量补货
					if (-1 != qty.compareTo(useableQty)) {
						this.replenishmentDown(skuInvList, useableQty, bhCode, occupyCode, occupyLineId, targetLocationId, ruleId, wh);
						qty -= useableQty;
						occupyQty += useableQty;
						this.deleteSkuInvs(skuInvs, skuInvList);
						i--;
						if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
							break;
						}
						continue;
					}
				}
    		}
    		// 策略中包含货箱
    		if (units.contains(Constants.ALLOCATE_UNIT_HX) && null != inv.getInsideContainerId()) {
    			List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findUseableInventoryByInsideContainerId(inv.getInsideContainerId(), inv.getSkuId(), ouId);
				Double useableQty = this.sumContainerInventoryQuantity(skuInvList, ouId);
				if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
					continue;
				}
				if (Constants.REPLENISHMENT_UP.equals(replenishmentCode)) {
					// 向上补货
					Map<String, Double> mapQty = this.replenishmentUp(skuInvList, qty, useableQty, bhCode, occupyCode, occupyLineId, targetLocationId, ruleId, wh);
					qty -= mapQty.get("qty");
					occupyQty += mapQty.get("qty");
					moreQty += mapQty.get("moreQty");
					this.deleteSkuInvs(skuInvs, skuInvList);
					i--;
					if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
						break;
					}
					continue;
				} else if (Constants.REPLENISHMENT_DOWN.equals(replenishmentCode) || Constants.REPLENISHMENT_ONDEMAND.equals(replenishmentCode)) {
					// 向下补货和严格按需求量补货
					if (-1 != qty.compareTo(useableQty)) {
						this.replenishmentDown(skuInvList, useableQty, bhCode, occupyCode, occupyLineId, targetLocationId, ruleId, wh);
						qty -= useableQty;
						occupyQty += useableQty;
						this.deleteSkuInvs(skuInvs, skuInvList);
						i--;
						if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
							break;
						}
						continue;
					}
				}
    		}
    		// 策略中包含件
    		if (units.contains(Constants.ALLOCATE_UNIT_PIECE)) {
				Double useableQty = whSkuInventoryDao.getUseableQtyByUuid(inv.getUuid(), ouId);
				if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
					skuInvs.remove(i--);
					continue;
				}
				Double num = this.replenishmentPiece(inv, qty, useableQty, bhCode, occupyCode, occupyLineId,targetLocationId, ruleId, wh);
				qty -= num;
				occupyQty += num;
				if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
					break;
				}
    		}
		}
    	Map<String, Double> map = new HashMap<String, Double>();
    	map.put("qty", occupyQty);
    	map.put("moreQty", moreQty);
    	return map;
    }

	private Double replenishmentPiece(WhSkuInventoryCommand inv, Double occupyQty, Double useableQty, String bhCode,
			String occupyCode, Long occupyLineId, Long targetLocationId, Long ruleId, Warehouse wh) {
		Double num = Constants.DEFAULT_DOUBLE;
		// OnHandQty <= useableQty
		if (1 != inv.getOnHandQty().compareTo(useableQty)) {
			if (-1 == occupyQty.compareTo(inv.getOnHandQty())) {
				// 创建已分配库存,待移入库存
				createSkuInventoryAllocatedAndTobefilled(inv, occupyCode, bhCode, occupyLineId, targetLocationId, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
				num += occupyQty;
			} else {
				// 创建已分配库存,待移入库存
				createSkuInventoryAllocatedAndTobefilled(inv, occupyCode, bhCode, occupyLineId, targetLocationId, wh, inv.getOnHandQty(), ruleId, GLOBAL_LOG_UPDATE);
				num += inv.getOnHandQty();
			}
		// OnHandQty > useableQty
		} else {
			// occupyQty < useableQty
			if (-1 == occupyQty.compareTo(useableQty)) {
				// 创建已分配库存,待移入库存
				createSkuInventoryAllocatedAndTobefilled(inv, occupyCode, bhCode, occupyLineId, targetLocationId, wh, occupyQty, ruleId, GLOBAL_LOG_UPDATE);
				num += occupyQty;
			} else {
				// 创建已分配库存,待移入库存
				createSkuInventoryAllocatedAndTobefilled(inv, occupyCode, bhCode, occupyLineId, targetLocationId, wh, useableQty, ruleId, GLOBAL_LOG_UPDATE);
				num += useableQty;
			}
		}
		return num;
	}
    
    private Double sumContainerInventoryQuantity(List<WhSkuInventoryCommand> skuInvList, Long ouId) {
		Double qty = Constants.DEFAULT_DOUBLE;
		if (null == skuInvList || skuInvList.isEmpty()) {
			return qty;
		}
		Set<String> uuidSet = new HashSet<String>();
		for (WhSkuInventoryCommand skuInv : skuInvList) {
			uuidSet.add(skuInv.getUuid());
		}
		// 根据uuid查出已分配的库存并减去得到可用的库存
		qty = whSkuInventoryDao.getUseableQtyByUuidList(new ArrayList<String>(uuidSet), ouId);
		return qty;
	}
    
    private void deleteSkuInvs(List<WhSkuInventoryCommand> list, List<WhSkuInventoryCommand> deleteList) {
		for (WhSkuInventoryCommand deleteInv : deleteList) {
			for (int i = 0; i < list.size(); i++) {
				WhSkuInventoryCommand skuInv = list.get(i);
				if (skuInv.getId().equals(deleteInv.getId())) {
					list.remove(i);
					break;
				}
			}
		}
	}

    private Double occupySkuInventoryOfPieceToLimit(Warehouse wh, String replenishmentCode, ReplenishmentStrategyCommand rsc, Long locationId, Long ruleId, Long skuId, Long ouId, Double upperLimitQty) {
        if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(rsc.getStrategyCode())) {
            return replenishPieceQuantityBestMatch(wh, replenishmentCode, rsc, locationId, ruleId, skuId, ouId, upperLimitQty);
        } else {
            return replenishPieceElse(wh, replenishmentCode, rsc, locationId, ruleId, skuId, ouId, upperLimitQty);
        }
    }

    private Double replenishPieceElse(Warehouse wh, String replenishmentCode, ReplenishmentStrategyCommand rsc, Long locationId, Long ruleId, Long skuId, Long ouId, Double upperLimitQty) {
        WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
        skuCommand.setSkuId(skuId);
        skuCommand.setAreaId(rsc.getAreaId());
        skuCommand.setOuId(ouId);
        skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
        List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, upperLimitQty);
        if (null != skuInvs && !skuInvs.isEmpty()) {
            for (WhSkuInventoryCommand invCommand : skuInvs) {
                Double onHandQtySingle = invCommand.getOnHandQty();
                Double usableQtySingle = this.whSkuInventoryDao.getUseableQtyByUuid(invCommand.getUuid(), ouId);
                if (usableQtySingle.doubleValue() == 0) {
                    continue;
                }
                Double upperCounter;// 同UUID的可用数量
                if (onHandQtySingle <= usableQtySingle) {
                    upperCounter = onHandQtySingle;
                } else {
                    upperCounter = usableQtySingle;
                }

                invCommand.setOnHandQty(upperCounter);
                // 占用数量 >= 在库数量
                if (upperLimitQty >= upperCounter) {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                    upperLimitQty -= upperCounter;
                } else {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                    upperLimitQty = 0.0;
                }
                if (upperLimitQty.doubleValue() == 0) {
                    break;
                }
            }
        }
        return upperLimitQty;
    }

    private Double replenishPieceQuantityBestMatch(Warehouse wh, String replenishmentCode, ReplenishmentStrategyCommand rsc, Long locationId, Long ruleId, Long skuId, Long ouId, Double upperLimitQty) {
        WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
        skuCommand.setSkuId(skuId);
        skuCommand.setAreaId(rsc.getAreaId());
        skuCommand.setOuId(ouId);
        skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
        List<WhSkuInventoryCommand> invs = findInventoryUuidByBestMatchAndPiece(skuCommand, upperLimitQty);
        for (WhSkuInventoryCommand invCmd : invs) {
            List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
            List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);

            if (skuInvs == null || skuInvs.isEmpty()) {
                continue;
            }
            for (WhSkuInventoryCommand invCommand : skuInvs) {

                Double onHandQtySingle = invCommand.getOnHandQty();
                Double usableQtySingle = this.whSkuInventoryDao.getUseableQtyByUuid(invCommand.getUuid(), ouId);
                if (usableQtySingle.doubleValue() == 0) {
                    continue;
                }
                Double upperCounter;// 同UUID的可用数量
                if (onHandQtySingle <= usableQtySingle) {
                    upperCounter = onHandQtySingle;
                } else {
                    upperCounter = usableQtySingle;
                }

                invCommand.setOnHandQty(upperCounter);
                // 占用数量 >= 在库数量
                if (upperLimitQty >= upperCounter) {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                    upperLimitQty -= upperCounter;
                } else {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                    upperLimitQty = 0.0;
                }
                if (upperLimitQty.doubleValue() == 0) {
                    break;
                }
            }
            if (upperLimitQty.doubleValue() == 0) {
                break;
            }

        }
        return upperLimitQty;
    }

    /**
     * 货箱的补货
     * 
     * @param wh
     * @param replenishmentCode
     * @param rsc
     * @param locationId
     * @param ruleId
     * @param skuId
     * @param ouId
     * @param upperLimitQty
     * @return
     */
    private Double occupySkuInventoryOfBoxToLimit(Warehouse wh, String replenishmentCode, ReplenishmentStrategyCommand rsc, Long locationId, Long ruleId, Long skuId, Long ouId, Double upperLimitQty) {
        WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
        skuCommand.setSkuId(skuId);
        skuCommand.setAreaId(rsc.getAreaId());
        skuCommand.setOuId(ouId);
        skuCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_HX);
        List<WhSkuInventoryCommand> uuids = this.findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, upperLimitQty);
        if (uuids == null || uuids.size() == 0) {
            return upperLimitQty;

        }

        if (Constants.REPLENISHMENT_UP.equals(rsc.getReplenishmentCode())) {
            return replenishBoxUpToLimit(wh, replenishmentCode, locationId, ruleId, skuId, ouId, uuids, upperLimitQty);
        } else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
            return replenishBoxDownOrOndemandToLimit(wh, replenishmentCode, locationId, ruleId, skuId, ouId, uuids, upperLimitQty);
        }
        return upperLimitQty;
    }

    /**
     * 货箱向下补货或者严格按照需求补货
     * 
     * @param wh
     * @param replenishmentCode
     * @param locationId
     * @param ruleId
     * @param skuId
     * @param ouId
     * @param uuids
     * @param upperLimitQty
     * @return
     */
    private Double replenishBoxDownOrOndemandToLimit(Warehouse wh, String replenishmentCode, Long locationId, Long ruleId, Long skuId, Long ouId, List<WhSkuInventoryCommand> uuids, Double upperLimitQty) {
        for (WhSkuInventoryCommand invCmd : uuids) {
            Double onHandQty = invCmd.getSumOnHandQty();
            List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
            Double usableQty = this.whSkuInventoryDao.getUseableQtyByUuidList(uuidList, ouId);
            Double allocatedQty = onHandQty.doubleValue() - usableQty;// 已分配
            // 占用数量 >= 在库数量
            if (usableQty.doubleValue() == 0) {
                continue;
            }
            if (upperLimitQty < usableQty) {
                break;
            }
            List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
            for (WhSkuInventoryCommand invCommand : invs) {
                // 创建已分配库存,待移入库存
                if (invCommand.getOnHandQty() <= allocatedQty) {
                    allocatedQty -= invCommand.getOnHandQty();
                    continue;
                }
                invCommand.setOnHandQty(invCommand.getOnHandQty() - allocatedQty);
                allocatedQty = 0d;
                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
            }
            upperLimitQty -= onHandQty;
            if (upperLimitQty.doubleValue() == 0) {
                break;
            }

        }
        return upperLimitQty;
    }

    private Double replenishBoxUpToLimit(Warehouse wh, String replenishmentCode, Long locationId, Long ruleId, Long skuId, Long ouId, List<WhSkuInventoryCommand> uuids, Double upperLimitQty) {
        for (WhSkuInventoryCommand invCmd : uuids) {
            Double onHandQty = invCmd.getSumOnHandQty();
            // 新增逻辑：去除掉已经分配掉的库存
            List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
            Double usableQty = this.whSkuInventoryDao.getUseableQtyByUuidList(uuidList, ouId);
            Double allocatedQty = onHandQty.doubleValue() - usableQty;// 已分配

            if (usableQty.doubleValue() == 0) {
                continue;
            }
            if (upperLimitQty.doubleValue() > usableQty) {
                invCmd.setOuId(wh.getId());
                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
                for (WhSkuInventoryCommand invCommand : invs) {
                    // 创建已分配库存,待移入库存
                    if (invCommand.getOnHandQty() < allocatedQty) {
                        allocatedQty -= invCommand.getOnHandQty();
                        continue;
                    }
                    invCommand.setOnHandQty(invCommand.getOnHandQty() - allocatedQty);
                    allocatedQty = 0d;
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                }
                continue;
            }

            invCmd.setOuId(wh.getId());
            List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findInventoryByUuidAndCondition(invCmd);
            for (WhSkuInventoryCommand invCommand : invs) {
                if (invCommand.getOnHandQty() <= allocatedQty) {
                    allocatedQty -= invCommand.getOnHandQty();
                    continue;
                }
                invCommand.setOnHandQty(invCommand.getOnHandQty() - allocatedQty);
                allocatedQty = 0d;
                if (upperLimitQty.doubleValue() == 0) {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                    continue;
                }
                if (upperLimitQty.doubleValue() > invCommand.getOnHandQty()) {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                    upperLimitQty -= invCommand.getOnHandQty();
                    continue;
                }
                // 创建已分配库存,待移入库存
                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                invCommand.setOnHandQty(invCommand.getOnHandQty() - upperLimitQty);
                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                upperLimitQty = 0.0;

            }
            if (upperLimitQty.doubleValue() == 0) {
                break;
            }

        }
        return upperLimitQty;
    }

    private Double occupySkuInventoryOfPalletToLimit(Warehouse wh, String replenishmentCode, ReplenishmentStrategyCommand rsc, Long locationId, Long ruleId, Long skuId, Long ouId, Double upperLimitQty) {
        return occupySkuInventoryOfContainerToLimit(wh, replenishmentCode, rsc, locationId, ruleId, skuId, ouId, upperLimitQty, Constants.ALLOCATE_UNIT_TP);
    }
    private Double occupySkuInventoryOfContainerToLimit(Warehouse wh, String replenishmentCode, ReplenishmentStrategyCommand rsc, Long locationId, Long ruleId, Long skuId, Long ouId, Double upperLimitQty, String allocateUnitCode) {
        WhSkuInventoryCommand skuCommand = new WhSkuInventoryCommand();
        skuCommand.setSkuId(skuId);
        skuCommand.setAreaId(rsc.getAreaId());
        skuCommand.setOuId(ouId);
        skuCommand.setAllocateUnitCodes(allocateUnitCode);
        List<WhSkuInventoryCommand> uuids = this.findInventorysByAllocateStrategy(rsc.getStrategyCode(), skuCommand, upperLimitQty);
        if (uuids == null || uuids.size() == 0) {
            return upperLimitQty;

        }

        if (Constants.REPLENISHMENT_UP.equals(rsc.getReplenishmentCode())) {
            return replenishUpToLimit(wh, replenishmentCode, locationId, ruleId, skuId, ouId, uuids, upperLimitQty);
        } else if (Constants.REPLENISHMENT_DOWN.equals(rsc.getReplenishmentCode()) || Constants.REPLENISHMENT_ONDEMAND.equals(rsc.getReplenishmentCode())) {
            return replenishDownOrOndemandToLimit(wh, replenishmentCode, locationId, ruleId, skuId, ouId, uuids, upperLimitQty);
        }
        return upperLimitQty;
    }

    

    private Double replenishUpToLimit(Warehouse wh, String replenishmentCode, Long locationId, Long ruleId, Long skuId, Long ouId, List<WhSkuInventoryCommand> uuids, Double upperLimitQty) {
        for (WhSkuInventoryCommand invCmd : uuids) {
            Double onHandQty = invCmd.getSumOnHandQty();
            // 新增逻辑：去除掉已经分配掉的库存
            List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
            Double usableQty = this.whSkuInventoryDao.getUseableQtyByUuidList(uuidList, ouId);
            Double allocatedQty = onHandQty.doubleValue() - usableQty;// 已分配

            if (upperLimitQty.doubleValue() > usableQty) {

                upperLimitQty -= (onHandQty - allocatedQty);


                List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
                for (WhSkuInventoryCommand invCommand : invs) {
                    // 创建已分配库存,待移入库存
                    if (invCommand.getOnHandQty() < allocatedQty) {
                        allocatedQty -= invCommand.getOnHandQty();
                        continue;
                    }
                    invCommand.setOnHandQty(invCommand.getOnHandQty() - allocatedQty);
                    allocatedQty = 0d;

                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                }

                continue;
            }

            List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
            for (WhSkuInventoryCommand invCommand : invs) {
                if (invCommand.getOnHandQty() <= allocatedQty) {
                    allocatedQty -= invCommand.getOnHandQty();
                    continue;
                }
                invCommand.setOnHandQty(invCommand.getOnHandQty() - allocatedQty);
                allocatedQty = 0d;
                if (upperLimitQty.doubleValue() == 0) {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                    continue;
                }
                if (upperLimitQty.doubleValue() > invCommand.getOnHandQty()) {
                    // 创建已分配库存,待移入库存
                    createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                    upperLimitQty -= invCommand.getOnHandQty();
                    continue;
                }
                // 创建已分配库存,待移入库存
                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_UPDATE);
                invCommand.setOnHandQty(invCommand.getOnHandQty() - upperLimitQty);
                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
                upperLimitQty = 0.0;

            }
            if (upperLimitQty.doubleValue() == 0) {
                break;
            }

        }
        return upperLimitQty;


    }

    private Double replenishDownOrOndemandToLimit(Warehouse wh, String replenishmentCode, Long locationId, Long ruleId, Long skuId, Long ouId, List<WhSkuInventoryCommand> uuids, Double upperLimitQty) {
        for (WhSkuInventoryCommand invCmd : uuids) {
            Double onHandQty = invCmd.getSumOnHandQty();
            List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
            Double usableQty = this.whSkuInventoryDao.getUseableQtyByUuidList(uuidList, ouId);
            Double allocatedQty = onHandQty.doubleValue() - usableQty;// 已分配
            // 占用数量 >= 在库数量
            if (usableQty.doubleValue() == 0) {
                continue;
            }
            if (upperLimitQty < usableQty) {
                break;
            }

            List<WhSkuInventoryCommand> invs = whSkuInventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
            for (WhSkuInventoryCommand invCommand : invs) {
                if (invCommand.getOnHandQty() <= allocatedQty) {
                    allocatedQty -= invCommand.getOnHandQty();
                    continue;
                }
                invCommand.setOnHandQty(invCommand.getOnHandQty() - allocatedQty);
                allocatedQty = 0d;
                // 创建已分配库存,待移入库存
                createSkuInventoryAllocatedAndTobefilled(invCommand, null, replenishmentCode, null, locationId, wh, upperLimitQty, ruleId, GLOBAL_LOG_DELETE);
            }
            upperLimitQty -= onHandQty;
            if (upperLimitQty.doubleValue() == 0) {
                break;
            }

        }
        return upperLimitQty;

    }

    /***
     * pda拣货生成容器库存
     * @param operationId
     * @param ouId
     */
    public void pickingAddContainerInventory(Long containerId,Long locationId,String skuAttrIds,Long operationId,Long ouId,Boolean isTabbInvTotal,Long userId,Integer pickingWay,Integer scanPattern,Double scanSkuQty,String outBoundBox,
                                             Long turnoverBoxId,Long outerContainerId,Long insideContainerId,Boolean isShortPicking,Integer useContainerLatticeNo,Set<Long> insideContainerIds){
        
        Set<Long> skuInvCmdList = new HashSet<Long>();
        Set<Long> execLineIds = new HashSet<Long>();
        Set<Long> invSkuIds = new HashSet<Long>();
        if(Constants.PICKING_WAY_SIX == pickingWay){   //整箱拣货
            //到库存表中查询
            List<WhSkuInventoryCommand> allSkuInvList = whSkuInventoryDao.getWhSkuInventoryByOccupationLineId(ouId, operationId,outerContainerId,insideContainerId);
            if(null == allSkuInvList || allSkuInvList.size() == 0){
                    throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
            }
            for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                      if(isShortPicking) {  //短拣的商品生成库位库存
                            Long  invSkuId =  this.addLocInventory(skuInvCmd, skuInvCmd.getOnHandQty(), isTabbInvTotal, ouId, userId,isShortPicking);
                            invSkuIds.add(invSkuId);
                       }else{
                            Long  invSkuId =  this.addContianerInventory(pickingWay,skuInvCmd, skuInvCmd.getOnHandQty(), isTabbInvTotal, ouId, userId, null, outerContainerId, null, null,insideContainerId);  //整托整箱模式
                            invSkuIds.add(invSkuId);
                      }
             }
            List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId,outerContainerId,insideContainerId);
            if(null== operationExecLineList || operationExecLineList.size()==0) {
                throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
            }
            for(WhOperationExecLine opLineExec:operationExecLineList) {
                Long execid = opLineExec.getId();
                execLineIds.add(execid); 
            }
        }else if(Constants.PICKING_WAY_FIVE == pickingWay){  //整托拣货
            //到库存表中查询
            List<WhSkuInventoryCommand> allSkuInvList = whSkuInventoryDao.getWhSkuInventoryByOccupationLineId(ouId, operationId,outerContainerId,null);
            if(null == allSkuInvList || allSkuInvList.size() == 0){
                    throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
            }
            for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                //整托情况
                for(Long icId:insideContainerIds){
                 Long  invSkuId =  this.addContianerInventory(pickingWay,skuInvCmd, skuInvCmd.getOnHandQty(), isTabbInvTotal, ouId, userId, null, outerContainerId, null, null,icId); 
                 invSkuIds.add(invSkuId);
                }
            }
            List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId,null,insideContainerId);
            if(null== operationExecLineList || operationExecLineList.size()==0) {
                throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
            }
            for(WhOperationExecLine opLineExec:operationExecLineList) {
                Long execid = opLineExec.getId();
                execLineIds.add(execid); 
            }
        }else{//非整托整箱
            //到库存表中查询
            List<WhSkuInventoryCommand> allSkuInvList = whSkuInventoryDao.getWhSkuInventoryByOccupationLineId(ouId, operationId,outerContainerId,insideContainerId);
            if(null == allSkuInvList || allSkuInvList.size() == 0){
                    throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
            }  
            for(WhSkuInventoryCommand skuInvCmd:allSkuInvList){
                String invCmdSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                    //作业执行明细id
                            if(skuAttrIds.equals(invCmdSkuAttrIds) && skuInvCmd.getLocationId().longValue() == locationId.longValue()){
                                if(Constants.PICKING_WAY_THREE == pickingWay) {   //使用出库箱(使用小车加出库箱和只使用出库箱的情况)
                                         skuInvCmdList.add(skuInvCmd.getId());
                                         if(isShortPicking) {  //短拣的商品生成库位库存
                                               Long invSkuId =     this.addLocInventory(skuInvCmd, scanSkuQty, isTabbInvTotal, ouId, userId,isShortPicking);
                                               invSkuIds.add(invSkuId);
                                         }
                                         Long invSkuId =   this.addContianerInventory(pickingWay,skuInvCmd, scanSkuQty, isTabbInvTotal, ouId, userId, outBoundBox, null, null, null,null);  //出库箱模式,添加容器库存
                                         invSkuIds.add(invSkuId);
                               }
                               if(Constants.PICKING_WAY_FOUR == pickingWay){   //周转箱
                                        skuInvCmdList.add(skuInvCmd.getId());
                                        if(isShortPicking) {  //短拣的商品生成库位库存
                                             Long invSkuId =     this.addLocInventory(skuInvCmd, scanSkuQty, isTabbInvTotal, ouId, userId,isShortPicking);
                                             invSkuIds.add(invSkuId);
                                        }
                                        Long invSkuId =  this.addContianerInventory(pickingWay,skuInvCmd,scanSkuQty, isTabbInvTotal, ouId, userId, null, null, null, turnoverBoxId,null);  //出库箱模式,添加容器库存
                                        invSkuIds.add(invSkuId);
                               }
                                if(null != containerId){ //使用小车(小车加出库箱)
                                      skuInvCmdList.add(skuInvCmd.getId());
                                      Long  invSkuId = null;
                                      if(isShortPicking) {  //短拣的商品生成库位库存
                                            this.addLocInventory(skuInvCmd, scanSkuQty, isTabbInvTotal, ouId, userId,isShortPicking);
                                      }
                                      if(Constants.PICKING_WAY_ONE == pickingWay){  //小车
                                            invSkuId =  this.addContianerInventory(pickingWay,skuInvCmd, scanSkuQty, isTabbInvTotal, ouId, userId,  null, containerId, useContainerLatticeNo, null,null);  //出库箱模式,添加容器库存
                                      }
                                      if(Constants.PICKING_WAY_TWO == pickingWay){
                                            invSkuId =  this.addContianerInventory(pickingWay,skuInvCmd, scanSkuQty, isTabbInvTotal, ouId, userId, outBoundBox, containerId, useContainerLatticeNo, null,null);  //出库箱模式,添加容器库存
                                      }
                                      invSkuIds.add(invSkuId);
                                 }
                                Double newQty = skuInvCmd.getOnHandQty()-scanSkuQty;
                                if(newQty > 0) {
                                     this.addLocInventory(skuInvCmd, newQty, isTabbInvTotal, ouId, userId,false);
                                }
                                int result1 =  whSkuInventoryDao.deleteWhSkuInventoryById(skuInvCmd.getId(),ouId);
                                if(result1 < 1) {
                                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                                }
                                break;  //跳出当前循环
                            }
            }
            List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId,outerContainerId,insideContainerId);
            if(null== operationExecLineList || operationExecLineList.size()==0) {
                throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
            }
            for(WhOperationExecLine opLineExec:operationExecLineList) {
                String execSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(opLineExec);
                if(opLineExec.getFromLocationId().equals(locationId) && skuAttrIds.equals(execSkuAttrIds) && scanSkuQty.equals(Double.valueOf(opLineExec.getQty()))){
                    Long execid = opLineExec.getId();
                    execLineIds.add(execid);
                    break;
                }
            }
        }
        //校验容器/出库箱库存与删除的拣货库位库存时否一致
        List<WhOperationExecLine> list  = whOperationExecLineDao.checkContainerInventory(invSkuIds, ouId, execLineIds);
        if(null != list && list.size() !=0){
            throw new BusinessException(ErrorCodes.CHECK_CONTAINER_INVENTORY_IS_ERROR);
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
    private Long addLocInventory(WhSkuInventoryCommand skuInvCmd,Double qty,Boolean isTabbInvTotal,Long ouId,Long userId,Boolean isShortPicking) {
        log.info("WhSkuInventoryManagerImpl addLocInventory is start");
        Long invSkuId = null;
        List<WhSkuInventorySnCommand> snList = skuInvCmd.getWhSkuInventorySnCommandList();
        if(null == snList || snList.size() == 0) {
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(skuInvCmd.getLocationId());
            if(isShortPicking){
                inv.setOccupationCode(null);
            }
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
            invSkuId = inv.getId();
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.PICKING);
        }else{
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(skuInvCmd.getLocationId());
            if(isShortPicking){
                inv.setOccupationCode(null);
            }
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
            invSkuId = inv.getId();
            insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.PICKING);
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
        return invSkuId;
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
    private Long addContianerInventory(Integer pickingWay,WhSkuInventoryCommand skuInvCmd,Double qty,Boolean isTabbInvTotal,Long ouId,Long userId,String outBoundBoxCode,Long outerContainerId,Integer containerLatticeNo,Long turnoverBoxId,Long insideContainerId) {
        log.info("WhSkuInventoryManagerImpl addContianerInventory is start");
        Long invSkuId = null;
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
            if(Constants.PICKING_WAY_ONE == pickingWay){
                inv.setOuterContainerId(outerContainerId);     //小车 
                inv.setInsideContainerId(null);
                inv.setContainerLatticeNo(containerLatticeNo);
            }
            if(Constants.PICKING_WAY_TWO == pickingWay){
                inv.setOuterContainerId(outerContainerId);  //小车加出库箱模式
                inv.setContainerLatticeNo(containerLatticeNo);
                inv.setInsideContainerId(null);
                inv.setOutboundboxCode(outBoundBoxCode);
            }
            if(Constants.PICKING_WAY_THREE == pickingWay) {  //出库箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(null);
                inv.setOutboundboxCode(outBoundBoxCode);
            }
            if(Constants.PICKING_WAY_FOUR == pickingWay){  //周转箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(turnoverBoxId);
            }
            if(Constants.PICKING_WAY_FIVE == pickingWay) { //整托
                inv.setOuterContainerId(outerContainerId);
                inv.setInsideContainerId(insideContainerId);
            }
            if(Constants.PICKING_WAY_SIX == pickingWay) { //整箱
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
            invSkuId = inv.getId();
            insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.PICKING);
        }else{
            String uuid = "";
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(skuInvCmd, inv);
            inv.setLocationId(null);
            inv.setOccupationCode(null);
            inv.setInboundTime(new Date()); 
            inv.setOnHandQty(Double.valueOf(qty));
            inv.setId(null);
            if(Constants.PICKING_WAY_ONE == pickingWay){
                inv.setOuterContainerId(outerContainerId);     //小车 
                inv.setInsideContainerId(null);
            }
            if(Constants.PICKING_WAY_TWO == pickingWay){
                inv.setOuterContainerId(outerContainerId);  //小车加出库箱模式
                inv.setContainerLatticeNo(containerLatticeNo);
                inv.setOutboundboxCode(outBoundBoxCode);
                inv.setInsideContainerId(null);
            }
            if(Constants.PICKING_WAY_THREE == pickingWay) {  //出库箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(null);
                inv.setOutboundboxCode(outBoundBoxCode);
            }
            if(Constants.PICKING_WAY_FOUR == pickingWay){  //周转箱模式
                inv.setOuterContainerId(null);
                inv.setInsideContainerId(turnoverBoxId);
            }
            if(Constants.PICKING_WAY_FIVE == pickingWay) { //整托
                inv.setOuterContainerId(outerContainerId);
                inv.setInsideContainerId(insideContainerId);
            }
            if(Constants.PICKING_WAY_SIX == pickingWay) { //整箱
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
            invSkuId = inv.getId();
            insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
            insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.PICKING);
            if (!uuid.equals(skuInvCmd.getUuid())) {
                // uuid发生变更,重新插入sn
                long count = 0;
                for (WhSkuInventorySnCommand cSnCmd : snList) {
                    if(cSnCmd.getUuid().equals(skuInvCmd.getUuid())){
                        count++;
                        WhSkuInventorySn sn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(cSnCmd, sn);
                        sn.setUuid(inv.getUuid());
                        whSkuInventorySnDao.saveOrUpdate(sn); // 更新sn
                        insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                        if(count >= qty) {
                            break;
                        }
                    }
                }
                insertSkuInventorySnLog(inv.getUuid(), ouId); // 记录sn日志
            }
        }
        log.info("WhSkuInventoryManagerImpl addContianerInventory is end");
        return invSkuId;
    }
    
    /**
     * 建议上架推荐库位失败的情况下走人工分支,库位绑定（分配容器库存及生成待移入库位库存）
     * @author tangming
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    public void manMadeBinding(Long outerContainerId,Long insideContainerId, Warehouse warehouse, Long locationId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId,Double scanSkuQty){
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findContainerOnHandInventoryByInsideContainerId(ouId,insideContainerId);
            if(null == invList || invList.size() == 0) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_INV_NULL);
            }
            // 拆箱上架
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                       WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                       BeanUtils.copyProperties(invCmd, inv);
                       inv.setId(null);
                       inv.setQty(scanSkuQty);// 待移入
                       inv.setLocationId(locationId);
                       try {
                           inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                       } catch (Exception e) {
                           log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                           throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                       }
                       inv.setLastModifyTime(new Date());
                       whSkuInventoryTobefilledDao.insert(inv);
                       insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                    Double locSkuQty = 0.0;
                    if(null != invCmd.getOnHandQty()) {// 待移入
                        locSkuQty = invCmd.getOnHandQty()-scanSkuQty;
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
                        insertSkuInventoryLog(invCmd.getId(), -scanSkuQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                        if(locSkuQty.equals(0.0)) {//删除
                            WhSkuInventory invDelete = new WhSkuInventory();
                            BeanUtils.copyProperties(invCmd, invDelete);
                            whSkuInventoryDao.delete(invDelete.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                        }else{ //修改
                            WhSkuInventory invDelete = new WhSkuInventory();
                            BeanUtils.copyProperties(invCmd, invDelete);
                            invDelete.setOnHandQty(locSkuQty);
                            whSkuInventoryDao.saveOrUpdateByVersion(invDelete);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, invDelete, ouId, userId, null, null);
                        }
                    }else{
                        locSkuQty = invCmd.getToBeFilledQty()-scanSkuQty;
                        if(locSkuQty.equals(0.0)) {//删除
                            WhSkuInventoryTobefilled invTobefilled = new WhSkuInventoryTobefilled();
                            BeanUtils.copyProperties(invCmd, invTobefilled);
                            whSkuInventoryTobefilledDao.delete(invTobefilled.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, invTobefilled, ouId, userId, null, null);
                        }else{ //修改
                            WhSkuInventoryTobefilled invTobefilled = new WhSkuInventoryTobefilled();
                            BeanUtils.copyProperties(invCmd, invTobefilled);
                            invTobefilled.setQty(locSkuQty);
                            whSkuInventoryTobefilledDao.saveOrUpdateByVersion(invTobefilled);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, invTobefilled, ouId, userId, null, null);
                        }
                    }
                } else {   //存在sn/残次条码
                        String uuid = "";
                        WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                        BeanUtils.copyProperties(invCmd, inv);
                        inv.setId(null);
                        inv.setQty(scanSkuQty);// 待移入
                        inv.setLocationId(locationId);
                        try {
                            uuid = SkuInventoryUuid.invUuid(inv);
                            inv.setUuid(uuid);// UUID
                        } catch (Exception e) {
                            log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                        }
                        inv.setLastModifyTime(new Date());
                        whSkuInventoryTobefilledDao.insert(inv);
                        insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                        Double locSkuQty = 0.0;
                       if(null != invCmd.getOnHandQty()) {
                           locSkuQty = invCmd.getOnHandQty()-scanSkuQty;
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
                           insertSkuInventoryLog(invCmd.getId(), -scanSkuQty, oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                           if(locSkuQty.equals(0.0)) {//删除
                               WhSkuInventory invDelete = new WhSkuInventory();
                               BeanUtils.copyProperties(invCmd, invDelete);
                               whSkuInventoryDao.delete(invDelete.getId());
                               insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                           }else{ //修改
                               WhSkuInventory invUpdate = new WhSkuInventory();
                               BeanUtils.copyProperties(invCmd, invUpdate);
                               invUpdate.setOnHandQty(locSkuQty);
                               whSkuInventoryDao.saveOrUpdateByVersion(invUpdate);
                               insertGlobalLog(GLOBAL_LOG_UPDATE, invUpdate, ouId, userId, null, null);
                           }
                       }else{
                           locSkuQty = invCmd.getToBeFilledQty()-scanSkuQty;
                           if(locSkuQty.equals(0.0)) {//删除
                               WhSkuInventoryTobefilled invTobefilled = new WhSkuInventoryTobefilled();
                               BeanUtils.copyProperties(invCmd, invTobefilled);
                               whSkuInventoryTobefilledDao.delete(invTobefilled.getId());
                               insertGlobalLog(GLOBAL_LOG_DELETE, invTobefilled, ouId, userId, null, null);
                           }else{ //修改
                               WhSkuInventoryTobefilled invTobefilled = new WhSkuInventoryTobefilled();
                               BeanUtils.copyProperties(invCmd, invTobefilled);
                               invTobefilled.setQty(locSkuQty);
                               whSkuInventoryTobefilledDao.saveOrUpdateByVersion(invTobefilled);
                               insertGlobalLog(GLOBAL_LOG_UPDATE, invTobefilled, ouId, userId, null, null);
                           }
                       }
                   
                     if (!uuid.equals(invCmd.getUuid())) {
                         // uuid发生变更,重新插入sn
//                         for(WhSkuInventorySnCommand snCmd:snList) { //已经扫描过的sn
//                             if(invCmd.getUuid().equals(snCmd.getUuid())) {
//                                 WhSkuInventorySn sn = new WhSkuInventorySn();
//                                 BeanUtils.copyProperties(snCmd, sn);
//                                 sn.setUuid(uuid);
//                                 whSkuInventorySnDao.saveOrUpdate(sn);
//                                 insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
//                                 break;
//                             }
//                         }
                         // 插入sn
                         Double count = 1.0;
                         for (WhSkuInventorySnCommand snCmd : snList) {
                             if(invCmd.getUuid().equals(snCmd.getUuid())) {
                                 WhSkuInventorySn sn = new WhSkuInventorySn();
                                 BeanUtils.copyProperties(snCmd, sn);
                                 sn.setId(null);
                                 sn.setUuid(inv.getUuid());
                                 whSkuInventorySnDao.insert(sn);
                                 insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                                 // 记录SN日志
//                                 insertSkuInventorySnLog(sn.getId(), ouId);
                                 if(scanSkuQty.equals(count)) {
                                     break;
                                 }
                                 count++;
                             }
                         }
                         //删除之前的sn/残次条码
                         for (WhSkuInventorySnCommand cSnCmd : snList) {
                             WhSkuInventorySn sn = new WhSkuInventorySn();
                             BeanUtils.copyProperties(cSnCmd, sn);
                             whSkuInventorySnDao.delete(sn.getId());
                             insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                         }
                      }
                     
                     
                   }
                }
        } else {
            // 整托上架、整箱上架
            List<WhSkuInventoryCommand> invList = null;
            if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType){
                invList = whSkuInventoryDao.findContainerOnHandInventoryByOuterContainerId(ouId, outerContainerId);
            }
            if(WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType){
                invList = whSkuInventoryDao.findContainerOnHandInventoryByInsideContainerId(ouId,insideContainerId);
            }
            if(null == invList || invList.size() == 0) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_INV_NULL);
            }
            List<WhSkuInventoryTobefilled> tobefilledList = whSkuInventoryTobefilledDao.findWhSkuInventoryTobefilled(outerContainerId, insideContainerId, ouId);
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                //插入待移入库位库存之前先查询待移入库存表中是否已经绑定库位
                if (null == snList || 0 == snList.size()) {
                    //只修改待移入库存记录的绑定库位即可
                    Boolean result = false;
                    for(WhSkuInventoryTobefilled tobefilled:tobefilledList) {
                        String tobeSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByWhSkuInvTobefilled(tobefilled);
                        if(tobefilled.getInsideContainerId().equals(invCmd.getInsideContainerId()) && skuAttrIds.equals(tobeSkuAttrIds)) {
                            tobefilled.setLocationId(locationId);  //只修改库位即可
                            whSkuInventoryTobefilledDao.saveOrUpdateByVersion(tobefilled);
                            result = true;
                        }
                    }
                    if(!result) {
                        // 插入待移入库位库存
                        WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                        BeanUtils.copyProperties(invCmd, inv);
                        inv.setId(null);
                        if(null == invCmd.getOnHandQty()) {
                            inv.setQty(invCmd.getToBeFilledQty());// 待移入
                        }else{
                            inv.setQty(invCmd.getOnHandQty());// 待移入
                        }
                        inv.setLocationId(locationId);
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
                        Double qty = 0.0;
                        if(null == invCmd.getOnHandQty()) {   //待移入
                            qty = invCmd.getToBeFilledQty();
                        }else{
                            qty = invCmd.getOnHandQty();
                            insertSkuInventoryLog(invCmd.getId(), -qty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                        }
                        WhSkuInventory invDelete = new WhSkuInventory();
                        BeanUtils.copyProperties(invCmd, invDelete);
                        whSkuInventoryDao.delete(invDelete.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                    }
                } else {
                        //只修改待移入库存记录的绑定库位即可
                        Boolean result = false;
                        for(WhSkuInventoryTobefilled tobefilled:tobefilledList) {
                            String tobeSkuAttrIds = SkuCategoryProvider.getSkuAttrIdByWhSkuInvTobefilled(tobefilled);
                            if(tobefilled.getInsideContainerId().equals(invCmd.getInsideContainerId()) && skuAttrIds.equals(tobeSkuAttrIds)) {
                                tobefilled.setLocationId(locationId);  //只修改库位即可
                                whSkuInventoryTobefilledDao.saveOrUpdateByVersion(tobefilled);
                                result = true;
                            }
                        }
                        if(!result) {  //新插入待移入库存
                         // 插入待移入库位库存
                            WhSkuInventoryTobefilled inv = new WhSkuInventoryTobefilled();
                            BeanUtils.copyProperties(invCmd, inv);
                            inv.setId(null);
                            if(null == invCmd.getOnHandQty()) {
                                inv.setQty(invCmd.getToBeFilledQty());// 待移入
                            }else{
                                inv.setQty(invCmd.getOnHandQty());// 待移入
                            }
                            inv.setLocationId(locationId);
                            try {
                                inv.setUuid(SkuInventoryUuid.invUuid(inv));// UUID
                            } catch (Exception e) {
                                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                            }
                            inv.setLastModifyTime(new Date());
                            whSkuInventoryTobefilledDao.insert(inv);
                            insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
//                            for (WhSkuInventorySnCommand snCmd : snList) {
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
//                            }
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
                            Double qty = 0.0;
                            if(null == invCmd.getOnHandQty()) {
                                qty = invCmd.getToBeFilledQty();
                            }else{
                                qty = invCmd.getOnHandQty();
                                insertSkuInventoryLog(invCmd.getId(), -qty, oldSkuInvOnHandQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                            }
                            WhSkuInventory invDelete = new WhSkuInventory();
                            BeanUtils.copyProperties(invCmd, invDelete);
                            whSkuInventoryDao.delete(invDelete.getId());
                            insertGlobalLog(GLOBAL_LOG_DELETE, invDelete, ouId, userId, null, null);
                            
                            //先添加后删除sn
                            for (WhSkuInventorySnCommand snCmd : snList) {
                                if(invCmd.getUuid().equals(snCmd.getUuid())) {
                                    WhSkuInventorySn sn = new WhSkuInventorySn();
                                    BeanUtils.copyProperties(snCmd, sn);
                                    sn.setId(null);
                                    sn.setUuid(inv.getUuid());
                                    whSkuInventorySnDao.insert(sn);
                                    insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                                }
                            }
                            
                            for (WhSkuInventorySnCommand cSnCmd : snList) {
                                // insertSkuInventorySnLog(cSnCmd.getUuid(), ouId);
                                WhSkuInventorySn sn = new WhSkuInventorySn();
                                BeanUtils.copyProperties(cSnCmd, sn);
                                whSkuInventorySnDao.delete(sn.getId());
                                insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
                            }
                        }
                   
                }
            }
        }
    }
    
    
    /****
     * 建议上架走人工流程,上架
     * @param outerContainerCmd
     * @param insideConatinerCmd
     * @param locationId
     * @param putawayPatternDetailType
     * @param ouId
     * @param skuAttrId
     */
    public void execPutaway(Double skuScanQty,Warehouse warehouse,Long userId,ContainerCommand outerContainerCmd,ContainerCommand insideContainerCmd,String locationCode,Integer putawayPatternDetailType,Long ouId,String skuAttrId){
        Long containerId = null;
        String containerCode = null;
        Long insideContainerId = null;
        String insideContainerCode = null;
        if (null != outerContainerCmd) {
            containerId = outerContainerCmd.getId();
            containerCode = outerContainerCmd.getCode();
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
                if(null == loc) {
                    loc = locationDao.getLocationByBarcode(locationCode, ouId);
                }
                if(null == loc) {
                    log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                }
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
                String whSkuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                if(!skuAttrId.equals(whSkuAttrId)) {
                    continue;
                }
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setOnHandQty(skuScanQty);// 在库库存
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                    // 修改待移入库存
                    Double tobefilledQty = invCmd.getToBeFilledQty()-skuScanQty;   //待移入库存还剩下的sku数量
                    if(tobefilledQty.equals(0.0)) {
                        WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                        BeanUtils.copyProperties(invCmd, cInv);
                        whSkuInventoryTobefilledDao.delete(cInv.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                    }else{
                        WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                        BeanUtils.copyProperties(invCmd, cInv);
                        cInv.setQty(tobefilledQty);
                        whSkuInventoryTobefilledDao.saveOrUpdateByVersion(cInv);
                        insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                    }
                } else {   //有sn的情况
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setId(null);
                    inv.setOnHandQty(skuScanQty);// 在库库存
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
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId,InvTransactionType.SHELF);
                    // 插入sn
                    Double count = 1.0;
                    for (WhSkuInventorySnCommand snCmd : snList) {
                        if(snCmd.getUuid().equals(invCmd.getUuid())) {
                            WhSkuInventorySn sn = new WhSkuInventorySn();
                            BeanUtils.copyProperties(snCmd, sn);
                            sn.setUuid(inv.getUuid());
                            whSkuInventorySnDao.saveOrUpdate(sn);
                            insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                            // 记录SN日志
                            insertSkuInventorySnLog(sn.getId(), ouId);
                            if(skuScanQty.equals(count)) {
                                break;
                            }
                            count++;
                        }
                    }
//                    Double num = 1.0;
//                    //删除之前的sn/残次条码
//                    for (WhSkuInventorySnCommand cSnCmd : snList) {
//                        WhSkuInventorySn sn = new WhSkuInventorySn();
//                        BeanUtils.copyProperties(cSnCmd, sn);
//                        whSkuInventorySnDao.delete(sn.getId());
//                        insertGlobalLog(GLOBAL_LOG_DELETE, sn, ouId, userId, null, null);
//                        if(skuScanQty.equals(num)) {
//                            break;
//                        }
//                        num++;
//                    }
                    
                    // 修改待移入库存
                    Double tobefilledQty = invCmd.getToBeFilledQty()-skuScanQty;   //待移入库存还剩下的sku数量
                    if(tobefilledQty.equals(0.0)) {
                        WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                        BeanUtils.copyProperties(invCmd, cInv);
                        whSkuInventoryTobefilledDao.delete(cInv.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                    }else{
                        WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                        BeanUtils.copyProperties(invCmd, cInv);
                        cInv.setQty(tobefilledQty);
                        whSkuInventoryTobefilledDao.saveOrUpdateByVersion(cInv);
                        insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                    }
                }
            }
            // 4.如果不跟踪容器号，则上架后需判断是否释放容器
            if (false == isTV) {
                // 判断修改内部容器状态
                int allCounts = whSkuInventoryDao.findAllInventoryCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                int count1 = whSkuInventoryDao.findToBefilledCountsByInsideContainerId(ouId, insideContainerCmd.getId());
                if (0 == allCounts && 0 == count1) {
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
                if (null != outerContainerCmd) {
                    csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outerContainerCmd.getId().toString());
                    if (null == csrCmd) {
                        csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(outerContainerCmd, ouId, logId);
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
//                        boolean isUpdateOuterStatusCanPutaway = false;
                        for (Long icId : cacheInsideContainerIds) {
                            int toBeFilledCounts = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, icId);
                            int rcvdCounts = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId, icId);
                           if(0 < rcvdCounts || 0 < toBeFilledCounts){  //待移入库存或者容器库存没有全部上架
                                isUpdateOuterStatusUsable = false;
//                                isUpdateOuterStatusCanPutaway = true;
                                break;
                            }
//                           else if (0 < rcvdCounts && 0 < toBeFilledCounts) {
//                                isUpdateOuterStatusUsable = false;
//                                break;
//                            }
                        }
                        Integer containerStatus = outerContainerCmd.getStatus();
                        if (true == isUpdateOuterStatusUsable) {
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
                                Container container = new Container();
                                BeanUtils.copyProperties(outerContainerCmd, container);
                                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                                container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                                containerDao.saveOrUpdateByVersion(container);
                                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                            }
                        }
//                        else {
//                            if (true == isUpdateOuterStatusCanPutaway) {
//                                if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
//                                    Container container = new Container();
//                                    BeanUtils.copyProperties(outerContainerCmd, container);
//                                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
//                                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
//                                    containerDao.saveOrUpdateByVersion(container);
//                                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//                                }
//                            }
//                        }
                    }
                }
            }
        } else {
            // 整托上架、整箱上架(整托,整箱可用强制上架的流程)
        }
    }

    /***
     * 补货上架
     * @param operationId
     * @param ouId
     * @param isTabbInvTotal
     * @param userId
     * @param workCode
     */
    @Override
    public void replenishmentPutaway(Long operationId, Long ouId, Boolean isTabbInvTotal, Long userId,String workCode) {
        // TODO Auto-generated method stub
        //判断是否是怕波次内补货
        WhWorkCommand workCmd = whWorkDao.findWorkByWorkCode(workCode, ouId);
        if(null == workCmd) {
            throw new BusinessException(ErrorCodes.WORK_NO_EXIST);
        }
        Boolean isWaveReplenishment = false;// 默认非波次补货
        if(null != workCmd.getWaveId()) {
            isWaveReplenishment = true;
        }
        List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId,null,null);
        if(null== operationExecLineList || operationExecLineList.size()==0) {
            throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
        }
        String replenishmentCode = operationExecLineList.get(0).getReplenishmentCode();
        Long locId = operationExecLineList.get(0).getFromLocationId();
        Long skuId = operationExecLineList.get(0).getSkuId();
        Long outerContainerId = operationExecLineList.get(0).getToOuterContainerId();
        Long insideContainerId = operationExecLineList.get(0).getToInsideContainerId();
        List<WhSkuInventoryCommand>  invList = null;
        if(isWaveReplenishment) {  //波次内补货
            // 1.获取所有待移入库存
            invList = whSkuInventoryDao.getWhSkuInventoryTobefilledByWave(ouId, operationId, replenishmentCode);
        }else{
            invList = whSkuInventoryDao.getWhSkuInventoryCommandByNoWave(skuId, replenishmentCode, ouId, locId, outerContainerId, insideContainerId);  //非波次内补货
        }
        if (null == invList || 0 == invList.size()) {
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {});
        }
        for(WhOperationExecLine operExecLine:operationExecLineList){
               Double qty = Double.valueOf(operExecLine.getQty());   //补货数量
               if(operExecLine.getIsShortPicking()) {  //短拣
                   continue;
               }else{//非短拣
                   boolean isTV = true;// 是否跟踪容器
                   boolean isBM = true;// 是否批次管理
                   boolean isVM = true;// 是否管理效期
                   Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
                   Location loc = locationDao.findByIdExt(locId, ouId);
                   if (null == loc) {
                           log.error("location is null error, id is:[{}], logId is:[{}]", locId, logId);
                           throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
                   }
                   isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
                   isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
                   isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
                   // 2.执行上架(一入一出)
                   for (WhSkuInventoryCommand invCmd : invList) {
                       List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                       Double putWayQty = invCmd.getQty();   //待移入数量(一条记录)
                       String uuid = "";
                       if (null == snList || 0 == snList.size()) {
                           WhSkuInventory inv = new WhSkuInventory();
                           BeanUtils.copyProperties(invCmd, inv);
                           inv.setId(null);
                           if(qty <= putWayQty) {
                               inv.setOnHandQty(qty);// 在库库存
                           }
                           if(qty > putWayQty) {
                               inv.setOnHandQty(invCmd.getToBeFilledQty());// 在库库存
                           }
                           inv.setFrozenQty(0.0);
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
                           Double oldQty = 0.0;
                           if (true == isTabbInvTotal) {
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
                           insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                           if(qty <= putWayQty) {
                               // 删除待移入库存
                               WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                               BeanUtils.copyProperties(invCmd, cInv);
                               whSkuInventoryTobefilledDao.delete(cInv.getId());
                               insertGlobalLog(GLOBAL_LOG_DELETE, cInv, ouId, userId, null, null);
                               continue;
                           }
                           if(qty > putWayQty) {
                               // 删除待移入库存
                               WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                               BeanUtils.copyProperties(invCmd, cInv);
                               cInv.setQty(qty-putWayQty);
                               whSkuInventoryTobefilledDao.saveOrUpdateByVersion(cInv);
                               insertGlobalLog(GLOBAL_LOG_UPDATE, cInv, ouId, userId, null, null);
                               continue;
                           }
                       } else {
                           WhSkuInventory inv = new WhSkuInventory();
                           BeanUtils.copyProperties(invCmd, inv);
                           inv.setId(null);
                           if(qty <= putWayQty) {
                               inv.setOnHandQty(qty);// 在库库存
                           }
                           if(qty > putWayQty) {
                               inv.setOnHandQty(invCmd.getToBeFilledQty());// 在库库存
                           }
                           inv.setFrozenQty(0.0);
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
                           Double oldQty = 0.0;
                           if (true == isTabbInvTotal) {
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
                           insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
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
                          
                           if(qty > putWayQty) {
                               // 删除待移入库存
                               WhSkuInventoryTobefilled cInv = new WhSkuInventoryTobefilled();
                               BeanUtils.copyProperties(invCmd, cInv);
                               cInv.setQty(qty-putWayQty);
                               whSkuInventoryTobefilledDao.saveOrUpdateByVersion(cInv);
                               insertGlobalLog(GLOBAL_LOG_UPDATE, cInv, ouId, userId, null, null);
                               continue;
                           }
                           if(qty <= putWayQty) {
                               // 删除待移入库存
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
                               continue;
                           }
                       }
                   }
                   if(isTV) {
                     //如果库位跟踪容器号,修改容器状态
                       if(null != outerContainerId) {  //修改托盘
                           Container container =  containerDao.findByIdExt(outerContainerId, ouId);
                           if(null == container) {
                               throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS );
                           }
                           container.setLifecycle(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                           container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                           containerDao.saveOrUpdateByVersion(container);
                           insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                       }
                       if(null != insideContainerId) {
                           Container container =  containerDao.findByIdExt(insideContainerId, ouId);
                           if(null == container) {
                               throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS );
                           }
                           container.setLifecycle(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                           container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                           containerDao.saveOrUpdateByVersion(container);
                           insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                       }
                       //
                   }
                   List<WhSkuInventoryAllocatedCommand> allocatedList = whSkuInventoryAllocatedDao.getWhSkuInventoryCommandByOccupationLineId(ouId, operationId);
                   //删除库位库存表中的容器库存
                   for(WhSkuInventoryAllocatedCommand allocatedCmd:allocatedList) {
                       Long outerId = allocatedCmd.getOuterContainerId();
                       Long insideId = allocatedCmd.getInsideContainerId();
                       Long sId = allocatedCmd.getSkuId();
                       String uuid = allocatedCmd.getUuid();
                       List<WhSkuInventoryCommand> list = whSkuInventoryDao.findWhSkuInventoryCmdByuuid(outerId, insideId, sId, uuid,ouId);
                       for(WhSkuInventoryCommand skuCmd:list){
                           WhSkuInventory whSkuInventory = new WhSkuInventory();
                           BeanUtils.copyProperties(skuCmd, whSkuInventory);
                           whSkuInventoryDao.deleteWhSkuInventoryById(whSkuInventory.getId(), ouId);
                           insertGlobalLog(GLOBAL_LOG_DELETE, whSkuInventory, ouId, userId, null, null);
                       }
                   }
                   //删除已分配库存表中的容器库存
                   for(WhSkuInventoryAllocatedCommand allocatedCmd:allocatedList) {
                       WhSkuInventoryAllocated allocated = new WhSkuInventoryAllocated();
                       BeanUtils.copyProperties(allocatedCmd, allocated);
                       whSkuInventoryAllocatedDao.deleteExt(allocated.getId(), ouId);
                       insertGlobalLog(GLOBAL_LOG_DELETE, allocated, ouId, userId, null, null);
                   }
               }
        }
        
        //校验库位库存
        List<WhSkuInventoryCommand> list =  whSkuInventoryDao.checkReplenishmentInventory(ouId, operationId);
        for(WhSkuInventoryCommand skuInvCmd:list) {
            if(null != skuInvCmd.getLocationId() || null != skuInvCmd.getOuterContainerId() || null != skuInvCmd.getInsideContainerId() || null != skuInvCmd.getSkuId() || skuInvCmd.getQty() != null) {
                throw new BusinessException(ErrorCodes.CHECK_INVENTORY_IS_ERROR);
            }
        }
        //判断目标库位上是否有拣货工作
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        for(Long locationId:locationIds) {
            //更新到工作明细
            List<WhWorkLineCommand> workLineList = whWorkLineDao.findWorkLineByLocationId(locationId, ouId);
            for(WhWorkLineCommand workLineCmd:workLineList) {
                     Long odoLineId = workLineCmd.getOdoLineId();
                     Long odoId = workLineCmd.getOdoId();
                     String workSkuAttrId = SkuCategoryProvider.getSkuAttrIdByWhWorkLineCommand(workLineCmd);
                     List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findReplenishmentBylocationId(ouId, locationId, odoLineId, odoId);
                     for(WhSkuInventoryCommand invCmd:skuInvCmdList) {
                            Long outerId = invCmd.getOuterContainerId();
                            Long insideId = invCmd.getInsideContainerId();
                            String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                            if(workSkuAttrId.equals(skuAttrId)) {
                                  if(null != outerId && null != insideId) {
                                      WhWorkLine workLine = new WhWorkLine();
                                      BeanUtils.copyProperties(workLineCmd, workLine);
                                      workLine.setFromInsideContainerId(insideId);
                                      workLine.setFromOuterContainerId(outerId);
                                      whWorkLineDao.saveOrUpdateByVersion(workLine);
                                      insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                  }
                                  if(null == outerId && null != insideId){
                                      WhWorkLine workLine = new WhWorkLine();
                                      BeanUtils.copyProperties(workLineCmd, workLine);
                                      workLine.setFromInsideContainerId(insideId);
                                      whWorkLineDao.saveOrUpdateByVersion(workLine);
                                      insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                  }
                            }
                     }
            }
            //更新到作业明细
            List<WhOperationLineCommand> operLineCmdList = whOperationLineDao.findOperationLineByLocationId(ouId, locationId);
            for(WhOperationLineCommand operLineCmd:operLineCmdList){
                Long odoLineId = operLineCmd.getOdoLineId();
                Long odoId = operLineCmd.getOdoId();
                String workSkuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(operLineCmd);
                List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findReplenishmentBylocationId(ouId, locationId, odoLineId, odoId);
                for(WhSkuInventoryCommand invCmd:skuInvCmdList) {
                       Long outerId = invCmd.getOuterContainerId();
                       Long insideId = invCmd.getInsideContainerId();
                       String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                       if(workSkuAttrId.equals(skuAttrId)) {
                             if(null != outerId && null != insideId) {
                                 WhOperationLine opLine = new WhOperationLine();
                                 BeanUtils.copyProperties(operLineCmd, opLine);
                                 opLine.setFromOuterContainerId(outerId);
                                 opLine.setFromInsideContainerId(insideId);
                                 whOperationLineDao.saveOrUpdateByVersion(opLine);
                                 insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                             }
                             if(null == outerId && null != insideId){
                                 WhOperationLine opLine = new WhOperationLine();
                                 BeanUtils.copyProperties(operLineCmd, opLine);
                                 opLine.setFromInsideContainerId(insideId);
                                 whOperationLineDao.saveOrUpdateByVersion(opLine);
                                 insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                             }
                       }
                }
            }
        }
        
        
    }
    
    /***
     * 补货中的拣货由库位库存生成容器库存
     * @param operationId
     * @param ouId
     * @param outerContainerId
     * @param insideContainerId
     * @param turnoverBoxId
     */
    public void replenishmentContainerInventory(Long operationId,Long ouId,Long outerContainerId,Long insideContainerId,Long turnoverBoxId,Boolean isTabbInvTotal,Long userId,String workCode,Double scanSkuQty){
        List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId,outerContainerId,insideContainerId);
        if(null== operationExecLineList || operationExecLineList.size()==0) {
            throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
        }
        //到已分配库存表中查询
        List<WhSkuInventoryAllocatedCommand> skuInvCmdList = whSkuInventoryAllocatedDao.getWhSkuInventoryCommandByOccupationLineId(ouId, operationId);
        if(null == skuInvCmdList || skuInvCmdList.size() == 0){
                throw new BusinessException(ErrorCodes.ALLOCATE_INVENTORY_NO_EXIST);  //分配库存不存在
        }
        WhWorkCommand workCmd = whWorkDao.findWorkByWorkCode(workCode, ouId);
        if(null == workCmd) {
            throw new BusinessException(ErrorCodes.WORK_NO_EXIST);
        }
        for(WhSkuInventoryAllocatedCommand allocated:skuInvCmdList){
            String icAllocatedIds = (allocated.getOuterContainerId()== null?"┊":allocated.getOuterContainerId()+"┊") + (allocated.getInsideContainerId()==null?"︴":allocated.getInsideContainerId()+"︴");
            String allocatedSkuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(allocated);
            Long locationId = allocated.getLocationId();
            String suuid = allocated.getUuid();
            Long skuId = allocated.getSkuId();
            List<WhSkuInventoryCommand>  invList = whSkuInventoryDao.getWhSkuInventoryCmdByuuid(locationId, skuId, suuid,ouId);
                    for(WhSkuInventoryCommand skuInvCmd:invList) {
                      String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                      String icInvCmdIds = (skuInvCmd.getOuterContainerId() == null?"┊":skuInvCmd.getOuterContainerId()+"┊") + (skuInvCmd.getInsideContainerId()==null?"︴":skuInvCmd.getInsideContainerId()+"︴");
                        if(skuAttrId.equals(allocatedSkuAttrId) && icAllocatedIds.equals(icInvCmdIds) && locationId.equals(skuInvCmd.getLocationId())) {  //是同一条记录
                            if(null != skuInvCmd.getWhSkuInventorySnCommandList() && skuInvCmd.getWhSkuInventorySnCommandList().size() != 0) { //有sn/残次信息
                                String uuid = "";
                                WhSkuInventory skuInv = new WhSkuInventory();
                                BeanUtils.copyProperties(skuInvCmd, skuInv);
                                skuInv.setLocationId(null);
                                if(null != outerContainerId) { //整托
                                    skuInv.setOuterContainerId(outerContainerId);
                                    skuInv.setInsideContainerId(insideContainerId);
                                }
                                if(null == outerContainerId && null != insideContainerId){ // 整箱
                                    skuInv.setOuterContainerId(null);
                                    skuInv.setInsideContainerId(insideContainerId);
                                }
                                if(null == outerContainerId && null == insideContainerId && null != turnoverBoxId){
                                    skuInv.setOuterContainerId(null);
                                    skuInv.setInsideContainerId(turnoverBoxId);
                                }
                                try {
                                    uuid = SkuInventoryUuid.invUuid(skuInv);
                                    skuInv.setUuid(uuid);// UUID
                                } catch (Exception e) {
                                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                                }
                                Double oldQty = 0.0;
                                if (true == isTabbInvTotal) {
                                    try {
                                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                                    } catch (Exception e) {
                                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                    }
                                } else {
                                    oldQty = 0.0;
                                }
                                skuInv.setLastModifyTime(new Date());
                                skuInv.setOuId(ouId);
                                skuInv.setOnHandQty(Double.valueOf(scanSkuQty));  //
                                whSkuInventoryDao.insert(skuInv);
                                insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
                                // 记录入库库存日志
                                insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                                //修改已分配库存的数量 
                                Double result = allocated.getQty()- Double.valueOf(scanSkuQty);
                                if(result == 0){
                                    whSkuInventoryAllocatedDao.deleteExt(allocated.getAlloctedId(), ouId);
                                }else{
                                    WhSkuInventoryAllocated allocate = new WhSkuInventoryAllocated();
                                    BeanUtils.copyProperties(allocated, allocate);
                                    allocate.setQty(result);
                                    allocate.setId(allocated.getAlloctedId());
                                    whSkuInventoryAllocatedDao.saveOrUpdateByVersion(allocate);
                                }
                                //修改库位库存表中的在库库存
                                String uuid1 = "";
                                if(skuInvCmd.getOnHandQty() > allocated.getQty()) {
                                    WhSkuInventory skuInventory = new WhSkuInventory();
                                    BeanUtils.copyProperties(skuInvCmd, skuInventory);
                                    skuInventory.setOnHandQty(skuInvCmd.getOnHandQty()-skuInv.getOnHandQty());
                                    try {
                                        uuid1 = SkuInventoryUuid.invUuid(skuInv);
                                        skuInv.setUuid(uuid1);// UUID
                                    } catch (Exception e) {
                                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                                    }
                                    Double oldQty1 = 0.0;
                                    if (true == isTabbInvTotal) {
                                        try {
                                            oldQty1 = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid1, ouId);
                                        } catch (Exception e) {
                                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                        }
                                    } else {
                                        oldQty1 = 0.0;
                                    }
                                    skuInventory.setLastModifyTime(new Date());
                                    whSkuInventoryDao.insert(skuInventory);
                                    insertGlobalLog(GLOBAL_LOG_INSERT, skuInventory, ouId, userId, null, null);
                                    // 记录入库库存日志
                                    insertSkuInventoryLog(skuInventory.getId(), -skuInv.getOnHandQty(), oldQty1, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                                }
                                //删除原来的库位库存
                                WhSkuInventory  deleSkuInv = new WhSkuInventory();
                                BeanUtils.copyProperties(skuInvCmd, deleSkuInv);
                                whSkuInventoryDao.deleteWhSkuInventoryById(deleSkuInv.getId(), ouId);
                                insertGlobalLog(GLOBAL_LOG_DELETE, deleSkuInv, ouId, userId, null, null);
                                //修改sn/残次信息
                                List<WhSkuInventorySnCommand> skuInvSnCmdList = skuInvCmd.getWhSkuInventorySnCommandList();
                                for(WhSkuInventorySnCommand skuInvSnCmd:skuInvSnCmdList){
                                    if(skuInvSnCmd.getUuid().equals(skuInvSnCmd.getUuid())){
                                        WhSkuInventorySn skuInvSn = new WhSkuInventorySn();
                                        BeanUtils.copyProperties(skuInvSnCmd, skuInvSn);
                                        skuInvSn.setUuid(uuid1);
                                        whSkuInventorySnDao.saveOrUpdate(skuInvSn);
                                        insertGlobalLog(GLOBAL_LOG_UPDATE, skuInvSn, ouId, userId, null, null);
                                        // 记录SN日志
                                        insertSkuInventorySnLog(skuInvSn.getId(), ouId);
                                    }
                                 
                                }
                            }else{//没有sn/残次信息 
                                String uuid = "";
                                WhSkuInventory skuInv = new WhSkuInventory();
                                BeanUtils.copyProperties(skuInvCmd, skuInv);
                                skuInv.setLocationId(null);
                                if(null != outerContainerId) { //整托
                                    skuInv.setOuterContainerId(outerContainerId);
                                    skuInv.setInsideContainerId(insideContainerId);
                                }
                                if(null == outerContainerId && null != insideContainerId){ // 整箱
                                    skuInv.setOuterContainerId(null);
                                    skuInv.setInsideContainerId(insideContainerId);
                                }
                                if(null == outerContainerId && null == insideContainerId && null != turnoverBoxId){
                                    skuInv.setOuterContainerId(null);
                                    skuInv.setInsideContainerId(turnoverBoxId);
                                }
                                try {
                                    uuid = SkuInventoryUuid.invUuid(skuInv);
                                    skuInv.setUuid(uuid);// UUID
                                } catch (Exception e) {
                                    log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                    throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                                }
                                Double oldQty = 0.0;
                                if (true == isTabbInvTotal) {
                                    try {
                                        oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                                    } catch (Exception e) {
                                        log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                    }
                                } else {
                                    oldQty = 0.0;
                                }
                                skuInv.setLastModifyTime(new Date());
                                skuInv.setOuId(ouId);
                                skuInv.setOnHandQty(Double.valueOf(scanSkuQty));   //如果库位库存表中的在库库存数量大于已分配的数量插入的容器库存时已分配数量,如果相等无论在库库存还是已分配都可以
                                whSkuInventoryDao.insert(skuInv);
                                insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
                                // 记录入库库存日志
                                insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                                //修改库存记录(主要是已分配的库存数量,没捡一个sku,减去一条记录)
                                //修改已分配库存的数量 
                                Double result = allocated.getQty()- Double.valueOf(scanSkuQty);
                                if(result == 0){
                                    whSkuInventoryAllocatedDao.deleteExt(allocated.getAlloctedId(), ouId);
                                }else{
                                    WhSkuInventoryAllocated allocate = new WhSkuInventoryAllocated();
                                    BeanUtils.copyProperties(allocated, allocate);
                                    allocate.setQty(result);
                                    allocate.setId(allocated.getAlloctedId());
                                    whSkuInventoryAllocatedDao.saveOrUpdateByVersion(allocate);
                                }
                                //修改库位库存表中的在库库存
                                String uuid1 = "";
                                if(skuInvCmd.getOnHandQty() > allocated.getQty()) {
                                    WhSkuInventory skuInventory = new WhSkuInventory();
                                    BeanUtils.copyProperties(skuInvCmd, skuInventory);
                                    skuInventory.setOnHandQty(skuInvCmd.getOnHandQty()-skuInv.getOnHandQty());
                                    try {
                                        uuid1 = SkuInventoryUuid.invUuid(skuInv);
                                        skuInv.setUuid(uuid1);// UUID
                                    } catch (Exception e) {
                                        log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                                        throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                                    }
                                    Double oldQty1 = 0.0;
                                    if (true == isTabbInvTotal) {
                                        try {
                                            oldQty1 = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid1, ouId);
                                        } catch (Exception e) {
                                            log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                                        }
                                    } else {
                                        oldQty1 = 0.0;
                                    }
                                    skuInventory.setLastModifyTime(new Date());
                                    whSkuInventoryDao.insert(skuInventory);
                                    insertGlobalLog(GLOBAL_LOG_INSERT, skuInventory, ouId, userId, null, null);
                                    // 记录入库库存日志
                                    insertSkuInventoryLog(skuInventory.getId(), -skuInv.getOnHandQty(), oldQty1, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                                }
                                //删除原来的库位库存
                                WhSkuInventory  deleSkuInv = new WhSkuInventory();
                                BeanUtils.copyProperties(skuInvCmd, deleSkuInv);
                                whSkuInventoryDao.deleteWhSkuInventoryById(deleSkuInv.getId(), ouId);
                                insertGlobalLog(GLOBAL_LOG_DELETE, deleSkuInv, ouId, userId, null, null);
                            }
                            break;
                        }
              }
        }
//        //校验容器/出库箱库存与删除的拣货库位库存时否一致
//        List<WhOperationExecLine> list  = whOperationExecLineDao.checkContainerInventory(invSkuIds, ouId, execLineIds);
//        if(null != list && list.size() !=0){
//            throw new BusinessException(ErrorCodes.CHECK_CONTAINER_INVENTORY_IS_ERROR);
//        }
    }
    
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhInboundConfirmCommand findInventoryByPo(WhPo po, List<WhPoLine> lineList, Long ouId) {
    	Collections.sort(lineList, new Comparator<WhPoLine>() {

			@Override
			public int compare(WhPoLine po1, WhPoLine po2) {
				if (po1.getSkuId() == null || po2.getSkuId() == null) {
					throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
				}
				return po1.getSkuId().compareTo(po2.getSkuId());
			}
    		
		});
    	
    	List<WhInboundLineConfirmCommand> confirmLineList = new ArrayList<WhInboundLineConfirmCommand>();
    	// 存放记录uuid是否找过对应sn信息
    	Map<String, Boolean> uuidMap = new HashMap<String, Boolean>();
    	
    	// 根据ASN_CODE查询库存表已收获的数据
    	List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findInventoryByPo(po.getId(), ouId);
    	for (WhPoLine poLine : lineList) {
    		Long skuId = poLine.getSkuId();
    		WhSkuCommand sku = skuDao.findWhSkuByIdExt(skuId, ouId);
    		WhInboundLineConfirmCommand confirmLine = new WhInboundLineConfirmCommand();
    		confirmLine.setUpc(sku.getExtCode());
    		confirmLine.setStyle(sku.getStyle());
    		confirmLine.setColor(sku.getColor());
    		confirmLine.setSize(sku.getSize());
    		confirmLine.setCartonNo(poLine.getCartonNo());
    		confirmLine.setLineSeq(poLine.getExtLineNum());
    		confirmLine.setQty(poLine.getQtyPlanned());
    		confirmLine.setSkuId(skuId);
    		confirmLine.setIsIqc(poLine.getIsIqc());
    		
    		List<WhInboundInvLineConfirmCommand> invLines = new ArrayList<WhInboundInvLineConfirmCommand>();
    		// 第一次实际收货数据计算
    		Double actualQty = Constants.DEFAULT_DOUBLE;
    		// 第一次把有库存属性的商品对应库存数据匹配
    		for (int i = 0; i < skuInvs.size(); i++) {
    			WhSkuInventoryCommand inv = skuInvs.get(i);
    			if (!hasInvAttr(poLine)) {
					break;
				}
    			if (checkInvAttrEqual(poLine, inv)) {
    				WhInboundInvLineConfirmCommand invLineConfirm = new WhInboundInvLineConfirmCommand();
    				BeanUtils.copyProperties(inv, invLineConfirm, "id");
    				invLineConfirm.setIsIqc(poLine.getIsIqc());
    				if (poLine.getQtyPlanned().compareTo(inv.getOnHandQty()) != -1) {
    					// poLine.getQtyPlanned() >= inv.getOnHandQty()
    					invLineConfirm.setQtyRcvd(inv.getOnHandQty());
    					actualQty = inv.getOnHandQty();
    					skuInvs.remove(i--);
					} else {
						invLineConfirm.setQtyRcvd(poLine.getQtyPlanned());
						actualQty = poLine.getQtyPlanned();
						inv.setOnHandQty(inv.getOnHandQty() - poLine.getQtyPlanned());
					}
    				// 查询sn信息, 有则封装
    				this.getSnInfo(uuidMap, inv, invLineConfirm, invLines, ouId);
    				break;
				}
    		}
    		confirmLine.setActualQty(actualQty);
    		confirmLine.setWhInBoundInvLineConfirmsList(invLines);
    		confirmLineList.add(confirmLine);
		}
    	// 第二次把库存数据填补上
    	for (int i = 0; i < confirmLineList.size(); i++) {
    		WhInboundLineConfirmCommand confirmLine = confirmLineList.get(i);
    		Long skuId = confirmLine.getSkuId();
    		String upc = confirmLine.getUpc();
    		Double qtyPlanned = confirmLine.getQty();
    		Double actualQty = confirmLine.getActualQty();
    		Double qty = qtyPlanned - actualQty;
    		
    		List<WhInboundInvLineConfirmCommand> whInBoundInvLineConfirmsList = confirmLine.getWhInBoundInvLineConfirmsList();
    		// 判断是否是最后一件
    		if (i + 1 >= confirmLineList.size() || !upc.equals(confirmLineList.get(i + 1).getUpc())) {
				// 是最后一件
    			for (int j = 0; j < skuInvs.size(); j++) {
    				WhSkuInventoryCommand inv = skuInvs.get(j);
    				if (skuId.equals(inv.getSkuId())) {
    					WhInboundInvLineConfirmCommand invLine = new WhInboundInvLineConfirmCommand();
    					BeanUtils.copyProperties(inv, invLine, "id");
    					invLine.setQtyRcvd(inv.getOnHandQty());
    					invLine.setIsIqc(confirmLine.getIsIqc());
    					whInBoundInvLineConfirmsList.add(invLine);
    					skuInvs.remove(j--);
    					this.getSnInfo(uuidMap, inv, invLine, whInBoundInvLineConfirmsList, ouId);
					}
				}
    			
			} else {
				if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
					continue;
				}
				for (int j = 0; j < skuInvs.size(); j++) {
					WhSkuInventoryCommand inv = skuInvs.get(j);
    				if (skuId.equals(inv.getSkuId())) {
    					WhInboundInvLineConfirmCommand invLine = new WhInboundInvLineConfirmCommand();
    					BeanUtils.copyProperties(inv, invLine, "id");
    					if (qty.compareTo(inv.getOnHandQty()) != -1) {
							// qty >= inv.getOnHandQty()
    						invLine.setQtyRcvd(inv.getOnHandQty());
    						invLine.setIsIqc(confirmLine.getIsIqc());
    						whInBoundInvLineConfirmsList.add(invLine);
    						qty -= inv.getOnHandQty();
    						skuInvs.remove(j--);
						} else {
							invLine.setQtyRcvd(qty);
    						invLine.setIsIqc(confirmLine.getIsIqc());
    						whInBoundInvLineConfirmsList.add(invLine);
    						inv.setOnHandQty(inv.getOnHandQty() - qty);
    						qty = Constants.DEFAULT_DOUBLE;
						}
    					if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
							break;
						}
    				}
				}
			}
		}
    	
    	WhInboundConfirmCommand inboundConfirm = new WhInboundConfirmCommand();
    	inboundConfirm.setUuid(UUID.randomUUID().toString());
    	inboundConfirm.setExtPoCode(po.getExtPoCode());
    	inboundConfirm.setExtCode(po.getExtCode());
    	Customer customer = customerManager.getCustomerById(po.getCustomerId());
    	if (null == customer) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
    	inboundConfirm.setCustomerCode(customer.getCustomerCode());
    	Store store = storeManager.getStoreById(po.getStoreId());
    	if (null == store) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
    	inboundConfirm.setStoreCode(store.getStoreCode());
    	inboundConfirm.setFromLocation(po.getFromLocation());
    	inboundConfirm.setToLocation(po.getToLocation());
    	inboundConfirm.setDeliveryTime(po.getDeliveryTime());
    	Warehouse wh = warehouseManager.findWarehouseByIdExt(ouId);
    	if (null == wh) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
    	inboundConfirm.setWhCode(wh.getCode());
    	inboundConfirm.setPoStatus(po.getStatus().toString());
    	inboundConfirm.setPoType(po.getPoType().toString());
    	inboundConfirm.setIsIqc(po.getIsIqc());
    	inboundConfirm.setQtyPlanned(po.getQtyPlanned());
    	inboundConfirm.setQtyRcvd(po.getQtyRcvd());
    	inboundConfirm.setCtnPlanned(po.getCtnPlanned());
    	inboundConfirm.setCtnRcvd(po.getCtnRcvd());
    	inboundConfirm.setDataSource(po.getDataSource());
    	inboundConfirm.setStatus(1);
    	inboundConfirm.setErrorCount(0);
    	inboundConfirm.setCreateTime(new Date());
    	inboundConfirm.setLastModifyTime(new Date());
    	inboundConfirm.setWhInboundLineConfirmList(confirmLineList);
    	return inboundConfirm;
    }

	private void getSnInfo(Map<String, Boolean> uuidMap, WhSkuInventoryCommand inv, WhInboundInvLineConfirmCommand invLineConfirm, List<WhInboundInvLineConfirmCommand> invLines, Long ouId) {
		if (null == uuidMap.get(inv.getUuid()) || !uuidMap.get(inv.getUuid())) {
			List<WhSkuInventorySnCommand> invSnList = whSkuInventorySnDao.findInvSnByAsnCodeAndUuid(inv.getOccupationCode(), inv.getUuid(), ouId);
			if (null != invSnList && !invSnList.isEmpty()) {
				List<WhInboundSnLineConfirmCommand> snLineList = new ArrayList<WhInboundSnLineConfirmCommand>();
				for (WhSkuInventorySnCommand sn : invSnList) {
					WhInboundSnLineConfirmCommand snLine = new WhInboundSnLineConfirmCommand();
					snLine.setSn(sn.getSn());
					snLine.setDefectWareBarcode(sn.getDefectWareBarcode());
					snLine.setDefectSource(sn.getDefectSource());
					if (Constants.SKU_SN_DEFECT_SOURCE_WH.equals(sn.getDefectSource())) {
						snLine.setDefectType(sn.getWhDefectTypeCode());
						snLine.setDefectReasons(sn.getWhDefectReasonsCode());
					} else {
						snLine.setDefectType(sn.getStoreDefectTypeCode());
						snLine.setDefectReasons(sn.getStoreDefectReasonsCode());
					}
					snLineList.add(snLine);
				}
				invLineConfirm.setWhInBoundSnLineConfirmCommandList(snLineList);
			}
			invLines.add(invLineConfirm);
			uuidMap.put(inv.getUuid(), Boolean.TRUE);
		}
	}
    
    private boolean hasInvAttr(WhPoLine poLine) {
    	if (StringUtils.isEmpty(poLine.getInvType()) && null == poLine.getInvStatus()
    			&& StringUtils.isEmpty(poLine.getInvAttr1()) && StringUtils.isEmpty(poLine.getInvAttr2())
    			&& StringUtils.isEmpty(poLine.getInvAttr3()) && StringUtils.isEmpty(poLine.getInvAttr4())
    			&& StringUtils.isEmpty(poLine.getInvAttr5()) && StringUtils.isEmpty(poLine.getBatchNo())
    			&& StringUtils.isEmpty(poLine.getCountryOfOrigin()) && null == poLine.getMfgDate() 
    			&& null == poLine.getExpDate()) {
			return false;
		}
    	return true;
    }
    
    private boolean checkInvAttrEqual(WhPoLine poLine, WhSkuInventoryCommand inv) {
    	if (poLine.getInvType() == null ? inv.getInvType() == null : poLine.getInvType().equals(inv.getInvType())
    			&& poLine.getInvStatus() == null ? inv.getInvStatus() == null : poLine.getInvStatus().equals(inv.getInvStatus())
    			&& poLine.getInvAttr1() == null ? inv.getInvAttr1() == null : poLine.getInvAttr1().equals(inv.getInvAttr1())
    			&& poLine.getInvAttr2() == null ? inv.getInvAttr2() == null : poLine.getInvAttr2().equals(inv.getInvAttr2())
    			&& poLine.getInvAttr3() == null ? inv.getInvAttr3() == null : poLine.getInvAttr3().equals(inv.getInvAttr3())
    			&& poLine.getInvAttr4() == null ? inv.getInvAttr4() == null : poLine.getInvAttr4().equals(inv.getInvAttr4())
    			&& poLine.getInvAttr5() == null ? inv.getInvAttr5() == null : poLine.getInvAttr5().equals(inv.getInvAttr5())
    			&& poLine.getBatchNo() == null ? inv.getBatchNumber() == null : poLine.getBatchNo().equals(inv.getBatchNumber())
    			&& poLine.getCountryOfOrigin() == null ? inv.getCountryOfOrigin() == null : poLine.getCountryOfOrigin().equals(inv.getCountryOfOrigin())
    			&& poLine.getMfgDate() == null ? inv.getMfgDate() == null : poLine.getMfgDate().equals(inv.getMfgDate())
    			&& poLine.getExpDate() == null ? inv.getExpDate() == null : poLine.getExpDate().equals(inv.getExpDate())) {
			return true;
		}
    	return false;
    }

    @Override
    public List<WhSkuInventory> findWhSkuInventoryByPramas(WhSkuInventory inventory) {
        return whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);
    }

    /**
     * 生成出库箱库存
     * 
     * @param whSkuInventoryCommand
     * @return
     */
    @Override
    public void saveOrUpdate(WhSkuInventoryCommand whSkuInventoryCommand) {
        WhSkuInventory whSkuInventory = new WhSkuInventory();
        //复制数据        
        BeanUtils.copyProperties(whSkuInventoryCommand, whSkuInventory);
        if(null != whSkuInventoryCommand.getId() ){
            whSkuInventoryDao.saveOrUpdate(whSkuInventory);
        }else{
            whSkuInventoryDao.insert(whSkuInventory);
        }
    }

    /**
     * 根据参数删除数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    @Override
    public void deleteSkuInventory(Long id, Long ouid) {
        int count = whSkuInventoryDao.deleteWhSkuInventoryById(id, ouid);
    }

    @Override
    public List<WhSkuInventory> findSkuInventoryByContainer(WhSkuInventory whSkuInventory) {
        return whSkuInventoryDao.findSkuInventoryByContainer(whSkuInventory);
    }
    
    /**
     * 根据参数查询库存信息
     * 
     * @param id
     * @param uuid
     * @param ouid
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSkuInventoryCommand findWhSkuInventoryByIdAndUuidAndOuid(Long id, String uuid, Long ouid) {
        WhSkuInventoryCommand whSkuInventoryCommand = whSkuInventoryDao.findWhSkuInventoryByIdAndUuidAndOuid(id, uuid, ouid);
        return whSkuInventoryCommand;
    }
    
    /**
     * 生成出库箱库存(按单复合)
     */
    public void addOutBoundInventory(WhCheckingByOdoResultCommand cmd,Boolean isTabbInvTotal,Long userId){
       Long ouId = cmd.getOuId();
       Long containerId = cmd.getContainerId(); // 小车id
       Integer containerLatticeNo = cmd.getContainerLatticeNo(); // 货格号
       String outboundbox = cmd.getOutboundbox(); // 出库箱号
       String seedingWallCode = cmd.getSeedingWallCode(); // 播种墙编码
       String turnoverBoxCode = cmd.getTurnoverBoxCode(); // 周转箱
       String outboundboxCode = cmd.getOutboundBoxCode();  //出库箱编码
       /** 出库单ID */
       Long odoId = cmd.getOdoId(); 
       /** 出库单明细ID */
       Long odoLineId = cmd.getOdoLineId();
       /**复合明细集合*/
       List<WhCheckingLineCommand> checkingLineList = cmd.getCheckingLineList();
       List<WhSkuInventoryCommand> skuInvList = null;
       //小车货格
       if(null != containerId && null != containerLatticeNo) {
           skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId,odoId,ouId, containerId, containerLatticeNo, null, null, null);
       }
       //小车出库箱
       if(null != containerId && null != outboundbox) {
           skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId,odoId,ouId, containerId, null, outboundbox, null, null);
       }
       //播种墙货格
       if(null != seedingWallCode && null != containerLatticeNo) {
           skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId,odoId,ouId, null, containerLatticeNo, null, null, seedingWallCode);
       }  
       //播种墙出库箱
       if(null != seedingWallCode && null != outboundbox) {
           skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId,odoId,ouId,null, null, outboundbox, null, seedingWallCode);
       }  
       //周转箱
       if(null != turnoverBoxCode) {
           skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId,odoId,ouId, null, null, null, turnoverBoxCode, null);
       } 
       for(WhSkuInventoryCommand invCmd:skuInvList){//一单多箱的情况库存记录大于复合明细记录,
           List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
           String uuid = invCmd.getUuid();
           if (null == snList || 0 == snList.size()) {//没有sn
               for(WhCheckingLineCommand checkingLineCmd:checkingLineList){ //复合记录
                   String checkingUuid = checkingLineCmd.getUuid();
                   if(uuid.equals(checkingUuid)) {
                       Long qty = checkingLineCmd.getCheckingQty();
                       String odoUuid = null;
                       WhSkuInventory skuInv = new WhSkuInventory();
                       BeanUtils.copyProperties(invCmd, skuInv);
                       skuInv.setLocationId(null);
                       skuInv.setOuterContainerId(null);
                       skuInv.setInsideContainerId(null);
                       skuInv.setContainerLatticeNo(null);
                       skuInv.setSeedingWallCode(null);
                       skuInv.setOutboundboxCode(outboundboxCode); //出库箱编码
                       try {
                           odoUuid = SkuInventoryUuid.invUuid(skuInv);
                           skuInv.setUuid(uuid);// UUID
                       } catch (Exception e) {
                           log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                           throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                       }
                       Double oldQty = 0.0;
                       if (true == isTabbInvTotal) {
                           try {
                               oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                           } catch (Exception e) {
                               log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                               throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                           }
                       } else {
                           oldQty = 0.0;
                       }
                       skuInv.setUuid(odoUuid);
                       skuInv.setOnHandQty(Double.valueOf(qty));
                       whSkuInventoryDao.insert(skuInv);
                       insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
                       // 记录入库库存日志(这个实现的有问题)
                       insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,null);
                   }
               }
           }else{//有sn
               for(WhCheckingLineCommand checkingLineCmd:checkingLineList){ //复合记录
                   String checkingUuid = checkingLineCmd.getUuid();
                   String odoUuid = null;
                   if(uuid.equals(checkingUuid)) {
                       Long qty = checkingLineCmd.getCheckingQty();
                       WhSkuInventory skuInv = new WhSkuInventory();
                       BeanUtils.copyProperties(invCmd, skuInv);
                       skuInv.setLocationId(null);
                       skuInv.setOuterContainerId(null);
                       skuInv.setInsideContainerId(null);
                       skuInv.setContainerLatticeNo(null);
                       skuInv.setSeedingWallCode(null);
                       skuInv.setOutboundboxCode(outboundboxCode); //出库箱编码
                       try {
                           odoUuid = SkuInventoryUuid.invUuid(skuInv);
                           skuInv.setUuid(uuid);// UUID
                       } catch (Exception e) {
                           log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                           throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                       }
                       Double oldQty = 0.0;
                       if (true == isTabbInvTotal) {
                           try {
                               oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                           } catch (Exception e) {
                               log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                               throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                           }
                       } else {
                           oldQty = 0.0;
                       }
                       skuInv.setUuid(odoUuid);
                       skuInv.setOnHandQty(Double.valueOf(qty));
                       whSkuInventoryDao.insert(skuInv);
                       insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
                       // 记录入库库存日志(这个实现的有问题)
                       insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,null);
                     //操作sn/残次信息
                       // uuid发生变更,重新插入sn
                       Integer count = 0;
                       for (WhSkuInventorySnCommand cSnCmd : snList) {
                            WhSkuInventorySn sn = new WhSkuInventorySn();
                            BeanUtils.copyProperties(cSnCmd, sn);
                            sn.setUuid(odoUuid);
                            whSkuInventorySnDao.saveOrUpdate(sn); // 更新sn
                            insertGlobalLog(GLOBAL_LOG_UPDATE, sn, ouId, userId, null, null);
                            count ++;
                            if(count == Integer.valueOf(qty.toString())) {
                                break;
                            }
                       }
                       insertSkuInventorySnLog(odoUuid, ouId); // 记录sn日志
                   }
               }
           }
       }
    }
}
