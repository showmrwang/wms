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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inbound.InventoryModifyInboundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.outbound.InventoryModifyOutboundManager;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;

import lark.common.annotation.MoreDB;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryModifyManager")
public class InventoryModifyManagerImpl extends BaseManagerImpl implements InventoryModifyManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryModifyManagerImpl.class);
    @Autowired
    private InventoryValidateManager inventoryValidateManager;
    @Autowired
    private InventoryModifyInboundManager inventoryModifyInboundManager;
    @Autowired
    private InventoryModifyOutboundManager inventoryModifyOutboundManager;
    
    /**
     * 调整库存属性
     * @author lichuan
     * @param invCmd
     * @param ouId
     * @param userId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void modifyInvAttr(InventoryCommand invCmd, Long ouId, Long userId, String logId) {
        String invCmdStr = ParamsUtil.bean2String(invCmd);
        if (log.isInfoEnabled()) {
            log.info("inventoryModifyManager.modifyInvAttr start, invCmd is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[]{invCmdStr, ouId, userId, logId});
        }
        //校验
        if(null == invCmd){
            log.error("invCmd is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, "invCmd");
        }
        if(null == ouId){
            log.error("ouId is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, "ouId");
        }
        if(null == userId){
            log.error("userId is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, "userId");
        }
        String uuid = invCmd.getUuid();
        Long skuId = invCmd.getSkuId();
        Double modifyQty = invCmd.getModifyQty();
        //在库数量校验
        boolean isQtyValid = inventoryValidateManager.onHandQtyValidate(uuid, skuId, modifyQty, logId);
        if (!isQtyValid) {
            log.error("inventory onHandQty is not enough, uuid is:[{}], skuId is:[{}], logId is:[{}]", new Object[] {invCmd.getUuid(), invCmd.getSkuId(), logId});
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        //调整出
        inventoryModifyOutboundManager.outbound(invCmd, ouId, userId, logId);
        //调整入
        inventoryModifyInboundManager.inbound(invCmd, ouId, userId, logId);
        if (log.isInfoEnabled()) {
            log.info("inventoryModifyManager.modifyInvAttr end, invCmd is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", new Object[]{invCmdStr, ouId, userId, logId});
        }
    }

}
