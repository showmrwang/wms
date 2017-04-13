package com.baozun.scm.primservice.whoperation.manager.confirm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("whInvoiceConfirmManager")
@Transactional
public class WhInvoiceConfirmManagerImpl implements WhInvoiceConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhInvoiceConfirmManagerImpl.class);
    
}
