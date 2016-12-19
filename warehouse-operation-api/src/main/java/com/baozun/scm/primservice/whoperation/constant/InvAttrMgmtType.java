/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * @author lichuan
 *
 */
public class InvAttrMgmtType implements Serializable {

    private static final long serialVersionUID = -5232580182773300629L;
    // 库存类型(库存属性)
    public static final String INV_TYPE = "1";
    // 库存状态(库存属性)
    public static final String INV_STATUS = "2";
    // 批次号(库存属性)
    public static final String BATCH_NUMBER = "3";
    // 生产日期(库存属性)
    public static final String MFG_DATE = "4";
    // 失效日期(库存属性)
    public static final String EXP_DATE = "5";
    // 原产地(库存属性)
    public static final String COUNTRY_OF_ORIGIN = "6";
    // 库存属性1(库存属性)
    public static final String INV_ATTR1 = "7";
    // 库存属性2(库存属性)
    public static final String INV_ATTR2 = "8";
    // 库存属性3(库存属性)
    public static final String INV_ATTR3 = "9";
    // 库存属性4(库存属性)
    public static final String INV_ATTR4 = "10";
    // 库存属性5(库存属性)
    public static final String INV_ATTR5 = "11";
    // 所有库存属性
    public static final String ALL_INV_ATTRS = "1,2,3,4,5,6,7,8,9,10,11";
}
