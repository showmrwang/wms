/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.model.odo;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOdoTransportMgmt extends BaseModel {


    private static final long serialVersionUID = 1876575662717571176L;

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
    /** 是否COD */
    private Boolean isCod;
    /** COD金额 */
    private Double codAmt;
    /** 保价金额 */
    private Double insuranceCoverage;

    public Double getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public void setInsuranceCoverage(Double insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public Boolean getIsCod() {
        return isCod;
    }

    public void setIsCod(Boolean isCod) {
        this.isCod = isCod;
    }

    public Double getCodAmt() {
        return codAmt;
    }

    public void setCodAmt(Double codAmt) {
        this.codAmt = codAmt;
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

    public Date getDeliverGoodsTime() {
        return deliverGoodsTime;
    }

    public void setDeliverGoodsTime(Date deliverGoodsTime) {
        this.deliverGoodsTime = deliverGoodsTime;
    }

    public Date getPlanDeliverGoodsTime() {
        return planDeliverGoodsTime;
    }

    public void setPlanDeliverGoodsTime(Date planDeliverGoodsTime) {
        this.planDeliverGoodsTime = planDeliverGoodsTime;
    }

    public Date getActualDeliverGoodsTime() {
        return actualDeliverGoodsTime;
    }

    public void setActualDeliverGoodsTime(Date actualDeliverGoodsTime) {
        this.actualDeliverGoodsTime = actualDeliverGoodsTime;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
