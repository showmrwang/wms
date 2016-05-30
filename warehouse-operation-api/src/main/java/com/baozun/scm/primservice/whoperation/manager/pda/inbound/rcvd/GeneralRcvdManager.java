package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface GeneralRcvdManager extends BaseManager {

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param commmand
     */
    void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commmandList);
}
