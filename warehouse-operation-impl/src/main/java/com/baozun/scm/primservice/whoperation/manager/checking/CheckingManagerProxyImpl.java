/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.checking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhLocationSkuVolumeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineSnCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingPrint;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.OutboundboxStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.OdoManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ContainerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionOutBoundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhLocationSkuVolumeManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOdoPackageInfoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxLineSnManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhPrintInfoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventorySnManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.WarehouseMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundConsumable;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("checkingManagerProxy")
public class CheckingManagerProxyImpl extends BaseManagerImpl implements CheckingManagerProxy {

    public static final Logger log = LoggerFactory.getLogger(CheckingManagerProxyImpl.class);

    @Autowired
    private CheckingManager checkingManager;
    @Autowired
    private WhFunctionOutBoundManager whFunctionOutBoundManager;
    @Autowired
    private WhPrintInfoManager whPrintInfoManager;
    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;
    @Autowired
    private WhCheckingManager whCheckingManager;
    @Autowired
    private WhCheckingLineManager whCheckingLineManager;
    @Autowired
    private WhOdoPackageInfoManager whOdoPackageInfoManager;
    @Autowired
    private WhOutboundboxManager whOutboundboxManager;
    @Autowired
    private WhOutboundboxLineManager whOutboundboxLineManager;
    @Autowired
    private WhOutboundboxLineSnManager whOutboundboxLineSnManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhSkuInventorySnManager whSkuInventorySnManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private WhSkuManager whSkuManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private OdoManagerProxy odoManagerProxy;
    @Autowired
    private WhLocationSkuVolumeManager whLocationSkuVolumeManager;
    @Autowired
    private ContainerManager containerManager;
    @Autowired
    private WarehouseManager warehouseManager;


