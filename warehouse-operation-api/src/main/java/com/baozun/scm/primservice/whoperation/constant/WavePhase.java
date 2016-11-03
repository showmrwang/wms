package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class WavePhase implements Serializable {

    private static final long serialVersionUID = -1200827494569415671L;

    /** 波次阶段*/
    public static final String WEAK_ALLOCATED = "WEAK_ALLOCATED"; // 软分配
    public static final String MERGE_ODO = "MERGE_ODO"; // 合并出库单
    public static final String ALLOCATED = "ALLOCATED"; // 硬分配
    public static final String REPLENISHED = "REPLENISHED"; // 补货
    public static final String DISTRIBUTION = "DISTRIBUTION_PATTERN_CALC"; // 配货模式计算
    public static final String CREATE_OUTBOUND_CARTON = "CREATE_OUTBOUND_CARTON"; // 创建出库箱/容器
    public static final String CREATE_TASK = "CREATE_TASK"; // 创建任务
    public static final String CREATE_WORK = "CREATE_WORK"; // 创建工作
}
