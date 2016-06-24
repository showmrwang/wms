package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("poLineManager")
@Transactional
public class PoLineManagerImpl implements PoLineManager {

    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private GlobalLogManager globalLogManager;

    protected static final Logger log = LoggerFactory.getLogger(PoLineManager.class);

    /**
     * 插入poline数据进基本库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createPoLineSingleToInfo(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    /**
     * 插入poline数据进拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createPoLineSingleToShare(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoLineByUuidToInfo(WhPoLineCommand WhPoLine) {
        whPoLineDao.deletePoLineByUuid(WhPoLine.getPoId(), WhPoLine.getOuId(), WhPoLine.getUuid());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deletePoLineByUuidToShard(WhPoLineCommand WhPoLine) {
        whPoLineDao.deletePoLineByUuid(WhPoLine.getPoId(), WhPoLine.getOuId(), WhPoLine.getUuid());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editPoLineToInfo(WhPoLine whPoLine) {
        // 查询原始POLINE数据
        WhPoLine poLine = whPoLineDao.findWhPoLineByIdWhPoLine(whPoLine.getId(), whPoLine.getOuId());
        double differenceQty = new BigDecimal(Double.toString(poLine.getQtyPlanned())).subtract(new BigDecimal(Double.toString(whPoLine.getQtyPlanned()))).doubleValue();// 计算计划数量原始和这次改动的差额
        // double differenceQty = poLine.getQtyPlanned() - whPoLine.getQtyPlanned();//
        // 计算计划数量原始和这次改动的差额
        whPoLine.setAvailableQty(new BigDecimal(Double.toString(whPoLine.getAvailableQty())).subtract(new BigDecimal(Double.toString(differenceQty))).doubleValue());// 修改可用数量
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 修改PO表头计划数量
        if (null == whPoLine.getUuid()) {
            WhPo whPo = whPoDao.findWhPoById(whPoLine.getPoId(), whPoLine.getOuId());
            whPo.setQtyPlanned(whPo.getQtyPlanned() - differenceQty);// 计划数量
            whPo.setModifiedId(whPoLine.getModifiedId());
            int count = whPoDao.saveOrUpdateByVersion(whPo);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editPoLineToShare(WhPoLine whPoLine) {
        // 查询原始POLINE数据
        WhPoLine poLine = whPoLineDao.findWhPoLineByIdWhPoLine(whPoLine.getId(), whPoLine.getOuId());
        double differenceQty = new BigDecimal(Double.toString(poLine.getQtyPlanned())).subtract(new BigDecimal(Double.toString(whPoLine.getQtyPlanned()))).doubleValue();// 计算计划数量原始和这次改动的差额
        // int differenceQty = poLine.getQtyPlanned() - whPoLine.getQtyPlanned();// 计算计划数量原始和这次改动的差额
        whPoLine.setAvailableQty(whPoLine.getAvailableQty() - differenceQty);// 修改可用数量
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 修改PO表头计划数量
        if (null == whPoLine.getUuid()) {
            WhPo whPo = whPoDao.findWhPoById(whPoLine.getPoId(), whPoLine.getOuId());
            whPo.setQtyPlanned(whPo.getQtyPlanned() - differenceQty);// 计划数量
            whPo.setModifiedId(whPoLine.getModifiedId());
            int count = whPoDao.saveOrUpdateByVersion(whPo);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoLineCommand findPoLinebyIdToInfo(WhPoLineCommand command) {
        return this.whPoLineDao.findWhPoLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoLineCommand findPoLinebyIdToShard(WhPoLineCommand command) {
        return this.whPoLineDao.findWhPoLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int editPoLineStatusToInfo(WhPoLineCommand command) {
        int result = whPoLineDao.editPoLineStatus(command.getIds(), command.getStatus(), command.getModifiedId(), command.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != command.getIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {command.getIds().size(), result});
        }
        return result;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int editPoLineStatusToShard(WhPoLineCommand command) {
        int result = whPoLineDao.editPoLineStatus(command.getIds(), command.getStatus(), command.getModifiedId(), command.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != command.getIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {command.getIds().size(), result});
        }
        return result;
    }

    /**
     * 通过创建POLINE信息查找是否该PO单下有对应明细信息(基础库)
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoLine findPoLineByAddPoLineParamToInfo(WhPoLine line, Boolean type) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(PoAsnStatus.POLINE_NEW);
        statusList.add(PoAsnStatus.POLINE_CREATE_ASN);
        statusList.add(PoAsnStatus.POLINE_RCVD);
        String uuid = line.getUuid();
        if (type) {
            // 查询POLINE单正式数据
            uuid = null;
        }
        return whPoLineDao.findPoLineByAddPoLineParam(statusList, line.getPoId(), null, line.getSkuId(), line.getIsIqc() == true ? 1 : 0, line.getMfgDate(), line.getExpDate(), line.getValidDate(), line.getBatchNo(), line.getCountryOfOrigin(),
                line.getInvStatus(), uuid);
    }

    /**
     * 通过创建POLINE信息查找是否该PO单下有对应明细信息(拆库)
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoLine findPoLineByAddPoLineParamToShare(WhPoLine line, Boolean type) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(PoAsnStatus.POLINE_NEW);
        statusList.add(PoAsnStatus.POLINE_CREATE_ASN);
        statusList.add(PoAsnStatus.POLINE_RCVD);
        String uuid = line.getUuid();
        if (type) {
            // 查询POLINE单正式数据
            uuid = null;
        }
        return whPoLineDao.findPoLineByAddPoLineParam(statusList, line.getPoId(), null, line.getSkuId(), line.getIsIqc() == true ? 1 : 0, line.getMfgDate(), line.getExpDate(), line.getValidDate(), line.getBatchNo(), line.getCountryOfOrigin(),
                line.getInvStatus(), uuid);
    }

    /**
     * 修改POLINE明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void updatePoLineSingleToInfo(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 修改POLINE明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updatePoLineSingleToShare(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

    }

    /**
     * 保存临时创建POLINE信息为正式数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createPoLineBatchToInfo(WhPoLineCommand whPoLine) {
        // 先删除此次保存UUID以外的数据
        whPoLineDao.deletePoLineByNotUuid(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        // 查询对应PO单下有UUID的数据
        List<WhPoLine> poLineList = new ArrayList<WhPoLine>();
        if (StringUtils.hasText(whPoLine.getUuid())) {
            poLineList = whPoLineDao.findWhPoLineByPoIdOuId(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        }
        double qtyPlannedCount = 0.0;
        for (WhPoLine p : poLineList) {
            // 整合计划数量
            qtyPlannedCount = new BigDecimal(Double.toString(qtyPlannedCount)).add(new BigDecimal(Double.toString(p.getQtyPlanned()))).doubleValue();
            // qtyPlannedCount = qtyPlannedCount + p.getQtyPlanned();
            if (null == p.getPoLineId()) {
                // 如果对应的polineid is null 直接去除这条的uuid数据 保存为正式数据
                p.setUuid(null);
                p.setModifiedId(whPoLine.getModifiedId());
                int count = whPoLineDao.saveOrUpdateByVersion(p);
                if (count <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            } else {
                // 如果对应的polineid is not null 直接合并相关信息 删除对应uuid数据
                WhPoLine w = whPoLineDao.findWhPoLineByIdWhPoLine(p.getPoLineId(), p.getOuId());
                if (null != w) {
                    // 合并数量
                    w.setQtyPlanned(w.getQtyPlanned() + p.getQtyPlanned());// 计划数量
                    w.setAvailableQty(w.getAvailableQty() + p.getQtyPlanned());// 可用数量=原可用数量+新计划数量
                    w.setModifiedId(whPoLine.getModifiedId());
                    int count = whPoLineDao.saveOrUpdateByVersion(w);
                    if (count <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
                // 删除对应UUID临时数据
                whPoLineDao.deletePoLineByIdOuId(p.getId(), p.getOuId());
            }
        }
        // 修改po表头计划数量和可用数量
        WhPo po = whPoDao.findWhPoById(whPoLine.getPoId(), whPoLine.getOuId());
        po.setQtyPlanned(po.getQtyPlanned() + qtyPlannedCount);// 计划数量
        po.setModifiedId(whPoLine.getModifiedId());
        int count = whPoDao.saveOrUpdateByVersion(po);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 保存临时创建POLINE信息为正式数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createPoLineBatchToShare(WhPoLineCommand whPoLine) {
        // 先删除此次保存UUID以外的数据
        whPoLineDao.deletePoLineByNotUuid(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        // 查询对应PO单下有UUID的数据
        List<WhPoLine> poLineList = new ArrayList<WhPoLine>();
        if (StringUtils.hasText(whPoLine.getUuid())) {
            poLineList = whPoLineDao.findWhPoLineByPoIdOuId(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        }
        double qtyPlannedCount = 0.0;
        for (WhPoLine p : poLineList) {
            qtyPlannedCount = new BigDecimal(Double.toString(qtyPlannedCount)).add(new BigDecimal(Double.toString(p.getQtyPlanned()))).doubleValue();// 整合计划数量
            // qtyPlannedCount = qtyPlannedCount + p.getQtyPlanned();
            if (null == p.getPoLineId()) {
                // 如果对应的polineid is null 直接去除这条的uuid数据 保存为正式数据
                p.setUuid(null);
                p.setModifiedId(whPoLine.getModifiedId());
                int count = whPoLineDao.saveOrUpdateByVersion(p);
                if (count <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            } else {
                // 如果对应的polineid is not null 直接合并相关信息 删除对应uuid数据
                WhPoLine w = whPoLineDao.findWhPoLineByIdWhPoLine(p.getPoLineId(), p.getOuId());
                if (null != w) {
                    // 合并数量
                    w.setQtyPlanned(w.getQtyPlanned() + p.getQtyPlanned());// 计划数量
                    w.setAvailableQty(w.getAvailableQty() + p.getQtyPlanned());// 可用数量=原可用数量+新计划数量
                    w.setModifiedId(whPoLine.getModifiedId());
                    int count = whPoLineDao.saveOrUpdateByVersion(w);
                    if (count <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
                // 删除对应UUID临时数据
                whPoLineDao.deletePoLineByIdOuId(p.getId(), p.getOuId());
            }
        }
        // 修改po表头计划数量和可用数量
        WhPo po = whPoDao.findWhPoById(whPoLine.getPoId(), whPoLine.getOuId());
        po.setQtyPlanned(po.getQtyPlanned() + qtyPlannedCount);// 计划数量
        po.setModifiedId(whPoLine.getModifiedId());
        int count = whPoDao.saveOrUpdateByVersion(po);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoLine> findWhPoLineListByPoIdToInfo(Long poid, Long ouid) {
        return whPoLineDao.findWhPoLineByPoIdOuId(poid, ouid, null);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhPoLine> findWhPoLineListByPoIdToShard(Long poid, Long ouid) {
        return whPoLineDao.findWhPoLineByPoIdOuId(poid, ouid, null);
    }

    /**
     * 删除对应poid+ouid uuid不为空的数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoLineByUuidNotNullToInfo(Long poid, Long ouid) {
        whPoLineDao.deletePoLineByUuidNotNull(poid, ouid);
    }

    /**
     * 删除对应poid+ouid uuid不为空的数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deletePoLineByUuidNotNullToShard(Long poid, Long ouid) {
        whPoLineDao.deletePoLineByUuidNotNull(poid, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveOrUpdateByVersionToInfo(WhPoLine o) {
        int count = this.whPoLineDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhPoLine o) {
        int count = this.whPoLineDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int deletePoLinesToInfo(List<WhPoLine> lineList) {
        WhPo whpo = this.whPoDao.findWhPoById(lineList.get(0).getPoId(), lineList.get(0).getOuId());
        if (null == whpo) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        whpo.setModifiedId(lineList.get(0).getModifiedId());
        int deleteCount = 0;
        for (WhPoLine line : lineList) {
            if (StringUtils.hasText(line.getUuid())) {
                this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                deleteCount++;
            } else {
                if (PoAsnStatus.POLINE_NEW == line.getStatus()) {
                    whpo.setQtyPlanned(whpo.getQtyPlanned() - line.getQtyPlanned());
                    this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                    deleteCount++;
                } else {
                    throw new BusinessException(ErrorCodes.POLINE_DELETE_STATUS_ERROR, new Object[] {line.getId()});
                }
            }
        }
        this.whPoDao.saveOrUpdateByVersion(whpo);
        return deleteCount;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int deletePoLinesToShard(List<WhPoLine> lineList) {
        WhPo whpo = this.whPoDao.findWhPoById(lineList.get(0).getPoId(), lineList.get(0).getOuId());
        if (null == whpo) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        whpo.setModifiedId(lineList.get(0).getModifiedId());
        int deleteCount = 0;
        for (WhPoLine line : lineList) {
            if (StringUtils.hasText(line.getUuid())) {
                this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                deleteCount++;
            } else {
                if (PoAsnStatus.POLINE_NEW == line.getStatus()) {
                    whpo.setQtyPlanned(whpo.getQtyPlanned() - line.getQtyPlanned());
                    this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                    deleteCount++;
                } else {
                    throw new BusinessException(ErrorCodes.POLINE_DELETE_STATUS_ERROR, new Object[] {line.getId()});
                }
            }
        }
        this.whPoDao.saveOrUpdateByVersion(whpo);
        return deleteCount;
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
            log.debug("insertGlobalLog methdo returns:{}", gl);
        }
        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method end!");
        try {
            globalLogManager.insertGlobalLog(gl);
        } catch (Exception e) {
            log.error("insert into global log error:{}", e);
            throw new BusinessException(ErrorCodes.INSERT_LOG_ERROR);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void batchUpdatePoLine(List<WhPoLine> polineList) {
        log.info(this.getClass().getSimpleName() + ".batchUpdatePoLine method begin!");
        for (WhPoLine poline : polineList) {
            if (log.isDebugEnabled()) {
                log.debug("batchUpdatePoLine-->foreach(polinelist);poLineId:{},poline:{}", poline.getId(), poline);
            }
            int count = this.whPoLineDao.saveOrUpdateByVersion(poline);
            if (count <= 0) {
                log.warn("saveorupdatebyVersion po line returns:{};details: [poline_id:{},poline:{}]", count, poline.getId(), poline);
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(poline.getModifiedId(), new Date(), poline.getClass().getSimpleName(), poline, Constants.GLOBAL_LOG_UPDATE, poline.getOuId());
        }
        log.info(this.getClass().getSimpleName() + ".batchUpdatePoLine method end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoLine> findInfoPoLineByPoCodeOuId(String poCode, Long ouId) {

        return this.whPoLineDao.findInfoPoLineByPoCodeOuId(poCode, ouId);
    }

    /**
     * TODO yimin.lu 这个方法逻辑有点不明确
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createPoLineBatchToShareNew(BiPoLineCommand biPoLineCommand, List<WhPoLine> infoPolineList) {
        WhPo whpo = this.whPoDao.findByPoCodeAndOuId(biPoLineCommand.getPoCode(), biPoLineCommand.getOuId());
        double qtyPlanned = Constants.DEFAULT_DOUBLE;
        for (WhPoLine infoPoline : infoPolineList) {
            qtyPlanned += infoPoline.getQtyPlanned();
            WhPoLine shardPoline = this.whPoLineDao.findByPoCodeAndOuIdAndPoLineId(biPoLineCommand.getPoCode(), biPoLineCommand.getOuId(), infoPoline.getPoLineId());
            if (shardPoline != null) {
                BeanUtils.copyProperties(infoPoline, shardPoline, "id", "lastModifyTime", "poId");
                this.whPoLineDao.saveOrUpdateByVersion(shardPoline);
            } else {
                infoPoline.setId(null);
                infoPoline.setLastModifyTime(new Date());
                infoPoline.setPoId(whpo.getId());
                this.whPoLineDao.insert(infoPoline);
            }
        }
        whpo.setQtyPlanned(qtyPlanned);
        this.whPoDao.saveOrUpdateByVersion(whpo);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoLine findPoLineByPolineIdAndStatusListAndPoIdAndOuIdToShared(Long poLineId, List<Integer> statusList, Long poId, Long ouId) {
        return this.whPoLineDao.findPoLineByPolineIdAndStatusListAndPoIdAndOuId(poLineId, statusList, poId, ouId, null);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoLine findPoLineByPolineIdAndStatusListAndPoIdAndOuIdToInfo(Long poLineId, List<Integer> statusList, Long poId, Long ouId, String uuid, boolean uuidFlag) {
        if (!uuidFlag) {
            uuid = "";
        }
        return this.whPoLineDao.findPoLineByPolineIdAndStatusListAndPoIdAndOuId(poLineId, statusList, poId, ouId, uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoLine> findWhPoLineByPoIdOuIdUuIdToInfo(Long id, Long ouId, String uuid) {
        WhPoLine line = new WhPoLine();
        line.setId(id);
        line.setOuId(ouId);
        line.setUuid(uuid);
        return this.whPoLineDao.findListByParam(line);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoLineByPoIdOuIdAndUuidNotNullNotEqual(Long id, Long ouId, String uuid) {
        List<WhPoLine> lineList = this.whPoLineDao.findPoLineByPoIdOuIdAndUuidNotNullNotEqual(id, ouId, uuid);
        if (lineList != null) {
            for (WhPoLine l : lineList) {
                this.whPoLineDao.delete(l.getId());
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(Page page, Sort[] sorts, Map<String, Object> paraMap) {
        return this.whPoLineDao.findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(page, sorts, paraMap);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoLine> findWhPoLineListByPoIdOuIdStatusListToInfo(Long poId, Long ouId, List<Integer> statusList) {
        WhPoLineCommand command=new WhPoLineCommand();
        command.setPoId(poId);
        command.setOuId(ouId);
        command.setStatusList(statusList);
        return this.whPoLineDao.findListByParamExt(command);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoLine findWhPoLineByIdOuIdToShard(Long id, Long ouId) {
        return this.whPoLineDao.findWhPoLineByIdWhPoLine(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public long findListCountByParamToShard(WhPoLine searchPoLine) {
        return this.whPoLineDao.findListCountByParam(searchPoLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateAsnToShard(Page page, Sort[] sorts, Map<String, Object> paraMap) {
        return this.whPoLineDao.findPoLineListByQueryMapWithPageExtForCreateAsnToShard(page, sorts, paraMap);
    }


}
