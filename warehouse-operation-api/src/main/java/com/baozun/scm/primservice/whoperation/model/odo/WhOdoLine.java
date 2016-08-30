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
package com.baozun.scm.primservice.whoperation.model.odo;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 *
 */
public class WhOdoLine extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = -2071411398425434080L;

    // columns START
    /** 出库单ID */
    private Long odoId;
    /** 行号 */
    private Integer linenum;
    /** 原始出库单明细行号 */
    private Integer originalLinenum;
    /** 店铺CODE 或者* */
    private String store;
    /** 外部单据行号 */
    private Integer extLinenum;
    /** 商品ID */
    private Long skuId;
    /** 商品条码 */
    private String skuBarCode;
    /** 商品名称 */
    private String skuName;
    /** 上位系统商品名称 */
    private String extSkuName;
    /** 计划数量 */
    private Double qty;
    /** 本次出库数量 */
    private Double currentQty;
    /** 实际出库数量 */
    private Double actualQty;
    /** 取消数量 */
    private Double cancelQty;
    /** 已分配数量 */
    private Double assignQty;
    /** 已拣货数量 */
    private Double diekingQty;
    /** 行单价 */
    private Double linePrice;
    /** 行总金额 */
    private Double lineAmt;
    /** 出库单明细状态 */
    private String odoLineStatus;
    /** 是否复核 */
    private Boolean isCheck;
    /** 整行出库标志 */
    private Boolean fullLineOutbound;
    /** 部分出库策略 */
    private String partOutboundStrategy;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 最小失效日期 */
    private Date minExpDate;
    /** 最大失效日期 */
    private Date maxExpDate;
    /** 批次号 */
    private String batchNumber;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存状态 */
    private Long invStatus;
    /** 库存类型 */
    private String invType;
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
    /** 出库箱类型 */
    private Long outboundCartonType;
    /** 颜色 */
    private String color;
    /** 款式 */
    private String style;
    /** 尺码 */
    private String size;
    /** 混放属性 */
    private String mixingAttr;
    /** 对应原始出库单CODE */
    private String originalOdoCode;
    /** 分配失败原因 */
    private String assignFailReason;
    /** 是否分配成功 */
    private Boolean isAssignSuccess;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    // columns END

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Integer getLinenum() {
        return linenum;
    }

    public void setLinenum(Integer linenum) {
        this.linenum = linenum;
    }

    public Integer getOriginalLinenum() {
        return originalLinenum;
    }

    public void setOriginalLinenum(Integer originalLinenum) {
        this.originalLinenum = originalLinenum;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Integer getExtLinenum() {
        return extLinenum;
    }

    public void setExtLinenum(Integer extLinenum) {
        this.extLinenum = extLinenum;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public String getExtSkuName() {
        return extSkuName;
    }

    public void setExtSkuName(String extSkuName) {
        this.extSkuName = extSkuName;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(Double currentQty) {
        this.currentQty = currentQty;
    }

    public Double getActualQty() {
        return actualQty;
    }

    public void setActualQty(Double actualQty) {
        this.actualQty = actualQty;
    }

    public Double getCancelQty() {
        return cancelQty;
    }

    public void setCancelQty(Double cancelQty) {
        this.cancelQty = cancelQty;
    }

    public Double getAssignQty() {
        return assignQty;
    }

    public void setAssignQty(Double assignQty) {
        this.assignQty = assignQty;
    }

    public Double getDiekingQty() {
        return diekingQty;
    }

    public void setDiekingQty(Double diekingQty) {
        this.diekingQty = diekingQty;
    }

    public Double getLinePrice() {
        return linePrice;
    }

    public void setLinePrice(Double linePrice) {
        this.linePrice = linePrice;
    }

    public Double getLineAmt() {
        return lineAmt;
    }

    public void setLineAmt(Double lineAmt) {
        this.lineAmt = lineAmt;
    }

    public String getOdoLineStatus() {
        return odoLineStatus;
    }

    public void setOdoLineStatus(String odoLineStatus) {
        this.odoLineStatus = odoLineStatus;
    }

    public Boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Boolean isCheck) {
        this.isCheck = isCheck;
    }

    public Boolean getFullLineOutbound() {
        return fullLineOutbound;
    }

    public void setFullLineOutbound(Boolean fullLineOutbound) {
        this.fullLineOutbound = fullLineOutbound;
    }

    public String getPartOutboundStrategy() {
        return partOutboundStrategy;
    }

    public void setPartOutboundStrategy(String partOutboundStrategy) {
        this.partOutboundStrategy = partOutboundStrategy;
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

    public Date getMinExpDate() {
        return minExpDate;
    }

    public void setMinExpDate(Date minExpDate) {
        this.minExpDate = minExpDate;
    }

    public Date getMaxExpDate() {
        return maxExpDate;
    }

    public void setMaxExpDate(Date maxExpDate) {
        this.maxExpDate = maxExpDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
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

    public Long getOutboundCartonType() {
        return outboundCartonType;
    }

    public void setOutboundCartonType(Long outboundCartonType) {
        this.outboundCartonType = outboundCartonType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMixingAttr() {
        return mixingAttr;
    }

    public void setMixingAttr(String mixingAttr) {
        this.mixingAttr = mixingAttr;
    }

    public String getOriginalOdoCode() {
        return originalOdoCode;
    }

    public void setOriginalOdoCode(String originalOdoCode) {
        this.originalOdoCode = originalOdoCode;
    }

    public String getAssignFailReason() {
        return assignFailReason;
    }

    public void setAssignFailReason(String assignFailReason) {
        this.assignFailReason = assignFailReason;
    }

    public Boolean getIsAssignSuccess() {
        return isAssignSuccess;
    }

    public void setIsAssignSuccess(Boolean isAssignSuccess) {
        this.isAssignSuccess = isAssignSuccess;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }
}
