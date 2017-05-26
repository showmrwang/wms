package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportService;

public interface OdoTransportServiceManager extends BaseManager {

    WhOdoTransportService findByOdoId( Long odoId, Long ouId);

}
