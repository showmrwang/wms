package com.baozun.scm.primservice.whoperation.constant;

public final class Constants {

    private Constants() {}

    /** 权限URL与类型的分隔符 */
    public static final String PRIVILEGE_URL_TYPE_SEP = "-";

    /** 启用状态 */
    public static final Integer LIFECYCLE_START = 1;

    /** 停用状态 */
    public static final Integer LIFECYCLE_BLOCK = 0;

    /** 删除状态 */
    public static final Integer LIFECYCLE_DELETE = 2;

    /** 公共库 */
    public static final Integer INFO_SOURCE = 1;

    /** 拆分库 */
    public static final Integer SHARD_SOURCE = 2;

    /** 调编码生成器租户编码 */
    public static final String WMS = "wms";

    /** 调编码生成器 po单实体标识 */
    public static final String WHPO_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.poasn.WhPo";

    /** 调编码生成器 bipo单实体标识 */
    public static final String BIPO_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.poasn.BiPo";

    /** 调编码生成器 asn单实体标识 */
    public static final String WHASN_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.poasn.WhAsn";

    /** 调编码生成器 容器实体标识 */
    public static final String CONTAINER_MODEL_URL = "com.baozun.scm.primservice.whinfo.model.warehouse.Container";

    /** 调编码生成器Asn预约实体标识 */
    public static final String ASN_RESERVE_MODEL_URL = "com.baozun.scm.primservice.whinfo.model.warehouse.AsnReserve";


    /** 调编码生成器 ASN单WMS内部单号分组 */
    public static final String WMS_ASN_INNER = "WMS_ASN_INNER";

    /** 调编码生成器 ASN单WMS外部单号分组 */
    public static final String WMS_ASN_EXT = "WMS_ASN_EXT";

    /** 插入全局日志表 */
    public static final String GLOBAL_LOG_INSERT = "INSERT";

    /** 更新全局日志表 */
    public static final String GLOBAL_LOG_UPDATE = "UPDATE";

    /** 删除全局日志表 */
    public static final String GLOBAL_LOG_DELETE = "DELETE";

    /** PO单类型 */
    public static final String PO_TYPE = "PO_TYPE";

    /** ASN单状态 */
    public static final String ASNSTATUS = "ASNSTATUS";

    /** ASN单紧急状态 */
    public static final String URGENT_STATUS = "URGENT_STATUS";

    /** 预约状态 */
    public static final String ASN_RESERVE_STATUS = "ASN_RESERVE_STATUS";

    /** 库存类型 */
    public static final String INVENTORY_TYPE = "INVENTORY_TYPE";

    /** 库存属性1 */
    public static final String INVENTORY_ATTR_1 = "INVENTORY_ATTR_1";

    /** 库存属性2 */
    public static final String INVENTORY_ATTR_2 = "INVENTORY_ATTR_2";

    /** 库存属性3 */
    public static final String INVENTORY_ATTR_3 = "INVENTORY_ATTR_3";

    /** 库存属性4 */
    public static final String INVENTORY_ATTR_4 = "INVENTORY_ATTR_4";

    /** 库存属性5 */
    public static final String INVENTORY_ATTR_5 = "INVENTORY_ATTR_5";

    /** 时间单位类型 */
    public static final String TIME_UOM = "TIME_UOM";

    /** 集团组织 */
    public static final Long OU_TYPE_ROOT = 1L;

    /** 物流中心组织 */
    public static final Long OU_TYPE_OPERATIONCENTER = 2L;

    /** 仓库组织 */
    public static final Long OU_TYPE_WAREHOUSE = 3L;

    /** 系统异常 */
    public static final Integer SYSTEM_EXCEPTION = 11;

    /** 系统错误 */
    public static final Integer SYSTEM_ERROR = 1;

    /** 异常前缀 */
    public static final String BUSINESS_EXCEPTION_PREFIX = "business_exception_";

    /** po单状态 */
    public static final String POSTATUS = "POSTATUS";

    /** 默认Double数值 */
    public static final Double DEFAULT_DOUBLE = 0d;

    /** 默认Integer数值 */
    public static final Integer DEFAULT_INTEGER = 0;

    /** BIpo单状态 */
    public static final String BIPO_STATUS = "BIPO_STATUS";

    /** BIpo单明细状态 */
    public static final String BIPOLINE_STATUS = "BIPOLINE_STATUS";

    /** 日期format格式 */
    public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";

    public static final String DATE_PATTERN_YMDHM = "yyyy-MM-dd HH:mm";

    /** 月台规则TYPE */
    public static final String PLATFORM_RECOMMEND_RULE = "platform_recommend_rule";

    /** 入库分拣规则TYPE */
    public static final String INBOUND_RULE = "inbound_rule";

    /** 上架推荐规则TYPE 整托盘 整箱 */
    public static final String SHELVE_RECOMMEND_RULE_ALL = "shelve_recommend_rule_all";

    /** 上架推荐规则TYPE 拆箱 */
    public static final String SHELVE_RECOMMEND_RULE = "shelve_recommend_rule";

    /** 默认Long数值 */
    public static final Long DEFAULT_LONG = 0l;

    /** Asn预约紧急 */
    public static final Long ASN_RESERVE_URGENT = 84L;

}
