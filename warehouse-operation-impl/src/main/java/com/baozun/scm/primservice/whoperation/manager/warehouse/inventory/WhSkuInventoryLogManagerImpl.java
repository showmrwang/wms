package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import lark.common.annotation.MoreDB;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

@Transactional
@Service("whSkuInventoryLogManager")
public class WhSkuInventoryLogManagerImpl implements WhSkuInventoryLogManager {

    @Autowired
    private WhSkuInventoryLogDao whSkuInventoryLogDao;

    /**
     * 插入库存日志表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertSkuInventoryLog(WhSkuInventoryLog skuInvLog) {
        WhSkuInventoryLog log = new WhSkuInventoryLog();
        BeanUtils.copyProperties(skuInvLog, log);
        log.setSysDate(DateUtil.getSysDate());
        whSkuInventoryLogDao.insert(log);
    }


    /**
     * 通过库存ID 查询出对应库存日志封装数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSkuInventoryLog findInventoryLogBySkuInvId(Long skuInvId, Long ouid) {
        return whSkuInventoryLogDao.findInventoryLogBySkuInvId(skuInvId, ouid);
    }

    /**
     * 通过UUID 查询对应库存的所有库存记录
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Double sumSkuInvOnHandQty(String uuid, Long ouid) {
        return whSkuInventoryLogDao.sumSkuInvOnHandQty(uuid, ouid);
    }

}
