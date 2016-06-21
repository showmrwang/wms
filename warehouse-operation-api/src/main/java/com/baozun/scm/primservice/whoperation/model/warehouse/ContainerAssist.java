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
 * 
 * @author larkark
 *
 */
public class ContainerAssist extends BaseModel {

    private static final long serialVersionUID = 6472904910596053276L;
    // alias
    public static final String TABLE_ALIAS = "ContainerAssist";
    public static final String ALIAS_CONTAINER_ID = "容器ID";
    public static final String ALIAS_SYS_WEIGHT = "系统重量";
    public static final String ALIAS_SYS_VOLUME = "系统体积";
    public static final String ALIAS_SYS_LENGTH = "系统长度";
    public static final String ALIAS_SYS_WIDTH = "系统宽度";
    public static final String ALIAS_SYS_HEIGHT = "系统高度";
    public static final String ALIAS_OU_ID = "仓库组织ID";
    public static final String ALIAS_CREATE_TIME = "创建时间";
    public static final String ALIAS_LAST_MODIFY_TIME = "最后修改时间";
    public static final String ALIAS_OPERATOR_ID = "操作人ID";
    public static final String ALIAS_LIFECYCLE = "1.可用;2.已删除;0.禁用";
    public static final String ALIAS_CARTON_QTY = "箱数量";
    public static final String ALIAS_SKU_CATEGORY = "SKU种类";
    public static final String ALIAS_SKU_QTY = "SKU数量";
    public static final String ALIAS_STORE_QTY = "店铺数量";
    public static final String ALIAS_SKU_ATTR_CATEGORY = "唯一SKU数";

    // columns START
    /** 容器ID */
    private java.lang.Long containerId;
    /** 系统重量 */
    private Double sysWeight;
    /** 系统体积 */
    private Double sysVolume;
    /** 系统长度 */
    private Double sysLength;
    /** 系统宽度 */
    private Double sysWidth;
    /** 系统高度 */
    private Double sysHeight;
    /** 仓库组织ID */
    private java.lang.Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private java.lang.Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private java.lang.Integer lifecycle;
    /** 箱数量 */
    private java.lang.Long cartonQty;
    /** SKU种类 */
    private java.lang.Long skuCategory;
    /** SKU数量 */
    private java.lang.Long skuQty;
    /** 店铺数量 */
    private java.lang.Long storeQty;
    /** 唯一sku数 */
    private java.lang.Long skuAttrCategory;
    // columns END

    public ContainerAssist() {}

    public ContainerAssist(java.lang.Long id) {
        this.id = id;
    }

    public void setContainerId(java.lang.Long value) {
        this.containerId = value;
    }

    public java.lang.Long getContainerId() {
        return this.containerId;
    }

    public void setSysWeight(Double value) {
        this.sysWeight = value;
    }

    public Double getSysWeight() {
        return this.sysWeight;
    }

    public void setSysVolume(Double value) {
        this.sysVolume = value;
    }

    public Double getSysVolume() {
        return this.sysVolume;
    }

    public void setSysLength(Double value) {
        this.sysLength = value;
    }

    public Double getSysLength() {
        return this.sysLength;
    }

    public void setSysWidth(Double value) {
        this.sysWidth = value;
    }

    public Double getSysWidth() {
        return this.sysWidth;
    }

    public void setSysHeight(Double value) {
        this.sysHeight = value;
    }

    public Double getSysHeight() {
        return this.sysHeight;
    }

    public void setOuId(java.lang.Long value) {
        this.ouId = value;
    }

    public java.lang.Long getOuId() {
        return this.ouId;
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

    public void setOperatorId(java.lang.Long value) {
        this.operatorId = value;
    }

    public java.lang.Long getOperatorId() {
        return this.operatorId;
    }

    public void setLifecycle(java.lang.Integer value) {
        this.lifecycle = value;
    }

    public java.lang.Integer getLifecycle() {
        return this.lifecycle;
    }

    public void setCartonQty(java.lang.Long value) {
        this.cartonQty = value;
    }

    public java.lang.Long getCartonQty() {
        return this.cartonQty;
    }

    public void setSkuCategory(java.lang.Long value) {
        this.skuCategory = value;
    }

    public java.lang.Long getSkuCategory() {
        return this.skuCategory;
    }

    public void setSkuQty(java.lang.Long value) {
        this.skuQty = value;
    }

    public java.lang.Long getSkuQty() {
        return this.skuQty;
    }

    public void setStoreQty(java.lang.Long value) {
        this.storeQty = value;
    }

    public java.lang.Long getStoreQty() {
        return this.storeQty;
    }

    public java.lang.Long getSkuAttrCategory() {
        return skuAttrCategory;
    }

    public void setSkuAttrCategory(java.lang.Long skuAttrCategory) {
        this.skuAttrCategory = skuAttrCategory;
    }
    


}

