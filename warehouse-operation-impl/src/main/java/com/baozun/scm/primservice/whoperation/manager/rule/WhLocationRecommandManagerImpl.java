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
package com.baozun.scm.primservice.whoperation.manager.rule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;

/**
 * @author lichuan
 *
 */
@Service("whLocationRecommandManager")
@Transactional
public class WhLocationRecommandManagerImpl extends BaseManagerImpl implements WhLocationRecommendManager {
    protected static final Logger log = LoggerFactory.getLogger(WhLocationRecommandManagerImpl.class);
    
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhFunctionPutAwayManager whFunctionPutAwayManager;
    
    /**
     * @author lichuan
     * @param ruleList
     * @return
     */
    @Override
    public List<LocationCommand> recommendLocationByShevleRule(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, String logId) {
        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule start, logId is:[{}]", logId);
        }
        List<LocationCommand> list = null;
        if(null == ruleList || 0 == ruleList.size()){
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        Long funcId = ruleAffer.getFuncId();
        Long ouId = ruleAffer.getOuid();
        List<WhSkuInventoryCommand> invList = null;
        WhFunctionPutAway putawayFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);;
        if (putawayFunc.getIsEntireTrayPutaway()) {
            // 查询所有对应容器号的库存信息
            invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(ruleAffer.getOuid(), ruleAffer.getAfferContainerCodeList());
        }
        
        
        
        
        
        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule end, logId is:[{}]", logId);
        }
        return list;
    }
    
    
}
