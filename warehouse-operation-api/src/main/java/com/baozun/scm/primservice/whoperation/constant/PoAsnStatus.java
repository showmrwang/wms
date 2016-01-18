package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * Po单Asn单状态常量
 * 
 * @author bin.hu
 * 
 */
public class PoAsnStatus implements Serializable {

    private static final long serialVersionUID = -4336754187196074057L;

    /** PO */

    public static final int PO_NEW = 1; // 新建
    public static final int PO_CREATE_ASN = 2;// 已创建ASN

    /** PO */
}
