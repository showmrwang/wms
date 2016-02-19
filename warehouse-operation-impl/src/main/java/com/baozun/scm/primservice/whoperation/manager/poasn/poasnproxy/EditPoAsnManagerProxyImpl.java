package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

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
    @Autowired
    private PoLineManager poLineManager;

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
            result = poManager.editPoStatusToInfo(whPo);
        } else {
            // OUID不为空更新拆库表内信息
            result = poManager.editPoStatusToShard(whPo);
        }
        return result;
    }

    /**
     * 修改PO单头信息
     */
    @Override
    public ResponseMsg editPo(WhPo po) {
        log.info("EditPo start =======================");
        ResponseMsg rm = new ResponseMsg();
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        if (po.getStatus() != PoAsnStatus.PO_NEW) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg("po status is error status is: " + po.getStatus());
            log.warn("EditPo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        if (null == po.getOuId()) {
            // OUID为空更新基础表内信息
            poManager.editPoToInfo(po);
        } else {
            // OUID不为空更新拆库表内信息
            poManager.editPoToShard(po);
        }
        log.info("EditPo end  =======================");
        return rm;
    }

    /**
     * 通过poid and ouid and uuid删除对应po明细
     */
    @Override
    public void deletePoLineByUuid(WhPoLineCommand whPoLine) {
        log.info("DeletePoLineByUuid start =======================");
        try {
            if (null == whPoLine.getOuId()) {
                // OUID为空删除基础表内信息
                poLineManager.deletePoLineByUuidToInfo(whPoLine);
            } else {
                // OUID不为空删除拆库表内信息
                poLineManager.deletePoLineByUuidToShard(whPoLine);
            }
        } catch (Exception e) {
            log.error("DeletePoLineByUuid Error PoId: " + whPoLine.getPoId() + " OuId: " + whPoLine.getOuId() + " UUID: " + whPoLine.getUuid());
            log.error(e + "");
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        log.info("DeletePoLineByUuid start =======================");
    }

    /**
     * 修改POLINE 信息
     */
    @Override
    public ResponseMsg editPoLine(WhPoLine whPoLine) {
        log.info("EditPoLine start =======================");
        ResponseMsg rm = new ResponseMsg();
        // POLINE状态必须为新建 已创建ASN 收货中才能修改
        if (whPoLine.getStatus() != PoAsnStatus.POLINE_NEW && whPoLine.getStatus() != PoAsnStatus.POLINE_CREATE_ASN && whPoLine.getStatus() != PoAsnStatus.POLINE_RCVD) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg("poLine status is error status is: " + whPoLine.getStatus());
            log.warn("EditPoLine warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        if (null == whPoLine.getOuId()) {
            // OUID为空修改基础表内信息
            poLineManager.editPoLineToInfo(whPoLine);
        } else {
            // OUID不为空修改拆库表内信息
            poLineManager.editPoLineToShare(whPoLine);
        }
        log.info("EditPoLine start =======================");
        return rm;
    }

    /**
     * 修改PO单明细状态(可批量)
     */
    @Override
    public int editPoLineStatus(WhPoLineCommand command) {
        int result = 0;
        if (null == command.getOuId()) {
            // OUID为空更新基础表内信息
            result = poLineManager.editPoLineStatusToInfo(command);
        } else {
            // OUID不为空更新拆库表内信息
            result = poLineManager.editPoLineStatusToShard(command);
        }
        return result;
    }

    /**
     * 修改ASN信息
     */
    @Override
    public ResponseMsg editAsn(WhAsn asn) {
        log.info("EditAsn start =======================");
        ResponseMsg rm = new ResponseMsg();
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        if (asn.getStatus() != PoAsnStatus.PO_NEW) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg("po status is error status is: " + asn.getStatus());
            log.warn("EditPo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        if (null == asn.getOuId()) {
            // OUID为空更新基础表内信息
            asnManager.editAsnToInfo(asn);
        } else {
            // OUID不为空更新拆库表内信息
            asnManager.editAsnToShard(asn);
        }
        log.info("EditAsn end  =======================");
        return rm;
    }

    /**
     * 删除PO单及其PO明细
     */
    @Override
    public void deletePoAndPoLine(List<WhPoCommand> whPoCommand) {
        log.info("DeletePoAndPoLine start =======================");
        for (WhPoCommand po : whPoCommand) {
            WhPoCommand whpo = null;
            if (null == po.getOuId()) {
                // 查询基本库内信息
                whpo = poManager.findWhPoByIdToInfo(po);
                if (whpo.getStatus() != PoAsnStatus.PO_NEW) {
                    // 如果状态不是新建不允许修改 抛错
                    log.warn("DeletePoAndPoLine warn WhPo status NE PO_NEW");
                    throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whpo.getPoCode()});
                }
            } else {
                // 查询拆库内信息
                whpo = poManager.findWhPoByIdToShard(po);
                if (whpo.getStatus() != PoAsnStatus.PO_NEW) {
                    // 如果状态不是新建不允许修改 抛错
                    log.warn("DeletePoAndPoLine warn WhPo status NE PO_NEW");
                    throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whpo.getPoCode()});
                }
            }
        }

        try {
            // 删除对应PO单和POLINE明细
            if (null == whPoCommand.get(0).getOuId()) {
                // 删除基础库PO单信息
                poManager.deletePoAndPoLineToInfo(whPoCommand);
            } else {
                // 删除拆库PO单信息
                poManager.deletePoAndPoLineToShard(whPoCommand);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DELETE_FAILURE);
        }
        log.info("DeletePoAndPoLine end =======================");
    }
}
