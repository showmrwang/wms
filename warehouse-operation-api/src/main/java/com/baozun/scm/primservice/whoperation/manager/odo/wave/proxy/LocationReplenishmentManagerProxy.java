package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

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
    void locationReplenishmentMsg(Warehouse wh, List<Location> locationList);
    
    void locationReplenishmentTask(List<ReplenishmentMsg> msgList, Warehouse wh);
}
