package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

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
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

@Service("asnLineManager")
@Transactional
public class AsnLineManagerImpl extends BaseManagerImpl implements AsnLineManager {

    protected static final Logger log = LoggerFactory.getLogger(AsnLineManager.class);

    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhAsnDao whAsnDao;

    // TODO 更新POLINE 时系统日志需要增加PO的CODE 卢义敏
    // TODO 更新ASNLINE 时系统日志需要增加ASN的CODE 卢义敏

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        log.info("begin!");
        log.debug("logId:{},params:{}", this.getLogId(), params);
        return whAsnLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLine> findListByShard(WhAsnLine asnLine) {
        log.info("begin!");
        log.debug("logId:{},asnLine:{}", this.getLogId(), asnLine);
        return this.whAsnLineDao.findListByParam(asnLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnLineCommand findWhAsnLineCommandByIdToShard(Long id, Long ouId) {
        return this.whAsnLineDao.findWhAsnLineCommandByIdOuId(id, ouId);
    }

    /**
     * @author yimin.lu 当PO单SHARD库时，修改ASN单明细，并修改相关单据数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnLineWhenPoToShard(WhAsn asn, WhAsnLine asnLine, WhPoLine poline) {
        log.info(this.getClass().getSimpleName() + ".editAsnLineWhenPoToShard method begin!");
        log.debug("prams: [asn:{},asnline:{},poline:{}]", asn, asnLine, poline);
        // 更新asnline
        int updateCount = this.whAsnLineDao.saveOrUpdateByVersion(asnLine);
        if (updateCount <= 0) {
            log.warn("saveorupdatebyVersion asn line returns:{};details: [poline_id:{},poline:{}]", updateCount, asnLine.getId(), asnLine);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        insertGlobalLog(GLOBAL_LOG_UPDATE, asnLine, asnLine.getOuId(), asnLine.getModifiedId(), asn.getAsnCode(), null);
        // 更新asn
        updateCount = this.whAsnDao.saveOrUpdateByVersion(asn);
        if (updateCount <= 0) {
            log.warn("saveorupdatebyVersion asn returns:{};details: [poline_id:{},poline:{}]", updateCount, asn.getId(), asn);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        insertGlobalLog(GLOBAL_LOG_UPDATE, asn, asn.getOuId(), asn.getModifiedId(), null, null);
        // 更新poline
        int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
        if (count <= 0) {
            log.warn("saveorupdatebyVersion po line returns:{};details: [poline_id:{},poline:{}]", count, poline.getId(), poline);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 写入操作日志
        insertGlobalLog(GLOBAL_LOG_UPDATE, poline, poline.getOuId(), poline.getModifiedId(), null, null);
        log.info(this.getClass().getSimpleName() + ".editAsnLineWhenPoToShard method end!");
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
        insertGlobalLog(GLOBAL_LOG_UPDATE, asn, asn.getOuId(), asn.getModifiedId(), null, null);
        // 循环更新ASNLINE
        for (WhAsnLine asnLine : asnlineList) {
            int updateCount = this.whAsnLineDao.deleteByIdOuId(asnLine.getId(), asnLine.getOuId());
            if (updateCount <= 0) {
                log.warn("saveorupdatebyVersion asn line returns:{};details: [poline_id:{},poline:{}]", updateCount, asnLine.getId(), asnLine);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            insertGlobalLog(GLOBAL_LOG_DELETE, asnLine, asnLine.getOuId(), asnLine.getModifiedId(), asn.getAsnCode(), null);
        }
        // 循环更新POLINE
        for (WhPoLine poline : polineList) {
            int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
            if (count <= 0) {
                log.warn("saveorupdatebyVersion po line returns:{};details: [poline_id:{},poline:{}]", count, poline.getId(), poline);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            insertGlobalLog(GLOBAL_LOG_UPDATE, poline, poline.getOuId(), poline.getModifiedId(), null, null);
        }
        log.info(this.getClass().getSimpleName() + ".batchDeleteWhenPoToShard method end!");
    }


    /***
     * 通过ASNID+SKUID获取ASN明细可拆商品明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLineCommand> findWhAsnLineCommandDevanningList(Long asnid, Long ouid, Long skuid, Long id) {
        log.info("begin!");
        log.debug("asnId:{},ouId:{},skuId:{},id:{}", asnid, ouid, skuid, id);
        return whAsnLineDao.findWhAsnLineCommandDevanningList(id, asnid, ouid, skuid);
    }

    /**
     * 获取ASN拆箱对应商品明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnLineCommand findWhAsnLineCommandEditDevanning(WhAsnLineCommand whAsnLine) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineCommandEditDevanning method begin!");
        if (log.isDebugEnabled()) {
            log.debug("params:[whAsnLine:{}]", whAsnLine.toString());
        }
        WhAsnLineCommand asn = whAsnLineDao.findWhAsnLineCommandByIdOuId(whAsnLine.getId(), whAsnLine.getOuId());
        if (null == asn) {
            log.warn("findWhAsnLineCommandEditDevanning asn is null logid: " + whAsnLine.getLogId());
            throw new BusinessException(ErrorCodes.ASNLINE_NULL);
        }
        if (!asn.getStatus().equals(PoAsnStatus.ASNLINE_NOT_RCVD)) {
            // 如果ASNLINE单据状态不为未收货 抛出异常
            log.warn("findWhAsnLineCommandEditDevanning asn Status error asn.getStatus():" + asn.getStatus() + " logid: " + whAsnLine.getLogId());
            throw new BusinessException(ErrorCodes.ASNLINE_STATUS_ERROR);
        }
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineCommandEditDevanning method begin!");
        return whAsnLineDao.findWhAsnLineCommandEditDevanning(whAsnLine.getId(), whAsnLine.getAsnId(), whAsnLine.getOuId(), whAsnLine.getSkuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateByVersion(WhAsnLine line) {
        log.info("begin!");
        log.debug("whAsnLine:{}", line);
        try {
            int updateCount = this.whAsnLineDao.saveOrUpdateByVersion(line);
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            WhAsn asn = this.whAsnDao.findWhAsnById(line.getAsnId(), line.getOuId());
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, line, line.getOuId(), line.getModifiedId(), asn.getAsnCode(), null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    /**
     * 校验sku是否在asn中
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public boolean checkAsnSku(String occupationCode, String skuCode, Long ouId) {
        int res = this.whAsnLineDao.checkAsnSku(occupationCode, skuCode, ouId);
        boolean flag = (0 == res) ? false : true;
        return flag;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnLine findWhAsnLineByIdToShard(Long id, Long ouid) {
        return this.whAsnLineDao.findWhAsnLineById(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public long findListCountByParam(WhAsnLine searchAsnLine) {
        return this.whAsnLineDao.findListCountByParam(searchAsnLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap) {
        return whAsnLineDao.findListByQueryMapWithPageExtForCreateAsn(page, sorts, paraMap);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnLine findWhAsnLineByPoLineIdAndUuidAndOuId(Long polineId, String uuid, Long ouId) {
        return this.whAsnLineDao.findWhAsnLineByPoLineIdAndUuidAndOuId(polineId, uuid, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLine> findWhAsnLineByAsnIdOuIdUuid(Long asnId, Long ouId, String uuid) {
        return this.whAsnLineDao.findWhAsnLineByAsnIdOuIdUuid(asnId, ouId, uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteWhAsnByIdOuIdToShard(Long id, Long ouId, Long userId) {
        log.info("begin!");
        log.debug("params:[id:{},ouId:{}]", id, ouId);
        try{
            WhAsnLine line = this.findWhAsnLineByIdToShard(id, ouId);
            int deleteCount = this.whAsnLineDao.deleteByIdOuId(id, ouId);
            if (deleteCount <= 0) {
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }
            WhAsn asn = this.whAsnDao.findWhAsnById(line.getAsnId(), ouId);
            this.insertGlobalLog(GLOBAL_LOG_DELETE, line, ouId, userId, asn.getAsnCode(), null);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("" + e);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLine> findTempWhAsnLineByAsnIdOuIdNotUuid(Long asnId, Long ouId, String uuid) {
        return this.whAsnLineDao.findTempWhAsnLineByAsnIdOuIdNotUuid(asnId, ouId, uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLine> findWhAsnLineByAsnIdOuIdToShard(Long asnId, Long ouId) {
        return this.whAsnLineDao.findWhAsnLineByAsnIdOuId(asnId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnLine> findListByParamExt(WhAsnLine asnLine) {
        return this.whAsnLineDao.findListByParamExt(asnLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnLine findWhAsnLineByAsnIdPolineIdOuIdAndUuid(Long asnId, Long poLineId, Long ouId, String uuid) {
        return this.whAsnLineDao.findWhAsnLineByAsnIdPolineIdOuIdAndUuid(asnId, poLineId, ouId, uuid);
    }

}
