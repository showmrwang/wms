package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationSkuVolume;

/**
 * bk
 * 
 * @author Administrator
 * 
 */
public interface LocationManager extends BaseManager {

    LocationProductVolume getLocationProductVolumeByPcIdAndSize(Long twoLevelType, String sizeType, Long ouId);

    List<Location> findLocationWithStaticNoMix(Long ouId);

    /**
     * [业务方法] 取得复核库位
     * 
     * @param ouId
     * @return
     */
    List<Location> findCheckLocationWithStaticNoMix(Long ouId);

    /**
     * [业务方法]取得静态非混放库位绑定的商品
     * 
     * @param locationId
     * @param ouId
     * @return
     */
    Long getBindedSkuByLocationId(Long locationId, Long ouId);

    List<Long> sortByIds(Set<Long> ids, Long ouId);

    /**
     * 查取商品所有数据
     * 
     * @param skuId
     * @param ouId
     * @param logId
     * @return
     */
    SkuRedisCommand findSkuMasterBySkuId(Long skuId, Long ouId, String logId);

    List<LocationSkuVolume> findListByfacilityId(Long facilityId, Long ouId);

    /**
     * [通用方法] 扣减耗材
     * 
     * @param facilityId
     * @param skuId
     * @param ouId
     * @param userId
     * @param outboundboxCode
     * @return
     */
    Long reduceQty(Long facilityId, Long skuId, String outboundboxCode, Long ouId, Long userId);

    /**
     * [业务方法] 扣减耗材并且保存出库箱
     * @param command
     * @return
     */
    LocationSkuVolume reduceQtyAndUpdateOutboundbox(WhCheckingCommand command);

    Location findLocationByCode(String locationCode, Long ouId);



}
