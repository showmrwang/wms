package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WeightingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


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
    public WhCheckingCommand findWhChecking(Long checkingId, Long ouId);



    /**
     * tangming
     * 按单复合
     * @param checkingLineList
     */
    public WeightingCommand checkingByOdo(WhCheckingByOdoResultCommand cmd, Boolean isTabbInvTotal, Long userId, Long ouId, Long functionId);

    /**
     * [业务方法] 通过输入查找复核信息
     * @param whCheckingCommand
     * @return
     */
    public WhCheckingByOdoCommand retrieveCheckData(WhCheckingCommand whCheckingCommand);

    /**
     * [业务方法] 更新出库箱
     * @param whCheckingCommand
     */
    public void updateCheckOutboundBox(WhCheckingCommand whCheckingCommand);
}
