package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;

/**
 * bk
 * 
 * @author Administrator
 *
 */
public interface LocationManager extends BaseManager {

    LocationProductVolume getLocationProductVolumeByPcIdAndSize(Long twoLevelType, String sizeType, Long ouId);
    
    List<Long> sortByIds(Set<Long> ids, Long ouId);


}
