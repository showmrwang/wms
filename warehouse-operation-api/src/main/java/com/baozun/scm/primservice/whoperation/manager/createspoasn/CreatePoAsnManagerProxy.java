package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;

public interface CreatePoAsnManagerProxy extends BaseManager {

    ResponseMsg CreatePo(WhPoCommand po);

    ResponseMsg CreateAsn(WhAsnCommand asn);

}
