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

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("checkingManager")
@Transactional
public class CheckingManagerImpl extends BaseManagerImpl implements CheckingManager {
    
    public static final Logger log = LoggerFactory.getLogger(CheckingManagerImpl.class);
    
    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;

    @Override
    public void printPackingList(Long userId, Long ouId) {
        // 打印装箱清单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            // 需要明天确认            
            List<Long> facilityIdsList = Arrays.asList();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_16, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSalesList(Long userId, Long ouId) {
        // 打印销售清单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            // 需要明天确认            
            List<Long> facilityIdsList = Arrays.asList();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_13, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSinglePlane(Long userId, Long ouId) {
        // 打印面单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            // 需要明天确认            
            List<Long> facilityIdsList = Arrays.asList();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_15, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBoxLabel(Long userId, Long ouId) {
        // 打印箱标签
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            // 需要明天确认            
            List<Long> facilityIdsList = Arrays.asList();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_1, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printInvoiceReview(Long userId, Long ouId) {
        // 打印发票（复核）
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            // 需要明天确认            
            List<Long> facilityIdsList = Arrays.asList();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_14, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
