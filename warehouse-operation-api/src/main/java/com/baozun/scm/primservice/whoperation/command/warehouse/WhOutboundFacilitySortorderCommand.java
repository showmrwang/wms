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
public class WhOutboundFacilitySortorderCommand extends BaseCommand {
	
	private static final long serialVersionUID = -5653820155459833336L;
	
	/** id */
	private Long id;
	/** 设施ID */
	private Long facilityId;
	/** 设施类型 */
	private String facilityType;
	/** 优先顺序 */
	private Integer priority;
	/** 组织ID */
	private Long ouId;
	
	// 自定义
	/** 分组类型 */
	private String facilityGroupType;
	/** 分组编码 */
	private String facilityGroupCode;
	/** 分组名称 */
	private String facilityGroupName;
	/** 设施编码 */
	private String facilityCode;
	/** 设施名称 */
	private String facilityName;

	public WhOutboundFacilitySortorderCommand(){}

	public WhOutboundFacilitySortorderCommand(Long id){
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFacilityId(Long value) {
		this.facilityId = value;
	}
	
	public Long getFacilityId() {
		return this.facilityId;
	}
	public void setFacilityType(String value) {
		this.facilityType = value;
	}
	
	public String getFacilityType() {
		return this.facilityType;
	}
	public void setPriority(Integer value) {
		this.priority = value;
	}
	
	public Integer getPriority() {
		return this.priority;
	}
	public void setOuId(Long value) {
		this.ouId = value;
	}
	
	public Long getOuId() {
		return this.ouId;
	}

	public String getFacilityGroupType() {
		return facilityGroupType;
	}

	public void setFacilityGroupType(String facilityGroupType) {
		this.facilityGroupType = facilityGroupType;
	}

	public String getFacilityGroupCode() {
		return facilityGroupCode;
	}

	public void setFacilityGroupCode(String facilityGroupCode) {
		this.facilityGroupCode = facilityGroupCode;
	}

	public String getFacilityGroupName() {
		return facilityGroupName;
	}

	public void setFacilityGroupName(String facilityGroupName) {
		this.facilityGroupName = facilityGroupName;
	}

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
}

