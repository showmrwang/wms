package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.poasn.PoAsnOuDao;
import com.baozun.scm.primservice.whoperation.model.poasn.PoAsnOu;

@Service("poAsnOuManager")
@Transactional
public class PoAsnOuManagerImpl implements PoAsnOuManager {

    @Autowired
    private PoAsnOuDao poAsnOuDao;

    /**
     * 新建关联数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void insertPoAsnOu(PoAsnOu poAsnOu) {
        // 查询对应数据
        PoAsnOu pao = poAsnOuDao.findPoAsnOuByPoIdOuId(poAsnOu.getPoId(), poAsnOu.getOuId());
        if (null == pao) {
            // 如果为空插入条新数据
            poAsnOuDao.insert(poAsnOu);
        }
    }

}
