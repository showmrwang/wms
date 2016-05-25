package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnSnDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;

@Service("asnSnManager")
@Transactional
public class AsnSnManagerImpl extends BaseManagerImpl implements AsnSnManager {
    @Autowired
    private WhAsnSnDao whAsnSnDao;
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnSn> findListByParamToShard(WhAsnSn sn) {
        return this.whAsnSnDao.findListByParam(sn);
    }

}
