package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaRcvdManagerProxy extends BaseManager {

    /**
     * 通用收货，扫描ASN，初始化缓存
     */
    void initAsnCacheForGeneralReceiving(Long occupationId, Long ouId);

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param commmand
     */
    void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commmandList);

    /**
     * 将扫描的临时数据推送到缓存中
     * 
     * @param rcvdCacheCommand
     */
    void cacheScanedSkuWhenGeneralRcvd(WhSkuInventoryCommand command);

    /**
     * 刷新ASN缓存操作
     * 
     * @param id
     */
    void freshAsnCacheForGeneralReceiving(Long occupationId, Double newChargeRate, Double oldChargeRate);

    /**
     * 获取匹配的明细行
     * 
     * @param command
     * @return
     */
    String getMatchLineListStr(WhSkuInventoryCommand command);

}
