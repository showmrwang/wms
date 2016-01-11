package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;


/**
 * 创建ASN单据
 * 
 * @author bin.hu
 * 
 */
@Service("createsAsnManager")
@Transactional
public class CreatesAsnManagerImpl implements CreatesAsnManager {

    @Autowired
    private WhAsnDao whAsnDao;

    /**
     * 通过asncode查询出asn列表
     */
    @Override
    public List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode) {
        return whAsnDao.findWhAsnListByAsnCode(asnCode);
    }

}
