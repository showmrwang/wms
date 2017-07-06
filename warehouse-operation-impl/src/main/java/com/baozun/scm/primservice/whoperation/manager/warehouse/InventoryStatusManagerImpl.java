package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.InventoryStatusDao;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;


@Service("inventoryStatusManager")
@Transactional
public class InventoryStatusManagerImpl implements InventoryStatusManager {
    @Autowired
    private InventoryStatusDao inventoryStatusDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<InventoryStatus> findInventoryStatusList(InventoryStatus status) {
        List<InventoryStatus> inventoryStatusList = inventoryStatusDao.findListByParam(status);
        return inventoryStatusList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public List<InventoryStatus> findAllInventoryStatus() {
        InventoryStatus status = new InventoryStatus();
        status.setLifecycle(1);
        return this.inventoryStatusDao.findListByParam(status);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public InventoryStatus findInventoryStatusByName(String invStatusName) {
        return this.inventoryStatusDao.findInventoryStatusByName(invStatusName);
    }

}
