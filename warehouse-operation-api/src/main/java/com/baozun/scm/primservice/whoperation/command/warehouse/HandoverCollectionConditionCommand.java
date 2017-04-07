package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class HandoverCollectionConditionCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -2846174415673740672L;
    /** 集货交接规则ID */
    private Long chrId;
    /** 规则条件ID */
    private Long ruleConditionId;
    /** 仓库组织ID */
    private Long ouId;
    /** 规则条件名 */
    private String ruleConditionName;

    // columns END
    public Long getChrId() {
        return chrId;
    }

    public void setChrId(Long chrId) {
        this.chrId = chrId;
    }

    public Long getRuleConditionId() {
        return ruleConditionId;
    }

    public void setRuleConditionId(Long ruleConditionId) {
        this.ruleConditionId = ruleConditionId;
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
