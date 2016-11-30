package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

/**
 * 库位容量补货
 * 
 * @author Administrator
 *
 */
public interface LocationReplenishmentManagerProxy extends BaseManager {

    /**
     * 库位补货
     * 
     * @param wh
     * @param location
     * @param skuId
     */
    void locationReplenishment(Warehouse wh, Location location, Long skuId);
}
