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

    /** 调编码生成器 补货单实体标识 */
    public static final String BH_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask";

    /** 调编码生成器 容器实体标识 */
    public static final String CONTAINER_MODEL_URL = "com.baozun.scm.primservice.whinfo.model.warehouse.Container";

    /** 调编码生成器Asn预约实体标识 */
    public static final String ASN_RESERVE_MODEL_URL = "com.baozun.scm.primservice.whinfo.model.warehouse.AsnReserve";

    /** 波次明细 */
    public static final String WAVE_LINE_URL = "com.baozun.scm.primservice.whinfo.model.odo.wave.WhWaveLine";


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

    /** 功能模板 入库分拣功能 */
    public static final String FUNCTION_TEMPLET_INBOUND = "inbound";

    /** 功能模板 收货功能 */
    public static final String FUNCTION_TEMPLET_RECEIVE = "receive";

    /** 功能模板 上架功能 */
    public static final String FUNCTION_TEMPLET_SHELF = "shelf";

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

    /** Asn预约普通 */
    public static final String ASN_RESERVE_NORMAL = "1";
    /** Asn预约紧急 */
    public static final String ASN_RESERVE_URGENT = "2";

    /** 未匹配规则 */
    public static final Integer NO_MATCHING_RULES = 0;

    /** 无可用月台 */
    public static final Integer NONE_AVAILABLE_PLATFORMS = 1;

    /** 可用月台 */
    public static final Integer AVAILABLE_PLATFORM = 2;

    /** ASN预约优先级 */
    public static final String ASN_RESERVE_LEVEL = "ASN_RESERVE_LEVEL";

    /** asn签入类型 */
    public static final String CHECK_IN_TYPE_NORMAL_CHECKIN = "normalCheckIn";
    /** 分配月台签入类型 */
    public static final String CHECK_IN_TYPE_MANUAL_CHECKIN = "manualCheckIn";

    /** 分配规则 */
    public static final String ALLOCATE_RULE = "ALLOCATE_RULE";
    public static final String ALLOCATE = "ALLOCATE";
    public static final String WAVE_CREATE = "WAVE_CREATE";

    /** 分配策略 */
    public static final String ALLOCATE_STRATEGY = "ALLOCATE_STRATEGY";
    /** 分配单位 */
    public static final String PRODUCT_UNIT = "PRODUCT_UNIT";
    /** 补货策略 */
    public static final String REPLENISH_STRATEGY = "REPLENISH_STRATEGY";
    /** 分配区域 */
    public static final String AREA_TYPE_ALLOCATE_AREA = "allocatearea";

    /** 补货规则 货品规则类型 */
    public static final String RULE_TYPE_REPLENISHMENT_SKU = "REPLENISHMENT_SKU";
    /** 补货规则 库位规则类型 */
    public static final String RULE_TYPE_REPLENISHMENT_LOCATION = "REPLENISHMENT_LOCATION";

    /** 出库箱规则 */
    public static final String RULE_TYPE_OUTBOUND_BOX = "OUTBOUND_BOX";
    /** 装箱排序\拆分 */
    public static final String RULE_TYPE_OUTBOUND_BOX_TACTICS = "OUTBOUND_BOX_TACTICS";

    // 库存状态
    public static final Long INVENTORY_STATUS_GOOD = 3L;// 良品
    public static final Long INVENTORY_STATUS_DEFEATSALE = 4L; // 残次可销售
    public static final Long INVENTORY_STATUS_DEFEATNOTSALE = 5L; // 残次不可销售
    public static final Long INVENTORY_STATUS_PENDING = 6L; // 待处理
    public static final Long INVENTORY_STATUS_OBSOLESCENT = 7L; // 待报废
    public static final Long INVENTORY_STATUS_DEADLINESALE = 8L; // 临近保质期

    // 商品库存
    public static final int INVENTORY_SN_STATUS_ONHAND = 1;// 在库
    public static final int INVENTORY_SN_STATUS_ALLOCATED = 2;// 分配
    public static final int INVENTORY_SN_STATUS_FROZEN = 3;// 冻结

    // 残次品条码编码生成实体
    public static final String INVENTORY_DEFECT_WARE_BARCODE = "DEFECT_WARE";
    // 残次品条码编码生成 前缀
    // public static final String INVENTORY_SN_BARCODE_PREFIX = "SN";

    // 商品属性管理跳转
    public static final String ATTR_CONTROL_HEADER = "containerReceiving/receiving-sku-"; // 页面头
    public static final String VALID_DATE = "validDate"; // 管理效期
    public static final String BATCH_NO = "batchNo"; // 管理批次号
    public static final String COUNTRY = "country"; // 管理原产地
    public static final String INV_TYPE = "invType"; // 管理库存类型
    public static final String INV_ATTR_1 = "invAttr1"; // 管理库存属性1
    public static final String INV_ATTR_2 = "invAttr2"; // 管理库存属性2
    public static final String INV_ATTR_3 = "invAttr3"; // 管理库存属性3
    public static final String INV_ATTR_4 = "invAttr4"; // 管理库存属性4
    public static final String INV_ATTR_5 = "invAttr5"; // 管理库存属性5
    public static final String INV_STATUS = "invStatus"; // 管理库存状态
    public static final String DEFECTIVE = "defective"; // 残次品类型及残次原因
    public static final String SN = "sn"; // 管理序列号
    public static final String FINISH = "finish"; // 收货完成


    /** 字典表 一级容器类型 */
    public static final String DICTIONARY_CONTAINTER_TYPE = "ONE_LEVEL_TYPE";

    /** 字典表 一级容器类型 托盘类 */
    public static final String CONTAINER_TYPE_PALLET = "PALLET_TYPE";

    /** 字典表 一级容器类型 货箱类 */
    public static final String CONTAINER_TYPE_BOX = "BOX_TYPE";

    /** 字典表 二级容器类型 系统自定义托盘类 */
    public static final String CONTAINER_TYPE_2ND_PALLET = "SYS_PALLET_TYPE";

    /** 字典表 二级容器类型 系统自定义货箱类 */
    public static final String CONTAINER_TYPE_2ND_BOX = "SYS_BOX_TYPE";

    /** 二级容器类型 */
    public static final String DICTIONARY_CONTAINTER_SECOND_TYPE = "TWO_LEVEL_TYPE";

    public static final int RCVD_ISVALID = 0;// 0：是否管理效期
    public static final int RCVD_ISBATCHNO = 1; // 1:是否管理批次号
    public static final int RCVD_ISCOUNTRYOFORIGIN = 2;// 2:是否管理原产地
    public static final int RCVD_ISINVTYPE = 3; // 3:是否管理库存类型
    public static final int RCVD_INVATTR1 = 4; // 4:是否管理库存属性1
    public static final int RCVD_INVATTR2 = 5; // 5:是否管理库存属性2
    public static final int RCVD_INVATTR3 = 6;// 6:是否管理库存属性3
    public static final int RCVD_INVATTR4 = 7; // 7:是否管理库存属性4
    public static final int RCVD_INVATTR5 = 8;// 8:是否管理库存属性5
    public static final int RCVD_ISINVSTATUS = 9; // 9:是否管理库存状态
    public static final int RCVD_ISDEFEAT = 10;// 10:残次品类型及残次原因
    public static final int RCVD_ISSERIALNUMBER = 11;// 11:是否管理序列号

    /** 库存明细残次类型来源 店铺 */
    public static final String SKU_SN_DEFECT_SOURCE_STORE = "STORE";

    /** 库存明细残次类型来源 仓库 */
    public static final String SKU_SN_DEFECT_SOURCE_WH = "WH";

    /** 上架规则sql占位符 */
    public static final String SHELVE_RULE_PLACEHOLDER = "${insideContainerIdListStr}";

    /** 补货规则商品规则skuId占位符 */
    public static final String REOLENISHMENT_RULE_SKUID_LIST_PLACEHOLDER = "${skuIdListStr}";
    /** 补货规则库位规则locationId占位符 */
    public static final String REOLENISHMENT_RULE_LOCATIONID_LIST_PLACEHOLDER = "${locationIdListStr}";

    /** 复核台推荐规则出库单ID占位符 */
    public static final String CHECK_OPERATIONS_AREA_ODOID_LIST_PLACEHOLDER = "${odoIdListStr}";
    /** 播种墙推荐规则出库单ID占位符 */
    public static final String SEEDING_WALL_ODOID_LIST_PLACEHOLDER = "${odoIdListStr}";

    /** 出库箱装箱规则sql占位符 */
    public static final String OUTBOUNDBOX_RULE_PLACEHOLDER = "${odoIdListStr}";

    /** 库存 占用单据来源 ASN */
    public static final String SKU_INVENTORY_OCCUPATION_SOURCE_ASN = "ASN";
    public static final String SKU_INVENTORY_OCCUPATION_SOURCE_ODO = "ODO";

    // 序列号管理类型
    public static final String SERIAL_NUMBER_TYPE_IN = "1";// 入库管
    public static final String SERIAL_NUMBER_TYPE_OUT = "2";// 出库管
    public static final String SERIAL_NUMBER_TYPE_ALL = "3";// 全部管
    public static final String SERIAL_NUMBER_TYPE_ALL_NOT = "4";// 全部不管

    // 度量单位：日期类型
    public static final String TIME_UOM_YEAR = "year";
    public static final String TIME_UOM_MONTH = "month";
    public static final String TIME_UOM_DAY = "day";

    // 默认true:false
    public static final boolean BOOLEAN_TRUE = true;
    public static final boolean BOOLEAN_FALSE = false;

    /**
     * 系统字典表
     */
    public static final String IS_WHOLE_ORDER_OUTBOUND = "IS_WHOLE_ORDER_OUTBOUND";

    public static final String PART_OUTBOUND_STRATEGY = "PART_OUTBOUND_STRATEGY";

    public static final String ODO_CROSS_DOCKING_SYSMBOL = "ODO_CROSS_DOCKING_SYSMBOL";

    public static final String TRANSPORT_MODE = "TRANSPORT_MODE";

    public static final String ODO_ORDER_TYPE = "ODO_ORDER_TYPE";

    public static final String ODO_PRE_TYPE = "ODO_PRE_TYPE";

    public static final String ODO_TYPE = "ODO_TYPE";

    public static final String DISTRIBUTE_MODE = "DISTRIBUTE_MODE";

    public static final String ODO_STATUS = "ODO_STATUS";
    public static final String ODO_LINE_STATUS = "ODO_LINE_STATUS";

    public static final String ODO_AIM_TYPE = "ODO_AIM_TYPE";

    public static final String ODO_DELIVER_GOODS_TIME_MODE = "ODO_DELIVER_GOODS_TIME_MODE";

    public static final String INCLUDE_FRAGILE_CARGO = "INCLUDE_FRAGILE_CARGO";

    public static final String INCLUDE_HAZARDOUS_CARGO = "INCLUDE_HAZARDOUS_CARGO";

    public static final String ASNLINESTATUS = "ASNLINESTATUS";

    public static final String IS_NOT = "IS_NOT";

    // 出库单默认优先级
    public static final Integer ODO_DEFAULT_PRIORITYLEVLE = 50;

    /** 调编码生成器出库单实体标识 */
    public static final String WHODO_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.odo.WhOdo";

    /** 调编码生成器波次实体标识 */
    public static final String WHWAVE_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave";

    /** 调编码生成器工作头实体标识 */
    public static final String WHWORK_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.warehouse.WhWork";

    /** 调编码生成器工作明细实体标识 */
    public static final String WHWORKLINE_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine";

    /** 调编码生成器作业头实体标识 */
    public static final String WHOPERATION_MODEL_URL = "com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation";

    /** 调编码生成器 出库单WMS内部单号分组 */
    public static final String WMS_ODO_INNER = "WMS_ODO_INNER";

    /** 调编码生成器 出库单WMS外部单号分组 */
    public static final String WMS_ODO_EXT = "WMS_ODO_EXT";

    /** 出库单增值服务类型 */
    public static final String ODO_VAS_TYPE_WH = "WH";
    public static final String ODO_VAS_TYPE_EXPRESS = "EXPRESS";

    /** TABLE_FIELD 类型 */
    public static final String TABLE_FIELD_TYPE_WH_ASN = "WH_ASN";

    // caseLevel收货属性扫描序列
    public static final String CASELEVEL_SCAN_SIGN_START = "start";// 开始
    public static final String CASELEVEL_SCAN_SIGN_END = "end";// 结束
    public static final String CASELEVEL_SCAN_SIGN_ISVALID = "isValid";// 是否管理效期
    public static final String CASELEVEL_SCAN_SIGN_ISBATCHNO = "isBatchNo"; // 是否管理批次号
    public static final String CASELEVEL_SCAN_SIGN_ISCOUNTRYOFORIGIN = "isCountryOfOrigin";// 是否管理原产地
    public static final String CASELEVEL_SCAN_SIGN_INVATTR1 = "invAttr1"; // 是否管理库存属性1
    public static final String CASELEVEL_SCAN_SIGN_INVATTR2 = "invAttr2"; // 是否管理库存属性2
    public static final String CASELEVEL_SCAN_SIGN_INVATTR3 = "invAttr3";// 是否管理库存属性3
    public static final String CASELEVEL_SCAN_SIGN_INVATTR4 = "invAttr4"; // 是否管理库存属性4
    public static final String CASELEVEL_SCAN_SIGN_INVATTR5 = "invAttr5";// 是否管理库存属性5
    public static final String CASELEVEL_SCAN_SIGN_ISINVTYPE = "isInvType"; // 是否管理库存类型
    public static final String CASELEVEL_SCAN_SIGN_ISINVSTATUS = "isInvStatus"; // 是否管理库存状态
    public static final String CASELEVEL_SCAN_SIGN_ISDEFEAT = "isDefeat";// 残次品类型及残次原因

    public static final String INV_ATTR_TYPE = "1"; // 库存类型
    public static final String INV_ATTR_STATUS = "2"; // 库存状态
    public static final String INV_ATTR_BATCH = "3"; // 批次号
    public static final String INV_ATTR_MFG_DATE = "4"; // 生产日期
    public static final String INV_ATTR_EXP_DATE = "5"; // 失效日期
    public static final String INV_ATTR_ORIGIN = "6"; // 原产地
    public static final String INV_ATTR1 = "7"; // 库存属性1
    public static final String INV_ATTR2 = "8"; // 库存属性2
    public static final String INV_ATTR3 = "9"; // 库存属性3
    public static final String INV_ATTR4 = "10"; // 库存属性4
    public static final String INV_ATTR5 = "11"; // 库存属性5


    public static final String WAVE_STATUS = "WAVE_STATUS";// 波次状态
    public static final String WH_WAVE_PHASE = "WH_WAVE_PHASE";// 波次阶段

    public static final Integer SN_DEFECR_COUNT = 1;

    /** 配货模式规则 */
    public static final String DISTRIBUTION_PATTERN = "DISTRIBUTION_PATTERN";

    /** 波次阶段 分配失败原因分组 */
    public static final String WAVE_FAIL_REASON = "WAVE_FAIL_REASON";
    /** 硬分配阶段 0：分配规则 */
    public static final Integer HARD_PHASE_RULE = 0;
    /** 硬分配阶段 1：硬分配 */
    public static final Integer HARD_PHASE_ALLOCATION = 1;
    /** 硬分配阶段 分配规则失败 */
    public static final String RULE_ALLOCATION_FAILURE = "ERROR004";
    /** 硬分配阶段 库存不足 没有空库位和静态库位超分配 */
    public static final String INVENTORY_SHORTAGE = "ERROR005";
    /** 补货阶段 商品未匹配到补货规则 */
    public static final String REPLENISHED_SKU_NO_RULE = "ERROR001";
    /** 补货阶段 未匹配到目标库位 */
    public static final String REPLENISHED_NO_TARGET_LOCATION = "ERROR002";
    /** 补货阶段 补货不足失败 */
    public static final String REPLENISHED_FAIL = "ERROR003";
    /** 波次配货模式计算阶段未匹配到配货模式 */
    public static final String DISTRIBUTE_MODE_FAIL = "ERROR006";
    /** 出库箱推荐阶段 未找到可用容器 */
    public static final String CREATE_OUTBOUND_CARTON_UNMATCHED_BOX = "ERROR007";
    /** 出库箱推荐阶段 发生异常 */
    public static final String CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION = "ERROR008";
    /** 出库单配货模式不存在或不可用 */
    public static final String CREATE_OUTBOUND_CARTON_DISTRIBUTE_MODE_ERROR = "ERROR009";
    /** 摘果出库单明细分组不存在 */
    public static final String CREATE_OUTBOUND_CARTON_SPLIT_REQUIRE_ERROR = "ERROR010";
    /** 出库单明细没有占用库存 */
    public static final String CREATE_OUTBOUND_CARTON_OCC_INVENTORY_ERROR = "ERROR011";
    /** 软分配阶段失败 */
    public static final String SOFT_ALLOCATION_FAIL = "ERROR012";

    public static final String ALLOCATE_STRATEGY_FIRSTINFIRSTOUT = "1"; // 先入先出
    public static final String ALLOCATE_STRATEGY_FIRSTINLASTOUT = "2"; // 先入后出
    public static final String ALLOCATE_STRATEGY_FIRSTEXPIRATIONFIRSTOUT = "3"; // 先到期先出
    public static final String ALLOCATE_STRATEGY_FIRSTEXPIRATIONLASTOUT = "4"; // 后到期先出
    public static final String ALLOCATE_STRATEGY_QUANTITYBESTMATCH = "5"; // 数量最佳匹配
    public static final String ALLOCATE_STRATEGY_MAXIMUMSTORAGESPACE = "6"; // 最大存储空间
    public static final String ALLOCATE_STRATEGY_MINIMUMORDERPICKINGTIMES = "7"; // 最小拣货次数
    public static final String ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT = "8"; // 静态库位可超分配
    public static final String ALLOCATE_STRATEGY_STATICLOCATIONNOTCANASSIGNMENT = "9"; // 静态库位不可超分配
    public static final String ALLOCATE_STRATEGY_MIXEDSKUSLOCATION = "10"; // 混SKU库位
    public static final String ALLOCATE_STRATEGY_EMPTYLOCATION = "11"; // 空库位

    public static final String ALLOCATE_UNIT_TP = "TP"; // 托盘
    public static final String ALLOCATE_UNIT_HX = "HX"; // 货箱
    public static final String ALLOCATE_UNIT_PIECE = "PIECE"; // 件

    public static final String REPLENISHMENT_UP = "1"; // 向上补货
    public static final String REPLENISHMENT_DOWN = "2"; // 向下补货
    public static final String REPLENISHMENT_ONDEMAND = "3";// 严格按照需求量

    /** 体积单位升在Uom表中的编码 */
    public static final String LENGTH_TO_VOLUME_UOM_CODE = "LENGTH_TO_VOLUME_CODE";

    /** 软分配阶段 */
    public static final String WEAK_ALLOCATED = "WEAK_ALLOCATED";
    /** 使用外部容器(小车)无出库箱拣货流程 */
    public static final Integer PICKING_WAY_ONE = 1;
    /** 使用外部容器(小车)有出库箱拣货流程 */
    public static final Integer PICKING_WAY_TWO = 2;
    /** 使用出库箱拣货流程 */
    public static final Integer PICKING_WAY_THREE = 3;
    /** 使用周转箱拣货流程 */
    public static final Integer PICKING_WAY_FOUR = 4;
    /** 整托拣货流程 */
    public static final Integer PICKING_WAY_FIVE = 5;
    /** 整箱拣货流程 */
    public static final Integer PICKING_WAY_SIX = 6;

    /** 仅占用托盘内商品 */
    public static final Integer INV_OCCUPY_MODE_ONE = 1;
    /** 仅占用货箱内商品 */
    public static final Integer INV_OCCUPY_MODE_TWO = 2;
    /** 仅占用库位上散件商品 */
    public static final Integer INV_OCCUPY_MODE_THREE = 3;
    /** 混合占用 */
    public static final Integer INV_OCCUPY_MODE_FOUR = 4;
    /** 修改出库单状态-合并出库单 */
    public static final int ODO_MERGE = 1;
    /** 修改出库单状态-取消合并出库单 */
    public static final int ODO_CANCEL = 2;
    /** 修改出库单状态-原始出库单还原为新建 */
    public static final int ODO_NEW = 3;
    /** 修改出库单状态-合并出库单明细 */
    public static final int ODO_LINE_MERGE = 1;
    /** 修改出库单状态-合并出库单明细-取消 */
    public static final int ODO_LINE_CANCEL = 2;
    /** 修改出库单状态-原始出库单明细还原为新建 */
    public static final int ODO_LINE_NEW = 3;

    /** 播种墙推荐规则 */
    public static final String RULE_TYPE_SEEDING_WALL = "SEEDING_WALL";
    /** 复核台推荐规则 */
    public static final String RULE_TYPE_CHECK_OPERATIONS_AREA = "CHECK_OPERATIONS_AREA";
    /** 工作号 */
    public static final String WORK_CODE = "1";
    /** 库位号 */
    public static final String LOC_CODE = "2";
    /** 容器号 */
    public static final String CONTAINER_CODE = "3";
    /** 出库小批次 */
    public static final String OUT_BOUND = "4";
    /** 波次号 */
    public static final String WAVE_CODE = "5";
    /** 出库箱 */
    public static final String OUT_BOUND_BOX = "6";

    /** 配置为原始库位 */
    public static final String FROM_LOCATION = "FROM_LOCATION";
    /** 配置为原始库位与目标库位 */
    public static final String FROM_LOCATION_TO_LOCATION = "FROM_LOCATION_TO_LOCATION";
    /** 配置为原始库位托盘 */
    public static final String FROM_OUTER_CONTAINER = "FROM_OUTER_CONTAINER";
    /** 配置为原始库位托盘与目标库位 */
    public static final String FROM_OUTER_CONTAINER_TO_LOCATION = "FROM_OUTER_CONTAINER_TO_LOCATION";
    /** 配置为原始库位货箱 */
    public static final String FROM_INSIDE_CONTAINER = "FROM_INSIDE_CONTAINER";
    /** 配置为原始库位货箱与目标库位 */
    public static final String FROM_INSIDE_CONTAINER_TO_LOCATION = "FROM_INSIDE_CONTAINER_TO_LOCATION";
    /** 配置为目标库位 */
    public static final String TO_LOCATION = "TO_LOCATION";

    /** 非整托整箱拣货流程 */
    public static final Integer REPLENISH_WAY_ONE = 1;
    /** 整托拣货流程 */
    public static final Integer REPLENISH_WAY_TWO = 2;
    /** 整箱拣货流程 */
    public static final Integer REPLENISH_WAY_THREE = 3;

    /** 导入EXCEL 错误信息文件上传路径 zk 配置 */
    public static final String IMPORT_EXCEL_ZK_ERROR_URL = "/upload.file.error.url";

    /** 导入EXCEL 文件上传路径 zk 配置 */
    public static final String IMPORT_EXCEL_ZK_URL = "/upload.file.url";

    public static final String ERROR = "ERROR";

    public static final String OK = "OK";

    /** 导入EXCEL信息CODE */
    public static final String IMPORT_EXCEL_CODE = "IMPORT_EXCEL_CODE";

    /** 导出EXECL文件后缀 .xls */
    public static final String EXPORT_EXCEL_XLS = ".xls";

    /** 导出EXECL文件后缀 .xlsx */
    public static final String EXPORT_EXCEL_XLSX = ".xlsx";

    /** 导出EXCEL上传路径 zk 配置 */
    public static final String EXPORT_EXCEL_ZK_URL = "/export.excel.url";

    /** 导入EXCEL模板下载路径 ZK 配置 */
    public static final String DOWNLOAD_EXCEL_ZK_URL = "/download.excel.url";

    /**
     * 仓库出库设施增加状态
     */
    public static final Integer WH_FACILITY_STATUS_1 = 1;// 空闲
    public static final Integer WH_FACILITY_STATUS_2 = 2;// 占用
    public static final Integer WH_FACILITY_STATUS_3 = 3;// 播种中
    public static final Integer WH_FACILITY_STATUS_4 = 4;// 播种完成
    public static final Integer WH_FACILITY_STATUS_5 = 5;// 播种异常

    /**
     * 仓库暂存库位增加状态
     */
    public static final Integer WH_GLOBAL_STATUS_1 = 1;// 空闲
    public static final Integer WH_GLOBAL_STATUS_2 = 1;// 占用

    /** 拣货模式:播种*/
    public static final String WH_PICKING_MODE = "2";

    /** 播种模式 */
    public static final String WH_SEEDING_WALL = "SEEDING_WALL_MODE";

    /** 播种模式 */
    public static final String SEEDING_MODE_1 = "1";// 边拣货边播种
    public static final String SEEDING_MODE_2 = "2";// 拣完货再播种

    /** 扫描播种墙 */
    public static final String TARGET_1 = "SEEDING_WALL";
    /** 扫描播种墙校验码 */
    public static final String TARGET_2 = "SEEDING_WALL_CHECK_CODE";
    /** 扫描出库暂存库位 */
    public static final String TARGET_3 = "TEMPORARY_STORAGE_LOCATION";
    /** 扫描出库暂存库位校验码 */
    public static final String TARGET_4 = "TEMPORARY_STORAGE_LOCATION_CHECK_CODE";
    /** 扫描中转库位 */
    public static final String TARGET_5 = "TRANSIT_LOCATION";
    /** 扫描中转库位校验码 */
    public static final String TARGET_6 = "TRANSIT_LOCATION_CHECK_CODE";

    /** 目标位置类型:播种墙 */
    public static final int SEEDING_WALL = 1;
    /** 目标位置类型:出库暂存库位 */
    public static final int TEMPORARY_STORAGE_LOCATION = 2;
    /** 目标位置类型:中转库位 */
    public static final int TRANSIT_LOCATION = 3;

    /** 播种墙容量满足一次移动所有容器 */
    public static final int SEEDING_WALL_SUFFICIENT = 1;
    /** 每次只能移动一个容器到播种墙 */
    public static final int SEEDING_WALL_MOVE_ONE = 2;
    /** 播种墙剩余容量不满足, 容器只能移到暂存库位 */
    public static final int SEEDING_WALL_NOT_SUFFICIENT = 3;

    /** 复核台 */
    public static final String FACILITY_TYPE_CHECKTABLE = "checkTable";
    /** 播种墙 */
    public static final String FACILITY_TYPE_SEEDINGWALL = "seedingWall";
    /** 复核台组 */
    public static final String FACILITY_GROUP_TYPE_CHECKTABLE = "checkTableGroup";
    /** 播种墙组 */
    public static final String FACILITY_GROUP_TYPE_SEEDINGWALL = "seedingWallGroup";
    /** 复核暂存库位 */
    public static final String TEMPORARY_STORAGE_CHECKTABLE = "checkTableTemporaryStorage";
    /** 出库暂存库位 */
    public static final String TEMPORARY_STORAGE_OUTBOUND = "outBoundTemporaryStorage";

    /** 越库标识 */
    public static final String ODO_CROSS_DOCKING_SYSMBOL_1 = "1";// 库存不足支持越库
    public static final String ODO_CROSS_DOCKING_SYSMBOL_2 = "2";// 不支持越库
    public static final String ODO_CROSS_DOCKING_SYSMBOL_3 = "3";// 越库

    /** 箱标签(出库) */
    public static final String PRINT_ORDER_TYPE_1 = "OutBoxLabel";
    /** 箱号(出库) */
    public static final String PRINT_ORDER_TYPE_2 = "OutBoxNo";
    /** 箱标签(入库) */
    public static final String PRINT_ORDER_TYPE_3 = "InBoxLabel";
    /** 箱号(入库) */
    public static final String PRINT_ORDER_TYPE_4 = "InBoxNo";
    /** 月台标签 */
    public static final String PRINT_ORDER_TYPE_5 = "PlatformLabel";
    /** 质检单号 */
    public static final String PRINT_ORDER_TYPE_6 = "QCNo";
    /** 残次标签 */
    public static final String PRINT_ORDER_TYPE_7 = "DefectiveLabel";
    /** PO单*/
    public static final String PRINT_ORDER_TYPE_8 = "PO";
    /** ASN单 */
    public static final String PRINT_ORDER_TYPE_9 = "ASN";
    /** 质检单 */
    public static final String PRINT_ORDER_TYPE_10 = "QC";
    /** 预收货交接清单 */
    public static final String PRINT_ORDER_TYPE_11 = "PreReceipt";
    /** PO收货差异清单*/
    public static final String PRINT_ORDER_TYPE_12 = "PoDifferenceReceipt";
    /** 销售清单 */
    public static final String PRINT_ORDER_TYPE_13 = "Sales";
    /** 发票 */
    public static final String PRINT_ORDER_TYPE_14 = "Invoice";
    /** 面单 */
    public static final String PRINT_ORDER_TYPE_15 = "Logistics";
    /** 装箱清单 */
    public static final String PRINT_ORDER_TYPE_16 = "Packing";
    /** 拣货单*/
    public static final String PRINT_ORDER_TYPE_17 = "Picking";
    /** 出库交接清单 */
    public static final String PRINT_ORDER_TYPE_18 = "OutboundTransfer";
    /** 工作条码*/
    public static final String PRINT_ORDER_TYPE_19 = "WorkBarCode";
    /** 工作标签 */
    public static final String PRINT_ORDER_TYPE_20 = "WorkLabel";
    /** 工作清单 */
    public static final String PRINT_ORDER_TYPE_21 = "WorkList";
    /** 库位条码 */
    public static final String PRINT_ORDER_TYPE_22 = "LocationBarCode";
    /** 库位校验码*/
    public static final String PRINT_ORDER_TYPE_23 = "LocationCheckCode";
    /** 库位标签 */
    public static final String PRINT_ORDER_TYPE_24 = "LocationLabel";
    /** 容器条码 */
    public static final String PRINT_ORDER_TYPE_25 = "ContainerBarCode";
    /** 出库箱条码 */
    public static final String PRINT_ORDER_TYPE_26 = "OutBoundBoxBarCode";
    /** 盘点表*/
    public static final String PRINT_ORDER_TYPE_27 = "Check";
    /** 商品条码 */
    public static final String PRINT_ORDER_TYPE_28 = "SkuBarCode";
    /** 出库设施条码 */
    public static final String PRINT_ORDER_TYPE_29 = "FacilityBarCode";
    /** 出库设施校验码 */
    public static final String PRINT_ORDER_TYPE_30 = "FacilityCheckCode";
    /** ASN收货差异清单 */
    public static final String PRINT_ORDER_TYPE_31 = "AsnDifferenceReceipt";


    /** 波次阶段创建出库箱 */
    public static final String CREATE_OUTBOUND_CARTON = "CREATE_OUTBOUND_CARTON";

    /** 出库单装箱信息 整托 */
    public static final int ODO_OUTBOUND_BOX_WHOLE_TRAY = 1;
    /** 出库单装箱信息 整箱 */
    public static final int ODO_OUTBOUND_BOX_WHOLE_CAASE = 2;

    /** 拣货模式 摘果 */
    public static final String PICKING_MODE_PICKING = "1";
    /** 拣货模式 播种 */
    public static final String PICKING_MODE_SEED = "2";
    /** 拣货模式 按批摘果-单品单件 */
    public static final String PICKING_MODE_BATCH_SINGLE = "3";
    /** 拣货模式 按批摘果-秒杀 */
    public static final String PICKING_MODE_BATCH_SECKILL = "4";
    /** 拣货模式 按批摘果-主副品 */
    public static final String PICKING_MODE_BATCH_MAIN = "5";
    /** 拣货模式 按批摘果-套装 */
    public static final String PICKING_MODE_BATCH_GROUP = "6";

    // 容器体积可用率
    public static final Double OUTBOUND_BOX_AVAILABILITY = 0.8;

    public static final String OUTBOUNDBOX_RELATIONSHIP_TYPE_STORE = "storetype";
    public static final String OUTBOUNDBOX_RELATIONSHIP_TYPE_CUSTOMER = "customertype";
    // 出库箱类型编码
    public static final String OUTBOUNDBOX_CODE = "OUTBOUNDBOX_CODE";

    /** 调编码生成器 出库箱推荐标识 */
    public static final String OUTBOUND_BOX_BATCH = "WMS_OUTBOUND_BOX_BATCH";

    /** 对接系统系数来源 */
    public static final String DATA_SOURCE_WMS = "wms";
    public static final String DATA_SOURCE_PAC = "pac";
    public static final String DATA_SOURCE_HUB = "hub";
    public static final String COLLECTION_STATUS_10 = "10";

    /** 拣货操作库存 */
    public static final String PICKING_INVENTORY = "PICKING_INVENTORY";
    /** 补货中的拣货操作库存 */
    public static final String REPLENISHMENT_PICKING_INVENTORY = "REPLENISHMENT_PICKING_INVENTORY";

    /**数据来源=WMS*/
    public static final String WMS_DATA_SOURCE = "WMS";

}
