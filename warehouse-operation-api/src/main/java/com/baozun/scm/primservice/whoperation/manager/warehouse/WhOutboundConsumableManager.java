package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundConsumable;


public interface WhOutboundConsumableManager extends BaseManager {
    public void saveOrUpdateByVersion(WhOutboundConsumable whOutboundConsumable);
}
