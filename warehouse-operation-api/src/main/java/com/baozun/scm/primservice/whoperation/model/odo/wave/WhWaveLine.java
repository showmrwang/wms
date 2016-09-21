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

public class WhWaveLine extends BaseModel {
	
	private static final long serialVersionUID = 7448650201760226012L;
	
	/** 波次ID */
	private java.lang.Long waveId;
	/** 出库单明细行ID */
	private java.lang.Long odoLineId;
	/** 出库单ID */
	private java.lang.Long odoId;
	/** 出库单号 */
	private java.lang.String odoCode;
	/** 出库单优先级 */
	private java.lang.Integer odoPriorityLevel;
	/** 出库单计划发货时间 */
	private java.util.Date odoPlanDeliverGoodsTime;
	/** 出库单下单时间 */
	private java.util.Date odoOrderTime;
	/** 分配规则ID */
	private java.lang.Long allocateRuleId;
	/** 行号 */
	private java.lang.Integer linenum;
	/** 店铺ID */
	private java.lang.Long storeId;
	/** 外部单据行号 */
	private java.lang.Integer extLinenum;
	/** 商品ID */
	private java.lang.Long skuId;
	/** 商品条码 */
	private java.lang.String skuBarCode;
	/** 商品名称 */
	private java.lang.String skuName;
	/** 上位系统商品名称 */
	private java.lang.String extSkuName;
	/** 数量 */
	private Long qty;
	/** 是否整单出库 */
	private java.lang.Boolean isWholeOrderOutbound;
	/** 整行出库标志 */
	private java.lang.Boolean fullLineOutbound;
	/** 生产日期 */
	private java.util.Date mfgDate;
	/** 失效日期 */
	private java.util.Date expDate;
	/** 最小失效日期 */
	private java.util.Date minExpDate;
	/** 最大失效日期 */
	private java.util.Date maxExpDate;
	/** 批次号 */
	private java.lang.String batchNumber;
	/** 原产地 */
	private java.lang.String countryOfOrigin;
	/** 库存状态 */
	private java.lang.Long invStatus;
	/** 库存类型 */
	private java.lang.String invType;
	/** 库存属性1 */
	private java.lang.String invAttr1;
	/** 库存属性2 */
	private java.lang.String invAttr2;
	/** 库存属性3 */
	private java.lang.String invAttr3;
	/** 库存属性4 */
	private java.lang.String invAttr4;
	/** 库存属性5 */
	private java.lang.String invAttr5;
	/** 出库箱类型 */
	private java.lang.Long outboundCartonType;
	/** 颜色 */
	private java.lang.String color;
	/** 款式 */
	private java.lang.String style;
	/** 尺码 */
	private java.lang.String size;
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 创建人ID */
	private java.lang.Long createdId;
	/** 最后修改时间 */
	private java.util.Date lastModifyTime;
	/** 操作人ID */
	private java.lang.Long modifiedId;

	public java.lang.Long getWaveId() {
		return this.waveId;
	}
	
	public void setWaveId(java.lang.Long value) {
		this.waveId = value;
	}
	
	public java.lang.Long getOdoLineId() {
		return this.odoLineId;
	}
	
	public void setOdoLineId(java.lang.Long value) {
		this.odoLineId = value;
	}
	
	public java.lang.Long getOdoId() {
		return this.odoId;
	}
	
	public void setOdoId(java.lang.Long value) {
		this.odoId = value;
	}
	
	public java.lang.String getOdoCode() {
		return this.odoCode;
	}
	
	public void setOdoCode(java.lang.String value) {
		this.odoCode = value;
	}
	
	public java.lang.Integer getOdoPriorityLevel() {
		return this.odoPriorityLevel;
	}
	
	public void setOdoPriorityLevel(java.lang.Integer value) {
		this.odoPriorityLevel = value;
	}
	
	public java.util.Date getOdoPlanDeliverGoodsTime() {
		return this.odoPlanDeliverGoodsTime;
	}
	
	public void setOdoPlanDeliverGoodsTime(java.util.Date value) {
		this.odoPlanDeliverGoodsTime = value;
	}
	
	public java.util.Date getOdoOrderTime() {
		return this.odoOrderTime;
	}
	
	public void setOdoOrderTime(java.util.Date value) {
		this.odoOrderTime = value;
	}
	
	public java.lang.Long getAllocateRuleId() {
		return this.allocateRuleId;
	}
	
	public void setAllocateRuleId(java.lang.Long value) {
		this.allocateRuleId = value;
	}
	
	public java.lang.Integer getLinenum() {
		return this.linenum;
	}
	
	public void setLinenum(java.lang.Integer value) {
		this.linenum = value;
	}
	
	public java.lang.Long getStoreId() {
		return this.storeId;
	}
	
	public void setStoreId(java.lang.Long value) {
		this.storeId = value;
	}
	
	public java.lang.Integer getExtLinenum() {
		return this.extLinenum;
	}
	
