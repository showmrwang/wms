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
package com.baozun.scm.primservice.whoperation.model.odo.wave;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class WhWave extends BaseModel {

	private static final long serialVersionUID = 7225386756128374805L;
	
	/** 波次主档编码 */
	private java.lang.String code;
	/** 波次状态，系统常量 */
	private java.lang.String status;
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 波次主档ID */
	private java.lang.Long waveMasterId;
	/** 波次阶段，系统常量 */
	private java.lang.String phaseCode;
	/** 硬分配阶段0：分配规则1：硬分配 */
	private java.lang.Integer allocatePhase;
	/** 开始运行时间 */
	private java.util.Date startTime;
	/** 结束运行时间 */
	private java.util.Date finishTime;
	/** 出库单总单数 */
	private java.lang.Integer totalOdoQty;
	/** 出库单明细总行数 */
	private java.lang.Integer totalOdoLineQty;
	/** 总金额 */
	private Long totalAmount;
	/** 总体积 */
	private Long totalVolume;
	/** 总重量 */
	private Long totalWeight;
	/** 商品总件数 */
	private java.lang.Integer totalSkuQty;
	/** 商品种类数 */
	private java.lang.Integer skuCategoryQty;
	/** 工作总单数 */
	private java.lang.Integer execOdoQty;
	/** 工作总行数 */
	private java.lang.Integer execOdoLineQty;
	/** 出库箱总数 */
	private java.lang.Integer outboundCartonQty;
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

	public java.lang.String getCode() {
		return this.code;
	}
	
	public void setCode(java.lang.String value) {
		this.code = value;
	}
	
	public java.lang.String getStatus() {
		return this.status;
	}
	
	public void setStatus(java.lang.String value) {
		this.status = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
	
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.lang.Long getWaveMasterId() {
		return this.waveMasterId;
	}
	
	public void setWaveMasterId(java.lang.Long value) {
		this.waveMasterId = value;
	}
	
	public java.lang.String getPhaseCode() {
		return this.phaseCode;
	}
	
	public void setPhaseCode(java.lang.String value) {
		this.phaseCode = value;
	}
	
	public java.lang.Integer getAllocatePhase() {
		return this.allocatePhase;
	}
	
	public void setAllocatePhase(java.lang.Integer value) {
		this.allocatePhase = value;
	}
	
	public java.util.Date getStartTime() {
		return this.startTime;
	}
	
	public void setStartTime(java.util.Date value) {
		this.startTime = value;
	}
	
	public java.util.Date getFinishTime() {
		return this.finishTime;
	}
	
	public void setFinishTime(java.util.Date value) {
		this.finishTime = value;
	}
	
	public java.lang.Integer getTotalOdoQty() {
		return this.totalOdoQty;
	}
	
	public void setTotalOdoQty(java.lang.Integer value) {
		this.totalOdoQty = value;
	}
	
	public java.lang.Integer getTotalOdoLineQty() {
		return this.totalOdoLineQty;
	}
	
	public void setTotalOdoLineQty(java.lang.Integer value) {
		this.totalOdoLineQty = value;
	}
	
	public Long getTotalAmount() {
		return this.totalAmount;
	}
	
	public void setTotalAmount(Long value) {
		this.totalAmount = value;
	}
	
	public Long getTotalVolume() {
		return this.totalVolume;
	}
	
	public void setTotalVolume(Long value) {
		this.totalVolume = value;
	}
	
	public Long getTotalWeight() {
		return this.totalWeight;
	}
	
	public void setTotalWeight(Long value) {
		this.totalWeight = value;
	}
	
	public java.lang.Integer getTotalSkuQty() {
		return this.totalSkuQty;
	}
	
	public void setTotalSkuQty(java.lang.Integer value) {
		this.totalSkuQty = value;
	}
	
	public java.lang.Integer getSkuCategoryQty() {
		return this.skuCategoryQty;
	}
	
	public void setSkuCategoryQty(java.lang.Integer value) {
		this.skuCategoryQty = value;
	}
	
	public java.lang.Integer getExecOdoQty() {
		return this.execOdoQty;
	}
	
	public void setExecOdoQty(java.lang.Integer value) {
		this.execOdoQty = value;
	}
	
	public java.lang.Integer getExecOdoLineQty() {
		return this.execOdoLineQty;
	}
	
	public void setExecOdoLineQty(java.lang.Integer value) {
		this.execOdoLineQty = value;
	}
	
	public java.lang.Integer getOutboundCartonQty() {
		return this.outboundCartonQty;
	}
	
	public void setOutboundCartonQty(java.lang.Integer value) {
		this.outboundCartonQty = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	
	public void setLastModifyTime(java.util.Date value) {
		this.lastModifyTime = value;
	}
	
	public java.lang.Long getCreatedId() {
		return this.createdId;
	}
	
	public void setCreatedId(java.lang.Long value) {
		this.createdId = value;
	}
	
	public java.lang.Long getModifiedId() {
		return this.modifiedId;
	}
	
	public void setModifiedId(java.lang.Long value) {
		this.modifiedId = value;
	}
	
	public java.lang.Integer getLifecycle() {
		return this.lifecycle;
	}
	
	public void setLifecycle(java.lang.Integer value) {
		this.lifecycle = value;
	}
	
}

