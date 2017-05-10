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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 * 
 */
public class WarehouseMgmt extends BaseModel {

    private static final long serialVersionUID = -4785552327058507298L;

    // columns START
    /** 仓库主表ID */
    private Long warehouseId;
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
    private Integer goodsReceiptMode;
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
    /** 是否计算秒杀 */
    private Boolean isCalcSeckill = false;
    /** 秒杀出库单总数 */
    private Integer seckillOdoQtys;
    /** 是否计算主副品 */
    private Boolean isCalcTwoSkuSuit = false;
    /** 主副品出库单总数 */
    private Integer twoSkuSuitOdoQtys;
    /** 是否计算套装组合 */
    private Boolean isCalcSuits = false;
    /** 套装组合出库单总数 */
    private Integer suitsOdoQtys;
    /** 套装组合最大商品种类数 */
    private Integer suitsMaxSkuCategorys;
    /** 长度默认单位类型 */
    private Long defaultLengthUomType;
    /** 重量默认单位类型 */
    private Long defaultWeightUomType;
    /** 体积默认单位类型 */
    private Long defaultVolumeUomType;
    /** 面积默认单位类型 */
    private Long defaultAreaUomType;
    /** 拣货出现差异是否生成盘点任务 */
    private Boolean isGenerateInventoryTask = false;
    /** 上架是否启用校验码0：否 1：是 */
    private Boolean isInboundLocationBarcode = false;
    /** 补货是否启用校验码0：否 1：是 */
    private Boolean isReplenishmentLocationBarcode = false;
    /** 是否应用复核台和播种墙推荐逻辑 */
    private Boolean isApplyFacility = false;
    /** 拣货是否提示复核台信息 */
    private Boolean isDiekingCheckMessage = false;
    /** 拣货是否提示播种墙信息 */
    private Boolean isDiekingSeedingwallMessage = false;
    /** 是否校验设施校验码 */
    private Boolean isFacilityCheckCode = false;
    /** 播种模式 */
    private String seedingMode;
    /** x列数量 */
    private Integer xqty;
    /** y列数量 */
    private Integer yqty;
    /** z列数量 */
    private Integer zqty;
    /** 播种墙对应单据数 */
    private Integer seedingOdoQty;
    /** 是否强制校验消费者退货入的包裹登记 */
    private Boolean isCheckReturnedPurchaseReg;
    /** 出库单不可取消节点 */
    private String odoNotCancelNode;

    public String getOdoNotCancelNode() {
        return odoNotCancelNode;
    }

    public void setOdoNotCancelNode(String odoNotCancelNode) {
        this.odoNotCancelNode = odoNotCancelNode;
    }


    /** 是否管理耗材 */
    private Boolean isMgmtConsumableSku = false;
    /** 是否推荐耗材 */
    private Boolean isRecommandConsumableSku = false;
    /** 是否强制校验耗材 */
    private Boolean isCheckConsumableSkuBarcode = false;
    /** 是否自动化仓 */
    private Boolean isAutomaticWarehouse = false;

    // columns END

    public WarehouseMgmt() {}

    public Boolean getIsCheckReturnedPurchaseReg() {
        return isCheckReturnedPurchaseReg;
    }

    public void setIsCheckReturnedPurchaseReg(Boolean isCheckReturnedPurchaseReg) {
        this.isCheckReturnedPurchaseReg = isCheckReturnedPurchaseReg;
    }

    public WarehouseMgmt(Long id) {
        this.id = id;
    }

    public void setWarehouseId(Long value) {
        this.warehouseId = value;
    }

    public Long getWarehouseId() {
        return this.warehouseId;
    }

    public void setIsMandatorilyReserved(Boolean value) {
        this.isMandatorilyReserved = value;
    }

    public Boolean getIsMandatorilyReserved() {
        return this.isMandatorilyReserved;
    }

    public void setIsPoOvercharge(Boolean value) {
        this.isPoOvercharge = value;
    }

    public Boolean getIsPoOvercharge() {
        return this.isPoOvercharge;
    }

    public void setPoOverchargeProportion(Integer value) {
        this.poOverchargeProportion = value;
    }

    public Integer getPoOverchargeProportion() {
        return this.poOverchargeProportion;
    }

    public void setIsAsnOvercharge(Boolean value) {
        this.isAsnOvercharge = value;
    }

    public Boolean getIsAsnOvercharge() {
        return this.isAsnOvercharge;
    }

    public void setAsnOverchargeProportion(Integer value) {
        this.asnOverchargeProportion = value;
    }

