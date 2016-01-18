package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface AsnManager extends BaseManager {

    List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid);

    int editAsnStatusByInfo(WhAsnCommand whAsn);

    int editAsnStatusByShard(WhAsnCommand whAsn);


}
