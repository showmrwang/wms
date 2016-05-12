/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.model.poasn;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author larkark
 *
 */
public class BiPo extends BaseModel {
	
	
    /**
     * 
     */
    private static final long serialVersionUID = -5065027361218381811L;
    // columns START
	/** PO单号 */
	private String poCode;
	/** 相关单据号 */
	private String extCode;
	/** 客户ID */
	private Long customerId;
	/** 店铺ID */
	private Long storeId;
	/** 供应商ID */
	private Long supplierId;
	/** 运输商ID */
	private Long logisticsProviderId;
	/** PO单类型 */
	private Integer poType;
	/** 状态 */
	private Integer status;
	/** 是否质检 1:是 0:否 */
	private Boolean isIqc;
	/** 采购时间 */
	private Date poDate;
	/** 计划到货时间 */
	private Date eta;
	/** 实际到货时间 */
	private Date deliveryTime;
	/** 计划到货数量 */
    private Double qtyPlanned = 0.0d;
	/** 实际到货数量 */
    private Double qtyRcvd = 0.0d;
	/** 计划箱数 */
    private Integer ctnPlanned = 0;
	/** 实际箱数 */
    private Integer ctnRcvd = 0;
	/** 开始收货时间 */
	private Date startTime;
	/** 结束收货时间 */
	private Date stopTime;
	/** 上架完成时间 */
	private Date inboundTime;
	/** 是否WMS创建 1:是 0:否 */
	private Boolean isWms;
	/** 是否VMI收货单 1:是 0:否 */
	private Boolean isVmi;
	/** 创建时间 */
	private Date createTime;
	/** 创建人 */
	private Long createdId;
	/** 修改时间 */
	private Date lastModifyTime;
	/** 操作人ID */
	private Long modifiedId;
    /** 用于标识创单时有OUID时有此字段 */
    private Long ouId;
	//columns END
    public String getPoCode() {
        return poCode;
    }
    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }
    public String getExtCode() {
        return extCode;
    }
    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Long getStoreId() {
        return storeId;
    }
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    public Long getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    public Long getLogisticsProviderId() {
        return logisticsProviderId;
    }
    public void setLogisticsProviderId(Long logisticsProviderId) {
        this.logisticsProviderId = logisticsProviderId;
    }
    public Integer getPoType() {
        return poType;
    }
    public void setPoType(Integer poType) {
        this.poType = poType;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Boolean getIsIqc() {
        return isIqc;
    }
    public void setIsIqc(Boolean isIqc) {
        this.isIqc = isIqc;
    }
    public Date getPoDate() {
        return poDate;
    }
    public void setPoDate(Date poDate) {
        this.poDate = poDate;
    }
    public Date getEta() {
        return eta;
    }
    public void setEta(Date eta) {
        this.eta = eta;
    }
    public Date getDeliveryTime() {
        return deliveryTime;
    }
    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
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
        return ctnPlanned;
    }
    public void setCtnPlanned(Integer ctnPlanned) {
        this.ctnPlanned = ctnPlanned;
    }
    public Integer getCtnRcvd() {
        return ctnRcvd;
    }
    public void setCtnRcvd(Integer ctnRcvd) {
        this.ctnRcvd = ctnRcvd;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getStopTime() {
        return stopTime;
    }
    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
    public Date getInboundTime() {
        return inboundTime;
    }
    public void setInboundTime(Date inboundTime) {
        this.inboundTime = inboundTime;
    }
    public Boolean getIsWms() {
        return isWms;
    }
    public void setIsWms(Boolean isWms) {
        this.isWms = isWms;
    }
    public Boolean getIsVmi() {
        return isVmi;
    }
    public void setIsVmi(Boolean isVmi) {
        this.isVmi = isVmi;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Long getCreatedId() {
        return createdId;
    }
    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
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

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
	
	
}

