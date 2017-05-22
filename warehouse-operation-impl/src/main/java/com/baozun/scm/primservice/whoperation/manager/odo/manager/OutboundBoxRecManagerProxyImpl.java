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

package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WhWaveCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.Container2ndCategoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutboundBoxRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.OutBoundBoxTypeManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhDistributionPatternRuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutInventoryboxRelationship;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;
import com.baozun.scm.primservice.whoperation.util.StringUtil;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;

@Service("outboundBoxRecManagerProxy")
public class OutboundBoxRecManagerProxyImpl extends BaseManagerImpl implements OutboundBoxRecManagerProxy {
    public static final Logger log = LoggerFactory.getLogger(OutboundBoxRecManagerProxyImpl.class);

    @Autowired
    private WhWaveManager whWaveManager;

    @Autowired
    private WhWaveLineManager whWaveLineManager;

    @Autowired
    private OdoManager odoManager;

    @Autowired
    private OdoLineManager odoLineManager;

    @Autowired
    private WhDistributionPatternRuleManager whDistributionPatternRuleManager;

    @Autowired
    private RuleManager ruleManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SkuRedisManager skuRedisManager;

    @Autowired
    private OutBoundBoxTypeManager outboundBoxTypeManager;

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private WhSkuInventoryManager skuInventoryManager;

    @Autowired
    private WarehouseManager warehouseManager;

    @Autowired
    private PkManager pkManager;

    @Autowired
    private OutboundBoxRecManager outboundBoxRecManager;

    @Autowired
    private OdoOutBoundBoxMapper odoOutBoundBoxMapper;

