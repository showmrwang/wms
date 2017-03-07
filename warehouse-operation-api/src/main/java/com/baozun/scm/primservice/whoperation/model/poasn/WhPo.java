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
 * 采购单
 * 
 * @author larkark
 * 
 */
public class WhPo extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = 3128949844364994933L;
    /** PO单号 */
    private String poCode;
    /** 相关单据号 */
    private String extCode;
    /** 上位系统入库单据编码 */
    private String extPoCode;
    /** 对应组织ID */
    private Long ouId;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 供应商ID */
    private Long supplierId;
    /** 来源地 */
	private String fromLocation;
	/** 目的地 */
	private String toLocation;
    /** 运输商ID */
    private Long logisticsProviderId;
    /** 运输商Code */
    private String logisticsProvider;
    /** PO单类型 */
    private Integer poType;
    /** 上位系统单据类型 */
	private String extPoType;
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
    private Double qtyPlanned = 0.0;
    /** 实际到货数量 */
    private Double qtyRcvd = 0.0;
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
    /** 数据来源 区分上位系统 */
	private String dataSource;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 用于保存拆分PO的临时数据 */
    private String uuid;
    /** 超收比例 */
    private Double overChageRate;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPoCode() {
        return this.poCode;
    }

    public void setPoCode(String value) {
        this.poCode = value;
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

    public Integer getPoType() {
        return this.poType;
    }

    public void setPoType(Integer value) {
        this.poType = value;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer value) {
        this.status = value;
    }

    public Boolean getIsIqc() {
        return this.isIqc;
    }

    public void setIsIqc(Boolean value) {
        this.isIqc = value;
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

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
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

    public Double getOverChageRate() {
        return overChageRate;
    }

    public void setOverChageRate(Double overChageRate) {
        this.overChageRate = overChageRate;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public String getExtPoType() {
		return extPoType;
	}

	public void setExtPoType(String extPoType) {
		this.extPoType = extPoType;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getExtPoCode() {
		return extPoCode;
	}

	public void setExtPoCode(String extPoCode) {
		this.extPoCode = extPoCode;
	}


}
