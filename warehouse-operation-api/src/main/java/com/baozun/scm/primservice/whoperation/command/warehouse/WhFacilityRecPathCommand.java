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

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark
 *
 */
public class WhFacilityRecPathCommand extends BaseCommand {

	private static final long serialVersionUID = -7878477583781158976L;
	
	/** 主键ID */
    private Long id;
    /** 批次号 */
    private String batch;
    /** 容器编码 */
    private String containerCode;
    /** 播种墙编码 */
    private String seedingwallCode;
    /** 播种墙容量上限 */
    private Integer seedingwallUpperLimit;
    /** 播种墙校验码 */
    private String seedingwallCheckCode;
    /** 暂存区域库位编码 */
    private String temporaryStorageLocationCode;
    /** 暂存区域库位校验码 */
    private String temporaryStorageLocationCheckCode;
    /** 中转库位编码 */
    private String transitLocationCode;
    /** 中转库位校验码 */
    private String transitLocationCheckCode;
    /** 状态 1:移动中 2:移动完成 */
    private Integer status;
    /** 组织仓库ID */
    private Long ouId;
    /** 批次对应箱数 */
    private Integer batchContainerQty;
    
    // 自定义
    /** 设施Id */
    private Long facilityId;
    /** 暂存库位Id */
    private Long temporaryStorageLocationId;
    /** 中转库位Id */
    private Long transitLocationId;
    /** 容器Id */
    private Long containerId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getSeedingwallCode() {
        return seedingwallCode;
    }

    public void setSeedingwallCode(String seedingwallCode) {
        this.seedingwallCode = seedingwallCode;
    }

    public Integer getSeedingwallUpperLimit() {
        return seedingwallUpperLimit;
    }

    public void setSeedingwallUpperLimit(Integer seedingwallUpperLimit) {
        this.seedingwallUpperLimit = seedingwallUpperLimit;
    }

    public String getSeedingwallCheckCode() {
        return seedingwallCheckCode;
    }

    public void setSeedingwallCheckCode(String seedingwallCheckCode) {
        this.seedingwallCheckCode = seedingwallCheckCode;
    }

    public String getTemporaryStorageLocationCode() {
        return temporaryStorageLocationCode;
    }

    public void setTemporaryStorageLocationCode(String temporaryStorageLocationCode) {
        this.temporaryStorageLocationCode = temporaryStorageLocationCode;
    }

    public String getTemporaryStorageLocationCheckCode() {
        return temporaryStorageLocationCheckCode;
    }

    public void setTemporaryStorageLocationCheckCode(String temporaryStorageLocationCheckCode) {
        this.temporaryStorageLocationCheckCode = temporaryStorageLocationCheckCode;
    }

    public String getTransitLocationCode() {
        return transitLocationCode;
    }

    public void setTransitLocationCode(String transitLocationCode) {
        this.transitLocationCode = transitLocationCode;
    }

    public String getTransitLocationCheckCode() {
        return transitLocationCheckCode;
    }

    public void setTransitLocationCheckCode(String transitLocationCheckCode) {
        this.transitLocationCheckCode = transitLocationCheckCode;
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

    public Integer getBatchContainerQty() {
        return batchContainerQty;
    }

    public void setBatchContainerQty(Integer batchContainerQty) {
        this.batchContainerQty = batchContainerQty;
    }

	public Long getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Long facilityId) {
		this.facilityId = facilityId;
	}

	public Long getTemporaryStorageLocationId() {
		return temporaryStorageLocationId;
	}

	public void setTemporaryStorageLocationId(Long temporaryStorageLocationId) {
		this.temporaryStorageLocationId = temporaryStorageLocationId;
	}

	public Long getTransitLocationId() {
		return transitLocationId;
	}

	public void setTransitLocationId(Long transitLocationId) {
		this.transitLocationId = transitLocationId;
	}

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}
}
