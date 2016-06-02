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
package com.baozun.scm.primservice.whoperation.command.warehouse.inventory;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;

/**
 * 商品库存表
 * 
 * @author larkark
 * 
 */
public class WhSkuInventoryCommand extends BaseCommand {

    private static final long serialVersionUID = -635262476522169659L;

    /** 主键ID */
    private Long id;
    /** 商品ID */
    private Long skuId;
    /** 库位ID 库位号 */
    private Long locationId;
    /** 外部容器ID 托盘 货箱 */
    private Long outerContainerId;
    /** 内部容器ID 托盘 货箱 */
    private Long insideContainerId;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 占用单据号 */
    private String occupationCode;
    /** 在库库存 */
    private Double onHandQty;
    /** 已分配库存 */
    private Double allocatedQty;
    /** 待移入库存 */
    private Double toBeFilledQty;
    /** 冻结库存 */
    private Double frozenQty;
    /** 库存状态 */
    private Long invStatus;
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
    /** 内部对接码 */
    private String uuid;
    /** 是否可用 */
    private Boolean isLocked;
    /** 对应仓库ID */
    private Long ouId;
    /** 占用单据号来源 */
    private String occupationCodeSource;
    /** 生产日期 String */
    private String mfgDateStr;
    /** 失效日期 String */
    private String expDateStr;

    /** 商品编码 */
    private String skuCode;
    /** 商品名称 */
    private String skuName;
    /** 库位号 */
    private String locationCode;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 内部容器号 */
    private String insideContainerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺名称 */
    private String storeName;
    /** 库存类型 */
    private String invTypeName;
    /** 可用数量 */
    private Double qty;
    /** 库存状态 */
    private String invstatusName;
    /** 库存属性1 lable */
    private String invAttr1Str;
    /** 库存属性2 lable */
    private String invAttr2Str;
    /** 库存属性3 lable */
    private String invAttr3Str;
    /** 库存属性4 lable */
    private String invAttr4Str;
    /** 库存属性5 lable */
    private String invAttr5Str;
    /** 占用Key */
    private String occupyKey;
    private Double expectQty;

    // pda-general-receiving
    private Long occupationId;
    /** 入库时间 */
    private Date inboundTime;
    /** 最后操作时间 */
    private Date lastModifyTime;

    // 用于通用收货的流程
    /** 通用收货所选择的功能 */
    private WhFunctionRcvd rcvd;
    /** 通用收货功能菜单Index */
    private Integer menuIndex;
    /** 通用收货URL流程参数字符串 */
    private String functionUrl;
    /** 功能菜单Id */
    private Long FunctionId;
    /** 通用收货功能商品扫描流程参数字符串 */
    private String skuUrl;
    /** 通用收货功能商品扫描流程指针 */
    private Integer skuUrlOperator;
    /** 通用收货功能是否提示库存属性 */
    private Boolean isInvattrAsnPointoutUser;
    /** 通用收货功能是否允许差异收货 */
    private Boolean isInvattrDiscrepancyAllowrcvd;
    /** 通用收货功能匹配明细行 */
    private String lineIdListString;
    /** 通用收货功能匹配明细行 */
    private RcvdSnCacheCommand sn;
    /** 通用收货累计收货数量 */
    private Integer skuAddUpCount;
    /** 通用收货userId */
    private Long userId;
    /** 通用收货批量收货数量 */
    private Integer skuBatchCount;
    /** 残次数量 */
    private Integer snCount;
    /** 残次累计数量 */
    private Integer snAddUpCount;
    //
    /** 按照标准装箱提示收货 */
    private Boolean normIncPointoutRcvd;
    /** 是否限定唯一库存类型 */
    private Boolean isLimitUniqueInvType;
    /** 是否限定唯一库存状态 */
    private Boolean isLimitUniqueInvStatus;
    /** 是否限定唯一库存属性1 */
    private Boolean isLimitUniqueInvAttr1;
    /** 是否限定唯一库存属性2 */
    private Boolean isLimitUniqueInvAttr2;
    /** 是否限定唯一库存属性3 */
    private Boolean isLimitUniqueInvAttr3;
    /** 是否限定唯一库存属性4 */
    private Boolean isLimitUniqueInvAttr4;
    /** 是否限定唯一库存属性5 */
    private Boolean isLimitUniqueInvAttr5;
    /** 是否限定唯一原产地 */
    private Boolean isLimitUniquePlaceoforigin;
    /** 是否限定唯一批次号 */
    private Boolean isLimitUniqueBatch;
    /** 是否限定唯一生产日期 */
    private Boolean isLimitUniqueDateOfManufacture;
    /** 是否限定唯一失效日期 */
    private Boolean isLimitUniqueExpiryDate;
    /** 是否支持混放SKU */
    private Boolean isMixingSku;


