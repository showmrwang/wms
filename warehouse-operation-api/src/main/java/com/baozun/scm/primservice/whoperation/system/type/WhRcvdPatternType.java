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
package com.baozun.scm.primservice.whoperation.system.type;

/**
 * @author lichuan
 *
 */
public enum WhRcvdPatternType {
    /**
     * 收货模式
     */
    CONTAINER_RCVD(1), // 货箱收货
    PALLET_RCVD(2) // 托盘收货
    ;
    
    private int value;
    
    private WhRcvdPatternType(int value){
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
    
    public static WhRcvdPatternType valueOf(int value) {
        switch (value) {
            case 1:
                return CONTAINER_RCVD;
            case 2:
                return PALLET_RCVD;
            default:
                throw new IllegalArgumentException();
        }
    }
    

}
