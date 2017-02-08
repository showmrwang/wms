package com.baozun.scm.primservice.whoperation.constant;

public final class CacheKeyConstant {
    private CacheKeyConstant() {}

    /** 通用收货ASNLINE缓存前缀：规则：前缀+ASNID+ASNLINEID+SKUID;间隔符"_" */
    public static final String CACHE_ASNLINE_SKU_PREFIX = "%CACHE-ASNLINE-SKU$";

    /** 通用收货SKU数量缓存前缀：规则：前缀+ASNID+SKUID;间隔符"_" */
    public static final String CACHE_ASN_SKU_PREFIX = "%CACHE-ASN-SKU$";

    /** 通用收货ASN超收比例前缀：规则：前缀+ASNID;间隔符"_" */
    public static final String CACHE_ASN_OVERCHARGE_PREFIX = "%CACHE-ASN-OVERCHARGE$";

    /** 通用收货ASN缓存 prefix+asnId */
    public static final String CACHE_ASN_PREFIX = "%CACHE-ASN$";

    /** 通用收货PO缓存 prefix+poId */
    public static final String CACHE_PO_PREFIX = "%CACHE-PO$";

    /** 通用收货ASNLINE缓存 规则:前缀+ASNID */
    public static final String CACHE_ASNLINE_PREFIX = "%CACHE-ASNLINE$";

    /** 超收比例 */
    public static final String CACHE_ASN_OVERCHARGE = "%CACHE-ASN-OVERCHARGE$";
    public static final String CACHE_STORE_ASN_OVERCHARGE = "%CACHE-STORE-ASN-OVERCHARGE$";
    public static final String CACHE_STORE_PO_OVERCHARGE = "%CACHE-STORE-PO-OVERCHARGE$";
    public static final String CACHE_WAREHOUSE_ASN_OVERCHARGE = "%CACHE-WAREHOUSE-ASN-OVERCHARGE$";
    public static final String CACHE_WAREHOUSE_PO_OVERCHARGE = "%CACHE-WAREHOUSE-PO-OVERCHARGE$";

    /** SN缓存 asnlineId */
    public static final String CACHE_ASNLINE_SN = "%CACHE-ASNLINE-SN$";

    /** 缓存Key多属性分隔符 */
    public static final String CACHE_KEY_SPLIT = "$";
    // --------------------------------------------------------------------------------------
    /** 收货等操作用户列表 */
    public static final String CACHE_OPERATOR_USER = "%CACHE-OPERATOR-USER$";

    /** 收货缓存数据集合 prefix+userId */
    public static final String CACHE_RCVD_PREFIX = "%CACHE-RCVD$";

    /** 通用收货ASN的Sn号缓存 prefix+userId */
    public static final String CACHE_RCVD_SN_PREFIX = "%CACHE-RCVD-SN$";

    /** 明细超收数量 */
    public static final String CACHE_ASNLINE_OVERCHARGE_PREFIX = "%CACHE-ASNLINE-OVERCHARGE-";

    /** 容器商品占用缓存 */
    @Deprecated
    public static final String CACHE_RCVD_CONTAINER = "%CACHE-RCVD-CONTAINER$";

    /** 容器商品占用缓存 */
    /** @mender yimin.lu 2016/11/1 调整缓存的对象为 userId+容器lifecycle+容器Status 以$分隔 */
    public static final String CACHE_RCVD_CONTAINER_USER_PREFIX = "%CACHE-RCVD-CONTAINER-USER$";

    /** 容器商品属性缓存前缀 规则：前缀+containerId+$+skuId ,#mender yimin.lu 2016/11/14 value: */
    public static final String CACHE_RCVD_CONTAINER_PREFIX = "%CACHE-RCVD-CONTAINER%";

    public static final String CACHE_RCVD_PALLET_PREFIX = "%CACHE-RCVD-PALLET$";

    /** 商品条码对应SKUID缓存 前缀+barcode */
    public static final String WMS_CACHE_SKU_BARCODE = "%WMS-CACHE-SKU-BARCODE-";

    /** 商品主档信息缓存 前缀+SKUID+OUID */
    public static final String WMS_CACHE_SKU_MASTER = "%WMS-CACHE-SKU-MASTER-";

    /**
     * 容器对应skuId缓存:根据容器号查询出库存，再根据库存查询该库存对应的有哪些sku_id,然后将sku_id存入缓存中
     */
    public static final String CACHE_MMP_CONTAINER_CODE_PREFIX = "CACHE_MMP_CONTAINER_CODE_PREFIX_";

    /** 系统参数缓存 前缀+groupValue+dicValue Dictionary */
    public static final String WMS_CACHE_SYS_DICTIONARY = "%WMS-CACHE-SYS-DICTIONARY$";

