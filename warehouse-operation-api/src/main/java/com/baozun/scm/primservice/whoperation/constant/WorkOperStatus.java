package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class WorkOperStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1326943227233933669L;

    /** 已创建/新建 */
    public static final Integer NEW = 1;
    /** 部分完成 */
    public static final Integer PARTLY_FINISH = 5;
    /** 已完成 */
    public static final Integer FINISH = 10;
    /** 已取消 */
    public static final Integer CANCEL = 17;

}
