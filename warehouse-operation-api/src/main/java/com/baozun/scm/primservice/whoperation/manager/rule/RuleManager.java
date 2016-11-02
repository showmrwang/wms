package com.baozun.scm.primservice.whoperation.manager.rule;

import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface RuleManager extends BaseManager {

    RuleExportCommand ruleExport(RuleAfferCommand ruleAffer);

    RuleExportCommand ruleExportContainerCode(RuleAfferCommand ruleAffer);

    /**
     * 根据出库单ID获取排序后的出库单明细的拆分条件
     */
    public RuleExportCommand ruleExportOutboundBoxSplitRequire(RuleAfferCommand ruleAffer);
}
