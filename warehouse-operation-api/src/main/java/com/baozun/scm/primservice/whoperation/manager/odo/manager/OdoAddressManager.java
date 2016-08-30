package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;

public interface OdoAddressManager extends BaseManager {

    /**
     * [通用方法]根据ODOID和OUID查找ODOADDRESS
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    WhOdoAddress findOdoAddressByOdoId(Long odoId, Long ouId);

}
