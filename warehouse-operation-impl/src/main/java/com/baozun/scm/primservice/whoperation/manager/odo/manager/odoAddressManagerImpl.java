package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAddressDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;

@Service("odoAddressManager")
@Transactional
public class odoAddressManagerImpl extends BaseManagerImpl implements OdoAddressManager {
    @Autowired
    private WhOdoAddressDao whOdoAddressDao;
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoAddress findOdoAddressByOdoId(Long odoId, Long ouId) {
        return this.whOdoAddressDao.findOdoAddressByOdoId(odoId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoAddress findOdoAddressByIdOuId(Long id, Long ouId) {
        return this.whOdoAddressDao.findOdoAddressByIdOuId(id, ouId);
    }

}
