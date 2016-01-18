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
    public static final int PO_RCVD = 3;// 收货中
    public static final int PO_RCVD_FINISH = 10;// 收货完成
    public static final int PO_CANCELED = 17;// 取消
    public static final int PO_CLOSE = 20;// 关闭
    /** PO */

    /** PO LINE */
    public static final int POLINE_NOT_RCVD = 1; // 未收货
    public static final int POLINE_RCVD_FINISH = 10; // 收货完成
    public static final int POLINE_CANCELED = 17;// 取消
    public static final int POLINE_CLOSE = 20;// 关闭
    /** PO LINE */

    /** ASN */
    public static final int ASN_NEW = 1;// 新建
    public static final int ASN_RESERVE = 2;// 预约
    public static final int ASN_CHECKIN = 3;// 签入 Checkin
    public static final int ASN_RCVD = 4;// 收货中
    public static final int ASN_ONSHELF = 5;// 上架中 onshelf
    public static final int ASN_INBOUND = 6;// 已入库
    public static final int ASN_REVIEW = 7;// 已审核 review
    public static final int ASN_RCVD_FINISH = 10;// 收货完成
    public static final int ASN_CANCELED = 17;// 取消
    public static final int ASN_CLOSE = 20;// 关闭
    /** ASN */

    /** ASN LINE */
    public static final int ASNLINE_NOT_RCVD = 1; // 未收货
    public static final int ASNLINE_RCVD_PART = 2; // 部分收货
    public static final int ASNLINE_RCVD_ALL = 3; // 全部收货
    public static final int ASNLINE_RCVD_FINISH = 10; // 收货完成
    public static final int ASNLINE_CANCELED = 17;// 取消
    public static final int ASNLINE_CLOSE = 20;// 关闭
    /** ASN LINE */

    /** ASN RESERVE LINE */
    public static final int ASN_RESERVE_NEW = 1; // 创建
    public static final int ASN_RESERVE_CHECKIN = 2; // 签入
    public static final int ASN_RESERVE_FINISH = 10; // 收货完成
    public static final int ASN_RESERVE_CANCELED = 17;// 取消
    /** ASN RESERVE LINE */



}
