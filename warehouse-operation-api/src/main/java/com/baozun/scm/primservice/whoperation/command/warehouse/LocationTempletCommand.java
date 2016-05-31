package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class LocationTempletCommand extends BaseCommand{
    /**
     * 
     */
    private static final long serialVersionUID = 3162333787444430005L;
    private Long id;
    /** 物理仓ID */
    private Long ouId;
    /** 库位模板编码 */
    private String templetCode;
    /** 库位模板名称 */
    private String templetName;
    /** 长 */
    private Double length;
    /** 宽 */
    private Double width;
    /** 高 */
    private Double high;
    /** 长度单位 */
    private String lengthUom;
    /** 体积 */
    private Double volume;
    /** 体积单位 */
    private String volumeUom;
    /** 重量 */
    private Double weight;
    /** 重量单位 */
    private String weightUom;
    /** 尺寸类别 */
    private String sizeType;
    /** 间隔符 */
    private String splitMark;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    
    private String dismensions;
    
    private String separator;
    
    private String ouName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public String getTempletCode() {
        return templetCode;
    }
    public void setTempletCode(String templetCode) {
        this.templetCode = templetCode;
    }
    public String getTempletName() {
        return templetName;
    }
    public void setTempletName(String templetName) {
        this.templetName = templetName;
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
    public String getSizeType() {
        return sizeType;
    }
    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }
    public String getSplitMark() {
        return splitMark;
    }
    public void setSplitMark(String splitMark) {
        this.splitMark = splitMark;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public String getDismensions() {
        return dismensions;
    }
    public void setDismensions(String dismensions) {
        this.dismensions = dismensions;
    }
    public String getSeparator() {
        return separator;
    }
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    public String getOuName() {
        return ouName;
    }
    public void setOuName(String ouName) {
        this.ouName = ouName;
    }
    
    
}
