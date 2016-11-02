package com.baozun.scm.primservice.whoperation.manager.system;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.system.SysThreadDeploy;

public interface SysThreadDeployManager extends BaseManager {
	
	SysThreadDeploy findSysThreadDeployByCode(String code, Long ouId);
}
