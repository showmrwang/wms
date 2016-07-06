package com.baozun.scm.primservice.whoperation.command.sku.skucommand;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class SkuMgmtCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -3400071214711611635L;
    private Long id;
    /** SKU_ID */
    private Long skuId;
    /** 是否管理效期 有效期商品 */
    private Boolean isValid;
    /** 是否管理批次号 */
    private Boolean isBatchNo;
    /** 序列号管理类型*/
    private Long serialNumberType;
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
    
    /** 是否可折叠 */
    private Boolean isFoldable;

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

    public Long getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(Long serialNumberType) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

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

    public Boolean getIsFoldable() {
        return isFoldable;
    }

    public void setIsFoldable(Boolean isFoldable) {
        this.isFoldable = isFoldable;
    }
    
    
}
