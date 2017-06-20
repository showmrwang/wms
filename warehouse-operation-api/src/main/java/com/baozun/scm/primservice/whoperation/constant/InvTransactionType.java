package com.baozun.scm.primservice.whoperation.constant;

/***
 * 库存事务类型
 * 
 * @author bin.hu
 *
 */
public final class InvTransactionType {

    /** 收货 */
    public static final String RECEIVING = "1";

    /** 入库分拣 */
    public static final String OUTBOUND_SORTING = "2";

    /** 上架 */
    public static final String SHELF = "3";

    /** 组托 */
    public static final String ASSEMBLY = "4";

    /** 库内移动 */
    public static final String INTRA_WH_MOVE = "5";

    /** 补货 */
    public static final String REPLENISHMENT = "6";

    /** 拣货 */
    public static final String PICKING = "7";

    /** 设施集货 */
    public static final String FACILITY_GOODS_COLLECTION = "8";

    /** 复核 */
    public static final String CHECK = "9";

    /** 交接 */
    public static final String HANDOVER = "10";

    /** 交接出库 */
    public static final String HANDOVER_OUTBOUND = "11";

    /** 拆分移动出库箱 */
    public static final String SPLIT_MOVE_OUTBOUND_BOX = "12";

    /** 拆分移动货箱 */
    public static final String SPLIT_MOVE_PACKING_CASE = "13";

    /** 库内调整 */
    public static final String INTRA_WH_ADJUSTMENT = "14";

    /** 库间调拨 */
    public static final String WH_TO_WH_FLITTING = "15";

    /** 库内加工 */
    public static final String INTRA_WH_MACHINING = "16";

    /** 播种 */
    public static final String SEEDING = "17";
    
    /** 异常处理上架 */
    public static final String EXCEPTION_HANDLE_SHELF = "18";

}
