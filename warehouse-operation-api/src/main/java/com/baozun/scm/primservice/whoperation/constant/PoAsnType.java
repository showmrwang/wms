package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class PoAsnType implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -245524364547712617L;


    /** PO */
    public static final int POTYPE_1 = 1; // 采购入库
    public static final int POTYPE_2 = 2; // 消费者退换货入库
    public static final int POTYPE_3 = 3; // 门店退货入库
    public static final int POTYPE_4 = 4; // 库间调拨入库
}
