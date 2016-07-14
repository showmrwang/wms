package com.baozun.scm.primservice.whoperation.constant;

public class CacheKeyConstant {
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
    @Deprecated
    public static final String CACHE_RCVD_CONTAINER = "CACHE_RCVD_CONTAINER";

    /** 容器商品占用缓存 */
    public static final String CACHE_RCVD_CONTAINER_USER_PREFIX = "CACHE_RCVD_CONTAINER_USER_";

    /** 容器商品属性缓存前缀 规则：前缀+containerId */
    public static final String CACHE_RCVD_CONTAINER_PREFIX = "CACHE_RCVD_CONTAINER_";

    public static final String CACHE_RCVD_PALLET_PREFIX = "CACHE_RCVD_PALLET_";
}
