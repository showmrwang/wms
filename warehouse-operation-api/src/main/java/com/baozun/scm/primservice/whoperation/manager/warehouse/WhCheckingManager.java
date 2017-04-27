package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;


public interface WhCheckingManager extends BaseManager {

    /**
     * 保存更新打印条件
     * 
     * @param command
     * @param splitCondition 
     * @param userId
     * @param ouId
     */
    void saveOrUpdate(WhCheckingCommand whCheckingCommand);

    /**
     * [业务方法] 按单复核-校验输入
     * @param WhCheckingByOdoCommand
     * @return
     */
    WhCheckingByOdoCommand checkInput(WhCheckingByOdoCommand whCheckingCommand);
    
    /**
     * 
     * @param checkingId
     * @param ouId
     * @return
     */
    public WhCheckingCommand findWhChecking(Long checkingId,Long ouId);

}
