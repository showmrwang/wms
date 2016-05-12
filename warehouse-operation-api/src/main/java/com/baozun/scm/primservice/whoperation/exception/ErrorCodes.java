package com.baozun.scm.primservice.whoperation.exception;

public class ErrorCodes {

    /** The Constant BUSINESS_EXCEPTION_PREFIX. */
    public static final String BUSINESS_EXCEPTION_PREFIX = "business_exception_";


    /** 系统错误. */
    public static final Integer SYSTEM_ERROR = 1;

    /** 无权访问. */
    public static final Integer ACCESS_DENIED = 2;

    /** session无效. */
    public static final Integer INVALID_SESSION = 3;

    /** dataBind error */
    public static final Integer DATA_BIND_EXCEPTION = 4;

    /** 参数不正确 */
    public static final Integer PARAMS_ERROR = 5;

    /** 传入的参数{0}不能为空,确保传入数据的完整性 **/
    public static final Integer PARAM_IS_NULL = 6;

    /** 编码生成器接口异常！ **/
    public static final Integer CODE_INTERFACE_REEOR = 9;

    /** 系统异常 **/
    public static final Integer SYSTEM_EXCEPTION = 11;

    /** 数据访问层异常 **/
    public static final Integer DAO_EXCEPTION = 12;

    /** 更新支付信息失败(t_so_payinfo) **/
    public static final Integer UPDATE_PAYINFO_FAILURE = 30001;

    /** 更新支付订单信息失败 (t_so_paycode) **/
    public static final Integer UPDATE_PAYCODE_FAILURE = 30002;

    /** 取消交易用户类型错误 **/
    public static final int transaction_cancel_usertype_error = 60014;

    /** 优惠券验证 **/
    public static final Integer COUPON_SYSTEM_ERROR = 140002;

    /** 支付订单号不存在 **/
    public static final int transaction_ordercode_error = 60016;

    /** 交易关闭 **/
    public static final int transaction_closed = 60008;
    
    /** 修改数据失败 请刷新页面后重试 **/
    public static final int UPDATE_DATA_ERROR = 1006;

    /** 保存数据失败,所设定的维度范围内包含已生成的库位编码，请重新设定维度 **/
    public static final int CREATE_CODE_ERROR = 1007;

    /** 删除数据失败,请刷新页面后重试 **/
    public static final int DELETE_CODE_ERROR = 1008;

    /** 删除数据失败,库位条码生成失败，请刷新页面后重试 **/
    public static final int UPDATE_BARCODE_ERROR = 1009;

    /** 删除数据失败,补货条码生成失败，请刷新页面后重试 **/
    public static final int UPDATE_REPLENISHMENT_ERROR = 1010;

    /** 重复的库位编码，请重新选择库位编码起止 **/
    public static final int REDUPLICATIVE_DATA_ERROR = 1011;

    /** 所设定的维度包含过多的编码，已超过上限{0}，不能进行操作，请重新设定 **/
    public static final int UPDATE_DATA_UPPER_ERROR = 1012;

    /** 已有所设定的{0}，请重新设定 **/
    public static final int UPDATE_UOM_CODENAME_ERROR = 1013;

    /** 数据备份失败，请刷新重试 **/
    public static final int BACKUP_DATA_ERROR = 1014;

    /** 区域下存在子区域，不能进行删除操作 **/
    public static final int REGION_PARENT_ERROR = 1015;

    /** 更新的数据数量({0})与实际更新数量({1})不一致，请刷新页面后重试 **/
    public static final int UPDATE_DATA_QUANTITYERROR = 1016;

    /** ASN号编码({0})在系统中未找到 **/
    public static final int ASN_CODE_IS_NOT_FOUND = 1017;

    /** 用户角色id为空 **/
    public static final int USER_EMPTY = 1020;
    /** 所选ucid与当前用户不符 **/
    public static final int USER_UC_NOT_EUQAL = 1021;
    /** 删除失败 **/
    public static final int DELETE_FAILURE = 1022;
    /** 保存失败 **/
    public static final int SAVE_FAILURE = 1023;
    /** 店铺id为空 **/
    public static final int STORE_EMPTY = 1024;
    /** 商品Id{0},未查询到相应的商品信息! */
    public static final int SKU_IS_NULL_BY_ID = 1045;
    /** 拆箱信息不存在 */
    public static final int CARTONNULL_ERROR = 1079;
    /** 商品编码 [{0}] 商品名称 [{1}] 状态不可用，请去商品维护页面重新设置 */
    public static final int SKU_IS_LIFECYCLE_ERROR = 1082;
    /**二级容器不存在*/
    public static final int CONTAINER2NDCATEGORY_NULL_ERROR = 1083;

