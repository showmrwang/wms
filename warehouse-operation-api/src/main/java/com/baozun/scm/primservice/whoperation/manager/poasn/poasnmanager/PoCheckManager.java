package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;

public interface PoCheckManager extends BaseManager {

    ResponseMsg insertPoWithCheckWithoutOuId(PoCheckCommand poCheckCommand);

    ResponseMsg insertPoWithCheckAndOuId(CheckPoCode checkPoCode);

}
