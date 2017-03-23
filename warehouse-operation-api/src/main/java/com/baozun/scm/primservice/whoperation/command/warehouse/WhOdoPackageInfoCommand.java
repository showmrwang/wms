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

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author larkark
 *
 */
public class WhOdoPackageInfoCommand extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = -7925726900197760850L;
    
    //columns START
	/** 出库单ID */
	private java.lang.Long odoId;
	/** 出库箱ID */
	private java.lang.Long outboundboxId;
	/** 出库箱号 */
	private java.lang.String outboundboxCode;
	/** 状态 */
	private java.lang.Integer status;
	/** 计重 */
	private Double calcWeight;
	/** 浮动百分比 */
	private java.lang.Integer floats;
	/** 称重 */
	private Long actualWeight;
	/** 1.可用;0.禁用 */
	private java.lang.Integer lifecycle;
	/** 创建人ID */
	private java.lang.Long createId;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 最后修改时间 */
	private java.util.Date lastModifyTime;
	/** 修改人ID */
	private java.lang.Long modifiedId;
	//columns END
	
    public java.lang.Long getOdoId() {
        return odoId;
    }
    public void setOdoId(java.lang.Long odoId) {
        this.odoId = odoId;
    }
    public java.lang.Long getOutboundboxId() {
        return outboundboxId;
    }
    public void setOutboundboxId(java.lang.Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }
    public java.lang.String getOutboundboxCode() {
        return outboundboxCode;
    }
    public void setOutboundboxCode(java.lang.String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }
    public java.lang.Integer getStatus() {
        return status;
    }
    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }
    public Double getCalcWeight() {
        return calcWeight;
    }
    public void setCalcWeight(Double calcWeight) {
        this.calcWeight = calcWeight;
    }
    public java.lang.Integer getFloats() {
        return floats;
    }
    public void setFloats(java.lang.Integer floats) {
        this.floats = floats;
    }
    public Long getActualWeight() {
        return actualWeight;
    }
    public void setActualWeight(Long actualWeight) {
        this.actualWeight = actualWeight;
    }
    public java.lang.Integer getLifecycle() {
        return lifecycle;
    }
    public void setLifecycle(java.lang.Integer lifecycle) {
        this.lifecycle = lifecycle;
    }
    public java.lang.Long getCreateId() {
        return createId;
    }
    public void setCreateId(java.lang.Long createId) {
        this.createId = createId;
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
    public java.lang.Long getModifiedId() {
        return modifiedId;
    }
    public void setModifiedId(java.lang.Long modifiedId) {
        this.modifiedId = modifiedId;
    }

}

