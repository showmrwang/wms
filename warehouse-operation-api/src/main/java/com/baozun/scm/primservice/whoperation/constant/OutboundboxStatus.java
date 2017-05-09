package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class OutboundboxStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1804358862162683985L;

    /** 已创建/新建 */
    public static final String NEW = "1";
    /** 已复核 */
    public static final String CHECKING = "8";
    /** 已称重 */
    public static final String WEIGHING = "9";
    /** 已交接 */
    public static final String FINISH = "10";
    /** 已取消 */
    public static final String CANCEL = "17";
    /** 异常 */
    public static final String ERROR = "20";
}
