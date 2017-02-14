package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhLocationSkuVolume;

public interface WhLocationSkuVolumeManager extends BaseManager {

    /**
     * [业务方法] 通过复核库位查找绑定的商品
     * @param locationId
     * @param ouId
     * @return
     */
    WhLocationSkuVolume findSkuByCheckLocation(Long locationId, Long ouId);

}
