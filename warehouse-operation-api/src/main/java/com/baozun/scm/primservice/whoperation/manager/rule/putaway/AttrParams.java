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
package com.baozun.scm.primservice.whoperation.manager.rule.putaway;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lichuan
 *
 */
public class AttrParams implements Serializable {
    private static final long serialVersionUID = 3902603875951982810L;

    // 库位推荐规则类型
    String lrt = "";
    // 是否跟踪容器(库位属性) --
    Boolean isTrackVessel = null;
    // 是否批次管理(库位属性) --
    Boolean isBatchMgt = null;
    // 是否管理效期(库位属性) --
    Boolean isValidMgt = null;
    // 是否允许混放(库位属性)
    Boolean isMixStacking = null;
    // 店铺库存属性还是仓库库存属性
    Boolean isStoreInvAttrFlag = null;
    // 库存属性管理
    String invAttrMgmt = "";
    // 库存类型(库存属性)
    String invType = "";
    // 库存状态(库存属性)
    Long invStatus = null;
    // 批次号(库存属性)
    String batchNumber = "";
    // 生产日期(库存属性)
    Date mfgDate = null;
    // 失效日期(库存属性)
    Date expDate = null;
    // 原产地(库存属性)
    String countryOfOrigin = "";
    // 库存属性1(库存属性)
    String invAttr1 = "";
    // 库存属性2(库存属性)
    String invAttr2 = "";
    // 库存属性3(库存属性)
    String invAttr3 = "";
    // 库存属性4(库存属性)
    String invAttr4 = "";
    // 库存属性5(库存属性)
    String invAttr5 = "";
    // 商品Id
    Long skuId = null;
    // 仓库Id
    Long ouId = null;
    // sku种类数
    Long skuCategory = null;
    // 唯一sku数
    Long skuAttrCategory = null;

    /**
     * @return the lrt
     */
    public String getLrt() {
        return lrt;
    }

    /**
     * @param lrt the lrt to set
     */
    public void setLrt(String lrt) {
        this.lrt = lrt;
    }

    /**
     * @return the isTrackVessel
     */
    public Boolean getIsTrackVessel() {
        return isTrackVessel;
    }

    /**
     * @param isTrackVessel the isTrackVessel to set
     */
    public void setIsTrackVessel(Boolean isTrackVessel) {
        this.isTrackVessel = isTrackVessel;
    }

    /**
     * @return the isBatchMgt
     */
    public Boolean getIsBatchMgt() {
        return isBatchMgt;
    }

    /**
     * @param isBatchMgt the isBatchMgt to set
     */
    public void setIsBatchMgt(Boolean isBatchMgt) {
        this.isBatchMgt = isBatchMgt;
    }

    /**
     * @return the isValidMgt
     */
    public Boolean getIsValidMgt() {
        return isValidMgt;
    }

    /**
     * @param isValidMgt the isValidMgt to set
     */
    public void setIsValidMgt(Boolean isValidMgt) {
        this.isValidMgt = isValidMgt;
    }

    /**
     * @return the isMixStacking
     */
    public Boolean getIsMixStacking() {
        return isMixStacking;
    }

    /**
     * @param isMixStacking the isMixStacking to set
     */
    public void setIsMixStacking(Boolean isMixStacking) {
        this.isMixStacking = isMixStacking;
    }

    /**
     * @return the isStoreInvAttrFlag
     */
    public Boolean getIsStoreInvAttrFlag() {
        return isStoreInvAttrFlag;
    }

    /**
     * @param isStoreInvAttrFlag the isStoreInvAttrFlag to set
     */
    public void setIsStoreInvAttrFlag(Boolean isStoreInvAttrFlag) {
        this.isStoreInvAttrFlag = isStoreInvAttrFlag;
    }

    /**
     * @return the invAttrMgmt
     */
    public String getInvAttrMgmt() {
        return invAttrMgmt;
    }

    /**
     * @param invAttrMgmt the invAttrMgmt to set
     */
    public void setInvAttrMgmt(String invAttrMgmt) {
        this.invAttrMgmt = invAttrMgmt;
    }

    /**
     * @return the invType
     */
    public String getInvType() {
        return invType;
    }

    /**
     * @param invType the invType to set
     */
    public void setInvType(String invType) {
        this.invType = invType;
    }

    /**
     * @return the invStatus
     */
    public Long getInvStatus() {
        return invStatus;
    }

    /**
     * @param invStatus the invStatus to set
     */
    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    /**
     * @return the batchNumber
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * @param batchNumber the batchNumber to set
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    /**
     * @return the mfgDate
     */
    public Date getMfgDate() {
        return mfgDate;
    }

    /**
     * @param mfgDate the mfgDate to set
     */
    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    /**
     * @return the expDate
     */
    public Date getExpDate() {
        return expDate;
    }

    /**
     * @param expDate the expDate to set
     */
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    /**
     * @return the countryOfOrigin
     */
    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    /**
     * @param countryOfOrigin the countryOfOrigin to set
     */
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    /**
     * @return the invAttr1
     */
    public String getInvAttr1() {
        return invAttr1;
    }

    /**
     * @param invAttr1 the invAttr1 to set
     */
    public void setInvAttr1(String invAttr1) {
        this.invAttr1 = invAttr1;
    }

    /**
     * @return the invAttr2
     */
    public String getInvAttr2() {
        return invAttr2;
    }

    /**
     * @param invAttr2 the invAttr2 to set
     */
    public void setInvAttr2(String invAttr2) {
        this.invAttr2 = invAttr2;
    }

    /**
     * @return the invAttr3
     */
    public String getInvAttr3() {
        return invAttr3;
    }

    /**
     * @param invAttr3 the invAttr3 to set
     */
    public void setInvAttr3(String invAttr3) {
        this.invAttr3 = invAttr3;
    }

    /**
     * @return the invAttr4
     */
    public String getInvAttr4() {
        return invAttr4;
    }

    /**
     * @param invAttr4 the invAttr4 to set
     */
    public void setInvAttr4(String invAttr4) {
        this.invAttr4 = invAttr4;
    }

    /**
     * @return the invAttr5
     */
    public String getInvAttr5() {
        return invAttr5;
    }

    /**
     * @param invAttr5 the invAttr5 to set
     */
    public void setInvAttr5(String invAttr5) {
        this.invAttr5 = invAttr5;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getSkuCategory() {
        return skuCategory;
    }

    public void setSkuCategory(Long skuCategory) {
        this.skuCategory = skuCategory;
    }

    public Long getSkuAttrCategory() {
        return skuAttrCategory;
    }

    public void setSkuAttrCategory(Long skuAttrCategory) {
        this.skuAttrCategory = skuAttrCategory;
    }


}
