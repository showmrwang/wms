package com.baozun.scm.primservice.whoperation.manager.confirm.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("whInboundConfirmManager")
@Transactional
public class WhInboundConfirmManagerImpl implements WhInboundConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhInboundConfirmManagerImpl.class);
    
}
