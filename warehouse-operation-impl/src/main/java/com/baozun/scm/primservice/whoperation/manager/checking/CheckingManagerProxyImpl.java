/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.checking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhLocationSkuVolumeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.OutboundboxStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.OdoManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ContainerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionOutBoundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhLocationSkuVolumeManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOdoPackageInfoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxLineSnManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhPrintInfoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventorySnManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.WarehouseMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundConsumable;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("checkingManagerProxy")
public class CheckingManagerProxyImpl extends BaseManagerImpl implements CheckingManagerProxy {

    public static final Logger log = LoggerFactory.getLogger(CheckingManagerProxyImpl.class);

    @Autowired
    private CheckingManager checkingManager;
    @Autowired
    private WhFunctionOutBoundManager whFunctionOutBoundManager;
    @Autowired
    private WhPrintInfoManager whPrintInfoManager;
    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;
    @Autowired
    private WhCheckingManager whCheckingManager;
    @Autowired
    private WhCheckingLineManager whCheckingLineManager;
    @Autowired
    private WhOdoPackageInfoManager whOdoPackageInfoManager;
    @Autowired
    private WhOutboundboxManager whOutboundboxManager;
    @Autowired
    private WhOutboundboxLineManager whOutboundboxLineManager;
    @Autowired
    private WhOutboundboxLineSnManager whOutboundboxLineSnManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhSkuInventorySnManager whSkuInventorySnManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private WhSkuManager whSkuManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private OdoManagerProxy odoManagerProxy;
    @Autowired
    private WhLocationSkuVolumeManager whLocationSkuVolumeManager;
    @Autowired
    private ContainerManager containerManager;
    @Autowired
    private WarehouseManager warehouseManager;


    /** =============================================================== */


    /**
     * 根据ID查找出库设施
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    @Override
    public WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId) {
        return checkingManager.findOutboundFacilityById(id, ouId);
    }

    /**
     * 获取复核箱内所有商品的SN/残次信息
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @return
     */
    public List<WhSkuInventorySnCommand> findCheckingSkuInvSnByCheckingId(Long checkingId, Long ouId) {
        return checkingManager.findCheckingSkuInvSnByCheckingId(checkingId, ouId);
    }

    /**
     * 根据复核头获取所有复核明细
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @param logId
     * @return
     */
    public List<WhCheckingLineCommand> getCheckingLineListByChecking(Long checkingId, Long ouId, String logId) {
        List<WhCheckingLineCommand> whCheckingLineCommandList = whCheckingLineManager.getCheckingLineByCheckingId(checkingId, ouId);

        return whCheckingLineCommandList;
    }

    /**
     * 从缓存获取客户信息
     *
     * @author mingwei.xie
     * @param customerId
     * @return
     */
    public Customer findCustomerByRedis(Long customerId) {
        Map<Long, Customer> customerMap = this.findCustomerByRedis(Collections.singletonList(customerId));
        return customerMap.get(customerId);
    }

    /**
     * 从缓存获取店铺信息
     *
     * @author mingwei.xie
     * @param storeId
     * @return
     */
    public Store findStoreByRedis(Long storeId) {
        Map<Long, Store> storeMap = this.findStoreByRedis(Collections.singletonList(storeId));
        return storeMap.get(storeId);
    }



    /**
     * 释放播种墙
     *
     * @author mingwei.xie
     * @param checkingCommand
     * @param userId
     * @param ouId
     * @return
     */
    private WhOutboundFacility releaseSeedingFacility(WhCheckingCommand checkingCommand, Long userId, Long ouId) {
        WhOutboundFacilityCommand seedingFacility = this.findOutboundFacilityById(checkingCommand.getFacilityId(), ouId);
        WhOutboundFacility whOutboundFacility = new WhOutboundFacility();
        BeanUtils.copyProperties(seedingFacility, whOutboundFacility);

        whOutboundFacility.setOperatorId(userId);
        // TODO 待验证状态常量
        whOutboundFacility.setStatus(BaseModel.LIFECYCLE_NORMAL.toString());
        whOutboundFacility.setBatch(null);
        return whOutboundFacility;
    }

