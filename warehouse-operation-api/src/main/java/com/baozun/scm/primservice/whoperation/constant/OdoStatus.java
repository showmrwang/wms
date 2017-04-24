package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class OdoStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 249822883185178906L;

    /** ODO */
    public static final String ODO_NEW = "1";// 新建
    public static final String ODO_OUTSTOCK = "2";// 部分出库
    public static final String ODO_MERGE = "5";// 已合并
    public static final String ODO_WAVE = "8";// 波次中
    public static final String ODO_OUTSTOCK_FINISH = "10";// 全部出库
    public static final String ODO_HANDOVER_FINISH = "16";// 已交接
    public static final String ODO_CANCEL = "17";// 取消
    public static final String ODO_SEEDING_EXECUTING = "18";// 播种中
    public static final String ODO_SEEDING_FINISH = "19";// 播种完成
    public static final String ODO_PICKING_EXECUTING = "20";// 拣货中
    public static final String ODO_PICKING_FINISH = "21";// 拣货完成
    public static final String ODO_TOBECREATED = "99";// 待创建
    /** ODO */

    /** ODO_LINE */
    public static final String ODOLINE_NEW = "1";// 新建
    public static final String ODOLINE_TOBECREATED = "99";// 待创建
    public static final String ODOLINE_OUTSTOCK = "2";// 部分出库
    public static final String ODOLINE_MERGE = "5";// 已合并
    public static final String ODOLINE_WAVE = "8";// 波次中
    public static final String ODOLINE_PUTAWAY = "11";// 待拣货
    public static final String ODOLINE_PUTAWAY_FINISH = "12";// 拣货完成
    public static final String ODOLINE_CHECK_FINISH = "13";// 已复核
    public static final String ODOLINE_WEIGHT_FINISH = "15";// 已称重
    public static final String ODOLINE_HANDOVER_FINISH = "16";// 已交接
    public static final String ODOLINE_OUTSTOCK_FINISH = "10";// 全部出库
    public static final String ODOLINE_CLOSE = "20";// 关闭
    public static final String ODOLINE_CANCEL = "17";// 取消
    /** ODO_LINE */

}
