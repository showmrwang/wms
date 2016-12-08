package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;

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
     * [业务方法]取得静态非混放库位绑定的商品
     * 
     * @param locationId
     * @param ouId
     * @return
     */
    Long getBindedSkuByLocationId(Long locationId, Long ouId);
    
    List<Long> sortByIds(Set<Long> ids, Long ouId);


}
