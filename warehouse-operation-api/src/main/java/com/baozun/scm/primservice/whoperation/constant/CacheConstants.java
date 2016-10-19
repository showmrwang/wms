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
package com.baozun.scm.primservice.whoperation.constant;

/**
 * @author lichuan
 *
 */
public final class CacheConstants {
    private CacheConstants() {}
    
    public static final int CACHE_ONE_SECOND = 1;
    public static final int CACHE_ONE_MINUTE = 60;
    public static final int CACHE_THIRTY_MINUTE = 30*60;
    public static final int CACHE_ONE_HOUR = 60*60;
    public static final int CACHE_ONE_DAY = 1*24*60*60;
    public static final int CACHE_ONE_WEEK = 7*24*60*60;
    public static final int CACHE_ONE_MONTH = 30*24*60*60;
    public static final int CACHE_ONE_YEAR = 365*24*60*60;
    
    /** 库位推荐队列 */
    public static final String LOCATION_RECOMMEND_QUEUE = "LOCATION_RECOMMEND_QUEUE";
    /** 库存缓存 */
    public static final String INVENTORY = "INVENTORY";
    /** 容器库存缓存 */
    public static final String CONTAINER_INVENTORY = "CONTAINER_INVENTORY";
    /** 容器库存统计缓存 */
    public static final String CONTAINER_INVENTORY_STATISTIC = "CONTAINER_INVENTORY_STATISTIC";
    /** 已扫描的容器队列 */
    public static final String SCAN_CONTAINER_QUEUE = "SCAN_CONTAINER_QUEUE_";
    /** 已扫描的Sku队列 */
    public static final String SCAN_SKU_QUEUE = "SCAN_SKU_QUEUE_";
    /** 容器缓存 */
    public static final String CONTAINER = "CONTAINER";
    /** 容器统计缓存 */
    public static final String CONTAINER_STATISTIC = "CONTAINER_STATISTIC";
    /** 已扫描的库位队列 */
    public static final String SCAN_LOCATION_QUEUE = "SCAN_LOCATION_QUEUE_";
    /** 缓存库位推荐队列 */
    public static final String LOCATION_RECOMMEND = "LOCATION_RECOMMEND";
    
    /**人工上架:扫sn*/
    public static final String PDA_MAN_MANDE_SCAN_SKU_SN = "PDA_MAN_MANDE_SCAN_SKU_SN";
    
    /**人工上架:扫SKU残次信息*/
    public static final String PDA_MAN_MANDE_SCAN_SKU_DEFECT = "PDA_MAN_MANDE_SCAN_SKU_DEFECT";
}