	public void setExtLinenum(java.lang.Integer value) {
		this.extLinenum = value;
	}
	
	public java.lang.Long getSkuId() {
		return this.skuId;
	}
	
	public void setSkuId(java.lang.Long value) {
		this.skuId = value;
	}
	
	public java.lang.String getSkuBarCode() {
		return this.skuBarCode;
	}
	
	public void setSkuBarCode(java.lang.String value) {
		this.skuBarCode = value;
	}
	
	public java.lang.String getSkuName() {
		return this.skuName;
	}
	
	public void setSkuName(java.lang.String value) {
		this.skuName = value;
	}
	
	public java.lang.String getExtSkuName() {
		return this.extSkuName;
	}
	
	public void setExtSkuName(java.lang.String value) {
		this.extSkuName = value;
	}
	
	public Long getQty() {
		return this.qty;
	}
	
	public void setQty(Long value) {
		this.qty = value;
	}
	
	public java.lang.Boolean getIsWholeOrderOutbound() {
		return this.isWholeOrderOutbound;
	}
	
	public void setIsWholeOrderOutbound(java.lang.Boolean value) {
		this.isWholeOrderOutbound = value;
	}
	
	public java.lang.Boolean getFullLineOutbound() {
		return this.fullLineOutbound;
	}
	
	public void setFullLineOutbound(java.lang.Boolean value) {
		this.fullLineOutbound = value;
	}
	
	public java.util.Date getMfgDate() {
		return this.mfgDate;
	}
	
	public void setMfgDate(java.util.Date value) {
		this.mfgDate = value;
	}
	
	public java.util.Date getExpDate() {
		return this.expDate;
	}
	
	public void setExpDate(java.util.Date value) {
		this.expDate = value;
	}
	
	public java.util.Date getMinExpDate() {
		return this.minExpDate;
	}
	
	public void setMinExpDate(java.util.Date value) {
		this.minExpDate = value;
	}
	
	public java.util.Date getMaxExpDate() {
		return this.maxExpDate;
	}
	
	public void setMaxExpDate(java.util.Date value) {
		this.maxExpDate = value;
	}
	
	public java.lang.String getBatchNumber() {
		return this.batchNumber;
	}
	
	public void setBatchNumber(java.lang.String value) {
		this.batchNumber = value;
	}
	
	public java.lang.String getCountryOfOrigin() {
		return this.countryOfOrigin;
	}
	
	public void setCountryOfOrigin(java.lang.String value) {
		this.countryOfOrigin = value;
	}
	
	public java.lang.Long getInvStatus() {
		return this.invStatus;
	}
	
	public void setInvStatus(java.lang.Long value) {
		this.invStatus = value;
	}
	
	public java.lang.String getInvType() {
		return this.invType;
	}
	
	public void setInvType(java.lang.String value) {
		this.invType = value;
	}
	
	public java.lang.String getInvAttr1() {
		return this.invAttr1;
	}
	
	public void setInvAttr1(java.lang.String value) {
		this.invAttr1 = value;
	}
	
	public java.lang.String getInvAttr2() {
		return this.invAttr2;
	}
	
	public void setInvAttr2(java.lang.String value) {
		this.invAttr2 = value;
	}
	
	public java.lang.String getInvAttr3() {
		return this.invAttr3;
	}
	
	public void setInvAttr3(java.lang.String value) {
		this.invAttr3 = value;
	}
	
	public java.lang.String getInvAttr4() {
		return this.invAttr4;
	}
	
	public void setInvAttr4(java.lang.String value) {
		this.invAttr4 = value;
	}
	
	public java.lang.String getInvAttr5() {
		return this.invAttr5;
	}
	
	public void setInvAttr5(java.lang.String value) {
		this.invAttr5 = value;
	}
	
	public java.lang.Long getOutboundCartonType() {
		return this.outboundCartonType;
	}
	
	public void setOutboundCartonType(java.lang.Long value) {
		this.outboundCartonType = value;
	}
	
	public java.lang.String getColor() {
		return this.color;
	}
	
	public void setColor(java.lang.String value) {
		this.color = value;
	}
	
	public java.lang.String getStyle() {
		return this.style;
	}
	
	public void setStyle(java.lang.String value) {
		this.style = value;
	}
	
	public java.lang.String getSize() {
		return this.size;
	}
	
	public void setSize(java.lang.String value) {
		this.size = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
	
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.lang.Long getCreatedId() {
		return this.createdId;
	}
	
	public void setCreatedId(java.lang.Long value) {
		this.createdId = value;
	}
	
	public java.util.Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	
	public void setLastModifyTime(java.util.Date value) {
		this.lastModifyTime = value;
	}
	
	public java.lang.Long getModifiedId() {
		return this.modifiedId;
	}
	
	public void setModifiedId(java.lang.Long value) {
		this.modifiedId = value;
	}
	
}

