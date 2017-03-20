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
 * （t_wh_function_outbound）
 * 
 * @author larkark
 *
 */
public class WhFunctionOutBound extends BaseModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3798555340974463442L;
    
    // columns START
    /** 对应功能ID */
    private Long functionId;
    /** 功能模块多选：复核、称重、交接 */
    private String functionModule;
    /** 多选：首单复核、副品复核、按单复核、按箱复核（复核） */
    private String checkingMode;
    /** 首单复核是否清点总数（复核） */
    private Boolean isFocCountNums;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描（复核） */
    private Integer scanPattern;
    /** 是否提示商品库存属性（复核） */
    private Boolean isTipInvAttr;
    /** 是否扫描商品库存属性（复核） */
    private Boolean isScanInvAttr;
    /** 是否允许引进出库箱号（复核） */
    private Boolean isCheckingToOutboundbox;
    /** 是否自动生成出库箱号（复核） */
    private Boolean isAutoGenerateOutboundbox;
    /** 是否扫描出库箱号（复核） */
    private Boolean isScanOutboundbox;
    /** 多选：装箱清单、销售清单、面单、箱标签、发票（复核） */
    private String checkingPrint;
    /** 扫描商品是否拍照（复核） */
    private Boolean isScanSkuPhotograph;
    /** 多选：原产地、批次号、生产日期、失效日期、序列号、残次条码（复核） */
    private String scanInvAttrPhotograph;
    /** 面单、箱标签（称重） */
    private String weighingPrint;
    /** 是否校验称重和计重（称重） */
    private Boolean isValidateWeight;
    /** 重量浮动百分比（称重） */
    private Integer weightFloatPercentage;
    /** 单选：首单复核、副品复核、按单复核、按箱复核（复核&称重） */
    private String wayBillPrintType;
    /** 是否扫描面单（复核&称重） */
    private Boolean isScanWayBill;
    /** 交接清单（交接） */
    private String handoverPrint;
    /** 是否提示交接工位（交接） */
    private Integer isTipHandoverStation;
    /** 对应组织ID */
    private Long ouId;
    // columns END
    
    
    public Long getFunctionId() {
        return functionId;
    }
    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
    public String getFunctionModule() {
        return functionModule;
    }
    public void setFunctionModule(String functionModule) {
        this.functionModule = functionModule;
    }
    public String getCheckingMode() {
        return checkingMode;
    }
    public void setCheckingMode(String checkingMode) {
        this.checkingMode = checkingMode;
    }
    public Boolean getIsFocCountNums() {
        return isFocCountNums;
    }
    public void setIsFocCountNums(Boolean isFocCountNums) {
        this.isFocCountNums = isFocCountNums;
    }
    public Integer getScanPattern() {
        return scanPattern;
    }
    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }
    public Boolean getIsTipInvAttr() {
        return isTipInvAttr;
    }
    public void setIsTipInvAttr(Boolean isTipInvAttr) {
        this.isTipInvAttr = isTipInvAttr;
    }
    public Boolean getIsScanInvAttr() {
        return isScanInvAttr;
    }
    public void setIsScanInvAttr(Boolean isScanInvAttr) {
        this.isScanInvAttr = isScanInvAttr;
    }
    public Boolean getIsCheckingToOutboundbox() {
        return isCheckingToOutboundbox;
    }
    public void setIsCheckingToOutboundbox(Boolean isCheckingToOutboundbox) {
        this.isCheckingToOutboundbox = isCheckingToOutboundbox;
    }
    public Boolean getIsAutoGenerateOutboundbox() {
        return isAutoGenerateOutboundbox;
    }
    public void setIsAutoGenerateOutboundbox(Boolean isAutoGenerateOutboundbox) {
        this.isAutoGenerateOutboundbox = isAutoGenerateOutboundbox;
    }
    public Boolean getIsScanOutboundbox() {
        return isScanOutboundbox;
    }
    public void setIsScanOutboundbox(Boolean isScanOutboundbox) {
        this.isScanOutboundbox = isScanOutboundbox;
    }
    public String getCheckingPrint() {
        return checkingPrint;
    }
    public void setCheckingPrint(String checkingPrint) {
        this.checkingPrint = checkingPrint;
    }
    public Boolean getIsScanSkuPhotograph() {
        return isScanSkuPhotograph;
    }
    public void setIsScanSkuPhotograph(Boolean isScanSkuPhotograph) {
        this.isScanSkuPhotograph = isScanSkuPhotograph;
    }
    public String getScanInvAttrPhotograph() {
        return scanInvAttrPhotograph;
    }
    public void setScanInvAttrPhotograph(String scanInvAttrPhotograph) {
        this.scanInvAttrPhotograph = scanInvAttrPhotograph;
    }
    public String getWeighingPrint() {
        return weighingPrint;
    }
    public void setWeighingPrint(String weighingPrint) {
        this.weighingPrint = weighingPrint;
    }
    public Boolean getIsValidateWeight() {
        return isValidateWeight;
    }
    public void setIsValidateWeight(Boolean isValidateWeight) {
        this.isValidateWeight = isValidateWeight;
    }
    public Integer getWeightFloatPercentage() {
        return weightFloatPercentage;
    }
    public void setWeightFloatPercentage(Integer weightFloatPercentage) {
        this.weightFloatPercentage = weightFloatPercentage;
    }
    public String getWayBillPrintType() {
        return wayBillPrintType;
    }
    public void setWayBillPrintType(String wayBillPrintType) {
        this.wayBillPrintType = wayBillPrintType;
    }
    public Boolean getIsScanWayBill() {
        return isScanWayBill;
    }
    public void setIsScanWayBill(Boolean isScanWayBill) {
        this.isScanWayBill = isScanWayBill;
    }
    public String getHandoverPrint() {
        return handoverPrint;
    }
    public void setHandoverPrint(String handoverPrint) {
        this.handoverPrint = handoverPrint;
    }
    public Integer getIsTipHandoverStation() {
        return isTipHandoverStation;
    }
    public void setIsTipHandoverStation(Integer isTipHandoverStation) {
        this.isTipHandoverStation = isTipHandoverStation;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    
}
