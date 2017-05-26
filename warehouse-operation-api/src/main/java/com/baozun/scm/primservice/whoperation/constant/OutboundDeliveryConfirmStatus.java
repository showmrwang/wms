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
public class OutboundDeliveryConfirmStatus implements Serializable {

    private static final long serialVersionUID = -2787293776008079868L;
    /** 新建 */
    public static final int NEW = 1;
    /** 已完成 */
    public static final int FINISH = 10;
    /** 已取消 */
    public static final int CANCEL = 17;
    /** 异常 */
    public static final int EXCEPTION = 20;

}
