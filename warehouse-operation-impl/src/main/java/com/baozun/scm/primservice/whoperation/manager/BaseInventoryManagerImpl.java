/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.BaseInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

/**
 * @author lichuan
 *
 */
public abstract class BaseInventoryManagerImpl extends BaseManagerImpl implements BaseInventoryManager {
    // log不支持继承
    private static final Logger log = LoggerFactory.getLogger(BaseInventoryManagerImpl.class);
    @Autowired
    private WhSkuInventoryDao inventoryDao;
    
    
    /**
     * 根据调整查询所有可用库存明细
     * @author lichuan
     * @param skuId
     * @param uuid
     * @param ouId
     * @param expectQty
     * @param logId
     * @return
     */
    public List<WhSkuInventoryCommand> findAllValidInventoryBySkuAndUuid(Long skuId, String uuid, Long ouId, Double expectQty, String logId){
        if(log.isInfoEnabled()){
            log.info("baseInventoryManager.findAllValidInventoryBySkuAndUuid start, logId is:[{}]", logId);
        }
        List<WhSkuInventoryCommand> list = null;
        list = inventoryDao.findAllValidInventoryBySkuAndUuid(skuId, uuid, ouId, expectQty);
        if(log.isInfoEnabled()){
            log.info("baseInventoryManager.findAllValidInventoryBySkuAndUuid end, logId is:[{}]", logId);
        }
        return list;
    }
    
    /**
     * 插入新的库存份
     * @author lichuan
     * @param invCmd
     * @param qty
     */
    public void insertShareInventory(WhSkuInventoryCommand invCmd, Double qty, String logId){
        if(log.isInfoEnabled()){
            log.info("baseInventoryManager.insertShareInventory start, logId is:[{}]", logId);
        }
        WhSkuInventory inv = new WhSkuInventory();
        inv.setSkuId(invCmd.getSkuId());
        inv.setLocationId(invCmd.getLocationId());
        inv.setOuterContainerId(invCmd.getOuterContainerId());
        inv.setInsideContainerId(invCmd.getInsideContainerId());
        inv.setCustomerId(invCmd.getCustomerId());
        inv.setStoreId(invCmd.getStoreId());
        inv.setOccupationCode(null);
        inv.setOnHandQty(qty);
        inv.setAllocatedQty(invCmd.getAllocatedQty());
        inv.setToBeFilledQty(invCmd.getToBeFilledQty());
        inv.setFrozenQty(invCmd.getFrozenQty());
        inv.setInvStatus(invCmd.getInvStatus());
        inv.setInvType(invCmd.getInvType());
        inv.setBatchNumber(invCmd.getBatchNumber());
        inv.setMfgDate(invCmd.getMfgDate());
        inv.setExpDate(invCmd.getExpDate());
        inv.setCountryOfOrigin(invCmd.getCountryOfOrigin());
        inv.setInvAttr1(invCmd.getInvAttr1());
        inv.setInvAttr2(invCmd.getInvAttr2());
        inv.setInvAttr3(invCmd.getInvAttr3());
        inv.setInvAttr4(invCmd.getInvAttr4());
        inv.setInvAttr5(invCmd.getInvAttr5());
        inv.setUuid(invCmd.getUuid());
        inv.setIsLocked(invCmd.getIsLocked());
        inv.setOuId(invCmd.getOuId());
        inv.setOccupationCodeSource(invCmd.getOccupationCodeSource());
        inventoryDao.insert(inv);
        if(log.isInfoEnabled()){
            log.info("baseInventoryManager.insertShareInventory end, logId is:[{}]", logId);
        }
    }
    
    /**
     * 删除库存记录日志
     * @author lichuan
     * @param occupyCode
     * @param logId
     */
    public void removeInventoryAndLog(String occupyCode, String logId){
        
    }
    
    /**
     * 释放库存
     * @author lichuan
     * @param occupyCode
     * @param logId
     */
    public void releaseOccupiedInventory(String occupyCode, String logId){
        if(log.isInfoEnabled()){
            log.info("baseInventoryManager.releaseOccupiedInventory start, logId is:[{}]", logId);
        }
        long count = inventoryDao.releaseOccupiedInventory(occupyCode);
        if(0 == count){
            
        }
        if(log.isInfoEnabled()){
            log.info("baseInventoryManager.releaseOccupiedInventory end, logId is:[{}]", logId);
        }
    }
    
}
