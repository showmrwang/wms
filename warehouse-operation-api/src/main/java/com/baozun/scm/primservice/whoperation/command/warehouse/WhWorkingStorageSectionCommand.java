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

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 暂存区域
 * @author larkark
 *
 */
public class WhWorkingStorageSectionCommand extends BaseCommand {
	
	private static final long serialVersionUID = -6224741170286187475L;
	
	//columns START
	/** id */
	private Long id;
	/** 暂存区编码 */
	private String workingStorageSectionCode;
	/** 暂存区名称 */
	private String workingStorageSectionName;
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
	
	// 自定义
	/** 关联的暂存区域id集合 */
	private List<Long> temporaryStorageIdList;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWorkingStorageSectionCode() {
		return workingStorageSectionCode;
	}
	public void setWorkingStorageSectionCode(String workingStorageSectionCode) {
		this.workingStorageSectionCode = workingStorageSectionCode;
	}
	public String getWorkingStorageSectionName() {
		return workingStorageSectionName;
	}
	public void setWorkingStorageSectionName(String workingStorageSectionName) {
		this.workingStorageSectionName = workingStorageSectionName;
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
	public List<Long> getTemporaryStorageIdList() {
		return temporaryStorageIdList;
	}
	public void setTemporaryStorageIdList(List<Long> temporaryStorageIdList) {
		this.temporaryStorageIdList = temporaryStorageIdList;
	}

}

