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
public enum WhPutawayPatternType {
    /**
     * 上架模式
     */
    SYS_GUIDE_PUTAWAY(1), // 系统指导上架
    SYS_SUGGEST_PUTAWAY(2), // 系统建议上架
    MAN_MADE_PUTAWAY(3) // 人为指定上架
    ;
    
    private int value;

    private WhPutawayPatternType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WhPutawayPatternType valueOf(int value) {
        switch (value) {
            case 1:
                return SYS_GUIDE_PUTAWAY;
            case 2:
                return SYS_SUGGEST_PUTAWAY;
            case 3:
                return MAN_MADE_PUTAWAY;
            default:
                throw new IllegalArgumentException();
        }
    }

}
