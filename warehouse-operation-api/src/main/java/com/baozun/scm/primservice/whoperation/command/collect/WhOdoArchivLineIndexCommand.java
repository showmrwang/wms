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
package com.baozun.scm.primservice.whoperation.command.collect;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOdoArchivLineIndexCommand extends BaseModel {

    private static final long serialVersionUID = 241134293595577437L;
    
    /** asn_id */
	private Long asnId;
	/** 仓库数据收集库对应数据来源表名 */
	private String collectTableName;
	/** 仓库数据收集库对应数据ID */
	private Long collectOdoArchivLineId;
	/** 仓库组织ID */
	private Long ouId;
	/** 商品ID */
	private Long skuId;
	/** 店铺ID */
	private Long storeId;
	/** 可退货数量 */
	private Double returnedPurchaseQty;
	/** 生产日期 */
	private Date mfgDate;
	/** 失效日期 */
	private Date expDate;
	/** 批次号 */
	private String batchNumber;
	/** 原产地 */
	private String countryOfOrigin;
	/** 库存状态 */
	private Long invStatus;
	/** 库存类型 */
	private String invType;
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
	/** 颜色 */
	private String color;
	/** 款式 */
	private String style;
	/** 尺码 */
	private String size;
	/** SN号 */
	private String sn;
	/** 内部逻辑字段 */
	private String uuid;
	/** 电商平台订单号 */
	private String ecOrderCode;
	/** 数据来源 */
	private String dataSource;
	/** 表序号 */
    private String num;
    /** 出库箱明细id */
    private Long whOutboundboxLineId;
    
	public WhOdoArchivLineIndexCommand() {}

	public WhOdoArchivLineIndexCommand(Long id) {
		this.id = id;
	}

	public void setAsnId(Long asnId) {
		this.asnId = asnId;
	}

	public Long getAsnId() {
		return this.asnId;
	}

	public void setCollectTableName(String collectTableName) {
		this.collectTableName = collectTableName;
	}

	public String getCollectTableName() {
		return this.collectTableName;
	}

	public void setCollectOdoArchivLineId(Long collectOdoArchivLineId) {
		this.collectOdoArchivLineId = collectOdoArchivLineId;
	}

	public Long getCollectOdoArchivLineId() {
		return this.collectOdoArchivLineId;
	}

	public void setOuId(Long ouId) {
		this.ouId = ouId;
	}

	public Long getOuId() {
		return this.ouId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Long getSkuId() {
		return this.skuId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public Long getStoreId() {
		return this.storeId;
	}

	public void setReturnedPurchaseQty(Double returnedPurchaseQty) {
		this.returnedPurchaseQty = returnedPurchaseQty;
	}

	public Double getReturnedPurchaseQty() {
		return this.returnedPurchaseQty;
	}

	public void setMfgDate(Date mfgDate) {
		this.mfgDate = mfgDate;
	}

	public Date getMfgDate() {
		return this.mfgDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public Date getExpDate() {
		return this.expDate;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getBatchNumber() {
		return this.batchNumber;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public String getCountryOfOrigin() {
		return this.countryOfOrigin;
	}

	public void setInvStatus(Long invStatus) {
		this.invStatus = invStatus;
	}

	public Long getInvStatus() {
		return this.invStatus;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public String getInvType() {
		return this.invType;
	}

	public void setInvAttr1(String invAttr1) {
		this.invAttr1 = invAttr1;
	}

	public String getInvAttr1() {
		return this.invAttr1;
	}

	public void setInvAttr2(String invAttr2) {
		this.invAttr2 = invAttr2;
	}

	public String getInvAttr2() {
		return this.invAttr2;
	}

	public void setInvAttr3(String invAttr3) {
		this.invAttr3 = invAttr3;
	}

	public String getInvAttr3() {
		return this.invAttr3;
	}

	public void setInvAttr4(String invAttr4) {
		this.invAttr4 = invAttr4;
	}

	public String getInvAttr4() {
		return this.invAttr4;
	}

	public void setInvAttr5(String invAttr5) {
		this.invAttr5 = invAttr5;
	}

	public String getInvAttr5() {
		return this.invAttr5;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return this.color;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyle() {
		return this.style;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return this.size;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getSn() {
		return this.sn;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getEcOrderCode() {
		return ecOrderCode;
	}

	public void setEcOrderCode(String ecOrderCode) {
		this.ecOrderCode = ecOrderCode;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

    public Long getWhOutboundboxLineId() {
        return whOutboundboxLineId;
    }

    public void setWhOutboundboxLineId(Long whOutboundboxLineId) {
        this.whOutboundboxLineId = whOutboundboxLineId;
    }
}
