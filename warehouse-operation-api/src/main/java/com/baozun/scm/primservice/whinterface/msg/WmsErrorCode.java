package com.baozun.scm.primservice.whinterface.msg;

public class WmsErrorCode {
    /** 系统异常 **/
    public static final String SYSTEM_EXCEPTION = "1001";

    /** 参数为空 **/
    public static final String PARAM_IS_NULL = "1002";

    /** 数据已存在 **/
    public static final String DATA_IS_EXIST = "1003";

    /** 无该客户信息 **/
    public static final String NOT_HAVE_CUSTOMER_INFOMATION = "1004";

    /** 无该店铺信息 **/
    public static final String NOT_HAVE_STORE_INFOMATION = "1005";

    /** 店铺不在此客户下 **/
    public static final String STORE_IS_NOT_IN_CUSTOMER = "1006";

    /** 无该仓库信息 **/
    public static final String NOT_HAVE_WAREHOUSE_INFOMATION = "1007";

    /** 上位系统单据号找不到对应的单据 */
    public static final String EXTCODE_NO_ERROR = "3001";

    /** 上位系统单据号不唯一 */
    public static final String EXTCODE_NOT_UNIQUE_ERROR = "3002";

    /** 传入参数异常 */
    public static final String PARAM_IS_ERROR = "3003";

    /** 出库单锁定/解锁失败 */
    public static final String OUTBOUND_LOCKED_ERROR = "3004";

    /** 更新数据失败 */
    public static final String UPDATE_DATA_ERROR = "3005";

    /** 参数查询结果异常 */
    public static final String SEARCH_ERROR = "3006";

    /** 单据不允许取消 */
    public static final String STATUS_CANCEL_ERROR = "3007";


}
