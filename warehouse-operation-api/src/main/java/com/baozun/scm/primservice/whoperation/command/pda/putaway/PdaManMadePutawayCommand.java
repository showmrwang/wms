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
    /** 是否外部容器库存 ,true时，外部容器,false时内部容器*/
    private Boolean isOuterContainer;
    /** 对应功能ID */
    private Long functionId;
    /** 库存ID */
    private Long locationId;
    /** 库位条码 */
    private String barCode;
    /** 库位编码 */
    private String locationCode;
    
    private String insideContainerCode;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 上架类型 */
    private Integer putawayPatternDetailType;
    /**是否上架*/
    private Boolean putway;
    /**托盘内是否还有没扫描到容器*/
    private Boolean isNeedScanContainer;
    
    private Boolean isNeedScanSku;  //是否需要扫描sku
    
    private String tipContainerCode;
    
    private Boolean isTipContainerCode;  //是否提示容器号 
    
    private Boolean isAfterPutawayTipContianer;
    
    private Boolean isInboundLocationBarcode;    //上架是否启用校验码false：否 true：是 
    

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
    
    
    
    
}

