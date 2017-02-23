package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;
import java.util.List;

public class WmsOutBoundInvoice implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 870855129350619512L;

    private String  invoiceCode ;// 发票抬头
    private String  invoiceDate ;// 发票日期
    private String  invoiceNo   ;// 发票号
    private String  payer   ;// 付款单位（发票抬头）
    private String  item    ;// 商品
    private Double  qty ;// 数量
    private Double  unitPrice   ;// 单价
    private Double  amt ;// 总金额
    private String  memo    ;// 发票备注
    private String  payee   ;// 收款人
    private String  drawer  ;// 开票人
    private String  company ;// 公司
    private List<WmsOutBoundInvoiceLine>   wmsOutBoundInvoiceLines ;// 发票明细信息
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
    public Double getQty() {
        return qty;
    }
    public void setQty(Double qty) {
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
    public List<WmsOutBoundInvoiceLine> getWmsOutBoundInvoiceLines() {
        return wmsOutBoundInvoiceLines;
    }
    public void setWmsOutBoundInvoiceLines(List<WmsOutBoundInvoiceLine> wmsOutBoundInvoiceLines) {
        this.wmsOutBoundInvoiceLines = wmsOutBoundInvoiceLines;
    }
    

}
