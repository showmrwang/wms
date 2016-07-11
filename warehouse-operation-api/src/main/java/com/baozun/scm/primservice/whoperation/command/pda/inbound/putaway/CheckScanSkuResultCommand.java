package com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

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

/**
 * @author lichuan
 *
 */
public class CheckScanSkuResultCommand extends BaseCommand {

    private static final long serialVersionUID = -2606444046425060455L;
    /** 上架模式 */
    private int putawayPatternType;
    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 是否提示容器 */
    private boolean isNeedTipContainer;
    /** 提示容器编码 */
    private Long tipContainerId;
    /** 是否已执行上架 */
    private boolean isPutaway;
    /** 缓存是否存在 */
    private boolean isCacheExists;
    /** 是否提示商品 */
    private boolean isNeedTipSku;
    /** 提示商品条码 */
    private Long tipSkuBarcode;
    /** 是否直接核扫商品 */
    private boolean isNeedScanSku;
    /** 是否提示sn明细 */
    private boolean isNeedTipSkuSn;
    /** 提示唯一sku */
    private String tipSkuAttrId;
    /** 是否提示库位 */
    private boolean isNeedTipLoc;
    /** 提示库位id */
    private Long tipLocId;

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

    public boolean isNeedTipContainer() {
        return isNeedTipContainer;
    }

    public void setNeedTipContainer(boolean isNeedTipContainer) {
        this.isNeedTipContainer = isNeedTipContainer;
    }

    public Long getTipContainerId() {
        return tipContainerId;
    }

    public void setTipContainerId(Long tipContainerId) {
        this.tipContainerId = tipContainerId;
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

    public Long getTipSkuBarcode() {
        return tipSkuBarcode;
    }

    public void setTipSkuBarcode(Long tipSkuBarcode) {
        this.tipSkuBarcode = tipSkuBarcode;
    }

    public boolean isNeedScanSku() {
        return isNeedScanSku;
    }

    public void setNeedScanSku(boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }

    public boolean isNeedTipSkuSn() {
        return isNeedTipSkuSn;
    }

    public void setNeedTipSkuSn(boolean isNeedTipSkuSn) {
        this.isNeedTipSkuSn = isNeedTipSkuSn;
    }

    public String getTipSkuAttrId() {
        return tipSkuAttrId;
    }

    public void setTipSkuAttrId(String tipSkuAttrId) {
        this.tipSkuAttrId = tipSkuAttrId;
    }

    public boolean isNeedTipLoc() {
        return isNeedTipLoc;
    }

    public void setNeedTipLoc(boolean isNeedTipLoc) {
        this.isNeedTipLoc = isNeedTipLoc;
    }

    public Long getTipLocId() {
        return tipLocId;
    }

    public void setTipLocId(Long tipLocId) {
        this.tipLocId = tipLocId;
    }



}