    public Boolean getNormIncPointoutRcvd() {
        return normIncPointoutRcvd;
    }

    public void setNormIncPointoutRcvd(Boolean normIncPointoutRcvd) {
        this.normIncPointoutRcvd = normIncPointoutRcvd;
    }

    public Boolean getIsLimitUniqueInvType() {
        return isLimitUniqueInvType;
    }

    public void setIsLimitUniqueInvType(Boolean isLimitUniqueInvType) {
        this.isLimitUniqueInvType = isLimitUniqueInvType;
    }

    public Boolean getIsLimitUniqueInvStatus() {
        return isLimitUniqueInvStatus;
    }

    public void setIsLimitUniqueInvStatus(Boolean isLimitUniqueInvStatus) {
        this.isLimitUniqueInvStatus = isLimitUniqueInvStatus;
    }

    public Boolean getIsLimitUniqueInvAttr1() {
        return isLimitUniqueInvAttr1;
    }

    public void setIsLimitUniqueInvAttr1(Boolean isLimitUniqueInvAttr1) {
        this.isLimitUniqueInvAttr1 = isLimitUniqueInvAttr1;
    }

    public Boolean getIsLimitUniqueInvAttr2() {
        return isLimitUniqueInvAttr2;
    }

    public void setIsLimitUniqueInvAttr2(Boolean isLimitUniqueInvAttr2) {
        this.isLimitUniqueInvAttr2 = isLimitUniqueInvAttr2;
    }

    public Boolean getIsLimitUniqueInvAttr3() {
        return isLimitUniqueInvAttr3;
    }

    public void setIsLimitUniqueInvAttr3(Boolean isLimitUniqueInvAttr3) {
        this.isLimitUniqueInvAttr3 = isLimitUniqueInvAttr3;
    }

    public Boolean getIsLimitUniqueInvAttr4() {
        return isLimitUniqueInvAttr4;
    }

    public void setIsLimitUniqueInvAttr4(Boolean isLimitUniqueInvAttr4) {
        this.isLimitUniqueInvAttr4 = isLimitUniqueInvAttr4;
    }

    public Boolean getIsLimitUniqueInvAttr5() {
        return isLimitUniqueInvAttr5;
    }

    public void setIsLimitUniqueInvAttr5(Boolean isLimitUniqueInvAttr5) {
        this.isLimitUniqueInvAttr5 = isLimitUniqueInvAttr5;
    }

    public Boolean getIsLimitUniquePlaceoforigin() {
        return isLimitUniquePlaceoforigin;
    }

    public void setIsLimitUniquePlaceoforigin(Boolean isLimitUniquePlaceoforigin) {
        this.isLimitUniquePlaceoforigin = isLimitUniquePlaceoforigin;
    }

    public Boolean getIsLimitUniqueBatch() {
        return isLimitUniqueBatch;
    }

    public void setIsLimitUniqueBatch(Boolean isLimitUniqueBatch) {
        this.isLimitUniqueBatch = isLimitUniqueBatch;
    }

    public Boolean getIsLimitUniqueDateOfManufacture() {
        return isLimitUniqueDateOfManufacture;
    }

    public void setIsLimitUniqueDateOfManufacture(Boolean isLimitUniqueDateOfManufacture) {
        this.isLimitUniqueDateOfManufacture = isLimitUniqueDateOfManufacture;
    }

    public Boolean getIsLimitUniqueExpiryDate() {
        return isLimitUniqueExpiryDate;
    }

    public void setIsLimitUniqueExpiryDate(Boolean isLimitUniqueExpiryDate) {
        this.isLimitUniqueExpiryDate = isLimitUniqueExpiryDate;
    }

    public Boolean getIsMixingSku() {
        return isMixingSku;
    }

    public void setIsMixingSku(Boolean isMixingSku) {
        this.isMixingSku = isMixingSku;
    }

    public Integer getSnCount() {
        return snCount;
    }

    public void setSnCount(Integer snCount) {
        this.snCount = snCount;
    }

    public Integer getSnAddUpCount() {
        return snAddUpCount;
    }

    public void setSnAddUpCount(Integer snAddUpCount) {
        this.snAddUpCount = snAddUpCount;
    }

    public RcvdSnCacheCommand getSn() {
        return sn;
    }

    public void setSn(RcvdSnCacheCommand sn) {
        this.sn = sn;
    }

    public Integer getSkuBatchCount() {
        return skuBatchCount;
    }

