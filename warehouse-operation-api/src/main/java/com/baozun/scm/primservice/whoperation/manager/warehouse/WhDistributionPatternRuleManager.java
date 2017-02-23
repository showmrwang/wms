/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;

public interface WhDistributionPatternRuleManager extends BaseManager {
    
    /**
     * 根据code查找配货模式规则
     *
     * @author qiming.liu
     * @param distribution_pattern_code
     * @param ouId
     * @return
     */
    WhDistributionPatternRuleCommand findRuleByCode(String distributionPatternCode, Long ouId);

    /**
     * 获取仓库组织下的配货模式规则
     *
     * @author mingwei.xie
     * @param ouId
     * @return
     */
    List<WhDistributionPatternRule> findRuleByOuId(Long ouId);


}
