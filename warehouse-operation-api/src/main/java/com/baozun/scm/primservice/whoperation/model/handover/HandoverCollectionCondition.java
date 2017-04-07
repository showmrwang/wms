package com.baozun.scm.primservice.whoperation.model.handover;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class HandoverCollectionCondition extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -5994406190196947477L;

    /** 集货交接规则ID */
    private Long chrId;
    /** 规则条件ID */
    private Long ruleConditionId;
    /** 仓库组织ID */
    private Long ouId;

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

}
