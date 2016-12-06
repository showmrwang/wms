/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


/**
 * 
 * @author larkark
 *
 */
public class WhWorkLineCommand extends BaseCommand {
	
	/**
     * 
     */
    private static final long serialVersionUID = -6219910037554120940L;
	
	//columns START
    
    /** 主键ID */
    private java.lang.Long id;
	/** 工作明细号 */
	private java.lang.String lineCode;
	/** 工作ID */
	private java.lang.Long workId;
	/** 仓库组织ID */
	private java.lang.Long ouId;
	/** 操作开始时间 */
	private java.util.Date startTime;
	/** 操作结束时间 */
	private java.util.Date finishTime;
	/** 商品ID */
	private java.lang.Long skuId;
	/** 计划量 */
	private java.lang.Double qty;
	/** 执行量/完成量 */
	private java.lang.Double completeQty;
	/** 取消量 */
	private java.lang.Double cancelQty;
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
	/** 是否整托整箱 */
    private java.lang.Boolean isWholeCase;
	/** 出库单ID */
	private java.lang.Long odoId;
	/** 出库单明细ID */
	private java.lang.Long odoLineId;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 最后操作时间 */
	private java.util.Date lastModifyTime;
	/** 操作人ID */
	private java.lang.Long operatorId;
	
	//columns END
	
