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
 package com.baozun.scm.primservice.whoperation.command.warehouse.inventory;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author larkark
 *
 */
public class WhSkuInventoryTobefilledCommand extends BaseModel {
	
    private static final long serialVersionUID = -7526776915622545994L;
    //alias
	public static final String TABLE_ALIAS = "WhSkuInventoryTobefilled";
	public static final String ALIAS_SKU_ID = "商品ID";
	public static final String ALIAS_LOCATION_ID = "库位ID 库位号";
	public static final String ALIAS_OUTER_CONTAINER_ID = "外部容器ID 托盘 货箱";
	public static final String ALIAS_INSIDE_CONTAINER_ID = "内部容器ID 托盘 货箱";
	public static final String ALIAS_CUSTOMER_ID = "客户ID";
	public static final String ALIAS_STORE_ID = "店铺ID";
	public static final String ALIAS_OCCUPATION_CODE = "占用单据号";
	public static final String ALIAS_OCCUPATION_LINE_ID = "占用单据明细行ID";
	public static final String ALIAS_REPLENISHMENT_CODE = "补货单号";
	public static final String ALIAS_QTY = "调整数量";
	public static final String ALIAS_INV_STATUS = "库存状态";
	public static final String ALIAS_INV_TYPE = "库存类型";
	public static final String ALIAS_BATCH_NUMBER = "批次号";
	public static final String ALIAS_MFG_DATE = "生产日期";
	public static final String ALIAS_EXP_DATE = "失效日期";
	public static final String ALIAS_COUNTRY_OF_ORIGIN = "原产地";
	public static final String ALIAS_INV_ATTR1 = "库存属性1";
	public static final String ALIAS_INV_ATTR2 = "库存属性2";
	public static final String ALIAS_INV_ATTR3 = "库存属性3";
	public static final String ALIAS_INV_ATTR4 = "库存属性4";
	public static final String ALIAS_INV_ATTR5 = "库存属性5";
	public static final String ALIAS_UUID = "内部对接码";
	public static final String ALIAS_OU_ID = "对应仓库ID";
	public static final String ALIAS_LAST_MODIFY_TIME = "最后操作时间";

	//columns START
	/** 商品ID */
	private java.lang.Long skuId;
	/** 库位ID 库位号 */
	private java.lang.Long locationId;
	/** 外部容器ID 托盘 货箱 */
	private java.lang.Long outerContainerId;
	/** 内部容器ID 托盘 货箱 */
	private java.lang.Long insideContainerId;
	/** 客户ID */
	private java.lang.Long customerId;
	/** 店铺ID */
	private java.lang.Long storeId;
	/** 占用单据号 */
	private java.lang.String occupationCode;
	/** 占用单据明细行ID */
	private java.lang.Long occupationLineId;
	/** 补货单号 */
	private java.lang.String replenishmentCode;
	/** 调整数量 */
	private Double qty;
	/** 库存状态 */
	private java.lang.Long invStatus;
	/** 库存类型 */
	private java.lang.String invType;
	/** 批次号 */
	private java.lang.String batchNumber;
	/** 生产日期 */
	private java.util.Date mfgDate;
	/** 失效日期 */
	private java.util.Date expDate;
	/** 原产地 */
	private java.lang.String countryOfOrigin;
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
	/** 内部对接码 */
	private java.lang.String uuid;
	/** 对应仓库ID */
	private java.lang.Long ouId;
	/** 最后操作时间 */
	private java.util.Date lastModifyTime;
	/** 占用单据号来源 */
    private String occupationCodeSource;
	//columns END
	
    /** 残次库存信息 */
    private List<WhSkuInventorySnCommand> whSkuInventorySnCommandList;

	public WhSkuInventoryTobefilledCommand(){
	}

