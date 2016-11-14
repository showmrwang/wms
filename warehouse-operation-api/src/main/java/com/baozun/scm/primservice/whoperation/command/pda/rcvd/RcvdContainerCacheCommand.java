package com.baozun.scm.primservice.whoperation.command.pda.rcvd;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RcvdContainerCacheCommand implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -4818032720551231454L;

    /** 是否有混放属性 */
    private Boolean isMixAttr = false;
    /** 混放属性 */
    private String mixAttr;
    /** 容器状态 */
    private Integer status;
    /** lifecycle */
    private Integer lifecycle;
    /** 商品ID集合 */
    private Set<Long> skuIdSet = new HashSet<Long>();

    /** 内部容器ID 托盘 货箱 */
    private Long insideContainerId;
    /** 库存状态 */
    private String invStatus;
    /** 库存类型 */
    private String invType;
    /** 批次号 */
    private String batchNumber;
    /** 生产日期 */
    private String mfgDate;
    /** 失效日期 */
    private String expDate;
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
    /** SKUID */
    private String SkuId;
    /** 用户ID */
    private Long userId;
    /** OUID */
    private Long ouId;

    public Set<Long> getSkuIdSet() {
        return skuIdSet;
    }

    public void setSkuIdSet(Set<Long> skuIdSet) {
        this.skuIdSet = skuIdSet;
    }

    public Boolean getIsMixAttr() {
        return isMixAttr;
    }

    public void setIsMixAttr(Boolean isMixAttr) {
        this.isMixAttr = isMixAttr;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public String getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }

    public String getSkuId() {
        return SkuId;
    }

    public void setSkuId(String skuId) {
        SkuId = skuId;
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

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getMixAttr() {
        return mixAttr;
    }

    public void setMixAttr(String mixAttr) {
        this.mixAttr = mixAttr;
    }


    
}
