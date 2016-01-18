package com.baozun.scm.primservice.whoperation.manager.poasn.editpoasn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.manager.poasn.editpoasn.EditPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;

/**
 * 编辑PoAsn单信息
 * 
 * @author bin.hu
 * 
 */
@Service("editPoAsnManagerProxy")
public class EditPoAsnManagerProxyImpl implements EditPoAsnManagerProxy {

    @Autowired
    private AsnManager asnManager;

    /**
     * 修改ASN单状态(可批量)
     */
    @Override
    public int editAsnStatus(WhAsnCommand whAsn) {
        int result = 0;
        if (null == whAsn.getOuId()) {
            result = asnManager.editAsnStatusByInfo(whAsn);
        } else {
            result = asnManager.editAsnStatusByShard(whAsn);
        }
        return result;
    }

}