    /**
     * 按箱复核完成，保存数据
     *
     * @author mingwei.xie
     * @param function
     * @param outboundFacilityId
     * @param checkingCommand
     * @param checkingSourceCode
     * @param checkingType
     * @param userId
     * @param ouId
     * @param logId
     */
    public void finishedCheckingByContainer(WhFunctionOutBound function, Long outboundFacilityId, WhCheckingCommand checkingCommand, String checkingSourceCode, String checkingType, Boolean isWeighting, Long userId, Long ouId, String logId) {
        // 复核台信息
        WhOutboundFacilityCommand facilityCommand = checkingManager.findOutboundFacilityById(outboundFacilityId, ouId);
        // 仓库配置信息
        WarehouseMgmt warehouseMgmt = warehouseManager.findWhMgmtByOuId(ouId);
        // 复核头信息
        WhCheckingCommand orgChecking = checkingManager.findCheckingById(checkingCommand.getId(), ouId);

        if (null == orgChecking) {
            throw new BusinessException(ErrorCodes.CHECKING_CHECKING_INFO_NULL_ERROR);
        }
        if (CheckingStatus.NEW != orgChecking.getStatus() && CheckingStatus.PART_FINISH != orgChecking.getStatus()) {
            throw new BusinessException(ErrorCodes.CHECKING_CHECKING_STATUS_ERROR);
        }

        // 原始复核明细集合 <lineId, line>原始的复核明细集合，方便取数据
        Map<Long, WhCheckingLineCommand> orgCheckingLineMap = new HashMap<>();
        // 原始复核明细对应的uuid集合 <lineId, List<uuid>>用于查询整个复核箱中的所有库存
        Map<Long, Set<String>> checkingLineUuidMap = new HashMap<>();
        // 原始复核箱中的明细集合
        List<WhCheckingLineCommand> orgCheckingLineList = this.getOrgCheckingLineList(orgChecking, orgCheckingLineMap, checkingLineUuidMap, ouId, logId);
        // 原始明细uuid对应的库存集合 <lineId_uuid,List<WhSkuInventory>>方便根据明细lineId和uuid获取原始库存
        Map<String, List<WhSkuInventory>> uuidLineSkuInvListMap = this.getLineIdUuidSkuInvListMap(checkingLineUuidMap, ouId);
        // 原始复核数据中SN/残次信息集合
        Map<Long, WhSkuInventorySnCommand> orgSnInvMap = this.getOrgCheckingWhSkuInventorySnCommandMap(orgChecking, ouId);


        // 已复核的SN
        List<WhSkuInventorySnCommand> checkedSnInvList = new ArrayList<>();
        // 出库箱装箱明细
        List<WhOutboundboxLine> outboundboxLineList = new ArrayList<>();
        // 出库单在出库箱中的库存
        List<WhSkuInventory> outboundboxSkuInvList = new ArrayList<>();
        // 出库单在原始箱中的库存
        Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet = new HashSet<>();
        // 需要更新已复核数量的复核明细集合
        Set<WhCheckingLineCommand> toUpdateCheckingLineSet = new HashSet<>();
        // 包裹计重
        BigDecimal packageCalcWeight = new BigDecimal(0.00);

        // 复核使用的出库箱
        WhOutboundboxCommand checkedBox = checkingCommand.getOutboundbox();
        // 完成复核的明细集合
        List<WhCheckingLineCommand> checkedLineList = checkedBox.getCheckingLineList();
        for (WhCheckingLineCommand checkedLine : checkedLineList) {
            Long checkingLineId = checkedLine.getId();
            // 已复核数量
            Long checkingQty = checkedLine.getCheckingQty();
            // 已复核SN/残次信息列表
            List<Long> snInvIdList = checkedLine.getSnInventoryIdList();


            // 原始复核明细
            WhCheckingLineCommand orgCheckingLine = orgCheckingLineMap.get(checkingLineId);
            // 原始明细lineId_uuid对应的库存集合
            List<WhSkuInventory> odoOrgSkuInvList = uuidLineSkuInvListMap.get(orgCheckingLine.getOdoLineId() + "_" + orgCheckingLine.getUuid());


            // 更新明细复核数量
            this.updateOrgCheckingLine(userId, toUpdateCheckingLineSet, checkingQty, orgCheckingLine);
            // 扣减原始复核明细对应的库存数量
            this.updateOrgCheckingSkuInv(toUpdateOdoOrgSkuInvSet, odoOrgSkuInvList, checkingQty, logId);

            // 新出库箱中的明细库存
            WhSkuInventory odoSkuInv = this.createWhSkuInventory(odoOrgSkuInvList.get(0), checkedBox.getOutboundboxCode(), checkingQty, ouId, logId);
            outboundboxSkuInvList.add(odoSkuInv);

            // 创建出库箱明细
            WhOutboundboxLine whOutboundboxLine = this.createWhOutboundboxLine(orgCheckingLine, checkingQty, odoSkuInv);
            outboundboxLineList.add(whOutboundboxLine);

            // 记录已复核的SN信息
            this.updateOrgCheckingSkuInvSn(orgSnInvMap, checkedSnInvList, snInvIdList, odoSkuInv);

            // 累计包裹重量，计算包裹计重
            SkuRedisCommand skuRedis = skuRedisManager.findSkuMasterBySkuId(odoSkuInv.getSkuId(), ouId, logId);
            Sku sku = skuRedis.getSku();
            packageCalcWeight = packageCalcWeight.add(new BigDecimal(sku.getWeight()).multiply(new BigDecimal(checkingQty)));

        }

        // 创建出库箱信息
        WhOutboundbox whOutboundbox = this.createWhOutboundbox(orgChecking, checkedBox.getOutboundboxCode(), orgCheckingLineList.get(0).getOdoId(), isWeighting, userId, ouId, logId);

        // 装箱包裹计重信息
        WhOdoPackageInfoCommand odoPackageInfo = this.createOdoPackageInfo(function, whOutboundbox, packageCalcWeight, orgCheckingLineList.get(0).getOdoId(), userId, ouId);

        // 设置复核头状态
        this.updateChecking(orgChecking, orgCheckingLineList, userId);

        // 出库单信息
        WhOdo whOdo = odoManagerProxy.findOdOById(orgCheckingLineList.get(0).getOdoId(), ouId);

        // 保存耗材库存
        WhOutboundConsumable whOutboundConsumable = null;
        WhSkuInventoryCommand consumableSkuInv = null;
        if (warehouseMgmt.getIsMgmtConsumableSku()) {
            whOutboundConsumable = this.createOutboundConsumable(facilityCommand, checkedBox.getOutboundboxCode(), orgChecking, checkedBox, whOdo, userId, ouId, logId);
            consumableSkuInv = checkingManager.getConsumableSkuInventory(checkedBox, ouId, logId);
            if (null == consumableSkuInv) {
                throw new BusinessException(ErrorCodes.CHECKING_OCCUPATION_CONSUMABLE_SKUINV_NULL_ERROR);
            }
        }


        // 更新出库单
        whOdo = this.updateOdo(orgChecking.getId(), orgCheckingLineList, whOdo, userId, ouId, logId);
        // 释放小车
        Container trolleyContainer = null;
        // 释放播种墙
        WhOutboundFacility seedingFacility = null;
        // 释放周转箱
        Container turnoverBoxContainer = null;

        boolean isBoxCheckingFinished = this.checkBoxCheckingFinished(orgCheckingLineList);
        if (isBoxCheckingFinished) {
            // 完成箱复核,判断小车、播种墙是否完成复核
            switch (checkingType) {
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_BOX:
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_GRID:
                    // 按箱复核类型 小车
                    boolean isTrolleyCheckingFinished = this.checkTrolleyCheckingFinished(orgChecking.getId(), checkingSourceCode, ouId, logId);
                    if (isTrolleyCheckingFinished) {
                        // 释放小车
                        trolleyContainer = releaseTrolleyContainer(orgChecking, userId, ouId);
                    }
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_BOX:
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_GRID:
                    // 按箱复核类型 播种墙
                    boolean isFacilityCheckingFinished = this.checkFacilityCheckingFinished(orgChecking.getId(), checkingSourceCode, ouId, logId);
                    if (isFacilityCheckingFinished) {
                        // 释放播种墙
                        seedingFacility = this.releaseSeedingFacility(orgChecking, userId, ouId);
                    }
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_OUTBOUND_BOX:
                    // 按箱复核类型 出库箱
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TURNOVER_BOX:
                    // 按箱复核类型 周转箱
                    // 释放周转箱
                    turnoverBoxContainer = releaseTurnoverBox(orgChecking, userId, ouId);
                    break;
                default:
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SOURCE_TYPE_ERROR);
            }
        }



