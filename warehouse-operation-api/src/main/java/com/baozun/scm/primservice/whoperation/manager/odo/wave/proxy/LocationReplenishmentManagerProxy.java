package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

/**
 * 库位容量补货
 * 
 * @author Administrator
 *
 */
public interface LocationReplenishmentManagerProxy extends BaseManager {

    /**
     * 库位补货-生成补货信息
     * 
     * @param wh
     * @param locationList
     */
    void locationReplenishmentMsg(Warehouse wh, Location l);
    
    void locationReplenishmentTask(List<ReplenishmentMsg> msgList, Warehouse wh);

    /**
     * 查找所有的补货信息
     * 
     * @param ouId
     */
    List<ReplenishmentMsg> findReplenishmentMsgListByOuId(Long ouId);

    /**
     * 插入补货失败信息
     * 
     * @param map
     * @param logId
     * @return
     */
    boolean insertLocationReplenishmentErrorMsg(Map<String, List<ReplenishmentMsg>> map, String logId);
}
