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
package com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * @author lichuan
 *
 */
public class ScanResultCommand extends BaseCommand {

    private static final long serialVersionUID = -7534090365486133862L;

    /** 上架模式 */
    private int putawayPatternType;
    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 是否提示库位 */
    private boolean isNeedTipLocation;
    /** 上架是否校验库位 */
    private boolean isValidateLocation;
    /** 是否有外部容器 */
    private boolean isHasOuterContainer;
    /** 是否有内部容器 */
    private boolean isHasInsideContainer;
    /** 是否已推荐库位 */
    private boolean isRecommendLocation;
    /** 是否提示容器 */
    private boolean isNeedTipContainer;
    /** 提示库位编码 */
    private String tipLocationCode;
    /** 提示容器编码 */
    private String tipContainerCode;
    /** 内部容器号 */
    private String insideContainerCode;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 容器类型 1：外部容器 2：内部容器 */
    private int containerType;
    /** 是否已执行上架 */
    private boolean isPutaway;
    /** 缓存是否存在 */
    private boolean isCacheExists;
    /** 是否提示商品 */
    private boolean isNeedTipSku;
    /** 提示商品条码 */
    private String tipSkuBarcode;
    /** 是否直接核扫商品 */
    private boolean isNeedScanSku;
    /** 扫描sku模式1：数量扫描2：逐件扫描 */
    private int scanPattern;
    /** 上架以后是否提示下一个容器 */
    private boolean isAfterPutawayTipContianer;
    /** 是否需要提示扫商品属性 */
    private boolean isNeedTipSkuDetail;
    /** 提示商品数量 */
    private int tipSkuQty;
    /** 是否需要提示商品的库存类型 */
    private boolean isNeedTipSkuInvType;
    /** 提示商品库存类型 */
    private String tipSkuInvType = "";
    /** 是否需要提示商品的库存状态 */
    private boolean isNeedTipSkuInvStatus;
    /** 提示商品库存状态 */
    private String tipSkuInvStatus = "";
    /** 是否需要提示商品的生产日期 */
    private boolean isNeedTipSkuMfgDate;
    /** 提示商品生产日期 */
    private String tipSkuMfgDate = "";
    /** 是否需要提示商品过期日期 */
    private boolean isNeedTipSkuExpDate;
    /** 提示商品过期日期 */
    private String tipSkuExpDate = "";
    /** 是否需要提示商品库存属性1 */
    private boolean isNeedTipSkuInvAttr1;
    /** 提示商品库存属性1 */
    private String tipSkuInvAttr1 = "";
    /** 是否需要提示商品库存属性2 */
    private boolean isNeedTipSkuInvAttr2;
    /** 提示商品库存属性2 */
    private String tipSkuInvAttr2 = "";
    /** 是否需要提示商品库存属性3 */
    private boolean isNeedTipSkuInvAttr3;
    /** 提示商品库存属性3 */
    private String tipSkuInvAttr3;
    /** 是否需要提示商品库存属性4 */
    private boolean isNeedTipSkuInvAttr4;
    /** 提示商品库存属性4 */
    private String tipSkuInvAttr4 = "";
    /** 是否需要提示商品库存属性5 */
    private boolean isNeedTipSkuInvAttr5;
    /** 提示商品库存属性5 */
    private String tipSkuInvAttr5 = "";
    /** 是否需要提示商品sn */
    private boolean isNeedTipSkuSn;
    /** 提示商品sn */
    private String tipSkuSn = "";
    /** 是否需要提示商品残次条码 */
    private boolean isNeedTipSkuDefect;
    /** 提示商品条码 */
    private String tipSkuDefect = "";
    /** 是否需要扫描Sn残次信息 */
    private boolean isNeedScanSkuSn;
    /** 上架以后是否需要提示下一个库位 */
    private boolean isAfterPutawayTipLoc;
    /** 是否需要排队 */
    private boolean isNeedQueueUp;
    /** 是否库位推荐失败 */
    private boolean isRecommendFail;
    /** 库位推荐失败以后是否提示下一个容器 */
    private boolean isAfterRecommendTipContainer;
    /**只扫caselevel的容器*/
    private boolean isCaselevelScanContainer;

