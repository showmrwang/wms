/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.system.status;

/**
 * @author lichuan
 *
 */
public enum AsnStatus {
    /**
     * asn状态
     */
    CREATED(1), // 新建
    APPOINTMENT(2), // 预约
    SIGN_IN(3), // 签入
    RECEVING(4), // 收货中
    PUTAWAY(5), // 上架中
    INBOUND(6), // 已入库
    CHECKED(7), // 已审核
    FINISHED(10), // 收货完成
    CANCELED(17), // 取消
    CLOSEED(20) // 关闭
    ;
    
    private int value;
    
    private AsnStatus(int value){
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
    
    public static AsnStatus valueOf(int value) {
        switch (value) {
            case 1:
                return CREATED;
            case 2:
                return APPOINTMENT;
            case 3:
                return SIGN_IN;
            case 4:
                return RECEVING;
            case 5:
                return PUTAWAY;
            case 6:
                return INBOUND;
            case 7:
                return CHECKED;
            case 10:
                return FINISHED;
            case 17:
                return CANCELED;
            case 20:
                return CLOSEED;
            default:
                throw new IllegalArgumentException();
        }
    }
}
