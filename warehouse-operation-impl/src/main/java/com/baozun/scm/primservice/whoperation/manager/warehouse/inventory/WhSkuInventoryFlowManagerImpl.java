package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.Date;

import lark.common.annotation.MoreDB;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryFlowDao;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryFlow;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Transactional
@Service("whSkuInventoryFlowManager")
public class WhSkuInventoryFlowManagerImpl implements WhSkuInventoryFlowManager {

    @Autowired
    private WhSkuInventoryFlowDao whSkuInventoryFlowDao;
    @Autowired
    private WhOdoDao whOdoDao;

    /**
     * 保存库存流水信息
     * 
     * @param log
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertWhSkuInventoryFlow(WhSkuInventoryLog log) {
        // 插入库存流水信息
        WhSkuInventoryFlow flow = new WhSkuInventoryFlow();
        BeanUtils.copyProperties(flow, log);
        flow.setUpc(log.getExtCode());
        flow.setCreateTime(new Date());
        flow.setRevisionQty(log.getRevisionQty());
        // 判断是否存在出库单占用
        if (!StringUtil.isEmpty(log.getOccupationCode())) {
            // 查询对应出库单信息
            WhOdo odo = whOdoDao.findOdoByCodeAndOuId(log.getOccupationCode(), log.getOuId());
            if (null != odo) {
                // 有出库单信息 插入电商平台订单号+出库单类型
                flow.setEcOrderCode(odo.getEcOrderCode());
                flow.setOdoType(odo.getOdoType());
            }
        }
        whSkuInventoryFlowDao.insert(flow);
    }
}
