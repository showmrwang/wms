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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.CheckPoCodeDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("poManager")
@Transactional
public class PoManagerImpl extends BaseManagerImpl implements PoManager {
    protected static final Logger log = LoggerFactory.getLogger(PoManager.class);
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private CheckPoCodeDao checkPoCodeDao;
    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private BiPoLineDao biPoLineDao;


    /**
     * 读取公共库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 读取拆分库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }


    /**
     * 保存po单信息
     * 
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        po.setId(null);
        long i = whPoDao.insert(po);
        if (0 == i) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");// 保存至po单信息失败
            // throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            return rm;
        }
        if (whPoLines != null && whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setId(null);
                whPoLine.setPoId(po.getId());
                whPoLineDao.insert(whPoLine);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return rm;

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        try {
            BiPo biPo = new BiPo();
            BeanUtils.copyProperties(po, biPo);
            // biPo.setId(po.getBiPoId());
            biPoDao.insert(biPo);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, biPo, po.getOuId(), po.getCreatedId(), null, DbDataSource.MOREDB_INFOSOURCE);
            if (po.getOuId() != null) {

                whPoDao.insert(po);
            }
            // 有line信息保存
            if (whPoLines != null && whPoLines.size() > 0) {
                for (WhPoLine whPoLine : whPoLines) {
                    whPoLine.setPoId(po.getId());
                    BiPoLine biPoLine = new BiPoLine();
                    BeanUtils.copyProperties(whPoLine, biPoLine);
                    biPoLineDao.insert(biPoLine);
                    // whPoLineDao.insert(whPoLine);
                    this.insertGlobalLog(GLOBAL_LOG_INSERT, biPoLine, po.getOuId(), po.getCreatedId(), po.getPoCode(), DbDataSource.MOREDB_INFOSOURCE);
                }
            }


        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(po.getId() + "");
        return rm;
    }

    /**
     * 修改公共库PO单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int editPoStatusToInfo(WhPoCommand whPo) {
        int result = whPoDao.editPoStatus(whPo.getPoIds(), whPo.getStatus(), whPo.getModifiedId(), whPo.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whPo.getPoIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whPo.getPoIds().size(), result});
        }
        return result;
    }

    /**
     * 修改拆库PO单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int editPoStatusToShard(WhPoCommand whPo) {
        int result = whPoDao.editPoStatus(whPo.getPoIds(), whPo.getStatus(), whPo.getModifiedId(), whPo.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whPo.getPoIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whPo.getPoIds().size(), result});
        }
        return result;
    }

    /**
     * 通过OP单ID查询相关信息 基础表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoCommand findWhPoCommandByIdToInfo(Long id, Long ouId) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(id, ouId);
        return whPoCommand;
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoCommand findWhPoCommandByIdToShard(Long id, Long ouId) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(id, ouId);
        return whPoCommand;
    }

    /**
     * 更新PO单信息 基础表
     * 
     * @param whPo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editPoToInfo(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 更新PO单信息 拆库表
     * 
     * @param whPo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editPoToShard(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg insertPoWithOuId(PoCheckCommand poCheckCommand) {
        WhPo whPo = poCheckCommand.getWhPo();
        List<WhPoLine> whPoLines = poCheckCommand.getWhPoLines();
        ResponseMsg rm = poCheckCommand.getRm();
        String extCode = whPo.getExtCode();
        Long storeId = whPo.getStoreId();
        Long ouId = whPo.getOuId();
        /* 查找在性对应的拆库表中是否有此po单信息 */
        long count = whPoDao.findPoByCodeAndStore(extCode, storeId, ouId);
        /* 没有此po单信息 */
        if (0 == count) {
            long i = whPoDao.insert(whPo);
            if (0 == i) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.SAVE_CHECK_TABLE_FAILED + "");// 保存至po_check信息失败
                return rm;
            }
            // whPoDao.saveOrUpdate(whpo);
            if (whPoLines.size() > 0) {
                // 有line信息保存
                for (WhPoLine whPoLine : whPoLines) {
                    whPoLine.setPoId(whPo.getId());
                    whPoLineDao.insert(whPoLine);
                }
            }
            rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            rm.setMsg(whPo.getId() + "");
        } else {
            /* 存在此po单信息 */
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.PO_EXIST + "");
            return rm;
        }
        return rm;

    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 公共库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoCommand> findWhPoListByExtCodeToInfo(String poCode, List<Integer> status, Long ouid, Integer linenum) {
        return whPoDao.findWhPoListByExtCode(status, poCode, ouid, linenum);
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status, Long ouid, Integer linenum) {
        return whPoDao.findWhPoListByExtCode(status, poCode, ouid, linenum);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhAsnByIdToInfo(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhAsnByIdToShard(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoAndPoLineToInfo(List<WhPoCommand> whPoCommand) {
        for (WhPoCommand po : whPoCommand) {
            // 删除PO表头信息
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
            // 删除POLINE明细信息
            whPoLineDao.deleteByPoIdOuId(po.getId(), po.getOuId());
            // 删除校验表数据
            CheckPoCode cpCode = new CheckPoCode();
            cpCode.setOuId(po.getOuId());
            cpCode.setPoCode(po.getPoCode());
            cpCode.setStoreId(po.getStoreId());
            checkPoCodeDao.deleteByParams(cpCode);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deletePoAndPoLineToShard(List<WhPoCommand> whPoCommand) {
        for (WhPoCommand po : whPoCommand) {
            // 删除PO表头信息
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
            // 删除POLINE明细信息
            whPoLineDao.deleteByPoIdOuId(po.getId(), po.getOuId());
        }
    }

    /**
     * 通过asn数据修改对应po单状态 只针对没有ouid的po单
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg updatePoStatusByAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm) {
        // 修改表头状态
        if (whPo.getStatus() == PoAsnStatus.PO_NEW) {
            // 如果是新建状态 改状态为已创建ASN
            whPo.setStatus(PoAsnStatus.PO_CREATE_ASN);
            whPo.setModifiedId(asn.getModifiedId());
            int result = whPoDao.saveOrUpdateByVersion(whPo);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        // 修改明细状态及可用数量
        for (WhAsnLineCommand asnline : asnLineList) {
            WhPoLine whPoLine = poLineMap.get(asnline.getPoLineId());
            // 修改poline的可用数量
            whPoLine.setAvailableQty(whPoLine.getAvailableQty() - asnline.getQtyPlanned());
            whPoLine.setModifiedId(asn.getModifiedId());
            if (whPoLine.getStatus() == PoAsnStatus.POLINE_NEW) {
                // 如果明细状态为新建的话 改成已创建ASN状态
                whPoLine.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
            }
            int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;
    }

    /**
     * 通过asn数据修改对应po单状态 只针对没有ouid的po单 一键创建ASN
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg updatePoStatusByAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines, ResponseMsg rm) {
        // 修改表头状态
        if (whpo.getStatus() == PoAsnStatus.PO_NEW) {
            // 如果是新建状态 改状态为已创建ASN
            whpo.setStatus(PoAsnStatus.PO_CREATE_ASN);
            whpo.setModifiedId(asn.getModifiedId());
            int result = whPoDao.saveOrUpdateByVersion(whpo);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        // 修改明细状态和可用数量
        for (WhPoLine pl : whPoLines) {
            // 修改poline的可用数量
            pl.setAvailableQty(0.0);// 一键创建asnline poline的可用数量0
            pl.setModifiedId(asn.getModifiedId());
            if (pl.getStatus() == PoAsnStatus.POLINE_NEW) {
                // 如果明细状态为新建的话 改成已创建ASN状态
                pl.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
            }
            int result = whPoLineDao.saveOrUpdateByVersion(pl);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveOrUpdateByVersionToInfo(WhPo o) {
        int count = this.whPoDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入操作日志
        this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhPo o) {
        int count = this.whPoDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入操作日志
        this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
    }

    /**
     * INFO库取消PO单;取消PO单的同时需要取消所有的明细；PO单状态为新建的时候可以取消
     * 
     * @author yimin.lu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void cancelPoToInfo(List<WhPo> poList) {
        log.info(this.getClass().getSimpleName() + ".cancelPoToInfo method begin");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPoToInfo params:{}", poList);
        }
        // 循环需要更新的PO单集合
        for (WhPo whPo : poList) {
            if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
                // 将对应的PO单更新状态为取消
                whPo.setStatus(PoAsnStatus.PO_CANCELED);
                int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
                if (poCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(whPo.getModifiedId(), new Date(), whPo.getClass().getSimpleName(), whPo, Constants.GLOBAL_LOG_UPDATE, whPo.getOuId());
                // 循环取消对应的Po单明细
                List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(whPo.getId(), whPo.getOuId(), null);
                for (WhPoLine line : lineList) {
                    line.setStatus(PoAsnStatus.POLINE_CANCELED);
                    line.setModifiedId(whPo.getModifiedId());
                    int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (lineCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    // 插入操作日志
                    this.insertGlobalLog(line.getModifiedId(), new Date(), line.getClass().getSimpleName(), line, Constants.GLOBAL_LOG_UPDATE, line.getOuId());
                }
            } else {
                throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whPo.getPoCode()});
            }
        }
    }

    /**
     * INFO库取消PO单;取消PO单的同时需要取消所有的明细
     * 
     * @author yimin.lu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelPoToShard(List<WhPo> poList) {
        log.info(this.getClass().getSimpleName() + ".cancelPoToShard method begin");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPoToShard params:{}", poList);
        }
        // 循环需要更新的PO单集合
        for (WhPo whPo : poList) {
            if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
                // 将对应的PO单更新状态为取消
                whPo.setStatus(PoAsnStatus.PO_CANCELED);
                int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
                if (poCount == 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(whPo.getModifiedId(), new Date(), whPo.getClass().getSimpleName(), whPo, Constants.GLOBAL_LOG_UPDATE, whPo.getOuId());
                // 循环取消对应的Po单明细
                List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(whPo.getId(), whPo.getOuId(), null);
                for (WhPoLine line : lineList) {
                    line.setStatus(PoAsnStatus.POLINE_CANCELED);
                    line.setModifiedId(whPo.getModifiedId());
                    int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (lineCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    // 插入操作日志
                    this.insertGlobalLog(line.getModifiedId(), new Date(), line.getClass().getSimpleName(), line, Constants.GLOBAL_LOG_UPDATE, line.getOuId());
                }
            } else {
                throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR);
            }
        }
    }

    /**
     * @author yimin.lu 删除ASN单的时候，对应的PO单在INFO库的情况下，更新PO单和对应的PO单明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editPoAdnPoLineWhenDeleteAsnToInfo(WhPoCommand whpo, List<WhPoLine> polineList) {
        log.info(this.getClass().getSimpleName() + ".editPoAdnPoLineWhenDeleteAsnToInfo method begin!");
        try {
            // 保存PO
            WhPo po = new WhPo();
            BeanUtils.copyProperties(whpo, po);
            int poUpdateCount = this.whPoDao.saveOrUpdateByVersion(po);
            if (poUpdateCount <= 0) {
                log.warn("edit linked po error!");
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(po.getModifiedId(), new Date(), po.getClass().getSimpleName(), po, Constants.GLOBAL_LOG_UPDATE, po.getOuId());
            for (WhPoLine poLine : polineList) {
                int lineCount = this.whPoLineDao.saveOrUpdateByVersion(poLine);
                if (lineCount <= 0) {
                    log.warn("edit linked poline error!");
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(poLine.getModifiedId(), new Date(), poLine.getClass().getSimpleName(), poLine, Constants.GLOBAL_LOG_UPDATE, poLine.getOuId());
            }
        } catch (Exception e) {
            log.error(" update info.po and info.poline when deleting asn throws error:{}!", e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        log.info(this.getClass().getSimpleName() + ".editPoAdnPoLineWhenDeleteAsnToInfo method end!");
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
        try {
            GlobalLogCommand gl = new GlobalLogCommand();
            gl.setModifiedId(userId);
            gl.setModifyTime(modifyTime);
            gl.setObjectType(objectType);
            gl.setModifiedValues(modifiedValues);
            gl.setType(type);
            gl.setOuId(ouId);
            globalLogManager.insertGlobalLog(gl);
        } catch (Exception e) {
            log.error(" insert into global log error:{}!", e);
            throw new BusinessException(ErrorCodes.INSERT_LOG_ERROR);
        }

        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deleteCheckPoCodeToInfo(List<CheckPoCode> poCodeList, Long userId) {
        log.info(this.getClass().getSimpleName() + ".deleteCheckPoCodeToInfo method begin");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".deleteCheckPoCodeToInfo params:{}", poCodeList);
        }
        for (CheckPoCode poCode : poCodeList) {
            checkPoCodeDao.deleteByParams(poCode);
            // 插入操作日志
            this.insertGlobalLog(userId, new Date(), poCode.getClass().getSimpleName(), poCode, Constants.GLOBAL_LOG_DELETE, poCode.getOuId());
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhPoByPoCodeOuIdToShard(String poCode, Long ouId) {
        return this.whPoDao.findByPoCodeAndOuId(poCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhPoByPoCodeOuIdToInfo(String poCode, Long ouId) {
        return this.whPoDao.findByPoCodeAndOuId(poCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createWhPoToInfo(WhPo shardpo, List<WhPoLine> whpolineList) {
        // 集团下BIPO创建子PO单
        // 1.如果po单状态为新建的话，置为已分配到仓库状态；其余状态不变】

        // 更改BIPO状态
        BiPo bipo = this.biPoDao.findbyPoCode(shardpo.getPoCode());
        if (PoAsnStatus.BIPO_NEW == bipo.getStatus()) {
            bipo.setStatus(PoAsnStatus.BIPO_ALLOT);
            int bipoCount = this.biPoDao.saveOrUpdateByVersion(bipo);
            if (bipoCount < 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        // IF逻辑：如果仓库没有分配过
        if (null == shardpo.getId()) {
            this.whPoDao.insert(shardpo);
            for (WhPoLine line : whpolineList) {
                // 更改BIPO明细
                BiPoLine bipoline = this.biPoLineDao.findById(line.getPoLineId());
                if (bipoline.getAvailableQty() < line.getQtyPlanned()) {
                    throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
                }
                bipoline.setAvailableQty(bipoline.getAvailableQty() - line.getQtyPlanned());
                if (PoAsnStatus.BIPOLINE_NEW == bipoline.getStatus()) {
                    bipoline.setStatus(PoAsnStatus.BIPO_ALLOT);
                }
                bipoline.setModifiedId(shardpo.getModifiedId());
                this.biPoLineDao.saveOrUpdateByVersion(bipoline);

                // 更改INFO.WHPO明细
                line.setPoId(shardpo.getId());
                this.whPoLineDao.insert(line);
            }
            // ELSE:此仓库已经分配过PO
        } else {
            int pocount = this.whPoDao.saveOrUpdateByVersion(shardpo);
            if (pocount < 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            for (WhPoLine line : whpolineList) {

                BiPoLine bipoline = this.biPoLineDao.findById(line.getPoLineId());
                bipoline.setAvailableQty(bipoline.getAvailableQty() - line.getQtyPlanned());
                bipoline.setModifiedId(shardpo.getModifiedId());
                if (PoAsnStatus.BIPOLINE_NEW == bipoline.getStatus()) {
                    bipoline.setStatus(PoAsnStatus.BIPO_ALLOT);
                }
                this.biPoLineDao.saveOrUpdateByVersion(bipoline);

                if (null == line.getId()) {
                    line.setPoId(shardpo.getId());
                    this.whPoLineDao.insert(line);
                } else {
                    int linecount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (linecount < 1) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createWhPoToShard(WhPo shardpo, List<WhPoLine> whpolineList) {
        if (null == shardpo.getId()) {
            this.whPoDao.insert(shardpo);
            for (WhPoLine line : whpolineList) {
                line.setPoId(shardpo.getId());
                this.whPoLineDao.insert(line);
            }
        } else {
            int pocount = this.whPoDao.saveOrUpdateByVersion(shardpo);
            if (pocount < 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            for (WhPoLine line : whpolineList) {
                if (null == line.getId()) {
                    line.setPoId(shardpo.getId());
                    this.whPoLineDao.insert(line);
                } else {
                    int linecount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (linecount < 1) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPo> findWhPoByPoCodeToInfo(String poCode) {
        return this.whPoDao.findWhPoByPoCodeToInfo(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoCommand findWhPoByIdToInfo(WhPoCommand command) {
        return this.whPoDao.findWhPoCommandById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoCommand findWhPoByIdToShard(WhPoCommand command) {
        return this.whPoDao.findWhPoCommandById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createSubPoWithLineToInfo(WhPo po, List<WhPoLine> whPoLineList) {
        if (null == po) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        if (null == po.getId()) {
            this.whPoDao.insert(po);
        } else {
            int count = this.whPoDao.saveOrUpdateByVersion(po);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        for (WhPoLine line : whPoLineList) {
            line.setPoId(po.getId());
            if (null == line.getId()) {
                this.whPoLineDao.insert(line);
            } else {
                int count = this.whPoLineDao.saveOrUpdateByVersion(line);
                if (count <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void revokeSubPoToInfo(List<WhPoLine> lineList) {
        if (null == lineList || lineList.size() == 0) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        for (WhPoLine line : lineList) {
            int count = this.whPoLineDao.saveOrUpdateByVersion(line);
            if (count < 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
    }

}
