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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class SysThreadDeploy extends BaseModel {
	
	private static final long serialVersionUID = 139612677153024697L;
	
	/** 线程CODE */
	private String threadCode;
	/** 描述 */
	private String description;
	/** 线程数量 */
	private Integer threadQty;
	/** 每次执行单据数 */
	private Integer runQty;
	/** 仓库组织ID */
	private Long ouId;
	/** 创建时间 */
	private Date createTime;
	/** 创建人ID */
	private Long createId;
	/** 操作时间 */
	private Date lastModifyTime;
	/** 操作人ID */
	private Long operatorId;

	public String getThreadCode() {
		return this.threadCode;
	}
	
	public void setThreadCode(String value) {
		this.threadCode = value;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public Integer getThreadQty() {
		return this.threadQty;
	}
	
	public void setThreadQty(Integer value) {
		this.threadQty = value;
	}
	
	public Integer getRunQty() {
		return this.runQty;
	}
	
	public void setRunQty(Integer value) {
		this.runQty = value;
	}
	
	public Long getOuId() {
		return this.ouId;
	}
	
	public void setOuId(Long value) {
		this.ouId = value;
	}
	
	public Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreateTime(Date value) {
		this.createTime = value;
	}
	
	public Long getCreateId() {
		return this.createId;
	}
	
	public void setCreateId(Long value) {
		this.createId = value;
	}
	
	public Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	
	public void setLastModifyTime(Date value) {
		this.lastModifyTime = value;
	}
	
	public Long getOperatorId() {
		return this.operatorId;
	}
	
	public void setOperatorId(Long value) {
		this.operatorId = value;
	}
	
}

