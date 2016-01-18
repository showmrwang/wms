package com.baozun.scm.primservice.whoperation.manager.editpoasn;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface EditPoAsnManagerProxy extends BaseManager {

    int editAsnStatus(WhAsnCommand whAsnCommand);
}
