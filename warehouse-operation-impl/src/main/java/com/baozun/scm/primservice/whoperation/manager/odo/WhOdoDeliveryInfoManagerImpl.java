package com.baozun.scm.primservice.whoperation.manager.odo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("whOdoDeliveryInfoManager")
@Transactional
public class WhOdoDeliveryInfoManagerImpl extends BaseManagerImpl implements WhOdoDeliveryInfoManager {
    public static final Logger log = LoggerFactory.getLogger(WhOdoDeliveryInfoManagerImpl.class);

    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;
}
