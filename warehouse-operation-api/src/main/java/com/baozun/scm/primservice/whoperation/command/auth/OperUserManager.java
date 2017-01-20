package com.baozun.scm.primservice.whoperation.command.auth;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.localauth.OperUser;

public interface OperUserManager extends BaseManager {

    OperUser findUserById(Long userId);
}
