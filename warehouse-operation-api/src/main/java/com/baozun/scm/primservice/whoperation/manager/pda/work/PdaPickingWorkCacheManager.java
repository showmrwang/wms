package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaPickingWorkCacheManager extends BaseManager{

    /***
     * 提示小车
     * @param operatorId
     * @return
     */
    public Long pdaPickingWorkTipOutContainer(Long operatorId);
    
    
    /***
     * 提示出库箱
     * @param operatorId
     * @return
     */
    public Long pdaPickingWorkTipoutbounxBox(Long operatorId);
    
    /**
     * 提示周转箱
     * @param operatorId
     * @return
     */
    public Long pdaPickingWorkTipOutBound(Long operatorId);
}
