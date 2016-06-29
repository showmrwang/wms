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
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.inventory.InventoryStatisticManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;

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
    @Autowired
    private ContainerDao containerDao;

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
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, codelist);
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
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, codelist);
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
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, codelist);
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
    public Long sysGuidePalletPutawayCacheTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, String logId) {
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
                        if (null == value) value = "-1";
                        if (0 == icId.compareTo(new Long(value))) {
                            isExists = true;
                            long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                            if (skus > 0) {
                                Set<Long> icSkus = insideContainerSkuIds.get(icId);
                                if (isCacheAllExists(icSkus, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
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

    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param skuCmd
     * @return
     */
    @Override
    public CheckScanSkuResultCommand sysGuidePalletPutawayCacheSkuOrTipContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, Map<Long, Map<Long, Long>> insideContainerSkuIdsQty,
            WhSkuCommand skuCmd, String logId) {
        CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
        Long ocId = ocCmd.getId();
        Long icId = icCmd.getId();
        // 0.先判断当前内部容器是否在缓存中
        boolean icExists = false;
        for (Long iId : insideContainerIds) {
            if (0 == icId.compareTo(iId)) {
                icExists = true;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        // 1.当前的内部容器是不是提示容器队列的第一个
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
        if (0 < len) {
            String cacheIcId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString(), 0);// 队列的第一个
            if (!icId.toString().equals(cacheIcId)) {
                log.error("tip container is not queue firtst element exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        } else {
            log.error("scan container queue is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

        }
        // 2.得到当前内部容器的所有商品并复核商品
        Long skuId = skuCmd.getId();
        Double skuQty = skuCmd.getScanSkuQty();
        Set<Long> icSkusIds = insideContainerSkuIds.get(icId);
        boolean skuExists = false;
        for (Long sId : icSkusIds) {
            if (0 == skuId.compareTo(sId)) {
                skuExists = true;
                Map<Long, Long> icSkuAndQty = insideContainerSkuIdsQty.get(icId);
                Long icSkuQty = icSkuAndQty.get(skuId);
                if (skuQty.longValue() == icSkuQty.longValue()) {
                    long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                    if (skus > 0) {
                        boolean isExists = false;
                        for (int i = 0; i < new Long(skus).intValue(); i++) {
                            String value = cacheManager.findListItem(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), i);
                            if (null == value) value = "-1";
                            if (0 == skuId.compareTo(new Long(value))) {
                                isExists = true;
                                break;
                            }
                        }
                        if (false == isExists) {
                            cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                            if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                // 全部商品已复核完毕
                                if (isCacheAllExists(insideContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString())) {
                                    // 全部容器已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 提示下一个容器
                                    Long tipContainerId = sysGuidePalletPutawayCacheTipContainer(ocCmd, insideContainerIds, insideContainerSkuIds, logId);
                                    cssrCmd.setNeedTipContainer(true);
                                    cssrCmd.setTipContainerId(tipContainerId);
                                }
                            } else {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                            }
                            break;
                        } else {
                            log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                            throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                        }
                    } else {
                        cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                        if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                            // 全部商品已复核完毕
                            if (isCacheAllExists(insideContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString())) {
                                // 全部容器已复核完毕
                                cssrCmd.setPutaway(true);// 可上架
                            } else {
                                // 提示下一个容器
                                Long tipContainerId = sysGuidePalletPutawayCacheTipContainer(ocCmd, insideContainerIds, insideContainerSkuIds, logId);
                                cssrCmd.setNeedTipContainer(true);
                                cssrCmd.setTipContainerId(tipContainerId);
                            }
                        } else {
                            // 继续复核
                            cssrCmd.setNeedScanSku(true);
                        }
                        break;
                    }
                } else {
                    log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {icCmd.getCode()});
                }
            }
        }
        if (false == skuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
        }
        return cssrCmd;
    }


    private boolean isCacheAllExists(Set<Long> ids, String cacheKey) {
        boolean allExists = true;
        long len = cacheManager.listLen(cacheKey);
        if (0 < len) {
            for (Long id : ids) {
                Long cId = id;
                boolean isExists = false;
                for (int i = 0; i < new Long(len).intValue(); i++) {
                    String value = cacheManager.findListItem(cacheKey, i);
                    if (null == value) value = "-1";
                    if (0 == new Long(value).compareTo(cId)) {
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
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     */
    @Override
    public void sysGuidePalletPutawayRemoveAllCache(ContainerCommand containerCmd, String logId) {
        Long ocId = containerCmd.getId();
        // 0.先清除所有复核商品队列
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, ocId.toString());
        if (null != isCmd) {
            Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
            for (Long icId : insideContainerIds) {
                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
            }
        }
        // 1.再清除所有提示容器队列
        cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
        // 2.清除所有库存统计信息
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, ocId.toString());
        // 3.清除所有库存缓存信息
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, ocId.toString());
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public List<ContainerCommand> sysGuideContainertPutawayCacheInsideContainer(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        // 查询对应所有内部容器信息
        List<ContainerCommand> icList = whSkuInventoryDao.findAllInsideContainerByOuterContainerId(ouId, containerId);
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
        }
        // 缓存
        cacheManager.setMapObject(CacheConstants.CONTAINER, containerId.toString(), icList, CacheConstants.CACHE_ONE_MONTH);
        return icList;
    }

    /**
     * @author lichuan
     * @param icList
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideContainerPutawayTipContainer0(ContainerCommand containerCmd, List<ContainerCommand> icList, String logId) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        if (0 < len) {
            String insideContainerId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), len - 1);
            tipContainerId = new Long(insideContainerId);
        } else {
            // 随机取一个容器
            for (ContainerCommand ic : icList) {
                Long icId = ic.getId();
                if (null != icId) {
                    tipContainerId = icId;
                    cacheManager.pushToListHead(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), icId.toString());
                    break;
                }
            }
        }
        return tipContainerId;
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public List<WhSkuInventoryCommand> sysGuideContainerPutawayCacheInventory(ContainerCommand insideContainerCmd, Long ouId, String logId) {
        Long containerId = insideContainerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventory start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = insideContainerCmd.getCode();
        // 缓存所有库存
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCode(ouId, codelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventory end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return invList;
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param isCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public InventoryStatisticResultCommand sysGuideContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId) {
        Long containerId = insideContainerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return isCmd;
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public InventoryStatisticResultCommand sysGuideContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, Long ouId, String logId) {
        Long containerId = insideContainerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = insideContainerCmd.getCode();
        // 缓存所有库存
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCode(ouId, codelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.PALLET_PUTAWAY, ouId, logId);
        isCmd.setInsideContainerId(containerId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
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
    public List<WhSkuInventoryCommand> sysGuideContainerPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryAndStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = containerCmd.getCode();
        // 缓存所有库存
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCode(ouId, codelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide container putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_MONTH);
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.PALLET_PUTAWAY, ouId, logId);
        isCmd.setInsideContainerId(containerId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryAndStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return invList;
    }

    /**
     * @author lichuan
     * @param ocCmd
     * @param icCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param insideContainerSkuIdsQty
     * @param skuCmd
     * @param logId
     * @return
     */
    @Override
    public CheckScanSkuResultCommand sysGuideContainerPutawayCacheSkuAndCheckContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds,
            Map<Long, Map<Long, Long>> insideContainerSkuIdsQty, WhSkuCommand skuCmd, String logId) {
        CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
        Long ocId = null;
        Long icId = icCmd.getId();
        // 0.先判断当前内部容器是否在缓存中
        boolean icExists = false;
        for (Long iId : insideContainerIds) {
            if (0 == icId.compareTo(iId)) {
                icExists = true;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        if (null != ocCmd) {
            ocId = ocCmd.getId();
            // 1.当前的内部容器是不是提示容器队列的第一个
            long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
            if (0 < len) {
                String cacheIcId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString(), 0);// 队列的第一个
                if (!icId.toString().equals(cacheIcId)) {
                    log.error("tip container is not queue firtst element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan container queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

            }
            // 2.得到当前内部容器的所有商品并复核商品
            Long skuId = skuCmd.getId();
            Double skuQty = skuCmd.getScanSkuQty();
            Set<Long> icSkusIds = insideContainerSkuIds.get(icId);
            boolean skuExists = false;
            for (Long sId : icSkusIds) {
                if (0 == skuId.compareTo(sId)) {
                    skuExists = true;
                    Map<Long, Long> icSkuAndQty = insideContainerSkuIdsQty.get(icId);
                    Long icSkuQty = icSkuAndQty.get(skuId);
                    if (skuQty.longValue() == icSkuQty.longValue()) {
                        long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        if (skus > 0) {
                            boolean isExists = false;
                            for (int i = 0; i < new Long(skus).intValue(); i++) {
                                String value = cacheManager.findListItem(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), i);
                                if (null == value) value = "-1";
                                if (0 == skuId.compareTo(new Long(value))) {
                                    isExists = true;
                                    break;
                                }
                            }
                            if (false == isExists) {
                                cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                                if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                    // 全部商品已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                    //判断上架以后是否需要提示下一个容器
                                    
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else {
                                log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                                throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                            }
                        } else {
                            cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                            if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                // 全部商品已复核完毕
                                cssrCmd.setPutaway(true);// 可上架
                            } else {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                            }
                            break;
                        }
                    } else {
                        log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {icCmd.getCode()});
                    }
                }
            }
            if (false == skuExists) {
                log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
            }
        } else {
            // 1.得到当前内部容器的所有商品并复核商品
            Long skuId = skuCmd.getId();
            Double skuQty = skuCmd.getScanSkuQty();
            Set<Long> icSkusIds = insideContainerSkuIds.get(icId);
            boolean skuExists = false;
            for (Long sId : icSkusIds) {
                if (0 == skuId.compareTo(sId)) {
                    skuExists = true;
                    Map<Long, Long> icSkuAndQty = insideContainerSkuIdsQty.get(icId);
                    Long icSkuQty = icSkuAndQty.get(skuId);
                    if (skuQty.longValue() == icSkuQty.longValue()) {
                        long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        if (skus > 0) {
                            boolean isExists = false;
                            for (int i = 0; i < new Long(skus).intValue(); i++) {
                                String value = cacheManager.findListItem(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), i);
                                if (null == value) value = "-1";
                                if (0 == skuId.compareTo(new Long(value))) {
                                    isExists = true;
                                    break;
                                }
                            }
                            if (false == isExists) {
                                cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                                if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                    // 全部商品已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else {
                                log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                                throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                            }
                        } else {
                            cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                            if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                // 全部商品已复核完毕
                                cssrCmd.setPutaway(true);// 可上架
                            } else {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                            }
                            break;
                        }
                    } else {
                        log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {icCmd.getCode()});
                    }
                }
            }
            if (false == skuExists) {
                log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
            }
        }
        return cssrCmd;
    }
}
