package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WmsOutBoundLine implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 119226684259433695L;

    private String  storeCode   ;// 店铺CODE
    private Integer extLinenum  ;// 外部单据行号
    private String  upc ;// 商品唯一编码
    private String  extSkuName  ;// 上位系统商品名称
    private Double  qty ;// 商品数量
    private Double  linePrice   ;// 行单价
    private Double  lineAmt ;// 行总价
    private Boolean fullLineOutbound    ;// 整行出库标志 默认是
    private String  partOutboundStrategy    ;// 部分出库策略
    private String  invStatus   ;// 库存状态
    private String  invType ;// 库存类型,WMS4.0维护数据，上位系统可以运用对应数据
    private String  batchNumber ;// 批次号
    private Date    mfgDate ;// 生产日期
    private Date    expDate ;// 失效日期
    private String  countryOfOrigin ;// 原产地
    private String  invAttr1    ;// 库存属性1 WMS4.0维护数据，上位系统可以运用对应数据
    private String  invAttr2    ;// 库存属性2 WMS4.0维护数据，上位系统可以运用对应数据
    private String  invAttr3    ;// 库存属性3 WMS4.0维护数据，上位系统可以运用对应数据
    private String  invAttr4    ;// 库存属性4 WMS4.0维护数据，上位系统可以运用对应数据
    private String  invAttr5    ;// 库存属性5 WMS4.0维护数据，上位系统可以运用对应数据
    private String  outboundCartonType  ;// 出库箱类型
    private List<WmsOutBoundSnLine>    wmsOutBoundSnLines  ;// SN/残次信息
    private List<WmsOutBoundVasLine>    wmsOutBoundVasLines ;// 出库单明细增值服务
    private String  mixingAttr  ;// 混放属性
    public String getStoreCode() {
        return storeCode;
    }
    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }
    public Integer getExtLinenum() {
        return extLinenum;
    }
    public void setExtLinenum(Integer extLinenum) {
        this.extLinenum = extLinenum;
    }
    public String getUpc() {
        return upc;
    }
    public void setUpc(String upc) {
        this.upc = upc;
    }
    public String getExtSkuName() {
        return extSkuName;
    }
    public void setExtSkuName(String extSkuName) {
        this.extSkuName = extSkuName;
    }
    public Double getQty() {
        return qty;
    }
    public void setQty(Double qty) {
        this.qty = qty;
    }
    public Double getLinePrice() {
        return linePrice;
    }
    public void setLinePrice(Double linePrice) {
        this.linePrice = linePrice;
    }
    public Double getLineAmt() {
        return lineAmt;
    }
    public void setLineAmt(Double lineAmt) {
        this.lineAmt = lineAmt;
    }
    public Boolean getFullLineOutbound() {
        return fullLineOutbound;
    }
    public void setFullLineOutbound(Boolean fullLineOutbound) {
        this.fullLineOutbound = fullLineOutbound;
    }
    public String getPartOutboundStrategy() {
        return partOutboundStrategy;
    }
    public void setPartOutboundStrategy(String partOutboundStrategy) {
        this.partOutboundStrategy = partOutboundStrategy;
    }
    public String getInvStatus() {
        return invStatus;
    }
    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }
    public String getInvType() {
        return invType;
    }
    public void setInvType(String invType) {
        this.invType = invType;
    }
    public String getBatchNumber() {
        return batchNumber;
    }
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public Date getMfgDate() {
        return mfgDate;
    }
    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }
    public Date getExpDate() {
        return expDate;
    }
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }
    public String getInvAttr1() {
        return invAttr1;
    }
    public void setInvAttr1(String invAttr1) {
        this.invAttr1 = invAttr1;
    }
    public String getInvAttr2() {
        return invAttr2;
    }
    public void setInvAttr2(String invAttr2) {
        this.invAttr2 = invAttr2;
    }
    public String getInvAttr3() {
        return invAttr3;
    }
    public void setInvAttr3(String invAttr3) {
        this.invAttr3 = invAttr3;
    }
    public String getInvAttr4() {
        return invAttr4;
    }
    public void setInvAttr4(String invAttr4) {
        this.invAttr4 = invAttr4;
    }
    public String getInvAttr5() {
        return invAttr5;
    }
    public void setInvAttr5(String invAttr5) {
        this.invAttr5 = invAttr5;
    }
    public String getOutboundCartonType() {
        return outboundCartonType;
    }
    public void setOutboundCartonType(String outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }
    public List<WmsOutBoundSnLine> getWmsOutBoundSnLines() {
        return wmsOutBoundSnLines;
    }
    public void setWmsOutBoundSnLines(List<WmsOutBoundSnLine> wmsOutBoundSnLines) {
        this.wmsOutBoundSnLines = wmsOutBoundSnLines;
    }
    public List<WmsOutBoundVasLine> getWmsOutBoundVasLines() {
        return wmsOutBoundVasLines;
    }
    public void setWmsOutBoundVasLines(List<WmsOutBoundVasLine> wmsOutBoundVasLines) {
        this.wmsOutBoundVasLines = wmsOutBoundVasLines;
    }
    public String getMixingAttr() {
        return mixingAttr;
    }
    public void setMixingAttr(String mixingAttr) {
        this.mixingAttr = mixingAttr;
    }
    

}
