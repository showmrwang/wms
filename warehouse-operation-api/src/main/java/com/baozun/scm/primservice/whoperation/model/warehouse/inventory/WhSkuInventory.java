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
package com.baozun.scm.primservice.whoperation.model.warehouse.inventory;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 商品库存表
 * 
 * @author larkark
 * 
 */
public class WhSkuInventory extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -263625851030926135L;


    /** 商品ID */
    private Long skuId;
    /** 库位ID 库位号 */
    private Long locationId;
    /** 外部容器ID 托盘 货箱 */
    private Long outerContainerId;
    /** 内部容器ID 托盘 货箱 */
    private Long insideContainerId;
    /** 客户ID */
    private Long customerId;
    /** 店铺ID */
    private Long storeId;
    /** 占用单据号 */
    private String occupationCode;
    /** 占用单据明细行ID */
    private Long occupationLineId;
    /** 在库可用库存 */
    private Double onHandQty;
    /** 已分配库存 */
    private Double allocatedQty;
    /** 待移入库存 */
    private Double toBeFilledQty;
    /** 冻结库存 */
    private Double frozenQty;
    /** 库存状态 */
    private Long invStatus;
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
    /** 是否可用 */
    private Boolean isLocked = false;
    /** 对应仓库ID */
    private Long ouId;
    /** 占用单据号来源 */
    private String occupationCodeSource;
    /** 入库时间 */
    private Date inboundTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    
    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public Double getOnHandQty() {
        return onHandQty;
    }

    public void setOnHandQty(Double onHandQty) {
        this.onHandQty = onHandQty;
    }

    public Double getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(Double allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public Double getToBeFilledQty() {
        return toBeFilledQty;
    }

    public void setToBeFilledQty(Double toBeFilledQty) {
        this.toBeFilledQty = toBeFilledQty;
    }

    public Double getFrozenQty() {
        return frozenQty;
    }

    public void setFrozenQty(Double frozenQty) {
        this.frozenQty = frozenQty;
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

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getOccupationCodeSource() {
        return occupationCodeSource;
    }

    public void setOccupationCodeSource(String occupationCodeSource) {
        this.occupationCodeSource = occupationCodeSource;
    }

    public Date getInboundTime() {
        return inboundTime;
    }

    public void setInboundTime(Date inboundTime) {
        this.inboundTime = inboundTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WhSkuInventory))
            return false;

        WhSkuInventory that = (WhSkuInventory) o;

        if (getCountryOfOrigin() != null ? !getCountryOfOrigin().equals(that.getCountryOfOrigin()) : that.getCountryOfOrigin() != null)
            return false;
        if (getInvAttr1() != null ? !getInvAttr1().equals(that.getInvAttr1()) : that.getInvAttr1() != null)
            return false;
        if (getInvAttr2() != null ? !getInvAttr2().equals(that.getInvAttr2()) : that.getInvAttr2() != null)
            return false;
        if (getInvAttr3() != null ? !getInvAttr3().equals(that.getInvAttr3()) : that.getInvAttr3() != null)
            return false;
        if (getInvAttr4() != null ? !getInvAttr4().equals(that.getInvAttr4()) : that.getInvAttr4() != null)
            return false;
        return !(getInvAttr5() != null ? !getInvAttr5().equals(that.getInvAttr5()) : that.getInvAttr5() != null);

    }

    @Override public int hashCode() {
        int result = getCountryOfOrigin() != null ? getCountryOfOrigin().hashCode() : 0;
        result = 31 * result + (getInvAttr1() != null ? getInvAttr1().hashCode() : 0);
        result = 31 * result + (getInvAttr2() != null ? getInvAttr2().hashCode() : 0);
        result = 31 * result + (getInvAttr3() != null ? getInvAttr3().hashCode() : 0);
        result = 31 * result + (getInvAttr4() != null ? getInvAttr4().hashCode() : 0);
        result = 31 * result + (getInvAttr5() != null ? getInvAttr5().hashCode() : 0);
        return result;
    }

	public Long getOccupationLineId() {
		return occupationLineId;
	}

	public void setOccupationLineId(Long occupationLineId) {
		this.occupationLineId = occupationLineId;
	}
}
