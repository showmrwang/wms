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

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 * 
 */
public class WhHandoverStationCommand extends BaseModel {

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

    private java.lang.String chrCode; // 集货交接规则编码
    // columns END
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
    /** 交接批次 */
    private java.lang.String handover_batch;
    /** 当前出库箱数量 */
    private java.lang.Integer Capacity;
    /** 状态 */
    private java.lang.String status;
    /** 是否提示交接 */
    private boolean isTipHandover;
    private String url;
    /** 复核台 */
    private String facilityName;
    /** 复核台组 */
    private String groupName;
    /** 复合台id */
    private java.lang.Long facilityId;

    public java.lang.Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(java.lang.Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public java.lang.String getStatus() {
        return status;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public java.lang.String getHandover_batch() {
        return handover_batch;
    }

    public void setHandover_batch(java.lang.String handover_batch) {
        this.handover_batch = handover_batch;
    }

    public java.lang.Integer getCapacity() {
        return Capacity;
    }

    public void setCapacity(java.lang.Integer capacity) {
        Capacity = capacity;
    }

    public WhHandoverStationCommand() {}

    public WhHandoverStationCommand(java.lang.Long id) {
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

    public java.lang.String getChrCode() {
        return chrCode;
    }

    public void setChrCode(java.lang.String chrCode) {
        this.chrCode = chrCode;
    }

    public java.lang.Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(java.lang.Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public java.lang.Long getCreateId() {
        return createId;
    }

    public void setCreateId(java.lang.Long createId) {
        this.createId = createId;
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

    public java.lang.Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public boolean isTipHandover() {
        return isTipHandover;
    }

    public boolean getIsTipHandover() {
        return isTipHandover;
    }

    public void setTipHandover(boolean isTipHandover) {
        this.isTipHandover = isTipHandover;
    }


}
