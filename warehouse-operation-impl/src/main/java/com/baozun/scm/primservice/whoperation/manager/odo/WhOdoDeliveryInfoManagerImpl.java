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
        int cnt = whOdoDeliveryInfoDao.saveOrUpdateByVersion(whOdodeliveryInfo);
        if (0 < cnt) {
            // throw new BusinessException("更新运单表失败");
            return null;
        }
        return whOdodeliveryInfo;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdodeliveryInfo> findByParams(WhOdodeliveryInfo whOdodeliveryInfo) {
        List<WhOdodeliveryInfo> list = whOdoDeliveryInfoDao.findListByParam(whOdodeliveryInfo);
        return list;
    }
}
