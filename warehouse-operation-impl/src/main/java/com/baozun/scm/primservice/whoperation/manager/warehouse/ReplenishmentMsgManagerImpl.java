package com.baozun.scm.primservice.whoperation.manager.warehouse;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentMsgDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;

@Transactional
@Service("replenishmentMsgManager")
public class ReplenishmentMsgManagerImpl extends BaseManagerImpl implements ReplenishmentMsgManager {
    @Autowired
    private ReplenishmentMsgDao replenishmentMsgDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ReplenishmentMsg findMsgbyLocIdAndSkuId(Long locId, Long skuId, Long ouId) {
        return this.replenishmentMsgDao.findByLocIdAndSkuId(locId, skuId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteById(Long id, Long ouId) {
        try {
            int delcount = this.replenishmentMsgDao.deleteByIdOuId(id, ouId);
            if (delcount <= 0) {
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insert(ReplenishmentMsg msg) {
        try {
            this.replenishmentMsgDao.insert(msg);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateByVersion(ReplenishmentMsg msg) {
        try {
            int count = this.replenishmentMsgDao.saveOrUpdateByVersion(msg);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }

    }

}
