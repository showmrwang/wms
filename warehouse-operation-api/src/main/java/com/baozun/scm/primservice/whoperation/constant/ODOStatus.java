package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * Po单Asn单状态常量
 * 
 * @author bin.hu
 * 
 */
public class ODOStatus implements Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 249822883185178906L;

    /** ODO */
    public static final int ODO_NEW = 1;// 新建
    public static final int ODO_OUTSTOCK = 2;// 部分出库
    public static final int ODO_OUTSTOCK_FINISH = 10;// 出库完成
    public static final int ODO_CANCEL = 17;// 取消
    /** ODO */

    /** ODO_LINE */
    public static final int ODOLINE_NEW = 1;// 新建
    /** ODO_LINE */
}
