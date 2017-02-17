package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoGroupSearchCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -4770697049406502326L;
    // --------------------------
    private String odoCode;
    private String extCode;
    private String odoStatus;
    private String epistaticSystemsOrderType;
    private String customerId;
    private String outboundTargetType;
    private String odoType;
    private String storeId;
    private String outboundTarget;
    private String modeOfTransport;
    private String transportServiceProvider;
    private String transportServiceProviderType;
    private String distributeMode;
    private String outBoundCartonType;
    private String whVasType;
    private String orderType;
    private String createTimeStart;
    private String createTimeEnd;
    private String ecOrderCode;
    private String orderTimeStart;
    private String orderTimeEnd;
    private String deliverGoodsTimeMode;
    private String deliverGoodsTimeStart;
    private String deliverGoodsTimeEnd;
    private Integer includeFragileCargo;
    private String crossDockingSysmbol;
    private Integer isWholeOrderOutbound;
    private String skuCode;
    private String skuName;
    private String skuBarCode;
    private String odoLineStatus;
    private String lineOutboundCartonType;
    private String mixingAttr;
    private String invType;
    private String sn;
    private String defectWareBarcode;
    private String invStatus;
    private String defectType;
    private String defectReasons;
    private String invAttr1;
    private String invAttr2;
    private String invAttr3;
    private String invAttr4;
    private String invAttr5;
    private String batchNumber;
    private String countryOfOrigin;
    private String mfgDateStart;
    private String mfgDateEnd;
    private String expDateStart;
    private String expDateEnd;
    private String minExpDateStart;
    private String minExpDateEnd;
    private String maxExpDateStart;
    private String maxExpDateEnd;
    private String ids;
    private boolean needOutboundCartonType;
    private boolean needEpistaticSystemsOrderType;
    private boolean needStore;
    private boolean needDeliverGoodsTime;
    private String pageOption;

    private Long ouId;
    private Long userId;
    private Long groupCustomerId;
    private Long groupStoreId;
    private String groupOdoStatus;
    /** 出库单类型 */
    private String groupOdoType;
    /** 配货模式 */
    private String groupDistributeMode;
    /** 运输服务商 */
    private String groupTransportServiceProvider;
    /** 上位单据类型 */
    private String groupEpistaticSystemsOrderType;

    private Boolean isDistributeMode;

    private Boolean isEpistaticSystemsOrderType;

    // 限制行
    private Integer lineNum;
    // 是否行检索
    private Boolean lineFlag;

    // ------------------用于创建出库单字段
    private List<OdoWaveGroupSearchCondition> conditionList;

    private List<Long> odoIdList;

    private Long waveMasterId;

    //
    private List<Long> customerList;

    private List<Long> storeList;

    // --------------------------------------------------------------------------------

    public String getOdoCode() {
        return odoCode;
    }

    public List<Long> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Long> customerList) {
        this.customerList = customerList;
    }

    public List<Long> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<Long> storeList) {
        this.storeList = storeList;
    }

    public List<OdoWaveGroupSearchCondition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<OdoWaveGroupSearchCondition> conditionList) {
        this.conditionList = conditionList;
    }

    public List<Long> getOdoIdList() {
        return odoIdList;
    }

    public void setOdoIdList(List<Long> odoIdList) {
        this.odoIdList = odoIdList;
    }

    public Long getWaveMasterId() {
        return waveMasterId;
    }

    public void setWaveMasterId(Long waveMasterId) {
        this.waveMasterId = waveMasterId;
    }

    public Boolean getIsDistributeMode() {
        return isDistributeMode;
    }

    public void setIsDistributeMode(Boolean isDistributeMode) {
        this.isDistributeMode = isDistributeMode;
    }

    public Boolean getIsEpistaticSystemsOrderType() {
        return isEpistaticSystemsOrderType;
    }

    public void setIsEpistaticSystemsOrderType(Boolean isEpistaticSystemsOrderType) {
        this.isEpistaticSystemsOrderType = isEpistaticSystemsOrderType;
    }

    public String getGroupOdoType() {
        return groupOdoType;
    }

    public void setGroupOdoType(String groupOdoType) {
        this.groupOdoType = groupOdoType;
    }

    public String getGroupDistributeMode() {
        return groupDistributeMode;
    }

    public void setGroupDistributeMode(String groupDistributeMode) {
        this.groupDistributeMode = groupDistributeMode;
    }

    public String getGroupTransportServiceProvider() {
        return groupTransportServiceProvider;
    }

    public void setGroupTransportServiceProvider(String groupTransportServiceProvider) {
        this.groupTransportServiceProvider = groupTransportServiceProvider;
    }

    public String getGroupEpistaticSystemsOrderType() {
        return groupEpistaticSystemsOrderType;
    }

    public void setGroupEpistaticSystemsOrderType(String groupEpistaticSystemsOrderType) {
        this.groupEpistaticSystemsOrderType = groupEpistaticSystemsOrderType;
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
    public String getOdoStatus() {
        return odoStatus;
    }
    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }

    public String getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }

    public void setEpistaticSystemsOrderType(String epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getOutboundTargetType() {
        return outboundTargetType;
    }
    public void setOutboundTargetType(String outboundTargetType) {
        this.outboundTargetType = outboundTargetType;
    }
    public String getOdoType() {
        return odoType;
    }
    public void setOdoType(String odoType) {
        this.odoType = odoType;
    }
    public String getStoreId() {
        return storeId;
    }
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    public String getOutboundTarget() {
        return outboundTarget;
    }
    public void setOutboundTarget(String outboundTarget) {
        this.outboundTarget = outboundTarget;
    }
    public String getModeOfTransport() {
        return modeOfTransport;
    }
    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }

    public String getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }
    public String getTransportServiceProviderType() {
        return transportServiceProviderType;
    }
    public void setTransportServiceProviderType(String transportServiceProviderType) {
        this.transportServiceProviderType = transportServiceProviderType;
    }
    public String getDistributeMode() {
        return distributeMode;
    }
    public void setDistributeMode(String distributeMode) {
        this.distributeMode = distributeMode;
    }
    public String getOutBoundCartonType() {
        return outBoundCartonType;
    }
    public void setOutBoundCartonType(String outBoundCartonType) {
        this.outBoundCartonType = outBoundCartonType;
    }
    public String getWhVasType() {
        return whVasType;
    }
    public void setWhVasType(String whVasType) {
        this.whVasType = whVasType;
    }
    public String getOrderType() {
        return orderType;
    }
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    public String getCreateTimeStart() {
        return createTimeStart;
    }
    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }
    public String getCreateTimeEnd() {
        return createTimeEnd;
    }
    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
    public String getEcOrderCode() {
        return ecOrderCode;
    }
    public void setEcOrderCode(String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
    }
    public String getOrderTimeStart() {
        return orderTimeStart;
    }
    public void setOrderTimeStart(String orderTimeStart) {
        this.orderTimeStart = orderTimeStart;
    }
    public String getOrderTimeEnd() {
        return orderTimeEnd;
    }
    public void setOrderTimeEnd(String orderTimeEnd) {
        this.orderTimeEnd = orderTimeEnd;
    }
    public String getDeliverGoodsTimeMode() {
        return deliverGoodsTimeMode;
    }
    public void setDeliverGoodsTimeMode(String deliverGoodsTimeMode) {
        this.deliverGoodsTimeMode = deliverGoodsTimeMode;
    }
    public String getDeliverGoodsTimeStart() {
        return deliverGoodsTimeStart;
    }
    public void setDeliverGoodsTimeStart(String deliverGoodsTimeStart) {
        this.deliverGoodsTimeStart = deliverGoodsTimeStart;
    }
    public String getDeliverGoodsTimeEnd() {
        return deliverGoodsTimeEnd;
    }
    public void setDeliverGoodsTimeEnd(String deliverGoodsTimeEnd) {
        this.deliverGoodsTimeEnd = deliverGoodsTimeEnd;
    }
    public Integer getIncludeFragileCargo() {
        return includeFragileCargo;
    }
    public void setIncludeFragileCargo(Integer includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
    }
    public String getCrossDockingSysmbol() {
        return crossDockingSysmbol;
    }
    public void setCrossDockingSysmbol(String crossDockingSysmbol) {
        this.crossDockingSysmbol = crossDockingSysmbol;
    }
    public Integer getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }
    public void setIsWholeOrderOutbound(Integer isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
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
    public String getSkuBarCode() {
        return skuBarCode;
    }
    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }
    public String getOdoLineStatus() {
        return odoLineStatus;
    }
    public void setOdoLineStatus(String odoLineStatus) {
        this.odoLineStatus = odoLineStatus;
    }
    public String getLineOutboundCartonType() {
        return lineOutboundCartonType;
    }
    public void setLineOutboundCartonType(String lineOutboundCartonType) {
        this.lineOutboundCartonType = lineOutboundCartonType;
    }
    public String getMixingAttr() {
        return mixingAttr;
    }
    public void setMixingAttr(String mixingAttr) {
        this.mixingAttr = mixingAttr;
    }
    public String getInvType() {
        return invType;
    }
    public void setInvType(String invType) {
        this.invType = invType;
    }
    public String getSn() {
        return sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getDefectWareBarcode() {
        return defectWareBarcode;
    }
    public void setDefectWareBarcode(String defectWareBarcode) {
        this.defectWareBarcode = defectWareBarcode;
    }
    public String getInvStatus() {
        return invStatus;
    }
    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }
    public String getDefectType() {
        return defectType;
    }
    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }
    public String getDefectReasons() {
        return defectReasons;
    }
    public void setDefectReasons(String defectReasons) {
        this.defectReasons = defectReasons;
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
    public String getBatchNumber() {
        return batchNumber;
    }
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }
    public String getMfgDateStart() {
        return mfgDateStart;
    }
    public void setMfgDateStart(String mfgDateStart) {
        this.mfgDateStart = mfgDateStart;
    }
    public String getMfgDateEnd() {
        return mfgDateEnd;
    }
    public void setMfgDateEnd(String mfgDateEnd) {
        this.mfgDateEnd = mfgDateEnd;
    }
    public String getExpDateStart() {
        return expDateStart;
    }
    public void setExpDateStart(String expDateStart) {
        this.expDateStart = expDateStart;
    }
    public String getExpDateEnd() {
        return expDateEnd;
    }
    public void setExpDateEnd(String expDateEnd) {
        this.expDateEnd = expDateEnd;
    }
    public String getMinExpDateStart() {
        return minExpDateStart;
    }
    public void setMinExpDateStart(String minExpDateStart) {
        this.minExpDateStart = minExpDateStart;
    }
    public String getMinExpDateEnd() {
        return minExpDateEnd;
    }
    public void setMinExpDateEnd(String minExpDateEnd) {
        this.minExpDateEnd = minExpDateEnd;
    }
    public String getMaxExpDateStart() {
        return maxExpDateStart;
    }
    public void setMaxExpDateStart(String maxExpDateStart) {
        this.maxExpDateStart = maxExpDateStart;
    }
    public String getMaxExpDateEnd() {
        return maxExpDateEnd;
    }
    public void setMaxExpDateEnd(String maxExpDateEnd) {
        this.maxExpDateEnd = maxExpDateEnd;
    }
    public String getIds() {
        return ids;
    }
    public void setIds(String ids) {
        this.ids = ids;
    }
    public boolean isNeedOutboundCartonType() {
        return needOutboundCartonType;
    }
    public void setNeedOutboundCartonType(boolean needOutboundCartonType) {
        this.needOutboundCartonType = needOutboundCartonType;
    }
    public boolean isNeedEpistaticSystemsOrderType() {
        return needEpistaticSystemsOrderType;
    }
    public void setNeedEpistaticSystemsOrderType(boolean needEpistaticSystemsOrderType) {
        this.needEpistaticSystemsOrderType = needEpistaticSystemsOrderType;
    }
    public boolean isNeedStore() {
        return needStore;
    }
    public void setNeedStore(boolean needStore) {
        this.needStore = needStore;
    }
    public boolean isNeedDeliverGoodsTime() {
        return needDeliverGoodsTime;
    }
    public void setNeedDeliverGoodsTime(boolean needDeliverGoodsTime) {
        this.needDeliverGoodsTime = needDeliverGoodsTime;
    }
    public String getPageOption() {
        return pageOption;
    }
    public void setPageOption(String pageOption) {
        this.pageOption = pageOption;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getGroupCustomerId() {
        return groupCustomerId;
    }
    public void setGroupCustomerId(Long groupCustomerId) {
        this.groupCustomerId = groupCustomerId;
    }
    public Long getGroupStoreId() {
        return groupStoreId;
    }
    public void setGroupStoreId(Long groupStoreId) {
        this.groupStoreId = groupStoreId;
    }
    public String getGroupOdoStatus() {
        return groupOdoStatus;
    }
    public void setGroupOdoStatus(String groupOdoStatus) {
        this.groupOdoStatus = groupOdoStatus;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }
    public Boolean getLineFlag() {
        return lineFlag;
    }
    public void setLineFlag(Boolean lineFlag) {
        this.lineFlag = lineFlag;
    }



}
