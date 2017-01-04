/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 出库设施分组
 * 
 * @author larkark
 *
 */
public class WhOutboundFacilityGroupCommand extends BaseCommand {

    private static final long serialVersionUID = 4913289741207358984L;

    /** 主键ID */
    private Long id;
    /** 分组编码 */
    private String facilityGroupCode;
    /** 分组名称 */
    private String facilityGroupName;
    /** 分组类型 */
    private String facilityGroupType;
    /** 暂存区域ID */
    private Long workingStorageSectionId;
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
    
    // 自定义
    /** 分组类型集合 */
    private List<String> facilityGroupTypeList;
    /** 暂存区域集合 */
    private List<Long> workingStorageSectionList;
    /** 暂存区域名称 */
    private String workingStorageSectionName;
    /** 分组类型名称 */
    private String facilityGroupTypeName;
    /** 关联的复核台/播种墙id集合 */
    private List<Long> facilityIdList;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFacilityGroupType() {
        return facilityGroupType;
    }

    public void setFacilityGroupType(String facilityGroupType) {
        this.facilityGroupType = facilityGroupType;
    }

    public Long getWorkingStorageSectionId() {
        return workingStorageSectionId;
    }

    public void setWorkingStorageSectionId(Long workingStorageSectionId) {
        this.workingStorageSectionId = workingStorageSectionId;
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

	public List<String> getFacilityGroupTypeList() {
		return facilityGroupTypeList;
	}

	public void setFacilityGroupTypeList(List<String> facilityGroupTypeList) {
		this.facilityGroupTypeList = facilityGroupTypeList;
	}

	public List<Long> getWorkingStorageSectionList() {
		return workingStorageSectionList;
	}

	public void setWorkingStorageSectionList(List<Long> workingStorageSectionList) {
		this.workingStorageSectionList = workingStorageSectionList;
	}

	public String getWorkingStorageSectionName() {
		return workingStorageSectionName;
	}

	public void setWorkingStorageSectionName(String workingStorageSectionName) {
		this.workingStorageSectionName = workingStorageSectionName;
	}

	public String getFacilityGroupTypeName() {
		return facilityGroupTypeName;
	}

	public void setFacilityGroupTypeName(String facilityGroupTypeName) {
		this.facilityGroupTypeName = facilityGroupTypeName;
	}

	public List<Long> getFacilityIdList() {
		return facilityIdList;
	}

	public void setFacilityIdList(List<Long> facilityIdList) {
		this.facilityIdList = facilityIdList;
	}
}
