package com.baozun.scm.primservice.whoperation.manager.odo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoTransportMgmtCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCondition;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.ReplenishmentTaskStatus;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoAddressManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoTransportMgmtManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoVasManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.DistributionModeArithmeticManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.WaveDistributionModeManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ReplenishmentTaskManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhDistributionPatternRuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttrSn;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.TransportProvider;

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
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhWaveManager waveManager;
    @Autowired
    private WhWaveLineManager waveLineManager;
    @Autowired
    private DistributionModeArithmeticManagerProxy distributionModeArithmeticManagerProxy;
    @Autowired
    private WaveDistributionModeManagerProxy waveDistributionModeManagerProxy;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhDistributionPatternRuleManager whDistributionPatternRuleManager;
    @Autowired
    private WhWorkManager whWorkManager;
    @Autowired
    private WhWorkLineManager whWorkLineManager;
    @Autowired
    private ReplenishmentTaskManager replenishmentTaskManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;

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
             * 第一步：封装ODO和ODOLine
             */
            List<WhOdoLine> odoLineList = null;
            /**
             * 第二步：封装ODO
             */
            WhOdo odo = this.copyOdoProperties(odoCommand);
            /**
             * 第三步：封装出库单运输商表
             */
            WhOdoTransportMgmt transportMgmt = this.copyTransportMgmtProperties(transportMgmtCommand);
            /**
             * 第四步：封装配送对象
             */
            WhOdoAddress address = null;
            /**
             * 第五步：封装增值服务
             */
            List<WhOdoVas> odoVasList = null;
            /**
             * 第六步：封装SN
             */
            List<WhOdoLineAttrSn> lineSnList = null;
            /**
             * 第六步：SN TODO
             */
            this.createOdo(odo, odoLineList, transportMgmt, address, odoVasList, lineSnList, ouId, userId);
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

    private void createOdo(WhOdo odo, List<WhOdoLine> odoLineList, WhOdoTransportMgmt transportMgmt, WhOdoAddress odoAddress, List<WhOdoVas> odoVasList, List<WhOdoLineAttrSn> lineSnList, Long ouId, Long userId) {
        try {
            // 默认属性
            if (odo.getCurrentQty() == null) {
                odo.setCurrentQty(Constants.DEFAULT_DOUBLE);
            }
            if (odo.getActualQty() == null) {
                odo.setActualQty(Constants.DEFAULT_DOUBLE);
            }
            if (odo.getCancelQty() == null) {
                odo.setCancelQty(Constants.DEFAULT_DOUBLE);
            }
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
            if (null == odo.getIsAllowMerge()) {
            	odo.setIsAllowMerge(true);
            }
            if (StringUtils.isEmpty(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.ODO_NEW);
            }
            odo.setOuId(ouId);
            // 设置单号和外部对接编码
            String odoCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_INNER, "ODO", null);
            odo.setOdoCode(odoCode);
            if (StringUtils.isEmpty(odo.getExtCode())) {
                String extCode = codeManager.generateCode(Constants.WMS, Constants.WHODO_MODEL_URL, Constants.WMS_ODO_EXT, null, null);
                odo.setExtCode(extCode);
            }
            // 如果单据为新建状态，则设置技术器编码，并放入到配货模式池中
            if (OdoStatus.ODO_NEW.equals(odo.getOdoStatus())) {
                // 设置计数器编码
                Set<Long> skuIdSet = new HashSet<Long>();
                for (WhOdoLine line : odoLineList) {
                    skuIdSet.add(line.getSkuId());
                }
                String counterCode = this.distributionModeArithmeticManagerProxy.getCounterCodeForOdo(ouId, odo.getSkuNumberOfPackages(), odo.getQty(), skuIdSet);
                odo.setCounterCode(counterCode);
            }

            // 匹配配货模式

            transportMgmt.setOuId(ouId);
            this.odoManager.createOdo(odo, odoLineList, transportMgmt, odoAddress, odoVasList, lineSnList, ouId, userId);
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
                transportMgmt.setDeliverGoodsTime(DateUtils.parseDate(transportMgmtCommand.getDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
            }
            if (StringUtils.hasText(transportMgmtCommand.getPlanDeliverGoodsTimeStr())) {
                transportMgmt.setPlanDeliverGoodsTime(DateUtils.parseDate(transportMgmtCommand.getPlanDeliverGoodsTimeStr(), Constants.DATE_PATTERN_YMD));
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
            odo.setOdoStatus(OdoStatus.ODO_TOBECREATED);
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
        // @mender yimin.lu 201 6/9/28
        line.setPlanQty(lineCommand.getQty());
        line.setLinePrice(lineCommand.getLinePrice());
        line.setLineAmt(lineCommand.getLineAmt());
        line.setOdoLineStatus(OdoStatus.ODO_TOBECREATED);
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

        /**
         *  保存明细的增值服务：
         */
        List<WhOdoVasCommand> odoVasList = lineCommand.getOdoVasList();
        List<WhOdoVas> insertVasList = new ArrayList<WhOdoVas>();
        if (odoVasList != null && odoVasList.size() > 0) {
            if (odoVasList != null && odoVasList.size() > 0) {
                for (WhOdoVasCommand vc : odoVasList) {
                    WhOdoVas ov = new WhOdoVas();
                    BeanUtils.copyProperties(vc, ov);
                    ov.setOdoId(lineCommand.getOdoId());
                    ov.setOuId(ouId);
                    insertVasList.add(ov);
                }
            }
        }


        this.odoManager.saveUnit(line, insertVasList);
    }

    @Override
    public Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoLineCommand> pages = this.odoLineManager.findOdoLineListByQueryMapWithPageExt(page, sorts, params);
        List<OdoLineCommand> odoLineList=pages.getItems();
        if(odoLineList!=null&&odoLineList.size()>0){
            //库存状态
            List<InventoryStatus> invStatusList=this.inventoryStatusManager.findAllInventoryStatus();
            Map<Long, String> invStatusMap = new HashMap<Long, String>();
            // 出库单明细状态
            Set<String> dic1 = new HashSet<String>();
            for(InventoryStatus s:invStatusList){
                invStatusMap.put(s.getId(), s.getName());
            }
            for(OdoLineCommand odo:odoLineList){
                odo.setInvStatusName(invStatusMap.get(odo.getInvStatus()));
                dic1.add(odo.getOdoLineStatus());
            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(Constants.ODO_LINE_STATUS, new ArrayList<String>(dic1));
            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            for (OdoLineCommand odoline : odoLineList) {
                SysDictionary sys = dicMap.get(Constants.ODO_LINE_STATUS + "_" + odoline.getOdoLineStatus());
                odoline.setOdoLineStatusName(sys == null ? odoline.getOdoLineStatus() : sys.getDicLabel());
            }
        }
        return pages;
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
            boolean isAddToCachePool = false;

            /** 以下逻辑判断ODO状态 */
            long lineCount = this.odoLineManager.findOdoLineListCountByOdoId(odoId, ouId);
            if (lineCount > 0) { 
                if (OdoStatus.ODO_TOBECREATED.equals(odo.getOdoStatus())) {
                    odo.setOdoStatus(OdoStatus.ODO_NEW);
                    odo.setModifiedId(userId);
                    // 出库单变成新增的节点，需要将数据插入到订单池
                    isAddToCachePool = true;
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
            if (isAddToCachePool) {
                boolean isExists = this.distributionModeArithmeticManagerProxy.isExistsInOrderPool(odo.getCounterCode(), odoId);
                if (!isExists) {
                    this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(odo.getCounterCode(), odoId);
                }
            }
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
            boolean isAddToCachePool = false;

            /** 以下逻辑判断ODO状态 */
            long lineCount = this.odoLineManager.findOdoLineListCountByOdoId(odoId, ouId);
            if (lineCount > 0) {
                if (OdoStatus.ODO_TOBECREATED.equals(odo.getOdoStatus())) {
                    odo.setOdoStatus(OdoStatus.ODO_NEW);
                    odo.setModifiedId(userId);
                    // 出库单变成新增的节点，需要将数据插入到订单池
                    isAddToCachePool = true;
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
            if (isAddToCachePool) {
                boolean isExists = this.distributionModeArithmeticManagerProxy.isExistsInOrderPool(odo.getCounterCode(), odoId);
                if (!isExists) {
                    this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(odo.getCounterCode(), odoId);
                }
            }
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

    @Override
    public Pagination<OdoWaveGroupResultCommand> findOdoSummaryListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoWaveGroupResultCommand> pages = this.odoManager.findOdoListForWaveByQueryMapWithPageExt(page, sorts, params);
        try {
            Long ouId = (Long) params.get("ouId");

            if (pages != null) {
                Set<String> dic1 = new HashSet<String>();// 出库单状态
                Set<String> dic2 = new HashSet<String>();// 出库单类型
                Set<String> dic3 = new HashSet<String>();// 配货模式
                Set<String> dic4 = new HashSet<String>();// 上位单据类型
                Set<String> transCodeSet = new HashSet<String>();// 运输服务商
                Set<Long> customerIdSet = new HashSet<Long>();
                Set<Long> storeIdSet = new HashSet<Long>();
                Map<String,String> distributionModeMap=new HashMap<String,String>();
                List<OdoWaveGroupResultCommand> list = pages.getItems();
                if (list != null && list.size() > 0) {
                    for (OdoWaveGroupResultCommand command : list) {
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            dic1.add(command.getOdoStatus());
                        }
                        if (StringUtils.hasText(command.getOdoType())) {
                            dic2.add(command.getOdoType());
                        }
                        if (StringUtils.hasText(command.getDistributeMode())) {
                            dic3.add(command.getDistributeMode());
                        }
                        if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                            dic4.add(command.getEpistaticSystemsOrderType());
                        }
                        if (StringUtils.hasText(command.getTransportServiceProvider())) {
                            transCodeSet.add(command.getTransportServiceProvider());
                        }
                        if (command.getCustomerId() != null) {
                            customerIdSet.add(command.getCustomerId());
                        }
                        if (command.getStoreId() != null) {
                            storeIdSet.add(command.getStoreId());
                        }
                    }
                    Map<String, List<String>> map = new HashMap<String, List<String>>();
                    if (dic1.size() > 0) {
                        map.put(Constants.ODO_STATUS, new ArrayList<String>(dic1));
                    }
                    if (dic2.size() > 0) {
                        map.put(Constants.ODO_TYPE, new ArrayList<String>(dic2));
                    }
                    //配货模式
                    if (dic3.size() > 0) {
                        for (String ruleCode : dic3) {
                            
                            WhDistributionPatternRuleCommand rule = this.whDistributionPatternRuleManager.findRuleByCode(ruleCode, ouId);
                            if (rule != null) {
                                distributionModeMap.put(ruleCode, rule.getDistributionPatternName());
                            }
                        }
                       
                    }
                    if (dic4.size() > 0) {
                        map.put(Constants.ODO_PRE_TYPE, new ArrayList<String>(dic4));
                    }
                    Map<String, TransportProvider> transMap = new HashMap<String, TransportProvider>();
                    if (transCodeSet.size() > 0) {
                        for (String transCode : transCodeSet) {
                            TransportProvider tp = this.odoTransportMgmtManager.findByCode(transCode);
                            transMap.put(transCode, tp);
                        }

                    }

                    Map<String, SysDictionary> dicMap = map.size() > 0 ? this.findSysDictionaryByRedis(map) : null;

                    Map<Long, Customer> customerMap = customerIdSet.size() > 0 ? this.findCustomerByRedis(new ArrayList<Long>(customerIdSet)) : null;
                    Map<Long, Store> storeMap = storeIdSet.size() > 0 ? this.findStoreByRedis(new ArrayList<Long>(storeIdSet)) : null;
                    for (OdoWaveGroupResultCommand command : list) {
                        String groupName = "";
                        if (command.getCustomerId() != null) {
                            Customer customer = customerMap.get(command.getCustomerId());
                            command.setCustomerName(customer == null ? command.getCustomerId().toString() : customer.getCustomerName());
                            groupName += "$" + command.getCustomerName();
                        }
                        if (command.getStoreId() != null) {
                            Store store = storeMap.get(command.getStoreId());
                            command.setStoreName(store == null ? command.getStoreId().toString() : store.getStoreName());
                            groupName += "$" + command.getStoreName();
                        }
                        if (StringUtils.hasText(command.getOdoStatus())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_STATUS + "_" + command.getOdoStatus());
                            command.setOdoStatusName(sys.getDicLabel());
                            groupName += "$" + command.getOdoStatusName();
                        }
                        if (StringUtils.hasText(command.getOdoType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_TYPE + "_" + command.getOdoType());
                            command.setOdoTypeName(sys.getDicLabel());
                            groupName += "$" + command.getOdoTypeName();
                        }
                        if (StringUtils.hasText(command.getDistributeMode())) {
                            String name = distributionModeMap.get(command.getDistributeMode());
                            if (StringUtils.hasText(name)) {
                                command.setDistributeModeName(name);
                            } else {
                                command.setDistributeModeName(command.getDistributeMode());
                            }
                            groupName += "$" + command.getDistributeModeName();
                        }
                        if (StringUtils.hasText(command.getTransportServiceProvider())) {
                            if (transMap != null) {
                                if (transMap.containsKey(command.getTransportServiceProvider())) {
                                    TransportProvider tp = transMap.get(command.getTransportServiceProvider());
                                    command.setTransportServiceProviderName(tp.getName());
                                    groupName += "$" + command.getTransportServiceProviderName();
                                }
                            }
                        }
                        if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                            SysDictionary sys = dicMap.get(Constants.ODO_PRE_TYPE + "_" + command.getEpistaticSystemsOrderType());
                            command.setEpistaticSystemsOrderTypeName(sys.getDicLabel());
                            groupName += "$" + command.getEpistaticSystemsOrderTypeName();
                        }
                        command.setGroupName(groupName);
                    }
                    pages.setItems(list);
                }
            }
        } catch (Exception ex) {
            log.error(ex + "");
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        return pages;
    }

    @Override
    public List<OdoResultCommand> findOdoCommandListForWave(OdoSearchCommand command) {
        List<OdoResultCommand> list = this.odoManager.findOdoCommandListForWave(command);
        // 出库单状态
        Set<String> dic1 = new HashSet<String>();// ODO_STATUS
        // 订单平台类型
        Set<String> dic2 = new HashSet<String>();// ODO_ORDER_TYPE
        // 上位单据类型
        Set<String> dic3 = new HashSet<String>();// ODO_PRE_TYPE
        // 出库单类型
        Set<String> dic4 = new HashSet<String>();// ODO_TYPE
        // 业务模式（配货模式）
        Set<String> dic5 = new HashSet<String>();// DISTRIBUTE_MODE
        // 整单出库标志
        Set<String> dic6 = new HashSet<String>();// IS_NOT
        // 越库标志
        Set<String> dic7 = new HashSet<String>();// ODO_CROSS_DOCKING_SYSMBOL
        // 运行标志
        Set<String> dic8 = new HashSet<String>();//
        // 客户
        Set<Long> customerIdSet = new HashSet<Long>();
        // 店铺
        Set<Long> storeIdSet = new HashSet<Long>();
        if (list != null && list.size() > 0) {
            for (OdoResultCommand res : list) {
                if (StringUtils.hasText(res.getOdoStatus())) {
                    dic1.add(res.getOdoStatus());
                }
                if (StringUtils.hasText(res.getOrderType())) {
                    dic2.add(res.getOrderType());
                }
                if (StringUtils.hasText(res.getEpistaticSystemsOrderType())) {
                    dic3.add(res.getEpistaticSystemsOrderType());
                }
                if (StringUtils.hasText(res.getOdoType())) {
                    dic4.add(res.getOdoType());
                }
                if (StringUtils.hasText(res.getDistributeMode())) {
                    dic5.add(res.getDistributeMode());
                }
                if (StringUtils.hasText(res.getIsWholeOrderOutbound())) {
                    dic6.add(res.getIsWholeOrderOutbound());
                }
                if (StringUtils.hasText(res.getCrossDockingSysmbol())) {
                    dic7.add(res.getCrossDockingSysmbol());
                }
                customerIdSet.add(Long.parseLong(res.getCustomerId()));
                storeIdSet.add(Long.parseLong(res.getStoreId()));
            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(Constants.ODO_STATUS, new ArrayList<String>(dic1));
            map.put(Constants.ODO_ORDER_TYPE, new ArrayList<String>(dic2));
            map.put(Constants.ODO_PRE_TYPE, new ArrayList<String>(dic3));
            map.put(Constants.ODO_TYPE, new ArrayList<String>(dic4));
            map.put(Constants.DISTRIBUTE_MODE, new ArrayList<String>(dic5));
            map.put(Constants.IS_WHOLE_ORDER_OUTBOUND, new ArrayList<String>(dic6));
            map.put(Constants.ODO_CROSS_DOCKING_SYSMBOL, new ArrayList<String>(dic7));
            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            Map<Long, Customer> customerMap = this.findCustomerByRedis(new ArrayList<Long>(customerIdSet));
            Map<Long, Store> storeMap = this.findStoreByRedis(new ArrayList<Long>(storeIdSet));
            for (OdoResultCommand result : list) {
                if (StringUtils.hasText(result.getOdoStatus())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_STATUS + "_" + result.getOdoStatus());
                    result.setOdoStatusName(sys == null ? result.getOdoStatus() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getOrderType())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_ORDER_TYPE + "_" + result.getOrderType());
                    result.setOrderTypeName(sys == null ? result.getOrderType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getEpistaticSystemsOrderType())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_PRE_TYPE + "_" + result.getEpistaticSystemsOrderType());
                    result.setEpistaticSystemsOrderTypeName(sys == null ? result.getEpistaticSystemsOrderType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getOdoType())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_TYPE + "_" + result.getOdoType());
                    result.setOdoTypeName(sys == null ? result.getOdoType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getDistributeMode())) {
                    SysDictionary sys = dicMap.get(Constants.DISTRIBUTE_MODE + "_" + result.getDistributeMode());
                    result.setDistributeModeName(sys == null ? result.getDistributeMode() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getIsWholeOrderOutbound())) {
                    SysDictionary sys = dicMap.get(Constants.IS_WHOLE_ORDER_OUTBOUND + "_" + result.getIsWholeOrderOutbound());
                    result.setIsWholeOrderOutboundName(sys == null ? result.getIsWholeOrderOutbound() : sys.getDicLabel());
                }
                if (StringUtils.hasText(result.getCrossDockingSysmbol())) {
                    SysDictionary sys = dicMap.get(Constants.ODO_CROSS_DOCKING_SYSMBOL + "_" + result.getCrossDockingSysmbol());
                    result.setCrossDockingSysmbolName(sys == null ? result.getCrossDockingSysmbol() : sys.getDicLabel());
                }
                Customer customer = customerMap.get(Long.parseLong(result.getCustomerId()));
                result.setCustomerName(customer == null ? result.getCustomerId() : customer.getCustomerName());
                Store store = storeMap.get(Long.parseLong(result.getStoreId()));
                result.setStoreName(store == null ? result.getStoreId() : store.getStoreName());
            }
        }

        return list;
    }

    @Override
    public OdoWaveGroupResultCommand findOdoSummaryForWave(OdoWaveGroupSearchCommand command) {
        return this.odoManager.findOdoSummaryForWave(command);
    }

    @Override
    public String createOdoWave(OdoGroupSearchCommand command) {
        /**
         * 校验阶段
         */
        /**
         * 校验出库单头和明细状态；以及是否处于别的波次中
         */
        String logId = command.getLogId();
        Long ouId = command.getOuId();
        Long userId = command.getUserId();
        Map<Long, WhOdo> odoMap = new HashMap<Long, WhOdo>();

        Map<Long, WhOdoTransportMgmt> transMap = new HashMap<Long, WhOdoTransportMgmt>();
        List<WhOdoLine> odolineList = new ArrayList<WhOdoLine>();
        Long waveMasterId = command.getWaveMasterId();// 波次主档信息
        WhWaveMaster master = this.odoManager.findWaveMasterByIdouId(waveMasterId, ouId);
        if (master == null) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        // 全选
        if (command.getConditionList() != null && command.getConditionList().size() > 0) {
            for (OdoWaveGroupSearchCondition gsc : command.getConditionList()) {
                OdoSearchCommand search = new OdoSearchCommand();
                BeanUtils.copyProperties(command, search);
                search.setGroupCustomerId(gsc.getCustomerId());
                search.setGroupOdoStatus(gsc.getOdoStatus());
                search.setGroupStoreId(gsc.getStoreId());
                search.setGroupOdoType(gsc.getOdoType());
                search.setGroupDistributeMode(gsc.getDistributeMode());
                search.setGroupEpistaticSystemsOrderType(gsc.getEpistaticSystemsOrderType());
                search.setGroupTransportServiceProvider(gsc.getTransportServiceProvider());
                search.setIsEpistaticSystemsOrderType(gsc.getIsEpistaticSystemsOrderType());
                search.setIsDistributeMode(gsc.getIsDistributeMode());
                search.setLineFlag(true);
                if (StringUtils.hasText(command.getOdoStatus())) {
                    search.setOdoStatus(Arrays.asList(command.getOdoStatus().split(",")));
                }
                if (StringUtils.hasText(command.getEpistaticSystemsOrderType())) {
                    search.setEpistaticSystemsOrderType(Arrays.asList(command.getEpistaticSystemsOrderType().split(",")));
                }
                if (StringUtils.hasText(command.getCustomerId())) {
                    search.setCustomerId(Arrays.asList(command.getCustomerId().split(",")));
                }
                if (StringUtils.hasText(command.getOutboundTargetType())) {
                    search.setOutboundTargetType(Arrays.asList(command.getOutboundTargetType().split(",")));
                }
                if (StringUtils.hasText(command.getOdoType())) {
                    search.setOdoType(Arrays.asList(command.getOdoType().split(",")));
                }
                if (StringUtils.hasText(command.getStoreId())) {
                    search.setStoreId(Arrays.asList(command.getStoreId().split(",")));
                }
                if (StringUtils.hasText(command.getModeOfTransport())) {
                    search.setModeOfTransport(Arrays.asList(command.getModeOfTransport().split(",")));
                }
                if (StringUtils.hasText(command.getTransportServiceProvider())) {
                    String[] arr = command.getTransportServiceProvider().split(",");
                    search.setTransportServiceProvider(Arrays.asList(arr));
                }
                if (StringUtils.hasText(command.getTransportServiceProviderType())) {
                    search.setTransportServiceProviderType(Arrays.asList(command.getTransportServiceProviderType().split(",")));
                }
                if (StringUtils.hasText(command.getDistributeMode())) {
                    search.setDistributeMode(Arrays.asList(command.getDistributeMode().split(",")));
                }
                if (StringUtils.hasText(command.getOutBoundCartonType())) {
                    search.setOutBoundCartonType(Arrays.asList(command.getOutBoundCartonType().split(",")));
                }
                if (StringUtils.hasText(command.getLineOutboundCartonType())) {
                    search.setLineOutboundCartonType(Arrays.asList(command.getLineOutboundCartonType().split(",")));
                }
                if (StringUtils.hasText(command.getInvType())) {
                    search.setInvType(Arrays.asList(command.getInvType().split(",")));
                }
                if (StringUtils.hasText(command.getInvStatus())) {
                    search.setInvStatus(Arrays.asList(command.getInvStatus().split(",")));
                }
                if (StringUtils.hasText(command.getInvAttr1())) {
                    search.setInvAttr1(Arrays.asList(command.getInvAttr1().split(",")));
                }
                if (StringUtils.hasText(command.getInvAttr2())) {
                    search.setInvAttr2(Arrays.asList(command.getInvAttr2().split(",")));
                }
                if (StringUtils.hasText(command.getInvAttr3())) {
                    search.setInvAttr3(Arrays.asList(command.getInvAttr3().split(",")));
                }
                if (StringUtils.hasText(command.getInvAttr4())) {
                    search.setInvAttr4(Arrays.asList(command.getInvAttr4().split(",")));
                }
                if (StringUtils.hasText(command.getInvAttr5())) {
                    search.setInvAttr5(Arrays.asList(command.getInvAttr5().split(",")));
                }
                if (StringUtils.hasText(command.getWhVasType())) {
                    search.setWhVasType(Arrays.asList(command.getWhVasType().split(",")));
                }
                if (StringUtils.hasText(command.getOrderType())) {
                    search.setOrderType(Arrays.asList(command.getOrderType().split(",")));
                }
                if (StringUtils.hasText(command.getDeliverGoodsTimeMode())) {
                    search.setDeliverGoodsTimeMode(Arrays.asList(command.getDeliverGoodsTimeMode().split(",")));
                }
                if (StringUtils.hasText(command.getOdoLineStatus())) {
                    search.setOdoLineStatus(Arrays.asList(command.getOdoLineStatus().split(",")));
                }
                if (StringUtils.hasText(command.getLineOutboundCartonType())) {
                    search.setLineOutboundCartonType(Arrays.asList(command.getLineOutboundCartonType().split(",")));
                }
                // 如果不选分组 默认按照客户分组
                // 如果没有选出库单状态，则默认为：新建和部分出库
                if (search.getOdoStatus() == null || search.getOdoStatus().size() == 0) {
                    search.setOdoStatus(Arrays.asList(new String[] {OdoStatus.ODO_NEW, OdoStatus.ODO_OUTSTOCK}));
                }
                // 如果没有选出库单明细状态，则默认为新建和部分出库
                if (search.getOdoLineStatus() == null || search.getOdoLineStatus().size() == 0) {
                    search.setOdoLineStatus(Arrays.asList(new String[] {OdoStatus.ODOLINE_NEW, OdoStatus.ODOLINE_OUTSTOCK}));
                }
                List<OdoResultCommand> liOdoList = this.odoManager.findOdoCommandListForWave(search);
                if(liOdoList!=null&&liOdoList.size()>0){
                    for (OdoResultCommand odoBk : liOdoList) {
                        WhOdo odo = this.odoManager.findOdoByIdOuId(odoBk.getId(), ouId);
                        if (OdoStatus.ODO_NEW.equals(odo.getOdoStatus()) || OdoStatus.ODO_OUTSTOCK.equals(odo.getOdoStatus())) {
                            if (StringUtils.hasText(odo.getWaveCode())) {
                                throw new BusinessException(odo.getExtCode() + "已处于别的波次[波次编号：" + odo.getWaveCode() + "]中");
                            }

                            List<WhOdoLine> lineList = this.odoLineManager.findOdoLineListByOdoId(odo.getId(), odo.getOuId());
                            // 整单出库逻辑
                            if (odo.getIsWholeOrderOutbound()) {
                                // boolean isWholeOrderOutboundFlag =
                                // StringUtils.isEmpty(lineList.get(0).getWaveCode()) ? true :
                                // false;
                                for (WhOdoLine line : lineList) {
                                    odolineList.add(line);
                                }
                                // 部分出库逻辑
                            } else {
                                for (WhOdoLine line : lineList) {
                                    if (StringUtils.isEmpty(line.getWaveCode())) {
                                        odolineList.add(line);
                                    }
                                }
                            }
                            odoMap.put(odo.getId(), odo);
                            WhOdoTransportMgmt trans = this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odo.getId(), ouId);
                            transMap.put(odo.getId(), trans);
                        }
                    }
                }
            }
        }
        // 部分点选
        if (command.getOdoIdList() != null && command.getOdoIdList().size() > 0) {
            for (Long odoId : command.getOdoIdList()) {
                WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                if (odo == null) {
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                }
                if (OdoStatus.ODO_NEW.equals(odo.getOdoStatus()) || OdoStatus.ODO_OUTSTOCK.equals(odo.getOdoStatus())) {

                    if (StringUtils.hasText(odo.getWaveCode())) {
                        throw new BusinessException(odo.getExtCode() + "已处于别的波次[波次编号：" + odo.getWaveCode() + "]中");
                    }

                    List<WhOdoLine> lineList = this.odoLineManager.findOdoLineListByOdoId(odo.getId(), odo.getOuId());
                    // 整单出库逻辑
                    if (odo.getIsWholeOrderOutbound()) {
                        // boolean isWholeOrderOutboundFlag =
                        // StringUtils.isEmpty(lineList.get(0).getWaveCode()) ? true : false;
                        for (WhOdoLine line : lineList) {
                            odolineList.add(line);
                        }
                        // 部分出库逻辑
                    } else {
                        for (WhOdoLine line : lineList) {
                            if (StringUtils.isEmpty(line.getWaveCode())) {
                                odolineList.add(line);
                            }
                        }
                    }
                    odoMap.put(odoId, odo);
                    WhOdoTransportMgmt trans = this.odoTransportMgmtManager.findTransportMgmtByOdoIdOuId(odo.getId(), ouId);
                    transMap.put(odo.getId(), trans);
                }
            }
        }


        /**
         * 校验波次主档信息
         */
        int odoCount = odoMap.size();// 波次出库单总单数
        int odolineCount = odolineList.size();// 波次明细数

        Map<Long, Double> skuMap = new HashMap<Long, Double>();// 商品总件数
        double totalAmt = Constants.DEFAULT_DOUBLE;// 总金额
        double totalSkuQty = Constants.DEFAULT_DOUBLE;// 商品总件数
        for (WhOdoLine line : odolineList) {
            totalAmt += line.getPlanQty() * line.getLinePrice();
            if (skuMap.containsKey(line.getSkuId())) {
                skuMap.put(line.getSkuId(), skuMap.get(line.getSkuId()) + line.getPlanQty());
            } else {

                skuMap.put(line.getSkuId(), line.getPlanQty());
            }
            totalSkuQty += line.getPlanQty();
        }
        // 商品种类数
        int skuCategoryQty = skuMap.size();
        // 总体积
        double totalVolume = Constants.DEFAULT_DOUBLE;
        // 总重量
        double totalWeight = Constants.DEFAULT_DOUBLE;

        // 体积单位转换率
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds = this.odoManager.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        // 重量单位转换率
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> weightUomCmds = this.odoManager.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }

        Iterator<Entry<Long, Double>> skuIt = skuMap.entrySet().iterator();
        while (skuIt.hasNext()) {
            Entry<Long, Double> entry = skuIt.next();
            Sku sku = this.odoManager.findSkuByIdToShard(entry.getKey(), ouId);
            if (sku != null) {
                totalVolume += sku.getVolume() * entry.getValue() * (StringUtils.isEmpty(sku.getVolumeUom()) ? 1 : lenUomConversionRate.get(sku.getVolumeUom()));
                totalWeight = sku.getWeight() * entry.getValue() * (StringUtils.isEmpty(sku.getWeightUom()) ? 1 : weightUomConversionRate.get(sku.getWeightUom()));
            }
        }

        if (master.getMinOdoQty() != null) {
            if (master.getMinOdoQty() > odoCount) {
                throw new BusinessException("出库单数目不满足波次最小出库单数");
            }
        }
        if (master.getMaxOdoQty() != null) {
            if (master.getMaxOdoQty() < odoCount) {
                throw new BusinessException("出库单数不满足波次最大出库单数");
            }
        }
        if (master.getMaxOdoLineQty() != null) {
            if (master.getMaxOdoLineQty() < odolineCount) {
                throw new BusinessException("出库单明细数不满足波次最大出库明细数");
            }
        }
        if (master.getMaxSkuQty() != null) {
            if (master.getMaxSkuQty() < totalSkuQty) {
                throw new BusinessException("商品数不满足波次最大出库商品数");
            }
        }
        if (master.getMaxSkuCategoryQty() != null) {
            if (master.getMaxSkuCategoryQty() < skuCategoryQty) {
                throw new BusinessException("商品种类数不满足波次最大出库商品种类数");
            }
        }
        if (master.getMaxVolume() != null) {
            if (master.getMaxVolume() < totalVolume) {
                throw new BusinessException("体积不满足波次最大出库体积");
            }
        }
        if (master.getMaxWeight() != null) {
            if (master.getMaxWeight() < totalWeight) {
                throw new BusinessException("重量不满足波次最大出库重量");
            }
        }
        /**
         * 创建波次头
         */
        WhWave wave = new WhWave();
        // a 生成波次编码，校验唯一性；补偿措施
        // #TODO 校验波次号
        String waveCode = "";
        try {
            waveCode = codeManager.generateCode(Constants.WMS, Constants.WHWAVE_MODEL_URL, "", "WAVE", null);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.CODE_MANAGER_ERROR);
        }
        if (StringUtils.isEmpty(waveCode)) {
            throw new BusinessException(ErrorCodes.CODE_MANAGER_ERROR);
        }
        wave.setCode(waveCode);
        wave.setStatus(WaveStatus.WAVE_NEW);
        wave.setOuId(ouId);
        wave.setWaveMasterId(waveMasterId);
        wave.setTotalOdoQty(odoCount);
        wave.setTotalOdoLineQty(odolineCount);
        wave.setTotalAmount(totalAmt);
        wave.setTotalVolume(totalVolume);
        wave.setTotalWeight(totalWeight);
        wave.setTotalSkuQty(totalSkuQty);
        wave.setSkuCategoryQty(skuCategoryQty);
        wave.setIsRunWave(false);
        wave.setCreatedId(userId);
        wave.setCreateTime(new Date());
        wave.setModifiedId(userId);
        wave.setLastModifyTime(new Date());
        wave.setIsError(false);
        wave.setLifecycle(Constants.LIFECYCLE_START);

        List<WhWaveLine> waveLineList = new ArrayList<WhWaveLine>();
        for (WhOdoLine line : odolineList) {
            WhOdo odo = odoMap.get(line.getOdoId());
            WhOdoTransportMgmt trans = transMap.get(line.getOdoId());
            transMap.put(odo.getId(), trans);
            WhWaveLine waveLine = new WhWaveLine();
            waveLine.setOdoLineId(line.getId());
            waveLine.setOdoId(line.getOdoId());
            waveLine.setOdoCode(odo.getOdoCode());
            waveLine.setOdoPriorityLevel(odo.getPriorityLevel());
            waveLine.setOdoPlanDeliverGoodsTime(trans.getPlanDeliverGoodsTime());
            waveLine.setOdoOrderTime(odo.getOrderTime());
            waveLine.setIsStaticLocationAllocate(false);
            waveLine.setLinenum(line.getLinenum());
            waveLine.setStoreId(odo.getStoreId());
            waveLine.setExtLinenum(line.getExtLinenum());
            waveLine.setSkuId(line.getSkuId());
            waveLine.setSkuBarCode(line.getSkuBarCode());
            waveLine.setSkuName(line.getSkuName());
            waveLine.setQty(line.getPlanQty());
            waveLine.setAllocateQty(line.getAssignQty());
            waveLine.setIsWholeOrderOutbound(odo.getIsWholeOrderOutbound());
            waveLine.setFullLineOutbound(line.getFullLineOutbound());
            waveLine.setMfgDate(line.getMfgDate());
            waveLine.setExpDate(line.getExpDate());
            waveLine.setMinExpDate(line.getMinExpDate());
            waveLine.setMaxExpDate(line.getMaxExpDate());
            waveLine.setBatchNumber(line.getBatchNumber());
            waveLine.setCountryOfOrigin(line.getCountryOfOrigin());
            waveLine.setInvStatus(line.getInvStatus());
            waveLine.setInvType(line.getInvType());
            waveLine.setInvAttr1(line.getInvAttr1());
            waveLine.setInvAttr2(line.getInvAttr2());
            waveLine.setInvAttr3(line.getInvAttr3());
            waveLine.setInvAttr4(line.getInvAttr4());
            waveLine.setInvAttr5(line.getInvAttr5());
            waveLine.setOutboundCartonType(line.getOutboundCartonType());
            waveLine.setColor(line.getColor());
            waveLine.setStyle(line.getStyle());
            waveLine.setSize(line.getSize());
            waveLine.setOuId(ouId);
            waveLine.setCreateTime(new Date());
            waveLine.setCreatedId(userId);
            waveLine.setLastModifyTime(new Date());
            waveLine.setModifiedId(userId);
            waveLineList.add(waveLine);
        }
        this.odoManager.createOdoWave(wave, master.getWaveTemplateId(), waveLineList, odoMap, odolineList, userId, logId);
        return waveCode;
    }

    @Override
    public Pagination<WaveCommand> findWaveListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.waveManager.findWaveListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public void deleteWave(WaveCommand waveCommand) {
        Long ouId=waveCommand.getOuId();
        Long userId=waveCommand.getUserId();
        Long waveId=waveCommand.getId();
        //查询波次
        WhWave wave=this.waveManager.getWaveByIdAndOuId(waveId,ouId);
        List<WhWaveLine> waveLineList=this.waveLineManager.findWaveLineListByWaveId(waveId,ouId);
        //查询波次关联的出库单
        List<WhOdo> odoList = this.odoManager.findOdoListByWaveCode(wave.getCode(), ouId);
        List<WhOdoLine> odoLineList = this.odoLineManager.findOdoLineListByWaveCode(wave.getCode(), ouId);

        this.waveManager.deleteWave(wave, waveLineList, odoList, odoLineList, userId);

    }

    @Override
    public void finishCreateOdo(OdoCommand odoCommand) {
        Long odoId = odoCommand.getId();
        Long ouId = odoCommand.getOuId();
        Long userId=odoCommand.getUserId();
        String logId = odoCommand.getLogId();
        List<WhOdoLine> lineList = this.odoLineManager.findOdoLineListByOdoId(odoId, ouId);
        List<WhOdoLine> saveLineList = new ArrayList<WhOdoLine>();// 用于保存的明细行
        if (lineList != null && lineList.size() > 0) {
            WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
            // 出库单统计数目
            double qty = Constants.DEFAULT_DOUBLE;
            int skuNumberOfPackages = Constants.DEFAULT_INTEGER;
            double amt = Constants.DEFAULT_DOUBLE;
            boolean isHazardous = odo.getIncludeHazardousCargo();
            boolean isFragile = odo.getIncludeFragileCargo();
            Set<Long> skuIdSet = new HashSet<Long>();
            for (WhOdoLine line : lineList) {
                if (OdoStatus.ODOLINE_TOBECREATED.equals(line.getOdoLineStatus())) {
                    SkuRedisCommand skuMaster = skuRedisManager.findSkuMasterBySkuId(line.getSkuId(), ouId, logId);
                    SkuMgmt skuMgmt = skuMaster.getSkuMgmt();
                    if (!isHazardous && skuMgmt.getIsHazardousCargo()) {
                        isHazardous = true;
                    }
                    if (!isFragile && skuMgmt.getIsFragileCargo()) {
                        isFragile = true;
                    }

                    line.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                    line.setModifiedId(userId);
                    saveLineList.add(line);
                }
                skuIdSet.add(line.getSkuId());
                amt += line.getLineAmt();
                qty += line.getQty();
            }
            odo.setQty(qty);
            odo.setAmt(amt);
            skuNumberOfPackages = skuIdSet.size();
            odo.setSkuNumberOfPackages(skuNumberOfPackages);
            odo.setIncludeFragileCargo(isFragile);
            odo.setIncludeHazardousCargo(isHazardous);
            if (OdoStatus.ODO_TOBECREATED.equals(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.ODO_NEW);
            }
            List<WhOdoVasCommand> vasList = this.odoVasManager.findOdoOuVasCommandByOdoIdOdoLineIdType(odo.getId(), null, ouId);
            // 设置允许合并与否
            if (vasList == null || vasList.size() == 0) {
                odo.setIsAllowMerge(true);
            } else {
                odo.setIsAllowMerge(false);
            }
            // #TODO 现在出库单暂时不支持编辑
            String counterCode = this.distributionModeArithmeticManagerProxy.getCounterCodeForOdo(ouId, skuNumberOfPackages, qty, skuIdSet);
            odo.setCounterCode(counterCode);
            odo.setModifiedId(userId);
            boolean flag = false;
            try {
                this.odoManager.finishCreateOdo(odo, saveLineList);
                flag = true;
            } catch (Exception e) {
                throw e;
            }
            if (flag) {
                this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(counterCode, odoId);
            }
        }

    }

    @Override
    public List<String> findExportExeclList(OdoSearchCommand odoSearchCommand) {
        return this.odoManager.findExportExeclList(odoSearchCommand);
    }

    @Override
    public WhWave getWaveByIdAndOuId(Long id, Long ouId) {
        return this.waveManager.findWaveByIdOuId(id, ouId);
    }

    @Override
    public Pagination<WaveLineCommand> findWaveLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<WaveLineCommand> pages = this.waveLineManager.findWaveLineListByQueryMapWithPageExt(page, sorts, params);
        List<WaveLineCommand> waveLineList=pages.getItems();
        if(waveLineList!=null&&waveLineList.size()>0){
            //库存状态
            List<InventoryStatus> invStatusList=this.inventoryStatusManager.findAllInventoryStatus();
            Map<Long, String> invStatusMap = new HashMap<Long, String>();
            for(InventoryStatus s:invStatusList){
                invStatusMap.put(s.getId(), s.getName());
            }
            for(WaveLineCommand wave:waveLineList){
                wave.setInvStatusName(invStatusMap.get(wave.getInvStatus()));
            }
        }
        pages.setItems(waveLineList);
        return pages;
    }

    @Override
    public void divFromWaveByOdo(WaveLineCommand waveLineCommand) {
        String logId = "";
        List<Long> odoIdList = waveLineCommand.getOdoIds();
        if (odoIdList == null || odoIdList.size() == 0) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        Long ouId = waveLineCommand.getOdoId();
        Long userId = waveLineCommand.getUserId();
        Long waveId = waveLineCommand.getWaveId();

        WhWave wave = this.waveManager.findWaveByIdOuId(waveId, ouId);

        List<WhWaveLine> waveLineAll = this.waveLineManager.findWaveLineListByWaveId(waveId, ouId);

        Map<Long, List<WhWaveLine>> odoIdWaveLineAllMap = new HashMap<Long, List<WhWaveLine>>();

        for (WhWaveLine waveLine : waveLineAll) {
            if (odoIdWaveLineAllMap.containsKey(waveLine.getOdoId())) {
                odoIdWaveLineAllMap.get(waveLine.getId()).add(waveLine);
            } else {
                List<WhWaveLine> waveLineList = new ArrayList<WhWaveLine>();
                waveLineList.add(waveLine);
                odoIdWaveLineAllMap.put(waveLine.getId(), waveLineList);
            }
        }



        Map<Long, List<WhWaveLine>> odoIdWaveLineMap = new HashMap<Long, List<WhWaveLine>>();

        WhWaveLine lineSearch = new WhWaveLine();
        lineSearch.setWaveId(waveId);
        lineSearch.setOuId(ouId);
        for (Long odoId : odoIdList) {
            lineSearch.setOdoId(odoId);
            List<WhWaveLine> waveLineList = this.waveLineManager.getWaveLineByParam(lineSearch);
            if (waveLineList == null || waveLineList.size() == 0) {
                continue;
            }
            odoIdWaveLineAllMap.remove(odoId);
            odoIdWaveLineMap.put(odoId, waveLineList);
        }

        if (odoIdWaveLineAllMap.size() == 0) {

            // return;
        }

        statisticsForWave(wave, odoIdWaveLineAllMap);

        this.waveManager.divFromWaveByOdo(wave, odoIdWaveLineMap, ouId, userId, logId);

    }


    private void statisticsForWave(WhWave wave, Map<Long, List<WhWaveLine>> odoIdWaveLineAllMap) {
        Long ouId = wave.getOuId();
        List<WhOdoLine> odolineList = new ArrayList<WhOdoLine>();
        Iterator<Entry<Long, List<WhWaveLine>>> it = odoIdWaveLineAllMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, List<WhWaveLine>> entry = it.next();
            for (WhWaveLine line : entry.getValue()) {
                WhOdoLine odoLine = this.odoLineManager.findOdoLineById(line.getOdoLineId(), ouId);
                odolineList.add(odoLine);
            }
        }

        int odoCount = odoIdWaveLineAllMap.size();// 波次出库单总单数
        int odolineCount = odolineList.size();// 波次明细数

        Map<Long, Double> skuMap = new HashMap<Long, Double>();// 商品总件数
        double totalAmt = Constants.DEFAULT_DOUBLE;// 总金额
        double totalSkuQty = Constants.DEFAULT_DOUBLE;// 商品总件数
        for (WhOdoLine line : odolineList) {
            totalAmt += line.getPlanQty() * line.getLinePrice();
            if (skuMap.containsKey(line.getSkuId())) {
                skuMap.put(line.getSkuId(), skuMap.get(line.getSkuId()) + line.getPlanQty());
            } else {

                skuMap.put(line.getSkuId(), line.getPlanQty());
            }
            totalSkuQty += line.getPlanQty();
        }
        // 商品种类数
        int skuCategoryQty = skuMap.size();
        // 总体积
        double totalVolume = Constants.DEFAULT_DOUBLE;
        // 总重量
        double totalWeight = Constants.DEFAULT_DOUBLE;

        // 体积单位转换率
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds = this.odoManager.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        // 重量单位转换率
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> weightUomCmds = this.odoManager.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }

        Iterator<Entry<Long, Double>> skuIt = skuMap.entrySet().iterator();
        while (skuIt.hasNext()) {
            Entry<Long, Double> entry = skuIt.next();
            Sku sku = this.odoManager.findSkuByIdToShard(entry.getKey(), wave.getOuId());
            if (sku != null) {
                totalVolume += sku.getVolume() * entry.getValue() * (StringUtils.isEmpty(sku.getVolumeUom()) ? 1 : lenUomConversionRate.get(sku.getVolumeUom()));
                totalWeight = sku.getWeight() * entry.getValue() * (StringUtils.isEmpty(sku.getWeightUom()) ? 1 : weightUomConversionRate.get(sku.getWeightUom()));
            }
        }

        wave.setTotalOdoQty(odoCount);
        wave.setTotalOdoLineQty(odolineCount);
        wave.setTotalAmount(totalAmt);
        wave.setTotalVolume(totalVolume);
        wave.setTotalWeight(totalWeight);
        wave.setTotalSkuQty(totalSkuQty);
        wave.setSkuCategoryQty(skuCategoryQty);
    }

    @Override
    public void releaseWave(WaveCommand waveCommand) {
        Long waveId = waveCommand.getId();
        Long ouId = waveCommand.getOuId();
        Long userId = waveCommand.getUserId();
        WhWave wave = this.waveManager.findWaveByIdOuId(waveId, ouId);
        wave.setStatus(WaveStatus.WAVE_RELEASE);
        wave.setModifiedId(userId);

        List<WhWork> workList = this.whWorkManager.findWorkByWaveWithLock(wave.getCode(), ouId);

        this.waveManager.releaseWave(wave, workList, ouId, userId);



    }

    @Override
    public void cancelWave(WaveCommand waveCommand) {
        // yimin.lu 2016/12/16
        // 取消逻辑：
        // 新建状态下取消：-》取消
        // 完成状态下取消->取消处理中-》取消
        Long waveId = waveCommand.getId();
        Long ouId = waveCommand.getOuId();
        Long userId = waveCommand.getUserId();
        WhWave wave = this.waveManager.findWaveByIdOuId(waveId, ouId);

        List<WhOdo> odoList = this.odoManager.findOdoListByWaveCode(wave.getCode(), ouId);

        // 新建状态下的波次
        if (WaveStatus.WAVE_NEW == wave.getStatus()) {
            wave.setModifiedId(userId);
            wave.setStatus(WaveStatus.WAVE_CANCEL);
            this.waveManager.cancelWaveForNew(wave, odoList, ouId, userId);
            return;
        }
        // 完成状态下的波次
        // 取消所有的补货任务
        // 取消所有的未执行的补货工作和拣货工作
        // 如果有正在执行的工作，波次状态置为取消处理中；否则为取消
        // 库存的回滚
        Set<Long> odoToLazyFreeSet = new HashSet<Long>();// 需要延后取消的出库单
        Map<String,Long> odoCodeIdMap=new HashMap<String,Long>();
        for(WhOdo odo:odoList){
            odoCodeIdMap.put(odo.getOdoCode(), odo.getId());
        }
        // 补货任务
        ReplenishmentTask task = this.replenishmentTaskManager.findTaskByWaveWithStatus(waveId, ouId, ReplenishmentTaskStatus.REPLENISHMENT_TASK_NEW);

        List<WhWork> workList = this.whWorkManager.findWorkByWave(wave.getCode(), ouId);
        if (workList == null || workList.size() == 0) {

        }
        // Map<Long,List<WhWorkLine>> workToCancelMap = new HashMap<Long,List<WhWorkLine>>();
        Set<Long> workToLazyCancelSet = new HashSet<Long>();
        for (WhWork work : workList) {
            if (WorkStatus.NEW.intValue() == work.getStatus().intValue()) {
                // workToCancelMap.put(work.getId(),workLineList);
            } else {
                workToLazyCancelSet.add(work.getId());
                // List<WhWorkLine> workLineList =
                // this.whWorkLineManager.findListByWorkId(work.getId(), ouId);
                if(odoCodeIdMap.containsKey(work.getOrderCode())){
                    Long l=odoCodeIdMap.get(work.getOrderCode());
                    odoToLazyFreeSet.add(l);
                }
            }
        }
        
        this.waveManager.cancelWaveWithWork(wave, task, workList, workToLazyCancelSet, odoList, odoToLazyFreeSet, userId);
        // this.waveManager.cancelWaveWithLazy(wave)
    }

    @Override
    public void runWave(WaveCommand waveCommand) {
        WhWave wave = this.waveManager.findWaveByIdOuId(waveCommand.getId(), waveCommand.getOuId());
        if (wave.getIsRunWave()) {
            throw new BusinessException("波次已在运行中");
        }
        if (WaveStatus.WAVE_NEW != wave.getStatus()) {
            throw new BusinessException("波次状态非新建");
        }
        wave.setModifiedId(waveCommand.getUserId());
        this.waveManager.startWave(wave);
    }
}
