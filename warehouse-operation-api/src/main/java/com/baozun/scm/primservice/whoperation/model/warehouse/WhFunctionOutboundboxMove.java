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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 *
 */
public class WhFunctionOutboundboxMove extends BaseModel {
	
	//alias
	public static final String TABLE_ALIAS = "WhFunctionOutboundboxMove";
	public static final String ALIAS_FUNCTION_ID = "对应功能ID";
	public static final String ALIAS_MOVE_PATTERN = "移动模式  1整箱移动 2部分移动 默认整箱移动";
	public static final String ALIAS_IS_SCAN_SKU = "是否提示扫描商品(整箱移动模式)";
	public static final String ALIAS_SCAN_PATTERN = "扫描模式  1数量扫描 2逐件扫描 默认数量扫描";
	public static final String ALIAS_IS_TIP_INV_ATTR = "是否提示商品库存属性(整箱移动模式)";
	public static final String ALIAS_IS_SCAN_INV_ATTR = "是否扫描商品库存属性(整箱移动模式)";
	public static final String ALIAS_IS_PRINT_CARTON_LABEL = "是否自动打印箱标签";
	public static final String ALIAS_IS_PRINT_PACKING_LIST = "是否自动打印装箱清单";
	public static final String ALIAS_OU_ID = "对应组织ID";
	
	//date formats
	
	//columns START
	/** 对应功能ID */
	private java.lang.Long functionId;
	/** 移动模式  1整箱移动 2部分移动 默认整箱移动 */
	private java.lang.Integer movePattern;
	/** 是否提示扫描商品(整箱移动模式) */
	private java.lang.Boolean isScanSku;
	/** 扫描模式  1数量扫描 2逐件扫描 默认数量扫描 */
	private java.lang.Integer scanPattern;
	/** 是否提示商品库存属性(整箱移动模式) */
	private java.lang.Boolean isTipInvAttr;
	/** 是否扫描商品库存属性(整箱移动模式) */
	private java.lang.Boolean isScanInvAttr;
	/** 是否自动打印箱标签 */
	private java.lang.Boolean isPrintCartonLabel;
	/** 是否自动打印装箱清单 */
	private java.lang.Boolean isPrintPackingList;
	/** 对应组织ID */
	private java.lang.Long ouId;
	//columns END

	public WhFunctionOutboundboxMove(){
	}

	public WhFunctionOutboundboxMove(
		java.lang.Long id
	){
		this.id = id;
	}

	public void setFunctionId(java.lang.Long value) {
		this.functionId = value;
	}
	
	public java.lang.Long getFunctionId() {
		return this.functionId;
	}
	public void setMovePattern(java.lang.Integer value) {
		this.movePattern = value;
	}
	
	public java.lang.Integer getMovePattern() {
		return this.movePattern;
	}
	public void setIsScanSku(java.lang.Boolean value) {
		this.isScanSku = value;
	}
	
	public java.lang.Boolean getIsScanSku() {
		return this.isScanSku;
	}
	public void setScanPattern(java.lang.Integer value) {
		this.scanPattern = value;
	}
	
	public java.lang.Integer getScanPattern() {
		return this.scanPattern;
	}
	public void setIsTipInvAttr(java.lang.Boolean value) {
		this.isTipInvAttr = value;
	}
	
	public java.lang.Boolean getIsTipInvAttr() {
		return this.isTipInvAttr;
	}
	public void setIsScanInvAttr(java.lang.Boolean value) {
		this.isScanInvAttr = value;
	}
	
	public java.lang.Boolean getIsScanInvAttr() {
		return this.isScanInvAttr;
	}
	public void setIsPrintCartonLabel(java.lang.Boolean value) {
		this.isPrintCartonLabel = value;
	}
	
	public java.lang.Boolean getIsPrintCartonLabel() {
		return this.isPrintCartonLabel;
	}
	public void setIsPrintPackingList(java.lang.Boolean value) {
		this.isPrintPackingList = value;
	}
	
	public java.lang.Boolean getIsPrintPackingList() {
		return this.isPrintPackingList;
	}
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("FunctionId",getFunctionId())		
		.append("MovePattern",getMovePattern())		
		.append("IsScanSku",getIsScanSku())		
		.append("ScanPattern",getScanPattern())		
		.append("IsTipInvAttr",getIsTipInvAttr())		
		.append("IsScanInvAttr",getIsScanInvAttr())		
		.append("IsPrintCartonLabel",getIsPrintCartonLabel())		
		.append("IsPrintPackingList",getIsPrintPackingList())		
		.append("OuId",getOuId())		
			.toString();
	}
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getFunctionId())
		.append(getMovePattern())
		.append(getIsScanSku())
		.append(getScanPattern())
		.append(getIsTipInvAttr())
		.append(getIsScanInvAttr())
		.append(getIsPrintCartonLabel())
		.append(getIsPrintPackingList())
		.append(getOuId())
			.toHashCode();
	}
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof WhFunctionOutboundboxMove == false) return false;
		if(this == obj) return true;
		WhFunctionOutboundboxMove other = (WhFunctionOutboundboxMove)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getFunctionId(),other.getFunctionId())

		.append(getMovePattern(),other.getMovePattern())

		.append(getIsScanSku(),other.getIsScanSku())

		.append(getScanPattern(),other.getScanPattern())

		.append(getIsTipInvAttr(),other.getIsTipInvAttr())

		.append(getIsScanInvAttr(),other.getIsScanInvAttr())

		.append(getIsPrintCartonLabel(),other.getIsPrintCartonLabel())

		.append(getIsPrintPackingList(),other.getIsPrintPackingList())

		.append(getOuId(),other.getOuId())

			.isEquals();
	}
}

