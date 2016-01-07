package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface CreatePoManagerProxy extends BaseManager{

    int saveOrUpdate(WhPoCommand command, Long userId);
}
