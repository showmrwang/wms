/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
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
    
    /**
     * 库位绑定（分配容器库存及生成待移入库位库存）
     * @author lichuan
     * @param invList
     * @param warehouse
     * @param lrrList
     * @param putawayPatternDetailType
     */
    @Override
    public void containerInvAllocatedAndLocationInvBinding(List<WhSkuInventoryCommand> invList, Warehouse warehouse, List<LocationRecommendResultCommand> lrrList, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        if(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType){
            //拆箱上架
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
            //插入待移入库位库存
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
                                splitInv.setToBeFilledQty(null);
                                splitInv.setOnHandQty(new Double(invCmd.getOnHandQty().intValue() - qty));// 剩余在库数量
                                splitInv.setLocationId(null);
                                whSkuInventoryDao.insert(splitInv);
                                insertGlobalLog(GLOBAL_LOG_INSERT, splitInv, ouId, userId, null, null);
                            }
                        }
                    }
                }
            }
            //待出容器库存（分配容器库存）
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setAllocatedQty(inv.getOnHandQty());// 已分配
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
                        whSkuInventoryDao.saveOrUpdateByVersion(inv);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                    }
                }
            }
        }else{
            // 整托上架、整箱上架
            LocationRecommendResultCommand lrr = lrrList.get(0);
            Long lrrLocId = lrr.getLocationId();
            //String lrrLocCode = lrr.getLocationCode();
            //插入待移入库位库存
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
            //待出容器库存（分配容器库存）
            for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                if (null == snList || 0 == snList.size()) {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setAllocatedQty(inv.getOnHandQty());// 已分配
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                } else {
                    WhSkuInventory inv = new WhSkuInventory();
                    BeanUtils.copyProperties(invCmd, inv);
                    inv.setAllocatedQty(inv.getOnHandQty());// 已分配
                    whSkuInventoryDao.saveOrUpdateByVersion(inv);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, inv, ouId, userId, null, null);
                }
            }
        }
    }

}
