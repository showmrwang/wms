package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class OperationStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2947961005300945555L;
    
    /** 已创建/新建 */
    public static final Integer NEW = 1;
    /** 执行中 */
    public static final Integer EXECUTING = 5;
    /** 已完成 */
    public static final Integer FINISH = 10;
    /** 已取消*/
    public static final Integer CANCEL = 17;

}
