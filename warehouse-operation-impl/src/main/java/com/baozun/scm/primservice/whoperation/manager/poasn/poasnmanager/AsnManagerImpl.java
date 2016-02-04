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

import com.baozun.scm.primservice.whoperation.command.poasn.AsnCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;


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
    public ResponseMsg createAsnAndLineToShare(WhAsn asn, ResponseMsg rm) {
        long i = whAsnDao.insert(asn);
        if (0 == i) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED_ASN + "");
            return rm;
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

}
