/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.model.confirm.outbound;

import java.util.List;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库单反馈发票信息
 *
 */
public class WhOutboundInvoiceConfirm extends BaseModel {


    private static final long serialVersionUID = -4482430743611635667L;

    /** 出库单反馈ID */
    private Long outboundConfirmId;
    /** 发票抬头 */
    private String invoiceCode;
    /** 发票日期 */
    private String invoiceDate;
    /** 发票号 */
    private String invoiceNo;
    /** 付款单位（发票抬头） */
    private String payer;
    /** 商品 */
    private String item;
    /** 数量 */
    private Integer qty;
    /** 单价 */
    private Double unitPrice;
    /** 总金额 */
    private Double amt;
    /** 发票备注 */
    private String memo;
    /** 收款人 */
    private String payee;
    /** 开票人 */
    private String drawer;
    /** 公司 */
    private String company;
    /** 仓库组织ID */
    private Long ouId;

    /** 出库单发票反馈明细 */
    private List<WhOutboundInvoiceLineConfirm> whOutBoundConfirmInvoiceLines;

    public Long getOutboundConfirmId() {
        return outboundConfirmId;
    }

    public void setOutboundConfirmId(Long outboundConfirmId) {
        this.outboundConfirmId = outboundConfirmId;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getDrawer() {
        return drawer;
    }

    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public List<WhOutboundInvoiceLineConfirm> getWhOutBoundConfirmInvoiceLines() {
        return whOutBoundConfirmInvoiceLines;
    }

    public void setWhOutBoundConfirmInvoiceLines(List<WhOutboundInvoiceLineConfirm> whOutBoundConfirmInvoiceLines) {
        this.whOutBoundConfirmInvoiceLines = whOutBoundConfirmInvoiceLines;
    }



}
