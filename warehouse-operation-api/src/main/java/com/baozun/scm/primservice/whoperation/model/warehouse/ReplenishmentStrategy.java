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
public class ReplenishmentStrategy extends BaseModel {
    private static final long serialVersionUID = 6480997720579163581L;

    // columns START
    /** 补货规则ID */
    private Long replenishmentRuleId;
    /** 分配区域ID */
    private Long areaId;
    /** 分配策略 */
    private String strategyCode;
    /** 分配单位 */
    private String allocateUnitCodes;
    /** 补货策略 */
    private String replenishmentCode;
    /** 优先级 */
    private Integer priority;
    /** 仓库组织ID */
    private Long ouId;

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getReplenishmentRuleId() {
        return replenishmentRuleId;
    }

    public void setReplenishmentRuleId(Long replenishmentRuleId) {
        this.replenishmentRuleId = replenishmentRuleId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getStrategyCode() {
        return strategyCode;
    }

    public void setStrategyCode(String strategyCode) {
        this.strategyCode = strategyCode;
    }

    public String getAllocateUnitCodes() {
        return allocateUnitCodes;
    }

    public void setAllocateUnitCodes(String allocateUnitCodes) {
        this.allocateUnitCodes = allocateUnitCodes;
    }

    public String getReplenishmentCode() {
        return replenishmentCode;
    }

    public void setReplenishmentCode(String replenishmentCode) {
        this.replenishmentCode = replenishmentCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
