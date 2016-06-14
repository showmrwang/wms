package com.baozun.scm.primservice.whoperation.manager.warehouse.sortation;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.sortation.WhContainerAssignDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("whContainerAssignManager")
@Transactional
public class WhContainerAssignManagerImpl extends BaseManagerImpl implements WhContainerAssignManager {

    @Autowired
    private WhContainerAssignDao whContainerAssignDao;

    /**
     * 删除对应入库分拣相同商品属性对应目标容器表数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int deleteWhContainerAssign(Long ouid, Long containerId) {
        return whContainerAssignDao.deleteWhContainerAssign(ouid, containerId);
    }
}
