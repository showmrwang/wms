package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;

public interface WhPrintInfoManager extends BaseManager {
    
    /**
     * 打印信息表
     * @param outboundboxCode
     * @param checkingPrint
     * @return
     */
     List<WhPrintInfo> findByOutboundboxCodeAndPrintType(String outboundboxCode, String checkingPrint, Long ouId);
     
     /**
      * 保存更新复核明细
      * 
      * @param whCheckingCommand
      */
     void saveOrUpdate(WhPrintInfo whPrintInfo);

}
