package com.baozun.scm.primservice.whoperation.model.odo;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class WhOdodeliveryInfo extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 163233353819006528L;
    /** 出库单ID */
    private Long odoId;
    /** 物流商编码 */
    private String transportCode;
    /** 运单号 */
    private String waybillCode;
    /** 运输服务类型 */
    private String transportServiceType;
    /** 时效类型 */
    private String timeEffectType;
    /** 原始运单信息 */
    private Long deliveryInfoId;
    /** 出库箱ID */
    private Long outboundboxId;
    /** 出库箱号 */
    private String outboundboxCode;
    /** 状态 */
    private Integer status;
    /** 对应组织ID */
    private Long ouId;
    /** 1.可用;0.禁用 */
    private Integer lifecycle;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;
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
    /** 集包地名称 */
    private String extId;

    /** 当前月份 用于归档 */
    private String sysDate;

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
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

    public Long getDeliveryInfoId() {
        return deliveryInfoId;
    }

    public void setDeliveryInfoId(Long deliveryInfoId) {
        this.deliveryInfoId = deliveryInfoId;
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

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
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

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public String getTransBigWord() {
        return transBigWord;
    }

    public String getTmsCode() {
        return tmsCode;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public String getPackageCenterCode() {
        return packageCenterCode;
    }

    public String getPackageCenterName() {
        return packageCenterName;
    }

    public void setTransBigWord(String transBigWord) {
        this.transBigWord = transBigWord;
    }

    public void setTmsCode(String tmsCode) {
        this.tmsCode = tmsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public void setPackageCenterCode(String packageCenterCode) {
        this.packageCenterCode = packageCenterCode;
    }

    public void setPackageCenterName(String packageCenterName) {
        this.packageCenterName = packageCenterName;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

}