	public WhSkuInventoryTobefilledCommand(
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
	public void setLocationId(java.lang.Long value) {
		this.locationId = value;
	}
	
	public java.lang.Long getLocationId() {
		return this.locationId;
	}
	public void setOuterContainerId(java.lang.Long value) {
		this.outerContainerId = value;
	}
	
	public java.lang.Long getOuterContainerId() {
		return this.outerContainerId;
	}
	public void setInsideContainerId(java.lang.Long value) {
		this.insideContainerId = value;
	}
	
	public java.lang.Long getInsideContainerId() {
		return this.insideContainerId;
	}
	public void setCustomerId(java.lang.Long value) {
		this.customerId = value;
	}
	
	public java.lang.Long getCustomerId() {
		return this.customerId;
	}
	public void setStoreId(java.lang.Long value) {
		this.storeId = value;
	}
	
	public java.lang.Long getStoreId() {
		return this.storeId;
	}
	public void setOccupationCode(java.lang.String value) {
		this.occupationCode = value;
	}
	
	public java.lang.String getOccupationCode() {
		return this.occupationCode;
	}
	public void setOccupationLineId(java.lang.Long value) {
		this.occupationLineId = value;
	}
	
	public java.lang.Long getOccupationLineId() {
		return this.occupationLineId;
	}
	public void setReplenishmentCode(java.lang.String value) {
		this.replenishmentCode = value;
	}
	
	public java.lang.String getReplenishmentCode() {
		return this.replenishmentCode;
	}
	public void setQty(Double value) {
		this.qty = value;
	}
	
	public Double getQty() {
		return this.qty;
	}
	public void setInvStatus(java.lang.Long value) {
		this.invStatus = value;
	}
	
	public java.lang.Long getInvStatus() {
		return this.invStatus;
	}
	public void setInvType(java.lang.String value) {
		this.invType = value;
	}
	
	public java.lang.String getInvType() {
		return this.invType;
	}
	public void setBatchNumber(java.lang.String value) {
		this.batchNumber = value;
	}
	
	public java.lang.String getBatchNumber() {
		return this.batchNumber;
	}
	
	public void setMfgDate(java.util.Date value) {
		this.mfgDate = value;
	}
	
	public java.util.Date getMfgDate() {
		return this.mfgDate;
	}
	
	public void setExpDate(java.util.Date value) {
		this.expDate = value;
	}
	
	public java.util.Date getExpDate() {
		return this.expDate;
	}
	public void setCountryOfOrigin(java.lang.String value) {
		this.countryOfOrigin = value;
	}
	
	public java.lang.String getCountryOfOrigin() {
		return this.countryOfOrigin;
	}
	public void setInvAttr1(java.lang.String value) {
		this.invAttr1 = value;
	}
	
	public java.lang.String getInvAttr1() {
		return this.invAttr1;
	}
	public void setInvAttr2(java.lang.String value) {
		this.invAttr2 = value;
	}
	
	public java.lang.String getInvAttr2() {
		return this.invAttr2;
	}
	public void setInvAttr3(java.lang.String value) {
		this.invAttr3 = value;
	}
	
	public java.lang.String getInvAttr3() {
		return this.invAttr3;
	}
	public void setInvAttr4(java.lang.String value) {
		this.invAttr4 = value;
	}
	
	public java.lang.String getInvAttr4() {
		return this.invAttr4;
	}
	public void setInvAttr5(java.lang.String value) {
		this.invAttr5 = value;
	}
	
	public java.lang.String getInvAttr5() {
		return this.invAttr5;
	}
	public void setUuid(java.lang.String value) {
		this.uuid = value;
	}
	
	public java.lang.String getUuid() {
		return this.uuid;
	}
	public void setOuId(java.lang.Long value) {
		this.ouId = value;
	}
	
	public java.lang.Long getOuId() {
		return this.ouId;
	}
	
	public void setLastModifyTime(java.util.Date value) {
		this.lastModifyTime = value;
	}
	
	public java.util.Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	
    public String getOccupationCodeSource() {
        return occupationCodeSource;
    }

    public void setOccupationCodeSource(String occupationCodeSource) {
        this.occupationCodeSource = occupationCodeSource;
    }

    @Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("Id",getId())		
		.append("SkuId",getSkuId())		
		.append("LocationId",getLocationId())		
		.append("OuterContainerId",getOuterContainerId())		
		.append("InsideContainerId",getInsideContainerId())		
		.append("CustomerId",getCustomerId())		
		.append("StoreId",getStoreId())		
		.append("OccupationCode",getOccupationCode())		
		.append("OccupationLineId",getOccupationLineId())		
		.append("ReplenishmentCode",getReplenishmentCode())		
		.append("Qty",getQty())		
		.append("InvStatus",getInvStatus())		
		.append("InvType",getInvType())		
		.append("BatchNumber",getBatchNumber())		
		.append("MfgDate",getMfgDate())		
		.append("ExpDate",getExpDate())		
		.append("CountryOfOrigin",getCountryOfOrigin())		
		.append("InvAttr1",getInvAttr1())		
		.append("InvAttr2",getInvAttr2())		
		.append("InvAttr3",getInvAttr3())		
		.append("InvAttr4",getInvAttr4())		
		.append("InvAttr5",getInvAttr5())		
		.append("Uuid",getUuid())		
		.append("OuId",getOuId())		
		.append("LastModifyTime",getLastModifyTime())		
			.toString();
	}
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getId())
		.append(getSkuId())
		.append(getLocationId())
		.append(getOuterContainerId())
		.append(getInsideContainerId())
		.append(getCustomerId())
		.append(getStoreId())
		.append(getOccupationCode())
		.append(getOccupationLineId())
		.append(getReplenishmentCode())
		.append(getQty())
		.append(getInvStatus())
		.append(getInvType())
		.append(getBatchNumber())
		.append(getMfgDate())
		.append(getExpDate())
		.append(getCountryOfOrigin())
		.append(getInvAttr1())
		.append(getInvAttr2())
		.append(getInvAttr3())
		.append(getInvAttr4())
		.append(getInvAttr5())
		.append(getUuid())
		.append(getOuId())
		.append(getLastModifyTime())
			.toHashCode();
	}
    @Override
	public boolean equals(Object obj) {
		if(obj instanceof WhSkuInventoryTobefilledCommand == false) return false;
		if(this == obj) return true;
		WhSkuInventoryTobefilledCommand other = (WhSkuInventoryTobefilledCommand)obj;
		return new EqualsBuilder()
		.append(getId(),other.getId())

		.append(getSkuId(),other.getSkuId())

		.append(getLocationId(),other.getLocationId())

		.append(getOuterContainerId(),other.getOuterContainerId())

		.append(getInsideContainerId(),other.getInsideContainerId())

		.append(getCustomerId(),other.getCustomerId())

		.append(getStoreId(),other.getStoreId())

		.append(getOccupationCode(),other.getOccupationCode())

		.append(getOccupationLineId(),other.getOccupationLineId())

		.append(getReplenishmentCode(),other.getReplenishmentCode())

		.append(getQty(),other.getQty())

		.append(getInvStatus(),other.getInvStatus())

		.append(getInvType(),other.getInvType())

		.append(getBatchNumber(),other.getBatchNumber())

		.append(getMfgDate(),other.getMfgDate())

		.append(getExpDate(),other.getExpDate())

		.append(getCountryOfOrigin(),other.getCountryOfOrigin())

		.append(getInvAttr1(),other.getInvAttr1())

		.append(getInvAttr2(),other.getInvAttr2())

		.append(getInvAttr3(),other.getInvAttr3())

		.append(getInvAttr4(),other.getInvAttr4())

		.append(getInvAttr5(),other.getInvAttr5())

		.append(getUuid(),other.getUuid())

		.append(getOuId(),other.getOuId())

		.append(getLastModifyTime(),other.getLastModifyTime())

			.isEquals();
	}

    public List<WhSkuInventorySnCommand> getWhSkuInventorySnCommandList() {
        return whSkuInventorySnCommandList;
    }

    public void setWhSkuInventorySnCommandList(List<WhSkuInventorySnCommand> whSkuInventorySnCommandList) {
        this.whSkuInventorySnCommandList = whSkuInventorySnCommandList;
    }
    
    
}

