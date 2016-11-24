/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.command.pda.putaway;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * PDA人为指定上架 传参Command
 *
 * @author lijun.shen
 *
 */
public class PdaManMadePutawayCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -8669489713555504047L;


    
    /** 仓库组织ID */
    private Long ouId;
    /** 操作人ID */
    private Long userId;
    /** 容器号 */
    private String containerCode;
    private String trackContainerCode;  //跟踪容器号
    /** 是否外部容器库存 ,true时，外部容器库存,false时内部容器库存*/
    private Boolean isOuterContainer;
    /** 对应功能ID */
    private Long functionId;
    /** 库存ID */
    private Long locationId;
    /** 库位条码 */
    private String barCode;
    /** 库位编码 */
    private String locationCode;
    /**内部容器号*/
    private String insideContainerCode;
    /**外部容器号*/
    private String outerContainerCode;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 上架类型 */
    private Integer putawayPatternDetailType;
    
    private Boolean isTrackVessel = false;   //是否跟踪容器号，默认跟踪容器号
    
    private Boolean isScanTrackContainer;   //是否扫描跟踪容器
    /**是否上架*/
    private Boolean putway = false;
    /**托盘内是否还有没扫描到容器*/
    private Boolean isNeedScanContainer = false;
    
    private Boolean isAfterPutawayContainer = false; //整箱是否还有未上架的容器
    
    private Boolean isAfterPutawaySku = false; //拆箱上架，上架成功
    
    private Boolean isNeedScanSku =false;  //是否需要扫描sku
    
    private String tipContainerCode;
    
    private Boolean isScanOuterContainer = false;  //整箱上架是否已经扫外部容器(如果存在外部容器)
    
    private Boolean isTipInsideContainer = false;  //是否提示内部容器
    
    private Boolean isInboundLocationBarcode;    //上架是否启用校验码false：否 true：是 
    /** 是否需要扫商品属性 */
    private Boolean isNeedSkuDetail;   //默认false,相同sku不存在不同库存属性数据,true时,相同su存在不同的库存属性
    /** 是否需要扫商品的库存类型 */
    private Boolean isNeedScanSkuInvType = false;
    /** 提示商品库存类型 */
    private String skuInvType = "";
    /** 是否需要扫商品的库存状态 */
    private Boolean isNeedScanSkuInvStatus = false;
    /** 提示商品库存状态 */
    private String skuInvStatus = "";
    /**是否需要扫描批次号*/
    private Boolean isNeedScanBatchNumber = false;
    /**批次号*/
    private String batchNumber;
    /**是否需要扫描原产地*/
    private Boolean isNeedScanOrigin = false;   //是否需要扫描原产地
   /**sku原产地*/
    private String skuOrigin;
    /** 是否需要扫商品的生产日期 */
    private Boolean isNeedScanSkuMfgDate = false;
    /** 提示商品生产日期 */
    private Date skuMfgDate;
    /** 是否需要扫商品过期日期 */
    private Boolean isNeedScanSkuExpDate = false;
    /** 提示商品过期日期 */
    private Date skuExpDate;
    /** 是否需要扫商品库存属性1 */
    private Boolean isNeedScanSkuInvAttr1 = false;
    /** 提示商品库存属性1 */
    private String skuInvAttr1 = "";
    /** 是否需要扫商品库存属性2 */
    private Boolean isNeedScanSkuInvAttr2 = false;
    /** 提示商品库存属性2 */
    private String skuInvAttr2 = "";
    /** 是否需要扫商品库存属性3 */
    private Boolean isNeedScanSkuInvAttr3 = false;
    /** 提示商品库存属性3 */
    private String skuInvAttr3;
    /** 是否需要扫商品库存属性4 */
    private Boolean isNeedScanSkuInvAttr4 = false;
    /** 提示商品库存属性4 */
    private String skuInvAttr4 = "";
    /** 是否需要扫商品库存属性5 */
    private Boolean isNeedScanSkuInvAttr5 = false;
    /** 提示商品库存属性5 */
    private String skuInvAttr5 = "";
    /** 是否需要扫描Sn */
    private Boolean isNeedScanSn;
    /**是否需要扫残次信息*/
    private Boolean isNeedScanDefect;
    /**sku条码*/
    private String skuBarCode; 
    
    private Double scanSkuQty;
    
    private Boolean isScanSkuSnDefect = false;  //是否需要扫描商品的sn/残次信息
    
    private String skuSnCode;   //sn/残次信息
    
    private String skuDefectCode;   //sku残次信息
    
    private Long skuId;

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getTrackContainerCode() {
        return trackContainerCode;
    }

    public void setTrackContainerCode(String trackContainerCode) {
        this.trackContainerCode = trackContainerCode;
    }

    public Boolean getIsOuterContainer() {
        return isOuterContainer;
    }

    public void setIsOuterContainer(Boolean isOuterContainer) {
        this.isOuterContainer = isOuterContainer;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
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

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Integer getPutawayPatternDetailType() {
        return putawayPatternDetailType;
    }

    public void setPutawayPatternDetailType(Integer putawayPatternDetailType) {
        this.putawayPatternDetailType = putawayPatternDetailType;
    }

    public Boolean getIsTrackVessel() {
        return isTrackVessel;
    }

    public void setIsTrackVessel(Boolean isTrackVessel) {
        this.isTrackVessel = isTrackVessel;
    }

    public Boolean getIsScanTrackContainer() {
        return isScanTrackContainer;
    }

    public void setIsScanTrackContainer(Boolean isScanTrackContainer) {
        this.isScanTrackContainer = isScanTrackContainer;
    }

    public Boolean getPutway() {
        return putway;
    }

    public void setPutway(Boolean putway) {
        this.putway = putway;
    }

    public Boolean getIsNeedScanContainer() {
        return isNeedScanContainer;
    }

    public void setIsNeedScanContainer(Boolean isNeedScanContainer) {
        this.isNeedScanContainer = isNeedScanContainer;
    }

    public Boolean getIsAfterPutawayContainer() {
        return isAfterPutawayContainer;
    }

    public void setIsAfterPutawayContainer(Boolean isAfterPutawayContainer) {
        this.isAfterPutawayContainer = isAfterPutawayContainer;
    }

    public Boolean getIsAfterPutawaySku() {
        return isAfterPutawaySku;
    }

    public void setIsAfterPutawaySku(Boolean isAfterPutawaySku) {
        this.isAfterPutawaySku = isAfterPutawaySku;
    }

    public Boolean getIsNeedScanSku() {
        return isNeedScanSku;
    }

    public void setIsNeedScanSku(Boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }

    public String getTipContainerCode() {
        return tipContainerCode;
    }

    public void setTipContainerCode(String tipContainerCode) {
        this.tipContainerCode = tipContainerCode;
    }

    public Boolean getIsScanOuterContainer() {
        return isScanOuterContainer;
    }

    public void setIsScanOuterContainer(Boolean isScanOuterContainer) {
        this.isScanOuterContainer = isScanOuterContainer;
    }

    public Boolean getIsTipInsideContainer() {
        return isTipInsideContainer;
    }

    public void setIsTipInsideContainer(Boolean isTipInsideContainer) {
        this.isTipInsideContainer = isTipInsideContainer;
    }

    public Boolean getIsInboundLocationBarcode() {
        return isInboundLocationBarcode;
    }

    public void setIsInboundLocationBarcode(Boolean isInboundLocationBarcode) {
        this.isInboundLocationBarcode = isInboundLocationBarcode;
    }

    public Boolean getIsNeedSkuDetail() {
        return isNeedSkuDetail;
    }

    public void setIsNeedSkuDetail(Boolean isNeedSkuDetail) {
        this.isNeedSkuDetail = isNeedSkuDetail;
    }

    public Boolean getIsNeedScanSkuInvType() {
        return isNeedScanSkuInvType;
    }

    public void setIsNeedScanSkuInvType(Boolean isNeedScanSkuInvType) {
        this.isNeedScanSkuInvType = isNeedScanSkuInvType;
    }

    public String getSkuInvType() {
        return skuInvType;
    }

    public void setSkuInvType(String skuInvType) {
        this.skuInvType = skuInvType;
    }

    public Boolean getIsNeedScanSkuInvStatus() {
        return isNeedScanSkuInvStatus;
    }

    public void setIsNeedScanSkuInvStatus(Boolean isNeedScanSkuInvStatus) {
        this.isNeedScanSkuInvStatus = isNeedScanSkuInvStatus;
    }

    public String getSkuInvStatus() {
        return skuInvStatus;
    }

    public void setSkuInvStatus(String skuInvStatus) {
        this.skuInvStatus = skuInvStatus;
    }

    public Boolean getIsNeedScanBatchNumber() {
        return isNeedScanBatchNumber;
    }

    public void setIsNeedScanBatchNumber(Boolean isNeedScanBatchNumber) {
        this.isNeedScanBatchNumber = isNeedScanBatchNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Boolean getIsNeedScanOrigin() {
        return isNeedScanOrigin;
    }

    public void setIsNeedScanOrigin(Boolean isNeedScanOrigin) {
        this.isNeedScanOrigin = isNeedScanOrigin;
    }

    public String getSkuOrigin() {
        return skuOrigin;
    }

    public void setSkuOrigin(String skuOrigin) {
        this.skuOrigin = skuOrigin;
    }

    public Boolean getIsNeedScanSkuMfgDate() {
        return isNeedScanSkuMfgDate;
    }

    public void setIsNeedScanSkuMfgDate(Boolean isNeedScanSkuMfgDate) {
        this.isNeedScanSkuMfgDate = isNeedScanSkuMfgDate;
    }

    public Date getSkuMfgDate() {
        return skuMfgDate;
    }

    public void setSkuMfgDate(Date skuMfgDate) {
        this.skuMfgDate = skuMfgDate;
    }

    public Boolean getIsNeedScanSkuExpDate() {
        return isNeedScanSkuExpDate;
    }

    public void setIsNeedScanSkuExpDate(Boolean isNeedScanSkuExpDate) {
        this.isNeedScanSkuExpDate = isNeedScanSkuExpDate;
    }

    public Date getSkuExpDate() {
        return skuExpDate;
    }

    public void setSkuExpDate(Date skuExpDate) {
        this.skuExpDate = skuExpDate;
    }

    public Boolean getIsNeedScanSkuInvAttr1() {
        return isNeedScanSkuInvAttr1;
    }

    public void setIsNeedScanSkuInvAttr1(Boolean isNeedScanSkuInvAttr1) {
        this.isNeedScanSkuInvAttr1 = isNeedScanSkuInvAttr1;
    }

    public String getSkuInvAttr1() {
        return skuInvAttr1;
    }

    public void setSkuInvAttr1(String skuInvAttr1) {
        this.skuInvAttr1 = skuInvAttr1;
    }

    public Boolean getIsNeedScanSkuInvAttr2() {
        return isNeedScanSkuInvAttr2;
    }

    public void setIsNeedScanSkuInvAttr2(Boolean isNeedScanSkuInvAttr2) {
        this.isNeedScanSkuInvAttr2 = isNeedScanSkuInvAttr2;
    }

    public String getSkuInvAttr2() {
        return skuInvAttr2;
    }

    public void setSkuInvAttr2(String skuInvAttr2) {
        this.skuInvAttr2 = skuInvAttr2;
    }

    public Boolean getIsNeedScanSkuInvAttr3() {
        return isNeedScanSkuInvAttr3;
    }

    public void setIsNeedScanSkuInvAttr3(Boolean isNeedScanSkuInvAttr3) {
        this.isNeedScanSkuInvAttr3 = isNeedScanSkuInvAttr3;
    }

    public String getSkuInvAttr3() {
        return skuInvAttr3;
    }

    public void setSkuInvAttr3(String skuInvAttr3) {
        this.skuInvAttr3 = skuInvAttr3;
    }

    public Boolean getIsNeedScanSkuInvAttr4() {
        return isNeedScanSkuInvAttr4;
    }

    public void setIsNeedScanSkuInvAttr4(Boolean isNeedScanSkuInvAttr4) {
        this.isNeedScanSkuInvAttr4 = isNeedScanSkuInvAttr4;
    }

    public String getSkuInvAttr4() {
        return skuInvAttr4;
    }

    public void setSkuInvAttr4(String skuInvAttr4) {
        this.skuInvAttr4 = skuInvAttr4;
    }

    public Boolean getIsNeedScanSkuInvAttr5() {
        return isNeedScanSkuInvAttr5;
    }

    public void setIsNeedScanSkuInvAttr5(Boolean isNeedScanSkuInvAttr5) {
        this.isNeedScanSkuInvAttr5 = isNeedScanSkuInvAttr5;
    }

    public String getSkuInvAttr5() {
        return skuInvAttr5;
    }

    public void setSkuInvAttr5(String skuInvAttr5) {
        this.skuInvAttr5 = skuInvAttr5;
    }

    public Boolean getIsNeedScanSn() {
        return isNeedScanSn;
    }

    public void setIsNeedScanSn(Boolean isNeedScanSn) {
        this.isNeedScanSn = isNeedScanSn;
    }

    public Boolean getIsNeedScanDefect() {
        return isNeedScanDefect;
    }

    public void setIsNeedScanDefect(Boolean isNeedScanDefect) {
        this.isNeedScanDefect = isNeedScanDefect;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public Double getScanSkuQty() {
        return scanSkuQty;
    }

    public void setScanSkuQty(Double scanSkuQty) {
        this.scanSkuQty = scanSkuQty;
    }

    public Boolean getIsScanSkuSnDefect() {
        return isScanSkuSnDefect;
    }

    public void setIsScanSkuSnDefect(Boolean isScanSkuSnDefect) {
        this.isScanSkuSnDefect = isScanSkuSnDefect;
    }

    public String getSkuSnCode() {
        return skuSnCode;
    }

    public void setSkuSnCode(String skuSnCode) {
        this.skuSnCode = skuSnCode;
    }

    public String getSkuDefectCode() {
        return skuDefectCode;
    }

    public void setSkuDefectCode(String skuDefectCode) {
        this.skuDefectCode = skuDefectCode;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
    

    
    

  
    
}

