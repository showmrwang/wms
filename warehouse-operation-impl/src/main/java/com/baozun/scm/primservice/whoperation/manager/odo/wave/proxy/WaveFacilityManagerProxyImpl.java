package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
        // 校验传入参数
        checkParams(recFacilityPath);
        Long ouId = recFacilityPath.getOuId();
        Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
        if (!wh.getIsApplyFacility()) {
            return null;
        }

        if (Constants.WH_SEEDING_WALL.equals(recFacilityPath.getPickingMode())) {
            return matchSeedingWallWhenSeeding(wh, recFacilityPath);
        } else {
            // #TODO
        }
        return null;
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
            // 是否已有推荐成功的箱信息
            // 规则
            RuleAfferCommand ruleAffer = new RuleAfferCommand();
            ruleAffer.setSeedingWallOdoIdList(odoIdList);
            ruleAffer.setOuid(ouId);
            RuleExportCommand export = this.ruleManager.ruleExport(ruleAffer);
            WhSeedingWallRuleCommand whSeedingWallRule = export.getWhSeedingWallRuleCommand();
            if (whSeedingWallRule == null) {
                recFacilityPath.setStatus(0);
                return recFacilityPath;
            }
            List<WhFacilityRecPath> pathList = this.whFacilityRecPathManager.findWhFacilityRecPathByBatchAndContainer(batch, null, ouId);
            WhFacilityRecPath prePath = null;
            if (pathList != null && pathList.size() > 0) {// 存在推荐成功的信息
                prePath = pathList.get(0);
            }
            // 模式
            if (StringUtils.isEmpty(wh.getSeedingMode())) {
                recFacilityPath.setStatus(0);
                return recFacilityPath;
            }
            // 播种墙组
            WhOutboundFacilityGroup facilityGroup = this.whFacilityRecPathManager.findOutboundFacilityGroupById(whSeedingWallRule.getOutboundFacilityGroupId(), ouId);
            if (facilityGroup == null) {
                recFacilityPath.setStatus(0);
                return recFacilityPath;
            }

            this.whFacilityRecPathManager.occupyFacilityAndlocationByFacilityGroup(facilityGroup, prePath, recFacilityPath, wh);
        } catch (Exception e) {
            recFacilityPath.setStatus(0);
            return recFacilityPath;
        }
        recFacilityPath.setStatus(1);
        return recFacilityPath;
    }

    @Override
    public void matchSeedingWalBySortQueue(WhFacilityQueue queue) {

    }

}
