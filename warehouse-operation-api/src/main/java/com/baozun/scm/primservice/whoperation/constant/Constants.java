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

    /** 调编码生成器 asn单实体标识 */
    public static final String WHASN_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.poasn.WhAsn";

    /** 调编码生成器 ASN单WMS内部单号分组 */
    public static final String WMS_ASN_INNER = "WMS_ASN_INNER";

    /** 调编码生成器 ASN单WMS外部单号分组 */
    public static final String WMS_ASN_EXT = "WMS_ASN_EXT";

}
