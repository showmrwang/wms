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

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineSnCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingPrint;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OutboundboxStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.InventoryStatusDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionOutBoundManager;
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
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
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
    private UomDao uomDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private InventoryStatusDao inventoryStatusDao;
    @Autowired
    private SkuRedisManager skuRedisManager;


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
                        checkingManager.printSinglePlane(idsList, whCheckingResultCommand.getUserId(), ouId);
                    }
                    if (CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])) {
                        // 箱标签
                        checkingManager.printBoxLabel(idsList, whCheckingResultCommand.getUserId(), ouId);
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
        return whOutboundFacilityDao.findByIdExt(id, ouId);
    }



    public List<WhSkuInventorySnCommand> findCheckingSkuInvSnByCheckingId(Long checkingId, Long ouId) {
        return whSkuInventorySnDao.findCheckingSkuInvSnByCheckingId(checkingId, ouId);
    }


    public List<WhCheckingLineCommand> getCheckingLineListByChecking(Long checkingId, Long ouId, String logId) {
        List<WhCheckingLineCommand> whCheckingLineCommandList = whCheckingLineManager.getCheckingLineByCheckingId(checkingId, ouId);;

        return whCheckingLineCommandList;
    }

    public Customer findCustomerByRedis(Long customerId) {
        Map<Long, Customer> customerMap = this.findCustomerByRedis(Collections.singletonList(customerId));
        return customerMap.get(customerId);
    }

    public Store findStoreByRedis(Long storeId) {
        Map<Long, Store> storeMap = this.findStoreByRedis(Collections.singletonList(storeId));
        return storeMap.get(storeId);
    }

    /**
     * 完成复核
     *
     * @param checkingCommand
     */
    public void finishedCheckingByContainer(WhFunctionOutBound function, WarehouseMgmt warehouseMgmt, WhCheckingCommand checkingCommand, String checkingType, Long userId, Long ouId, String logId) {
        // 复核头信息
        WhCheckingCommand orgChecking = checkingManager.findCheckingById(checkingCommand.getId(), ouId);

        if (null == orgChecking) {
            throw new BusinessException("未找到复核数据");
        }
        if (CheckingStatus.NEW != orgChecking.getStatus()) {
            throw new BusinessException("复核状态错误");
        }


        // <lineId, line>原始的复核明细集合，方便取数据
        Map<Long, WhCheckingLineCommand> orgCheckingLineMap = new HashMap<>();

        // <lineId, List<uuid>>用于查询整个复核箱中的所有库存
        Map<Long, Set<String>> checkingLineUuidMap = new HashMap<>();
        // 原始复核箱中的明细集合
        List<WhCheckingLineCommand> orgCheckingLineList = this.getOrgCheckingLineList(orgChecking, orgCheckingLineMap, checkingLineUuidMap, ouId, logId);


        // <lineId_uuid,List<WhSkuInventory>>方便根据明细lineId和uuid获取原始库存
        Map<String, List<WhSkuInventory>> uuidLineSkuInvListMap = this.getStringListMap(checkingLineUuidMap, ouId);
        // 复核数据中SN/残次信息
        Map<Long, WhSkuInventorySnCommand> orgSnInvMap = this.getLongWhSkuInventorySnCommandMap(orgChecking, ouId);


        // 已复核的SN
        List<WhSkuInventorySnCommand> checkedSnInvList = new ArrayList<>();

        // 创建出库箱信息
        List<WhOutboundbox> whOutboundboxList = new ArrayList<>();
        // 出库箱装箱明细
        Map<WhOutboundbox, List<WhOutboundboxLine>> outboundboxLineListMap = new HashMap<>();
        // 出库单在出库箱中的库存
        Map<WhOutboundbox, List<WhSkuInventory>> outboundboxSkuInvListMap = new HashMap<>();
        // 出库单在原始箱中的库存
        Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet = new HashSet<>();
        // 装箱包裹计重信息
        List<WhOdoPackageInfoCommand> odoPackageInfoList = new ArrayList<>();
        // 需要更新已复核数量的复核明细集合
        Set<WhCheckingLineCommand> toUpdateCheckingLineSet = new HashSet<>();

        // 复核完使用的所有出库箱
        List<WhOutboundboxCommand> checkedBoxList = checkingCommand.getOutboundboxList();
        for (WhOutboundboxCommand checkedBox : checkedBoxList) {
            String checkedBoxCode = checkedBox.getOutboundboxCode();
            if (warehouseMgmt.getIsMgmtConsumableSku()) {
                // 管理耗材才会记录这些信息
                String consumableCode = checkedBox.getConsumableCode();
                Long consumableSkuId = checkedBox.getConsumableSkuId();
                String locationCode = checkedBox.getConsumableLocationCode();


            }

            WhOutboundbox outboundbox = null;
            if (Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_GRID.equals(checkingType) || Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_GRID.equals(checkingType) || Constants.OUTBOUND_BOX_CHECKING_TYPE_TURNOVER_BOX.equals(checkingType)) {
                WhOutboundboxCommand outboundboxCommand = whOutboundboxManager.findByOutboundBoxCode(checkedBoxCode, ouId);
                outboundbox = new WhOutboundbox();
                BeanUtils.copyProperties(outboundboxCommand, outboundbox);
            } else {
                // 小车货格、播种墙货格、周转箱 创建出库箱装箱信息
                outboundbox = this.createWhOutboundbox(orgChecking, checkedBoxCode, orgCheckingLineList.get(0).getOdoId());
            }
            whOutboundboxList.add(outboundbox);

            // 新箱子中的装箱明细集合
            List<WhOutboundboxLine> outboundboxLineList = new ArrayList<>();
            // 新箱子中的库存明细集合
            List<WhSkuInventory> outboundBoxSkuInvList = new ArrayList<>();

            // 包裹计重
            BigDecimal packageCalcWeight = new BigDecimal(0.00);

            List<WhCheckingLineCommand> checkedLineList = checkedBox.getCheckingLineList();
            for (WhCheckingLineCommand checkedLine : checkedLineList) {
                Long checkingLineId = checkedLine.getId();
                // 已复核数量
                Long checkingQty = checkedLine.getCheckingQty();
                // 已复核SN/残次信息列表
                List<Long> snInvIdList = checkedLine.getSnInventoryIdList();


                // 原始复核明细
                WhCheckingLineCommand orgCheckingLine = orgCheckingLineMap.get(checkingLineId);
                if (null == orgCheckingLine.getCheckingQty()) {
                    orgCheckingLine.setCheckingQty(checkingQty);
                } else {
                    orgCheckingLine.setCheckingQty(orgCheckingLine.getCheckingQty() + checkingQty);
                }
                orgCheckingLine.setModifiedId(userId);
                toUpdateCheckingLineSet.add(orgCheckingLine);

                // 新出库箱中的复核明细
                WhCheckingLine newCheckingLine = new WhCheckingLine();
                BeanUtils.copyProperties(orgCheckingLine, newCheckingLine);
                newCheckingLine.setCheckingQty(checkingQty);

                // 原始明细lineId_uuid对应的库存集合
                List<WhSkuInventory> odoOrgSkuInvList = uuidLineSkuInvListMap.get(orgCheckingLine.getOdoLineId() + "_" + orgCheckingLine.getUuid());

                // 新出库箱中的明细库存
                WhSkuInventory odoSkuInv = this.createWhSkuInventory(odoOrgSkuInvList.get(0), checkedBoxCode, checkingQty, ouId, logId);
                outboundBoxSkuInvList.add(odoSkuInv);

                // 扣减原始复核明细对应的库存数量
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
                        // 记录需要更新的原始库存，正常情况最后库存数量都是0，执行删除操作
                        toUpdateOdoOrgSkuInvSet.add(odoOrgSkuInv);
                    }
                    if (odoFinishedLineCheckingQty > 0) {
                        throw new BusinessException("库存不足");
                    }
                }

                // 创建出库箱明细
                WhOutboundboxLine whOutboundboxLine = this.createWhOutboundboxLine(orgCheckingLine, checkingQty, odoSkuInv);
                outboundboxLineList.add(whOutboundboxLine);

                // 记录已复核的SN信息
                if (null != snInvIdList && !snInvIdList.isEmpty()) {
                    for (Long snInvId : snInvIdList) {
                        WhSkuInventorySnCommand checkedSnInv = orgSnInvMap.get(snInvId);
                        if (null == checkedSnInv) {
                            throw new BusinessException("未找到复核的SN信息");
                        }
                        // 更新SN/残次信息的uuid
                        checkedSnInv.setUuid(odoSkuInv.getUuid());
                        //TODO 更新状态
                        // 记录需要更新uuid的SN/残次信息
                        checkedSnInvList.add(checkedSnInv);
                    }
                }

                // 累计包裹重量，计算包裹计重
                SkuRedisCommand skuRedis = skuRedisManager.findSkuMasterBySkuId(odoSkuInv.getSkuId(), ouId, logId);
                Sku sku = skuRedis.getSku();
                packageCalcWeight = packageCalcWeight.add(new BigDecimal(sku.getWeight()).multiply(new BigDecimal(checkingQty)));

            }

            // 记录包裹计重信息
            WhOdoPackageInfoCommand odoPackageInfo = this.createOdoPackageInfo(function, outboundbox, packageCalcWeight, orgCheckingLineList.get(0).getOdoId(), userId, ouId);
            odoPackageInfoList.add(odoPackageInfo);

            // 记录出库箱对应的明细集合
            outboundboxLineListMap.put(outboundbox, outboundboxLineList);
            // 记录出库箱对应的库存集合
            outboundboxSkuInvListMap.put(outboundbox, outboundBoxSkuInvList);

        }

        // 设置复核头状态
        this.checkUpdateChecking(orgChecking, orgCheckingLineList, userId);

        // 保存复核数据
        checkingManager.finishedChecking(orgChecking, toUpdateCheckingLineSet, whOutboundboxList, outboundboxLineListMap, outboundboxSkuInvListMap, toUpdateOdoOrgSkuInvSet, odoPackageInfoList, checkedSnInvList, checkingType,
                warehouseMgmt.getIsTabbInvTotal(), userId, ouId, logId);
    }

    private void checkUpdateChecking(WhCheckingCommand orgChecking, List<WhCheckingLineCommand> orgCheckingLineList, Long userId) {
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

    private Map<Long, WhSkuInventorySnCommand> getLongWhSkuInventorySnCommandMap(WhCheckingCommand whCheckingCommand, Long ouId) {
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

    private Map<String, List<WhSkuInventory>> getStringListMap(Map<Long, Set<String>> checkingLineUuidMap, Long ouId) {
        Map<String, List<WhSkuInventory>> uuidLineSkuInvListMap = new HashMap<>();
        for (Long lineId : checkingLineUuidMap.keySet()) {
            Set<String> lineUuidSet = checkingLineUuidMap.get(lineId);
            for (String uuid : lineUuidSet) {
                // 出库单复核明细的原始库存
                List<WhSkuInventory> odoOrgSkuInvList = checkingManager.findCheckingOdoSkuInvByOdoLineIdUuid(lineId, ouId, uuid);
                if (null == odoOrgSkuInvList || odoOrgSkuInvList.isEmpty()) {
                    throw new BusinessException("周转箱明细原始库存未找到");
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


    private WhOutboundbox createWhOutboundbox(WhCheckingCommand whChecking, String outboundBoxCode, Long odoId) {
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


    public WhSkuInventory createWhSkuInventory(WhSkuInventory checkingLine, String outboundBoxCode, Long checkingQty, Long ouId, String logId) {
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
            log.error("seeding createWhSkuInventory error, throw NoSuchAlgorithmException, skuInventory is:[{}], logId is:[{}]", skuInventory, logId);
            throw new BusinessException("uuid创建失败");
        }
        skuInventory.setUuid(uuid);
        skuInventory.setIsLocked(false);
        skuInventory.setOuId(checkingLine.getOuId());
        skuInventory.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_ODO);
        skuInventory.setLastModifyTime(new Date());

        return skuInventory;
    }
}
