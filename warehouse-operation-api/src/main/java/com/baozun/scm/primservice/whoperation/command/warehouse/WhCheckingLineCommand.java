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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class WhCheckingLineCommand extends BaseCommand {


    private static final long serialVersionUID = 8095167255432777708L;

    /** 主键ID */
    private Long id;
    /** 复核ID */
    private Long checkingId;
    /** 商品编码 */
    private String skuCode;
    /** 商品外部编码 */
    private String skuExtCode;
    /** 商品条码 */
    private String skuBarCode;
    /** 商品名称 */
    private String skuName;
    /** 计划数量 */
    private Long qty;
    /** 已复核数量 */
    private Long checkingQty;
    /** 客户CODE */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺CODE */
    private String storeCode;
    /** 店铺名称 */
    private String storeName;
    /** 库存状态 */
    private String invStatus;
    /** 库存类型 */
    private String invType;
    /** 批次号 */
    private String batchNumber;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存属性1 */
    private String invAttr1;
    /** 库存属性2 */
    private String invAttr2;
    /** 库存属性3 */
    private String invAttr3;
    /** 库存属性4 */
    private String invAttr4;
    /** 库存属性5 */
    private String invAttr5;
    /** 内部对接码 */
    private String uuid;
    /** 仓库组织ID */
    private Long ouId;
    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;
    /** 商品属性字符串*/
    private String SkuAttr;
    /** 商品id*/
    private Long skuId;
    /** 播种墙id*/
    private Long facilityId;
    /** 出库箱code*/
    private String outboundboxCode;
    /** 播种墙编码*/
    private String seedingWallCode;
    /** 小车编码*/
    private String outerContainerCode;
    // 字典表str
    /** 库存状态str*/
    private String invStatusStr = new String();
    /** 库存类型str*/
    private String invTypeStr = new String();
    /** 库存属性1str*/
    private String invAttr1Str = new String();
    /** 库存属性2str*/
    private String invAttr2Str = new String();
    /** 库存属性3str*/
    private String invAttr3Str = new String();
    /** 库存属性4str*/
    private String invAttr4Str = new String();
    /** 库存属性5str*/
    private String invAttr5Str = new String();
    /** 生产日期str*/
    private String mfgDateStr = new String();
    /** 失效日期str*/
    private String expDateStr = new String();
    /** 周转箱编码*/
    private String containerCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCheckingId() {
        return checkingId;
    }

    public void setCheckingId(Long checkingId) {
        this.checkingId = checkingId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuExtCode() {
        return skuExtCode;
    }

    public void setSkuExtCode(String skuExtCode) {
        this.skuExtCode = skuExtCode;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getCheckingQty() {
        return checkingQty;
    }

    public void setCheckingQty(Long checkingQty) {
        this.checkingQty = checkingQty;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getInvAttr1() {
        return invAttr1;
    }

    public void setInvAttr1(String invAttr1) {
        this.invAttr1 = invAttr1;
    }

    public String getInvAttr2() {
        return invAttr2;
    }

    public void setInvAttr2(String invAttr2) {
        this.invAttr2 = invAttr2;
    }

    public String getInvAttr3() {
        return invAttr3;
    }

    public void setInvAttr3(String invAttr3) {
        this.invAttr3 = invAttr3;
    }

    public String getInvAttr4() {
        return invAttr4;
    }

    public void setInvAttr4(String invAttr4) {
        this.invAttr4 = invAttr4;
    }

    public String getInvAttr5() {
        return invAttr5;
    }

    public void setInvAttr5(String invAttr5) {
        this.invAttr5 = invAttr5;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOdoLineId() {
        return odoLineId;
    }

    public void setOdoLineId(Long odoLineId) {
        this.odoLineId = odoLineId;
    }

    public String getSkuAttr() {
        return SkuAttr;
    }

    public void setSkuAttr(String skuAttr) {
        SkuAttr = skuAttr;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public String getSeedingWallCode() {
        return seedingWallCode;
    }

    public void setSeedingWallCode(String seedingWallCode) {
        this.seedingWallCode = seedingWallCode;
    }

    public String getInvStatusStr() {
        return invStatusStr;
    }

    public void setInvStatusStr(String invStatusStr) {
        this.invStatusStr = invStatusStr;
    }

    public String getInvAttr1Str() {
        return invAttr1Str;
    }

    public void setInvAttr1Str(String invAttr1Str) {
        this.invAttr1Str = invAttr1Str;
    }

    public String getInvAttr2Str() {
        return invAttr2Str;
    }

    public void setInvAttr2Str(String invAttr2Str) {
        this.invAttr2Str = invAttr2Str;
    }

    public String getInvAttr3Str() {
        return invAttr3Str;
    }

    public void setInvAttr3Str(String invAttr3Str) {
        this.invAttr3Str = invAttr3Str;
    }

    public String getInvAttr4Str() {
        return invAttr4Str;
    }

    public void setInvAttr4Str(String invAttr4Str) {
        this.invAttr4Str = invAttr4Str;
    }

    public String getInvAttr5Str() {
        return invAttr5Str;
    }

    public void setInvAttr5Str(String invAttr5Str) {
        this.invAttr5Str = invAttr5Str;
    }

    public String getInvTypeStr() {
        return invTypeStr;
    }

    public void setInvTypeStr(String invTypeStr) {
        this.invTypeStr = invTypeStr;
    }

    public String getMfgDateStr() {
        return mfgDateStr;
    }

    public void setMfgDateStr(String mfgDateStr) {
        this.mfgDateStr = mfgDateStr;
    }

    public String getExpDateStr() {
        return expDateStr;
    }

    public void setExpDateStr(String expDateStr) {
        this.expDateStr = expDateStr;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

}
