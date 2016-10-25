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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 * 
 */
public class Container2ndCategory extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7419679506693588839L;


    /** 类别编码 */
    private String categoryCode;
    /** 类别名称 */
    private String categoryName;
    /** 对应一级类型ID */
    private String oneLevelType;
    /** 编码生成器CODE */
    private String codeGenerator;
    /** 长 */
    private Double length;
    /** 宽 */
    private Double width;
    /** 高 */
    private Double high;
    /** 体积 */
    private Double volume;
    /** 重量 */
    private Double weight;
    /** 长边货格数量 */
    private Integer lengthGridNum;
    /** 宽边货格数量 */
    private Integer widthGridNum;
    /** 高边货格数量 */
    private Integer highGridNum;
    /** 总货格数量 */
    private Integer totalGridNum;
    /** 货格长度 */
    private Double gridLength;
    /** 货格宽度 */
    private Double gridWidth;
    /** 货格高度 */
    private Double gridHigh;
    /** 货格体积 */
    private Double gridVolume;
    /** 拣货模式 */
    private String pickingMode;
    /** 前缀 */
    private String prefix;
    /** 后缀 */
    private String suffix;
    /** 所属组织ID */
    private Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;

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

    public String getOneLevelType() {
        return oneLevelType;
    }

    public void setOneLevelType(String oneLevelType) {
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

    public Integer getLengthGridNum() {
        return lengthGridNum;
    }

    public void setLengthGridNum(Integer lengthGridNum) {
        this.lengthGridNum = lengthGridNum;
    }

    public Integer getWidthGridNum() {
        return widthGridNum;
    }

    public void setWidthGridNum(Integer widthGridNum) {
        this.widthGridNum = widthGridNum;
    }

    public Integer getHighGridNum() {
        return highGridNum;
    }

    public void setHighGridNum(Integer highGridNum) {
        this.highGridNum = highGridNum;
    }

    public Integer getTotalGridNum() {
        return totalGridNum;
    }

    public void setTotalGridNum(Integer totalGridNum) {
        this.totalGridNum = totalGridNum;
    }

    public Double getGridLength() {
        return gridLength;
    }

    public void setGridLength(Double gridLength) {
        this.gridLength = gridLength;
    }

    public Double getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(Double gridWidth) {
        this.gridWidth = gridWidth;
    }

    public Double getGridHigh() {
        return gridHigh;
    }

    public void setGridHigh(Double gridHigh) {
        this.gridHigh = gridHigh;
    }

    public Double getGridVolume() {
        return gridVolume;
    }

    public void setGridVolume(Double gridVolume) {
        this.gridVolume = gridVolume;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
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
}
