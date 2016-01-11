package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface CreatesAsnManager extends BaseManager {

    List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode);

}
