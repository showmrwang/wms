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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.WhContainerCategoryType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationRecommendManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;

/**
 * @author lichuan
 *
 */
@Service("pdaPutawayManager")
@Transactional
public class PdaPutawayManagerImpl extends BaseManagerImpl implements PdaPutawayManager {
    protected static final Logger log = LoggerFactory.getLogger(PdaPutawayManagerImpl.class);

    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private WhLocationRecommendManager whLocationRecommendManager;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    /**
     * 系统指导上架扫托盘号
     * 
     * @author lichuan
     * @param containerCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public String sysGuideScanPallet(String containerCode, Long funcId, Long ouId, Long userId, String logId) {
        String locationCode = "";
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanPallet start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!containerCmd.getLifecycle().equals(BaseModel.LIFECYCLE_NORMAL)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Long containerCate = containerCmd.getTwoLevelType();
        Container2ndCategory container2 = container2ndCategoryDao.findByIdExt(containerCate, ouId);
        if (null == container2) {
            log.error("container2ndCategory is null error, 2endCategoryId is:[{}], logId is:[{}]", containerCate, logId);
            throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
        }
        if (1 != container2.getLifecycle()) {
            log.error("container2ndCategory lifecycle is not normal error, containerId is:[{}], logId is:[{}]", container2.getId(), logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
        }
        Long containerCateId = containerCmd.getOneLevelType();
        SysDictionary dic = sysDictionaryDao.findById(containerCateId);
        if (!WhContainerCategoryType.PALLET.equals(dic.getDicValue())) {
            log.error("container2ndCategory is not pallet error!, LogId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_IS_NOT_PALLET_ERROR);
        }
        // 判断该容器是否有符合的上架规则
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(containerCode);
        ruleAffer.setFuncId(funcId);
        List<String> list = new ArrayList<String>();
        list.add(containerCode);
        ruleAffer.setAfferContainerCodeList(list);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        List<LocationCommand> locList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export.getShelveRecommendRuleList(), WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
        if (null == locList || 0 == locList.size()) {
            log.error("location recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
        }
        // 绑定库位

        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanPallet end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], locactionCode is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId, locationCode});
        }
        return locationCode;
    }


}
