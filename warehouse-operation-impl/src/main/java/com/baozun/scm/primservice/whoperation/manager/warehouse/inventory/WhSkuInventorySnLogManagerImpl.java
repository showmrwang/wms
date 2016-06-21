package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnLogDao;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySnLog;
import com.baozun.scm.primservice.whoperation.util.DateUtil;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Transactional
@Service("whSkuInventorySnLogManager")
public class WhSkuInventorySnLogManagerImpl implements WhSkuInventorySnLogManager {

    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhSkuInventorySnLogDao whSkuInventorySnLogDao;

    /**
     * 插入库存SN/残次日志
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertSkuInventorySnLog(String uuid, Long ouid) {
        // 获取所有该UUID库存下的SN/残次信息
        List<WhSkuInventorySnCommand> snList = whSkuInventorySnDao.findWhSkuInventoryByUuidLeftJoinForeignKey(ouid, uuid);
        for (WhSkuInventorySnCommand sn : snList) {
            WhSkuInventorySnLog snLog = new WhSkuInventorySnLog();
            BeanUtils.copyProperties(sn, snLog);
            snLog.setSysDate(DateUtil.getSysDate());
            // 判断残次类型和残次原因是属于店铺还是仓库
            if (!StringUtil.isEmpty(sn.getDefectSource())) {
                if (sn.getDefectSource().equals(Constants.SKU_SN_DEFECT_SOURCE_STORE)) {
                    // 如果是店铺 把查出来的店铺残次原因/残次类型插入日志表
                    snLog.setDefectType(sn.getStoreDefectTypeName());
                    snLog.setDefectReasons(sn.getStoreDefectReasonsName());
                }
                if (sn.getDefectSource().equals(Constants.SKU_SN_DEFECT_SOURCE_STORE)) {
                    // 如果是仓库 把查出来的仓库残次原因/残次类型插入日志表
                    snLog.setDefectType(sn.getWhDefectTypeName());
                    snLog.setDefectReasons(sn.getWhDefectReasonsName());
                }
            }
            whSkuInventorySnLogDao.insert(snLog);
        }
    }

}
