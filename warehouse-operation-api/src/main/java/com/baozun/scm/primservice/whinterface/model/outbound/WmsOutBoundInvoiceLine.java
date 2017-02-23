package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

public class WmsOutBoundInvoiceLine implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5587655300912854181L;

    private String  linenum ;// 行号
    private Double  qty ;// 数量
    private Double  unitPrice   ;// 单价
    private Double  amt ;// 总金额
    private String  item    ;// 类别
    public String getLinenum() {
        return linenum;
    }
    public void setLinenum(String linenum) {
        this.linenum = linenum;
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
    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }

}
