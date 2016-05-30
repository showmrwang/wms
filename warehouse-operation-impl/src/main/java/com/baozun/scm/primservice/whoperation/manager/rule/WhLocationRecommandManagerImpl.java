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
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendShelveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.WhLocationRecommendType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RecommendShelveDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.putaway.AttrParams;
import com.baozun.scm.primservice.whoperation.manager.rule.putaway.PutawayCondition;
import com.baozun.scm.primservice.whoperation.manager.rule.putaway.PutawayConditionFactory;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
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
    @Autowired
    private RecommendShelveDao recommendShelveDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private PutawayConditionFactory putawayConditionFactory;
    
    /**
     * @author lichuan
     * @param ruleList
     * @return
     */
    @Override
    public List<LocationCommand> recommendLocationByShevleRule(RuleAfferCommand ruleAffer, List<ShelveRecommendRuleCommand> ruleList, int putawayPatternDetail, String logId) {
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
        boolean isTV = false;// 是否跟踪容器
        boolean isBM = false;// 是否批次管理
        boolean isVM = false;// 是否管理效期
        boolean isMS = false;// 是否允许混放
        List<WhSkuInventoryCommand> invList = null;
        WhFunctionPutAway putawayFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);;
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetail) {
            // 查询所有对应容器号的库存信息
            invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ruleAffer.getOuid(), ruleAffer.getAfferContainerCodeList());
            isTV = true;
        }
        
        for (ShelveRecommendRuleCommand rule : ruleList) {
            Long ruleId = rule.getId();
            List<RecommendShelveCommand> rsList = recommendShelveDao.findCommandByRuleIdOrderByPriority(ruleId, ouId);
            if(null == rsList || 0 == rsList.size()){
                continue;//继续遍历剩下规则
            }
            for (RecommendShelveCommand rs : rsList) {
                RecommendShelveCommand crs = rs;
                if(null == crs) continue;
                //上架库区
                Long whAreaId = crs.getShelveAreaId();
                //库位推荐规则
                String locationRecommendRule = crs.getLocationRule();
                Area area = areaDao.findByIdExt(whAreaId, ouId);
                if (null == area || 1 != area.getLifecycle()) {
                    continue;//库区不存在或不可用，则当前库区不能推荐
                }
                if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetail) {
                    AttrParams attrParams = new AttrParams();
                    attrParams.setIsTrackVessel(isTV);
                    List<Location> avaliableLocs = null;
                    if (WhLocationRecommendType.EMPTY_LOCATION.equals(locationRecommendRule)) {
                        attrParams.setLrt(WhLocationRecommendType.EMPTY_LOCATION);
                        PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                        String cSql = putawayCondition.getCondition(attrParams);
                        avaliableLocs = locationDao.findAllEmptyLocsByAreaId(area.getId(), ouId, cSql);
                    } else if (WhLocationRecommendType.STATIC_LOCATION.equals(locationRecommendRule)) {
                        attrParams.setLrt(WhLocationRecommendType.STATIC_LOCATION);
                        PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                        String cSql = putawayCondition.getCondition(attrParams);
                        avaliableLocs = locationDao.findAllStaticLocsByAreaId(area.getId(), ouId, cSql);
                    } else if (WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS.equals(locationRecommendRule)) {
                        avaliableLocs = null;
                    } else if (WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS.equals(locationRecommendRule)) {
                        avaliableLocs = null;
                    } else if (WhLocationRecommendType.ONE_LOCATION_ONLY.equals(locationRecommendRule)) {
                        attrParams.setLrt(WhLocationRecommendType.ONE_LOCATION_ONLY);
                        PutawayCondition putawayCondition = putawayConditionFactory.getPutawayCondition(WhPutawayPatternType.SYS_GUIDE_PUTAWAY, WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
                        String cSql = putawayCondition.getCondition(attrParams);
                        avaliableLocs = locationDao.findAllEmptyLocsByAreaId(area.getId(), ouId, cSql);
                    } else {
                        avaliableLocs = null;
                    }
                    if (null == avaliableLocs || 0 == avaliableLocs.size()) {
                        continue;// 如果没有可用的库位，则遍历下一个上架规则
                    }
                    for (Location al : avaliableLocs) {
//                        Boolean isTrackVessel = al.getIsTrackVessel();// 是否跟踪容器
//                        Boolean isBatchMgt = al.getIsBatchMgt();// 是否批次管理
//                        Boolean isValidMgt = al.getIsValidMgt();// 是否管理效期
//                        Boolean isMixStacking = al.getIsMixStacking();// 是否允许混放
                        //计算体积和重量
                    }

                }
            }
            
        }
        
       
        
        
        
        
        
        if (log.isInfoEnabled()) {
            log.info("whLocationRecommandManager.recommendLocationByShevleRule end, logId is:[{}]", logId);
        }
        return list;
    }
    
    
}
