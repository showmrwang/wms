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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhFacilityRecPath extends BaseModel {

    private static final long serialVersionUID = 2851330307781455507L;

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
}