        WhCheckingResultCommand whCheckingResultCommand = new WhCheckingResultCommand();
        whCheckingResultCommand.setFunctionId(function.getFunctionId());
        whCheckingResultCommand.setOuId(ouId);
        whCheckingResultCommand.setUserId(userId);
        if (null != trolleyContainer) {
            whCheckingResultCommand.setContainer(trolleyContainer);
        } else if (null != turnoverBoxContainer) {
            whCheckingResultCommand.setContainer(turnoverBoxContainer);
        }
        if (null != seedingFacility) {
            whCheckingResultCommand.setSeedingFacility(seedingFacility);
        }
        // 更新复核头
        whCheckingResultCommand.setOrgCheckingCommand(orgChecking);
        // 更新复核明细
        whCheckingResultCommand.setToUpdateCheckingLineSet(toUpdateCheckingLineSet);
        // 更新复核箱的库存
        whCheckingResultCommand.setToUpdateOdoOrgSkuInvSet(toUpdateOdoOrgSkuInvSet);
        // 创建出库箱
        whCheckingResultCommand.setWhOutboundbox(whOutboundbox);
        // 创建出库箱明细
        whCheckingResultCommand.setOutboundboxLineList(outboundboxLineList);
        // 创建出库箱库存
        whCheckingResultCommand.setOutboundboxSkuInvList(outboundboxSkuInvList);
        // 创建包裹计重
        whCheckingResultCommand.setOdoPackageInfoCommand(odoPackageInfo);
        // 创建耗材信息
        whCheckingResultCommand.setWhOutboundConsumable(whOutboundConsumable);
        // 待删除的耗材库存
        whCheckingResultCommand.setConsumableSkuInv(consumableSkuInv);
        // 更新出库单
        whCheckingResultCommand.setWhOdo(whOdo);
        // 更新SN/残次信息
        whCheckingResultCommand.setCheckedSnInvList(checkedSnInvList);

