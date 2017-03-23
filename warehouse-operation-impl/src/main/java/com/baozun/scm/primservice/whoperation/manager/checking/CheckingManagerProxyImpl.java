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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingPrint;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionOutBoundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOdoPackageInfoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOutboundboxManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhPrintInfoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
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
    private WhOutboundboxLineManager whOutboundboxLineManager;
    @Autowired
    private WhOutboundboxManager whOutboundboxManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private WhSkuManager whSkuManager;
    

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
                   if(null == whPrintInfoLst){
                       idsList.add(whCheckingCommand.getId());    
                   }
                }
                try {
                    if(CheckingPrint.PACKING_LIST.equals(checkingPrintArray[i])){
                        // 装箱清单
                        checkingManager.printPackingList(whCheckingResultCommand.getUserId(), ouId);    
                    }
                    if(CheckingPrint.SALES_LIST.equals(checkingPrintArray[i])){
                        // 销售清单      
                        checkingManager.printSalesList(whCheckingResultCommand.getUserId(), ouId);   
                    }
                    if(CheckingPrint.SINGLE_PLANE.equals(checkingPrintArray[i])){
                        // 面单
                        checkingManager.printSinglePlane(whCheckingResultCommand.getUserId(), ouId);    
                    }
                    if(CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])){
                        // 箱标签
                        checkingManager.printBoxLabel(whCheckingResultCommand.getUserId(), ouId);    
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
                for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                    whCheckingLineCommand.setCheckingQty(whCheckingResultCommand.getCheckingQty());  
                    whCheckingLineManager.saveOrUpdate(whCheckingLineCommand);
                }
            }
        }
        return isSuccess;
    }
    
    /**
     * 生成出库箱库存与箱数据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean createOutboundbox(WhCheckingResultCommand whCheckingResultCommand) {
        Boolean isSuccess = true;
        for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
            // 小车货格库存，小车出库箱库存，播种墙货格库存，播种墙出库箱库存，周转箱库存            
            if(null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getContainerId() || null != whCheckingCommand.getOuterContainerId() || null != whCheckingCommand.getContainerLatticeNo()){
                List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), whCheckingCommand.getOuId());
                for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                    // 生成出库箱库存
                    WhSkuInventoryCommand whSkuInventoryCommand = new WhSkuInventoryCommand();
                    // TODO--未完待续                    
                    whSkuInventoryManager.saveOrUpdate(whSkuInventoryCommand);
                    // 删除原有
                    WhSkuInventory skuInventory = new WhSkuInventory();
                    // TODO--未完待续
                    List<WhSkuInventory> whSkuInventoryLst = whSkuInventoryManager.findWhSkuInventoryByPramas(skuInventory);
                    for(WhSkuInventory whSkuInventory : whSkuInventoryLst){
                        int r =  whSkuInventoryManager.deleteSkuInventory( whSkuInventory.getId(), whSkuInventory.getOuId());
                    }
                }
            }
        }
        return isSuccess;
    }
    
    /**
     * 更新出库单状态
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
                OdoCommand odoCommand = odoManager.findOdoCommandByIdOuId(whCheckingLineCommand.getOuId(), whCheckingResultCommand.getOuId());
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
        Long ouId = whCheckingResultCommand.getOuId();
        // 查询功能是否配置复核打印单据配置        
        WhFunctionOutBound whFunctionOutBound  = wFunctionOutBoundManager.findByFunctionIdExt(whCheckingResultCommand.getFunctionId(), ouId);
        for(WhCheckingCommand whCheckingCommand : whCheckingResultCommand.getWhCheckingCommandLst()){
            List<WhCheckingLineCommand> whCheckingLineCommandLst = whCheckingLineManager.getCheckingLineByCheckingId(whCheckingCommand.getId(), ouId);
            BigDecimal calcWeight = new BigDecimal(0.00);
            for(WhCheckingLineCommand whCheckingLineCommand : whCheckingLineCommandLst){
                WhSkuCommand whSkuCommand = whSkuManager.getSkuBybarCode(whCheckingLineCommand.getSkuBarCode(), ouId);
                BigDecimal b1 = new BigDecimal(whSkuCommand.getWeight().toString());
                BigDecimal b2 = new BigDecimal(whCheckingLineCommand.getCheckingQty().toString());
                calcWeight = calcWeight.add(b1.multiply(b2));
            }
            WhOdoPackageInfoCommand whOdoPackageInfoCommand = new WhOdoPackageInfoCommand();
            whOdoPackageInfoCommand.setOdoId(whCheckingResultCommand.getOuId());
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
        
        return null;
    }

}
