package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WhFacilityGroupSkuVolumeManager extends BaseManager {

    Long findSkuByCheckLocationSerialNumber(Integer serialNumber, Long ouId);

}
