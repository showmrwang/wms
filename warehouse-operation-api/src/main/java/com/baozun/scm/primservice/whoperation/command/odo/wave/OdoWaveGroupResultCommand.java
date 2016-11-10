package com.baozun.scm.primservice.whoperation.command.odo.wave;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoWaveGroupResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 2904637294225979289L;

    /** 组别名称 */
    private String groupName;
    /** 客户 */
    private Long customerId;
    private String customerName;
    /** 店铺 */
    private Long storeId;
    private String storeName;
    /** 出库单状态 */
    private String odoStatus;
    private String odoStatusName;
    /** 出库单类型 */
    private String odoType;
    private String odoTypeName;
    /** 配货模式 */
    private String distributeMode;
    private String distributeModeName;
    /** 运输服务商 */
    private String transportServiceProvider;
    private String transportServiceProviderName;
    /** 上位单据类型 */
    private String epostaticSystemsOrderType;
    private String epostaticSystemsOrderTypeName;
    /** 订单数 */
    private Long odoCount;
    /** 明细行总数数 */
    private Long lineCount;
    /** 总件数 */
    private Double qty;
    /** 商品种类数 */
    private Long skuTypeCount;
    /** 店铺数量 */
    private Long storeCount;
    /** 出库单类型数 */
    private Long odoTypeCount;
    /** 总金额 */
    private Double amt;
    /** 业务模式数 */
    private Long distributionModeCount;
    public String getOdoType() {
        return odoType;
    }

    public void setOdoType(String odoType) {
        this.odoType = odoType;
    }

    public String getOdoTypeName() {
        return odoTypeName;
    }

    public void setOdoTypeName(String odoTypeName) {
        this.odoTypeName = odoTypeName;
    }

    public String getDistributeMode() {
        return distributeMode;
    }

    public void setDistributeMode(String distributeMode) {
        this.distributeMode = distributeMode;
    }

    public String getDistributeModeName() {
        return distributeModeName;
    }

    public void setDistributeModeName(String distributeModeName) {
        this.distributeModeName = distributeModeName;
    }

    public String getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public String getTransportServiceProviderName() {
        return transportServiceProviderName;
    }

    public void setTransportServiceProviderName(String transportServiceProviderName) {
        this.transportServiceProviderName = transportServiceProviderName;
    }

    public String getEpostaticSystemsOrderType() {
        return epostaticSystemsOrderType;
    }

    public void setEpostaticSystemsOrderType(String epostaticSystemsOrderType) {
        this.epostaticSystemsOrderType = epostaticSystemsOrderType;
    }

    public String getEpostaticSystemsOrderTypeName() {
        return epostaticSystemsOrderTypeName;
    }

    public void setEpostaticSystemsOrderTypeName(String epostaticSystemsOrderTypeName) {
        this.epostaticSystemsOrderTypeName = epostaticSystemsOrderTypeName;
    }

    public Long getLineCount() {
        return lineCount;
    }

    public void setLineCount(Long lineCount) {
        this.lineCount = lineCount;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Long getSkuTypeCount() {
        return skuTypeCount;
    }

    public void setSkuTypeCount(Long skuTypeCount) {
        this.skuTypeCount = skuTypeCount;
    }

    public Long getStoreCount() {
        return storeCount;
    }

    public void setStoreCount(Long storeCount) {
        this.storeCount = storeCount;
    }

    public Long getOdoTypeCount() {
        return odoTypeCount;
    }

    public void setOdoTypeCount(Long odoTypeCount) {
        this.odoTypeCount = odoTypeCount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }

    public Long getOdoCount() {
        return odoCount;
    }

    public void setOdoCount(Long odoCount) {
        this.odoCount = odoCount;
    }


    public String getOdoStatusName() {
        return odoStatusName;
    }

    public void setOdoStatusName(String odoStatusName) {
        this.odoStatusName = odoStatusName;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public Long getDistributionModeCount() {
        return distributionModeCount;
    }

    public void setDistributionModeCount(Long distributionModeCount) {
        this.distributionModeCount = distributionModeCount;
    }



}
