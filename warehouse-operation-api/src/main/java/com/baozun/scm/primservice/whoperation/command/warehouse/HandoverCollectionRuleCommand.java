package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class HandoverCollectionRuleCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -7492646205706487882L;
    // columns START
    private Long id;
    /** 集货交接规则名称 */
    private String ruleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** 集货交接规则编码 */
    private String ruleCode;
    /** 集货交接规则类型：仓库，复核台组 */
    private String ruleType;
    /** 规则描述 */
    private String rule;
    /** 规则SQL */
    private String ruleSql;
    /** 仓库组织ID */
    private Long ouId;
    /** 应用类型 */
    private String applyType;
    /** 交接工位ID */
    private Long handoverStationId;
    /** 优先级 */
    private Integer priority;
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
    /** 分拣排序条件列表 */
    private List<HandoverCollectionConditionCommand> handoverCollectionConditionCommandList;
    /** 符合规则的库位ID */
    private List<Long> outboundboxIdList;
    /** 规则测试结果 */
    private Boolean testResult;

    // columns END
    public String getRuleName() {
        return ruleName;
    }

    public Boolean getTestResult() {
        return testResult;
    }

    public void setTestResult(Boolean testResult) {
        this.testResult = testResult;
    }

    public List<Long> getOutboundboxIdList() {
        return outboundboxIdList;
    }

    public void setOutboundboxIdList(List<Long> outboundboxIdList) {
        this.outboundboxIdList = outboundboxIdList;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
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

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public Long getHandoverStationId() {
        return handoverStationId;
    }

    public void setHandoverStationId(Long handoverStationId) {
        this.handoverStationId = handoverStationId;
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

    public List<HandoverCollectionConditionCommand> getHandoverCollectionConditionCommandList() {
        return handoverCollectionConditionCommandList;
    }

    public void setHandoverCollectionConditionCommandList(List<HandoverCollectionConditionCommand> handoverCollectionConditionCommandList) {
        this.handoverCollectionConditionCommandList = handoverCollectionConditionCommandList;
    }

}
