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
    public static final int CACHE_THIRTY_MINUTE = 30 * 60;
    public static final int CACHE_ONE_HOUR = 60 * 60;
    public static final int CACHE_ONE_DAY = 1 * 24 * 60 * 60;
    public static final int CACHE_ONE_WEEK = 7 * 24 * 60 * 60;
    public static final int CACHE_ONE_MONTH = 30 * 24 * 60 * 60;
    public static final int CACHE_ONE_YEAR = 365 * 24 * 60 * 60;

    /** 库位推荐队列 */
    public static final String LOCATION_RECOMMEND_QUEUE = "LOCATION_RECOMMEND_QUEUE";
    /** 库位推荐排队过期 */
    public static final String LOCATION_RECOMMEND_EXPIRE_TIME = "LOCATION_RECOMMEND_EXPIRE_TIME";
    /** 库位推荐排队时间 */
    public static final String LOCATION_RECOMMEND_VALID_TIME = "LOCATION_RECOMMEND_VALID_TIME";
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

    public static final String SCAN_SKU_QUEUE_SN = "SCAN_SKU_QUEUE_SN";
    
    /** 人工上架:扫sn */
    public static final String PDA_MAN_MANDE_SCAN_SKU_SN = "MAN_MANDE_SCAN_SKU_SN";
    /** 人工上架:扫sn/残次 */
    public static final String PDA_MAN_MANDE_SCAN_SKU_SN_DEFECT = "PDA_MAN_MANDE_SCAN_SKU_SN_DEFECT";

    /** 人工上架:扫SKU残次信息 */
    public static final String PDA_MAN_MANDE_SCAN_SKU_DEFECT = "MAN_MANDE_SCAN_SKU_DEFECT";
    /** 人工上架:容器库存缓存 */
    public static final String PDA_MAN_MANDE_CONTAINER_INVENTORY = "MAN_MANDE_CONTAINER_INVENTORY";
    /** 人工上架:容器库存统计缓存 */
    public static final String PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC = "MAN_MANDE_CONTAINER_INVENTORY_STATISTIC";
    /** 人工上架:已扫描的容器队列 */
    public static final String PDA_MAN_MANDE_SCAN_CONTAINER_QUEUE = "MAN_MANDE_SCAN_CONTAINER_QUEUE_";
    /** 人工上架:已扫描的Sku队列 */
    public static final String PDA_MAN_MANDE_SCAN_SKU_QUEUE = "MAN_MANDE_SCAN_SKU_QUEUE_";
    /** 人工上架:容器缓存 */
    public static final String PDA_MAN_MANDE_CONTAINER = "MAN_MANDE_CONTAINER";
    /** 人工上架:容器统计缓存 */
    public static final String PDA_MAN_MANDE_CONTAINER_STATISTIC = "MAN_MANDE_CONTAINER_STATISTIC";

    // pda拣货
    /*** pda拣货、补货和库内移动，作业明细统计 */
    public static final String OPERATIONLINE_STATISTICS = "OPERATIONLINE_STATISTICS";
    /*** pda补货和库内移动，作业执行明细统计 */
    public static final String OPERATIONEXEC_STATISTICS = "OPERATIONEXEC_STATISTICS";


    /*** 缓存库位 */
    public static final String CACHE_LOCATION = "CACHE_LOCATION";
    /*** 缓存单个库位库存 */
    public static final String CACHE_LOC_INVENTORY = "CACHE_LOC_INVENTORY";
    // /***缓存唯一sku*/
    // public static final String CACHE_LOC_SKU_ATTR = "CACHE_LOC_SKU_ATTR";
    /** pda拣货:已扫描的Sku队列 */
    public static final String PDA_PICKING_SCAN_SKU_QUEUE = "PDA_PICKING_SCAN_SKU_QUEUE_";

    public static final String OPERATION_LINE = "OPERATION_LINE";

    /** 满箱作业处理 */
    public static final String CACHE_OPERATION_LINE = "CACHE_OPERATION_LINE";

    /** 补货上架缓存库位 */
    public static final String CACHE_PUTAWAY_LOCATION = "CACHE_PUTAWAY_LOCATION";
    /** 补货上架缓存周转箱 */
    public static final String CACHE_TURNOVERBOX_IDS = "CACHE_TURNOVERBOX_IDS";
    /** 缓存作业执行明细统计信息 */
    public static final String CACHE_OPERATION_EXEC_LINE = "CACHE_OPERATION_EXEC_LINE";

    /************************************* 播种缓存 ***********************************/

    /** 播种商品数据缓存 */
    public static final String CACHE_SEEDING = "SEEDING";
    /** odo出库单绑定货格 */
    public static final String CACHE_SEEDING_ODO_BIND_LATTICE = "SEEDING-ODO-BIND-LATTICE";
    /** 播种功能缓存 */
    public static final String CACHE_SEEDING_FUNCTION = "SEEDING-FUNCTION";
    /** 播种货格信息 */
    public static final String CACHE_SEEDING_LATTICE = "SEEDING-LATTICE";
    /** 货格绑定出库箱缓存 */
    public static final String CACHE_SEEDING_LATTICE_CURRENT_BIND_OUTBOUNDBOX = "SEEDING-LATTICE-CURRENT-BING-OUTBOUNDBOX";
    /** 货格绑定的所有出库箱 */
    public static final String CACHE_SEEDING_LATTICE_BIND_OUTBOUNDBOX_BOTH = "SEEDING-LATTICE-BING-OUTBOUNDBOX-BOTH";
    /** 出库箱装入的明细 */
    public static final String CACHE_SEEDING_OUTBOUNDBOX_COLLECTION_LINE = "SEEDING-LATTICE-OUTBOUNDBOX-COLLECTION-LINE";
    /** 货格装入的明细 */
    public static final String CACHE_SEEDING_LATTICE_COLLECTION_LINE = "SEEDING-LATTICE-LATTICE-COLLECTION-LINE";
    /** 周转箱中的明细 */
    public static final String CACHE_SEEDING_TURNOVERBOX_COLLECTION_LINE = "SEEDING-TURNOVERBOX-COLLECTION-LINE";



    // --------------------------------------------集货缓存--------------------------------------------
    /** 集货推荐结果缓存:前缀+USERID */
    public static final String PDA_CACHE_COLLECTION_REC = "CACHE_COLLECTION_REC";
    /** 人为集货扫描容器缓存 */
    public static final String PDA_CACHE_MANUAL_COLLECTION_CONTAINER = "CACHE_MANUAL_COLLECTION_CONTAINER";
    /** 未获得推荐结果的容器列表缓存:前缀+USERID */
    public static final String PDA_CACHE_COLLECTION_REC_FAIL = "CACHE_COLLECTION_REC_FAIL";
    /** 人为集货-集货推荐结果缓存 */
    public static final String PDA_CACHE_MANUAL_COLLECTION_REC = "CACHE_MANUAL_COLLECTION_REC";


}
