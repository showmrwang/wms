package com.baozun.scm.primservice.whoperation.command.poasn;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhAsnSearchCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = -6016959562599621684L;
    
    /** asn单号 */
    private String asnCode;
    /** 对应PO单号 */
    private String extCode;
    /** asn相关单据号 */
    private String asnExtCode;
    /** 是否质检 1:是 0:否 */
    private String isIqc;
    /** ASN单类型 */
    private List<String> asnType;
    /** 所属仓库ID */
    private List<String> ouId;
    /** 店铺ID */
    private List<String> storeId;
    /** 客户ID */
    private List<String> customerId;
    /** 供应商ID */
    private List<String> supplierId;
    /** 运输商ID */
    private List<String> logisticsProviderId;
    /** 运输商ID */
    private String logisticsProvider;
    /** 状态 */
    private List<String> asnStatus;
    /** 采购时间 */
    private String poDateStart;
    /** 采购时间 */
    private String poDateEnd;
    /** 计划到货时间 */
    private String etaStart;
    /** 计划到货时间 */
    private String etaEnd;
    /** 实际到货时间 */
    private String deliveryTimeStart;
    /** 实际到货时间 */
    private String deliveryTimeEnd;
    /** 开始收货时间 */
    private String startTimeStart;
    /** 开始收货时间 */
    private String startTimeEnd;
    /** 结束收获时间 */
    private String stopTimeStart;
    /** 结束收获时间 */
    private String stopTimeEnd;
    /** 上架完成时间 */
    private String inboundTimeStart;
    /** 上架完成时间 */
    private String inboundTimeEnd;

    //明细
    /** POLine行号 */
    private String poLinenum;
    /** 商品编码 */
    private String skuCode;
    /** 商品条码 */
    private String skuBarCode;
    /** 商品名称 */
    private String skuName;
    /** 状态 */
    private List<String> lineStatus;
    /** 是否质检 1:是 0:否 */
    private String lineIsIqc;
    /** 混放属性*/
    private String mixAttr;
    /** 生产日期 */
    private String mfgDateStart;
    /** 生产日期 */
    private String mfgDateEnd;
    /** 失效日期 */
    private String expDateStart;
    /** 失效日期 */
    private String expDateEnd;
    /** 批次号 */
    private String batchNo;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存状态 */
    private List<String> invStatus;
    /** 库存类型 */
    private List<String> invType;
    /** 库存属性1 */
    private List<String> invAttr1;
    /** 库存属性2 */
    private List<String> invAttr2;
    /** 库存属性3 */
    private List<String> invAttr3;
    /** 库存属性4 */
    private List<String> invAttr4;
    /** 库存属性5 */
    private List<String> invAttr5;
    /** 序列号 */
    private String sn;
    /** 残次品条码 */
    private String defectWareBarcode;
    /** 残次原因类型 */
    private String defectType;
    /** 残次原因 */
    private String defectReasons;
    
    
    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }
    public String getAsnCode() {
        return asnCode;
    }
    public void setAsnCode(String asnCode) {
        this.asnCode = asnCode;
    }
    public String getAsnExtCode() {
        return asnExtCode;
    }
    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }
    public String getIsIqc() {
        return isIqc;
    }
    public void setIsIqc(String isIqc) {
        this.isIqc = isIqc;
    }
    public List<String> getAsnType() {
        return asnType;
    }
    public void setAsnType(List<String> asnType) {
        this.asnType = asnType;
    }
    public List<String> getOuId() {
        return ouId;
    }
    public void setOuId(List<String> ouId) {
        this.ouId = ouId;
    }
    public List<String> getStoreId() {
        return storeId;
    }
    public void setStoreId(List<String> storeId) {
        this.storeId = storeId;
    }
    public List<String> getCustomerId() {
        return customerId;
    }
    public void setCustomerId(List<String> customerId) {
        this.customerId = customerId;
    }
    public List<String> getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(List<String> supplierId) {
        this.supplierId = supplierId;
    }
    public List<String> getLogisticsProviderId() {
        return logisticsProviderId;
    }
    public void setLogisticsProviderId(List<String> logisticsProviderId) {
        this.logisticsProviderId = logisticsProviderId;
    }
    public String getPoDateStart() {
        return poDateStart;
    }
    public void setPoDateStart(String poDateStart) {
        this.poDateStart = poDateStart;
    }
    public String getPoDateEnd() {
        return poDateEnd;
    }
    public void setPoDateEnd(String poDateEnd) {
        this.poDateEnd = poDateEnd;
    }
    public String getEtaStart() {
        return etaStart;
    }
    public void setEtaStart(String etaStart) {
        this.etaStart = etaStart;
    }
    public String getEtaEnd() {
        return etaEnd;
    }
    public void setEtaEnd(String etaEnd) {
        this.etaEnd = etaEnd;
    }
    public String getDeliveryTimeStart() {
        return deliveryTimeStart;
    }
    public void setDeliveryTimeStart(String deliveryTimeStart) {
        this.deliveryTimeStart = deliveryTimeStart;
    }
    public String getDeliveryTimeEnd() {
        return deliveryTimeEnd;
    }
    public void setDeliveryTimeEnd(String deliveryTimeEnd) {
        this.deliveryTimeEnd = deliveryTimeEnd;
    }
    public String getStartTimeStart() {
        return startTimeStart;
    }
    public void setStartTimeStart(String startTimeStart) {
        this.startTimeStart = startTimeStart;
    }
    public String getStartTimeEnd() {
        return startTimeEnd;
    }
    public void setStartTimeEnd(String startTimeEnd) {
        this.startTimeEnd = startTimeEnd;
    }
    public String getStopTimeStart() {
        return stopTimeStart;
    }
    public void setStopTimeStart(String stopTimeStart) {
        this.stopTimeStart = stopTimeStart;
    }
    public String getStopTimeEnd() {
        return stopTimeEnd;
    }
    public void setStopTimeEnd(String stopTimeEnd) {
        this.stopTimeEnd = stopTimeEnd;
    }
    public String getInboundTimeStart() {
        return inboundTimeStart;
    }
    public void setInboundTimeStart(String inboundTimeStart) {
        this.inboundTimeStart = inboundTimeStart;
    }
    public String getInboundTimeEnd() {
        return inboundTimeEnd;
    }
    public void setInboundTimeEnd(String inboundTimeEnd) {
        this.inboundTimeEnd = inboundTimeEnd;
    }
    public String getPoLinenum() {
        return poLinenum;
    }
    public void setPoLinenum(String poLinenum) {
        this.poLinenum = poLinenum;
    }
    public String getSkuCode() {
        return skuCode;
    }
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    public String getSkuBarCode() {
        return skuBarCode;
    }
    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }
    public String getSkuName() {
        return skuName;
    }
    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
    public List<String> getLineStatus() {
        return lineStatus;
    }
    public void setLineStatus(List<String> lineStatus) {
        this.lineStatus = lineStatus;
    }
    public String getLineIsIqc() {
        return lineIsIqc;
    }
    public void setLineIsIqc(String lineIsIqc) {
        this.lineIsIqc = lineIsIqc;
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
    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }
    public List<String> getInvStatus() {
        return invStatus;
    }
    public void setInvStatus(List<String> invStatus) {
        this.invStatus = invStatus;
    }
    public List<String> getInvType() {
        return invType;
    }
    public void setInvType(List<String> invType) {
        this.invType = invType;
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
    public String getExtCode() {
        return extCode;
    }
    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }
    public String getMixAttr() {
        return mixAttr;
    }
    public void setMixAttr(String mixAttr) {
        this.mixAttr = mixAttr;
    }
    public List<String> getAsnStatus() {
        return asnStatus;
    }
    public void setAsnStatus(List<String> asnStatus) {
        this.asnStatus = asnStatus;
    }
}
