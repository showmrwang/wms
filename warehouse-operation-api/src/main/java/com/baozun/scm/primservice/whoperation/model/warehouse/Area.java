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
 * 区域
 * T_WH_AREA
 * @author gianni.zhang
 *
 */
public class Area extends BaseModel {
	
    private static final long serialVersionUID = 7486976375360795476L;

    //columns START
    /** ou id */
	private Long ouId;
	
	/** 区域编码*/
	private String areaCode;

	/** 区域名称 */
	private String areaName;
    
    /** 区域类型 */
	private String areaType;
    
    /** 创建时间 */
	private Date createTime;
    
    /** 修改时间 */
	private Date lastModifyTime;
    
    /** 操作者id */
	private Long operatorId;
    
    /** 生命周期*/
	private Integer lifecycle = BaseModel.LIFECYCLE_NORMAL;
	//columns END

	public void setOuId(Long value) {
		this.ouId = value;
	}
	
	public Long getOuId() {
		return this.ouId;
	}
	
	public void setAreaCode(String value) {
		this.areaCode = value;
	}
	
	public String getAreaCode() {
		return this.areaCode;
	}
	
	public void setAreaName(String value) {
		this.areaName = value;
	}
	
	public String getAreaName() {
		return this.areaName;
	}
	
	public void setAreaType(String value) {
		this.areaType = value;
	}
	
	public String getAreaType() {
		return this.areaType;
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
	
	public void setOperatorId(Long value) {
		this.operatorId = value;
	}
	
	public Long getOperatorId() {
		return this.operatorId;
	}
	
	public void setLifecycle(Integer value) {
		this.lifecycle = value;
	}
	
	public Integer getLifecycle() {
		return this.lifecycle;
	}
}

