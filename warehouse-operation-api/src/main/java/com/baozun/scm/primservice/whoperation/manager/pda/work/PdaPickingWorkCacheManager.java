package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaPickingWorkCacheManager extends BaseManager{

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
}
