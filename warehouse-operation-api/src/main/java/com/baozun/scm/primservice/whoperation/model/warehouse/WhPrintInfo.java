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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 打印信息表(t_wh_print_info)
 * 
 * @author larkark
 *
 */
public class WhPrintInfo extends BaseModel {
	
    private static final long serialVersionUID = -8918726234171605466L;
    
    //columns START
    /** 出库单id */
    private Long odoId;
    /** 出库单code */
    private String odoCode;
    /** 运单号 */
    private String waybillCode;
    /** 播种墙ID */
    private Long facilityId;
    /** 周转箱ID */
    private Long containerId;
    /** 容器号 */
    private String containerCode;
    /** 小批次 */
    private String batch;
    /** 波次号 */
    private String waveCode;
    /** 对应组织ID */
    private Long ouId;
    /** 外部容器，小车 */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 货格编码数 */
    private Integer containerLatticeNo;
    /** 耗材ID */
    private Long outboundboxId;
    /** 出库箱编码 */
    private String outboundboxCode;
    /** 打印单据类型 */
    private String printType;
    /** 打印次数 */
    private Integer printCount;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;
    //columns END
    /** 当前月份 用于归档 */
    private String sysDate;
	
    public Long getFacilityId() {
        return facilityId;
    }
    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }
    public Long getContainerId() {
        return containerId;
    }
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }
    public String getContainerCode() {
        return containerCode;
    }
    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }
    public String getBatch() {
        return batch;
    }
    public void setBatch(String batch) {
        this.batch = batch;
    }
    public String getWaveCode() {
        return waveCode;
    }
    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public Long getOuterContainerId() {
        return outerContainerId;
    }
    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }
    public String getOuterContainerCode() {
        return outerContainerCode;
    }
    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }
    public Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }
    public void setContainerLatticeNo(Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }
    public Long getOutboundboxId() {
        return outboundboxId;
    }
    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }
    public String getOutboundboxCode() {
        return outboundboxCode;
    }
    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }
    public String getPrintType() {
        return printType;
    }
    public void setPrintType(String printType) {
        this.printType = printType;
    }
    public Integer getPrintCount() {
        return printCount;
    }
    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }
    public Long getOdoId() {
        return odoId;
    }
    public String getOdoCode() {
        return odoCode;
    }
    public String getWaybillCode() {
        return waybillCode;
    }
    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }
    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }
    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
    public Long getCreateId() {
        return createId;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public Date getLastModifyTime() {
        return lastModifyTime;
    }
    public Long getModifiedId() {
        return modifiedId;
    }
    public void setCreateId(Long createId) {
        this.createId = createId;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }
    public String getSysDate() {
        return sysDate;
    }
    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }
    
}

