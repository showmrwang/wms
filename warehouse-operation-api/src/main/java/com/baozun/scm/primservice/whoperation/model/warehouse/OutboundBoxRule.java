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
public class OutboundBoxRule extends BaseModel {

    private static final long serialVersionUID = -2356143093986279395L;

    /** 出库箱装箱规则名称 */
    private String outboundboxRuleName;
    /** 出库箱装箱规则编码 */
    private String outboundboxRuleCode;
    /** 出库箱装箱规则条件 */
    private String outboundboxRule;
    /** 出库箱装箱规则SQL */
    private String outboundboxRuleSql;
    /** 装箱排序拆分要求SQL */
    private String sortSql;
    /** 拆分要求查询条件 */
    private String splitRequire;
    /** 仓库组织ID */
    private Long ouId;
    /** 优先级 */
    private Integer priority;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 最后操作人ID */
    private Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;

    public String getOutboundboxRuleName() {
        return outboundboxRuleName;
    }

    public void setOutboundboxRuleName(String outboundboxRuleName) {
        this.outboundboxRuleName = outboundboxRuleName;
    }

    public String getOutboundboxRuleCode() {
        return outboundboxRuleCode;
    }

    public void setOutboundboxRuleCode(String outboundboxRuleCode) {
        this.outboundboxRuleCode = outboundboxRuleCode;
    }

    public String getOutboundboxRule() {
        return outboundboxRule;
    }

    public void setOutboundboxRule(String outboundboxRule) {
        this.outboundboxRule = outboundboxRule;
    }

    public String getOutboundboxRuleSql() {
        return outboundboxRuleSql;
    }

    public void setOutboundboxRuleSql(String outboundboxRuleSql) {
        this.outboundboxRuleSql = outboundboxRuleSql;
    }

    public String getSortSql() {
        return sortSql;
    }

    public void setSortSql(String sortSql) {
        this.sortSql = sortSql;
    }

    public String getSplitRequire() {
        return splitRequire;
    }

    public void setSplitRequire(String splitRequire) {
        this.splitRequire = splitRequire;
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
