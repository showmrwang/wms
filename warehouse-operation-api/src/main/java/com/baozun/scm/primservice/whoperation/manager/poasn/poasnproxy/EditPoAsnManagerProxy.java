package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface EditPoAsnManagerProxy extends BaseManager {

    int editAsnStatus(WhAsnCommand whAsnCommand);

    int editPoStatus(WhPoCommand whPoCommand);

    ResponseMsg editPo(WhPo po);

    void deletePoLineByUuid(WhPoLineCommand whPoLine);

    ResponseMsg editPoLine(WhPoLine whPoLine);

    int editPoLineStatus(WhPoLineCommand command);
}
