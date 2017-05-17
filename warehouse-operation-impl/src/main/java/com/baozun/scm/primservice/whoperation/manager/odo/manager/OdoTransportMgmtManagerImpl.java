package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportServiceDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportService;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;

@Service("odoTransportMgmtManager")
@Transactional
public class OdoTransportMgmtManagerImpl extends BaseManagerImpl implements OdoTransportMgmtManager {
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private WhOdoTransportServiceDao whOdoTransportServiceDao;
    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId) {
        return this.whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateOdoTransportMgmt(WhOdoTransportMgmt tranMgmt) {
        return whOdoTransportMgmtDao.saveOrUpdate(tranMgmt);
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateTransportService(Long odoId, boolean flag, int index, String errorMsg, Long ouId) {
        WhOdoTransportService transportService = whOdoTransportServiceDao.findByOdoIdAndOuId(odoId, ouId);
        if (null == transportService) {
            transportService = new WhOdoTransportService();
            transportService.setOdoId(odoId);
            transportService.setOuId(ouId);
            transportService.setIsVasSuccess(false);
            transportService.setIsTspSuccess(false);
            transportService.setIsWaybillCodeSuccess(false);
            if (index == 1) {
                transportService.setIsVasSuccess(flag);
                transportService.setVasErrorCode(flag ? null : errorMsg);
            } else if (index == 2) {
                transportService.setIsTspSuccess(flag);
                transportService.setTspErrorCode(flag ? null : errorMsg);
            } else if (index == 3) {
                transportService.setIsWaybillCodeSuccess(flag);
                transportService.setWaybillCodeErrorCode(flag ? null : errorMsg);
            }
            whOdoTransportServiceDao.insert(transportService);
        } else {
            if (index == 1) {
                transportService.setIsVasSuccess(flag);
                transportService.setVasErrorCode(flag ? null : errorMsg);
            } else if (index == 2) {
                transportService.setIsTspSuccess(flag);
                transportService.setTspErrorCode(flag ? null : errorMsg);
            } else if (index == 3) {
                transportService.setIsWaybillCodeSuccess(flag);
                transportService.setWaybillCodeErrorCode(flag ? null : errorMsg);
            }
            whOdoTransportServiceDao.updateWhOdoTransportService(transportService);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateOdoTransportMgmtAndSaveDeliveryInfo(WhOdoTransportMgmt transMgmt, WhOdodeliveryInfo delivery) {
        whOdoTransportMgmtDao.saveOrUpdate(transMgmt);
        whOdoDeliveryInfoDao.insert(delivery);
        this.saveOrUpdateTransportService(transMgmt.getOdoId(), true, 3, null, transMgmt.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateOdoTransportMgmtExt(WhOdoTransportMgmt transMgmt) {
        int num = whOdoTransportMgmtDao.saveOrUpdate(transMgmt);
        this.saveOrUpdateTransportService(transMgmt.getOdoId(), true, 2, null, transMgmt.getOuId());
        return num;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoTransportService findTransportMgmtServiceByOdoIdOuId(Long odoId, Long ouId) {
        return whOdoTransportServiceDao.findByOdoIdAndOuId(odoId, ouId);
    }

}
