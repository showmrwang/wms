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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryModifyOutboundManager")
public class InventoryModifyOutboundManagerImpl extends BaseManagerImpl implements InventoryModifyOutboundManager {
    
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
        
        //查询库存明细
        
        //库存占用
        
        //执行出库
        
        //记录库存日志
    }
}
