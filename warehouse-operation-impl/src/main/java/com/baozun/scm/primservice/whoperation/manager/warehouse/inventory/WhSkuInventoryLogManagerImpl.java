package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.text.SimpleDateFormat;
import java.util.Date;

import lark.common.annotation.MoreDB;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;

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
        log.setSysDate(getSysDate());
        whSkuInventoryLogDao.insert(log);
    }

    /**
     * 获取当前时间用于判断插入月份LOG表
     * 
     * @return
     */
    public String getSysDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM");// 设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
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