    /**
     * 根据复核打印配置打印单据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean printDefect(WhCheckingResultCommand whCheckingResultCommand) {
        Boolean isSuccess = true;
        Long ouId = whCheckingResultCommand.getOuId();
        // 查询功能是否配置复核打印单据配置
        WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundManager.findByFunctionIdExt(whCheckingResultCommand.getFunctionId(), ouId);
        String checkingPrint = whFunctionOutBound.getCheckingPrint();
        if (null != checkingPrint && "".equals(checkingPrint)) {
            String[] checkingPrintArray = checkingPrint.split(",");
            for (int i = 0; i < checkingPrintArray.length; i++) {
                List<Long> idsList = new ArrayList<Long>();
                for (WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()) {
                    List<WhPrintInfo> whPrintInfoLst = whPrintInfoManager.findByOutboundboxCodeAndPrintType(whCheckingCommand.getOutboundboxCode(), checkingPrintArray[i], ouId);
                    if (null == whPrintInfoLst || 0 == whPrintInfoLst.size()) {
                        idsList.add(whCheckingCommand.getId());
                        WhPrintInfo whPrintInfo = new WhPrintInfo();
                        whPrintInfo.setFacilityId(whCheckingCommand.getFacilityId());
                        whPrintInfo.setContainerId(whCheckingCommand.getContainerId());
                        Container container = containerDao.findByIdExt(whCheckingCommand.getContainerId(), whCheckingCommand.getOuId());
                        whPrintInfo.setContainerCode(container.getCode());
                        whPrintInfo.setBatch(whCheckingCommand.getBatch());
                        whPrintInfo.setWaveCode(whCheckingCommand.getWaveCode());
                        whPrintInfo.setOuId(whCheckingCommand.getOuId());
                        whPrintInfo.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                        Container outerContainer = containerDao.findByIdExt(whCheckingCommand.getOuterContainerId(), whCheckingCommand.getOuId());
                        whPrintInfo.setOuterContainerCode(outerContainer.getCode());
                        whPrintInfo.setContainerLatticeNo(whCheckingCommand.getContainerLatticeNo());
                        whPrintInfo.setOutboundboxId(whCheckingCommand.getOutboundboxId());
                        whPrintInfo.setOutboundboxCode(whCheckingCommand.getOutboundboxCode());
                        whPrintInfo.setPrintType(checkingPrintArray[i]);
                        whPrintInfo.setPrintCount(1);
                        whPrintInfoManager.saveOrUpdate(whPrintInfo);
                    }
                }
                try {
                    if (CheckingPrint.PACKING_LIST.equals(checkingPrintArray[i])) {
                        // 装箱清单
                        checkingManager.printPackingList(idsList, whCheckingResultCommand.getUserId(), ouId);
                    }
                    if (CheckingPrint.SALES_LIST.equals(checkingPrintArray[i])) {
                        // 销售清单
                        checkingManager.printSalesList(idsList, whCheckingResultCommand.getUserId(), ouId);
                    }
                    if (CheckingPrint.SINGLE_PLANE.equals(checkingPrintArray[i])) {
                        // 面单
                        checkingManager.printSinglePlane(null,null, whCheckingResultCommand.getUserId(), ouId);
                    }
                    if (CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])) {
                        // 箱标签
                        checkingManager.printBoxLabel(null, whCheckingResultCommand.getUserId(), ouId);
                    }
                } catch (Exception e) {
                    log.error(e + "");
                }
            }
        }
        return isSuccess;
    }

    /**
     * 更新复核数据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean updateChecking(WhCheckingResultCommand whCheckingResultCommand) {
        Boolean isSuccess = true;
        for (WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()) {
            if (CheckingStatus.NEW == whCheckingCommand.getStatus()) {
                whCheckingCommand.setStatus(CheckingStatus.FINISH);
                whCheckingManager.saveOrUpdate(whCheckingCommand);
                List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
                for (WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst) {// 疑问1
                    whCheckingLineManager.saveOrUpdate(whCheckingLineCommand);
                }
            }
        }
        return isSuccess;
    }

    // 按单复合更新复合明细数据

    /**
     * 生成出库箱库存与箱数据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean createOutboundbox(WhCheckingResultCommand whCheckingResultCommand) {// 疑问2
        Boolean isSuccess = true;
        for (WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()) {
            List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
            // 小车货格库存，小车出库箱库存，播种墙货格库存，播种墙出库箱库存，周转箱库存
            if (null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getContainerId() || null != whCheckingCommand.getOuterContainerId() || null != whCheckingCommand.getContainerLatticeNo()) {
                for (WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst) {
                    // 获取查询条件
                    WhSkuInventory skuInventory = new WhSkuInventory();
                    skuInventory.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                    skuInventory.setInsideContainerId(whCheckingCommand.getContainerId());
                    // 根据播种墙ID获取播种墙信息
                    if (null != whCheckingCommand.getFacilityId()) {
                        WhOutboundFacilityCommand whOutboundFacilityCommand = checkingManager.findOutboundFacilityById(whCheckingCommand.getFacilityId(), whCheckingLineCommand.getOuId());
                        skuInventory.setSeedingWallCode(whOutboundFacilityCommand.getFacilityCode());
                    }
                    skuInventory.setContainerLatticeNo(whCheckingCommand.getContainerLatticeNo());
                    skuInventory.setOutboundboxCode(whCheckingCommand.getOutboundboxCode());
                    skuInventory.setUuid(whCheckingLineCommand.getUuid());
                    List<WhSkuInventory> whSkuInventoryLst = whSkuInventoryManager.findWhSkuInventoryByPramas(skuInventory);
                    // 生成出库箱库存
                    String uuid = checkingManager.createOutboundboxInventory(whCheckingCommand, whSkuInventoryLst);
                    whCheckingLineCommand.setUuid(uuid);
                    // 删除原有库存
                    for (WhSkuInventory whSkuInventory : whSkuInventoryLst) {
                        whSkuInventoryManager.deleteSkuInventory(whSkuInventory.getId(), whSkuInventory.getOuId());
                    }
                }
            }
            this.saveWhOutboundboxCommand(whCheckingCommand);
            for (WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst) {
                this.saveWhOutboundboxLineCommand(whCheckingLineCommand);
                List<WhSkuInventorySnCommand> whSkuInventorySnCommandLst = whSkuInventorySnManager.findWhSkuInventoryByUuid(whCheckingLineCommand.getOuId(), whCheckingLineCommand.getUuid());
                for (WhSkuInventorySnCommand whSkuInventorySnCommand : whSkuInventorySnCommandLst) {
                    whSkuInventorySnCommand.setUuid(whSkuInventorySnCommand.getUuid());
                    whSkuInventorySnManager.saveOrUpdate(whSkuInventorySnCommand);
                    this.saveWhOutboundboxLineSnCommand(whSkuInventorySnCommand, whCheckingLineCommand);
                }
            }
        }
        return isSuccess;
    }

    /**
     * 更新出库单状态(按单复合只更新一个出库单，按单复合更新多个 )
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean updateOdoStatus(WhCheckingResultCommand whCheckingResultCommand) {
        Boolean isSuccess = true;
        // 修改出库单状态为复核完成状态。
        for (WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()) {
            List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
            for (WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst) {
                OdoCommand odoCommand = odoManager.findOdoCommandByIdOuId(whCheckingLineCommand.getId(), whCheckingResultCommand.getOuId());
                WhOdo whOdo = new WhOdo();
                // 复制数据
                BeanUtils.copyProperties(odoCommand, whOdo);
                // TODO,据说明天讨论
                whOdo.setOdoStatus(odoCommand.getOdoStatus());
                odoManager.updateByVersion(whOdo);
            }
        }
        return isSuccess;
    }

    /**
     * 算包裹计重
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean packageWeightCalculation(WhCheckingResultCommand whCheckingResultCommand) {
        Boolean isSuccess = true;
        Long ouId = whCheckingResultCommand.getOuId();
        // 查询功能是否配置复核打印单据配置
        WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundManager.findByFunctionIdExt(whCheckingResultCommand.getFunctionId(), ouId);
        for (WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()) {
            Map<String, List<WhCheckingLineCommand>> checkingLineMap = getCheckingLineForGroup(whCheckingCommand.getId(), ouId, whCheckingCommand.getOutboundboxId());
            for (String key : checkingLineMap.keySet()) {
                List<WhCheckingLineCommand> whCheckingLineCommandLst = checkingLineMap.get(key);
                BigDecimal calcWeight = new BigDecimal(0.00);
                for (WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst) {
                    WhSkuCommand whSkuCommand = whSkuManager.getSkuBybarCode(whCheckingLineCommand.getSkuBarCode(), ouId);
                    BigDecimal b1 = new BigDecimal(whSkuCommand.getWeight().toString());
                    BigDecimal b2 = new BigDecimal(whCheckingLineCommand.getCheckingQty().toString());
                    calcWeight = calcWeight.add(b1.multiply(b2));
                }
                WhOdoPackageInfoCommand whOdoPackageInfoCommand = new WhOdoPackageInfoCommand();
                whOdoPackageInfoCommand.setOdoId(whCheckingLineCommandLst.get(0).getOdoId());
                whOdoPackageInfoCommand.setOutboundboxId(whCheckingCommand.getOutboundboxId());
                whOdoPackageInfoCommand.setOutboundboxCode(whCheckingCommand.getOutboundboxCode());
                whOdoPackageInfoCommand.setStatus(1);
                whOdoPackageInfoCommand.setCalcWeight(calcWeight.longValue());
                whOdoPackageInfoCommand.setFloats(whFunctionOutBound.getWeightFloatPercentage());
                whOdoPackageInfoCommand.setActualWeight(null);
                whOdoPackageInfoCommand.setLifecycle(1);
                whOdoPackageInfoCommand.setCreateId(whCheckingResultCommand.getUserId());
                whOdoPackageInfoCommand.setCreateTime(new Date());
                whOdoPackageInfoCommand.setLastModifyTime(new Date());
                whOdoPackageInfoCommand.setModifiedId(whCheckingResultCommand.getUserId());
                whOdoPackageInfoManager.saveOrUpdate(whOdoPackageInfoCommand);
            }
        }
        return isSuccess;
    }

    public Map<String, List<WhCheckingLineCommand>> getCheckingLineForGroup(Long checkingId, Long ouId, Long outboundboxId) {
        Map<String, List<WhCheckingLineCommand>> checkingLineMap = new HashMap<String, List<WhCheckingLineCommand>>();
        List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(checkingId, ouId);
        for (WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst) {
            String key = whCheckingLineCommand.getOdoId().toString() + outboundboxId.toString();
            List<WhCheckingLineCommand> checkingLineCommandLst = new ArrayList<WhCheckingLineCommand>();
            if (null != checkingLineMap.get(key)) {
                checkingLineCommandLst = checkingLineMap.get(key);
            }
            checkingLineCommandLst.add(whCheckingLineCommand);
            checkingLineMap.put(key, checkingLineCommandLst);
        }
        return checkingLineMap;
    }

    /**
     * 出库箱头信息
     * 
     * @author qiming.liu
     * @param whCheckingCommand
     */
    public void saveWhOutboundboxCommand(WhCheckingCommand whCheckingCommand) {

        WhOutboundboxCommand whOutboundboxCommand = new WhOutboundboxCommand();
        whOutboundboxCommand.setBatch(whCheckingCommand.getBatch());
        whOutboundboxCommand.setWaveCode(whCheckingCommand.getWaveCode());
        whOutboundboxCommand.setCustomerCode(whCheckingCommand.getCustomerCode());
        whOutboundboxCommand.setCustomerName(whCheckingCommand.getCustomerName());
        whOutboundboxCommand.setStoreCode(whCheckingCommand.getStoreCode());
        whOutboundboxCommand.setStoreName(whCheckingCommand.getStoreName());
        whOutboundboxCommand.setTransportCode(whCheckingCommand.getTransportCode());
        whOutboundboxCommand.setTransportName(whCheckingCommand.getTransportName());
        whOutboundboxCommand.setProductCode(whCheckingCommand.getProductCode());
        whOutboundboxCommand.setProductName(whCheckingCommand.getProductName());
        whOutboundboxCommand.setTimeEffectCode(whCheckingCommand.getTimeEffectCode());
        whOutboundboxCommand.setTimeEffectName(whCheckingCommand.getTimeEffectName());
        whOutboundboxCommand.setStatus(OutboundboxStatus.NEW);
        whOutboundboxCommand.setOuId(whCheckingCommand.getOuId());
        whOutboundboxCommand.setOdoId(null);
        whOutboundboxCommand.setOutboundboxId(whCheckingCommand.getOutboundboxId());
        whOutboundboxCommand.setOutboundboxCode(whCheckingCommand.getOutboundboxCode());
        whOutboundboxCommand.setDistributionMode(whCheckingCommand.getDistributionMode());
        whOutboundboxCommand.setPickingMode(whCheckingCommand.getPickingMode());
        whOutboundboxCommand.setCheckingMode(whCheckingCommand.getCheckingMode());
        whOutboundboxManager.saveOrUpdate(whOutboundboxCommand);

    }

