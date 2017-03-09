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
public class WhOutboundInvoice extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 7952086962134349038L;
    
    //columns START
	/** 出库单ID */
	private java.lang.Long outboundId;
	/** 发票抬头 */
	private java.lang.String invoiceCode;
	/** 发票日期 */
	private java.lang.String invoiceDate;
	/** 发票号 */
	private java.lang.String invoiceNo;
	/** 付款单位（发票抬头） */
	private java.lang.String payer;
	/** 商品 */
	private java.lang.String item;
	/** 数量 */
	private java.lang.Integer qty;
	/** 单价 */
	private Double unitPrice;
	/** 总金额 */
	private Double amt;
	/** 发票备注 */
	private java.lang.String memo;
	/** 收款人 */
	private java.lang.String payee;
	/** 开票人 */
	private java.lang.String drawer;
	/** 公司 */
	private java.lang.String company;
	//columns END
	
    public java.lang.Long getOutboundId() {
        return outboundId;
    }
    public void setOutboundId(java.lang.Long outboundId) {
        this.outboundId = outboundId;
    }
    public java.lang.String getInvoiceCode() {
        return invoiceCode;
    }
    public void setInvoiceCode(java.lang.String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }
    public java.lang.String getInvoiceDate() {
        return invoiceDate;
    }
    public void setInvoiceDate(java.lang.String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    public java.lang.String getInvoiceNo() {
        return invoiceNo;
    }
    public void setInvoiceNo(java.lang.String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
    public java.lang.String getPayer() {
        return payer;
    }
    public void setPayer(java.lang.String payer) {
        this.payer = payer;
    }
    public java.lang.String getItem() {
        return item;
    }
    public void setItem(java.lang.String item) {
        this.item = item;
    }
    public java.lang.Integer getQty() {
        return qty;
    }
    public void setQty(java.lang.Integer qty) {
        this.qty = qty;
    }
    public Double getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public Double getAmt() {
        return amt;
    }
    public void setAmt(Double amt) {
        this.amt = amt;
    }
    public java.lang.String getMemo() {
        return memo;
    }
    public void setMemo(java.lang.String memo) {
        this.memo = memo;
    }
    public java.lang.String getPayee() {
        return payee;
    }
    public void setPayee(java.lang.String payee) {
        this.payee = payee;
    }
    public java.lang.String getDrawer() {
        return drawer;
    }
    public void setDrawer(java.lang.String drawer) {
        this.drawer = drawer;
    }
    public java.lang.String getCompany() {
        return company;
    }
    public void setCompany(java.lang.String company) {
        this.company = company;
    }

}

