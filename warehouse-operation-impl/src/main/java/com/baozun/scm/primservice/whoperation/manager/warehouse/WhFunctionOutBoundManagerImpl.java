package com.baozun.scm.primservice.whoperation.manager.warehouse;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionOutBoundDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;

/**
 * 功能查询和维护:出库
 * @author Administrator
 *
 */
@Service("whFunctionOutBoundManager")
@Transactional
public class WhFunctionOutBoundManagerImpl extends BaseManagerImpl implements WhFunctionOutBoundManager {


    @Autowired
    private WhFunctionOutBoundDao whFunctionOutBoundDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionOutBound findByFunctionIdExt(Long functionId, Long ouId) {
        WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
        return whFunctionOutBound;
    }

}
