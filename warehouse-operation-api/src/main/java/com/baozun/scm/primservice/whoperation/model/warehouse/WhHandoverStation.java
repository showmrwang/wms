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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 * 
 */
public class WhHandoverStation extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // columns START
    /** 交接工位类型:仓库交接工位，复核台组交接工位 */
    private java.lang.String type;
    /** 序号 */
    private java.lang.Integer serialNumber;
    /** 交接工位编码 */
    private java.lang.String code;
    /** 复核台组ID */
    private java.lang.Long facilityGroupId;
    /** 对应组织ID */
    private java.lang.Long ouId;
    /** 待交接包裹数上限 */
    private java.lang.Integer upperCapacity;
    /** 自动化仓道口 */
    private java.lang.String automaticWarehouseCrossing;
    /** 集货交接规则ID */
    private java.lang.Long chrId;
    /** 1.可用;0.禁用 */
    private java.lang.Integer lifecycle;
    /** 创建人ID */
    private java.lang.Long createId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 修改人ID */
    private java.lang.Long modifiedId;

    // columns END

    public WhHandoverStation() {}

    public WhHandoverStation(java.lang.Long id) {
        this.id = id;
    }

    public void setType(java.lang.String value) {
        this.type = value;
    }

    public java.lang.String getType() {
        return this.type;
    }

    public void setSerialNumber(java.lang.Integer value) {
        this.serialNumber = value;
    }

    public java.lang.Integer getSerialNumber() {
        return this.serialNumber;
    }

    public void setCode(java.lang.String value) {
        this.code = value;
    }

    public java.lang.String getCode() {
        return this.code;
    }

    public void setFacilityGroupId(java.lang.Long value) {
        this.facilityGroupId = value;
    }

    public java.lang.Long getFacilityGroupId() {
        return this.facilityGroupId;
    }

    public void setOuId(java.lang.Long value) {
        this.ouId = value;
    }

    public java.lang.Long getOuId() {
        return this.ouId;
    }

    public void setUpperCapacity(java.lang.Integer value) {
        this.upperCapacity = value;
    }

    public java.lang.Integer getUpperCapacity() {
        return this.upperCapacity;
    }

    public void setAutomaticWarehouseCrossing(java.lang.String value) {
        this.automaticWarehouseCrossing = value;
    }

    public java.lang.String getAutomaticWarehouseCrossing() {
        return this.automaticWarehouseCrossing;
    }

    public void setChrId(java.lang.Long value) {
        this.chrId = value;
    }

    public java.lang.Long getChrId() {
        return this.chrId;
    }

    public void setLifecycle(java.lang.Integer value) {
        this.lifecycle = value;
    }

    public java.lang.Integer getLifecycle() {
        return this.lifecycle;
    }

    public void setCreateId(java.lang.Long value) {
        this.createId = value;
    }

    public java.lang.Long getCreateId() {
        return this.createId;
    }

    public void setCreateTime(java.util.Date value) {
        this.createTime = value;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(java.util.Date value) {
        this.lastModifyTime = value;
    }

    public java.util.Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setModifiedId(java.lang.Long value) {
        this.modifiedId = value;
    }

    public java.lang.Long getModifiedId() {
        return this.modifiedId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("Id", getId()).append("Type", getType()).append("SerialNumber", getSerialNumber()).append("Code", getCode()).append("FacilityGroupId", getFacilityGroupId()).append("OuId", getOuId())
                .append("UpperCapacity", getUpperCapacity()).append("AutomaticWarehouseCrossing", getAutomaticWarehouseCrossing()).append("ChrId", getChrId()).append("Lifecycle", getLifecycle()).append("CreateId", getCreateId())
                .append("CreateTime", getCreateTime()).append("LastModifyTime", getLastModifyTime()).append("ModifiedId", getModifiedId()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).append(getType()).append(getSerialNumber()).append(getCode()).append(getFacilityGroupId()).append(getOuId()).append(getUpperCapacity()).append(getAutomaticWarehouseCrossing()).append(getChrId())
                .append(getLifecycle()).append(getCreateId()).append(getCreateTime()).append(getLastModifyTime()).append(getModifiedId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WhHandoverStation == false) return false;
        if (this == obj) return true;
        WhHandoverStation other = (WhHandoverStation) obj;
        return new EqualsBuilder().append(getId(), other.getId())

        .append(getType(), other.getType())

        .append(getSerialNumber(), other.getSerialNumber())

        .append(getCode(), other.getCode())

        .append(getFacilityGroupId(), other.getFacilityGroupId())

        .append(getOuId(), other.getOuId())

        .append(getUpperCapacity(), other.getUpperCapacity())

        .append(getAutomaticWarehouseCrossing(), other.getAutomaticWarehouseCrossing())

        .append(getChrId(), other.getChrId())

        .append(getLifecycle(), other.getLifecycle())

        .append(getCreateId(), other.getCreateId())

        .append(getCreateTime(), other.getCreateTime())

        .append(getLastModifyTime(), other.getLastModifyTime())

        .append(getModifiedId(), other.getModifiedId())

        .isEquals();
    }
}
