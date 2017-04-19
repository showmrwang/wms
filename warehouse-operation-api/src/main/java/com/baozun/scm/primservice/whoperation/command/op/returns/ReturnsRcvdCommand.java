package com.baozun.scm.primservice.whoperation.command.op.returns;

import java.io.Serializable;

public class ReturnsRcvdCommand implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -862069988977691110L;

    private Long asnId;

    private String batchNo;

    private Long containerId;

    private String countryOfOrigin;

    private Long defectType;

    private Long defectReason;

    private String defectSource;

    private String expDate;

    private String expressNum;

    private String invAttr1;

    private String invAttr2;

    private String invAttr3;

    private String invAttr4;

    private String invAttr5;

    private Long invStatus;

    private Long lineId;

    private String mfgDate;

    private Long skuId;

    private String skuBarCode;

    private String sn;

    private String invType;

    private String defectWareBarCode;

    private String serialNumberType;


    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public String getDefectWareBarCode() {
        return defectWareBarCode;
    }

    public void setDefectWareBarCode(String defectWareBarCode) {
        this.defectWareBarCode = defectWareBarCode;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public Long getAsnId() {
        return asnId;
    }

    public void setAsnId(Long asnId) {
        this.asnId = asnId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getDefectSource() {
        return defectSource;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getExpressNum() {
        return expressNum;
    }

    public void setExpressNum(String expressNum) {
        this.expressNum = expressNum;
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

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getDefectType() {
        return defectType;
    }

    public void setDefectType(Long defectType) {
        this.defectType = defectType;
    }

    public Long getDefectReason() {
        return defectReason;
    }

    public void setDefectReason(Long defectReason) {
        this.defectReason = defectReason;
    }



}
