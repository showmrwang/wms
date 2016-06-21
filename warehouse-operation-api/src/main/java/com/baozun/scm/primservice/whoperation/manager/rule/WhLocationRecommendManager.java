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
package com.baozun.scm.primservice.whoperation.manager.rule;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;

/**
 * @author lichuan
 *
 */
public interface WhLocationRecommendManager extends BaseManager {

    List<LocationCommand> recommendLocationByShevleRule(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, int putawayPatternDetail, String logId);

    List<LocationRecommendResultCommand> recommendLocationByShevleRule(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, int putawayPatternDetailType, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList,
            Map<String, Map<String, Double>> uomMap, String logId);

}
