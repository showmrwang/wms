package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;

@Service("odoTransportMgmtManager")
@Transactional
public class OdoTransportMgmtManagerImpl extends BaseManagerImpl implements OdoTransportMgmtManager {
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId) {
        return this.whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoId, ouId);
    }

}
