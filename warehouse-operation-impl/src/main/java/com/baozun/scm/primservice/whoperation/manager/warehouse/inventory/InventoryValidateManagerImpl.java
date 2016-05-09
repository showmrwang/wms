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

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

import lark.common.annotation.MoreDB;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryValidateManager")
public class InventoryValidateManagerImpl extends BaseManagerImpl implements InventoryValidateManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryValidateManagerImpl.class);
    @Autowired
    private WhSkuInventoryDao inventoryDao;
    
    /**
     * 在库数量检查
     * @author lichuan
     * @param uuid
     * @param skuId
     * @param expectQty
     * @param logId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public boolean onHandQtyValidate(String uuid, Long skuId, Long ouId, Double expectQty, String logId) {
        boolean result = true;
        InventoryCommand inv = inventoryDao.findWhSkuInventoryByIdGroupByUuid(skuId, uuid, ouId);
        if (null == inv) {
            result = false;
        }
        Double onHandQty = inv.getOnHandQty();
        if (null == onHandQty || -1 == onHandQty.compareTo(expectQty)) {
            result = false;
        }
        return result;
    }
    
    /**
     * 库存占用数量校验
     * @author lichuan
     * @param occupyCode
     * @param eQty
     * @return
     */
    @Override
    public List<InventoryCommand> validateOccupyByExpectQty(String occupyCode, Double eQty) {
        // TODO Auto-generated method stub
        return null;
    }
    

}
