package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

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
    public WhCheckingByOdoResultCommand checkingByOdo(WhCheckingByOdoResultCommand cmd, Boolean isTabbInvTotal, Long userId, Long ouId, Long functionId);

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

    /**
     * [业务方法] 复核信息
     * @param whCheckingByOdoCommand
     * @return
     */
    public WhCheckingByOdoCommand findCheckingInfo(WhCheckingByOdoCommand whCheckingByOdoCommand);


    public Boolean printDefect(WhCheckingByOdoResultCommand cmd);


    /***
     * 绑定运单号
     * @param command
     * @return
     */
    public WhCheckingByOdoResultCommand bindkWaybillCode(Long funcationId, Long ouId, Long odoId, String outboundboxCode, Long consumableSkuId, Boolean binding);

    /**
     * [业务方法] 查询待复核数据
     * @param command
     * @return
     */
    public List<WhCheckingByOdoCommand> retrieveCheckDataGeneral(WhCheckingCommand command);
}
