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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 复核头表(t_wh_checking)
 * 
 * @author larkark
 *
 */
public class WhChecking extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 6019495871640623013L;
    
    //columns START
	/** 播种墙ID */
	private java.lang.Long facilityId;
	/** 周转箱ID */
	private java.lang.Long containerId;
	/** 小批次 */
	private java.lang.String batch;
	/** 波次号 */
	private java.lang.String waveCode;
	/** 客户CODE */
	private java.lang.String customerCode;
	/** 客户名称 */
	private java.lang.String customerName;
	/** 店铺CODE */
	private java.lang.String storeCode;
	/** 店铺名称 */
	private java.lang.String storeName;
	/** 运输服务商CODE */
	private java.lang.String transportCode;
	/** 运输服务商名称 */
	private java.lang.String transportName;
	/** 产品类型CODE */
	private java.lang.String productCode;
	/** 产品类型名称 */
	private java.lang.String productName;
	/** 时效类型CODE */
	private java.lang.String timeEffectCode;
	/** 时效类型名称 */
	private java.lang.String timeEffectName;
	/** 复核状态 */
	private java.lang.String status;
	/** 对应组织ID */
	private java.lang.Long ouId;
	/** 外部容器，小车 */
	private java.lang.Long outerContainerId;
	/** 货格编码数 */
	private java.lang.Integer containerLatticeNo;
	/** 耗材ID */
	private java.lang.Long outboundboxId;
	/** 出库箱编码 */
	private java.lang.String outboundboxCode;
	/** 配货模式 */
	private java.lang.String distributionMode;
	/** 拣货模式 */
	private java.lang.String pickingMode;
	/** 复核模式 */
	private java.lang.String checkingMode;
	//columns END
	
	
    public java.lang.Long getFacilityId() {
        return facilityId;
    }
    public void setFacilityId(java.lang.Long facilityId) {
        this.facilityId = facilityId;
    }
    public java.lang.Long getContainerId() {
        return containerId;
    }
    public void setContainerId(java.lang.Long containerId) {
        this.containerId = containerId;
    }
    public java.lang.String getBatch() {
        return batch;
    }
    public void setBatch(java.lang.String batch) {
        this.batch = batch;
    }
    public java.lang.String getWaveCode() {
        return waveCode;
    }
    public void setWaveCode(java.lang.String waveCode) {
        this.waveCode = waveCode;
    }
    public java.lang.String getCustomerCode() {
        return customerCode;
    }
    public void setCustomerCode(java.lang.String customerCode) {
        this.customerCode = customerCode;
    }
    public java.lang.String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(java.lang.String customerName) {
        this.customerName = customerName;
    }
    public java.lang.String getStoreCode() {
        return storeCode;
    }
    public void setStoreCode(java.lang.String storeCode) {
        this.storeCode = storeCode;
    }
    public java.lang.String getStoreName() {
        return storeName;
    }
    public void setStoreName(java.lang.String storeName) {
        this.storeName = storeName;
    }
    public java.lang.String getTransportCode() {
        return transportCode;
    }
    public void setTransportCode(java.lang.String transportCode) {
        this.transportCode = transportCode;
    }
    public java.lang.String getTransportName() {
        return transportName;
    }
    public void setTransportName(java.lang.String transportName) {
        this.transportName = transportName;
    }
    public java.lang.String getProductCode() {
        return productCode;
    }
    public void setProductCode(java.lang.String productCode) {
        this.productCode = productCode;
    }
    public java.lang.String getProductName() {
        return productName;
    }
    public void setProductName(java.lang.String productName) {
        this.productName = productName;
    }
    public java.lang.String getTimeEffectCode() {
        return timeEffectCode;
    }
    public void setTimeEffectCode(java.lang.String timeEffectCode) {
        this.timeEffectCode = timeEffectCode;
    }
    public java.lang.String getTimeEffectName() {
        return timeEffectName;
    }
    public void setTimeEffectName(java.lang.String timeEffectName) {
        this.timeEffectName = timeEffectName;
    }
    public java.lang.String getStatus() {
        return status;
    }
    public void setStatus(java.lang.String status) {
        this.status = status;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.lang.Long getOuterContainerId() {
        return outerContainerId;
    }
    public void setOuterContainerId(java.lang.Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }
    public java.lang.Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }
    public void setContainerLatticeNo(java.lang.Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }
    public java.lang.Long getOutboundboxId() {
        return outboundboxId;
    }
    public void setOutboundboxId(java.lang.Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }
    public java.lang.String getOutboundboxCode() {
        return outboundboxCode;
    }
    public void setOutboundboxCode(java.lang.String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }
    public java.lang.String getDistributionMode() {
        return distributionMode;
    }
    public void setDistributionMode(java.lang.String distributionMode) {
        this.distributionMode = distributionMode;
    }
    public java.lang.String getPickingMode() {
        return pickingMode;
    }
    public void setPickingMode(java.lang.String pickingMode) {
        this.pickingMode = pickingMode;
    }
    public java.lang.String getCheckingMode() {
        return checkingMode;
    }
    public void setCheckingMode(java.lang.String checkingMode) {
        this.checkingMode = checkingMode;
    }

}

