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

    /** 集合对象{0}不能为空！ **/
    public static final Integer LIST_IS_NULL = 7;

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

    /** 数据解析失败！ */
    public static final int PARSE_EXCEPTION_ERROR = 1018;

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
    /** 商品编码 [{0}] 商品名称 [{1}] 状态不可用，请去商品维护页面重新设置 */
    public static final int SKU_IS_LIFECYCLE_ERROR = 1082;
    /** 二级容器不存在 */
    public static final int CONTAINER2NDCATEGORY_NULL_ERROR = 1083;
    /** 拆箱信息不存在 */
    public static final int CARTONNULL_ERROR = 1084;

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
    /** 拆箱商品数量不能小于等于0，请重新设置 */
    public static final int ADD_CARTONLIST_BCDEVANNINGQTY_ERROR = 3036;
    /** 每箱商品数量不能小于等于0，请重新设置 */
    public static final int ADD_CARTONLIST_QUANTITY_ERROR = 3037;


    // 20000-30000 start
    /** 未查询到结果！ **/
    public static final Integer OBJECT_IS_NULL = 20004;
    /** ASN单据[{0}]未绑定店铺 **/
    public static final int WH_ASN_STORE_EMPTY = 20006;
    /** Asn单据店面和仓库没有配置预收货模式 */
    public static final int STORE_WAREHOUSE_IS_CONFIG = 20007;
    /** 商品不存在 */
    public static final int SKU_NOT_FOUND = 20008;
    /** 商品不在Asn收货明细中 */
    public static final int SKU_NOT_FOUND_IN_ASN = 20011;
    // 20000-30000 end

    // 5001-10000 common start
    /** 容器号不能为空 */
    public static final int COMMON_CONTAINER_CODE_IS_NULL_ERROR = 5001;
    /** 托盘号不能为空 */
    public static final int COMMON_PALLET_CODE_IS_NULL_ERROR = 5002;
    /** 容器信息不存在 */
    public static final int COMMON_CONTAINER_IS_NOT_EXISTS = 5003;
    /** 容器状态不可用 */
    public static final int COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL = 5004;
    /** 推荐库位失败！ */
    public static final int COMMON_LOCATION_RECOMMEND_ERROR = 5005;
    /** ASN单据[{0}]信息不存在 */
    public static final int COMMON_ASN_IS_NULL_ERROR = 5006;
    /** ASN单据[{0}]状态异常 */
    public static final int COMMON_ASN_STATUS_ERROR = 5007;
    /** PO单据[{0}]信息不存在 */
    public static final int COMMON_PO_IS_NULL_ERROR = 5008;
    /** PO单据[{0}]状态异常 */
    public static final int COMMON_PO_STATUS_ERROR = 5009;
    /** 未找到推荐库位！ */
    public static final int COMMON_LOCATION_NOT_RECOMMEND_ERROR = 5010;
    /** 库位信息不存在！ */
    public static final int COMMON_LOCATION_IS_NOT_EXISTS = 5011;
    /** 库存记录行计算唯一标识异常！ */
    public static final int COMMON_INV_PROCESS_UUID_ERROR = 5012;
    /** 店铺信息不存在！ */
    public static final int COMMON_STORE_NOT_FOUND_ERROR = 5013;
    /** 仓库信息不存在！ */
    public static final int COMMON_WAREHOUSE_NOT_FOUND_ERROR = 5014;
    /** 内部容器信息不存在！ */
    public static final int COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS = 5015;
    /** 外部容器信息不存在！ */
    public static final int COMMON_OUTER_CONTAINER_IS_NOT_EXISTS = 5016;
    /** 内部容器状态不可用！ */
    public static final int COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL = 5017;
    /** 菜单功能信息不存在！ */
    public static final int COMMON_FUNCTION_IS_NULL_ERROR = 5018;
    /** 菜单功能参数信息不存在！ */
    public static final int COMMON_FUNCTION_CONF_IS_NULL_ERROR = 5019;
    // 5001-10000 common end

    // 10001-20000 luyimin
    /** ASN没有可以收货的明细 */
    public static final int ASN_NO_FOR_RCVD = 10001;
    /** ASN数据初始化失败 */
    public static final int ASN_CACHE_ERROR = 10002;
    /** 商品数据初始化失败 */
    public static final int SKU_CACHE_ERROR = 10003;
    /** 商品已超收 */
    public static final int SKU_OVERCHARGE_ERROR = 10004;
    /** 缓存数据失败 */
    public static final int RCVD_CACHE_ERROR = 10005;
    /** 商品属性有差异，不允许差异收货! */
    public static final int RCVD_DISCREPANCY_ERROR = 10006;
    /** 数据异常，此商品找不到对应的ASN单明细 */
    public static final int RCVD_SKU_ASNLINE_NOTFOUND_ERROR = 10007;
    /** 商品库存属性找不到对应的明细行 */
    public static final int RCVD_MATCH_ERROR = 10008;
    /** 获取商品信息异常，商品数据已失效！ */
    public static final int RCVD_SKU_EXPRIED_ERROR = 10009;
    /** 商品收货失败，请点击重试 */
    public static final int RCVD_SKU_SAVE_ERROR = 10010;
    /** 容器不可用，请更换容器 ! */
    public static final int RCVD_CONTAINER_LIMIT_ERROR = 10011;
    /** 取消操作异常，请重试! */
    public static final int RCVD_CANCEL_ERROR = 10012;
    /** 仓库店铺超收比例缓存失败，请重试！ */
    public static final int RCVD_WAREHOUSE_STORE_OVERCHAGE_CACHE_ERROR = 10013;
    /** 容器已被占用！ */
    public static final int RCVD_CONTAINER_OCCUPATIED_ERROR = 10014;
    /** ASN的外部编码已经存在 */
    public static final int ASN_EXTCODE_EXISTS = 10015;
    /** 残次收货数量超出批量收货数量 */
    public static final int RCVD_BATCH_DEFEAT_COUNT_OVER_ERROR = 10016;
    /** 容器收货完成操作异常，请重试! */
    public static final int RCVD_CONTAINERRCVDSAVE_ERROR = 10017;
    // 10001-20000 luyimin


    // 20001-30000 shenjian
    // 20001-30000 shenjian

    // 30001-40000 zhanglei
    // 30001-40000 zhanglei

    // 40001-50000 lichuan
    /** 库位推荐失败，无可用上架规则 */
    public static final int RECOMMEND_LOCATION_NO_RULE_ERROR = 40001;
    /** 当前容器不是托盘，此上架功能不支持！ */
    public static final int CONTAINER_IS_NOT_PALLET_ERROR = 40002;
    /** 容器号[{0}]未找到收货库存信息 */
    public static final int CONTAINER_NOT_FOUND_RCVD_INV_ERROR = 40003;
    /** 库位绑定失败，该容器号对应的收货库存信息已有库位！ */
    public static final int CONTAINER_RCVD_INV_HAS_LOCATION_ERROR = 40004;
    /** 收货库存信息异常，未找到占用编码 */
    public static final int RCVD_INV_INFO_NOT_OCCUPY_ERROR = 40005;
    /** 上架失败，收货库存信息没有推荐库位！ */
    public static final int RCVD_INV_NOT_HAS_LOCATION_ERROR = 40006;
    /** 容器号[{0}]状态无法上架！*/
    public static final int CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY = 40007;
    /** 容器号[{0}]是内部容器，请扫描外部容器号*/
    public static final int CONTAINER_IS_INSIDE_ERROR_UNABLE_PUTAWAY = 40008;
    /** 收货库存信息没有内部容器异常！ */
    public static final int RCVD_INV_NOT_HAS_INSIDE_CONTAINER_ERROR = 40009;
    /** 容器号[{0}]长宽高异常！ */
    public static final int CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR = 40010;
    /** 容器号[{0}]重量异常！ */
    public static final int CONTAINER_WEIGHT_IS_NULL_ERROR = 40011;
    /** 商品[{0}]长宽高异常！ */
    public static final int SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR = 40012;
    /** 商品[{0}]重量异常！ */
    public static final int SKU_WEIGHT_IS_NULL_ERROR = 40013;
    /** 容器辅助信息统计失败！ */
    public static final int CONTAINER_ASSIST_INFO_GENERATE_ERROR = 40014;
    // 40001-50000 lichuan

    // 50001-60000 hubin
    /** 容器号不能为空 */
    public static final int PDA_INBOUND_SORTATION_CONTAINERCODE_NULL = 50001;
    /** 容器不存在 */
    public static final int PDA_INBOUND_SORTATION_CONTAINER_NULL = 50002;
    /** 容器生命周期无效 */
    public static final int PDA_INBOUND_SORTATION_LIFRCYCLE_ERROR = 50003;
    /** 容器对应的库存信息不存在 */
    public static final int PDA_INBOUND_SORTATION_CONTAINER_INV_NULL = 50004;
    /** 找不到对应入库分拣规则 */
    public static final int PDA_INBOUND_SORTATION_CONTAINER_USABLENESS_FALSE = 50005;
    /** SKU不能为空 */
    public static final int PDA_INBOUND_SORTATION_SKUCODE_NULL = 50006;
    /** 此SKU在对应原始容器号无库存信息 */
    public static final int PDA_INBOUND_SORTATION_SKUINV_NULL = 50007;
    /** SKU状态不可用 */
    public static final int PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR = 50008;
    /** 移入数量不能为空 shiftInQty */
    public static final int PDA_INBOUND_SORTATION_SHIFTINQTY_NULL = 50009;
    /** 移入数量不能小于等于0 */
    public static final int PDA_INBOUND_SORTATION_SHIFTINQTY_ERROR = 50010;
    /** 移入数量不能大于待移出数量 shiftOutQty */
    public static final int PDA_INBOUND_SORTATION_SHIFTINOUTQTY_ERROR = 50011;
    /** 确认容器号和目标容器号不相同 targetContainerCodeSelect */
    public static final int PDA_INBOUND_SORTATION_TARGETCONTAINER_ERROR = 50012;
    /** 此SN/残次条码不存在 */
    public static final int PDA_INBOUND_SORTATION_SN_NULL = 50013;
    /** 此SN/残次条码已扫描过 */
    public static final int PDA_INBOUND_SORTATION_SN_DOUBLE_ERROR = 50014;
    /** 移入数量【{0}】和SN/残次条码数量【{1}】不一致 */
    public static final int PDA_INBOUND_SORTATION_SNLISTQTY_ERROR = 50015;
    /** 目标容器已装满 请更换容器 */
    public static final int PDA_INBOUND_SORTATION_CONTAINER_ISFULL_ERROR = 50016;
    /** 原始容器号库存数量【{0}】小于本次移入数量【{1}】 OnHandQty */
    public static final int PDA_INBOUND_SORTATION_ONHANDQTY_ERROR = 50017;
    /** 库存属性对应库存不存在 请重新操作 */
    public static final int PDA_INBOUND_SORTATION_SKUATTRINV_ERROR = 50018;
    /** 容器状态异常 状态编码【{0}】 */
    public static final int PDA_INBOUND_SORTATION_STATUS_ERROR = 50019;
    /** 此目标容器已被其他用户使用 请更换容器 WhContainerAssign */
    public static final int PDA_INBOUND_CONTAINER_ASSIGN_USER_ERROR = 50020;
    /** 此商品不能放入目标容器 请更换容器 IsSkuMatchContainer */
    public static final int PDA_INBOUND_SKU_MATCH_CONTAINER_ERROR = 50021;
    // 50001-60000 hubin

    // 60001-70000 xiemingwei
    // 60001-70000 xiemingwei

    // 70001-80000 tangming
    // 70001-80000 tangming

    // 80001-90000 shenlijun
    // 80001-90000 shenlijun
}
