package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class CollectionStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3908395817807690153L;

    /** 集货状态*/

    public static final Integer NEW = 1; // 新建
    public static final Integer TRANSFER = 2; // 中转状态
    public static final Integer TEMPORARY_STORAGE = 3; // 暂存状态
    public static final Integer TO_SEED = 4; // 待播种
    public static final Integer SEEDING = 5; // 播种中
    public static final Integer SEEDING_EXECUTING = 6; // 播种执行中
    public static final Integer FINISH = 10; // 已完成
    public static final Integer ERROR = 20; // 异常

}
