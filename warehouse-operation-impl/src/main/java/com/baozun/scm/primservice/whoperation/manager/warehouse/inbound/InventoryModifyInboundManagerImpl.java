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
package com.baozun.scm.primservice.whoperation.manager.warehouse.inbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryModifyInboundManager")
public class InventoryModifyInboundManagerImpl extends BaseInventoryManagerImpl implements InventoryModifyInboundManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryModifyInboundManagerImpl.class);
    /**
     * @author lichuan
     * @param invCmd
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    public void inbound(InventoryCommand invCmd, Long ouId, Long userId, String logId) {
        // 创po单
        
        // 创asn单

        // 入库

        // 上架

        // 记录库存日志

    }
}
