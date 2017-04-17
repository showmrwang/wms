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
public class PickingMode implements Serializable {

    private static final long serialVersionUID = -608498741484351299L;
    public static final String PICKING = "1"; // 摘果
    public static final String PICKING_SEED = "2"; // 播种
    public static final String PICKING_SINGLE = "3";// 按批摘果（单品单件）
    public static final String PICKING_SECKILL = "4";// 按批摘果（秒杀）
    public static final String PICKING_TWOSKUSUIT = "5";// 按批摘果（主副品）
    public static final String PICKING_SUITS = "6";// 按批摘果（ 套装组合）

}
