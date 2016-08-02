package com.baozun.scm.primservice.whoperation.command.sku.skucommand;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class SkuStandardPackingCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -2094837591641431205L;
    // columns START
    private Long id;
    /** 对应商品ID */
    private Long skuId;
    /** 容器类型 */
    private Long containerType;
    /** 数量 */
    private Long quantity;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** lifecycle */
    private Integer lifecycle;
    /** ouId */
    private Long ouId;
    /** 需要的容器数量 */
    private Long count;
    /** 容器类型名称 */
    private String categoryName;
    // columns END

    private String skuCode;

    private String skuName;

    private String containerTypeName;

    private Long asnSkuCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getContainerType() {
        return containerType;
    }

    public void setContainerType(Long containerType) {
        this.containerType = containerType;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
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

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getAsnSkuCount() {
        return asnSkuCount;
    }

    public void setAsnSkuCount(Long asnSkuCount) {
        this.asnSkuCount = asnSkuCount;
    }


}
