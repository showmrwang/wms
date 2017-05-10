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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 播种集货表(t_wh_seeding_collection)
 * 
 * @author larkark
 * 
 */
public class WhCheckingCollection extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = -4141071444187444104L;

    // columns START
    /** 播种墙ID */
    private Long facilityId;
    /** 暂存库位ID */
    private Long temporaryLocationId;
    /** 中转库位ID */
    private Long locationId;
    /** 周转箱ID */
    private Long containerId;
    /** 小批次 */
    private String batch;
    /** 集货状态 */
    private Integer collectionStatus;
    /** 波次号 */
    private String waveCode;
    /** 对应组织ID */
    private Long ouId;
    /** 外部容器，小车 */
    private Long outerContainerId;
    /** 货格编码数 */
    private Integer containerLatticeNo;
    /** 耗材ID */
    private Long outboundboxId;
    /** 出库箱编码 */
    private String outboundboxCode;
    /** 配货模式 */
    private String distributionMode;
    /** 拣货模式 */
    private String pickingMode;
    /** 复核模式 */
    private String checkingMode;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;

    /** 创建人 */
    private java.lang.Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private java.lang.Long modifiedId;

    // columns END
    public Long getFacilityId() {
        return facilityId;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
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

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getTemporaryLocationId() {
        return temporaryLocationId;
    }

    public void setTemporaryLocationId(Long temporaryLocationId) {
        this.temporaryLocationId = temporaryLocationId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(Integer collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }

    public void setContainerLatticeNo(Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }

    public Long getOutboundboxId() {
        return outboundboxId;
    }

    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

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

    public java.lang.Long getCreateId() {
        return createId;
    }

    public void setCreateId(java.lang.Long createId) {
        this.createId = createId;
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

    public java.lang.Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }

}
