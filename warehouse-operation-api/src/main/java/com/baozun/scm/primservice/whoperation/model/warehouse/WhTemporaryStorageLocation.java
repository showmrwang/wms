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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库/复核暂存库位
 * @author larkark
 *
 */
public class WhTemporaryStorageLocation extends BaseModel {
	
	private static final long serialVersionUID = -5292272359777187812L;
	
	//columns START
	/** 编码 */
	private String temporaryStorageCode;
	/** 名称 */
	private String temporaryStorageName;
	/** 暂存区库位类型 */
	private String temporaryStorageType;
	/** 校验码 */
	private String checkCode;
	/** 作业/容器容量 */
	private Integer capacity;
	/** 复核台ID */
	private Long checkOperationsAreaId;
	/** 暂存区域ID */
	private Long workingStorageSectionId;
	/** 创建时间 */
	private Date createTime;
	/** 修改时间 */
	private Date lastModifyTime;
	/** 操作人ID */
	private Long operatorId;
	/** 1.可用;2.已删除;0.禁用 */
	private Integer lifecycle;
	/** 仓库组织ID */
	private Long ouId;
	//columns END
	
	public String getTemporaryStorageCode() {
		return temporaryStorageCode;
	}
	public void setTemporaryStorageCode(String temporaryStorageCode) {
		this.temporaryStorageCode = temporaryStorageCode;
	}
	public String getTemporaryStorageName() {
		return temporaryStorageName;
	}
	public void setTemporaryStorageName(String temporaryStorageName) {
		this.temporaryStorageName = temporaryStorageName;
	}
	public String getTemporaryStorageType() {
		return temporaryStorageType;
	}
	public void setTemporaryStorageType(String temporaryStorageType) {
		this.temporaryStorageType = temporaryStorageType;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Long getCheckOperationsAreaId() {
		return checkOperationsAreaId;
	}
	public void setCheckOperationsAreaId(Long checkOperationsAreaId) {
		this.checkOperationsAreaId = checkOperationsAreaId;
	}
	public Long getWorkingStorageSectionId() {
		return workingStorageSectionId;
	}
	public void setWorkingStorageSectionId(Long workingStorageSectionId) {
		this.workingStorageSectionId = workingStorageSectionId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public Long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
	public Integer getLifecycle() {
		return lifecycle;
	}
	public void setLifecycle(Integer lifecycle) {
		this.lifecycle = lifecycle;
	}
	public Long getOuId() {
		return ouId;
	}
	public void setOuId(Long ouId) {
		this.ouId = ouId;
	}

}

