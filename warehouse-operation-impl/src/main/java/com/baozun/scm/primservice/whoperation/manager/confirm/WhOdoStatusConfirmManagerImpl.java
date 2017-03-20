package com.baozun.scm.primservice.whoperation.manager.confirm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("whOdoStatusConfirmManager")
@Transactional
public class WhOdoStatusConfirmManagerImpl extends BaseManagerImpl implements WhOdoStatusConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhOdoStatusConfirmManagerImpl.class);

}
