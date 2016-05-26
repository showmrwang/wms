package com.baozun.scm.primservice.whoperation.manager.pda.Rcvd;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

public interface GeneralRcvdManager extends BaseManager {

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param commmand
     */
    void saveScanedSkuWhenGeneralRcvdForPda(WhSkuInventory commmand);
}
