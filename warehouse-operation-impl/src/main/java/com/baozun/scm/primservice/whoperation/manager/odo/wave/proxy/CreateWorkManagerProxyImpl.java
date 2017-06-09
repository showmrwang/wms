package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.CreateWorkResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoOutBoundBoxMapper;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.CreateWorkManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ReplenishmentRuleManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;

@Service("createWorkManagerProxy")
public class CreateWorkManagerProxyImpl implements CreateWorkManagerProxy {
    
    protected static final Logger log = LoggerFactory.getLogger(CreateWorkManagerProxyImpl.class);

    @Autowired
    private CreateWorkManager createWorkManager;
    
    @Autowired
    private WhWaveManager whWaveManager;
    
    @Autowired
    private ReplenishmentRuleManager replenishmentRuleManager;
    
    @Autowired
    private OdoOutBoundBoxMapper odoOutBoundBoxMapper;

    
    /**
     * 波次内创建工作和作业
     * 
     * @param waveId
     * @param ouId
     * @param userId
     * @return
     */
    public void createWorkInWave(Long waveId, Long ouId, Long userId) {
        try {
            CreateWorkResultCommand createWorkResultCommand = new CreateWorkResultCommand();
            // 查询波次中的所有小批次
            WhWave whWave = whWaveManager.getWaveByWaveIdAndOuId(waveId, ouId);
            if (null == whWave) {
                throw new BusinessException(ErrorCodes.WHWAVE_IS_NULL);
            }
            if( null != whWave.getIsCreateReplenishedWork() && false == whWave.getIsCreateReplenishedWork()){
                // 波次中创建补货工作和作业
                createWorkResultCommand = this.createReplenishmentWorkInWave(whWave, ouId, userId);
                Boolean isReplenishmentWorkInWave = createWorkResultCommand.getIsReplenishmentWorkInWave();
                // 这个地方确认过，如果没有补货工作也会状态也会改为ture               
                if(true == isReplenishmentWorkInWave){
                    whWave = createWorkResultCommand.getWhWave();
                    whWave.setIsCreateReplenishedWork(true);
                }
            }
            if( null != whWave.getIsCreatePickingWork() && false == whWave.getIsCreatePickingWork()){
                // 波次中创建拣货工作和作业
                createWorkResultCommand = this.createPickingWorkInWave(whWave, ouId, userId);
                Boolean isPickingWorkInWave = createWorkResultCommand.getIsPickingWorkInWave();
                if(true == isPickingWorkInWave){
                    whWave = createWorkResultCommand.getWhWave();
                    whWave.setIsCreatePickingWork(true);
                }
            }
            if(true == whWave.getIsCreateReplenishedWork() && true == whWave.getIsCreatePickingWork()){
                whWave.setStatus(WaveStatus.WAVE_EXECUTED);
                whWave.setFinishTime(new Date());
            }
            whWaveManager.updateWaveByWhWave(whWave); 
        } catch (Exception e) {
            log.error("CreateWorkManagerProxyImpl createWorkInWave error" + e);
        }
    }
    
    /**
     * 波次外创建工作和作业
     * 
     * @param ouId
     * @param userId
     * @return
     */
    public void createWorkOutWave(Long ouId, Long userId) {
        try {
            this.createReplenishmentWorkOutWave(ouId, userId);
        } catch (Exception e) {
            log.error("CreateWorkManagerProxyImpl createWorkOutWave error" + e);
        }
    }
    
