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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 *
 */
public class WhWork extends BaseModel {
	
    private static final long serialVersionUID = 7328468466927329336L;
	
	//columns START
    
	/** 工作号 */
	private java.lang.String code;
	/** 工作状态，系统常量 */
	private java.lang.Integer status;
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 工作类型编码 */
	private java.lang.String workType;
	/** 工作类别编码 */
	private java.lang.String workCategory;
	/** 是否锁定 默认值：1 */
	private java.lang.Boolean isLocked;
	/** 工作优先级 */
	private java.lang.Integer workPriority;
	/** 小批次 */
	private java.lang.String batch;
	/** 操作开始时间 */
	private java.util.Date startTime;
	/** 操作结束时间 */
	private java.util.Date finishTime;
	/** 波次ID */
	private java.lang.Long waveId;
	/** 波次号 */
	private java.lang.String waveCode;
	/** 订单号 */
	private java.lang.String orderCode;
	/** 库位 */
	private java.lang.String locationCode;
	/** 托盘 */
	private java.lang.String outerContainerCode;
	/** 容器 */
	private java.lang.String containerCode;
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
	/** 是否启用 1:启用 0:停用 */
	private java.lang.Integer lifecycle;
	
	//columns END

	public java.lang.String getCode() {
        return code;
    }
    public void setCode(java.lang.String code) {
        this.code = code;
    }
    public java.lang.Integer getStatus() {
        return status;
    }
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.lang.String getWorkType() {
        return workType;
    }
    public void setWorkType(java.lang.String workType) {
        this.workType = workType;
    }
    public java.lang.String getWorkCategory() {
        return workCategory;
    }
    public void setWorkCategory(java.lang.String workCategory) {
        this.workCategory = workCategory;
    }
    public java.lang.Boolean getIsLocked() {
        return isLocked;
    }
    public void setIsLocked(java.lang.Boolean isLocked) {
        this.isLocked = isLocked;
    }
    public java.lang.Integer getWorkPriority() {
        return workPriority;
    }
    public void setWorkPriority(java.lang.Integer workPriority) {
        this.workPriority = workPriority;
    }
    public java.lang.String getBatch() {
        return batch;
    }
    public void setBatch(java.lang.String batch) {
        this.batch = batch;
    }
    public java.util.Date getStartTime() {
        return startTime;
    }
    public void setStartTime(java.util.Date startTime) {
        this.startTime = startTime;
    }
    public java.util.Date getFinishTime() {
        return finishTime;
    }
    public void setFinishTime(java.util.Date finishTime) {
        this.finishTime = finishTime;
    }
    public java.lang.Long getWaveId() {
        return waveId;
    }
    public void setWaveId(java.lang.Long waveId) {
        this.waveId = waveId;
    }
    public java.lang.String getWaveCode() {
        return waveCode;
    }
    public void setWaveCode(java.lang.String waveCode) {
        this.waveCode = waveCode;
    }
    public java.lang.String getOrderCode() {
        return orderCode;
    }
    public void setOrderCode(java.lang.String orderCode) {
        this.orderCode = orderCode;
    }
    public java.lang.String getLocationCode() {
        return locationCode;
    }
    public void setLocationCode(java.lang.String locationCode) {
        this.locationCode = locationCode;
    }
    public java.lang.String getOuterContainerCode() {
        return outerContainerCode;
    }
    public void setOuterContainerCode(java.lang.String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }
    public java.lang.String getContainerCode() {
        return containerCode;
    }
    public void setContainerCode(java.lang.String containerCode) {
        this.containerCode = containerCode;
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
    public java.lang.Integer getLifecycle() {
        return lifecycle;
    }
    public void setLifecycle(java.lang.Integer lifecycle) {
        this.lifecycle = lifecycle;
    }
	
    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("Code",getCode())		
		.append("Status",getStatus())		
		.append("OuId",getOuId())		
		.append("WorkType",getWorkType())		
		.append("WorkCategory",getWorkCategory())		
		.append("IsLocked",getIsLocked())		
		.append("WorkPriority",getWorkPriority())		
		.append("Batch",getBatch())		
		.append("StartTime",getStartTime())		
		.append("FinishTime",getFinishTime())		
		.append("WaveId",getWaveId())		
		.append("WaveCode",getWaveCode())		
		.append("OrderCode",getOrderCode())		
		.append("LocationCode",getLocationCode())		
		.append("OuterContainerCode",getOuterContainerCode())		
		.append("ContainerCode",getContainerCode())		
		.append("CreateTime",getCreateTime())		
		.append("LastModifyTime",getLastModifyTime())		
		.append("CreatedId",getCreatedId())		
		.append("ModifiedId",getModifiedId())		
		.append("OperatorId",getOperatorId())		
		.append("Lifecycle",getLifecycle())		
			.toString();
	}
   
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getCode())
		.append(getStatus())
		.append(getOuId())
		.append(getWorkType())
		.append(getWorkCategory())
		.append(getIsLocked())
		.append(getWorkPriority())
		.append(getBatch())
		.append(getStartTime())
		.append(getFinishTime())
		.append(getWaveId())
		.append(getWaveCode())
		.append(getOrderCode())
		.append(getLocationCode())
		.append(getOuterContainerCode())
		.append(getContainerCode())
		.append(getCreateTime())
		.append(getLastModifyTime())
		.append(getCreatedId())
		.append(getModifiedId())
		.append(getOperatorId())
		.append(getLifecycle())
			.toHashCode();
	}
    
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof WhWork == false) return false;
		if(this == obj) return true;
		WhWork other = (WhWork)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getCode(),other.getCode())

		.append(getStatus(),other.getStatus())

		.append(getOuId(),other.getOuId())

		.append(getWorkType(),other.getWorkType())

		.append(getWorkCategory(),other.getWorkCategory())

		.append(getIsLocked(),other.getIsLocked())

		.append(getWorkPriority(),other.getWorkPriority())

		.append(getBatch(),other.getBatch())

		.append(getStartTime(),other.getStartTime())

		.append(getFinishTime(),other.getFinishTime())

		.append(getWaveId(),other.getWaveId())

		.append(getWaveCode(),other.getWaveCode())

		.append(getOrderCode(),other.getOrderCode())

		.append(getLocationCode(),other.getLocationCode())

		.append(getOuterContainerCode(),other.getOuterContainerCode())

		.append(getContainerCode(),other.getContainerCode())

		.append(getCreateTime(),other.getCreateTime())

		.append(getLastModifyTime(),other.getLastModifyTime())

		.append(getCreatedId(),other.getCreatedId())

		.append(getModifiedId(),other.getModifiedId())

		.append(getOperatorId(),other.getOperatorId())

		.append(getLifecycle(),other.getLifecycle())

			.isEquals();
	}
}

