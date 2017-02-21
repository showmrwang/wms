package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

public class WmsOutBoundVas implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1772452067537406948L;

    private  String  vasType ;// 增值服务类型 快递/仓库
    private String  whVasType   ;// 仓库增值服务类型
    private String  expressVasType  ;// 快递增值服务类型
    private String  printTemplet    ;// 打印模板
    private String  skuBarCode  ;// 赠品编码/礼品包装
    private String  content ;// 内容/备注
    private String  cartonNo    ;// 箱号
    private String  qty ;// 数量 赠送数量
    private String  amt ;// 金额
    public String getVasType() {
        return vasType;
    }
    public void setVasType(String vasType) {
        this.vasType = vasType;
    }
    public String getWhVasType() {
        return whVasType;
    }
    public void setWhVasType(String whVasType) {
        this.whVasType = whVasType;
    }
    public String getExpressVasType() {
        return expressVasType;
    }
    public void setExpressVasType(String expressVasType) {
        this.expressVasType = expressVasType;
    }
    public String getPrintTemplet() {
        return printTemplet;
    }
    public void setPrintTemplet(String printTemplet) {
        this.printTemplet = printTemplet;
    }
    public String getSkuBarCode() {
        return skuBarCode;
    }
    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCartonNo() {
        return cartonNo;
    }
    public void setCartonNo(String cartonNo) {
        this.cartonNo = cartonNo;
    }
    public String getQty() {
        return qty;
    }
    public void setQty(String qty) {
        this.qty = qty;
    }
    public String getAmt() {
        return amt;
    }
    public void setAmt(String amt) {
        this.amt = amt;
    }


}
