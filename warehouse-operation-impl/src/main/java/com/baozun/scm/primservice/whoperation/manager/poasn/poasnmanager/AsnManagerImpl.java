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

import com.baozun.scm.primservice.whoperation.command.poasn.AsnCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


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


    /**
     * 通过asncode查询出asn列表
     */
    @Override
    @MoreDB("shardSource")
    public List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid) {
        return whAsnDao.findWhAsnListByAsnCode(asnCode, status, ouid);
    }

    /**
     * 修改公共库ASN单状态
     */
    @Override
    @MoreDB("infoSource")
    public int editAsnStatusByInfo(WhAsnCommand whAsn) {
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
    @MoreDB("shardSource")
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
    @MoreDB("infoSource")
    public Pagination<WhAsnCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return whAsnDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 读取拆库
     */
    @Override
    @MoreDB("shardSource")
    public Pagination<WhAsnCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whAsnDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 保存asn单信息
     * 
     */
    @Override
    @MoreDB("shardSource")
    public ResponseMsg createAsnAndLineToShare(WhAsn asn, List<WhAsnLineCommand> asnLineList, ResponseMsg rm) {
        // 如果有asnline信息 asn表头信息需要查询po表头信息
        if (null != asnLineList) {
            for (WhAsnLineCommand asnline : asnLineList) {
                WhAsnLine asnLine = new WhAsnLine();
                // 查询对应poline信息
                WhPoLine whPoLine = whPoLineDao.findWhPoLineByIdWhPoLine(asnline.getPoLineId(), asn.getOuId());
                BeanUtils.copyProperties(whPoLine, asnLine);
                asnLine.setAsnId(asn.getId());
                asnLine.setPoLineId(whPoLine.getId());
                asnLine.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                asnLine.setCreatedId(asn.getCreatedId());
                asnLine.setCreateTime(new Date());
                asnLine.setModifiedId(asn.getCreatedId());
                asnLine.setLastModifyTime(new Date());
            }
        } else {
            // 没有ASNLINE信息直接保存ASN表头
            whAsnDao.insert(asn);
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;

    }

    @Override
    @MoreDB("shardSource")
    public ResponseMsg insertAsnWithOuId(AsnCheckCommand asnCheckCommand) {
        WhAsn whAsn = asnCheckCommand.getWhAsn();
        ResponseMsg rm = asnCheckCommand.getRm();
        String asnExtCode = whAsn.getAsnExtCode();
        Long storeId = whAsn.getStoreId();
        Long ouId = whAsn.getOuId();
        /* 查找在性对应的拆库表中是否有此asn单信息 */
        long count = whAsnDao.findAsnByCodeAndStore(asnExtCode, storeId, ouId);
        /* 没有此asn单信息 */
        if (0 == count) {
            long i = whAsnDao.insert(whAsn);
            if (0 == i) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.SAVE_CHECK_TABLE_FAILED_ASN + "");
                return rm;
            }
            rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            rm.setMsg(whAsn.getId() + "");
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
    @MoreDB("infoSource")
    public WhAsnCommand findWhAsnByIdToInfo(WhAsnCommand whPo) {
        return whAsnDao.findWhAsnById(whPo.getId(), whPo.getOuId());
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB("shardSource")
    public WhAsnCommand findWhAsnByIdToShard(WhAsnCommand whPo) {
        return whAsnDao.findWhAsnById(whPo.getId(), whPo.getOuId());
    }

    @Override
    @MoreDB("infoSource")
    public void editAsnToInfo(WhAsn whasn) {
        int count = 0;
        count = whAsnDao.saveOrUpdateByVersion(whasn);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB("shardSource")
    public void editAsnToShard(WhAsn whasn) {
        int count = 0;
        count = whAsnDao.saveOrUpdateByVersion(whasn);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }
}
