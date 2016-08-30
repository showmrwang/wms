package com.baozun.scm.primservice.whoperation.manager.odo;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoTransportMgmtCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoTransportMgmtManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;

@Service("odoManagerProxy")
public class OdoManagerProxyImpl extends BaseManagerImpl implements OdoManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(OdoManagerProxy.class);
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private OdoLineManager OdoLineManager;
    @Autowired
    private OdoTransportMgmtManager odoTransportMgmtManager;

    @Override
    public Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoManager.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public ResponseMsg createOdoFromWms(OdoGroupCommand odoGroup) {
        ResponseMsg msg = new ResponseMsg();
        try {
            Long ouId = odoGroup.getOuId();
            Long userId = odoGroup.getUserId();
            // 原始数据集合
            OdoCommand odoCommand = odoGroup.getOdo();
            OdoTransportMgmtCommand transportMgmtCommand = odoGroup.getTransPortMgmt();

            /**
             * 第一步：封装ODO
             */
            WhOdo odo = this.copyOdoProperties(odoCommand);
            /**
             * 第二步：封装出库单运输商表
             */
            WhOdoTransportMgmt transportMgmt = this.copyTransportMgmtProperties(transportMgmtCommand);
            this.createOdo(odo, transportMgmt, null, ouId, userId);
        } catch (BusinessException e) {
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            msg.setMsg(e.getErrorCode() + "");
            return msg;
        } catch (Exception ex) {
            log.error("" + ex);
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            msg.setMsg(ErrorCodes.PARAMS_ERROR + "");
            return msg;
        }
        msg.setMsg("SUCCESS");
        msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return msg;
    }

    private void createOdo(WhOdo odo, WhOdoTransportMgmt transportMgmt, List<WhOdoLine> odoLineList, Long ouId, Long userId) {
        try {

            // 默认属性
            odo.setCurrentQty(Constants.DEFAULT_DOUBLE);
            odo.setActualQty(Constants.DEFAULT_DOUBLE);
            odo.setCancelQty(Constants.DEFAULT_DOUBLE);
            if (null == odo.getIsWholeOrderOutbound()) {
                odo.setIsWholeOrderOutbound(true);
            }
            if (null == odo.getPriorityLevel()) {
                odo.setPriorityLevel(Constants.ODO_DEFAULT_PRIORITYLEVLE);
            }
            if (null == odo.getIncludeFragileCargo()) {
                odo.setIncludeFragileCargo(false);
            }
            if (null == odo.getIncludeHazardousCargo()) {
                odo.setIncludeHazardousCargo(false);
            }
            if (null == odo.getIsLocked()) {
                odo.setIsLocked(false);
            }
            odo.setCreatedId(userId);
            odo.setCreateTime(new Date());
            odo.setModifiedId(userId);
            odo.setLastModifyTime(new Date());
            if (null == odo.getOrderTime()) {
                odo.setOrderTime(new Date());
            }
            if (null == odo.getQty()) {
                odo.setQty(Constants.DEFAULT_DOUBLE);
            }
            if (null == odo.getSkuNumberOfPackages()) {
                odo.setSkuNumberOfPackages(Constants.DEFAULT_INTEGER);
            }
            if (null == odo.getAmt()) {
                odo.setAmt(Constants.DEFAULT_DOUBLE);
            }
            // TODO yimin.lu
            odo.setOdoStatus(OdoStatus.ODO_TOBECREATED);
            odo.setOuId(ouId);
            // 设置单号和外部对接编码
            String odoCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_INNER, "ODO", null);
            odo.setOdoCode(odoCode);
            if (StringUtils.isEmpty(odo.getExtCode())) {
                String extCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_EXT, null, null);
                odo.setExtCode(extCode);
            }
            transportMgmt.setOuId(ouId);
            this.odoManager.createOdo(odo, transportMgmt);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error(ex + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
    }

    private WhOdoTransportMgmt copyTransportMgmtProperties(OdoTransportMgmtCommand transportMgmtCommand) throws ParseException {
        try {

            if (transportMgmtCommand == null) {
                return null;
            }
            WhOdoTransportMgmt transportMgmt = new WhOdoTransportMgmt();
            BeanUtils.copyProperties(transportMgmtCommand, transportMgmt);
            if (StringUtils.hasText(transportMgmtCommand.getDeliverGoodsTimeStr())) {
                transportMgmt.setDeliverGoodsTime(DateUtils.parseDate(transportMgmtCommand.getDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMDHM));
            }
            if (StringUtils.hasText(transportMgmtCommand.getPlanDeliverGoodsTimeStr())) {
                transportMgmt.setPlanDeliverGoodsTime(DateUtils.parseDate(transportMgmtCommand.getPlanDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMDHM));
            }
            return transportMgmt;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
    }

    private WhOdo copyOdoProperties(OdoCommand odoCommand) {
        try {
            if (odoCommand == null) {
                return null;
            }
            WhOdo odo = new WhOdo();
            // 复制属性
            BeanUtils.copyProperties(odoCommand, odo);
            // 返回
            return odo;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
    }

    @Override
    public WhOdo findOdOById(Long id, Long ouId) {
        return this.odoManager.findOdoByIdOuId(id, ouId);
    }

    @Override
    public WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId) {
        return this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId);
    }

    @Override
    public WhOdoLine findOdoLineById(Long id, Long ouId) {
        return this.OdoLineManager.findOdoLineById(id, ouId);
    }

    @Override
    public void saveOdoUnit(OdoLineCommand lineCommand) {
        Long ouId = lineCommand.getOuId();
        Long userId = lineCommand.getUserId();
        WhOdoLine line = new WhOdoLine();
        WhOdo odo = this.odoManager.findOdoByIdOuId(lineCommand.getOdoId(), ouId);
        if (lineCommand.getLinenum() != null) {
            line.setLinenum(lineCommand.getLinenum());
        }
        if (lineCommand.getExtLinenum() != null) {
            line.setExtLinenum(lineCommand.getExtLinenum());
        }
        line.setSkuId(lineCommand.getSkuId());
        line.setOdoId(lineCommand.getOdoId());
        line.setOuId(ouId);
        line.setSkuBarCode(lineCommand.getSkuBarCode());
        line.setStore(lineCommand.getStore());
        line.setSkuName(lineCommand.getSkuName());
        line.setQty(lineCommand.getQty());
        line.setLinePrice(lineCommand.getLinePrice());
        line.setLineAmt(lineCommand.getLineAmt());
        line.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
        line.setIsCheck(lineCommand.getIsCheck());
        line.setFullLineOutbound(lineCommand.getFullLineOutbound());
        line.setPartOutboundStrategy(lineCommand.getPartOutboundStrategy());
        line.setOutboundCartonType(lineCommand.getOutboundCartonType());
        line.setMixingAttr(lineCommand.getMixingAttr());
        line.setInvStatus(lineCommand.getInvStatus());
        line.setInvType(lineCommand.getInvType());
        if (StringUtils.hasText(lineCommand.getMfgDateStr())) {

            try {
                line.setMfgDate(DateUtils.parseDate(lineCommand.getMfgDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(lineCommand.getExpDateStr())) {

            try {
                line.setExpDate(DateUtils.parseDate(lineCommand.getExpDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(lineCommand.getMinExpDateStr())) {

            try {
                line.setMinExpDate(DateUtils.parseDate(lineCommand.getMinExpDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(lineCommand.getMaxExpDateStr())) {

            try {
                line.setMaxExpDate(DateUtils.parseDate(lineCommand.getMaxExpDateStr(), Constants.DATE_PATTERN_YMD));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        line.setInvAttr1(lineCommand.getInvAttr1());
        line.setInvAttr2(lineCommand.getInvAttr2());
        line.setInvAttr3(lineCommand.getInvAttr3());
        line.setInvAttr4(lineCommand.getInvAttr4());
        line.setInvAttr5(lineCommand.getInvAttr5());
        // 默认值设置
        line.setCurrentQty(Constants.DEFAULT_DOUBLE);
        line.setActualQty(Constants.DEFAULT_DOUBLE);
        line.setCancelQty(Constants.DEFAULT_DOUBLE);
        line.setAssignQty(Constants.DEFAULT_DOUBLE);
        line.setDiekingQty(Constants.DEFAULT_DOUBLE);
        line.setCreateTime(new Date());
        line.setCreatedId(userId);
        line.setLastModifyTime(new Date());
        line.setModifiedId(userId);
        // Odo修改
        if (lineCommand.getIsFragileCargo()) {
            odo.setIncludeFragileCargo(true);
        }
        if (lineCommand.getIsHazardousCargo()) {
            odo.setIncludeHazardousCargo(true);
        }
        odo.setQty(odo.getQty() + line.getQty());
        odo.setAmt(odo.getAmt() + line.getLineAmt());
        int skuCount = this.odoManager.existsSkuInOdo(lineCommand.getOdoId(), lineCommand.getSkuId(), ouId);
        if (skuCount <= 0) {
            odo.setSkuNumberOfPackages(odo.getSkuNumberOfPackages() + 1);
        }
        odo.setModifiedId(userId);
        this.odoManager.saveUnit(line, odo);
    }

}
