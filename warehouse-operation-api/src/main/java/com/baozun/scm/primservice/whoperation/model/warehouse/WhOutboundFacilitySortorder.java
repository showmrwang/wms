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
 * 
 * @author larkark
 *
 */
public class WhOutboundFacilitySortorder extends BaseModel {
	
	private static final long serialVersionUID = -5653820155459833336L;
	
	//columns START
	/** 设施ID */
	private Long facilityId;
	/** 设施类型 */
	private String facilityType;
	/** 优先顺序 */
	private Integer priority;
	/** 组织ID */
	private Long ouId;
	//columns END

	public WhOutboundFacilitySortorder(){}

	public WhOutboundFacilitySortorder(Long id){
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
}

