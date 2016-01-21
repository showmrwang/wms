package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

public interface EditPoAsnManagerProxy extends BaseManager {

    int editAsnStatus(WhAsnCommand whAsnCommand);

    int editPoStatus(WhPoCommand whPoCommand);

    ResponseMsg editPo(WhPo po);
}
