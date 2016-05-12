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
    public static final int PO_DELETE = 0; // 删除
    public static final int PO_NEW = 1; // 新建
    public static final int PO_CREATE_ASN = 2;// 已创建ASN
    public static final int PO_RCVD = 3;// 收货中
    public static final int PO_RCVD_FINISH = 10;// 收货完成
    public static final int PO_CANCELED = 17;// 取消
    public static final int PO_CLOSE = 20;// 关闭
    /** PO */

    /** PO LINE */
    public static final int POLINE_NEW = 1; // 新建
    public static final int POLINE_CREATE_ASN = 2;// 已创建ASN
    public static final int POLINE_RCVD = 3;// 收货中
    public static final int POLINE_RCVD_FINISH = 10; // 收货完成
    public static final int POLINE_CANCELED = 17;// 取消
    public static final int POLINE_CLOSE = 20;// 关闭
    /** PO LINE */

    /** ASN */
    public static final int ASN_DELETE = 0;// 删除
    public static final int ASN_NEW = 1;// 新建
    public static final int ASN_RESERVE = 2;// 预约
    public static final int ASN_CHECKIN = 3;// 签入 Checkin
    public static final int ASN_RCVD = 4;// 收货中
    public static final int ASN_RCVD_FINISH = 10;// 收货完成
    public static final int ASN_CANCELED = 17;// 取消
    public static final int ASN_CLOSE = 20;// 关闭
    /** ASN */

    /** ASN LINE */
    public static final int ASNLINE_NOT_RCVD = 1; // 未收货
    public static final int ASNLINE_RCVD = 2; // 部分收货
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



    /** BIPO */
    public static final int BIPO_DELETE = 0; // 删除
    public static final int BIPO_NEW = 1; // 新建
    public static final int BIPO_RCVD = 3;// 收货中
    public static final int BIPO_RCVD_FINISH = 10;// 收货完成
    public static final int BIPO_CANCELED = 17;// 取消
    public static final int BIPO_CLOSE = 20;// 关闭
    public static final int BIPO_ALLOT = 5;// 已分配仓库
    /** PO */

    /** BIPO LINE */
    public static final int BIPOLINE_NEW = 1; // 新建
    public static final int BIPOLINE_RCVD = 3;// 收货中
    public static final int BIPOLINE_RCVD_FINISH = 10; // 收货完成
    public static final int BIPOLINE_CANCELED = 17;// 取消
    public static final int BIPOLINE_CLOSE = 20;// 关闭
    public static final int BIPOLINE_ALLOT = 5;// 已分配仓库

}
