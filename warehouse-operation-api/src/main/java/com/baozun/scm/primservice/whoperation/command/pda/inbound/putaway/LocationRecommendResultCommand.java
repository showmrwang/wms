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
public class LocationRecommendResultCommand extends BaseCommand {

    private static final long serialVersionUID = -7534090365486133862L;
    /** 上架模式 */
    private int putawayPatternType;
    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 内部容器Id */
    private Long insideContainerId;
    /** 内部容器号 */
    private String insideContainerCode;
    /** 外部容器Id */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 商品Id */
    private Long skuId;
    /** 商品条码 */
    private String skuBarcode;
    /** 推荐库位Id */
    private Long locationId;
    /** 推荐库位编码 */
    private String locationCode;
    /** 唯一商品 */
    private String skuAttrId;
    /** 残次条码 */
    private String defectBarcode;

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

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuBarcode() {
        return skuBarcode;
    }

    public void setSkuBarcode(String skuBarcode) {
        this.skuBarcode = skuBarcode;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getSkuAttrId() {
        return skuAttrId;
    }

    public void setSkuAttrId(String skuAttrId) {
        this.skuAttrId = skuAttrId;
    }

    public String getDefectBarcode() {
        return defectBarcode;
    }

    public void setDefectBarcode(String defectBarcode) {
        this.defectBarcode = defectBarcode;
    }



}
