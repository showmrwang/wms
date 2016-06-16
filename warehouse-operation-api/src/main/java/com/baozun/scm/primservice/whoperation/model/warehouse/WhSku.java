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
public class WhSku extends BaseModel {
	
	
	/** 商品编码 */
	private java.lang.String code;
	/** 外部编码 */
	private java.lang.String extCode;
	/** 商品条码 */
	private java.lang.String barCode;
	/** 商品名称 */
	private java.lang.String name;
	/** 商品英文名称 */
	private java.lang.String enName;
	/** 商品描述 */
	private java.lang.String description;
	/** 颜色 */
	private java.lang.String color;
	/** 长 */
	private Double length;
	/** 宽 */
	private Double width;
	/** 高 */
	private Double height;
	/** 长度单位 */
	private java.lang.String lengthUom;
	/** 体积 */
	private Double volume;
	/** 体积单位 */
	private java.lang.String volumeUom;
	/** 重量 */
	private Double weight;
	/** 重量单位 */
	private java.lang.String weightUom;
	/** 货品类型 */
	private java.lang.Long typeOfGoods;
	/** 所属品牌ID */
	private java.lang.Long brandId;
	/** 所属店铺 */
	private java.lang.Long customerId;
	/** 对应组织ID */
	private java.lang.Long ouId;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 创建人ID */
	private java.lang.Long createdId;
	/** 修改时间 */
	private java.util.Date lastModifyTime;
	/** 修改人ID */
	private java.lang.Long modifiedId;
	/** 款式 */
	private java.lang.String style;
	/** 尺码 */
	private java.lang.String size;
	/** 1.可用;2.已删除;0.禁用 */
	private java.lang.Integer lifecycle;

	public WhSku(){
	}

	public WhSku(
		java.lang.Long id
	){
		this.id = id;
	}

	public void setCode(java.lang.String value) {
		this.code = value;
	}
	
	public java.lang.String getCode() {
		return this.code;
	}
	public void setExtCode(java.lang.String value) {
		this.extCode = value;
	}
	
	public java.lang.String getExtCode() {
		return this.extCode;
	}
	public void setBarCode(java.lang.String value) {
		this.barCode = value;
	}
	
	public java.lang.String getBarCode() {
		return this.barCode;
	}
	public void setName(java.lang.String value) {
		this.name = value;
	}
	
	public java.lang.String getName() {
		return this.name;
	}
	public void setEnName(java.lang.String value) {
		this.enName = value;
	}
	
	public java.lang.String getEnName() {
		return this.enName;
	}
	public void setDescription(java.lang.String value) {
		this.description = value;
	}
	
	public java.lang.String getDescription() {
		return this.description;
	}
	public void setColor(java.lang.String value) {
		this.color = value;
	}
	
	public java.lang.String getColor() {
		return this.color;
	}
	public void setLength(Double value) {
		this.length = value;
	}
	
	public Double getLength() {
		return this.length;
	}
	public void setWidth(Double value) {
		this.width = value;
	}
	
	public Double getWidth() {
		return this.width;
	}
	public void setHeight(Double value) {
		this.height = value;
	}
	
	public Double getHeight() {
		return this.height;
	}
	public void setLengthUom(java.lang.String value) {
		this.lengthUom = value;
	}
	
	public java.lang.String getLengthUom() {
		return this.lengthUom;
	}
	public void setVolume(Double value) {
		this.volume = value;
	}
	
	public Double getVolume() {
		return this.volume;
	}
	public void setVolumeUom(java.lang.String value) {
		this.volumeUom = value;
	}
	
	public java.lang.String getVolumeUom() {
		return this.volumeUom;
	}
	public void setWeight(Double value) {
		this.weight = value;
	}
	
	public Double getWeight() {
		return this.weight;
	}
	public void setWeightUom(java.lang.String value) {
		this.weightUom = value;
	}
	
	public java.lang.String getWeightUom() {
		return this.weightUom;
	}
	public void setTypeOfGoods(java.lang.Long value) {
		this.typeOfGoods = value;
	}
	
	public java.lang.Long getTypeOfGoods() {
		return this.typeOfGoods;
	}
	public void setBrandId(java.lang.Long value) {
		this.brandId = value;
	}
	
	public java.lang.Long getBrandId() {
		return this.brandId;
	}
	public void setCustomerId(java.lang.Long value) {
		this.customerId = value;
	}
	
