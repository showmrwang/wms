package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.logistics.model.VasTransResult.VasLine;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoVasDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;

@Service("odoVasManager")
@Transactional
public class OdoVasManagerImpl extends BaseManagerImpl implements OdoVasManager {
    @Autowired
    private WhOdoVasDao whOdoVasDao;
    @Autowired
    private OdoTransportMgmtManager odoTransportMgmtManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoVas> findOdoVasByOdoIdOdoLineIdType(Long odoId, Long odoLineId, String vasType, Long ouId) {
        return this.whOdoVasDao.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, vasType, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoVasCommand> findOdoOuVasCommandByOdoIdOdoLineIdType(Long odoId, Long odoLineId, Long ouId) {
        return this.whOdoVasDao.findOdoOuVasCommandByOdoIdOdoLineIdType(odoId, odoLineId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOdoOuVas(List<WhOdoVas> insertVasList, List<WhOdoVas> updateVasList, List<WhOdoVas> delVasList) {
        if (insertVasList != null && insertVasList.size() > 0) {
            for (WhOdoVas ov : insertVasList) {

                this.whOdoVasDao.insert(ov);
            }
        }
        if (updateVasList != null && updateVasList.size() > 0) {
            for (WhOdoVas ov : updateVasList) {

                this.whOdoVasDao.saveOrUpdate(ov);
            }
        }
        if (delVasList != null && delVasList.size() > 0) {
            for (WhOdoVas ov : delVasList) {

                this.whOdoVasDao.deleteByIdOuId(ov.getId(), ov.getOuId());
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoVasCommand> findOdoExpressVasCommandByOdoIdOdoLineId(Long odoId, Long odoLineId, Long ouId) {
        return this.whOdoVasDao.findOdoExpressVasCommandByOdoIdOdoLineId(odoId, odoLineId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertVasList(Long odoId, List<VasLine> vasList, WhOdoTransportMgmt transMgmt, Long ouId) {
        for (VasLine vasLine : vasList) {
            // 保价
            if ("INSURED".equals(vasLine.getCode())) {
                transMgmt.setInsuranceCoverage(vasLine.getInsuranceAmount().doubleValue());
                int num = odoTransportMgmtManager.updateOdoTransportMgmt(transMgmt);
                if (num < 1) {
                    throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
                }
            }
        }
    }

}
