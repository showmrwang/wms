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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class OutboundBoxSort extends BaseModel {

    private static final long serialVersionUID = -9031392217575292441L;

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
}
