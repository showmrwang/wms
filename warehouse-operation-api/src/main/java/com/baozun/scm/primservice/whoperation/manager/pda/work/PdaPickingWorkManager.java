package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingParamCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;



public interface PdaPickingWorkManager extends BaseManager {
    
    /**
     * pda拣货推荐容器
     * @param command
     * @param pickingWay
     * @return
     */
    public PickingParamCommand pdaPickingRemmendContainer(PickingParamCommand command,Integer pickingWay);
}
