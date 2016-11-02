package com.baozun.scm.primservice.whoperation.manager.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.system.SysThreadDeployDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.system.SysThreadDeploy;

@Transactional
@Service("sysThreadDeployManager")
public class SysThreadDeployManagerImpl extends BaseManagerImpl implements SysThreadDeployManager {
	
	@Autowired
	private SysThreadDeployDao sysThreadDeployDao;
	
	@Override
	public SysThreadDeploy findSysThreadDeployByCode(String code, Long ouId) {
		SysThreadDeploy sysThreadDeploy = sysThreadDeployDao.getSysThreadDeployByCode(code, ouId);
		return sysThreadDeploy;
	}

}
