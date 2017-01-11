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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark 物理仓信息表
 */
public class Warehouse extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 5058064306259260991L;


    /** 仓库编码 */
    private String code;
    /** 仓库名称 */
    private String name;
    /** 运营模式(待定) */
    private Integer opMode;
    /** 管理模式(待定) */
    private Integer manageMode;
    /** 联系人 */
    private String pic;
    /** 联系人电话 */
    private String picContact;
    /** 仓库电话 */
    private String phone;
    /** 仓库传真 */
    private String fax;
    /** 其他联系人和方法1 */
    private String otherContact1;
    /** 其他联系人和方法2 */
    private String otherContact2;
    /** 其他联系人和方法3 */
    private String otherContact3;
    /** 国家ID */
    private Long countryId;
    /** 省ID */
    private Long provinceId;
    /** 市ID */
    private Long cityId;
    /** 详细地址 */
    private String address;
    /** 邮政编码 */
    private String zipCode;
    /** 仓库面积(m²) */
    private Double size;
    /** 面积尺寸单位 */
    private String sizeUom;
    /** 租金单价 */
    private Double rentPrice;
    /** 货币单位 */
    private String rentPriceUom;
    /** 仓库描述 */
    private String description;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人id */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    /** 是否强制预约 0:否 1:是 */
    private Boolean isMandatorilyReserved = false;
    /** 是否PO超收 0:否 1:是 */
    private Boolean isPoOvercharge = false;
    /** PO超收比例 */
    private Integer poOverchargeProportion;
    /** 是否ASN超收 0:否 1:是 */
    private Boolean isAsnOvercharge = false;
    /** asn超收比例 */
    private Integer asnOverchargeProportion;
    /** 是否自动审核PO */
    private Boolean isPoAutoVerify = false;
    /** 是否自动审核ASN */
    private Boolean isAsnAutoVerify = false;
    /** 预收货模式 1-总数 2-总箱数 3-商品数量 */
    private Integer goodsReceiptMode = 1;
    /** 收货是否自动打印箱标签 */
    private Boolean isAutoPrintBintag = false;
    /** 收货是否自动生成箱号 */
    private Boolean isAutoGenerationCn = false;
    /** 是否允许越库 */
    private Boolean isAllowBlocked = false;
    /** 容器内sku是否需要扫描 */
    private Boolean isSkuNeedsScan = false;
    /** 库存属性管理 */
    private String invAttrMgmt;
    /** 是否自动打印收货差异清单 */
    private Boolean isAutoPrintDiff = false;
    /** 入库是否提示质检 */
    private Boolean isHintQualityTesting = false;
    /** 是否自动打印预收货交接清单 */
    private Boolean isAutoPrintGoodsReceipt = false;
    /** 是否自动打印月台标签 */
    private Boolean isAutoPrintPlatformtag = false;
    /** SKU严重混放数量 */
    private Integer skuMixNumber;
    

    /** 用户分拣是否共享目标容器 */
    private Boolean isSortationContainerAssign = false;
    /** 是否允许多次出库 */
    private Boolean isRepeatedlyOutbound = false;
    /** 在库存日志是否记录交易前后库存总数 */
    private Boolean isTabbInvTotal = false;

    
    /** 上架是否启用校验码0：否 1：是 */
    private Boolean isInboundLocationBarcode = false;
    
    /** 长度默认单位类型 */
    private Long defaultLengthUomType;
    /** 重量默认单位类型 */
    private Long defaultWeightUomType;
    /** 体积默认单位类型 */
    private Long defaultVolumeUomType;
    /** 面积默认单位类型 */
    private Long defaultAreaUomType;

    
    /** 拣货出现差异是否生成盘点任务 */
    private Boolean isGenerateInventoryTask;
    

    /** 配货模式计算 */
    private Boolean isCalcSeckill;
    private Integer seckillOdoQtys;
    private Boolean isCalcTwoSkuSuit;
    private Integer twoSkuSuitOdoQtys;
    private Integer suitsMaxSkuCategorys;
    private Boolean isCalcSuits;
    private Integer suitsOdoQtys;
    /** 是否应用复核台和播种墙推荐逻辑 */
	private Boolean isApplyFacility;
	/** 拣货是否提示复核台信息 */
	private Boolean isDiekingCheckMessage;
	/** 拣货是否提示播种墙信息 */
	private Boolean isDiekingSeedingwallMessage;
	/** 是否校验设施校验码 */
	private Boolean isFacilityCheckCode;
	/** 播种模式 */
	private String seedingMode;
	/** 播种墙行数 */
	private Integer xqty;
	/** 播种墙列数 */
	private Integer yqty;
	/** z列数量 */
	private Integer zqty;
	/** 播种墙对应单据数 */
	private Integer seedingOdoQty;


    public Integer getSuitsMaxSkuCategorys() {
        return suitsMaxSkuCategorys;
    }

    public void setSuitsMaxSkuCategorys(Integer suitsMaxSkuCategorys) {
        this.suitsMaxSkuCategorys = suitsMaxSkuCategorys;
    }

    public Boolean getIsCalcSeckill() {
        return isCalcSeckill;
    }

    public void setIsCalcSeckill(Boolean isCalcSeckill) {
        this.isCalcSeckill = isCalcSeckill;
    }

    public Integer getSeckillOdoQtys() {
        return seckillOdoQtys;
    }

    public void setSeckillOdoQtys(Integer seckillOdoQtys) {
        this.seckillOdoQtys = seckillOdoQtys;
    }

    public Boolean getIsCalcTwoSkuSuit() {
        return isCalcTwoSkuSuit;
    }

    public void setIsCalcTwoSkuSuit(Boolean isCalcTwoSkuSuit) {
        this.isCalcTwoSkuSuit = isCalcTwoSkuSuit;
    }

    public Integer getTwoSkuSuitOdoQtys() {
        return twoSkuSuitOdoQtys;
    }

    public void setTwoSkuSuitOdoQtys(Integer twoSkuSuitOdoQtys) {
        this.twoSkuSuitOdoQtys = twoSkuSuitOdoQtys;
    }

    public Boolean getIsCalcSuits() {
        return isCalcSuits;
    }

    public void setIsCalcSuits(Boolean isCalcSuits) {
        this.isCalcSuits = isCalcSuits;
    }

    public Integer getSuitsOdoQtys() {
        return suitsOdoQtys;
    }

    public void setSuitsOdoQtys(Integer suitsOdoQtys) {
        this.suitsOdoQtys = suitsOdoQtys;
    }

    public Boolean getIsGenerateInventoryTask() {
        return isGenerateInventoryTask;
    }

    public void setIsGenerateInventoryTask(Boolean isGenerateInventoryTask) {
        this.isGenerateInventoryTask = isGenerateInventoryTask;
    }
    
    
    
    public Long getDefaultLengthUomType() {
        return defaultLengthUomType;
    }

    public void setDefaultLengthUomType(Long defaultLengthUomType) {
        this.defaultLengthUomType = defaultLengthUomType;
    }

    public Long getDefaultWeightUomType() {
        return defaultWeightUomType;
    }

    public void setDefaultWeightUomType(Long defaultWeightUomType) {
        this.defaultWeightUomType = defaultWeightUomType;
    }

    public Long getDefaultVolumeUomType() {
        return defaultVolumeUomType;
    }

    public void setDefaultVolumeUomType(Long defaultVolumeUomType) {
        this.defaultVolumeUomType = defaultVolumeUomType;
    }

    public Long getDefaultAreaUomType() {
        return defaultAreaUomType;
    }

    public void setDefaultAreaUomType(Long defaultAreaUomType) {
        this.defaultAreaUomType = defaultAreaUomType;
    }

    
    
    
    

    public Boolean getIsSortationContainerAssign() {
        return isSortationContainerAssign;
    }

    public void setIsSortationContainerAssign(Boolean isSortationContainerAssign) {
        this.isSortationContainerAssign = isSortationContainerAssign;
    }

    public Boolean getIsRepeatedlyOutbound() {
        return isRepeatedlyOutbound;
    }

    public void setIsRepeatedlyOutbound(Boolean isRepeatedlyOutbound) {
        this.isRepeatedlyOutbound = isRepeatedlyOutbound;
    }

    public Boolean getIsTabbInvTotal() {
        return isTabbInvTotal;
    }

    public void setIsTabbInvTotal(Boolean isTabbInvTotal) {
        this.isTabbInvTotal = isTabbInvTotal;
    }
    

    public void setCode(String value) {
        this.code = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }

    public void setOpMode(Integer value) {
        this.opMode = value;
    }

    public Integer getOpMode() {
        return this.opMode;
    }

    public void setManageMode(Integer value) {
        this.manageMode = value;
    }

    public Integer getManageMode() {
        return this.manageMode;
    }

    public void setPic(String value) {
        this.pic = value;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPicContact(String value) {
        this.picContact = value;
    }

    public String getPicContact() {
        return this.picContact;
    }

    public void setPhone(String value) {
        this.phone = value;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setFax(String value) {
        this.fax = value;
    }

    public String getFax() {
        return this.fax;
    }

    public void setOtherContact1(String value) {
        this.otherContact1 = value;
    }

    public String getOtherContact1() {
        return this.otherContact1;
    }

    public void setOtherContact2(String value) {
        this.otherContact2 = value;
    }

    public String getOtherContact2() {
        return this.otherContact2;
    }

    public void setOtherContact3(String value) {
        this.otherContact3 = value;
    }

    public String getOtherContact3() {
        return this.otherContact3;
    }

    public void setCountryId(Long value) {
        this.countryId = value;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public void setProvinceId(Long value) {
        this.provinceId = value;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setCityId(Long value) {
        this.cityId = value;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setAddress(String value) {
        this.address = value;
    }

    public String getAddress() {
        return this.address;
    }

    public void setZipCode(String value) {
        this.zipCode = value;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setSizeUom(String value) {
        this.sizeUom = value;
    }

    public String getSizeUom() {
        return this.sizeUom;
    }

    public void setRentPriceUom(String value) {
        this.rentPriceUom = value;
    }

    public String getRentPriceUom() {
        return this.rentPriceUom;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(Date value) {
        this.lastModifyTime = value;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setOperatorId(Long value) {
        this.operatorId = value;
    }

    public Long getOperatorId() {
        return this.operatorId;
    }

    public void setLifecycle(Integer value) {
        this.lifecycle = value;
    }

    public Integer getLifecycle() {
        return this.lifecycle;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(Double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Boolean getIsMandatorilyReserved() {
        return isMandatorilyReserved;
    }

    public void setIsMandatorilyReserved(Boolean isMandatorilyReserved) {
        this.isMandatorilyReserved = isMandatorilyReserved;
    }

    public Boolean getIsPoOvercharge() {
        return isPoOvercharge;
    }

    public void setIsPoOvercharge(Boolean isPoOvercharge) {
        this.isPoOvercharge = isPoOvercharge;
    }

    public Integer getPoOverchargeProportion() {
        return poOverchargeProportion;
    }

    public void setPoOverchargeProportion(Integer poOverchargeProportion) {
        this.poOverchargeProportion = poOverchargeProportion;
    }

    public Boolean getIsAsnOvercharge() {
        return isAsnOvercharge;
    }

    public void setIsAsnOvercharge(Boolean isAsnOvercharge) {
        this.isAsnOvercharge = isAsnOvercharge;
    }

    public Integer getAsnOverchargeProportion() {
        return asnOverchargeProportion;
    }

    public void setAsnOverchargeProportion(Integer asnOverchargeProportion) {
        this.asnOverchargeProportion = asnOverchargeProportion;
    }

    public Boolean getIsPoAutoVerify() {
        return isPoAutoVerify;
    }

    public void setIsPoAutoVerify(Boolean isPoAutoVerify) {
        this.isPoAutoVerify = isPoAutoVerify;
    }

    public Boolean getIsAsnAutoVerify() {
        return isAsnAutoVerify;
    }

    public void setIsAsnAutoVerify(Boolean isAsnAutoVerify) {
        this.isAsnAutoVerify = isAsnAutoVerify;
    }

    public Integer getGoodsReceiptMode() {
        return goodsReceiptMode;
    }

    public void setGoodsReceiptMode(Integer goodsReceiptMode) {
        this.goodsReceiptMode = goodsReceiptMode;
    }

    public Boolean getIsAutoPrintBintag() {
        return isAutoPrintBintag;
    }

    public void setIsAutoPrintBintag(Boolean isAutoPrintBintag) {
        this.isAutoPrintBintag = isAutoPrintBintag;
    }

    public Boolean getIsAutoGenerationCn() {
        return isAutoGenerationCn;
    }

    public void setIsAutoGenerationCn(Boolean isAutoGenerationCn) {
        this.isAutoGenerationCn = isAutoGenerationCn;
    }

    public Boolean getIsAllowBlocked() {
        return isAllowBlocked;
    }

    public void setIsAllowBlocked(Boolean isAllowBlocked) {
        this.isAllowBlocked = isAllowBlocked;
    }

    public Boolean getIsSkuNeedsScan() {
        return isSkuNeedsScan;
    }

    public void setIsSkuNeedsScan(Boolean isSkuNeedsScan) {
        this.isSkuNeedsScan = isSkuNeedsScan;
    }

    public String getInvAttrMgmt() {
        return invAttrMgmt;
    }

    public void setInvAttrMgmt(String invAttrMgmt) {
        this.invAttrMgmt = invAttrMgmt;
    }

    public Boolean getIsAutoPrintDiff() {
        return isAutoPrintDiff;
    }

    public void setIsAutoPrintDiff(Boolean isAutoPrintDiff) {
        this.isAutoPrintDiff = isAutoPrintDiff;
    }

    public Boolean getIsHintQualityTesting() {
        return isHintQualityTesting;
    }

    public void setIsHintQualityTesting(Boolean isHintQualityTesting) {
        this.isHintQualityTesting = isHintQualityTesting;
    }

    public Boolean getIsAutoPrintGoodsReceipt() {
        return isAutoPrintGoodsReceipt;
    }

    public void setIsAutoPrintGoodsReceipt(Boolean isAutoPrintGoodsReceipt) {
        this.isAutoPrintGoodsReceipt = isAutoPrintGoodsReceipt;
    }

    public Boolean getIsAutoPrintPlatformtag() {
        return isAutoPrintPlatformtag;
    }

    public void setIsAutoPrintPlatformtag(Boolean isAutoPrintPlatformtag) {
        this.isAutoPrintPlatformtag = isAutoPrintPlatformtag;
    }

    public Integer getSkuMixNumber() {
        return skuMixNumber;
    }

    public void setSkuMixNumber(Integer skuMixNumber) {
        this.skuMixNumber = skuMixNumber;
    }

    public Boolean getIsInboundLocationBarcode() {
        return isInboundLocationBarcode;
    }

    public void setIsInboundLocationBarcode(Boolean isInboundLocationBarcode) {
        this.isInboundLocationBarcode = isInboundLocationBarcode;
    }

	public Boolean getIsApplyFacility() {
		return isApplyFacility;
	}

	public void setIsApplyFacility(Boolean isApplyFacility) {
		this.isApplyFacility = isApplyFacility;
	}

	public Boolean getIsDiekingCheckMessage() {
		return isDiekingCheckMessage;
	}

	public void setIsDiekingCheckMessage(Boolean isDiekingCheckMessage) {
		this.isDiekingCheckMessage = isDiekingCheckMessage;
	}

	public Boolean getIsDiekingSeedingwallMessage() {
		return isDiekingSeedingwallMessage;
	}

	public void setIsDiekingSeedingwallMessage(Boolean isDiekingSeedingwallMessage) {
		this.isDiekingSeedingwallMessage = isDiekingSeedingwallMessage;
	}

	public String getSeedingMode() {
		return seedingMode;
	}

	public void setSeedingMode(String seedingMode) {
		this.seedingMode = seedingMode;
	}

	public Integer getXqty() {
		return xqty;
	}

	public void setXqty(Integer xqty) {
		this.xqty = xqty;
	}

	public Integer getYqty() {
		return yqty;
	}

	public void setYqty(Integer yqty) {
		this.yqty = yqty;
	}

	public Integer getZqty() {
		return zqty;
	}

	public void setZqty(Integer zqty) {
		this.zqty = zqty;
	}

	public Integer getSeedingOdoQty() {
		return seedingOdoQty;
	}

	public void setSeedingOdoQty(Integer seedingOdoQty) {
		this.seedingOdoQty = seedingOdoQty;
	}

	public Boolean getIsFacilityCheckCode() {
		return isFacilityCheckCode;
	}

	public void setIsFacilityCheckCode(Boolean isFacilityCheckCode) {
		this.isFacilityCheckCode = isFacilityCheckCode;
	}

}