    public void setSkuBatchCount(Integer skuBatchCount) {
        this.skuBatchCount = skuBatchCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLineIdListString() {
        return lineIdListString;
    }

    public void setLineIdListString(String lineIdListString) {
        this.lineIdListString = lineIdListString;
    }

    public Boolean getIsInvattrAsnPointoutUser() {
        return isInvattrAsnPointoutUser;
    }

    public void setIsInvattrAsnPointoutUser(Boolean isInvattrAsnPointoutUser) {
        this.isInvattrAsnPointoutUser = isInvattrAsnPointoutUser;
    }

    public Integer getSkuUrlOperator() {
        return skuUrlOperator;
    }

    public void setSkuUrlOperator(Integer skuUrlOperator) {
        this.skuUrlOperator = skuUrlOperator;
    }

    public String getSkuUrl() {
        return skuUrl;
    }

    public void setSkuUrl(String skuUrl) {
        this.skuUrl = skuUrl;
    }

    public Long getFunctionId() {
        return FunctionId;
    }

    public void setFunctionId(Long functionId) {
        FunctionId = functionId;
    }

    public String getFunctionUrl() {
        return functionUrl;
    }

    public void setFunctionUrl(String functionUrl) {
        this.functionUrl = functionUrl;
    }


    public Integer getMenuIndex() {
        return menuIndex;
    }

    public void setMenuIndex(Integer menuIndex) {
        this.menuIndex = menuIndex;
    }

    public WhFunctionRcvd getRcvd() {
        return rcvd;
    }

    public void setRcvd(WhFunctionRcvd rcvd) {
        this.rcvd = rcvd;
    }

    public Long getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Long occupationId) {
        this.occupationId = occupationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public Double getOnHandQty() {
        return onHandQty;
    }

    public void setOnHandQty(Double onHandQty) {
        this.onHandQty = onHandQty;
    }

    public Double getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(Double allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public Double getToBeFilledQty() {
        return toBeFilledQty;
    }

    public void setToBeFilledQty(Double toBeFilledQty) {
        this.toBeFilledQty = toBeFilledQty;
    }

    public Double getFrozenQty() {
        return frozenQty;
    }

    public void setFrozenQty(Double frozenQty) {
        this.frozenQty = frozenQty;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

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

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getInvTypeName() {
        return invTypeName;
    }

    public void setInvTypeName(String invTypeName) {
        this.invTypeName = invTypeName;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getInvstatusName() {
        return invstatusName;
    }

    public void setInvstatusName(String invstatusName) {
        this.invstatusName = invstatusName;
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

    public String getInvAttr1Str() {
        return invAttr1Str;
    }

    public void setInvAttr1Str(String invAttr1Str) {
        this.invAttr1Str = invAttr1Str;
    }

    public String getInvAttr2Str() {
        return invAttr2Str;
    }

    public void setInvAttr2Str(String invAttr2Str) {
        this.invAttr2Str = invAttr2Str;
    }

    public String getInvAttr3Str() {
        return invAttr3Str;
    }

    public void setInvAttr3Str(String invAttr3Str) {
        this.invAttr3Str = invAttr3Str;
    }

    public String getInvAttr4Str() {
        return invAttr4Str;
    }

    public void setInvAttr4Str(String invAttr4Str) {
        this.invAttr4Str = invAttr4Str;
    }

    public String getInvAttr5Str() {
        return invAttr5Str;
    }

    public void setInvAttr5Str(String invAttr5Str) {
        this.invAttr5Str = invAttr5Str;
    }

    public String getOccupationCodeSource() {
        return occupationCodeSource;
    }

    public void setOccupationCodeSource(String occupationCodeSource) {
        this.occupationCodeSource = occupationCodeSource;
    }

    /**
     * @return the occupyKey
     */
    public String getOccupyKey() {
        return occupyKey;
    }

    /**
     * @param occupyKey the occupyKey to set
     */
    public void setOccupyKey(String occupyKey) {
        this.occupyKey = occupyKey;
    }

    /**
     * @return the expectQty
     */
    public Double getExpectQty() {
        return expectQty;
    }

    /**
     * @param expectQty the expectQty to set
     */
    public void setExpectQty(Double expectQty) {
        this.expectQty = expectQty;
    }

    public Date getInboundTime() {
        return inboundTime;
    }

    public void setInboundTime(Date inboundTime) {
        this.inboundTime = inboundTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Boolean getIsInvattrDiscrepancyAllowrcvd() {
        return isInvattrDiscrepancyAllowrcvd;
    }

    public void setIsInvattrDiscrepancyAllowrcvd(Boolean isInvattrDiscrepancyAllowrcvd) {
        this.isInvattrDiscrepancyAllowrcvd = isInvattrDiscrepancyAllowrcvd;
    }

    public Integer getSkuAddUpCount() {
        return skuAddUpCount;
    }

    public void setSkuAddUpCount(Integer skuAddUpCount) {
        this.skuAddUpCount = skuAddUpCount;
    }


}
