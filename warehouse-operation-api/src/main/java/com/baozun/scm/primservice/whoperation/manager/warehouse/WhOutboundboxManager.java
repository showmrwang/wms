package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WhOutboundboxManager extends BaseManager{
    
    /**
     * 保存更新出库箱头信息
     * 
     * @param WhOutboundboxCommand
     */
    void saveOrUpdate(WhOutboundboxCommand whOutboundboxCommand);

}