    /**
     * 出库箱明细
     * 
     * @author qiming.liu
     * @param whCheckingLineCommand
     */
    public void saveWhOutboundboxLineCommand(WhCheckingLineCommand whCheckingLineCommand) {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1;
        WhOutboundboxLineCommand whOutboundboxLineCommand = new WhOutboundboxLineCommand();
        whOutboundboxLineCommand.setWhOutboundboxId(null);
        whOutboundboxLineCommand.setSkuCode(whCheckingLineCommand.getSkuCode());
        whOutboundboxLineCommand.setSkuExtCode(whCheckingLineCommand.getSkuExtCode());
        whOutboundboxLineCommand.setSkuBarCode(whCheckingLineCommand.getSkuBarCode());
        whOutboundboxLineCommand.setSkuName(whCheckingLineCommand.getSkuName());
        whOutboundboxLineCommand.setQty(whCheckingLineCommand.getQty().doubleValue());
        whOutboundboxLineCommand.setCustomerCode(whCheckingLineCommand.getCustomerCode());
        whOutboundboxLineCommand.setCustomerName(whCheckingLineCommand.getCustomerName());
        whOutboundboxLineCommand.setStoreCode(whCheckingLineCommand.getStoreCode());
        whOutboundboxLineCommand.setStoreName(whCheckingLineCommand.getStoreName());
        whOutboundboxLineCommand.setInvStatus(whCheckingLineCommand.getInvStatus());
        whOutboundboxLineCommand.setInvType(whCheckingLineCommand.getInvType());
        whOutboundboxLineCommand.setBatchNumber(whCheckingLineCommand.getBatchNumber());
        whOutboundboxLineCommand.setMfgDate(whCheckingLineCommand.getMfgDate());
        whOutboundboxLineCommand.setExpDate(whCheckingLineCommand.getExpDate());
        whOutboundboxLineCommand.setCountryOfOrigin(whCheckingLineCommand.getCountryOfOrigin());
        whOutboundboxLineCommand.setInvAttr1(whCheckingLineCommand.getInvAttr1());
        whOutboundboxLineCommand.setInvAttr2(whCheckingLineCommand.getInvAttr2());
        whOutboundboxLineCommand.setInvAttr3(whCheckingLineCommand.getInvAttr3());
        whOutboundboxLineCommand.setInvAttr4(whCheckingLineCommand.getInvAttr4());
        whOutboundboxLineCommand.setInvAttr5(whCheckingLineCommand.getInvAttr5());
        whOutboundboxLineCommand.setUuid(whCheckingLineCommand.getUuid());
        whOutboundboxLineCommand.setOuId(whCheckingLineCommand.getOuId());
        whOutboundboxLineCommand.setOdoId(whCheckingLineCommand.getOdoId());
        whOutboundboxLineCommand.setOdoLineId(whCheckingLineCommand.getOdoLineId());
        whOutboundboxLineCommand.setSysDate(String.valueOf(month));
        whOutboundboxLineManager.saveOrUpdate(whOutboundboxLineCommand);

    }

    /**
     * t_wh_outboundbox_line_sn
     * 
     * @author qiming.liu
     * @param whCheckingLineCommand
     */
    public void saveWhOutboundboxLineSnCommand(WhSkuInventorySnCommand whSkuInventorySnCommand, WhCheckingLineCommand whCheckingLineCommand) {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1;
        WhOutboundboxLineSnCommand whOutboundboxLineSnCommand = new WhOutboundboxLineSnCommand();
        whOutboundboxLineSnCommand.setWhOutboundboxLineId(whCheckingLineCommand.getId());
        whOutboundboxLineSnCommand.setSn(whSkuInventorySnCommand.getSn());
        whOutboundboxLineSnCommand.setOccupationCode(whSkuInventorySnCommand.getOccupationCode());
        whOutboundboxLineSnCommand.setReplenishmentCode(null);
        whOutboundboxLineSnCommand.setDefectWareBarcode(whSkuInventorySnCommand.getDefectWareBarcode());
        whOutboundboxLineSnCommand.setDefectSource(whSkuInventorySnCommand.getDefectSource());
        whOutboundboxLineSnCommand.setDefectTypeId(whSkuInventorySnCommand.getDefectTypeId());
        whOutboundboxLineSnCommand.setDefectReasonsId(whSkuInventorySnCommand.getDefectReasonsId());
        whOutboundboxLineSnCommand.setStatus(whSkuInventorySnCommand.getStatus());
        whOutboundboxLineSnCommand.setInvAttr(whSkuInventorySnCommand.getInvAttr());
        whOutboundboxLineSnCommand.setUuid(whSkuInventorySnCommand.getUuid());
        whOutboundboxLineSnCommand.setOuId(whSkuInventorySnCommand.getOuId());
        whOutboundboxLineSnCommand.setSysUuid(whSkuInventorySnCommand.getSysUuid());
        whOutboundboxLineSnCommand.setSysDate(String.valueOf(month));
        whOutboundboxLineSnManager.saveOrUpdate(whOutboundboxLineSnCommand);
    }



    /** =============================================================== */


    /**
     * 根据ID查找出库设施
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    @Override
    public WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId) {
        return checkingManager.findOutboundFacilityById(id, ouId);
    }

    /**
     * 获取复核箱内所有商品的SN/残次信息
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @return
     */
    public List<WhSkuInventorySnCommand> findCheckingSkuInvSnByCheckingId(Long checkingId, Long ouId) {
        return checkingManager.findCheckingSkuInvSnByCheckingId(checkingId, ouId);
    }

    /**
     * 根据复核头获取所有复核明细
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @param logId
     * @return
     */
    public List<WhCheckingLineCommand> getCheckingLineListByChecking(Long checkingId, Long ouId, String logId) {
        List<WhCheckingLineCommand> whCheckingLineCommandList = whCheckingLineManager.getCheckingLineByCheckingId(checkingId, ouId);

        return whCheckingLineCommandList;
    }

    /**
     * 从缓存获取客户信息
     *
     * @author mingwei.xie
     * @param customerId
     * @return
     */
    public Customer findCustomerByRedis(Long customerId) {
        Map<Long, Customer> customerMap = this.findCustomerByRedis(Collections.singletonList(customerId));
        return customerMap.get(customerId);
    }

    /**
     * 从缓存获取店铺信息
     *
     * @author mingwei.xie
     * @param storeId
     * @return
     */
    public Store findStoreByRedis(Long storeId) {
        Map<Long, Store> storeMap = this.findStoreByRedis(Collections.singletonList(storeId));
        return storeMap.get(storeId);
    }


