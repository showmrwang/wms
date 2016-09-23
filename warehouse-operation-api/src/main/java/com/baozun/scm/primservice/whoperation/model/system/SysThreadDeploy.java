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
package com.baozun.scm.primservice.whoperation.model.system;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class SysThreadDeploy extends BaseModel {
	
	private static final long serialVersionUID = 139612677153024697L;
	
	/** 线程CODE */
	private java.lang.String threadCode;
	/** 描述 */
	private java.lang.String description;
	/** 线程数量 */
	private java.lang.Integer threadQty;
	/** 每次执行单据数 */
	private java.lang.Integer runQty;
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 创建人ID */
	private java.lang.Long createId;
	/** 操作时间 */
	private java.util.Date lastModifyTime;
	/** 操作人ID */
	private java.lang.Long operatorId;

	public java.lang.String getThreadCode() {
		return this.threadCode;
	}
	
	public void setThreadCode(java.lang.String value) {
		this.threadCode = value;
	}
	
	public java.lang.String getDescription() {
		return this.description;
	}
	
	public void setDescription(java.lang.String value) {
		this.description = value;
	}
	
	public java.lang.Integer getThreadQty() {
		return this.threadQty;
	}
	
	public void setThreadQty(java.lang.Integer value) {
		this.threadQty = value;
	}
	
	public java.lang.Integer getRunQty() {
		return this.runQty;
	}
	
	public void setRunQty(java.lang.Integer value) {
		this.runQty = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
	
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.lang.Long getCreateId() {
		return this.createId;
	}
	
	public void setCreateId(java.lang.Long value) {
		this.createId = value;
	}
	
	public java.util.Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	
	public void setLastModifyTime(java.util.Date value) {
		this.lastModifyTime = value;
	}
	
	public java.lang.Long getOperatorId() {
		return this.operatorId;
	}
	
	public void setOperatorId(java.lang.Long value) {
		this.operatorId = value;
	}
	
}