    public Integer getAsnOverchargeProportion() {
        return this.asnOverchargeProportion;
    }

    public void setIsPoAutoVerify(Boolean value) {
        this.isPoAutoVerify = value;
    }

    public Boolean getIsPoAutoVerify() {
        return this.isPoAutoVerify;
    }

    public void setIsAsnAutoVerify(Boolean value) {
        this.isAsnAutoVerify = value;
    }

    public Boolean getIsAsnAutoVerify() {
        return this.isAsnAutoVerify;
    }

    public void setGoodsReceiptMode(Integer value) {
        this.goodsReceiptMode = value;
    }

    public Integer getGoodsReceiptMode() {
        return this.goodsReceiptMode;
    }

    public void setIsAutoPrintBintag(Boolean value) {
        this.isAutoPrintBintag = value;
    }

    public Boolean getIsAutoPrintBintag() {
        return this.isAutoPrintBintag;
    }

    public void setIsAutoGenerationCn(Boolean value) {
        this.isAutoGenerationCn = value;
    }

    public Boolean getIsAutoGenerationCn() {
        return this.isAutoGenerationCn;
    }

    public void setIsAllowBlocked(Boolean value) {
        this.isAllowBlocked = value;
    }

    public Boolean getIsAllowBlocked() {
        return this.isAllowBlocked;
    }

    public void setIsSkuNeedsScan(Boolean value) {
        this.isSkuNeedsScan = value;
    }

    public Boolean getIsSkuNeedsScan() {
        return this.isSkuNeedsScan;
    }

    public void setInvAttrMgmt(String value) {
        this.invAttrMgmt = value;
    }

    public String getInvAttrMgmt() {
        return this.invAttrMgmt;
    }

    public void setIsAutoPrintDiff(Boolean value) {
        this.isAutoPrintDiff = value;
    }

    public Boolean getIsAutoPrintDiff() {
        return this.isAutoPrintDiff;
    }

    public void setIsHintQualityTesting(Boolean value) {
        this.isHintQualityTesting = value;
    }

    public Boolean getIsHintQualityTesting() {
        return this.isHintQualityTesting;
    }

    public void setIsAutoPrintGoodsReceipt(Boolean value) {
        this.isAutoPrintGoodsReceipt = value;
    }

    public Boolean getIsAutoPrintGoodsReceipt() {
        return this.isAutoPrintGoodsReceipt;
    }

    public void setIsAutoPrintPlatformtag(Boolean value) {
        this.isAutoPrintPlatformtag = value;
    }

    public Boolean getIsAutoPrintPlatformtag() {
        return this.isAutoPrintPlatformtag;
    }

    public void setSkuMixNumber(Integer value) {
        this.skuMixNumber = value;
    }

    public Integer getSkuMixNumber() {
        return this.skuMixNumber;
    }

    public void setIsSortationContainerAssign(Boolean value) {
        this.isSortationContainerAssign = value;
    }

    public Boolean getIsSortationContainerAssign() {
        return this.isSortationContainerAssign;
    }

    public void setIsRepeatedlyOutbound(Boolean value) {
        this.isRepeatedlyOutbound = value;
    }

    public Boolean getIsRepeatedlyOutbound() {
        return this.isRepeatedlyOutbound;
    }

    public void setIsTabbInvTotal(Boolean value) {
        this.isTabbInvTotal = value;
    }

    public Boolean getIsTabbInvTotal() {
        return this.isTabbInvTotal;
    }

    public void setIsCalcSeckill(Boolean value) {
        this.isCalcSeckill = value;
    }

    public Boolean getIsCalcSeckill() {
        return this.isCalcSeckill;
    }

    public void setSeckillOdoQtys(Integer value) {
        this.seckillOdoQtys = value;
    }

    public Integer getSeckillOdoQtys() {
        return this.seckillOdoQtys;
    }

    public void setIsCalcTwoSkuSuit(Boolean value) {
        this.isCalcTwoSkuSuit = value;
    }

    public Boolean getIsCalcTwoSkuSuit() {
        return this.isCalcTwoSkuSuit;
    }

    public void setTwoSkuSuitOdoQtys(Integer value) {
        this.twoSkuSuitOdoQtys = value;
    }

    public Integer getTwoSkuSuitOdoQtys() {
        return this.twoSkuSuitOdoQtys;
    }

    public void setIsCalcSuits(Boolean value) {
        this.isCalcSuits = value;
    }

    public Boolean getIsCalcSuits() {
        return this.isCalcSuits;
    }

    public void setSuitsOdoQtys(Integer value) {
        this.suitsOdoQtys = value;
    }

