package com.baozun.scm.primservice.whoperation.manager.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("whOperationManager")
@Transactional
public class WhOperationManagerImpl extends BaseManagerImpl implements WhOperationManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOperationManagerImpl.class);
    
}
