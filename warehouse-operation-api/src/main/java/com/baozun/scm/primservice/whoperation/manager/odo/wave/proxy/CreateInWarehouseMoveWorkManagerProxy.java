package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;



public interface CreateInWarehouseMoveWorkManagerProxy extends BaseManager {
    
    /**
     * [业务方法] 创建并执行库内移动工作
     * @param ids
     * @param uuids
     * @param toLocation
     * @return
     */
    Boolean createAndExecuteInWarehouseMoveWork(Long[] ids, String[] uuids, Double[] moveQtys, Long toLocation, Boolean isExecute, Long ouId, Long userId);
    
}
