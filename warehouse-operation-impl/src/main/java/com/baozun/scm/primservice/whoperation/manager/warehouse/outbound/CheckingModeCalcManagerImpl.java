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
package com.baozun.scm.primservice.whoperation.manager.warehouse.outbound;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationExecLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingMode;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.DistributionMode;
import com.baozun.scm.primservice.whoperation.constant.PickingMode;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

/**
 * @author lichuan
 *
 */
@Service("checkingModeCalcManager")
@Transactional
public class CheckingModeCalcManagerImpl extends BaseManagerImpl implements CheckingModeCalcManager {
    protected static final Logger log = LoggerFactory.getLogger(CheckingModeCalcManagerImpl.class);

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhCheckingDao whCheckingDao;
    @Autowired
    private WhCheckingLineDao WhCheckingLineDao;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;

    /**
     * 复核台集货完成生产待复核数据
     * 
     * @author lichuan
     * @param workCmd
     * @param execLineCommandList
     * @param ouId
     * @param logId
     */
    @Override
    public void generateCheckingDataByCollection(WhWorkCommand workCmd, List<WhOperationExecLineCommand> execLineCommandList, Long ouId, String logId) {
        if (null == workCmd) {
            log.error("work is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.WORK_NO_EXIST);
        }
        if (log.isInfoEnabled()) {
            log.info("generateCheckingData by checkingCollection start, workCode is:[{}], logId is:[{}]", workCmd.getCode(), logId);
        }
        // 计算复核模式(一期首单复核和副品复核归到按单复核流程)
        String distributionMode = workCmd.getDistributionMode();
        String pickingMode = workCmd.getPickingMode();
        String checkingMode = workCmd.getCheckingMode();
        if (DistributionMode.DISTRIBUTION_SECKILL.equals(distributionMode)) {
            // 秒杀配货模式可以选择按单复核或首单复核
            if (CheckingMode.CHECK_FIRST_ODO.equals(checkingMode)) {
                checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
            } else {
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        } else if (DistributionMode.DISTRIBUTION_TWOSKUSUIT.equals(distributionMode)) {
            // 主副品配货模式可以选择副品复核和按单复核
            if (CheckingMode.CHECK_ACCESSORY.equals(checkingMode)) {
                checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
            } else {
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        } else if (DistributionMode.DISTRIBUTION_SUITS.equals(distributionMode)) {
            // 套装配货模式可以选择按单复核或首单复核
            if (CheckingMode.CHECK_FIRST_ODO.equals(checkingMode)) {
                checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
            } else {
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        } else {
            if (PickingMode.PICKING_SINGLE.equals(pickingMode)) {
                // 按批摘果（单品单件）拣货模式只能选择按单复核
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            } else if (PickingMode.PICKING.equals(pickingMode)) {
                // 摘果拣货模式可以选择按单复核或者按箱复核
                if (!(CheckingMode.CHECK_BY_ODO.equals(checkingMode) || CheckingMode.CHECK_BY_CONTAINER.equals(checkingMode))) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            } else if (PickingMode.PICKING_SEED.equals(pickingMode)) {
                // 播种拣货模式可以选择按单复核或者按箱复核
                if (!(CheckingMode.CHECK_BY_ODO.equals(checkingMode) || CheckingMode.CHECK_BY_CONTAINER.equals(checkingMode))) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        }
        // 拣货作业执行明细分组
        List<WhOperationExecLineCommand> operationExecLineGroup = new ArrayList<WhOperationExecLineCommand>();
        if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
            for (WhOperationExecLineCommand cmd : execLineCommandList) {
                String line = ParamsUtil.concatParam((null == cmd.getUseOuterContainerId() ? "" : cmd.getUseOuterContainerId().toString()), (null == cmd.getUseContainerLatticeNo() ? "" : cmd.getUseContainerLatticeNo().toString()),
                        (null == cmd.getUseContainerId() ? "" : cmd.getUseContainerId().toString()), cmd.getUseOutboundboxCode());
                boolean isExists = isExistsOperationExecLineGroup(operationExecLineGroup, line);
                if (false == isExists) {
                    operationExecLineGroup.add(cmd);
                }
            }
        }
        String batch = workCmd.getBatch();// 小批次
        String waveCode = workCmd.getWaveCode();
        if (null != operationExecLineGroup && !operationExecLineGroup.isEmpty()) {
            // 每组数据分别生成待复核数据
            for (WhOperationExecLineCommand groupLine : operationExecLineGroup) {
                Long outerContainerId = groupLine.getUseOuterContainerId();
                Integer containerLatticeNo = groupLine.getUseContainerLatticeNo();
                Long insideContainerId = groupLine.getUseContainerId();
                String outboundBoxCode = groupLine.getUseOutboundboxCode();
                Long outboundBoxId = groupLine.getOutboundBoxId();
                // 根据条件查询所有库存数据
                WhSkuInventory params = new WhSkuInventory();
                params.setOuterContainerId(outerContainerId);
                params.setContainerLatticeNo(containerLatticeNo);
                params.setInsideContainerId(insideContainerId);
                params.setOutboundboxCode(outboundBoxCode);
                List<WhSkuInventory> invList = whSkuInventoryDao.getSkuInvListGroupUuid(params);
                if (null != invList && invList.size() > 0) {
                    // 生成复核头
                    WhChecking checking = new WhChecking();
                    checking.setBatch(batch);
                    checking.setCheckingMode(checkingMode);
                    checking.setContainerId(insideContainerId);
                    checking.setContainerLatticeNo(containerLatticeNo);
                    checking.setCustomerCode("");
                    checking.setCustomerName("");
                    checking.setDistributionMode(distributionMode);
                    checking.setFacilityId(null);
                    checking.setOuId(ouId);
                    checking.setOutboundboxCode(outboundBoxCode);
                    checking.setOutboundboxId(outboundBoxId);
                    checking.setOuterContainerId(outerContainerId);
                    checking.setPickingMode(pickingMode);
                    checking.setProductCode("");
                    checking.setProductName("");
                    checking.setStatus(CheckingStatus.NEW);
                    checking.setStoreCode("");
                    checking.setStoreName("");
                    checking.setTimeEffectCode("");
                    checking.setTimeEffectName("");
                    checking.setTransportCode("");
                    checking.setTransportName("");
                    checking.setWaveCode(waveCode);
                    whCheckingDao.insert(checking);
                    // 生成复核明细
                    for (WhSkuInventory inv : invList) {
                        if (null != inv) {
                            WhCheckingLine line = new WhCheckingLine();
                            line.setBatchNumber(batch);
                            line.setCheckingId(checking.getId());
                            line.setCheckingQty(0L);
                            line.setCountryOfOrigin(inv.getCountryOfOrigin());
                            line.setCustomerCode("");
                            line.setCustomerName("");
                            line.setExpDate(inv.getExpDate());
                            line.setInvAttr1(inv.getInvAttr1());
                            line.setInvAttr2(inv.getInvAttr2());
                            line.setInvAttr3(inv.getInvAttr3());
                            line.setInvAttr4(inv.getInvAttr4());
                            line.setInvAttr5(inv.getInvAttr5());
                            line.setInvStatus(null == inv.getInvStatus() ? "" : inv.getInvStatus().toString());
                            line.setInvType(inv.getInvType());
                            line.setMfgDate(inv.getMfgDate());
                            if (StringUtils.isEmpty(inv.getOccupationCode())) {
                                log.error("odo code is null error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
                            }
                            WhOdo odo = whOdoDao.findOdoByCodeAndOuId(inv.getOccupationCode(), ouId);
                            if (null == odo) {
                                log.error("odo is null error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
                            }
                            line.setOdoId(odo.getId());
                            line.setOdoLineId(inv.getOccupationLineId());
                            line.setOuId(ouId);
                            line.setQty(null == inv.getOnHandQty() ? 0L : inv.getOnHandQty().longValue());
                            SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(inv.getSkuId(), ouId, logId);
                            if (null == cacheSku) {
                                log.error("sku is not found error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                            }
                            Sku sku = cacheSku.getSku();
                            if (null == sku) {
                                log.error("sku is not found error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                            }
                            line.setSkuBarCode(sku.getBarCode());
                            line.setSkuCode(sku.getCode());
                            line.setSkuExtCode(sku.getExtCode());
                            line.setSkuName(sku.getName());
                            line.setStoreCode("");
                            line.setStoreName("");
                            line.setUuid(inv.getUuid());
                            WhCheckingLineDao.insert(line);
                        }
                    }
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("generateCheckingData by checkingCollection end, workCode is:[{}], logId is:[{}]", workCmd.getCode(), logId);
        }
    }

    private boolean isExistsOperationExecLineGroup(List<WhOperationExecLineCommand> operationExecLineGroup, String execLine) {
        boolean isExists = false;
        if (null != operationExecLineGroup && !operationExecLineGroup.isEmpty()) {
            for (WhOperationExecLineCommand cmd : operationExecLineGroup) {
                String line = ParamsUtil.concatParam((null == cmd.getUseOuterContainerId() ? "" : cmd.getUseOuterContainerId().toString()), (null == cmd.getUseContainerLatticeNo() ? "" : cmd.getUseContainerLatticeNo().toString()),
                        (null == cmd.getUseContainerId() ? "" : cmd.getUseContainerId().toString()), cmd.getUseOutboundboxCode());
                if (line.equals(execLine)) {
                    isExists = true;
                    break;
                }
            }
        }
        return isExists;
    }

    /**
     * 播种完成生产待复核数据
     * 
     * @author lichuan
     * @param facilityId
     * @param batchNo
     * @param facilitySeedingSkuInventoryList
     * @param userId
     * @param ouId
     * @param logId
     */
    @Override
    public void generateCheckingDataBySeeding(Long facilityId, String batchNo, List<WhSeedingCollectionLineCommand> seedingLineList, Long userId, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("generateCheckingData by seedingCollection start, batchNo is:[{}], logId is:[{}]", batchNo, logId);
        }
        // 计算复核模式(一期首单复核和副品复核归到按单复核流程)
        WhSeedingCollection scParams = new WhSeedingCollection();
        scParams.setBatch(batchNo);
        scParams.setOuId(ouId);
        List<WhSeedingCollection> scList = whSeedingCollectionDao.findListByParam(scParams);
        String distributionMode = "";
        String pickingMode = "";
        String checkingMode = "";
        if (null != scList && !scList.isEmpty()) {
            for (WhSeedingCollection sc : scList) {
                if (null != sc) {
                    // 取到三个模式
                    distributionMode = sc.getDistributionMode();
                    pickingMode = sc.getPickingMode();
                    checkingMode = sc.getCheckingMode();
                    break;
                }
            }
        }
        if (DistributionMode.DISTRIBUTION_SECKILL.equals(distributionMode)) {
            // 秒杀配货模式可以选择按单复核或首单复核
            if (CheckingMode.CHECK_FIRST_ODO.equals(checkingMode)) {
                checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
            } else {
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        } else if (DistributionMode.DISTRIBUTION_TWOSKUSUIT.equals(distributionMode)) {
            // 主副品配货模式可以选择副品复核和按单复核
            if (CheckingMode.CHECK_ACCESSORY.equals(checkingMode)) {
                checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
            } else {
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        } else if (DistributionMode.DISTRIBUTION_SUITS.equals(distributionMode)) {
            // 套装配货模式可以选择按单复核或首单复核
            if (CheckingMode.CHECK_FIRST_ODO.equals(checkingMode)) {
                checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
            } else {
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        } else {
            if (PickingMode.PICKING_SINGLE.equals(pickingMode)) {
                // 按批摘果（单品单件）拣货模式只能选择按单复核
                if (!CheckingMode.CHECK_BY_ODO.equals(checkingMode)) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            } else if (PickingMode.PICKING.equals(pickingMode)) {
                // 摘果拣货模式可以选择按单复核或者按箱复核
                if (!(CheckingMode.CHECK_BY_ODO.equals(checkingMode) || CheckingMode.CHECK_BY_CONTAINER.equals(checkingMode))) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            } else if (PickingMode.PICKING_SEED.equals(pickingMode)) {
                // 播种拣货模式可以选择按单复核或者按箱复核
                if (!(CheckingMode.CHECK_BY_ODO.equals(checkingMode) || CheckingMode.CHECK_BY_CONTAINER.equals(checkingMode))) {
                    checkingMode = CheckingMode.CHECK_BY_ODO;// 复核模式转换为按单复核
                }
            }
        }
        // 播种结果明细分组
        List<WhSeedingCollectionLineCommand> seedingSkuInventoryListGroup = new ArrayList<WhSeedingCollectionLineCommand>();
        if (null != seedingLineList && !seedingLineList.isEmpty()) {
            for (WhSeedingCollectionLineCommand seedingInv : seedingLineList) {
                String line = ParamsUtil.concatParam((null == seedingInv.getFacilityCode() ? "" : seedingInv.getFacilityCode()), seedingInv.getLatticeNo() + "", seedingInv.getOutboundBoxCode());
                boolean isExists = isExistsSeedingSkuInventoryLineGroup(seedingSkuInventoryListGroup, line);
                if (false == isExists) {
                    seedingSkuInventoryListGroup.add(seedingInv);
                }
            }
        }
        String batch = "";// 小批次
        String waveCode = "";
        if (null != seedingSkuInventoryListGroup && !seedingSkuInventoryListGroup.isEmpty()) {
            // 每组数据分别生成待复核数据
            for (WhSeedingCollectionLineCommand groupLine : seedingSkuInventoryListGroup) {
                String facilityCode = groupLine.getFacilityCode();
                Integer latticeNo = groupLine.getLatticeNo();
                String outboundBoxCode = groupLine.getOutboundBoxCode();
                // Long outboundBoxId = groupLine.getOutboundBoxId();
                // 根据条件查询所有库存数据
                WhSkuInventory params = new WhSkuInventory();
                params.setSeedingWallCode(facilityCode);
                params.setContainerLatticeNo(latticeNo);
                params.setOutboundboxCode(outboundBoxCode);
                List<WhSkuInventory> invList = whSkuInventoryDao.getSkuInvListGroupUuid(params);
                if (null != invList && invList.size() > 0) {
                    // 生成复核头
                    WhChecking checking = new WhChecking();
                    checking.setBatch(batch);
                    checking.setCheckingMode(checkingMode);
                    checking.setContainerId(null);
                    checking.setContainerLatticeNo(latticeNo);
                    checking.setCustomerCode("");
                    checking.setCustomerName("");
                    checking.setDistributionMode(distributionMode);
                    checking.setFacilityId(null);
                    checking.setOuId(ouId);
                    checking.setOutboundboxCode(outboundBoxCode);
                    // checking.setOutboundboxId(outboundBoxId);
                    checking.setOuterContainerId(null);
                    checking.setPickingMode(pickingMode);
                    checking.setProductCode("");
                    checking.setProductName("");
                    checking.setStatus(CheckingStatus.NEW);
                    checking.setStoreCode("");
                    checking.setStoreName("");
                    checking.setTimeEffectCode("");
                    checking.setTimeEffectName("");
                    checking.setTransportCode("");
                    checking.setTransportName("");
                    checking.setWaveCode(waveCode);
                    whCheckingDao.insert(checking);
                    // 生成复核明细
                    for (WhSkuInventory inv : invList) {
                        if (null != inv) {
                            WhCheckingLine line = new WhCheckingLine();
                            line.setBatchNumber(batch);
                            line.setCheckingId(checking.getId());
                            line.setCheckingQty(0L);
                            line.setCountryOfOrigin(inv.getCountryOfOrigin());
                            line.setCustomerCode("");
                            line.setCustomerName("");
                            line.setExpDate(inv.getExpDate());
                            line.setInvAttr1(inv.getInvAttr1());
                            line.setInvAttr2(inv.getInvAttr2());
                            line.setInvAttr3(inv.getInvAttr3());
                            line.setInvAttr4(inv.getInvAttr4());
                            line.setInvAttr5(inv.getInvAttr5());
                            line.setInvStatus(null == inv.getInvStatus() ? "" : inv.getInvStatus().toString());
                            line.setInvType(inv.getInvType());
                            line.setMfgDate(inv.getMfgDate());
                            WhOdo odo = whOdoDao.findOdoByCodeAndOuId(inv.getOccupationCode(), ouId);
                            if (null == odo) {
                                log.error("odo is null error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
                            }
                            line.setOdoId(odo.getId());
                            line.setOdoLineId(inv.getOccupationLineId());
                            line.setOuId(ouId);
                            line.setQty(null == inv.getOnHandQty() ? 0L : inv.getOnHandQty().longValue());
                            SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(inv.getSkuId(), ouId, logId);
                            if (null == cacheSku) {
                                log.error("sku is not found error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                            }
                            Sku sku = cacheSku.getSku();
                            if (null == sku) {
                                log.error("sku is not found error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                            }
                            line.setSkuBarCode(sku.getBarCode());
                            line.setSkuCode(sku.getCode());
                            line.setSkuExtCode(sku.getExtCode());
                            line.setSkuName(sku.getName());
                            line.setStoreCode("");
                            line.setStoreName("");
                            line.setUuid(inv.getUuid());
                            WhCheckingLineDao.insert(line);
                        }
                    }
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("generateCheckingData by seedingCollection end, batchNo is:[{}], logId is:[{}]", batchNo, logId);
        }
    }

    private boolean isExistsSeedingSkuInventoryLineGroup(List<WhSeedingCollectionLineCommand> seedingSkuInventoryListGroup, String execLine) {
        boolean isExists = false;
        if (null != seedingSkuInventoryListGroup && !seedingSkuInventoryListGroup.isEmpty()) {
            for (WhSeedingCollectionLineCommand cmd : seedingSkuInventoryListGroup) {
                String line = ParamsUtil.concatParam((null == cmd.getFacilityCode() ? "" : cmd.getFacilityCode()), cmd.getLatticeNo() + "", cmd.getOutboundBoxCode());
                if (line.equals(execLine)) {
                    isExists = true;
                    break;
                }
            }
        }
        return isExists;
    }


}
