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
public class WhFacilityLocationSkuVolume extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -1406232931645827489L;

    // columns START
    /** 复核台ID */
    private java.lang.Long facilityId;
    /** 库位商品容量ID */
    private java.lang.Long lsvId;
    /** 对应组织ID */
    private java.lang.Long ouId;

    // columns END

    public WhFacilityLocationSkuVolume() {}

    public WhFacilityLocationSkuVolume(java.lang.Long id) {
        this.id = id;
    }

    public void setFacilityId(java.lang.Long value) {
        this.facilityId = value;
    }

    public java.lang.Long getFacilityId() {
        return this.facilityId;
    }

    public void setLsvId(java.lang.Long value) {
        this.lsvId = value;
    }

    public java.lang.Long getLsvId() {
        return this.lsvId;
    }

    public void setOuId(java.lang.Long value) {
        this.ouId = value;
    }

    public java.lang.Long getOuId() {
        return this.ouId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("Id", getId()).append("FacilityId", getFacilityId()).append("LsvId", getLsvId()).append("OuId", getOuId()).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).append(getFacilityId()).append(getLsvId()).append(getOuId()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WhFacilityLocationSkuVolume == false) return false;
        if (this == obj) return true;
        WhFacilityLocationSkuVolume other = (WhFacilityLocationSkuVolume) obj;
        return new EqualsBuilder().append(getId(), other.getId())

        .append(getFacilityId(), other.getFacilityId())

        .append(getLsvId(), other.getLsvId())

        .append(getOuId(), other.getOuId())

        .isEquals();
    }
}