        // 保存复核数据
        checkingManager.finishedChecking(whCheckingResultCommand, warehouseMgmt.getIsTabbInvTotal(), userId, ouId, logId);
    }

    /**
     * 释放复核的周转箱
     *
     * @author mingwei.xie
     * @param checkingCommand
     * @param userId
     * @param ouId
     * @return
     */
    private Container releaseTurnoverBox(WhCheckingCommand checkingCommand, Long userId, Long ouId) {
        Container container = containerManager.getContainerById(checkingCommand.getContainerId(), ouId);
        container.setOperatorId(userId);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
        container.setIsFull(false);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        return container;
    }

    /**
     * 释放复核的小车
     *
     * @author mingwei.xie
     * @param orgChecking
     * @param userId
     * @param ouId
     * @return
     */
    private Container releaseTrolleyContainer(WhCheckingCommand orgChecking, Long userId, Long ouId) {
        Container container = containerManager.getContainerById(orgChecking.getOuterContainerId(), ouId);
        container.setOperatorId(userId);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
        container.setIsFull(false);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        return container;
    }


    /**
     * 检查播种墙释放复核完成
     *
     * @author mingwei.xie
     * @param checkingId
     * @param checkingSourceCode
     * @param ouId
     * @param logId
     * @return
     */
    public boolean checkFacilityCheckingFinished(Long checkingId, String checkingSourceCode, Long ouId, String logId) {
        List<WhCheckingCommand> facilityCheckingCommandList = checkingManager.findCheckingBySeedingFacility(checkingSourceCode, ouId);
        boolean isFacilityCheckingFinished = true;
        for (WhCheckingCommand whCheckingCommand : facilityCheckingCommandList) {
            if (whCheckingCommand.getId().equals(checkingId)) {
                continue;
            }
            if (CheckingStatus.FINISH != whCheckingCommand.getStatus()) {
                isFacilityCheckingFinished = false;
                break;
            }
        }
        return isFacilityCheckingFinished;
    }

    /**
     * 检查小车释放复核完成
     *
     * @author mingwei.xie
     * @param checkingId
     * @param checkingSourceCode
     * @param ouId
     * @param logId
     * @return
     */
    public boolean checkTrolleyCheckingFinished(Long checkingId, String checkingSourceCode, Long ouId, String logId) {
        List<WhCheckingCommand> trolleyCheckingCommandList = checkingManager.findCheckingByTrolley(checkingSourceCode, ouId);
        boolean isTrolleyCheckingFinished = true;
        for (WhCheckingCommand whCheckingCommand : trolleyCheckingCommandList) {
            if (whCheckingCommand.getId().equals(checkingId)) {
                continue;
            }
            if (CheckingStatus.FINISH != whCheckingCommand.getStatus()) {
                isTrolleyCheckingFinished = false;
                break;
            }
        }
        return isTrolleyCheckingFinished;
    }

    /**
     * 检查复核的箱子是否完成复核
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @param logId
     * @return
     */
    public boolean checkBoxCheckingFinished(Long checkingId, Long ouId, String logId) {
        List<WhCheckingLineCommand> checkingLineList = this.getCheckingLineListByChecking(checkingId, ouId, logId);
        boolean isCheckingFinished = true;
        for (WhCheckingLineCommand checkingLine : checkingLineList) {
            if (!checkingLine.getQty().equals(checkingLine.getCheckingQty())) {
                isCheckingFinished = false;
                break;
            }
        }
        return isCheckingFinished;
    }

    /**
     * 检查出库单释放完成复核
     *
     * @author mingwei.xie
     * @param checkingId 正在复核的出库单
     * @param odoId
     * @param ouId
     * @param logId
     * @return
     */
    private boolean checkOdoCheckingFinished(Long checkingId, Long odoId, Long ouId, String logId) {
        List<WhCheckingCommand> odoCheckingCommandList = checkingManager.findCheckingByOdo(odoId, ouId);
        boolean isCheckingFinished = true;
        for (WhCheckingCommand whCheckingCommand : odoCheckingCommandList) {
            if (whCheckingCommand.getId().equals(checkingId)) {
                continue;
            }
            if (CheckingStatus.FINISH != whCheckingCommand.getStatus()) {
                isCheckingFinished = false;
                break;
            }
        }
        return isCheckingFinished;
    }


    /** ============================================================= */


    private WhOdo updateOdo(Long checkingId, List<WhCheckingLineCommand> orgCheckingLineList, WhOdo whOdo, Long userId, Long ouId, String logId) {
        boolean isBoxCheckingFinished = this.checkBoxCheckingFinished(orgCheckingLineList);
        if (isBoxCheckingFinished) {
            boolean isOdoCheckingFinished = this.checkOdoCheckingFinished(checkingId, whOdo.getId(), ouId, logId);
            if (isOdoCheckingFinished) {
                whOdo.setOdoStatus(OdoStatus.CHECKING_FINISH);
                whOdo.setModifiedId(userId);
            } else {
                whOdo = null;
            }
        } else {
            whOdo = null;
        }
        return whOdo;
    }

    private boolean checkBoxCheckingFinished(List<WhCheckingLineCommand> orgCheckingLineList) {
        boolean isBoxCheckingFinished = true;
        for (WhCheckingLineCommand whCheckingLine : orgCheckingLineList) {
            if (!whCheckingLine.getQty().equals(whCheckingLine.getCheckingQty())) {
                isBoxCheckingFinished = false;
                break;
            }
        }
        return isBoxCheckingFinished;
    }


    private void updateOrgCheckingLine(Long userId, Set<WhCheckingLineCommand> toUpdateCheckingLineSet, Long checkingQty, WhCheckingLineCommand orgCheckingLine) {
        if (null == orgCheckingLine.getCheckingQty()) {
            orgCheckingLine.setCheckingQty(checkingQty);
        } else {
            orgCheckingLine.setCheckingQty(orgCheckingLine.getCheckingQty() + checkingQty);
        }
        orgCheckingLine.setModifiedId(userId);
        toUpdateCheckingLineSet.add(orgCheckingLine);
    }

    private void updateOrgCheckingSkuInvSn(Map<Long, WhSkuInventorySnCommand> orgSnInvMap, List<WhSkuInventorySnCommand> checkedSnInvList, List<Long> snInvIdList, WhSkuInventory odoSkuInv) {
        if (null != snInvIdList && !snInvIdList.isEmpty()) {
            for (Long snInvId : snInvIdList) {
                WhSkuInventorySnCommand checkedSnInv = orgSnInvMap.get(snInvId);
                if (null == checkedSnInv) {
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SN_ERROR);
                }
                // 更新SN/残次信息的uuid
                checkedSnInv.setUuid(odoSkuInv.getUuid());
                // 记录需要更新uuid的SN/残次信息
                checkedSnInvList.add(checkedSnInv);
            }
        }
    }

    private void updateOrgCheckingSkuInv(Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet, List<WhSkuInventory> odoOrgSkuInvList, Long checkingQty, String logId) {
        Long odoFinishedLineCheckingQty = checkingQty;
        // while (odoFinishedLineCheckingQty > 0) {
        for (WhSkuInventory odoOrgSkuInv : odoOrgSkuInvList) {
            if (odoOrgSkuInv.getOnHandQty() <= 0) {
                log.warn("skuInventory onHandQty is zero, skuInventory is:[{}], logId is:[{}]", odoOrgSkuInv, logId);
                continue;
            }
            if (odoOrgSkuInv.getOnHandQty() >= odoFinishedLineCheckingQty) {
                odoOrgSkuInv.setOnHandQty(odoOrgSkuInv.getOnHandQty() - odoFinishedLineCheckingQty);
                odoFinishedLineCheckingQty = 0L;
            } else {
                odoFinishedLineCheckingQty = odoFinishedLineCheckingQty - odoOrgSkuInv.getOnHandQty().longValue();
                odoOrgSkuInv.setOnHandQty(0d);
            }
            // 记录需要更新的原始库存
            toUpdateOdoOrgSkuInvSet.add(odoOrgSkuInv);

            if (0 == odoFinishedLineCheckingQty) {
                break;
            }
        }
        if (odoFinishedLineCheckingQty > 0) {
            throw new BusinessException(ErrorCodes.CHECKING_SKUINV_INSUFFICIENT_ERROR);
        }
        // }
    }

    private WhOutboundConsumable createOutboundConsumable(WhOutboundFacilityCommand facilityCommand, String outboundBoxCode, WhCheckingCommand orgChecking, WhOutboundboxCommand checkedBox, WhOdo whOdo, Long userId, Long ouId, String logId) {

        WhOutboundConsumable whOutboundConsumable = new WhOutboundConsumable();

        // 管理耗材才会记录这些信息
        String consumableCode = checkedBox.getConsumableCode();
        Long consumableSkuId = checkedBox.getConsumableSkuId();
        String locationCode = checkedBox.getConsumableLocationCode();

        WhLocationSkuVolumeCommand locationSkuVolume = whLocationSkuVolumeManager.findFacilityLocSkuVolumeByLocSku(facilityCommand.getId(), locationCode, consumableSkuId, ouId);
        if (null == locationSkuVolume) {
            throw new BusinessException(ErrorCodes.CHECKING_CONSUMABLE_SKUINVLOC_ERROR);
        }

        // 累计包裹重量，计算包裹计重
        SkuRedisCommand skuRedis = skuRedisManager.findSkuMasterBySkuId(consumableSkuId, ouId, logId);
        Sku sku = skuRedis.getSku();

        whOutboundConsumable.setBatch(orgChecking.getBatch());
        whOutboundConsumable.setWaveCode(orgChecking.getWaveCode());
        whOutboundConsumable.setCustomerCode(orgChecking.getCustomerCode());
        whOutboundConsumable.setCustomerName(orgChecking.getCustomerName());
        whOutboundConsumable.setStoreCode(orgChecking.getStoreCode());
        whOutboundConsumable.setStoreName(orgChecking.getStoreName());
        whOutboundConsumable.setOdoId(whOdo.getId());
        whOutboundConsumable.setOdoCode(whOdo.getOdoCode());
        // TODO 不知道设置
        whOutboundConsumable.setTransportCode("");
        whOutboundConsumable.setWaybillCode("");
        whOutboundConsumable.setFacilityId(facilityCommand.getId());
        whOutboundConsumable.setFacilityCode(facilityCommand.getFacilityCode());
        whOutboundConsumable.setLocationId(locationSkuVolume.getLocationId());
        whOutboundConsumable.setLocationCode(locationSkuVolume.getLocationCode());
        whOutboundConsumable.setAreaId(locationSkuVolume.getWorkAreaId());
        whOutboundConsumable.setAreaCode(locationSkuVolume.getWorkAreaCode());
        whOutboundConsumable.setQty(1d);
        whOutboundConsumable.setOuId(ouId);
        // TODO 保存出库箱后返回的主键
        // whOutboundConsumable.setOutboundboxId();
        whOutboundConsumable.setOutboundboxCode(outboundBoxCode);
        whOutboundConsumable.setSkuCode(sku.getCode());
        whOutboundConsumable.setSkuBarcode(sku.getBarCode());
        whOutboundConsumable.setSkuName(sku.getName());
        whOutboundConsumable.setSkuLength(sku.getLength());
        whOutboundConsumable.setSkuWidth(sku.getWidth());
        whOutboundConsumable.setSkuHeight(sku.getHeight());
        whOutboundConsumable.setSkuVolume(sku.getVolume());
        whOutboundConsumable.setSkuWeight(sku.getWeight());
        whOutboundConsumable.setCreateId(userId);
        whOutboundConsumable.setCreateTime(new Date());
        whOutboundConsumable.setModifiedId(userId);
        whOutboundConsumable.setLastModifyTime(new Date());

        return whOutboundConsumable;

    }

    private void updateChecking(WhCheckingCommand orgChecking, List<WhCheckingLineCommand> orgCheckingLineList, Long userId) {
        // 校验明细复核数据
        boolean isFinishedChecking = true;
        for (WhCheckingLineCommand orgCheckingLine : orgCheckingLineList) {
            if (!orgCheckingLine.getQty().equals(orgCheckingLine.getCheckingQty())) {
                isFinishedChecking = false;
                break;
            }
        }
        if (isFinishedChecking) {
            orgChecking.setStatus(CheckingStatus.FINISH);
        } else {
            orgChecking.setStatus(CheckingStatus.PART_FINISH);
        }
        orgChecking.setModifiedId(userId);
    }

    private WhOdoPackageInfoCommand createOdoPackageInfo(WhFunctionOutBound function, WhOutboundbox outboundbox, BigDecimal packageCalcWeight, Long odoId, Long userId, Long ouId) {

        WhOdoPackageInfoCommand whOdoPackageInfoCommand = new WhOdoPackageInfoCommand();
        whOdoPackageInfoCommand.setOdoId(odoId);
        // TODO 创建出库箱后再设置
        whOdoPackageInfoCommand.setOutboundboxId(outboundbox.getId());
        whOdoPackageInfoCommand.setOutboundboxCode(outboundbox.getOutboundboxCode());
        whOdoPackageInfoCommand.setStatus(1);
        whOdoPackageInfoCommand.setCalcWeight(packageCalcWeight.longValue());
        whOdoPackageInfoCommand.setFloats(function.getWeightFloatPercentage());
        whOdoPackageInfoCommand.setActualWeight(null);
        whOdoPackageInfoCommand.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        whOdoPackageInfoCommand.setCreateId(userId);
        whOdoPackageInfoCommand.setCreateTime(new Date());
        whOdoPackageInfoCommand.setLastModifyTime(new Date());
        whOdoPackageInfoCommand.setModifiedId(userId);
        whOdoPackageInfoCommand.setOuId(ouId);
        return whOdoPackageInfoCommand;

    }

    private Map<Long, WhSkuInventorySnCommand> getOrgCheckingWhSkuInventorySnCommandMap(WhCheckingCommand whCheckingCommand, Long ouId) {
        // <snInvId, snInv>
        Map<Long, WhSkuInventorySnCommand> orgSnInvMap = null;
        // 复核数据中SN/残次信息
        List<WhSkuInventorySnCommand> orgSnInvList = this.findCheckingSkuInvSnByCheckingId(whCheckingCommand.getId(), ouId);
        if (null != orgSnInvList && !orgSnInvList.isEmpty()) {
            orgSnInvMap = new HashMap<>();
            for (WhSkuInventorySnCommand orgSkuInv : orgSnInvList) {
                orgSnInvMap.put(orgSkuInv.getId(), orgSkuInv);
            }
        }
        return orgSnInvMap;
    }

    private Map<String, List<WhSkuInventory>> getLineIdUuidSkuInvListMap(Map<Long, Set<String>> checkingLineUuidMap, Long ouId) {
        Map<String, List<WhSkuInventory>> uuidLineSkuInvListMap = new HashMap<>();
        for (Long lineId : checkingLineUuidMap.keySet()) {
            Set<String> lineUuidSet = checkingLineUuidMap.get(lineId);
            for (String uuid : lineUuidSet) {
                // 出库单复核明细的原始库存
                List<WhSkuInventory> odoOrgSkuInvList = checkingManager.findCheckingOdoSkuInvByOdoLineIdUuid(lineId, ouId, uuid);
                if (null == odoOrgSkuInvList || odoOrgSkuInvList.isEmpty()) {
                    throw new BusinessException(ErrorCodes.CHECKING_TURNOVERBOX_ORG_SKUINV_ERROR);
                }

                uuidLineSkuInvListMap.put(lineId + "_" + uuid, odoOrgSkuInvList);
            }
        }
        return uuidLineSkuInvListMap;
    }

    private List<WhCheckingLineCommand> getOrgCheckingLineList(WhCheckingCommand checking, Map<Long, WhCheckingLineCommand> orgCheckingLineMap, Map<Long, Set<String>> checkingLineUuidMap, Long ouId, String logId) {
        // 整箱的复核明细信息集合
        List<WhCheckingLineCommand> orgCheckingLineList = this.getCheckingLineListByChecking(checking.getId(), ouId, logId);
        for (WhCheckingLineCommand orgCheckingLine : orgCheckingLineList) {
            orgCheckingLineMap.put(orgCheckingLine.getId(), orgCheckingLine);

            Set<String> checkingLineUuidSet = checkingLineUuidMap.get(orgCheckingLine.getId());
            if (null == checkingLineUuidSet) {
                checkingLineUuidSet = new HashSet<>();
                checkingLineUuidMap.put(orgCheckingLine.getOdoLineId(), checkingLineUuidSet);
            }
            checkingLineUuidSet.add(orgCheckingLine.getUuid());
        }
        return orgCheckingLineList;
    }

    private WhOutboundbox createWhOutboundbox(WhCheckingCommand whChecking, String outboundBoxCode, Long odoId, Boolean isWeighting, Long userId, Long ouId, String logId) {
        WhOutboundbox whOutboundbox = new WhOutboundbox();
        whOutboundbox.setBatch(whChecking.getBatch());
        whOutboundbox.setWaveCode(whChecking.getWaveCode());
        whOutboundbox.setCustomerCode(whChecking.getCustomerCode());
        whOutboundbox.setCustomerName(whChecking.getCustomerName());
        whOutboundbox.setStoreCode(whChecking.getStoreCode());
        whOutboundbox.setStoreName(whChecking.getStoreName());
        whOutboundbox.setOuId(whChecking.getOuId());
        whOutboundbox.setOdoId(odoId);
        whOutboundbox.setOutboundboxCode(outboundBoxCode);
        whOutboundbox.setDistributionMode(whChecking.getDistributionMode());
        whOutboundbox.setPickingMode(whChecking.getPickingMode());
        whOutboundbox.setCheckingMode(whChecking.getCheckingMode());

        if (isWeighting) {
            whOutboundbox.setStatus(OutboundboxStatus.CHECKING);
        } else {
            whOutboundbox.setStatus(OutboundboxStatus.WEIGHING);
        }

        return whOutboundbox;
    }

    private WhOutboundboxLine createWhOutboundboxLine(WhCheckingLineCommand whCheckingLine, Long checkingQty, WhSkuInventory odoSkuInv) {
        WhOutboundboxLine whOutboundboxLine = new WhOutboundboxLine();
        // whOutboundboxLine.setWhOutboundboxId(whOutboundbox.getId());
        whOutboundboxLine.setSkuCode(whCheckingLine.getSkuCode());
        whOutboundboxLine.setSkuExtCode(whCheckingLine.getSkuExtCode());
        whOutboundboxLine.setSkuBarCode(whCheckingLine.getSkuBarCode());
        whOutboundboxLine.setSkuName(whCheckingLine.getSkuName());
        whOutboundboxLine.setQty(checkingQty.doubleValue());
        whOutboundboxLine.setCustomerCode(whCheckingLine.getCustomerCode());
        whOutboundboxLine.setCustomerName(whCheckingLine.getCustomerName());
        whOutboundboxLine.setStoreName(whCheckingLine.getStoreName());
        whOutboundboxLine.setStoreCode(whCheckingLine.getStoreCode());
        whOutboundboxLine.setInvStatus(whCheckingLine.getInvStatus());
        whOutboundboxLine.setInvType(whCheckingLine.getInvType());
        whOutboundboxLine.setBatchNumber(whCheckingLine.getBatchNumber());
        whOutboundboxLine.setMfgDate(whCheckingLine.getMfgDate());
        whOutboundboxLine.setExpDate(whCheckingLine.getExpDate());
        whOutboundboxLine.setCountryOfOrigin(whCheckingLine.getCountryOfOrigin());
        whOutboundboxLine.setInvAttr1(whCheckingLine.getInvAttr1());
        whOutboundboxLine.setInvAttr2(whCheckingLine.getInvAttr2());
        whOutboundboxLine.setInvAttr3(whCheckingLine.getInvAttr3());
        whOutboundboxLine.setInvAttr4(whCheckingLine.getInvAttr4());
        whOutboundboxLine.setInvAttr5(whCheckingLine.getInvAttr5());
        whOutboundboxLine.setUuid(odoSkuInv.getUuid());
        whOutboundboxLine.setOuId(whCheckingLine.getOuId());
        whOutboundboxLine.setOdoId(whCheckingLine.getOdoId());
        whOutboundboxLine.setOdoLineId(whCheckingLine.getOdoLineId());

        return whOutboundboxLine;
    }


    private WhSkuInventory createWhSkuInventory(WhSkuInventory checkingLine, String outboundBoxCode, Long checkingQty, Long ouId, String logId) {
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setSkuId(checkingLine.getSkuId());
        skuInventory.setCustomerId(checkingLine.getCustomerId());
        skuInventory.setStoreId(checkingLine.getStoreId());
        // 占用编码是内部编码
        skuInventory.setOccupationCode(checkingLine.getOccupationCode());
        skuInventory.setOccupationLineId(checkingLine.getOccupationLineId());
        skuInventory.setSeedingWallCode(null);
        skuInventory.setContainerLatticeNo(null);
        skuInventory.setOutboundboxCode(outboundBoxCode);
        skuInventory.setOnHandQty(checkingQty.doubleValue());
        skuInventory.setAllocatedQty(0d);
        skuInventory.setToBeFilledQty(0d);
        skuInventory.setFrozenQty(0d);
        skuInventory.setInvStatus(checkingLine.getInvStatus());
        skuInventory.setInvType(checkingLine.getInvType());
        skuInventory.setBatchNumber(checkingLine.getBatchNumber());
        skuInventory.setMfgDate(checkingLine.getMfgDate());
        skuInventory.setExpDate(checkingLine.getExpDate());
        skuInventory.setCountryOfOrigin(checkingLine.getCountryOfOrigin());
        skuInventory.setInvAttr1(checkingLine.getInvAttr1());
        skuInventory.setInvAttr2(checkingLine.getInvAttr2());
        skuInventory.setInvAttr3(checkingLine.getInvAttr3());
        skuInventory.setInvAttr4(checkingLine.getInvAttr4());
        skuInventory.setInvAttr5(checkingLine.getInvAttr5());

        String uuid = null;
        try {
            uuid = SkuInventoryUuid.invUuid(skuInventory);
        } catch (Exception e) {
            log.error("checking createWhSkuInventory error, throw NoSuchAlgorithmException, skuInventory is:[{}], logId is:[{}]", skuInventory, logId);
            throw new BusinessException(ErrorCodes.CHECKING_BOX_SKUINV_CREATE_UUID_ERROR);
        }
        skuInventory.setUuid(uuid);
        skuInventory.setIsLocked(false);
        skuInventory.setOuId(checkingLine.getOuId());
        skuInventory.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO);
        skuInventory.setLastModifyTime(new Date());

        return skuInventory;
    }
}