    /** 页面会话错误 请刷新页面后重试 **/
    public static final int SESSION_DATA_ERROR = 2001;
    /** 保存至po_check信息失败 */
    public static final int SAVE_CHECK_TABLE_FAILED = 3001;
    /** po单已存在 */
    public static final int PO_EXIST = 3002;
    /** 保存至po单信息失败 */
    public static final int SAVE_PO_FAILED = 3003;
    /** 保存至asn_check信息失败 */
    public static final int SAVE_CHECK_TABLE_FAILED_ASN = 3004;
    /** asn单已存在 */
    public static final int ASN_EXIST = 3005;
    /** 保存至asn单信息失败 */
    public static final int SAVE_PO_FAILED_ASN = 3006;
    /** 调用编码生成器获取编码出错 */
    public static final int GET_GENERATECODE_NULL = 3007;
    /** ASN计划数量大于PO可用数量 */
    public static final int ASNLINE_QTYPLANNED_ERROR = 3008;
    /** PO单号 [poCode:{0}] 状态为新建才允许删除或取消 */
    public static final int PO_DELETE_STATUS_ERROR = 3009;
    /** PO单数据不存在 */
    public static final int PO_NULL = 3010;
    /** ASN单数据不存在 */
    public static final int ASN_NULL = 3012;
    /** PO单状态为【收货中、收货完成】，PO单才允许审核成功 */
    public static final int PO_AUDIT_STATUS_ERROR = 3011;
    /** PO单所包含的ASN状态不全为【关闭】，PO单不允许审核成功 */
    public static final int PO_AUDIT_ASNSTATUS_ERROR = 3013;
    /** ASN单状态错误，不允许审核成功 */
    public static final int ASN_AUDIT_STATUS_ERROR = 3014;
    /** ASN单所包含的明细状态有未收货，ASN单不允许审核成功 */
    public static final int ASN_AUDIT_LINESTATUS_ERROR = 3015;
    /** PO单明细 [id:{0}] 状态为新建才允许删除 */
    public static final int POLINE_DELETE_STATUS_ERROR = 3016;
    /** ASN单状态为新建才允许删除 */
    public static final int ASN_DELETE_STATUS_ERROR = 3017;
    /** 封装数据失败 */
    public static final int PACKAGING_ERROR = 3018;
    /** 插入数据失败，请刷新页面重试 */
    public static final int INSERT_DATA_ERROR = 3019;
    /** 删除数据失败，请刷新页面重试 */
    public static final int DELETE_DATA_ERROR = 3020;
    /** 校验数据失败，请刷新页面重试 */
    public static final int CHECK_DATA_ERROR = 3021;
    /** 插入{}数据失败，请刷新页面重试 */
    public static final int INSERT_ERROR = 3022;
    /** 删除{}数据失败，请刷新页面重试 */
    public static final int DELETE_ERROR = 3023;
    /** 更新{}数据失败，请刷新页面重试 */
    public static final int UPDATE_ERROR = 3024;
    /** 插入操作日志失败，请刷新页面重试 */
    public static final int INSERT_LOG_ERROR = 3025;
    /** ASN单明细数据不存在 */
    public static final int ASNLINE_NULL = 3026;
    /** 数值异常！ */
    public static final int NUMBER_ERROR = 3027;
    /** 客户数据过期！ */
    public static final int CUSTOMER_DATA_EXPIRED = 3028;
    /** ASN单明细状态为未收货才允许删除 */
    public static final int ASNLINE_DELETE_STATUS_ERROR = 3029;
    /** 数据已经失效，请确保数据处于可用状态 */
    public static final int DATA_EXPRIE_ERROR = 3030;
    /** ASN单明细状态不是 [未收货] 状态，请刷新页面重试 */
    public static final int ASNLINE_STATUS_ERROR = 3031;
    /** 请新增拆箱商品明细 */
    public static final int ADD_CARTONLIST_NULL_ERROR = 3032;
    /** 新增拆箱商品总数量为 [{0}] 超过可拆箱商品数量 [{1}]，请刷新页面重试 */
    public static final int ADD_CARTONLIST_QTY_ERROR = 3033;
    /** 拆箱商品数量 [{0}] 容器内商品数量[{1}] 容器个数 [{2}]，每箱分配数量不相同，请重新设置 */
    public static final int ADD_CARTONLIST_BINQTY_ERROR = 3034;
    /** 取消PO单失败 */
    public static final int CANCEL_PO_ERROR = 3035;
}
