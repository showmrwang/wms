package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhLocationSkuVolumeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhLocationSkuVolume;

public interface WhLocationSkuVolumeManager extends BaseManager {

    /**
     * [业务方法] 通过复核库位查找绑定的商品
     * 
     * @param locationId
     * @param ouId
     * @return
     */
    WhLocationSkuVolume findSkuByCheckLocation(Long locationId, Long ouId);

    /**
     * 根据复核台ID查找库位商品容量信息
     * 
     * @author mingwei.xie
     * @param facilityId
     * @param ouId
     * @return
     */
    List<WhLocationSkuVolumeCommand> findLocSkuVolumeByFacilityId(Long facilityId, Long ouId);

    /**
     * 查找商品对应的库位容量信息
     *
     * @param skuId
     * @param ouId
     * @return
     */
    WhLocationSkuVolumeCommand findFacilityLocSkuVolumeBySkuId(Long facilityId, Long skuId, Long ouId);

}
