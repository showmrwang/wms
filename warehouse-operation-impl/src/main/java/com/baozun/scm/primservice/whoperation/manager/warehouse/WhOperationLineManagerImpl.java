package com.baozun.scm.primservice.whoperation.manager.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("whOperationLineManager")
@Transactional
public class WhOperationLineManagerImpl extends BaseManagerImpl implements WhOperationLineManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOperationLineManagerImpl.class);
    
    
}
