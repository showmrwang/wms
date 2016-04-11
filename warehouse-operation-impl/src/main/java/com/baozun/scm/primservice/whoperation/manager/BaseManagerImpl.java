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
package com.baozun.scm.primservice.whoperation.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.util.LogUtil;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

/**
 * @author lichuan
 *
 */
public abstract class BaseManagerImpl implements BaseManager {
    // log不支持继承
    private static final Logger log = LoggerFactory.getLogger(BaseManagerImpl.class);
    protected String logId = "";
    @Autowired
    private GlobalLogManager globalLogManager;


    protected void insertGlobalLog(String ddl, BaseModel model, Long ouId, Long userId) {
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setOuId(ouId);
        gl.setModifiedId(userId);
        gl.setObjectType(model.getClass().getSimpleName());
        gl.setModifiedValues(model);
        if (Constants.GLOBAL_LOG_UPDATE.equals(ddl)) {
            gl.setType(Constants.GLOBAL_LOG_UPDATE);
        } else if (Constants.GLOBAL_LOG_INSERT.equals(ddl)) {
            gl.setType(Constants.GLOBAL_LOG_INSERT);
        } else {
            gl.setType(Constants.GLOBAL_LOG_DELETE);
        }
        if (log.isDebugEnabled()) {
            log.debug("save globalLog, model is:[{}], param globalLogCommand is:[{}]", gl.getObjectType(), ParamsUtil.bean2String(gl));
        }
        globalLogManager.insertGlobalLog(gl);
    }

    /**
     * 获取格式化的日志信息
     * 
     * @author lichuan
     * @param format
     * @param argArray
     * @return
     */
    protected String getLogMsg(String format, Object... argArray) {
        return LogUtil.getLogMsg(format, argArray);
    }

    /**
     * @return the logId
     */
    public String getLogId() {
        return logId;
    }

    /**
     * @param logId the logId to set
     */
    public void setLogId(String logId) {
        this.logId = logId;
    }

}
