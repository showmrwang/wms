package com.baozun.scm.primservice.whoperation.command.pda.work;

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
 * @author tangming
 *
 */
public class CheckScanResultCommand extends BaseCommand {

    private static final long serialVersionUID = -2606444046425060455L;
    /** 拣货模式 */
    private int pickingWay;
    /** 库位上外部容器是否扫描完毕*/
    private Boolean isNeedTipOutContainer =false;
    /**库位上内部容器是否扫描完毕*/
    private Boolean isNeedTipInsideContainer =false;
    /**外部容器id*/
    private Long tipOuterContainerId;
    /**内部容器id*/
    private Long tipiInsideContainerId;
    /** 所有库位已经全部拣货完成 */
    private Boolean isPicking =false;
    /** 缓存是否存在 */
    private Boolean isCacheExists;
    /** 提示商品条码 */
    private Long tipSkuBarcode;
    /** 是否直接核扫商品 */
    private Boolean isNeedScanSku = false;
    /** 是否扫描sn明细 */
    private Boolean isNeedScanSkuSn = false; //默认不继续扫描sn
    /** 提示唯一sku */
    private String tipSkuAttrId;
    /**唯一sku,sn及残次条码*/
    private String tipSkuAttrIdSnDefect;
    /** 是否提示库位 (所有的库位是否已经扫描完毕)*/
    private Boolean isNeedTipLoc =false;
    /**提示库位id*/
    private Long tipLocationId;
    /**出库箱号*/
    private String outBounxBoxCode;
    /**是否需要扫描出库箱*/
    private Boolean isNeedScanOutBounxBox;
    /**货格号*/
    private Integer useContainerLatticeNo;
    
    private Boolean isHaveOuterContainer = true;  //货箱是否有外部容器
    
    private Boolean isHaveInsideContainer = true; //sku是否有内部容器
    
    private Boolean isScanLatticeNo = false; // 是否扫描货格
    
    
    
    public int getPickingWay() {
        return pickingWay;
    }
    public void setPickingWay(int pickingWay) {
        this.pickingWay = pickingWay;
    }
    public Boolean getIsNeedTipOutContainer() {
        return isNeedTipOutContainer;
    }
    public void setIsNeedTipOutContainer(Boolean isNeedTipOutContainer) {
        this.isNeedTipOutContainer = isNeedTipOutContainer;
    }
    public Boolean getIsNeedTipInsideContainer() {
        return isNeedTipInsideContainer;
    }
    public void setIsNeedTipInsideContainer(Boolean isNeedTipInsideContainer) {
        this.isNeedTipInsideContainer = isNeedTipInsideContainer;
    }
    public Long getTipOuterContainerId() {
        return tipOuterContainerId;
    }
    public void setTipOuterContainerId(Long tipOuterContainerId) {
        this.tipOuterContainerId = tipOuterContainerId;
    }
    public Long getTipiInsideContainerId() {
        return tipiInsideContainerId;
    }
    public void setTipiInsideContainerId(Long tipiInsideContainerId) {
        this.tipiInsideContainerId = tipiInsideContainerId;
    }
    public Boolean getIsPicking() {
        return isPicking;
    }
    public void setIsPicking(Boolean isPicking) {
        this.isPicking = isPicking;
    }
    public Boolean getIsCacheExists() {
        return isCacheExists;
    }
    public void setIsCacheExists(Boolean isCacheExists) {
        this.isCacheExists = isCacheExists;
    }
    public Long getTipSkuBarcode() {
        return tipSkuBarcode;
    }
    public void setTipSkuBarcode(Long tipSkuBarcode) {
        this.tipSkuBarcode = tipSkuBarcode;
    }
    public Boolean getIsNeedScanSku() {
        return isNeedScanSku;
    }
    public void setIsNeedScanSku(Boolean isNeedScanSku) {
        this.isNeedScanSku = isNeedScanSku;
    }
    public String getTipSkuAttrId() {
        return tipSkuAttrId;
    }
    public void setTipSkuAttrId(String tipSkuAttrId) {
        this.tipSkuAttrId = tipSkuAttrId;
    }
    public Boolean getIsNeedTipLoc() {
        return isNeedTipLoc;
    }
    public void setIsNeedTipLoc(Boolean isNeedTipLoc) {
        this.isNeedTipLoc = isNeedTipLoc;
    }
    public Long getTipLocationId() {
        return tipLocationId;
    }
    public void setTipLocationId(Long tipLocationId) {
        this.tipLocationId = tipLocationId;
    }
    public String getOutBounxBoxCode() {
        return outBounxBoxCode;
    }
    public void setOutBounxBoxCode(String outBounxBoxCode) {
        this.outBounxBoxCode = outBounxBoxCode;
    }
    public Boolean getIsNeedScanOutBounxBox() {
        return isNeedScanOutBounxBox;
    }
    public void setIsNeedScanOutBounxBox(Boolean isNeedScanOutBounxBox) {
        this.isNeedScanOutBounxBox = isNeedScanOutBounxBox;
    }
    public Integer getUseContainerLatticeNo() {
        return useContainerLatticeNo;
    }
    public void setUseContainerLatticeNo(Integer useContainerLatticeNo) {
        this.useContainerLatticeNo = useContainerLatticeNo;
    }
    public String getTipSkuAttrIdSnDefect() {
        return tipSkuAttrIdSnDefect;
    }
    public void setTipSkuAttrIdSnDefect(String tipSkuAttrIdSnDefect) {
        this.tipSkuAttrIdSnDefect = tipSkuAttrIdSnDefect;
    }
    public Boolean getIsHaveOuterContainer() {
        return isHaveOuterContainer;
    }
    public void setIsHaveOuterContainer(Boolean isHaveOuterContainer) {
        this.isHaveOuterContainer = isHaveOuterContainer;
    }
    public Boolean getIsHaveInsideContainer() {
        return isHaveInsideContainer;
    }
    public void setIsHaveInsideContainer(Boolean isHaveInsideContainer) {
        this.isHaveInsideContainer = isHaveInsideContainer;
    }
    public Boolean getIsNeedScanSkuSn() {
        return isNeedScanSkuSn;
    }
    public void setIsNeedScanSkuSn(Boolean isNeedScanSkuSn) {
        this.isNeedScanSkuSn = isNeedScanSkuSn;
    }
    public Boolean getIsScanLatticeNo() {
        return isScanLatticeNo;
    }
    public void setIsScanLatticeNo(Boolean isScanLatticeNo) {
        this.isScanLatticeNo = isScanLatticeNo;
    }
    
}
