package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.poasn.CheckAsnCodeDao;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;

@Service("asnCheckManager")
@Transactional
public class AsnCheckManagerImpl implements AsnCheckManager {

    @Autowired
    private CheckAsnCodeDao checkAsnCodeDao;

    @Override
    @MoreDB("infoSource")
    public boolean insertAsnWithCheckAndOuId(CheckAsnCode checkAsnCode) {
        /**
         * true:不存在此asn单号 false:存在此asn
         */
        boolean flag = false;
        /* 查找check表中是否有此asn单信息 */
        List<CheckAsnCode> asn = checkAsnCodeDao.findListByParam(checkAsnCode);
        if (!asn.isEmpty()) {
            /* 存在此asn单号 */
            flag = true;
        } else {
            /* 不存在此asn单号则在check表中插入此asn信息 */
            checkAsnCodeDao.insert(checkAsnCode);
            // if (1 != i) {
            /* 插入check表成功 */
            flag = false;
            // } else {
            // /* 插入check表失败 */
            // throw new BusinessException(ErrorCodes.SAVE_CHECK_TABLE_FAILED_ASN);
            // }
        }
        return flag;
    }

    @Override
    @MoreDB("infoSource")
    public List<CheckAsnCode> findCheckAsnCodeListByParam(CheckAsnCode checkAsnCode) {
        /* 查找check表中是否有此asn单信息 */
        List<CheckAsnCode> asn = checkAsnCodeDao.findListByParam(checkAsnCode);
        return asn;
    }
}
