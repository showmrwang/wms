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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author larkark
 *
 */
public class WhWorkOper extends BaseModel {
	
    private static final long serialVersionUID = 5663275118303971146L;
    
	//columns START
    
    /** 操作员ID */
	private java.lang.Long operUserId;
	/** 工作ID */
	private java.lang.Long workId;
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 作业ID */
	private java.lang.Long operationId;
	/** 状态 */
	private java.lang.Integer status;
	/** 是否管理员指派 */
	private java.lang.Boolean isAdminAssign;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 最后操作时间 */
	private java.util.Date lastModifyTime;
	/** 创建人ID */
	private java.lang.Long createdId;
	/** 修改人ID */
	private java.lang.Long modifiedId;
	/** 操作人ID */
	private java.lang.Long operatorId;
	
	//columns END
	
    public java.lang.Long getOperUserId() {
        return operUserId;
    }
    public void setOperUserId(java.lang.Long operUserId) {
        this.operUserId = operUserId;
    }
    public java.lang.Long getWorkId() {
        return workId;
    }
    public void setWorkId(java.lang.Long workId) {
        this.workId = workId;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.lang.Long getOperationId() {
        return operationId;
    }
    public void setOperationId(java.lang.Long operationId) {
        this.operationId = operationId;
    }
    public java.lang.Integer getStatus() {
        return status;
    }
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }
    public java.lang.Boolean getIsAdminAssign() {
        return isAdminAssign;
    }
    public void setIsAdminAssign(java.lang.Boolean isAdminAssign) {
        this.isAdminAssign = isAdminAssign;
    }
    public java.util.Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }
    public void setLastModifyTime(java.util.Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
    public java.lang.Long getCreatedId() {
        return createdId;
    }
    public void setCreatedId(java.lang.Long createdId) {
        this.createdId = createdId;
    }
    public java.lang.Long getModifiedId() {
        return modifiedId;
    }
    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }
    public java.lang.Long getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(java.lang.Long operatorId) {
        this.operatorId = operatorId;
    }
}

