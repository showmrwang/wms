package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

/**
 * 编辑PoAsn单信息
 * 
 * @author bin.hu
 * 
 */
@Service("editPoAsnManagerProxy")
public class EditPoAsnManagerProxyImpl implements EditPoAsnManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(EditPoAsnManagerProxy.class);


    @Autowired
    private AsnManager asnManager;
    @Autowired
    private PoManager poManager;

    /**
     * 修改ASN单状态(可批量)
     */
    @Override
    public int editAsnStatus(WhAsnCommand whAsn) {
        int result = 0;
        if (null == whAsn.getOuId()) {
            // OUID为空更新基础表内信息
            result = asnManager.editAsnStatusByInfo(whAsn);
        } else {
            // OUID不为空更新拆库表内信息
            result = asnManager.editAsnStatusByShard(whAsn);
        }
        return result;
    }

    /**
     * 修改PO单状态(可批量)
     */
    @Override
    public int editPoStatus(WhPoCommand whPo) {
        int result = 0;
        if (null == whPo.getOuId()) {
            // OUID为空更新基础表内信息
            result = poManager.editPoStatusByInfo(whPo);
        } else {
            // OUID不为空更新拆库表内信息
            result = poManager.editPoStatusByShard(whPo);
        }
        return result;
    }

    /**
     * 修改PO单头信息
     */
    @Override
    public ResponseMsg editPo(WhPo po) {
        log.info("EditPo start =======================");
        if (po.getStatus() != PoAsnStatus.PO_NEW) {

        }
        log.info("EditPo end  =======================");
        return null;
    }
}
