package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


/**
 * 
 * @author shenlijun
 *
 */
public class WhSkuCommand extends BaseCommand {

    private static final long serialVersionUID = 5078657856518742601L;
    private Long id;
    /** 商品编码 */
    private java.lang.String code;
    /** 外部编码 */
    private java.lang.String extCode;
    /** 商品条码 */
    private java.lang.String barCode;
    /** 商品名称 */
    private java.lang.String name;
    /** 商品英文名称 */
    private java.lang.String enName;
    /** 商品描述 */
    private java.lang.String description;
    /** 颜色 */
    private java.lang.String color;
    /** 长 */
    private Double length;
    /** 宽 */
    private Double width;
    /** 高 */
    private Double height;
    /** 长度单位 */
    private java.lang.String lengthUom;
    /** 体积 */
    private Double volume;
    /** 体积单位 */
    private java.lang.String volumeUom;
    /** 重量 */
    private Double weight;
    /** 重量单位 */
    private java.lang.String weightUom;
    /** 货品类型 */
    private java.lang.Long typeOfGoods;
    /** 所属品牌ID */
    private java.lang.Long brandId;
    /** 所属店铺 */
    private java.lang.Long customerId;
    /** 对应组织ID */
    private java.lang.Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 创建人ID */
    private java.lang.Long createdId;
    /** 修改时间 */
    private java.util.Date lastModifyTime;
    /** 修改人ID */
    private java.lang.Long modifiedId;
    /** 款式 */
    private java.lang.String style;
    /** 尺码 */
    private java.lang.String size;
    /** 1.可用;2.已删除;0.禁用 */
    private java.lang.Integer lifecycle;

    private String brandName;

    private String customerName;
    /** 扫描数量 */
    private Double scanSkuQty;
    private Boolean isNeedTipSkuDetail;
    private Boolean isNeedTipSkuSn;
    private Boolean isNeedTipSkuDefect;
    private String invType;
    private String invStatus;
    private String invMfgDate;
    private String invExpDate;
    private String invAttr1;
    private String invAttr2;
    private String invAttr3;
    private String invAttr4;
    private String invAttr5;
    private String skuSn;
    private String skuDefect;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public java.lang.String getCode() {
        return code;
    }

    public void setCode(java.lang.String code) {
        this.code = code;
    }

    public java.lang.String getExtCode() {
        return extCode;
    }

    public void setExtCode(java.lang.String extCode) {
        this.extCode = extCode;
    }

    public java.lang.String getBarCode() {
        return barCode;
    }

    public void setBarCode(java.lang.String barCode) {
        this.barCode = barCode;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getEnName() {
        return enName;
    }

    public void setEnName(java.lang.String enName) {
        this.enName = enName;
    }

    public java.lang.String getDescription() {
        return description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public java.lang.String getColor() {
        return color;
    }

    public void setColor(java.lang.String color) {
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

    public java.lang.String getLengthUom() {
        return lengthUom;
    }

    public void setLengthUom(java.lang.String lengthUom) {
        this.lengthUom = lengthUom;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public java.lang.String getVolumeUom() {
        return volumeUom;
    }

    public void setVolumeUom(java.lang.String volumeUom) {
        this.volumeUom = volumeUom;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public java.lang.String getWeightUom() {
        return weightUom;
    }

    public void setWeightUom(java.lang.String weightUom) {
        this.weightUom = weightUom;
    }

    public java.lang.Long getTypeOfGoods() {
        return typeOfGoods;
    }

    public void setTypeOfGoods(java.lang.Long typeOfGoods) {
        this.typeOfGoods = typeOfGoods;
    }

    public java.lang.Long getBrandId() {
        return brandId;
    }

    public void setBrandId(java.lang.Long brandId) {
        this.brandId = brandId;
    }

    public java.lang.Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(java.lang.Long customerId) {
        this.customerId = customerId;
    }

    public java.lang.Long getOuId() {
        return ouId;
    }

    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.lang.Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(java.lang.Long createdId) {
        this.createdId = createdId;
    }

    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(java.util.Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public java.lang.Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public java.lang.String getStyle() {
        return style;
    }

    public void setStyle(java.lang.String style) {
        this.style = style;
    }

    public java.lang.String getSize() {
        return size;
    }

    public void setSize(java.lang.String size) {
        this.size = size;
    }

    public java.lang.Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(java.lang.Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Double getScanSkuQty() {
        return scanSkuQty;
    }

    public void setScanSkuQty(Double scanSkuQty) {
        this.scanSkuQty = scanSkuQty;
    }

    public Boolean getIsNeedTipSkuDetail() {
        return isNeedTipSkuDetail;
    }

    public void setIsNeedTipSkuDetail(Boolean isNeedTipSkuDetail) {
        this.isNeedTipSkuDetail = isNeedTipSkuDetail;
    }

    public Boolean getIsNeedTipSkuSn() {
        return isNeedTipSkuSn;
    }

    public void setIsNeedTipSkuSn(Boolean isNeedTipSkuSn) {
        this.isNeedTipSkuSn = isNeedTipSkuSn;
    }

    public Boolean getIsNeedTipSkuDefect() {
        return isNeedTipSkuDefect;
    }

    public void setIsNeedTipSkuDefect(Boolean isNeedTipSkuDefect) {
        this.isNeedTipSkuDefect = isNeedTipSkuDefect;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvMfgDate() {
        return invMfgDate;
    }

    public void setInvMfgDate(String invMfgDate) {
        this.invMfgDate = invMfgDate;
    }

    public String getInvExpDate() {
        return invExpDate;
    }

    public void setInvExpDate(String invExpDate) {
        this.invExpDate = invExpDate;
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

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public String getSkuDefect() {
        return skuDefect;
    }

    public void setSkuDefect(String skuDefect) {
        this.skuDefect = skuDefect;
    }


}
