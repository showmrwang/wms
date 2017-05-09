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

import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryFlowDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
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
    @Autowired
    private WhAsnDao whAsnDao;

    /**
     * 保存库存流水信息 bin.hu
     * 
     * @param log
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertWhSkuInventoryFlow(WhSkuInventoryLog log) {
        logger.info("WhSkuInventoryFlowManagerImpl.insertWhSkuInventoryFlow begin!");
        // 校验是否需要生成库存流水信息
        boolean checkFlow = checkFlow(log);
        if (checkFlow) {
            Long count = 0L;
            // 插入库存流水信息
            WhSkuInventoryFlow flow = new WhSkuInventoryFlow();
            BeanUtils.copyProperties(log, flow);
            flow.setUpc(log.getExtCode());
            flow.setCreateTime(new Date());
            flow.setRevisionQty(log.getRevisionQty());
            // 判断是否存在出库单占用
            if (!StringUtil.isEmpty(log.getOccupationCode())) {
                flow.setEcOrderCode(log.getOccupationCode());
                // 查询对应出库单信息
                WhOdo odo = whOdoDao.findOdoByCodeAndOuId(log.getOccupationCode(), log.getOuId());
                if (null != odo) {
                    // 有出库单信息 插入电商平台订单号+出库单类型
                    flow.setEcOrderCode(odo.getEcOrderCode());
                    flow.setOdoType(odo.getOdoType());
                }
                WhAsn asn = whAsnDao.findAsnByCodeAndOuId(log.getOccupationCode(), log.getOuId());
                if (null != asn) {
                    // 有出库单信息 插入上位系统入库单号+入库单类型
                    flow.setEcOrderCode(asn.getExtCode());
                    flow.setOdoType(asn.getAsnType().toString());
                }
            }
            count = whSkuInventoryFlowDao.insert(flow);
            if (count.intValue() == 0) {
                logger.error("WhSkuInventoryFlowManagerImpl insertWhSkuInventoryFlow error");
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
        }
        logger.info("WhSkuInventoryFlowManagerImpl.insertWhSkuInventoryFlow end!");
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
    public List<WmsSkuInventoryFlow> findWmsSkuInventoryFlowByCreateTime(String beginTime, String endTime, Integer start, Integer pageSize, Long ouid) {
        return whSkuInventoryFlowDao.findWmsSkuInventoryFlowByCreateTime(beginTime, endTime, start, pageSize, ouid);
    }
}
