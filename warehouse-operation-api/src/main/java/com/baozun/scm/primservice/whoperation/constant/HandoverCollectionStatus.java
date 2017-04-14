package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class HandoverCollectionStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2960269940732571747L;
    public static final String NEW = "1";// 已创建/新建
    public static final String TO_HANDOVER = "5";// 待交接
    public static final String FINISH = "10";// 交接完成
    public static final String CANCEL = "17";// 已取消
    public static final String ERROR = "20";// 异常
}
