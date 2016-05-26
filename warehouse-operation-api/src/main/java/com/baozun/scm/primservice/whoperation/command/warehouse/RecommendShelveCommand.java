package com.baozun.scm.primservice.whoperation.command.warehouse;


import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class RecommendShelveCommand extends BaseCommand {

    private static final long serialVersionUID = 7122323764014769839L;

    /** 主键ID */
    private Long id;
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
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 操作人ID */
    private Long modifiedId;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
