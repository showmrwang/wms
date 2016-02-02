package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import com.baozun.scm.primservice.whoperation.command.poasn.AsnCheckCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;

public interface AsnCheckManager extends BaseManager {

    ResponseMsg insertAsnWithCheckWithoutOuId(AsnCheckCommand asnCheckCommand);

    boolean insertAsnWithCheckAndOuId(CheckAsnCode checkAsnCode);

}
