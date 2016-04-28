package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;

@Service("BiPoManager")
@Transactional
public class BiPoManagerImpl implements BiPoManager {
    @Autowired
    private BiPoDao biPoDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<BiPo> findListByParam(BiPo biPo) {
        return this.biPoDao.findListByParam(biPo);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoById(Long id) {
        return this.biPoDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoByPoCode(String poCode) {
        return this.biPoDao.findbyPoCode(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoCommand findBiPoCommandById(Long id) {
        return this.biPoDao.findCommandbyId(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoCommand findBiPoCommandByPoCode(String poCode) {
        return this.biPoDao.findCommandbyPoCode(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<BiPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.biPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

}
