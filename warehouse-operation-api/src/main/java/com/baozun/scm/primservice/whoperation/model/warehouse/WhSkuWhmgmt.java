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
public class WhSkuWhmgmt extends BaseModel {
	

	/** SKU_ID */
	private java.lang.Long skuId;
	/** 占零拣货位数量 */
	private java.lang.Integer occupancyZeroPickingQty;
	/** 占箱拣货位数量 */
	private java.lang.Integer occupancyBoxPickingQty;
	/** 拣货率  用于人员绩效管理 */
	private Long pickingRate;
	/** 打包率 用于人员绩效管理 */
	private Long packingRate;
	/** 特殊处理率 */
	private Long abnormalRate;
	/** 二级容器类型 */
	private java.lang.Long twoLevelType;
	/** 出库箱类型 */
	private java.lang.Long outboundCtnType;
	/** 上架规则 */
	private java.lang.Long shelfRule;
	/** 分配规则 */
	private java.lang.Long allocateRule;
	/** 码盘标准 */
	private java.lang.String codeWheelNorm;
	/** 最后盘点时间 */
	private java.util.Date lastCheckTime;
	/** 对应组织ID */
	private java.lang.Long ouId;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 最后修改时间 */
	private java.util.Date lastModifyTime;
	/** 操作人ID */
	private java.lang.Long operatorId;
	/** 货品类型 */
    private java.lang.Long typeOfGoods;
    

	
	
	public java.lang.Long getTypeOfGoods() {
        return typeOfGoods;
    }

    public void setTypeOfGoods(java.lang.Long typeOfGoods) {
        this.typeOfGoods = typeOfGoods;
    }

    public WhSkuWhmgmt(){
	}

	public WhSkuWhmgmt(
		java.lang.Long id
	){
		this.id = id;
	}

	public void setSkuId(java.lang.Long value) {
		this.skuId = value;
	}
	
	public java.lang.Long getSkuId() {
		return this.skuId;
	}
	public void setOccupancyZeroPickingQty(java.lang.Integer value) {
		this.occupancyZeroPickingQty = value;
	}
	
	public java.lang.Integer getOccupancyZeroPickingQty() {
		return this.occupancyZeroPickingQty;
	}
	public void setOccupancyBoxPickingQty(java.lang.Integer value) {
		this.occupancyBoxPickingQty = value;
	}
	
	public java.lang.Integer getOccupancyBoxPickingQty() {
		return this.occupancyBoxPickingQty;
	}
	public void setPickingRate(Long value) {
		this.pickingRate = value;
	}
	
	public Long getPickingRate() {
		return this.pickingRate;
	}
	public void setPackingRate(Long value) {
		this.packingRate = value;
	}
	
	public Long getPackingRate() {
		return this.packingRate;
	}
	public void setAbnormalRate(Long value) {
		this.abnormalRate = value;
	}
	
	public Long getAbnormalRate() {
		return this.abnormalRate;
	}
	public void setTwoLevelType(java.lang.Long value) {
		this.twoLevelType = value;
	}
	
	public java.lang.Long getTwoLevelType() {
		return this.twoLevelType;
	}
	public void setOutboundCtnType(java.lang.Long value) {
		this.outboundCtnType = value;
	}
	
	public java.lang.Long getOutboundCtnType() {
		return this.outboundCtnType;
	}
	public void setShelfRule(java.lang.Long value) {
		this.shelfRule = value;
	}
	
	public java.lang.Long getShelfRule() {
		return this.shelfRule;
	}
	public void setAllocateRule(java.lang.Long value) {
		this.allocateRule = value;
	}
	
	public java.lang.Long getAllocateRule() {
		return this.allocateRule;
	}
	public void setCodeWheelNorm(java.lang.String value) {
		this.codeWheelNorm = value;
	}
	
	public java.lang.String getCodeWheelNorm() {
		return this.codeWheelNorm;
	}
	public void setLastCheckTime(java.util.Date value) {
		this.lastCheckTime = value;
	}
	
	public java.util.Date getLastCheckTime() {
		return this.lastCheckTime;
	}
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
	
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	
	public void setLastModifyTime(java.util.Date value) {
		this.lastModifyTime = value;
	}
	
	public java.util.Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	public void setOperatorId(java.lang.Long value) {
		this.operatorId = value;
	}
	
	public java.lang.Long getOperatorId() {
		return this.operatorId;
	}
    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("SkuId",getSkuId())		
		.append("OccupancyZeroPickingQty",getOccupancyZeroPickingQty())		
		.append("OccupancyBoxPickingQty",getOccupancyBoxPickingQty())		
		.append("PickingRate",getPickingRate())		
		.append("PackingRate",getPackingRate())		
		.append("AbnormalRate",getAbnormalRate())		
		.append("TwoLevelType",getTwoLevelType())		
		.append("OutboundCtnType",getOutboundCtnType())		
		.append("ShelfRule",getShelfRule())		
		.append("AllocateRule",getAllocateRule())		
		.append("CodeWheelNorm",getCodeWheelNorm())		
		.append("LastCheckTime",getLastCheckTime())		
		.append("OuId",getOuId())		
		.append("CreateTime",getCreateTime())		
		.append("LastModifyTime",getLastModifyTime())		
		.append("OperatorId",getOperatorId())		
			.toString();
	}
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getSkuId())
		.append(getOccupancyZeroPickingQty())
		.append(getOccupancyBoxPickingQty())
		.append(getPickingRate())
		.append(getPackingRate())
		.append(getAbnormalRate())
		.append(getTwoLevelType())
		.append(getOutboundCtnType())
		.append(getShelfRule())
		.append(getAllocateRule())
		.append(getCodeWheelNorm())
		.append(getLastCheckTime())
		.append(getOuId())
		.append(getCreateTime())
		.append(getLastModifyTime())
		.append(getOperatorId())
			.toHashCode();
	}
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof WhSkuWhmgmt == false) return false;
		if(this == obj) return true;
		WhSkuWhmgmt other = (WhSkuWhmgmt)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getSkuId(),other.getSkuId())

		.append(getOccupancyZeroPickingQty(),other.getOccupancyZeroPickingQty())

		.append(getOccupancyBoxPickingQty(),other.getOccupancyBoxPickingQty())

		.append(getPickingRate(),other.getPickingRate())

		.append(getPackingRate(),other.getPackingRate())

		.append(getAbnormalRate(),other.getAbnormalRate())

		.append(getTwoLevelType(),other.getTwoLevelType())

		.append(getOutboundCtnType(),other.getOutboundCtnType())

		.append(getShelfRule(),other.getShelfRule())

		.append(getAllocateRule(),other.getAllocateRule())

		.append(getCodeWheelNorm(),other.getCodeWheelNorm())

		.append(getLastCheckTime(),other.getLastCheckTime())

		.append(getOuId(),other.getOuId())

		.append(getCreateTime(),other.getCreateTime())

		.append(getLastModifyTime(),other.getLastModifyTime())

		.append(getOperatorId(),other.getOperatorId())

			.isEquals();
	}
}

