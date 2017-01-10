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
public class WhSeedingCollectionCommand extends BaseCommand {

    private static final long serialVersionUID = 8610455402243874039L;

    // columns START
    /** id */
    private Long id;
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
    private Integer seedingStatus;
    /** 对应组织ID */
    private Long ouId;

    // ===============自定义字段===============
    /** 播种墙编码*/
    private String seedingwallCode;

    // columns END

    public WhSeedingCollectionCommand() {}

    public WhSeedingCollectionCommand(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getSeedingwallCode() {
        return seedingwallCode;
    }

    public void setSeedingwallCode(String seedingwallCode) {
        this.seedingwallCode = seedingwallCode;
    }

    public Integer getSeedingStatus() {
        return seedingStatus;
    }

    public void setSeedingStatus(Integer seedingStatus) {
        this.seedingStatus = seedingStatus;
    }

}
