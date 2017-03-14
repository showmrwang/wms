package com.baozun.scm.primservice.whoperation.manager.archiv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/***
 * 出库单归档
 * 
 * @author bin.hu
 *
 */
@Service("odoArchivManager")
@Transactional
public class OdoArchivManagerImpl implements OdoArchivManager {

    protected static final Logger log = LoggerFactory.getLogger(OdoArchivManager.class);

}
