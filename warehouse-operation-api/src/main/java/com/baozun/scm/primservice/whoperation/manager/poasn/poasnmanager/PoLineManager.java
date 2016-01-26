package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoLineManager extends BaseManager {

    void createPoLineSingleToInfo(WhPoLine whPoLine);

    void createPoLineSingleToShare(WhPoLine whPoLine);

}
