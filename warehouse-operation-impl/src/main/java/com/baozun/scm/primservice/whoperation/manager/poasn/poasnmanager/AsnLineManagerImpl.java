package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

@Service("asnLineManager")
@Transactional
public class AsnLineManagerImpl implements AsnLineManager {
    protected static final Logger log = LoggerFactory.getLogger(AsnLineManager.class);
    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhAsnDao whAsnDao;

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

    /**
     * @author yimin.lu 当PO单INFO库时，修改ASN单明细，并修改相关单据数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnLineToShard(WhAsn asn, WhAsnLine asnLine) {
        log.info(this.getClass().getSimpleName() + ".editAsnLineToShard method begin!");
        if (log.isDebugEnabled()) {
            log.debug("prams: [asn:{},asnline:{}]", asn, asnLine);
        }
        // 更新ASN单明细
        int updateCount=this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
        if(updateCount<=0){
            log.warn("saveorupdatebyVersion asn line returns:{};details: [poline_id:{},poline:{}]", updateCount, asnLine.getId(), asnLine);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入日志
        this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        // 更新ASN单表头数据
        updateCount = this.whAsnDao.saveOrUpdateByVersion(asn);
        if (updateCount <= 0) {
            log.warn("saveorupdatebyVersion asn returns:{};details: [poline_id:{},poline:{}]", updateCount, asn.getId(), asn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        this.insertGlobalLog(asn.getModifiedId(), new Date(), asn.getClass().getSimpleName(), asn, Constants.GLOBAL_LOG_UPDATE, asn.getOuId());
        log.info(this.getClass().getSimpleName() + ".editAsnLineToShard method end!");
    }

    /**
     * @author yimin.lu 当PO单SHARD库时，修改ASN单明细，并修改相关单据数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnLineWhenPoToShard(WhAsn asn, WhAsnLine asnLine, WhPoLine poline) {
        log.info(this.getClass().getSimpleName() + ".editAsnLineWhenPoToShard method begin!");
        if (log.isDebugEnabled()) {
            log.debug("prams: [asn:{},asnline:{},poline:{}]", asn, asnLine, poline);
        }
        // 更新asnline
        int updateCount=this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
        if(updateCount<=0){
            log.warn("saveorupdatebyVersion asn line returns:{};details: [poline_id:{},poline:{}]", updateCount, asnLine.getId(), asnLine);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        // 更新asn
        updateCount = this.whAsnDao.saveOrUpdateByVersion(asn);
        if (updateCount <= 0) {
            log.warn("saveorupdatebyVersion asn returns:{};details: [poline_id:{},poline:{}]", updateCount, asn.getId(), asn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        this.insertGlobalLog(asn.getModifiedId(), new Date(), asn.getClass().getSimpleName(), asn, Constants.GLOBAL_LOG_UPDATE, asn.getOuId());
        // 更新poline
        int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
        if (count <= 0) {
            log.warn("saveorupdatebyVersion po line returns:{};details: [poline_id:{},poline:{}]", count, poline.getId(), poline);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        this.insertGlobalLog(poline.getModifiedId(), new Date(), poline.getClass().getSimpleName(), poline, Constants.GLOBAL_LOG_UPDATE, poline.getOuId());
        log.info(this.getClass().getSimpleName() + ".editAsnLineWhenPoToShard method end!");
    }

    /**
     * @author yimin.lu 当PO单INFO库时候批量删除Asn单明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void batchDeleteWhenPoToInfo(List<WhAsnLine> asnlineList, WhAsn asn) {
        log.info(this.getClass().getSimpleName() + ".batchDeleteWhenPoToInfo method begin!");
        if (log.isDebugEnabled()) {
            log.debug("params:[asn:{},asnlineList:{}]", asn, asnlineList);
        }
        // 更新ASN
        int count = this.whAsnDao.saveOrUpdateByVersion(asn);
        if (count <= 0) {
            log.warn("saveorupdatebyVersion asn returns:{};details: [poline_id:{},poline:{}]", count, asn.getId(), asn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入操作日志
        this.insertGlobalLog(asn.getModifiedId(), new Date(), asn.getClass().getSimpleName(), asn, Constants.GLOBAL_LOG_UPDATE, asn.getOuId());
        // 循环更新ASNLINE
        for (WhAsnLine asnLine : asnlineList) {
            int updateCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
            if (updateCount <= 0) {
                log.warn("saveorupdatebyVersion asn line returns:{};details: [poline_id:{},poline:{}]", updateCount, asnLine.getId(), asnLine);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        }
        log.info(this.getClass().getSimpleName() + ".batchDeleteWhenPoToInfo method end!");
    }

    /**
     * @author yimin.lu 当PO单SHARD库时候批量删除Asn单明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void batchDeleteWhenPoToShard(List<WhAsnLine> asnlineList, List<WhPoLine> polineList, WhAsn asn) {
        log.info(this.getClass().getSimpleName() + ".batchDeleteWhenPoToShard method begin!");
        if (log.isDebugEnabled()) {
            log.debug("params:[asn:{},asnlineList:{},polineList:{}]", asn, asnlineList, polineList);
        }
        // 更新ASN
        int asncount = this.whAsnDao.saveOrUpdateByVersion(asn);
        if (asncount <= 0) {
            log.warn("saveorupdatebyVersion asn returns:{};details: [poline_id:{},poline:{}]", asncount, asn.getId(), asn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        this.insertGlobalLog(asn.getModifiedId(), new Date(), asn.getClass().getSimpleName(), asn, Constants.GLOBAL_LOG_UPDATE, asn.getOuId());
        // 循环更新ASNLINE
        for (WhAsnLine asnLine : asnlineList) {
            int updateCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
            if (updateCount <= 0) {
                log.warn("saveorupdatebyVersion asn line returns:{};details: [poline_id:{},poline:{}]", updateCount, asnLine.getId(), asnLine);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(asnLine.getModifiedId(), new Date(), asnLine.getClass().getSimpleName(), asnLine, Constants.GLOBAL_LOG_UPDATE, asnLine.getOuId());
        }
        // 循环更新POLINE
        for (WhPoLine poline : polineList) {
            int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
            if (count <= 0) {
                log.warn("saveorupdatebyVersion po line returns:{};details: [poline_id:{},poline:{}]", count, poline.getId(), poline);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(poline.getModifiedId(), new Date(), poline.getClass().getSimpleName(), poline, Constants.GLOBAL_LOG_UPDATE, poline.getOuId());
        }
        log.info(this.getClass().getSimpleName() + ".batchDeleteWhenPoToShard method end!");
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
        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method begin!");
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(userId);
        gl.setModifyTime(modifyTime);
        gl.setObjectType(objectType);
        gl.setModifiedValues(modifiedValues);
        gl.setType(type);
        gl.setOuId(ouId);
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".insertGlobalLog method returns:{}!", gl);
        }
        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method end!");
        try {
            globalLogManager.insertGlobalLog(gl);
        } catch (Exception e) {
            log.error("insert global log error:{}", e);
            throw new BusinessException(ErrorCodes.INSERT_LOG_ERROR);
        }

    }
}
