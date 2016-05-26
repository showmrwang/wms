/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.warehouse.outbound;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.InventoryOccupyManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.InventoryValidateManager;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryModifyOutboundManager")
public class InventoryModifyOutboundManagerImpl extends BaseInventoryManagerImpl implements InventoryModifyOutboundManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryModifyOutboundManagerImpl.class);
    @Autowired
    private InventoryOccupyManager inventoryOccupyManager;
    @Autowired
    private InventoryValidateManager inventoryValidateManager;
    /**
     * @author lichuan
     * @param uuid
     * @param skuId
     * @param userId
     * @param logId
     */
    @Override
    public void outbound(InventoryCommand invCmd, Long ouId, Long userId, String logId) {
        //创出库单
        String orderCode = "O" + new Date().getTime();//虚拟出库单
        //查询库存明细
        List<WhSkuInventoryCommand> invs = new ArrayList<WhSkuInventoryCommand>();
        Long skuId = invCmd.getSkuId();
        String uuid = invCmd.getUuid();
        Double expectQty = invCmd.getModifyQty();
        invs = findAllValidInventoryBySkuAndUuid(skuId, uuid, ouId, expectQty, logId);
        //库存占用
        inventoryOccupyManager.simpleOccupy(invs, orderCode, logId);
        inventoryValidateManager.validateOccupyByExpectQty(orderCode, expectQty);
        //执行出库
        exeOutbound(invCmd, orderCode, logId);
    }
    
    private void exeOutbound(InventoryCommand invCmd, String occupyCode, String logId){
        //删除库存记录日志
        removeInventoryAndLog(occupyCode, logId);
    }
}
