/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.model.whinterface.outbound;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOutbound extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 3411058928565844192L;
    
    //columns START
	/** 上位系统出库单号 */
	private java.lang.String extOdoCode;
	/** 电商平台订单号 */
	private java.lang.String ecOrderCode;
	/** 客户编码 */
	private java.lang.String customerCode;
	/** 店铺编码 */
	private java.lang.String storeCode;
	/** 出库单类型 */
	private java.lang.String odoType;
	/** 是否整单出库 默认是 */
	private java.lang.Boolean isWholeOrderOutbound;
	/** 部分出库策略 */
	private java.lang.String partOutboundStrategy;
	/** 越库标志 默认否 */
	private java.lang.Boolean crossDockingSymbol;
	/** 订单的平台类型 */
	private java.lang.String ecOrderType;
	/** 下单时间 */
	private java.util.Date orderTime;
	/** 商品数量 */
	private Long qty;
	/** 订单总金额 */
	private Long amt;
	/** 上位系统单据类型 */
	private java.lang.String epistaticSystemsOrderType;
	/** 出库箱类型 */
	private java.lang.String outboundCartonType;
	/** 是否含危险品 默认否 */
	private java.lang.Boolean includeHazardousCargo;
	/** 是否含易碎品 默认否 */
	private java.lang.Boolean includeFragileCargo;
	/** 运输服务商 */
	private java.lang.String transportServiceProvider;
	/** 是否COD 默认否 */
	private java.lang.Boolean isCod;
	/** COD金额 */
	private Long codAmt;
	/** 保价金额 */
	private Long insuranceCoverage;
	/** 数据来源 区分上位系统 */
	private java.lang.String dataSource;
	/** 仓库编码 */
	private java.lang.String whCode;
	/** 是否锁定 默认否 */
	private java.lang.Boolean isLocked;
	/** 快递时效类型 */
	private java.lang.String expressAgingType;
	/** 状态 */
	private java.lang.Integer status;
	/** 失败次数 */
	private java.lang.Integer errorCount;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 最后修改时间 */
	private java.util.Date lastModifyTime;
	//columns END
	
    public java.lang.String getExtOdoCode() {
        return extOdoCode;
    }
    public void setExtOdoCode(java.lang.String extOdoCode) {
        this.extOdoCode = extOdoCode;
    }
    public java.lang.String getEcOrderCode() {
        return ecOrderCode;
    }
    public void setEcOrderCode(java.lang.String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
    }
    public java.lang.String getCustomerCode() {
        return customerCode;
    }
    public void setCustomerCode(java.lang.String customerCode) {
        this.customerCode = customerCode;
    }
    public java.lang.String getStoreCode() {
        return storeCode;
    }
    public void setStoreCode(java.lang.String storeCode) {
        this.storeCode = storeCode;
    }
    public java.lang.String getOdoType() {
        return odoType;
    }
    public void setOdoType(java.lang.String odoType) {
        this.odoType = odoType;
    }
    public java.lang.Boolean getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }
    public void setIsWholeOrderOutbound(java.lang.Boolean isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
    }
    public java.lang.String getPartOutboundStrategy() {
        return partOutboundStrategy;
    }
    public void setPartOutboundStrategy(java.lang.String partOutboundStrategy) {
        this.partOutboundStrategy = partOutboundStrategy;
    }
    public java.lang.Boolean getCrossDockingSymbol() {
        return crossDockingSymbol;
    }
    public void setCrossDockingSymbol(java.lang.Boolean crossDockingSymbol) {
        this.crossDockingSymbol = crossDockingSymbol;
    }
    public java.lang.String getEcOrderType() {
        return ecOrderType;
    }
    public void setEcOrderType(java.lang.String ecOrderType) {
        this.ecOrderType = ecOrderType;
    }
    public java.util.Date getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(java.util.Date orderTime) {
        this.orderTime = orderTime;
    }
    public Long getQty() {
        return qty;
    }
    public void setQty(Long qty) {
        this.qty = qty;
    }
    public Long getAmt() {
        return amt;
    }
    public void setAmt(Long amt) {
        this.amt = amt;
    }
    public java.lang.String getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }
    public void setEpistaticSystemsOrderType(java.lang.String epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }
    public java.lang.String getOutboundCartonType() {
        return outboundCartonType;
    }
    public void setOutboundCartonType(java.lang.String outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }
    public java.lang.Boolean getIncludeHazardousCargo() {
        return includeHazardousCargo;
    }
    public void setIncludeHazardousCargo(java.lang.Boolean includeHazardousCargo) {
        this.includeHazardousCargo = includeHazardousCargo;
    }
    public java.lang.Boolean getIncludeFragileCargo() {
        return includeFragileCargo;
    }
    public void setIncludeFragileCargo(java.lang.Boolean includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
    }
    public java.lang.String getTransportServiceProvider() {
        return transportServiceProvider;
    }
    public void setTransportServiceProvider(java.lang.String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }
    public java.lang.Boolean getIsCod() {
        return isCod;
    }
    public void setIsCod(java.lang.Boolean isCod) {
        this.isCod = isCod;
    }
    public Long getCodAmt() {
        return codAmt;
    }
    public void setCodAmt(Long codAmt) {
        this.codAmt = codAmt;
    }
    public Long getInsuranceCoverage() {
        return insuranceCoverage;
    }
    public void setInsuranceCoverage(Long insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }
    public java.lang.String getDataSource() {
        return dataSource;
    }
    public void setDataSource(java.lang.String dataSource) {
        this.dataSource = dataSource;
    }
    public java.lang.String getWhCode() {
        return whCode;
    }
    public void setWhCode(java.lang.String whCode) {
        this.whCode = whCode;
    }
    public java.lang.Boolean getIsLocked() {
        return isLocked;
    }
    public void setIsLocked(java.lang.Boolean isLocked) {
        this.isLocked = isLocked;
    }
    public java.lang.String getExpressAgingType() {
        return expressAgingType;
    }
    public void setExpressAgingType(java.lang.String expressAgingType) {
        this.expressAgingType = expressAgingType;
    }
    public java.lang.Integer getStatus() {
        return status;
    }
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }
    public java.lang.Integer getErrorCount() {
        return errorCount;
    }
    public void setErrorCount(java.lang.Integer errorCount) {
        this.errorCount = errorCount;
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
    
}

