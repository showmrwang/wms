package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class OdoLineStatus implements Serializable {

   
    private static final long serialVersionUID = 7187081492568354679L;
    /**
     * 出库单明细状态
     * 
     * @author lichuan
     */
    public static final String CREATING = "0"; // 新建中、待创建
    public static final String NEW = "1"; // 新建
    public static final String MERGE = "5"; // 已合并
    public static final String WAVE = "8"; // 波次中
    public static final String WAVE_FINISH = "10"; // 波次完成
    public static final String RELEASE_WORK = "20"; // 工作已释放
    public static final String PICKING = "25"; // 拣货中
    public static final String PICKING_FINISH = "30";// 拣货完成
    public static final String COLLECTION = "35";// 集货中
    public static final String COLLECTION_FINISH = "40"; // 集货完成
    public static final String SEEDING = "45"; // 播种中
    public static final String SEEDING_FINISH = "50"; // 播种完成
    public static final String CHECKING = "55"; // 复核中
    public static final String CHECKING_FINISH = "60";// 复核完成
    public static final String WEIGHING = "65"; // 称重中
    public static final String WEIGHING_FINISH = "70"; // 称重完成
    public static final String HANDOVER = "75";// 交接中
    public static final String HANDOVER_FINISH = "80"; // 交接完成
    public static final String CANCEL = "88"; // 取消
    public static final String PARTLY_FINISH = "99";// 部分完成
    public static final String FINSH = "100"; // 完成、关闭
}
