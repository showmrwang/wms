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
 * 
 * @author larkark
 *
 */
public class WhOutboundboxLine extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 7963998914616774299L;
    
    //columns START
	/** 出库箱ID */
	private java.lang.Long whOutboundboxId;
	/** 商品编码 */
	private java.lang.String skuCode;
	/** 商品外部编码 */
	private java.lang.String skuExtCode;
	/** 商品条码 */
	private java.lang.String skuBarCode;
	/** 商品名称 */
	private java.lang.String skuName;
	/** 数量 */
	private Long qty;
	/** 客户CODE */
	private java.lang.String customerCode;
	/** 客户名称 */
	private java.lang.String customerName;
	/** 店铺CODE */
	private java.lang.String storeCode;
	/** 店铺名称 */
	private java.lang.String storeName;
	/** 库存状态 */
	private java.lang.String invStatus;
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
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 出库单ID */
	private java.lang.Long odoId;
	/** 出库单明细ID */
	private java.lang.Long odoLineId;
	//columns END
	
    public java.lang.Long getWhOutboundboxId() {
        return whOutboundboxId;
    }
    public void setWhOutboundboxId(java.lang.Long whOutboundboxId) {
        this.whOutboundboxId = whOutboundboxId;
    }
    public java.lang.String getSkuCode() {
        return skuCode;
    }
    public void setSkuCode(java.lang.String skuCode) {
        this.skuCode = skuCode;
    }
    public java.lang.String getSkuExtCode() {
        return skuExtCode;
    }
    public void setSkuExtCode(java.lang.String skuExtCode) {
        this.skuExtCode = skuExtCode;
    }
    public java.lang.String getSkuBarCode() {
        return skuBarCode;
    }
    public void setSkuBarCode(java.lang.String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }
    public java.lang.String getSkuName() {
        return skuName;
    }
    public void setSkuName(java.lang.String skuName) {
        this.skuName = skuName;
    }
    public Long getQty() {
        return qty;
    }
    public void setQty(Long qty) {
        this.qty = qty;
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
    public java.lang.String getInvStatus() {
        return invStatus;
    }
    public void setInvStatus(java.lang.String invStatus) {
        this.invStatus = invStatus;
    }
    public java.lang.String getInvType() {
        return invType;
    }
    public void setInvType(java.lang.String invType) {
        this.invType = invType;
    }
    public java.lang.String getBatchNumber() {
        return batchNumber;
    }
    public void setBatchNumber(java.lang.String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public java.util.Date getMfgDate() {
        return mfgDate;
    }
    public void setMfgDate(java.util.Date mfgDate) {
        this.mfgDate = mfgDate;
    }
    public java.util.Date getExpDate() {
        return expDate;
    }
    public void setExpDate(java.util.Date expDate) {
        this.expDate = expDate;
    }
    public java.lang.String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    public void setCountryOfOrigin(java.lang.String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }
    public java.lang.String getInvAttr1() {
        return invAttr1;
    }
    public void setInvAttr1(java.lang.String invAttr1) {
        this.invAttr1 = invAttr1;
    }
    public java.lang.String getInvAttr2() {
        return invAttr2;
    }
    public void setInvAttr2(java.lang.String invAttr2) {
        this.invAttr2 = invAttr2;
    }
    public java.lang.String getInvAttr3() {
        return invAttr3;
    }
    public void setInvAttr3(java.lang.String invAttr3) {
        this.invAttr3 = invAttr3;
    }
    public java.lang.String getInvAttr4() {
        return invAttr4;
    }
    public void setInvAttr4(java.lang.String invAttr4) {
        this.invAttr4 = invAttr4;
    }
    public java.lang.String getInvAttr5() {
        return invAttr5;
    }
    public void setInvAttr5(java.lang.String invAttr5) {
        this.invAttr5 = invAttr5;
    }
    public java.lang.String getUuid() {
        return uuid;
    }
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.lang.Long getOdoId() {
        return odoId;
    }
    public void setOdoId(java.lang.Long odoId) {
        this.odoId = odoId;
    }
    public java.lang.Long getOdoLineId() {
        return odoLineId;
    }
    public void setOdoLineId(java.lang.Long odoLineId) {
        this.odoLineId = odoLineId;
    }

}

