package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @author yimin.lu 修改PO单状态为取消; 传递参数：poIds,ouId,status,modifiedId,status
     */
    @Override
    public ResponseMsg cancelPo(WhPoCommand whPo) {
        log.info("EidtPoAsnManagerProxyImpl.cancelPo: start====================== ");
        List<WhPo> poList = new ArrayList<WhPo>();
        // 循环需要更新的po.id
        for (Long id : whPo.getPoIds()) {
            // 根据ID查询到PO单
            WhPoCommand updateCommand = new WhPoCommand();
            // 查询条件Command
            WhPoCommand poCommand = new WhPoCommand();
            poCommand.setId(id);
            poCommand.setOuId(whPo.getOuId());
            // 根据id和ouId分库查询数据
            if (null == poCommand.getOuId()) {
                updateCommand = poManager.findWhPoByIdToInfo(poCommand);
            } else {
                updateCommand = poManager.findWhPoByIdToShard(poCommand);
            }
            if (null == updateCommand) {
                return getResponseMsg("Can not find po!", ResponseMsg.DATA_ERROR, null);
            }
            // 组装数据：修改者ID和修改的状态
            updateCommand.setModifiedId(whPo.getModifiedId());
            WhPo po = new WhPo();
            BeanUtils.copyProperties(updateCommand, po);
            poList.add(po);
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPo params:{}", poList);
        }
        if (poList.size() == 0) {
            return getResponseMsg("no po to be cancelled!", ResponseMsg.DATA_ERROR, null);
        }
        try{
            if (null == whPo.getOuId()) {
                this.poManager.cancelPoToInfo(poList);
            } else {
                this.poManager.cancelPoToShard(poList);
            }
        }catch(Exception e){
            if(e instanceof BusinessException){
                throw e;
            } else {
                return getResponseMsg("Cancel Po failure! please retry it!", ResponseMsg.DATA_ERROR, null);
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
    public void auditAsn(WhAsnCommand asnCommand) {
        log.info("auditAsn start =======================");
        WhAsnCommand whAsnCommand = this.asnManager.findWhAsnByIdToShard(asnCommand);
        if (null == whAsnCommand) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (PoAsnStatus.ASN_CANCELED == whAsnCommand.getStatus() || PoAsnStatus.ASN_CLOSE == whAsnCommand.getStatus()) {
            throw new BusinessException(ErrorCodes.ASN_AUDIT_STATUS_ERROR);
        }
        // ASN明细校验：只有明细处于收货中或者收货完成可以关闭
        WhAsnLine whAsnLine = new WhAsnLine();
        whAsnLine.setAsnId(whAsnCommand.getId());
        whAsnLine.setOuId(whAsnCommand.getOuId());
        List<WhAsnLine> asnLineList = this.asnLineManager.findListByShard(whAsnLine);
        if (null == asnLineList || asnLineList.size() == 0) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        for (WhAsnLine line : asnLineList) {
            if (null == line) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            } else {
                if (null == line.getStatus() || PoAsnStatus.ASNLINE_NOT_RCVD == line.getStatus()) {
                    throw new BusinessException(ErrorCodes.ASN_AUDIT_STATUS_ERROR);
                }
            }
        }
        // 修改ASN单状态为关闭
        whAsnCommand.setStatus(PoAsnStatus.ASN_CLOSE);
        whAsnCommand.setAsnIds(Arrays.asList(new Long[] {asnCommand.getId()}));
        int updateCount = this.editAsnStatus(whAsnCommand);
        if (updateCount == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        log.info("auditPo end =======================");
    }

    @Override
    public void deleteAsnAndAsnLine(WhAsnCommand whAsnCommand) {
        log.info("deleteAsnAndAsnLine start =======================");
        WhAsnCommand whasn = null;
        // 查询拆库内信息
        whasn = asnManager.findWhAsnByIdToShard(whAsnCommand);
        // 业务逻辑：获取到的asn的状态不为新建状态，不能够删除
        if (null == whasn || null == whasn.getStatus()) {
            throw new BusinessException(ErrorCodes.DELETE_CODE_ERROR);
        }
        if (whasn.getStatus() != PoAsnStatus.PO_NEW) {
            log.warn("deleteAsnAndAsnLine warn WhPo status NE PO_NEW");
            throw new BusinessException(ErrorCodes.ASN_DELETE_STATUS_ERROR, new Object[] {whasn.getAsnExtCode()});
        }
        
        WhAsnLine asnLineSearch=new WhAsnLine();
        asnLineSearch.setAsnId(whasn.getId());
        asnLineSearch.setOuId(whasn.getOuId());
        List<WhAsnLine> asnLineList=this.asnLineManager.findListByShard(asnLineSearch);
        // 修改asn要对应修改po单、po单明细的数据
        WhPoCommand poSearch=new WhPoCommand();
        poSearch.setId(whasn.getPoId());
        poSearch.setOuId(whasn.getPoOuId());
        List<WhPoLine> polineList = new ArrayList<WhPoLine>();
        if(null==whasn.getPoOuId()){
            WhPoCommand whpo = this.poManager.findWhPoByIdToInfo(poSearch);
            if (null == whpo) {
                throw new BusinessException(ErrorCodes.DELETE_CODE_ERROR);
            }
            List<WhPoLine> whpolineList = this.poLineManager.findWhPoLineListByPoIdToInfo(whpo.getId(), whpo.getOuId());
            boolean poStatusFlag = true;
            if (asnLineList != null && asnLineList.size() > 0) {
                for(WhAsnLine asnLine:asnLineList){
                    if (whpolineList != null && whpolineList.size() > 0) {
                        for (WhPoLine whpoline : whpolineList) {

                            if (whpoline.getId().equals(asnLine.getPoLineId())) {
                                whpoline.setAvailableQty(whpoline.getAvailableQty() + asnLine.getQtyPlanned());
                                if (whpoline.getAvailableQty() == whpoline.getQtyPlanned()) {
                                    whpoline.setStatus(PoAsnStatus.POLINE_NEW);
                                }
                                polineList.add(whpoline);
                            }
                            if (PoAsnStatus.POLINE_CREATE_ASN == whpoline.getStatus()) {
                                if (poStatusFlag) {
                                    poStatusFlag = false;
                                }
                            }
                        }
                    }


                }
                if (poStatusFlag) {
                    whpo.setStatus(PoAsnStatus.PO_NEW);
                }
             // TODO 需要补偿机制
                this.poManager.editPoAdnPoLineWhenDeleteAsnToInfo(whpo, polineList);
                this.asnManager.deleteAsnAndAsnLineWhenPoOuIdNullToShard(whAsnCommand);
            }
        } else {
            WhPoCommand whpo = this.poManager.findWhPoByIdToShard(poSearch);
            if (null == whpo) {
                throw new BusinessException(ErrorCodes.DELETE_CODE_ERROR);
            }
            List<WhPoLine> whpolineList = this.poLineManager.findWhPoLineListByPoIdToShard(whpo.getId(), whpo.getOuId());
            boolean poStatusFlag = true;
            if (asnLineList != null && asnLineList.size() > 0) {
                for (WhAsnLine asnLine : asnLineList) {
                    if (whpolineList != null && whpolineList.size() > 0) {
                        for (WhPoLine whpoline : whpolineList) {

                            if (whpoline.getId().equals(asnLine.getPoLineId())) {
                                whpoline.setAvailableQty(whpoline.getAvailableQty() + asnLine.getQtyPlanned());
                                if (whpoline.getAvailableQty() == whpoline.getQtyPlanned()) {
                                    whpoline.setStatus(PoAsnStatus.POLINE_NEW);
                                }
                                polineList.add(whpoline);
                            }
                            if (PoAsnStatus.POLINE_CREATE_ASN == whpoline.getStatus()) {
                                if (poStatusFlag) {
                                    poStatusFlag = false;
                                }
                            }
                        }
                    }


                }
                if (poStatusFlag) {
                    whpo.setStatus(PoAsnStatus.PO_NEW);
                }
                this.asnManager.deleteAsnAndAsnLineToShard(whAsnCommand, whpo, polineList);
            }
        }
        log.info("deleteAsnAndAsnLine end =======================");

    }

    @Override
    public ResponseMsg editAsnLine(WhAsnLineCommand whAsnLineCommand) {
        log.info("EditAsnLine start =======================");
        WhAsnLine asnLine = new WhAsnLine();
        BeanUtils.copyProperties(whAsnLineCommand, asnLine);
        ResponseMsg rm = new ResponseMsg();
        // ASNLINE状态必须为未收货 收货中才能修改
        if (whAsnLineCommand.getStatus() != PoAsnStatus.ASNLINE_NOT_RCVD) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg("asnLine status is error status is: " + whAsnLineCommand.getStatus());
            log.warn("EditAsnLine warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        Integer changeCount = whAsnLineCommand.getQtyPlanned() - whAsnLineCommand.getQtyPlannedOld();
        // 这个IF的逻辑：
        // TODO:需求待确认，ASN只能修改数量的时候有此段逻辑
        if (changeCount != null && changeCount == 0) {
            if (null == whAsnLineCommand.getPoOuId()) {
                WhPoLineCommand polineCommand = new WhPoLineCommand();
                polineCommand.setId(whAsnLineCommand.getPoLineId());
                polineCommand.setOuId(whAsnLineCommand.getPoOuId());
                WhPoLineCommand newPolineCommand = this.poLineManager.findPoLinebyIdToInfo(polineCommand);
                if (null == newPolineCommand) {
                    rm.setResponseStatus(ResponseMsg.DATA_ERROR);
                    rm.setMsg("asnLine can not Related to poLine,when asnLine.polineid is" + whAsnLineCommand.getPoLineId());
                    log.warn("EditPoLine warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                    return rm;
                }
                WhPoLine poline = new WhPoLine();
                BeanUtils.copyProperties(polineCommand, poline);
                poline.setModifiedId(whAsnLineCommand.getModifiedId());
                poline.setAvailableQty(poline.getAvailableQty() + changeCount);
                // TODO yimin.lu 需要补偿机制
                this.asnLineManager.editAsnLineToShard(asnLine);
                this.poLineManager.saveOrUpdateByVersionToInfo(poline);
            } else {
                WhPoLineCommand polineCommand = new WhPoLineCommand();
                polineCommand.setId(whAsnLineCommand.getPoLineId());
                polineCommand.setOuId(whAsnLineCommand.getPoOuId());
                WhPoLineCommand newPolineCommand = this.poLineManager.findPoLinebyIdToShard(polineCommand);
                if (null == newPolineCommand) {
                    rm.setResponseStatus(ResponseMsg.DATA_ERROR);
                    rm.setMsg("asnLine can not Related to poLine,when asnLine.polineid is" + whAsnLineCommand.getPoLineId());
                    log.warn("EditPoLine warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                    return rm;
                }
                WhPoLine poline = new WhPoLine();
                BeanUtils.copyProperties(polineCommand, poline);
                poline.setModifiedId(whAsnLineCommand.getModifiedId());
                poline.setAvailableQty(poline.getAvailableQty() + changeCount);
                this.asnLineManager.editAsnLineWhenPoToShard(asnLine, poline);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        log.info("EditAsnLine start =======================");
        return rm;
    }

    @Override
    public void deleteAsnLines(WhAsnLineCommand command) {
        List<WhPoLine> polineList=new ArrayList<WhPoLine>();
        List<WhAsnLine> asnlineList=new ArrayList<WhAsnLine>();
        for(Long id:command.getIds()){
            int changeCount=0;
            WhAsnLineCommand searchCommand=new WhAsnLineCommand();
            searchCommand.setId(id);
            searchCommand.setOuId(command.getOuId());
            WhAsnLineCommand returnCommand = this.asnLineManager.findWhAsnLineByIdToShard(searchCommand);
            changeCount=returnCommand.getQtyPlanned();
            WhAsnLine asnline=new WhAsnLine();
            BeanUtils.copyProperties(returnCommand, asnline);
            asnline.setModifiedId(command.getModifiedId());
            asnlineList.add(asnline);

            WhPoLineCommand serachPoCommand = new WhPoLineCommand();
            serachPoCommand.setId(returnCommand.getPoLineId());
            serachPoCommand.setOuId(command.getPoOuId());
            WhPoLineCommand returnPoCommand = null;
            if (null == command.getPoOuId()) {
                returnPoCommand = this.poLineManager.findPoLinebyIdToInfo(serachPoCommand);
            } else {
                returnPoCommand = this.poLineManager.findPoLinebyIdToShard(serachPoCommand);
            }
            WhPoLine poline = new WhPoLine();
            BeanUtils.copyProperties(returnPoCommand, poline);
            poline.setModifiedId(command.getModifiedId());
            poline.setAvailableQty(poline.getAvailableQty() + changeCount);
            polineList.add(poline);
        }
        if (null == command.getPoOuId()) {
            // TODO yimin.lu
            this.asnLineManager.batchDeleteWhenPoToInfo(asnlineList);
            this.poLineManager.batchUpdatePoLine(polineList);
        } else {
            this.asnLineManager.batchDeleteWhenPoToShard(asnlineList, polineList);
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
        return rm;
    }
}
