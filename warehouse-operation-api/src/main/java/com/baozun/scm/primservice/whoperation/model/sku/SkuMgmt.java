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
package com.baozun.scm.primservice.whoperation.model.sku;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class SkuMgmt extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 5878382182432911553L;

    // columns START
    /** SKU_ID */
    private Long skuId;
    /** 是否管理效期 有效期商品 */
    private Boolean isValid;
    /** 是否管理批次号 */
    private Boolean isBatchNo;
    /** 序列号管理类型*/
    private String serialNumberType;
    /** 是否管理原产地 */
    private Boolean isCountryOfOrigin;
    /** 是否管理库存类型 */
    private Boolean isInvType;
    /** 是否管理SKU属性 */
    private Boolean isSkuAttr;
    /** isToWeight */
    private Boolean isToWeight;
    // /** 出库箱类型 */
    // private Long outboundCtnType;
    /** 容器类型 */
    private Long containerType;
    /** 是否易碎品 */
    private Boolean isFragileCargo;
    /** 是否需要贴标 */
    private Boolean isLabeling;
    /** 是否需要质检 */
    private Boolean isQualityControl;
    /** 是否需要重新包装*/
    private Boolean isRepackaging;
    /** 是否混放 */
    private Boolean isMixAllowed;
    /** 混放属性 */
    private String mixAttr;
    /** 是否危险品 */
    private Boolean isHazardousCargo;
    // /** 占零拣货位数量 */
    // private Integer occupancyZeroPickingQty;
    // /** 占箱拣货位数量 */
    // private Integer occupancyBoxPickingQty;
    /** 是否贵重物品 */
    private Boolean isValuables;
    /** 最大有效期天数 */
    private Integer maxValidDate;
    /** 最小有效期天数 */
    private Integer minValidDate;
    /** 是否收过期商品 */
    private Boolean isExpiredGoodsReceive;
    /**商品保质期单位*/
    private String goodShelfLifeUnit;
    /** 有效期天数 */
    private Integer validDate;
    /** 效期预警天数 */
    private Integer warningDate;
    /** 存储条件 */
    private String storageCondition;
    // /** 拣货率 用于人员绩效管理 */
    // private Double pickingRate;
    // /** 打包率 用于人员绩效管理 */
    // private Double packingRate;
    // /** 特殊处理率 */
    // private Double abnormalRate;
    // /** 上架规则 */
    // private Long shelfRule;
    // /** 分配规则 */
    // private Long allocateRule;
    // /** 码盘标准 */
    // private String codeWheelNorm;
    /** 价格 */
    private Double price;
    // /** 最后盘点时间 */
    // private Date lastCheckTime;
    /** 标准装箱数 */
    private Integer cartonMeas;
    /** 组织ID */
    private Long ouId;

   
    //是否可折叠
    private Boolean isFoldable;
    
    public Boolean getIsFoldable() {
        return isFoldable;
    }

    public void setIsFoldable(Boolean isFoldable) {
        this.isFoldable = isFoldable;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Boolean getIsBatchNo() {
        return isBatchNo;
    }

    public void setIsBatchNo(Boolean isBatchNo) {
        this.isBatchNo = isBatchNo;
    }

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public Boolean getIsCountryOfOrigin() {
        return isCountryOfOrigin;
    }

    public void setIsCountryOfOrigin(Boolean isCountryOfOrigin) {
        this.isCountryOfOrigin = isCountryOfOrigin;
    }

    public Boolean getIsInvType() {
        return isInvType;
    }

    public void setIsInvType(Boolean isInvType) {
        this.isInvType = isInvType;
    }

    public Boolean getIsSkuAttr() {
        return isSkuAttr;
    }

    public void setIsSkuAttr(Boolean isSkuAttr) {
        this.isSkuAttr = isSkuAttr;
    }

    public Boolean getIsToWeight() {
        return isToWeight;
    }

    public void setIsToWeight(Boolean isToWeight) {
        this.isToWeight = isToWeight;
    }

    // public Long getOutboundCtnType() {
    // return outboundCtnType;
    // }
    //
    // public void setOutboundCtnType(Long outboundCtnType) {
    // this.outboundCtnType = outboundCtnType;
    // }

    public Long getContainerType() {
        return containerType;
    }

    public void setContainerType(Long containerType) {
        this.containerType = containerType;
    }

    public Boolean getIsFragileCargo() {
        return isFragileCargo;
    }

    public void setIsFragileCargo(Boolean isFragileCargo) {
        this.isFragileCargo = isFragileCargo;
    }

    public Boolean getIsLabeling() {
        return isLabeling;
    }

    public void setIsLabeling(Boolean isLabeling) {
        this.isLabeling = isLabeling;
    }

    public Boolean getIsQualityControl() {
        return isQualityControl;
    }

    public void setIsQualityControl(Boolean isQualityControl) {
        this.isQualityControl = isQualityControl;
    }

    public Boolean getIsRepackaging() {
        return isRepackaging;
    }

    public void setIsRepackaging(Boolean isRepackaging) {
        this.isRepackaging = isRepackaging;
    }

    public Boolean getIsMixAllowed() {
        return isMixAllowed;
    }

    public void setIsMixAllowed(Boolean isMixAllowed) {
        this.isMixAllowed = isMixAllowed;
    }

    public String getMixAttr() {
        return mixAttr;
    }

    public void setMixAttr(String mixAttr) {
        this.mixAttr = mixAttr;
    }

    public Boolean getIsHazardousCargo() {
        return isHazardousCargo;
    }

    public void setIsHazardousCargo(Boolean isHazardousCargo) {
        this.isHazardousCargo = isHazardousCargo;
    }

    // public Integer getOccupancyZeroPickingQty() {
    // return occupancyZeroPickingQty;
    // }
    //
    // public void setOccupancyZeroPickingQty(Integer occupancyZeroPickingQty) {
    // this.occupancyZeroPickingQty = occupancyZeroPickingQty;
    // }
    //
    // public Integer getOccupancyBoxPickingQty() {
    // return occupancyBoxPickingQty;
    // }
    //
    // public void setOccupancyBoxPickingQty(Integer occupancyBoxPickingQty) {
    // this.occupancyBoxPickingQty = occupancyBoxPickingQty;
    // }

    public Boolean getIsValuables() {
        return isValuables;
    }

    public void setIsValuables(Boolean isValuables) {
        this.isValuables = isValuables;
    }

    public Integer getMaxValidDate() {
        return maxValidDate;
    }

    public void setMaxValidDate(Integer maxValidDate) {
        this.maxValidDate = maxValidDate;
    }

    public Integer getMinValidDate() {
        return minValidDate;
    }

    public void setMinValidDate(Integer minValidDate) {
        this.minValidDate = minValidDate;
    }

    public Boolean getIsExpiredGoodsReceive() {
        return isExpiredGoodsReceive;
    }

    public void setIsExpiredGoodsReceive(Boolean isExpiredGoodsReceive) {
        this.isExpiredGoodsReceive = isExpiredGoodsReceive;
    }

    public String getGoodShelfLifeUnit() {
        return goodShelfLifeUnit;
    }

    public void setGoodShelfLifeUnit(String goodShelfLifeUnit) {
        this.goodShelfLifeUnit = goodShelfLifeUnit;
    }

    public Integer getValidDate() {
        return validDate;
    }

    public void setValidDate(Integer validDate) {
        this.validDate = validDate;
    }

    public Integer getWarningDate() {
        return warningDate;
    }

    public void setWarningDate(Integer warningDate) {
        this.warningDate = warningDate;
    }

    public String getStorageCondition() {
        return storageCondition;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }


    // public Long getShelfRule() {
    // return shelfRule;
    // }
    //
    // public void setShelfRule(Long shelfRule) {
    // this.shelfRule = shelfRule;
    // }
    //
    // public Long getAllocateRule() {
    // return allocateRule;
    // }
    //
    // public void setAllocateRule(Long allocateRule) {
    // this.allocateRule = allocateRule;
    // }
    //
    // public String getCodeWheelNorm() {
    // return codeWheelNorm;
    // }
    //
    // public void setCodeWheelNorm(String codeWheelNorm) {
    // this.codeWheelNorm = codeWheelNorm;
    // }
    //
    // public Double getPickingRate() {
    // return pickingRate;
    // }
    //
    // public void setPickingRate(Double pickingRate) {
    // this.pickingRate = pickingRate;
    // }
    //
    // public Double getPackingRate() {
    // return packingRate;
    // }
    //
    // public void setPackingRate(Double packingRate) {
    // this.packingRate = packingRate;
    // }
    //
    // public Double getAbnormalRate() {
    // return abnormalRate;
    // }
    //
    // public void setAbnormalRate(Double abnormalRate) {
    // this.abnormalRate = abnormalRate;
    // }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // public Date getLastCheckTime() {
    // return lastCheckTime;
    // }
    //
    // public void setLastCheckTime(Date lastCheckTime) {
    // this.lastCheckTime = lastCheckTime;
    // }

    public Integer getCartonMeas() {
        return cartonMeas;
    }

    public void setCartonMeas(Integer cartonMeas) {
        this.cartonMeas = cartonMeas;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
