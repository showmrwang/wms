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
    /** 容器ID */
    private Long containerId;
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
    /**是否上架*/
    private Boolean putway = false;
    /**托盘内是否还有没扫描到容器*/
    private Boolean isNeedScanContainer = false;
    
    private Boolean isNeedScanSku;  //是否需要扫描sku
    
    private String tipContainerCode;
    
    private Boolean isTipContainerCode;  //是否提示容器号 
    
    private Boolean isAfterPutawayTipContianer;
    
    private Boolean isInboundLocationBarcode;    //上架是否启用校验码false：否 true：是 
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
    /**sku条码*/
    private String skuBarCode; 

    public Boolean getIsOuterContainer() {
        return isOuterContainer;
    }

    public void setIsOuterContainer(Boolean isOuterContainer) {
        this.isOuterContainer = isOuterContainer;
    }

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

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }


    public Boolean getPutway() {
        return putway;
    }

    public void setPutway(Boolean putway) {
        this.putway = putway;
    }

    public Boolean getIsNeedScanSku() {
        return isNeedScanSku;
    }

    public void setIsNeedScanSku(Boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }

    public Boolean getIsNeedScanContainer() {
        return isNeedScanContainer;
    }

    public void setIsNeedScanContainer(Boolean isNeedScanContainer) {
        this.isNeedScanContainer = isNeedScanContainer;
    }

    public String getTipContainerCode() {
        return tipContainerCode;
    }

    public void setTipContainerCode(String tipContainerCode) {
        this.tipContainerCode = tipContainerCode;
    }

    public Boolean getIsTipContainerCode() {
        return isTipContainerCode;
    }

    public void setIsTipContainerCode(Boolean isTipContainerCode) {
        this.isTipContainerCode = isTipContainerCode;
    }

    public Boolean getIsAfterPutawayTipContianer() {
        return isAfterPutawayTipContianer;
    }

    public void setIsAfterPutawayTipContianer(Boolean isAfterPutawayTipContianer) {
        this.isAfterPutawayTipContianer = isAfterPutawayTipContianer;
    }

    public Boolean getIsInboundLocationBarcode() {
        return isInboundLocationBarcode;
    }

    public void setIsInboundLocationBarcode(Boolean isInboundLocationBarcode) {
        this.isInboundLocationBarcode = isInboundLocationBarcode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Integer getPutawayPatternDetailType() {
        return putawayPatternDetailType;
    }

    public void setPutawayPatternDetailType(Integer putawayPatternDetailType) {
        this.putawayPatternDetailType = putawayPatternDetailType;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
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

    public int getTipSkuQty() {
        return tipSkuQty;
    }

    public void setTipSkuQty(int tipSkuQty) {
        this.tipSkuQty = tipSkuQty;
    }

    public boolean isNeedTipSkuDetail() {
        return isNeedTipSkuDetail;
    }

    public void setNeedTipSkuDetail(boolean isNeedTipSkuDetail) {
        this.isNeedTipSkuDetail = isNeedTipSkuDetail;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }
    
    
    
    
}

