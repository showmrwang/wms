package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface EditPoAsnManagerProxy extends BaseManager {

    ResponseMsg editAsnStatus(WhAsnCommand whAsnCommand);

    ResponseMsg cancelPo(WhPoCommand whPoCommand);

    ResponseMsg editPo(WhPo po);

    void deletePoLineByUuid(WhPoLineCommand whPoLine);

    ResponseMsg editPoLine(WhPoLine whPoLine);

    ResponseMsg editAsnLine(WhAsnLineCommand whAsnLineCommand);

    void deletePoLines(WhPoLineCommand command);

    ResponseMsg editAsn(WhAsn asn);

    ResponseMsg deletePoAndPoLine(List<WhPoCommand> whPoCommand);

    void auditPo(WhPoCommand poCommand);

    ResponseMsg auditAsn(WhAsnCommand asnCommand);

    ResponseMsg deleteAsnAndAsnLine(WhAsnCommand WhAsnCommand);

    ResponseMsg deleteAsnLines(WhAsnLineCommand command);
}
