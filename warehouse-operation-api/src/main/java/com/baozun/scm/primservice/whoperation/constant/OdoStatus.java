package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class OdoStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 249822883185178906L;

    /** ODO */
    public static final String ODO_NEW = "1";// 新建
    public static final String ODO_OUTSTOCK = "2";// 部分出库
    public static final String ODO_MERGE = "5";// 已合并
    public static final String ODO_OUTSTOCK_FINISH = "10";// 出库完成
    public static final String ODO_CANCEL = "17";// 取消
    public static final String ODO_TOBECREATED = "99";// 待创建
    /** ODO */

    /** ODO_LINE */
    public static final String ODOLINE_NEW = "1";// 新建
    public static final String ODOLINE_MERGE = "5";// 已合并
    /** ODO_LINE */
}