    public Integer getSuitsOdoQtys() {
        return this.suitsOdoQtys;
    }

    public void setSuitsMaxSkuCategorys(Integer value) {
        this.suitsMaxSkuCategorys = value;
    }

    public Integer getSuitsMaxSkuCategorys() {
        return this.suitsMaxSkuCategorys;
    }

    public void setDefaultLengthUomType(Long value) {
        this.defaultLengthUomType = value;
    }

    public Long getDefaultLengthUomType() {
        return this.defaultLengthUomType;
    }

    public void setDefaultWeightUomType(Long value) {
        this.defaultWeightUomType = value;
    }

    public Long getDefaultWeightUomType() {
        return this.defaultWeightUomType;
    }

    public void setDefaultVolumeUomType(Long value) {
        this.defaultVolumeUomType = value;
    }

    public Long getDefaultVolumeUomType() {
        return this.defaultVolumeUomType;
    }

    public void setDefaultAreaUomType(Long value) {
        this.defaultAreaUomType = value;
    }

    public Long getDefaultAreaUomType() {
        return this.defaultAreaUomType;
    }

    public void setIsGenerateInventoryTask(Boolean value) {
        this.isGenerateInventoryTask = value;
    }

    public Boolean getIsGenerateInventoryTask() {
        return this.isGenerateInventoryTask;
    }

    public void setIsInboundLocationBarcode(Boolean value) {
        this.isInboundLocationBarcode = value;
    }

    public Boolean getIsInboundLocationBarcode() {
        return this.isInboundLocationBarcode;
    }

    public void setIsReplenishmentLocationBarcode(Boolean value) {
        this.isReplenishmentLocationBarcode = value;
    }

    public Boolean getIsReplenishmentLocationBarcode() {
        return this.isReplenishmentLocationBarcode;
    }

    public void setIsApplyFacility(Boolean value) {
        this.isApplyFacility = value;
    }

    public Boolean getIsApplyFacility() {
        return this.isApplyFacility;
    }

    public void setIsDiekingCheckMessage(Boolean value) {
        this.isDiekingCheckMessage = value;
    }

    public Boolean getIsDiekingCheckMessage() {
        return this.isDiekingCheckMessage;
    }

    public void setIsDiekingSeedingwallMessage(Boolean value) {
        this.isDiekingSeedingwallMessage = value;
    }

    public Boolean getIsDiekingSeedingwallMessage() {
        return this.isDiekingSeedingwallMessage;
    }

    public void setSeedingMode(String value) {
        this.seedingMode = value;
    }

    public String getSeedingMode() {
        return this.seedingMode;
    }

    public void setXqty(Integer value) {
        this.xqty = value;
    }

    public Integer getXqty() {
        return this.xqty;
    }

    public void setYqty(Integer value) {
        this.yqty = value;
    }

    public Integer getYqty() {
        return this.yqty;
    }

    public void setZqty(Integer value) {
        this.zqty = value;
    }

    public Integer getZqty() {
        return this.zqty;
    }

    public void setSeedingOdoQty(Integer value) {
        this.seedingOdoQty = value;
    }

    public Integer getSeedingOdoQty() {
        return this.seedingOdoQty;
    }

    public Boolean getIsFacilityCheckCode() {
        return isFacilityCheckCode;
    }

    public void setIsFacilityCheckCode(Boolean isFacilityCheckCode) {
        this.isFacilityCheckCode = isFacilityCheckCode;
    }

    public Boolean getIsMgmtConsumableSku() {
        return isMgmtConsumableSku;
    }

    public void setIsMgmtConsumableSku(Boolean isMgmtConsumableSku) {
        this.isMgmtConsumableSku = isMgmtConsumableSku;
    }

    public Boolean getIsRecommandConsumableSku() {
        return isRecommandConsumableSku;
    }

    public void setIsRecommandConsumableSku(Boolean isRecommandConsumableSku) {
        this.isRecommandConsumableSku = isRecommandConsumableSku;
    }

    public Boolean getIsCheckConsumableSkuBarcode() {
        return isCheckConsumableSkuBarcode;
    }

    public void setIsCheckConsumableSkuBarcode(Boolean isCheckConsumableSkuBarcode) {
        this.isCheckConsumableSkuBarcode = isCheckConsumableSkuBarcode;
    }

    public Boolean getIsAutomaticWarehouse() {
        return isAutomaticWarehouse;
    }

    public void setIsAutomaticWarehouse(Boolean isAutomaticWarehouse) {
        this.isAutomaticWarehouse = isAutomaticWarehouse;
    }
}
