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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * 工作头表
 * 
 * @author larkark
 *
 */
public class WhOperationCommand extends BaseModel {
	
    private static final long serialVersionUID = -8165493664871702794L;
    
    //columns START
    /** 主键ID */
//    private java.lang.Long id;
    /** 作业号 */
    private java.lang.String code;
    /** 状态 */
    private java.lang.Integer status;
    /** 工作ID */
    private java.lang.Long workId;
    /** 仓库组织ID */
    private java.lang.Long ouId;
    /** 工作类型编码 */
    private java.lang.String workType;
    /** 工作类别编码 */
    private java.lang.String workCategory;
    /** 优先级 */
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
    /** 是否整托整箱 */
    private java.lang.Boolean isWholeCase;
    /** 是否短拣 */
    private java.lang.Boolean isShortPicking;
    /** 是否拣货完成 */
    private java.lang.Boolean isPickingFinish;
    /** 是否波次内补货 */
    private java.lang.Boolean isWaveReplenish;
    /** 是否拣货库存待移入 */
    private java.lang.Boolean isPickingTobefilled;
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

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public java.lang.Boolean getIsWholeCase() {
        return isWholeCase;
    }

    public void setIsWholeCase(java.lang.Boolean isWholeCase) {
        this.isWholeCase = isWholeCase;
    }

    public java.lang.Integer getStatus() {
        return status;
    }

    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }

    public java.lang.Boolean getIsShortPicking() {
        return isShortPicking;
    }

    public void setIsShortPicking(java.lang.Boolean isShortPicking) {
        this.isShortPicking = isShortPicking;
    }

    public java.lang.Boolean getIsPickingFinish() {
        return isPickingFinish;
    }

    public void setIsPickingFinish(java.lang.Boolean isPickingFinish) {
        this.isPickingFinish = isPickingFinish;
    }

    public java.lang.Boolean getIsWaveReplenish() {
        return isWaveReplenish;
    }

    public void setIsWaveReplenish(java.lang.Boolean isWaveReplenish) {
        this.isWaveReplenish = isWaveReplenish;
    }

    public java.lang.Boolean getIsPickingTobefilled() {
        return isPickingTobefilled;
    }

    public void setIsPickingTobefilled(java.lang.Boolean isPickingTobefilled) {
        this.isPickingTobefilled = isPickingTobefilled;
    }
    
}

