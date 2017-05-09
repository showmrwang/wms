package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class CheckingDisplayCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 2418200424112523037L;

    // 复核头信息
    /** 出库单号 */
    private String odoCode;
    /** 外部对接编码 */
    private String extCode;
    /** 波次号 */
    private String waveCode;
    /** 运输服务商 */
    private String transportName;
    /** 产品类型 */
    private String productName;
    /** 时效类型名称 */
    private String timeEffectName;
    /** 客户名称 */
    private String customerName;
    /** 店铺名称 */
    private String storeName;

    // 复核库位信息
    /** 复核模式 */
    private String checkingMode;
    /** 主副品小批次 */
    private String batch;
    /** 波次号(已有) */
    /** 小批次下总箱数 */
    private Long outboundboxCount;
    /** 待复核总箱数 */
    private Long toBeCheckedOutboundboxCount;
    /** 小批次下总单数 */
    private Long odoCount;
    /** 待复核总单数 */
    private Long toBeCheckedOdoCount;
    /** 主品条码 */
    private String skuCode;
    /** 主品名称 */
    private String skuName;

    /** 客户(已有) */
    /** 店铺(已有) */
    public String getOdoCode() {
        return odoCode;
    }

    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTimeEffectName() {
        return timeEffectName;
    }

    public void setTimeEffectName(String timeEffectName) {
        this.timeEffectName = timeEffectName;
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

    public String getCheckingMode() {
        return checkingMode;
    }

    public void setCheckingMode(String checkingMode) {
        this.checkingMode = checkingMode;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Long getOutboundboxCount() {
        return outboundboxCount;
    }

    public void setOutboundboxCount(Long outboundboxCount) {
        this.outboundboxCount = outboundboxCount;
    }

    public Long getToBeCheckedOutboundboxCount() {
        return toBeCheckedOutboundboxCount;
    }

    public void setToBeCheckedOutboundboxCount(Long toBeCheckedOutboundboxCount) {
        this.toBeCheckedOutboundboxCount = toBeCheckedOutboundboxCount;
    }

    public Long getOdoCount() {
        return odoCount;
    }

    public void setOdoCount(Long odoCount) {
        this.odoCount = odoCount;
    }

    public Long getToBeCheckedOdoCount() {
        return toBeCheckedOdoCount;
    }

    public void setToBeCheckedOdoCount(Long toBeCheckedOdoCount) {
        this.toBeCheckedOdoCount = toBeCheckedOdoCount;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

}
