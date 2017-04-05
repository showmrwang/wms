package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("whSkuInventorySnManager")
@Transactional
public class WhSkuInventorySnManagerImpl extends BaseManagerImpl implements WhSkuInventorySnManager {

    protected static final Logger log = LoggerFactory.getLogger(WhSkuInventorySnManager.class);

    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;

    /**
     * 根据库存UUID查找对应SN/残次信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventorySnCommand> findWhSkuInventoryByUuid(Long ouid, String uuid) {
        return whSkuInventorySnDao.findWhSkuInventoryByUuid(ouid, uuid);
    }

    @Override
    public void saveOrUpdate(WhSkuInventorySnCommand whSkuInventorySnCommand) {
        // TODO Auto-generated method stub
        
    }

}
