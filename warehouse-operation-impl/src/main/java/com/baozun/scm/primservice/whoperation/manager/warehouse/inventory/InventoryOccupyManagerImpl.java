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
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;

import lark.common.annotation.MoreDB;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryOccupyManager")
public class InventoryOccupyManagerImpl extends BaseInventoryManagerImpl implements InventoryOccupyManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryOccupyManagerImpl.class);
    @Autowired
    private WhSkuInventoryDao inventoryDao;
    
    /**
     * @author lichuan
     * @param invCmds
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void simpleOccupy(List<InventoryCommand> invCmds, String occupyCode, String logId) {
        String occupyKey = null;
        Double eQty = null;
        for(InventoryCommand invCmd : invCmds){
            if(null == occupyKey || !occupyKey.equals(invCmd.getOccupyKey())){
                occupyKey = invCmd.getOccupyKey();
                eQty = (null == invCmd.getExpectQty() ? 0.0 : invCmd.getExpectQty());
            }
            if(0 >= eQty.compareTo(new Double("0.0"))){
                continue;
            }
            Long invId = invCmd.getId();
            if(-1 == eQty.compareTo(invCmd.getOnHandQty())){
                int occupied = inventoryDao.occupyInvByCodeAndId(eQty, occupyCode, invId);
                if(-1 == occupied){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
                insertShareInventory(invCmd, invCmd.getOnHandQty().doubleValue() - eQty.doubleValue(), logId);
                eQty = new Double("0.0");
            }else{
                int occupied = inventoryDao.occupyInvByCodeAndId(eQty, occupyCode, invId);
                if(-1 == occupied){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
                eQty = eQty.doubleValue() - invCmd.getOnHandQty().doubleValue();
            }
        }
    }

}
