package com.baozun.scm.primservice.whoperation.constant;

public class CacheKeyConstant {
    private CacheKeyConstant() {}

    /** 通用收货ASN缓存Version控制前缀：规则：前缀+ASNID */
    // public static final String CACHE_ASN_VERSION_PREFIX = "CACHE_ASN_VERSION_";

    /** 通用收货ASN缓存前缀：规则：前缀+ASNID */
    public static final String CACHE_ASN_PREFIX = "CACHE_ASN_";

    /** 通用收货ASNLINE缓存前缀：规则：前缀+ASNID+ASNLINEID;间隔符"_" */
    public static final String CACHE_ASNLINE_PREFIX = "CACHE_ASNLINE_";

    /** 通用收货ASNLINE缓存前缀：规则：前缀+ASNID+ASNLINEID+SKUID;间隔符"_" */
    public static final String CACHE_ASNLINE_SKU_PREFIX = "CACHE_ASNLINE_SKU_";

    /** 通用收货SKU数量缓存前缀：规则：前缀+ASNID+SKUID;间隔符"_" */
    public static final String CACHE_ASN_SKU_PREFIX = "CACHE_ASN_SKU_";

    /** 通用收货SKU计划数量缓存前缀：规则：前缀+ASNID+SKUID;间隔符"_" */
    public static final String CACHE_ASN_SKU_QTY_PREFIX = "CACHE_ASN_SKU_QTY_";

    /** 通用收货SKU-明细对应关系缓存前缀：规则：前缀+ASNID+SKUID;间隔符"_" */
    public static final String CACHE_ASN_SKU_ASN_LINE_PREFIX = "CACHE_ASN_SKU_ASNLINE";

    /** 通用收货ASN超收比例前缀：规则：前缀+ASNID;间隔符"_" */
    public static final String CACHE_ASN_OVERCHARGE_PREFIX = "CACHE_ASN_OVERCHARGE_";
}
