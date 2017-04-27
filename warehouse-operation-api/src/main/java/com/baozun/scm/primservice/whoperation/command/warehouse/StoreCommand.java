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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;


/**
 * 
 * @author larkark
 * 
 */
public class StoreCommand extends BaseCommand {



    /**
     * 
     */
    private static final long serialVersionUID = -8824163791897913381L;

    private Long id;
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
     * 是否自动审核PO
     */
    private Boolean isPoAutoVerify = false;
    /*
     * 是否自动审核ASN
     */
    private Boolean isAsnAutoVerify = false;
    /*
     * 预收货模式 1-总数 2-总箱数 3-商品数量
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
    /** 入库单据反馈节点 1:收货时反馈 2:上架时反馈 */
    private Integer inboundConfirmNode = 1;
    /** 入库反馈类型 1:按单反馈 2:按箱反馈 */
    private Integer inboundConfirmType = 1;
    /** 入库反馈单据类型 1:按PO单反馈 2:按ASN单反馈 */
    private Integer inboundConfirmOrderType = 1;
    /** 消费者退货入是否关联原出库单库存属性 */
    private Boolean isReturnedPurchaseOriginalInvAttr = false;
    /** 消费者退货入是否强制校验商品库存属性 */
    private Boolean isCheckReturnedPurchaseInvAttr = false;
    /** 消费者退货入是否强制校验商品库存状态 */
    private Boolean isCheckReturnedPurchaseInvStatus = false;
    /** 消费者退货入收货店铺 */
    private String returnedPurchaseStore;
    /** 开票公司 */
    private String makeOutAnInvoiceCompany;
    /** 发票导出模板 */
    private String invoiceExportTemplet;
    /** 开票分组 */
    private String makeOutAnInvoiceGroup;
    /** 配送对象分组 */
    private String distributionTargetGroup;
    /** 快递推荐优先规则 */
    private String expressRecommendation;

    public String getDistributionTargetGroup() {
        return distributionTargetGroup;
    }

    public void setDistributionTargetGroup(String distributionTargetGroup) {
        this.distributionTargetGroup = distributionTargetGroup;
    }

    /*
     * 用于全局表最后修改时间统一
     */
    private Date globalLastModifyTime;

    private String invoiceTypeName;

    private String paymentTermName;

    private String customerName;

    private Long userId;

    private String userName;

    private Long ucId;

    private List<Store> allStoreList;

    private List<Store> existStoreList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getInvoiceTypeName() {
        return invoiceTypeName;
    }

    public void setInvoiceTypeName(String invoiceTypeName) {
        this.invoiceTypeName = invoiceTypeName;
    }

    public String getPaymentTermName() {
        return paymentTermName;
    }

    public void setPaymentTermName(String paymentTermName) {
        this.paymentTermName = paymentTermName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUcId() {
        return ucId;
    }

    public void setUcId(Long ucId) {
        this.ucId = ucId;
    }

    public List<Store> getAllStoreList() {
        return allStoreList;
    }

    public void setAllStoreList(List<Store> allStoreList) {
        this.allStoreList = allStoreList;
    }

    public List<Store> getExistStoreList() {
        return existStoreList;
    }

    public void setExistStoreList(List<Store> existStoreList) {
        this.existStoreList = existStoreList;
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

    public String getMakeOutAnInvoiceCompany() {
        return makeOutAnInvoiceCompany;
    }

    public void setMakeOutAnInvoiceCompany(String makeOutAnInvoiceCompany) {
        this.makeOutAnInvoiceCompany = makeOutAnInvoiceCompany;
    }

    public String getInvoiceExportTemplet() {
        return invoiceExportTemplet;
    }

    public void setInvoiceExportTemplet(String invoiceExportTemplet) {
        this.invoiceExportTemplet = invoiceExportTemplet;
    }

    public String getMakeOutAnInvoiceGroup() {
        return makeOutAnInvoiceGroup;
    }

    public void setMakeOutAnInvoiceGroup(String makeOutAnInvoiceGroup) {
        this.makeOutAnInvoiceGroup = makeOutAnInvoiceGroup;
    }

    public String getExpressRecommendation() {
        return expressRecommendation;
    }

    public void setExpressRecommendation(String expressRecommendation) {
        this.expressRecommendation = expressRecommendation;
    }

}