    /** 客户缓存 前缀+ customerId */
    public static final String WMS_CACHE_CUSTOMER = "WMS-CACHE-CUSTOMER-";

    /** 店铺缓存 前缀+ customerId+storeId */
    public static final String WMS_CACHE_STORE = "WMS-CACHE-STORE-";



    /** caseLevel收货人缓存前缀 规则：前缀 + userId + asnId + containerId；间隔符："-" */
    public static final String WMS_CACHE_CL_OPT_USER_PREFIX = "WMS-CACHE-CL-OPT-USER-";
    /** caseLevel本次扫描已收SN/残次品信息缓存前缀 规则：前缀 + userId + asnId + containerId + skuId + UUID；间隔符："-" */
    public static final String WMS_CACHE_CL_SCAN_RECD_SN_DEFECT_INFO_PREFIX = "WMS-CACHE-CL-SCAN-RECD-SN-DEFECT-INFO-PREFIX-";
    /** caseLevel本次收货已收carton缓存前缀 规则：前缀 + userId + asnId + containerId + skuId + cartonId + UUID */
    public static final String WMS_CACHE_CL_RECD_CARTON_PREFIX = "WMS-CACHE-CL-RECD-CARTON-";
    /** caseLevel收货装箱信息表缓存前缀 规则：前缀 + userId + asnId + containerId；分隔符："- */
    public static final String WMS_CACHE_CL_ORIGIN_CARTON_PREFIX = "WMS-CACHE-CL-ORIGIN-CARTON-";
    /** caseLevel货箱SN缓存前缀 规则：前缀 + userId + asnId + containerId；分隔符："-" */
    public static final String WMS_CACHE_CL_ORIGIN_SN_PREFIX = "WMS-CACHE-CL-ORIGIN-SN-";
    /** caseLevel前次收货数据前缀 规则：前缀 + userId + asnId + containerId + skuId；间隔符："-" */
    public static final String WMS_CACHE_CL_LAST_RECD_QTY_PREFIX = "WMS-CACHE-CL-LAST-RECD-QTY-";

    // 过期时间
    public static final int CACHE_ONE_SECOND = 1;
    public static final int CACHE_ONE_MINUTE = 60;
    public static final int CACHE_THIRTY_MINUTE = 30 * 60;
    public static final int CACHE_ONE_HOUR = 60 * 60;
    public static final int CACHE_ONE_DAY = 1 * 24 * 60 * 60;
    public static final int CACHE_ONE_WEEK = 7 * 24 * 60 * 60;
    public static final int CACHE_ONE_MONTH = 30 * 24 * 60 * 60;
    public static final int CACHE_ONE_YEAR = 365 * 24 * 60 * 60;

    /** 波次分配前缀 规则: 前缀+waveId; 间隔符:"$" */
    public static final String CACHE_ALLOCATE_SOFT = "%CACHE-ALLOCATE_SOFT$";

    // --------------------------------------------------------------------------------------------------------------------------------------
    /** 波次分配规则 出库单计数器 */
    public static final String KEY_PREFIX = "%";
    public static final String WAVE_ODO_SPLIT = "|";
    public static final String WAVE_ODO_SKU_SPLIT = "$";

    // 出库单设置
    /** 出库单缓存 ：前缀$CODE|ODOID */
    public static final String OU_ODO_PREFIX = "%OU_ODO%";

    /** 秒杀出库单：前缀$CODE|ODOID */
    public static final String SECKILL_ODO_PREFIX = "%SECKILL_ODO%";

    /** 秒杀出库单计数器：前缀$CODE|ODOID */
    public static final String SECKILL_PREFIX = "%SECKILL%";

    /** 主副品 */
    public static final String TWOSKUSUIT_ODO_PREFIX = "%TWOSKUSUIT_ODO%";
    /** 主副品计数器 */
    public static final String TWOSKUSUIT_PREFIX = "%TWOSKUSUIT%";

    /** 套装 */
    public static final String SUITS_ODO_PREFIX = "%SUITS_ODO%";
    /** 套装计数器 */
    public static final String SUITS_PREFIX = "%SUITS%";

    /** 主副品扣减集合 */
    public static final String TWOSKUSUIT_DIV_ODO_PREFIX = "%TWOSKUSUIT_DIV_ODO%";
    /** 套装扣减集合 */
    public static final String SUITS_DIV_ODO_PREFIX = "%SUITS_DIV_ODO%";

    /**仓库缓存：前缀+ouId*/
    public static final String CACHE_WAREHOSUE = "%WAREHOUSE%";

    /** 波次主档缓存：前缀+waveMasterId */
    public static final String CACHE_WAVE_MASTER = "%WAVE_MASTER%";

}