    public java.lang.Long getId() {
        return id;
    }
    public void setId(java.lang.Long id) {
        this.id = id;
    }
    public java.lang.String getLineCode() {
        return lineCode;
    }
    public void setLineCode(java.lang.String lineCode) {
        this.lineCode = lineCode;
    }
    public java.lang.Long getWorkId() {
        return workId;
    }
    public void setWorkId(java.lang.Long workId) {
        this.workId = workId;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.util.Date getStartTime() {
        return startTime;
    }
    public void setStartTime(java.util.Date startTime) {
        this.startTime = startTime;
    }
    public java.util.Date getFinishTime() {
        return finishTime;
    }
    public void setFinishTime(java.util.Date finishTime) {
        this.finishTime = finishTime;
    }
    public java.lang.Long getSkuId() {
        return skuId;
    }
    public void setSkuId(java.lang.Long skuId) {
        this.skuId = skuId;
    }
    public java.lang.Long getInvStatus() {
        return invStatus;
    }
    public void setInvStatus(java.lang.Long invStatus) {
        this.invStatus = invStatus;
    }
    public java.lang.String getInvType() {
        return invType;
    }
    public void setInvType(java.lang.String invType) {
        this.invType = invType;
    }
    public java.lang.String getBatchNumber() {
        return batchNumber;
    }
    public void setBatchNumber(java.lang.String batchNumber) {
        this.batchNumber = batchNumber;
    }
    public java.util.Date getMfgDate() {
        return mfgDate;
    }
    public void setMfgDate(java.util.Date mfgDate) {
        this.mfgDate = mfgDate;
    }
    public java.util.Date getExpDate() {
        return expDate;
    }
    public void setExpDate(java.util.Date expDate) {
        this.expDate = expDate;
    }
    public java.util.Date getMinExpDate() {
        return minExpDate;
    }
    public void setMinExpDate(java.util.Date minExpDate) {
        this.minExpDate = minExpDate;
    }
    public java.util.Date getMaxExpDate() {
        return maxExpDate;
    }
    public void setMaxExpDate(java.util.Date maxExpDate) {
        this.maxExpDate = maxExpDate;
    }
    public java.lang.String getCountryOfOrigin() {
        return countryOfOrigin;
    }
    public void setCountryOfOrigin(java.lang.String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }
    public java.lang.String getInvAttr1() {
        return invAttr1;
    }
    public void setInvAttr1(java.lang.String invAttr1) {
        this.invAttr1 = invAttr1;
    }
    public java.lang.String getInvAttr2() {
        return invAttr2;
    }
    public void setInvAttr2(java.lang.String invAttr2) {
        this.invAttr2 = invAttr2;
    }
    public java.lang.String getInvAttr3() {
        return invAttr3;
    }
    public void setInvAttr3(java.lang.String invAttr3) {
        this.invAttr3 = invAttr3;
    }
    public java.lang.String getInvAttr4() {
        return invAttr4;
    }
    public void setInvAttr4(java.lang.String invAttr4) {
        this.invAttr4 = invAttr4;
    }
    public java.lang.String getInvAttr5() {
        return invAttr5;
    }
    public void setInvAttr5(java.lang.String invAttr5) {
        this.invAttr5 = invAttr5;
    }
    public java.lang.Long getFromLocationId() {
        return fromLocationId;
    }
    public void setFromLocationId(java.lang.Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }
    public java.lang.Long getFromOuterContainerId() {
        return fromOuterContainerId;
    }
    public void setFromOuterContainerId(java.lang.Long fromOuterContainerId) {
        this.fromOuterContainerId = fromOuterContainerId;
    }
    public java.lang.Long getFromInsideContainerId() {
        return fromInsideContainerId;
    }
    public void setFromInsideContainerId(java.lang.Long fromInsideContainerId) {
        this.fromInsideContainerId = fromInsideContainerId;
    }
    public java.lang.Long getUseOutboundboxId() {
        return useOutboundboxId;
    }
    public void setUseOutboundboxId(java.lang.Long useOutboundboxId) {
        this.useOutboundboxId = useOutboundboxId;
    }
    public java.lang.String getUseOutboundboxCode() {
        return useOutboundboxCode;
    }
    public void setUseOutboundboxCode(java.lang.String useOutboundboxCode) {
        this.useOutboundboxCode = useOutboundboxCode;
    }
    public java.lang.Long getUseContainerId() {
        return useContainerId;
    }
    public void setUseContainerId(java.lang.Long useContainerId) {
        this.useContainerId = useContainerId;
    }
    public java.lang.Long getUseOuterContainerId() {
        return useOuterContainerId;
    }
    public void setUseOuterContainerId(java.lang.Long useOuterContainerId) {
        this.useOuterContainerId = useOuterContainerId;
    }
    public java.lang.Integer getUseContainerLatticeNo() {
        return useContainerLatticeNo;
    }
    public void setUseContainerLatticeNo(java.lang.Integer useContainerLatticeNo) {
        this.useContainerLatticeNo = useContainerLatticeNo;
    }
    public java.lang.Long getToLocationId() {
        return toLocationId;
    }
    public void setToLocationId(java.lang.Long toLocationId) {
        this.toLocationId = toLocationId;
    }
    public java.lang.Long getToOuterContainerId() {
        return toOuterContainerId;
    }
    public void setToOuterContainerId(java.lang.Long toOuterContainerId) {
        this.toOuterContainerId = toOuterContainerId;
    }
    public java.lang.Long getToInsideContainerId() {
        return toInsideContainerId;
    }
    public void setToInsideContainerId(java.lang.Long toInsideContainerId) {
        this.toInsideContainerId = toInsideContainerId;
    }
    public java.lang.Boolean getIsWholeCase() {
        return isWholeCase;
    }
    public void setIsWholeCase(java.lang.Boolean isWholeCase) {
        this.isWholeCase = isWholeCase;
    }
    public java.lang.Long getOdoId() {
        return odoId;
    }
    public void setOdoId(java.lang.Long odoId) {
        this.odoId = odoId;
    }
    public java.lang.Long getOdoLineId() {
        return odoLineId;
    }
    public void setOdoLineId(java.lang.Long odoLineId) {
        this.odoLineId = odoLineId;
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
    public java.lang.Long getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(java.lang.Long operatorId) {
        this.operatorId = operatorId;
    }
    public java.lang.Double getQty() {
        return qty;
    }
    public void setQty(java.lang.Double qty) {
        this.qty = qty;
    }
    public java.lang.Double getCompleteQty() {
        return completeQty;
    }
    public void setCompleteQty(java.lang.Double completeQty) {
        this.completeQty = completeQty;
    }
    public java.lang.Double getCancelQty() {
        return cancelQty;
    }
    public void setCancelQty(java.lang.Double cancelQty) {
        this.cancelQty = cancelQty;
    }
    public java.lang.String getUuid() {
        return uuid;
    }
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }
}

