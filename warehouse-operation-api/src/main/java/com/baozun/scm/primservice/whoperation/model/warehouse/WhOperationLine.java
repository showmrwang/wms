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
public class WhOperationLine extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = 7607310913535193662L;
    
    //columns START
    /** 作业ID */
    private java.lang.Long operationId;
    /** 工作明细ID */
    private java.lang.Long workLineId;
    /** 仓库组织ID */
    private java.lang.Long ouId;
    /** 操作开始时间 */
    private java.util.Date startTime;
    /** 操作结束时间 */
    private java.util.Date finishTime;
    /** 商品ID */
    private java.lang.Long skuId;
    /** 计划量 */
    private Double qty;
    /** 执行量 */
    private Double completeQty;
    /** 取消量 */
    private Double cancelQty;
    /** 库存状态 */
    private java.lang.Long invStatus;
    /** 库存类型 */
    private java.lang.String invType;
    /** 批次号 */
    private java.lang.String batchNumber;
    /** 生产日期 */
    private java.util.Date mfgDate;
    /** 失效日期 */
    private java.util.Date expDate;
    /** 最小失效日期 */
    private java.util.Date minExpDate;
    /** 最大失效日期 */
    private java.util.Date maxExpDate;
    /** 原产地 */
    private java.lang.String countryOfOrigin;
    /** 库存属性1 */
    private java.lang.String invAttr1;
    /** 库存属性2 */
    private java.lang.String invAttr2;
    /** 库存属性3 */
    private java.lang.String invAttr3;
    /** 库存属性4 */
    private java.lang.String invAttr4;
    /** 库存属性5 */
    private java.lang.String invAttr5;
    /** 内部对接码 */
    private java.lang.String uuid;
    /** 原始库位 */
    private java.lang.Long fromLocationId;
    /** 原始库位外部容器 */
    private java.lang.Long fromOuterContainerId;
    /** 原始库位内部容器 */
    private java.lang.Long fromInsideContainerId;
    /** 使用出库箱，耗材ID */
    private java.lang.Long useOutboundboxId;
    /** 使用出库箱编码 */
    private java.lang.String useOutboundboxCode;
    /** 使用容器 */
    private java.lang.Long useContainerId;
    /** 使用外部容器，小车 */
    private java.lang.Long useOuterContainerId;
    /** 使用货格编码数 */
    private java.lang.Integer useContainerLatticeNo;
    /** 目标库位 */
    private java.lang.Long toLocationId;
    /** 目标库位外部容器 */
    private java.lang.Long toOuterContainerId;
    /** 目标库位内部容器 */
    private java.lang.Long toInsideContainerId;
    /** 出库单ID */
    private java.lang.Long odoId;
    /** 出库单明细ID */
    private java.lang.Long odoLineId;
    /** 补货单据号 */
    private java.lang.String replenishmentCode;
    /** invMoveCode */
    private java.lang.String invMoveCode;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后操作时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private java.lang.Long operatorId;
    //columns END

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getWorkLineId() {
        return workLineId;
    }

    public void setWorkLineId(Long workLineId) {
        this.workLineId = workLineId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Date getMinExpDate() {
        return minExpDate;
    }

    public void setMinExpDate(Date minExpDate) {
        this.minExpDate = minExpDate;
    }

    public Date getMaxExpDate() {
        return maxExpDate;
    }

    public void setMaxExpDate(Date maxExpDate) {
        this.maxExpDate = maxExpDate;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public Long getFromOuterContainerId() {
        return fromOuterContainerId;
    }

    public void setFromOuterContainerId(Long fromOuterContainerId) {
        this.fromOuterContainerId = fromOuterContainerId;
    }

    public Long getFromInsideContainerId() {
        return fromInsideContainerId;
    }

    public void setFromInsideContainerId(Long fromInsideContainerId) {
        this.fromInsideContainerId = fromInsideContainerId;
    }

    public Long getUseOutboundboxId() {
        return useOutboundboxId;
    }

    public void setUseOutboundboxId(Long useOutboundboxId) {
        this.useOutboundboxId = useOutboundboxId;
    }

    public String getUseOutboundboxCode() {
        return useOutboundboxCode;
    }

    public void setUseOutboundboxCode(String useOutboundboxCode) {
        this.useOutboundboxCode = useOutboundboxCode;
    }

    public Long getUseContainerId() {
        return useContainerId;
    }

    public void setUseContainerId(Long useContainerId) {
        this.useContainerId = useContainerId;
    }

    public Long getUseOuterContainerId() {
        return useOuterContainerId;
    }

    public void setUseOuterContainerId(Long useOuterContainerId) {
        this.useOuterContainerId = useOuterContainerId;
    }

    public Integer getUseContainerLatticeNo() {
        return useContainerLatticeNo;
    }

    public void setUseContainerLatticeNo(Integer useContainerLatticeNo) {
        this.useContainerLatticeNo = useContainerLatticeNo;
    }

    public Long getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }

    public Long getToOuterContainerId() {
        return toOuterContainerId;
    }

    public void setToOuterContainerId(Long toOuterContainerId) {
        this.toOuterContainerId = toOuterContainerId;
    }

    public Long getToInsideContainerId() {
        return toInsideContainerId;
    }

    public void setToInsideContainerId(Long toInsideContainerId) {
        this.toInsideContainerId = toInsideContainerId;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOdoLineId() {
        return odoLineId;
    }

    public void setOdoLineId(Long odoLineId) {
        this.odoLineId = odoLineId;
    }

    public String getReplenishmentCode() {
        return replenishmentCode;
    }

    public void setReplenishmentCode(String replenishmentCode) {
        this.replenishmentCode = replenishmentCode;
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

    public java.lang.String getInvMoveCode() {
        return invMoveCode;
    }

    public void setInvMoveCode(java.lang.String invMoveCode) {
        this.invMoveCode = invMoveCode;
    }

    public Double getCompleteQty() {
        return completeQty;
    }

    public void setCompleteQty(Double completeQty) {
        this.completeQty = completeQty;
    }

    public Double getCancelQty() {
        return cancelQty;
    }

    public void setCancelQty(Double cancelQty) {
        this.cancelQty = cancelQty;
    }

}
