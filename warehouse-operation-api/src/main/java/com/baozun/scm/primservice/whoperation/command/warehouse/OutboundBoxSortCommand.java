/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OutboundBoxSortCommand extends BaseCommand {

    private static final long serialVersionUID = -1097968317561877965L;

    /** 主键ID */
    private Long id;
    /** 出库箱装箱规则ID */
    private Long outboundboxRuleId;
    /** 规则条件ID */
    private Long ruleConditionId;
    /** 排序方式：ASC升序DESC降序 */
    private String sortord;
    /** 排序 */
    private Integer sort;
    /** ouId */
    private Long ouId;
    /** 规则条件字段名 */
    private String ruleConditionName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOutboundboxRuleId() {
        return outboundboxRuleId;
    }

    public void setOutboundboxRuleId(Long outboundboxRuleId) {
        this.outboundboxRuleId = outboundboxRuleId;
    }

    public Long getRuleConditionId() {
        return ruleConditionId;
    }

    public void setRuleConditionId(Long ruleConditionId) {
        this.ruleConditionId = ruleConditionId;
    }

    public String getSortord() {
        return sortord;
    }

    public void setSortord(String sortord) {
        this.sortord = sortord;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getRuleConditionName() {
        return ruleConditionName;
    }

    public void setRuleConditionName(String ruleConditionName) {
        this.ruleConditionName = ruleConditionName;
    }
}
