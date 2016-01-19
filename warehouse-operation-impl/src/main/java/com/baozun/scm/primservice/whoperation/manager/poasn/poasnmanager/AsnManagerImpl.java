package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;


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

}
