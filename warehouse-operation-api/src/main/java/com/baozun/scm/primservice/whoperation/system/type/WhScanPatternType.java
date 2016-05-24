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
public enum WhScanPatternType {
    /**
     * 枪扫描模式
     */
    NUMBER_ONLY_SCAN(1), // 数量扫描
    ONE_BY_ONE_SCAN(2) // 逐件扫描
    ;
    
    private int value;
    
    private WhScanPatternType(int value){
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
    
    public static WhScanPatternType valueOf(int value) {
        switch (value) {
            case 1:
                return NUMBER_ONLY_SCAN;
            case 2:
                return ONE_BY_ONE_SCAN;
            default:
                throw new IllegalArgumentException();
        }
    }

}
