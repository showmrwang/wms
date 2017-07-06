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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.statis.InventoryStatisticManager;

/**
 * @author feng.hu
 *
 */
@Service("pdaContainerMoveCacheManager")
@Transactional
public class PdaContainerMoveCacheManagerImpl extends BaseManagerImpl implements PdaContainerMoveCacheManager {

protected static final Logger log = LoggerFactory.getLogger(PdaContainerMoveCacheManagerImpl.class);
    
    @Autowired
    InventoryStatisticManager inventoryStatisticManager;    
    @Autowired
    private CacheManager cacheManager;
    
    
    /**
     * pda出库箱整箱移动复核sku并缓存及提示后续操作判断
     * @author feng.hu
     * @param sourceContainerCode
     * @param insideContainerSkuIds
     * @param insideContainerSkuIdsQty
     * @param skuCmd
     * @param scanPattern
     * @param logId
     * @return
     */
    @Override
    public CheckScanSkuResultCommand sysContainerSplitMoveCacheSkuAndCheck(String sourceContainerCode, Map<String, Set<String>> insideContainerSkuAttrIds,
                                        Map<String, Long> skuAttrIdsQty,Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect, WhSkuCommand skuCmd, String logId){        
        if (log.isInfoEnabled()) {
            log.info("sysOutBoundboxContainerSplitMoveCacheSkuAndCheck start, sourceContainerCode is:[{}], barCode is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, (null != skuCmd ? skuCmd.getBarCode() : ""),logId});
        }        
        //返回的对象
        CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
        
        Map<String, Set<String>> skuAttrIdsSnDefect = insideContainerSkuAttrIdsSnDefect.get(sourceContainerCode);// 唯一sku对应的所有sn残次信息
        
        // 1.当前的商品是不是提示商品队列的第一个
        String skuAttrId = "";
        String saId = "";
        Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
        Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
        boolean isSnLine = false;
        if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
            skuAttrId = SkuCategoryProvider.concatSkuAttrId(SkuCategoryProvider.getSkuAttrIdBySkuCmd(skuCmd), skuCmd.getSkuSn(), skuCmd.getSkuDefect());
            isSnLine = true;
        } else {
            skuAttrId = SkuCategoryProvider.getSkuAttrIdBySkuCmd(skuCmd);
            isSnLine = false;
        }
        saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
        Double scanSkuQty = skuCmd.getScanSkuQty();        
        TipScanSkuCacheCommand tipSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode);
        ArrayDeque<String> tipSkuAttrIds = null;
        if (null != tipSkuCmd) {
            tipSkuAttrIds = tipSkuCmd.getScanSkuAttrIds();
        }
        if (null != tipSkuAttrIds && !tipSkuAttrIds.isEmpty()) {
            String value = tipSkuAttrIds.peekFirst();
            if (!skuAttrId.equals(value)) {
                log.error("tip sku is not queue first element exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            } else {
                // 插入扫描列表，如果扫描数量与提示数量相同则执行部分移动
                ScanSkuCacheCommand tipScanSkuSnCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + saId);
                if (null != tipScanSkuSnCmd) {
                    List<String> skuSnAttrIds = tipScanSkuSnCmd.getScanSkuAttrIds();
                    skuSnAttrIds.add(skuAttrId);
                    tipScanSkuSnCmd.setScanSkuAttrIds(skuSnAttrIds);
                    cacheManager.setObject(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + saId, tipScanSkuSnCmd, CacheConstants.CACHE_ONE_DAY);
                } else {
                    tipScanSkuSnCmd = new ScanSkuCacheCommand();                    
                    List<String> skuSnAttrIds = new ArrayList<String>();
                    skuSnAttrIds.add(skuAttrId);
                    tipScanSkuSnCmd.setScanSkuAttrIds(skuSnAttrIds);
                    cacheManager.setObject(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + saId, tipScanSkuSnCmd, CacheConstants.CACHE_ONE_DAY);
                }
                if (true == isSnLine) {
                    long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + saId, 1);
                    if (cacheValue == scanSkuQty.longValue()) {
                        //部分移动SN商品，当扫描商品数和SN数量一致时提交一次
                        cssrCmd.setPartlyPutaway(true);
                    }
                } else {
                    long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + saId, scanSkuQty.intValue());
                    if (cacheValue == scanSkuQty.longValue()) {
                        //部分移动非SN商品，扫描一次就提交一次
                        cssrCmd.setPartlyPutaway(true);
                    }
                }
            }
        } else {
            log.error("scan sku queue is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        // 2.判断当前商品是否扫描完毕
        boolean isSkuChecked = true;
        //缓存中的数量
        Long sourceSkuQty = skuAttrIdsQty.get(saId);
        if (true == isSnLine) {
            long value = 0L;
            // 取到扫描的数量
            String cacheQty = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + saId);
            if (!StringUtils.isEmpty(cacheQty)) {
                value = new Long(cacheQty).longValue();
            }
            if ((value + 1) > sourceSkuQty.longValue()) {
                log.error("sku scan qty has already more than loc binding qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", saId, (value + scanSkuQty.longValue()), sourceSkuQty, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_ERROR, new Object[] {sourceContainerCode, saId});
            }
            long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + saId, 1);
            // sn或残次商品
            String snDefect = SkuCategoryProvider.getSnDefect(skuAttrId);// 当前sn残次信息
            Set<String> allSnDefect = skuAttrIdsSnDefect.get(saId);// 唯一sku对应的所有sn残次信息
            // 判断所有sn残次信息是否都已扫描完毕
            String tipSkuAttrId = "";
            for (String sd : allSnDefect) {
                if (snDefect.equals(SkuCategoryProvider.getSnDefect(sd))) {
                    continue;// 跳过当前的
                }
                Set<String> tempSkuAttrIds = new HashSet<String>();
                tempSkuAttrIds.add(sd);
                boolean isExists = isCacheAllExists2(tempSkuAttrIds, tipSkuAttrIds);
                if (true == isExists) {
                    continue;
                } else {
                    isSkuChecked = false;
                    tipSkuAttrId = sd;
                    break;
                }
            }
            if (false == isSkuChecked) {
                cssrCmd.setScanSkuQty(cacheValue);// 已扫描数量
                // 提示相同商品的下一个sn明细
                if(cssrCmd.isPartlyPutaway()){
                    cssrCmd.setNeedTipSku(true); 
                }else{
                    cssrCmd.setNeedTipSkuSn(true);
                }
                cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                log.info("sysOutBoundboxContainerSplitMoveCacheSkuAndCheck end, next sn info tipSkuAttrId is:[{}],  logId is:[{}]", new Object[] {tipSkuAttrId,logId});
                return cssrCmd;
            }
        } else {
            // 非sn残次商品            
            long value = 0L;
            // 取到扫描的数量
            String cacheQty = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + saId);
            if (!StringUtils.isEmpty(cacheQty)) {
                value = new Long(cacheQty).longValue();
            }
            if ((value + scanSkuQty.longValue()) > sourceSkuQty.longValue()) {
                log.error("sku scan qty has already more than loc binding qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", saId, value + scanSkuQty.longValue(), sourceSkuQty, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_ERROR, new Object[] {sourceContainerCode, saId});
            }
            long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + saId, scanSkuQty.intValue());
            if (cacheValue < sourceSkuQty.longValue()) {
                // 提示相同商品
                cssrCmd.setScanSkuQty(cacheValue);// 已扫描数量
                cssrCmd.setNeedTipSku(true);
                cssrCmd.setTipSkuAttrId(saId);
                cssrCmd.setTipSameSkuAttrId(true);
                log.info("sysOutBoundboxContainerSplitMoveCacheSkuAndCheck end, some sku info cacheValue is:[{}],  logId is:[{}]", new Object[] {cacheValue,logId});
                return cssrCmd;
            }
        }
        // 4.提示下一个商品
        Set<String> skuAttrIds = insideContainerSkuAttrIds.get(sourceContainerCode);
        //获取有sn信息的所有skuAttrIdsSnDef
        Set<String> skuAttrIdsSn = formatSkuSnInfo(skuAttrIds, skuAttrIdsSnDefect); 
        //判断所有的商品是否全部扫描完成
        boolean isAllCache = isCacheAllExists2(skuAttrIdsSn, tipSkuAttrIds);
        if (false == isAllCache) {
            // 提示下个商品
            String tipSkuAttrId = "";
            for (String sId : skuAttrIdsSn) {
                Set<String> tempSkuAttrIds = new HashSet<String>();
                tempSkuAttrIds.add(sId);
                boolean isExists = isCacheAllExists2(tempSkuAttrIds, tipSkuAttrIds);
                if (true == isExists) {
                    continue;
                } else {
                    tipSkuAttrId = sId;
                    break;
                }
            }
            cssrCmd.setNeedTipSku(true);
            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
            log.info("sysOutBoundboxContainerSplitMoveCacheSkuAndCheck end, next sku info tipSkuAttrId is:[{}],  logId is:[{}]", new Object[] {tipSkuAttrId,logId});
            return cssrCmd;
        } else {
                cssrCmd.setPutaway(true);
                cssrCmd.setNeedTipLoc(true);
        }
        log.info("sysOutBoundboxContainerSplitMoveCacheSkuAndCheck end, finished. logId is:[{}]", new Object[] {logId});
        return cssrCmd;
    } 
    
    private boolean isCacheAllExists2(Set<String> ids, ArrayDeque<String> cacheKeys) {
        boolean allExists = true;
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                Iterator<String> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    String value = iter.next();
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
    
    private Set<String> formatSkuSnInfo(Set<String> skuAttrIds, Map<String, Set<String>> skuAttrIdsSnMap) {
        if (null == skuAttrIdsSnMap || skuAttrIdsSnMap.size() == 0) {
            return skuAttrIds;
        }
        Set<String> skuAttrSnResult = new HashSet<String>();
        for (String skuAttrId : skuAttrIds) {
            if (StringUtils.isEmpty(skuAttrId)) {
                continue;
            }
            Set<String> skuAttrSnSet = skuAttrIdsSnMap.get(skuAttrId);
            if (null == skuAttrSnSet || skuAttrSnSet.size() == 0) {
                skuAttrSnResult.add(skuAttrId);
            } else {
                skuAttrSnResult.addAll(skuAttrSnSet);
            }
        }
        return skuAttrSnResult;
    }

}
