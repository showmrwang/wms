package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class CheckingStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1804358862162683985L;

    /** 已创建/新建 */
    public static final int NEW = 1;
    /** 部分复核完成*/
    public static final int PART_FINISH = 9;
    /** 已完成 */
    public static final int FINISH = 10;
    /** 已取消 */
    public static final int CANCEL = 17;
    /** 异常 */
    public static final int ERROR = 20;
}
