package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.ArrayList;
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

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
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


    /**
     * 插入poline数据进基本库
     */
    @Override
    @MoreDB("infoSource")
    public void createPoLineSingleToInfo(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    /**
     * 插入poline数据进拆库
     */
    @Override
    @MoreDB("shardSource")
    public void createPoLineSingleToShare(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    @Override
    @MoreDB("infoSource")
    public Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB("shardSource")
    public Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB("infoSource")
    public void deletePoLineByUuidToInfo(WhPoLineCommand WhPoLine) {
        whPoLineDao.deletePoLineByUuid(WhPoLine.getPoId(), WhPoLine.getOuId(), WhPoLine.getUuid());
    }

    @Override
    @MoreDB("shardSource")
    public void deletePoLineByUuidToShare(WhPoLineCommand WhPoLine) {
        whPoLineDao.deletePoLineByUuid(WhPoLine.getPoId(), WhPoLine.getOuId(), WhPoLine.getUuid());
    }

    @Override
    @MoreDB("infoSource")
    public void editPoLineToInfo(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB("shardSource")
    public void editPoLineToShare(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB("infoSource")
    public WhPoLineCommand findPoLinebyIdToInfo(WhPoLineCommand command) {
        return this.whPoLineDao.findWhPoLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB("shardSource")
    public WhPoLineCommand findPoLinebyIdToShard(WhPoLineCommand command) {
        return this.whPoLineDao.findWhPoLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB("infoSource")
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
    @MoreDB("shardSource")
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
    @MoreDB("infoSource")
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
    @MoreDB("shardSource")
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
    @MoreDB("infoSource")
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
    @MoreDB("shardSource")
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
    @MoreDB("infoSource")
    public void createPoLineBatchToInfo(WhPoLineCommand whPoLine) {
        // 先删除此次保存UUID以外的数据
        whPoLineDao.deletePoLineByNotUuid(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        // 查询对应PO单下有UUID的数据
        List<WhPoLine> poLineList = whPoLineDao.findWhPoLineByPoIdOuId(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        Integer qtyPlannedCount = 0;
        for (WhPoLine p : poLineList) {
            qtyPlannedCount = qtyPlannedCount + p.getQtyPlanned();// 整合计划数量
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
    @MoreDB("shardSource")
    public void createPoLineBatchToShare(WhPoLineCommand whPoLine) {
        // 先删除此次保存UUID以外的数据
        whPoLineDao.deletePoLineByNotUuid(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        // 查询对应PO单下有UUID的数据
        List<WhPoLine> poLineList = whPoLineDao.findWhPoLineByPoIdOuId(whPoLine.getPoId(), whPoLine.getOuId(), whPoLine.getUuid());
        Integer qtyPlannedCount = 0;
        for (WhPoLine p : poLineList) {
            qtyPlannedCount = qtyPlannedCount + p.getQtyPlanned();// 整合计划数量
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
}
