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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
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
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.OutboundboxStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
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
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;


@Service("checkingManagerProxy")
public class CheckingManagerProxyImpl extends BaseManagerImpl implements CheckingManagerProxy {
    
    public static final Logger log = LoggerFactory.getLogger(CheckingManagerProxyImpl.class);
    
    @Autowired
    private CheckingManager checkingManager;
    @Autowired
    private WhFunctionOutBoundManager wFunctionOutBoundManager;
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
        WhFunctionOutBound whFunctionOutBound  = wFunctionOutBoundManager.findByFunctionIdExt(whCheckingResultCommand.getFunctionId(), ouId);
        String checkingPrint = whFunctionOutBound.getCheckingPrint();
        if(null != checkingPrint && "".equals(checkingPrint)){
            String[] checkingPrintArray = checkingPrint.split(",");
            for (int i = 0; i < checkingPrintArray.length; i++) {
                List<Long> idsList = new ArrayList<Long>();
                for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
                   List<WhPrintInfo> whPrintInfoLst = whPrintInfoManager.findByOutboundboxCodeAndPrintType(whCheckingCommand.getOutboundboxCode(), checkingPrintArray[i], ouId);
                   if(null == whPrintInfoLst || 0 == whPrintInfoLst.size()){
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
                    if(CheckingPrint.PACKING_LIST.equals(checkingPrintArray[i])){
                        // 装箱清单
                        checkingManager.printPackingList(idsList, whCheckingResultCommand.getUserId(), ouId);    
                    }
                    if(CheckingPrint.SALES_LIST.equals(checkingPrintArray[i])){
                        // 销售清单      
                        checkingManager.printSalesList(idsList, whCheckingResultCommand.getUserId(), ouId);   
                    }
                    if(CheckingPrint.SINGLE_PLANE.equals(checkingPrintArray[i])){
                        // 面单
                        checkingManager.printSinglePlane(idsList, whCheckingResultCommand.getUserId(), ouId);    
                    }
                    if(CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])){
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
        for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
            if(CheckingStatus.NEW == whCheckingCommand.getStatus()){
                whCheckingCommand.setStatus(CheckingStatus.FINISH);
                whCheckingManager.saveOrUpdate(whCheckingCommand);
                List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
                for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){//疑问1
                    whCheckingLineManager.saveOrUpdate(whCheckingLineCommand);
                }
            }
        }
        return isSuccess;
    }
    
    //按单复合更新复合明细数据
    
    /**
     * 生成出库箱库存与箱数据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean createOutboundbox(WhCheckingResultCommand whCheckingResultCommand) {//疑问2
        Boolean isSuccess = true;
        for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
            List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
            // 小车货格库存，小车出库箱库存，播种墙货格库存，播种墙出库箱库存，周转箱库存            
            if(null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getContainerId() || null != whCheckingCommand.getOuterContainerId() || null != whCheckingCommand.getContainerLatticeNo()){
                for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                    // 获取查询条件                    
                    WhSkuInventory skuInventory = new WhSkuInventory();
                    skuInventory.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                    skuInventory.setInsideContainerId(whCheckingCommand.getContainerId());
                    // 根据播种墙ID获取播种墙信息 
                    if(null != whCheckingCommand.getFacilityId()){
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
                    for(WhSkuInventory whSkuInventory : whSkuInventoryLst){
                        whSkuInventoryManager.deleteSkuInventory( whSkuInventory.getId(), whSkuInventory.getOuId());
                    }
                }
            }
            this.saveWhOutboundboxCommand(whCheckingCommand);
            for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                this.saveWhOutboundboxLineCommand(whCheckingLineCommand);
                List<WhSkuInventorySnCommand> whSkuInventorySnCommandLst = whSkuInventorySnManager.findWhSkuInventoryByUuid(whCheckingLineCommand.getOuId(), whCheckingLineCommand.getUuid());
                for(WhSkuInventorySnCommand whSkuInventorySnCommand : whSkuInventorySnCommandLst){
                    whSkuInventorySnCommand.setUuid(whSkuInventorySnCommand.getUuid());
                    whSkuInventorySnManager.saveOrUpdate(whSkuInventorySnCommand);
                    this.saveWhOutboundboxLineSnCommand(whSkuInventorySnCommand, whCheckingLineCommand);
                }
            }
        }
        return isSuccess;
    }

    /**
     * 更新出库单状态(按单复合只更新一个出库单，按单复合更新多个
     * )
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean updateOdoStatus(WhCheckingResultCommand whCheckingResultCommand) {
        Boolean isSuccess = true;
        // 修改出库单状态为复核完成状态。
        for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
            List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
            for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                OdoCommand odoCommand = odoManager.findOdoCommandByIdOuId(whCheckingLineCommand.getId(), whCheckingResultCommand.getOuId());
                WhOdo whOdo = new WhOdo();
                //复制数据        
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
        WhFunctionOutBound whFunctionOutBound  = wFunctionOutBoundManager.findByFunctionIdExt(whCheckingResultCommand.getFunctionId(), ouId);
        for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
            Map<String, List<WhCheckingLineCommand>> checkingLineMap = getCheckingLineForGroup(whCheckingCommand.getId(), ouId, whCheckingCommand.getOutboundboxId());
            for(String key : checkingLineMap.keySet()){
                List<WhCheckingLineCommand> whCheckingLineCommandLst = checkingLineMap.get(key);
                BigDecimal calcWeight = new BigDecimal(0.00);
                for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
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
                whOdoPackageInfoCommand.setCalcWeight(calcWeight.doubleValue());
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
        for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
            String key = whCheckingLineCommand.getOdoId().toString() + outboundboxId.toString();
            List<WhCheckingLineCommand> checkingLineCommandLst = new ArrayList<WhCheckingLineCommand>();
            if(null != checkingLineMap.get(key)){
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
     * @param whCheckingResultCommand
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
     * @param whCheckingResultCommand
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
        whOutboundboxLineCommand.setQty(whCheckingLineCommand.getQty());
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
     * @param whCheckingResultCommand
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
     * tangming
     * 按单复合更新复合表
     * @param checkingLineList
     */
    private Long updateCheckingByOdo(List<WhCheckingLineCommand> checkingLineList,Long ouId){
        for(WhCheckingLineCommand cmd:checkingLineList){
            Long id = cmd.getId();  //复合明细id
            Long checkingQty = cmd.getCheckingQty();  //复合明细数量
            WhCheckingLineCommand lineCmd =  whCheckingLineManager.getCheckingLineById(id, ouId);
            WhCheckingLine line = new WhCheckingLine();
            BeanUtils.copyProperties(lineCmd, line);
            line.setCheckingQty(checkingQty);
            whCheckingLineManager.saveOrUpdateByVersion(line);
        }
        Long checkingId = null;
        for(WhCheckingLineCommand lineCmd:checkingLineList){
             checkingId = lineCmd.getCheckingId(); //复合头id
             break;
        }
        //更新复合头状态
        WhCheckingCommand  checking = whCheckingManager.findWhChecking(checkingId, ouId);
        if(null == checking) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        checking.setStatus(CheckingStatus.FINISH);
        whCheckingManager.saveOrUpdate(checking);
        
        return checkingId;
    }
    
    /**
     * tangming
     * 按单复合
     * @param checkingLineList
     */
    public void checkingByOdo(WhCheckingByOdoResultCommand cmd,Boolean isTabbInvTotal,Long userId,Long ouId,Long functionId){
        List<WhCheckingLineCommand> checkingLineList = cmd.getCheckingLineList();
        Long outboundboxId = cmd.getOutboundboxId();
        String outboundbox = cmd.getOutboundbox();
        //更新复合明细表
        Long checkingId = this.updateCheckingByOdo(checkingLineList, ouId);
        cmd.setOuId(ouId);
        //生成出库箱库存
        whSkuInventoryManager.addOutBoundInventory(cmd, isTabbInvTotal, userId);
        //获取出库单id 
        WhCheckingLineCommand lineCmd = checkingLineList.get(0);
        Long odoId = lineCmd.getOdoId();
//        出库箱头信息（t_wh_outboundbox）和出库箱明细
        this.addOutboundbox(checkingId, ouId, odoId);
        //生成出库箱明细
        this.addOutboundboxLine(outboundbox, ouId, lineCmd);
        //更新出库单状态
//        this.updateOdoStatusByOdo(odoId, ouId);
        //算包裹计重
        this.packageWeightCalculationByOdo(checkingLineList, functionId, ouId, outboundboxId, userId);
    }
    
    
    /**
     * 出库箱头信息
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    private void addOutboundbox(Long checkingId,Long ouId,Long odoId) {
        WhCheckingCommand checkingCmd =  whCheckingManager.findWhChecking(checkingId, ouId);
        if(null == checkingCmd){
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        
        WhOutboundboxCommand whOutboundboxCommand = new WhOutboundboxCommand();
        BeanUtils.copyProperties(checkingCmd, whOutboundboxCommand);
        whOutboundboxCommand.setOuId(ouId);
        whOutboundboxCommand.setOdoId(odoId);
        whOutboundboxManager.saveOrUpdate(whOutboundboxCommand);
        
    }
    
    /***
     * 生成出库箱明细
     * @param outboundbox
     * @param ouId
     */
    private void addOutboundboxLine(String outboundbox,Long ouId,WhCheckingLineCommand lineCmd){
        List<WhSkuInventoryCommand> list = new ArrayList<WhSkuInventoryCommand>();
        List<WhSkuInventoryCommand> listSkuInvCmd = whSkuInventoryManager.findOutboundboxInventory(outboundbox, ouId);
        if(null != listSkuInvCmd && listSkuInvCmd.size() != 0){
            //先遍历出uuid相同的记录
            for(int j=0;j<listSkuInvCmd.size()-1;j++){
                String uuid = listSkuInvCmd.get(j).getUuid();
                for(int i=j+1;i<listSkuInvCmd.size();i++){
                    String skuInvUuid = listSkuInvCmd.get(i).getUuid();
                    if(uuid.equals(skuInvUuid)) {
                        list.add(listSkuInvCmd.get(i));
                    }
                }
            }
        }
        //添加出库箱明细
        for(WhSkuInventoryCommand skuInvCmd:list){
            WhOutboundboxLineCommand outboundboxLineComd = new WhOutboundboxLineCommand();
            Long skuId = skuInvCmd.getSkuId();
            outboundboxLineComd.setWhOutboundboxId(null);
            outboundboxLineComd.setSkuCode(lineCmd.getSkuCode());
            outboundboxLineComd.setSkuExtCode(lineCmd.getSkuExtCode());
            outboundboxLineComd.setSkuBarCode(lineCmd.getSkuBarCode());
            outboundboxLineComd.setSkuName(lineCmd.getSkuName());
            outboundboxLineComd.setQty(skuInvCmd.getOnHandQty().longValue());
            outboundboxLineComd.setCustomerCode(lineCmd.getCustomerCode());
            outboundboxLineComd.setCustomerName(lineCmd.getCustomerName());
            outboundboxLineComd.setStoreCode(lineCmd.getStoreCode());
            outboundboxLineComd.setStoreName(lineCmd.getStoreName());
            outboundboxLineComd.setInvStatus(skuInvCmd.getInvStatus().toString());
            outboundboxLineComd.setInvType(skuInvCmd.getInvType());
            outboundboxLineComd.setBatchNumber(skuInvCmd.getBatchNumber());
            outboundboxLineComd.setMfgDate(skuInvCmd.getMfgDate());
            outboundboxLineComd.setExpDate(skuInvCmd.getExpDate());
            outboundboxLineComd.setCountryOfOrigin(skuInvCmd.getCountryOfOrigin());
            outboundboxLineComd.setInvAttr1(skuInvCmd.getInvAttr1());
            outboundboxLineComd.setInvAttr2(skuInvCmd.getInvAttr2());
            outboundboxLineComd.setInvAttr3(skuInvCmd.getInvAttr3());
            outboundboxLineComd.setInvAttr4(skuInvCmd.getInvAttr4());
            outboundboxLineComd.setInvAttr5(skuInvCmd.getInvAttr5());
            outboundboxLineComd.setUuid(skuInvCmd.getUuid());
            outboundboxLineComd.setOuId(skuInvCmd.getOuId());
            outboundboxLineComd.setOdoId(lineCmd.getOdoId());
            outboundboxLineComd.setOdoLineId(lineCmd.getOdoLineId());
            outboundboxLineComd.setSysDate(String.valueOf(new Date()));
            whOutboundboxLineManager.saveOrUpdate(outboundboxLineComd);
        }
     }
    
    /**
     * 更新出库单状态(按单复合只更新一个出库单)
     * 
     * 
     * @author
     * @param whCheckingResultCommand
     */
    private void updateOdoStatusByOdo(Long odoId,Long ouId) {
        // 修改出库单状态为复核完成状态。
        OdoCommand odoCommand = odoManager.findOdoCommandByIdOuId(odoId, ouId);
        WhOdo whOdo = new WhOdo();
        //复制数据        
        BeanUtils.copyProperties(odoCommand, whOdo);
        //修改出库单状态为复核完成状态。
        whOdo.setOdoStatus(OdoStatus.ODO_CHECKING_FINISH);  
        odoManager.updateByVersion(whOdo);
    }
    
    
    /**
     * 算包裹计重(按单复合)
     * 
     * @param whCheckingResultCommand
     */
    private void packageWeightCalculationByOdo(List<WhCheckingLineCommand> checkingLineList,Long functionId,Long ouId,Long outboundboxId,Long userId) {
        // 查询功能是否配置复核打印单据配置        
        WhFunctionOutBound whFunctionOutBound  = wFunctionOutBoundManager.findByFunctionIdExt(functionId, ouId);
        for(WhCheckingLineCommand whCheckingCommand :checkingLineList){
            Map<String, List<WhCheckingLineCommand>> checkingLineMap = getCheckingLineForGroup(whCheckingCommand.getId(), ouId, outboundboxId);
            for(String key : checkingLineMap.keySet()){
                List<WhCheckingLineCommand> whCheckingLineCommandLst = checkingLineMap.get(key);
                BigDecimal calcWeight = new BigDecimal(0.00);
                for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                    WhSkuCommand whSkuCommand = whSkuManager.getSkuBybarCode(whCheckingLineCommand.getSkuBarCode(), ouId);
                    BigDecimal b1 = new BigDecimal(whSkuCommand.getWeight().toString());
                    BigDecimal b2 = new BigDecimal(whCheckingLineCommand.getCheckingQty().toString());
                    calcWeight = calcWeight.add(b1.multiply(b2));
                }
                WhOdoPackageInfoCommand whOdoPackageInfoCommand = new WhOdoPackageInfoCommand();
                whOdoPackageInfoCommand.setOdoId(whCheckingLineCommandLst.get(0).getOdoId());
                whOdoPackageInfoCommand.setOutboundboxId(outboundboxId);
                whOdoPackageInfoCommand.setOutboundboxCode(whCheckingCommand.getOutboundboxCode());
                whOdoPackageInfoCommand.setStatus(1);
                whOdoPackageInfoCommand.setCalcWeight(calcWeight.doubleValue());
                whOdoPackageInfoCommand.setFloats(whFunctionOutBound.getWeightFloatPercentage());
                whOdoPackageInfoCommand.setActualWeight(null);
                whOdoPackageInfoCommand.setLifecycle(1);
                whOdoPackageInfoCommand.setCreateId(userId);
                whOdoPackageInfoCommand.setCreateTime(new Date());
                whOdoPackageInfoCommand.setLastModifyTime(new Date());
                whOdoPackageInfoCommand.setModifiedId(userId);
                whOdoPackageInfoManager.saveOrUpdate(whOdoPackageInfoCommand);
            }
        }
    }
    
}