    public void recommendOutboundBox(Long ouId, String logId) {

        // 创建出库箱阶段的波次列表
        List<WhWaveCommand> whWaveCommandList = whWaveManager.getWhWaveByPhaseCode(Constants.CREATE_OUTBOUND_CARTON, ouId);

        // 根据波次查询对应的出库单
        for (WhWaveCommand whWaveCommand : whWaveCommandList) {
            // 根据波次获得出库单ID集合
            List<Long> whOdoIdList = whWaveLineManager.getOdoIdListByWaveId(whWaveCommand.getId(), ouId);

            if (whOdoIdList.isEmpty()) {
                // 设置波次的波次阶段为下一个阶段
                whWaveManager.changeWavePhaseCode(whWaveCommand.getId(), ouId);
                continue;
            }
            // 波次下出库单列表
            List<OdoCommand> whOdoCommandList = odoManager.getWhOdoListById(whOdoIdList, ouId);

            // 配货模式规则, <distributionPatternCode, rule>
            Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);

            // 摘果模式由于要匹配规则，只使用ID即可
            List<Long> singleOdoPickModeOdoIdList = new ArrayList<>();
            // 非摘果模式的出库单直接推荐周转箱,按照拣货模式分组<pickingMode, List<OdoCommand>>
            Map<String, List<OdoCommand>> batchOdoPickModeOdoListMap = new HashMap<>();
            // 方便根据出库单ID获取出库单<odoId, odoCommand>
            Map<Long, OdoCommand> odoCommandMap = new HashMap<>();

            // 将出库单和波次绑定，并且将出库单按照拣货模式分组，分成摘果出库单和非摘果出库单
            for (OdoCommand odoCommand : whOdoCommandList) {
                odoCommand.setWhWaveCommand(whWaveCommand);
                odoCommandMap.put(odoCommand.getId(), odoCommand);

                WhDistributionPatternRule rule = distributionPatternRuleMap.get(odoCommand.getDistributeMode());
                if (null == rule) {
                    // 踢出波次
                    Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                    whWaveManager.deleteWaveLinesAndReleaseInventoryByOdoId(whWaveCommand.getId(), odoCommand.getId(), Constants.CREATE_OUTBOUND_CARTON_DISTRIBUTE_MODE_ERROR, warehouse);
                    continue;
                }
                if (Constants.PICKING_MODE_PICKING.equals(rule.getPickingMode().toString())) {
                    singleOdoPickModeOdoIdList.add(odoCommand.getId());
                } else {
                    List<OdoCommand> batchOdoPickModeOdoList = batchOdoPickModeOdoListMap.get(rule.getPickingMode().toString());
                    if (null == batchOdoPickModeOdoList) {
                        batchOdoPickModeOdoList = new ArrayList<>();
                        batchOdoPickModeOdoListMap.put(rule.getPickingMode().toString(), batchOdoPickModeOdoList);
                    }
                    batchOdoPickModeOdoList.add(odoCommand);
                }
            }

            // 将波次明细绑定到出库单，将所有波次明细按照出库单分组，绑定到出库单
            List<WhWaveLineCommand> whWaveLineCommandList = whWaveLineManager.findWaveLineCommandListByWaveId(whWaveCommand.getId(), ouId);
            for (WhWaveLineCommand whWaveLineCommand : whWaveLineCommandList) {
                OdoCommand odoCommand = odoCommandMap.get(whWaveLineCommand.getOdoId());
                if (null == odoCommand) {
                    // 波次错误
                    log.warn("波次明细对应的出库单为空");
                    continue;
                }
                // <odoLineId, waveLine>
                Map<Long, WhWaveLineCommand> waveLineMap = odoCommand.getOdoLineIdwaveLineMap();
                if (null == waveLineMap) {
                    waveLineMap = new HashMap<>();
                    odoCommand.setOdoLineIdwaveLineMap(waveLineMap);
                }
                waveLineMap.put(whWaveLineCommand.getOdoLineId(), whWaveLineCommand);
            }

            // 处理摘果出库单
            if (!singleOdoPickModeOdoIdList.isEmpty()) {
                this.packingForSingleOdoPickMode(singleOdoPickModeOdoIdList, odoCommandMap, distributionPatternRuleMap, ouId, logId);
            }

            // 处理非摘果的出库单，考虑分成两个线程
            if (!batchOdoPickModeOdoListMap.isEmpty()) {
                this.packingForBatchOdoPickMode(batchOdoPickModeOdoListMap, odoCommandMap, distributionPatternRuleMap, ouId, logId);
            }

            // 判断是否有出库单未推荐出库箱，但是还在波次内
            List<Long> afterRecOdoIdList = whWaveLineManager.getOdoIdListByWaveId(whWaveCommand.getId(), ouId);
            List<Long> odoOutboundBoxOdoIdList = odoOutBoundBoxMapper.getWaveOdoIdList(whWaveCommand.getId(), ouId);
            if(!afterRecOdoIdList.isEmpty() && !odoOutboundBoxOdoIdList.containsAll(afterRecOdoIdList)){
                throw new BusinessException("波次内遗留出库单未处理");
            }


            // 设置波次的波次阶段为下一个阶段
            whWaveManager.changeWavePhaseCode(whWaveCommand.getId(), ouId);

            // 波次取消需要取消补货任务
            whWaveManager.checkReplenishmentTaskForWave(whWaveCommand.getId(), ouId);
        }

    }

    /**
     * 摘果出库单的装箱
     *
     * @param singleOdoPickModeOdoIdList 波次下摘果出库单ID集合
     * @param odoCommandMap 波次下出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     */
    private void packingForSingleOdoPickMode(List<Long> singleOdoPickModeOdoIdList, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        if (null == singleOdoPickModeOdoIdList || singleOdoPickModeOdoIdList.isEmpty()) {
            log.info("singleOdoPickModeOdoIdList is null or empty");
            return;
        }
        // <distributionPatternCode, List<OdoCommand>>未装箱的摘果出库单集合，根据配货模式分组，待分成小批次推荐小车或者周转箱
        Map<String, List<OdoCommand>> singleOdoPickModeUnpackedOdoMap = new HashMap<>();
        // <distributionPatternCode, List<odoCommand>>已装箱的摘果出库单按照配货模式分组，待分成小批次装入小车
        Map<String, List<OdoCommand>> singleOdoPickModePackedOdoMap = new HashMap<>();

        // 出库单匹配的规则<odoId, rule>
        Map<Long, OutboundBoxRuleCommand> singlePickOdoMatchRuleMap = this.getOdoOutboundBoxRuleMap(singleOdoPickModeOdoIdList, ouId);
        // 遍历每个odo订单，使用匹配的规则将订单明细odoLine进行分组
        for (Long odoId : singleOdoPickModeOdoIdList) {

            // 待装箱的摘果出库单
            OdoCommand singlePickOdo = odoCommandMap.get(odoId);

            OutboundBoxRuleCommand ruleCommand = singlePickOdoMatchRuleMap.get(odoId);
            if (null == ruleCommand) {
                // 未匹配装箱规则，按照配货模式分组分成小批次，待推荐小车或者周转箱
                this.odoListGroupByDistributeMode(singlePickOdo, singleOdoPickModeUnpackedOdoMap);
                continue;
            }

            // 出库单分配的出库箱列表
            List<OutInvBoxTypeCommand> odoPackedOutboundBoxList = new ArrayList<>();
            // 出库单分配的整箱容器列表
            List<ContainerCommand> odoPackedWholeCaseList = new ArrayList<>();
            // 出库单分配的整托容器列表
            List<ContainerCommand> odoPackedWholeTrayList = new ArrayList<>();

            // 获得出库单明细分组后的id列表，出库单应用规则后，将明细分组
            List<String> odoLineIdGroupList = this.getOdoLineIdGroupList(odoId, ruleCommand);
            if (null == odoLineIdGroupList || odoLineIdGroupList.isEmpty()) {
                // 踢出波次
                Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                whWaveManager.deleteWaveLinesAndReleaseInventoryByOdoId(singlePickOdo.getWhWaveCommand().getId(), odoId, Constants.CREATE_OUTBOUND_CARTON_SPLIT_REQUIRE_ERROR, warehouse);
                continue;
            }

            // 存在未匹配出库箱的明细,整单匹配小车
            boolean unmatchedBoxOdoLineExist = false;
            try {
                // 按照规则分好组之后
                for (String odoLineIdGroupStr : odoLineIdGroupList) {
                    // 获取排序后的出库单明细列表
                    List<OdoLineCommand> sortedOdoLineList = this.getSortedOdoLineList(odoLineIdGroupStr, ouId);
                    if (null == sortedOdoLineList || sortedOdoLineList.isEmpty()) {
                        throw new BusinessException("明细分组后的出库单列表为空");
                    }

                    // 去除整托整箱的明细，暂时只考虑波次明细，即出库单明细整托整箱的情况，多个明细占用整托整箱的计算以后考虑
                    this.packingWholeCaseLine(sortedOdoLineList, singlePickOdo, odoPackedWholeCaseList, odoPackedWholeTrayList, ouId);

                    // 无法装入出库单头出库箱的明细列表
                    List<OdoLineCommand> unmatchedOdoBoxList = this.packingForOdoBox(sortedOdoLineList, odoPackedOutboundBoxList, singlePickOdo, ouId, logId);

                    // 无法装入出库单明细出库箱的明细列表
                    List<OdoLineCommand> unmatchedOdoLineBoxList = this.packingForOdoLineBox(unmatchedOdoBoxList, odoPackedOutboundBoxList, singlePickOdo, ouId, logId);

                    // 无法装入商品出库箱的明细列表
                    List<OdoLineCommand> unmatchedSkuBoxList = this.packingForSkuBox(unmatchedOdoLineBoxList, odoPackedOutboundBoxList, singlePickOdo, ouId, logId);

                    // 无法装入出库单店铺出库箱的明细列表
                    List<OdoLineCommand> unmatchedStoreBoxList = this.packingForStoreBox(unmatchedSkuBoxList, odoPackedOutboundBoxList, singlePickOdo, ouId, logId);

                    // 无法装入出库单客户出库箱的明细列表
                    List<OdoLineCommand> unmatchedCustomerBoxList = this.packingForCustomerBox(unmatchedStoreBoxList, odoPackedOutboundBoxList, singlePickOdo, ouId, logId);

                    // 存在无法装入出库单客户出库箱的商品，匹配仓库通用的出库箱，通用出库箱：未分配店铺和客户的出库箱
                    List<OdoLineCommand> unmatchedGeneralBoxList = this.packingForGeneralBox(unmatchedCustomerBoxList, odoPackedOutboundBoxList, singlePickOdo, ouId, logId);
                    if (!unmatchedGeneralBoxList.isEmpty()) {
                        // 存在未匹配出库箱的明细，整单匹配小车
                        unmatchedBoxOdoLineExist = true;
                        break;
                    }
                }// end-for 遍历规则分组的明细
            } catch (BusinessException be) {
                log.error("packing whole case odoLine error, odoLine qty less than whole case total qty, exception is:[{}], logId is:[{}]", be, logId);
                // 踢出波次
                Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                whWaveManager.deleteWaveLinesAndReleaseInventoryByOdoId(singlePickOdo.getWhWaveCommand().getId(), odoId, Constants.CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION, warehouse);

                continue;
            }

            // 存在未匹配出库箱的明细，整单匹配小车
            if (unmatchedBoxOdoLineExist) {
                // 按照配货模式分组，分成小批次
                this.odoListGroupByDistributeMode(singlePickOdo, singleOdoPickModeUnpackedOdoMap);
                continue;
            }

            // 记录出库单所有的出库箱和整托整箱的容器
            singlePickOdo.setOutboundBoxList(odoPackedOutboundBoxList);
            singlePickOdo.setWholeCaseList(odoPackedWholeCaseList);
            singlePickOdo.setWholeTrayList(odoPackedWholeTrayList);
            // 将出库单按照配货模式分组，分成小批次
            this.odoListGroupByDistributeMode(singlePickOdo, singleOdoPickModePackedOdoMap);

        }// end-for 遍历拣货模式为摘果的出库单，判断是否匹配到规则

        if (!singleOdoPickModePackedOdoMap.isEmpty()) {
            // 为已分配出库箱的出库单分配小车，匹配不上小车的，拣货时直接使用出库箱拣货
            this.allocateTrolleyForSingleOdoPickModePackedOdo(singleOdoPickModePackedOdoMap, distributionPatternRuleMap, ouId, logId);
        }


        // 未分配出库箱的出库单分配小车
        Map<String, List<OdoCommand>> unmatchedTrolleyOdoListDistributeMap = new HashMap<>();
        if (!singleOdoPickModeUnpackedOdoMap.isEmpty()) {
            unmatchedTrolleyOdoListDistributeMap = this.allocateTrolleyForUnPackingOdo(singleOdoPickModeUnpackedOdoMap, distributionPatternRuleMap, ouId, logId);
        }

        // 未匹配小车的出库单分配周转箱
        if (!unmatchedTrolleyOdoListDistributeMap.isEmpty()) {
            List<OdoCommand> unmatchedTurnoverBoxOdoList = this.allocateTurnoverBoxForUnPackingOdo(unmatchedTrolleyOdoListDistributeMap,distributionPatternRuleMap,  ouId, logId);
            if (!unmatchedTurnoverBoxOdoList.isEmpty()) {
                // 未匹配周转箱的出库单,待踢出波次
                this.releaseOdoFromWave(unmatchedTurnoverBoxOdoList, Constants.CREATE_OUTBOUND_CARTON_UNMATCHED_BOX, ouId, logId);
            }
        }
    }


    /**
     * 非摘果模式的出库单装箱 拣货模式为按批摘果（单品单件）、按批摘果（秒杀）、播种、按批摘果（套装）； 按批摘果（主副品）
     *
     * @param batchOdoPickModeOdoListMap 按照拣货模式分组的出库单集合
     * @param odoCommandMap 波次下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     */
    private void packingForBatchOdoPickMode(Map<String, List<OdoCommand>> batchOdoPickModeOdoListMap, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        for (String pickingMode : batchOdoPickModeOdoListMap.keySet()) {
            List<OdoCommand> pickingModeOdoList = batchOdoPickModeOdoListMap.get(pickingMode);
            Map<String, List<OdoCommand>> distributeModeOdoMap = new HashMap<>();
            // 按配货模式分组
            for (OdoCommand odoCommand : pickingModeOdoList) {
                List<OdoCommand> distributeModeOdoList = distributeModeOdoMap.get(odoCommand.getDistributeMode());
                if (null == distributeModeOdoList) {
                    distributeModeOdoList = new ArrayList<>();
                    distributeModeOdoMap.put(odoCommand.getDistributeMode(), distributeModeOdoList);
                }
                distributeModeOdoList.add(odoCommand);
            }


            // 再按照拣货模式分组成小批次，各种拣货模式的小批次规则不一样，需要区别划分
            if (Constants.PICKING_MODE_BATCH_SINGLE.equals(pickingMode)) {
                /** 拣货模式 按批摘果-单品单件 */
                // 小批次定义：出库单配货模式 + 拣货模式（按批摘果（单品单件））= 1批次
                List<OdoCommand> unmatchedTurnoverBoxOdoList = this.packingForSingleMode(distributeModeOdoMap, odoCommandMap, distributionPatternRuleMap, ouId, logId);
                if (!unmatchedTurnoverBoxOdoList.isEmpty()) {
                    // 未匹配周转箱的出库单,待踢出波次
                    this.releaseOdoFromWave(unmatchedTurnoverBoxOdoList, Constants.CREATE_OUTBOUND_CARTON_UNMATCHED_BOX, ouId, logId);
                }
            }
            if (Constants.PICKING_MODE_BATCH_SECKILL.equals(pickingMode)) {
                /** 拣货模式 按批摘果-秒杀 */
                // 小批次定义：出库单配货模式 + 拣货模式（按批摘果（秒杀））+ 配货模式编码SKU = 小批次
                List<OdoCommand> unmatchedTurnoverBoxOdoList = this.packingForSecKillMode(distributeModeOdoMap, odoCommandMap, distributionPatternRuleMap, ouId, logId);
                if (!unmatchedTurnoverBoxOdoList.isEmpty()) {
                    // 未匹配周转箱的出库单,待踢出波次
                    this.releaseOdoFromWave(unmatchedTurnoverBoxOdoList, Constants.CREATE_OUTBOUND_CARTON_UNMATCHED_BOX, ouId, logId);
                }
            }
            if (Constants.PICKING_MODE_BATCH_MAIN.equals(pickingMode)) {
                /** 拣货模式 按批摘果-主副品 */
                // 小批次定义：出库单配货模式 + 拣货模式（按批摘果（主副品））+ 主品SKU = 小批次
                List<OdoCommand> unmatchedTurnoverBoxOdoList = this.packingForMainSkuMode(distributeModeOdoMap, odoCommandMap, distributionPatternRuleMap, ouId, logId);
                if (!unmatchedTurnoverBoxOdoList.isEmpty()) {
                    // 未匹配周转箱的出库单,待踢出波次
                    this.releaseOdoFromWave(unmatchedTurnoverBoxOdoList, Constants.CREATE_OUTBOUND_CARTON_UNMATCHED_BOX, ouId, logId);
                }
            }
            if (Constants.PICKING_MODE_BATCH_GROUP.equals(pickingMode)) {
                /** 拣货模式 按批摘果-套装 */
                // 小批次定义：出库单配货模式 + 拣货模式（按批摘果（套装））+ 套装组合编码 = 小批次
                List<OdoCommand> unmatchedTurnoverBoxOdoList = this.packingForSkuGroupMode(distributeModeOdoMap, odoCommandMap, distributionPatternRuleMap, ouId, logId);
                if (!unmatchedTurnoverBoxOdoList.isEmpty()) {
                    // 未匹配周转箱的出库单,待踢出波次
                    this.releaseOdoFromWave(unmatchedTurnoverBoxOdoList, Constants.CREATE_OUTBOUND_CARTON_UNMATCHED_BOX, ouId, logId);
                }
            }
            if (Constants.PICKING_MODE_SEED.equals(pickingMode)) {
                /** 拣货模式 播种 */
                // 小批次定义：出库单配货模式 + 拣货模式（播种）+ 出库单对应拣货区域 + 仓库基础信息（播种墙对应单据数）= 小批次
                List<OdoCommand> unmatchedTurnoverBoxOdoList = this.packingForModeSeed(distributeModeOdoMap, odoCommandMap, distributionPatternRuleMap, ouId, logId);
                if (!unmatchedTurnoverBoxOdoList.isEmpty()) {
                    // 未匹配周转箱的出库单,待踢出波次
                    this.releaseOdoFromWave(unmatchedTurnoverBoxOdoList, Constants.CREATE_OUTBOUND_CARTON_UNMATCHED_BOX, ouId, logId);
                }
            }

        }
    }



    /**
     * 获取配货模式
     *
     * @author mingwei.xie
     * @param ouId 仓库组织ID
     * @return <distributionPatternCode, rule>
     */
    private Map<String, WhDistributionPatternRule> getDistributionPatternRule(Long ouId) {
        // String cacheKey =
        // CacheKeyConstant.CREATE_OUTBOUND_CARTON_DISTRIBUTION_PATTERN_RULE_PREFIX + ouId;
        // Map<String, WhDistributionPatternRule> ruleMapCache;
        // try {
        // ruleMapCache = cacheManager.getObject(cacheKey);
        // } catch (Exception e) {
        // log.error("getDistributionPatternRule cacheManager.getObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("配货模式缓存读取错误");
        // }
        // if (null == ruleMapCache || ruleMapCache.isEmpty()) {
        // List<WhDistributionPatternRule> distributionPatternRuleList =
        // whDistributionPatternRuleManager.findRuleByOuId(ouId);
        // ruleMapCache = new HashMap<>();
        // for (WhDistributionPatternRule rule : distributionPatternRuleList) {
        // ruleMapCache.put(rule.getDistributionPatternCode(), rule);
        // }
        // try {
        // cacheManager.setObject(cacheKey, ruleMapCache, CacheKeyConstant.CACHE_ONE_DAY);
        // } catch (Exception e) {
        // log.error("getDistributionPatternRule cacheManager.setObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("配货模式缓存写入缓存错误");
        // }
        // }
        // try {
        // ruleMapCache = cacheManager.getObject(cacheKey);
        // } catch (Exception e) {
        // log.error("getDistributionPatternRule cacheManager.getObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("配货模式缓存读取错误");
        // }
        // if (null == ruleMapCache || ruleMapCache.isEmpty()) {
        // log.warn("cache whDistributionPatternRule, data is null, logId is:[{}]", log);
        // }

        List<WhDistributionPatternRule> distributionPatternRuleList = whDistributionPatternRuleManager.findRuleByOuId(ouId);
        Map<String, WhDistributionPatternRule> ruleMapCache = new HashMap<>();
        for (WhDistributionPatternRule rule : distributionPatternRuleList) {
            ruleMapCache.put(rule.getDistributionPatternCode(), rule);
        }
        return ruleMapCache;
    }


    private Map<Long, OutboundBoxRuleCommand> getOdoOutboundBoxRuleMap(List<Long> pickingModePickingOdoIdList, Long ouId) {
        RuleAfferCommand ruleAfferCommand = new RuleAfferCommand();
        ruleAfferCommand.setOuid(ouId);
        ruleAfferCommand.setRuleType(Constants.RULE_TYPE_OUTBOUND_BOX);
        ruleAfferCommand.setOutboundBoxRuleOdoIdList(pickingModePickingOdoIdList);
        // 将拣货模式为摘果的进行出库箱规则匹配
        RuleExportCommand ruleExportCommand = ruleManager.ruleExport(ruleAfferCommand);
        return ruleExportCommand.getOdoOutboundBoxRuleMap();
    }

    private void odoListGroupByDistributeMode(OdoCommand odoCommand, Map<String, List<OdoCommand>> distributeModeOdoMap) {
        List<OdoCommand> distributeModeOdoList = distributeModeOdoMap.get(odoCommand.getDistributeMode());
        if (null == distributeModeOdoList) {
            distributeModeOdoList = new ArrayList<>();
            distributeModeOdoMap.put(odoCommand.getDistributeMode(), distributeModeOdoList);
        }
        distributeModeOdoList.add(odoCommand);
    }

    private List<String> getOdoLineIdGroupList(Long odoId, OutboundBoxRuleCommand ruleCommand) {
        // 出库单匹配到装箱规则
        RuleAfferCommand ruleAfferCommand = new RuleAfferCommand();
        ruleAfferCommand.setOutboundBoxSortOdoId(odoId);
        ruleAfferCommand.setOutboundBoxRuleCommand(ruleCommand);
        ruleAfferCommand.setOuid(ruleCommand.getOuId());
        RuleExportCommand exportCommand = ruleManager.ruleExportOutboundBoxSplitRequire(ruleAfferCommand);
        // 获得出库单明细分组后的id列表
        return exportCommand.getOdoLineIdGroupList();
    }

    /**
     * 将出库单按照规则排序
     *
     * @author mingwei.xie
     * @param odoLineIdGroupStr 已按照规则分组排序的出库单明细id
     * @param ouId 仓库组织ID
     * @return 按照规则分组排序好的出库单明细集合
     */
    private List<OdoLineCommand> getSortedOdoLineList(String odoLineIdGroupStr, Long ouId) {
        // 按照装箱规则分组后放在一个箱子内的出库单明细
        List<String> odoLineIdStrList = Arrays.asList(odoLineIdGroupStr.split(","));
        // 出库单需按照此顺序匹配出库箱
        List<Long> odoLineIdList = new ArrayList<>();
        for (String odoLineIdStr : odoLineIdStrList) {
            odoLineIdList.add(Long.valueOf(odoLineIdStr));
        }
        if (odoLineIdList.isEmpty()) {
            return null;
        }
        // 按装箱规则分好组之后的出库单对象列表
        List<OdoLineCommand> odoLineList = odoLineManager.findOdoLineById(odoLineIdList, ouId);
        Map<Long, OdoLineCommand> odoLineMap = new HashMap<>();
        for (OdoLineCommand whOdoLine : odoLineList) {
            odoLineMap.put(whOdoLine.getId(), whOdoLine);
        }
        List<OdoLineCommand> sortedOdoLineList = new ArrayList<>();
        for (Long odoLineId : odoLineIdList) {
            sortedOdoLineList.add(odoLineMap.get(odoLineId));
        }
        return sortedOdoLineList;
    }

    /**
     * 整托整箱
     *
     * @author mingwei.ixe
     * @param sortedOdoLineList 已排序分组的出库单明细
     * @param singlePickOdo 摘果出库单
     * @param odoPackedWholeCaseList 整箱列表
     * @param odoPackedWholeTrayList 整托列表
     * @param ouId 仓库组织ID
     */
    private void packingWholeCaseLine(List<OdoLineCommand> sortedOdoLineList, OdoCommand singlePickOdo, List<ContainerCommand> odoPackedWholeCaseList, List<ContainerCommand> odoPackedWholeTrayList, Long ouId) {
        // 出库单信息<odoLineId, waveLine>
        Map<Long, WhWaveLineCommand> singlePickOdoLineIdWaveLineMap = singlePickOdo.getOdoLineIdwaveLineMap();
        // 去除整托整箱的明细，暂时只考虑波次明细，即出库单明细整托整箱的情况，多个明细占用整托整箱的计算以后考虑
        ListIterator<OdoLineCommand> sortedOdoLineIterator = sortedOdoLineList.listIterator();
        while (sortedOdoLineIterator.hasNext()) {
            OdoLineCommand odoLine = sortedOdoLineIterator.next();
            WhWaveLineCommand waveLineCommand = singlePickOdoLineIdWaveLineMap.get(odoLine.getId());
            if (waveLineCommand.getIsPalletContainer()) {
                //记录整托中的箱子，在整箱中排除
                Set<Long> wholeTrayInsideContainerIdSet = new HashSet<>();
                // 判断整托占用
                if (null != waveLineCommand.getTrayIds() && !"".equals(waveLineCommand.getTrayIds())) {
                    List<Long> outerContainerIdList = new ArrayList<>();
                    List<String> outerContainerIdStrList = Arrays.asList(waveLineCommand.getTrayIds().split(","));
                    for (String outerContainerIdStr : outerContainerIdStrList) {
                        outerContainerIdList.add(Long.valueOf(outerContainerIdStr));
                    }

                    //一个明细占用的整托，只有一种商品
                    List<WhSkuInventoryCommand> skuInvList = outboundBoxRecManager.findSkuInvListByWholeTray(outerContainerIdList, ouId);
                    for(WhSkuInventoryCommand skuInv : skuInvList){
                        //记录整托中的箱子，在整箱中排除
                        wholeTrayInsideContainerIdSet.add(skuInv.getInsideContainerId());

                        WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                        odoOutBoundBoxCommand.setQty(skuInv.getOnHandQty());
                        odoOutBoundBoxCommand.setOuId(ouId);
                        odoOutBoundBoxCommand.setWaveId(singlePickOdo.getWhWaveCommand().getId());
                        odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                        odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                        odoOutBoundBoxCommand.setContainerId(skuInv.getInsideContainerId());
                        odoOutBoundBoxCommand.setWholeCase(Constants.ODO_OUTBOUND_BOX_WHOLE_TRAY);
                        odoOutBoundBoxCommand.setIsCreateWork(false);
                        // 添加批次号,批次号在分配小车时创建
                        // odoOutBoundBoxCommand.setBoxBatch();

                        // 明细扣减相应数量
                        odoLine.setQty(odoLine.getQty() - skuInv.getOnHandQty());

                        // 查找容器信息，将包裹装入容器，并且记录箱子
                        ContainerCommand containerCommand = outboundBoxRecManager.findContainerById(skuInv.getInsideContainerId(), ouId);
                        containerCommand.setOdoOutboundBoxCommandList(Collections.singletonList(odoOutBoundBoxCommand));

                        // 记录出库单包装的箱子
                        if (null == odoPackedWholeTrayList) {
                            odoPackedWholeTrayList = new ArrayList<>();
                        }
                        odoPackedWholeTrayList.add(containerCommand);
                    }

                }// end-if 判断整托占用
                 // 判断整箱占用
                if (null != waveLineCommand.getPackingCaseIds() && !"".equals(waveLineCommand.getPackingCaseIds())) {
                    List<Long> innerContainerIdList = new ArrayList<>();
                    List<String> innerContainerIdStrList = Arrays.asList(waveLineCommand.getPackingCaseIds().split(","));
                    for (String innerContainerIdStr : innerContainerIdStrList) {
                        //只添加不在整托中的箱子
                        if(!wholeTrayInsideContainerIdSet.contains(Long.valueOf(innerContainerIdStr))){
                            innerContainerIdList.add(Long.valueOf(innerContainerIdStr));
                        }
                    }
                    //一个明细占用的整托，只有一种商品
                    List<WhSkuInventoryCommand> skuInvList = outboundBoxRecManager.findSkuInvListByWholeContainer(innerContainerIdList, ouId);
                    for(WhSkuInventoryCommand skuInv : skuInvList){
                        WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                        odoOutBoundBoxCommand.setQty(skuInv.getOnHandQty());
                        odoOutBoundBoxCommand.setOuId(ouId);
                        odoOutBoundBoxCommand.setWaveId(singlePickOdo.getWhWaveCommand().getId());
                        odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                        odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                        odoOutBoundBoxCommand.setContainerId(skuInv.getInsideContainerId());
                        odoOutBoundBoxCommand.setWholeCase(Constants.ODO_OUTBOUND_BOX_WHOLE_CAASE);
                        odoOutBoundBoxCommand.setIsCreateWork(false);
                        // 添加批次号,批次号在分配小车时创建
                        // odoOutBoundBoxCommand.setBoxBatch();

                        // 明细扣减相应数量
                        odoLine.setQty(odoLine.getQty() - skuInv.getOnHandQty());

                        // 查找容器信息，将包裹装入容器，并且记录箱子
                        ContainerCommand containerCommand = outboundBoxRecManager.findContainerById(skuInv.getInsideContainerId(), ouId);
                        containerCommand.setOdoOutboundBoxCommandList(Collections.singletonList(odoOutBoundBoxCommand));

                        // 记录出库单包装的箱子
                        if (null == odoPackedWholeCaseList) {
                            odoPackedWholeCaseList = new ArrayList<>();
                        }
                        odoPackedWholeCaseList.add(containerCommand);
                    }

                }// end-if 判断整箱占用
                if (0 > odoLine.getQty()) {
                    throw new BusinessException("数据异常");
                }

                if (0 == odoLine.getQty()) {
                    // 如果明细数量扣减为0，移除明细
                    sortedOdoLineIterator.remove();
                }
            }// end-if 判断是否是整托整箱
        }// end-while 遍历每一个明细
    }

    private List<OdoLineCommand> packingForOdoBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        List<OdoLineCommand> unmatchedBoxOdoLineList = new ArrayList<>();
        if (null == odoLineList || odoLineList.isEmpty()) {
            return unmatchedBoxOdoLineList;
        }

        // odo订单上配置的出库箱类型
        OutInvBoxTypeCommand odoBoxType = null;
        if (null != odoCommand.getOutboundCartonType()) {
            odoBoxType = outboundBoxTypeManager.findOutInventoryBoxType(odoCommand.getOutboundCartonType(), ouId);
        }

        if (null == odoBoxType || BaseModel.LIFECYCLE_DISABLE.equals(odoBoxType.getLifecycle())) {
            unmatchedBoxOdoLineList.addAll(odoLineList);
            odoLineList.clear();
        }

        // 明细列表不为空则需要继续分配出库箱
        while (null != odoBoxType && !odoLineList.isEmpty()) {
            // 每一次新的遍历都是使用一个新箱子
            OutInvBoxTypeCommand newBox = new OutInvBoxTypeCommand();
            // 复制箱子信息
            BeanUtils.copyProperties(odoBoxType, newBox);
            // 出库箱是否可用计算器
            SimpleCubeCalculator newBoxCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());

            // 记录该箱子中各个明细装了多少数量<odoLine, odoOutboundBox>,每个箱子中，明细对应的包裹
            Map<Long, WhOdoOutBoundBoxCommand> odoLineOutBoundBoxMap = new HashMap<>();

            Iterator<OdoLineCommand> odoLineIterator = odoLineList.listIterator();
            // 一轮遍历结束，不管箱子有没有装满，已经没有可以放进去的商品
            while (odoLineIterator.hasNext()) {
                OdoLineCommand odoLine = odoLineIterator.next();

                // 出库单明细的商品
                SkuRedisCommand skuRedisCommand = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommand.getSku();

                // 判断商品边长是否符合的计算器
                SimpleCubeCalculator boxAvailableCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
                boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isSkuAvailable = boxAvailableCalculator.calculateAvailable();
                //boolean isSkuAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                if (!isSkuAvailable) {
                    // 商品边长不合适，添加到下一出库箱范围
                    unmatchedBoxOdoLineList.add(odoLine);
                    odoLineIterator.remove();
                    continue;
                }

                // 累计填充体积
                this.packingSkuIntoBox(odoLine, newBox, newBoxCalculator, sku, odoLineOutBoundBoxMap, odoCommand, ouId, logId);
                // 明细最后一个商品已装入出库箱，该明细不需要再分配出库箱
                if (0 == odoLine.getQty()) {
                    odoLineIterator.remove();
                }
            }
            // 箱子不为空的
            if (!odoLineOutBoundBoxMap.isEmpty()) {
                // 出库箱装箱包裹列表
                List<WhOdoOutBoundBoxCommand> newBoxOdoOutboundBoxList = new ArrayList<>();
                // 创建出库箱编码，设置包裹的出库箱编码
                String outboundBoxCode = this.codeManager.generateCode(Constants.WMS, Constants.OUTBOUNDBOX_CODE, null, null, null);
                for (WhOdoOutBoundBoxCommand odoOutBoundBox : odoLineOutBoundBoxMap.values()) {
                    odoOutBoundBox.setOutbounxboxTypeCode(outboundBoxCode);
                    newBoxOdoOutboundBoxList.add(odoOutBoundBox);
                }
                // 将明细包裹放入箱子
                newBox.setOdoOutBoundBoxCommandList(newBoxOdoOutboundBoxList);

                // 将箱子添加到箱子列表
                packBoxList.add(newBox);

            }
        }
        return unmatchedBoxOdoLineList;
    }

    private List<OdoLineCommand> packingForOdoLineBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packingBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        List<OdoLineCommand> unmatchedBoxOdoLineList = new ArrayList<>();
        if (null == odoLineList || odoLineList.isEmpty()) {
            return unmatchedBoxOdoLineList;
        }
        Iterator<OdoLineCommand> odoLineIterator = odoLineList.listIterator();
        while (odoLineIterator.hasNext()) {
            OdoLineCommand odoLine = odoLineIterator.next();
            OutInvBoxTypeCommand odoLineBox = null;
            if (null != odoLine.getOutboundCartonType()) {
                odoLineBox = outboundBoxTypeManager.findOutInventoryBoxType(odoLine.getOutboundCartonType(), ouId);
            }
            if (null == odoLineBox || BaseModel.LIFECYCLE_DISABLE.equals(odoLineBox.getLifecycle())) {
                // 明细上的出库箱类型未定义，查找下一个范围
                unmatchedBoxOdoLineList.add(odoLine);
                odoLineIterator.remove();
                continue;
            }

            // 出库单明细的商品
            SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
            Sku sku = skuRedisCommandTemp.getSku();

            // 判断商品边长是否符合的计算器
            SimpleCubeCalculator boxAvailableCalculator = new SimpleCubeCalculator(odoLineBox.getLength(), odoLineBox.getWidth(), odoLineBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
            boolean isSkuAvailable = boxAvailableCalculator.calculateAvailable();
            //boolean isSkuAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
            if (!isSkuAvailable) {
                // 商品边长不合适，添加到下一出库箱范围
                unmatchedBoxOdoLineList.add(odoLine);
                odoLineIterator.remove();
                continue;
            }
            // 只要商品明细还存在数量就需要继续分配出库箱
            this.packingSingleSkuForSingleBox(odoLine, odoLineBox, packingBoxList, odoCommand, ouId, logId);

            // 明细的商品已装箱完毕，移除该明细
            if (0 == odoLine.getQty()) {
                odoLineIterator.remove();
            }

        }
        return unmatchedBoxOdoLineList;
    }

    private List<OdoLineCommand> packingForSkuBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packingBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        List<OdoLineCommand> unmatchedBoxOdoLineList = new ArrayList<>();
        if (null == odoLineList || odoLineList.isEmpty()) {
            return unmatchedBoxOdoLineList;
        }
        Iterator<OdoLineCommand> odoLineIterator = odoLineList.listIterator();
        while (odoLineIterator.hasNext()) {
            OdoLineCommand odoLine = odoLineIterator.next();
            // 出库单明细的商品
            SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
            Sku sku = skuRedisCommandTemp.getSku();
            WhSkuWhmgmt skuWhmgmt = skuRedisCommandTemp.getWhSkuWhMgmt();

            OutInvBoxTypeCommand skuBox = null;
            if (null != skuWhmgmt.getOutboundCtnType()) {
                skuBox = outboundBoxTypeManager.findOutInventoryBoxType(skuWhmgmt.getOutboundCtnType(), ouId);
            }
            if (null == skuBox || BaseModel.LIFECYCLE_DISABLE.equals(skuBox.getLifecycle())) {
                // 商品上的出库箱类型未定义，查找下一范围
                unmatchedBoxOdoLineList.add(odoLine);
                odoLineIterator.remove();
                continue;
            }

            // 判断商品边长是否符合的计算器
            SimpleCubeCalculator boxAvailableCalculator = new SimpleCubeCalculator(skuBox.getLength(), skuBox.getWidth(), skuBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
            boolean isSkuAvailable = boxAvailableCalculator.calculateAvailable();
            //boolean isSkuAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
            if (!isSkuAvailable) {
                // 商品边长不合适，添加到下一出库箱范围
                unmatchedBoxOdoLineList.add(odoLine);
                odoLineIterator.remove();
                continue;
            }
            // 只要商品明细还存在数量就需要继续分配出库箱
            this.packingSingleSkuForSingleBox(odoLine, skuBox, packingBoxList, odoCommand, ouId, logId);
            // 明细的商品已装箱完毕，移除该明细
            if (0 == odoLine.getQty()) {
                odoLineIterator.remove();
            }

        }
        return unmatchedBoxOdoLineList;
    }

    private List<OdoLineCommand> packingForStoreBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packingBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        List<OdoLineCommand> unmatchedBoxOdoLineList = new ArrayList<>();
        if (null == odoLineList || odoLineList.isEmpty()) {
            return unmatchedBoxOdoLineList;
        }
        // 获取出库单头店铺配置的出库箱
        List<OutInvBoxTypeCommand> odoStoreBoxList = this.getOdoStoreBoxList(odoCommand, ouId);
        if (null != odoStoreBoxList && !odoStoreBoxList.isEmpty()) {
            unmatchedBoxOdoLineList = this.packingMultiSkuForMultiBox(odoLineList, packingBoxList, odoStoreBoxList, odoCommand, ouId, logId);
        } else {
            unmatchedBoxOdoLineList.addAll(odoLineList);
            odoLineList.clear();
        }
        return unmatchedBoxOdoLineList;
    }

    private List<OdoLineCommand> packingForCustomerBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packingBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        List<OdoLineCommand> unmatchedBoxOdoLineList = new ArrayList<>();
        if (null == odoLineList || odoLineList.isEmpty()) {
            return unmatchedBoxOdoLineList;
        }
        // 获取出库单头客户配置的出库箱
        List<OutInvBoxTypeCommand> odoCustomerBoxList = this.getOdoCustomerBoxList(odoCommand, ouId);
        if (null != odoCustomerBoxList && !odoCustomerBoxList.isEmpty()) {
            unmatchedBoxOdoLineList = this.packingMultiSkuForMultiBox(odoLineList, packingBoxList, odoCustomerBoxList, odoCommand, ouId, logId);
        } else {
            unmatchedBoxOdoLineList.addAll(odoLineList);
            odoLineList.clear();
        }
        return unmatchedBoxOdoLineList;
    }

    private List<OdoLineCommand> packingForGeneralBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packingBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        List<OdoLineCommand> unmatchedGeneralBoxOdoLineList = new ArrayList<>();
        if (null == odoLineList || odoLineList.isEmpty()) {
            return unmatchedGeneralBoxOdoLineList;
        }
        // 获取店铺通用出库箱
        List<OutInvBoxTypeCommand> odoGeneralBoxList = this.getOdoGeneralBoxList(ouId);
        if (null != odoGeneralBoxList && !odoGeneralBoxList.isEmpty()) {
            unmatchedGeneralBoxOdoLineList = this.packingMultiSkuForMultiBox(odoLineList, packingBoxList, odoGeneralBoxList, odoCommand, ouId, logId);
        } else {
            unmatchedGeneralBoxOdoLineList.addAll(odoLineList);
            odoLineList.clear();
        }
        return unmatchedGeneralBoxOdoLineList;
    }

    private void packingSingleSkuForSingleBox(OdoLineCommand odoLine, OutInvBoxTypeCommand outInvBoxType, List<OutInvBoxTypeCommand> packingBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
        Sku sku = skuRedisCommandTemp.getSku();

        // 只要商品明细还存在数量就需要继续分配出库箱
        while (odoLine.getQty() > 0) {
            // 出库箱是否可用计算器
            SimpleCubeCalculator boxCalculator = new SimpleCubeCalculator(outInvBoxType.getLength(), outInvBoxType.getWidth(), outInvBoxType.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());

            // 每一次新的遍历都是使用一个新箱子
            OutInvBoxTypeCommand newBox = new OutInvBoxTypeCommand();
            // 复制箱子信息
            BeanUtils.copyProperties(outInvBoxType, newBox);
            // 记录该箱子中各个明细装了多少数量<odoLineId, odoOutboundBox>
            Map<Long, WhOdoOutBoundBoxCommand> odoLineOutBoundBoxMap = new HashMap<>();

            // 累计填充体积
            this.packingSkuIntoBox(odoLine, newBox, boxCalculator, sku, odoLineOutBoundBoxMap, odoCommand, ouId, logId);
            // 箱子不为空的
            if (!odoLineOutBoundBoxMap.isEmpty()) {
                // 出库箱装箱包裹列表
                List<WhOdoOutBoundBoxCommand> newBoxOdoOutboundBoxList = new ArrayList<>();
                // 创建出库箱编码，设置包裹的出库箱编码，编码服务未配置
                String outboundBoxCode = this.codeManager.generateCode(Constants.WMS, Constants.OUTBOUNDBOX_CODE, null, null, null);
                for (WhOdoOutBoundBoxCommand odoOutBoundBox : odoLineOutBoundBoxMap.values()) {
                    odoOutBoundBox.setOutbounxboxTypeCode(outboundBoxCode);
                    newBoxOdoOutboundBoxList.add(odoOutBoundBox);
                }
                // 将明细包裹放入箱子
                newBox.setOdoOutBoundBoxCommandList(newBoxOdoOutboundBoxList);
                // 将箱子添加到箱子列表
                packingBoxList.add(newBox);

            }
        }
    }

    private List<OdoLineCommand> packingMultiSkuForMultiBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> packingBoxList, List<OutInvBoxTypeCommand> availableBoxList, OdoCommand odoCommand, Long ouId, String logId) {
        // 待移除的
        List<OdoLineCommand> unmatchedBoxOdoLineList = new ArrayList<>();

        // 先把所有出库箱都无法装下的明细商品，将明细放到下一个匹配范围
        Map<OdoLineCommand, List<OutInvBoxTypeCommand>> odoLineAvailableBoxListMap = this.getOdoLineAvailableBoxMap(odoLineList, availableBoxList, ouId, logId);

        if (null == odoLineAvailableBoxListMap || odoLineAvailableBoxListMap.isEmpty()) {
            unmatchedBoxOdoLineList.addAll(odoLineList);
            odoLineList.clear();
        }

        while (null != odoLineAvailableBoxListMap && !odoLineList.isEmpty()) {
            List<OutInvBoxTypeCommand> firstOdoLineAvailableBoxList = odoLineAvailableBoxListMap.get(odoLineList.get(0));
            if (null == firstOdoLineAvailableBoxList || firstOdoLineAvailableBoxList.isEmpty()) {
                // 添加到下一个匹配范围
                unmatchedBoxOdoLineList.add(odoLineList.get(0));
                // 从当前的分配列表移除
                odoLineList.remove(odoLineList.get(0));
                continue;
            }
            // 记录应该使用的出库箱
            OutInvBoxTypeCommand availableBoxType;
            // 每一次重新计算使用哪个箱子
            if (1 == firstOdoLineAvailableBoxList.size()) {
                availableBoxType = firstOdoLineAvailableBoxList.get(0);
            } else {
                availableBoxType = this.getAvailableBox(odoLineList, firstOdoLineAvailableBoxList, ouId, logId);
            }
            // 每一次新的遍历都是使用一个新箱子
            OutInvBoxTypeCommand newBox = new OutInvBoxTypeCommand();
            // 复制箱子信息
            BeanUtils.copyProperties(availableBoxType, newBox);
            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            // 记录该箱子中各个明细装了多少数量<odoLineId, outboundBox>
            Map<Long, WhOdoOutBoundBoxCommand> odoLineOutBoundBoxMap = new HashMap<>();

            Iterator<OdoLineCommand> odoLineIterator = odoLineList.listIterator();
            while (odoLineIterator.hasNext()) {
                // 一轮遍历结束，不管箱子有没有装满，已经没有可以放进去的商品
                OdoLineCommand odoLine = odoLineIterator.next();
                // 出库单明细的商品
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                // 判断商品边长是否符合的计算器
                SimpleCubeCalculator boxAvailableCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
                boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isSkuAvailable = boxAvailableCalculator.calculateAvailable();
                //boolean isSkuAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                if (!isSkuAvailable) {
                    // 商品边长不合适，下一个明细,该处不移除，需要在下一次遍历中找出合适的箱子
                    continue;
                }
                this.packingSkuIntoBox(odoLine, newBox, outboundBoxCalculator, sku, odoLineOutBoundBoxMap, odoCommand, ouId, logId);
                // 明细最后一个商品已装入出库箱，该明细不需要再分配出库箱
                if (0 == odoLine.getQty()) {
                    odoLineIterator.remove();
                }
            }// end-while 遍历明细，试着将明细的商品放入出库箱
             // 箱子不为空的
            if (!odoLineOutBoundBoxMap.isEmpty()) {
                // 出库箱装箱包裹列表
                List<WhOdoOutBoundBoxCommand> newBoxOdoOutboundBoxList = new ArrayList<>();
                // 创建出库箱编码，设置包裹的出库箱编码
                String outboundBoxCode = this.codeManager.generateCode(Constants.WMS, Constants.OUTBOUNDBOX_CODE, null, null, null);
                for (WhOdoOutBoundBoxCommand odoOutBoundBox : odoLineOutBoundBoxMap.values()) {
                    odoOutBoundBox.setOutbounxboxTypeCode(outboundBoxCode);
                    newBoxOdoOutboundBoxList.add(odoOutBoundBox);
                }
                // 将明细包裹放入箱子
                newBox.setOdoOutBoundBoxCommandList(newBoxOdoOutboundBoxList);
                // 将箱子添加到箱子列表
                packingBoxList.add(newBox);

            }
        }// end-while 分配出库箱装箱
        return unmatchedBoxOdoLineList;
    }

    private void packingSkuIntoBox(OdoLineCommand odoLine, OutInvBoxTypeCommand targetBox, SimpleCubeCalculator boxCalculator, Sku sku, Map<Long, WhOdoOutBoundBoxCommand> lineBoxMap, OdoCommand odoCommand, Long ouId, String logId) {
        for (int odoLineSkuQty = odoLine.getQty().intValue(); odoLineSkuQty > 0; odoLineSkuQty--) {
            // 累加填充体积，如果判断为无法放入，需要扣除相应的体积
            boxCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
            boolean isBoxAvailable = boxCalculator.calculateAvailable();
            if (!isBoxAvailable) {
                // 商品无法放入，减去填充体积，因为在累加的时候已经先行累加
                Double stuffVolume = boxCalculator.getStuffVolume();
                Double skuVolume = boxCalculator.getCurrentStuffVolume();
                boxCalculator.setVolume(stuffVolume - skuVolume);
                break;
            }
            // 商品可放入，明细扣减相应的数量
            WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = lineBoxMap.get(odoLine.getId());
            if (null == odoOutBoundBoxCommand) {
                odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                odoOutBoundBoxCommand.setQty(0.0);
                odoOutBoundBoxCommand.setOuId(ouId);
                odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                odoOutBoundBoxCommand.setOutbounxboxTypeId(targetBox.getId());
                odoOutBoundBoxCommand.setIsCreateWork(false);
                // 设置出库箱类型编码，应该在出库箱装满之后，以出库箱为单位创建编码，统一设置箱子内所有包裹的出库箱编码
                // odoOutBoundBoxCommand.setOutbounxboxTypeCode();
                odoOutBoundBoxCommand.setWaveId(odoCommand.getWhWaveCommand().getId());
                // 添加到记录明细装箱数量的map中
                lineBoxMap.put(odoLine.getId(), odoOutBoundBoxCommand);
            }
            // 明细包裹数加1
            odoOutBoundBoxCommand.setQty(odoOutBoundBoxCommand.getQty() + 1);
            // 明细商品数减1
            odoLine.setQty((double) odoLineSkuQty - 1);
        }
    }

    /**
     * 已装入出库箱的摘果出库单分配小车
     *
     * @author mingwei.xie
     * @param singleOdoPickModePackedOdoMap 按照配货模式分组后的出库单
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     */
    private void allocateTrolleyForSingleOdoPickModePackedOdo(Map<String, List<OdoCommand>> singleOdoPickModePackedOdoMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        // 将小批次的出库单分别标记涉及的区域;
        for (String distributeMode : singleOdoPickModePackedOdoMap.keySet()) {

            // 摘果的出库单分组下，一个配货模式就是一个小批次
            List<OdoCommand> distributeModeOdoList = singleOdoPickModePackedOdoMap.get(distributeMode);
            // 将出库单按照出库箱数降序排序
            this.odoListOrderByBoxNumDesc(distributeModeOdoList);

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);
            while (!distributeModeOdoList.isEmpty()) {
                List<OdoCommand> batchOdoList = new ArrayList<>();

                // 划分小批次
                Iterator<OdoCommand> odoIterator = distributeModeOdoList.iterator();
                while (odoIterator.hasNext()) {
                    OdoCommand odoCommand = odoIterator.next();
                    odoIterator.remove();
                    batchOdoList.add(odoCommand);
                    if (batchOdoList.size() == rule.getOrdersUpperLimit()) {
                        break;
                    }
                }

                // 创建批次号
                String batchNo = outboundBoxRecManager.getBatchNo(ouId);

                // 按照出库单涉及的区域将出库单分组<areaIdListStr, List<Odo>>
                Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap = this.odoGroupByAllocateArea(batchOdoList, ouId, logId);
                // 按区域分组后的areaId列表集合，为了查询子集用
                List<List<Long>> allocateAreaIdListCollection = new ArrayList<>(allocateAreaOdoListMap.keySet());

                // 将区域列表按照数量降序排序
                this.orderAllocateAreaListByListSize(allocateAreaIdListCollection);
                // 遍历按照数量降序排序后的区域列表
                for (List<Long> allocateAreaIdList : allocateAreaIdListCollection) {
                    // 相同批次中涉及相同区域的出库单
                    List<OdoCommand> odoComList = allocateAreaOdoListMap.get(allocateAreaIdList);
                    // 找出合适小车，一个货格只能放一个出库箱，一个出库单只能使用一个小车，一个批次可以使用多个小车
                    if (odoComList.isEmpty()) {
                        // 这种情况可能是在填充小车时已经把出库单挑选了，如果移除了需要判断是不是null
                        continue;
                    }
                    // 将出库单按照箱子数量降序排序，先将箱子多的出库单分配小车
                    this.odoListOrderByBoxNumDesc(odoComList);
                    // 为出库单推荐小车，找不到小车的移除出列表，直接使用出库箱拣货
                    while (!odoComList.isEmpty()) {
                        OdoCommand odoCommand = odoComList.get(0);
                        if (null != odoCommand.getWholeTrayList() && !odoCommand.getWholeTrayList().isEmpty()) {
                            // 包含整托的，不推荐小车
                            if (null != odoCommand.getOutboundBoxList()) {
                                for (OutInvBoxTypeCommand outBoundBoxType : odoCommand.getOutboundBoxList()) {
                                    for (WhOdoOutBoundBoxCommand odoOutboundBox : outBoundBoxType.getOdoOutBoundBoxCommandList()) {
                                        // 设置包裹的批次号
                                        odoOutboundBox.setBoxBatch(batchNo);
                                    }
                                }
                            }
                            if (null != odoCommand.getWholeTrayList()) {
                                for (ContainerCommand wholeTrayContainer : odoCommand.getWholeTrayList()) {
                                    for (WhOdoOutBoundBoxCommand odoOutBoundBox : wholeTrayContainer.getOdoOutboundBoxCommandList()) {
                                        odoOutBoundBox.setBoxBatch(batchNo);
                                    }
                                }
                            }
                            if (null != odoCommand.getWholeCaseList()) {
                                for (ContainerCommand wholeCaseContainer : odoCommand.getWholeCaseList()) {
                                    for (WhOdoOutBoundBoxCommand odoOutBoundBox : wholeCaseContainer.getOdoOutboundBoxCommandList()) {
                                        odoOutBoundBox.setBoxBatch(batchNo);
                                    }
                                }
                            }
                            // 保存装箱数据到数据库，事务控制到出库单级别，异常的出库单踢出波次
                            outboundBoxRecManager.saveRecOutboundBoxByOdo(odoCommand);
                            odoComList.remove(odoCommand);
                            continue;
                        }// end-if 包含整托装箱的出库单，不推荐小车
                         // 查找可用小车，货格数足够，且货格尺寸可用
                        Container2ndCategoryCommand availableTrolley = this.findAvailableTrolley(odoCommand, ouId, logId);
                        // 获取可用容器，设置包裹的outContainerId
                        Container trolleyContainer = null;
                        if(null !=  availableTrolley){
                            trolleyContainer = this.getUseAbleContainer(availableTrolley, ouId);
                        }
                        if (null == availableTrolley || null == trolleyContainer) {
                            // 没有可用小车，保存数据
                            if (null != odoCommand.getOutboundBoxList()) {
                                for (OutInvBoxTypeCommand outBoundBoxType : odoCommand.getOutboundBoxList()) {
                                    for (WhOdoOutBoundBoxCommand odoOutboundBox : outBoundBoxType.getOdoOutBoundBoxCommandList()) {
                                        // 设置包裹的批次号
                                        odoOutboundBox.setBoxBatch(batchNo);
                                    }
                                }
                            }
                            if (null != odoCommand.getWholeCaseList()) {
                                for (ContainerCommand wholeCaseContainer : odoCommand.getWholeCaseList()) {
                                    for (WhOdoOutBoundBoxCommand odoOutBoundBox : wholeCaseContainer.getOdoOutboundBoxCommandList()) {
                                        odoOutBoundBox.setBoxBatch(batchNo);
                                    }
                                }
                            }
                            // 保存装箱数据到数据库，统一事务
                            outboundBoxRecManager.saveRecOutboundBoxByOdo(odoCommand);
                            odoComList.remove(odoCommand);
                            continue;
                        }

                        // 设置odoOutboundBox的货格编码
                        if (null != odoCommand.getOutboundBoxList()) {
                            for (int gridNum = 1; gridNum <= odoCommand.getOutboundBoxList().size(); gridNum++) {
                                OutInvBoxTypeCommand outBoundBoxType = odoCommand.getOutboundBoxList().get(gridNum - 1);
                                List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = outBoundBoxType.getOdoOutBoundBoxCommandList();
                                for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                                    odoOutboundBox.setContainerLatticeNo(gridNum + availableTrolley.getAssignedGridNum());
                                    odoOutboundBox.setBoxBatch(batchNo);
                                    odoOutboundBox.setOuterContainerId(trolleyContainer.getId());
                                }
                            }
                            // 出库箱分配完货格后，设置小车的已分配货格数
                            availableTrolley.setAssignedGridNum(odoCommand.getOutboundBoxList().size() + availableTrolley.getAssignedGridNum());
                        }

                        if (null != odoCommand.getWholeCaseList()) {
                            for (int gridNum = 1; gridNum <= odoCommand.getWholeCaseList().size(); gridNum++) {
                                ContainerCommand container = odoCommand.getWholeCaseList().get(gridNum - 1);
                                List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutboundBoxCommandList();
                                for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                                    odoOutboundBox.setContainerLatticeNo(gridNum + availableTrolley.getAssignedGridNum());
                                    odoOutboundBox.setBoxBatch(batchNo);
                                    odoOutboundBox.setOuterContainerId(trolleyContainer.getId());
                                }
                            }
                            // 整箱的分配完货格后，设置小车的已分配货格数
                            availableTrolley.setAssignedGridNum(odoCommand.getWholeCaseList().size() + availableTrolley.getAssignedGridNum());
                        }
                        // 保存出库单的出库箱推荐信息到数据库，事务控制到出库单级别，异常的出库单踢出波次
                        outboundBoxRecManager.saveRecOutboundBoxByOdo(odoCommand);
                        // 出库单已分配小车，移除出列表
                        odoComList.remove(odoCommand);
                        // 从其他订单中选出可以继续放入小车的订单
                        // 把新建的容器传入，设置包裹的容器ID
                        this.fillTrolleyWithOdo(availableTrolley, allocateAreaIdList, allocateAreaIdListCollection, allocateAreaOdoListMap, batchNo, trolleyContainer.getId(), ouId, logId);
                    }
                }
            }
        }
    }

    /**
     * 未分配出库箱的出库单直接分配小车
     *
     * @author mingwei.xie
     * @param distributeModeUnPackingOdoMap 未分配出库箱的出库单按照配货模式分组的集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 按照配货模式分组后的未分配小车的出库单
     */
    private Map<String, List<OdoCommand>> allocateTrolleyForUnPackingOdo(Map<String, List<OdoCommand>> distributeModeUnPackingOdoMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        // 未分配小车的出库单
        Map<String, List<OdoCommand>> unmatchedTrolleyOdoListDistributeMap = new HashMap<>();

        for (String distributeMode : distributeModeUnPackingOdoMap.keySet()) {

            // 摘果的出库单分组下，一个配货模式就是一个小批次
            List<OdoCommand> distributeModeOdoList = distributeModeUnPackingOdoMap.get(distributeMode);

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);
            while (!distributeModeOdoList.isEmpty()) {
                List<OdoCommand> batchOdoList = new ArrayList<>();

                // 划分小批次
                Iterator<OdoCommand> odoIterator = distributeModeOdoList.iterator();
                while (odoIterator.hasNext()) {
                    OdoCommand odoCommand = odoIterator.next();
                    odoIterator.remove();
                    batchOdoList.add(odoCommand);
                    if (batchOdoList.size() == rule.getOrdersUpperLimit()) {
                        break;
                    }
                }

                // 创建批次号
                String batchNo = outboundBoxRecManager.getBatchNo(ouId);


                // 按照出库单涉及的区域将出库单分组<areaIdListStr, List<Odo>>
                Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap = this.odoGroupByAllocateArea(batchOdoList, ouId, logId);
                // 按区域分组后的areaId列表集合，为了查询子集用
                List<List<Long>> allocateAreaIdListCollection = new ArrayList<>(allocateAreaOdoListMap.keySet());

                // 将区域列表按照数量降序排序
                this.orderAllocateAreaListByListSize(allocateAreaIdListCollection);

                // 遍历按照数量降序排序后的区域列表，先分配涉及区域最多的
                for (List<Long> allocateAreaIdList : allocateAreaIdListCollection) {
                    // 相同批次中涉及相同区域的出库单
                    List<OdoCommand> odoComList = allocateAreaOdoListMap.get(allocateAreaIdList);
                    if (odoComList.isEmpty()) {
                        // 这种情况可能是在填充小车时已经把出库单挑选了
                        continue;
                    }
                    while (!odoComList.isEmpty()) {
                        OdoCommand odoCommand = odoComList.get(0);

                        // 二级容器类型是小车的，按照货格数降序排序
                        List<Container2ndCategoryCommand> trolleyList = this.getTrolleyListOrderByGridNumDesc(ouId, logId);
                        for (Container2ndCategoryCommand trolley : trolleyList) {
                            // 出库单装入小车形成的所有包裹，每一个货格相当于一个出库箱，占用的所有货格中的包裹集合
                            List<WhOdoOutBoundBoxCommand> odoBoxList = new ArrayList<>();
                            // 小车是否可用
                            boolean isTrolleyAvailable = this.packingIntoTrolley(trolley, odoCommand, odoBoxList, ouId, logId);
                            // 创建新容器
                            Container trolleyContainer = this.getUseAbleContainer(trolley, ouId);
                            if (!isTrolleyAvailable || null == trolleyContainer) {
                                // 小车不可用或者小车已分配完
                                continue;
                            }


                            for (WhOdoOutBoundBoxCommand odoBox : odoBoxList) {
                                // 设置包裹的批次号
                                odoBox.setBoxBatch(batchNo);
                                // 设置包裹的outContainerId
                                odoBox.setOuterContainerId(trolleyContainer.getId());
                            }

                            // 保存包裹数据到数据库
                            outboundBoxRecManager.saveRecOutboundBoxForTrolleyPackedOdo(odoBoxList);

                            // 当前出库单明细已装完，移除出列表
                            odoComList.remove(odoCommand);
                            if (trolley.getAssignedGridNum() == trolley.getTotalGridNum()) {
                                // 小车已分配完，分配下一个出库单
                                break;
                            }
                            // 将创建的小车传入，设置包裹的容器ID
                            // 填满小车
                            this.fillTrolleyForUnPackingOdo(trolley, allocateAreaIdList, allocateAreaIdListCollection, allocateAreaOdoListMap, batchNo, trolleyContainer.getId(), ouId, logId);
                            // 出库单已经放入小车，退出小车的循环，执行下一个出库单
                            break;
                        }// end-for 遍历小车
                         // 遍历完小车，如果有小车可用，则出库单已分配完，且从出库单列表移除
                        if (odoComList.contains(odoCommand)) {
                            // 添加到未匹配小车的列表
                            odoComList.remove(odoCommand);
                            List<OdoCommand> unmatchedTrolleyOdoDistributeList = unmatchedTrolleyOdoListDistributeMap.get(distributeMode);
                            if (null == unmatchedTrolleyOdoDistributeList) {
                                unmatchedTrolleyOdoDistributeList = new ArrayList<>();
                                unmatchedTrolleyOdoListDistributeMap.put(distributeMode, unmatchedTrolleyOdoDistributeList);
                            }
                            unmatchedTrolleyOdoDistributeList.add(odoCommand);
                        }
                    }// end-while 遍历涉及区域最多的出库单
                }// end-for 遍历区域列表
            }
        }// end-for 按照配货模式分组后的未能匹配出库箱的摘果订单
        return unmatchedTrolleyOdoListDistributeMap;
    }

    /**
     * 未分配小车的出库单直接分配周转箱
     *
     * @author mingwei.xie
     * @param distributeModePackingOdoMap 按照配货模式分组后的未分配小车的出库单
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 未分配周转箱的出库单集合，待踢出波次
     */
    private List<OdoCommand> allocateTurnoverBoxForUnPackingOdo(Map<String, List<OdoCommand>> distributeModePackingOdoMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        List<OdoCommand> unmatchedTurnoverBoxOdoList = new ArrayList<>();

        for (String distributeMode : distributeModePackingOdoMap.keySet()) {

            // 摘果的出库单分组下，一个配货模式就是一个小批次
            List<OdoCommand> distributeModeOdoList = distributeModePackingOdoMap.get(distributeMode);

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);
            while (!distributeModeOdoList.isEmpty()) {
                List<OdoCommand> batchOdoList = new ArrayList<>();

                // 划分小批次
                Iterator<OdoCommand> odoIterator = distributeModeOdoList.iterator();
                while (odoIterator.hasNext()) {
                    OdoCommand odoCommand = odoIterator.next();
                    odoIterator.remove();
                    batchOdoList.add(odoCommand);
                    if (batchOdoList.size() == rule.getOrdersUpperLimit()) {
                        break;
                    }
                }
                // 创建批次号
                String batchNo = outboundBoxRecManager.getBatchNo(ouId);

                Iterator<OdoCommand> batchOdoListIterator = batchOdoList.iterator();
                while (batchOdoListIterator.hasNext()) {
                    OdoCommand odoCommand = batchOdoListIterator.next();
                    // 周转箱列表
                    List<Container2ndCategoryCommand> allTurnoverBoxList = this.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId, logId);
                    if (null == allTurnoverBoxList || allTurnoverBoxList.isEmpty()) {
                        unmatchedTurnoverBoxOdoList.add(odoCommand);
                        batchOdoListIterator.remove();
                        continue;
                    }
                    // 出库单明细
                    List<OdoLineCommand> odoLineList = odoLineManager.findOdoLineCommandListByOdoId(odoCommand.getId(), ouId);
                    // 各个明细可用的周转箱列表
                    Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap = this.getOdoLineAvailableTurnoverBoxMap(odoLineList, allTurnoverBoxList, ouId, logId);
                    // 有明细无法匹配周转箱，则出库单踢出波次
                    boolean isMatchedBox = true;
                    for (OdoLineCommand odoLine : odoLineAvailableTurnoverBoxListMap.keySet()) {
                        if (null == odoLineAvailableTurnoverBoxListMap.get(odoLine) || odoLineAvailableTurnoverBoxListMap.get(odoLine).isEmpty()) {
                            isMatchedBox = false;
                            break;
                        }
                    }
                    if (!isMatchedBox) {
                        // 有明细无法匹配周转箱，整单踢出波次
                        unmatchedTurnoverBoxOdoList.add(odoCommand);
                        continue;
                    }
                    // 已装箱的周转箱列表
                    List<Container2ndCategoryCommand> packingTurnoverBoxList = new ArrayList<>();
                    this.packingMultiSkuForMultiTurnoverBox(odoLineList, packingTurnoverBoxList, odoLineAvailableTurnoverBoxListMap, odoCommand, ouId, logId);

                    boolean isAllocateBoxSuccess = true;
                    for (Container2ndCategoryCommand container2ndCategory : packingTurnoverBoxList) {
                        // 创建周转箱
                        Container turnoverBox = this.getUseAbleContainer(container2ndCategory, ouId);
                        if(null == turnoverBox){
                            isAllocateBoxSuccess = false;
                            break;
                        }
                        // 周转箱中的所有包裹
                        List<WhOdoOutBoundBoxCommand> odoOutboundBoxList = container2ndCategory.getOdoOutBoundBoxCommandList();
                        for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutboundBoxList) {
                            odoOutboundBox.setBoxBatch(batchNo);
                            odoOutboundBox.setContainerId(turnoverBox.getId());
                        }

                    }
                    if(!isAllocateBoxSuccess){
                        // 有明细无法匹配周转箱，整单踢出波次
                        unmatchedTurnoverBoxOdoList.add(odoCommand);
                        continue;
                    }

                    // 保存出库单的周转箱装箱信息到数据库
                    outboundBoxRecManager.saveRecOutboundBoxByContainer(packingTurnoverBoxList);
                }

            }

        }
        return unmatchedTurnoverBoxOdoList;
    }

    private Container2ndCategoryCommand findAvailableTrolley(OdoCommand odoCommand, Long ouId, String logId) {
        // 可用的小车
        Container2ndCategoryCommand availableTrolley = null;
        // 查找二级容器类型，有符合的小车(货格数大于1)，创建新容器信息(实物小车是反复利用的)
        List<Container2ndCategoryCommand> trolleyList = this.getTrolleyListOrderByGridNumDesc(ouId, logId);
        for (Container2ndCategoryCommand trolley : trolleyList) {
            boolean isTrolleyAvailable = this.isTrolleyAvailable(trolley, odoCommand, ouId, logId);
            if (!isTrolleyAvailable) {
                // 小车不可用
                continue;
            }
            availableTrolley = trolley;
            break;
        }
        return availableTrolley;
    }

    private boolean isTrolleyAvailable(Container2ndCategoryCommand trolley, OdoCommand odoCommand, Long ouId, String logId) {
        boolean isTrolleyAvailable = true;
        if (trolley.getAssignedGridNum() + odoCommand.getOutboundBoxList().size() + odoCommand.getWholeCaseList().size() > trolley.getTotalGridNum()) {
            // 出库箱数大于小车可用货格数
            isTrolleyAvailable = false;
        } else {
            // 货格数够用，判断商品边长是否符合
            // 出库箱是否可用计算器
            SimpleCubeCalculator trolleyGridCalculator = new SimpleCubeCalculator(trolley.getGridLength(), trolley.getGridWidth(), trolley.getGridHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());

            // 出库箱的箱子
            List<OutInvBoxTypeCommand> odoOutboundBoxList = odoCommand.getOutboundBoxList();
            for (OutInvBoxTypeCommand odoBox : odoOutboundBoxList) {
                boolean isAvailable = trolleyGridCalculator.calculateLengthAvailable(odoBox.getLength(), odoBox.getWidth(), odoBox.getHigh(), SimpleCubeCalculator.SYS_UOM);
                if (!isAvailable) {
                    // 货格无法容纳某个出库箱，换一个小车
                    isTrolleyAvailable = false;
                    break;
                }
            }

            // 整箱的
            List<ContainerCommand> odoWholeCaseList = odoCommand.getWholeCaseList();
            for (ContainerCommand container : odoWholeCaseList) {
                Container2ndCategory container2ndCategory = outboundBoxRecManager.findContainer2ndCategoryById(container.getTwoLevelType(), ouId);
                boolean isAvailable = trolleyGridCalculator.calculateLengthAvailable(container2ndCategory.getLength(), container2ndCategory.getWidth(), container2ndCategory.getHigh(), SimpleCubeCalculator.SYS_UOM);
                if (!isAvailable) {
                    // 货格无法容纳某个出库箱，换一个小车
                    isTrolleyAvailable = false;
                    break;
                }
            }
        }
        return isTrolleyAvailable;
    }

    /**
     *
     * @param targetTrolley 待填满的小车
     * @param parentAreaIdList 已装入小车的出库单的区域集合
     * @param areaIdListCollection 批次内的区域集合
     * @param areaOdoListMap 区域集合对应的出库单列表
     * @param ouId 组织ID
     * @param logId 日志ID
     */
    private void fillTrolleyWithOdo(Container2ndCategoryCommand targetTrolley, List<Long> parentAreaIdList, List<List<Long>> areaIdListCollection, Map<List<Long>, List<OdoCommand>> areaOdoListMap, String batchNo, Long trolleyId, Long ouId, String logId) {
        // 遍历区域列表，找出子集，列表已按照区域数量排序
        for (List<Long> subAreaIdList : areaIdListCollection) {
            if (!parentAreaIdList.containsAll(subAreaIdList)) {
                continue;
            }
            // 相同批次中涉及相同区域的出库单，也可能是第一个放入小车的出库单所在的集合
            List<OdoCommand> subOdoComList = areaOdoListMap.get(subAreaIdList);
            if (subOdoComList.isEmpty()) {
                continue;
            }
            // 将出库单按照箱子数量降序排序，先将箱子多的出库单分配小车
            this.odoListOrderByBoxNumDesc(subOdoComList);
            // 将小车填满，记录小车可用货格数
            Iterator<OdoCommand> subOdoIterator = subOdoComList.iterator();
            // 找到可以放入小车的出库单
            while (subOdoIterator.hasNext()) {
                OdoCommand subOdoCommand = subOdoIterator.next();
                boolean isTrolleyAvailable = this.isTrolleyAvailable(targetTrolley, subOdoCommand, ouId, logId);
                // 如果该出库单可用该小车，则将出库单放入该小车
                if (!isTrolleyAvailable) {
                    // 出库单无法放入该小车，下一个出库单
                    continue;
                }
                // 设置odoOutboundBox的货格编码
                if (null != subOdoCommand.getOutboundBoxList()) {
                    for (int gridNum = 1; gridNum <= subOdoCommand.getOutboundBoxList().size(); gridNum++) {
                        OutInvBoxTypeCommand outBoundBoxType = subOdoCommand.getOutboundBoxList().get(gridNum - 1);
                        List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = outBoundBoxType.getOdoOutBoundBoxCommandList();
                        for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                            odoOutboundBox.setContainerLatticeNo(gridNum + targetTrolley.getAssignedGridNum());
                            // 设置批次号
                            odoOutboundBox.setBoxBatch(batchNo);
                            // 设置外部容器ID 小车类
                            odoOutboundBox.setOuterContainerId(trolleyId);
                        }
                    }
                    // 重新设置小车已分配货格数
                    targetTrolley.setAssignedGridNum(subOdoCommand.getOutboundBoxList().size() + targetTrolley.getAssignedGridNum());
                }
                if (null != subOdoCommand.getWholeCaseList()) {
                    for (int gridNum = 1; gridNum <= subOdoCommand.getWholeCaseList().size(); gridNum++) {
                        ContainerCommand container = subOdoCommand.getWholeCaseList().get(gridNum - 1);
                        List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutboundBoxCommandList();
                        for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                            odoOutboundBox.setContainerLatticeNo(gridNum + targetTrolley.getAssignedGridNum());
                            // 设置批次号
                            odoOutboundBox.setBoxBatch(batchNo);
                            // 设置外部容器ID 小车类
                            odoOutboundBox.setOuterContainerId(trolleyId);
                        }
                    }
                    // 重新设置小车已分配货格数
                    targetTrolley.setAssignedGridNum(subOdoCommand.getWholeCaseList().size() + targetTrolley.getAssignedGridNum());
                }
                // 保存出库单的装箱信息到数据库
                outboundBoxRecManager.saveRecOutboundBoxByOdo(subOdoCommand);
                // 出库单已放入小车，移除出列表
                subOdoIterator.remove();
                if (targetTrolley.getTotalGridNum() == targetTrolley.getAssignedGridNum()) {
                    // 小车已填满，退出遍历出库单的循环
                    break;
                }
            }// end-while 遍历子区域集合的出库单集合
            if (targetTrolley.getTotalGridNum() == targetTrolley.getAssignedGridNum()) {
                // 小车已填满，退出遍历区域列表的循环
                break;
            }
        }// end-for 遍历区域列表集合
    }

    /**
     *
     * @param trolley 容器 小车
     * @param odoCommand 待装入小车的出库单
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 出库单是否可装入小车
     */
    private boolean packingIntoTrolley(Container2ndCategoryCommand trolley, OdoCommand odoCommand, List<WhOdoOutBoundBoxCommand> odoBoxList, Long ouId, String logId) {

        List<OdoLineCommand> odoLineList = odoLineManager.findOdoLineCommandListByOdoId(odoCommand.getId(), ouId);
        boolean isTrolleyAvailable = true;

        // 小车原始已分配货格数
        int origTrolleyAssignedGridNum = trolley.getAssignedGridNum();
        for (int gridNum = origTrolleyAssignedGridNum + 1; gridNum <= trolley.getTotalGridNum(); gridNum++) {

            // 货格是否可用计算器
            SimpleCubeCalculator gridCalculator = new SimpleCubeCalculator(trolley.getGridLength(), trolley.getGridWidth(), trolley.getGridHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());

            // 记录该货格中各个明细装了多少数量<odoLine, odoOutboundBox>,每个箱子中，明细对应的包裹
            Map<Long, WhOdoOutBoundBoxCommand> odoLineOutBoundBoxMap = new HashMap<>();
            ListIterator<OdoLineCommand> odoLineListIterator = odoLineList.listIterator();

            // 未装箱的出库单分配小车不需要考虑混放属性
            while (odoLineListIterator.hasNext()) {
                OdoLineCommand odoLine = odoLineListIterator.next();
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                // SkuMgmt skuMgmt = skuRedisCommandTemp.getSkuMgmt();

                // 判断商品边长是否符合的计算器
                SimpleCubeCalculator boxAvailableCalculator =
                        new SimpleCubeCalculator(trolley.getGridLength(), trolley.getGridWidth(), trolley.getGridHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
                boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isGridAvailable = boxAvailableCalculator.calculateAvailable();
                //boolean isGridAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);

                // 出库单明细的商品
                if (!isGridAvailable) {
                    isTrolleyAvailable = false;
                    // 小车不可用，退出明细的while循环，标记小车不可用，进而退出货格的循环，换一个出库单
                    break;
                }

                for (int odoLineSkuQty = odoLine.getQty().intValue(); odoLineSkuQty > 0; odoLineSkuQty--) {
                    gridCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                    if (!gridCalculator.calculateAvailable()) {
                        // 商品无法放入，减去填充体积，因为在累加的时候已经先行累加
                        Double stuffVolume = gridCalculator.getStuffVolume();
                        Double skuVolume = gridCalculator.getCurrentStuffVolume();
                        gridCalculator.setVolume(stuffVolume - skuVolume);
                        // 退出明细数量循环，当前货格无法继续放入该商品，换下一个明细的商品
                        break;
                    }
                    WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = odoLineOutBoundBoxMap.get(odoLine.getId());
                    if (null == odoOutBoundBoxCommand) {
                        odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                        odoOutBoundBoxCommand.setQty(0.0);
                        odoOutBoundBoxCommand.setOuId(ouId);
                        odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                        odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                        // odoOutBoundBoxCommand.setOuterContainerId();
                        odoOutBoundBoxCommand.setContainerLatticeNo(gridNum);
                        odoOutBoundBoxCommand.setWaveId(odoCommand.getWhWaveCommand().getId());
                        odoOutBoundBoxCommand.setIsCreateWork(false);
                        // 添加到记录明细装箱数量的map中
                        odoLineOutBoundBoxMap.put(odoLine.getId(), odoOutBoundBoxCommand);
                    }
                    // 明细包裹数加1
                    odoOutBoundBoxCommand.setQty(odoOutBoundBoxCommand.getQty() + 1);
                    // 明细商品数减1
                    odoLine.setQty((double) odoLineSkuQty - 1);
                }// end-for 明细的商品数
                if (0 == odoLine.getQty()) {
                    odoLineListIterator.remove();
                }
            }// end-while 出库单下的明细
            if (!isTrolleyAvailable) {
                // 该出库单不能使用该小车，小车的已分配货格数需要还原
                trolley.setAssignedGridNum(origTrolleyAssignedGridNum);
                break;
            }
            // 执行到这里，一个货格已遍历了出库单的所有明细
            if (!odoLineOutBoundBoxMap.isEmpty()) {
                // 不会出现空的情况，如果为空，说明没有一个明细的商品可以放入货格，则小车已被标记为不可用
                odoBoxList.addAll(odoLineOutBoundBoxMap.values());
                trolley.setAssignedGridNum(gridNum);
            }
            if (odoLineList.isEmpty()) {
                break;
            }
        }// end-for 可用的货格数
         // 执行到这里，如果小车可用，出库单明细一定已经分配完，否则小车不可用，分配完的明细已被移除
        if (!odoLineList.isEmpty()) {
            // 小车剩余的货格不能容纳出库单所有的商品，换一个出库单
            isTrolleyAvailable = false;
            // 小车未被使用，将已分配货格数还原
            trolley.setAssignedGridNum(origTrolleyAssignedGridNum);
        }

        return isTrolleyAvailable;
    }

    /**
     * 播种模式的整托整箱
     *
     * @author mingwei.ixe
     * @param batchSeedOdoLineList 播种批次的出库单明细列表
     * @param odoCommandMap 波次下的出库单集合
     * @param odoPackedWholeCaseList 整箱列表
     * @param odoPackedWholeTrayList 整托列表
     * @param ouId 仓库组织ID
     */
    private void packingWholeCaseLineForModeSeed(List<OdoLineCommand> batchSeedOdoLineList, Map<Long, OdoCommand> odoCommandMap, List<ContainerCommand> odoPackedWholeCaseList, List<ContainerCommand> odoPackedWholeTrayList, Long ouId) {
        // 出库单信息<odoLineId, waveLine>
        // 去除整托整箱的明细，暂时只考虑波次明细，即出库单明细整托整箱的情况，多个明细占用整托整箱的计算以后考虑
        ListIterator<OdoLineCommand> sortedOdoLineIterator = batchSeedOdoLineList.listIterator();
        while (sortedOdoLineIterator.hasNext()) {
            OdoLineCommand odoLine = sortedOdoLineIterator.next();

            OdoCommand odoCommand = odoCommandMap.get(odoLine.getOdoId());
            Map<Long, WhWaveLineCommand> singlePickOdoLineIdWaveLineMap = odoCommand.getOdoLineIdwaveLineMap();
            WhWaveLineCommand waveLineCommand = singlePickOdoLineIdWaveLineMap.get(odoLine.getId());
            if (waveLineCommand.getIsPalletContainer()) {
                // 判断整托占用
                if (null != waveLineCommand.getTrayIds() && !"".equals(waveLineCommand.getTrayIds())) {
                    List<Long> outerContainerIdList = new ArrayList<>();
                    List<String> outerContainerIdStrList = Arrays.asList(waveLineCommand.getTrayIds().split(","));
                    for (String outerContainerIdStr : outerContainerIdStrList) {
                        outerContainerIdList.add(Long.valueOf(outerContainerIdStr));
                    }
                    //一个明细占用的整托，只有一种商品
                    List<WhSkuInventoryCommand> skuInvList = outboundBoxRecManager.findSkuInvListByWholeTray(outerContainerIdList, ouId);
                    for(WhSkuInventoryCommand skuInv : skuInvList){
                        WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                        odoOutBoundBoxCommand.setQty(skuInv.getOnHandQty());
                        odoOutBoundBoxCommand.setOuId(ouId);
                        odoOutBoundBoxCommand.setWaveId(odoCommand.getWhWaveCommand().getId());
                        odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                        odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                        odoOutBoundBoxCommand.setContainerId(skuInv.getInsideContainerId());
                        odoOutBoundBoxCommand.setWholeCase(Constants.ODO_OUTBOUND_BOX_WHOLE_TRAY);
                        odoOutBoundBoxCommand.setIsCreateWork(false);
                        // 添加批次号,批次号在分配小车时创建
                        // odoOutBoundBoxCommand.setBoxBatch();

                        // 明细扣减相应数量
                        odoLine.setQty(odoLine.getQty() - skuInv.getOnHandQty());

                        // 查找容器信息，将包裹装入容器，并且记录箱子
                        ContainerCommand containerCommand = outboundBoxRecManager.findContainerById(skuInv.getInsideContainerId(), ouId);
                        containerCommand.setOdoOutboundBoxCommandList(Collections.singletonList(odoOutBoundBoxCommand));

                        // 记录出库单包装的箱子
                        if (null == odoPackedWholeTrayList) {
                            odoPackedWholeTrayList = new ArrayList<>();
                        }
                        odoPackedWholeTrayList.add(containerCommand);
                    }

                }
                // 判断整箱占用
                if (null != waveLineCommand.getPackingCaseIds() && !"".equals(waveLineCommand.getPackingCaseIds())) {
                    List<Long> innerContainerIdList = new ArrayList<>();
                    List<String> innerContainerIdStrList = Arrays.asList(waveLineCommand.getPackingCaseIds().split(","));
                    for (String innerContainerIdStr : innerContainerIdStrList) {
                        innerContainerIdList.add(Long.valueOf(innerContainerIdStr));
                    }

                    //一个明细占用的整托，只有一种商品
                    List<WhSkuInventoryCommand> skuInvList = outboundBoxRecManager.findSkuInvListByWholeContainer(innerContainerIdList, ouId);
                    for(WhSkuInventoryCommand skuInv : skuInvList){
                        WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                        odoOutBoundBoxCommand.setQty(skuInv.getOnHandQty());
                        odoOutBoundBoxCommand.setOuId(ouId);
                        odoOutBoundBoxCommand.setWaveId(odoCommand.getWhWaveCommand().getId());
                        odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                        odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                        odoOutBoundBoxCommand.setContainerId(skuInv.getInsideContainerId());
                        odoOutBoundBoxCommand.setWholeCase(Constants.ODO_OUTBOUND_BOX_WHOLE_TRAY);
                        odoOutBoundBoxCommand.setIsCreateWork(false);
                        // 添加批次号,批次号在分配小车时创建
                        // odoOutBoundBoxCommand.setBoxBatch();

                        // 明细扣减相应数量
                        odoLine.setQty(odoLine.getQty() - skuInv.getOnHandQty());

                        // 查找容器信息，将包裹装入容器，并且记录箱子
                        ContainerCommand containerCommand = outboundBoxRecManager.findContainerById(skuInv.getInsideContainerId(), ouId);
                        containerCommand.setOdoOutboundBoxCommandList(Collections.singletonList(odoOutBoundBoxCommand));

                        // 记录出库单包装的箱子
                        if (null == odoPackedWholeCaseList) {
                            odoPackedWholeCaseList = new ArrayList<>();
                        }
                        odoPackedWholeCaseList.add(containerCommand);
                    }

                }
                if (0 > odoLine.getQty()) {
                    throw new BusinessException("数据异常");
                }

                if (0 == odoLine.getQty()) {
                    // 如果明细数量扣减为0，移除明细
                    sortedOdoLineIterator.remove();
                }
            }
        }
    }

    /**
     * 拣货模式 按批摘果-单品单件 小批次定义：出库单配货模式 + 拣货模式（按批摘果（单品单件））= 1批次
     *
     * @param distributeModeOdoMap 按照配货模式分组后的出库单集合
     * @param odoCommandMap 波次下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 未匹配周转箱的出库单
     */
    private List<OdoCommand> packingForSingleMode(Map<String, List<OdoCommand>> distributeModeOdoMap, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        List<OdoCommand> unmatchedTurnoverBoxOdoList = new ArrayList<>();
        for (String distributeMode : distributeModeOdoMap.keySet()) {

            // 一个配货模式就是一个小批次
            List<OdoCommand> batchSingleOdoList = distributeModeOdoMap.get(distributeMode);

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);
            while (!batchSingleOdoList.isEmpty()) {
                List<OdoCommand> batchOdoList = new ArrayList<>();

                // 划分小批次
                Iterator<OdoCommand> odoIterator = batchSingleOdoList.iterator();
                while (odoIterator.hasNext()) {
                    OdoCommand odoCommand = odoIterator.next();
                    odoIterator.remove();
                    batchOdoList.add(odoCommand);
                    if (batchOdoList.size() == rule.getOrdersUpperLimit()) {
                        break;
                    }
                }

                // 创建批次号
                String batchNo = outboundBoxRecManager.getBatchNo(ouId);

                List<Long> odoIdList = new ArrayList<>();
                for (OdoCommand odoCommand : batchOdoList) {
                    odoIdList.add(odoCommand.getId());
                }
                // 周转箱列表
                List<Container2ndCategoryCommand> allTurnoverBoxList = this.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId, logId);
                if (null == allTurnoverBoxList || allTurnoverBoxList.isEmpty()) {
                    unmatchedTurnoverBoxOdoList.addAll(batchOdoList);
                    batchOdoList.clear();
                    continue;
                }
                // 小批次下的所有明细
                List<OdoLineCommand> batchSingleOdoLineList = odoLineManager.findOdoLineByOdoId(odoIdList, ouId);
                // 各个明细可用的周转箱列表
                Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap = this.getOdoLineAvailableTurnoverBoxMap(batchSingleOdoLineList, allTurnoverBoxList, ouId, logId);

                // 未移除的明细记录库存表的占用码，用于查询占用的库存记录
                List<Long> batchOccLineIdList = new ArrayList<>();
                // 明细集合，方便取数据<odoLineId, odoLineCommand>
                Map<Long, OdoLineCommand> odoLineCommandMap = new HashMap<>();
                // 有明细无法匹配周转箱，则出库单踢出波次
                for (OdoLineCommand odoLine : odoLineAvailableTurnoverBoxListMap.keySet()) {
                    if (null == odoLineAvailableTurnoverBoxListMap.get(odoLine) || odoLineAvailableTurnoverBoxListMap.get(odoLine).isEmpty()) {
                        OdoCommand odoCommand = odoCommandMap.get(odoLine.getOdoId());
                        unmatchedTurnoverBoxOdoList.add(odoCommand);
                        // 单品单件 即只有一个明细，数量是1
                        batchOdoList.remove(odoCommand);
                        batchSingleOdoLineList.remove(odoLine);
                    } else {
                        batchOccLineIdList.add(odoLine.getId());
                        odoLineCommandMap.put(odoLine.getId(), odoLine);
                    }
                }

                if (batchSingleOdoLineList.isEmpty()) {
                    continue;
                }

                // 批次下的库存列表
                List<WhSkuInventoryCommand> batchSkuInventoryList = outboundBoxRecManager.findListByOccLineIdListOrderByPickingSort(batchOccLineIdList, ouId);
                try {
                    // 周转箱列表
                    List<Container2ndCategoryCommand> packingTurnoverBoxList = this.allocateTurnoverBoxForBatchModeOdo(batchSkuInventoryList, batchSingleOdoLineList, odoLineCommandMap, odoCommandMap, odoLineAvailableTurnoverBoxListMap, ouId, logId);


                    for (Container2ndCategoryCommand container : packingTurnoverBoxList) {
                        // 创建周转箱
                        Container turnoverBox = this.getUseAbleContainer(container, ouId);
                        if(null == turnoverBox){
                            throw new BusinessException("没有可用周转箱");
                        }

                        List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutBoundBoxCommandList();
                        for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                            // 设置包裹的containerId
                            odoOutboundBox.setContainerId(turnoverBox.getId());
                            odoOutboundBox.setBoxBatch(batchNo);
                        }

                    }
                    // 在一个事务中保存整个小批次的包裹信息
                    outboundBoxRecManager.saveRecOutboundBoxByContainer(packingTurnoverBoxList);
                } catch (BusinessException be) {
                    log.error("packingForSingleMode error,batchOdoList is:[{}], e is:[{}], logId is:[{}]", batchOdoList, be, logId);
                    // 整个批次的出库单踢出波次
                    this.releaseOdoFromWave(batchOdoList, Constants.CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION, ouId, logId);
                }
            }

        }
        return unmatchedTurnoverBoxOdoList;
    }

    /**
     * 拣货模式 按批摘果-秒杀 小批次定义：出库单配货模式 + 拣货模式（按批摘果（秒杀））+ 配货模式编码SKU = 小批次
     *
     * @param distributeModeOdoMap 按照配货模式分组后的出库单集合
     * @param odoCommandMap 波次下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 未匹配周转箱的出库单，待踢出波次
     */
    private List<OdoCommand> packingForSecKillMode(Map<String, List<OdoCommand>> distributeModeOdoMap, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        List<OdoCommand> unmatchedTurnoverBoxOdoList = new ArrayList<>();
        for (String distributeMode : distributeModeOdoMap.keySet()) {
            // 一个配货模式的出库单集合
            List<OdoCommand> batchSecKillOdoList = distributeModeOdoMap.get(distributeMode);
            // 一个配货模式下的出库单按照计数器编码的SKU分成小批次
            Map<String, List<OdoCommand>> skuBatchMap = new HashMap<>();
            // 按照商品分成小批次
            for (OdoCommand odoCommand : batchSecKillOdoList) {
                // 计数器编码中的商品编码
                String skuCode = odoCommand.getCounterCode().substring(odoCommand.getCounterCode().indexOf(CacheKeyConstant.CACHE_KEY_SPLIT));
                List<OdoCommand> batchOdoList = skuBatchMap.get(skuCode);
                if (null == batchOdoList) {
                    batchOdoList = new ArrayList<>();
                    skuBatchMap.put(skuCode, batchOdoList);
                }
                batchOdoList.add(odoCommand);
            }

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);

            for (String skuCode : skuBatchMap.keySet()) {

                List<OdoCommand> skuBatchOdoList = skuBatchMap.get(skuCode);

                while (!skuBatchOdoList.isEmpty()) {
                    List<OdoCommand> batchOdoList = new ArrayList<>();

                    // 划分小批次
                    Iterator<OdoCommand> odoIterator = skuBatchOdoList.iterator();
                    while (odoIterator.hasNext()) {
                        OdoCommand odoCommand = odoIterator.next();
                        odoIterator.remove();
                        batchOdoList.add(odoCommand);
                        if (batchOdoList.size() == rule.getOrdersUpperLimit()) {
                            break;
                        }
                    }
                    // 创建批次号
                    String batchNo = outboundBoxRecManager.getBatchNo(ouId);

                    List<Long> odoIdList = new ArrayList<>();
                    for (OdoCommand odoCommand : batchOdoList) {
                        odoIdList.add(odoCommand.getId());
                    }
                    // 周转箱列表
                    List<Container2ndCategoryCommand> allTurnoverBoxList = this.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId, logId);
                    if (null == allTurnoverBoxList || allTurnoverBoxList.isEmpty()) {
                        unmatchedTurnoverBoxOdoList.addAll(batchOdoList);
                        batchOdoList.clear();
                        continue;
                    }
                    // 小批次下的所有明细
                    List<OdoLineCommand> batchSecKillOdoLineList = odoLineManager.findOdoLineByOdoId(odoIdList, ouId);
                    // 各个明细可用的周转箱列表
                    Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap = this.getOdoLineAvailableTurnoverBoxMap(batchSecKillOdoLineList, allTurnoverBoxList, ouId, logId);

                    // 未移除的明细记录库存表的占用码，用于查询占用的库存记录
                    List<Long> batchOccLineIdList = new ArrayList<>();
                    // 明细集合，方便取数据<odoLineId, odoLineCommand>
                    Map<Long, OdoLineCommand> odoLineCommandMap = new HashMap<>();
                    // 有明细无法匹配周转箱，则出库单踢出波次
                    for (OdoLineCommand odoLine : odoLineAvailableTurnoverBoxListMap.keySet()) {
                        if (null == odoLineAvailableTurnoverBoxListMap.get(odoLine) || odoLineAvailableTurnoverBoxListMap.get(odoLine).isEmpty()) {
                            OdoCommand odoCommand = odoCommandMap.get(odoLine.getOdoId());
                            unmatchedTurnoverBoxOdoList.add(odoCommand);
                            batchOdoList.remove(odoCommand);
                            batchSecKillOdoLineList.remove(odoLine);
                        } else {
                            batchOccLineIdList.add(odoLine.getId());
                            odoLineCommandMap.put(odoLine.getId(), odoLine);
                        }
                    }

                    if (batchSecKillOdoLineList.isEmpty()) {
                        continue;
                    }

                    // 批次下的库存列表
                    List<WhSkuInventoryCommand> batchSkuInventoryList = outboundBoxRecManager.findListByOccLineIdListOrderByPickingSort(batchOccLineIdList, ouId);

                    try {
                        // 周转箱列表
                        List<Container2ndCategoryCommand> packingTurnoverBoxList = this.allocateTurnoverBoxForBatchModeOdo(batchSkuInventoryList, batchSecKillOdoLineList, odoLineCommandMap, odoCommandMap, odoLineAvailableTurnoverBoxListMap, ouId, logId);


                        for (Container2ndCategoryCommand container : packingTurnoverBoxList) {
                            // 创建周转箱
                            Container turnoverBox = this.getUseAbleContainer(container, ouId);
                            if(null == turnoverBox){
                                throw new BusinessException("没有可用周转箱");
                            }

                            List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutBoundBoxCommandList();
                            for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                                // 设置包裹的containerId
                                odoOutboundBox.setContainerId(turnoverBox.getId());
                                odoOutboundBox.setBoxBatch(batchNo);
                            }

                        }
                        // 在一个事务中保存整个小批次的包裹信息
                        outboundBoxRecManager.saveRecOutboundBoxByContainer(packingTurnoverBoxList);
                    } catch (BusinessException be) {
                        log.error("packingForSecKillMode error,batchOdoList is:[{}], e is:[{}], logId is:[{}]", batchOdoList, be, logId);
                        // 整个批次的出库单踢出波次
                        this.releaseOdoFromWave(batchOdoList, Constants.CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION, ouId, logId);
                    }

                }

            }
        }
        return unmatchedTurnoverBoxOdoList;
    }

    /**
     * 拣货模式 按批摘果-主副品 小批次定义：出库单配货模式 + 拣货模式（按批摘果（主副品））+ 主品SKU = 小批次
     *
     * @param distributeModeOdoMap 按照配货模式分组的出库单集合
     * @param odoCommandMap 波次下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 未匹配周转箱的出库单集合
     */
    private List<OdoCommand> packingForMainSkuMode(Map<String, List<OdoCommand>> distributeModeOdoMap, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        List<OdoCommand> unmatchedTurnoverBoxOdoList = new ArrayList<>();
        for (String distributeMode : distributeModeOdoMap.keySet()) {
            List<OdoCommand> batchMainOdoList = distributeModeOdoMap.get(distributeMode);
            // 按照主品sku分组<mainSkuId, List<OdoId>>
            Map<Long, List<Long>> batchMainOdoListMap = new HashMap<>();
            for (OdoCommand odoCommand : batchMainOdoList) {
                // Long mainSkuId =
                // Long.valueOf(odoCommand.getCounterCode().substring(odoCommand.getCounterCode().lastIndexOf(CacheKeyConstant.CACHE_KEY_SPLIT)
                // + 1));
                Long mainSkuId = Long.valueOf(odoCommand.getDistributionCode());
                List<Long> mainSkuOdoIdList = batchMainOdoListMap.get(mainSkuId);
                if (null == mainSkuOdoIdList) {
                    mainSkuOdoIdList = new ArrayList<>();
                    batchMainOdoListMap.put(mainSkuId, mainSkuOdoIdList);
                }
                mainSkuOdoIdList.add(odoCommand.getId());
            }

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);

            for (Long mainSkuId : batchMainOdoListMap.keySet()) {

                // 小批次
                List<Long> mainSkuBatchOdoIdList = batchMainOdoListMap.get(mainSkuId);

                while (!mainSkuBatchOdoIdList.isEmpty()) {
                    List<Long> batchOdoIdList = new ArrayList<>();

                    // 划分小批次
                    Iterator<Long> odoIdIterator = mainSkuBatchOdoIdList.iterator();
                    while (odoIdIterator.hasNext()) {
                        Long odoId = odoIdIterator.next();
                        odoIdIterator.remove();

                        batchOdoIdList.add(odoId);
                        if (batchOdoIdList.size() == rule.getOrdersUpperLimit()) {
                            break;
                        }
                    }

                    // 创建批次号
                    String batchNo = outboundBoxRecManager.getBatchNo(ouId);


                    // 批次下所有明细集合
                    List<OdoLineCommand> batchOdoLineList = odoLineManager.findOdoLineByOdoId(batchOdoIdList, ouId);

                    // 周转箱列表
                    List<Container2ndCategoryCommand> allTurnoverBoxList = this.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId, logId);
                    if (null == allTurnoverBoxList || allTurnoverBoxList.isEmpty()) {
                        for (Long odoId : batchOdoIdList) {
                            OdoCommand odoCommand = odoCommandMap.get(odoId);
                            unmatchedTurnoverBoxOdoList.add(odoCommand);
                            batchMainOdoList.remove(odoCommand);
                        }
                        batchOdoIdList.clear();
                        continue;
                    }
                    // 各个明细可用的周转箱列表
                    Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap = this.getOdoLineAvailableTurnoverBoxMap(batchOdoLineList, allTurnoverBoxList, ouId, logId);
                    // 有明细未匹配上周转箱，出库单下的所有明细移除，出库单踢出波次
                    Set<Long> unmatchedBoxOdoIdList = new HashSet<>();
                    for (OdoLineCommand odoLine : odoLineAvailableTurnoverBoxListMap.keySet()) {
                        if (null == odoLineAvailableTurnoverBoxListMap.get(odoLine) || odoLineAvailableTurnoverBoxListMap.get(odoLine).isEmpty()) {
                            unmatchedBoxOdoIdList.add(odoLine.getOdoId());
                        }
                    }

                    // 未移除的明细记录库存表的占用码，用于查询占用的库存记录
                    List<Long> mainOdoOccLineIdList = new ArrayList<>();
                    List<Long> accessoryOdoOccLineIdList = new ArrayList<>();
                    // 明细集合，方便取数据<odoLineId, odoLineCommand>
                    Map<Long, OdoLineCommand> odoLineCommandMap = new HashMap<>();
                    // 主品明细列表，以防主副品一样的订单<OdoId, OdoLine>
                    Map<Long, OdoLineCommand> mainOdoMap = new HashMap<>();
                    // 副品明细列表
                    Map<Long, OdoLineCommand> accessoryOdoMap = new HashMap<>();
                    // 有明细无法匹配周转箱，则出库单踢出波次
                    Iterator<OdoLineCommand> batchOdoLineIterator = batchOdoLineList.iterator();
                    while (batchOdoLineIterator.hasNext()) {
                        OdoLineCommand odoLineCommand = batchOdoLineIterator.next();
                        if (unmatchedBoxOdoIdList.contains(odoLineCommand.getOdoId())) {
                            batchOdoLineIterator.remove();
                            OdoCommand odoCommand = odoCommandMap.get(odoLineCommand.getOdoId());
                            unmatchedTurnoverBoxOdoList.add(odoCommand);
                            batchMainOdoList.remove(odoCommand);
                        } else {
                            // 未移除的明细记录库存表的占用码，用于查询占用的库存记录
                            if (odoLineCommand.getSkuId().equals(mainSkuId)) {
                                if (!mainOdoMap.containsKey(odoLineCommand.getOdoId())) {
                                    mainOdoOccLineIdList.add(odoLineCommand.getId());
                                    mainOdoMap.put(odoLineCommand.getOdoId(), odoLineCommand);
                                }
                            } else {
                                if (!accessoryOdoMap.containsKey(odoLineCommand.getOdoId())) {
                                    accessoryOdoOccLineIdList.add(odoLineCommand.getId());
                                    accessoryOdoMap.put(odoLineCommand.getOdoId(), odoLineCommand);
                                }
                            }
                            odoLineCommandMap.put(odoLineCommand.getId(), odoLineCommand);
                        }
                    }
                    if (batchOdoLineList.isEmpty()) {
                        continue;
                    }


                    // 周转箱列表
                    List<Container2ndCategoryCommand> packingTurnoverBoxList = new ArrayList<>();

                    // 主品出库单明细集合
                    List<OdoLineCommand> batchMainOdoLineList = new ArrayList<>(mainOdoMap.values());
                    // 副品出库单明细集合
                    List<OdoLineCommand> batchAccessoryOdoLineList = new ArrayList<>(accessoryOdoMap.values());

                    try {
                        // 批次下的主品库存列表
                        List<WhSkuInventoryCommand> batchMainSkuInventoryList = outboundBoxRecManager.findListByOccLineIdListOrderByPickingSort(mainOdoOccLineIdList, ouId);
                        List<Container2ndCategoryCommand> mainOdoPackingTurnoverBoxList =
                                this.allocateTurnoverBoxForBatchModeOdo(batchMainSkuInventoryList, batchMainOdoLineList, odoLineCommandMap, odoCommandMap, odoLineAvailableTurnoverBoxListMap, ouId, logId);

                        // 批次下的副品库存列表
                        List<WhSkuInventoryCommand> batchAccessorySkuInventoryList = outboundBoxRecManager.findListByOccLineIdListOrderByPickingSort(accessoryOdoOccLineIdList, ouId);
                        List<Container2ndCategoryCommand> accessoryOdoPackingTurnoverBoxList =
                                this.allocateTurnoverBoxForBatchModeOdo(batchAccessorySkuInventoryList, batchAccessoryOdoLineList, odoLineCommandMap, odoCommandMap, odoLineAvailableTurnoverBoxListMap, ouId, logId);


                        packingTurnoverBoxList.addAll(mainOdoPackingTurnoverBoxList);
                        packingTurnoverBoxList.addAll(accessoryOdoPackingTurnoverBoxList);


                        for (Container2ndCategoryCommand container : packingTurnoverBoxList) {
                            // 创建周转箱
                            Container turnoverBox = this.getUseAbleContainer(container, ouId);
                            if(null == turnoverBox){
                                throw new BusinessException("没有可用周转箱");
                            }


                            List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutBoundBoxCommandList();
                            for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                                // 设置包裹的containerId
                                odoOutboundBox.setContainerId(turnoverBox.getId());
                                odoOutboundBox.setBoxBatch(batchNo);
                            }

                        }
                        // 在一个事务中保存整个小批次的包裹信息
                        outboundBoxRecManager.saveRecOutboundBoxByContainer(packingTurnoverBoxList);
                    } catch (BusinessException be) {
                        log.error("packingForMainSkuMode error,batchOdoIdList is:[{}], e is:[{}], logId is:[{}]", batchOdoIdList, be, logId);

                        // 整个批次的出库单踢出波次
                        this.releaseOdoFromWave(batchOdoIdList, odoCommandMap.get(batchOdoIdList.get(0)).getWhWaveCommand().getId(), Constants.CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION, ouId, logId);
                    }
                }

            }

        }
        return unmatchedTurnoverBoxOdoList;
    }

    /**
     * 拣货模式 按批摘果-套装 小批次定义：出库单配货模式 + 拣货模式（按批摘果（套装））+ 套装组合编码 = 小批次
     *
     * @param distributeModeOdoMap 按照配货模式分组的出库单集合
     * @param odoCommandMap 波次下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 未匹配周转箱的出库单，待踢出波次
     */
    private List<OdoCommand> packingForSkuGroupMode(Map<String, List<OdoCommand>> distributeModeOdoMap, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        List<OdoCommand> unmatchedTurnoverBoxOdoList = new ArrayList<>();
        for (String distributeMode : distributeModeOdoMap.keySet()) {
            List<OdoCommand> batchGroupDistributeModeOdoList = distributeModeOdoMap.get(distributeMode);
            Map<String, List<OdoCommand>> skuGroupBatchMap = new HashMap<>();
            // 将相同配货模式下的出库单按照套装组合码分组
            for (OdoCommand odoCommand : batchGroupDistributeModeOdoList) {
                String skuGroupCode = odoCommand.getCounterCode().substring(odoCommand.getCounterCode().indexOf(CacheKeyConstant.CACHE_KEY_SPLIT));
                List<OdoCommand> skuGroupOdoList = skuGroupBatchMap.get(skuGroupCode);
                if (null == skuGroupOdoList) {
                    skuGroupOdoList = new ArrayList<>();
                    skuGroupBatchMap.put(skuGroupCode, skuGroupOdoList);
                }
                skuGroupOdoList.add(odoCommand);
            }

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);

            for (String skuGroupCode : skuGroupBatchMap.keySet()) {

                List<OdoCommand> skuGroupBatchOdoList = skuGroupBatchMap.get(skuGroupCode);

                while (!skuGroupBatchOdoList.isEmpty()) {
                    List<OdoCommand> batchOdoList = new ArrayList<>();

                    // 划分小批次
                    Iterator<OdoCommand> odoIterator = skuGroupBatchOdoList.iterator();
                    while (odoIterator.hasNext()) {
                        OdoCommand odoCommand = odoIterator.next();
                        odoIterator.remove();
                        batchOdoList.add(odoCommand);
                        if (batchOdoList.size() == rule.getOrdersUpperLimit()) {
                            break;
                        }
                    }

                    // 创建批次号
                    String batchNo = outboundBoxRecManager.getBatchNo(ouId);

                    List<Long> skuGroupOdoIdList = new ArrayList<>();
                    for (OdoCommand odoCommand : batchOdoList) {
                        skuGroupOdoIdList.add(odoCommand.getId());
                    }
                    // 小批次下的所有明细
                    List<OdoLineCommand> skuGroupBatchOdoLineList = odoLineManager.findOdoLineByOdoId(skuGroupOdoIdList, ouId);
                    // 周转箱列表
                    List<Container2ndCategoryCommand> allTurnoverBoxList = this.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId, logId);
                    if (null == allTurnoverBoxList || allTurnoverBoxList.isEmpty()) {
                        unmatchedTurnoverBoxOdoList.addAll(batchOdoList);
                        batchOdoList.clear();
                        continue;
                    }
                    // 各个明细可用的周转箱列表
                    Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap = this.getOdoLineAvailableTurnoverBoxMap(skuGroupBatchOdoLineList, allTurnoverBoxList, ouId, logId);
                    // 有明细未匹配上周转箱，出库单下的所有明细移除，出库单踢出波次
                    Set<Long> unmatchedBoxOdoIdList = new HashSet<>();
                    for (OdoLineCommand odoLine : odoLineAvailableTurnoverBoxListMap.keySet()) {
                        if (null == odoLineAvailableTurnoverBoxListMap.get(odoLine) || odoLineAvailableTurnoverBoxListMap.get(odoLine).isEmpty()) {
                            unmatchedBoxOdoIdList.add(odoLine.getOdoId());
                        }
                    }



                    // 有明细无法匹配周转箱，则出库单踢出波次
                    Iterator<OdoLineCommand> skuGroupBatchOdoLineIterator = skuGroupBatchOdoLineList.iterator();
                    while (skuGroupBatchOdoLineIterator.hasNext()) {
                        OdoLineCommand odoLineCommand = skuGroupBatchOdoLineIterator.next();
                        if (unmatchedBoxOdoIdList.contains(odoLineCommand.getOdoId())) {
                            skuGroupBatchOdoLineIterator.remove();
                            OdoCommand odoCommand = odoCommandMap.get(odoLineCommand.getOdoId());
                            unmatchedTurnoverBoxOdoList.add(odoCommand);
                            batchGroupDistributeModeOdoList.remove(odoCommand);
                        }
                    }

                    if (skuGroupBatchOdoLineList.isEmpty()) {
                        continue;
                    }



                    // 将明细按照SKU分组，分开放入周转箱<skuId,List<odoLine>>,
                    Map<Long, List<OdoLineCommand>> skuGroupOdoLineListMap = new HashMap<>();
                    for (OdoLineCommand odoLine : skuGroupBatchOdoLineList) {
                        List<OdoLineCommand> skuGroupOdoLineList = skuGroupOdoLineListMap.get(odoLine.getSkuId());
                        if (null == skuGroupOdoLineList) {
                            skuGroupOdoLineList = new ArrayList<>();
                            skuGroupOdoLineListMap.put(odoLine.getSkuId(), skuGroupOdoLineList);
                        }
                        skuGroupOdoLineList.add(odoLine);
                    }

                    try {
                        List<Container2ndCategoryCommand> packingTurnoverBoxList = new ArrayList<>();
                        // 以sku划分放入周转箱
                        for (Long skuId : skuGroupOdoLineListMap.keySet()) {
                            List<OdoLineCommand> skuGroupOdoLineList = skuGroupOdoLineListMap.get(skuId);
                            // 明细集合，方便取数据<odoLineId, odoLineCommand>
                            Map<Long, OdoLineCommand> odoLineCommandMap = new HashMap<>();
                            List<Long> skuGroupOdoLineIdList = new ArrayList<>();
                            for (OdoLineCommand odoLineCommand : skuGroupOdoLineList) {
                                skuGroupOdoLineIdList.add(odoLineCommand.getId());
                                odoLineCommandMap.put(odoLineCommand.getId(), odoLineCommand);
                            }

                            // 批次sku组下的库存列表
                            List<WhSkuInventoryCommand> batchSkuInventoryList = outboundBoxRecManager.findListByOccLineIdListOrderByPickingSort(skuGroupOdoLineIdList, ouId);

                            // 周转箱列表
                            List<Container2ndCategoryCommand> skuBatchTurnoverBoxList = this.allocateTurnoverBoxForBatchModeOdo(batchSkuInventoryList, skuGroupOdoLineList, odoLineCommandMap, odoCommandMap, odoLineAvailableTurnoverBoxListMap, ouId, logId);

                            packingTurnoverBoxList.addAll(skuBatchTurnoverBoxList);
                        }


                        for (Container2ndCategoryCommand container : packingTurnoverBoxList) {
                            // 创建周转箱
                            Container turnoverBox = this.getUseAbleContainer(container, ouId);
                            if(null == turnoverBox){
                                throw new BusinessException("没有可用周转箱");
                            }

                            List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutBoundBoxCommandList();
                            for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                                // 设置包裹的containerId
                                odoOutboundBox.setContainerId(turnoverBox.getId());
                                odoOutboundBox.setBoxBatch(batchNo);
                            }

                        }
                        // 在一个事务中保存整个小批次的包裹信息和创建容器
                        outboundBoxRecManager.saveRecOutboundBoxByContainer(packingTurnoverBoxList);
                    } catch (BusinessException be) {
                        log.error("packingForSkuGroupMode error,batchOdoList is:[{}], e is:[{}], logId is:[{}]", batchOdoList, be, logId);
                        // 整个批次的出库单踢出波次
                        this.releaseOdoFromWave(batchOdoList, Constants.CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION, ouId, logId);
                    }
                }
            }
        }
        return unmatchedTurnoverBoxOdoList;
    }

    /**
     *
     * 拣货模式 播种 小批次定义：出库单配货模式 + 拣货模式（播种）+ 出库单对应拣货区域 + 仓库基础信息（播种墙对应单据数）= 小批次
     *
     * @author mingwei.xie
     * @param distributeModeOdoMap 按照配货模式分组的出库单集合
     * @param odoCommandMap 波次下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 未匹配周转箱的出库单，待踢出波次
     */
    private List<OdoCommand> packingForModeSeed(Map<String, List<OdoCommand>> distributeModeOdoMap, Map<Long, OdoCommand> odoCommandMap, Map<String, WhDistributionPatternRule> distributionPatternRuleMap, Long ouId, String logId) {
        List<OdoCommand> unmatchedTurnoverBoxOdoList = new ArrayList<>();
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        // 播种墙单据数
        Integer seedingOdoQty = warehouse.getSeedingOdoQty();
        // 小批次定义：出库单配货模式 + 拣货模式（播种）+ 出库单对应拣货区域 + 仓库基础信息（播种墙对应单据数）
        for (String distributeMode : distributeModeOdoMap.keySet()) {

            // 配货模式规则, <distributionPatternCode, rule>
            //Map<String, WhDistributionPatternRule> distributionPatternRuleMap = this.getDistributionPatternRule(ouId);
            WhDistributionPatternRule rule = distributionPatternRuleMap.get(distributeMode);
            if (rule.getOrdersUpperLimit() < seedingOdoQty) {
                // 按照小的限制划分批次
                seedingOdoQty = rule.getOrdersUpperLimit();
            }

            List<OdoCommand> batchSeedDistributeModeOdoList = distributeModeOdoMap.get(distributeMode);
            // 按照出库单涉及的区域将出库单分组<areaIdList, List<Odo>>
            Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap = this.odoGroupByAllocateArea(batchSeedDistributeModeOdoList, ouId, logId);
            // 按区域分组后的areaId列表集合，为了查询子集用
            List<List<Long>> allocateAreaIdListCollection = new ArrayList<>(allocateAreaOdoListMap.keySet());

            // 将区域列表按照数量降序排序
            this.orderAllocateAreaListByListSize(allocateAreaIdListCollection);
            // 遍历按照数量降序排序后的区域列表
            for (List<Long> allocateAreaIdList : allocateAreaIdListCollection) {
                // 相同批次中涉及相同区域的出库单
                List<OdoCommand> sameAreaOdoList = allocateAreaOdoListMap.get(allocateAreaIdList);
                if (sameAreaOdoList.isEmpty()) {
                    continue;
                }

                while (!sameAreaOdoList.isEmpty()) {
                    List<Long> batchOdoIdList = new ArrayList<>();

                    // 涉及区域最多的出库单列表，开始分配小批次，从集合中分配指定数量的出库单到批次集合
                    this.seedBatchAssort(sameAreaOdoList, batchOdoIdList, seedingOdoQty);

                    // 涉及区域最多的出库单列表不够分配小批次，先从区域子集找出库单补充小批次
                    if (batchOdoIdList.size() < seedingOdoQty) {
                        // 优先从区域子集的出库单列表分配出库单到小批次
                        this.seedBatchAssortBySubAreaList(allocateAreaIdListCollection, allocateAreaOdoListMap, allocateAreaIdList, batchOdoIdList, seedingOdoQty);
                    }

                    // 子集也无法分配完小批次，从非子集的列表中分配来补充小批次
                    if (batchOdoIdList.size() < seedingOdoQty) {
                        // 从非区域子集的出库单列表分配出库单到小批次
                        this.seedBatchAssortForNonSubAreaList(allocateAreaIdListCollection, allocateAreaOdoListMap, batchOdoIdList, seedingOdoQty);
                    }
                    // 执行到这里，也许小批次名额已分配完毕，也许所有出库单都分完也没有超过小批次的限制

                    // 创建批次号
                    String batchNo = outboundBoxRecManager.getBatchNo(ouId);
                    // 周转箱列表
                    List<Container2ndCategoryCommand> allTurnoverBoxList = this.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId, logId);
                    if (null == allTurnoverBoxList || allTurnoverBoxList.isEmpty()) {
                        for (Long odoId : batchOdoIdList) {
                            OdoCommand unmatchedBoxOdo = odoCommandMap.get(odoId);
                            unmatchedTurnoverBoxOdoList.add(unmatchedBoxOdo);
                        }
                        continue;
                    }
                    // 小批次下的所有明细
                    List<OdoLineCommand> batchSeedOdoLineList = odoLineManager.findOdoLineByOdoId(batchOdoIdList, ouId);

                    // 播种的周转箱装箱完毕一起保存装箱信息
                    // 出库单分配的整箱容器列表
                    List<ContainerCommand> odoPackedWholeCaseList = new ArrayList<>();
                    // 出库单分配的整托容器列表
                    List<ContainerCommand> odoPackedWholeTrayList = new ArrayList<>();

                    // 播种的整托整箱处理，如果明细是整托整箱，不分配周转箱
                    this.packingWholeCaseLineForModeSeed(batchSeedOdoLineList, odoCommandMap, odoPackedWholeCaseList, odoPackedWholeTrayList, ouId);

                    // 各个明细可用的周转箱列表
                    Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap = this.getOdoLineAvailableTurnoverBoxMap(batchSeedOdoLineList, allTurnoverBoxList, ouId, logId);
                    // 有明细未匹配上周转箱，出库单下的所有明细移除，出库单踢出波次
                    Set<Long> unmatchedBoxOdoIdList = new HashSet<>();
                    for (OdoLineCommand odoLine : odoLineAvailableTurnoverBoxListMap.keySet()) {
                        if (null == odoLineAvailableTurnoverBoxListMap.get(odoLine) || odoLineAvailableTurnoverBoxListMap.get(odoLine).isEmpty()) {
                            unmatchedBoxOdoIdList.add(odoLine.getOdoId());
                        }
                    }


                    // 移除已装箱的整箱数据
                    Iterator<ContainerCommand> odoPackedWholeCaseIterator = odoPackedWholeCaseList.iterator();
                    while (odoPackedWholeCaseIterator.hasNext()) {
                        ContainerCommand container = odoPackedWholeCaseIterator.next();
                        if (unmatchedBoxOdoIdList.contains(container.getOdoOutboundBoxCommandList().get(0).getOdoId())) {
                            odoPackedWholeCaseIterator.remove();
                        }
                    }

                    // 移除已装箱的整托数据
                    Iterator<ContainerCommand> odoPackedWholeTrayIterator = odoPackedWholeTrayList.iterator();
                    while (odoPackedWholeTrayIterator.hasNext()) {
                        ContainerCommand container = odoPackedWholeTrayIterator.next();
                        if (unmatchedBoxOdoIdList.contains(container.getOdoOutboundBoxCommandList().get(0).getOdoId())) {
                            odoPackedWholeTrayIterator.remove();
                        }
                    }

                    // 未移除的明细记录库存表的占用码，用于查询占用的库存记录
                    List<Long> batchOccLineIdList = new ArrayList<>();
                    // 明细集合，方便取数据<odoLineId, odoLineCommand>
                    Map<Long, OdoLineCommand> odoLineCommandMap = new HashMap<>();
                    // 有明细无法匹配周转箱，则出库单踢出波次
                    Iterator<OdoLineCommand> batchSeedOdoLineIterator = batchSeedOdoLineList.iterator();
                    while (batchSeedOdoLineIterator.hasNext()) {
                        OdoLineCommand odoLineCommand = batchSeedOdoLineIterator.next();
                        if (unmatchedBoxOdoIdList.contains(odoLineCommand.getOdoId())) {
                            batchSeedOdoLineIterator.remove();
                        } else {
                            // 未移除的明细记录库存表的占用码，用于查询占用的库存记录
                            batchOccLineIdList.add(odoLineCommand.getId());
                            odoLineCommandMap.put(odoLineCommand.getId(), odoLineCommand);
                        }
                    }

                    // 移除相应的出库单
                    for (Long removeOdoId : unmatchedBoxOdoIdList) {
                        OdoCommand odoCommand = odoCommandMap.get(removeOdoId);
                        unmatchedTurnoverBoxOdoList.add(odoCommand);
                        // 分配进小批次的时候就已经移除了
                        batchSeedDistributeModeOdoList.remove(odoCommand);
                    }

                    if (odoPackedWholeCaseList.isEmpty() && odoPackedWholeTrayList.isEmpty() && batchOccLineIdList.isEmpty()) {
                        continue;
                    }

                    // 周转箱列表
                    List<Container2ndCategoryCommand> packingTurnoverBoxList = new ArrayList<>();

                    // 批次下的库存列表
                    List<WhSkuInventoryCommand> batchSkuInventoryList = new ArrayList<>();

                    if( !batchOccLineIdList.isEmpty()) {
                        batchSkuInventoryList = outboundBoxRecManager.findListByOccLineIdListOrderByPickingSort(batchOccLineIdList, ouId);
                    }
                    try {
                        packingTurnoverBoxList = this.allocateTurnoverBoxForBatchModeOdo(batchSkuInventoryList, batchSeedOdoLineList, odoLineCommandMap, odoCommandMap, odoLineAvailableTurnoverBoxListMap, ouId, logId);


                        for (Container2ndCategoryCommand container : packingTurnoverBoxList) {
                            // 创建周转箱
                            Container turnoverBox = this.getUseAbleContainer(container, ouId);
                            if(null == turnoverBox){
                                throw new BusinessException("没有可用周转箱");
                            }

                            List<WhOdoOutBoundBoxCommand> odoOutBoundBoxCommandList = container.getOdoOutBoundBoxCommandList();
                            for (WhOdoOutBoundBoxCommand odoOutboundBox : odoOutBoundBoxCommandList) {
                                // 设置包裹的containerId
                                odoOutboundBox.setContainerId(turnoverBox.getId());
                                odoOutboundBox.setBoxBatch(batchNo);
                            }

                        }
                        if (null != odoPackedWholeTrayList) {
                            for (ContainerCommand wholeTrayContainer : odoPackedWholeTrayList) {
                                for (WhOdoOutBoundBoxCommand odoOutBoundBox : wholeTrayContainer.getOdoOutboundBoxCommandList()) {
                                    odoOutBoundBox.setBoxBatch(batchNo);
                                }
                            }
                        }
                        if (null != odoPackedWholeCaseList) {
                            for (ContainerCommand wholeCaseContainer : odoPackedWholeCaseList) {
                                for (WhOdoOutBoundBoxCommand odoOutBoundBox : wholeCaseContainer.getOdoOutboundBoxCommandList()) {
                                    odoOutBoundBox.setBoxBatch(batchNo);
                                }
                            }
                        }
                        // 还有整托整箱的包裹信息
                        // 在一个事务中保存整个小批次的包裹信息
                        outboundBoxRecManager.saveRecOutboundBoxForSeedBatch(packingTurnoverBoxList, odoPackedWholeCaseList, odoPackedWholeTrayList);
                    } catch (BusinessException be) {
                        log.error("packingForModeSeed error,batchOdoIdList is:[{}], e is:[{}], logId is:[{}]", batchOdoIdList, be, logId);

                        // 整个批次的出库单踢出波次
                        this.releaseOdoFromWave(batchOdoIdList, odoCommandMap.get(batchOdoIdList.get(0)).getWhWaveCommand().getId(), Constants.CREATE_OUTBOUND_CARTON_REC_BOX_EXCEPTION, ouId, logId);
                    }
                }
            }
        }


        return unmatchedTurnoverBoxOdoList;
    }

    private List<Container2ndCategoryCommand> allocateTurnoverBoxForBatchModeOdo(List<WhSkuInventoryCommand> batchSkuInventoryList, List<OdoLineCommand> batchOdoLineList, Map<Long, OdoLineCommand> odoLineCommandMap, Map<Long, OdoCommand> odoCommandMap,
            Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap, Long ouId, String logId) {

        // 小批次下使用的周转箱列表，全部装箱完毕之后再统一创建容器，保存装箱信息
        List<Container2ndCategoryCommand> batchTurnoverBoxList = new ArrayList<>();

        while (!batchSkuInventoryList.isEmpty()) {
            // 获取第一条库存记录的出库单明细，以获得明细对应可用的周转箱集合
            OdoLineCommand availableBoxOdoLine = odoLineCommandMap.get(batchSkuInventoryList.get(0).getOccupationLineId());
            if (!batchOdoLineList.contains(availableBoxOdoLine)) {
                // 如果批次中的明细已被移除，说明明细已经装箱完毕，但是相应的库存记录没有移除完毕，正常情况应该不会出现
                // 避免已经没有出库单明细，库存却没有移除完毕而进入无限循环，此处先检查，再做一次补充删除
                batchSkuInventoryList.remove(batchSkuInventoryList.get(0));
                continue;
            }

            // 获取第一个库存记录可用的周转箱集合
            List<Container2ndCategoryCommand> odoLineAvailableBoxList = odoLineAvailableTurnoverBoxListMap.get(availableBoxOdoLine);
            Container2ndCategoryCommand availableTurnoverBoxType;
            // 每一次重新计算使用哪个箱子
            if (1 == odoLineAvailableBoxList.size()) {
                availableTurnoverBoxType = odoLineAvailableBoxList.get(0);
            } else {
                // 此处查找可用的周转箱箱依然是尝试将批次内的所有商品装在一个箱子，因为无法确认哪些明细一定放在一个箱子，即使一个明细也可能被拆分在多个箱子
                availableTurnoverBoxType = this.getAvailableTurnoverBox(batchOdoLineList, odoLineAvailableBoxList, ouId, logId);
            }

            Container2ndCategoryCommand newBox = new Container2ndCategoryCommand();
            BeanUtils.copyProperties(availableTurnoverBoxType, newBox);
            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            // 记录该箱子中各个明细装了多少数量<odoLineId, outboundBox>
            Map<Long, WhOdoOutBoundBoxCommand> odoLineOutBoundBoxMap = new HashMap<>();
            // 为了获取第一个放入箱子的商品属性记录
            WhSkuInventoryCommand firstSkuInventory = batchSkuInventoryList.get(0);
            boolean isMixAllowed = false;
            String mixAttr = null;
            Long fistSkuId = null;

            // 记录箱子中已存放的商品属性<skuId, UUID>，此处其实只有播种的模式才需要
            Map<Long, String> skuAttrMap = new HashMap<>();

            ListIterator<WhSkuInventoryCommand> batchSkuInventoryIterator = batchSkuInventoryList.listIterator();
            // 依次将可以放入箱子的库存放入周转箱
            while (batchSkuInventoryIterator.hasNext()) {
                // 第一个库存记录的商品一定可以放入箱子，因为可用的箱子集合就是该商品的可用箱子集合
                WhSkuInventoryCommand skuInventoryCommand = batchSkuInventoryIterator.next();
                OdoLineCommand packingOdoLine = odoLineCommandMap.get(skuInventoryCommand.getOccupationLineId());

                // 出库单明细的商品
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(skuInventoryCommand.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                SkuMgmt skuMgmt = skuRedisCommandTemp.getSkuMgmt();
                if (firstSkuInventory.equals(skuInventoryCommand)) {
                    isMixAllowed = skuMgmt.getIsMixAllowed();
                    mixAttr = skuMgmt.getMixAttr();
                    fistSkuId = skuInventoryCommand.getSkuId();

                    if (isMixAllowed) {
                        assert !StringUtil.isEmpty(mixAttr);
                    }
                }
                if (isMixAllowed && skuMgmt.getIsMixAllowed() && !mixAttr.equals(skuMgmt.getMixAttr())) {
                    continue;
                }
                if ((!isMixAllowed || !skuMgmt.getIsMixAllowed()) && !skuInventoryCommand.getSkuId().equals(fistSkuId)) {
                    continue;
                }
                // 判断商品边长是否符合的计算器
                SimpleCubeCalculator boxAvailableCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
                boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isSkuAvailable = boxAvailableCalculator.calculateAvailable();
                //boolean isSkuAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                if (!isSkuAvailable) {
                    // 商品边长不合适，下一个明细,该处不移除，需要在下一次遍历中找出合适的箱子
                    continue;
                }
                String uuId = skuAttrMap.get(skuInventoryCommand.getSkuId());
                if (null != uuId && !uuId.equals(skuInventoryCommand.getUuid())) {
                    continue;
                }
                // 商品是否放进了容器，是则需要记录容器的商品属性
                boolean isSkuAvailablePacking = false;
                OdoCommand odoCommand = odoCommandMap.get(packingOdoLine.getOdoId());
                for (int odoLineSkuQty = packingOdoLine.getQty().intValue(); odoLineSkuQty > 0; odoLineSkuQty--) {
                    // 累加填充体积，如果判断为无法放入，需要扣除相应的体积
                    outboundBoxCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                    boolean isBoxAvailable = outboundBoxCalculator.calculateAvailable();
                    if (!isBoxAvailable) {
                        // 商品无法放入，减去填充体积，因为在累加的时候已经先行累加
                        Double stuffVolume = outboundBoxCalculator.getStuffVolume();
                        Double skuVolume = outboundBoxCalculator.getCurrentStuffVolume();
                        outboundBoxCalculator.setVolume(stuffVolume - skuVolume);
                        break;
                    }
                    // 记录商品可以放入容器
                    isSkuAvailablePacking = true;

                    // 商品可放入，明细扣减相应的数量
                    WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = odoLineOutBoundBoxMap.get(packingOdoLine.getId());
                    if (null == odoOutBoundBoxCommand) {
                        odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                        odoOutBoundBoxCommand.setQty(0.0);
                        odoOutBoundBoxCommand.setOuId(ouId);
                        odoOutBoundBoxCommand.setOdoId(packingOdoLine.getOdoId());
                        odoOutBoundBoxCommand.setOdoLineId(packingOdoLine.getId());
                        // odoOutBoundBoxCommand.setContainerId(newBox.getId());
                        odoOutBoundBoxCommand.setWaveId(odoCommand.getWhWaveCommand().getId());
                        odoOutBoundBoxCommand.setIsCreateWork(false);
                        // 添加到记录明细装箱数量的map中
                        odoLineOutBoundBoxMap.put(packingOdoLine.getId(), odoOutBoundBoxCommand);
                    }
                    // 明细包裹数加1
                    odoOutBoundBoxCommand.setQty(odoOutBoundBoxCommand.getQty() + 1);
                    // 明细商品数减1
                    packingOdoLine.setQty((double) odoLineSkuQty - 1);
                    skuInventoryCommand.setOnHandQty(skuInventoryCommand.getOnHandQty() - 1);
                    if (skuInventoryCommand.getOnHandQty() <= 0) {
                        // 该条库存记录的商品已装箱完，操作下一条库存记录
                        break;
                    }
                }
                if (null == uuId && isSkuAvailablePacking) {
                    skuAttrMap.put(skuInventoryCommand.getSkuId(), skuInventoryCommand.getUuid());
                }
                if (0 == packingOdoLine.getQty()) {
                    // 明细数为0，则明细拣货完毕，相关的所有库存移除
                    // 正常情况下，如果占用的数量刚好等于出库单明细数量，则出库单分配到最后，一定是操作最后一条库存，即使多占用了，也不会多出一条完全用不到的库存，所以，此处的库存记录则是占用的最后一条
                    batchSkuInventoryIterator.remove();
                    batchOdoLineList.remove(packingOdoLine);
                }
                if (skuInventoryCommand.getOnHandQty() <= 0 && 0 != packingOdoLine.getQty()) {
                    // 库存数为0，库位的商品已装箱完毕，移除该库存记录
                    batchSkuInventoryIterator.remove();
                }
            }
            newBox.setOdoOutBoundBoxCommandList(new ArrayList<>(odoLineOutBoundBoxMap.values()));
            batchTurnoverBoxList.add(newBox);
        }
        if (!batchOdoLineList.isEmpty()) {
            // 执行到此，如果明细列表不为空，则有明细未完全分配，则是占用的库存少了
            throw new BusinessException("库存占用不足");
        }

        return batchTurnoverBoxList;
    }

    /**
     * 子集也无法分配完小批次，从非子集的列表中分配来补充小批次
     *
     * @param allocateAreaIdListCollection 区域列表集合
     * @param allocateAreaOdoListMap 同区域的出库单集合
     * @param batchOdoIdList 已分配到批次的出库单集合
     * @param seedingOdoQty 播种墙单据数
     */
    private void seedBatchAssortForNonSubAreaList(List<List<Long>> allocateAreaIdListCollection, Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap, List<Long> batchOdoIdList, Integer seedingOdoQty) {
        for (List<Long> noSubAreaIdList : allocateAreaIdListCollection) {
            List<OdoCommand> subOdoComList = allocateAreaOdoListMap.get(noSubAreaIdList);
            if (subOdoComList.isEmpty()) {
                continue;
            }

            this.seedBatchAssort(subOdoComList, batchOdoIdList, seedingOdoQty);
            if (batchOdoIdList.size() == seedingOdoQty) {
                break;
            }
        }
    }

    /**
     * 相同区域的出库单不够分配小批次，从区域子集的出库单列表分配出库单到小批次
     *
     * @param allocateAreaIdListCollection 区域集合列表
     * @param allocateAreaOdoListMap 出库单区域集合
     * @param parentAreaIdList 已分配到批次的区域集合
     * @param batchOdoIdList 已分配到批次的出库单
     * @param seedingOdoQty 播种墙单据数
     */
    private void seedBatchAssortBySubAreaList(List<List<Long>> allocateAreaIdListCollection, Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap, List<Long> parentAreaIdList, List<Long> batchOdoIdList, Integer seedingOdoQty) {
        for (List<Long> subAreaIdList : allocateAreaIdListCollection) {
            if (!parentAreaIdList.containsAll(subAreaIdList)) {
                // 判断是不是区域子集
                continue;
            }

            // 该处可能是已加入小批次的集合，但是列表已经为空，前面分配时已从列表移除
            List<OdoCommand> subOdoComList = allocateAreaOdoListMap.get(subAreaIdList);
            if (subOdoComList.isEmpty()) {
                continue;
            }

            // 执行到该处的出库单列表，涉及区域一定是已分配小批次的子集
            this.seedBatchAssort(subOdoComList, batchOdoIdList, seedingOdoQty);
            if (batchOdoIdList.size() == seedingOdoQty) {
                // 如果小批次的出库单数已分配完，不需要再查找子集
                break;
            }
        }
    }

    /**
     * 播种批次分类 涉及区域最多的出库单列表，开始分配小批次，从集合中分配指定数量的出库单到批次集合
     *
     * @param odoCommandList 待分配到批次中的出库单集合
     * @param batchOdoIdList 记录批次中出库单
     * @param seedingOdoQty 播种墙单据数
     */
    private void seedBatchAssort(List<OdoCommand> odoCommandList, List<Long> batchOdoIdList, int seedingOdoQty) {
        Iterator<OdoCommand> odoIterator = odoCommandList.iterator();
        while (odoIterator.hasNext()) {
            OdoCommand odoCommand = odoIterator.next();
            odoIterator.remove();
            batchOdoIdList.add(odoCommand.getId());
            if (batchOdoIdList.size() == seedingOdoQty) {
                break;
            }
        }
    }

    private void fillTrolleyForUnPackingOdo(Container2ndCategoryCommand trolley, List<Long> allocateAreaIdList, List<List<Long>> allocateAreaIdListCollection, Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap, String batchNo, Long trolleyId,
            Long ouId, String logId) {
        for (List<Long> subAreaIdList : allocateAreaIdListCollection) {
            if (!allocateAreaIdList.containsAll(subAreaIdList)) {
                continue;
            }
            // 相同批次中涉及相同区域的出库单
            List<OdoCommand> subOdoComList = allocateAreaOdoListMap.get(subAreaIdList);
            if (subOdoComList.isEmpty()) {
                continue;
            }
            // 将小车填满，记录小车可用货格数
            Iterator<OdoCommand> subOdoIterator = subOdoComList.iterator();
            while (subOdoIterator.hasNext()) {
                OdoCommand subOdoCommand = subOdoIterator.next();
                List<WhOdoOutBoundBoxCommand> odoBoxList = new ArrayList<>();
                boolean isSubTrolleyAvailable = this.packingIntoTrolley(trolley, subOdoCommand, odoBoxList, ouId, logId);
                if (!isSubTrolleyAvailable) {
                    // 小车不可用，换下一个出库单
                    continue;
                }
                for (WhOdoOutBoundBoxCommand odoBox : odoBoxList) {
                    // 设置包裹的批次号
                    odoBox.setBoxBatch(batchNo);
                    // 设置包裹的outContainerId
                    odoBox.setOuterContainerId(trolleyId);
                }

                // 保存包裹数据到数据库
                outboundBoxRecManager.saveRecOutboundBoxForTrolleyPackedOdo(odoBoxList);
                subOdoIterator.remove();
                if (trolley.getAssignedGridNum() == trolley.getTotalGridNum()) {
                    // 小车货格已填满
                    break;
                }
            }// end-while 相同区域下的出库单列表
            if (trolley.getAssignedGridNum() == trolley.getTotalGridNum()) {
                // 小车货格已填满
                break;
            }
        }// end-for 遍历区域列表
    }

    /**
     * 按照分配区域将拣货模式分组
     *
     * @param distributeModeOdoList 同一配货模式下的出库单集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return Map<List<areaId>, List<OdoCommand>> 涉及相同区域的出库单集合
     */
    private Map<List<Long>, List<OdoCommand>> odoGroupByAllocateArea(List<OdoCommand> distributeModeOdoList, Long ouId, String logId) {
        // 记录哪些出库单涉及相同的区域集合
        Map<List<Long>, List<OdoCommand>> allocateAreaOdoListMap = new HashMap<>();
        // 分别统计每个出库单的区域
        Iterator<OdoCommand> distributeModeOdoIterator = distributeModeOdoList.iterator();
        while (distributeModeOdoIterator.hasNext()) {
            OdoCommand odoCommand = distributeModeOdoIterator.next();

            // 出库单涉及的区域ID集合
            Set<Long> odoAllocateAreaIdSet = new HashSet<>();
            // 根据明细ID查询库存表对应的占用的库存记录，获得分配区域
            List<OdoLineCommand> odoLineCommandList = odoLineManager.findOdoLineCommandListByOdoId(odoCommand.getId(), odoCommand.getOuId());
            // 获取出库单的库存记录
            List<WhSkuInventoryCommand> skuInventoryList = outboundBoxRecManager.findListByOccupationCode(odoCommand.getOdoCode(), ouId);
            // 出库单明细占用的库存记录<odoLineId, List<skuInv>>
            Map<Long, List<WhSkuInventoryCommand>> odoLineIdSkuInvMap = new HashMap<>();
            for (WhSkuInventoryCommand skuInventory : skuInventoryList) {
                // 库存占用明细ID就是出库单明细ID
                List<WhSkuInventoryCommand> skuInvList = odoLineIdSkuInvMap.get(skuInventory.getOccupationLineId());
                if (null == skuInvList) {
                    skuInvList = new ArrayList<>();
                    odoLineIdSkuInvMap.put(skuInventory.getOccupationLineId(), skuInvList);
                }
                skuInvList.add(skuInventory);
            }
            try {
                // 封装该出库单涉及的分配区域
                for (OdoLineCommand odoLineCommand : odoLineCommandList) {
                    List<WhSkuInventoryCommand> skuInvList = odoLineIdSkuInvMap.get(odoLineCommand.getId());
                    if (null == skuInvList || skuInvList.isEmpty()) {
                        // 出库单明细不可能没有占用的库存
                        throw new BusinessException("数据异常");
                    }
                    for (WhSkuInventoryCommand skuInv : skuInvList) {
                        odoAllocateAreaIdSet.add(skuInv.getLocationId());
                    }
                }
            } catch (BusinessException be) {
                log.error("odoLine occupy skuInventory error, exception is:[{}], logId is:[{}]", be, logId);
                // 踢出波次
                Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
                whWaveManager.deleteWaveLinesAndReleaseInventoryByOdoId(odoCommand.getWhWaveCommand().getId(), odoCommand.getId(), Constants.CREATE_OUTBOUND_CARTON_OCC_INVENTORY_ERROR, warehouse);
                distributeModeOdoIterator.remove();
                continue;
            }
            // 出库单涉及的分配区域列表
            List<Long> allocateAreaIdList = new ArrayList<>(odoAllocateAreaIdSet);
            // 出库单记录库存列表
            odoCommand.setSkuInventoryCommandList(skuInventoryList);
            // 将区域ID排序
            Collections.sort(allocateAreaIdList);
            // 出库单记录涉及分配区域列表
            odoCommand.setAllocateAreaIdList(allocateAreaIdList);
            // 涉及相同区域的出库单列表
            List<OdoCommand> areaGroupOdoList = allocateAreaOdoListMap.get(allocateAreaIdList);
            if (null == areaGroupOdoList) {
                areaGroupOdoList = new ArrayList<>();
                allocateAreaOdoListMap.put(allocateAreaIdList, areaGroupOdoList);
            }
            areaGroupOdoList.add(odoCommand);
        }// end-for 同一批次下的出库单
        return allocateAreaOdoListMap;
    }


    /**
     * 将区域列表按照数量降序排序
     *
     * @param allocateAreaIdListCollection 分配区域列表
     */
    private void orderAllocateAreaListByListSize(List<List<Long>> allocateAreaIdListCollection) {
        Collections.sort(allocateAreaIdListCollection, new Comparator<List<Long>>() {
            @Override
            public int compare(List<Long> longs, List<Long> t1) {
                if (t1.size() > longs.size()) {
                    return 1;
                }
                if (t1.size() < longs.size()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    private void odoListOrderByBoxNumDesc(List<OdoCommand> odoCommandList) {
        // 将出库单按照出库箱数降序排序
        Collections.sort(odoCommandList, new Comparator<OdoCommand>() {
            @Override
            public int compare(OdoCommand odoCommand, OdoCommand t1) {
                int originBoxNum = 0;
                if (null != odoCommand.getOutboundBoxList()) {
                    originBoxNum += odoCommand.getOutboundBoxList().size();
                }
                if (null != odoCommand.getWholeCaseList()) {
                    originBoxNum += odoCommand.getWholeCaseList().size();
                }
                int t1BoxNum = 0;
                if (null != t1.getOutboundBoxList()) {
                    originBoxNum += t1.getOutboundBoxList().size();
                }
                if (null != t1.getWholeCaseList()) {
                    originBoxNum += t1.getWholeCaseList().size();
                }

                if (t1BoxNum > originBoxNum) {
                    return 1;
                }
                if (t1BoxNum < originBoxNum) {
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * 获取长度单位转换率
     *
     * @author mingwei.xie
     * @return 长度单位转换率集合
     */
    private Map<String, Double> getLenUomConversionRate() {
        Map<String, Double> lenUomConversionRate = new HashMap<>();
        List<UomCommand> lenUomCmds = outboundBoxRecManager.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        // TODO 放入缓存
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        return lenUomConversionRate;
    }


    private List<OutInvBoxTypeCommand> getOdoStoreBoxList(OdoCommand odoCommand, Long ouId) {
        List<WhOutInventoryboxRelationship> outInventoryboxRelationshipList = outboundBoxRecManager.getOutInvBoxRelationshipByType(Constants.OUTBOUNDBOX_RELATIONSHIP_TYPE_STORE, odoCommand.getStoreId(), ouId);
        // 出库单头店铺的出库箱列表
        List<OutInvBoxTypeCommand> odoStoreBoxList = new ArrayList<>();
        if (null != outInventoryboxRelationshipList && !outInventoryboxRelationshipList.isEmpty()) {
            for (WhOutInventoryboxRelationship relationship : outInventoryboxRelationshipList) {
                OutInvBoxTypeCommand odoLineOdoOutboundBox = outboundBoxTypeManager.findOutInventoryBoxType(relationship.getOutInventoryBoxId(), ouId);
                if (null != odoLineOdoOutboundBox && BaseModel.LIFECYCLE_NORMAL.equals(odoLineOdoOutboundBox.getLifecycle())) {
                    odoStoreBoxList.add(odoLineOdoOutboundBox);
                }
            }
        }
        return odoStoreBoxList;
    }

    private List<OutInvBoxTypeCommand> getOdoCustomerBoxList(OdoCommand odoCommand, Long ouId) {
        // 查询出库单头客户配置的出库箱
        List<WhOutInventoryboxRelationship> outInventoryboxRelationshipList = outboundBoxRecManager.getOutInvBoxRelationshipByType(Constants.OUTBOUNDBOX_RELATIONSHIP_TYPE_CUSTOMER, odoCommand.getCustomerId(), ouId);
        // 出库单头客户的出库箱列表
        List<OutInvBoxTypeCommand> odoCustomerBoxList = new ArrayList<>();
        if (null != outInventoryboxRelationshipList && !outInventoryboxRelationshipList.isEmpty()) {
            for (WhOutInventoryboxRelationship relationship : outInventoryboxRelationshipList) {
                OutInvBoxTypeCommand odoLineOdoOutboundBox = outboundBoxTypeManager.findOutInventoryBoxType(relationship.getOutInventoryBoxId(), ouId);
                if (null != odoLineOdoOutboundBox && BaseModel.LIFECYCLE_NORMAL.equals(odoLineOdoOutboundBox.getLifecycle())) {
                    odoCustomerBoxList.add(odoLineOdoOutboundBox);
                }
            }
        }
        return odoCustomerBoxList;
    }

    private List<OutInvBoxTypeCommand> getOdoGeneralBoxList(Long ouId) {
        // 都未配置，则查询通用出库箱
        List<WhOutInventoryboxRelationship> outInventoryboxRelationshipList = outboundBoxRecManager.getGeneralRelationship(ouId);
        // 通用出库箱列表，即未指定店铺和客户
        List<OutInvBoxTypeCommand> odoGeneralBoxList = new ArrayList<>();
        if (null != outInventoryboxRelationshipList && !outInventoryboxRelationshipList.isEmpty()) {
            for (WhOutInventoryboxRelationship relationship : outInventoryboxRelationshipList) {
                OutInvBoxTypeCommand odoLineOdoOutboundBox = outboundBoxTypeManager.findOutInventoryBoxType(relationship.getOutInventoryBoxId(), ouId);
                if (null != odoLineOdoOutboundBox && BaseModel.LIFECYCLE_NORMAL.equals(odoLineOdoOutboundBox.getLifecycle())) {
                    odoGeneralBoxList.add(odoLineOdoOutboundBox);
                }
            }
        }
        // if (odoGeneralBoxList.isEmpty()) {
        // throw new BusinessException("通用出库箱未配置");
        // }
        return odoGeneralBoxList;
    }

    private Map<OdoLineCommand, List<OutInvBoxTypeCommand>> getOdoLineAvailableBoxMap(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> boxList, Long ouId, String logId) {
        // 出库单明细可用出库箱列表
        Map<OdoLineCommand, List<OutInvBoxTypeCommand>> odoLineAvailableBoxListMap = null;
        for (OutInvBoxTypeCommand box : boxList) {
            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(box.getLength(), box.getWidth(), box.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            for (OdoLineCommand odoLine : odoLineList) {
                // 出库单明细的商品
                SkuRedisCommand skuRedisCommand = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommand.getSku();
                // 只是判断商品边长，所以每次重新初始化箱子占用体积，相当于使用新箱子判断
                outboundBoxCalculator.initStuffCube(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isBoxAvailable = outboundBoxCalculator.calculateAvailable();
                if (isBoxAvailable) {
                    if (null == odoLineAvailableBoxListMap) {
                        odoLineAvailableBoxListMap = new HashMap<>();
                    }
                    List<OutInvBoxTypeCommand> odoLineAvailableBoxList = odoLineAvailableBoxListMap.get(odoLine);
                    if (null == odoLineAvailableBoxList) {
                        odoLineAvailableBoxList = new ArrayList<>();
                        odoLineAvailableBoxListMap.put(odoLine, odoLineAvailableBoxList);
                    }
                    odoLineAvailableBoxList.add(box);
                }
            }
        }
        return odoLineAvailableBoxListMap;
    }

    /**
     *
     *
     * @param odoLineList 小批次下的所有出库单的明细集合
     * @param boxList 周转箱集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 各个明细对应的可用的周转箱集合
     */
    private Map<OdoLineCommand, List<Container2ndCategoryCommand>> getOdoLineAvailableTurnoverBoxMap(List<OdoLineCommand> odoLineList, List<Container2ndCategoryCommand> boxList, Long ouId, String logId) {
        // 出库单明细可用出库箱列表
        Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableBoxListMap = new HashMap<>();
        for (Container2ndCategoryCommand box : boxList) {
            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(box.getLength(), box.getWidth(), box.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            for (OdoLineCommand odoLine : odoLineList) {
                List<Container2ndCategoryCommand> odoLineAvailableBoxList = odoLineAvailableBoxListMap.get(odoLine);
                if (null == odoLineAvailableBoxList) {
                    odoLineAvailableBoxList = new ArrayList<>();
                    odoLineAvailableBoxListMap.put(odoLine, odoLineAvailableBoxList);
                }

                // 出库单明细的商品
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                outboundBoxCalculator.initStuffCube(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isBoxAvailable = outboundBoxCalculator.calculateAvailable();
                if (isBoxAvailable) {
                    odoLineAvailableBoxList.add(box);
                }
            }
        }
        return odoLineAvailableBoxListMap;
    }

    private void packingMultiSkuForMultiTurnoverBox(List<OdoLineCommand> odoLineList, List<Container2ndCategoryCommand> packingTurnoverBoxList, Map<OdoLineCommand, List<Container2ndCategoryCommand>> odoLineAvailableTurnoverBoxListMap,
            OdoCommand odoCommand, Long ouId, String logId) {
        // 一次循环则使用一个新箱子
        while (!odoLineList.isEmpty()) {
            List<Container2ndCategoryCommand> firstOdoLineAvailableBoxList = odoLineAvailableTurnoverBoxListMap.get(odoLineList.get(0));
            // 记录应该使用的出库箱
            Container2ndCategoryCommand availableTurnoverBoxType;
            // 每一次重新计算使用哪个箱子
            if (1 == firstOdoLineAvailableBoxList.size()) {
                availableTurnoverBoxType = firstOdoLineAvailableBoxList.get(0);
            } else {
                // TODO 考虑混放属性
                availableTurnoverBoxType = this.getAvailableTurnoverBox(odoLineList, firstOdoLineAvailableBoxList, ouId, logId);
            }

            Container2ndCategoryCommand newBox = new Container2ndCategoryCommand();
            BeanUtils.copyProperties(availableTurnoverBoxType, newBox);

            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            // 记录该箱子中各个明细装了多少数量<odoLineId, outboundBox>
            Map<Long, WhOdoOutBoundBoxCommand> odoLineOutBoundBoxMap = new HashMap<>();
            // 为了获取第一个放入箱子的商品属性
            OdoLineCommand firstOdoLine = odoLineList.get(0);
            boolean isMixAllowed = false;
            String mixAttr = null;
            Long firstSkuId = null;

            ListIterator<OdoLineCommand> odoLineIterator = odoLineList.listIterator();
            while (odoLineIterator.hasNext()) {
                // 一轮遍历结束，不管箱子有没有装满，已经没有可以放进去的商品
                OdoLineCommand odoLine = odoLineIterator.next();
                // 出库单明细的商品
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                SkuMgmt skuMgmt = skuRedisCommandTemp.getSkuMgmt();
                if (firstOdoLine.equals(odoLine)) {
                    // 第一个明细的商品一定可以放进周转箱
                    isMixAllowed = skuMgmt.getIsMixAllowed();
                    mixAttr = skuMgmt.getMixAttr();
                    firstSkuId = odoLine.getSkuId();

                    if (isMixAllowed) {
                        assert !StringUtil.isEmpty(mixAttr);
                    }
                }
                if (isMixAllowed && skuMgmt.getIsMixAllowed() && !mixAttr.equals(skuMgmt.getMixAttr())) {
                    continue;
                }
                if ((!isMixAllowed || !skuMgmt.getIsMixAllowed()) && !odoLine.getSkuId().equals(firstSkuId)) {
                    continue;
                }
                // 判断商品边长是否符合的计算器
                SimpleCubeCalculator boxAvailableCalculator = new SimpleCubeCalculator(newBox.getLength(), newBox.getWidth(), newBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
                boxAvailableCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                boolean isSkuAvailable = boxAvailableCalculator.calculateAvailable();
                //boolean isSkuAvailable = boxAvailableCalculator.calculateLengthAvailable(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                if (!isSkuAvailable) {
                    // 商品边长不合适，下一个明细,该处不移除，需要在下一次遍历中找出合适的箱子
                    continue;
                }
                // 将明细的商品尽可能多的放入周转箱
                this.packingSkuIntoTurnoverBox(odoLine, outboundBoxCalculator, sku, odoLineOutBoundBoxMap, odoCommand, ouId, logId);
                // 明细最后一个商品已装入出库箱，该明细不需要再分配出库箱
                if (0 == odoLine.getQty()) {
                    odoLineIterator.remove();
                }

            }
            // 箱子不为空的
            if (!odoLineOutBoundBoxMap.isEmpty()) {
                // 将周转箱的包裹记录到周转箱
                newBox.setOdoOutBoundBoxCommandList(new ArrayList<>(odoLineOutBoundBoxMap.values()));
                // 记录出库单使用的所有周转箱，在外层一个事物中保存装箱数据
                packingTurnoverBoxList.add(newBox);
            }
        }
    }

    /**
     * 在出库单明细列表第一个明细可使用的出库箱集合中查找使用的出库箱， 查找逻辑：将箱子按照体积降序排序，当找到一个出库箱无法容纳所有明细商品时，
     * 如果该出库箱不是第一个，则使用上一个出库箱，否则使用当前出库箱， 无法放入出库箱的原因可能是出库箱体积不够，也可能是商品尺寸不合适
     */
    private OutInvBoxTypeCommand getAvailableBox(List<OdoLineCommand> odoLineList, List<OutInvBoxTypeCommand> firstOdoLineAvailableBoxList, Long ouId, String logId) {
        OutInvBoxTypeCommand availableBox = null;

        // 将按照体积降序排序
        Collections.sort(firstOdoLineAvailableBoxList, new Comparator<OutInvBoxTypeCommand>() {
            @Override
            public int compare(OutInvBoxTypeCommand outInvBoxTypeCommand, OutInvBoxTypeCommand t1) {
                return t1.getVolume().compareTo(outInvBoxTypeCommand.getVolume());
            }
        });

        // 可以容纳所有商品的出库箱集合，之后按照体积排序，取最小体积的出库箱
        List<OutInvBoxTypeCommand> availableBoxList = new ArrayList<>();
        for (OutInvBoxTypeCommand testBox : firstOdoLineAvailableBoxList) {
            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(testBox.getLength(), testBox.getWidth(), testBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            boolean isBoxAvailable = true;
            for (OdoLineCommand odoLine : odoLineList) {
                // 出库单明细的商品
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                // 放入明细所有数量的商品
                for (int odoLineSkuQty = odoLine.getQty().intValue(); odoLineSkuQty > 0; odoLineSkuQty--) {
                    // 累加填充体积
                    outboundBoxCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                    isBoxAvailable = outboundBoxCalculator.calculateAvailable();
                    if (!isBoxAvailable) {
                        break;
                    }
                }// end-for 放入明细所有数量的商品
                if (!isBoxAvailable) {
                    break;
                }
            }// end-for 遍历所有明细
            if (isBoxAvailable) {
                availableBoxList.add(testBox);
            }
        }
        if (!availableBoxList.isEmpty()) {
            // 取体积最小的
            availableBox = availableBoxList.get(availableBoxList.size() - 1);
        } else {
            // 没有一个出库箱可以容纳所有商品，取第一个体积最大的出库箱，尽可能多的将第一个明细装完
            availableBox = firstOdoLineAvailableBoxList.get(0);
        }
        return availableBox;
    }

    /**
     * 获取最小的可容纳所有明细的周转箱，没有，则获取最大体积的周转箱
     *
     * @param odoLineList 待装箱的明细列表
     * @param firstOdoLineAvailableBoxList 第一个明细可用的周转箱集合
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 可用的周转箱
     */
    private Container2ndCategoryCommand getAvailableTurnoverBox(List<OdoLineCommand> odoLineList, List<Container2ndCategoryCommand> firstOdoLineAvailableBoxList, Long ouId, String logId) {
        Container2ndCategoryCommand availableBox = null;

        // 将按照体积降序排序
        Collections.sort(firstOdoLineAvailableBoxList, new Comparator<Container2ndCategoryCommand>() {
            @Override
            public int compare(Container2ndCategoryCommand box, Container2ndCategoryCommand t1) {
                return t1.getVolume().compareTo(box.getVolume());
            }
        });

        // <editor-fold desc="Description">
        // 可以容纳所有商品的周转箱箱集合，之后按照体积排序，取最小体积的出库箱
        List<Container2ndCategoryCommand> availableBoxList = new ArrayList<>();
        for (Container2ndCategoryCommand testBox : firstOdoLineAvailableBoxList) {
            // 出库箱是否可用计算器
            SimpleCubeCalculator outboundBoxCalculator = new SimpleCubeCalculator(testBox.getLength(), testBox.getWidth(), testBox.getHigh(), SimpleCubeCalculator.SYS_UOM, Constants.OUTBOUND_BOX_AVAILABILITY, this.getLenUomConversionRate());
            boolean isBoxAvailable = true;
            for (OdoLineCommand odoLine : odoLineList) {
                // 出库单明细的商品
                SkuRedisCommand skuRedisCommandTemp = skuRedisManager.findSkuMasterBySkuId(odoLine.getSkuId(), ouId, logId);
                Sku sku = skuRedisCommandTemp.getSku();
                // 放入明细所有数量的商品
                for (int odoLineSkuQty = odoLine.getQty().intValue(); odoLineSkuQty > 0; odoLineSkuQty--) {
                    // 累加填充体积
                    outboundBoxCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
                    isBoxAvailable = outboundBoxCalculator.calculateAvailable();
                    if (!isBoxAvailable) {
                        break;
                    }
                }// end-for 放入明细所有数量的商品
                if (!isBoxAvailable) {
                    break;
                }
            }// end-for 遍历所有明细
            if (isBoxAvailable) {
                availableBoxList.add(testBox);
            }
        }
        if (!availableBoxList.isEmpty()) {
            // 取体积最小的
            availableBox = availableBoxList.get(availableBoxList.size() - 1);
        } else {
            // 没有一个出库箱可以容纳所有商品，取第一个体积最大的出库箱，尽可能多的将第一个明细装完
            availableBox = firstOdoLineAvailableBoxList.get(0);
        }
        return availableBox;
        // </editor-fold>
    }

    /**
     * 获取二级容器类型是小车的容器，按照货格数降序排序
     *
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 小车容器列表
     */
    private List<Container2ndCategoryCommand> getTrolleyListOrderByGridNumDesc(Long ouId, String logId) {
        // String cacheKey =
        // CacheKeyConstant.CREATE_OUTBOUND_CARTON_TROLLEY_LIST_ORDER_BY_GRID_NUM_DESC_PREFIX +
        // ouId;
        // List<Container2ndCategoryCommand> trolleyListCache ;
        //
        // try {
        // trolleyListCache = cacheManager.getObject(cacheKey);
        // } catch (Exception e) {
        // log.error("getTrolleyListOrderByGridNumDesc cacheManager.getObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("小车类二级容器缓存读取错误");
        // }
        // if (null == trolleyListCache || trolleyListCache.isEmpty()) {
        // List<Container2ndCategoryCommand> trolleyList =
        // outboundBoxRecManager.getTrolleyListOrderByGridNumDesc(ouId);
        // try {
        // cacheManager.setObject(cacheKey, trolleyList, CacheKeyConstant.CACHE_ONE_DAY);
        // } catch (Exception e) {
        // log.error("getTrolleyListOrderByGridNumDesc cacheManager.setObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("小车类二级容器缓存写入缓存错误");
        // }
        // }
        // try {
        // trolleyListCache = cacheManager.getObject(cacheKey);
        // } catch (Exception e) {
        // log.error("getTrolleyListOrderByGridNumDesc cacheManager.getObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("小车类二级容器缓存读取错误");
        // }
        // if (null == trolleyListCache || trolleyListCache.isEmpty()) {
        // log.warn("cache getTrolleyListOrderByGridNumDesc, data is null, logId is:[{}]", log);
        // }
        List<Container2ndCategoryCommand> trolleyList = outboundBoxRecManager.getTrolleyListOrderByGridNumDesc(ouId);

        return trolleyList;
    }

    /**
     * 获取周转箱列表，按照体积降序排序
     *
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     * @return 按照体积降序排序后的二级容器类型是周转箱的集合
     */
    private List<Container2ndCategoryCommand> getTurnoverBoxByOuIdOrderByVolumeDesc(Long ouId, String logId) {
        // String cacheKey =
        // CacheKeyConstant.CREATE_OUTBOUND_CARTON_TURNOVERBOX_ORDER_BY_VOLUME_DESC_PREFIX + ouId;
        // List<Container2ndCategoryCommand> turnoverBoxListCache;
        //
        // try {
        // turnoverBoxListCache = cacheManager.getObject(cacheKey);
        // } catch (Exception e) {
        // log.error("getTurnoverBoxByOuIdOrderByVolumeDesc cacheManager.getObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("周转箱类二级容器缓存读取错误");
        // }
        // if (null == turnoverBoxListCache || turnoverBoxListCache.isEmpty()) {
        // List<Container2ndCategoryCommand> turnoverBoxList =
        // outboundBoxRecManager.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId);
        // try {
        // cacheManager.setObject(cacheKey, turnoverBoxList, CacheKeyConstant.CACHE_ONE_DAY);
        // } catch (Exception e) {
        // log.error("getTurnoverBoxByOuIdOrderByVolumeDesc cacheManager.setObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("周转箱类二级容器缓存写入缓存错误");
        // }
        // }
        // try {
        // turnoverBoxListCache = cacheManager.getObject(cacheKey);
        // } catch (Exception e) {
        // log.error("getTurnoverBoxByOuIdOrderByVolumeDesc cacheManager.getObject error, exception is:[{}], logOd is:[{}]",
        // e, logId);
        // throw new BusinessException("周转箱类二级容器缓存读取错误");
        // }
        // if (null == turnoverBoxListCache || turnoverBoxListCache.isEmpty()) {
        // log.warn("cache getTurnoverBoxByOuIdOrderByVolumeDesc, data is null, logId is:[{}]",
        // log);
        // }
        List<Container2ndCategoryCommand> turnoverBoxList = outboundBoxRecManager.getTurnoverBoxByOuIdOrderByVolumeDesc(ouId);
        return turnoverBoxList;
    }

    /**
     * 将商品装入周周转箱
     *
     * @param odoLine 出库单明细
     * @param boxCalculator 容器体积计算器
     * @param sku 商品
     * @param lineBoxMap 明细对应的包裹集合
     * @param odoCommand 出库单
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     */
    private void packingSkuIntoTurnoverBox(OdoLineCommand odoLine, SimpleCubeCalculator boxCalculator, Sku sku, Map<Long, WhOdoOutBoundBoxCommand> lineBoxMap, OdoCommand odoCommand, Long ouId, String logId) {
        // 累计填充体积
        for (int odoLineSkuQty = odoLine.getQty().intValue(); odoLineSkuQty > 0; odoLineSkuQty--) {
            // 累加填充体积，如果判断为无法放入，需要扣除相应的体积
            boxCalculator.accumulationStuffVolume(sku.getLength(), sku.getWidth(), sku.getHeight(), SimpleCubeCalculator.SYS_UOM);
            boolean isBoxAvailable = boxCalculator.calculateAvailable();
            if (!isBoxAvailable) {
                // 商品无法放入，减去填充体积，因为在累加的时候已经先行累加
                Double stuffVolume = boxCalculator.getStuffVolume();
                Double skuVolume = boxCalculator.getCurrentStuffVolume();
                boxCalculator.setVolume(stuffVolume - skuVolume);
                break;
            }
            // 商品可放入，明细扣减相应的数量
            WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = lineBoxMap.get(odoLine.getId());
            if (null == odoOutBoundBoxCommand) {
                odoOutBoundBoxCommand = new WhOdoOutBoundBoxCommand();
                odoOutBoundBoxCommand.setQty(0.0);
                odoOutBoundBoxCommand.setOuId(ouId);
                odoOutBoundBoxCommand.setOdoId(odoLine.getOdoId());
                odoOutBoundBoxCommand.setOdoLineId(odoLine.getId());
                odoOutBoundBoxCommand.setIsCreateWork(false);
                // 应该是新创建的周转箱
                // odoOutBoundBoxCommand.setContainerId(targetBox.getId());
                odoOutBoundBoxCommand.setWaveId(odoCommand.getWhWaveCommand().getId());
                // 添加到记录明细装箱数量的map中
                lineBoxMap.put(odoLine.getId(), odoOutBoundBoxCommand);
            }
            // 明细包裹数加1
            odoOutBoundBoxCommand.setQty(odoOutBoundBoxCommand.getQty() + 1);
            // 明细商品数减1
            odoLine.setQty((double) odoLineSkuQty - 1);
        }
    }

    /**
     * 定时任务的操作人
     *
     * @return 操作人ID
     */
    private Long getUserId() {
        return 1L;
    }

    /**
     * 根据二级容器类型创建容器
     *
     * @param availableContainer 二级容器类型
     * @param ouId 仓库组织ID
     * @return 新建的容器对象
     */
    private Container getUseAbleContainer(Container2ndCategoryCommand availableContainer, Long ouId) {
        if(null == availableContainer){
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        Container searchContainer = new Container();
        searchContainer.setOneLevelType(availableContainer.getOneLevelType());
        searchContainer.setTwoLevelType(availableContainer.getId());
        searchContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        searchContainer.setOuId(ouId);
        searchContainer.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
        List<Container> containerList = outboundBoxRecManager.findUseAbleContainerByContainerType(searchContainer);
        Container useAbleContainer = null;
        if(null != containerList && !containerList.isEmpty()){
            useAbleContainer = containerList.get(0);
            useAbleContainer.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            useAbleContainer.setStatus(ContainerStatus.CONTAINER_STATUS_REC_OUTBOUNDBOX);
            outboundBoxRecManager.occupationContainerByRecOutboundBox(useAbleContainer);
        }

        return useAbleContainer;
    }

    /**
     * 踢出波次
     *
     * @param unmatchedTurnoverBoxOdoList 未匹配周转箱的出库单列表
     * @param reason 踢出波次的原因
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     */
    private void releaseOdoFromWave(List<OdoCommand> unmatchedTurnoverBoxOdoList, String reason, Long ouId, String logId) {
        List<Long> unmatchedTurnoverBoxOdoIdList = new ArrayList<>();
        for (OdoCommand odoCommand : unmatchedTurnoverBoxOdoList) {
            unmatchedTurnoverBoxOdoIdList.add(odoCommand.getId());
        }
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        outboundBoxRecManager.releaseOdoFromWave(unmatchedTurnoverBoxOdoList.get(0).getWhWaveCommand().getId(), unmatchedTurnoverBoxOdoIdList, reason, warehouse, logId);
    }

    /**
     * 踢出波次
     *
     * @param odoIdList 出库单ID集合
     * @param waveId 波次ID
     * @param reason 踢出波次的原因
     * @param ouId 仓库组织ID
     * @param logId 日志ID
     */
    private void releaseOdoFromWave(List<Long> odoIdList, Long waveId, String reason, Long ouId, String logId) {

        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        outboundBoxRecManager.releaseOdoFromWave(waveId, odoIdList, reason, warehouse, logId);
    }
}
