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
public class WhFunctionReplenishment extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = 1467580212048279168L;

    // columns START
    /** 对应功能ID */
    private Long functionId;
    /** 是否自动获取工作 */
    private Boolean isAutoObtainWork;
    /** 获取工作方式 工作号、库位号、容器号、出库小批次、波次号、出库箱 */
    private String obtainWorkWay;
    /** 自动获取工作最大数量 */
    private Integer maxObtainWorkQty;
    /** 是否扫描拣货库位 */
    private Boolean isScanLocation;
    /** 是否扫描拣货库位托盘 */
    private Boolean isScanOuterContainer;
    /** 是否扫描拣货库位货箱 */
    private Boolean isScanInsideContainer;
    /** 是否扫描商品 */
    private Boolean isScanSku;
    /** 扫描模式  1数量扫描 2逐件扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 是否提示商品库存属性 */
    private Boolean isTipInvAttr;
    /** 是否扫描商品库存属性 */
    private Boolean isScanInvAttr;
    /** 是否扫描出库箱 */
    private Boolean isScanOutboundbox;
    /** 整拖拣货模式 */
    private Integer palletPickingMode;
    /** 整箱拣货模式 */
    private Integer containerPickingMode;
    /** 补货上架是否需引入容器 */
    private Boolean isPutawayToContainer;
    /** 补货上架扫描模式  1数量扫描 2逐件扫描 默认数量扫描 */
    private Integer putawayScanPattern;
    /** 补货上架是否扫描商品库存属性 */
    private Boolean isPutawayScanInvAttr;
    /** 对应组织ID */
    private Long ouId;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Boolean getIsAutoObtainWork() {
        return isAutoObtainWork;
    }

    public void setIsAutoObtainWork(Boolean isAutoObtainWork) {
        this.isAutoObtainWork = isAutoObtainWork;
    }

    public String getObtainWorkWay() {
        return obtainWorkWay;
    }

    public void setObtainWorkWay(String obtainWorkWay) {
        this.obtainWorkWay = obtainWorkWay;
    }

    public Integer getMaxObtainWorkQty() {
        return maxObtainWorkQty;
    }

    public void setMaxObtainWorkQty(Integer maxObtainWorkQty) {
        this.maxObtainWorkQty = maxObtainWorkQty;
    }

    public Boolean getIsScanLocation() {
        return isScanLocation;
    }

    public void setIsScanLocation(Boolean isScanLocation) {
        this.isScanLocation = isScanLocation;
    }

    public Boolean getIsScanOuterContainer() {
        return isScanOuterContainer;
    }

    public void setIsScanOuterContainer(Boolean isScanOuterContainer) {
        this.isScanOuterContainer = isScanOuterContainer;
    }

    public Boolean getIsScanInsideContainer() {
        return isScanInsideContainer;
    }

    public void setIsScanInsideContainer(Boolean isScanInsideContainer) {
        this.isScanInsideContainer = isScanInsideContainer;
    }

    public Boolean getIsScanSku() {
        return isScanSku;
    }

    public void setIsScanSku(Boolean isScanSku) {
        this.isScanSku = isScanSku;
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

    public Boolean getIsScanOutboundbox() {
        return isScanOutboundbox;
    }

    public void setIsScanOutboundbox(Boolean isScanOutboundbox) {
        this.isScanOutboundbox = isScanOutboundbox;
    }

    public Integer getPalletPickingMode() {
        return palletPickingMode;
    }

    public void setPalletPickingMode(Integer palletPickingMode) {
        this.palletPickingMode = palletPickingMode;
    }

    public Integer getContainerPickingMode() {
        return containerPickingMode;
    }

    public void setContainerPickingMode(Integer containerPickingMode) {
        this.containerPickingMode = containerPickingMode;
    }

    public Boolean getIsPutawayToContainer() {
        return isPutawayToContainer;
    }

    public void setIsPutawayToContainer(Boolean isPutawayToContainer) {
        this.isPutawayToContainer = isPutawayToContainer;
    }

    public Integer getPutawayScanPattern() {
        return putawayScanPattern;
    }

    public void setPutawayScanPattern(Integer putawayScanPattern) {
        this.putawayScanPattern = putawayScanPattern;
    }

    public Boolean getIsPutawayScanInvAttr() {
        return isPutawayScanInvAttr;
    }

    public void setIsPutawayScanInvAttr(Boolean isPutawayScanInvAttr) {
        this.isPutawayScanInvAttr = isPutawayScanInvAttr;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

}
