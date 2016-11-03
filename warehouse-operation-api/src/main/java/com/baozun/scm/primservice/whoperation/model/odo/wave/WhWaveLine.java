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
package com.baozun.scm.primservice.whoperation.model.odo.wave;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class WhWaveLine extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 79848539176778509L;
    /** 波次ID */
    private Long waveId;
    /** 出库单明细行ID */
    private Long odoLineId;
    /** 出库单ID */
    private Long odoId;
    /** 出库单号 */
    private String odoCode;
    /** 出库单优先级 */
    private Integer odoPriorityLevel;
    /** 出库单计划发货时间 */
    private Date odoPlanDeliverGoodsTime;
    /** 出库单下单时间 */
    private Date odoOrderTime;
    /** 分配规则ID */
    private Long allocateRuleId;
    /** 行号 */
    private Integer linenum;
    /** 店铺ID */
    private Long storeId;
    /** 外部单据行号 */
    private Integer extLinenum;
    /** 商品ID */
    private Long skuId;
    /** 商品条码 */
    private String skuBarCode;
    /** 商品名称 */
    private String skuName;
    /** 上位系统商品名称 */
    private String extSkuName;
    /** 数量 */
    private Double qty;
    /** 分配数量 */
    private Double allocateQty;
    /** 是否整单出库 */
    private Boolean isWholeOrderOutbound;
    /** 整行出库标志 */
    private Boolean fullLineOutbound;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 最小失效日期 */
    private Date minExpDate;
    /** 最大失效日期 */
    private Date maxExpDate;
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
    /** 出库箱类型 */
    private Long outboundCartonType;
    /** 颜色 */
    private String color;
    /** 款式 */
    private String style;
    /** 尺码 */
    private String size;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 是否占用整托/整箱 */
    private Boolean isPalletContainer;
    /** 整托整箱占用数量 */
    private Double palletContainerQty;
    /** 是否静态库位可超分配 */
    private Boolean isStaticLocationAllocate;
    /** 被占用的静态库位ID */
    private String staticLocationIds;
    /** 分配区域ID */
    private Long areaId;
    /** 整托占用IDS */
    private String trayIds;
    /** 整箱占用IDS */
    private String packingCaseIds;
    
    public Double getAllocateQty() {
        return allocateQty;
    }

    public void setAllocateQty(Double allocateQty) {
        this.allocateQty = allocateQty;
    }

    public Long getWaveId() {
        return this.waveId;
    }

    public void setWaveId(Long value) {
        this.waveId = value;
    }

    public Long getOdoLineId() {
        return this.odoLineId;
    }

    public void setOdoLineId(Long value) {
        this.odoLineId = value;
    }

    public Long getOdoId() {
        return this.odoId;
    }

    public void setOdoId(Long value) {
        this.odoId = value;
    }

    public String getOdoCode() {
        return this.odoCode;
    }

    public void setOdoCode(String value) {
        this.odoCode = value;
    }

    public Integer getOdoPriorityLevel() {
        return this.odoPriorityLevel;
    }

    public void setOdoPriorityLevel(Integer value) {
        this.odoPriorityLevel = value;
    }

    public Date getOdoPlanDeliverGoodsTime() {
        return this.odoPlanDeliverGoodsTime;
    }

    public void setOdoPlanDeliverGoodsTime(Date value) {
        this.odoPlanDeliverGoodsTime = value;
    }

    public Date getOdoOrderTime() {
        return this.odoOrderTime;
    }

    public void setOdoOrderTime(Date value) {
        this.odoOrderTime = value;
    }

    public Long getAllocateRuleId() {
        return this.allocateRuleId;
    }

    public void setAllocateRuleId(Long value) {
        this.allocateRuleId = value;
    }

    public Integer getLinenum() {
        return this.linenum;
    }

    public void setLinenum(Integer value) {
        this.linenum = value;
    }

    public Long getStoreId() {
        return this.storeId;
    }

    public void setStoreId(Long value) {
        this.storeId = value;
    }

    public Integer getExtLinenum() {
        return this.extLinenum;
    }

    public void setExtLinenum(Integer value) {
        this.extLinenum = value;
    }

    public Long getSkuId() {
        return this.skuId;
    }

    public void setSkuId(Long value) {
        this.skuId = value;
    }

    public String getSkuBarCode() {
        return this.skuBarCode;
    }

    public void setSkuBarCode(String value) {
        this.skuBarCode = value;
    }

    public String getSkuName() {
        return this.skuName;
    }

    public void setSkuName(String value) {
        this.skuName = value;
    }

    public String getExtSkuName() {
        return this.extSkuName;
    }

    public void setExtSkuName(String value) {
        this.extSkuName = value;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Boolean getIsWholeOrderOutbound() {
        return this.isWholeOrderOutbound;
    }

    public void setIsWholeOrderOutbound(Boolean value) {
        this.isWholeOrderOutbound = value;
    }

    public Boolean getFullLineOutbound() {
        return this.fullLineOutbound;
    }

    public void setFullLineOutbound(Boolean value) {
        this.fullLineOutbound = value;
    }

    public Date getMfgDate() {
        return this.mfgDate;
    }

    public void setMfgDate(Date value) {
        this.mfgDate = value;
    }

    public Date getExpDate() {
        return this.expDate;
    }

    public void setExpDate(Date value) {
        this.expDate = value;
    }

    public Date getMinExpDate() {
        return this.minExpDate;
    }

    public void setMinExpDate(Date value) {
        this.minExpDate = value;
    }

    public Date getMaxExpDate() {
        return this.maxExpDate;
    }

    public void setMaxExpDate(Date value) {
        this.maxExpDate = value;
    }

    public String getBatchNumber() {
        return this.batchNumber;
    }

    public void setBatchNumber(String value) {
        this.batchNumber = value;
    }

    public String getCountryOfOrigin() {
        return this.countryOfOrigin;
    }

    public void setCountryOfOrigin(String value) {
        this.countryOfOrigin = value;
    }

    public Long getInvStatus() {
        return this.invStatus;
    }

    public void setInvStatus(Long value) {
        this.invStatus = value;
    }

    public String getInvType() {
        return this.invType;
    }

    public void setInvType(String value) {
        this.invType = value;
    }

    public String getInvAttr1() {
        return this.invAttr1;
    }

    public void setInvAttr1(String value) {
        this.invAttr1 = value;
    }

    public String getInvAttr2() {
        return this.invAttr2;
    }

    public void setInvAttr2(String value) {
        this.invAttr2 = value;
    }

    public String getInvAttr3() {
        return this.invAttr3;
    }

    public void setInvAttr3(String value) {
        this.invAttr3 = value;
    }

    public String getInvAttr4() {
        return this.invAttr4;
    }

    public void setInvAttr4(String value) {
        this.invAttr4 = value;
    }

    public String getInvAttr5() {
        return this.invAttr5;
    }

    public void setInvAttr5(String value) {
        this.invAttr5 = value;
    }

    public Long getOutboundCartonType() {
        return this.outboundCartonType;
    }

    public void setOutboundCartonType(Long value) {
        this.outboundCartonType = value;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String value) {
        this.color = value;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String value) {
        this.style = value;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String value) {
        this.size = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
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

	public Boolean getIsPalletContainer() {
		return isPalletContainer;
	}

	public void setIsPalletContainer(Boolean isPalletContainer) {
		this.isPalletContainer = isPalletContainer;
	}

	public Double getPalletContainerQty() {
		return palletContainerQty;
	}

	public void setPalletContainerQty(Double palletContainerQty) {
		this.palletContainerQty = palletContainerQty;
	}

	public Boolean getIsStaticLocationAllocate() {
		return isStaticLocationAllocate;
	}

	public void setIsStaticLocationAllocate(Boolean isStaticLocationAllocate) {
		this.isStaticLocationAllocate = isStaticLocationAllocate;
	}

	public String getStaticLocationIds() {
		return staticLocationIds;
	}

	public void setStaticLocationIds(String staticLocationIds) {
		this.staticLocationIds = staticLocationIds;
	}

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getTrayIds() {
		return trayIds;
	}

	public void setTrayIds(String trayIds) {
		this.trayIds = trayIds;
	}

	public String getPackingCaseIds() {
		return packingCaseIds;
	}

	public void setPackingCaseIds(String packingCaseIds) {
		this.packingCaseIds = packingCaseIds;
	}

}
