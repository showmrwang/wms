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

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark
 *
 */
public class WhOdoArchivLineIndexCommand extends BaseCommand {

    private static final long serialVersionUID = 241134293595577437L;
    
    private Long id;
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
    
    /** 生产日期 */
    private String mfgDateStr;
    /** 失效日期 */
    private String expDateStr;
    /** 已收数量 */
    private Integer qtyRcvd;


    // ---显示
    /** 库存类型名称 */
    private String invTypeLabel;
    /** 库存状态名称 */
    private String invName;
    /** 库存属性1 */
    private String inv1Str;
    /** 库存属性2 */
    private String inv2Str;
    /** 库存属性3 */
    private String inv3Str;
    /** 库存属性4 */
    private String inv4Str;
    /** 库存属性5 */
    private String inv5Str;
    /** 商品编码 */
    private String skuCode;
    /** 商品名称 */
    private String skuName;
    /** 商品对接码 */
    private String skuExtCode;
    /** 商品条码 */
    private String skuBarCode;



    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuExtCode() {
        return skuExtCode;
    }

    public void setSkuExtCode(String skuExtCode) {
        this.skuExtCode = skuExtCode;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getInvTypeLabel() {
        return invTypeLabel;
    }

    public void setInvTypeLabel(String invTypeLabel) {
        this.invTypeLabel = invTypeLabel;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getInv1Str() {
        return inv1Str;
    }

    public void setInv1Str(String inv1Str) {
        this.inv1Str = inv1Str;
    }

    public String getInv2Str() {
        return inv2Str;
    }

    public void setInv2Str(String inv2Str) {
        this.inv2Str = inv2Str;
    }

    public String getInv3Str() {
        return inv3Str;
    }

    public void setInv3Str(String inv3Str) {
        this.inv3Str = inv3Str;
    }

    public String getInv4Str() {
        return inv4Str;
    }

    public void setInv4Str(String inv4Str) {
        this.inv4Str = inv4Str;
    }

    public String getInv5Str() {
        return inv5Str;
    }

    public void setInv5Str(String inv5Str) {
        this.inv5Str = inv5Str;
    }

    public Integer getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(Integer qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }

    public String getMfgDateStr() {
        return mfgDateStr;
    }

    public void setMfgDateStr(String mfgDateStr) {
        this.mfgDateStr = mfgDateStr;
    }

    public String getExpDateStr() {
        return expDateStr;
    }

    public void setExpDateStr(String expDateStr) {
        this.expDateStr = expDateStr;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
