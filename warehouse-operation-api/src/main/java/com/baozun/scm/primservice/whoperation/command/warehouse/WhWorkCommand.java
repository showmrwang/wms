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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


/**
 * 
 * @author larkark
 * 
 */
public class WhWorkCommand extends BaseCommand {

    private static final long serialVersionUID = 7328468466927329336L;

    // columns START

    /** 主键ID */
    private java.lang.Long id;
    /** 工作号 */
    private String code;
    /** 工作状态，系统常量 */
    private Integer status;
    /** 仓库组织ID */
    private Long ouId;
    /** 工作类型编码 */
    private String workType;
    /** 工作类别编码 */
    private String workCategory;
    /** 是否锁定 默认值：1 */
    private Boolean isLocked;
    /** 是否已迁出 */
    private Boolean isAssignOut;
    /** 是否短拣 */
    private Boolean isShortPicking;
    /** 是否波次内补货 */
    private Boolean isWaveReplenish;
    /** 是否拣货库存待移入 */
    private Boolean isPickingTobefilled;
    /** 是否多次作业 */
    private Boolean isMultiOperation;
    /** 当前工作明细涉及到的所有库区编码信息列表 */
    private String workArea;
    /** 工作优先级 */
    private Integer workPriority;
    /** 小批次 */
    private String batch;
    /** 签出批次 */
    private String assignOutBatch;
    /** 操作开始时间 */
    private Date startTime;
    /** 操作结束时间 */
    private Date finishTime;
    /** 波次ID */
    private Long waveId;
    /** 波次号 */
    private String waveCode;
    /** 订单号 */
    private String orderCode;
    /** 库位 */
    private String locationCode;
    /** 托盘 */
    private String outerContainerCode;
    /** 容器 */
    private String containerCode;
    /** 创建时间 */
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;
    /** 操作人ID */
    private Long operatorId;
    /** 是否启用 1:启用 0:停用 */
    private java.lang.Integer lifecycle;

    /*-----------------自定义------------------*/
    /** url */
    private String url;
    /** 区域名称 */
    private String areaName;
    /** 类型名称 */
    private String typeName;
    /** 配货模式 */
    private String distributionMode;
    /** 拣货模式 */
    private String pickingMode;
    /** 复核模式 */
    private String checkingMode;
    /** 容器id*/
    private Long containerId;

    // columns END

    public String getDistributionMode() {
        return distributionMode;
    }

    public void setDistributionMode(String distributionMode) {
        this.distributionMode = distributionMode;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
    }

    public String getCheckingMode() {
        return checkingMode;
    }

    public void setCheckingMode(String checkingMode) {
        this.checkingMode = checkingMode;
    }

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkCategory() {
        return workCategory;
    }

    public void setWorkCategory(String workCategory) {
        this.workCategory = workCategory;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Boolean getIsAssignOut() {
        return isAssignOut;
    }

    public void setIsAssignOut(Boolean isAssignOut) {
        this.isAssignOut = isAssignOut;
    }

    public Boolean getIsShortPicking() {
        return isShortPicking;
    }

    public void setIsShortPicking(Boolean isShortPicking) {
        this.isShortPicking = isShortPicking;
    }

    public Boolean getIsWaveReplenish() {
        return isWaveReplenish;
    }

    public void setIsWaveReplenish(Boolean isWaveReplenish) {
        this.isWaveReplenish = isWaveReplenish;
    }

    public Boolean getIsPickingTobefilled() {
        return isPickingTobefilled;
    }

    public void setIsPickingTobefilled(Boolean isPickingTobefilled) {
        this.isPickingTobefilled = isPickingTobefilled;
    }

    public Boolean getIsMultiOperation() {
        return isMultiOperation;
    }

    public void setIsMultiOperation(Boolean isMultiOperation) {
        this.isMultiOperation = isMultiOperation;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public Integer getWorkPriority() {
        return workPriority;
    }

    public void setWorkPriority(Integer workPriority) {
        this.workPriority = workPriority;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getAssignOutBatch() {
        return assignOutBatch;
    }

    public void setAssignOutBatch(String assignOutBatch) {
        this.assignOutBatch = assignOutBatch;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Long getWaveId() {
        return waveId;
    }

    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
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

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

}
