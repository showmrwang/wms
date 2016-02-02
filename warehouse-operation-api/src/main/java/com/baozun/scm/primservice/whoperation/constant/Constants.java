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

    public static final String WMS = "wms";

    public static final String WHPO_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.poasn.WhPo";

}
