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
 * 播种集货表(t_wh_seeding_collection)
 * @author larkark
 *
 */
public class WhSeedingCollection extends BaseModel {

    private static final long serialVersionUID = 5069651238461522418L;

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
    /** 播种状态*/
    private Integer collectionStatus;
    /** 对应组织ID */
    private Long ouId;

    // columns END

    public WhSeedingCollection() {}

    public WhSeedingCollection(Long id) {
        this.id = id;
    }

    public void setFacilityId(Long value) {
        this.facilityId = value;
    }

    public Long getFacilityId() {
        return this.facilityId;
    }

    public void setTemporaryLocationId(Long value) {
        this.temporaryLocationId = value;
    }

    public Long getTemporaryLocationId() {
        return this.temporaryLocationId;
    }

    public void setLocationId(Long value) {
        this.locationId = value;
    }

    public Long getLocationId() {
        return this.locationId;
    }

    public void setContainerId(Long value) {
        this.containerId = value;
    }

    public Long getContainerId() {
        return this.containerId;
    }

    public void setBatch(String value) {
        this.batch = value;
    }

    public String getBatch() {
        return this.batch;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public Integer getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(Integer collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

}
