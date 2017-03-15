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
package com.baozun.scm.primservice.whoperation.model.odo;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOdo extends BaseModel {

    private static final long serialVersionUID = 5523630809017120661L;

    /** 出库单号 */
    private String odoCode;
    /** 外部对接编码 */
    private String extCode;
    /** 电商平台订单号 */
    private String ecOrderCode;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 出库单类型 */
    private String odoType;
    /** 原始出库单号 */
    private String originalOdoCode;
    /** 优先级 */
    private Integer priorityLevel;
    /** 是否整单出库0：否 1：是 */
    private Boolean isWholeOrderOutbound;
    /** 越库标志 */
    private String crossDockingSymbol;
    /** 订单平台类型 */
    private String orderType;
    /** 下单时间 */
    private Date orderTime;
    /** 出库单状态 */
    private String odoStatus;
    /** 计划数量 */
    private Double qty;
    /** 本次出库数量 */
    private Double currentQty;
    /** 实际出库数量 */
    private Double actualQty;
    /** 取消数量 */
    private Double cancelQty;
    /** SKU总件数 */
    private Integer skuNumberOfPackages;
    /** 订单总金额 */
    private Double amt;
    /** 配货模式 */
    private String distributeMode;
    /** 上位系统单据类型 */
    private String epistaticSystemsOrderType;
    /** 出库箱类型 */
    private Long outboundCartonType;
    /** 含危险品1:是0:否 */
    private Boolean includeHazardousCargo;
    /** 含易碎品1:是0:否 */
    private Boolean includeFragileCargo;
    /** 是否锁定 */
    private Boolean isLocked;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 合并后出库单单号 */
    private String groupOdoCode;
    /** 是否分配成功 */
    private Boolean isAssignSuccess;
    /** 分配失败原因 */
    private String assignFailReason;
    /** 是否允许合并 1：允许 0：不允许 */
    private java.lang.Boolean isAllowMerge;
    /** 波次号 */
    private String waveCode;
    /** 计数器编码 */
    private String counterCode;
    /** 配货模式码 */
    private String distributionCode;

    /** 当前月份 用于归档 */
    private String sysDate;
    /** 归档时间 */
    private Date archivTime;

    public String getDistributionCode() {
        return distributionCode;
    }

    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode;
    }

    public String getCounterCode() {
        return counterCode;
    }

    public void setCounterCode(String counterCode) {
        this.counterCode = counterCode;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getOdoCode() {
        return odoCode;
    }

    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getEcOrderCode() {
        return ecOrderCode;
    }

    public void setEcOrderCode(String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOdoType() {
        return odoType;
    }

    public void setOdoType(String odoType) {
        this.odoType = odoType;
    }

    public String getOriginalOdoCode() {
        return originalOdoCode;
    }

    public void setOriginalOdoCode(String originalOdoCode) {
        this.originalOdoCode = originalOdoCode;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Boolean getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }

    public void setIsWholeOrderOutbound(Boolean isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
    }

    public String getCrossDockingSymbol() {
        return crossDockingSymbol;
    }

    public void setCrossDockingSymbol(String crossDockingSymbol) {
        this.crossDockingSymbol = crossDockingSymbol;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(Double currentQty) {
        this.currentQty = currentQty;
    }

    public Double getActualQty() {
        return actualQty;
    }

    public void setActualQty(Double actualQty) {
        this.actualQty = actualQty;
    }

    public Double getCancelQty() {
        return cancelQty;
    }

    public void setCancelQty(Double cancelQty) {
        this.cancelQty = cancelQty;
    }

    public Integer getSkuNumberOfPackages() {
        return skuNumberOfPackages;
    }

    public void setSkuNumberOfPackages(Integer skuNumberOfPackages) {
        this.skuNumberOfPackages = skuNumberOfPackages;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getDistributeMode() {
        return distributeMode;
    }

    public void setDistributeMode(String distributeMode) {
        this.distributeMode = distributeMode;
    }

    public String getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }

    public void setEpistaticSystemsOrderType(String epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }

    public Long getOutboundCartonType() {
        return outboundCartonType;
    }

    public void setOutboundCartonType(Long outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }

    public Boolean getIncludeHazardousCargo() {
        return includeHazardousCargo;
    }

    public void setIncludeHazardousCargo(Boolean includeHazardousCargo) {
        this.includeHazardousCargo = includeHazardousCargo;
    }

    public Boolean getIncludeFragileCargo() {
        return includeFragileCargo;
    }

    public void setIncludeFragileCargo(Boolean includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
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

    public String getGroupOdoCode() {
        return groupOdoCode;
    }

    public void setGroupOdoCode(String groupOdoCode) {
        this.groupOdoCode = groupOdoCode;
    }

    public Boolean getIsAssignSuccess() {
        return isAssignSuccess;
    }

    public void setIsAssignSuccess(Boolean isAssignSuccess) {
        this.isAssignSuccess = isAssignSuccess;
    }

    public String getAssignFailReason() {
        return assignFailReason;
    }

    public void setAssignFailReason(String assignFailReason) {
        this.assignFailReason = assignFailReason;
    }

    public Boolean getIsAllowMerge() {
        return isAllowMerge;
    }

    public void setIsAllowMerge(Boolean isAllowMerge) {
        this.isAllowMerge = isAllowMerge;
    }

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public Date getArchivTime() {
        return archivTime;
    }

    public void setArchivTime(Date archivTime) {
        this.archivTime = archivTime;
    }

}
