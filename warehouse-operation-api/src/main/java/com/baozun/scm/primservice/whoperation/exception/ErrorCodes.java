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

    /** 商品数据已经失效！ */
    public static final int SKU_EXPIRY_ERROR = 1065;
    /** 库位数据已经失效！ */
    public static final int LOCATION_EXPIRY_ERROR = 1066;
    /** 已有重复的数据！ */
    public static final int DATA_DUPLICATION_ERROR = 1067;
    /** 超出库位混放数量！ */
    public static final int LOCATION_MIXCOUNT_ERROR = 1068;
    /** 获取数据失败，请刷新页面重试！ */
    public static final int GET_DATA_ERROR = 1069;
    /** 仓库残次编码[{}]已存在 */
    public static final int WH_DEFECT_TYPE_CODE_EXISTS = 1078;

    /** 仓库残次名称[{}]已存在 */
    public static final int WH_DEFECT_TYPE_NAME_EXISTS = 1079;
    /** 仓库ID空异常 */
    public static final int OUID_IS_NULL_ERROR = 1080;
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
    /** 关闭PO单失败 */
    public static final int CLOSE_PO_ERROR = 3038;


    // 20000-30000 start
    /** 容器未找到 */
    public static final int PALLET_CODE_NOT_FIND = 20001;
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
    /** 推荐的库位信息不存在！ */
    public static final int COMMON_RECOMMAND_LOCATION_IS_NOT_EXISTS = 5035;
    /** 二级容器状态不可用 */
    public static final int COMMON_TWO_CONTAINER_LIFECYCLE_IS_NOT_NORMAL = 5036;
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
    /** 容器限定唯一的商品属性，请更换容器 ! */
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
    /** 编码服务调用异常 */
    public static final int CODE_MANAGER_ERROR = 10055;
    /** 没有收货数据，请确认容器收货完成 */
    public static final int RCVD_CONTAINER_FINISH_ERROR = 10056;
    /** 没有可导出的数据 请刷新页面后重试 */
    public static final int EXPORT_EXCEL_NULL_ERROR = 10057;
    /** 找不到空闲可用的暂存库位 */
    public static final int FACILITYMATCH_NO_TEMPORARYSTORAGELOCATION = 10058;
    /** 找不到播种墙推荐路径 */
    public static final int FACILITYMATCH_NO_FACILITYRECPATH = 10059;

    /** 更新数据失败， 请重新尝试 **/
    public static final int UPDATE_FAILURE = 11079;
    /** ASN没有此商品信息 **/
    public static final int RCVD_ASN_NO_SKU_ERROR = 11080;
    /** 此状态下出库单不允许编辑 */
    public static final int ODO_EDIT_ERROR = 11082;
    /** PO单所包含的子Po状态不全为【关闭】，PO单不允许审核成功 */
    public static final int BIPO_AUDIT_SUBSTATUS_ERROR = 11083;
    /**
     * PO单状态不允许审核成功 public static final int BIPO_AUDIT_STATUS_ERROR = 11084;
     */
    /** ASN已收货完成 */
    public static final int ASN_RCVD_FINISHED = 11085;
    /** 出库单反馈数据异常 */
    public static final int ODO_CONFIRM_ERROR = 11086;
    /** 出库单加入配货模式计数器池失败 */
    public static final int ODO_DISTRIBUTIONPATTERN_ERROR = 11087;
    /** 容器二级容器类型状态异常 */
    public static final int CONTAINER2NDCATEGORY_STATUS_ERROR = 11088;
    /** 同一波次必须是同一发票公司和发票模板 */
    public static final int CREATE_WAVE_INVOICE_ERROR = 11089;
    /** 波次主档未找到可以创建波次的出库单数据 */
    public static final int CREATE_WAVE_MASTER_NULL_ODO_ERROR = 11090;
    /** 创建波次失败 */
    public static final int CREATE_WAVE_ERROR = 11091;
    /** PO单状态不允许审核成功 */
    public static final int BIPO_AUDIT_STATUS_ERROR = 11092;
    /** 仓库出库单取消节点异常 */
    public static final int WAREHOUSE_CANCEL_NODE_ERROR = 11093;
    /** 出库单不允许取消 */
    public static final int ODO_CANCEL_ERROR = 11094;
    /** 波次已经释放，波次不允许取消 */
    public static final int WAVE_CANCEL_WORK_ERROR = 11095;
    /** 库存属性标识生成异常 */
    public static final int UUID_GENERATE_ERROR = 11096;
    /** 收货反馈数据失败 */
    public static final int FEEDBACK_RCVD_ERROR = 11097;

    /** 消费者退换入请走操作台：退换货入流程 */
    public static final int PDARECEIVING_RETURNS_NO_ERROR = 11098;
    /** 关联原始单据反馈数据失败 */
    public static final int FEEDBACK_ORIGINAL_ERROR = 11099;
    /** 系统不允许部分行取消 */
    public static final int ODO_CANCEL_NO_SUPPORT_LINE_ERROR = 11100;
    /** 出库单外部单号已存在 **/
    public static final int ODO_EXTCODE_ISEXIST = 11101;
    /** PO单正在被他人操作 **/
    public static final int ASN_CREATE_SYNC_ERROR = 11104;
    /** 工作单状态非新建锁定，不允许签出 */
    public static final int ASSIGN_OUT_CHECK_STATUS_ERROR = 11102;
    /** 库位容量补货 不允许签出 */
    public static final int ASSIGN_OUT_REPLENISHMENT_STATUS_ERROR = 11103;
    /** 签出的出库单才允许取消签出 */
    public static final int CANCEL_ASSIGNOUT_CAPABLE = 11105;
    /** 库存数量不足，不可分配 */
    public static final int ALLOCATEDQTY_SHORT_ERROR = 11106;
    /** 待移入数量为0 */
    public static final int TOBEFILLEDQTY_NULL_ERROR = 11107;
    /** 分配的序列号数量不足 */
    public static final int ALLOCATED_SN_QTY_ERROR = 11108;
    /** 补货任务签入异常 */
    public static final int REPLENISHMENT_ASSIGN_IN_ERROR = 11109;
    /** 取消ASN异常 */
    public static final int CANCEL_ASN_ERROR = 11110;
    /** 拣货工作和补货工作外，其余工作类型暂时不支持签入签出 */
    public static final int ASSIGN_IN_OUT_TYPE_ERROR = 11111;
    // 10001-20000 luyimin


    // 20001-30000 shenjian
    /** 商品条码[{0}]未查询到相应的商品信息 */
    public static final int BARCODE_NOT_FOUND_SKU = 20009;
    // 20001-30000 shenjian

    // 30001-40000 zhanglei
    /** [称重] 输入的编码未查询到相应的出库信息*/
    public static final int WEIGHTING_INPUT_NOT_CORRECT = 30101;
    /** [集货] 该出库箱未能找到集货信息*/
    public static final int CONCENTRATION_NO_INFO_RETURN = 30102;
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
    /** 容器号[{0}]当前库位未找到待移入库存信息 */
    public static final int CONTAINER_NOT_FOUND_TOBEFILLED_INV_ERROR = 40027;
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
    public static final int CASELEVEL_SKU_SN_EXIST = 60002;
    /** [{0}]存在SN商品,请更换收货功能 */
    public static final int CASELEVEL_CONTAINER_NULL = 60003;
    /** 容器[{0}]不可用或已完成收货 */
    public static final int CASELEVEL_CONTAINER_UNAVAILABLE = 60004;
    /** 容器[{0}]已被[{1}]操作 */
    public static final int CASELEVEL_CONTAINER_OCCUPIED = 60005;
    /** [{0}]无caseLevel装箱信息 */
    public static final int CASELEVEL_NULL = 60006;
    /** 属性未维护,请更换收货功能 */
    public static final int CASELEVEL_SKU_ATTR_NULL = 60007;
    /** 已过保质期且不收过期商品,无法收货 */
    public static final int CASELEVEL_SKU_EXPIRED = 60008;
    /** 有效天数达不到系统要求,无法收货 */
    public static final int CASELEVEL_SKU_VALID_LT = 60009;
    /** 有效天数大于系统要求,无法收货 */
    public static final int CASELEVEL_SKU_VALID_GT = 60010;
    /** 库存类型不一致,请更换收货功能 */
    public static final int CASELEVEL_SKU_INV_TYPE_DIFF = 60011;
    /** 库存状态不一致,请更换收货功能 */
    public static final int CASELEVEL_SKU_INV_STATUS_DIFF = 60012;
    /** 需要录入残次信息,请更换收货功能 */
    public static final int CASELEVEL_SKU_IS_DEFECTIVE = 60013;
    /** 商品[{0}]不在货箱内 */
    public static final int CASELEVEL_SKU_NOT_IN_CARTON_ERROR = 60014;
    /** 商品[{0}]不可用 */
    public static final int CASELEVEL_SKU_UNAVAILABLE = 60015;
    /** 商品属性录入失败 */
    public static final int CASELEVEL_SKU_ATTR_INPUT_NULL = 60016;
    /** 缓存key错误 */
    public static final int CASELEVEL_CACHE_KEY_ERROR = 60017;
    /** 容器占用失败 */
    public static final int CASELEVEL_CONTAINER_OCCUPIED_FAILED = 60018;
    /** 日期解析异常 */
    public static final int CASELEVEL_PARSE_DATE_ERROR = 60019;
    /** 收货功能信息为空 */
    public static final int CASELEVEL_RCVD_FUN_NULL = 60020;
    /** 序列化异常 */
    public static final int CASELEVEL_SERIALIZE_ERROR = 60021;
    /** 收货数错误 */
    public static final int CASELEVEL_SKU_QTY_ERROR = 60022;
    /** 请求错误,属性扫描已结束 */
    public static final int CASELEVEL_REQUEST_ERROR = 60023;
    /** 不存在该属性的装箱记录 */
    public static final int CASELEVEL_SKU_ATTR_ERROR = 60024;
    /** 扫描序列错误 */
    public static final int CASELEVEL_SCAN_SQE_ERROR = 60025;
    /** 目标不在列表中 */
    public static final int CASELEVEL_TARGET_NOT_EXIST = 60026;
    /** SN号[{0}]已收入 */
    public static final int CASELEVEL_SN_EXIST_ERROR = 60027;
    /** SN号[{0}]不在此货箱中 */
    public static final int CASELEVEL_SN_NOT_EXIST_ERROR = 60028;
    /** 残次条码创建失败 */
    public static final int CASELEVEL_SN_DEFECT_WARE_BARCODE_ERROR = 60029;
    /** SN缓存操作失败 */
    public static final int CASELEVEL_SN_CACHE_ERROR = 60030;
    /** UUID获取失败 */
    public static final int CASELEVEL_UUID_ERROR = 60031;
    /** 缓存商品失败 */
    public static final int CASELEVEL_SKU_CACHE_ERROR = 60032;
    /** 无匹配的计划收货信息 */
    public static final int CASELEVEL_SKU_MATCH_ERROR = 60033;
    /** 缓存读取失败 */
    public static final int CASELEVEL_GET_CACHE_ERROR = 60034;
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
    /** 批量创建残次条码数量错误 */
    public static final int CASELEVEL_BATCH_NUM_SN_DEFECT_WARE_BARCODE_ERROR = 60080;


    /** 集货信息不存在 */
    public static final int SEEDING_COLLECTION_NULL = 60200;
    /** 周转箱状态不可播种 */
    public static final int SEEDING_COLLECTION_STATUS_ERROR = 60201;
    /** 周转箱明细原始库存未找到 */
    public static final int SEEDING_COLLECTION_LINE_ORG_SKUINV_NULL = 60202;
    /** 商品不在周转箱内 */
    public static final int SEEDING_TURNOVERBOX_HAVE_NOT_SKU = 60203;
    /** 存在多属性商品 */
    public static final int SEEDING_SKU_MULTIPLE_ATTR = 60204;
    /** 该商品已播种结束 */
    public static final int SEEDING_SKU_FINISHED_SEEDING = 60205;
    /** 扫描模式错误 */
    public static final int SEEDING_SCAN_PATTERN_ERROR = 60206;
    /** 播种模式错误 */
    public static final int SEEDING_SEEDING_PATTERN_ERROR = 60207;
    /** 未找到播种货格 */
    public static final int SEEDING_SEEDING_LATTICE_ERROR = 60208;
    /** 播种数量大于该出库单明细所需 */
    public static final int SEEDING_SEEDING_QTY_ERROR = 60209;
    /** 该货格不在本次商品播种内 */
    public static final int SEEDING_SEEDING_THIS_TURN_LATTICE_ERROR = 60210;
    /** 出库箱已被使用 */
    public static final int SEEDING_SEEDING_OUTBOUND_BOX_USED_ERROR = 60211;
    /** 不存在上次播种缓存数据 */
    public static final int SEEDING_SEEDING_LAST_TIME_CACHE_ERROR = 60212;
    /** 货格未绑定出库单 */
    public static final int SEEDING_SEEDING_EMPTY_LATTICE_ERROR = 60213;
    /** 出库单播种缓存数据异常 */
    public static final int SEEDING_SEEDING_ODO_SEEDING_CACHE_ERROR = 60214;
    /** 出库单明细实际已播种数量错误 */
    public static final int SEEDING_SEEDING_ODO_LINE_SEEDING_CACHE_ERROR = 60215;
    /** 播种墙不存在 */
    public static final int SEEDING_SEEDING_FACILITY_NULL_ERROR = 60216;
    /** 播种墙未绑定批次 */
    public static final int SEEDING_SEEDING_BIND_BATCH_NULL_ERROR = 60217;
    /** 批次出库单数大于播种墙货格数 */
    public static final int SEEDING_SEEDING_BATCH_ODO_QTY_ERROR = 60218;
    /** 功能显示单据号类型错误 */
    public static final int SEEDING_SEEDING_FUNCTION_SHOWCODE_ERROR = 60219;
    /** 集货状态不正确 */
    public static final int SEEDING_SEEDING_COLLECTION_STATUS_ERROR = 60220;
    /** 未找到该货格播种信息 */
    public static final int SEEDING_SEEDING_LATTICE_SEEDING_INFO_ERROR = 60221;
    /** 播种墙未配置出库箱 */
    public static final int SEEDING_SEEDING_FACILITY_BIND_BOX_ERROR = 60222;
    /** 出库箱类型不存在 */
    public static final int SEEDING_SEEDING_OUTBOUND_BOX_ERROR = 60223;
    /** 货格已绑定出库箱 */
    public static final int SEEDING_SEEDING_LATTICE_BIND_BOX_ERROR = 60224;
    /** 播种墙功能不存在 */
    public static final int SEEDING_SEEDING_FUNCTION_ERROR = 60225;
    /** 出库单信息缓存错误 */
    public static final int SEEDING_SEEDING_ODO_CACHE_ERROR = 60226;
    /** uuid创建失败 */
    public static final int SEEDING_SEEDING_CREATE_UUID_ERROR = 60227;
    /** 未找到批次下的出库单信息 */
    public static final int SEEDING_SEEDING_BATCH_ODO_INFO_NULL_ERROR = 60228;
    /** 库存不足 */
    public static final int SEEDING_SEEDING_SKUINVENTORY_SHORTAGE = 60229;
    /** 播种墙已完成播种 */
    public static final int SEEDING_FINISHED = 60230;



    /** 出库功能不存在 */
    public static final int CHECKING_FUNCTION_NULL_ERROR = 60300;
    /** 未找到复核数据 */
    public static final int CHECKING_CHECKING_INFO_NULL_ERROR = 60301;
    /** 未找到复核台信息 */
    public static final int CHECKING_FACILITY_NULL_ERROR = 60302;
    /** 批次集货未完成 */
    public static final int CHECKING_BATCH_COLLECTION_ERROR = 60303;
    /** 复核状态错误 */
    public static final int CHECKING_CHECKING_STATUS_ERROR = 60304;
    /** 当前功能不支持此复核模式 */
    public static final int CHECKING_FUNCTION_CHECKING_MODE_ERROR = 60305;
    /** 请扫描小车编码 */
    public static final int CHECKING_CHECKING_SOURCE_OUT_CONTAINER_ERROR = 60306;
    /** 请扫描播种墙编码 */
    public static final int CHECKING_CHECKING_SOURCE_SEEDING_WALL_ERROR = 60307;
    /** 请扫描播种墙或者小车 */
    public static final int CHECKING_CHECKING_SOURCE_ERROR = 60308;
    /** 批次小车集货未完成 */
    public static final int CHECKING_CHECKING_COLLECTION_ERROR = 60309;
    /** 播种墙集货未完成 */
    public static final int CHECKING_SEEDING_COLLECTION_ERROR = 60310;
    /** 复核类型错误 */
    public static final int CHECKING_CHECKING_SOURCE_TYPE_ERROR = 60311;
    /** 复核明细不存在 */
    public static final int CHECKING_CHECKING_LINE_NULL_ERROR = 60312;
    /** 出库单已取消 */
    public static final int CHECKING_ODO_CANCEL_ERROR = 60313;
    /** 请扫描小车上的出库箱 */
    public static final int CHECKING_OUT_CONTAINER_BOX_ERROR = 60314;
    /** 请扫描播种墙上的出库箱 */
    public static final int CHECKING_SEEDING_WALL_BOX_ERROR = 60315;
    /** 功能配置错误,允许引入出库箱但是不扫描出库箱 */
    public static final int CHECKING_FUNCTION_ERROR = 60316;
    /** 功能配置错误,不允许引入出库箱也不自动生成出库箱编码 */
    public static final int CHECKING_FUNCTION_CHECKING_TO_BOX_ERROR = 60317;
    /** 创建出库箱编码异常 */
    public static final int CHECKING_CRATE_OUTBOUNDBOX_CODE_ERROR = 60318;
    /** 出库箱已被使用 */
    public static final int CHECKING_OUTBOUNDBOX_USED_ERROR = 60319;
    /** 无此条码商品 */
    public static final int CHECKING_BARCODE_SKU_NULL_ERROR = 60320;
    /** 无此商品条码的出库箱 */
    public static final int CHECKING_BARCODE_SKU_BOX_NULL_ERROR = 60321;
    /** 无法确认出库箱类型 */
    public static final int CHECKING_CANNOT_CONFIRM_BOX_ERROR = 60322;
    /** 未找到耗材库位库存 */
    public static final int CHECKING_CONSUMABLE_LOCATION_NULL_ERROR = 60323;
    /** 无可用库存 */
    public static final int CHECKING_CONSUMABLE_SKUINV_NULL_ERROR = 60324;
    /** 没有该库位商品信息 */
    public static final int CHECKING_CONSUMABLE_LOCATION_SKU_NULL_ERROR = 60325;
    /** 占用耗材库存异常 */
    public static final int CHECKING_OCCUPATION_CONSUMABLE_ERROR = 60326;
    /** 耗材信息错误 */
    public static final int CHECKING_RELEASE_CONSUMABLE_PARAM_ERROR = 60327;
    /** 未找到耗材占用的库存 */
    public static final int CHECKING_OCCUPATION_CONSUMABLE_SKUINV_NULL_ERROR = 60328;
    /** 删除耗材占用的库存异常 */
    public static final int CHECKING_DELETE_OCCUPATION_CONSUMABLE_SKUINV_ERROR = 60329;
    /** 未找到耗材占用的原始库存 */
    public static final int CHECKING_ORG_OCCUPATION_CONSUMABLE_SKUINV_NULL_ERROR = 60330;
    /** 还原耗材占用库存异常 */
    public static final int CHECKING_RESTORE_ORG_OCCUPATION_CONSUMABLE_SKUINV_ERROR = 60331;
    /** 该箱已复核完毕 */
    public static final int CHECKING_BOX_CHECKING_FINISHED_ERROR = 60332;
    /** 小车释放失败 */
    public static final int CHECKING_RELEASE_TROLLEY_ERROR = 60333;
    /** 播种墙释放失败 */
    public static final int CHECKING_RELEASE_SEEDING_FACILITY_ERROR = 60334;
    /** 周转箱释放失败 */
    public static final int CHECKING_RELEASE_TURNOVERBOX_ERROR = 60335;
    /** 未找到复核的SN信息 */
    public static final int CHECKING_CHECKING_SN_ERROR = 60336;
    /** 库存不足 */
    public static final int CHECKING_SKUINV_INSUFFICIENT_ERROR = 60337;
    /** 耗材库位信息未找到 */
    public static final int CHECKING_CONSUMABLE_SKUINVLOC_ERROR = 60338;
    /** 周转箱明细原始库存未找到 */
    public static final int CHECKING_TURNOVERBOX_ORG_SKUINV_ERROR = 60339;
    /** uuid创建失败 */
    public static final int CHECKING_BOX_SKUINV_CREATE_UUID_ERROR = 60340;
    /** 耗材库存删除失败 */
    public static final int CHECKING_CONSUMABLE_SKUINV_DELETE_ERROR = 60341;
    /** 出库单更新失败 */
    public static final int CHECKING_ODO_UPDATE_ERROR = 60342;
    /** 小车/周转箱释放失败 */
    public static final int CHECKING_RELEASE_CONTAINER_ERROR = 60343;
    /** 复核头更新异常 */
    public static final int CHECKING_UPDATE_CHECKING_ERROR = 60344;
    /** 复核明细更新异常 */
    public static final int CHECKING_UPDATE_CHECKING_LINE_ERROR = 60345;
    /** 复核箱库存删除失败 */
    public static final int CHECKING_CHECKING_SKUINV_DELETE_ERROR = 60346;
    /** 复核箱库存更新失败 */
    public static final int CHECKING_CHECKING_SKUINV_UPDATE_ERROR = 60347;
    /** SN更新异常 */
    public static final int CHECKING_CHECKING_SN_UPDATE_ERROR = 60348;
    /** 物流推荐失败 */
    public static final int CHECKING_ODO_TRANSPORT_SERVICE_ERROR = 60349;
    /** 运单号已被使用 */
    public static final int CHECKING_WAYBILL_CODE_UNUSEABLE_ERROR = 60350;
    /** 该电脑未绑定复核台 */
    public static final int CHECKING_COMPUTER_BIND_ERROR = 60351;
    /** 出库单状态【{0}】不允许复核 */
    public static final int CHECKING_STATUS_ERROR = 60352;



    // 60001-70000 xiemingwei

    // 70001-80000 tangming

    /** 静态库位，如果绑定的第一个商品是不允许混放的商品，则该静态库位不允许再绑定其他商品 */
    public static final int lOCATION_NO_MIX = 70007;


    /** 所选商品已绑定出库箱 */
    public static final int EXIST_SKU_BOUND_BOX = 70010;
    /** 静态库位绑定的所有允许混放的商品混放属性必须一致 */
    public static final int lOCATION_NO_MIX_ATTR = 70013;

    /** 该库位已经绑定允许混放的商品，但该商品不能混放，不能绑定 */
    public static final int SKU_NO_MIX = 70014;

    /** 当前容器状态不允许上架 */
    public static final int COMMON_CONTAINER__NOT_PUTWAY = 70015;
    /*** 该商品容器中不存在 */
    public static final int SKU_IS_NOT_EXIST = 70016;
    /*** 该商品不是当前库位绑定的商品 */
    public static final int LOCATION_SKU_IS_NOT_EXISTS = 70017;
    /*** 内部容器不在托盘内 */
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
    /** 库位不允许混放 上架sku商品和库位上的商品不同,不能上架到对应库位 */
    public static final int PDA_LOC_NO_MAX_SKUATT_NO_SAME = 70038;
    /** 扫描sn或者残次信息数量大于输入数值 */
    public static final int SCAN_SKU_SN_QTY_ERROR = 70039;
    /** 没有找到对应的SKU库存属性相同的记录 */
    public static final int NO_FOUND_SKU_SAME_INV_ATTR = 70040;
    /** 扫描的跟踪容器不在库位上 */
    public static final int TRACK_CONTAINER_NO_LOCATION = 70041;
    /** 没有待上架库存 */
    public static final int NO_SHEVLES_PUTWAY_INVENTORY = 70042;
    /** 商品库存信息不存在 */
    public static final int NO_SKU_INVENTORY = 70043;


    /** 推荐小车不能为空 */
    public static final int OUT_CONTAINER_IS_NO_NULL = 70044;
    /** 推荐出库箱不能为空 */
    public static final int OUT_BOUNX_BOX_IS_NO_NULL = 70045;
    /** 推荐周转箱不能为空 */
    public static final int TURNOVER_BOX_IS_NO_NULL = 70046;
    /** 推荐出库箱状态不可用 */
    public static final int OUT_BOUNDBOX_IS_NOT_NORMAL = 70048;
    /** 推荐出库箱不正确 */
    public static final int OUT_BOUNX_BOX_IS_STATUS_NO = 70049;
    /** 库位库存不存在 */
    public static final int LOCATION_INVENTORY_IS_NO = 70050;
    /** 校验作业执行明细 */
    public static final int CHECK_OPERTAION_EXEC_LINE_DIFF = 70062;
    /** 作业执行明细不存在 */
    public static final int OPERATION_EXEC_LINE_NO_EXIST = 70051;
    /** 工作不存在 */
    public static final int WORK_NO_EXIST = 70052;
    /** 库位条码不正确 */
    public static final int LOCATION_BARCODE_IS_ERROR = 70053;
    /** 库位编码不正确 */
    public static final int LOCATION_CODE_IS_EEROR = 70054;
    /** 作业不存在 */
    public static final int OPATION_NO_EXIST = 70055;
    /** 提示库位失败 */
    public static final int TIP_LOCATION_FAIL = 70056;
    /** 提示周转箱失败 */
    public static final int TIP_CONTAINER_FAIL = 70057;
    /** 缓存失败 */
    public static final int CHECK_INVENTORY_IS_ERROR = 70058;

    /** 分配库存不存在 */
    public static final int ALLOCATE_INVENTORY_NO_EXIST = 70059;
    /** 补货工作结束 */
    public static final int REPLE_WORK_ISEND = 70060;
    /** 校验容器/出库箱库存 */
    public static final int CHECK_CONTAINER_INVENTORY_IS_ERROR = 70061;

    /** 待移入库存不存在 */
    public static final int TOBEFILLED_INVENTORY_NO_EXIST = 70062;
    /** 耗材不存在 */
    public static final int OUT_BOUNDBOX_NO_EXIST = 70063;
    /** 容器库存不存在 */
    public static final int CONTAINER_INVENTORY_NO_EXIST = 70064;
    /** 引入的新容器不能上架 */
    public static final int NEW_TURNOVERBOX_NO_LOC = 70065;
    /** 当前出库箱复合数量不正确 */
    public static final int CHECKING_NUM_IS_EEROR = 70065;
    /** 占用耗材不正确 */
    public static final int SUPPLIES__IS_EEROR = 70066;
    /** 服务器繁忙,请重新拣货 */
    public static final int PICKING__NO_END = 70067;
    /** 打印失败 */
    public static final int PRINT_IS_FAIL = 70068;

    // 70001-80000 tangming

    // 80001-90000 zhukai
    /** 容器[{0}]无集货数据 */
    public static final int COLLECTION_CONTAINER_DATA_NULL_ERROR = 80001;
    /** 容器[{0}]不在暂存库位上 */
    public static final int COLLECTION_CONTAINER_NOT_IN_TEMPORARYLOCATION = 80002;
    /** 容器[{0}]不在中转库位上 */
    public static final int COLLECTION_CONTAINER_NOT_IN_LOCATION = 80003;
    /** 该容器已扫描过 */
    public static final int COLLECTION_RECOMMEND_RESULT_REPEAT = 80004;
    /** 该容器[{0}]与之前扫描的容器批次不同 */
    public static final int COLLECTION_CONTAINER_BATCH_DIFFERENCE = 80005;
    /** 没有需要操作的容器 */
    public static final int COLLECTION_CONTAINER_QTY_IS_NULL = 80006;
    /** 容器[{0}]不在已携带容器中 */
    public static final int COLLECTION_CONTAINER_IS_NOT_IN_SCAN_LIST = 80007;
    /** 请在播种墙,暂存库位,中转库位中扫描正确的设施编码 */
    public static final int COLLECTION_DESTINATION_NOT_RIGHT = 80008;
    /** 推荐失败无法携带 */
    public static final int COLLECTION_RECOMMEND_RESULT_ERROR = 80009;
    /** 系统错误,推荐结果不唯一 */
    public static final int COLLECTION_RECOMMEND_RESULT_NOT_UNIQE = 80010;
    /** 该容器[{0}]不在集货数据中 */
    public static final int COLLECTION_NOT_HAVE_CONTAINER_INFO = 80012;
    /** 系统没有匹配到推荐的播种墙,请稍后再试 */
    public static final int COLLECTION_SYS_NO_MATCH_SEEDINGWALL = 80013;
    /** 系统错误,未找到系统推荐路径 */
    public static final int COLLECTION_RECOMMEND_PATH_ERROR = 80014;
    /** 波次[{}]中出库单集合含有不同的发票信息 */
    public static final int WAVE_ODOLIST_INVOICE_DIFFERENCE = 80011;
    /** 出库单排序失败,请检查波次主档销售清单排序设置或打印条件销售清单排序 */
    public static final int WAVE_ODOINDEX_SORT_ERROR = 80015;
    /** 出库单不为取消状态,无法操作 */
    public static final int ODO_NOT_CANCEL_STATUS = 80016;
    /** 该容器号没有找到库存信息 */
    public static final int NOT_FIND_SKU_INV_BY_CONTAINERCODE = 80017;
    /** 数据异常,出库单信息不存在 */
    public static final int ODO_NOT_FIND = 80018;
    /** 错误,库存中包含多条出库单信息 */
    public static final int SKU_INV_HAVE_MORE_ODO = 80019;
    // 80001-90000 zhukai

    // 100001-110000 liuqiming
    /** 波次头信息为空 */
    public static final int WHWAVE_IS_NULL = 100001;
    /** 工作明细数量不正确 */
    public static final int WORK_LINE_TOTAL_ERROR = 100002;
    /** 数量不正确 */
    public static final int QTY_ERROR = 100003;
    /** 分配量与待移入量不相等 */
    public static final int QTY_TOQTY_ERROR = 100004;
    /** 作业明细数量不正确 */
    public static final int OPERATION_LINE_TOTAL_ERROR = 100005;
    /** 分配库存列表为空 */
    public static final int ALLOCATED_LIST_IS_NULL = 100005;
    /** 配货模式不存在 */
    public static final int DISTRIBUTION_PATTERN_RULE_IS_NULL = 100006;
    /** 工作明细信息列表 */
    public static final int WORK_LINE_IS_NULL = 100006;
    /** 目标库位客户不唯一 */
    public static final int IN_WAREHOUSE_MOVE_CUSTOMER_ERROR = 100007;
    /** 超过最大混放SKU数 */
    public static final int IN_WAREHOUSE_MOVE_MIX_STACKING_NUMBER_ERROR = 100008;
    /** 超过最大混放SKU属性数 */
    public static final int IN_WAREHOUSE_MOVE_MAX_CHAOS_SKU_ERROR = 100009;
    /** 目标库位不允许混放 */
    public static final int IN_WAREHOUSE_MOVE_MIX_STACKING_ERROR = 100010;
    /** 静态库位必须摆放指定商品 */
    public static final int IN_WAREHOUSE_MOVE_ISSTATIC_ERROR = 100011;
    /** 创建库内移动工作失败 */
    public static final int CREATE_IN_WAREHOUSE_MOVE_WORK_ERROR = 1000012;
    /** 创建库内移动工作失败 */
    public static final int PRINT_ERROR = 1000013;
    // 100001-110000 liuqiming
    // 110000-120000 xuhui
    /** 交货交接规则为空 */
    public static final int HAND_OVER_COLLECTION_RULE_IS_NULL = 110001;
    /** 出库箱为空 */
    public static final int OUT_BOUNX_BOX_IS_NULL = 110002;
    /** 推荐的库位为空 请重试 */
    public static final int RECOMMEND_OUTBOUND_ERROR = 110003;
    /** 推荐失败，请联系管理员 */
    public static final int RECOMMEND_OUTBOUND_IS_NULL = 110004;
    /** 推荐的交接工位信息不完整，无上限信息 */
    public static final int HANDOVER_STATION_IS_NULL = 110005;
    /** 该批次已交接 */
    public static final int HANDOVER_EXISTS = 110006;
    /** 交接集货信息为空 */
    public static final int HANDOVER_COLLECTION_IS_NULL = 110007;
    /** 插入交接表失败 */
    public static final int HANDOVER_INSERT_ERRPR = 110008;
    /** 库存删除失败或无该库存 */
    public static final int SKUINVENTORY_DELETE_ERROR = 110009;
    /** 修改出库单状态出错 */
    public static final int ODO_SAVEORUPDATEBYVERSION_ERROR = 110010;
    /** 修改出库明细单状态出错 */
    public static final int ODOLINE_SAVEORUPDATEBYVERSION_ERROR = 110011;
    /** 出库箱对应库存为空 */
    public static final int SKUINVENTORY_IS_NULL = 110012;
    /** 出库单下的出库箱并没有全都完成交接 */
    public static final int ODO_OUTBOUNDBOX_NOT_HANDOVER_ALL = 110013;
    /** 该ASN已收货完成且不能超收 */
    public static final int ASN_RCVD_ALREADY_FINISHED = 110014;
    /** 出库箱尚未打印面单或箱标签，请打印面单或箱标签 */
    public static final int OUTBOUNDBOX_NOT_PRINT = 110015;
    /** 该出库箱尚未复核或称重 */
    public static final int OUTBOUNDBOX_NOT_CHECK_OR_WEIGHT = 110016;
    /** 推荐失败 */
    public static final int RECOMMEND_FAILED = 110017;
    /** 推荐的交接库位不存在 */
    public static final int RECOMMEND_STATION_DONT_EXSIT = 110018;
    /** 该电脑没绑定复核台 */
    public static final int COMPUTER_NOT_CONNECT_FACILITY = 110019;
    /** 该出库箱已交接出库 */
    public static final int OUTBOUNDBOX_ALREADY_HANDOVER = 110020;
    /** 该出库箱尚未计算计重 */
    public static final int OUTBOUNDBOX_NOT_CALC__WEIGHT = 110021;
    /** 该出库箱无对应运单信息 */
    public static final int NO_ODO_DELIVERY_INFO = 110022;
    /** 该出库箱未绑定运单号或物流商编码 */
    public static final int NO_WAYBILL_CODE_OR_TRANSPORT_CODE = 110023;
    /** 该面单未绑定出库箱 */
    public static final int WAYBILL_CODE_NO_OUTBOUNDBOX_CODE = 110024;
    // 120000-130000 zhaozili
    /** 出库箱不存在 */
    public static final int OUT_BOUND_BOX_NOT_EXSIT = 120001;
    /** 出库箱不满足容器拆分条件 */
    public static final int OUT_BOUND_BOX_NOT_MOVE_CONDITION = 120002;
    /** 目标出库箱出库单号与原始出库箱/货格出库单不一致 */
    public static final int OUT_BOUND_BOX_ODO_CODE_NOT_MATCH = 120003;
    /** 容器号[{0}]未找到库存信息 */
    public static final int CONTAINER_NOT_FOUND_INV_ERROR = 120004;
    /** 容器号[{0}]未找到库存信息 */
    public static final int CONTAINER_INVENTORY_STATISTIC_ERROR = 120005;
    // 120000-130000 zhaozili

    /** 文件不存在 */
    public static final int IMPORT_ERROR_FILE_NOT_EXISTS = 60203;
    /** 导入失败 */
    public static final int IMPORT_ERROR = 60202;
}
