package com.baozun.scm.primservice.whoperation.model.warehouse;


import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 *
 * @author larkark
 *
 */
public class RecommendPlatform extends BaseModel {

    private static final long serialVersionUID = 2169858659226775428L;

    /** 月台推荐规则ID */
    private Long platformRecommendRuleId;
    /** 月台ID */
    private Long platformId;
    /** 优先级 */
    private Integer priority;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后操作时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
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

    public Long getPlatformRecommendRuleId() {
        return platformRecommendRuleId;
    }

    public void setPlatformRecommendRuleId(Long platformRecommendRuleId) {
        this.platformRecommendRuleId = platformRecommendRuleId;
    }
}