    /**只扫非caselevel的容器*/
    private boolean isNotCaselevelScanContainer;
    /** 扫描所有的容器*/
    private boolean scanContainer;
    /**内部容器是否已经扫描完毕*/
    private boolean isNotContainer;
    /**继续扫描商品*/
    private boolean isScanSku;
    /**sku条码*/
    private String skuBarCode;
    
    private String tipLocBarCode;  //提示库位条码
    
    private String locationCode;
    
    
    private Boolean isNeedScanNewLocation;   //拆箱扫描商品重量达到库位承重的时候,跳转
    
    private Boolean isContinueScanSn;   //

    public int getPutawayPatternType() {
        return putawayPatternType;
    }

    public void setPutawayPatternType(int putawayPatternType) {
        this.putawayPatternType = putawayPatternType;
    }

    public int getPutawayPatternDetailType() {
        return putawayPatternDetailType;
    }

    public void setPutawayPatternDetailType(int putawayPatternDetailType) {
        this.putawayPatternDetailType = putawayPatternDetailType;
    }

    public boolean isNeedTipLocation() {
        return isNeedTipLocation;
    }

    public void setNeedTipLocation(boolean isNeedTipLocation) {
        this.isNeedTipLocation = isNeedTipLocation;
    }
    
    public boolean isValidateLocation() {
        return isValidateLocation;
    }

    public void setValidateLocation(boolean isValidateLocation) {
        this.isValidateLocation = isValidateLocation;
    }

    public boolean isHasOuterContainer() {
        return isHasOuterContainer;
    }

    public void setHasOuterContainer(boolean isHasOuterContainer) {
        this.isHasOuterContainer = isHasOuterContainer;
    }

    public boolean isHasInsideContainer() {
        return isHasInsideContainer;
    }

    public void setHasInsideContainer(boolean isHasInsideContainer) {
        this.isHasInsideContainer = isHasInsideContainer;
    }

    public boolean isRecommendLocation() {
        return isRecommendLocation;
    }

    public void setRecommendLocation(boolean isRecommendLocation) {
        this.isRecommendLocation = isRecommendLocation;
    }

    public boolean isNeedTipContainer() {
        return isNeedTipContainer;
    }

    public void setNeedTipContainer(boolean isNeedTipContainer) {
        this.isNeedTipContainer = isNeedTipContainer;
    }

    public String getTipLocationCode() {
        return tipLocationCode;
    }

    public void setTipLocationCode(String tipLocationCode) {
        this.tipLocationCode = tipLocationCode;
    }

    public String getTipContainerCode() {
        return tipContainerCode;
    }

    public void setTipContainerCode(String tipContainerCode) {
        this.tipContainerCode = tipContainerCode;
    }

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public int getContainerType() {
        return containerType;
    }

    public void setContainerType(int containerType) {
        this.containerType = containerType;
    }

    public boolean isPutaway() {
        return isPutaway;
    }

    public void setPutaway(boolean isPutaway) {
        this.isPutaway = isPutaway;
    }

    public boolean isCacheExists() {
        return isCacheExists;
    }

    public void setCacheExists(boolean isCacheExists) {
        this.isCacheExists = isCacheExists;
    }

    public boolean isNeedTipSku() {
        return isNeedTipSku;
    }

    public void setNeedTipSku(boolean isNeedTipSku) {
        this.isNeedTipSku = isNeedTipSku;
    }

    public String getTipSkuBarcode() {
        return tipSkuBarcode;
    }

    public void setTipSkuBarcode(String tipSkuBarcode) {
        this.tipSkuBarcode = tipSkuBarcode;
    }

    public boolean isNeedScanSku() {
        return isNeedScanSku;
    }

    public void setNeedScanSku(boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }

    public int getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(int scanPattern) {
        this.scanPattern = scanPattern;
    }

    public boolean isAfterPutawayTipContianer() {
        return isAfterPutawayTipContianer;
    }

    public void setAfterPutawayTipContianer(boolean isAfterPutawayTipContianer) {
        this.isAfterPutawayTipContianer = isAfterPutawayTipContianer;
    }

    public boolean isNeedTipSkuDetail() {
        return isNeedTipSkuDetail;
    }

    public void setNeedTipSkuDetail(boolean isNeedTipSkuDetail) {
        this.isNeedTipSkuDetail = isNeedTipSkuDetail;
    }

    public int getTipSkuQty() {
        return tipSkuQty;
    }

    public void setTipSkuQty(int tipSkuQty) {
        this.tipSkuQty = tipSkuQty;
    }

