package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

@Service("asnLineManager")
@Transactional
public class AsnLineManagerImpl implements AsnLineManager {

    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private WhPoLineDao whPoLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whAsnLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLine> findListByShard(WhAsnLine asnLine) {
        return this.whAsnLineDao.findListByParam(asnLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnLineCommand findWhAsnLineByIdToShard(WhAsnLineCommand command) {
        return this.whAsnLineDao.findWhAsnLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnLineToShard(WhAsnLine asnLine) {
        int updateCount=this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
        if(updateCount<=0){
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnLineWhenPoToShard(WhAsnLine asnLine, WhPoLine poline) {
        int updateCount=this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
        if(updateCount<=0){
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        
        int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(poline.getModifiedId(), new Date(), poline.getClass().getSimpleName(), poline, Constants.GLOBAL_LOG_UPDATE, poline.getOuId());
    }

    /**
     * 用于插入日志操作
     * 
     * @param userId
     * @param modifyTime
     * @param objectType
     * @param modifiedValues
     * @param type
     * @param ouId
     */
    private void insertGlobalLog(Long userId, Date modifyTime, String objectType, Object modifiedValues, String type, Long ouId) {
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(userId);
        gl.setModifyTime(modifyTime);
        gl.setObjectType(objectType);
        gl.setModifiedValues(modifiedValues);
        gl.setType(type);
        gl.setOuId(ouId);
        globalLogManager.insertGlobalLog(gl);

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void batchDeleteWhenPoToInfo(List<WhAsnLine> asnlineList) {
        for (WhAsnLine asnLine : asnlineList) {
            int updateCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void batchDeleteWhenPoToShard(List<WhAsnLine> asnlineList, List<WhPoLine> polineList) {
        for (WhAsnLine asnLine : asnlineList) {
            int updateCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        }
        for (WhPoLine poline : polineList) {
            int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(poline.getModifiedId(), new Date(), poline.getClass().getSimpleName(), poline, Constants.GLOBAL_LOG_UPDATE, poline.getOuId());
        }
    }

}
