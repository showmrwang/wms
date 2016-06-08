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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.WhContainerCategoryType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationRecommendManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

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
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private WhCartonDao whCartonDao;

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
    public LocationCommand sysGuideScanPallet(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId) {
        LocationCommand locCmd = new LocationCommand();
        String locationCode = "";
        String asnCode = "";
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
        List<String> cclist = new ArrayList<String>();
        cclist.add(containerCode);
        List<WhSkuInventoryCommand> invList = null;
        // 查询所有对应容器号的库存信息
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, cclist);
        if (null == invList) {
            log.error("container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        List<LocationCommand> locExistsList = whSkuInventoryDao.findWhSkuInventoryLocByOuterContainerCode(ouId, cclist);
        boolean isLoc = false;
        if (null != locExistsList && 0 < locExistsList.size()) {
            for (LocationCommand lc : locExistsList) {
                if (!StringUtils.isEmpty(lc.getCode()) && false == isLoc) {
                    locationCode = lc.getCode();
                    isLoc = true;
                }
                asnCode = lc.getOccupationCode();
                if (StringUtils.isEmpty(asnCode)) {
                    log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                }
                WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                if (null == asn) {
                    log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                }
                if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus()) {
                    log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                }
                Long poId = asn.getPoId();
                WhPo po = whPoDao.findWhPoById(poId, ouId);
                if (null == po) {
                    log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.PO_NULL);
                }
                String poCode = po.getPoCode();
                if (PoAsnStatus.PO_RCVD != po.getStatus()) {
                    log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                }
            }
        }
      
        if (!StringUtils.isEmpty(locationCode)) {
            // 已经推荐过库位
            locCmd.setCode(locationCode);
            return locCmd;
        }
        // 判断该容器是否有符合的上架规则
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(containerCode);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(cclist);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 推荐库位
        List<LocationCommand> locList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export.getShelveRecommendRuleList(), WhPutawayPatternDetailType.PALLET_PUTAWAY, logId);
        if (null == locList || 0 == locList.size()) {
            log.error("location recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
        }
        // 取到库位
        LocationCommand loc = locList.get(0);
        locationCode = loc.getCode();
        Long locationId = loc.getId();
        // 绑定库位
        for (WhSkuInventoryCommand invCmd : invList) {
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            if (null != inv.getLocationId()) {
                throw new BusinessException(ErrorCodes.CONTAINER_RCVD_INV_HAS_LOCATION_ERROR);
            }
            inv.setToBeFilledQty(inv.getOnHandQty());// 待移入
            inv.setOnHandQty(null);
            inv.setLocationId(locationId);
            whSkuInventoryDao.saveOrUpdateByVersion(inv);
        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuideScanPallet end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], locactionCode is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId, locationCode});
        }
        locCmd.setCode(locationCode);
        return locCmd;
    }

    /**
     * @author lichuan
     * @param containerCode
     * @param locationCode
     * @param funcId
     * @param asnId
     * @param putawayPatternDetailType
     * @param caseMode
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void sysGuidePutaway(String containerCode, String locationCode, Long funcId, Integer putawayPatternDetailType, Integer caseMode, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuidePutaway start, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
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
        List<String> cclist = new ArrayList<String>();
        cclist.add(containerCode);
        List<WhSkuInventoryCommand> invList = null;
        // 查询所有对应容器号的库存信息
        invList = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(ouId, cclist);
        if (null == invList) {
            log.error("container:[{}] rcvd inventory not found error!, logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        List<LocationCommand> locExistsList = whSkuInventoryDao.findWhSkuInventoryLocByOuterContainerCode(ouId, cclist);
        if (null != locExistsList && 0 < locExistsList.size()) {
            for (LocationCommand lc : locExistsList) {
                String locCode = lc.getCode();
                String asnCode = lc.getOccupationCode();
                if (StringUtils.isEmpty(locCode)) {
                    log.error("location not recommend fail! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_NOT_RECOMMEND_ERROR);
                }
                if (StringUtils.isEmpty(asnCode)) {
                    log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                }
                WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                if (null == asn) {
                    log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                }
                if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus()) {
                    log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                }
                Long poId = asn.getPoId();
                WhPo po = whPoDao.findWhPoById(poId, ouId);
                if (null == po) {
                    log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.PO_NULL);
                }
                String poCode = po.getPoCode();
                if (PoAsnStatus.PO_RCVD != po.getStatus()) {
                    log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                }
            }
        }
      
        if (StringUtils.isEmpty(locationCode)) {

        }
        // 执行上架
        for (WhSkuInventoryCommand invCmd : invList) {
            WhSkuInventory inv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, inv);
            if (null == inv.getLocationId()) {
                throw new BusinessException(ErrorCodes.RCVD_INV_NOT_HAS_LOCATION_ERROR);
            }
            inv.setOccupationCode(null);
            inv.setOnHandQty(inv.getToBeFilledQty());// 在库
            inv.setToBeFilledQty(null);
            whSkuInventoryDao.saveOrUpdateByVersion(inv);
            // 生成库存日志

        }
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.sysGuidePutaway end, containerCode is:[{}], funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[] {containerCode, funcId, ouId, userId, logId});
        }
    }

    /**
     * @author lichuan
     * @param containerode
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public int findCaselevelCartonNumsByOuterContainerCode(String containerCode, Long ouId, String logId) { 
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findCaselevelCartonNumsByOuterContainerCode start, containerCode is:[{}], ouId is:[{}], logId is:[{}]", containerCode, ouId, logId);
        }
        int nums = whCartonDao.findCartonNumsByOuterContainerCode(containerCode, ouId);
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findCaselevelCartonNumsByOuterContainerCode end, containerCode is:[{}], ouId is:[{}], nums is:[{}], logId is:[{}]", containerCode, ouId, nums, logId);
        }
        return nums;
    }

    
    /**
     * @author lichuan
     * @param containerode
     * @param ouId
     * @param logId
     * @return
     */
    @Override
    public int findNotCaselevelCartonNumsByOuterContainerCode(String containerCode, Long ouId, String logId) { 
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findNotCaselevelCartonNumsByOuterContainerCode start, containerCode is:[{}], ouId is:[{}], logId is:[{}]", containerCode, ouId, logId);
        }
        int nums = whCartonDao.findNoneCartonNumsByOuterContainerCode(containerCode, ouId);
        if (log.isInfoEnabled()) {
            log.info("pdaPutawayManager.findNotCaselevelCartonNumsByOuterContainerCode end, containerCode is:[{}], ouId is:[{}], nums is:[{}], logId is:[{}]", containerCode, ouId, nums, logId);
        }
        return nums;
    }

}
