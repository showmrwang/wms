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
package com.baozun.scm.primservice.whoperation.command.whinterface.inbound;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark
 *
 */
public class WhInboundLineConfirmCommand extends BaseCommand {
	
	private static final long serialVersionUID = 933652040762605378L;
	
	/** id */
	private Long id;
	/** 入库反馈ID */
	private Long inboundConfirmId;
	/** 商品唯一编码 */
	private String upc;
	/** 款式 */
	private String style;
	/** 颜色 */
	private String color;
	/** 尺码 */
	private String size;
	/** 计划数量 */
	private Double qty;
	/** 行唯一标识 */
	private String lineSeq;
	/** 箱号 */
	private String cartonNo;
	/** 入库反馈库存明细 */
	private List<WhInboundInvLineConfirmCommand> whInBoundInvLineConfirmsList;
	
	/** 商品id */
	private Long skuId;
	/** 实际收货数量 */
	private Double actualQty;
	/** 是否质检 */
	private Boolean isIqc;
	
	public WhInboundLineConfirmCommand() {}

	public WhInboundLineConfirmCommand(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInboundConfirmId() {
		return inboundConfirmId;
	}

	public void setInboundConfirmId(Long inboundConfirmId) {
		this.inboundConfirmId = inboundConfirmId;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public String getLineSeq() {
		return lineSeq;
	}

	public void setLineSeq(String lineSeq) {
		this.lineSeq = lineSeq;
	}

	public String getCartonNo() {
		return cartonNo;
	}

	public void setCartonNo(String cartonNo) {
		this.cartonNo = cartonNo;
	}

	public List<WhInboundInvLineConfirmCommand> getWhInBoundInvLineConfirmsList() {
		return whInBoundInvLineConfirmsList;
	}

	public void setWhInBoundInvLineConfirmsList(List<WhInboundInvLineConfirmCommand> whInBoundInvLineConfirmsList) {
		this.whInBoundInvLineConfirmsList = whInBoundInvLineConfirmsList;
	}

	public Double getActualQty() {
		return actualQty;
	}

	public void setActualQty(Double actualQty) {
		this.actualQty = actualQty;
	}

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Boolean getIsIqc() {
		return isIqc;
	}

	public void setIsIqc(Boolean isIqc) {
		this.isIqc = isIqc;
	}

}