    public boolean isNeedTipSkuInvType() {
        return isNeedTipSkuInvType;
    }

    public void setNeedTipSkuInvType(boolean isNeedTipSkuInvType) {
        this.isNeedTipSkuInvType = isNeedTipSkuInvType;
    }

    public String getTipSkuInvType() {
        return tipSkuInvType;
    }

    public void setTipSkuInvType(String tipSkuInvType) {
        this.tipSkuInvType = tipSkuInvType;
    }

    public boolean isNeedTipSkuInvStatus() {
        return isNeedTipSkuInvStatus;
    }

    public void setNeedTipSkuInvStatus(boolean isNeedTipSkuInvStatus) {
        this.isNeedTipSkuInvStatus = isNeedTipSkuInvStatus;
    }

    public String getTipSkuInvStatus() {
        return tipSkuInvStatus;
    }

    public void setTipSkuInvStatus(String tipSkuInvStatus) {
        this.tipSkuInvStatus = tipSkuInvStatus;
    }

    public boolean isNeedTipSkuMfgDate() {
        return isNeedTipSkuMfgDate;
    }

    public void setNeedTipSkuMfgDate(boolean isNeedTipSkuMfgDate) {
        this.isNeedTipSkuMfgDate = isNeedTipSkuMfgDate;
    }

    public String getTipSkuMfgDate() {
        return tipSkuMfgDate;
    }

    public void setTipSkuMfgDate(String tipSkuMfgDate) {
        this.tipSkuMfgDate = tipSkuMfgDate;
    }

    public boolean isNeedTipSkuExpDate() {
        return isNeedTipSkuExpDate;
    }

    public void setNeedTipSkuExpDate(boolean isNeedTipSkuExpDate) {
        this.isNeedTipSkuExpDate = isNeedTipSkuExpDate;
    }

    public String getTipSkuExpDate() {
        return tipSkuExpDate;
    }

    public void setTipSkuExpDate(String tipSkuExpDate) {
        this.tipSkuExpDate = tipSkuExpDate;
    }

    public boolean isNeedTipSkuInvAttr1() {
        return isNeedTipSkuInvAttr1;
    }

    public void setNeedTipSkuInvAttr1(boolean isNeedTipSkuInvAttr1) {
        this.isNeedTipSkuInvAttr1 = isNeedTipSkuInvAttr1;
    }

    public String getTipSkuInvAttr1() {
        return tipSkuInvAttr1;
    }

    public void setTipSkuInvAttr1(String tipSkuInvAttr1) {
        this.tipSkuInvAttr1 = tipSkuInvAttr1;
    }

    public boolean isNeedTipSkuInvAttr2() {
        return isNeedTipSkuInvAttr2;
    }

    public void setNeedTipSkuInvAttr2(boolean isNeedTipSkuInvAttr2) {
        this.isNeedTipSkuInvAttr2 = isNeedTipSkuInvAttr2;
    }

    public String getTipSkuInvAttr2() {
        return tipSkuInvAttr2;
    }

    public void setTipSkuInvAttr2(String tipSkuInvAttr2) {
        this.tipSkuInvAttr2 = tipSkuInvAttr2;
    }

    public boolean isNeedTipSkuInvAttr3() {
        return isNeedTipSkuInvAttr3;
    }

    public void setNeedTipSkuInvAttr3(boolean isNeedTipSkuInvAttr3) {
        this.isNeedTipSkuInvAttr3 = isNeedTipSkuInvAttr3;
    }

    public String getTipSkuInvAttr3() {
        return tipSkuInvAttr3;
    }

    public void setTipSkuInvAttr3(String tipSkuInvAttr3) {
        this.tipSkuInvAttr3 = tipSkuInvAttr3;
    }

    public boolean isNeedTipSkuInvAttr4() {
        return isNeedTipSkuInvAttr4;
    }

    public void setNeedTipSkuInvAttr4(boolean isNeedTipSkuInvAttr4) {
        this.isNeedTipSkuInvAttr4 = isNeedTipSkuInvAttr4;
    }

    public String getTipSkuInvAttr4() {
        return tipSkuInvAttr4;
    }

    public void setTipSkuInvAttr4(String tipSkuInvAttr4) {
        this.tipSkuInvAttr4 = tipSkuInvAttr4;
    }

    public boolean isNeedTipSkuInvAttr5() {
        return isNeedTipSkuInvAttr5;
    }

