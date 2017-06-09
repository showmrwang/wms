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
package com.baozun.scm.primservice.whoperation.model.warehouse.inventory;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author larkark
 *
 */
public class WhSkuInventoryAllocated extends BaseModel {
	
	private static final long serialVersionUID = -9036158098288241257L;
	
	//columns START
	/** 商品ID */
	private Long skuId;
	/** 库位ID 库位号 */
	private Long locationId;
	/** 外部容器ID 托盘 货箱 */
	private Long outerContainerId;
	/** 内部容器ID 托盘 货箱 */
	private Long insideContainerId;
	/** 客户ID */
	private Long customerId;
	/** 店铺ID */
	private Long storeId;
	/** 占用单据号 */
	private String occupationCode;
	/** 占用单据明细行ID */
	private Long occupationLineId;
	/** 补货单号 */
	private String replenishmentCode;
	/** 调整数量 */
	private Double qty;
	/** 库存状态 */
	private Long invStatus;
	/** 库存类型 */
	private String invType;
	/** 批次号 */
	private String batchNumber;
	/** 生产日期 */
	private Date mfgDate;
	/** 失效日期 */
	private Date expDate;
	/** 原产地 */
	private String countryOfOrigin;
	/** 库存属性1 */
	private String invAttr1;
	/** 库存属性2 */
	private String invAttr2;
	/** 库存属性3 */
	private String invAttr3;
	/** 库存属性4 */
	private String invAttr4;
	/** 库存属性5 */
	private String invAttr5;
	/** 内部对接码 */
	private String uuid;
	/** 对应仓库ID */
	private Long ouId;
	/** 最后操作时间 */
	private Date lastModifyTime;
	/** 占用单据来源 */
    private String occupationCodeSource;
	/** 补货阶段使用的规则id */
	private Long replenishmentRuleId;
	//columns END

	public WhSkuInventoryAllocated(){
	}

	public WhSkuInventoryAllocated(Long id) {
		this.id = id;
	}

	public void setSkuId(Long value) {
		this.skuId = value;
	}
	
	public Long getSkuId() {
		return this.skuId;
	}
	public void setLocationId(Long value) {
		this.locationId = value;
	}
	
	public Long getLocationId() {
		return this.locationId;
	}
	public void setOuterContainerId(Long value) {
		this.outerContainerId = value;
	}
	
	public Long getOuterContainerId() {
		return this.outerContainerId;
	}
	public void setInsideContainerId(Long value) {
		this.insideContainerId = value;
	}
	
	public Long getInsideContainerId() {
		return this.insideContainerId;
	}
	public void setCustomerId(Long value) {
		this.customerId = value;
	}
	
	public Long getCustomerId() {
		return this.customerId;
	}
	public void setStoreId(Long value) {
		this.storeId = value;
	}
	
	public Long getStoreId() {
		return this.storeId;
	}
	public void setOccupationCode(String value) {
		this.occupationCode = value;
	}
	
	public String getOccupationCode() {
		return this.occupationCode;
	}
	public void setOccupationLineId(Long value) {
		this.occupationLineId = value;
	}
	
	public Long getOccupationLineId() {
		return this.occupationLineId;
	}
	public void setReplenishmentCode(String value) {
		this.replenishmentCode = value;
	}
	
	public String getReplenishmentCode() {
		return this.replenishmentCode;
	}
	public void setQty(Double value) {
		this.qty = value;
	}
	
	public Double getQty() {
		return this.qty;
	}
	public void setInvStatus(Long value) {
		this.invStatus = value;
	}
	
	public Long getInvStatus() {
		return this.invStatus;
	}
	public void setInvType(String value) {
		this.invType = value;
	}
	
	public String getInvType() {
		return this.invType;
	}
	public void setBatchNumber(String value) {
		this.batchNumber = value;
	}
	
	public String getBatchNumber() {
		return this.batchNumber;
	}

	public void setMfgDate(Date value) {
		this.mfgDate = value;
	}
	
	public Date getMfgDate() {
		return this.mfgDate;
	}
	
	public void setExpDate(Date value) {
		this.expDate = value;
	}
	
	public Date getExpDate() {
		return this.expDate;
	}
	public void setCountryOfOrigin(String value) {
		this.countryOfOrigin = value;
	}
	
	public String getCountryOfOrigin() {
		return this.countryOfOrigin;
	}
	public void setInvAttr1(String value) {
		this.invAttr1 = value;
	}
	
	public String getInvAttr1() {
		return this.invAttr1;
	}
	public void setInvAttr2(String value) {
		this.invAttr2 = value;
	}
	
	public String getInvAttr2() {
		return this.invAttr2;
	}
	public void setInvAttr3(String value) {
		this.invAttr3 = value;
	}
	
	public String getInvAttr3() {
		return this.invAttr3;
	}
	public void setInvAttr4(String value) {
		this.invAttr4 = value;
	}
	
	public String getInvAttr4() {
		return this.invAttr4;
	}
	public void setInvAttr5(String value) {
		this.invAttr5 = value;
	}
	
	public String getInvAttr5() {
		return this.invAttr5;
	}
	public void setUuid(String value) {
		this.uuid = value;
	}
	
	public String getUuid() {
		return this.uuid;
	}
	public void setOuId(Long value) {
		this.ouId = value;
	}
	
	public Long getOuId() {
		return this.ouId;
	}

	public void setLastModifyTime(Date value) {
		this.lastModifyTime = value;
	}
	
	public Date getLastModifyTime() {
		return this.lastModifyTime;
	}

	public Long getReplenishmentRuleId() {
		return replenishmentRuleId;
	}

	public void setReplenishmentRuleId(Long replenishmentRuleId) {
		this.replenishmentRuleId = replenishmentRuleId;
	}

    public String getOccupationCodeSource() {
        return occupationCodeSource;
    }

    public void setOccupationCodeSource(String occupationCodeSource) {
        this.occupationCodeSource = occupationCodeSource;
    }
	
}

