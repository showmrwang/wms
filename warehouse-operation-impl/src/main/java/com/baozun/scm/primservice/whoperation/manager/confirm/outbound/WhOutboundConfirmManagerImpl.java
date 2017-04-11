package com.baozun.scm.primservice.whoperation.manager.confirm.outbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("whOutboundConfirmManager")
@Transactional
public class WhOutboundConfirmManagerImpl implements WhOutboundConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhOutboundConfirmManagerImpl.class);

}