    /**
     * 释放复核的周转箱、小车、播种墙
     *
     * @author mingwei.xie
     * @param checkingId
     * @param checkingSourceCode
     * @param checkingType
     * @param userId
     * @param ouId
     * @param logId
     */
    public void releaseCheckingSource(Long checkingId, String checkingSourceCode, String checkingType, Long userId, Long ouId, String logId) {
        WhCheckingCommand checkingCommand = checkingManager.findCheckingById(checkingId, ouId);
        boolean isCheckingFinished = this.checkBoxCheckingFinished(checkingCommand.getId(), ouId, logId);
        if (isCheckingFinished) {
            // 完成箱复核,判断小车、播种墙是否完成复核
            switch (checkingType) {
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_BOX:
                    /** 按箱复核类型 小车出库箱 */
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_GRID:
                    /** 按箱复核类型 小车货格 */
                    boolean isTrolleyCheckingFinished = this.checkTrolleyCheckingFinished(checkingId, checkingSourceCode, ouId, logId);
                    if (isTrolleyCheckingFinished) {
                        try {
                            // 释放小车
                            Container container = this.releaseTrolleyContainer(checkingCommand, userId, ouId);

                            int updateCount = containerManager.saveOrUpdateByVersion(container);
                            if (1 != updateCount) {
                                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                            }
                        } catch (Exception e) {
                            throw new BusinessException(ErrorCodes.CHECKING_RELEASE_TROLLEY_ERROR);
                        }
                    }
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_BOX:
                    /** 按箱复核类型 播种墙出库箱 */
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_GRID:
                    /** 按箱复核类型 播种墙货格 */
                    boolean isFacilityCheckingFinished = this.checkFacilityCheckingFinished(checkingId, checkingSourceCode, ouId, logId);
                    if (isFacilityCheckingFinished) {
                        try {
                            // 释放播种墙
                            WhOutboundFacility whOutboundFacility = releaseSeedingFacility(checkingCommand, userId, ouId);

                            int updateCount = checkingManager.releaseSeedingFacility(whOutboundFacility);
                            if (1 != updateCount) {
                                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                            }
                        } catch (Exception e) {
                            throw new BusinessException(ErrorCodes.CHECKING_RELEASE_SEEDING_FACILITY_ERROR);
                        }
                    }
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_OUTBOUND_BOX:
                    /** 按箱复核类型 出库箱 */
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TURNOVER_BOX:
                    /** 按箱复核类型 周转箱 */
                    boolean isBoxCheckingFinished = this.checkBoxCheckingFinished(checkingCommand.getId(), ouId, logId);
                    if (isBoxCheckingFinished) {
                        try {
                            // 释放周转箱
                            Container container = this.releaseTurnoverBox(checkingCommand, userId, ouId);

                            int updateCount = containerManager.saveOrUpdateByVersion(container);
                            if (1 != updateCount) {
                                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                            }
                        } catch (Exception e) {
                            throw new BusinessException(ErrorCodes.CHECKING_RELEASE_TURNOVERBOX_ERROR);
                        }
                    }
                    break;
                default:
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SOURCE_TYPE_ERROR);
            }
        }
    }

    /**
     * 释放播种墙
     *
     * @author mingwei.xie
     * @param checkingCommand
     * @param userId
     * @param ouId
     * @return
     */
    private WhOutboundFacility releaseSeedingFacility(WhCheckingCommand checkingCommand, Long userId, Long ouId) {
        WhOutboundFacilityCommand seedingFacility = this.findOutboundFacilityById(checkingCommand.getFacilityId(), ouId);
        WhOutboundFacility whOutboundFacility = new WhOutboundFacility();
        BeanUtils.copyProperties(seedingFacility, whOutboundFacility);

        whOutboundFacility.setOperatorId(userId);
        // TODO 待验证状态常量
        whOutboundFacility.setStatus(BaseModel.LIFECYCLE_NORMAL.toString());
        whOutboundFacility.setBatch(null);
        return whOutboundFacility;
    }

    /**
     * 按箱复核完成，保存数据
     *
     * @author mingwei.xie
     * @param function
     * @param outboundFacilityId
     * @param checkingCommand
     * @param checkingSourceCode
     * @param checkingType
     * @param userId
     * @param ouId
     * @param logId
     */
    public void finishedCheckingByContainer(WhFunctionOutBound function, Long outboundFacilityId, WhCheckingCommand checkingCommand, String checkingSourceCode, String checkingType, Long userId, Long ouId, String logId) {
        // 复核台信息
        WhOutboundFacilityCommand facilityCommand = checkingManager.findOutboundFacilityById(outboundFacilityId, ouId);
        // 仓库配置信息
        WarehouseMgmt warehouseMgmt = warehouseManager.findWhMgmtByOuId(ouId);
        // 复核头信息
        WhCheckingCommand orgChecking = checkingManager.findCheckingById(checkingCommand.getId(), ouId);

        if (null == orgChecking) {
            throw new BusinessException(ErrorCodes.CHECKING_CHECKING_INFO_NULL_ERROR);
        }
        if (CheckingStatus.NEW != orgChecking.getStatus() && CheckingStatus.PART_FINISH != orgChecking.getStatus()) {
            throw new BusinessException(ErrorCodes.CHECKING_CHECKING_STATUS_ERROR);
        }

        // 原始复核明细集合 <lineId, line>原始的复核明细集合，方便取数据
        Map<Long, WhCheckingLineCommand> orgCheckingLineMap = new HashMap<>();
        // 原始复核明细对应的uuid集合 <lineId, List<uuid>>用于查询整个复核箱中的所有库存
        Map<Long, Set<String>> checkingLineUuidMap = new HashMap<>();
        // 原始复核箱中的明细集合
        List<WhCheckingLineCommand> orgCheckingLineList = this.getOrgCheckingLineList(orgChecking, orgCheckingLineMap, checkingLineUuidMap, ouId, logId);
        // 原始明细uuid对应的库存集合 <lineId_uuid,List<WhSkuInventory>>方便根据明细lineId和uuid获取原始库存
        Map<String, List<WhSkuInventory>> uuidLineSkuInvListMap = this.getLineIdUuidSkuInvListMap(checkingLineUuidMap, ouId);
        // 原始复核数据中SN/残次信息集合
        Map<Long, WhSkuInventorySnCommand> orgSnInvMap = this.getOrgCheckingWhSkuInventorySnCommandMap(orgChecking, ouId);


        // 已复核的SN
        List<WhSkuInventorySnCommand> checkedSnInvList = new ArrayList<>();
        // 出库箱装箱明细
        List<WhOutboundboxLine> outboundboxLineList = new ArrayList<>();
        // 出库单在出库箱中的库存
        List<WhSkuInventory> outboundboxSkuInvList = new ArrayList<>();
        // 出库单在原始箱中的库存
        Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet = new HashSet<>();
        // 需要更新已复核数量的复核明细集合
        Set<WhCheckingLineCommand> toUpdateCheckingLineSet = new HashSet<>();
        // 包裹计重
        BigDecimal packageCalcWeight = new BigDecimal(0.00);

        // 复核使用的出库箱
        WhOutboundboxCommand checkedBox = checkingCommand.getOutboundbox();
        // 完成复核的明细集合
        List<WhCheckingLineCommand> checkedLineList = checkedBox.getCheckingLineList();
        for (WhCheckingLineCommand checkedLine : checkedLineList) {
            Long checkingLineId = checkedLine.getId();
            // 已复核数量
            Long checkingQty = checkedLine.getCheckingQty();
            // 已复核SN/残次信息列表
            List<Long> snInvIdList = checkedLine.getSnInventoryIdList();


            // 原始复核明细
            WhCheckingLineCommand orgCheckingLine = orgCheckingLineMap.get(checkingLineId);
            // 原始明细lineId_uuid对应的库存集合
            List<WhSkuInventory> odoOrgSkuInvList = uuidLineSkuInvListMap.get(orgCheckingLine.getOdoLineId() + "_" + orgCheckingLine.getUuid());


            // 更新明细复核数量
            this.updateOrgCheckingLine(userId, toUpdateCheckingLineSet, checkingQty, orgCheckingLine);
            // 扣减原始复核明细对应的库存数量
            this.updateOrgCheckingSkuInv(toUpdateOdoOrgSkuInvSet, odoOrgSkuInvList, checkingQty, logId);

            // 新出库箱中的明细库存
            WhSkuInventory odoSkuInv = this.createWhSkuInventory(odoOrgSkuInvList.get(0), checkedBox.getOutboundboxCode(), checkingQty, ouId, logId);
            outboundboxSkuInvList.add(odoSkuInv);

            // 创建出库箱明细
            WhOutboundboxLine whOutboundboxLine = this.createWhOutboundboxLine(orgCheckingLine, checkingQty, odoSkuInv);
            outboundboxLineList.add(whOutboundboxLine);

            // 记录已复核的SN信息
            this.updateOrgCheckingSkuInvSn(orgSnInvMap, checkedSnInvList, snInvIdList, odoSkuInv);

            // 累计包裹重量，计算包裹计重
            SkuRedisCommand skuRedis = skuRedisManager.findSkuMasterBySkuId(odoSkuInv.getSkuId(), ouId, logId);
            Sku sku = skuRedis.getSku();
            packageCalcWeight = packageCalcWeight.add(new BigDecimal(sku.getWeight()).multiply(new BigDecimal(checkingQty)));

        }

        // 创建出库箱信息
        WhOutboundbox whOutboundbox = this.createWhOutboundbox(orgChecking, checkedBox.getOutboundboxCode(), orgCheckingLineList.get(0).getOdoId(), userId, ouId, logId);

        // 装箱包裹计重信息
        WhOdoPackageInfoCommand odoPackageInfo = this.createOdoPackageInfo(function, whOutboundbox, packageCalcWeight, orgCheckingLineList.get(0).getOdoId(), userId, ouId);

        // 设置复核头状态
        this.updateChecking(orgChecking, orgCheckingLineList, userId);

        // 出库单信息
        WhOdo whOdo = odoManagerProxy.findOdOById(orgCheckingLineList.get(0).getOdoId(), ouId);

        // 保存耗材库存
        WhOutboundConsumable whOutboundConsumable = null;
        WhSkuInventoryCommand consumableSkuInv = null;
        if (warehouseMgmt.getIsMgmtConsumableSku()) {
            whOutboundConsumable = this.createOutboundConsumable(facilityCommand, warehouseMgmt, orgChecking, checkedBox, whOdo, userId, ouId, logId);
            consumableSkuInv = checkingManager.getConsumableSkuInventory(checkedBox, ouId, logId);
        }


        // 更新出库单
        whOdo = this.updateOdo(checkingCommand.getId(), orgCheckingLineList, whOdo, userId, ouId, logId);
        // 释放小车
        Container trolleyContainer = null;
        // 释放播种墙
        WhOutboundFacility seedingFacility = null;
        // 释放周转箱
        Container turnoverBoxContainer = null;

        boolean isBoxCheckingFinished = this.checkBoxCheckingFinished(orgCheckingLineList);
        if (isBoxCheckingFinished) {
            // 完成箱复核,判断小车、播种墙是否完成复核
            switch (checkingType) {
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_BOX:
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_GRID:
                    // 按箱复核类型 小车
                    boolean isTrolleyCheckingFinished = this.checkTrolleyCheckingFinished(orgChecking.getId(), checkingSourceCode, ouId, logId);
                    if (isTrolleyCheckingFinished) {
                        // 释放小车
                        trolleyContainer = releaseTrolleyContainer(orgChecking, userId, ouId);
                    }
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_BOX:
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_GRID:
                    // 按箱复核类型 播种墙
                    boolean isFacilityCheckingFinished = this.checkFacilityCheckingFinished(orgChecking.getId(), checkingSourceCode, ouId, logId);
                    if (isFacilityCheckingFinished) {
                        // 释放播种墙
                        seedingFacility = this.releaseSeedingFacility(checkingCommand, userId, ouId);
                    }
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_OUTBOUND_BOX:
                    // 按箱复核类型 出库箱
                    break;
                case Constants.OUTBOUND_BOX_CHECKING_TYPE_TURNOVER_BOX:
                    // 按箱复核类型 周转箱
                    // 释放周转箱
                    turnoverBoxContainer = releaseTurnoverBox(checkingCommand, userId, ouId);
                    break;
                default:
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SOURCE_TYPE_ERROR);
            }
        }



        WhCheckingResultCommand whCheckingResultCommand = new WhCheckingResultCommand();
        if (null != trolleyContainer) {
            whCheckingResultCommand.setContainer(trolleyContainer);
        } else if (null != turnoverBoxContainer) {
            whCheckingResultCommand.setContainer(turnoverBoxContainer);
        }
        if (null != seedingFacility) {
            whCheckingResultCommand.setSeedingFacility(seedingFacility);
        }
        // 更新复核头
        whCheckingResultCommand.setOrgCheckingCommand(orgChecking);
        // 更新复核明细
        whCheckingResultCommand.setToUpdateCheckingLineSet(toUpdateCheckingLineSet);
        // 更新复核箱的库存
        whCheckingResultCommand.setToUpdateOdoOrgSkuInvSet(toUpdateOdoOrgSkuInvSet);
        // 创建出库箱
        whCheckingResultCommand.setWhOutboundbox(whOutboundbox);
        // 创建出库箱明细
        whCheckingResultCommand.setOutboundboxLineList(outboundboxLineList);
        // 创建出库箱库存
        whCheckingResultCommand.setOutboundboxSkuInvList(outboundboxSkuInvList);
        // 创建包裹计重
        whCheckingResultCommand.setOdoPackageInfoCommand(odoPackageInfo);
        // 创建耗材信息
        whCheckingResultCommand.setWhOutboundConsumable(whOutboundConsumable);
        // 待删除的耗材库存
        whCheckingResultCommand.setConsumableSkuInv(consumableSkuInv);
        // 更新出库单
        whCheckingResultCommand.setWhOdo(whOdo);
        // 更新SN/残次信息
        whCheckingResultCommand.setCheckedSnInvList(checkedSnInvList);

        // 保存复核数据
        checkingManager.finishedChecking(whCheckingResultCommand, warehouseMgmt.getIsTabbInvTotal(), userId, ouId, logId);
    }

    /**
     * 释放复核的周转箱
     *
     * @author mingwei.xie
     * @param checkingCommand
     * @param userId
     * @param ouId
     * @return
     */
    private Container releaseTurnoverBox(WhCheckingCommand checkingCommand, Long userId, Long ouId) {
        Container container = containerManager.getContainerById(checkingCommand.getContainerId(), ouId);
        container.setOperatorId(userId);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
        container.setIsFull(false);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        return container;
    }

    /**
     * 释放复核的小车
     *
     * @author mingwei.xie
     * @param orgChecking
     * @param userId
     * @param ouId
     * @return
     */
    private Container releaseTrolleyContainer(WhCheckingCommand orgChecking, Long userId, Long ouId) {
        Container container = containerManager.getContainerById(orgChecking.getOuterContainerId(), ouId);
        container.setOperatorId(userId);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
        container.setIsFull(false);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        return container;
    }


    /**
     * 检查播种墙释放复核完成
     *
     * @author mingwei.xie
     * @param checkingId
     * @param checkingSourceCode
     * @param ouId
     * @param logId
     * @return
     */
    public boolean checkFacilityCheckingFinished(Long checkingId, String checkingSourceCode, Long ouId, String logId) {
        List<WhCheckingCommand> facilityCheckingCommandList = checkingManager.findCheckingBySeedingFacility(checkingSourceCode, ouId);
        boolean isFacilityCheckingFinished = true;
        for (WhCheckingCommand whCheckingCommand : facilityCheckingCommandList) {
            if (whCheckingCommand.getId().equals(checkingId)) {
                continue;
            }
            List<WhCheckingLineCommand> trolleyCheckingLineList = this.getCheckingLineListByChecking(whCheckingCommand.getId(), ouId, logId);
            for (WhCheckingLineCommand checkingLine : trolleyCheckingLineList) {
                if (!checkingLine.getQty().equals(checkingLine.getCheckingQty())) {
                    isFacilityCheckingFinished = false;
                    break;
                }
            }
            if (!isFacilityCheckingFinished) {
                break;
            }
        }
        return isFacilityCheckingFinished;
    }

    /**
     * 检查小车释放复核完成
     *
     * @author mingwei.xie
     * @param checkingId
     * @param checkingSourceCode
     * @param ouId
     * @param logId
     * @return
     */
    public boolean checkTrolleyCheckingFinished(Long checkingId, String checkingSourceCode, Long ouId, String logId) {
        List<WhCheckingCommand> trolleyCheckingCommandList = checkingManager.findCheckingByTrolley(checkingSourceCode, ouId);
        boolean isTrolleyCheckingFinished = true;
        for (WhCheckingCommand whCheckingCommand : trolleyCheckingCommandList) {
            if (whCheckingCommand.getId().equals(checkingId)) {
                continue;
            }
            List<WhCheckingLineCommand> trolleyCheckingLineList = this.getCheckingLineListByChecking(whCheckingCommand.getId(), ouId, logId);
            for (WhCheckingLineCommand checkingLine : trolleyCheckingLineList) {
                if (!checkingLine.getQty().equals(checkingLine.getCheckingQty())) {
                    isTrolleyCheckingFinished = false;
                    break;
                }
            }
            if (!isTrolleyCheckingFinished) {
                break;
            }
        }
        return isTrolleyCheckingFinished;
    }

    /**
     * 检查复核的箱子是否完成复核
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @param logId
     * @return
     */
    public boolean checkBoxCheckingFinished(Long checkingId, Long ouId, String logId) {
        List<WhCheckingLineCommand> checkingLineList = this.getCheckingLineListByChecking(checkingId, ouId, logId);
        boolean isCheckingFinished = true;
        for (WhCheckingLineCommand checkingLine : checkingLineList) {
            if (!checkingLine.getQty().equals(checkingLine.getCheckingQty())) {
                isCheckingFinished = false;
                break;
            }
        }
        return isCheckingFinished;
    }

    /**
     * 检查出库单释放完成复核
     *
     * @author mingwei.xie
     * @param checkingId 正在复核的出库单
     * @param odoId
     * @param ouId
     * @param logId
     * @return
     */
    private boolean checkOdoCheckingFinished(Long checkingId, Long odoId, Long ouId, String logId) {
        List<WhCheckingCommand> odoCheckingCommandList = checkingManager.findCheckingByOdo(odoId, ouId);
        boolean isCheckingFinished = true;
        for (WhCheckingCommand whCheckingCommand : odoCheckingCommandList) {
            if (whCheckingCommand.getId().equals(checkingId)) {
                continue;
            }
            List<WhCheckingLineCommand> checkingLineList = this.getCheckingLineListByChecking(whCheckingCommand.getCheckingId(), ouId, logId);
            for (WhCheckingLineCommand checkingLine : checkingLineList) {
                if (!checkingLine.getQty().equals(checkingLine.getCheckingQty())) {
                    isCheckingFinished = false;
                    break;
                }
            }
        }
        return isCheckingFinished;
    }


    /** ============================================================= */


    private WhOdo updateOdo(Long checkingId, List<WhCheckingLineCommand> orgCheckingLineList, WhOdo whOdo, Long userId, Long ouId, String logId) {
        boolean isBoxCheckingFinished = this.checkBoxCheckingFinished(orgCheckingLineList);
        if (isBoxCheckingFinished) {
            boolean isOdoCheckingFinished = this.checkOdoCheckingFinished(checkingId, whOdo.getId(), ouId, logId);
            if (isOdoCheckingFinished) {
                whOdo.setOdoStatus(OdoStatus.CHECKING_FINISH);
                whOdo.setModifiedId(userId);
            } else {
                whOdo = null;
            }
        } else {
            whOdo = null;
        }
        return whOdo;
    }

    private boolean checkBoxCheckingFinished(List<WhCheckingLineCommand> orgCheckingLineList) {
        boolean isBoxCheckingFinished = true;
        for (WhCheckingLineCommand whCheckingLine : orgCheckingLineList) {
            if (!whCheckingLine.getQty().equals(whCheckingLine.getCheckingQty())) {
                isBoxCheckingFinished = false;
                break;
            }
        }
        return isBoxCheckingFinished;
    }


    private void updateOrgCheckingLine(Long userId, Set<WhCheckingLineCommand> toUpdateCheckingLineSet, Long checkingQty, WhCheckingLineCommand orgCheckingLine) {
        if (null == orgCheckingLine.getCheckingQty()) {
            orgCheckingLine.setCheckingQty(checkingQty);
        } else {
            orgCheckingLine.setCheckingQty(orgCheckingLine.getCheckingQty() + checkingQty);
        }
        orgCheckingLine.setModifiedId(userId);
        toUpdateCheckingLineSet.add(orgCheckingLine);
    }

    private void updateOrgCheckingSkuInvSn(Map<Long, WhSkuInventorySnCommand> orgSnInvMap, List<WhSkuInventorySnCommand> checkedSnInvList, List<Long> snInvIdList, WhSkuInventory odoSkuInv) {
        if (null != snInvIdList && !snInvIdList.isEmpty()) {
            for (Long snInvId : snInvIdList) {
                WhSkuInventorySnCommand checkedSnInv = orgSnInvMap.get(snInvId);
                if (null == checkedSnInv) {
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SN_ERROR);
                }
                // 更新SN/残次信息的uuid
                checkedSnInv.setUuid(odoSkuInv.getUuid());
                // 记录需要更新uuid的SN/残次信息
                checkedSnInvList.add(checkedSnInv);
            }
        }
    }

    private void updateOrgCheckingSkuInv(Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet, List<WhSkuInventory> odoOrgSkuInvList, Long checkingQty, String logId) {
        Long odoFinishedLineCheckingQty = checkingQty;
        while (odoFinishedLineCheckingQty > 0) {
            for (WhSkuInventory odoOrgSkuInv : odoOrgSkuInvList) {
                if (odoOrgSkuInv.getOnHandQty() <= 0) {
                    log.warn("skuInventory onHandQty is zero, skuInventory is:[{}], logId is:[{}]", odoOrgSkuInv, logId);
                    continue;
                }
                if (odoOrgSkuInv.getOnHandQty() >= odoFinishedLineCheckingQty) {
                    odoOrgSkuInv.setOnHandQty(odoOrgSkuInv.getOnHandQty() - odoFinishedLineCheckingQty);
                    odoFinishedLineCheckingQty = 0L;
                } else {
                    odoFinishedLineCheckingQty = odoFinishedLineCheckingQty - odoOrgSkuInv.getOnHandQty().longValue();
                    odoOrgSkuInv.setOnHandQty(0d);
                }
                // 记录需要更新的原始库存
                toUpdateOdoOrgSkuInvSet.add(odoOrgSkuInv);
            }
            if (odoFinishedLineCheckingQty > 0) {
                throw new BusinessException(ErrorCodes.CHECKING_SKUINV_INSUFFICIENT_ERROR);
            }
        }
    }

    private WhOutboundConsumable createOutboundConsumable(WhOutboundFacilityCommand facilityCommand, WarehouseMgmt warehouseMgmt, WhCheckingCommand orgChecking, WhOutboundboxCommand checkedBox, WhOdo whOdo, Long userId, Long ouId, String logId) {

        WhOutboundConsumable whOutboundConsumable = new WhOutboundConsumable();

        // 管理耗材才会记录这些信息
        String consumableCode = checkedBox.getConsumableCode();
        Long consumableSkuId = checkedBox.getConsumableSkuId();
        String locationCode = checkedBox.getConsumableLocationCode();

        WhLocationSkuVolumeCommand locationSkuVolume = whLocationSkuVolumeManager.findFacilityLocSkuVolumeByLocSku(facilityCommand.getId(), locationCode, consumableSkuId, ouId);
        if (null == locationSkuVolume) {
            throw new BusinessException(ErrorCodes.CHECKING_CONSUMABLE_SKUINVLOC_ERROR);
        }

        // 累计包裹重量，计算包裹计重
        SkuRedisCommand skuRedis = skuRedisManager.findSkuMasterBySkuId(consumableSkuId, ouId, logId);
        Sku sku = skuRedis.getSku();

        whOutboundConsumable.setBatch(orgChecking.getBatch());
        whOutboundConsumable.setWaveCode(orgChecking.getWaveCode());
        whOutboundConsumable.setCustomerCode(orgChecking.getCustomerCode());
        whOutboundConsumable.setCustomerName(orgChecking.getCustomerName());
        whOutboundConsumable.setStoreCode(orgChecking.getStoreCode());
        whOutboundConsumable.setStoreName(orgChecking.getStoreName());
        whOutboundConsumable.setOdoId(whOdo.getId());
        whOutboundConsumable.setOdoCode(whOdo.getOdoCode());
        // TODO 不知道设置
        whOutboundConsumable.setTransportCode("");
        whOutboundConsumable.setWaybillCode("");
        whOutboundConsumable.setFacilityId(facilityCommand.getId());
        whOutboundConsumable.setFacilityCode(facilityCommand.getFacilityCode());
        whOutboundConsumable.setLocationId(locationSkuVolume.getLocationId());
        whOutboundConsumable.setLocationCode(locationSkuVolume.getLocationCode());
        whOutboundConsumable.setAreaId(locationSkuVolume.getWorkAreaId());
        whOutboundConsumable.setAreaCode(locationSkuVolume.getWorkAreaCode());
        whOutboundConsumable.setQty(1d);
        whOutboundConsumable.setOuId(ouId);
        // TODO 保存出库箱后返回的主键
        // whOutboundConsumable.setOutboundboxId();
        whOutboundConsumable.setOutboundboxCode(consumableCode);
        whOutboundConsumable.setSkuCode(sku.getCode());
        whOutboundConsumable.setSkuBarcode(sku.getBarCode());
        whOutboundConsumable.setSkuName(sku.getName());
        whOutboundConsumable.setSkuLength(sku.getLength());
        whOutboundConsumable.setSkuWidth(sku.getWidth());
        whOutboundConsumable.setSkuHeight(sku.getHeight());
        whOutboundConsumable.setSkuVolume(sku.getVolume());
        whOutboundConsumable.setSkuWeight(sku.getWeight());
        whOutboundConsumable.setCreateId(userId);
        whOutboundConsumable.setCreateTime(new Date());
        whOutboundConsumable.setModifiedId(userId);
        whOutboundConsumable.setLastModifyTime(new Date());

        return whOutboundConsumable;

    }

    private void updateChecking(WhCheckingCommand orgChecking, List<WhCheckingLineCommand> orgCheckingLineList, Long userId) {
        // 校验明细复核数据
        boolean isFinishedChecking = true;
        for (WhCheckingLineCommand orgCheckingLine : orgCheckingLineList) {
            if (!orgCheckingLine.getQty().equals(orgCheckingLine.getCheckingQty())) {
                isFinishedChecking = false;
                break;
            }
        }
        if (isFinishedChecking) {
            orgChecking.setStatus(CheckingStatus.FINISH);
        } else {
            orgChecking.setStatus(CheckingStatus.PART_FINISH);
        }
        orgChecking.setModifiedId(userId);
    }

    private WhOdoPackageInfoCommand createOdoPackageInfo(WhFunctionOutBound function, WhOutboundbox outboundbox, BigDecimal packageCalcWeight, Long odoId, Long userId, Long ouId) {

        WhOdoPackageInfoCommand whOdoPackageInfoCommand = new WhOdoPackageInfoCommand();
        whOdoPackageInfoCommand.setOdoId(odoId);
        // TODO 创建出库箱后再设置
        whOdoPackageInfoCommand.setOutboundboxId(outboundbox.getId());
        whOdoPackageInfoCommand.setOutboundboxCode(outboundbox.getOutboundboxCode());
        whOdoPackageInfoCommand.setStatus(1);
        whOdoPackageInfoCommand.setCalcWeight(packageCalcWeight.longValue());
        whOdoPackageInfoCommand.setFloats(function.getWeightFloatPercentage());
        whOdoPackageInfoCommand.setActualWeight(null);
        whOdoPackageInfoCommand.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        whOdoPackageInfoCommand.setCreateId(userId);
        whOdoPackageInfoCommand.setCreateTime(new Date());
        whOdoPackageInfoCommand.setLastModifyTime(new Date());
        whOdoPackageInfoCommand.setModifiedId(userId);
        whOdoPackageInfoCommand.setOuId(ouId);
        return whOdoPackageInfoCommand;

    }

    private Map<Long, WhSkuInventorySnCommand> getOrgCheckingWhSkuInventorySnCommandMap(WhCheckingCommand whCheckingCommand, Long ouId) {
        // <snInvId, snInv>
        Map<Long, WhSkuInventorySnCommand> orgSnInvMap = null;
        // 复核数据中SN/残次信息
        List<WhSkuInventorySnCommand> orgSnInvList = this.findCheckingSkuInvSnByCheckingId(whCheckingCommand.getId(), ouId);
        if (null != orgSnInvList && !orgSnInvList.isEmpty()) {
            orgSnInvMap = new HashMap<>();
            for (WhSkuInventorySnCommand orgSkuInv : orgSnInvList) {
                orgSnInvMap.put(orgSkuInv.getId(), orgSkuInv);
            }
        }
        return orgSnInvMap;
    }

    private Map<String, List<WhSkuInventory>> getLineIdUuidSkuInvListMap(Map<Long, Set<String>> checkingLineUuidMap, Long ouId) {
        Map<String, List<WhSkuInventory>> uuidLineSkuInvListMap = new HashMap<>();
        for (Long lineId : checkingLineUuidMap.keySet()) {
            Set<String> lineUuidSet = checkingLineUuidMap.get(lineId);
            for (String uuid : lineUuidSet) {
                // 出库单复核明细的原始库存
                List<WhSkuInventory> odoOrgSkuInvList = checkingManager.findCheckingOdoSkuInvByOdoLineIdUuid(lineId, ouId, uuid);
                if (null == odoOrgSkuInvList || odoOrgSkuInvList.isEmpty()) {
                    throw new BusinessException(ErrorCodes.CHECKING_TURNOVERBOX_ORG_SKUINV_ERROR);
                }

                uuidLineSkuInvListMap.put(lineId + "_" + uuid, odoOrgSkuInvList);
            }
        }
        return uuidLineSkuInvListMap;
    }

    private List<WhCheckingLineCommand> getOrgCheckingLineList(WhCheckingCommand checking, Map<Long, WhCheckingLineCommand> orgCheckingLineMap, Map<Long, Set<String>> checkingLineUuidMap, Long ouId, String logId) {
        // 整箱的复核明细信息集合
        List<WhCheckingLineCommand> orgCheckingLineList = this.getCheckingLineListByChecking(checking.getId(), ouId, logId);
        for (WhCheckingLineCommand orgCheckingLine : orgCheckingLineList) {
            orgCheckingLineMap.put(orgCheckingLine.getId(), orgCheckingLine);

            Set<String> checkingLineUuidSet = checkingLineUuidMap.get(orgCheckingLine.getId());
            if (null == checkingLineUuidSet) {
                checkingLineUuidSet = new HashSet<>();
                checkingLineUuidMap.put(orgCheckingLine.getOdoLineId(), checkingLineUuidSet);
            }
            checkingLineUuidSet.add(orgCheckingLine.getUuid());
        }
        return orgCheckingLineList;
    }

    private WhOutboundbox createWhOutboundbox(WhCheckingCommand whChecking, String outboundBoxCode, Long odoId, Long userId, Long ouId, String logId) {
        WhOutboundbox whOutboundbox = new WhOutboundbox();
        whOutboundbox.setBatch(whChecking.getBatch());
        whOutboundbox.setWaveCode(whChecking.getWaveCode());
        whOutboundbox.setCustomerCode(whChecking.getCustomerCode());
        whOutboundbox.setCustomerName(whChecking.getCustomerName());
        whOutboundbox.setStoreCode(whChecking.getStoreCode());
        whOutboundbox.setStoreName(whChecking.getStoreName());
        whOutboundbox.setOuId(whChecking.getOuId());
        whOutboundbox.setOdoId(odoId);
        whOutboundbox.setOutboundboxCode(outboundBoxCode);
        whOutboundbox.setDistributionMode(whChecking.getDistributionMode());
        whOutboundbox.setPickingMode(whChecking.getPickingMode());
        whOutboundbox.setCheckingMode(whChecking.getCheckingMode());
        whOutboundbox.setStatus(OutboundboxStatus.WEIGHING);

        return whOutboundbox;
    }

    private WhOutboundboxLine createWhOutboundboxLine(WhCheckingLineCommand whCheckingLine, Long checkingQty, WhSkuInventory odoSkuInv) {
        WhOutboundboxLine whOutboundboxLine = new WhOutboundboxLine();
        // whOutboundboxLine.setWhOutboundboxId(whOutboundbox.getId());
        whOutboundboxLine.setSkuCode(whCheckingLine.getSkuCode());
        whOutboundboxLine.setSkuExtCode(whCheckingLine.getSkuExtCode());
        whOutboundboxLine.setSkuBarCode(whCheckingLine.getSkuBarCode());
        whOutboundboxLine.setSkuName(whCheckingLine.getSkuName());
        whOutboundboxLine.setQty(checkingQty.doubleValue());
        whOutboundboxLine.setCustomerCode(whCheckingLine.getCustomerCode());
        whOutboundboxLine.setCustomerName(whCheckingLine.getCustomerName());
        whOutboundboxLine.setStoreName(whCheckingLine.getStoreName());
        whOutboundboxLine.setStoreCode(whCheckingLine.getStoreCode());
        whOutboundboxLine.setInvStatus(whCheckingLine.getInvStatus());
        whOutboundboxLine.setInvType(whCheckingLine.getInvType());
        whOutboundboxLine.setBatchNumber(whCheckingLine.getBatchNumber());
        whOutboundboxLine.setMfgDate(whCheckingLine.getMfgDate());
        whOutboundboxLine.setExpDate(whCheckingLine.getExpDate());
        whOutboundboxLine.setCountryOfOrigin(whCheckingLine.getCountryOfOrigin());
        whOutboundboxLine.setInvAttr1(whCheckingLine.getInvAttr1());
        whOutboundboxLine.setInvAttr2(whCheckingLine.getInvAttr2());
        whOutboundboxLine.setInvAttr3(whCheckingLine.getInvAttr3());
        whOutboundboxLine.setInvAttr4(whCheckingLine.getInvAttr4());
        whOutboundboxLine.setInvAttr5(whCheckingLine.getInvAttr5());
        whOutboundboxLine.setUuid(odoSkuInv.getUuid());
        whOutboundboxLine.setOuId(whCheckingLine.getOuId());
        whOutboundboxLine.setOdoId(whCheckingLine.getOdoId());
        whOutboundboxLine.setOdoLineId(whCheckingLine.getOdoLineId());

        return whOutboundboxLine;
    }


    private WhSkuInventory createWhSkuInventory(WhSkuInventory checkingLine, String outboundBoxCode, Long checkingQty, Long ouId, String logId) {
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setSkuId(checkingLine.getSkuId());
        skuInventory.setCustomerId(checkingLine.getCustomerId());
        skuInventory.setStoreId(checkingLine.getStoreId());
        // 占用编码是内部编码
        skuInventory.setOccupationCode(checkingLine.getOccupationCode());
        skuInventory.setOccupationLineId(checkingLine.getOccupationLineId());
        skuInventory.setSeedingWallCode(null);
        skuInventory.setContainerLatticeNo(null);
        skuInventory.setOutboundboxCode(outboundBoxCode);
        skuInventory.setOnHandQty(checkingQty.doubleValue());
        skuInventory.setAllocatedQty(0d);
        skuInventory.setToBeFilledQty(0d);
        skuInventory.setFrozenQty(0d);
        skuInventory.setInvStatus(checkingLine.getInvStatus());
        skuInventory.setInvType(checkingLine.getInvType());
        skuInventory.setBatchNumber(checkingLine.getBatchNumber());
        skuInventory.setMfgDate(checkingLine.getMfgDate());
        skuInventory.setExpDate(checkingLine.getExpDate());
        skuInventory.setCountryOfOrigin(checkingLine.getCountryOfOrigin());
        skuInventory.setInvAttr1(checkingLine.getInvAttr1());
        skuInventory.setInvAttr2(checkingLine.getInvAttr2());
        skuInventory.setInvAttr3(checkingLine.getInvAttr3());
        skuInventory.setInvAttr4(checkingLine.getInvAttr4());
        skuInventory.setInvAttr5(checkingLine.getInvAttr5());

        String uuid = null;
        try {
            uuid = SkuInventoryUuid.invUuid(skuInventory);
        } catch (Exception e) {
            log.error("checking createWhSkuInventory error, throw NoSuchAlgorithmException, skuInventory is:[{}], logId is:[{}]", skuInventory, logId);
            throw new BusinessException(ErrorCodes.CHECKING_BOX_SKUINV_CREATE_UUID_ERROR);
        }
        skuInventory.setUuid(uuid);
        skuInventory.setIsLocked(false);
        skuInventory.setOuId(checkingLine.getOuId());
        skuInventory.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO);
        skuInventory.setLastModifyTime(new Date());

        return skuInventory;
    }
}
