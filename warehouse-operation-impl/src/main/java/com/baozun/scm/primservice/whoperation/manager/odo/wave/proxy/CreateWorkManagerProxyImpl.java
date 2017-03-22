package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
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
            // 查询波次中的所有小批次
            WhWave whWave = whWaveManager.getWaveByWaveIdAndOuId(waveId, ouId);
            if (null == whWave) {
                throw new BusinessException("没有波次头信息");
            }
            if( null != whWave.getIsCreateReplenishedWork() && false == whWave.getIsCreateReplenishedWork()){
                // 波次中创建补货工作和作业
                Boolean isReplenishmentWorkInWave = this.createReplenishmentWorkInWave(waveId, ouId, userId);
                if(true == isReplenishmentWorkInWave){
                    whWave.setIsCreateReplenishedWork(true);
                }
            }
            if( null != whWave.getIsCreatePickingWork() && false == whWave.getIsCreatePickingWork()){
                // 波次中创建拣货工作和作业
                Boolean isPickingWorkInWave = this.createPickingWorkInWave(waveId, ouId, userId);
                if(true == isPickingWorkInWave){
                    whWave.setIsCreatePickingWork(true);
                }
            }
            if(true == whWave.getIsCreateReplenishedWork() && true == whWave.getIsCreatePickingWork()){
                whWave.setStatus(WaveStatus.WAVE_EXECUTED);
            }
            whWaveManager.updateWaveByWhWave(whWave); 
        } catch (Exception e) {
            log.error("", e);
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
            log.error("", e);    
        }
    }
    
    /**
     * 创建补货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    public Boolean createReplenishmentWorkInWave(Long waveId, Long ouId, Long userId) {
        // 查询补货工作释放及拆分条件分组 -- 补货工作
        Boolean isReplenishmentWorkInWave = true;
        List<ReplenishmentRuleCommand> replenishmentRuleCommands = replenishmentRuleManager.getInReplenishmentConditionGroup(waveId, ouId);
        if(null == replenishmentRuleCommands){
            log.error("replenishmentRuleCommands is null", replenishmentRuleCommands);
            return false;
        }
        // 循环补货工作释放及拆分条件分组        
        for(ReplenishmentRuleCommand replenishmentRuleCommand : replenishmentRuleCommands){
            replenishmentRuleCommand.setTaskOuId(ouId);
            // 根据补货工作拆分条件统计分析补货数据 
            Map<String, List<WhSkuInventoryAllocatedCommand>> siacMap = createWorkManager.getSkuInventoryAllocatedCommandForGroup(replenishmentRuleCommand);
            // 循环统计的分组信息分别创建工作           
            for(String key : siacMap.keySet()){
                try {
                    createWorkManager.createReplenishmentWorkInWave(siacMap.get(key), replenishmentRuleCommand, userId);
                } catch (Exception e) {
                    log.error("", e);
                    isReplenishmentWorkInWave = false;
                }
            }
        }
        return isReplenishmentWorkInWave;
    }

    /**
     * 创建拣货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    public Boolean createPickingWorkInWave(Long waveId, Long ouId, Long userId) {
        Boolean isPickingWorkInWave = true;
        // 查询出小批次列表
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = odoOutBoundBoxMapper.getBoxBatchsForPicking(waveId, ouId);
        if (null == whOdoOutBoundBoxList || whOdoOutBoundBoxList.isEmpty()) {
            throw new BusinessException("小批次列表为空"); 
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
                        createWorkManager.createPickingWorkInWave(whOdoOutBoundBoxGroup, whOdoOutBoundBox, userId);
                    } catch (Exception e) {
                        log.error("", e);
                        isPickingWorkInWave = false;
                    }    
                }
            }
    	}
    	return isPickingWorkInWave;
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
                    log.error("", e);
                }
            }
        }
    }

}
