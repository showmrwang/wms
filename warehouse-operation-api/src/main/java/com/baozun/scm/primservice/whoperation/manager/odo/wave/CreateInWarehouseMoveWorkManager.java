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
package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.InWarehouseMoveWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

public interface CreateInWarehouseMoveWorkManager extends BaseManager {

    /**
     * [业务方法] 波次中创建补货工作和作业
     * @param whSkuInventoryCommandLst
     * @param userId
     * @return
     */
    public InWarehouseMoveWorkCommand saveAllocatedAndTobefilled(InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand, List<WhSkuInventoryCommand> whSkuInventoryCommandLst);

    /**
     * [业务方法] 波次中创建补货工作和作业
     * @param whSkuInventoryCommandLst
     * @param userId
     * @return
     */
    public String createInWarehouseMoveWork(InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand, Long ouId, Long userId);

    /**
     * [业务方法] 库内移动工作执行
     * @param 
     * @param 
     * @return
     */
    public List<WhSkuInventorySn> executeInWarehouseMoveWork(String inWarehouseMoveWorkCode, Long ouId, Long userId, List<WhSkuInventorySn> skuInventorySnLst);

    /**
     * [业务方法] 缓存sn列表
     * @param 
     * @param 
     * @return
     */
    public String snStatisticsRedis(List<WhSkuInventorySn> skuInventorySnsLst);
    
    /**
     * [业务方法] 获取缓存sn列表
     * @param 
     * @param 
     * @return
     */
    public List<WhSkuInventorySn> getSnStatistics(String key);
    
    /**
     * [业务方法] 删除缓存sn列表
     * @param 
     * @param 
     * @return
     */
    public void delSnStatistics(String key);
}
