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
package com.baozun.scm.primservice.whoperation.model.odo.wave;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhWavePhase extends BaseModel {



    /**
     * 
     */
    private static final long serialVersionUID = 9184552523680203027L;
    // columns START
    /** 阶段顺序 */
    private java.lang.Integer phaseOrder;
    /** 阶段编码，字典表系统常量 */
    private java.lang.String phaseCode;
    /** 波次模板ID */
    private java.lang.Long waveTemplateId;
    /** 仓库组织ID */
    private java.lang.Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后操作时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private java.lang.Long createdId;
    /** 操作人ID */
    private java.lang.Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private java.lang.Integer lifecycle;

    // columns END

    public WhWavePhase() {}

    public WhWavePhase(java.lang.Long id) {
        this.id = id;
    }

    public void setPhaseOrder(java.lang.Integer value) {
        this.phaseOrder = value;
    }

    public java.lang.Integer getPhaseOrder() {
        return this.phaseOrder;
    }

    public void setPhaseCode(java.lang.String value) {
        this.phaseCode = value;
    }

    public java.lang.String getPhaseCode() {
        return this.phaseCode;
    }

    public void setWaveTemplateId(java.lang.Long value) {
        this.waveTemplateId = value;
    }

    public java.lang.Long getWaveTemplateId() {
        return this.waveTemplateId;
    }

    public void setOuId(java.lang.Long value) {
        this.ouId = value;
    }

    public java.lang.Long getOuId() {
        return this.ouId;
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

    public void setCreatedId(java.lang.Long value) {
        this.createdId = value;
    }

    public java.lang.Long getCreatedId() {
        return this.createdId;
    }

    public void setModifiedId(java.lang.Long value) {
        this.modifiedId = value;
    }

    public java.lang.Long getModifiedId() {
        return this.modifiedId;
    }

    public void setLifecycle(java.lang.Integer value) {
        this.lifecycle = value;
    }

    public java.lang.Integer getLifecycle() {
        return this.lifecycle;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("Id", getId()).append("PhaseOrder", getPhaseOrder()).append("PhaseCode", getPhaseCode()).append("WaveTemplateId", getWaveTemplateId()).append("OuId", getOuId()).append("CreateTime", getCreateTime())
                .append("LastModifyTime", getLastModifyTime()).append("CreatedId", getCreatedId()).append("ModifiedId", getModifiedId()).append("Lifecycle", getLifecycle()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).append(getPhaseOrder()).append(getPhaseCode()).append(getWaveTemplateId()).append(getOuId()).append(getCreateTime()).append(getLastModifyTime()).append(getCreatedId()).append(getModifiedId())
                .append(getLifecycle()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WhWavePhase == false) return false;
        if (this == obj) return true;
        WhWavePhase other = (WhWavePhase) obj;
        return new EqualsBuilder().append(getId(), other.getId())

        .append(getPhaseOrder(), other.getPhaseOrder())

        .append(getPhaseCode(), other.getPhaseCode())

        .append(getWaveTemplateId(), other.getWaveTemplateId())

        .append(getOuId(), other.getOuId())

        .append(getCreateTime(), other.getCreateTime())

        .append(getLastModifyTime(), other.getLastModifyTime())

        .append(getCreatedId(), other.getCreatedId())

        .append(getModifiedId(), other.getModifiedId())

        .append(getLifecycle(), other.getLifecycle())

        .isEquals();
    }
}
