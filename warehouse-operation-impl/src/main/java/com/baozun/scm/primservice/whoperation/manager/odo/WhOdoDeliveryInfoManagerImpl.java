package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;

@Service("whOdoDeliveryInfoManager")
@Transactional
public class WhOdoDeliveryInfoManagerImpl extends BaseManagerImpl implements WhOdoDeliveryInfoManager {
    public static final Logger log = LoggerFactory.getLogger(WhOdoDeliveryInfoManagerImpl.class);

    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdodeliveryInfo> findByOdoIdWithoutOutboundbox(Long odoId, Long ouId) {
        List<WhOdodeliveryInfo> list = whOdoDeliveryInfoDao.findByOdoIdWithoutOutboundbox(odoId, ouId);
        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdodeliveryInfo saveOrUpdate(WhOdodeliveryInfo whOdodeliveryInfo) {
        if (null == whOdodeliveryInfo.getId()) {
            Long insertCnt = whOdoDeliveryInfoDao.insert(whOdodeliveryInfo);
            if (0 > insertCnt) {
                return null;
            }
        } else {
            int cnt = whOdoDeliveryInfoDao.saveOrUpdateByVersion(whOdodeliveryInfo);
            if (0 > cnt) {
                // throw new BusinessException("更新运单表失败");
                return null;
            }
        }
        return whOdodeliveryInfo;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdodeliveryInfo> findByParams(WhOdodeliveryInfo whOdodeliveryInfo) {
        List<WhOdodeliveryInfo> list = whOdoDeliveryInfoDao.findListByParam(whOdodeliveryInfo);
        return list;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdodeliveryInfo> findListByOdoId(Long odoId, Long ouId) {
        List<WhOdodeliveryInfo> ododeliveryInfoList = whOdoDeliveryInfoDao.findListByOdoId(odoId, ouId);
        return ododeliveryInfoList;
    }

    /**
     * 查询出库单下可用的运单
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    public WhOdodeliveryInfo findUseableWaybillInfoByOdoId(Long odoId, Long ouId) {
        return whOdoDeliveryInfoDao.findUseableWaybillInfoByOdoId(odoId, ouId);
    }

    /**
     * 运单号是否已被使用
     * 
     * @param waybillCode
     * @param ouId
     * @return
     */
    public Boolean checkUniqueWaybillCode(String waybillCode, Long ouId) {
        int count = whOdoDeliveryInfoDao.checkUniqueWaybillCode(waybillCode, ouId);

        return count == 0;
    }

    /**
     * 根据运单号查询运单信息
     * 
     * @param waybillCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdodeliveryInfo findByWaybillCode(String outboundboxCode, Long ouId) {
        WhOdodeliveryInfo whOdodeliveryInfo = new WhOdodeliveryInfo();
        whOdodeliveryInfo.setWaybillCode(outboundboxCode);
        whOdodeliveryInfo.setOuId(ouId);
        List<WhOdodeliveryInfo> list = whOdoDeliveryInfoDao.findListByParam(whOdodeliveryInfo);
        if (null != list && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
