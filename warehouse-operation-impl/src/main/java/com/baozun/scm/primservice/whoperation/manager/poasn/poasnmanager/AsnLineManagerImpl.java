package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;

@Service("asnLineManager")
@Transactional
public class AsnLineManagerImpl implements AsnLineManager {

    @Autowired
    private WhAsnLineDao whAsnLineDao;

    @Override
    @MoreDB("shardSource")
    public Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whAsnLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }



}
