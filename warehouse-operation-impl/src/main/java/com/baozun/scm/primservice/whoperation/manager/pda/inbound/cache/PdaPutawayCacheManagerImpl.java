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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.inventory.InventoryStatisticManager;

/**
 * @author lichuan
 *
 */
@Service("pdaPutawayCacheManager")
@Transactional
public class PdaPutawayCacheManagerImpl extends BaseManagerImpl implements PdaPutawayCacheManager {
    protected static final Logger log = LoggerFactory.getLogger(PdaPutawayCacheManagerImpl.class);

    @Autowired
    InventoryStatisticManager inventoryStatisticManager;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private CacheManager cacheManager;

    /**
     * @author lichuan
     * @param containerCode
     * @return
     */
    @Override
    public List<WhSkuInventoryCommand> sysGuidePalletPutawayCacheInventory(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventory start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = containerCmd.getCode();
        // 缓存所有库存
        List<String> ocCodelist = new ArrayList<String>();
        ocCodelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventory end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return invList;
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param isCmd
     * @param ouId
     * @return
     */
    @Override
    public InventoryStatisticResultCommand sysGuidePalletPutawayCacheInventoryStatistic(ContainerCommand containerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventoryStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventoryStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return isCmd;
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public InventoryStatisticResultCommand sysGuidePalletPutawayCacheInventoryStatistic(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventoryStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = containerCmd.getCode();
        // 缓存所有库存
        List<String> ocCodelist = new ArrayList<String>();
        ocCodelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.PALLET_PUTAWAY, ouId, logId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventoryStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return isCmd;
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public List<WhSkuInventoryCommand> sysGuidePalletPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventoryAndStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = containerCmd.getCode();
        // 缓存所有库存
        List<String> ocCodelist = new ArrayList<String>();
        ocCodelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, ocCodelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide pallet putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_MONTH);
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.PALLET_PUTAWAY, ouId, logId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway cache inventoryAndStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return invList;
    }


    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @return
     */
    @Override
    public Long sysGuidePalletPutawayCacheTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        if (len > 0) {
            for (Long id : insideContainerIds) {
                if (null != id) {
                    Long icId = id;
                    boolean isExists = false;
                    for (int i = 0; i < new Long(len).intValue(); i++) {
                        String value = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), i);
                        if (0 == icId.compareTo(new Long(value))) {
                            isExists = true;
                            long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                            if (skus > 0) {
                                Set<Long> icSkus = insideContainerSkuIds.get(icId);
                                if (new Long(skus).intValue() == icSkus.size()) {
                                    continue;
                                } else {
                                    tipContainerId = id;
                                    break;
                                }
                            } else {
                                tipContainerId = id;
                                break;
                            }
                        }
                    }
                    if (false == isExists) {
                        tipContainerId = id;
                        cacheManager.pushToListHead(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), tipContainerId.toString());
                        break;
                    } else {
                        if (null != tipContainerId) {
                            break;
                        }
                    }

                }
            }
        } else {
            for (Long id : insideContainerIds) {
                if (null != id) {
                    tipContainerId = id;// 随机取出一个放入队列
                    break;
                }
            }
            cacheManager.pushToListHead(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), tipContainerId.toString());
        }
        return tipContainerId;
    }

}
