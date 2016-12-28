package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;



public interface PdaReplenishmentWorkManager extends BaseManager {
    
    
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
