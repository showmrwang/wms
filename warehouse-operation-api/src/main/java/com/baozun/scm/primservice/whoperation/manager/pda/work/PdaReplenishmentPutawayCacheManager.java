package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaReplenishmentPutawayCacheManager extends BaseManager{

    /**
     * 缓存库位
     * @param locationIds
     * @param operationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipLocation(List<Long> locationIds,Long operationId);
    
    /***
     * 缓存周转箱
     * @param turnoverBoxIds
     * @param operationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipTurnoverBox(Set<Long> turnoverBoxIds,Long operationId);
    
    /****
     * 清楚补货上架缓存
     * @param operationId
     */
    public void pdaReplenishPutwayRemoveAllCache(Long operationId);
}
