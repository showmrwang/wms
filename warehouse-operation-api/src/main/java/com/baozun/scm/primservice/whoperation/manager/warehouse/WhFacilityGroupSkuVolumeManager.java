package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityGroupSkuVolume;

public interface WhFacilityGroupSkuVolumeManager extends BaseManager {

    WhFacilityGroupSkuVolume findSkuByCheckLocationSerialNumber(Long lsvId, Integer serialNumber, Long ouId);

}
