package com.baozun.scm.primservice.whoperation.manager.system;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Transactional
@Service("sysThreadDeployManager")
public class SysThreadDeployManagerImpl extends BaseManagerImpl implements SysThreadDeployManager {

}
