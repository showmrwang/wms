package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;

public interface CreatePoAsnManagerProxy extends BaseManager {

    ResponseMsg createPo(WhPoCommand po);

    ResponseMsg createAsn(WhAsnCommand asn);

    ResponseMsg createPoLineSingle(WhPoLineCommand whPoLine);

}
