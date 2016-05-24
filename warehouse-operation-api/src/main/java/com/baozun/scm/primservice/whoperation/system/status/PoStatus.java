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
public enum PoStatus {
    /**
     * po状态
     */
    CREATED(1), // 新建
    ASN_CREATED(2), // 已创建ASN
    RECEVING(3), // 收货中
    FINISHED(10), // 收货完成
    CANCELED(17), // 取消
    CLOSEED(20) // 关闭
    ;
    
    private int value;
    
    private PoStatus(int value){
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
    
    public static PoStatus valueOf(int value) {
        switch (value) {
            case 1:
                return CREATED;
            case 2:
                return ASN_CREATED;
            case 3:
                return RECEVING;
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
