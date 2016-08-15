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
    /** 是否外部容器库存 */
    private Boolean isOutContainerInv;
    /** 货箱容器号 */
    private String binContainerCode;
    /** 是否整箱上架 */
    private Boolean isEntireBinPutaway;
    /** 返回的url */
    private String returnURL;
    /** 对应功能ID */
    private Long functionId;
    /** 库存ID */
    private Long locationId;
    /** 是否静态库存 */
    private Boolean isStatic;
    /** 是否允许混放 */
    private Boolean isMixStacking;
    /** 库位条码 */
    private String barCode;
    /** 库存状态*/
    private Long statusId;
    /** sku条码*/
    private String skuBarcode;
   
    
    
    
    

    
    
    
    
    /** 上架模式 系统指导上架 人为指定上架 系统建议上架 */
    private Integer putawayPattern;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 是否整托上架 */
    private Boolean isEntireTrayPutaway;
    /** CASELEVEL是否需要扫描SKU */
    private Boolean isCaselevelScanSku;
    /** 非CASELEVEL是否需要扫描SKU */
    private Boolean isNotcaselevelScanSku;




    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public Boolean getIsOutContainerInv() {
        return isOutContainerInv;
    }

    public void setIsOutContainerInv(Boolean isOutContainerInv) {
        this.isOutContainerInv = isOutContainerInv;
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

    public Integer getPutawayPattern() {
        return putawayPattern;
    }

    public void setPutawayPattern(Integer putawayPattern) {
        this.putawayPattern = putawayPattern;
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

    public Boolean getIsEntireTrayPutaway() {
        return isEntireTrayPutaway;
    }

    public void setIsEntireTrayPutaway(Boolean isEntireTrayPutaway) {
        this.isEntireTrayPutaway = isEntireTrayPutaway;
    }

    public Boolean getIsEntireBinPutaway() {
        return isEntireBinPutaway;
    }

    public void setIsEntireBinPutaway(Boolean isEntireBinPutaway) {
        this.isEntireBinPutaway = isEntireBinPutaway;
    }

    public Boolean getIsCaselevelScanSku() {
        return isCaselevelScanSku;
    }

    public void setIsCaselevelScanSku(Boolean isCaselevelScanSku) {
        this.isCaselevelScanSku = isCaselevelScanSku;
    }

    public Boolean getIsNotcaselevelScanSku() {
        return isNotcaselevelScanSku;
    }

    public void setIsNotcaselevelScanSku(Boolean isNotcaselevelScanSku) {
        this.isNotcaselevelScanSku = isNotcaselevelScanSku;
    }

    public String getBinContainerCode() {
        return binContainerCode;
    }

    public void setBinContainerCode(String binContainerCode) {
        this.binContainerCode = binContainerCode;
    }


    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Boolean getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(Boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Boolean getIsMixStacking() {
        return isMixStacking;
    }

    public void setIsMixStacking(Boolean isMixStacking) {
        this.isMixStacking = isMixStacking;
    }

    public String getBarCode() {
        return barCode;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getSkuBarcode() {
        return skuBarcode;
    }

    public void setSkuBarcode(String skuBarcode) {
        this.skuBarcode = skuBarcode;
    }
}

