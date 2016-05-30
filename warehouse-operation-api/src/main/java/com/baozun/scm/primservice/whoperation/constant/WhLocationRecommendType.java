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
package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * @author lichuan
 *
 */
public class WhLocationRecommendType implements Serializable {

    private static final long serialVersionUID = -1343191159062091861L;
    
    /** 空库位 */
    public static final String EMPTY_LOCATION = "1";
    /** 静态库位 */
    public static final String STATIC_LOCATION = "2";
    /** 合并已有货位（相同库存属性） */
    public static final String MERGE_LOCATION_SAME_INV_ATTRS = "3";
    /** 合并已有货位（不同库存属性） */
    public static final String MERGE_LOCATION_DIFF_INV_ATTRS = "4";
    /** 上架到一个货位 */
    public static final String ONE_LOCATION_ONLY = "5";

}
