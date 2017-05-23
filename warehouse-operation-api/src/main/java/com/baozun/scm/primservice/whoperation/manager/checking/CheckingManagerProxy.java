package com.baozun.scm.primservice.whoperation.manager.checking;


import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.WarehouseMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;

public interface CheckingManagerProxy extends BaseManager {

    /**
     * 根据复核打印配置打印单据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     * @return
     */
    Boolean printDefect(WhCheckingResultCommand whCheckingResultCommand);

    /**
     * 更新复核数据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     * @return
     */
    Boolean updateChecking(WhCheckingResultCommand whCheckingResultCommand);

    /**
     * 生成出库箱库存与箱数据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     * @return
     */
    Boolean createOutboundbox(WhCheckingResultCommand whCheckingResultCommand);

    /**
     * 更新出库单状态
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     * @return
     */
    Boolean updateOdoStatus(WhCheckingResultCommand whCheckingResultCommand);

    /**
     * 算包裹计重
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     * @return
     */
    Boolean packageWeightCalculation(WhCheckingResultCommand whCheckingResultCommand);


    // /**
    // * tangming
    // * 按单复合打印
    // * @param outBoudBoxCode
    // * @param
    // * @param
    // */
    // public Boolean printDefectByOdo(String outBoudBoxCode,Long ouId,Long functionId);



    /**
     * 根据ID查找出库设施
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId);



    public List<WhSkuInventorySnCommand> findCheckingSkuInvSnByCheckingId(Long checkingId, Long ouId);



    /**
     * 获取复核箱的复核明细信息
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @param logId
     * @return
     */
    public List<WhCheckingLineCommand> getCheckingLineListByChecking(Long checkingId, Long ouId, String logId);

    public Customer findCustomerByRedis(Long customerId);

    public Store findStoreByRedis(Long storeId);

    /**
     * 完成复核
     *
     * @param checkingCommand
     */
    public void finishedCheckingByContainer(WhFunctionOutBound function, WhOutboundFacilityCommand facilityCommand, WarehouseMgmt warehouseMgmt, WhCheckingCommand checkingCommand, Long userId, Long ouId, String logId);

    public boolean checkBoxCheckingFinished(Long checkingId, Long ouId, String logId);

    public boolean checkFacilityCheckingFinished(String checkingSourceCode, Long ouId, String logId);

    public boolean checkTrolleyCheckingFinished(String checkingSourceCode, Long ouId, String logId);

    public void releaseCheckingSource(Long checkingId, String checkingSourceCode, String checkingType, Long userId, Long ouId, String logId);
}
