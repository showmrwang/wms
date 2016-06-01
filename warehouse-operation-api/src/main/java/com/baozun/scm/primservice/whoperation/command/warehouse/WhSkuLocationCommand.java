package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhSkuLocationCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -2431436292264188264L;
    /** ID */
    private Long id;
    // columns START
    /** 商品ID */
    private Long skuId;
    /** 库位ID */
    private Long locationId;
    /** 是否有效 */
    private Integer lifecycle;
    /** 修改者ID */
    private Long modifiedId;
    /** lastModifyTime */
    private Date lastModifyTime;
    /** ouId */
    private Long ouId;
    /** 商品名称 */
    private String skuName;
    /** 商品编码 */
    private String skuCode;
    /** 库位编码 */
    private String locationCode;
    /** ID集合,用于删除操作 */
    private List<Long> idList;

    // 校验使用字段
    /** 商品lifecycle */
    private Integer skuLifecycle;
    /** 库位lifecycle */
    private Integer locationLifecycle;
    /** sku-location关联数量 */
    private Integer skuLocationCount;
    /** 库位isStatic */
    private Boolean locationIsStatic;
    /** 库位availableMixCount */
    private Integer availableMixCount;

    // columns END
    public java.lang.Long getSkuId() {
        return skuId;
    }

    public Integer getAvailableMixCount() {
        return availableMixCount;
    }

    public void setAvailableMixCount(Integer availableMixCount) {
        this.availableMixCount = availableMixCount;
    }

    public Boolean getLocationIsStatic() {
        return locationIsStatic;
    }

    public void setLocationIsStatic(Boolean locationIsStatic) {
        this.locationIsStatic = locationIsStatic;
    }

    public Integer getSkuLifecycle() {
        return skuLifecycle;
    }

    public void setSkuLifecycle(Integer skuLifecycle) {
        this.skuLifecycle = skuLifecycle;
    }

    public Integer getLocationLifecycle() {
        return locationLifecycle;
    }

    public void setLocationLifecycle(Integer locationLifecycle) {
        this.locationLifecycle = locationLifecycle;
    }

    public Integer getSkuLocationCount() {
        return skuLocationCount;
    }

    public void setSkuLocationCount(Integer skuLocationCount) {
        this.skuLocationCount = skuLocationCount;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public void setSkuId(java.lang.Long skuId) {
        this.skuId = skuId;
    }

    public java.lang.Long getLocationId() {
        return locationId;
    }

    public void setLocationId(java.lang.Long locationId) {
        this.locationId = locationId;
    }

    public java.lang.Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(java.lang.Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public java.lang.Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(java.util.Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
