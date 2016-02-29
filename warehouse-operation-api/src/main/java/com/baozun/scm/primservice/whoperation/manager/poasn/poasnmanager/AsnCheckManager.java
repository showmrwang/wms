package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;

public interface AsnCheckManager extends BaseManager {

    boolean insertAsnWithCheckAndOuId(CheckAsnCode checkAsnCode);

    List<CheckAsnCode> findCheckAsnCodeListByParam(CheckAsnCode checkAsnCode);
}
