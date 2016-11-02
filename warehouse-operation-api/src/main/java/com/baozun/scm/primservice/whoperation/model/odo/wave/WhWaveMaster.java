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
package com.baozun.scm.primservice.whoperation.model.odo.wave;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 波次
 * 
 * @author larkark
 *
 */
public class WhWaveMaster extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = -387540770561186348L;
    /**
     * 
     */
    // columns START
    /** 波次主档名称 */
    private java.lang.String name;
    /** 波次主档编码 */
    private java.lang.String code;
    /** 仓库组织ID */
    private java.lang.Long ouId;
    /** 波次条件规则ID */
    private java.lang.Long waveConditionRuleId;
    /** 波次模板ID */
    private java.lang.Long waveTemplateId;
    /** 描述 */
    private java.lang.String description;
    /** 最大出库数量 */
    private java.lang.Integer maxOdoQty;
    /** 最大出库单明细数量 */
    private java.lang.Integer maxOdoLineQty;
    /** 最大商品件数 */
    private java.lang.Integer maxSkuQty;
    /** 最大商品种类数 */
    private java.lang.Integer maxSkuCategoryQty;
    /** 最大体积 */
    private Double maxVolume;
    /** 最大重量 */
    private Double maxWeight;
    /** 是否整单出库0：否 1：是 */
    private java.lang.Boolean isWholeOrderOutbound;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后操作时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private java.lang.Long createdId;
    /** 操作人ID */
    private java.lang.Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private java.lang.Integer lifecycle;
    /**最小出库单数量*/
    private java.lang.Integer  minOdoQty;

    /** 配货模式计算 */
    private Boolean isCalcSeckill;
    private Integer seckillOdoQtys;
    private Boolean isCalcTwoSkuSuit;
    private Integer twoSkuSuitOdoQtys;
    private Boolean isCalcSuits;
    private Integer suitsOdoQtys;
    private Integer rcvdWorkPriority;   //收货工作优先级
    private Integer putawayWorkPriority;  //上架工作优先级
    private Integer pickingWorkPriority; //拣货工作优先级
    private Integer replenishmentWorkPriority;  //补货工作优先级
    private Integer stockCountWorkPriority;  //盘点工作优先级
    private Integer inWarehouseMoveWorkPriority;   //库内移动工作优先级
    private Integer inWarehouseProcessWorkPriority;  //库内加工工作优先级
    private Boolean isAutoReleaseWork;  //是否自动释放工作    默认值：1
    private Integer pickingExtPriority;  //有补货的拣货优先级（库位上同时又拣货和补货时）

    // columns END

    public WhWaveMaster() {}

    public Boolean getIsCalcSeckill() {
        return isCalcSeckill;
    }

    public void setIsCalcSeckill(Boolean isCalcSeckill) {
        this.isCalcSeckill = isCalcSeckill;
    }

    public Integer getSeckillOdoQtys() {
        return seckillOdoQtys;
    }

    public void setSeckillOdoQtys(Integer seckillOdoQtys) {
        this.seckillOdoQtys = seckillOdoQtys;
    }

    public Boolean getIsCalcTwoSkuSuit() {
        return isCalcTwoSkuSuit;
    }

    public void setIsCalcTwoSkuSuit(Boolean isCalcTwoSkuSuit) {
        this.isCalcTwoSkuSuit = isCalcTwoSkuSuit;
    }

    public Integer getTwoSkuSuitOdoQtys() {
        return twoSkuSuitOdoQtys;
    }

    public void setTwoSkuSuitOdoQtys(Integer twoSkuSuitOdoQtys) {
        this.twoSkuSuitOdoQtys = twoSkuSuitOdoQtys;
    }

    public Boolean getIsCalcSuits() {
        return isCalcSuits;
    }

    public void setIsCalcSuits(Boolean isCalcSuits) {
        this.isCalcSuits = isCalcSuits;
    }

    public Integer getSuitsOdoQtys() {
        return suitsOdoQtys;
    }

    public void setSuitsOdoQtys(Integer suitsOdoQtys) {
        this.suitsOdoQtys = suitsOdoQtys;
    }

    public WhWaveMaster(java.lang.Long id) {
        this.id = id;
    }

    public void setName(java.lang.String value) {
        this.name = value;
    }

    public java.lang.String getName() {
        return this.name;
    }

    public void setCode(java.lang.String value) {
        this.code = value;
    }

    public java.lang.String getCode() {
        return this.code;
    }

    public void setOuId(java.lang.Long value) {
        this.ouId = value;
    }

    public java.lang.Long getOuId() {
        return this.ouId;
    }

    public void setWaveConditionRuleId(java.lang.Long value) {
        this.waveConditionRuleId = value;
    }

    public java.lang.Long getWaveConditionRuleId() {
        return this.waveConditionRuleId;
    }

    public void setWaveTemplateId(java.lang.Long value) {
        this.waveTemplateId = value;
    }

    public java.lang.Long getWaveTemplateId() {
        return this.waveTemplateId;
    }

    public void setDescription(java.lang.String value) {
        this.description = value;
    }

    public java.lang.String getDescription() {
        return this.description;
    }

    public void setMaxOdoQty(java.lang.Integer value) {
        this.maxOdoQty = value;
    }

    public java.lang.Integer getMaxOdoQty() {
        return this.maxOdoQty;
    }

    public void setMaxOdoLineQty(java.lang.Integer value) {
        this.maxOdoLineQty = value;
    }

    public java.lang.Integer getMaxOdoLineQty() {
        return this.maxOdoLineQty;
    }

    public void setMaxSkuQty(java.lang.Integer value) {
        this.maxSkuQty = value;
    }

    public java.lang.Integer getMaxSkuQty() {
        return this.maxSkuQty;
    }

    public void setMaxSkuCategoryQty(java.lang.Integer value) {
        this.maxSkuCategoryQty = value;
    }

    public java.lang.Integer getMaxSkuCategoryQty() {
        return this.maxSkuCategoryQty;
    }

    public void setIsWholeOrderOutbound(java.lang.Boolean value) {
        this.isWholeOrderOutbound = value;
    }

    public java.lang.Boolean getIsWholeOrderOutbound() {
        return this.isWholeOrderOutbound;
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

    public void setCreatedId(java.lang.Long value) {
        this.createdId = value;
    }

    public java.lang.Long getCreatedId() {
        return this.createdId;
    }

    public void setModifiedId(java.lang.Long value) {
        this.modifiedId = value;
    }

    public java.lang.Long getModifiedId() {
        return this.modifiedId;
    }

    public void setLifecycle(java.lang.Integer value) {
        this.lifecycle = value;
    }

    public java.lang.Integer getLifecycle() {
        return this.lifecycle;
    }

    public Double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(Double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public java.lang.Integer getMinOdoQty() {
        return minOdoQty;
    }

    public void setMinOdoQty(java.lang.Integer minOdoQty) {
        this.minOdoQty = minOdoQty;
    }

    public Integer getRcvdWorkPriority() {
        return rcvdWorkPriority;
    }

    public void setRcvdWorkPriority(Integer rcvdWorkPriority) {
        this.rcvdWorkPriority = rcvdWorkPriority;
    }

    public Integer getPutawayWorkPriority() {
        return putawayWorkPriority;
    }

    public void setPutawayWorkPriority(Integer putawayWorkPriority) {
        this.putawayWorkPriority = putawayWorkPriority;
    }

    public Integer getPickingWorkPriority() {
        return pickingWorkPriority;
    }

    public void setPickingWorkPriority(Integer pickingWorkPriority) {
        this.pickingWorkPriority = pickingWorkPriority;
    }

    public Integer getReplenishmentWorkPriority() {
        return replenishmentWorkPriority;
    }

    public void setReplenishmentWorkPriority(Integer replenishmentWorkPriority) {
        this.replenishmentWorkPriority = replenishmentWorkPriority;
    }

    public Integer getStockCountWorkPriority() {
        return stockCountWorkPriority;
    }

    public void setStockCountWorkPriority(Integer stockCountWorkPriority) {
        this.stockCountWorkPriority = stockCountWorkPriority;
    }

    public Integer getInWarehouseMoveWorkPriority() {
        return inWarehouseMoveWorkPriority;
    }

    public void setInWarehouseMoveWorkPriority(Integer inWarehouseMoveWorkPriority) {
        this.inWarehouseMoveWorkPriority = inWarehouseMoveWorkPriority;
    }

    public Integer getInWarehouseProcessWorkPriority() {
        return inWarehouseProcessWorkPriority;
    }

    public void setInWarehouseProcessWorkPriority(Integer inWarehouseProcessWorkPriority) {
        this.inWarehouseProcessWorkPriority = inWarehouseProcessWorkPriority;
    }

    public Boolean getIsAutoReleaseWork() {
        return isAutoReleaseWork;
    }

    public void setIsAutoReleaseWork(Boolean isAutoReleaseWork) {
        this.isAutoReleaseWork = isAutoReleaseWork;
    }

    public Integer getPickingExtPriority() {
        return pickingExtPriority;
    }

    public void setPickingExtPriority(Integer pickingExtPriority) {
        this.pickingExtPriority = pickingExtPriority;
    }
    
    

}
