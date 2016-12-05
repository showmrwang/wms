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
public class WhFunctionPicking extends BaseModel {
	
	
	//date formats
	
	//columns START
	/** 对应功能ID */
	private java.lang.Long functionId;
	/** 是否自动获取工作 */
	private java.lang.Boolean isAutoObtainWork;
	/** 获取工作方式 工作号、库位号、容器号、出库小批次、波次号、出库箱 */
	private java.lang.String obtainWorkWay;
	/** 自动获取工作最大数量 */
	private java.lang.Integer maxObtainWorkQty;
	/** 是否扫描拣货库位 */
	private java.lang.Boolean isScanLocation;
//	private java.lang.Boolean 
	/** 是否扫描拣货库位托盘 */
	private java.lang.Boolean isScanOuterContainer;
	/** 是否扫描拣货库位货箱 */
	private java.lang.Boolean isScanInsideContainer;
	/** 是否扫描商品 */
	private java.lang.Boolean isScanSku;
	/** 扫描模式  1数量扫描 2逐件扫描 默认数量扫描 */
	private java.lang.Integer scanPattern;
	/** 是否提示商品库存属性 */
	private java.lang.Boolean isTipInvAttr;
	/** 是否扫描商品库存属性 */
	private java.lang.Boolean isScanInvAttr;
	/** 是否扫描货格 */
	private java.lang.Boolean isScanLatticeNo;
	/** 整拖拣货模式 */
	private java.lang.Integer palletPickingMode;
	/** 整箱拣货模式 */
	private java.lang.Integer containerPickingMode;
	/** 对应组织ID */
	private java.lang.Long ouId;
	/**是否扫描出库箱*/
	private java.lang.Boolean isScanOutBoundBox;
	//columns END

	public WhFunctionPicking(){
	}

	public WhFunctionPicking(
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
	public void setIsAutoObtainWork(java.lang.Boolean value) {
		this.isAutoObtainWork = value;
	}
	
	public java.lang.Boolean getIsAutoObtainWork() {
		return this.isAutoObtainWork;
	}
	public void setObtainWorkWay(java.lang.String value) {
		this.obtainWorkWay = value;
	}
	
	public java.lang.String getObtainWorkWay() {
		return this.obtainWorkWay;
	}
	public void setMaxObtainWorkQty(java.lang.Integer value) {
		this.maxObtainWorkQty = value;
	}
	
	public java.lang.Integer getMaxObtainWorkQty() {
		return this.maxObtainWorkQty;
	}
	public void setIsScanLocation(java.lang.Boolean value) {
		this.isScanLocation = value;
	}
	
	public java.lang.Boolean getIsScanLocation() {
		return this.isScanLocation;
	}
	public void setIsScanOuterContainer(java.lang.Boolean value) {
		this.isScanOuterContainer = value;
	}
	
	public java.lang.Boolean getIsScanOuterContainer() {
		return this.isScanOuterContainer;
	}
	public void setIsScanInsideContainer(java.lang.Boolean value) {
		this.isScanInsideContainer = value;
	}
	
	public java.lang.Boolean getIsScanInsideContainer() {
		return this.isScanInsideContainer;
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
	public void setIsScanLatticeNo(java.lang.Boolean value) {
		this.isScanLatticeNo = value;
	}
	
	public java.lang.Boolean getIsScanLatticeNo() {
		return this.isScanLatticeNo;
	}
	public void setPalletPickingMode(java.lang.Integer value) {
		this.palletPickingMode = value;
	}
	
	public java.lang.Integer getPalletPickingMode() {
		return this.palletPickingMode;
	}
	public void setContainerPickingMode(java.lang.Integer value) {
		this.containerPickingMode = value;
	}
	
	public java.lang.Integer getContainerPickingMode() {
		return this.containerPickingMode;
	}
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
	
	
	
    public java.lang.Boolean getIsScanOutBoundBox() {
        return isScanOutBoundBox;
    }

    public void setIsScanOutBoundBox(java.lang.Boolean isScanOutBoundBox) {
        this.isScanOutBoundBox = isScanOutBoundBox;
    }

    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("FunctionId",getFunctionId())		
		.append("IsAutoObtainWork",getIsAutoObtainWork())		
		.append("ObtainWorkWay",getObtainWorkWay())		
		.append("MaxObtainWorkQty",getMaxObtainWorkQty())		
		.append("IsScanLocation",getIsScanLocation())		
		.append("IsScanOuterContainer",getIsScanOuterContainer())		
		.append("IsScanInsideContainer",getIsScanInsideContainer())		
		.append("IsScanSku",getIsScanSku())		
		.append("ScanPattern",getScanPattern())		
		.append("IsTipInvAttr",getIsTipInvAttr())		
		.append("IsScanInvAttr",getIsScanInvAttr())		
		.append("IsScanLatticeNo",getIsScanLatticeNo())		
		.append("PalletPickingMode",getPalletPickingMode())		
		.append("ContainerPickingMode",getContainerPickingMode())		
		.append("OuId",getOuId())		
			.toString();
	}
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getFunctionId())
		.append(getIsAutoObtainWork())
		.append(getObtainWorkWay())
		.append(getMaxObtainWorkQty())
		.append(getIsScanLocation())
		.append(getIsScanOuterContainer())
		.append(getIsScanInsideContainer())
		.append(getIsScanSku())
		.append(getScanPattern())
		.append(getIsTipInvAttr())
		.append(getIsScanInvAttr())
		.append(getIsScanLatticeNo())
		.append(getPalletPickingMode())
		.append(getContainerPickingMode())
		.append(getOuId())
			.toHashCode();
	}
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof WhFunctionPicking == false) return false;
		if(this == obj) return true;
		WhFunctionPicking other = (WhFunctionPicking)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getFunctionId(),other.getFunctionId())

		.append(getIsAutoObtainWork(),other.getIsAutoObtainWork())

		.append(getObtainWorkWay(),other.getObtainWorkWay())

		.append(getMaxObtainWorkQty(),other.getMaxObtainWorkQty())

		.append(getIsScanLocation(),other.getIsScanLocation())

		.append(getIsScanOuterContainer(),other.getIsScanOuterContainer())

		.append(getIsScanInsideContainer(),other.getIsScanInsideContainer())

		.append(getIsScanSku(),other.getIsScanSku())

		.append(getScanPattern(),other.getScanPattern())

		.append(getIsTipInvAttr(),other.getIsTipInvAttr())

		.append(getIsScanInvAttr(),other.getIsScanInvAttr())

		.append(getIsScanLatticeNo(),other.getIsScanLatticeNo())

		.append(getPalletPickingMode(),other.getPalletPickingMode())

		.append(getContainerPickingMode(),other.getContainerPickingMode())

		.append(getOuId(),other.getOuId())

			.isEquals();
	}
}

