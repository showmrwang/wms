package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WeightingCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 8042594422635950511L;

    /** 功能id*/
    private Long funcId;

    /**组织*/
    private Long ouId;

    /** 称重*/
    private Long actualWeight;

    // ====================================页面缓存====================================

    /** 出库单id*/
    private Long odoId;

    /** 出库单号*/
    private String odoCode;

    /**出库id*/
    private Long outboundBoxId;

    /**出库箱号*/
    private String outboundBoxCode;

    /** 计重*/
    private Long calcWeighting;

    /** 浮动百分比*/
    private Long difference;

    /** 可接受百分比*/
    private Integer floats;

    /** 波次号*/
    private String waveCode;

    /** 小批次号*/
    private String batch;

    /** 运输服务商名称*/
    private String transportName;

    /** 运单号*/
    private String waybillCode;

    /** 耗材条码*/
    private String skuCode;

    /** 客户名称*/
    private String customerName;

    /** 店铺名称*/
    private String storeName;

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(Long actualWeight) {
        this.actualWeight = actualWeight;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public String getOdoCode() {
        return odoCode;
    }

    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public Long getOutboundBoxId() {
        return outboundBoxId;
    }

    public void setOutboundBoxId(Long outboundBoxId) {
        this.outboundBoxId = outboundBoxId;
    }

    public String getOutboundBoxCode() {
        return outboundBoxCode;
    }

    public void setOutboundBoxCode(String outboundBoxCode) {
        this.outboundBoxCode = outboundBoxCode;
    }

    public Long getCalcWeighting() {
        return calcWeighting;
    }

    public void setCalcWeighting(Long calcWeighting) {
        this.calcWeighting = calcWeighting;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getDifference() {
        return difference;
    }

    public void setDifference(Long difference) {
        this.difference = difference;
    }

    public Integer getFloats() {
        return floats;
    }

    public void setFloats(Integer floats) {
        this.floats = floats;
    }

    
    
}