	public java.lang.Long getCustomerId() {
		return this.customerId;
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
	public void setCreatedId(java.lang.Long value) {
		this.createdId = value;
	}
	
	public java.lang.Long getCreatedId() {
		return this.createdId;
	}
	public void setLastModifyTime(java.util.Date value) {
		this.lastModifyTime = value;
	}
	
	public java.util.Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	public void setModifiedId(java.lang.Long value) {
		this.modifiedId = value;
	}
	
	public java.lang.Long getModifiedId() {
		return this.modifiedId;
	}
	public void setStyle(java.lang.String value) {
		this.style = value;
	}
	
	public java.lang.String getStyle() {
		return this.style;
	}
	public void setSize(java.lang.String value) {
		this.size = value;
	}
	
	public java.lang.String getSize() {
		return this.size;
	}
	public void setLifecycle(java.lang.Integer value) {
		this.lifecycle = value;
	}
	
	public java.lang.Integer getLifecycle() {
		return this.lifecycle;
	}
    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("Code",getCode())		
		.append("ExtCode",getExtCode())		
		.append("BarCode",getBarCode())		
		.append("Name",getName())		
		.append("EnName",getEnName())		
		.append("Description",getDescription())		
		.append("Color",getColor())		
		.append("Length",getLength())		
		.append("Width",getWidth())		
		.append("Height",getHeight())		
		.append("LengthUom",getLengthUom())		
		.append("Volume",getVolume())		
		.append("VolumeUom",getVolumeUom())		
		.append("Weight",getWeight())		
		.append("WeightUom",getWeightUom())		
		.append("TypeOfGoods",getTypeOfGoods())		
		.append("BrandId",getBrandId())		
		.append("CustomerId",getCustomerId())		
		.append("OuId",getOuId())		
		.append("CreateTime",getCreateTime())		
		.append("CreatedId",getCreatedId())		
		.append("LastModifyTime",getLastModifyTime())		
		.append("ModifiedId",getModifiedId())		
		.append("Style",getStyle())		
		.append("Size",getSize())		
		.append("Lifecycle",getLifecycle())		
			.toString();
	}
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getCode())
		.append(getExtCode())
		.append(getBarCode())
		.append(getName())
		.append(getEnName())
		.append(getDescription())
		.append(getColor())
		.append(getLength())
		.append(getWidth())
		.append(getHeight())
		.append(getLengthUom())
		.append(getVolume())
		.append(getVolumeUom())
		.append(getWeight())
		.append(getWeightUom())
		.append(getTypeOfGoods())
		.append(getBrandId())
		.append(getCustomerId())
		.append(getOuId())
		.append(getCreateTime())
		.append(getCreatedId())
		.append(getLastModifyTime())
		.append(getModifiedId())
		.append(getStyle())
		.append(getSize())
		.append(getLifecycle())
			.toHashCode();
	}
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof WhSku == false) return false;
		if(this == obj) return true;
		WhSku other = (WhSku)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getCode(),other.getCode())

		.append(getExtCode(),other.getExtCode())

		.append(getBarCode(),other.getBarCode())

		.append(getName(),other.getName())

		.append(getEnName(),other.getEnName())

		.append(getDescription(),other.getDescription())

		.append(getColor(),other.getColor())

		.append(getLength(),other.getLength())

		.append(getWidth(),other.getWidth())

		.append(getHeight(),other.getHeight())

		.append(getLengthUom(),other.getLengthUom())

		.append(getVolume(),other.getVolume())

		.append(getVolumeUom(),other.getVolumeUom())

		.append(getWeight(),other.getWeight())

		.append(getWeightUom(),other.getWeightUom())

		.append(getTypeOfGoods(),other.getTypeOfGoods())

		.append(getBrandId(),other.getBrandId())

		.append(getCustomerId(),other.getCustomerId())

		.append(getOuId(),other.getOuId())

		.append(getCreateTime(),other.getCreateTime())

		.append(getCreatedId(),other.getCreatedId())

		.append(getLastModifyTime(),other.getLastModifyTime())

		.append(getModifiedId(),other.getModifiedId())

		.append(getStyle(),other.getStyle())

		.append(getSize(),other.getSize())

		.append(getLifecycle(),other.getLifecycle())

			.isEquals();
	}
}

