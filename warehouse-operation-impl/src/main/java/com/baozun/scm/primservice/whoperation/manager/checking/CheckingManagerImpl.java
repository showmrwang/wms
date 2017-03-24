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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("checkingManager")
@Transactional
public class CheckingManagerImpl extends BaseManagerImpl implements CheckingManager {
    
    public static final Logger log = LoggerFactory.getLogger(CheckingManagerImpl.class);
    
    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhSkuManager whSkuManager;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Override
    public void printPackingList(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印装箱清单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_16, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSalesList(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印销售清单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_13, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSinglePlane(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印面单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_15, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBoxLabel(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印箱标签
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_1, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printInvoiceReview(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印发票（复核）
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_14, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成出库箱库存
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    @Override
    public void createOutboundboxInventory(WhCheckingCommand checkingCommand, WhCheckingLineCommand checkingLineCommand, WhSkuInventory whSkuInventory) {
        WhSkuInventoryCommand whSkuInventoryCommand = new WhSkuInventoryCommand();
        /** 商品ID */
        whSkuInventoryCommand.setSkuId(whSkuInventory.getSkuId());
        /** 库位ID 库位号 */
        whSkuInventoryCommand.setLocationId(whSkuInventory.getLocationId());
        /** 暂存库位ID */
        whSkuInventoryCommand.setTemporaryLocationId(whSkuInventory.getTemporaryLocationId());
        /** 外部容器ID 托盘 货箱 */
        whSkuInventoryCommand.setOuterContainerId(null);
        /** 内部容器ID 托盘 货箱 */
        whSkuInventoryCommand.setInsideContainerId(null);
        /** 客户ID */
        whSkuInventoryCommand.setCustomerId(whSkuInventory.getCustomerId());
        /** 店铺ID */
        whSkuInventoryCommand.setStoreId(whSkuInventory.getStoreId());
        /** 占用单据号 */
        whSkuInventoryCommand.setOccupationCode(whSkuInventory.getOccupationCode());
        /** 占用单据明细行ID */
        whSkuInventoryCommand.setOnHandQty(null);
        /** 播种墙编码 */
        whSkuInventoryCommand.setSeedingWallCode(null);
        /**货格号*/
        whSkuInventoryCommand.setContainerLatticeNo(null);
        /**出库箱号*/
        whSkuInventoryCommand.setOutboundboxCode(checkingCommand.getOutboundboxCode());
        /** 在库可用库存 */
        whSkuInventoryCommand.setOnHandQty(whSkuInventory.getOnHandQty());
        /** 已分配库存 */
        whSkuInventoryCommand.setAllocatedQty(null);
        /** 待移入库存 */
        whSkuInventoryCommand.setToBeFilledQty(null);
        /** 冻结库存 */
        whSkuInventoryCommand.setFrozenQty(whSkuInventory.getFrozenQty());
        /** 库存状态 */
        whSkuInventoryCommand.setInvStatus(whSkuInventory.getInvStatus());
        /** 库存类型 */
        whSkuInventoryCommand.setInvType(whSkuInventory.getInvType());
        /** 批次号 */
        whSkuInventoryCommand.setBatchNumber(whSkuInventory.getBatchNumber());
        /** 生产日期 */
        whSkuInventoryCommand.setMfgDate(checkingLineCommand.getMfgDate());
        /** 失效日期 */
        whSkuInventoryCommand.setExpDate(checkingLineCommand.getExpDate());
        /** 原产地 */
        whSkuInventoryCommand.setCountryOfOrigin(checkingLineCommand.getCountryOfOrigin());
        /** 库存属性1 */
        whSkuInventoryCommand.setInvAttr1(checkingLineCommand.getInvAttr1());
        /** 库存属性2 */
        whSkuInventoryCommand.setInvAttr2(checkingLineCommand.getInvAttr2());
        /** 库存属性3 */
        whSkuInventoryCommand.setInvAttr3(checkingLineCommand.getInvAttr3());
        /** 库存属性4 */
        whSkuInventoryCommand.setInvAttr4(checkingLineCommand.getInvAttr4());
        /** 库存属性5 */
        whSkuInventoryCommand.setInvAttr5(checkingLineCommand.getInvAttr5());
        /** 内部对接码 */
        try {
            WhSkuInventory inv = new WhSkuInventory();
            //复制数据        
            BeanUtils.copyProperties(whSkuInventoryCommand, inv);
            whSkuInventoryCommand.setUuid(SkuInventoryUuid.invUuid(inv));
        } catch (Exception e) {
            log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
        }
        /** 是否可用 */
        whSkuInventoryCommand.setIsLocked(whSkuInventory.getIsLocked());
        /** 对应仓库ID */
        whSkuInventoryCommand.setOuId(whSkuInventory.getOuId());
        /** 占用单据号来源 */
        whSkuInventoryCommand.setOccupationCodeSource(whSkuInventory.getOccupationCodeSource());
        /** 入库时间 */
        whSkuInventoryCommand.setInboundTime(whSkuInventory.getInboundTime());
        /** 最后操作时间 */
        whSkuInventoryCommand.setLastModifyTime(new Date());
        whSkuInventoryManager.saveOrUpdate(whSkuInventoryCommand);
    }

    @Override
    public WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId) {
        
        return whOutboundFacilityDao.findByIdExt(id, ouId);
    }
}
