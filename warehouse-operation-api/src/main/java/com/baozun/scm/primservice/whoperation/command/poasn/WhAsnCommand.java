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
package com.baozun.scm.primservice.whoperation.command.poasn;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class WhAsnCommand extends BaseCommand {

    private static final long serialVersionUID = -3374771897459764303L;

    /** 主键ID */
    private Long id;
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
    private Double qtyPlanned;
    /** 实际数量 */
    private Double qtyRcvd;
    /** 计划箱数 */
    private Integer ctnPlanned;
    /** 实际箱数 */
    private Integer ctnRcvd;
    /** 供应商ID */
    private Long supplierId;
    /** 运输商ID */
    private Long logisticsProviderId;
    /** 运输商Code */
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
    /** 对应PO单相关编码 */
    private String extCode;

    /** asnid list */
    private List<Long> asnIds;
    /** 客户名称 */
    private String customerName;
    /** 店铺名称 */
    private String storeName;
    /** ASN状态名称 */
    private String statusName;
    /** po单据号 */
    private String poCode;

    /** 操作员工ID */
    private Long userId;
    /** 采购时间 */
    private String poDateStr;
    /** 计划到货时间 */
    private String etaStr;

    /** 仓库名称 */
    private String ouName;
    /** 单据类型名称 */
    private String poTypeName;
    /** 是否 */
    private String isIqcName;
    /** 供应商ID */
    private String supplierName;
    /** 运输商ID */
    private String logisticsProviderName;
    /** 收货开始时间字符串类型 */
    private String startTimeStr;
    /** 收货结束时间字符串类型 */
    private String stopTimeStr;
    /** 上架完成时间字符串类型 */
    private String inboundTimeStr;

    private List<WhAsnLineCommand> asnLineList;
    /** po单对应的ou_id */
    private Long poOuId;
    /** 紧急状态 */
    private String urgentStatus;
    /** 超收比例 */
    private Double overChageRate;
    /** IT用uuid */
    private String uuid;
    /** 是否自动关单 */
    private Boolean isAutoClose;

    // ---------------------------------------------------------------------
    // 退货单查询新增字段
    private String trackingNumber;

    private String senderTargetName;

    private String phoneNumber;

    private String senderTargetAddress;

    private String barCode;

    private String createTimeStart;

    private String createTimeEnd;

    private String isSearch;// 是否检索

    // -----------------------------------------------------------------------

    public Boolean getIsAutoClose() {
        return isAutoClose;
    }

    public String getIsSearch() {
        return isSearch;
    }

    public void setIsSearch(String isSearch) {
        this.isSearch = isSearch;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getSenderTargetName() {
        return senderTargetName;
    }

    public void setSenderTargetName(String senderTargetName) {
        this.senderTargetName = senderTargetName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSenderTargetAddress() {
        return senderTargetAddress;
    }

    public void setSenderTargetAddress(String senderTargetAddress) {
        this.senderTargetAddress = senderTargetAddress;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public void setIsAutoClose(Boolean isAutoClose) {
        this.isAutoClose = isAutoClose;
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

    public List<WhAsnLineCommand> getAsnLineList() {
        return asnLineList;
    }

    public void setAsnLineList(List<WhAsnLineCommand> asnLineList) {
        this.asnLineList = asnLineList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
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

    public Long getLogisticsProviderId() {
        return this.logisticsProviderId;
    }

    public void setLogisticsProviderId(Long value) {
        this.logisticsProviderId = value;
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

    public List<Long> getAsnIds() {
        return asnIds;
    }

    public void setAsnIds(List<Long> asnIds) {
        this.asnIds = asnIds;
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

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getAsnExtCode() {
        return asnExtCode;
    }

    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }

    public String getPoDateStr() {
        return poDateStr;
    }

    public void setPoDateStr(String poDateStr) {
        this.poDateStr = poDateStr;
    }

    public String getEtaStr() {
        return etaStr;
    }

    public void setEtaStr(String etaStr) {
        this.etaStr = etaStr;
    }

    public String getOuName() {
        return ouName;
    }

    public void setOuName(String ouName) {
        this.ouName = ouName;
    }

    public String getPoTypeName() {
        return poTypeName;
    }

    public void setPoTypeName(String poTypeName) {
        this.poTypeName = poTypeName;
    }

    public String getIsIqcName() {
        return isIqcName;
    }

    public void setIsIqcName(String isIqcName) {
        this.isIqcName = isIqcName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getLogisticsProviderName() {
        return logisticsProviderName;
    }

    public void setLogisticsProviderName(String logisticsProviderName) {
        this.logisticsProviderName = logisticsProviderName;
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

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getStopTimeStr() {
        return stopTimeStr;
    }

    public void setStopTimeStr(String stopTimeStr) {
        this.stopTimeStr = stopTimeStr;
    }

    public String getInboundTimeStr() {
        return inboundTimeStr;
    }

    public void setInboundTimeStr(String inboundTimeStr) {
        this.inboundTimeStr = inboundTimeStr;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
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
