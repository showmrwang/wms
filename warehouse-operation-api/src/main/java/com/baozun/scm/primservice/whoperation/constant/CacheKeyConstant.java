package com.baozun.scm.primservice.whoperation.constant;

public final class CacheKeyConstant {
    private CacheKeyConstant() {}

    /** 通用收货ASNLINE缓存前缀：规则：前缀+ASNID+ASNLINEID+SKUID;间隔符"_" */
    public static final String CACHE_ASNLINE_SKU_PREFIX = "CACHE_ASNLINE_SKU_";

    /** 通用收货SKU数量缓存前缀：规则：前缀+ASNID+SKUID;间隔符"_" */
    public static final String CACHE_ASN_SKU_PREFIX = "CACHE_ASN_SKU_";

    /** 通用收货ASN超收比例前缀：规则：前缀+ASNID;间隔符"_" */
    public static final String CACHE_ASN_OVERCHARGE_PREFIX = "CACHE_ASN_OVERCHARGE_";

    /** 通用收货ASN缓存 */
    public static final String CACHE_ASN_PREFIX = "CACHE_ASN_";

    /** 通用收货ASNLINE缓存 规则:前缀+ASNID */
    public static final String CACHE_ASNLINE_PREFIX = "CACHE_ASNLINE_";

    /** 超收比例 */
    public static final String CACHE_ASN_OVERCHARGE = "CACHE_ASN_OVERCHARGE";
    public static final String CACHE_STORE_ASN_OVERCHARGE = "CACHE_STORE_ASN_OVERCHARGE";
    public static final String CACHE_STORE_PO_OVERCHARGE = "CACHE_STORE_PO_OVERCHARGE";
    public static final String CACHE_WAREHOUSE_ASN_OVERCHARGE = "CACHE_WAREHOUSE_ASN_OVERCHARGE";
    public static final String CACHE_WAREHOUSE_PO_OVERCHARGE = "CACHE_WAREHOUSE_PO_OVERCHARGE";

    // --------------------------------------------------------------------------------------
    /** 收货等操作用户列表 */
    public static final String CACHE_OPERATOR_USER = "CACHE_OPERATOR_USER";

    /** 收货缓存数据集合 */
    public static final String CACHE_RCVD_PREFIX = "CACHE_RCVD_";

    /** 通用收货ASN的Sn号缓存 */
    public static final String CACHE_RCVD_SN_PREFIX = "CACHE_RCVD_SN_";

    /** 明细超收数量 */
    public static final String CACHE_ASNLINE_OVERCHARGE_PREFIX = "CACHE_ASNLINE_OVERCHARGE_";

    /** 容器商品占用缓存 */
    public static final String CACHE_RCVD_CONTAINER = "CACHE_RCVD_CONTAINER";

    /** 容器商品占用缓存 */
    public static final String CACHE_RCVD_CONTAINER_USER_PREFIX = "CACHE_RCVD_CONTAINER_USER_";

    /** 容器商品属性缓存前缀 规则：前缀+containerId */
    public static final String CACHE_RCVD_CONTAINER_PREFIX = "CACHE_RCVD_CONTAINER_";

    public static final String CACHE_RCVD_PALLET_PREFIX = "CACHE_RCVD_PALLET_";

    /** 商品条码对应SKUID缓存 前缀+barcode */
    public static final String WMS_CACHE_SKU_BARCODE = "WMS_CACHE_SKU_BARCODE_";

    /** 商品主档信息缓存 前缀+SKUID+OUID */
    public static final String WMS_CACHE_SKU_MASTER = "WMS_CACHE_SKU_MASTER_";


    /**
     * 容器对应skuId缓存:根据容器号查询出库存，再根据库存查询该库存对应的有哪些sku_id,然后将sku_id存入缓存中
     */
    public static final String CACHE_MMP_CONTAINER_CODE_PREFIX = "CACHE_MMP_CONTAINER_CODE_PREFIX_";

    /** 系统参数缓存 前缀+groupValue+dicValue Dictionary */
    public static final String WMS_CACHE_SYS_DICTIONARY = "WMS_CACHE_SYS_DICTIONARY_";
    
    
    
    //-------------------------------------------------------------------------------
    
    /**
     * 人为指定上架:容器信息缓存
     */
    public static final String CACHE_MMP_CONTAINER_ID_PREFIX = "CACHE_MMP_CONTAINER_ID_PREFIX";
    
    
    

    /** 客户缓存 前缀+ customerId */
    public static final String WMS_CACHE_CUSTOMER = "WMS_CACHE_CUSTOMER_";

    // 过期时间
    public static final int CACHE_ONE_SECOND = 1;
    public static final int CACHE_ONE_MINUTE = 60;
    public static final int CACHE_THIRTY_MINUTE = 30 * 60;
    public static final int CACHE_ONE_HOUR = 60 * 60;
    public static final int CACHE_ONE_DAY = 1 * 24 * 60 * 60;
    public static final int CACHE_ONE_WEEK = 7 * 24 * 60 * 60;
    public static final int CACHE_ONE_MONTH = 30 * 24 * 60 * 60;
    public static final int CACHE_ONE_YEAR = 365 * 24 * 60 * 60;
}
