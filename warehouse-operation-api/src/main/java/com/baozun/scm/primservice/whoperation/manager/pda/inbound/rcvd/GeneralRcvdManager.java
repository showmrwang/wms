package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface GeneralRcvdManager extends BaseManager {

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param commmand
     */
    void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commmandList);

    /**
     * 根据内部容器号查询容器是否有库存
     * 
     * @param insideContainerId
     * @return
     */
    long findContainerListCountByInsideContainerIdFromSkuInventory(Long insideContainerId, Long ouId);

    /**
     * 获取容器的去重后的商品库存属性。多重库存属性用，分隔
     * 
     * @param insideContainerId
     * @return
     */
    RcvdContainerCacheCommand getUniqueSkuAttrFromWhSkuInventory(Long insideContainerId, Long ouId);
}
