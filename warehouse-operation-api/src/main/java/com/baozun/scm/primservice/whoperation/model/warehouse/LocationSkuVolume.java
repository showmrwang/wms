package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


public class LocationSkuVolume extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -2917577263760215186L;

    // columns START
    /** 序号 */
    private Integer serialNumber;
    /** 库位ID */
    private Long locationId;
    /** 商品ID */
    private Long skuId;
    /** 容量下限 */
    private Integer lowerCapacity;
    /** 容量上限 */
    private Integer upperCapacity;
    /** 对应组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;
    /** 枯萎编号 */
    private String locationCode;
    /** 在库库存 */
    private Long onHandQty;

    // columns END
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getLowerCapacity() {
        return lowerCapacity;
    }

    public void setLowerCapacity(Integer lowerCapacity) {
        this.lowerCapacity = lowerCapacity;
    }

    public Integer getUpperCapacity() {
        return upperCapacity;
    }

    public void setUpperCapacity(Integer upperCapacity) {
        this.upperCapacity = upperCapacity;
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

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Long getOnHandQty() {
        return onHandQty;
    }

    public void setOnHandQty(Long onHandQty) {
        this.onHandQty = onHandQty;
    }
}
