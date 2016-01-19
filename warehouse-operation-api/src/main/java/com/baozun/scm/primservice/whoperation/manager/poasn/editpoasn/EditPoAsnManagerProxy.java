package com.baozun.scm.primservice.whoperation.manager.poasn.editpoasn;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface EditPoAsnManagerProxy extends BaseManager {

    int editAsnStatus(WhAsnCommand whAsnCommand);

    int editPoStatus(WhPoCommand whPoCommand);
}
