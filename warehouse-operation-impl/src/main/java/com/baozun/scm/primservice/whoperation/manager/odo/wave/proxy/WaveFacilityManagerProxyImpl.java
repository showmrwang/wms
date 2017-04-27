package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingWallRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFacilityRecPathManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityQueue;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacilityGroup;

@Service("waveFacilityManagerProxy")
public class WaveFacilityManagerProxyImpl extends BaseManagerImpl implements WaveFacilityManagerProxy {
    protected static final Logger log = LoggerFactory.getLogger(WaveFacilityManagerProxyImpl.class);
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private WhWaveManager whWaveManager;
    @Autowired
    private WhFacilityRecPathManager whFacilityRecPathManager;

    @Override
    public RecFacilityPathCommand matchOutboundFacility(RecFacilityPathCommand recFacilityPath) {
        try {

            // 校验传入参数
            checkParams(recFacilityPath);
            Long ouId = recFacilityPath.getOuId();
            Warehouse wh = this.warehouseManager.findWarehouseByIdExt(ouId);
            if (!wh.getIsApplyFacility()) {
                return responseMsgForFacility(recFacilityPath, 0);
            }

            if (Constants.PICKING_MODE_SEED.equals(recFacilityPath.getPickingMode())) {
                return matchSeedingWallWhenSeeding(wh, recFacilityPath);
            } else {
                // #TODO
            }
        } catch (Exception e) {
            return responseMsgForFacility(recFacilityPath, 0);
        }
        return responseMsgForFacility(recFacilityPath, 1);
    }


    private RecFacilityPathCommand responseMsgForFacility(RecFacilityPathCommand recFacilityPath, Integer responseStatus) {
        recFacilityPath.setStatus(responseStatus);
        return recFacilityPath;
    }

    /**
     * 校验接口传参
     * 
     * @param recFacilityPath
     */
    private void checkParams(RecFacilityPathCommand recFacilityPath) {}

    /**
     * 播种时候推荐播种墙
     * 
     * @param wh
     * @param recFacilityPath
     * @param whSeedingWallRule
     */
    private RecFacilityPathCommand matchSeedingWallWhenSeeding(Warehouse wh, RecFacilityPathCommand recFacilityPath) {

        try {
            Long ouId = wh.getId();
            String batch = recFacilityPath.getBatch();
            List<Long> odoIdList = recFacilityPath.getOdoIdList();
            // 是否已有推荐成功的箱信息;如果存在，则不需要寻找播种墙，如果不存在，则需要推荐播种墙
            List<WhFacilityRecPath> pathList = this.whFacilityRecPathManager.findWhFacilityRecPathByBatchAndContainer(batch, null, ouId);
            WhFacilityRecPath prePath = null;
            if (pathList != null && pathList.size() > 0) {// 存在推荐成功的信息
                prePath = pathList.get(0);
            }

            if (prePath == null) {
                // 模式
                if (StringUtils.isEmpty(wh.getSeedingMode())) {
                    return responseMsgForFacility(recFacilityPath, 0);
                }
                // 规则
                RuleAfferCommand ruleAffer = new RuleAfferCommand();
                ruleAffer.setSeedingWallOdoIdList(odoIdList);
                ruleAffer.setOuid(ouId);
                ruleAffer.setRuleType(Constants.RULE_TYPE_SEEDING_WALL);
                RuleExportCommand export = this.ruleManager.ruleExport(ruleAffer);
                List<WhSeedingWallRuleCommand> whSeedingWallRuleList = export.getWhSeedingWallRuleCommandList();
                if (whSeedingWallRuleList == null || whSeedingWallRuleList.size() == 0) {
                    return responseMsgForFacility(recFacilityPath, 0);
                }
                // @mender yimin.lu 2017/4/12 当第一个规则失败时候，进行后续规则校验
                boolean flag = false;
                for (WhSeedingWallRuleCommand whSeedingWallRule : whSeedingWallRuleList) {
                    if (!flag) {
                        // 播种墙组
                        WhOutboundFacilityGroup facilityGroup = this.whFacilityRecPathManager.findOutboundFacilityGroupById(whSeedingWallRule.getOutboundFacilityGroupId(), ouId);
                        if (facilityGroup == null) {
                            return responseMsgForFacility(recFacilityPath, 0);
                        }
                        try {
                            this.whFacilityRecPathManager.occupyFacilityAndlocation(facilityGroup, null, recFacilityPath, wh);
                            flag = true;
                        } catch (Exception e) {
                            flag = false;
                        }
                    }
                }
                return responseMsgForFacility(recFacilityPath, flag ? 1 : 0);
            } else {
                this.whFacilityRecPathManager.occupyFacilityAndlocation(null, prePath, recFacilityPath, wh);
            }


        } catch (Exception e) {
            return responseMsgForFacility(recFacilityPath, 0);
        }
        return responseMsgForFacility(recFacilityPath, 1);
    }

    @Override
    public void matchSeedingWalBySortQueue(WhFacilityQueue queue) {
        try {
            this.whFacilityRecPathManager.matchSeedingWalBySortQueue(queue);
        } catch (Exception e) {
            log.error("matchSeedingWalBySortQueue error", e);
        }
    }


    @Override
    public List<WhFacilityQueue> getSortedQueue(Long ouId) {
        return this.whFacilityRecPathManager.getSortedQueue(ouId);
    }

}
