package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("createsPoManager")
@Transactional
public class CreatesPoManagerImpl implements CreatesPoManager {

    @Autowired
    private WhPoDao whPoDao;

    /**
     * 查询po单列表(带分页)
     */
    @Override
    public Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

}
