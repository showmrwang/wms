package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 5830200103545531555L;
    private Long id;
    private String odoCode;
    private String extCode;
    private String priorityLevel;
    private String isWholeOrderOutbound;
    private String isWholeOrderOutboundName;
    private String crossDockingSysmbol;
    private String crossDockingSysmbolName;
    private String ecOrderCode;
    private String customerId;
    private String customerName;
    private String storeId;
    private String storeName;
    private String orderTime;
    private String skuNumberOfPackages;
    private String amt;
    private String transportServiceProvider;
    private String transportServiceProviderName;
    private String modeOfTransport;
    private String modeOfTransportName;
    private String epistaticSystemsOrderType;
    private String epistaticSystemsOrderTypeName;
    private String odoType;
    private String odoTypeName;
    private String distributeMode;
    private String distributeModeName;
    private String odoStatus;
    private String odoStatusName;
    private String transportServiceProviderType;
    private String transportServiceProviderTypeName;
    private String outboundTargetType;
    private String outboundTargetTypeName;
    private String outboundTarget;
    private String distributionTargetName;
    private String distributionTargetMobilePhone;
    private String distributionTargetTelephone;
    private String distributionTargetCountry;
    private String distributionTargetProvince;
    private String distributionTargetCity;
    private String distributionTargetDistrict;
    private String distributionTargetVillagesTowns;
    private String distributionTargetAddress;
    private String distributionTargetEmail;
    private String distributionTargetZip;
    private String consigneeTargetName;
    private String consigneeTargetMobilePhone;
    private String consigneeTargetTelephone;
    private String consigneeTargetCountry;
    private String consigneeTargetProvince;
    private String consigneeTargetCity;
    private String consigneeTargetDistrict;
    private String consigneeTargetVillagesTowns;
    private String consigneeTargetAddress;
    private String consigneeTargetEmail;
    private String consigneeTargetZip;
    private String deliverGoodsTimeMode;
    private String deliverGoodsTimeModeName;
    private String deliverGoodsTime;
    private String planDeliverGoodsTime;
    private String actualDeliverGoodsTime;
    private String includeFragileCargo;
    private String includeFragileCargoName;
    private String includeHazardousCargo;
    private String includeHazardousCargoName;
    private String outboundCartonType;
    private String outboundCartonTypeName;
    private String createTime;
    private String createId;
    private String createdName;
    private String lastModifyTime;
    private String modifiedId;
    private String modifiedName;
    private String isLocked;
    private String orderType;
    private String orderTypeName;
    private String qty;
    private String isAssignSuccess;
    private String assignFailReason;
    /** 原始出库单号 */
    private String originalOdoCode;
    /** 物流服务调用标志 0:失败 */
    private Integer logisticsFlag;

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

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

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }

    public void setIsWholeOrderOutbound(String isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
    }

    public String getIsWholeOrderOutboundName() {
        return isWholeOrderOutboundName;
    }

    public void setIsWholeOrderOutboundName(String isWholeOrderOutboundName) {
        this.isWholeOrderOutboundName = isWholeOrderOutboundName;
    }

    public String getCrossDockingSysmbol() {
        return crossDockingSysmbol;
    }

    public void setCrossDockingSysmbol(String crossDockingSysmbol) {
        this.crossDockingSysmbol = crossDockingSysmbol;
    }

    public String getCrossDockingSysmbolName() {
        return crossDockingSysmbolName;
    }

    public void setCrossDockingSysmbolName(String crossDockingSysmbolName) {
        this.crossDockingSysmbolName = crossDockingSysmbolName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getSkuNumberOfPackages() {
        return skuNumberOfPackages;
    }

    public void setSkuNumberOfPackages(String skuNumberOfPackages) {
        this.skuNumberOfPackages = skuNumberOfPackages;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
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

    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }

    public String getModeOfTransportName() {
        return modeOfTransportName;
    }

    public void setModeOfTransportName(String modeOfTransportName) {
        this.modeOfTransportName = modeOfTransportName;
    }

    public String getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }

    public void setEpistaticSystemsOrderType(String epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }

    public String getEpistaticSystemsOrderTypeName() {
        return epistaticSystemsOrderTypeName;
    }

    public void setEpistaticSystemsOrderTypeName(String epistaticSystemsOrderTypeName) {
        this.epistaticSystemsOrderTypeName = epistaticSystemsOrderTypeName;
    }

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

    public String getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }

    public String getOdoStatusName() {
        return odoStatusName;
    }

    public void setOdoStatusName(String odoStatusName) {
        this.odoStatusName = odoStatusName;
    }

    public String getTransportServiceProviderType() {
        return transportServiceProviderType;
    }

    public void setTransportServiceProviderType(String transportServiceProviderType) {
        this.transportServiceProviderType = transportServiceProviderType;
    }

    public String getTransportServiceProviderTypeName() {
        return transportServiceProviderTypeName;
    }

    public void setTransportServiceProviderTypeName(String transportServiceProviderTypeName) {
        this.transportServiceProviderTypeName = transportServiceProviderTypeName;
    }

    public String getOutboundTargetType() {
        return outboundTargetType;
    }

    public void setOutboundTargetType(String outboundTargetType) {
        this.outboundTargetType = outboundTargetType;
    }

    public String getOutboundTargetTypeName() {
        return outboundTargetTypeName;
    }

    public void setOutboundTargetTypeName(String outboundTargetTypeName) {
        this.outboundTargetTypeName = outboundTargetTypeName;
    }

    public String getOutboundTarget() {
        return outboundTarget;
    }

    public void setOutboundTarget(String outboundTarget) {
        this.outboundTarget = outboundTarget;
    }

    public String getDistributionTargetName() {
        return distributionTargetName;
    }

    public void setDistributionTargetName(String distributionTargetName) {
        this.distributionTargetName = distributionTargetName;
    }

    public String getDistributionTargetMobilePhone() {
        return distributionTargetMobilePhone;
    }

    public void setDistributionTargetMobilePhone(String distributionTargetMobilePhone) {
        this.distributionTargetMobilePhone = distributionTargetMobilePhone;
    }

    public String getDistributionTargetTelephone() {
        return distributionTargetTelephone;
    }

    public void setDistributionTargetTelephone(String distributionTargetTelephone) {
        this.distributionTargetTelephone = distributionTargetTelephone;
    }

    public String getDistributionTargetCountry() {
        return distributionTargetCountry;
    }

    public void setDistributionTargetCountry(String distributionTargetCountry) {
        this.distributionTargetCountry = distributionTargetCountry;
    }

    public String getDistributionTargetProvince() {
        return distributionTargetProvince;
    }

    public void setDistributionTargetProvince(String distributionTargetProvince) {
        this.distributionTargetProvince = distributionTargetProvince;
    }

    public String getDistributionTargetCity() {
        return distributionTargetCity;
    }

    public void setDistributionTargetCity(String distributionTargetCity) {
        this.distributionTargetCity = distributionTargetCity;
    }

    public String getDistributionTargetDistrict() {
        return distributionTargetDistrict;
    }

    public void setDistributionTargetDistrict(String distributionTargetDistrict) {
        this.distributionTargetDistrict = distributionTargetDistrict;
    }

    public String getDistributionTargetVillagesTowns() {
        return distributionTargetVillagesTowns;
    }

    public void setDistributionTargetVillagesTowns(String distributionTargetVillagesTowns) {
        this.distributionTargetVillagesTowns = distributionTargetVillagesTowns;
    }

    public String getDistributionTargetAddress() {
        return distributionTargetAddress;
    }

    public void setDistributionTargetAddress(String distributionTargetAddress) {
        this.distributionTargetAddress = distributionTargetAddress;
    }

    public String getDistributionTargetEmail() {
        return distributionTargetEmail;
    }

    public void setDistributionTargetEmail(String distributionTargetEmail) {
        this.distributionTargetEmail = distributionTargetEmail;
    }

    public String getDistributionTargetZip() {
        return distributionTargetZip;
    }

    public void setDistributionTargetZip(String distributionTargetZip) {
        this.distributionTargetZip = distributionTargetZip;
    }

    public String getConsigneeTargetName() {
        return consigneeTargetName;
    }

    public void setConsigneeTargetName(String consigneeTargetName) {
        this.consigneeTargetName = consigneeTargetName;
    }

    public String getConsigneeTargetMobilePhone() {
        return consigneeTargetMobilePhone;
    }

    public void setConsigneeTargetMobilePhone(String consigneeTargetMobilePhone) {
        this.consigneeTargetMobilePhone = consigneeTargetMobilePhone;
    }

    public String getConsigneeTargetTelephone() {
        return consigneeTargetTelephone;
    }

    public void setConsigneeTargetTelephone(String consigneeTargetTelephone) {
        this.consigneeTargetTelephone = consigneeTargetTelephone;
    }

    public String getConsigneeTargetCountry() {
        return consigneeTargetCountry;
    }

    public void setConsigneeTargetCountry(String consigneeTargetCountry) {
        this.consigneeTargetCountry = consigneeTargetCountry;
    }

    public String getConsigneeTargetProvince() {
        return consigneeTargetProvince;
    }

    public void setConsigneeTargetProvince(String consigneeTargetProvince) {
        this.consigneeTargetProvince = consigneeTargetProvince;
    }

    public String getConsigneeTargetCity() {
        return consigneeTargetCity;
    }

    public void setConsigneeTargetCity(String consigneeTargetCity) {
        this.consigneeTargetCity = consigneeTargetCity;
    }

    public String getConsigneeTargetDistrict() {
        return consigneeTargetDistrict;
    }

    public void setConsigneeTargetDistrict(String consigneeTargetDistrict) {
        this.consigneeTargetDistrict = consigneeTargetDistrict;
    }

    public String getConsigneeTargetVillagesTowns() {
        return consigneeTargetVillagesTowns;
    }

    public void setConsigneeTargetVillagesTowns(String consigneeTargetVillagesTowns) {
        this.consigneeTargetVillagesTowns = consigneeTargetVillagesTowns;
    }

    public String getConsigneeTargetAddress() {
        return consigneeTargetAddress;
    }

    public void setConsigneeTargetAddress(String consigneeTargetAddress) {
        this.consigneeTargetAddress = consigneeTargetAddress;
    }

    public String getConsigneeTargetEmail() {
        return consigneeTargetEmail;
    }

    public void setConsigneeTargetEmail(String consigneeTargetEmail) {
        this.consigneeTargetEmail = consigneeTargetEmail;
    }

    public String getConsigneeTargetZip() {
        return consigneeTargetZip;
    }

    public void setConsigneeTargetZip(String consigneeTargetZip) {
        this.consigneeTargetZip = consigneeTargetZip;
    }

    public String getDeliverGoodsTimeMode() {
        return deliverGoodsTimeMode;
    }

    public void setDeliverGoodsTimeMode(String deliverGoodsTimeMode) {
        this.deliverGoodsTimeMode = deliverGoodsTimeMode;
    }

    public String getDeliverGoodsTimeModeName() {
        return deliverGoodsTimeModeName;
    }

    public void setDeliverGoodsTimeModeName(String deliverGoodsTimeModeName) {
        this.deliverGoodsTimeModeName = deliverGoodsTimeModeName;
    }

    public String getDeliverGoodsTime() {
        return deliverGoodsTime;
    }

    public void setDeliverGoodsTime(String deliverGoodsTime) {
        this.deliverGoodsTime = deliverGoodsTime;
    }

    public String getPlanDeliverGoodsTime() {
        return planDeliverGoodsTime;
    }

    public void setPlanDeliverGoodsTime(String planDeliverGoodsTime) {
        this.planDeliverGoodsTime = planDeliverGoodsTime;
    }

    public String getActualDeliverGoodsTime() {
        return actualDeliverGoodsTime;
    }

    public void setActualDeliverGoodsTime(String actualDeliverGoodsTime) {
        this.actualDeliverGoodsTime = actualDeliverGoodsTime;
    }

    public String getIncludeFragileCargo() {
        return includeFragileCargo;
    }

    public void setIncludeFragileCargo(String includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
    }

    public String getIncludeFragileCargoName() {
        return includeFragileCargoName;
    }

    public void setIncludeFragileCargoName(String includeFragileCargoName) {
        this.includeFragileCargoName = includeFragileCargoName;
    }

    public String getIncludeHazardousCargo() {
        return includeHazardousCargo;
    }

    public void setIncludeHazardousCargo(String includeHazardousCargo) {
        this.includeHazardousCargo = includeHazardousCargo;
    }

    public String getIncludeHazardousCargoName() {
        return includeHazardousCargoName;
    }

    public void setIncludeHazardousCargoName(String includeHazardousCargoName) {
        this.includeHazardousCargoName = includeHazardousCargoName;
    }

    public String getOutboundCartonType() {
        return outboundCartonType;
    }

    public void setOutboundCartonType(String outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }

    public String getOutboundCartonTypeName() {
        return outboundCartonTypeName;
    }

    public void setOutboundCartonTypeName(String outboundCartonTypeName) {
        this.outboundCartonTypeName = outboundCartonTypeName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(String modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getModifiedName() {
        return modifiedName;
    }

    public void setModifiedName(String modifiedName) {
        this.modifiedName = modifiedName;
    }

    public String getEcOrderCode() {
        return ecOrderCode;
    }

    public void setEcOrderCode(String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
    }

    public String getCreatedName() {
        return createdName;
    }

    public void setCreatedName(String createdName) {
        this.createdName = createdName;
    }

    public String getIsAssignSuccess() {
        return isAssignSuccess;
    }

    public void setIsAssignSuccess(String isAssignSuccess) {
        this.isAssignSuccess = isAssignSuccess;
    }

    public String getAssignFailReason() {
        return assignFailReason;
    }

    public void setAssignFailReason(String assignFailReason) {
        this.assignFailReason = assignFailReason;
    }

    public String getOriginalOdoCode() {
        return originalOdoCode;
    }

    public void setOriginalOdoCode(String originalOdoCode) {
        this.originalOdoCode = originalOdoCode;
    }

    public Integer getLogisticsFlag() {
        return logisticsFlag;
    }

    public void setLogisticsFlag(Integer logisticsFlag) {
        this.logisticsFlag = logisticsFlag;
    }

}
