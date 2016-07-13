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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
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
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.statis.InventoryStatisticManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

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
     * @param containerId
     * @param sysDate
     * @param logId
     * @return
     */
    @Override
    public synchronized boolean sysGuidePutawayLocRecommendQueue(Long containerId, String logId) {
        boolean ret = true;
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway locRecommend queue validate start, contianerId is:[{}], logId is:[{}]", containerId, logId);
        }
        long len = cacheManager.listLen(CacheConstants.LOCATION_RECOMMEND_QUEUE);
        String qcId = "";// 当前队列头
        if (0 < len) {
            // 此刻已有排队
            Set<String> ids = new HashSet<String>();
            ids.add(containerId.toString());
            if (isCacheAllExistsContain(ids, CacheConstants.LOCATION_RECOMMEND_QUEUE)) {
                // 已在队列中
                String first = getFirstValidDataFromQueue(CacheConstants.LOCATION_RECOMMEND_QUEUE);
                String[] values = ParamsUtil.splitParam(first);
                if (null != values) {
                    String fContainerId = values[0];
                    if (!containerId.toString().equals(fContainerId)) {
                        ret = false;// 需要排队
                    } else {
                        qcId = first;
                    }
                } else {
                    ret = false;
                }
            } else {
                // 未在队列中,则放入队列
                Date date = new Date();
                Long dt = date.getTime();
                String qId = ParamsUtil.concatParam(containerId.toString(), dt.toString());
                cacheManager.pushToListFooter(CacheConstants.LOCATION_RECOMMEND_QUEUE, qId);
                String first = getFirstValidDataFromQueue(CacheConstants.LOCATION_RECOMMEND_QUEUE);
                String[] values = ParamsUtil.splitParam(first);
                if (null != values) {
                    String fContainerId = values[0];
                    if (!containerId.toString().equals(fContainerId)) {
                        ret = false;// 需要排队
                    } else {
                        qcId = first;
                    }
                } else {
                    ret = false;
                }
            }
        } else {
            // 此刻尚未排队
            Date date = new Date();
            Long dt = date.getTime();
            String qId = ParamsUtil.concatParam(containerId.toString(), dt.toString());
            cacheManager.pushToListFooter(CacheConstants.LOCATION_RECOMMEND_QUEUE, qId);
            String first = cacheManager.findListItem(CacheConstants.LOCATION_RECOMMEND_QUEUE, 0);
            String[] values = ParamsUtil.splitParam(first);
            if (null != values) {
                String fContainerId = values[0];
                if (!containerId.toString().equals(fContainerId)) {
                    ret = false;// 需要排队
                } else {
                    qcId = first;
                }
            } else {
                ret = false;
            }
        }
        if (true == ret && !StringUtils.isEmpty(qcId)) {
            // 判断剩余时间，少于10秒需要重新排队
            String[] values = ParamsUtil.splitParam(qcId);
            if (null != values) {
                String qd = values[1];
                Date cDate = new Date();
                long cd = cDate.getTime();
                long rd = cd - new Long(qd).longValue();// 剩余毫秒数
                if (10 > rd / 1000) {
                    // 剩余时间少于10秒，认为时间不足够用来执行逻辑，需要重新排队
                    cacheManager.popListHead(CacheConstants.LOCATION_RECOMMEND_QUEUE);
                    ret = false;
                }
            } else {
                ret = false;
            }
        }
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway locRecommend queue validate end, contianerId is:[{}], logId is:[{}]", containerId, logId);
        }
        return ret;
    }

    private String getFirstValidDataFromQueue(String key) {
        String ret = null;
        long len = cacheManager.listLen(key);
        for (int i = 0; i < len; i++) {
            String fisrt = cacheManager.findListItem(key, i);
            String[] values = ParamsUtil.splitParam(fisrt);
            if (null != values) {
                String fDate = values[1];
                long fd = new Long(fDate).longValue();
                long fed = fd + 1000 * 60 * 1;// 一分钟后认为排队超时
                Date cDate = new Date();
                long cd = cDate.getTime();
                if (fed < cd) {
                    // 排队超时，移出队列
                    cacheManager.popListHead(key);
                } else {
                    ret = fisrt;
                }
            }
            if (null != ret) {
                break;
            }
        }
        return ret;
    }

    /**
     * @author lichuan
     * @param containerId
     * @param logId
     */
    @Override
    public void sysGuidePutawayLocRecommendPopQueue(Long containerId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway locRecommend popQueue start, contianerId is:[{}], logId is:[{}]", containerId, logId);
        }
        String fisrt = cacheManager.findListItem(CacheConstants.LOCATION_RECOMMEND_QUEUE, 0);
        String[] values = ParamsUtil.splitParam(fisrt);
        if (null != values) {
            String fContainerId = values[0];
            if (containerId.toString().equals(fContainerId)) {
                // 弹出队列
                cacheManager.popListHead(CacheConstants.LOCATION_RECOMMEND_QUEUE);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway locRecommend popQueue end, contianerId is:[{}], logId is:[{}]", containerId, logId);
        }
    }

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
     * @param ouId
     * @param logId
     */
    @Override
    public void sysGuidePutawayRemoveInventory(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway remove inventory start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        if (log.isInfoEnabled()) {
            log.info("sys guide pallet putaway remove inventory end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
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
     * @param logId
     * @return
     */
    @Override
    public Long sysGuidePalletPutawayCacheTipContainer0(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        if (0 < len) {
            String insideContainerId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), 0);
            tipContainerId = new Long(insideContainerId);
        } else {
            // 随机取一个容器
            for (Long ic : insideContainerIds) {
                Long icId = ic;
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
            // for (Long id : insideContainerIds) {
            // if (null != id) {
            // tipContainerId = id;// 随机取出一个放入队列
            // break;
            // }
            // }
            // cacheManager.pushToListHead(CacheConstants.SCAN_CONTAINER_QUEUE +
            // containerId.toString(), tipContainerId.toString());
            log.error("tip container is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
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
                log.error("tip container is not queue first element exception, logId is:[{}]", logId);
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
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public ContainerStatisticResultCommand sysGuideContainerPutawayCacheInsideContainerStatistic(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache containerStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        // 查询对应所有内部容器信息
        List<ContainerCommand> icList = whSkuInventoryDao.findAllInsideContainerByOuterContainerId(ouId, containerId);
        ContainerStatisticResultCommand csrCmd = inventoryStatisticManager.sysGuidePutawayContainerStatistic(icList, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, logId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString(), csrCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache containerStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return csrCmd;
    }

    /**
     * @author lichuan
     * @param icList
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideContainerPutawayTipContainer0(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        if (0 < len) {
            String insideContainerId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), 0);
            tipContainerId = new Long(insideContainerId);
        } else {
            // 随机取一个容器
            for (Long ic : insideContainerIds) {
                Long icId = ic;
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
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideContainerPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        if (0 < len) {
            // 随机取一个容器
            for (Long ic : insideContainerIds) {
                Long icId = ic;
                if (null != icId) {
                    boolean isExists = false;
                    for (int i = 0; i < len; i++) {
                        String cacheId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), i);
                        if (0 == icId.compareTo(new Long(cacheId))) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
                        tipContainerId = icId;
                        cacheManager.pushToListHead(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), icId.toString());
                        break;
                    }
                }
            }
        } else {
            log.error("tip container is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
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
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, logId);
        isCmd.setInsideContainerId(containerId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryAndStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return invList;
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param logId
     * @return
     */
    @Override
    public Boolean sysGuideContainerPutawayNeedTipContainer(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Set<Long> insideContainerIds, String logId) {
        Boolean ret = false;
        if (null == containerCmd) {
            ret = false;
            return ret;
        } else {
            Long containerId = containerCmd.getId();
            Long insideContainerId = insideContainerCmd.getId();
            // 0.先判断当前的容器是不是提示容器队列的第一个
            long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
            if (0 < len) {
                String cacheIcId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), 0);// 队列的第一个
                if (!insideContainerId.toString().equals(cacheIcId)) {
                    log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("sys guide container putaway cache inside container are null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            // 1.获取所有内部容器信息
            if (isCacheAllExists(insideContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString())) {
                // 全部容器已复核完毕
                ret = false;
            } else {
                ret = true;
            }
        }
        return ret;
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
                break;
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
                    log.error("tip container is not queue first element exception, logId is:[{}]", logId);
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
                                    // 判断上架以后是否需要提示下一个容器
                                    if (isCacheAllExists(insideContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString())) {
                                        // 全部容器已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else {
                                // 重复扫描如果是最后一件则认为可以上架，否则报错提示
                                if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                    // 全部商品已复核完毕
                                    // 判断上架以后是否需要提示下一个容器
                                    if (isCacheAllExists(insideContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString())) {
                                        // 全部容器已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                    }
                                } else {
                                    log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                                    throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                                }
                            }
                        } else {
                            cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), skuId.toString());
                            if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                // 全部商品已复核完毕
                                // 判断上架以后是否需要提示下一个容器
                                if (isCacheAllExists(insideContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString())) {
                                    // 全部容器已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
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

    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param isAfterPutawayTipContainer
     * @param logId
     */
    @Override
    public void sysGuideContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, String logId) {
        if (null != containerCmd) {
            Long ocId = containerCmd.getId();
            if (false == isAfterPutawayTipContainer) {
                // 0.先清除所有复核商品队列及内部库存及统计信息
                ContainerStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocId.toString());
                if (null != isCmd) {
                    Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
                    for (Long icId : insideContainerIds) {
                        cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
                    }
                }
                // 1.再清除所有提示容器队列
                cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
                // 2.清除所有内部容器统计信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_STATISTIC, ocId.toString());

            } else {
                Long icId = insideContainerCmd.getId();
                // 0.清除所有库存统计信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                // 1.清除所有库存缓存信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
            }
        } else {
            Long icId = insideContainerCmd.getId();
            // 0.清除所有商品队列
            cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
            // 1.清除所有库存统计信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
            // 2.清除所有库存缓存信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
        }
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public ContainerStatisticResultCommand sysGuideSplitContainerPutawayCacheInsideContainerStatistic(ContainerCommand containerCmd, Long ouId, String logId) {
        Long containerId = containerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache containerStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        // 查询对应所有内部容器信息
        List<ContainerCommand> icList = whSkuInventoryDao.findAllInsideContainerByOuterContainerId(ouId, containerId);
        ContainerStatisticResultCommand csrCmd = inventoryStatisticManager.sysGuidePutawayContainerStatistic(icList, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, logId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString(), csrCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache containerStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return csrCmd;
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideSplitContainerPutawayTipContainer0(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        if (0 < len) {
            String insideContainerId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), 0);
            tipContainerId = new Long(insideContainerId);
        } else {
            // 随机取一个容器
            for (Long ic : insideContainerIds) {
                Long icId = ic;
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
    public List<WhSkuInventoryCommand> sysGuideSplitContainerPutawayCacheInventory(ContainerCommand insideContainerCmd, Long ouId, String logId) {
        Long containerId = insideContainerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache inventory start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = insideContainerCmd.getCode();
        // 缓存所有库存
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCode(ouId, codelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide splitContainer putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache inventory end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
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
    public InventoryStatisticResultCommand sysGuideSplitContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, InventoryStatisticResultCommand isCmd, Long ouId, String logId) {
        Long containerId = insideContainerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache inventoryStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache inventoryStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
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
    public InventoryStatisticResultCommand sysGuideSplitContainerPutawayCacheInventoryStatistic(ContainerCommand insideContainerCmd, Long ouId, String logId) {
        Long containerId = insideContainerCmd.getId();
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache inventoryStatistic start, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        String containerCode = insideContainerCmd.getCode();
        // 缓存所有库存
        List<String> codelist = new ArrayList<String>();
        codelist.add(containerCode);
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = null;
        invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerCode(ouId, codelist);
        if (null == invList || 0 == invList.size()) {
            log.error("sys guide splitContainer putaway container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, logId);
        isCmd.setInsideContainerId(containerId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide splitContainer putaway cache inventoryStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
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
    public List<WhSkuInventoryCommand> sysGuideSplitContainerPutawayCacheInventoryAndStatistic(ContainerCommand containerCmd, Long ouId, String logId) {
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
        InventoryStatisticResultCommand isCmd = inventoryStatisticManager.sysGuidePutawayInvStatistic(invList, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, ouId, logId);
        isCmd.setInsideContainerId(containerId);
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString(), isCmd, CacheConstants.CACHE_ONE_MONTH);
        if (log.isInfoEnabled()) {
            log.info("sys guide container putaway cache inventoryAndStatistic end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return invList;
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param locationIds
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideSplitContainerPutawayTipLocation0(ContainerCommand insideContainerCmd, Set<Long> locationIds, String logId) {
        Long containerId = insideContainerCmd.getId();
        Long tipLocationId = null;
        long len = cacheManager.listLen(CacheConstants.SCAN_LOCATION_QUEUE + containerId.toString());
        if (0 < len) {
            String insideContainerId = cacheManager.findListItem(CacheConstants.SCAN_LOCATION_QUEUE + containerId.toString(), 0);
            tipLocationId = new Long(insideContainerId);
        } else {
            // 随机取一个容器
            for (Long id : locationIds) {
                Long locId = id;
                if (null != locId) {
                    tipLocationId = locId;
                    cacheManager.pushToListHead(CacheConstants.SCAN_LOCATION_QUEUE + containerId.toString(), locId.toString());
                    break;
                }
            }
        }
        return tipLocationId;
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param locationId
     * @param locSkuAttrIds
     * @param logId
     * @return
     */
    @Override
    public String sysGuideSplitContainerPutawayTipSku0(ContainerCommand insideContainerCmd, Long locationId, Map<Long, Set<String>> locSkuAttrIds, String logId) {
        String tipSku = "";
        Long icId = insideContainerCmd.getId();
        Set<String> skuAttrIds = locSkuAttrIds.get(locationId);
        long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
        if (0 < skus) {
            String saId = cacheManager.findListItem(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString(), 0);
            tipSku = saId;
        } else {
            // 随机提示一个
            for (String sId : skuAttrIds) {
                if (!StringUtils.isEmpty(sId)) {
                    tipSku = sId;
                    cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString(), sId);
                    break;
                }
            }
        }
        return tipSku;
    }

    private boolean isCacheAllExists2(Set<String> ids, String cacheKey) {
        boolean allExists = true;
        long len = cacheManager.listLen(cacheKey);
        if (0 < len) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                for (int i = 0; i < new Long(len).intValue(); i++) {
                    String value = cacheManager.findListItem(cacheKey, i);
                    if (null == value) value = "-1";
                    if (value.equals(cId)) {
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
    
    private boolean isCacheAllExistsContain(Set<String> ids, String cacheKey) {
        boolean allExists = true;
        long len = cacheManager.listLen(cacheKey);
        if (0 < len) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                for (int i = 0; i < new Long(len).intValue(); i++) {
                    String value = cacheManager.findListItem(cacheKey, i);
                    if (null == value) value = "-1";
                    if (value.contains(cId)) {
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
     * @param ocCmd
     * @param icCmd
     * @param insideContainerIds
     * @param insideContainerSkuAttrIdsQty
     * @param insideContainerSkuAttrIdsSnDefect
     * @param insideContainerLocSkuAttrIds
     * @param locationId
     * @param skuCmd
     * @param logId
     * @return
     */
    @Override
    public CheckScanSkuResultCommand sysGuideSplitContainerPutawayTipSkuOrLocOrContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty,
            Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect, Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds, Long locationId, WhSkuCommand skuCmd, String logId) {
        CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
        Long ocId = null;
        Long icId = icCmd.getId();
        // 0.先判断当前内部容器是否在缓存中
        boolean icExists = false;
        for (Long iId : insideContainerIds) {
            if (0 == icId.compareTo(iId)) {
                icExists = true;
                break;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);// 当前内部容器中所有唯一sku
        Map<String, Set<String>> skuAttrIdsSnDefect = insideContainerSkuAttrIdsSnDefect.get(icId);// 唯一sku对应的所有sn残次信息
        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icId);// 库位对应的所有唯一sku
        if (null != ocCmd) {
            ocId = ocCmd.getId();
            // 1.当前的内部容器是不是提示容器队列的第一个
            long len = cacheManager.listLen(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
            if (0 < len) {
                String cacheIcId = cacheManager.findListItem(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString(), 0);// 队列的第一个
                if (!icId.toString().equals(cacheIcId)) {
                    log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan container queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

            }
            // 2.当前的库位是不是提示库位队列的第一个
            long locs = cacheManager.listLen(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
            if (0 < locs) {
                String cachelocId = cacheManager.findListItem(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString(), 0);// 队列的第一个
                if (!locationId.toString().equals(cachelocId)) {
                    log.error("tip location is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan location queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            // 3.当前的商品是不是提示商品队列的第一个
            String skuAttrId = "";
            String saId = "";
            Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
            Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
            boolean isSnLine = false;
            if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
                skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(), skuCmd.getInvAttr2(), skuCmd.getInvAttr3(),
                        skuCmd.getInvAttr4(), skuCmd.getInvAttr5(), skuCmd.getSkuSn(), skuCmd.getSkuDefect());
                isSnLine = true;
            } else {
                skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(), skuCmd.getInvAttr2(), skuCmd.getInvAttr3(),
                        skuCmd.getInvAttr4(), skuCmd.getInvAttr5());
                isSnLine = false;
            }
            long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
            if (0 < skus) {
                String cacheSkuAttrId = cacheManager.findListItem(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString(), 0);// 队列的第一个
                if (!skuAttrId.equals(cacheSkuAttrId)) {
                    log.error("tip sku is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan sku queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
            // 4.判断当前商品是否扫描完毕
            Double scanSkuQty = skuCmd.getScanSkuQty();
            boolean isSkuChecked = true;
            if (true == isSnLine) {
                // sn或残次商品
                String snDefect = SkuCategoryProvider.getSnDefect(skuAttrId);// 当前sn残次信息
                Set<String> allSnDefect = skuAttrIdsSnDefect.get(saId);// 唯一sku对应的所有sn残次信息
                // 判断所有sn残次信息是否都已扫描完毕
                String tipSkuAttrId = "";
                for (String sd : allSnDefect) {
                    if (snDefect.equals(sd)) {
                        continue;// 跳过当前的
                    }
                    String tempSkuAttrId = SkuCategoryProvider.concatSkuAttrId(saId, sd);
                    Set<String> tempSkuAttrIds = new HashSet<String>();
                    tempSkuAttrIds.add(tempSkuAttrId);
                    boolean isExists = isCacheAllExists2(tempSkuAttrIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                    if (true == isExists) {
                        continue;
                    } else {
                        isSkuChecked = false;
                        tipSkuAttrId = tempSkuAttrId;
                        break;
                    }
                }
                if (false == isSkuChecked) {
                    // 提示相同商品的下一个sn明细
                    cssrCmd.setNeedTipSkuSn(true);
                    cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                    return cssrCmd;
                }
            } else {
                // 非sn残次商品
                Long skuQty = skuAttrIdsQty.get(saId);
                if (null != scanSkuQty && (0 == new Long(scanSkuQty.longValue()).compareTo(skuQty))) {
                    isSkuChecked = true;
                } else {
                    isSkuChecked = false;
                }
                if (false == isSkuChecked) {
                    log.error("scan sku qty is not equals loc binding qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
                }
            }
            // 5.提示下一个商品
            Set<String> skuAttrIds = locSkuAttrIds.get(locationId);
            boolean isAllCache = isCacheAllExists2(skuAttrIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
            if (false == isAllCache) {
                // 提示下个商品
                String tipSkuAttrId = "";
                for (String sId : skuAttrIds) {
                    Set<String> tempSkuAttrIds = new HashSet<String>();
                    tempSkuAttrIds.add(sId);
                    boolean isExists = isCacheAllExists2(tempSkuAttrIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                    if (true == isExists) {
                        continue;
                    } else {
                        tipSkuAttrId = sId;
                        break;
                    }
                }
                cssrCmd.setNeedTipSku(true);
                cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                return cssrCmd;
            } else {
                // 判断是否需要提示下一个库位
                Set<Long> allLocIds = locSkuAttrIds.keySet();
                boolean isLocAllCache = isCacheAllExists(allLocIds, CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
                if (false == isLocAllCache) {
                    // 提示下一个库位
                    cssrCmd.setPutaway(true);// 可上架
                    Long tipLocationId = null;
                    for (Long lId : allLocIds) {
                        if (0 == locationId.compareTo(lId)) {
                            continue;
                        }
                        Set<Long> tempLocIds = new HashSet<Long>();
                        tempLocIds.add(lId);
                        boolean isExists = isCacheAllExists(tempLocIds, CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
                        if (true == isExists) {
                            continue;
                        } else {
                            tipLocationId = lId;
                            break;
                        }
                    }
                    cssrCmd.setNeedTipLoc(true);
                    cssrCmd.setTipLocId(tipLocationId);
                    return cssrCmd;
                } else {
                    // 判断是否需要提示下一个容器
                    Set<Long> allContainerIds = insideContainerIds;
                    boolean isAllContainerCache = isCacheAllExists(allContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
                    Long tipContainerId = null;
                    if (false == isAllContainerCache) {
                        // 提示下一个容器
                        cssrCmd.setPutaway(true);
                        for (Long cId : allContainerIds) {
                            if (0 == icId.compareTo(cId)) {
                                continue;
                            }
                            Set<Long> tempContainerIds = new HashSet<Long>();
                            tempContainerIds.add(cId);
                            boolean isExists = isCacheAllExists(tempContainerIds, CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
                            if (true == isExists) {
                                continue;
                            } else {
                                tipContainerId = cId;
                                break;
                            }
                        }
                        cssrCmd.setNeedTipContainer(true);
                        cssrCmd.setTipContainerId(tipContainerId);
                        return cssrCmd;
                    } else {
                        cssrCmd.setPutaway(true);// 可上架
                        return cssrCmd;
                    }
                }
            }
        } else {
            // 无外部容器
            // 1.当前的库位是不是提示库位队列的第一个
            long locs = cacheManager.listLen(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
            if (0 < locs) {
                String cachelocId = cacheManager.findListItem(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString(), 0);// 队列的第一个
                if (!locationId.toString().equals(cachelocId)) {
                    log.error("tip location is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan location queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            // 2.当前的商品是不是提示商品队列的第一个
            String skuAttrId = "";
            String saId = "";
            Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
            Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
            boolean isSnLine = false;
            if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
                skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(), skuCmd.getInvAttr2(), skuCmd.getInvAttr3(),
                        skuCmd.getInvAttr3(), skuCmd.getInvAttr4(), skuCmd.getInvAttr5(), skuCmd.getSkuSn(), skuCmd.getSkuDefect());
                isSnLine = true;
            } else {
                skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(), skuCmd.getInvAttr2(), skuCmd.getInvAttr3(),
                        skuCmd.getInvAttr3(), skuCmd.getInvAttr4(), skuCmd.getInvAttr5());
                isSnLine = false;
            }
            long skus = cacheManager.listLen(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
            if (0 < skus) {
                String cacheSkuAttrId = cacheManager.findListItem(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString(), 0);// 队列的第一个
                if (!skuAttrId.equals(cacheSkuAttrId)) {
                    log.error("tip sku is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan sku queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
            // 3.判断当前商品是否扫描完毕
            Double scanSkuQty = skuCmd.getScanSkuQty();
            boolean isSkuChecked = true;
            if (true == isSnLine) {
                // sn或残次商品
                String snDefect = SkuCategoryProvider.getSnDefect(skuAttrId);// 当前sn残次信息
                Set<String> allSnDefect = skuAttrIdsSnDefect.get(saId);// 唯一sku对应的所有sn残次信息
                // 判断所有sn残次信息是否都已扫描完毕
                String tipSkuAttrId = "";
                for (String sd : allSnDefect) {
                    if (snDefect.equals(sd)) {
                        continue;// 跳过当前的
                    }
                    String tempSkuAttrId = SkuCategoryProvider.concatSkuAttrId(saId, sd);
                    Set<String> tempSkuAttrIds = new HashSet<String>();
                    tempSkuAttrIds.add(tempSkuAttrId);
                    boolean isExists = isCacheAllExists2(tempSkuAttrIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                    if (true == isExists) {
                        continue;
                    } else {
                        isSkuChecked = false;
                        tipSkuAttrId = tempSkuAttrId;
                        break;
                    }
                }
                if (false == isSkuChecked) {
                    // 提示相同商品的下一个sn明细
                    cssrCmd.setNeedTipSkuSn(true);
                    cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                    return cssrCmd;
                }
            } else {
                // 非sn残次商品
                Long skuQty = skuAttrIdsQty.get(saId);
                if (null != scanSkuQty && (0 == new Long(scanSkuQty.longValue()).compareTo(skuQty))) {
                    isSkuChecked = true;
                } else {
                    isSkuChecked = false;
                }
                if (false == isSkuChecked) {
                    log.error("scan sku qty is not equals loc binding qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
                }
            }
            // 4.提示下一个商品
            Set<String> skuAttrIds = locSkuAttrIds.get(locationId.toString());
            boolean isAllCache = isCacheAllExists2(skuAttrIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
            if (false == isAllCache) {
                // 提示下个商品
                String tipSkuAttrId = "";
                for (String sId : skuAttrIds) {
                    Set<String> tempSkuAttrIds = new HashSet<String>();
                    tempSkuAttrIds.add(sId);
                    boolean isExists = isCacheAllExists2(tempSkuAttrIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                    if (true == isExists) {
                        continue;
                    } else {
                        tipSkuAttrId = sId;
                        break;
                    }
                }
                cssrCmd.setNeedTipSku(true);
                cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                return cssrCmd;
            } else {
                // 判断是否需要提示下一个库位
                Set<Long> allLocIds = locSkuAttrIds.keySet();
                boolean isLocAllCache = isCacheAllExists(allLocIds, CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
                if (false == isLocAllCache) {
                    // 提示下一个库位
                    cssrCmd.setPutaway(true);// 可上架
                    Long tipLocationId = null;
                    for (Long lId : allLocIds) {
                        if (0 == locationId.compareTo(lId)) {
                            continue;
                        }
                        Set<Long> tempLocIds = new HashSet<Long>();
                        tempLocIds.add(lId);
                        boolean isExists = isCacheAllExists(tempLocIds, CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
                        if (true == isExists) {
                            continue;
                        } else {
                            tipLocationId = lId;
                            break;
                        }
                    }
                    cssrCmd.setNeedTipLoc(true);
                    cssrCmd.setTipLocId(tipLocationId);
                    return cssrCmd;
                } else {
                    cssrCmd.setPutaway(true);// 可上架
                    return cssrCmd;
                }
            }
        }
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerCmd
     * @param isAfterPutawayTipContainer
     * @param isAfterPutawayTipLoc
     * @param locationId
     * @param logId
     */
    @Override
    public void sysGuideSplitContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, Boolean isAfterPutawayTipLoc, Long locationId, String logId) {
        if (null != containerCmd) {
            Long ocId = containerCmd.getId();
            if (false == isAfterPutawayTipContainer && false == isAfterPutawayTipLoc) {
                // 0.先清除所有复核商品队列及库位队列及内部库存及统计信息
                ContainerStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocId.toString());
                if (null != isCmd) {
                    Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
                    for (Long icId : insideContainerIds) {
                        cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                        cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
                        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
                    }
                }
                // 1.再清除所有提示容器队列
                cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
                // 2.清除所有内部容器统计信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_STATISTIC, ocId.toString());

            } else {
                Long icId = insideContainerCmd.getId();
                // 0.清除商品队列或库位队列
                if (true == isAfterPutawayTipLoc) {
                    // 清除商品队列
                    cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                }
                if (true == isAfterPutawayTipContainer) {
                    // 清除商品队列及库位队列
                    cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                    cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());

                }
                // 1.清除所有库存统计信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                // 2.清除所有库存缓存信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
            }
        } else {
            Long icId = insideContainerCmd.getId();
            // 0.清除商品队列或库位队列
            if (true == isAfterPutawayTipLoc) {
                // 清除商品队列
                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
            }
            if (true == isAfterPutawayTipContainer) {
                // 清除商品队列及库位队列
                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
                cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());

            }
            // 1.清除所有库存统计信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
            // 2.清除所有库存缓存信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
        }
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param locationId
     * @param locSkuAttrIds
     * @param skuAttrId
     * @param logId
     * @return
     */
    @Override
    public String sysGuideSplitContainerPutawayTipSku(ContainerCommand insideContainerCmd, Long locationId, Map<Long, Set<String>> locSkuAttrIds, String skuAttrId, String logId) {
        Long icId = insideContainerCmd.getId();
        String tipSkuAttrId = skuAttrId;
        // 0.先判断提示的sku是否存在当前绑定的库位上
        Set<String> skuAttrIds = locSkuAttrIds.get(locationId);
        boolean isExists = false;
        for (String saId : skuAttrIds) {
            if (skuAttrId.equals(saId)) {
                isExists = true;
                break;
            }
        }
        if (false == isExists) {
            log.error("tip skuAttrId is not binding loc error, locId is:[{}], tipSkuAttrId is:[{}], logId is:[{}]", locationId, skuAttrId, logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        cacheManager.pushToListHead(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString(), skuAttrId);
        return tipSkuAttrId;
    }

    /**
     * @author lichuan
     * @param insideContainerCmd
     * @param locationIds
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideSplitContainerPutawayTipLocation(ContainerCommand insideContainerCmd, Set<Long> locationIds, Long locationId, String logId) {
        Long icId = insideContainerCmd.getId();
        Long tipLocId = locationId;
        // 0.先判断提示的库位是否在已绑定的库位内
        boolean isExists = false;
        for (Long lId : locationIds) {
            if (0 == locationId.compareTo(lId)) {
                isExists = true;
                break;
            }
        }
        if (false == isExists) {
            log.error("tip locationId is not in binding locs, tipLocId is:[{}], allLocs is:[{}], logId is:[{}]", locationId, locationIds, logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        cacheManager.pushToListHead(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString(), locationId.toString());
        return tipLocId;
    }

    /**
     * @author lichuan
     * @param containerCmd
     * @param insideContainerIds
     * @param containerId
     * @param logId
     * @return
     */
    @Override
    public Long sysGuideSplitContainerPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Long containerId, String logId) {
        Long tipContainerId = null;
        if (null != containerCmd) {
            Long ocId = containerCmd.getId();
            tipContainerId = containerId;
            // 0.先判断提示的容器是否在内部容器列表里
            boolean isExists = false;
            for (Long iId : insideContainerIds) {
                if (0 == containerId.compareTo(iId)) {
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("tip container is not exists in insideContainers, tipContainerId is:[{}], insideContainerIds is:[{}], logId is:[{}]", containerId, insideContainerIds, logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            cacheManager.pushToListHead(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString(), containerId.toString());
        }
        return tipContainerId;
    }
}
