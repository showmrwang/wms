package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;



public interface PdaPickingWorkManager extends BaseManager {
    
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
}
