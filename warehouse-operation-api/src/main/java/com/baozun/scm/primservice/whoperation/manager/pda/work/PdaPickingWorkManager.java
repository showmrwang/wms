package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;



public interface PdaPickingWorkManager extends BaseManager {
    
    /**
     * 保存工作操作员信息
     * 
     * @author qiming.liu
     * @param workId
     * @param ouId
     * @param userId
     * @return
     */
    void saveWorkOper (Long workId, Long ouId, Long userId);
    
    /**
     * 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    PickingScanResultCommand getOperatioLineForGroup(WhWork whWork, Long ouId);
    
    /**
     * pda拣货推荐容器
     * @author tangming
     * @param command
     * @param pickingWay
     * @return
     */
    public PickingScanResultCommand pdaPickingRemmendContainer(PickingScanResultCommand  command);
    
    /***
     * 循环扫描排序后的库位
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand loopScanLocation(PickingScanResultCommand  command);
    
    /**
     * pda拣货整托整箱
     * @author qiming.liu
     * @param command
     * @return
     */
    public PickingScanResultCommand pdaPickingWholeCase(PickingScanResultCommand  command);
    
    /**
     * 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    void findOperatioLineForGroup(WhWork whWork, Long ouId);
}
