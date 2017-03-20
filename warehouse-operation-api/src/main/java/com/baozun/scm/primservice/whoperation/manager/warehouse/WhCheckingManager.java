package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


public interface WhCheckingManager extends BaseManager{
    
    /**
     * 保存更新打印条件
     * 
     * @param command
     * @param splitCondition 
     * @param userId
     * @param ouId
     */
    void saveOrUpdate(WhCheckingCommand whCheckingCommand);

}