    public void setNeedTipSkuInvAttr5(boolean isNeedTipSkuInvAttr5) {
        this.isNeedTipSkuInvAttr5 = isNeedTipSkuInvAttr5;
    }

    public String getTipSkuInvAttr5() {
        return tipSkuInvAttr5;
    }

    public void setTipSkuInvAttr5(String tipSkuInvAttr5) {
        this.tipSkuInvAttr5 = tipSkuInvAttr5;
    }

    public boolean isNeedTipSkuSn() {
        return isNeedTipSkuSn;
    }

    public void setNeedTipSkuSn(boolean isNeedTipSkuSn) {
        this.isNeedTipSkuSn = isNeedTipSkuSn;
    }

    public String getTipSkuSn() {
        return tipSkuSn;
    }

    public void setTipSkuSn(String tipSkuSn) {
        this.tipSkuSn = tipSkuSn;
    }

    public boolean isNeedTipSkuDefect() {
        return isNeedTipSkuDefect;
    }

    public void setNeedTipSkuDefect(boolean isNeedTipSkuDefect) {
        this.isNeedTipSkuDefect = isNeedTipSkuDefect;
    }

    public String getTipSkuDefect() {
        return tipSkuDefect;
    }

    public void setTipSkuDefect(String tipSkuDefect) {
        this.tipSkuDefect = tipSkuDefect;
    }

    public boolean isNeedScanSkuSn() {
        return isNeedScanSkuSn;
    }

    public void setNeedScanSkuSn(boolean isNeedScanSkuSn) {
        this.isNeedScanSkuSn = isNeedScanSkuSn;
    }

    public boolean isAfterPutawayTipLoc() {
        return isAfterPutawayTipLoc;
    }

    public void setAfterPutawayTipLoc(boolean isAfterPutawayTipLoc) {
        this.isAfterPutawayTipLoc = isAfterPutawayTipLoc;
    }

    public boolean isNeedQueueUp() {
        return isNeedQueueUp;
    }

    public void setNeedQueueUp(boolean isNeedQueueUp) {
        this.isNeedQueueUp = isNeedQueueUp;
    }

    public boolean isRecommendFail() {
        return isRecommendFail;
    }

    public void setRecommendFail(boolean isRecommendFail) {
        this.isRecommendFail = isRecommendFail;
    }

    public boolean isAfterRecommendTipContainer() {
        return isAfterRecommendTipContainer;
    }

    public void setAfterRecommendTipContainer(boolean isAfterRecommendTipContainer) {
        this.isAfterRecommendTipContainer = isAfterRecommendTipContainer;
    }

    public boolean isCaselevelScanContainer() {
        return isCaselevelScanContainer;
    }

    public void setCaselevelScanContainer(boolean isCaselevelScanContainer) {
        this.isCaselevelScanContainer = isCaselevelScanContainer;
    }

    public boolean isNotCaselevelScanContainer() {
        return isNotCaselevelScanContainer;
    }

    public void setNotCaselevelScanContainer(boolean isNotCaselevelScanContainer) {
        this.isNotCaselevelScanContainer = isNotCaselevelScanContainer;
    }

    public boolean isScanContainer() {
        return scanContainer;
    }

    public void setScanContainer(boolean scanContainer) {
        this.scanContainer = scanContainer;
    }

    public boolean isNotContainer() {
        return isNotContainer;
    }

    public void setNotContainer(boolean isNotContainer) {
        this.isNotContainer = isNotContainer;
    }

    public boolean isScanSku() {
        return isScanSku;
    }

    public void setScanSku(boolean isScanSku) {
        this.isScanSku = isScanSku;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getTipLocBarCode() {
        return tipLocBarCode;
    }

    public void setTipLocBarCode(String tipLocBarCode) {
        this.tipLocBarCode = tipLocBarCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Boolean getIsNeedScanNewLocation() {
        return isNeedScanNewLocation;
    }

    public void setIsNeedScanNewLocation(Boolean isNeedScanNewLocation) {
        this.isNeedScanNewLocation = isNeedScanNewLocation;
    }

    public Boolean getIsContinueScanSn() {
        return isContinueScanSn;
    }

    public void setIsContinueScanSn(Boolean isContinueScanSn) {
        this.isContinueScanSn = isContinueScanSn;
    }

    
    
    
}
