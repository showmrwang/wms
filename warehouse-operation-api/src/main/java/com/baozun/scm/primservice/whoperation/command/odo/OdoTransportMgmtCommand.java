package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoTransportMgmtCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -6485257999960905213L;
    /** 出库单ID */
    private Long odoId;
    /** 运输服务商 */
    private String transportServiceProvider;
    /** 运输方式 */
    private String modeOfTransport;
    /** 快递服务类型 */
    private String courierServiceType;
    /** 出库目标类型 */
    private String outboundTargetType;
    /** 出库目标对象 */
    private String outboundTarget;
    /** 指定送货时间模式 */
    private String deliverGoodsTimeMode;
    /** 指定送货时间 */
    private java.util.Date deliverGoodsTime;
    /** 计划发货时间 */
    private java.util.Date planDeliverGoodsTime;
    /** 实际发货时间 */
    private java.util.Date actualDeliverGoodsTime;
    /** 仓库组织ID */
    private Long ouId;

    // ------------------------------------------------------------------------------------------------------------
    // 自定义字段
    private String deliverGoodsTimeStr;
    private String planDeliverGoodsTimeStr;
    private String actualDeliverGoodsTimeStr;

    public String getDeliverGoodsTimeStr() {
        return deliverGoodsTimeStr;
    }

    public void setDeliverGoodsTimeStr(String deliverGoodsTimeStr) {
        this.deliverGoodsTimeStr = deliverGoodsTimeStr;
    }

    public String getPlanDeliverGoodsTimeStr() {
        return planDeliverGoodsTimeStr;
    }

    public void setPlanDeliverGoodsTimeStr(String planDeliverGoodsTimeStr) {
        this.planDeliverGoodsTimeStr = planDeliverGoodsTimeStr;
    }

    public String getActualDeliverGoodsTimeStr() {
        return actualDeliverGoodsTimeStr;
    }

    public void setActualDeliverGoodsTimeStr(String actualDeliverGoodsTimeStr) {
        this.actualDeliverGoodsTimeStr = actualDeliverGoodsTimeStr;
    }
    public Long getOdoId() {
        return odoId;
    }
    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }
    public String getTransportServiceProvider() {
        return transportServiceProvider;
    }
    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }
    public String getModeOfTransport() {
        return modeOfTransport;
    }
    public void setModeOfTransport(String modeOfTransport) {
        this.modeOfTransport = modeOfTransport;
    }
    public String getCourierServiceType() {
        return courierServiceType;
    }
    public void setCourierServiceType(String courierServiceType) {
        this.courierServiceType = courierServiceType;
    }
    public String getOutboundTargetType() {
        return outboundTargetType;
    }
    public void setOutboundTargetType(String outboundTargetType) {
        this.outboundTargetType = outboundTargetType;
    }
    public String getOutboundTarget() {
        return outboundTarget;
    }
    public void setOutboundTarget(String outboundTarget) {
        this.outboundTarget = outboundTarget;
    }
    public String getDeliverGoodsTimeMode() {
        return deliverGoodsTimeMode;
    }
    public void setDeliverGoodsTimeMode(String deliverGoodsTimeMode) {
        this.deliverGoodsTimeMode = deliverGoodsTimeMode;
    }
    public java.util.Date getDeliverGoodsTime() {
        return deliverGoodsTime;
    }
    public void setDeliverGoodsTime(java.util.Date deliverGoodsTime) {
        this.deliverGoodsTime = deliverGoodsTime;
    }
    public java.util.Date getPlanDeliverGoodsTime() {
        return planDeliverGoodsTime;
    }
    public void setPlanDeliverGoodsTime(java.util.Date planDeliverGoodsTime) {
        this.planDeliverGoodsTime = planDeliverGoodsTime;
    }
    public java.util.Date getActualDeliverGoodsTime() {
        return actualDeliverGoodsTime;
    }
    public void setActualDeliverGoodsTime(java.util.Date actualDeliverGoodsTime) {
        this.actualDeliverGoodsTime = actualDeliverGoodsTime;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    
}
