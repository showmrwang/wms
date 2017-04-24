package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;

public interface WhOutboundboxLineManager extends BaseManager{
    
    /**
     * 保存更新出库箱明细
     * 
     * @param WhOutboundboxLineCommand
     */
    void saveOrUpdate(WhOutboundboxLineCommand whOutboundboxLineCommand);


    void saveOrUpdate(WhOutboundboxLine whOutboundboxLine);

}
