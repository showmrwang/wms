package com.baozun.scm.primservice.whoperation.manager.system;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


public interface GlobalLogManager extends BaseManager {

    void insertGlobalLog(GlobalLogCommand globalLogCommand);
    
    void insertGlobalLog(GlobalLogCommand globalLogCommand, String dataSource);
}
