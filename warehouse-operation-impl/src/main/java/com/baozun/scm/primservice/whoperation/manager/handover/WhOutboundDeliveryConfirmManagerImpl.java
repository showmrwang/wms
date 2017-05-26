package com.baozun.scm.primservice.whoperation.manager.handover;


import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.handover.WhOutboundDeliveryConfirmDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.handover.WhOutboundDeliveryConfirm;

@Service("whOutboundDeliveryConfirmManager")
@Transactional
public class WhOutboundDeliveryConfirmManagerImpl extends BaseManagerImpl implements WhOutboundDeliveryConfirmManager {
    @Autowired
    private WhOutboundDeliveryConfirmDao whOutboundDeliveryConfirmDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutboundDeliveryConfirm> findAll() {
        return whOutboundDeliveryConfirmDao.findListByParam(null);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdate(WhOutboundDeliveryConfirm whOutboundDeliveryConfirm) {
        whOutboundDeliveryConfirmDao.saveOrUpdate(whOutboundDeliveryConfirm);
    }

}
