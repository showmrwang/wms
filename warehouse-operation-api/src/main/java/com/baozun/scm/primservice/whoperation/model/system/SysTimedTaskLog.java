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
package com.baozun.scm.primservice.whoperation.model.system;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 定时执行Log
 * 
 * @author larkark
 *
 */
public class SysTimedTaskLog extends BaseModel {



    /**
     * 
     */
    private static final long serialVersionUID = 769403995646292113L;
    /** 定时类名 */
    private String taskBeanName;
    /** 定时方法名 */
    private String taskMethodName;
    /** 仓库编码 */
    private String ouCode;
    /** 执行单据编码 */
    private String documentCode;
    /** 执行单据ID */
    private Long documentId;
    /** 开始时间 */
    private Date timeStart;
    /** 结束时间 */
    private Date timeEnd;
    /** 用时 */
    private String timeSpent;
    /** 处理数量 */
    private Integer handleQty;
    /** 是否执行成功 0:执行中 1:成功 2:失败 */
    private Integer isSuccess;
    /** 对应月份表 */
    private String sysDate;

    public String getTaskBeanName() {
        return taskBeanName;
    }

    public void setTaskBeanName(String taskBeanName) {
        this.taskBeanName = taskBeanName;
    }

    public String getTaskMethodName() {
        return taskMethodName;
    }

    public void setTaskMethodName(String taskMethodName) {
        this.taskMethodName = taskMethodName;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Integer getHandleQty() {
        return handleQty;
    }

    public void setHandleQty(Integer handleQty) {
        this.handleQty = handleQty;
    }

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public String getOuCode() {
        return ouCode;
    }

    public void setOuCode(String ouCode) {
        this.ouCode = ouCode;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }


}
