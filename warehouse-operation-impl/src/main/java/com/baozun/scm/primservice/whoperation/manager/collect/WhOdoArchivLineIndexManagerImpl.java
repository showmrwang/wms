package com.baozun.scm.primservice.whoperation.manager.collect;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.collect.WhOdoArchivLineIndexDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;

@Service("whOdoArchivLineIndexManager")
@Transactional
public class WhOdoArchivLineIndexManagerImpl extends BaseManagerImpl implements WhOdoArchivLineIndexManager {
    @Autowired
    private WhOdoArchivLineIndexDao whOdoArchivLineIndexDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoArchivLineIndex findByIdToShard(Long id, Long ouId) {
        return this.whOdoArchivLineIndexDao.findByIdExt(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public void executeReturns(List<WhOdoArchivLineIndex> list) {
        for (WhOdoArchivLineIndex l : list) {
            this.whOdoArchivLineIndexDao.execute(l.getCollectTableName(), l.getCollectOdoArchivLineId(), l.getReturnedPurchaseQty());
        }

    }

}
