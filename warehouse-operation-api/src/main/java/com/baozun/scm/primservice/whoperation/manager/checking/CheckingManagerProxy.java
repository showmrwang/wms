package com.baozun.scm.primservice.whoperation.manager.checking;


import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

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
    
    
//    /**
//     * tangming
//     * 按单复合打印
//     * @param outBoudBoxCode
//     * @param 
//     * @param 
//     */
//    public Boolean printDefectByOdo(String outBoudBoxCode,Long ouId,Long functionId);
    
}
