package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;

@Service("odoLineManager")
@Transactional
public class OdoLineManagerImpl extends BaseManagerImpl implements OdoLineManager {
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoLine findOdoLineById(Long id, Long ouId) {
        return this.whOdoLineDao.findOdoLineById(id, ouId);
    }

}
