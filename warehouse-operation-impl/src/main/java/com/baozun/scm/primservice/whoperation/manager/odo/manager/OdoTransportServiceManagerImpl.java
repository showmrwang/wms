package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportServiceDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportService;

@Transactional
@Service("odoTransportServiceManager")
public class OdoTransportServiceManagerImpl extends BaseManagerImpl implements OdoTransportServiceManager {

    public static final Logger log = LoggerFactory.getLogger(OdoTransportServiceManagerImpl.class);

    @Autowired
    private WhOdoTransportServiceDao whOdoTransportServiceDao;

    public WhOdoTransportService findByOdoId( Long odoId, Long ouId){
        return whOdoTransportServiceDao.findByOdoIdAndOuId(odoId, ouId);
    }
}
