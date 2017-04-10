package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryFlowDao;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryFlow;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Transactional
@Service("whSkuInventoryFlowManager")
public class WhSkuInventoryFlowManagerImpl implements WhSkuInventoryFlowManager {

    protected static final Logger logger = LoggerFactory.getLogger(WhSkuInventoryFlowManagerImpl.class);

    @Autowired
    private WhSkuInventoryFlowDao whSkuInventoryFlowDao;
    @Autowired
    private WhOdoDao whOdoDao;

    /**
     * 保存库存流水信息 bin.hu
     * 
     * @param log
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertWhSkuInventoryFlow(WhSkuInventoryLog log) {
        // 校验是否需要生成库存流水信息
        boolean checkFlow = checkFlow(log);
        if (checkFlow) {
            // 插入库存流水信息
            WhSkuInventoryFlow flow = new WhSkuInventoryFlow();
            BeanUtils.copyProperties(log, flow);
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

    /**
     * 校验是否需要生成库存流水信息
     * 
     * @param log
     * @return
     */
    private boolean checkFlow(WhSkuInventoryLog log) {
        boolean b = true;
        if (log.getInvTransactionType().equals(InvTransactionType.SHELF)) {
            // 如果是上架库存事务类型 判断是否有库位信息
            if (StringUtil.isEmpty(log.getLocationCode())) {
                // 没有库位信息 不需要保存库存流水
                return false;
            }
        }
        return b;
    }

    /**
     * 通过创建时间段+仓库ID获取对应库存流水数据
     * 
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryFlow> findWhSkuInventoryFlowByCreateTime(String beginTime, String endTime, Long ouid) {
        return whSkuInventoryFlowDao.findWhSkuInventoryFlowByCreateTime(beginTime, endTime, ouid);
    }
}
