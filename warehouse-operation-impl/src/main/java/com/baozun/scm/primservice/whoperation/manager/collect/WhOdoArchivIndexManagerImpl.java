package com.baozun.scm.primservice.whoperation.manager.collect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("whOdoArchivIndexManager")
@Transactional
public class WhOdoArchivIndexManagerImpl implements WhOdoArchivIndexManager {

    protected static final Logger log = LoggerFactory.getLogger(WhOdoArchivIndexManager.class);
    
}
