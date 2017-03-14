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
package com.baozun.scm.primservice.whoperation.model.whinterface.inbound;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhInboundInvLineConfirm extends BaseModel {

	private static final long serialVersionUID = -6478840870122280825L;
	
	// columns START
	/** 入库反馈ID */
	private Long inboundConfirmLineId;
	/** 实际数量 */
	private Double qtyRcvd;
	/** 库存状态 */
	private String invStatus;
	/** 库存类型 */
	private String invType;
	/** 批次号 */
	private String batchNumber;
	/** 生产日期 */
	private Date mfgDate;
	/** 失效日期 */
	private Date expDate;
	/** 原产地 */
	private String countryOfOrigin;
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
	/** 扩展字段信息 */
	private String extMemo;
	/** 是否质检 默认否 */
	private Boolean isIqc;
	// columns END

	public WhInboundInvLineConfirm() {}

	public WhInboundInvLineConfirm(Long id) {
		this.id = id;
	}

	public Long getInboundConfirmLineId() {
		return inboundConfirmLineId;
	}

	public void setInboundConfirmLineId(Long inboundConfirmLineId) {
		this.inboundConfirmLineId = inboundConfirmLineId;
	}

	public Double getQtyRcvd() {
		return qtyRcvd;
	}

	public void setQtyRcvd(Double qtyRcvd) {
		this.qtyRcvd = qtyRcvd;
	}

	public String getInvStatus() {
		return invStatus;
	}

	public void setInvStatus(String invStatus) {
		this.invStatus = invStatus;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
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

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
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

	public String getExtMemo() {
		return extMemo;
	}

	public void setExtMemo(String extMemo) {
		this.extMemo = extMemo;
	}

	public Boolean getIsIqc() {
		return isIqc;
	}

	public void setIsIqc(Boolean isIqc) {
		this.isIqc = isIqc;
	}

}
