package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 3423612599719415302L;

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
    /** 部分出库策略 */
    private String partOutboundStrategy;
    /** 越库标志 */
    private String crossDockingSymbol;
    /** 订单平台类型 */
    private String orderType;
    /** 下单时间 */
    private java.util.Date orderTime;
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
    private java.util.Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** id */
    private Long id;
    // ------------------------------------------------------------------------------------
    // 自定义字段
    private Long userId;
    private String createTimeStr;
    private String lastModifyTimeStr;
    private String orderTimeStr;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderTimeStr() {
        return orderTimeStr;
    }

    public void setOrderTimeStr(String orderTimeStr) {
        this.orderTimeStr = orderTimeStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getLastModifyTimeStr() {
        return lastModifyTimeStr;
    }

    public void setLastModifyTimeStr(String lastModifyTimeStr) {
        this.lastModifyTimeStr = lastModifyTimeStr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getPartOutboundStrategy() {
        return partOutboundStrategy;
    }

    public void setPartOutboundStrategy(String partOutboundStrategy) {
        this.partOutboundStrategy = partOutboundStrategy;
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

    public java.util.Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(java.util.Date orderTime) {
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

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(java.util.Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }



}
