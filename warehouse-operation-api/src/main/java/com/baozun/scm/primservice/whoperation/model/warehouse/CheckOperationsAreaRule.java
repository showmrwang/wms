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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class CheckOperationsAreaRule extends BaseModel {

    private static final long serialVersionUID = -3820325940316369600L;

    /** 复核台推荐规则名称 */
    private String checkOperationsAreaRuleName;
    /** 复核台推荐规则编码 */
    private String checkOperationsAreaRuleCode;
    /** 规则条件 */
    private String rule;
    /** 规则SQL */
    private String ruleSql;
    /** 复核台ID */
    private Long outboundFacilityId;
    /** 复核台分组ID */
    private Long outboundFacilityGroupId;
    /** 分拣结果属数据来源 */
    private String dataSource;
    /** 仓库组织ID */
    private Long ouId;
    /** 优先级 */
    private Integer priority;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;

    public String getCheckOperationsAreaRuleName() {
        return checkOperationsAreaRuleName;
    }

    public void setCheckOperationsAreaRuleName(String checkOperationsAreaRuleName) {
        this.checkOperationsAreaRuleName = checkOperationsAreaRuleName;
    }

    public String getCheckOperationsAreaRuleCode() {
        return checkOperationsAreaRuleCode;
    }

    public void setCheckOperationsAreaRuleCode(String checkOperationsAreaRuleCode) {
        this.checkOperationsAreaRuleCode = checkOperationsAreaRuleCode;
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

    public Long getOutboundFacilityId() {
        return outboundFacilityId;
    }

    public void setOutboundFacilityId(Long outboundFacilityId) {
        this.outboundFacilityId = outboundFacilityId;
    }

    public Long getOutboundFacilityGroupId() {
        return outboundFacilityGroupId;
    }

    public void setOutboundFacilityGroupId(Long outboundFacilityGroupId) {
        this.outboundFacilityGroupId = outboundFacilityGroupId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
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
