package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

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
    private AsnLineManager asnLineManager;
    @Autowired
    private PoManager poManager;
    @Autowired
    private PoLineManager poLineManager;
    @Autowired
    private BiPoManager biPoManager;
    @Autowired
    private BiPoLineManager biPoLineManager;


    /**
     * @author yimin.lu 修改PO单状态为取消; 传递参数：poIds,ouId,status,modifiedId,status；TODO 取消失败的回滚操作
     */
    @Override
    public ResponseMsg cancelPo(WhPoCommand whPo) {
        Long id = whPo.getId();
        Long userId = whPo.getUserId();
        Long ouId = whPo.getOuId();
        log.info("EidtPoAsnManagerProxyImpl.cancelPo: start====================== ");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPo method params:{}", whPo);
        }
        // 循环需要更新的po.id
        if (log.isDebugEnabled()) {
            log.debug("cancelPo method id:{}", id);
        }
        // 根据ID查询到PO单
        WhPo updatePo = poManager.findWhPoByIdToShard(id, ouId);
        if (null == updatePo) {
            log.error("cancelPo method,the id :{} of poids can not find po!", id);
            return getResponseMsg("Can not find po!", ResponseMsg.STATUS_ERROR, null);
        }
        // 组装数据：修改者ID和修改的状态
        WhPo infoPo = this.poManager.findWhPoByExtCodeStoreIdOuIdToInfo(updatePo.getExtCode(), updatePo.getStoreId(), ouId);
        if (null == infoPo) {
            log.error("cancelPo method,the poCode :{}  can not find po in db.info! ", updatePo.getPoCode());
            return getResponseMsg("Can not find po in info", ResponseMsg.STATUS_ERROR, null);
        }

        try {
            this.poManager.cancelPoToShard(updatePo,userId);
            this.poManager.cancelPoToInfo(infoPo, userId);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                log.error(e + "");
                throw e;
            } else {
                log.error("Cancel Po throws exceptions!");
                return getResponseMsg("Cancel Po failure! please retry it!", ResponseMsg.STATUS_ERROR, null);
            }
        }
        return this.getResponseMsg("cancel po success", ResponseMsg.STATUS_SUCCESS, null);
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
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        log.info("EditPoLine start =======================");
        return rm;
    }


    /**
     * 修改POLINE 信息
     */
    @Override
    public ResponseMsg editBiPoLine(BiPoLine biPoLine) {
        log.info("EditPoLine start =======================");
        ResponseMsg rm = new ResponseMsg();
        // POLINE状态必须为新建 已创建ASN 收货中才能修改
        if (biPoLine.getStatus() != PoAsnStatus.BIPOLINE_NEW ) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg("poLine status is error status is: " + biPoLine.getStatus());
            log.warn("EditPoLine warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        this.biPoLineManager.editBiPoLineSingle(biPoLine);
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        log.info("EditPoLine start =======================");
        return rm;
    }

    /**
     * 删除Po单明细
     */
    @Override
    public void deletePoLines(WhPoLineCommand command) {
        log.info("EidtPoAsnManagerProxyImpl.deletePoLines: start====================== ");
        List<WhPoLine> lineList = new ArrayList<WhPoLine>();
        // 循环需要更新的poline.id
        for (Long id : command.getIds()) {
            WhPoLineCommand updateCommand = new WhPoLineCommand();
            // 根据id和ouId分库查询数据
            if (null == command.getOuId()) {
                updateCommand = poLineManager.findPoLineCommandbyIdToInfo(id, command.getOuId());
            } else {
                updateCommand = poLineManager.findPoLineCommandbyIdToShard(id, command.getOuId());
            }
            if (null == updateCommand) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            // 组装数据：修改者ID和修改的状态
            updateCommand.setModifiedId(command.getModifiedId());
            WhPoLine poline = new WhPoLine();
            BeanUtils.copyProperties(updateCommand, poline);
            lineList.add(poline);
        }
        if (lineList.size() > 0) {
            this.poLineManager.deletePoLinesToShard(lineList);
            this.poLineManager.deletePoLinesToInfo(lineList);
        }
        log.info("EidtPoAsnManagerProxyImpl.deletePoLines: end; delete  rows====================== ");
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
        // OUID不为空更新拆库表内信息
        asnManager.saveOrUpdateByVersionToShard(asn);
        log.info("EditAsn end  =======================");
        return rm;
    }

    /**
     * 删除PO单及其PO明细； TODO 删除失败的回滚操作
     */
    @Override
    public ResponseMsg deletePoAndPoLine(WhPoCommand po) {
        log.info("DeletePoAndPoLine start =======================");
        Long userId = po.getUserId();
        Long ouId = po.getOuId();
        WhPo whpo = poManager.findWhPoByIdToShard(po.getId(), po.getOuId());
        if (whpo.getStatus() != PoAsnStatus.PO_NEW) {
            // 如果状态不是新建不允许修改 抛错
            log.warn("DeletePoAndPoLine warn WhPo status NE PO_NEW");
            throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whpo.getPoCode()});
        };
        WhPo infoPo = this.poManager.findWhPoByExtCodeStoreIdOuIdToInfo(whpo.getExtCode(), whpo.getStoreId(), ouId);
        if (null == infoPo) {
            log.warn("DeletePoAndPoLine warn WhPo CAN NOT FIND IN INFO");
            throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whpo.getPoCode()});
        }
        try {
            poManager.deletePoAndPoLineToShard(whpo, userId);
            poManager.deletePoAndPoLineToInfo(infoPo, userId);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DELETE_FAILURE);
        }
        log.info("DeletePoAndPoLine end =======================");
        return this.getResponseMsg("delete po success", ResponseMsg.STATUS_SUCCESS, null);
    }

    /**
     * 审核PO：po单状态不为取消、删除、关闭；ASN都已经关闭或没有ASN
     * 
     * @author yimin.lu
     */
    @Override
    public void auditPo(WhPoCommand poCommand) {
        log.info("auditPo start =======================");
        // po单状态校验:不为取消或关闭的可以继续审核流程
        WhPo whpo = null;
        if (null == poCommand.getOuId()) {
            // 查询基本库内信息
            whpo = poManager.findWhPoByIdToInfo(poCommand.getId(), poCommand.getOuId());
        } else {
            // 查询拆库内信息
            whpo = poManager.findWhPoByIdToShard(poCommand.getId(), poCommand.getOuId());
        }
        if (null == whpo) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (PoAsnStatus.PO_RCVD != whpo.getStatus() && PoAsnStatus.PO_RCVD_FINISH != whpo.getStatus()) {
            throw new BusinessException(ErrorCodes.PO_AUDIT_STATUS_ERROR);
        }
        // ASN校验：po单下Asn不存在可以审核成功;或者存在asn，但是asn状态为取消或者关闭时候。可以审核成功
        List<WhAsn> asnList = this.asnManager.findWhAsnByPoIdOuIdToShard(whpo.getId(), whpo.getOuId());
        if (null == asnList || asnList.size() == 0) {

        } else {
            for (WhAsn asn : asnList) {
                if (null == asn) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                } else {
                    if (PoAsnStatus.ASN_CANCELED != asn.getStatus() && PoAsnStatus.ASN_CLOSE != asn.getStatus()) {
                        throw new BusinessException(ErrorCodes.PO_AUDIT_ASNSTATUS_ERROR);
                    }

                }
            }
        }
        // 修改PO单状态为关闭
        WhPo po = new WhPo();
        BeanUtils.copyProperties(whpo, po);
        po.setStatus(PoAsnStatus.PO_CLOSE);
        po.setModifiedId(poCommand.getUserId());
        if (null == po.getOuId()) {
            this.poManager.saveOrUpdateByVersionToInfo(po);
        } else {
            this.poManager.saveOrUpdateByVersionToShard(po);
        }
        log.info("EditPoAsnManager.auditPo end =======================");
    }

    /**
     * 审核ASN:收货中、收货完成的ASN就允许审核成功
     */
    @Override
    public ResponseMsg auditAsn(WhAsnCommand asnCommand) {
        log.info("auditAsn start =======================");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".auditAsn method params:{}", asnCommand);
        }
        // 查找对应的ASN
        WhAsnCommand whAsnCommand = this.asnManager.findWhAsnCommandByIdToShard(asnCommand.getId(), asnCommand.getOuId());
        if (null == whAsnCommand) {
            log.warn("no asn found [id:{},ouId:{}]", asnCommand.getId(), asnCommand.getOuId());
            return getResponseMsg(ErrorCodes.ASN_NULL + "", ResponseMsg.DATA_ERROR, null);
        }
        if (PoAsnStatus.ASN_RCVD != whAsnCommand.getStatus() && PoAsnStatus.ASN_RCVD_FINISH != whAsnCommand.getStatus()) {
            log.warn("asn'status error [id:{},ouId:{}]", asnCommand.getId(), asnCommand.getOuId());
            return getResponseMsg(ErrorCodes.ASN_AUDIT_STATUS_ERROR + "", ResponseMsg.DATA_ERROR, null);
        }
        // ASN明细校验：只有明细处于收货中或者收货完成可以关闭
        // 查找所有的asn明细
        List<WhAsnLine> asnLineList = this.asnLineManager.findWhAsnLineByAsnIdOuIdToShard(whAsnCommand.getId(), whAsnCommand.getOuId());
        if (null == asnLineList || asnLineList.size() == 0) {
            log.warn("no asnLine found!", asnCommand.getId(), asnCommand.getOuId());
            return getResponseMsg(ErrorCodes.ASNLINE_NULL + "", ResponseMsg.DATA_ERROR, null);
        }
        for (WhAsnLine line : asnLineList) {
            if (null == line) {
                log.warn("asnLine is null!");
                return getResponseMsg(ErrorCodes.ASNLINE_NULL + "", ResponseMsg.DATA_ERROR, null);
            } else {
                if (null == line.getStatus() || PoAsnStatus.ASNLINE_NOT_RCVD == line.getStatus()) {
                    log.warn("asn'status error [id:{},ouId:{}!", line.getId(), line.getOuId());
                    return getResponseMsg(ErrorCodes.ASN_AUDIT_STATUS_ERROR + "", ResponseMsg.DATA_ERROR, null);
                }
            }
        }
        // 修改ASN单状态为关闭
        whAsnCommand.setStatus(PoAsnStatus.ASN_CLOSE);
        whAsnCommand.setAsnIds(Arrays.asList(new Long[] {asnCommand.getId()}));

        try {
            asnManager.editAsnStatusByShard(Arrays.asList(new Long[] {asnCommand.getId()}), asnCommand.getOuId(), PoAsnStatus.ASN_CLOSE, asnCommand.getModifiedId());
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                log.error(e + "");
                throw e;
            } else {
                log.error("edit Asn Status throws Exception!");
                return getResponseMsg("audit Asn Status failure! please retry it!", ResponseMsg.STATUS_ERROR, null);
            }
        }

        log.info("auditASN end =======================");
        return getResponseMsg("auditAsn success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    /**
     * @author yimin.lu
     */
    @Override
    public ResponseMsg deleteAsnAndAsnLine(WhAsnCommand whAsnCommand) {
        /**
         * 逻辑：1.需要删除ASN表头信息；2.需要删除ASN单明细；3.需要更新对应的PO单明细并可能需要回滚状态；4.需要更新对应的PO单表头信息并可能需要回滚状态
         */
        log.info("deleteAsnAndAsnLine start =======================");
        // 查询拆库内信息
        WhAsnCommand whasn = asnManager.findWhAsnCommandByIdToShard(whAsnCommand.getId(), whAsnCommand.getOuId());
        // 业务逻辑：获取到的asn的状态不为新建状态，不能够删除
        if (null == whasn) {
            log.warn("no asn to be delete!");
            return getResponseMsg("no asn to be delete!", ResponseMsg.STATUS_ERROR, null);
        }
        if (null == whasn.getStatus() || whasn.getStatus() != PoAsnStatus.PO_NEW) {
            log.warn("deleteAsnAndAsnLine warn WhPo status NE PO_NEW");
            return getResponseMsg(ErrorCodes.ASN_DELETE_STATUS_ERROR + "", ResponseMsg.DATA_ERROR, null);
        }
        whasn.setModifiedId(whAsnCommand.getUserId());// 操作人更新
        // 检索需要删除的明细
        List<WhAsnLine> asnLineList = this.asnLineManager.findWhAsnLineByAsnIdOuIdToShard(whasn.getId(), whasn.getOuId());
        // 修改asn要对应修改po单、po单明细的数据
        List<WhPoLine> polineList = new ArrayList<WhPoLine>();
        // asn对应的po单
        WhPo whpo = null == whasn.getPoOuId() ? this.poManager.findWhPoByIdToInfo(whasn.getPoId(), whasn.getPoOuId()) : this.poManager.findWhPoByIdToShard(whasn.getPoId(), whasn.getPoOuId());
        if (null == whpo) {
            log.warn("no po found!");
            return getResponseMsg("no po found!", ResponseMsg.STATUS_ERROR, null);
        }
        // po单对应的明细
        List<WhPoLine> whpolineList = null == whasn.getPoOuId() ? this.poLineManager.findWhPoLineListByPoIdToInfo(whpo.getId(), whpo.getOuId()) : this.poLineManager.findWhPoLineListByPoIdToShard(whpo.getId(), whpo.getOuId());
        boolean poStatusFlag = true;// 判断是否需要回滚PO单状态：从已创建ASN回滚到新建状态
        // 对应的ASN明细修改对应的PO单明细
        /**
         * 逻辑：当明细的可用数量=明细的计划数量时候，将明细的状态置为新建状态
         */
        if (whpolineList != null && whpolineList.size() > 0) {
            for (WhPoLine whpoline : whpolineList) {
                if (asnLineList != null && asnLineList.size() > 0) {
                    for (WhAsnLine asnLine : asnLineList) {
                        if (whpoline.getId().equals(asnLine.getPoLineId())) {
                            whpoline.setAvailableQty(whpoline.getAvailableQty() + asnLine.getQtyPlanned());
                            if (whpoline.getAvailableQty() == whpoline.getQtyPlanned()) {
                                whpoline.setStatus(PoAsnStatus.POLINE_NEW);
                            }
                            whpoline.setModifiedId(whasn.getModifiedId());
                            polineList.add(whpoline);
                            break;
                        }
                    }
                }
                if (PoAsnStatus.POLINE_NEW != whpoline.getStatus()) {
                    poStatusFlag = poStatusFlag & false;
                }
            }
        }
        if (poStatusFlag) {
            whpo.setStatus(PoAsnStatus.PO_NEW);
        }
        // 数据库操作
        try {
            this.asnManager.deleteAsnAndAsnLineToShard(whasn, whpo, polineList);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                return getResponseMsg(String.valueOf(((BusinessException) e).getErrorCode()), ResponseMsg.DATA_ERROR, null);
            } else {
                log.error("delete asn failed:{}!", e);
                return getResponseMsg("delete asn failed!", ResponseMsg.STATUS_ERROR, null);
            }
        }
        log.info("deleteAsnAndAsnLine end =======================");
        return getResponseMsg("delete asn success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    @Override
    public ResponseMsg editAsnLine(WhAsnLineCommand whAsnLineCommand) {
        log.info("EditAsnLine start =======================");
        WhAsnLine asnLine = new WhAsnLine();
        BeanUtils.copyProperties(whAsnLineCommand, asnLine);
        // ASNLINE状态必须为未收货 收货中才能修改
        if (whAsnLineCommand.getStatus() != PoAsnStatus.ASNLINE_NOT_RCVD) {
            log.warn("EditAsnLine warn：asnLine status is error status is: " + whAsnLineCommand.getStatus());
            return this.getResponseMsg("asnLine status is error status is: " + whAsnLineCommand.getStatus(), ResponseMsg.STATUS_ERROR, null);
        }
        if (null == whAsnLineCommand.getQtyPlannedOld() || whAsnLineCommand.getQtyPlannedOld() <= 0) {
            log.warn("asnline's qtyPlanned may cause numerical error!");
            return this.getResponseMsg(ErrorCodes.NUMBER_ERROR + "", ResponseMsg.DATA_ERROR, null);
        }
        // double changeCount = whAsnLineCommand.getQtyPlanned() -
        // whAsnLineCommand.getQtyPlannedOld();
        double changeCount = new BigDecimal(Double.toString(whAsnLineCommand.getQtyPlanned())).subtract(new BigDecimal(Double.toString(whAsnLineCommand.getQtyPlannedOld()))).doubleValue();
        // 获取ASN单表头信息
        WhAsnCommand returnAsnCommand = this.asnManager.findWhAsnCommandByIdToShard(asnLine.getAsnId(), asnLine.getOuId());
        WhAsn asn = new WhAsn();
        BeanUtils.copyProperties(returnAsnCommand, asn);
        asn.setModifiedId(whAsnLineCommand.getModifiedId());
        asn.setQtyPlanned(asn.getQtyPlanned() + changeCount);
        // 获取PO单明细
        WhPoLineCommand newPolineCommand =
                null == whAsnLineCommand.getPoOuId() ? this.poLineManager.findPoLineCommandbyIdToInfo(whAsnLineCommand.getPoLineId(), whAsnLineCommand.getPoOuId()) : this.poLineManager.findPoLineCommandbyIdToShard(whAsnLineCommand.getPoLineId(),
                        whAsnLineCommand.getPoOuId());
        if (null == newPolineCommand) {
            log.warn("editAsnLine warn ResponseStatus: asnLine can not Related to poLine,when asnLine.polineid is" + whAsnLineCommand.getPoLineId());
            return this.getResponseMsg("asnLine status is error status is: " + whAsnLineCommand.getStatus(), ResponseMsg.STATUS_ERROR, null);
        }
        WhPoLine poline = new WhPoLine();
        BeanUtils.copyProperties(newPolineCommand, poline);
        poline.setModifiedId(whAsnLineCommand.getModifiedId());
        if (poline.getAvailableQty() - changeCount < 0) {
            log.warn("asnline's  qtyPlanned beyond the limit of poline's availableQty!");
            return this.getResponseMsg(ErrorCodes.ASNLINE_QTYPLANNED_ERROR + "", ResponseMsg.DATA_ERROR, null);
        }
        poline.setAvailableQty(poline.getAvailableQty() - changeCount);
        try {
            this.asnLineManager.editAsnLineWhenPoToShard(asn, asnLine, poline);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                return this.getResponseMsg(((BusinessException) e).getErrorCode() + "", ResponseMsg.DATA_ERROR, null);
            } else {
                log.error("editAsnLine failed!");
                return this.getResponseMsg("editAsnLine failed! please try again!", ResponseMsg.STATUS_ERROR, null);
            }
        }
        log.info("EditAsnLine end =======================");
        return this.getResponseMsg("editAsnLine success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    /**
     * @author yimin.lu
     */
    @Override
    public ResponseMsg deleteAsnLines(WhAsnLineCommand command) {
        log.info(this.getClass().getSimpleName() + ".deleteAsnLines method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".deleteAsnLines params:{}", command);
        }
        List<WhPoLine> polineList = new ArrayList<WhPoLine>();// 用于保存删除asn明细所需对应修改的po单明细行
        List<WhAsnLine> asnlineList = new ArrayList<WhAsnLine>();// 用于保存需要修改的asn单明细
        int asnPlanCount = 0;// 用于记录ASN单中删除明细所对应的数量，从而修改asn单表头的计划数量
        Map<Long, Double> poLineIdMaps = new HashMap<Long, Double>();// 用于保存PO单明细ID和对应修改的数量MAP
        for (Long id : command.getIds()) {
            double changeCount = 0.0;// 对应的asn单明细中的SKU数量
            // 查询对应的ASN明细
            WhAsnLineCommand returnCommand = this.asnLineManager.findWhAsnLineCommandByIdToShard(id, command.getOuId());
            if (PoAsnStatus.ASNLINE_NOT_RCVD != returnCommand.getStatus()) {
                throw new BusinessException(ErrorCodes.ASNLINE_DELETE_STATUS_ERROR);
            }
            changeCount = returnCommand.getQtyPlanned();
            asnPlanCount += changeCount;
            WhAsnLine asnline = new WhAsnLine();
            BeanUtils.copyProperties(returnCommand, asnline);
            asnline.setModifiedId(command.getModifiedId());
            if (log.isDebugEnabled()) {
                log.debug("asnline:{}", asnline);
            }
            asnlineList.add(asnline);
            // PO单ID的MAP集合
            if (poLineIdMaps.containsKey(returnCommand.getPoLineId())) {
                poLineIdMaps.put(returnCommand.getPoLineId(), poLineIdMaps.get(returnCommand.getPoLineId()) + changeCount);
            } else {
                poLineIdMaps.put(returnCommand.getPoLineId(), changeCount);
            }
        }
        if (asnlineList.size() == 0) {
            log.warn("no asnlines selected to delete!");
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        // 对应修改的PO单明细的集合
        for (Entry<Long, Double> entry : poLineIdMaps.entrySet()) {
            WhPoLineCommand serachPoCommand = new WhPoLineCommand();
            serachPoCommand.setId(entry.getKey());
            serachPoCommand.setOuId(command.getPoOuId());
            WhPoLineCommand returnPoCommand = null;
            if (null == command.getPoOuId()) {
                returnPoCommand = this.poLineManager.findPoLineCommandbyIdToInfo(entry.getKey(), command.getPoOuId());// INFO库
            } else {
                returnPoCommand = this.poLineManager.findPoLineCommandbyIdToShard(entry.getKey(), command.getPoOuId());// SHARD库
            }
            WhPoLine poline = new WhPoLine();
            BeanUtils.copyProperties(returnPoCommand, poline);
            // PO单明细需更新内容
            poline.setModifiedId(command.getModifiedId());
            poline.setAvailableQty(poline.getAvailableQty() + entry.getValue());
            // 如果计划数量==可用数量，则回滚PO单明细
            if (poline.getQtyPlanned().intValue() == poline.getAvailableQty().intValue()) {
                poline.setStatus(PoAsnStatus.POLINE_NEW);
            }
            if (log.isDebugEnabled()) {
                log.debug("poline:{}", poline);
            }
            polineList.add(poline);
        }
        // asn单数据
        WhAsnCommand asnSearchCommand = new WhAsnCommand();// asn单表头的检索对象
        asnSearchCommand.setId(asnlineList.get(0).getAsnId());
        asnSearchCommand.setOuId(asnlineList.get(0).getOuId());
        WhAsnCommand whAsnCommand = this.asnManager.findWhAsnCommandByIdToShard(asnlineList.get(0).getAsnId(), asnlineList.get(0).getOuId());
        if (null == whAsnCommand) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        // @mender:yimin.lu 只有新建状态下的ASN的明细才可以删除
        if (PoAsnStatus.ASN_NEW != whAsnCommand.getStatus()) {
            throw new BusinessException(ErrorCodes.ASN_DELETE_STATUS_ERROR);
        }

        WhAsn whAsn = new WhAsn();// 所需修改的asn单
        BeanUtils.copyProperties(whAsnCommand, whAsn);
        whAsn.setModifiedId(asnlineList.get(0).getModifiedId());
        // 修改ASN单表头计划数量
        whAsn.setQtyPlanned(whAsn.getQtyPlanned() - asnPlanCount);
        if (log.isDebugEnabled()) {
            log.debug("whAsn:{}", whAsn);
        }
        try {
            this.asnLineManager.batchDeleteWhenPoToShard(asnlineList, polineList, whAsn);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                return this.getResponseMsg(((BusinessException) e).getErrorCode() + "", ResponseMsg.DATA_ERROR, null);
            } else {
                log.error("editAsnLine failed!");
                return this.getResponseMsg("editAsnLine failed! please try again!", ResponseMsg.STATUS_ERROR, null);
            }
        }
        return this.getResponseMsg("deleteAsnLines success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    /**
     * 返回值设定
     * 
     * @param message
     * @param responseStatus
     * @param reasonStatus
     * @return
     */
    private ResponseMsg getResponseMsg(String message, Integer responseStatus, Integer reasonStatus) {
        log.info(this.getClass().getSimpleName() + ".getResponseMsg method begin!");
        ResponseMsg rm = new ResponseMsg();
        rm.setMsg(message);
        if (null != reasonStatus) {
            rm.setReasonStatus(reasonStatus);
        }
        if (null == responseStatus) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
        } else {
            rm.setResponseStatus(responseStatus);
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".getResponseMsg method params:{}", rm);
        }
        log.info(this.getClass().getSimpleName() + ".getResponseMsg method end!");
        return rm;
    }

    @Override
    public ResponseMsg deleteBiPo(BiPoCommand poCommand) {
        ResponseMsg rm = new ResponseMsg();
        BiPo bipo = this.biPoManager.findBiPoById(poCommand.getId());
        if (null == bipo) {
            rm.setMsg("BiPo is null!");
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            return rm;
        }
        if (PoAsnStatus.BIPO_NEW != bipo.getStatus()) {
            rm.setMsg("BIPO STATUS IS  WRONG FOR DELETE");
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            return rm;
        }
        try {

            this.biPoManager.deleteBiPoAndLine(poCommand.getId(), poCommand.getUserId());
        } catch (BusinessException e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(e.getErrorCode() + "");
            return rm;
        } catch (Exception ex) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.DAO_EXCEPTION + "");
            return rm;
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg("success");
        return rm;
    }

    @Override
    public ResponseMsg cancelBiPo(BiPoCommand command) {
        ResponseMsg rm = new ResponseMsg();
        BiPo bipo = this.biPoManager.findBiPoById(command.getId());
        if (null == bipo) {
            rm.setMsg("BiPo is null!");
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            return rm;
        }
        if (PoAsnStatus.BIPO_NEW != bipo.getStatus()) {
            List<WhPo> whpoList = this.poManager.findWhPoByExtCodeStoreIdToInfo(bipo.getExtCode(), bipo.getStoreId());
            if (whpoList != null) {
                for (WhPo whpo : whpoList) {
                    if (PoAsnStatus.PO_CANCELED != whpo.getStatus()) {
                        rm.setMsg("WHPO status error");
                        rm.setResponseStatus(ResponseMsg.DATA_ERROR);
                        return rm;
                    }
                }
            }

        }
        try {
            this.biPoManager.cancelBiPo(command.getId(), command.getUserId());
        } catch (BusinessException e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(e.getErrorCode() + "");
            return rm;
        } catch (Exception ex) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.DAO_EXCEPTION + "");
            return rm;
        }
        rm.setReasonStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg("success");
        return rm;

    }

    @Override
    public void deleteBiPoLineByPoIdAndUuid(BiPoLineCommand command) {
        this.biPoLineManager.deleteBiPoLineByPoIdAndUuidToInfo(command.getPoId(), command.getUuid(), command.getUserId());
    }

    @Override
    public ResponseMsg editBiPo(BiPo updatePo) {
        ResponseMsg rm = new ResponseMsg();
        try {
            this.biPoManager.editBiPo(updatePo);
        } catch (BusinessException e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(e.getErrorCode() + "");
            return rm;
        } catch (Exception ex) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.DAO_EXCEPTION + "");
            return rm;
        }
        rm.setReasonStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg("success");
        return rm;
    }

    @Override
    public int updateByVersionForLock(Long id, Long ouid, Date lastModifyTime) {
        return this.asnManager.updateByVersionForLock(id, ouid, lastModifyTime);
    }

    @Override
    public int updateByVersionForUnLock(Long id, Long ouid) {
        return this.asnManager.updateByVersionForUnLock(id, ouid);
    }

    @Override
    public ResponseMsg editPoNew(WhPoCommand command) {
        log.info("EditPo start =======================");
        ResponseMsg rm = new ResponseMsg();
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        WhPo shardPo = this.poManager.findWhPoByIdToShard(command.getId(), command.getOuId());
        if (shardPo.getStatus() != PoAsnStatus.PO_NEW) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg("po status is error status is: " + shardPo.getStatus());
            log.warn("EditPo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        try {
            packageUpdatePoData(command, shardPo);
            poManager.saveOrUpdateByVersionToShard(shardPo);
            // #TODO
            WhPo infoPo = this.poManager.findWhPoByExtCodeStoreIdOuIdToInfo(shardPo.getExtCode(), shardPo.getStoreId(), command.getOuId());
            packageUpdatePoData(command, infoPo);
            poManager.saveOrUpdateByVersionToShard(infoPo);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                log.error(e + "");
                throw e;
            } else {
                log.error("update po throws Exception!");
                return getResponseMsg("edit Po failure! please retry it!", ResponseMsg.DATA_ERROR, null);
            }
        }
        log.info("EditPo end  =======================");
        return rm;
    }

    /**
     * po单头信息修改操作数据组装
     * 
     * @param command
     * @return
     */
    private WhPo packageUpdatePoData(WhPoCommand command, WhPo savePo) {
        log.info(this.getClass().getSimpleName() + ".save.updatePo.packageUpdatePoData method ");
        // 物理仓
        savePo.setModifiedId(command.getUserId());
        // 计划到货时间
        try {
            savePo.setEta(DateUtil.getDateFormat(command.getEtaStr(), Constants.DATE_PATTERN_YMD));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 运输商
        if (command.getLogisticsProviderId() != null) {
            savePo.setLogisticsProviderId(command.getLogisticsProviderId());
        }
        // 超收比例
        savePo.setOverChageRate(command.getOverChageRate());
        // 整单质检
        savePo.setIsIqc(command.getIsIqc());
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".save.updatePo.packageUpdatePoData method returns {}", savePo);
        }
        return savePo;
    }

    @Override
    public void deleteBiPoLines(BiPoLineCommand command) {
        log.info("EidtPoAsnManagerProxyImpl.deleteBiPoLines: start====================== ");
        List<BiPoLine> lineList = new ArrayList<BiPoLine>();
        // 循环需要更新的poline.id
        for (Long id : command.getIds()) {
            BiPoLine updateCommand = biPoLineManager.findBiPoLineById(id);

            if (null == updateCommand) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            if (null != command.getOuId() && null == command.getUuid()) {
                throw new BusinessException(ErrorCodes.BIPO_DELETE_HAS_ALLOCATED_ERROR);
            }
            // 组装数据：修改者ID和修改的状态
            updateCommand.setModifiedId(command.getModifiedId());
            lineList.add(updateCommand);
        }
        if (lineList.size() > 0) {
            this.biPoLineManager.deleteList(lineList);
        }
        log.info("EidtPoAsnManagerProxyImpl.deleteBiPoLines: end; delete " + lineList.size() + " rows====================== ");
    }
}
