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
 * 出库设施基础信息
 * @author larkark
 *
 */
public class WhOutboundFacility extends BaseModel {
	
	private static final long serialVersionUID = -7827801846056186345L;
	
	//columns START
	/** 编码 */
	private String facilityCode;
	/** 名称 */
	private String facilityName;
	/** 类型 */
	private String facilityType;
	/** 出库箱类型 */
	private Long outboundboxTypeId;
	/** 作业/容器容量 */
	private Integer capacity;
	/** 设施容量下限 */
	private Integer facilityLowerLimit;
	/** 设施容量上限 */
	private Integer facilityUpperLimit;
	/** 分组ID */
	private Long facilityGroup;
	/** 效验码 */
	private String checkCode;
	/** 创建时间 */
	private Date createTime;
	/** 修改时间 */
	private Date lastModifyTime;
	/** 操作人ID */
	private Long operatorId;
	/** 1.可用;2.已删除;0.禁用 */
	private Integer lifecycle;
	/** 仓库组织ID */
	private Long ouId;
	//columns END
	
	public String getFacilityCode() {
		return facilityCode;
	}
	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	public Long getOutboundboxTypeId() {
		return outboundboxTypeId;
	}
	public void setOutboundboxTypeId(Long outboundboxTypeId) {
		this.outboundboxTypeId = outboundboxTypeId;
	}
	public Long getFacilityGroup() {
		return facilityGroup;
	}
	public void setFacilityGroup(Long facilityGroup) {
		this.facilityGroup = facilityGroup;
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
	public Long getOuId() {
		return ouId;
	}
	public void setOuId(Long ouId) {
		this.ouId = ouId;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Integer getFacilityLowerLimit() {
		return facilityLowerLimit;
	}
	public void setFacilityLowerLimit(Integer facilityLowerLimit) {
		this.facilityLowerLimit = facilityLowerLimit;
	}
	public Integer getFacilityUpperLimit() {
		return facilityUpperLimit;
	}
	public void setFacilityUpperLimit(Integer facilityUpperLimit) {
		this.facilityUpperLimit = facilityUpperLimit;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

}

