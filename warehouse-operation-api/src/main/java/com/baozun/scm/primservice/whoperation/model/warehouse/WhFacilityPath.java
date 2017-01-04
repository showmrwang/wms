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
 * 
 * @author larkark
 *
 */
public class WhFacilityPath extends BaseModel {
	
	private static final long serialVersionUID = -1724298926141098476L;
	
	//columns START
	/** 从区域类型 */
	private String fromAreaType;
	/** 从区域编码 */
	private String fromAreaCode;
	/** 从区域名称 */
	private String fromAreaName;
	/** 到区域类型 */
	private String toAreaType;
	/** 到区域编码 */
	private String toAreaCode;
	/** 到区域名称 */
	private String toAreaName;
	/** 中转库位编码 */
	private String transitLocationCode;
	/** 仓库组织ID */
	private Long ouId;
	/** 创建时间 */
	private Date createTime;
	/** 操作时间 */
	private Date lastModifyTime;
	/** 操作人ID */
	private Long operatorId;
	//columns END

	public WhFacilityPath(){}

	public WhFacilityPath(Long id){
		this.id = id;
	}
	
	public String getFromAreaType() {
		return fromAreaType;
	}

	public void setFromAreaType(String fromAreaType) {
		this.fromAreaType = fromAreaType;
	}

	public String getFromAreaCode() {
		return fromAreaCode;
	}

	public void setFromAreaCode(String fromAreaCode) {
		this.fromAreaCode = fromAreaCode;
	}

	public String getFromAreaName() {
		return fromAreaName;
	}

	public void setFromAreaName(String fromAreaName) {
		this.fromAreaName = fromAreaName;
	}

	public String getToAreaType() {
		return toAreaType;
	}

	public void setToAreaType(String toAreaType) {
		this.toAreaType = toAreaType;
	}

	public String getToAreaCode() {
		return toAreaCode;
	}

	public void setToAreaCode(String toAreaCode) {
		this.toAreaCode = toAreaCode;
	}

	public String getToAreaName() {
		return toAreaName;
	}

	public void setToAreaName(String toAreaName) {
		this.toAreaName = toAreaName;
	}

	public String getTransitLocationCode() {
		return transitLocationCode;
	}

	public void setTransitLocationCode(String transitLocationCode) {
		this.transitLocationCode = transitLocationCode;
	}

	public Long getOuId() {
		return ouId;
	}

	public void setOuId(Long ouId) {
		this.ouId = ouId;
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
}

