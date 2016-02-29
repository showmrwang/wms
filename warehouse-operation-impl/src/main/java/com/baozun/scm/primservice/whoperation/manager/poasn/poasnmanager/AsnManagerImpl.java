package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.util.StringUtil;


/**
 * 创建ASN单据
 * 
 * @author bin.hu
 * 
 */
@Service("asnManager")
@Transactional
public class AsnManagerImpl implements AsnManager {

    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private WhPoDao whPoDao;


    /**
     * 通过asncode查询出asn列表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid) {
        return whAsnDao.findWhAsnListByAsnCode(asnCode, status, ouid);
    }

    /**
     * 修改公共库ASN单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int editAsnStatusByInfo(WhAsnCommand whAsn) {
        //FIXME editAsnStatusByInfo log日志，方法doc说明
        int result = whAsnDao.editAsnStatus(whAsn.getAsnIds(), whAsn.getStatus(), whAsn.getModifiedId(), whAsn.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whAsn.getAsnIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whAsn.getAsnIds().size(), result});
        }
        return result;
    }

    /**
     * 修改拆库ASN单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int editAsnStatusByShard(WhAsnCommand whAsn) {
        int result = whAsnDao.editAsnStatus(whAsn.getAsnIds(), whAsn.getStatus(), whAsn.getModifiedId(), whAsn.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whAsn.getAsnIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whAsn.getAsnIds().size(), result});
        }
        return result;
    }

    /**
     * 读取公共库ASN单信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<WhAsnCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return whAsnDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 读取拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whAsnDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 保存asn单信息
     * 
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg createAsnAndLineToShare(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm) {
        // 如果有asnline信息 asn表头信息需要查询po表头信息
        // 查询对应的PO单信息
        // WhPo whPo = whPoDao.findWhPoById(asn.getPoId(), asn.getPoOuId());
        if (null != whPo.getOuId()) {
            // 如果有ouid一个事务更新PO单状态
            if (whPo.getStatus() == PoAsnStatus.PO_NEW) {
                // 如果是新建状态 改状态为已创建ASN
                whPo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                whPo.setModifiedId(asn.getModifiedId());
                int result = whPoDao.saveOrUpdateByVersion(whPo);
                if (result <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
        if (null != asnLineList) {
            WhAsn whAsn = new WhAsn();
            if (null == asn.getId()) {
                // 如果asn没有id 证明没有ASN表头 需要添加
                // 如果有asnline信息的话就是PO单明细页面创建ASN单数据
                // 把PO单有的信息copy到whasn表头内
                BeanUtils.copyProperties(whPo, whAsn);
                whAsn.setId(null);
                whAsn.setPoId(whPo.getId());
                whAsn.setQtyPlanned(asn.getQtyPlanned());
                whAsn.setStatus(PoAsnStatus.ASN_NEW);
                whAsn.setAsnCode(asn.getAsnCode());
                whAsn.setOuId(asn.getOuId());
                whAsn.setAsnExtCode(asn.getAsnExtCode());
                whAsn.setAsnType(whPo.getPoType());
                whAsn.setCreateTime(new Date());
                whAsn.setCreatedId(asn.getCreatedId());
                whAsn.setLastModifyTime(new Date());
                whAsn.setModifiedId(asn.getModifiedId());
                whAsnDao.insert(whAsn);
            }
            for (WhAsnLineCommand asnline : asnLineList) {
                WhAsnLine asnLine = new WhAsnLine();
                // 查询对应poline信息
                WhPoLine whPoLine = poLineMap.get(asnline.getPoLineId());
                if (asnline.getQtyPlanned() > whPoLine.getAvailableQty()) {
                    // 如果asnline计划数量 > poline的可用数量抛出异常
                    throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                }
                BeanUtils.copyProperties(whPoLine, asnLine);
                asnLine.setId(null);
                if (null == asn.getId()) {
                    asnLine.setAsnId(asn.getId());
                } else {
                    asnLine.setAsnId(whAsn.getId());
                }
                asnLine.setPoLineId(whPoLine.getId());
                asnLine.setPoLinenum(whPoLine.getLinenum());
                asnLine.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                asnLine.setQtyPlanned(asnline.getQtyPlanned());
                asnLine.setCreatedId(asn.getCreatedId());
                asnLine.setCreateTime(new Date());
                asnLine.setModifiedId(asn.getCreatedId());
                asnLine.setLastModifyTime(new Date());
                whAsnLineDao.insert(asnLine);
                if (null != whPo.getOuId()) {
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
            }
        } else {
            // 如果没有asnline信息的话就是ASN列表创建
            // 没有ASNLINE信息直接保存ASN表头
            whAsnDao.insert(asn);
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg insertAsnWithOuId(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm) {
        String asnExtCode = asn.getAsnExtCode();
        Long storeId = asn.getStoreId();
        Long ouId = asn.getOuId();
        /* 查找在性对应的拆库表中是否有此asn单信息 */
        long count = whAsnDao.findAsnByCodeAndStore(asnExtCode, storeId, ouId);
        /* 没有此asn单信息 */
        if (0 == count) {
            // 查询对应的PO单信息
            // WhPo whPo = whPoDao.findWhPoById(asn.getPoId(), asn.getOuId());
            if (null != whPo.getOuId()) {
                if (whPo.getStatus() == PoAsnStatus.PO_NEW) {
                    // 如果是新建状态 改状态为已创建ASN
                    whPo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                    whPo.setModifiedId(asn.getModifiedId());
                    int result = whPoDao.saveOrUpdateByVersion(whPo);
                    if (result <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            if (null != asnLineList) {
                WhAsn whAsn = new WhAsn();
                if (null == asn.getId()) {
                    // 如果有asnline信息的话就是PO单明细页面创建ASN单数据
                    // 把PO单有的信息copy到whasn表头内
                    BeanUtils.copyProperties(whPo, whAsn);
                    whAsn.setId(null);
                    whAsn.setPoId(whPo.getId());
                    whAsn.setQtyPlanned(asn.getQtyPlanned());
                    whAsn.setStatus(PoAsnStatus.ASN_NEW);
                    whAsn.setAsnCode(asn.getAsnCode());
                    whAsn.setAsnExtCode(asn.getAsnExtCode());
                    whAsn.setAsnType(whPo.getPoType());
                    whAsn.setCreateTime(new Date());
                    whAsn.setCreatedId(asn.getCreatedId());
                    whAsn.setLastModifyTime(new Date());
                    whAsn.setModifiedId(asn.getModifiedId());
                    whAsnDao.insert(whAsn);
                }
                for (WhAsnLineCommand asnline : asnLineList) {
                    WhAsnLine asnLine = new WhAsnLine();
                    // 查询对应poline信息
                    // WhPoLine whPoLine =
                    // whPoLineDao.findWhPoLineByIdWhPoLine(asnline.getPoLineId(), asn.getOuId());
                    WhPoLine whPoLine = poLineMap.get(asnline.getPoLineId());
                    if (asnline.getQtyPlanned() > whPoLine.getAvailableQty()) {
                        // 如果asnline计划数量 > poline的可用数量抛出异常
                        throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                    }
                    BeanUtils.copyProperties(whPoLine, asnLine);
                    asnLine.setId(null);
                    if (null == asn.getId()) {
                        asnLine.setAsnId(asn.getId());
                    } else {
                        asnLine.setAsnId(whAsn.getId());
                    }
                    asnLine.setPoLineId(whPoLine.getId());
                    asnLine.setPoLinenum(whPoLine.getLinenum());
                    asnLine.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                    asnLine.setQtyPlanned(asnline.getQtyPlanned());
                    asnLine.setCreatedId(asn.getCreatedId());
                    asnLine.setCreateTime(new Date());
                    asnLine.setModifiedId(asn.getCreatedId());
                    asnLine.setLastModifyTime(new Date());
                    whAsnLineDao.insert(asnLine);
                    if (null != whPo.getOuId()) {
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
                }
            } else {
                // 没有ASNLINE信息直接保存ASN表头
                whAsnDao.insert(asn);
            }
        } else {
            /* 存在此asn单信息 */
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.ASN_EXIST + "");
            return rm;
        }
        return rm;

    }

    /**
     * 通过OP单ID查询相关信息 基础表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhAsnCommand findWhAsnByIdToInfo(WhAsnCommand whPo) {
        return whAsnDao.findWhAsnById(whPo.getId(), whPo.getOuId());
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhAsnCommand findWhAsnByIdToShard(WhAsnCommand whPo) {
        return whAsnDao.findWhAsnById(whPo.getId(), whPo.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editAsnToInfo(WhAsn whasn) {
        int count = 0;
        count = whAsnDao.saveOrUpdateByVersion(whasn);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editAsnToShard(WhAsn whasn) {
        int count = 0;
        count = whAsnDao.saveOrUpdateByVersion(whasn);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg createAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines, ResponseMsg rm) {
        WhAsn whAsn = new WhAsn();
        BeanUtils.copyProperties(whpo, whAsn);
        // 创建whasn表头信息
        whAsn.setId(null);
        whAsn.setAsnCode(asn.getAsnCode());
        whAsn.setAsnExtCode(asn.getAsnExtCode());
        whAsn.setPoId(whpo.getId());
        whAsn.setPoOuId(whpo.getOuId());
        whAsn.setAsnType(whpo.getPoType());
        whAsn.setStatus(PoAsnStatus.ASN_NEW);
        whAsn.setCreateTime(new Date());
        whAsn.setCreatedId(asn.getUserId());
        whAsn.setLastModifyTime(new Date());
        whAsn.setModifiedId(asn.getUserId());
        whAsnDao.insert(whAsn);
        if (null != whpo.getOuId()) {
            // 如果ouid不为空 在一个事务里修改PO单状态
            if (whpo.getStatus() == PoAsnStatus.PO_NEW) {
                // 如果是新建状态 改状态为已创建ASN
                whpo.setStatus(PoAsnStatus.PO_CREATE_ASN);
                whpo.setModifiedId(asn.getModifiedId());
                int result = whPoDao.saveOrUpdateByVersion(whpo);
                if (result <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
        int qty = 0;// 计算计划数量
        // 插入asnline明细
        for (WhPoLine pl : whPoLines) {
            if (pl.getQtyPlanned() > pl.getAvailableQty() && StringUtil.isEmpty(pl.getUuid())) {
                // 计划数量比如大于可用数量并且UUID为空才能创建
                WhAsnLine al = new WhAsnLine();
                BeanUtils.copyProperties(pl, al);
                qty = qty + pl.getAvailableQty();// 计算计划数量
                al.setId(null);
                al.setAsnId(whAsn.getId());
                al.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                al.setPoLinenum(pl.getLinenum());
                al.setPoLineId(pl.getId());
                al.setQtyPlanned(pl.getAvailableQty());// 一键创建asnline的计划数量=poline的可用数量
                al.setCreatedId(asn.getUserId());
                al.setCreateTime(new Date());
                al.setModifiedId(asn.getUserId());
                al.setLastModifyTime(new Date());
                whAsnLineDao.insert(al);
                if (null != whpo.getOuId()) {
                    // 修改poline的可用数量
                    pl.setAvailableQty(0);// 一键创建asnline poline的可用数量0
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
            }
        }
        // 最后修改ASN的计划数量
        whAsn.setQtyPlanned(qty);
        int result = whAsnDao.saveOrUpdateByVersion(whAsn);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(whAsn.getId() + "");
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhAsn> findWhAsnByPoToShard(WhAsn whAsn) {
        return this.whAsnDao.findListByParam(whAsn);
    }

}
