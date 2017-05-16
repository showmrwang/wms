package com.baozun.scm.primservice.whoperation.command.op.returns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReturnsSkuEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8084841198967667906L;

    private String skuId;

    private String skuOptUrl;

    private String serialNumberType;

    private List<ReturnsSnEntity> snEntityList = new ArrayList<ReturnsSnEntity>();

    private String validDate;// 有效期天数

    private List<Long> lineIdList = new ArrayList<Long>();


    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSkuOptUrl() {
        return skuOptUrl;
    }

    public void setSkuOptUrl(String skuOptUrl) {
        this.skuOptUrl = skuOptUrl;
    }

    public List<ReturnsSnEntity> getSnEntityList() {
        return snEntityList;
    }

    public void setSnEntityList(List<ReturnsSnEntity> snEntityList) {
        this.snEntityList = snEntityList;
    }

    public List<Long> getLineIdList() {
        return lineIdList;
    }

    public void setLineIdList(List<Long> lineIdList) {
        this.lineIdList = lineIdList;
    }





}
