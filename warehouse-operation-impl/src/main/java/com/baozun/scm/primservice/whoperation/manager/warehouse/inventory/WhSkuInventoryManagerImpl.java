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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ManMadeContainerStatisticCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaPutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.AllocateStrategy;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
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
    private WarehouseManager warehouseManager;
    @Autowired
    private WhSkuDao skuDao;
    @Autowired
    private WhWaveLineManager whWaveLineManager;
    @Autowired
    private WhOdoDao whOdoDao;
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
    public void manMadePutaway(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long locationId, Long funcId, Warehouse warehouse, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
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
            Location loc = locationDao.findByIdExt(locationId, ouId);
            if (null == loc) {
                log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationId, logId);
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
            // invList =
            // whSkuInventoryDao.findLocToBeFilledInventoryByInsideContainerIdAndLocId(ouId,
            // insideContainerId, loc.getId());
            invList = whSkuInventoryDao.findContainerOnHandInventoryByInsideContainerId(ouId, insideContainerId);
            if (null == invList || 0 == invList.size()) {
                invList = whSkuInventoryDao.findContainerOnHandInventoryByOuterContainerId(ouId, containerId);
            }
            if (null == invList || 0 == invList.size()) {
                log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", insideContainerCode, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {insideContainerCode});
            }
            // 2.执行上架
            Set<Long> locIds = new HashSet<Long>();
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    if (!StringUtils.isEmpty(containerCode)) {
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
                    inv.setLocationId(locationId);
                    inv.setInboundTime(new Date());
                    inv.setIsLocked(false);
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
                    whSkuInventoryDao.saveOrUpdateByVersion(inv); // 更新库存记录
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    // 记录入库库存日志
                    insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, warehouse.getIsTabbInvTotal(), ouId, userId);

                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    if (!StringUtils.isEmpty(containerCode)) {
                        if (0 != containerId.compareTo(inv.getOuterContainerId())) {
                            log.error("outer container is not matching, paramContainerId is:[{}], invOuterContainerId is:[{}]", containerId, inv.getOuterContainerId());
                            throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_MATCH);
                        }
                    }
                    if (null == inv.getLocationId() || 0 != loc.getId().compareTo(inv.getLocationId())) {
                        log.error("location is null error, locationCode is:[{}], logId is:[{}]", locationId, logId);
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
                    inv.setLocationId(locationId);
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
                }
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
                    int count = whSkuInventoryDao.findWhSkuInventoryCountByInsideContainerId(ouId, insideContainerCmd.getId());
                    if (0 < count) {
                        // 还有没有上架的内部容器
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
                ManMadeContainerStatisticCommand manMadeContainer = new ManMadeContainerStatisticCommand();
                if (null != containerCmd) {
                    Container c = new Container();
                    BeanUtils.copyProperties(insideContainerCmd, c);
                    c.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                    c.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                    containerDao.saveOrUpdateByVersion(c);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, c, ouId, userId, null, null);
                    manMadeContainer = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
                    Set<Long> cacheInsideContainerIds = manMadeContainer.getInsideContainerIds();
                    // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                    TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
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
                        Integer containerStatus = containerCmd.getStatus();
                        if (false == isAllPutaway) {
                            if (ContainerStatus.CONTAINER_STATUS_PUTAWAY == containerStatus) {
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
                    if (!StringUtils.isEmpty(containerCode)) {
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
                    if (!StringUtils.isEmpty(containerCode)) {
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
                        manMadeContainer = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
                        Set<Long> cacheInsideContainerIds = manMadeContainer.getInsideContainerIds(); // 整箱上架:存在托盘,所有内部容器id集合
                        // 先判断内部容器是否全部已提示，只有全部提示了才能修改外部容器状态，否则外部容器一定是上架中
                        TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
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
		Long areaId = null;
		for (AllocateStrategy as : rules) {
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			} else {
				isStaticLocation = false;
			}
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));
			whWaveLineCommand.setAreaId(as.getAreaId());
			// 策略分配单位中包含托盘出
			if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_PALLET)) {
				whWaveLineCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PALLET);
				List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(as.getStrategyCode(), whWaveLineCommand, occupyQty);
				Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, null, occupyQty, occupyCode, odoLineId, wh, Constants.ALLOCATE_UNIT_PALLET, isStaticLocation, staticLocationIds);
				occupyQty -= num;
				actualQty += num;
				containerQty += num;
			}
			// 策略分配单位中包含货箱出
			if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_CONTAINER)) {
				whWaveLineCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_CONTAINER);
				List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(as.getStrategyCode(), whWaveLineCommand, occupyQty);
				Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, null, occupyQty, occupyCode, odoLineId, wh, Constants.ALLOCATE_UNIT_CONTAINER, isStaticLocation, staticLocationIds);
				occupyQty -= num;
				actualQty += num;
				containerQty += num;
			}
			// 按件查出库存并占用
			whWaveLineCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
			List<WhSkuInventoryCommand> skuInvs = findInventorysByAllocateStrategy(as.getStrategyCode(), whWaveLineCommand, occupyQty);
			Double num = inventoryOccupyManager.hardAllocateOccupy(skuInvs, occupyQty, occupyCode, odoLineId, wh);
			occupyQty -= num;
			actualQty += num;
			if (occupyQty.compareTo(new Double(0.0)) == 0) {
				break;
			}
		}
		// 数量全部占完回写波次明细分配数量
		whWaveLineManager.updateWaveLineByAllocateQty(whWaveLineCommand.getId(), actualQty, containerQty, isStaticLocation, staticLocationIds, areaId, wh.getId());
		if (log.isInfoEnabled()) {
			log.info("allocationInventoryByLine end,waveLineId:{},actualQty:{},logId:{}", whWaveLineCommand.getId(), actualQty, logId);
		}
	}
	
	@Override
	public void allocationInventoryByLineList(List<WhWaveLine> notHaveInvAttrLines, List<AllocateStrategy> rules, Warehouse wh, String logId) {
		if (log.isInfoEnabled()) {
			log.info("allocationInventoryByLineList start,ouId:{},logId:{}", wh.getId(), logId);
		}
		if (notHaveInvAttrLines == null || notHaveInvAttrLines.isEmpty()) {
			return;
		}
		List<WhSkuInventoryCommand> allSkuInvs = new ArrayList<WhSkuInventoryCommand>();
		// 记录静态库位可超分配的库位id
		Boolean isStaticLocation = false;
		Set<String> staticLocationIds = new HashSet<String>();
		Long areaId = null;
		for (AllocateStrategy as : rules) {
			List<String> allocateUnitCodes = Arrays.asList(as.getAllocateUnitCodes().split(","));	// 分配单位
			if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(as.getStrategyCode())) {
				isStaticLocation = true;
				areaId = as.getAreaId();
			} else {
				isStaticLocation = false;
			}
			WhWaveLineCommand whWaveLineCommand = new WhWaveLineCommand();
			for (int j = 0; j < notHaveInvAttrLines.size(); j++) {
				WhWaveLine line = notHaveInvAttrLines.get(j);
				BeanUtils.copyProperties(line, whWaveLineCommand);
				Double occupyQty = 0.0;					// 实际占用数量
				Double qty = line.getQty();				// 需要占用数量
				String occupyCode = line.getOdoCode();	// 占用编码
				Long odoLineId = line.getOdoLineId();	// 占用odoLineId
				Double containerQty = 0.0;
				// 查出此策略下的所有件库存
				if (allSkuInvs == null || allSkuInvs.isEmpty()) {
					whWaveLineCommand.setAreaId(as.getAreaId());
					whWaveLineCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PIECE);
					allSkuInvs = findInventorysByAllocateStrategy(as.getStrategyCode(), whWaveLineCommand, qty);
					if (allSkuInvs == null || allSkuInvs.isEmpty()) {
						break;
					}
				}
				// 策略分配单位中包含托盘出
				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_PALLET)) {
					whWaveLineCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_PALLET);
					// 查询出是托盘容器的库存份
					List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(as.getStrategyCode(), whWaveLineCommand, qty);
					// 占用库存并把策略下的所有件库存集合(allSkuInvs)扣减
					Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, allSkuInvs, qty, occupyCode, odoLineId, wh, Constants.ALLOCATE_UNIT_PALLET, isStaticLocation, staticLocationIds);
					qty -= num;
					occupyQty += num;
					containerQty += num;
				}
				// 策略分配单位中包含货箱出
				if (allocateUnitCodes.contains(Constants.ALLOCATE_UNIT_CONTAINER)) {
					whWaveLineCommand.setAllocateUnitCodes(Constants.ALLOCATE_UNIT_CONTAINER);
					// 查询出是货箱容器的库存份
					List<WhSkuInventoryCommand> uuids = findInventorysByAllocateStrategy(as.getStrategyCode(), whWaveLineCommand, qty);
					// 占用库存并把策略下的所有件库存集合(allSkuInvs)扣减
					Double num = inventoryOccupyManager.occupyInvUuidsByPalletContainer(uuids, allSkuInvs, qty, occupyCode, odoLineId, wh, Constants.ALLOCATE_UNIT_CONTAINER, isStaticLocation, staticLocationIds);
					qty -= num;
					occupyQty += num;
					containerQty += num;
				}
				Double num = 0.0;
				// 分配策略为数量最佳匹配
				if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(as.getStrategyCode())) {
					List<WhSkuInventoryCommand> uuids = findInventoryUuidByBestMatchAndPiece(whWaveLineCommand, qty);
					num = inventoryOccupyManager.occupyInvUuids(uuids, allSkuInvs, qty, occupyCode, odoLineId, wh, Constants.ALLOCATE_UNIT_CONTAINER, isStaticLocation, staticLocationIds);
					occupyQty += num;
				} else {
					// 按件占用
					num = inventoryOccupyManager.hardAllocateListOccupy(allSkuInvs, qty, occupyCode, odoLineId, wh, isStaticLocation, staticLocationIds);
					occupyQty += num;
				}
				if (qty.compareTo(num) == 1) {
					line.setQty(qty - num);
				} else if (qty.compareTo(num) == 0) {
					notHaveInvAttrLines.remove(j--);
				}
				whWaveLineManager.updateWaveLineByAllocateQty(line.getId(), occupyQty, containerQty, isStaticLocation, staticLocationIds, areaId, wh.getId());
				if (allSkuInvs == null || allSkuInvs.isEmpty()) {
					break;
				}
			}
			if (notHaveInvAttrLines.isEmpty()) {
				break;
			}
		}
		if (log.isInfoEnabled()) {
			log.info("allocationInventoryByLineList end,ouId:{},logId:{}", wh.getId(), logId);
		}
	}

	private List<WhSkuInventoryCommand> findInventorysByAllocateStrategy(String strategyCode, WhWaveLineCommand whWaveLineCommand, Double qty) {
		List<WhSkuInventoryCommand> list = new ArrayList<WhSkuInventoryCommand>();
		// 先入先出
		if (Constants.ALLOCATE_STRATEGY_FIRSTINFIRSTOUT.equals(strategyCode)) {
			list = findInventoryUuidByInBoundTime(whWaveLineCommand, true);
		}
		// 先入后出
		else if (Constants.ALLOCATE_STRATEGY_FIRSTINLASTOUT.equals(strategyCode)) {
			list = findInventoryUuidByInBoundTime(whWaveLineCommand, false);
		}
		// 先到期先出
		else if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT.equals(strategyCode)) {
			list = findInventoryUuidByExpTime(whWaveLineCommand, true);
		}
		// 后到期先出
		else if (Constants.ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT.equals(strategyCode)) {
			list = findInventoryUuidByExpTime(whWaveLineCommand, false);
		}
		// 数量最佳匹配
		else if (Constants.ALLOCATE_STRATEGY_QUANTITYBESTMATCH.equals(strategyCode)) {
			list = findInventoryUuidByBestMatch(whWaveLineCommand, qty);
		}
		// 最大存储空间
		else if (Constants.ALLOCATE_STRATEGY_MAXIMUMSTORAGESPACE.equals(strategyCode)) {
			list = findInventoryUuidByAmount(whWaveLineCommand, true);
		}
		// 最小拣货次数
		else if (Constants.ALLOCATE_STRATEGY_MINIMUMORDERPICKINGTIMES.equals(strategyCode)) {
			list = findInventoryUuidByAmount(whWaveLineCommand, false);
		}
		// 静态库位可超分配
		else if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT.equals(strategyCode)) {
			list = findInventoryUuidByStaticLocation(whWaveLineCommand, true);
		}
		// 静态库位不可超分配
		else if (Constants.ALLOCATE_STRATEGY_STATICLOCATIONNOTCANASSIGNMENT.equals(strategyCode)) {
			list = findInventoryUuidByStaticLocation(whWaveLineCommand, true);
		}
		// 混SKU库位
		else if (Constants.ALLOCATE_STRATEGY_MIXEDSKUSLOCATION.equals(strategyCode)) {
			list = findInventoryUuidByMixedLocation(whWaveLineCommand, true);
		}
		// 空库位
		else if (Constants.ALLOCATE_STRATEGY_EMPTYLOCATION.equals(strategyCode)) {
			
		}
		return list;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByInBoundTime(WhWaveLineCommand whWaveLineCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
		whWaveLineCommand.setPriority(new Boolean(flag));
		if (Constants.ALLOCATE_UNIT_PALLET.equals(whWaveLineCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_CONTAINER.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByInBoundTime(whWaveLineCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByInBoundTime(whWaveLineCommand);
		}
		return skuInvs;
	}
    
	public List<WhSkuInventoryCommand> findInventoryUuidByExpTime(WhWaveLineCommand whWaveLineCommand, boolean flag) {
		Long skuId = whWaveLineCommand.getSkuId();
		Long ouId = whWaveLineCommand.getOuId();
    	Boolean isExpirationSku = skuDao.checkIsExpirationSku(skuId, ouId);
    	if (isExpirationSku == null || !isExpirationSku) {
			return null;
		}
    	List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
		whWaveLineCommand.setPriority(new Boolean(flag));
		if (Constants.ALLOCATE_UNIT_PALLET.equals(whWaveLineCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_CONTAINER.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByExpTime(whWaveLineCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByExpTime(whWaveLineCommand);
		}
		return skuInvs;
	}
    
	public List<WhSkuInventoryCommand> findInventoryUuidByBestMatch(WhWaveLineCommand whWaveLineCommand, Double qty) {
		List<WhSkuInventoryCommand> skuInvs = null;
		whWaveLineCommand.setPriority(Boolean.TRUE);
		if (Constants.ALLOCATE_UNIT_PALLET.equals(whWaveLineCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_CONTAINER.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whWaveLineCommand);
			// 数量最佳计算
			return bestMatchSkuInvs(skuInvs, qty);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whWaveLineCommand);
		}
		return skuInvs;
	}

	public List<WhSkuInventoryCommand> findInventoryUuidByAmount(WhWaveLineCommand whWaveLineCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 入库时间顺序 true:升序 false:降序
		whWaveLineCommand.setPriority(new Boolean(flag));
		if (Constants.ALLOCATE_UNIT_PALLET.equals(whWaveLineCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_CONTAINER.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whWaveLineCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whWaveLineCommand);
		}
		return skuInvs;
	}

	public List<WhSkuInventoryCommand> findInventoryUuidByStaticLocation(WhWaveLineCommand whWaveLineCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 顺序 true:升序 false:降序
		whWaveLineCommand.setPriority(new Boolean(flag));
		whWaveLineCommand.setIsStatic(Boolean.TRUE);
		whWaveLineCommand.setIsMixStacking(null);
		if (Constants.ALLOCATE_UNIT_PALLET.equals(whWaveLineCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_CONTAINER.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whWaveLineCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whWaveLineCommand);
		}
		return skuInvs;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByMixedLocation(WhWaveLineCommand whWaveLineCommand, boolean flag) {
		List<WhSkuInventoryCommand> skuInvs = null;
		// 顺序 true:升序 false:降序
		whWaveLineCommand.setPriority(new Boolean(flag));
		whWaveLineCommand.setIsStatic(null);
		whWaveLineCommand.setIsMixStacking(Boolean.TRUE);
		if (Constants.ALLOCATE_UNIT_PALLET.equals(whWaveLineCommand.getAllocateUnitCodes())
				|| Constants.ALLOCATE_UNIT_CONTAINER.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryUuidByAmount(whWaveLineCommand);
		} else if (Constants.ALLOCATE_UNIT_PIECE.equals(whWaveLineCommand.getAllocateUnitCodes())) {
			skuInvs = whSkuInventoryDao.findInventoryByAmount(whWaveLineCommand);
		}
		return skuInvs;
	}
	
	public List<WhSkuInventoryCommand> findInventoryUuidByBestMatchAndPiece(WhWaveLineCommand whWaveLineCommand, Double qty) {
		whWaveLineCommand.setPriority(Boolean.TRUE);
		List<WhSkuInventoryCommand> skuInvs = whSkuInventoryDao.findInventoryUuid(whWaveLineCommand);
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
			if (skuInv.getSumOnHandQty().compareTo(qty) == 0) {
				invBestList.add(skuInv);
				return invBestList;
			}
			num += skuInv.getSumOnHandQty();
		}
		if (num.compareTo(qty) == -1) {
			return skuInvs;
		}
		num = 0.0;
		for (int i = 0; i < skuInvs.size(); i++) {
			WhSkuInventoryCommand skuInv = skuInvs.get(i);
			if (skuInv.getSumOnHandQty().compareTo(qty) == -1) {
				invBestList.add(0, skuInv);
				num += skuInv.getSumOnHandQty();
				if (num.compareTo(qty) == 0) {
					return invBestList;
				} else if (num.compareTo(qty) == 1) {
					break;
				}
			} else if (skuInv.getSumOnHandQty().compareTo(qty) == 1) {
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
	
}
