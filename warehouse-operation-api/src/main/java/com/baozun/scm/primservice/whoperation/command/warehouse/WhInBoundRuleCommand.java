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

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark
 * 
 */
public class WhInBoundRuleCommand extends BaseCommand {

    private static final long serialVersionUID = -844410122315150697L;

    /** 主键ID */
    private Long id;
    /** 入库分拣规则 */
    private String inboundRuleName;
    /** 入库分拣规则编码 */
    private String inboundRuleCode;
    /** 规则描述 */
    private String rule;
    /** 规则sql */
    private String ruleSql;
    /** 规则结果条件ID */
    private String sortingConditionIds;
    /** 规则结果SQL */
    private String sortingSql;
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
    /** 库存记录ID */
    private Long inventoryId;
    /** 规则测试结果 */
    private Boolean ruleSqlTestResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInboundRuleName() {
        return inboundRuleName;
    }

    public void setInboundRuleName(String inboundRuleName) {
        this.inboundRuleName = inboundRuleName;
    }

    public String getInboundRuleCode() {
        return inboundRuleCode;
    }

    public void setInboundRuleCode(String inboundRuleCode) {
        this.inboundRuleCode = inboundRuleCode;
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

    public String getSortingConditionIds() {
        return sortingConditionIds;
    }

    public void setSortingConditionIds(String sortingConditionIds) {
        this.sortingConditionIds = sortingConditionIds;
    }

    public String getSortingSql() {
        return sortingSql;
    }

    public void setSortingSql(String sortingSql) {
        this.sortingSql = sortingSql;
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

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Boolean getRuleSqlTestResult() {
        return ruleSqlTestResult;
    }

    public void setRuleSqlTestResult(Boolean ruleSqlTestResult) {
        this.ruleSqlTestResult = ruleSqlTestResult;
    }
}
