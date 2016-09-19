package com.baozun.scm.primservice.whoperation.manager.odo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.baozun.scm.primservice.whoperation.command.odo.OdoAddressCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoTransportMgmtCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoAddressManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoTransportMgmtManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoVasManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;

@Service("odoManagerProxy")
public class OdoManagerProxyImpl extends BaseManagerImpl implements OdoManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(OdoManagerProxy.class);
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private OdoLineManager odoLineManager;
    @Autowired
    private OdoAddressManager odoAddressManager;
    @Autowired
    private OdoTransportMgmtManager odoTransportMgmtManager;
    @Autowired
    private OdoVasManager odoVasManager;

    @Override
    public Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoManager.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public ResponseMsg createOdoFromWms(OdoGroupCommand odoGroup) {
        ResponseMsg msg = new ResponseMsg();
        Long returnOdoId = null;
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
            returnOdoId = odo.getId();
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
        msg.setMsg(returnOdoId + "");
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
        return this.odoLineManager.findOdoLineById(id, ouId);
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
        line.setStoreId(lineCommand.getStoreId());
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

    @Override
    public Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.odoLineManager.findOdoLineListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public WhOdoAddress findOdoAddressByOdoId(Long odoId, Long ouId) {
        return this.odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
    }

    @Override
    public void saveDistributionUnit(OdoAddressCommand odoAddressCommand) {
        Long ouId = odoAddressCommand.getOuId();
        Long userId = odoAddressCommand.getUserId();
        Long odoId = odoAddressCommand.getOdoId();
        try {
            WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
            if (odo == null) {
                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
            }
            /*
             * WhOdoTransportMgmt transportMgmt =
             * this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId); if
             * (transportMgmt == null) { throw new BusinessException(ErrorCodes.NO_ODO_FOUND); }
             */

            /** 以下逻辑判断ODO状态 */
            long lineCount = this.odoLineManager.findOdoLineListCountByOdoId(odoId, ouId);
            if (lineCount > 0) {
                if (OdoStatus.ODO_TOBECREATED.equals(odo.getOdoStatus())) {
                    odo.setOdoStatus(OdoStatus.ODO_NEW);
                    odo.setModifiedId(userId);
                }
            }
            // transportMgmt.setOutboundTargetType(odoAddressCommand.getOutboundTargetType());

            WhOdoAddress odoAddress = this.odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
            if (odoAddress == null) {
                odoAddress = new WhOdoAddress();
                odoAddress.setOdoId(odoId);
                odoAddress.setOuId(ouId);
            }
            odoAddress.setDistributionTargetName(odoAddressCommand.getDistributionTargetName());
            odoAddress.setDistributionTargetMobilePhone(odoAddressCommand.getDistributionTargetMobilePhone());
            if (StringUtils.hasText(odoAddressCommand.getDistributionTargetTelephoneNumber())) {
                String telephone = StringUtils.hasText(odoAddressCommand.getDistributionTargetTelephoneCode()) ? odoAddressCommand.getDistributionTargetTelephoneCode() + "-" : "";
                if (StringUtils.hasText(odoAddressCommand.getDistributionTargetTelephoneDivision())) {
                    telephone += odoAddressCommand.getDistributionTargetTelephoneNumber() + "-" + odoAddressCommand.getDistributionTargetTelephoneDivision();
                } else {
                    telephone += odoAddressCommand.getDistributionTargetTelephoneNumber();
                }
                odoAddress.setDistributionTargetTelephone(telephone);
            }

            odoAddress.setDistributionTargetCountry(odoAddressCommand.getDistributionTargetCountry());
            odoAddress.setDistributionTargetProvince(odoAddressCommand.getDistributionTargetProvince());
            odoAddress.setDistributionTargetCity(odoAddressCommand.getDistributionTargetCity());
            odoAddress.setDistributionTargetDistrict(odoAddressCommand.getDistributionTargetDistrict());
            odoAddress.setDistributionTargetVillagesTowns(odoAddressCommand.getDistributionTargetVillagesTowns());
            odoAddress.setDistributionTargetAddress(odoAddressCommand.getDistributionTargetAddress());
            odoAddress.setDistributionTargetEmail(odoAddressCommand.getDistributionTargetEmail());
            odoAddress.setDistributionTargetZip(odoAddressCommand.getDistributionTargetZip());

            this.odoManager.saveAddressUnit(odoAddress, odo);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }

    }

    @Override
    public WhOdoAddress findOdoAddressById(Long id, Long ouId) {
        return this.odoAddressManager.findOdoAddressByIdOuId(id, ouId);
    }

    @Override
    public void saveConsigneeUnit(OdoAddressCommand odoAddressCommand) {
        Long ouId = odoAddressCommand.getOuId();
        Long userId = odoAddressCommand.getUserId();
        Long odoId = odoAddressCommand.getOdoId();
        try {
            WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
            if (odo == null) {
                throw new BusinessException(ErrorCodes.NO_ODO_FOUND);
            }
            /*
             * WhOdoTransportMgmt transportMgmt =
             * this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odoId, ouId); if
             * (transportMgmt == null) { throw new BusinessException(ErrorCodes.NO_ODO_FOUND); }
             */

            /** 以下逻辑判断ODO状态 */
            long lineCount = this.odoLineManager.findOdoLineListCountByOdoId(odoId, ouId);
            if (lineCount > 0) {
                if (OdoStatus.ODO_TOBECREATED.equals(odo.getOdoStatus())) {
                    odo.setOdoStatus(OdoStatus.ODO_NEW);
                    odo.setModifiedId(userId);
                }
            }
            // transportMgmt.setOutboundTargetType(odoAddressCommand.getOutboundTargetType());

            WhOdoAddress odoAddress = this.odoAddressManager.findOdoAddressByOdoId(odoId, ouId);
            if (odoAddress == null) {
                odoAddress = new WhOdoAddress();
                odoAddress.setOdoId(odoId);
                odoAddress.setOuId(ouId);
            }
            odoAddress.setConsigneeTargetName(odoAddressCommand.getConsigneeTargetName());
            odoAddress.setConsigneeTargetMobilePhone(odoAddressCommand.getConsigneeTargetMobilePhone());
            if (StringUtils.hasText(odoAddressCommand.getConsigneeTargetTelephoneNumber())) {
                String telephone = StringUtils.hasText(odoAddressCommand.getConsigneeTargetTelephoneCode()) ? odoAddressCommand.getConsigneeTargetTelephoneCode() + "-" : "";
                if (StringUtils.hasText(odoAddressCommand.getConsigneeTargetTelephoneDivision())) {
                    telephone += odoAddressCommand.getConsigneeTargetTelephoneNumber() + "-" + odoAddressCommand.getConsigneeTargetTelephoneDivision();
                } else {
                    telephone += odoAddressCommand.getConsigneeTargetTelephoneNumber();
                }
                odoAddress.setConsigneeTargetTelephone(telephone);
            }

            odoAddress.setConsigneeTargetCountry(odoAddressCommand.getConsigneeTargetCountry());
            odoAddress.setConsigneeTargetProvince(odoAddressCommand.getConsigneeTargetProvince());
            odoAddress.setConsigneeTargetCity(odoAddressCommand.getConsigneeTargetCity());
            odoAddress.setConsigneeTargetDistrict(odoAddressCommand.getConsigneeTargetDistrict());
            odoAddress.setConsigneeTargetVillagesTowns(odoAddressCommand.getConsigneeTargetVillagesTowns());
            odoAddress.setConsigneeTargetAddress(odoAddressCommand.getConsigneeTargetAddress());
            odoAddress.setConsigneeTargetEmail(odoAddressCommand.getConsigneeTargetEmail());
            odoAddress.setConsigneeTargetZip(odoAddressCommand.getConsigneeTargetZip());

            this.odoManager.saveAddressUnit(odoAddress, odo);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }

    }

    @Override
    public List<WhOdoVas> findOdoVasByOdoIdOdoLineIdType(Long odoId,Long odoLineId, String vasType, Long ouId) {
        return this.odoVasManager.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, vasType, ouId);
    }

    @Override
    public List<WhOdoVasCommand> findOdoOuVasCommandByOdoIdOdoLineIdType(Long odoId, Long odoLineId, Long ouId) {
        return this.odoVasManager.findOdoOuVasCommandByOdoIdOdoLineIdType(odoId, odoLineId, ouId);
    }

    @Override
    public void saveOdoOuVas(Long odoId, Long odoLineId, Long ouId, List<WhOdoVasCommand> odoVasList, String logId) {
        // 这边的逻辑如下
        // 1.将没有ID的作为插入的数据
        // 2.将有ID的进行校验判断是否修改，并做更新操作
        // 3.将数据库中有但是数据集合中没有的，做删除操作
        Map<Long, WhOdoVasCommand> vasMap = new HashMap<Long, WhOdoVasCommand>();
        List<WhOdoVas> insertVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> updateVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> delVasList = new ArrayList<WhOdoVas>();
        if (odoVasList != null && odoVasList.size() > 0) {
            for (WhOdoVasCommand vc : odoVasList) {
                if (vc.getId() == null) {
                    WhOdoVas ov = new WhOdoVas();
                    BeanUtils.copyProperties(vc, ov);
                    ov.setVasType(Constants.ODO_VAS_TYPE_WH);
                    ov.setOdoId(odoId);
                    ov.setOdoLineId(odoLineId);
                    ov.setOuId(ouId);
                    insertVasList.add(ov);
                } else {
                    vasMap.put(vc.getId(), vc);
                }
            }
        }
        List<WhOdoVas> oldVasList = this.odoVasManager.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, Constants.ODO_VAS_TYPE_WH, ouId);

        if (oldVasList != null && oldVasList.size() > 0) {
            for (WhOdoVas odoVas : oldVasList) {
                if (vasMap.containsKey(odoVas.getId())) {
                    WhOdoVasCommand ovc = vasMap.get(odoVas.getId());
                    odoVas.setPrintTemplet(ovc.getPrintTemplet());
                    odoVas.setSkuBarCode(ovc.getSkuBarCode());
                    odoVas.setContent(ovc.getContent());
                    odoVas.setCartonNo(ovc.getCartonNo());
                    odoVas.setQty(ovc.getQty());
                    updateVasList.add(odoVas);
                } else {
                    delVasList.add(odoVas);
                }
            }
        }
        this.odoVasManager.saveOdoOuVas(insertVasList, updateVasList, delVasList);

    }

    @Override
    public List<WhOdoVasCommand> findOdoExpressVasCommandByOdoIdOdoLineId(Long odoId, Long odoLineId, Long ouId) {
        return this.odoVasManager.findOdoExpressVasCommandByOdoIdOdoLineId(odoId, odoLineId, ouId);
    }

    @Override
    public void saveOdoExpressVas(Long odoId, Long odoLineId, Long ouId, List<WhOdoVasCommand> odoVasList, String logId) {
        // 这边的逻辑如下
        // 1.将没有ID的作为插入的数据
        // 2.将有ID的进行校验判断是否修改，并做更新操作
        // 3.将数据库中有但是数据集合中没有的，做删除操作
        Map<Long, WhOdoVasCommand> vasMap = new HashMap<Long, WhOdoVasCommand>();
        List<WhOdoVas> insertVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> updateVasList = new ArrayList<WhOdoVas>();
        List<WhOdoVas> delVasList = new ArrayList<WhOdoVas>();
        if (odoVasList != null && odoVasList.size() > 0) {
            for (WhOdoVasCommand vc : odoVasList) {
                if (vc.getId() == null) {
                    WhOdoVas ov = new WhOdoVas();
                    BeanUtils.copyProperties(vc, ov);
                    ov.setVasType(Constants.ODO_VAS_TYPE_EXPRESS);
                    ov.setOdoId(odoId);
                    ov.setOdoLineId(odoLineId);
                    ov.setOuId(ouId);
                    insertVasList.add(ov);
                } else {
                    vasMap.put(vc.getId(), vc);
                }
            }
        }
        List<WhOdoVas> oldVasList = this.odoVasManager.findOdoVasByOdoIdOdoLineIdType(odoId, odoLineId, Constants.ODO_VAS_TYPE_EXPRESS, ouId);

        if (oldVasList != null && oldVasList.size() > 0) {
            for (WhOdoVas odoVas : oldVasList) {
                if (vasMap.containsKey(odoVas.getId())) {
                    WhOdoVasCommand ovc = vasMap.get(odoVas.getId());
                    odoVas.setAmt(ovc.getAmt());
                    odoVas.setModeOfPayment(ovc.getModeOfPayment());
                    updateVasList.add(odoVas);
                } else {
                    delVasList.add(odoVas);
                }
            }
        }
        this.odoVasManager.saveOdoOuVas(insertVasList, updateVasList, delVasList);
    }

    @Override
    public void deleteOdo(Long id, Long ouId, String logId) {
        this.odoManager.deleteOdo(id, ouId, logId);
    }

    @Override
    public void deleteLines(OdoLineCommand lineCommand) {
        List<WhOdoLine> lineList = new ArrayList<WhOdoLine>();
        Long ouId = lineCommand.getOuId();
        Long userId = lineCommand.getUserId();
        String logId = lineCommand.getLogId();
        try {
            if (StringUtils.isEmpty(lineCommand.getIds())) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
            }
            String[] idArray = lineCommand.getIds().replace(" ", "").split(",");
            List<Long> idList = new ArrayList<Long>();
            WhOdo odo = null;
            for (String id : idArray) {
                WhOdoLine line = this.odoLineManager.findOdoLineById(Long.parseLong(id), ouId);
                if (line == null) {
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                }
                lineList.add(line);
                if (odo == null) {
                    odo = this.odoManager.findOdoByIdOuId(line.getOdoId(), ouId);
                }
                if (odo == null) {
                    throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
                }
                // 设置出库单数量
                odo.setQty(odo.getQty() - line.getQty());
                // 设置出库单金额
                odo.setAmt(odo.getAmt() - line.getLineAmt());
                idList.add(Long.parseLong(id));
            }
            // #TODO 设置ODO总件数
            Integer skuCount = this.odoManager.getSkuNumberAwayFormSomeLines(idList, ouId);
            // #TODO 是否含有危险品、易碎品



            this.odoLineManager.deleteLines(lineList, ouId, userId, logId);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
    }

}
