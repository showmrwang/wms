package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
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
    private AsnLineManager asnLineManager;
    @Autowired
    private PoManager poManager;
    @Autowired
    private PoLineManager poLineManager;

    /**
     * 修改ASN单状态(可批量)
     */
    @Override
    public ResponseMsg editAsnStatus(WhAsnCommand whAsn) {
        log.info(this.getClass().getSimpleName() + ".editAsnStatus method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".editAsnStatus method params:{}", whAsn);
        }
        try {
            if (null == whAsn.getOuId()) {
                // OUID为空更新基础表内信息
                asnManager.editAsnStatusByInfo(whAsn);
            } else {
                // OUID不为空更新拆库表内信息
                asnManager.editAsnStatusByShard(whAsn);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                log.error(e + "");
                throw e;
            } else {
                log.error("edit Asn Status throws Exception!");
                return getResponseMsg("edit Asn Status failure! please retry it!", ResponseMsg.STATUS_ERROR, null);
            }
        }
        return getResponseMsg("edit asn status success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    /**
     * @author yimin.lu 修改PO单状态为取消; 传递参数：poIds,ouId,status,modifiedId,status
     */
    @Override
    public ResponseMsg cancelPo(WhPoCommand whPo) {
        log.info("EidtPoAsnManagerProxyImpl.cancelPo: start====================== ");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPo method params:{}", whPo);
        }
        List<WhPo> poList = new ArrayList<WhPo>();
        // 循环需要更新的po.id
        if (log.isDebugEnabled()) {
            log.debug("cancelPo method first step: foreach==>poids:{}", whPo.getPoIds());
        }
        for (Long id : whPo.getPoIds()) {
            // 根据ID查询到PO单
            WhPoCommand updateCommand = new WhPoCommand();
            // 查询条件Command
            WhPoCommand poCommandForSearch = new WhPoCommand();
            poCommandForSearch.setId(id);
            poCommandForSearch.setOuId(whPo.getOuId());
            // 根据id和ouId分库查询数据
            if (null == poCommandForSearch.getOuId()) {
                updateCommand = poManager.findWhPoByIdToInfo(poCommandForSearch);
            } else {
                updateCommand = poManager.findWhPoByIdToShard(poCommandForSearch);
            }
            if (null == updateCommand) {
                log.error("cancelPo method,the id :{} of poids can not find po!", id);
                return getResponseMsg("Can not find po!", ResponseMsg.STATUS_ERROR, null);
            }
            // 组装数据：修改者ID和修改的状态
            updateCommand.setModifiedId(whPo.getModifiedId());
            WhPo po = new WhPo();
            BeanUtils.copyProperties(updateCommand, po);
            poList.add(po);
        }
        if (poList.size() == 0) {
            log.error("no po found to be cancelled!");
            return getResponseMsg("no po to be cancelled!", ResponseMsg.STATUS_ERROR, null);
        }
        try{
            if (null == whPo.getOuId()) {
                this.poManager.cancelPoToInfo(poList);
            } else {
                this.poManager.cancelPoToShard(poList);
            }
        }catch(Exception e){
            if(e instanceof BusinessException){
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
        try{
            if (null == po.getOuId()) {
                // OUID为空更新基础表内信息
                poManager.editPoToInfo(po);
            } else {
                // OUID不为空更新拆库表内信息
                poManager.editPoToShard(po);
            }
        }catch(Exception e){
            if (e instanceof BusinessException) {
                log.error(e+"");
                throw e;
            }else{
                log.error("update po throws Exception!");
                return getResponseMsg("edit Po failure! please retry it!", ResponseMsg.DATA_ERROR, null);
            }
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
            WhPoLineCommand lineCommand = new WhPoLineCommand();
            lineCommand.setId(id);
            lineCommand.setOuId(command.getOuId());
            // 根据id和ouId分库查询数据
            if (null == lineCommand.getOuId()) {
                updateCommand = poLineManager.findPoLinebyIdToInfo(lineCommand);
            } else {
                updateCommand = poLineManager.findPoLinebyIdToShard(lineCommand);
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
        int deletecount = 0;
        if (lineList.size() > 0) {
            if (null == command.getOuId()) {
                deletecount = this.poLineManager.deletePoLinesToInfo(lineList);
            } else {
                deletecount = this.poLineManager.deletePoLinesToShard(lineList);
            }
        }
        log.info("EidtPoAsnManagerProxyImpl.deletePoLines: end; delete " + deletecount + " rows====================== ");
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
    public ResponseMsg deletePoAndPoLine(List<WhPoCommand> whPoCommandList) {
        log.info("DeletePoAndPoLine start =======================");
        if (null == whPoCommandList || whPoCommandList.size() == 0) {
            return this.getResponseMsg("no po selected to delete", null, null);
        }
        for (WhPoCommand po : whPoCommandList) {
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
            if (null == whPoCommandList.get(0).getOuId()) {
                // 删除基础库PO单信息
                poManager.deletePoAndPoLineToInfo(whPoCommandList);
            } else {
                // 删除拆库PO单信息 TODO 需要补偿机制
                poManager.deletePoAndPoLineToShard(whPoCommandList);
                List<CheckPoCode> poCodeList = new ArrayList<CheckPoCode>();
                for (WhPoCommand po : whPoCommandList) {
                    CheckPoCode cpCode = new CheckPoCode();
                    cpCode.setOuId(po.getOuId());
                    cpCode.setPoCode(po.getPoCode());
                    cpCode.setStoreId(po.getStoreId());
                    poCodeList.add(cpCode);
                }

                poManager.deleteCheckPoCodeToInfo(poCodeList, whPoCommandList.get(0).getUserId());
            }
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
        WhPoCommand whpo = null;
        if (null == poCommand.getOuId()) {
            // 查询基本库内信息
            whpo = poManager.findWhPoByIdToInfo(poCommand);
        } else {
            // 查询拆库内信息
            whpo = poManager.findWhPoByIdToShard(poCommand);
        }
        if (null == whpo) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (PoAsnStatus.PO_RCVD != whpo.getStatus() && PoAsnStatus.PO_RCVD_FINISH != whpo.getStatus()) {
            throw new BusinessException(ErrorCodes.PO_AUDIT_STATUS_ERROR);
        }
        // ASN校验：po单下Asn不存在可以审核成功;或者存在asn，但是asn状态为取消或者关闭时候。可以审核成功
        WhAsn whAsn = new WhAsn();
        whAsn.setPoId(whpo.getId());
        whAsn.setPoOuId(whpo.getOuId());
        List<WhAsn> asnList = this.asnManager.findWhAsnByPoToShard(whAsn);
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
        WhAsnCommand whAsnCommand = this.asnManager.findWhAsnByIdToShard(asnCommand);
        if (null == whAsnCommand) {
            log.warn("no asn found [id:{},ouId:{}]", asnCommand.getId(), asnCommand.getOuId());
            return getResponseMsg(ErrorCodes.ASN_NULL + "", ResponseMsg.DATA_ERROR, null);
        }
        if (PoAsnStatus.ASN_CANCELED == whAsnCommand.getStatus() || PoAsnStatus.ASN_CLOSE == whAsnCommand.getStatus()) {
            log.warn("asn'status error [id:{},ouId:{}]", asnCommand.getId(), asnCommand.getOuId());
            return getResponseMsg(ErrorCodes.ASN_AUDIT_STATUS_ERROR + "", ResponseMsg.DATA_ERROR, null);
        }
        // ASN明细校验：只有明细处于收货中或者收货完成可以关闭
        // 查找所有的asn明细
        WhAsnLine whAsnLine = new WhAsnLine();
        whAsnLine.setAsnId(whAsnCommand.getId());
        whAsnLine.setOuId(whAsnCommand.getOuId());
        List<WhAsnLine> asnLineList = this.asnLineManager.findListByShard(whAsnLine);
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
            if (null == whAsnCommand.getOuId()) {
                // OUID为空更新基础表内信息
                asnManager.editAsnStatusByInfo(whAsnCommand);
            } else {
                // OUID不为空更新拆库表内信息
                asnManager.editAsnStatusByShard(whAsnCommand);
            }
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
        WhAsnCommand whasn = asnManager.findWhAsnByIdToShard(whAsnCommand);
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
        WhAsnLine asnLineSearch=new WhAsnLine();
        asnLineSearch.setAsnId(whasn.getId());
        asnLineSearch.setOuId(whasn.getOuId());
        List<WhAsnLine> asnLineList=this.asnLineManager.findListByShard(asnLineSearch);
        // 修改asn要对应修改po单、po单明细的数据
        WhPoCommand poSearch=new WhPoCommand();
        poSearch.setId(whasn.getPoId());
        poSearch.setOuId(whasn.getPoOuId());
        List<WhPoLine> polineList = new ArrayList<WhPoLine>();
        // asn对应的po单
        WhPoCommand whpo = null == whasn.getPoOuId() ? this.poManager.findWhPoByIdToInfo(poSearch) : this.poManager.findWhPoByIdToShard(poSearch);
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
                    poStatusFlag = false;
                }
            }
        }
        if (poStatusFlag) {
            whpo.setStatus(PoAsnStatus.PO_NEW);
        }
        // 数据库操作
        try{
            if(null==whasn.getPoOuId()){
                // TODO 需要补偿机制
                this.poManager.editPoAdnPoLineWhenDeleteAsnToInfo(whpo, polineList);
                this.asnManager.deleteAsnAndAsnLineWhenPoOuIdNullToShard(whasn);
                // 对应的PO单在拆库中
            } else {
                this.asnManager.deleteAsnAndAsnLineToShard(whasn, whpo, polineList);
            }
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
        int changeCount = whAsnLineCommand.getQtyPlanned() - whAsnLineCommand.getQtyPlannedOld();
        WhPoLineCommand polineCommand = new WhPoLineCommand();
        polineCommand.setId(whAsnLineCommand.getPoLineId());
        polineCommand.setOuId(whAsnLineCommand.getPoOuId());
        // 获取ASN单表头信息
        WhAsnCommand searchAsnCommand = new WhAsnCommand();
        searchAsnCommand.setId(asnLine.getAsnId());
        searchAsnCommand.setOuId(asnLine.getOuId());
        WhAsnCommand returnAsnCommand = this.asnManager.findWhAsnByIdToShard(searchAsnCommand);
        WhAsn asn = new WhAsn();
        BeanUtils.copyProperties(returnAsnCommand, asn);
        asn.setModifiedId(whAsnLineCommand.getModifiedId());
        asn.setQtyPlanned(asn.getQtyPlanned() + changeCount);
        // 获取PO单明细
        WhPoLineCommand newPolineCommand = null == whAsnLineCommand.getPoOuId() ? this.poLineManager.findPoLinebyIdToInfo(polineCommand) : this.poLineManager.findPoLinebyIdToShard(polineCommand);
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
        try{
            if (null == whAsnLineCommand.getPoOuId()) {
                // TODO yimin.lu 需要补偿机制
                this.asnLineManager.editAsnLineToShard(asn, asnLine);
                this.poLineManager.saveOrUpdateByVersionToInfo(poline);
            } else {
                this.asnLineManager.editAsnLineWhenPoToShard(asn, asnLine, poline);
            }
        }catch(Exception e){
            if( e instanceof BusinessException){
                return this.getResponseMsg(((BusinessException) e).getErrorCode()+"", ResponseMsg.DATA_ERROR, null);
            }else{
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
    public void deleteAsnLines(WhAsnLineCommand command) {
        List<WhPoLine> polineList = new ArrayList<WhPoLine>();// 用于保存删除asn明细所需对应修改的po单明细行
        List<WhAsnLine> asnlineList = new ArrayList<WhAsnLine>();// 用于保存需要修改的asn单明细
        int asnPlanCount = 0;// 用于记录ASN单中删除明细所对应的数量，从而修改asn单表头的计划数量
        Map<Long, Integer> poLineIdMaps = new HashMap<Long, Integer>();// 用于保存PO单明细ID和对应修改的数量MAP
        for(Long id:command.getIds()){
            int changeCount = 0;// 对应的asn单明细中的SKU数量
            WhAsnLineCommand searchCommand=new WhAsnLineCommand();
            // 查询对应的ASN明细
            searchCommand.setId(id);
            searchCommand.setOuId(command.getOuId());
            WhAsnLineCommand returnCommand = this.asnLineManager.findWhAsnLineByIdToShard(searchCommand);

            changeCount=returnCommand.getQtyPlanned();
            asnPlanCount += changeCount;
            WhAsnLine asnline=new WhAsnLine();
            BeanUtils.copyProperties(returnCommand, asnline);
            asnline.setModifiedId(command.getModifiedId());
            asnlineList.add(asnline);
            // put
            if (poLineIdMaps.containsKey(returnCommand.getPoLineId())) {
                poLineIdMaps.put(returnCommand.getPoLineId(), poLineIdMaps.get(returnCommand.getPoLineId()) + changeCount);
            } else {
                poLineIdMaps.put(returnCommand.getPoLineId(), changeCount);
            }
        }
        // PO单明细的更新
        for (Entry<Long, Integer> entry : poLineIdMaps.entrySet()) {
            WhPoLineCommand serachPoCommand = new WhPoLineCommand();
            serachPoCommand.setId(entry.getKey());
            serachPoCommand.setOuId(command.getPoOuId());
            WhPoLineCommand returnPoCommand = null;
            if (null == command.getPoOuId()) {
                returnPoCommand = this.poLineManager.findPoLinebyIdToInfo(serachPoCommand);
            } else {
                returnPoCommand = this.poLineManager.findPoLinebyIdToShard(serachPoCommand);
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
            polineList.add(poline);
        }
        if (asnlineList.size() == 0) {
            log.warn("no asnlines selected to delete!");
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        WhAsnCommand asnSearchCommand = new WhAsnCommand();// asn单表头的检索对象
        asnSearchCommand.setId(asnlineList.get(0).getAsnId());
        asnSearchCommand.setOuId(asnlineList.get(0).getOuId());
        WhAsnCommand whAsnCommand = this.asnManager.findWhAsnByIdToShard(asnSearchCommand);
        if (null == whAsnCommand) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }

        WhAsn whAsn = new WhAsn();// 所需修改的asn单
        BeanUtils.copyProperties(whAsnCommand, whAsn);
        whAsn.setModifiedId(asnlineList.get(0).getModifiedId());
        // 修改ASN单表头计划数量
        whAsn.setQtyPlanned(whAsn.getQtyPlanned() - asnPlanCount);
        if (null == command.getPoOuId()) {
            // TODO yimin.lu
            this.asnLineManager.batchDeleteWhenPoToInfo(asnlineList, whAsn);
            this.poLineManager.batchUpdatePoLine(polineList);
        } else {
            this.asnLineManager.batchDeleteWhenPoToShard(asnlineList, polineList, whAsn);
        }

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
}
