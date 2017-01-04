package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;



public interface PdaReplenishmentWorkManager extends BaseManager {
    
    
    /**
     * 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    ReplenishmentResultCommand getReplenishmentForGroup(WhWork whWork, Long ouId);
    
    /***
     * 提示拣货库位
     * @author tangming
     * @param operationId
     * @return
     */
    public PickingScanResultCommand replenishmentTipLocation(Long functionId,Long operationId,Long ouId);
    
    /**
     * 校验库位
     * @param locationCode
     * @param locationBarCode
     * @param ouId
     * @return
     */
    public Long verificationLocation(String locationCode,String locationBarCode,Long ouId);
    
}
