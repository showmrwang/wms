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
package com.baozun.scm.primservice.whoperation.model.confirm.outbound;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库单反馈明细信息
 *
 */
public class WhOutboundLineConfirm extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -1306657301944256550L;

    /** 出库单反馈ID */
    private Long outbouncConfirmId;
    /** 店铺CODE */
    private String storeCode;
    /** WMS出库单号 */
    private String wmsOdoCode;
    /** 快递单号 */
    private String trackingNumber;
    /** 出库箱号 */
    private String outboundBoxCode;
    /** 外部单据行号 */
    private Integer extLineNum;
    /** 商品唯一编码 */
    private String upc;
    /** 款式 */
    private String style;
    /** 颜色 */
    private String color;
    /** 尺码 */
    private String size;
    /** 计划商品数量 */
    private Double qty;
    /** 实际出库数量 */
    private Double actualQty;
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
    /** 仓库组织ID */
    private Long ouId;
    /** SN/残次信息 */
    private List<WhOutboundSnLineConfirm> snLineConfirms;

    public Long getOutbouncConfirmId() {
        return outbouncConfirmId;
    }

    public void setOutbouncConfirmId(Long outbouncConfirmId) {
        this.outbouncConfirmId = outbouncConfirmId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getWmsOdoCode() {
        return wmsOdoCode;
    }

    public void setWmsOdoCode(String wmsOdoCode) {
        this.wmsOdoCode = wmsOdoCode;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getOutboundBoxCode() {
        return outboundBoxCode;
    }

    public void setOutboundBoxCode(String outboundBoxCode) {
        this.outboundBoxCode = outboundBoxCode;
    }

    public Integer getExtLineNum() {
        return extLineNum;
    }

    public void setExtLineNum(Integer extLineNum) {
        this.extLineNum = extLineNum;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getActualQty() {
        return actualQty;
    }

    public void setActualQty(Double actualQty) {
        this.actualQty = actualQty;
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

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public List<WhOutboundSnLineConfirm> getSnLineConfirms() {
        return snLineConfirms;
    }

    public void setSnLineConfirms(List<WhOutboundSnLineConfirm> snLineConfirms) {
        this.snLineConfirms = snLineConfirms;
    }



}
