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
package com.baozun.scm.primservice.whoperation.util;

import java.util.Date;
import java.util.UUID;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author lichuan
 *
 */
public class LogUtil {

    /**
     * 获取唯一Id
     * 
     * @author lichuan
     * @return
     */
    public static final String getLogId() {
        String logId = "";
        try {
            logId = UUID.randomUUID().toString();
        } catch (Exception e) {
            logId = "";
        }
        if ("".equals(logId) || null == logId) {
            logId = new Date().getTime() + "";
        } else {
            logId = logId.replaceAll("-", "");
        }
        return logId;
    }

    /**
     * 获取格式化的日志信息
     * 
     * @author lichuan
     * @param format
     * @param argArray
     * @return
     */
    public static final String getLogMsg(String format, Object... argArray) {
        if ("".equals(format) || null == format) return "";
        FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
        return ft.getMessage();
    }

}
