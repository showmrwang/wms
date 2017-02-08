package com.baozun.scm.primservice.whoperation.manager.auth;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.auth.OperUserManager;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.localauth.OperUserDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.localauth.OperUser;

@Service("operUserManager")
public class OperUserManagerImpl extends BaseManagerImpl implements OperUserManager {
    @Autowired
    private OperUserDao operUserDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public OperUser findUserById(Long userId) {
        return this.operUserDao.findById(userId);
    }

}
