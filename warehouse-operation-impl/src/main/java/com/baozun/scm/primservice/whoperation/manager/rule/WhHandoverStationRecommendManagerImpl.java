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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.HandoverCollectionConditionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.HandoverCollectionRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhHandoverStationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.HandoverApplyType;
import com.baozun.scm.primservice.whoperation.constant.HandoverCollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.HandoverGroupCondition;
import com.baozun.scm.primservice.whoperation.constant.HandoverStationType;
import com.baozun.scm.primservice.whoperation.dao.handover.HandoverCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.handover.WhHandoverStationDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.HandoverCollectionConditionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.HandoverCollectionRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ma.DistributionTargetDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollection;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhHandoverStation;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.DistributionTarget;

/**
 * @author lichuan
 * 
 */
@Service("whHandoverStationRecommendManager")
@Transactional
public class WhHandoverStationRecommendManagerImpl extends BaseManagerImpl implements WhHandoverStationRecommendManager {
    protected static final Logger log = LoggerFactory.getLogger(WhHandoverStationRecommendManagerImpl.class);

    @Autowired
    private HandoverCollectionConditionDao handoverCollectionConditionDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private HandoverCollectionDao handoverCollectionDao;
    @Autowired
    private WhHandoverStationDao whHandoverStationDao;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private DistributionTargetDao distributionTargetDao;
    @Autowired
    private HandoverCollectionRuleDao handoverCollectionRuleDao;
    @Autowired
    private WhOutboundboxDao whOutboundboxDao;
    @Autowired
    private WhWaveDao whWaveDao;
    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    /**
     * @author lichuan
     * @param ruleAffer
     * @param ruleList
     * @param outboundboxCommand
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    @Override
    public WhHandoverStationCommand recommendHandoverStationByRule(RuleAfferCommand ruleAffer, List<HandoverCollectionRuleCommand> ruleList, WhOutboundboxCommand outboundboxCommand, Long ouId, Long userId, String logId) {
        WhHandoverStationCommand handoverStationCommand = new WhHandoverStationCommand();
        if (log.isInfoEnabled()) {
            log.info("whHandoverStationRecommandManager.recommendHandoverStationByRule start, logId is:[{}]", logId);
        }
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("handoverCollectionRule is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.HAND_OVER_COLLECTION_RULE_IS_NULL);
        }
        if (null == outboundboxCommand) {
            log.error("outboundboxCommand is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NULL);
        }
        Long odoId = outboundboxCommand.getOdoId();
        if (null == odoId) {
            log.error("odo is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
        }
        WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
        if (null == odo) {
            log.error("odo is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
        }
        // 交接工位
        List<HandoverCollection> handoverCollections = null;
        Long handoverStationId = null;
        String handoverStationType = "";
        String groupCondition = "";
        List<HandoverCollectionConditionCommand> condition = null;
        for (HandoverCollectionRuleCommand rule : ruleList) {
            Long ruleId = rule.getId();
            if (null == ruleId) {
                continue;
            }
            // 根据集货交接规则找到分组条件
            List<HandoverCollectionConditionCommand> conditionList = handoverCollectionConditionDao.findConditionListByRuleIdAndouId(ruleId, ouId);
            if (null != conditionList && conditionList.size() > 0) {
                condition = conditionList;
                for (HandoverCollectionConditionCommand condtion : conditionList) {
                    if (HandoverGroupCondition.STROE.equals(condtion.getRuleCondtionCode())) {
                        if (StringUtils.isEmpty(groupCondition)) {
                            if (!StringUtils.isEmpty(outboundboxCommand.getStoreCode()))
                                groupCondition += outboundboxCommand.getStoreCode();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + outboundboxCommand.getStoreCode();
                        }
                    }
                    if (HandoverGroupCondition.CUSTOMER.equals(condtion.getRuleCondtionCode())) {
                        if (StringUtils.isEmpty(groupCondition)) {
                            if (!StringUtils.isEmpty(outboundboxCommand.getCustomerCode()))
                                groupCondition += outboundboxCommand.getCustomerCode();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + outboundboxCommand.getCustomerCode();
                        }
                    }
                    if (HandoverGroupCondition.ORDER_TYPE.equals(condtion.getRuleCondtionCode())) {
                        if (StringUtils.isEmpty(groupCondition)) {
                            if (!StringUtils.isEmpty(odo.getOdoType()))
                                groupCondition += odo.getOdoType();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + odo.getOdoType();
                        }
                    }
                    if (HandoverGroupCondition.TRANSPORT_CODE.equals(condtion.getRuleCondtionCode())) {
                        if (StringUtils.isEmpty(groupCondition)) {
                            if (!StringUtils.isEmpty(outboundboxCommand.getTransportCode()))
                                groupCondition += outboundboxCommand.getTransportCode();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + outboundboxCommand.getTransportCode();
                        }
                    }
                    if (HandoverGroupCondition.TIME_EFFECT_TYPE.equals(condtion.getRuleCondtionCode())) {
                        if (StringUtils.isEmpty(groupCondition)) {
                            if (!StringUtils.isEmpty(outboundboxCommand.getTimeEffectCode()))
                                groupCondition += outboundboxCommand.getTimeEffectCode();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + outboundboxCommand.getTimeEffectCode();
                        }
                    }
                    if (HandoverGroupCondition.LINE_INFO.equals(condtion.getRuleCondtionCode())) {
                        WhOdoTransportMgmt whOdoTransportMgmt = whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoId, ouId);
                        DistributionTarget distributionTarget = distributionTargetDao.findDistributionTargetByCode(whOdoTransportMgmt.getOutboundTarget());
                        if (StringUtils.isEmpty(groupCondition)) {
                            // 线路信息

                            if (null != distributionTarget && !StringUtils.isEmpty(distributionTarget.getLineInfo()))
                                groupCondition += distributionTarget.getLineInfo();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + distributionTarget.getLineInfo();
                        }
                    }
                    // if
                    // (HandoverGroupCondition.TRANSPORT_VAS.equals(condtion.getRuleCondtionCode()))
                    // {
                    // if (StringUtils.isEmpty(groupCondition)) {
                    // if (!StringUtils.isEmpty(outboundboxCommand.getTimeEffectCode()))
                    // groupCondition += outboundboxCommand.getTimeEffectCode();
                    // else
                    // continue;
                    // } else {
                    // groupCondition += "_" + outboundboxCommand.getTimeEffectCode();
                    // }
                    // }
                    if (HandoverGroupCondition.TRANSPORT_MODE.equals(condtion.getRuleCondtionCode())) {
                        WhOdoTransportMgmt whOdoTransportMgmt = whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoId, ouId);
                        if (StringUtils.isEmpty(groupCondition)) {
                            // 运输服务
                            if (null != whOdoTransportMgmt && !StringUtils.isEmpty(whOdoTransportMgmt.getModeOfTransport()))
                                groupCondition += whOdoTransportMgmt.getModeOfTransport();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + whOdoTransportMgmt.getModeOfTransport();
                        }
                    }
                    if (HandoverGroupCondition.WAVE_CODE.equals(condtion.getRuleCondtionCode())) {
                        if (StringUtils.isEmpty(groupCondition)) {
                            if (!StringUtils.isEmpty(outboundboxCommand.getWaveCode()))
                                groupCondition += outboundboxCommand.getWaveCode();
                            else
                                continue;
                        } else {
                            groupCondition += "_" + outboundboxCommand.getWaveCode();
                        }
                    }
                }
                if (StringUtils.isNotEmpty(groupCondition)) {
                    handoverCollections = handoverCollectionDao.findByGroupCondition(groupCondition, ouId);
                }
            }


            // 根据分组条件找交接集货表中是否存在满足条件的交接工位
            if (null == handoverCollections || 0 == handoverCollections.size()) {

                if (rule.getRule() != null && outboundboxCommand.getOutboundboxCode() != null) {
                    // 推荐成功的handovercollection保存条件
                    List<Long> executeRuleSql = handoverCollectionRuleDao.executeRuleSql(rule.getRuleSql().replace(Constants.HANDOVER_COLLECTION_RULE_PLACEHOLDER, "'" + outboundboxCommand.getOutboundboxCode() + "'"), ouId);
                    if (executeRuleSql.size() <= 0) {
                        // 运行规则sql可以的就下一步
                        continue;
                    }
                    // 判断集货交接应用类型
                    String applyType = rule.getApplyType();
                    if (HandoverApplyType.DESIGNATED_STATION.equals(applyType)) {
                        // 取到规则上指定的交接工位
                        handoverStationType = rule.getRuleType();
                        handoverStationId = rule.getHandoverStationId();
                        break;
                    } else {
                        // 系统推荐
                        handoverStationType = rule.getRuleType();
                        if (HandoverStationType.CHECKING_GROUP_HANDOVER_STATION.equals(handoverStationType)) {
                            // 直接找该复核台下的可以用下交接工位
                            if (null == ruleAffer) {
                                continue;
                            }
                            Long checkingFacilityId = ruleAffer.getCheckingFacilityId();
                            if (null == checkingFacilityId) {
                                // 如果出库箱没绑定复核台找一个可用的仓库下交接工位
                                List<WhHandoverStationCommand> stations = whHandoverStationDao.findOneByOuId(ouId);
                                if (null == stations || 0 == stations.size()) {
                                    continue;
                                } else {
                                    for (WhHandoverStationCommand station : stations) {

                                        if (null != station) {

                                            // 同波茨放一起 不論上限
                                            if (null != condition) {
                                                if (condition.size() == 1 && HandoverGroupCondition.WAVE_CODE.equals(condition.get(0).getRuleCondtionCode())) {
                                                    handoverStationId = station.getId();
                                                    break;
                                                }
                                            }
                                            // 判断该工位是否已经装满出库箱
                                            // 当前出库箱数
                                            Integer capacity = handoverCollectionDao.findCountByHandoverStationIdAndStatus(station.getId(), Constants.HANDOVER_COLLECTION_TO_HANDOVER, ouId);
                                            if (capacity >= station.getUpperCapacity()) {
                                                continue;
                                            }

                                            handoverStationId = station.getId();
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            List<WhHandoverStationCommand> stations = whHandoverStationDao.findOneByFacilityGroupId(checkingFacilityId, ouId);
                            if (null == stations || 0 == stations.size()) {
                                continue;
                            } else {
                                for (WhHandoverStationCommand station : stations) {

                                    if (null != station) {

                                        // 同波茨放一起 不論上限
                                        if (null != condition) {
                                            if (condition.size() == 1 && HandoverGroupCondition.WAVE_CODE.equals(condition.get(0).getRuleCondtionCode())) {
                                                handoverStationId = station.getId();
                                                break;
                                            }
                                        }
                                        // 判断该工位是否已经装满出库箱
                                        // 当前出库箱数
                                        Integer capacity = handoverCollectionDao.findCountByHandoverStationIdAndStatus(station.getId(), Constants.HANDOVER_COLLECTION_TO_HANDOVER, ouId);
                                        if (capacity >= station.getUpperCapacity()) {
                                            continue;
                                        }
                                        handoverStationId = station.getId();
                                        break;
                                    }
                                }
                                break;
                            }
                        } else {
                            // 找一个可用的仓库下交接工位
                            List<WhHandoverStationCommand> stations = whHandoverStationDao.findOneByOuId(ouId);
                            if (null == stations || 0 == stations.size()) {
                                continue;
                            } else {
                                for (WhHandoverStationCommand station : stations) {

                                    if (null != station) {

                                        // 同波茨放一起 不論上限
                                        if (null != condition) {
                                            if (condition.size() == 1 && HandoverGroupCondition.WAVE_CODE.equals(condition.get(0).getRuleCondtionCode())) {
                                                handoverStationId = station.getId();
                                                break;
                                            }
                                        }
                                        // 判断该工位是否已经装满出库箱
                                        // 当前出库箱数
                                        Integer capacity = handoverCollectionDao.findCountByHandoverStationIdAndStatus(station.getId(), Constants.HANDOVER_COLLECTION_TO_HANDOVER, ouId);
                                        if (capacity >= station.getUpperCapacity()) {
                                            continue;
                                        }

                                        handoverStationId = station.getId();
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            } else {
                // 取到已经推荐的交接工位
                HandoverCollection hc = handoverCollections.get(0);
                if (null != hc) {
                    handoverStationId = hc.getHandoverStationId();
                    break;
                }
            }
        }
        if (null == handoverStationId) {
            // 推荐交接工位失败
            log.error("handover station recommend fail, outboundbox is:[{}], logId is:[{}]", outboundboxCommand.getOutboundboxCode(), logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_OUTBOUND_IS_NULL);
        }
        WhHandoverStation handoverStation = whHandoverStationDao.findById(handoverStationId);
        if (null == handoverStation) {
            log.error("handover station is not exists error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_OUTBOUND_ERROR);
        }
        // 更新交接集货表
        HandoverCollection handoverCollection = handoverCollectionDao.findByOutboundboxCode(outboundboxCommand.getOutboundboxCode(), ouId);
        if (null == handoverCollection) {
            handoverCollection = new HandoverCollection();
            handoverCollection.setGroupCondition(groupCondition);
            handoverCollection.setHandoverBatch("");
            handoverCollection.setHandoverStationId(handoverStationId);
            handoverCollection.setHandoverStationType(handoverStationType);
            handoverCollection.setHandoverStatus(HandoverCollectionStatus.TO_HANDOVER);// 交接状态
            handoverCollection.setOuId(ouId);
            handoverCollection.setOutboundboxCode(outboundboxCommand.getOutboundboxCode());
            handoverCollection.setOutboundboxId(outboundboxCommand.getOutboundboxId());
            handoverCollection.setCreateId(userId);
            handoverCollection.setCreateTime(new Date());
            handoverCollectionDao.insert(handoverCollection);
        } else {
            handoverCollection.setLastModifyTime(new Date());
            handoverCollection.setModifiedId(userId);
            handoverCollection.setHandoverStationId(handoverStationId);
            handoverCollection.setHandoverStatus(HandoverCollectionStatus.TO_HANDOVER);// 交接状态
            handoverCollectionDao.saveOrUpdateByVersion(handoverCollection);
        }
        // 计算交接工位上限
        Integer upperCapacity = handoverStation.getUpperCapacity();
        if (null == upperCapacity) {
            log.error("handover station upperCapacity is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.HANDOVER_STATION_IS_NULL);
        }
        // 获取当前交接工位上的所有集货交接信息
        List<HandoverCollection> hcList = handoverCollectionDao.findByHandoverStation(handoverStation.getId(), ouId);
        if (null != hcList && hcList.size() > 0) {
            if (upperCapacity.intValue() <= hcList.size()) {
                // 达到交接工位上限，生成交接批次并提示可交接
                String handoverBatch = this.codeManager.generateCode(Constants.WMS, Constants.HANDOVER_BATCH, null, null, null);// 补货编码
                // 更新交接批次号
                for (HandoverCollection hc : hcList) {
                    hc.setHandoverBatch(handoverBatch);
                    handoverCollectionDao.saveOrUpdateByVersion(hc);
                }
                if (null != condition) {
                    if (condition.size() == 1 && HandoverGroupCondition.WAVE_CODE.equals(condition.get(0).getRuleCondtionCode())) {
                        // 规则是且只是波次时
                        // 根據波次去庫存表查詢是否都符合（符合完才會生成出庫箱並沒有内外部容器了） 如果 都沒了 根據出庫箱去交接集貨查是否都交接了 沒有的話不提示交接
                        WhOutboundboxCommand outboundBox = whOutboundboxDao.findByOutboundBoxCode(outboundboxCommand.getOutboundboxCode(), ouId);
                        if (StringUtils.isNotEmpty(outboundBox.getWaveCode())) {
                            WhWave wave = new WhWave();
                            wave.setCode(outboundBox.getWaveCode());
                            wave.setAllocatePhase(1);
                            List<WhWave> waves = whWaveDao.findListByParam(wave);
                            if (waves.size() == 0 || null == waves) {
                                wave.setAllocatePhase(0);
                                waves = whWaveDao.findListByParam(wave);
                            }
                            List<String> odocodeList = whWaveLineDao.findOdocodeByWaveId(waves.get(0).getId(), ouId);
                            List<WhSkuInventory> skuInventories = whSkuInventoryDao.isAllChecked(odocodeList);
                            if (skuInventories.size() < 1) {
                                // 全部复核了 再判断是否全部交接
                                List<String> OutboundBoxCodeList = whOutboundboxDao.findOutboundBoxCodeListByWaveCode(waves.get(0).getCode(), ouId);
                                // 获取了所有出库箱 去交接集货查是否都在了
                                List<HandoverCollection> handoveredBoxCollections = handoverCollectionDao.findByOutboundBoxCodeList(OutboundBoxCodeList, ouId);
                                if (handoveredBoxCollections.size() == OutboundBoxCodeList.size()) {
                                    // 全部交接
                                    // 提示可以执行交接
                                    handoverStationCommand.setCode(handoverStation.getCode());
                                    handoverStationCommand.setTipHandover(true);
                                    handoverStationCommand.setHandover_batch(handoverBatch);
                                    handoverStationCommand.setId(handoverStationId);
                                }
                            } else {
                                handoverStationCommand.setCode(handoverStation.getCode());
                                handoverStationCommand.setTipHandover(false);
                                handoverStationCommand.setId(handoverStationId);
                            }
                        }
                    } else {
                        // 提示可以执行交接
                        handoverStationCommand.setCode(handoverStation.getCode());
                        handoverStationCommand.setTipHandover(true);
                        handoverStationCommand.setHandover_batch(handoverBatch);
                        handoverStationCommand.setId(handoverStationId);
                    }
                } else {
                    // 提示可以执行交接
                    handoverStationCommand.setCode(handoverStation.getCode());
                    handoverStationCommand.setTipHandover(true);
                    handoverStationCommand.setHandover_batch(handoverBatch);
                    handoverStationCommand.setId(handoverStationId);
                }

            } else {
                handoverStationCommand.setCode(handoverStation.getCode());
                handoverStationCommand.setTipHandover(false);
                handoverStationCommand.setId(handoverStationId);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("whHandoverStationRecommandManager.recommendHandoverStationByRule end, handoverStation is:[{}], logId is:[{}]", handoverStationCommand.getCode(), logId);
        }
        return handoverStationCommand;
    }
}
