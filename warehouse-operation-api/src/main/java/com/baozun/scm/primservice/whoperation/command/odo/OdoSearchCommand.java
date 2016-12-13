package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoSearchCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 2989171570103313915L;
    private String odoCode;
    private String extCode;
    private List<String> odoStatus;
    private List<String> epistaticSystemsOrderType;
    private List<String> customerId;
    private List<String> outboundTargetType;
    private List<String> odoType;
    private List<String> storeId;
    private String outboundTarget;
    private List<String> modeOfTransport;
    private List<String> transportServiceProvider;
    private List<String> transportServiceProviderType;
    private List<String> distributeMode;
    private List<String> outBoundCartonType;
    private List<String> whVasType;
    private List<String> orderType;
    private String createTimeStart;
    private String createTimeEnd;
    private String ecOrderCode;
    private String orderTimeStart;
    private String orderTimeEnd;
    private List<String> deliverGoodsTimeMode;
    private String deliverGoodsTimeStart;
    private String deliverGoodsTimeEnd;
    private Integer includeFragileCargo;
    private List<String> crossDockingSysmbol;
    private Integer isWholeOrderOutbound;
    private String skuCode;
    private String skuName;
    private String skuBarCode;
    private List<String> odoLineStatus;
    private List<String> lineOutboundCartonType;
    private String mixingAttr;
    private List<String> invType;
    private String sn;
    private String defectWareBarcode;
    private List<String> invStatus;
    private String defectType;
    private String defectReasons;
    private List<String> invAttr1;
    private List<String> invAttr2;
    private List<String> invAttr3;
    private List<String> invAttr4;
    private List<String> invAttr5;
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
    // 是否波次运行异常订单
    private Integer isExceptionOrder;
    // 是否合并出库单
    private Integer isMerged;

    // 用于导出
    private List<Long> customerList;
    private List<Long> storeList;
    private Integer limit;
    private String sqlCondition;


    public String getSqlCondition() {
        return sqlCondition;
    }

    public void setSqlCondition(String sqlCondition) {
        this.sqlCondition = sqlCondition;
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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
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

    public Boolean getLineFlag() {
        return lineFlag;
    }

    public void setLineFlag(Boolean lineFlag) {
        this.lineFlag = lineFlag;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public String getGroupOdoStatus() {
        return groupOdoStatus;
    }

    public void setGroupOdoStatus(String groupOdoStatus) {
        this.groupOdoStatus = groupOdoStatus;
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

    public List<String> getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(List<String> odoStatus) {
        this.odoStatus = odoStatus;
    }


    public List<String> getEpistaticSystemsOrderType() {
        return epistaticSystemsOrderType;
    }

    public void setEpistaticSystemsOrderType(List<String> epistaticSystemsOrderType) {
        this.epistaticSystemsOrderType = epistaticSystemsOrderType;
    }

    public List<String> getCustomerId() {
        return customerId;
    }

    public void setCustomerId(List<String> customerId) {
        this.customerId = customerId;
    }

    public List<String> getOutboundTargetType() {
        return outboundTargetType;
    }

    public void setOutboundTargetType(List<String> outboundTargetType) {
        this.outboundTargetType = outboundTargetType;
    }

    public List<String> getStoreId() {
        return storeId;
    }

    public void setStoreId(List<String> storeId) {
        this.storeId = storeId;
    }

    public String getOutboundTarget() {
        return outboundTarget;
    }

    public void setOutboundTarget(String outboundTarget) {
        this.outboundTarget = outboundTarget;
    }

    public List<String> getModeOfTransport() {
        return modeOfTransport;
    }

    public void setModeOfTransport(List<String> modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }

    public List<String> getTransportServiceProviderType() {
        return transportServiceProviderType;
    }

    public void setTransportServiceProviderType(List<String> transportServiceProviderType) {
        this.transportServiceProviderType = transportServiceProviderType;
    }

    public List<String> getDistributeMode() {
        return distributeMode;
    }

    public void setDistributeMode(List<String> distributeMode) {
        this.distributeMode = distributeMode;
    }

    public List<String> getOutBoundCartonType() {
        return outBoundCartonType;
    }

    public void setOutBoundCartonType(List<String> outBoundCartonType) {
        this.outBoundCartonType = outBoundCartonType;
    }

    public List<String> getWhVasType() {
        return whVasType;
    }

    public void setWhVasType(List<String> whVasType) {
        this.whVasType = whVasType;
    }

    public List<String> getOrderType() {
        return orderType;
    }

    public void setOrderType(List<String> orderType) {
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

    public List<String> getDeliverGoodsTimeMode() {
        return deliverGoodsTimeMode;
    }

    public void setDeliverGoodsTimeMode(List<String> deliverGoodsTimeMode) {
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

    public List<String> getCrossDockingSysmbol() {
        return crossDockingSysmbol;
    }

    public void setCrossDockingSysmbol(List<String> crossDockingSysmbol) {
        this.crossDockingSysmbol = crossDockingSysmbol;
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

    public List<String> getOdoLineStatus() {
        return odoLineStatus;
    }

    public void setOdoLineStatus(List<String> odoLineStatus) {
        this.odoLineStatus = odoLineStatus;
    }

    public List<String> getLineOutboundCartonType() {
        return lineOutboundCartonType;
    }

    public void setLineOutboundCartonType(List<String> lineOutboundCartonType) {
        this.lineOutboundCartonType = lineOutboundCartonType;
    }

    public String getMixingAttr() {
        return mixingAttr;
    }

    public void setMixingAttr(String mixingAttr) {
        this.mixingAttr = mixingAttr;
    }

    public List<String> getInvType() {
        return invType;
    }

    public void setInvType(List<String> invType) {
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

    public List<String> getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(List<String> invStatus) {
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

    public List<String> getInvAttr1() {
        return invAttr1;
    }

    public void setInvAttr1(List<String> invAttr1) {
        this.invAttr1 = invAttr1;
    }

    public List<String> getInvAttr2() {
        return invAttr2;
    }

    public void setInvAttr2(List<String> invAttr2) {
        this.invAttr2 = invAttr2;
    }

    public List<String> getInvAttr3() {
        return invAttr3;
    }

    public void setInvAttr3(List<String> invAttr3) {
        this.invAttr3 = invAttr3;
    }

    public List<String> getInvAttr4() {
        return invAttr4;
    }

    public void setInvAttr4(List<String> invAttr4) {
        this.invAttr4 = invAttr4;
    }

    public List<String> getInvAttr5() {
        return invAttr5;
    }

    public void setInvAttr5(List<String> invAttr5) {
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

    public List<String> getOdoType() {
        return odoType;
    }

    public void setOdoType(List<String> odoType) {
        this.odoType = odoType;
    }

    public Integer getIncludeFragileCargo() {
        return includeFragileCargo;
    }

    public void setIncludeFragileCargo(Integer includeFragileCargo) {
        this.includeFragileCargo = includeFragileCargo;
    }

    public Integer getIsWholeOrderOutbound() {
        return isWholeOrderOutbound;
    }

    public void setIsWholeOrderOutbound(Integer isWholeOrderOutbound) {
        this.isWholeOrderOutbound = isWholeOrderOutbound;
    }

    public List<String> getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(List<String> transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
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

	public Integer getIsExceptionOrder() {
		return isExceptionOrder;
	}

	public void setIsExceptionOrder(Integer isExceptionOrder) {
		this.isExceptionOrder = isExceptionOrder;
	}

    public Integer getIsMerged() {
        return isMerged;
    }

    public void setIsMerged(Integer isMerged) {
        this.isMerged = isMerged;
    }

}
