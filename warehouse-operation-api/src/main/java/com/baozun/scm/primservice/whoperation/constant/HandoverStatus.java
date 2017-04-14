package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class HandoverStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2960269940732571747L;
    public static final Integer NEW = 1;// 已创建/新建
    public static final Integer FINISH = 10;// 交接完成
    public static final Integer CANCEL = 17;// 已取消
    public static final Integer ERROR = 20;// 异常
}
