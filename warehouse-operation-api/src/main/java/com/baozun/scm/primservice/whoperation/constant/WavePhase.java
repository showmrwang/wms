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
    
    public static final int WEAK_ALLOCATED_NUM = 10; // 软分配
    public static final int MERGE_ODO_NUM = 20; // 合并出库单
    public static final int ALLOCATED_NUM = 30; // 硬分配
    public static final int REPLENISHED_NUM = 40; // 补货
    public static final int DISTRIBUTION_NUM = 50; // 配货模式计算
    public static final int CREATE_OUTBOUND_CARTON_NUM = 60; // 创建出库箱/容器
    public static final int CREATE_TASK_NUM = 65; // 创建任务
    public static final int CREATE_WORK_NUM = 70; // 创建工作
}
