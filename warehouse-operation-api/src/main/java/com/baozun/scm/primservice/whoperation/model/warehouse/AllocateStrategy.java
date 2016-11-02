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
public class AllocateStrategy extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = 2038651877979289885L;
    // columns START
    /** 分配规则ID */
    private Long allocateRuleId;
    /** 分配区域ID */
    private Long areaId;
    /** 分配策略id */
    private String strategyCode;
    /** 分配单位ids */
    private String allocateUnitCodes;
    /** 优先级 */
    private Integer priority;
    /** 仓库组织ID */
    private Long ouId;

    // columns END

    public Long getAllocateRuleId() {
        return allocateRuleId;
    }

    public void setAllocateRuleId(Long allocateRuleId) {
        this.allocateRuleId = allocateRuleId;
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


}
