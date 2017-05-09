package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;

public interface OdoTransportMgmtManager extends BaseManager {

    WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId);

    int updateOdoTransportMgmt(WhOdoTransportMgmt tranMgmt);
    
    int updateOdoTransportMgmtExt(WhOdoTransportMgmt transMgmt);
    
    void saveOrUpdateTransportService(Long odoId, boolean flag, int index, String errorMsg, Long ouId);

    void updateOdoTransportMgmtAndSaveDeliveryInfo(WhOdoTransportMgmt transMgmt, WhOdodeliveryInfo delivery);

}
