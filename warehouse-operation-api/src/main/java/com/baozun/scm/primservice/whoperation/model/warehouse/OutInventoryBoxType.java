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
 * 出库箱类型
 * 
 * @author larkark
 *
 */
public class OutInventoryBoxType extends BaseModel {


    private static final long serialVersionUID = 8431021328606850616L;

    /**
     *
     */
    // columns START
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
    private Double volumne;
    /** 承重 */
    private Double weight;
    /** 外部长 */
    private Double lengthExt;
    /** 外部宽 */
    private Double widthExt;
    /** 外部高 */
    private Double highExt;
    /** 出库箱体积 */
    private Double volumneExt;
    /** 出库箱重量 */
    private Double weightExt;
    /** 创建时间 */
    private Date createTime;
    /** 最终修改时间 */
    private Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;


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

    public Double getVolumne() {
        return volumne;
    }

    public void setVolumne(Double volumne) {
        this.volumne = volumne;
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

    public Double getVolumneExt() {
        return volumneExt;
    }

    public void setVolumneExt(Double volumneExt) {
        this.volumneExt = volumneExt;
    }

    public Double getWeightExt() {
        return weightExt;
    }

    public void setWeightExt(Double weightExt) {
        this.weightExt = weightExt;
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
}
