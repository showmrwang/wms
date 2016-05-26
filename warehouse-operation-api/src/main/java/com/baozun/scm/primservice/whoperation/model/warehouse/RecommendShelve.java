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
public class RecommendShelve extends BaseModel {

    private static final long serialVersionUID = 3429860651760852196L;

    /** 上架推荐规则ID */
    private Long shelveRecommendRuleId;
    /** 上架区域ID */
    private Long shelveAreaId;
    /** 库位推荐规则 */
    private String locationRule;
    /** 优先级 */
    private Integer priority;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后操作时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 操作人ID */
    private Long modifiedId;

    public Long getShelveRecommendRuleId() {
        return shelveRecommendRuleId;
    }

    public void setShelveRecommendRuleId(Long shelveRecommendRuleId) {
        this.shelveRecommendRuleId = shelveRecommendRuleId;
    }

    public Long getShelveAreaId() {
        return shelveAreaId;
    }

    public void setShelveAreaId(Long shelveAreaId) {
        this.shelveAreaId = shelveAreaId;
    }

    public String getLocationRule() {
        return locationRule;
    }

    public void setLocationRule(String locationRule) {
        this.locationRule = locationRule;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
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
}
