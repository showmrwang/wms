package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class CollectionStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3908395817807690153L;

    /** 集货状态*/
    public static final Integer EXECUTING = 1; 	// 执行中
    public static final Integer TO_SEED = 2; 	// 待播种
    public static final Integer SEEDING = 3; 	// 播种中
    public static final Integer FINISH = 10; 	// 已完成
    public static final Integer ERROR = 20; 	// 播种异常

}
