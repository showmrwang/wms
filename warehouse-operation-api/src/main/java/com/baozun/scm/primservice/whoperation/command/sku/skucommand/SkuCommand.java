package com.baozun.scm.primservice.whoperation.command.sku.skucommand;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class SkuCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 4279405290281499480L;
    private Long id;
    /** 商品编码 */
    private String code;
    /** 商品条码 */
    private String barCode;
    /** 商品名称 */
    private String name;
    /** 商品英文名称 */
    private String enName;
    /** 商品描述 */
    private String description;
    /** 颜色 */
    private String color;
    /** 长 */
    private Double length;
    /** 宽 */
    private Double width;
    /** 高 */
    private Double height;
    /** 体积 */
    private Double volume;
    /** 重量 */
    private Double weight;
    /** 货品类型 */
    // private Long typeOfGoods;
    /** 所属品牌ID */
    private Long brandId;
    /** 所属客户 */
    private Long customerId;
    /** 组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;
    /** 款式 */
    private String style;
    /** 尺码 */
    private String size;
    /** 生命周期 */
    private Integer lifecycle;
    /** 长度单位 */
    private String lengthUom;
    /** 体积单位 */
    private String volumeUom;
    /** 重量单位 */
    private String weightUom;
    /** 外部编码 */
    private String extCode;

    private String brandName;

    private String customerName;
    /** 扫描数量 */
    private Double scanSkuQty;

    /** 序列号管理类型 */
    private String serialNumberType;

    private Integer validDate;

    private Boolean isFoldable;

    public Boolean getIsFoldable() {
        return isFoldable;
    }

    public void setIsFoldable(Boolean isFoldable) {
        this.isFoldable = isFoldable;
    }

    /** 用于模糊查询显示前15条数据 */
    private Integer lineNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /*
     * public Long getTypeOfGoods() { return typeOfGoods; }
     * 
     * public void setTypeOfGoods(Long typeOfGoods) { this.typeOfGoods = typeOfGoods; }
     */

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getLengthUom() {
        return lengthUom;
    }

    public void setLengthUom(String lengthUom) {
        this.lengthUom = lengthUom;
    }

    public String getVolumeUom() {
        return volumeUom;
    }

    public void setVolumeUom(String volumeUom) {
        this.volumeUom = volumeUom;
    }

    public String getWeightUom() {
        return weightUom;
    }

    public void setWeightUom(String weightUom) {
        this.weightUom = weightUom;
    }

    public String getExtCode() {
        return extCode;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }

    public Double getScanSkuQty() {
        return scanSkuQty;
    }

    public void setScanSkuQty(Double scanSkuQty) {
        this.scanSkuQty = scanSkuQty;
    }

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public Integer getValidDate() {
        return validDate;
    }

    public void setValidDate(Integer validDate) {
        this.validDate = validDate;
    }

}
