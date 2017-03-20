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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.constant.Constants;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhCheckingManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionOutBoundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhPrintInfoManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;


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
   
    /**
     * 根据复核打印配置打印单据
     * 
     * @author qiming.liu
     * @param ids
     * @param userId
     * @param ouId
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
                    PrintDataCommand data = new PrintDataCommand();
                    data.setIdList(idsList);
                    // 装箱清单      
                    printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_16, whCheckingResultCommand.getUserId(), ouId);
                    // 销售清单      
                    printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_13, whCheckingResultCommand.getUserId(), ouId);
                    // 面单
                    printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_15, whCheckingResultCommand.getUserId(), ouId);
                    // 箱标签
                    printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_1, whCheckingResultCommand.getUserId(), ouId);
                    // 发票（复核）
                    printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_14, whCheckingResultCommand.getUserId(), ouId);
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
     * @param ids
     * @param userId
     * @param ouId
     */
    @Override
    public Boolean updateChecking(WhCheckingResultCommand whCheckingResultCommand) {
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
        return null;
    }
    
    /**
     * 生成出库箱库存与箱数据
     * 
     * @author qiming.liu
     * @param ids
     * @param userId
     * @param ouId
     */
    @Override
    public Boolean createOutboundbox(WhCheckingResultCommand whCheckingResultCommand) {
        // 5.8.6.7生成出库箱库存与箱数据 TODO 
        // 先生成出库箱库存。
        // 删除原有的库存（小车货格库存，小车出库箱库存，播种墙货格库存，播种墙出库箱库存，周转箱库存）
        // t_wh_sku_inventory
        // t_wh_outboundbox,t_wh_outboundbox_line
        // 5.8.6.8更新出库单状态 TODO 
        // 修改出库单状态为复核完成状态。
        
        return null;
    }
    
    /**
     * 更新出库单状态
     * 
     * @author qiming.liu
     * @param ids
     * @param userId
     * @param ouId
     */
    @Override
    public Boolean updateOdoStatus(WhCheckingResultCommand whCheckingResultCommand) {
        // 5.8.6.7生成出库箱库存与箱数据 TODO 
        // 先生成出库箱库存。
        // 删除原有的库存（小车货格库存，小车出库箱库存，播种墙货格库存，播种墙出库箱库存，周转箱库存）
        // t_wh_sku_inventory
        // t_wh_outboundbox,t_wh_outboundbox_line
        // 5.8.6.8更新出库单状态 TODO 
        // 修改出库单状态为复核完成状态。
        
        return null;
    }

    /**
     * 算包裹计重
     * 
     * @author qiming.liu
     * @param ids
     * @param userId
     * @param ouId
     */
    @Override
    public Boolean packageWeightCalculation(WhCheckingResultCommand whCheckingResultCommand) {
        // 5.8.6.9算包裹计重  TODO 
        
        return null;
    }

}
