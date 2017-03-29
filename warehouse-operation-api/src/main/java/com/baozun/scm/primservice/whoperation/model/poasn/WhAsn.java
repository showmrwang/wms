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
package com.baozun.scm.primservice.whoperation.model.poasn;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * ASN单据信息
 * 
 * @author larkark
 * 
 */
public class WhAsn extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 3750380929086314560L;

    /** asn单号 */
    private String asnCode;
    /** asn相关单据号 */
    private String asnExtCode;
    /** 对应PO_ID */
    private Long poId;
    /** 原始上位系统出库单号 */
	private String originalExtOdoCode;
	/** 原始电商平台出库单号 */
	private String originalEcOrderCode;
    /** 所属仓库ID */
    private Long ouId;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 采购时间 */
    private Date poDate;
    /** 计划到货时间 */
    private Date eta;
    /** 实际到货时间 */
    private Date deliveryTime;
    /** 计划数量 */
    private Double qtyPlanned = 0.0;
    /** 实际数量 */
    private Double qtyRcvd = 0.0;
    /** 计划箱数 */
    private Integer ctnPlanned = 0;
    /** 实际箱数 */
    private Integer ctnRcvd = 0;
    /** 供应商ID */
    private Long supplierId;
    /** 运输商ID */
    private String logisticsProvider;
    /** ASN单类型 */
    private Integer asnType;
    /** 状态 */
    private Integer status;
    /** 是否质检 1:是 0:否 */
    private Boolean isIqc;
    /** 开始收货时间 */
    private Date startTime;
    /** 结束收获时间 */
    private Date stopTime;
    /** 上架完成时间 */
    private Date inboundTime;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** modifiedId */
    private Long modifiedId;
    /** PO单对应的ou_id */
    @Deprecated
    private Long poOuId;
    /** 对应PO单相关编码 */
    private String extCode;
    /** 紧急状态 */
    private String urgentStatus;
    /** 超收比例 */
    private Double overChageRate;
    /** IT专用 */
    private String uuid;
    /** 是否自动关单 */
    private Boolean isAutoClose;
    /** 当前月份 用于归档 */
    private String sysDate;
    /** 归档时间 */
    private Date archivTime;

    public Boolean getIsAutoClose() {
        return isAutoClose;
    }

    public void setIsAutoClose(Boolean isAutoClose) {
        this.isAutoClose = isAutoClose;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getOverChageRate() {
        return overChageRate;
    }

    public void setOverChageRate(Double overChageRate) {
        this.overChageRate = overChageRate;
    }

    public String getAsnCode() {
        return this.asnCode;
    }

    public void setAsnCode(String value) {
        this.asnCode = value;
    }

    public Long getPoId() {
        return this.poId;
    }

    public void setPoId(Long value) {
        this.poId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long value) {
        this.customerId = value;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public void setStoreId(Long value) {
        this.storeId = value;
    }

    public Date getPoDate() {
        return this.poDate;
    }

    public void setPoDate(Date value) {
        this.poDate = value;
    }

    public Date getEta() {
        return this.eta;
    }

    public void setEta(Date value) {
        this.eta = value;
    }

    public Date getDeliveryTime() {
        return this.deliveryTime;
    }

    public void setDeliveryTime(Date value) {
        this.deliveryTime = value;
    }

    public Double getQtyPlanned() {
        return qtyPlanned;
    }

    public void setQtyPlanned(Double qtyPlanned) {
        this.qtyPlanned = qtyPlanned;
    }

    public Double getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(Double qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }

    public Integer getCtnPlanned() {
        return this.ctnPlanned;
    }

    public void setCtnPlanned(Integer value) {
        this.ctnPlanned = value;
    }

    public Integer getCtnRcvd() {
        return this.ctnRcvd;
    }

    public void setCtnRcvd(Integer value) {
        this.ctnRcvd = value;
    }

    public Long getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(Long value) {
        this.supplierId = value;
    }

    public Integer getAsnType() {
        return this.asnType;
    }

    public void setAsnType(Integer value) {
        this.asnType = value;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer value) {
        this.status = value;
    }

    public Boolean getIsIqc() {
        return isIqc;
    }

    public void setIsIqc(Boolean isIqc) {
        this.isIqc = isIqc;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date value) {
        this.startTime = value;
    }

    public Date getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(Date value) {
        this.stopTime = value;
    }

    public Date getInboundTime() {
        return this.inboundTime;
    }

    public void setInboundTime(Date value) {
        this.inboundTime = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Long getCreatedId() {
        return this.createdId;
    }

    public void setCreatedId(Long value) {
        this.createdId = value;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(Date value) {
        this.lastModifyTime = value;
    }

    public Long getModifiedId() {
        return this.modifiedId;
    }

    public void setModifiedId(Long value) {
        this.modifiedId = value;
    }

    public String getAsnExtCode() {
        return asnExtCode;
    }

    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }

    public Long getPoOuId() {
        return poOuId;
    }

    public void setPoOuId(Long poOuId) {
        this.poOuId = poOuId;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getUrgentStatus() {
        return urgentStatus;
    }

    public void setUrgentStatus(String urgentStatus) {
        this.urgentStatus = urgentStatus;
    }

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public Date getArchivTime() {
        return archivTime;
    }

    public void setArchivTime(Date archivTime) {
        this.archivTime = archivTime;
    }

	public String getOriginalExtOdoCode() {
		return originalExtOdoCode;
	}

	public void setOriginalExtOdoCode(String originalExtOdoCode) {
		this.originalExtOdoCode = originalExtOdoCode;
	}

	public String getOriginalEcOrderCode() {
		return originalEcOrderCode;
	}

	public void setOriginalEcOrderCode(String originalEcOrderCode) {
		this.originalEcOrderCode = originalEcOrderCode;
	}


}
