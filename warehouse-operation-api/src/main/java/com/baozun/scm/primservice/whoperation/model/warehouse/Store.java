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
 * @author larkark
 * 
 */
public class Store extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 4017118248509111339L;
    /*
     * 店铺编码
     */
    private String storeCode;
    /*
     * 店铺名称
     */
    private String storeName;
    /*
     * 描述
     */
    private String description;
    /*
     * 联系人
     */
    private String pic;
    /*
     * 联系人电话
     */
    private String picContact;
    /*
     * 发票类型
     */
    private String invoiceType;
    /*
     * 结算方式
     */
    private String paymentTerm;

    /*
     * 是否强制预约 0:否 1:是
     */
    private Boolean isMandatorilyReserved;

    /*
     * 是否PO超收 0:否 1:是
     */
    private Boolean isPoOvercharge;

    /*
     * PO超收比例
     */
    private Integer poOverchargeProportion;
    /*
     * 是否ASN超收 0:否 1:是
     */
    private Boolean isAsnOvercharge;

    /*
     * asn超收比例
     */
    private Integer asnOverchargeProportion;
    
    /*
     *  是否自动审核PO
     */
    private Boolean isPoAutoVerify = false;
    /*
     * 是否自动审核ASN
     */
    private Boolean isAsnAutoVerify = false;
    /*
     *  预收货模式 1-总数 2-总箱数 3-商品数量
     */
    private Integer goodsReceiptMode = 1;
    /*
     * 收货是否自动打印箱标签
     */
    private Boolean isAutoPrintBintag = false;
    /*
     * 收货是否自动生成箱号
     */
    private Boolean isAutoGenerationCn = false;
    /*
     * 是否允许越库
     */
    private Boolean isAllowBlocked = false;
    /*
     * 库存属性管理
     */
    private String invAttrMgmt;
    /*
     * 是否允许收货差异
     */
    private Boolean isAllowCollectDiff = false;
    /*
     * 是否自动打印收货差异清单 
     */
    private Boolean isAutoPrintDiff = false;
    /*
     * 入库是否提示质检
     */
    private Boolean isHintQualityTesting = false;
    /*
     * 是否自动打印预收货交接清单
     */
    private Boolean isAutoPrintGoodsReceipt = false;
    
    /*
     * 客户ID
     */
    private Long customerId;
    /*
     * 创建时间
     */
    private Date createTime;
    /*
     * 修改时间
     */
    private Date lastModifyTime;
    /*
     * 操作人ID
     */
    private Long operatorId;
    /*
     * 1.可用;2.已删除;0.禁用
     */
    private Integer lifecycle;
    
    /** 联系手机 */
    private String picMobileTelephone;
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
    /** 邮箱 */
    private String email;
    /** 乡镇/街道 */
    private Long villagesTownsId;
    /** 区ID */
    private Long districtId;
    
    /** 入库单据反馈节点 1:收货时反馈 2:上架时反馈*/
    private Integer inboundConfirmNode = 1;
    /** 入库反馈类型 1:按单反馈 2:按箱反馈*/
    private Integer inboundConfirmType = 1;
    /** 入库反馈单据类型 1:按PO单反馈 2:按ASN单反馈*/
    private Integer inboundConfirmOrderType = 1;
    /** 消费者退货入是否关联原出库单库存属性 */
	private Boolean isReturnedPurchaseOriginalInvAttr = false;
	/** 消费者退货入是否强制校验商品库存属性 */
	private Boolean isCheckReturnedPurchaseInvAttr = false;
	/** 消费者退货入是否强制校验商品库存状态 */
	private Boolean isCheckReturnedPurchaseInvStatus = false;
	/** 消费者退货入收货店铺 */
	private String returnedPurchaseStore;
    
    /*
     * 用于全局表最后修改时间统一
     */
    private Date globalLastModifyTime ;

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPicContact() {
        return picContact;
    }

    public void setPicContact(String picContact) {
        this.picContact = picContact;
    }


    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
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

    public String getInvAttrMgmt() {
        return invAttrMgmt;
    }

    public void setInvAttrMgmt(String invAttrMgmt) {
        this.invAttrMgmt = invAttrMgmt;
    }

    public Boolean getIsAllowCollectDiff() {
        return isAllowCollectDiff;
    }

    public void setIsAllowCollectDiff(Boolean isAllowCollectDiff) {
        this.isAllowCollectDiff = isAllowCollectDiff;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Date getGlobalLastModifyTime() {
        return globalLastModifyTime;
    }

    public void setGlobalLastModifyTime(Date globalLastModifyTime) {
        this.globalLastModifyTime = globalLastModifyTime;
    }

    public String getPicMobileTelephone() {
        return picMobileTelephone;
    }

    public void setPicMobileTelephone(String picMobileTelephone) {
        this.picMobileTelephone = picMobileTelephone;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getVillagesTownsId() {
        return villagesTownsId;
    }

    public void setVillagesTownsId(Long villagesTownsId) {
        this.villagesTownsId = villagesTownsId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

	public Integer getInboundConfirmNode() {
		return inboundConfirmNode;
	}

	public void setInboundConfirmNode(Integer inboundConfirmNode) {
		this.inboundConfirmNode = inboundConfirmNode;
	}

	public Integer getInboundConfirmType() {
		return inboundConfirmType;
	}

	public void setInboundConfirmType(Integer inboundConfirmType) {
		this.inboundConfirmType = inboundConfirmType;
	}

	public Integer getInboundConfirmOrderType() {
		return inboundConfirmOrderType;
	}

	public void setInboundConfirmOrderType(Integer inboundConfirmOrderType) {
		this.inboundConfirmOrderType = inboundConfirmOrderType;
	}

	public Boolean getIsReturnedPurchaseOriginalInvAttr() {
		return isReturnedPurchaseOriginalInvAttr;
	}

	public void setIsReturnedPurchaseOriginalInvAttr(Boolean isReturnedPurchaseOriginalInvAttr) {
		this.isReturnedPurchaseOriginalInvAttr = isReturnedPurchaseOriginalInvAttr;
	}

	public Boolean getIsCheckReturnedPurchaseInvAttr() {
		return isCheckReturnedPurchaseInvAttr;
	}

	public void setIsCheckReturnedPurchaseInvAttr(Boolean isCheckReturnedPurchaseInvAttr) {
		this.isCheckReturnedPurchaseInvAttr = isCheckReturnedPurchaseInvAttr;
	}

	public Boolean getIsCheckReturnedPurchaseInvStatus() {
		return isCheckReturnedPurchaseInvStatus;
	}

	public void setIsCheckReturnedPurchaseInvStatus(Boolean isCheckReturnedPurchaseInvStatus) {
		this.isCheckReturnedPurchaseInvStatus = isCheckReturnedPurchaseInvStatus;
	}

	public String getReturnedPurchaseStore() {
		return returnedPurchaseStore;
	}

	public void setReturnedPurchaseStore(String returnedPurchaseStore) {
		this.returnedPurchaseStore = returnedPurchaseStore;
	}
    

}
