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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库箱类型
 * 
 * @author larkark
 *
 */
public class OutInventoryBoxType extends BaseModel {



    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最终修改时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;

    // columns END

    public OutInventoryBoxType() {}

    public OutInventoryBoxType(Long id) {
        this.id = id;
    }

    public void setSkuId(Long value) {
        this.skuId = value;
    }

    public Long getSkuId() {
        return this.skuId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setLifecycle(Integer value) {
        this.lifecycle = value;
    }

    public Integer getLifecycle() {
        return this.lifecycle;
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

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setCreateTime(java.util.Date value) {
        this.createTime = value;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(java.util.Date value) {
        this.lastModifyTime = value;
    }

    public java.util.Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setCreatedId(Long value) {
        this.createdId = value;
    }

    public Long getCreatedId() {
        return this.createdId;
    }

    public void setModifiedId(Long value) {
        this.modifiedId = value;
    }

    public Long getModifiedId() {
        return this.modifiedId;
    }

    public Double getWeight() {
        return weight;
    }

    
    
}
