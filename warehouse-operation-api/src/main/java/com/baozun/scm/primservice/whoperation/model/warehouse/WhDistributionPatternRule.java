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
public class WhDistributionPatternRule extends BaseModel {
	
    /**
     * 
     */
    private static final long serialVersionUID = 6576824233839161909L;
    
	//columns START
    
    /** 配货模式名称 */
	private String distributionPatternName;
	/** 配货模式编码 */
	private String distributionPatternCode;
	/** 规则描述 */
	private String rule;
	/** 规则SQL */
	private String ruleSql;
	/** 仓库组织ID */
	private Long ouId;
	/** 拣货模式 */
	private Integer pickingMode;
	/** 优先级 */
	private Integer priority;
	/** 是否需要出库箱 */
	private Boolean isNeedOutboundCarton;
	/** 描述 */
	private String description;
	/** 创建时间 */
	private Date createTime;
	/** 最后操作时间 */
	private Date lastModifyTime;
	/** 创建人ID */
	private Long createdId;
	/** 操作人ID */
	private Long modifiedId;
	/** 是否启用 1:启用 0:停用 */
	private Integer lifecycle;
	
	//columns END
	
    public String getDistributionPatternName() {
        return distributionPatternName;
    }
    public void setDistributionPatternName(String distributionPatternName) {
        this.distributionPatternName = distributionPatternName;
    }
    public String getDistributionPatternCode() {
        return distributionPatternCode;
    }
    public void setDistributionPatternCode(String distributionPatternCode) {
        this.distributionPatternCode = distributionPatternCode;
    }
    public String getRule() {
        return rule;
    }
    public void setRule(String rule) {
        this.rule = rule;
    }
    public String getRuleSql() {
        return ruleSql;
    }
    public void setRuleSql(String ruleSql) {
        this.ruleSql = ruleSql;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public Integer getPickingMode() {
        return pickingMode;
    }
    public void setPickingMode(Integer pickingMode) {
        this.pickingMode = pickingMode;
    }
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public Boolean getIsNeedOutboundCarton() {
        return isNeedOutboundCarton;
    }
    public void setIsNeedOutboundCarton(Boolean isNeedOutboundCarton) {
        this.isNeedOutboundCarton = isNeedOutboundCarton;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public Long getCreatedId() {
        return createdId;
    }
    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }
    public Long getModifiedId() {
        return modifiedId;
    }
    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }
    public Integer getLifecycle() {
        return lifecycle;
    }
    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }
}

