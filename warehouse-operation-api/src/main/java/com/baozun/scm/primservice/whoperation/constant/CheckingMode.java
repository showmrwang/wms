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
public class CheckingMode implements Serializable {

    private static final long serialVersionUID = -3059888686585483161L;
    /** 按单复核 */
    public static final String CHECK_BY_ODO = "CHECK_BY_ODO";
    /** 按箱复核 */
    public static final String CHECK_BY_CONTAINER = "CHECK_BY_CONTAINER";
    /** 首单复核 */
    public static final String CHECK_FIRST_ODO = "CHECK_FIRST_ODO";
    /** 副品复核 */
    public static final String CHECK_ACCESSORY = "CHECK_ACCESSORY";


}
