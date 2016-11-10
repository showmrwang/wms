package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ma.TransportProviderDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.TransportProvider;

@Service("odoTransportMgmtManager")
@Transactional
public class OdoTransportMgmtManagerImpl extends BaseManagerImpl implements OdoTransportMgmtManager {
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private TransportProviderDao transportProviderDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId) {
        return this.whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public TransportProvider findByCode(String transCode) {
        return this.transportProviderDao.findByCode(transCode);
    }

}
