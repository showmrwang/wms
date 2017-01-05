package com.baozun.scm.primservice.whoperation.manager.bi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("userImportExcelManager")
@Transactional
public class UserImportExcelManagerImpl extends BaseManagerImpl implements UserImportExcelManager {

    protected static final Logger log = LoggerFactory.getLogger(UserImportExcelManager.class);



}
