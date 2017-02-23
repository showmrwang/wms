package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WmsOutBound implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3727784379028665064L;

    private String extOdoCode;// 上位系统出库单号
    private String ecOrderCode;// 电商平台订单号
    private String customerCode;// 客户编码
    private String storeCode;// 店铺编码
    private String odoType;// 出库单类型
    private Boolean isWholeOrderOutbound;// 是否整单出库 默认是
    private String partOutboundStrategy;// 部分出库策略
    private Boolean crossDockingSymbol;// 越库标志 默认否
    private String ecOrderType;// 订单的平台类型
    private Date orderTime;// 下单时间
    private Double qty;// 商品数量
    private Double amt;// 订单总金额
    private String epistaticSystemsOrderType;// 上位系统单据类型
    private String outboundCartonType;// 出库箱类型 WMS4.0维护数据，上位系统可以运用对应数据
    private Boolean includeHazardousCargo;// 是否含危险品 默认否
    private Boolean includeFragileCargo;// 是否含易碎品 默认否
    private List<WmsOutBoundLine> wmsOutBoundLines;// 出库单明细信息
    private WmsOutBoundAddress wmsOutBoundAddress;// 出库单详细配送地址
    private String transportServiceProvider;// 运输服务商
    private Boolean isCod;// 是否COD 默认否
    private Double codAmt;// COD金额
    private Double insuranceCoverage;// 保价金额
    private List<WmsOutBoundInvoice> wmsOutBoundInvoice;// 出库单发票信息
    private String dataSource;// 数据来源 区分上位系统
    private String whCode;// 仓库编码
    private List<WmsOutBoundVas> wmsOutBoundVas;// 出库单增值服务

    public String getExtOdoCode() {
        return extOdoCode;
    }

    public void setExtOdoCode(String extOdoCode) {
        this.extOdoCode = extOdoCode;
    }

    public String getEcOrderCode() {
        return ecOrderCode;
    }

    public void setEcOrderCode(String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getOdoType() {
        return odoType;
    }

    public void setOdoType(String odoType) {
        this.odoType = odoType;
    }

    public Boolean getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }

    public void setIsWholeOrderOutbound(Boolean isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
    }

    public String getPartOutboundStrategy() {
        return partOutboundStrategy;
    }

    public void setPartOutboundStrategy(String partOutboundStrategy) {
        this.partOutboundStrategy = partOutboundStrategy;
    }

    public Boolean getCrossDockingSymbol() {
        return crossDockingSymbol;
    }

    public void setCrossDockingSymbol(Boolean crossDockingSymbol) {
        this.crossDockingSymbol = crossDockingSymbol;
    }

    public String getEcOrderType() {
        return ecOrderType;
    }

    public void setEcOrderType(String ecOrderType) {
        this.ecOrderType = ecOrderType;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }

    public void setEpistaticSystemsOrderType(String epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }

    public String getOutboundCartonType() {
        return outboundCartonType;
    }

    public void setOutboundCartonType(String outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }

    public Boolean getIncludeHazardousCargo() {
        return includeHazardousCargo;
    }

    public void setIncludeHazardousCargo(Boolean includeHazardousCargo) {
        this.includeHazardousCargo = includeHazardousCargo;
    }

    public Boolean getIncludeFragileCargo() {
        return includeFragileCargo;
    }

    public void setIncludeFragileCargo(Boolean includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
    }

    public List<WmsOutBoundLine> getWmsOutBoundLines() {
        return wmsOutBoundLines;
    }

    public void setWmsOutBoundLines(List<WmsOutBoundLine> wmsOutBoundLines) {
        this.wmsOutBoundLines = wmsOutBoundLines;
    }

    public WmsOutBoundAddress getWmsOutBoundAddress() {
        return wmsOutBoundAddress;
    }

    public void setWmsOutBoundAddress(WmsOutBoundAddress wmsOutBoundAddress) {
        this.wmsOutBoundAddress = wmsOutBoundAddress;
    }

    public String getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public Boolean getIsCod() {
        return isCod;
    }

    public void setIsCod(Boolean isCod) {
        this.isCod = isCod;
    }

    public Double getCodAmt() {
        return codAmt;
    }

    public void setCodAmt(Double codAmt) {
        this.codAmt = codAmt;
    }

    public Double getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public void setInsuranceCoverage(Double insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public List<WmsOutBoundInvoice> getWmsOutBoundInvoice() {
        return wmsOutBoundInvoice;
    }

    public void setWmsOutBoundInvoice(List<WmsOutBoundInvoice> wmsOutBoundInvoice) {
        this.wmsOutBoundInvoice = wmsOutBoundInvoice;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public List<WmsOutBoundVas> getWmsOutBoundVas() {
        return wmsOutBoundVas;
    }

    public void setWmsOutBoundVas(List<WmsOutBoundVas> wmsOutBoundVas) {
        this.wmsOutBoundVas = wmsOutBoundVas;
    }



}