    /**
     * 创建补货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    public CreateWorkResultCommand createReplenishmentWorkInWave(WhWave whWave, Long ouId, Long userId) {
        // 查询补货工作释放及拆分条件分组 -- 补货工作
        Boolean isReplenishmentWorkInWave = true;
        CreateWorkResultCommand resultCommand = new CreateWorkResultCommand();
        resultCommand.setWhWave(whWave);
        List<ReplenishmentRuleCommand> replenishmentRuleCommands = replenishmentRuleManager.getInReplenishmentConditionGroup(whWave.getId(), ouId);
        if(null == replenishmentRuleCommands){
            log.error("replenishmentRuleCommands is null", replenishmentRuleCommands);
            resultCommand.setIsReplenishmentWorkInWave(false);
            return resultCommand;
        }
        // 循环补货工作释放及拆分条件分组        
        for(ReplenishmentRuleCommand replenishmentRuleCommand : replenishmentRuleCommands){
            replenishmentRuleCommand.setTaskOuId(ouId);
            // 根据补货工作拆分条件统计分析补货数据 
            Map<String, List<WhSkuInventoryAllocatedCommand>> siacMap = createWorkManager.getSkuInventoryAllocatedCommandForGroup(replenishmentRuleCommand);
            // 循环统计的分组信息分别创建工作           
            for(String key : siacMap.keySet()){
                try {
                    resultCommand = createWorkManager.createReplenishmentWorkInWave(resultCommand.getWhWave(), siacMap.get(key), replenishmentRuleCommand, userId);
                } catch (Exception e) {
                    log.error("CreateWorkManagerProxyImpl createReplenishmentWorkInWave error" + e);
                    isReplenishmentWorkInWave = false;
                    continue;
                }
            }
        }
        resultCommand.setIsReplenishmentWorkInWave(isReplenishmentWorkInWave);
        return resultCommand;
    }

    /**
     * 创建拣货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    public CreateWorkResultCommand createPickingWorkInWave(WhWave whWave, Long ouId, Long userId) {
        Boolean isPickingWorkInWave = true;
        CreateWorkResultCommand resultCommand = new CreateWorkResultCommand();
        resultCommand.setWhWave(whWave);
        // 查询出小批次列表
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = odoOutBoundBoxMapper.getBoxBatchsForPicking(whWave.getId(), ouId);
        if (null == whOdoOutBoundBoxList || whOdoOutBoundBoxList.isEmpty()) {
            log.error("whOdoOutBoundBoxList is null", whOdoOutBoundBoxList);
            resultCommand.setIsPickingWorkInWave(false);
            return resultCommand;
        }
    	if(whOdoOutBoundBoxList != null){
    		// 循环小批次        
            for (WhOdoOutBoundBox whOdoOutBoundBox : whOdoOutBoundBoxList) {
                //根据批次查询小批次分组数据            
                whOdoOutBoundBox.setOuId(ouId);
                List<WhOdoOutBoundBox> odoOutBoundBoxForGroup = odoOutBoundBoxMapper.getOdoOutBoundBoxForGroup(whOdoOutBoundBox);
                //循环小批次下所有分组信息分别创建工作 和作业         
                for(WhOdoOutBoundBox whOdoOutBoundBoxGroup : odoOutBoundBoxForGroup){
                    try {
                        resultCommand = createWorkManager.createPickingWorkInWave(resultCommand.getWhWave(), whOdoOutBoundBoxGroup, whOdoOutBoundBox, resultCommand, userId);
                    } catch (Exception e) {
                        log.error("CreateWorkManagerProxyImpl createPickingWorkInWave error" + e);
                        isPickingWorkInWave = false;
                        continue;
                    }    
                }
            }
    	}
    	resultCommand.setIsPickingWorkInWave(isPickingWorkInWave);
    	return resultCommand;
    }
    
    /**
     * 创建波次外工作和作业
     * 
     * @param ouId
     * @param userId
     * @return
     */
    public void createReplenishmentWorkOutWave(Long ouId, Long userId) {
        // 查询补货工作释放及拆分条件分组 -- 补货工作
        List<ReplenishmentRuleCommand> replenishmentRuleCommands = replenishmentRuleManager.getOutReplenishmentConditionGroup(ouId);
        // 循环补货工作释放及拆分条件分组        
        for(ReplenishmentRuleCommand replenishmentRuleCommand : replenishmentRuleCommands){
            // 根据补货工作拆分条件统计分析补货数据 
            Map<String, List<WhSkuInventoryAllocatedCommand>> siacMap = createWorkManager.getSkuInventoryAllocatedCommandForGroup(replenishmentRuleCommand);
            // 循环统计的分组信息分别创建工作           
            for(String key : siacMap.keySet()){
                try {
                    createWorkManager.createReplenishmentWorkOutWave(siacMap.get(key), replenishmentRuleCommand, userId);
                } catch (Exception e) {
                    log.error("CreateWorkManagerProxyImpl createReplenishmentWorkOutWave error" + e);
                }
            }
        }
    }

    /**
     * 拣货后触发补货工作作业明细生成
     * 
     * @param locationIds
     * @param ouId
     * @param userId
     * @return
     */
    public void createReplenishmentAfterPicking(List<Long> locationIds, Long ouId, Long userId) {
        for(Long locationId : locationIds){
            try {
                createWorkManager.createReplenishmentAfterPicking(locationId, ouId, userId);
            } catch (Exception e) {
                log.error("CreateWorkManagerProxyImpl createReplenishmentAfterPicking error" + e);
                continue;
            }
        }
    }
}
