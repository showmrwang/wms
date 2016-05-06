package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class Container2ndCategoryCommand extends BaseCommand {
    

    /**
     * 
     */
    private static final long serialVersionUID = -8615840384131603857L;
    
    private Long id;
    /*
     * 类别编码
     */
    private String categoryCode;
    /*
     * 类别名称
     */
    private String categoryName;
    /*
     * 对应一级类型ID
     */
    private Long oneLevelType;
    /*
     * 编码生成器CODE
     */
    private String codeGenerator;
    /*
     * 长
     */
    private Double length;
    /*
     * 宽
     */
    private Double width;
    /*
     * 高
     */
    private Double high;

    /*
     * 长度单位
     */
    private String lengthUom;
    /*
     * 体积
     */
    private Double volume;

    /*
     * 体积单位
     */
    private String volumeUom;
    /*
     * 重量
     */
    private Double weight;
    /*
     * 重量单位
     */
    private String weightUom;
    /*
     * 前缀
     */
    private String prefix;
    /*
     * 后缀
     */
    private String suffix;
    /*
     * 所属组织ID
     */
    private Long ouId;
    /*
     * 创建时间
     */
    private Date createTime;
    /*
     * 最后修改时间
     */
    private Date lastModifyTime;
    /*
     * 操作人ID
     */
    private Long operatorId;
    /*
     * 1.可用;2.已删除;0.禁用
     */
    private Integer lifecycle;

    private String oneLevelTypeName;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getOneLevelType() {
        return oneLevelType;
    }

    public void setOneLevelType(Long oneLevelType) {
        this.oneLevelType = oneLevelType;
    }

    public String getCodeGenerator() {
        return codeGenerator;
    }

    public void setCodeGenerator(String codeGenerator) {
        this.codeGenerator = codeGenerator;
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

    public String getLengthUom() {
        return lengthUom;
    }

    public void setLengthUom(String lengthUom) {
        this.lengthUom = lengthUom;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getVolumeUom() {
        return volumeUom;
    }

    public void setVolumeUom(String volumeUom) {
        this.volumeUom = volumeUom;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getWeightUom() {
        return weightUom;
    }

    public void setWeightUom(String weightUom) {
        this.weightUom = weightUom;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getOneLevelTypeName() {
        return oneLevelTypeName;
    }

    public void setOneLevelTypeName(String oneLevelTypeName) {
        this.oneLevelTypeName = oneLevelTypeName;
    }

}
