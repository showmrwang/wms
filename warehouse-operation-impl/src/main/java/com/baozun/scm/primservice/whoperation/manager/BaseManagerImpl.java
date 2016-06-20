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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;
import com.baozun.scm.primservice.whoperation.util.LogUtil;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

/**
 * @author lichuan
 *
 */
public abstract class BaseManagerImpl implements BaseManager {
    // log不支持继承
    private static final Logger log = LoggerFactory.getLogger(BaseManagerImpl.class);
    protected static final String GLOBAL_LOG_UPDATE = Constants.GLOBAL_LOG_UPDATE;
    protected static final String GLOBAL_LOG_INSERT = Constants.GLOBAL_LOG_INSERT;
    protected static final String GLOBAL_LOG_DELETE = Constants.GLOBAL_LOG_DELETE;
    protected String logId = "";
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;


    /**
     * 全局日志
     * 
     * @author lichuan
     * @param dml
     * @param model
     * @param ouId
     * @param userId
     */
    protected void insertGlobalLog(String dml, BaseModel model, Long ouId, Long userId, String parentCode, String dataSource) {
        if (null == model) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, "model");
        }
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setOuId(ouId);
        gl.setModifiedId(userId);
        gl.setObjectType(model.getClass().getSimpleName());
        gl.setModifiedValues(model);
        gl.setParentCode(parentCode);
        if (Constants.GLOBAL_LOG_UPDATE.equals(dml)) {
            gl.setType(Constants.GLOBAL_LOG_UPDATE);
        } else if (Constants.GLOBAL_LOG_INSERT.equals(dml)) {
            gl.setType(Constants.GLOBAL_LOG_INSERT);
        } else if (Constants.GLOBAL_LOG_DELETE.equals(dml)) {
            gl.setType(Constants.GLOBAL_LOG_DELETE);
        } else {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (log.isDebugEnabled()) {
            log.debug("save globalLog, dataSource is:[{}], model is:[{}], param globalLogCommand is:[{}]", dataSource, gl.getObjectType(), ParamsUtil.bean2String(gl, false));
        }
        if (null == dataSource) {
            globalLogManager.insertGlobalLog(gl);
        } else {
            globalLogManager.insertGlobalLog(gl, dataSource);
        }
    }

    /**
     * 库存日志插入
     * 
     * @param skuInvId 变动的库存ID
     * @param qty 调整数量
     * @param oldQty 修改前库存数量
     * @param isTabbInvTotal 在库存日志是否记录交易前后库存总数
     * @param ouid 仓库组织ID
     * @param userid 操作人ID
     */
    protected void insertSkuInventoryLog(Long skuInvId, Double qty, Double oldQty, Boolean isTabbInvTotal, Long ouid, Long userid) {
        if (null == skuInvId) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, "skuInvId");
        }
        // 通过库存ID封装库存日志对象
        WhSkuInventoryLog log = whSkuInventoryLogManager.findInventoryLogBySkuInvId(skuInvId, ouid);
        // 调整数量
        log.setRevisionQty(qty);
        log.setModifiedId(userid);
        log.setModifyTime(new Date());
        // 判断是否要计算库存修改前后数量
        if (isTabbInvTotal) {
            if (null == oldQty) {
                log.setOldQty(0.0);
                log.setNewQty(qty);
            } else {
                log.setOldQty(oldQty);
                log.setNewQty(oldQty + qty);
            }
        }
        whSkuInventoryLogManager.insertSkuInventoryLog(log);
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
