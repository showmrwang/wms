package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

@Service("BiPoLineManager")
@Transactional
public class BiPoLineManagerImpl extends BaseManagerImpl implements BiPoLineManager {

    protected static final Logger log = LoggerFactory.getLogger(BiPoLineManager.class);
    @Autowired
    private BiPoLineDao biPoLineDao;
    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private BiPoManager biPoManager;
    @Autowired
    private CodeManager codeManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoLine findPoLineByAddPoLineParam(BiPoLine line, boolean type) {
        log.info("begin!");
        log.debug("param:[line:{},type:{}]", line, type);
        List<Integer> statusList = Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD});
        String uuid = line.getUuid();
        if (type) {
            // 查询POLINE单正式数据
            uuid = null;
        }
        log.info("end!");
        return biPoLineDao.findPoLineByAddPoLineParam(statusList, line.getPoId(), null, line.getSkuId(), line.getIsIqc() == true ? 1 : 0, line.getMfgDate(), line.getExpDate(), line.getValidDate(), line.getBatchNo(), line.getCountryOfOrigin(),
                line.getInvStatus(), uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void insert(BiPoLine line) {
        log.info("begin!");
        log.debug("params:[BiPoLine:{}]", line);
        try {
            biPoLineDao.insert(line);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, line, null, line.getModifiedId(), line.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
            log.info("end!");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveOrUpdateByVersion(BiPoLine line) {
        log.info("begin!");
        log.debug("param:[BiPoLine:{}]", line);
        try {
            int result = biPoLineDao.saveOrUpdateByVersion(line);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, line, null, line.getModifiedId(), line.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
            log.info("end!");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<BiPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.biPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createPoLineBatchToInfo(BiPoLineCommand biPoLineCommand) {
        Long poId = biPoLineCommand.getPoId();
        String uuid = biPoLineCommand.getUuid();
        Long userId = biPoLineCommand.getModifiedId();
        Long ouId = biPoLineCommand.getOuId();
        log.info("begin!");
        log.debug("params:[poId:{},uuid:{},userId:{},ouId:{}]", poId, uuid, userId, ouId);
        boolean flag = null == ouId ? false : true;
        try{
            BiPo biPo = this.biPoDao.findById(poId);
            // 先删除此次保存UUID以外的数据
            biPoLineDao.deleteBiPoLineByPoIdAndNotUuid(poId, uuid);
            // 查询对应PO单下有UUID的数据
            List<BiPoLine> poLineList = new ArrayList<BiPoLine>();
            if (StringUtils.hasText(uuid)) {
                poLineList = biPoLineDao.findBiPoLineByPoIdAndUuid(poId, uuid);
            }
            double qtyPlannedCount = 0d;
            for (BiPoLine p : poLineList) {
                qtyPlannedCount = qtyPlannedCount + p.getQtyPlanned();// 整合计划数量
                if (null == p.getPoLineId()) {
                    // 如果对应的polineid is null 直接去除这条的uuid数据 保存为正式数据
                    p.setUuid(null);
                    p.setModifiedId(userId);
                    double qtyPlanned = p.getQtyPlanned();
                    if (flag) {
                        p.setAvailableQty(Constants.DEFAULT_DOUBLE);
                        p.setStatus(PoAsnStatus.BIPOLINE_ALLOT);
                    }
                    int count = biPoLineDao.saveOrUpdateByVersion(p);
                    if (count <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    if (flag) {
                        WhPo whpo = this.whPoDao.findWhPoByExtCodeStoreIdOuId(biPo.getExtCode(), biPo.getStoreId(), ouId);
                        if (whpo != null) {
                            WhPoLine whPoLine = new WhPoLine();
                            BeanUtils.copyProperties(p, whPoLine);
                            whPoLine.setId(null);
                            whPoLine.setPoLineId(p.getId());
                            whPoLine.setOuId(ouId);
                            whPoLine.setQtyPlanned(qtyPlanned);
                            whPoLine.setAvailableQty(qtyPlanned);
                            whPoLine.setPoId(whpo.getId());
                            whPoLine.setStatus(PoAsnStatus.POLINE_NEW);
                            this.whPoLineDao.insert(whPoLine);
                        }
                    }
                    
                } else {
                    // 如果对应的polineid is not null 直接合并相关信息 删除对应uuid数据
                    BiPoLine w = biPoLineDao.findById(p.getPoLineId());
                    if (null != w) {
                        // 合并数量
                        w.setQtyPlanned(w.getQtyPlanned() + p.getQtyPlanned());// 计划数量
                        if (flag) {
                            w.setAvailableQty(Constants.DEFAULT_DOUBLE);
                        } else {
                            w.setAvailableQty(w.getAvailableQty() + p.getQtyPlanned());// 可用数量=原可用数量+新计划数量
                        }
                        w.setModifiedId(userId);
                        int count = biPoLineDao.saveOrUpdateByVersion(w);
                        if (count <= 0) {
                            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                        }
                        if(flag){
                            WhPoLine whpoLine = this.whPoLineDao.findByExtCodeStoreIdOuIdPoLineId(biPo.getExtCode(), biPo.getStoreId(), ouId, w.getId());
                            whpoLine.setQtyPlanned(w.getQtyPlanned());
                            whpoLine.setAvailableQty(whpoLine.getAvailableQty()+p.getQtyPlanned());
                            whpoLine.setModifiedId(userId);
                            this.whPoLineDao.saveOrUpdateByVersion(whpoLine);
                        }
                    }
                    // 删除对应UUID临时数据
                    biPoLineDao.delete(p.getId());
                }
            }
            // 修改po表头计划数量和可用数量
            BiPo po = this.biPoDao.findById(poId);
            po.setQtyPlanned(BigDecimal.valueOf(null == po.getQtyPlanned() ? 0d : po.getQtyPlanned()).add(BigDecimal.valueOf(qtyPlannedCount)).doubleValue());// 计划数量
            po.setModifiedId(userId);
            int count = biPoDao.saveOrUpdateByVersion(po);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            if(flag){
                WhPo whpo = this.whPoDao.findWhPoByExtCodeStoreIdOuId(po.getExtCode(), po.getStoreId(), ouId);
                whpo.setQtyPlanned(po.getQtyPlanned());
                whpo.setModifiedId(po.getModifiedId());
                this.whPoDao.saveOrUpdateByVersion(whpo);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
        log.info("end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoLine findBiPoLineById(Long id) {
        return this.biPoLineDao.findById(id);
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoLineCommand findBiPoLineCommandById(Long id) {
        return this.biPoLineDao.findCommandbyId(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editBiPoLineSingle(BiPoLine biPoLine) {
        log.info("begin!");
        log.debug("params:[BiPoLine:{}]", biPoLine);
        try{
            BiPoLine oldpoline = this.biPoLineDao.findById(biPoLine.getId());
            double count = biPoLine.getQtyPlanned() - oldpoline.getQtyPlanned();// 计划数量与原数量的差距
            // 没有UUID的时候，需要更改BIPO头信息中的计划数量
            if (StringUtils.isEmpty(biPoLine.getUuid())) {
                BiPo bipo = this.biPoDao.findById(biPoLine.getPoId());
                bipo.setQtyPlanned(bipo.getQtyPlanned() + count);
                bipo.setModifiedId(biPoLine.getModifiedId());
                this.biPoDao.saveOrUpdateByVersion(bipo);
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, bipo, null, biPoLine.getModifiedId(), null, DbDataSource.MOREDB_INFOSOURCE);
            }
            // 更改BIPO明细的计划数量
            biPoLine.setAvailableQty(biPoLine.getAvailableQty() + count);
            this.biPoLineDao.saveOrUpdateByVersion(biPoLine);
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, biPoLine, null, biPoLine.getModifiedId(), biPoLine.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
        log.info("end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deleteBiPoLineByPoIdAndUuidToInfo(Long poId, String uuid, Long userId) {
        if (StringUtils.hasText(uuid)) {// 有uuid时才进行操作
            try {
                List<BiPoLine> lines = this.biPoLineDao.findBiPoLineByPoIdAndUuid(poId, uuid);
                if (lines != null) {
                    for (BiPoLine line : lines) {
                        this.biPoLineDao.delete(line.getId());
                        this.insertGlobalLog(GLOBAL_LOG_DELETE, line, null, userId, poId + "", DbDataSource.MOREDB_INFOSOURCE);
                    }
                }
            } catch (BusinessException e) {
                throw e;
            } catch (Exception ex) {
                log.error("" + ex);
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<BiPoLineCommand> findListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.biPoLineDao.findListByQueryMapWithPageExtForCreateSubPo(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deleteList(List<BiPoLine> lineList) {
        if (null != lineList && lineList.size() > 0) {
            Long userId = lineList.get(0).getModifiedId();
            BiPo bipo = this.biPoDao.findById(lineList.get(0).getPoId());
            double qty = bipo.getQtyPlanned();
            for (BiPoLine line : lineList) {
                if (PoAsnStatus.BIPOLINE_NEW != line.getStatus()) {
                    throw new BusinessException(ErrorCodes.BIPO_DELETE_HAS_ALLOCATED_ERROR);
                }
                if (StringUtils.isEmpty(line.getUuid())) {
                    qty -= line.getQtyPlanned();
                }
                this.biPoLineDao.delete(line.getId());
            }
            bipo.setQtyPlanned(qty);
            bipo.setModifiedId(userId);
            this.biPoDao.saveOrUpdateByVersion(bipo);
        }
    }

}
