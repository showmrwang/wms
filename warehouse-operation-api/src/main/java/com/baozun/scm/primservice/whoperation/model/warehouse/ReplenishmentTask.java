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
public class ReplenishmentTask extends BaseModel {
	
	private static final long serialVersionUID = -8809101079286430345L;
	
	//columns START
	/** 补货编码 */
	private String replenishmentCode;
	/** 波次ID 用于波次补货 */
	private Long waveId;
	/** 库位ID 用于货品库位容量补货 */
	private Long locationId;
	/** 状态 */
	private Integer status;
	/** 仓库组织ID */
	private Long ouId;
	/** 是否已创建工作 */
    private Boolean isCreateWork;
	/** 创建时间 */
	private Date createTime;
	/** 修改时间 */
	private Date lastModifyTime;
	/** 操作人ID */
	private Long operatorId;
	//columns END

	public ReplenishmentTask(){
	}
	
	public ReplenishmentTask(Long id) {
		this.id = id;
	}
	
	public void setReplenishmentCode(String value) {
		this.replenishmentCode = value;
	}
	public String getReplenishmentCode() {
		return this.replenishmentCode;
	}
	public void setWaveId(Long value) {
		this.waveId = value;
	}
	public Long getWaveId() {
		return this.waveId;
	}
	public void setLocationId(Long value) {
		this.locationId = value;
	}
	public Long getLocationId() {
		return this.locationId;
	}
	public void setStatus(Integer value) {
		this.status = value;
	}
	public Integer getStatus() {
		return this.status;
	}
	public void setOuId(Long value) {
		this.ouId = value;
	}
	public Long getOuId() {
		return this.ouId;
	}
	public void setCreateTime(Date value) {
		this.createTime = value;
	}
	public Date getCreateTime() {
		return this.createTime;
	}
	public void setLastModifyTime(Date value) {
		this.lastModifyTime = value;
	}
	public Date getLastModifyTime() {
		return this.lastModifyTime;
	}
	public void setOperatorId(Long value) {
		this.operatorId = value;
	}
	public Long getOperatorId() {
		return this.operatorId;
	}
    public Boolean getIsCreateWork() {
        return isCreateWork;
    }
    public void setIsCreateWork(Boolean isCreateWork) {
        this.isCreateWork = isCreateWork;
    }
}

