package com.baozun.scm.primservice.whinterface.msg;

public class WmsErrorCode {
    /** 仓库编码找不到对应的仓库 */
    public static final String WAREHOUSE_IS_NULL = "1001";
    /** 上位系统单据号找不到对应的出库单 */
    public static final String EXTCODE_NO_ERROR = "1002";
    /** 上位系统单据号不唯一 */
    public static final String EXTCODE_NOT_UNIQUE_ERROR = "1003";
    /** 传入参数为空 */
    public static final String PARAM_IS_NULL = "1004";
    /** 传入参数异常 */
    public static final String PARAM_IS_ERROR = "1005";
    /** 出库单锁定/解锁失败 */
    public static final String OUTBOUND_LOCKED_ERROR = "1006";
    /** 更新数据失败 */
    public static final String UPDATE_DATA_ERROR = "1007";
    /** 未知的异常 */
    public static final String UNKNOWN_ERROR = "1008";
    /** 参数查询结果异常 */
    public static final String SEARCH_ERROR = "1009";
    /** 出库单不允许取消 */
    public static final String ODO_STATUS_CANCEL_ERROR = "1010";

}
