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
package com.baozun.scm.primservice.whoperation.model.whinterface.outbound;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOutboundLine extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 6907628639448298148L;
    
    //columns START
	/** 出库单ID */
	private java.lang.Long outboundId;
	/** 店铺CODE */
	private java.lang.String storeCode;
	/** 外部单据行号 */
	private java.lang.Integer extLinenum;
	/** 商品唯一编码 */
	private java.lang.String upc;
	/** 上位系统商品名称 */
	private java.lang.String extSkuName;
	/** 商品数量 */
	private Long qty;
	/** 行单价 */
	private Long linePrice;
	/** 行总价 */
	private Long lineAmt;
	/** 整行出库标志 默认是 */
	private java.lang.Boolean fullLineOutbound;
	/** 部分出库策略 */
	private java.lang.String partOutboundStrategy;
	/** 出库箱类型 */
	private java.lang.String outboundCartonType;
	/** 混放属性 */
	private java.lang.String mixingAttr;
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
	//columns END
	
    public java.lang.Long getOutboundId() {
        return outboundId;
    }
    public void setOutboundId(java.lang.Long outboundId) {
        this.outboundId = outboundId;
    }
    public java.lang.String getStoreCode() {
        return storeCode;
    }
    public void setStoreCode(java.lang.String storeCode) {
        this.storeCode = storeCode;
    }
    public java.lang.Integer getExtLinenum() {
        return extLinenum;
    }
    public void setExtLinenum(java.lang.Integer extLinenum) {
        this.extLinenum = extLinenum;
    }
    public java.lang.String getUpc() {
        return upc;
    }
    public void setUpc(java.lang.String upc) {
        this.upc = upc;
    }
    public java.lang.String getExtSkuName() {
        return extSkuName;
    }
    public void setExtSkuName(java.lang.String extSkuName) {
        this.extSkuName = extSkuName;
    }
    public Long getQty() {
        return qty;
    }
    public void setQty(Long qty) {
        this.qty = qty;
    }
    public Long getLinePrice() {
        return linePrice;
    }
    public void setLinePrice(Long linePrice) {
        this.linePrice = linePrice;
    }
    public Long getLineAmt() {
        return lineAmt;
    }
    public void setLineAmt(Long lineAmt) {
        this.lineAmt = lineAmt;
    }
    public java.lang.Boolean getFullLineOutbound() {
        return fullLineOutbound;
    }
    public void setFullLineOutbound(java.lang.Boolean fullLineOutbound) {
        this.fullLineOutbound = fullLineOutbound;
    }
    public java.lang.String getPartOutboundStrategy() {
        return partOutboundStrategy;
    }
    public void setPartOutboundStrategy(java.lang.String partOutboundStrategy) {
        this.partOutboundStrategy = partOutboundStrategy;
    }
    public java.lang.String getOutboundCartonType() {
        return outboundCartonType;
    }
    public void setOutboundCartonType(java.lang.String outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }
    public java.lang.String getMixingAttr() {
        return mixingAttr;
    }
    public void setMixingAttr(java.lang.String mixingAttr) {
        this.mixingAttr = mixingAttr;
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

}

