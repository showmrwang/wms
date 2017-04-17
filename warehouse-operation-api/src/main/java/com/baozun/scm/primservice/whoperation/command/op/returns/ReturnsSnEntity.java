package com.baozun.scm.primservice.whoperation.command.op.returns;

import java.io.Serializable;

public class ReturnsSnEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8562671464349479783L;


    private String lineId;

    private String skuAttrUrl;

    private String sn;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getSkuAttrUrl() {
        return skuAttrUrl;
    }

    public void setSkuAttrUrl(String skuAttrUrl) {
        this.skuAttrUrl = skuAttrUrl;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

}
