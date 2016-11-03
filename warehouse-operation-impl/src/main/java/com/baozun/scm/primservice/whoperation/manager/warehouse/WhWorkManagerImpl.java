package com.baozun.scm.primservice.whoperation.manager.warehouse;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;

@Service("whWorkManager")
@Transactional
public class WhWorkManagerImpl extends BaseManagerImpl implements WhWorkManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhWorkManagerImpl.class);
    
    @Autowired
    private WhWorkDao whWorkDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdate(WhWork whWork) {
        if (null != whWork.getId()) {
            whWorkDao.update(whWork);
        } else {
            whWorkDao.insert(whWork);
        }
    }
    
}
