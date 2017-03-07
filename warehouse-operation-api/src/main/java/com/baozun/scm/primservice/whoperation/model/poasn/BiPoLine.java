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
public class BiPoLine extends BaseModel {
	
    /**
     * 
     */
    private static final long serialVersionUID = -2391507028250772133L;
	
	//columns START
	/** PO单ID */
    private Long poId;
	/** PO行号 */
    private Integer linenum;
    /** 外部单据行号 */
    private String extLineNum;
	/** SKU_ID */
    private Long skuId;
	/** 计划数量 */
    private Double qtyPlanned = 0.0d;
	/** 超收数量 */
    private Double overshipped = 0.0d;
	/** 可用数量 */
    private Double availableQty = 0.0d;
	/** 计划箱数 */
    private Integer ctnPlanned = 0;
	/** 已收数量 */
    private Double qtyRcvd = 0.0d;
	/** 已收箱数 */
    private Integer ctnRcvd = 0;
	/** 状态 */
    private Integer status;
	/** 是否质检 1:是 0:否 */
    private Boolean isIqc;
	/** 生产日期 */
    private Date mfgDate;
	/** 失效日期 */
    private Date expDate;
	/** 有效期天数 */
    private Integer validDate;
    /** 箱号 */
    private String cartonNo;
	/** 批次号 */
    private String batchNo;
	/** 原产地 */
    private String countryOfOrigin;
	/** 库存状态 */
    private Long invStatus;
	/** 库存属性1 */
    private String invAttr1;
	/** 库存属性2 */
    private String invAttr2;
	/** 库存属性3 */
    private String invAttr3;
	/** 库存属性4 */
    private String invAttr4;
	/** 库存属性5 */
    private String invAttr5;
	/** 创建时间 */
    private Date createTime;
	/** 创建人ID */
    private Long createdId;
	/** 最好修改时间 */
    private Date lastModifyTime;
	/** 操作人ID */
    private Long modifiedId;
	/** 临时数据批次 IT使用 */
    private String uuid;
	/** POLINE明细ID IT逻辑用 */
    private Long poLineId;
	/** invType */
    private String invType;
	/** validDateUom */
    private String validDateUom;
	//columns END
    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public Integer getLinenum() {
        return linenum;
    }

    public void setLinenum(Integer linenum) {
        this.linenum = linenum;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }


    public Integer getCtnPlanned() {
        return ctnPlanned;
    }

    public void setCtnPlanned(Integer ctnPlanned) {
        this.ctnPlanned = ctnPlanned;
    }


    public Double getQtyPlanned() {
        return qtyPlanned;
    }

    public void setQtyPlanned(Double qtyPlanned) {
        this.qtyPlanned = qtyPlanned;
    }

    public Double getOvershipped() {
        return overshipped;
    }

    public void setOvershipped(Double overshipped) {
        this.overshipped = overshipped;
    }

    public Double getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Double availableQty) {
        this.availableQty = availableQty;
    }

    public Double getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(Double qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }

    public Integer getCtnRcvd() {
        return ctnRcvd;
    }

    public void setCtnRcvd(Integer ctnRcvd) {
        this.ctnRcvd = ctnRcvd;
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

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Integer getValidDate() {
        return validDate;
    }

    public void setValidDate(Integer validDate) {
        this.validDate = validDate;
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

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvAttr1() {
        return invAttr1;
    }

    public void setInvAttr1(String invAttr1) {
        this.invAttr1 = invAttr1;
    }

    public String getInvAttr2() {
        return invAttr2;
    }

    public void setInvAttr2(String invAttr2) {
        this.invAttr2 = invAttr2;
    }

    public String getInvAttr3() {
        return invAttr3;
    }

    public void setInvAttr3(String invAttr3) {
        this.invAttr3 = invAttr3;
    }

    public String getInvAttr4() {
        return invAttr4;
    }

    public void setInvAttr4(String invAttr4) {
        this.invAttr4 = invAttr4;
    }

    public String getInvAttr5() {
        return invAttr5;
    }

    public void setInvAttr5(String invAttr5) {
        this.invAttr5 = invAttr5;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getPoLineId() {
        return poLineId;
    }

    public void setPoLineId(Long poLineId) {
        this.poLineId = poLineId;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getValidDateUom() {
        return validDateUom;
    }

    public void setValidDateUom(String validDateUom) {
        this.validDateUom = validDateUom;
    }

	public String getExtLineNum() {
		return extLineNum;
	}

	public void setExtLineNum(String extLineNum) {
		this.extLineNum = extLineNum;
	}

	public String getCartonNo() {
		return cartonNo;
	}

	public void setCartonNo(String cartonNo) {
		this.cartonNo = cartonNo;
	}



}

