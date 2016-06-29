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
    /** 上架以后是否提示下一个容器 */
    private boolean isAfterPutawayTipContianer;


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

    public boolean isAfterPutawayTipContianer() {
        return isAfterPutawayTipContianer;
    }

    public void setAfterPutawayTipContianer(boolean isAfterPutawayTipContianer) {
        this.isAfterPutawayTipContianer = isAfterPutawayTipContianer;
    }


}
