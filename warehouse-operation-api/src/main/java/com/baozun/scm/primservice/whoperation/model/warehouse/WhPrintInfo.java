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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 打印信息表(t_wh_print_info)
 * 
 * @author larkark
 *
 */
public class WhPrintInfo extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = -8918726234171605466L;
    
    //columns START
	/** 播种墙ID */
	private java.lang.Long facilityId;
	/** 周转箱ID */
	private java.lang.Long containerId;
	/** 容器号 */
	private java.lang.String containerCode;
	/** 小批次 */
	private java.lang.String batch;
	/** 波次号 */
	private java.lang.String waveCode;
	/** 对应组织ID */
	private java.lang.Long ouId;
	/** 外部容器，小车 */
	private java.lang.Long outerContainerId;
	/** 外部容器号 */
	private java.lang.String outerContainerCode;
	/** 货格编码数 */
	private java.lang.Integer containerLatticeNo;
	/** 耗材ID */
	private java.lang.Long outboundboxId;
	/** 出库箱编码 */
	private java.lang.String outboundboxCode;
	/** 打印单据类型 */
	private java.lang.String printType;
	/** 打印次数 */
	private java.lang.Integer printCount;
	//columns END
	
	
    public java.lang.Long getFacilityId() {
        return facilityId;
    }
    public void setFacilityId(java.lang.Long facilityId) {
        this.facilityId = facilityId;
    }
    public java.lang.Long getContainerId() {
        return containerId;
    }
    public void setContainerId(java.lang.Long containerId) {
        this.containerId = containerId;
    }
    public java.lang.String getContainerCode() {
        return containerCode;
    }
    public void setContainerCode(java.lang.String containerCode) {
        this.containerCode = containerCode;
    }
    public java.lang.String getBatch() {
        return batch;
    }
    public void setBatch(java.lang.String batch) {
        this.batch = batch;
    }
    public java.lang.String getWaveCode() {
        return waveCode;
    }
    public void setWaveCode(java.lang.String waveCode) {
        this.waveCode = waveCode;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.lang.Long getOuterContainerId() {
        return outerContainerId;
    }
    public void setOuterContainerId(java.lang.Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }
    public java.lang.String getOuterContainerCode() {
        return outerContainerCode;
    }
    public void setOuterContainerCode(java.lang.String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }
    public java.lang.Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }
    public void setContainerLatticeNo(java.lang.Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
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
    public java.lang.String getPrintType() {
        return printType;
    }
    public void setPrintType(java.lang.String printType) {
        this.printType = printType;
    }
    public java.lang.Integer getPrintCount() {
        return printCount;
    }
    public void setPrintCount(java.lang.Integer printCount) {
        this.printCount = printCount;
    }

}

