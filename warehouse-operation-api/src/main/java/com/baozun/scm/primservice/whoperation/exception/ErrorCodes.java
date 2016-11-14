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
    /** 缓存服务繁忙，请稍后再试！ */
    public static final int COMMON_CACHE_IS_ERROR = 5020;
    /** 库存行没有绑定库位，无法上架 */
    public static final int COMMON_INV_LINE_NOT_BINDING_LOC_ERROR = 5021;
    /** 外部容器信息不匹配！ */
    public static final int COMMON_OUTER_CONTAINER_IS_NOT_MATCH = 5022;
    /** 功能信息变更异常！ */
    public static final int COMMON_FUNCTION_IS_CHANGE_ERROR = 5023;
    /** 库存类型未找到！ */
    public static final int COMMON_INV_TYPE_NOT_FOUND_ERROR = 5024;
    /** 库存状态未找到！ */
    public static final int COMMON_INV_STATUS_NOT_FOUND_ERROR = 5025;
    /** 库存属性未找到！ */
    public static final int COMMON_INV_ATTR_NOT_FOUND_ERROR = 5026;
    /** 库存属性1[{0}]未找到！ */
    public static final int COMMON_INV_ATTR1_NOT_FOUND_ERROR = 5027;
    /** 库存属性2[{0}]未找到！ */
    public static final int COMMON_INV_ATTR2_NOT_FOUND_ERROR = 5028;
    /** 库存属性3[{0}]未找到！ */
    public static final int COMMON_INV_ATTR3_NOT_FOUND_ERROR = 5029;
    /** 库存属性4[{0}]未找到！ */
    public static final int COMMON_INV_ATTR4_NOT_FOUND_ERROR = 5030;
    /** 库存属性5[{0}]未找到！ */
    public static final int COMMON_INV_ATTR5_NOT_FOUND_ERROR = 5031;
    /** 逐件扫描数量不正确！ */
    public static final int COMMON_ONE_BY_ONE_SCAN_QTY_ERROR = 5032;
    /** 商品多条码数量无法完成核对 */
    public static final int COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE = 5033;
    /** 库位绑定异常！ */
    public static final int COMMON_LOCATION_BINDING_ERROR = 5034;
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
    /** ASN数据缓存刷新失败 */
    public static final int ASN_CACHE_FRESH_ERROR = 10018;
    /** 单据号获取单据失败 */
    public static final int OCCUPATION_RCVD_GET_ERROR = 10019;
    /** 单据号获取PO单失败 */
    public static final int PO_RCVD_GET_ERROR = 10020;
    /** 获取容器数据失败 */
    public static final int CONTAINER_RCVD_GET_ERROR = 10021;
    /** 月台释放失败，请手动释放月台！ */
    public static final int RCVD_PLATFORM_REALEASE_ERROR = 10022;
    /** 托盘上有已收货完成的容器，请点击托盘收货完成返回上一页 */
    public static final int RCVD_PALLET_CONTANIER_ERROR = 10023;
    /** 容器状态回滚失败，请重试！ */
    public static final int RCVD_CONTAINER_REVOKE_ERROR = 10024;
    /** 商品效期已过期！ */
    public static final int SKU_EXPIRE_ERROR = 10025;
    /** 已分配仓库的单据，不允许删除！ */
    public static final int BIPO_DELETE_HAS_ALLOCATED_ERROR = 10026;
    /** PO单明细已没有可用数量！ */
    public static final int PO_NO_AVAILABLE_ERROR = 10027;
    /** PO单状态异常，不允许创建ASN */
    public static final int PO_CREATEASN_STATUS_ERROR = 10028;
    /** PO单已经指定了仓库，不允许创建子PO */
    public static final int BIPO_CREATESUB_OUID_ERROR = 10029;

    /** ASN单为空 */
    public static final int ASN_IS_NULL_ERROR = 10030;
    /** ASN相关单据号为空 */
    public static final int ASN_EXTCODE_IS_NULL_ERROR = 10031;
    /** ASN客户为空 */
    public static final int ASN_CUSTOMER_IS_NULL_ERROR = 10032;
    /** ASN店铺为空 */
    public static final int ASN_STORE_IS_NULL_ERROR = 10033;
    /** ASN所属仓库为空 */
    public static final int ASN_OUID_IS_NULL_ERROR = 10034;

    /** PO单为空 */
    public static final int PO_IS_NULL = 10035;
    /** PO单相关单据号为空 */
    public static final int PO_EXTCODE_IS_NULL = 10036;
    /** PO单类型为空 */
    public static final int PO_POTYPE_IS_NULL = 10037;
    /** PO单状态为空 */
    public static final int PO_STATUS_IS_NULL = 10038;
    /** 上位系统同步的PO单明细行为空 */
    public static final int PO_POLINELIST_IS_NULL = 10039;

    /** PO单校验相关单据号失败，同一个店铺下有相同的相关单据号 */
    public static final int PO_CHECK_EXTCODE_ERROR = 10040;
    /** 收货的货箱号不能和托盘号相同! */
    public static final int RCVD_CONTAINER_NO_DUPLICATION = 10041;
    /** 未找到对应的出库单！ */
    public static final int NO_ODO_FOUND = 10042;
    /** 商品管控效期，生产日期、失效日期必填 */
    public static final int RCVD_SKU_VALIDDATE = 10043;
    /** 商品管控的效期与输入的生产日期失效日期计算不一致 */
    public static final int RCVD_SKU_VALIDDATE_DIFFERENT = 10044;
    /** 输入的商品效期大于商品管控的最大效期天数 */
    public static final int RCVD_SKU_VALIDDATE_MAX_ERROR = 10045;
    /** 输入的商品效期小于商品管控的最小效期天数 */
    public static final int RCVD_SKU_VALIDDATE_MIN_ERROR = 10046;
    /** 输入的商品序列号重复 */
    public static final int RCVD_SN_DUP_ERROR = 10047;
    /** 输入的商品序列号不存在 */
    public static final int RCVD_SN_NO_EXISTS_ERROR = 10048;
    /** 容器状态异常 */
    public static final int RCVD_CONTAINER_STATUS_ERROR = 10049;
    /** 扫描商品数据异常 */
    public static final int RCVD_SKU_DATA_ERROR = 10050;
    /** 校验容器时，容器缓存异常 */
    public static final int RCVD_CONTAINER_CACHE_ERROR = 10051;
    /** 商品混放属性不一致，商品不允许混放 */
    public static final int RCVD_SKU_MIXING_ATTR_ERROR = 10052;
    /** 容器不允许混放！ */
    public static final int RCVD_CONTAINER_MIXING_ERROR = 10053;
    /** 已有库存的容器商品数据异常！ */
    public static final int RCVD_CONTAINER_HAS_SKU_DATA_ERROR = 10054;
    // 10001-20000 luyimin


    // 20001-30000 shenjian
    /** 商品条码[{0}]未查询到相应的商品信息 */
    public static final int BARCODE_NOT_FOUND_SKU = 20009;
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
    /** 容器号[{0}]状态无法上架！ */
    public static final int CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY = 40007;
    /** 容器号[{0}]是内部容器，请扫描外部容器号 */
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
    /** 库位[{0}]长宽高异常！ */
    public static final int LOCATION_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR = 40015;
    /** 库位[{0}]重量异常！ */
    public static final int LOCATION_WEIGHT_IS_NULL_ERROR = 40016;
    /** 容器号[{0}]不存在当前扫描的商品 */
    public static final int CONTAINER_NOT_FOUND_SCAN_SKU_ERROR = 40017;
    /** 容器号[{0}]此商品扫描数量不正确 */
    public static final int CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR = 40018;
    /** 容器号[{0}]此商品重复扫描 */
    public static final int CONTAINER_SKU_HAS_ALREADY_SCANNED = 40019;
    /** 当前扫描商品数量不正确！ */
    public static final int SCAN_SKU_QTY_IS_VALID = 40020;
    /** 上架失败，整托上架绑定多个库位异常 */
    public static final int PALLET_PUTAWAY_BINDING_MORE_THAN_ONE_LOC = 40021;
    /** 上架失败，整箱上架绑定多个库位异常 */
    public static final int CONTAINER_PUTAWAY_BINDING_MORE_THAN_ONE_LOC = 40022;
    /** 容器号[{0}]有外部容器，请先扫外部容器 */
    public static final int CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST = 40023;
    /** 外部容器[{0}]提示下一个容器号失败 */
    public static final int TIP_NEXT_CONTAINER_IS_ERROR = 40024;
    /** 收货库存信息商品数量异常！ */
    public static final int RCVD_INV_SKU_QTY_ERROR = 40025;
    /** 商品扫描数量[{0}]大于收货量 */
    public static final int SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY = 40026;
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

    /** [{0}]存在SN商品,请更换收货功能 */
    public static final int CASELEVEL_SKU_SN_EXIST= 60002;
    /** [{0}]存在SN商品,请更换收货功能 */
    public static final int CASELEVEL_CONTAINER_NULL= 60003;
    /** 容器[{0}]不可用或已完成收货 */
    public static final int CASELEVEL_CONTAINER_UNAVAILABLE = 60004;
    /** 容器[{0}]已被[{1}]操作 */
    public static final int CASELEVEL_CONTAINER_OCCUPIED= 60005;
    /** [{0}]无caseLevel装箱信息 */
    public static final int CASELEVEL_NULL= 60006;
    /** 属性未维护,请更换收货功能 */
    public static final int CASELEVEL_SKU_ATTR_NULL= 60007;
    /** 已过保质期且不收过期商品,无法收货 */
    public static final int CASELEVEL_SKU_EXPIRED= 60008;
    /** 有效天数达不到系统要求,无法收货 */
    public static final int CASELEVEL_SKU_VALID_LT= 60009;
    /** 有效天数大于系统要求,无法收货 */
    public static final int CASELEVEL_SKU_VALID_GT= 60010;
    /** 库存类型不一致,请更换收货功能 */
    public static final int CASELEVEL_SKU_INV_TYPE_DIFF= 60011;
    /** 库存状态不一致,请更换收货功能 */
    public static final int CASELEVEL_SKU_INV_STATUS_DIFF= 60012;
    /** 需要录入残次信息,请更换收货功能 */
    public static final int CASELEVEL_SKU_IS_DEFECTIVE= 60013;
    /** 商品[{0}]不在货箱内 */
    public static final int CASELEVEL_SKU_NOT_IN_CARTON_ERROR = 60014;
    /** 商品[{0}]不可用 */
    public static final int CASELEVEL_SKU_UNAVAILABLE= 60015;
    /** 商品属性录入失败 */
    public static final int CASELEVEL_SKU_ATTR_INPUT_NULL= 60016;
    /** 缓存key错误 */
    public static final int CASELEVEL_CACHE_KEY_ERROR= 60017;
    /** 容器占用失败 */
    public static final int CASELEVEL_CONTAINER_OCCUPIED_FAILED = 60018;
    /** 日期解析异常 */
    public static final int CASELEVEL_PARSE_DATE_ERROR= 60019;
    /** 收货功能信息为空 */
    public static final int CASELEVEL_RCVD_FUN_NULL = 60020;
    /** 序列化异常 */
    public static final int CASELEVEL_SERIALIZE_ERROR= 60021;
    /** 收货数错误 */
    public static final int CASELEVEL_SKU_QTY_ERROR= 60022;
    /** 请求错误,属性扫描已结束 */
    public static final int CASELEVEL_REQUEST_ERROR= 60023;
    /** 不存在该属性的装箱记录 */
    public static final int CASELEVEL_SKU_ATTR_ERROR= 60024;
    /** 扫描序列错误 */
    public static final int CASELEVEL_SCAN_SQE_ERROR= 60025;
    /** 目标不在列表中 */
    public static final int CASELEVEL_TARGET_NOT_EXIST= 60026;
    /** SN号[{0}]已收入 */
    public static final int CASELEVEL_SN_EXIST_ERROR= 60027;
    /** SN号[{0}]不在此货箱中 */
    public static final int CASELEVEL_SN_NOT_EXIST_ERROR= 60028;
    /** 残次条码创建失败 */
    public static final int CASELEVEL_SN_DEFECT_WARE_BARCODE_ERROR= 60029;
    /** SN缓存操作失败 */
    public static final int CASELEVEL_SN_CACHE_ERROR= 60030;
    /** UUID获取失败 */
    public static final int CASELEVEL_UUID_ERROR= 60031;
    /** 缓存商品失败 */
    public static final int CASELEVEL_SKU_CACHE_ERROR= 60032;
    /** 无匹配的计划收货信息 */
    public static final int CASELEVEL_SKU_MATCH_ERROR= 60033;
    /** 缓存读取失败 */
    public static final int CASELEVEL_GET_CACHE_ERROR= 60034;
    /** 商品[{0}]存在差异 */
    public static final int CASELEVEL_RCVD_SKU_DIFF_ERROR = 60035;
    /** 收货存在差异 */
    public static final int CASELEVEL_RCVD_DIFF_ERROR = 60036;
    /** 商品[{0}]无收货数据 */
    public static final int CASELEVEL_RCVD_SKU_DATA_NULL_ERROR = 60037;
    /** 商品[{0}]收入的都是残次品,不允许调整数量 */
    public static final int CASELEVEL_RCVD_IS_DEFECTIVE_ERROR = 60038;
    /** 商品[{0}]是SN商品,不允许调整数量 */
    public static final int CASELEVEL_RCVD_IS_SN_ERROR = 60039;
    /** 商品[{0}]不存在差异 */
    public static final int CASELEVEL_RCVD_NOT_DIFF_ERROR = 60040;
    /** 残次品数量大于收货数量 */
    public static final int CASELEVEL_DEFECT_QTY_GL_RCVD_QTY_ERROR = 60041;
    /** 缓存SN/残次信息失败 */
    public static final int CASELEVEL_SN_DEFECT_INFO_CACHE_ERROR = 60042;
    /** 未找到uuid对应的缓存信息 */
    public static final int CASELEVEL_UUID_RCVD_INFO_NULL_ERROR = 60043;
    /** 收货数错误 */
    public static final int CASELEVEL_RCVD_QTY_ERROR = 60044;
    /** 商品有效期换算率错误 */
    public static final int CASELEVEL_SKU_DATE_UOM_ERROR = 60045;
    /** 容器[{0}]无收货数据 */
    public static final int CASELEVEL_RCVD_DATA_NULL_ERROR = 60046;
    /** 缓存操作失败 */
    public static final int CASELEVEL_CACHE_ERROR = 60047;
    /** SN/残次信息错误 */
    public static final int CASELEVEL_RCVD_SN_DEFECT_ERROR = 60048;
    /** 残次条码不存在 */
    public static final int CASELEVEL_RCVD_DEFECT_CODE_ERROR = 60049;
    /** 残次条码[{0}]重复 */
    public static final int CASELEVEL_RCVD_DEFECT_CODE_REPEAT_ERROR = 60050;
    /** SN号不存在 */
    public static final int CASELEVEL_SN_CODE_NULL_ERROR = 60051;
    /** 生产日期不合法 */
    public static final int CASELEVEL_SKU_MFGDATE_ERROR = 60052;
    /** 残次原因不存在 */
    public static final int CASELEVEL_DEFECT_REASONS_ID_NULL_ERROR = 60053;
    /** 残次类型不存在 */
    public static final int CASELEVEL_DEFECT_TYPE_ID_NULL_ERROR = 60054;
    /** 残次来源不存在 */
    public static final int CASELEVEL_DEFECT_SOURCE_NULL_ERROR = 60055;
    /** 商品信息未找到 */
    public static final int CASELEVEL_SKU_INFO_NULL_ERROR = 60056;
    /** 商品属性为空 */
    public static final int CASELEVEL_SKU_ATTR_NULL_ERROR = 60057;
    /** ASN信息为空 */
    public static final int CASELEVEL_ASN_NULL_ERROR = 60058;
    /** 容器信息为空 */
    public static final int CASELEVEL_CONTAINER_NULL_ERROR = 60059;
    /** 库存状态是否残次属性为空 */
    public static final int CASELEVEL_INVENTORY_ISDEFECT_NULL_ERROR = 60060;
    /** 收货功能属性缺失 */
    public static final int CASELEVEL_INV_FUN_ATTR_NULL_ERROR = 60061;
    /** 店铺信息缺失 */
    public static final int CASELEVEL_STORE_ATTR_NULL_ERROR = 60062;
    /** 数据库保存失败 */
    public static final int CASELEVEL_DATABASE_SAVE_ERROR = 60063;
    /** 容器取消占用失败 */
    public static final int CASELEVEL_RELEASE_CONTAINER_ERROR = 60064;
    /** 未找到商品[{0}]默认收货数量 */
    public static final int CASELEVEL_SKU_DEFAULT_RCVD_QTY_ERROR = 60065;
    /** 装箱信息计划数未维护 */
    public static final int CASELEVEL_CARTON_PLAN_QTY_NULL_ERROR = 60066;
    /** 商品属性对应的字典项不存在 */
    public static final int CASELEVEL_SKU_ATTR_SYSDIC_NULL_ERROR = 60067;
    /** 释放月台失败,请手工释放月台 */
    public static final int CASELEVEL_RELEASE_PLATFORM_ERROR = 60068;
    /** 未维护残次类型 */
    public static final int CASELEVEL_DEFECT_INFO_NULL_ERROR = 60069;
    /** 店铺残次原因未配置 */
    public static final int CASELEVEL_STORE_DEFECT_REASON_NULL_ERROR = 60070;
    /** 仓库残次原因未配置 */
    public static final int CASELEVEL_WAREHOUSE_DEFECT_REASON_NULL_ERROR = 60071;
    /** 收货功能配置的库存类型无效 */
    public static final int CASELEVEL_FUN_INV_TYPE_ERROR = 60072;
    /** 收货功能配置的库存状态无效 */
    public static final int CASELEVEL_FUN_INV_STATUS_ERROR = 60073;
    /** 容器[{0}]状态更新失败 */
    public static final int CASELEVEL_CONTAINER_UPDATE_STATUS_ERROR = 60074;
    /** 商品[{0}]装箱信息更新失败 */
    public static final int CASELEVEL_CARTON_UPDATE_ERROR = 60075;
    /** ASN明细更新失败 */
    public static final int CASELEVEL_ASN_LINE_UPDATE_ERROR = 60076;
    /** ASN更新失败 */
    public static final int CASELEVEL_AS_UPDATE_ERROR = 60077;
    /** PO明细更新失败 */
    public static final int CASELEVEL_PO_LINE_UPDATE_ERROR = 60078;
    /** PO更新失败 */
    public static final int CASELEVEL_PO_UPDATE_ERROR = 60079;

    // 60001-70000 xiemingwei

    // 70001-80000 tangming
    /** 当前容器状态不允许上架*/
    public static final int COMMON_CONTAINER__NOT_PUTWAY = 70015;
    /***该商品容器中不存在*/
    public static final int SKU_IS_NOT_EXIST = 70016;
    /***该商品不是当前库位绑定的商品*/
    public static final int LOCATION_SKU_IS_NOT_EXISTS = 70017;
    /***内部容器不在托盘内*/
    public static final int INSIDECONTAINER_NOT_EXISTS_OUTCONTAINER = 70020;
   



    /** 托盘内sku商品种类数量异常 */
    public static final int PDA_MAN_MADE_PUTAWAY_SKU_AMOUNT_ERROR = 70021;
    /** 容器内有商品不允许混放，不能整托/整箱上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_SKU_NOTALLOWED_MIX = 70022;
    /** 库位号不能为空 */
    public static final int PDA_MAN_MADE_PUTAWAY_BARCODE_NULL = 70023;
    /** 库位不存在 */
    public static final int PDA_MAN_MADE_PUTAWAY_LOCATION_NULL = 70024;
    /** 库位生命周期无效 */
    public static final int PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR = 70025;
    /** 库位为静态库位，容器sku商品未能全部绑定库位，不能整托、整箱上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOTINCLUDEALLSKU = 70026;
    /** 商品状态不可用 */
    public static final int PDA_MAN_MADE_PUTAWAY_SKU_STATUS_NOT_USE = 70027;
    /** 库位不允许混放，容器存在多个sku商品，不能整托/整箱上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_NOT_MULTISKU = 70028;
    /** 容器对应的sku为空 */
    public static final int CONTAINER_NOT_FOUND_SKU = 70029;
    /** 库位不允许混放 容器存在不同sku库存属性商品，不能整托上架/箱 */
    public static final int PDA_NOT_ALLOW_DIFFERENT_INVENTORY_ATTRIBUTES = 70030;
    /** 库位不允许混放 容器存在不同sku库存属性商品，不能整托上架/箱 */
    public static final int PDA_CONTAINER_SKUATT_NOTSAME_LOCATION_SKUATT = 70031;
    /** 容器总重量已经超过库位承重，请更换库位进行上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_LOCATION_UNBEAR_WEIGHT = 70032;
    /** 库位,容器内SKU商品关键属性参数不相同，不能整托、整箱上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_ATTR_MGMT_NOT_EQUAL = 70033;
    /** 库位已有种类数+容器内SKU种类数已超过库位最大混放种类数，不能整托、整箱上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_SKU_VARIETY_OVER_MAX = 70034;
    /** 库位已有SKU属性数+容器内SKU属性数已超过库位最大混放SKU属性数，不能整托、整箱上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_SKU_ATT_OVER_MAX = 70035;
    
    /** 未找到外部容器对应的内部容器id */
    public static final int CONTAINER_NOT_FOUND_INSIDE_CONTAINER_ID = 70036;
    /** 库位为静态库位，没有绑定对应的sku商品，不能上架 */
    public static final int PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOT_SKU = 70037;
    /** 库位不允许混放 上架sku商品和库位上的商品不同,不能上架到对应库位*/
    public static final int PDA_LOC_NO_MAX_SKUATT_NO_SAME = 70038;
    /** 扫描sn或者残次信息数量大于输入数值*/
    public static final int SCAN_SKU_SN_QTY_ERROR = 70039;
    /**没有找到对应的SKU库存属性相同的记录*/
    public static final int  NO_FOUND_SKU_SAME_INV_ATTR = 70040;
    /**扫描的跟踪容器不在库位上*/
    public static final int  TRACK_CONTAINER_NO_LOCATION = 70041;
    /**没有待上架库存*/
    public static final int  NO_SHEVLES_PUTWAY_INVENTORY = 70042;
    /**商品库存信息不存在*/
    public static final int  NO_SKU_INVENTORY = 70043;
    
    
    
    
    // 70001-80000 tangming
    
    // 80001-90000 shenlijun
}
