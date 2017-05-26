package com.baozun.scm.primservice.whoperation.model.handover;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class WhOutboundDeliveryConfirm extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -4319391163876142410L;

    // columns START
    /** 出库单ID */
    private Long odoId;
    /** 出库单号 */
    private String odoCode;
    /** 外部对接编码 */
    private String extCode;
    /** 电商平台订单号 */
    private String ecOrderCode;
    /** 物流平台单号 */
    private String extTransOrderId;
    /** 物流商编码 */
    private String transportCode;
    /** 运单号 */
    private String waybillCode;
    /** 子运单号集合 */
    private String childWaybillCodes;
    /** 运输服务类型 */
    private String transportServiceType;
    /** 时效类型 */
    private String timeEffectType;
    /** 运单大头笔 */
    private String transBigWord;
    /** 二级配送公司编码 */
    private String tmsCode;
    /** 物流公司编码 */
    private String logisticsCode;
    /** 集包地编码 */
    private String packageCenterCode;
    /** 集包地名称 */
    private String packageCenterName;
    /** 出库箱ID */
    private Long outboundboxId;
    /** 出库箱号 */
    private String outboundboxCode;
    /** 客户CODE */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺CODE */
    private String storeCode;
    /** 店铺名称 */
    private String storeName;
    /** 状态 */
    private Integer status;
    /** 对应组织ID */
    private Long ouId;
    /** 仓库CODE */
    private String ouCode;
    /** 重量，单位千克 */
    private Long weight;
    /** 浮动百分比 */
    private Integer floats;
    /** 长度，单位厘米 */
    private Double length;
    /** 宽，单位厘米 */
    private Double width;
    /** 高，单位厘米 */
    private Double high;
    /** 收货人姓名 */
    private String consigneeTargetName;
    /** 收货人手机 */
    private String consigneeTargetMobilePhone;
    /** 收货人固定电话 */
    private String consigneeTargetTelephone;
    /** 收货人国家 */
    private String consigneeTargetCountry;
    /** 收货人省 */
    private String consigneeTargetProvince;
    /** 收货人市 */
    private String consigneeTargetCity;
    /** 收货人区 */
    private String consigneeTargetDistrict;
    /** 收货人乡镇/街道 */
    private String consigneeTargetVillagesTowns;
    /** 收货人详细地址 */
    private String consigneeTargetAddress;
    /** 收货人邮箱 */
    private String consigneeTargetEmail;
    /** 收货人邮编 */
    private String consigneeTargetZip;
    /** 类型：默认1 */
    private Integer type;
    /** 来源渠道：TM或TB */
    private String orderSource;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;

    // columns END
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

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getEcOrderCode() {
        return ecOrderCode;
    }

    public void setEcOrderCode(String ecOrderCode) {
        this.ecOrderCode = ecOrderCode;
    }

    public String getExtTransOrderId() {
        return extTransOrderId;
    }

    public void setExtTransOrderId(String extTransOrderId) {
        this.extTransOrderId = extTransOrderId;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getChildWaybillCodes() {
        return childWaybillCodes;
    }

    public void setChildWaybillCodes(String childWaybillCodes) {
        this.childWaybillCodes = childWaybillCodes;
    }

    public String getTransportServiceType() {
        return transportServiceType;
    }

    public void setTransportServiceType(String transportServiceType) {
        this.transportServiceType = transportServiceType;
    }

    public String getTimeEffectType() {
        return timeEffectType;
    }

    public void setTimeEffectType(String timeEffectType) {
        this.timeEffectType = timeEffectType;
    }

    public String getTransBigWord() {
        return transBigWord;
    }

    public void setTransBigWord(String transBigWord) {
        this.transBigWord = transBigWord;
    }

    public String getTmsCode() {
        return tmsCode;
    }

    public void setTmsCode(String tmsCode) {
        this.tmsCode = tmsCode;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getPackageCenterCode() {
        return packageCenterCode;
    }

    public void setPackageCenterCode(String packageCenterCode) {
        this.packageCenterCode = packageCenterCode;
    }

    public String getPackageCenterName() {
        return packageCenterName;
    }

    public void setPackageCenterName(String packageCenterName) {
        this.packageCenterName = packageCenterName;
    }

    public Long getOutboundboxId() {
        return outboundboxId;
    }

    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getOuCode() {
        return ouCode;
    }

    public void setOuCode(String ouCode) {
        this.ouCode = ouCode;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Integer getFloats() {
        return floats;
    }

    public void setFloats(Integer floats) {
        this.floats = floats;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }


}
