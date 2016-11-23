package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;



public interface PdaPickingWorkManager extends BaseManager {
    
    /**
     * pda拣货推荐容器
     * @param command
     * @param pickingWay
     * @return
     */
    public PickingScanResultCommand pdaPickingRemmendContainer(PickingScanResultCommand  command);
}
