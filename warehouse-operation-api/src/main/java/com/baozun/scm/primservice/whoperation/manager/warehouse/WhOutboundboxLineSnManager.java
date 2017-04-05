package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineSnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WhOutboundboxLineSnManager extends BaseManager{
    
    /**
     * 保存更新出库箱明细
     * 
     * @param WhOutboundboxLineSnCommand
     */
    void saveOrUpdate(WhOutboundboxLineSnCommand whOutboundboxLineSnCommand);

}
