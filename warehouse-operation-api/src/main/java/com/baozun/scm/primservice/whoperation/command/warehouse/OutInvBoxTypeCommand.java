package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutInventoryboxRelationship;

/**
 * 出库箱类型
 * 
 * @author larkark
 *
 */
public class OutInvBoxTypeCommand extends BaseCommand {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    /** sku主键 */
    private Long skuId;
    /** 对应组织ID */
    private Long ouId;
    /** 出库箱类型编码 */
    private String code;
    /** 出库箱类型名称 */
    private String name;
    /** 描述 */
    private String description;
    /** 是否有效 */
    private Integer lifecycle;
    /** 长 */
    private Double length;
    /** 宽 */
    private Double width;
    /** 高 */
    private Double high;
    /** 体积 */
    private Double volume;
    /** 承重 */
    private Double weight;
    /** 外部长 */
    private Double lengthExt;
    /** 外部宽 */
    private Double widthExt;
    /** 外部高 */
    private Double highExt;
    /** 出库箱体积 */
    private Double volumeExt;
    /** 出库箱重量 */
    private Double weightExt;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最终修改时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;
    /** 商品名称 */
    private String skuName;
    /** 商品条码 */
    private String skuBarCode;

    // 长度单位
    private String lengthUom;
    // 体积单位
    private String volumeUom;
    // 重量单位
    private String weightUom;

    // 长度单位
    private String lengthUomExt;
    // 体积单位
    private String volumeUomExt;
    // 重量单位
    private String weightUomExt;

    /** 出库箱对应店铺或客户 */
    private List<WhOutInventoryboxRelationship> WhOutInventoryboxRelationshipList;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
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

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
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

    public Double getLengthExt() {
        return lengthExt;
    }

    public void setLengthExt(Double lengthExt) {
        this.lengthExt = lengthExt;
    }

    public Double getWidthExt() {
        return widthExt;
    }

    public void setWidthExt(Double widthExt) {
        this.widthExt = widthExt;
    }

    public Double getHighExt() {
        return highExt;
    }

    public void setHighExt(Double highExt) {
        this.highExt = highExt;
    }

    public Double getVolumeExt() {
        return volumeExt;
    }

    public void setVolumeExt(Double volumeExt) {
        this.volumeExt = volumeExt;
    }

    public Double getWeightExt() {
        return weightExt;
    }

    public void setWeightExt(Double weightExt) {
        this.weightExt = weightExt;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(java.util.Date lastModifyTime) {
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

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLengthUomExt() {
        return lengthUomExt;
    }

    public void setLengthUomExt(String lengthUomExt) {
        this.lengthUomExt = lengthUomExt;
    }

    public String getVolumeUomExt() {
        return volumeUomExt;
    }

    public void setVolumeUomExt(String volumeUomExt) {
        this.volumeUomExt = volumeUomExt;
    }

    public String getWeightUomExt() {
        return weightUomExt;
    }

    public void setWeightUomExt(String weightUomExt) {
        this.weightUomExt = weightUomExt;
    }

    public List<WhOutInventoryboxRelationship> getWhOutInventoryboxRelationshipList() {
        return WhOutInventoryboxRelationshipList;
    }

    public void setWhOutInventoryboxRelationshipList(List<WhOutInventoryboxRelationship> whOutInventoryboxRelationshipList) {
        WhOutInventoryboxRelationshipList = whOutInventoryboxRelationshipList;
    }


}
