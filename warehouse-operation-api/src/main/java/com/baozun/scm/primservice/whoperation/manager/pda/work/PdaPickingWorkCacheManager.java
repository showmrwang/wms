package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaPickingWorkCacheManager extends BaseManager{

    /**
     * 缓存统计分析结果
     * 
     * @author qiming.liu
     * @param operatorId
     * @param operatioLineStatisticsCommand
     * @return
     */
    public void operatioLineStatisticsRedis(Long operatorId, OperatioLineStatisticsCommand operatioLineStatisticsCommand);
    
    /***
     * 提示小车
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipOutContainer(Long operatorId,Long ouId);
    
    
    /***
     * 提示出库箱
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipoutboundBox(Long operatorId,Long ouId);
    
    /**
     * 提示周转箱
     * @param operatorId
     * @return
     */
    public String pdaPickingWorkTipTurnoverBox(Long operatorId,Long ouId);
    
    /**
     * pda拣货整托整箱
     * @param operatorId
     * @return
     */
    public OperatioLineStatisticsCommand pdaPickingWorkTipWholeCase(Long operatorId,Long ouId);
}
