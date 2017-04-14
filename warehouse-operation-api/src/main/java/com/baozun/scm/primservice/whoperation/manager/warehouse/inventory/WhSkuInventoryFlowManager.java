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
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.List;

import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;

public interface WhSkuInventoryFlowManager extends BaseManager {

    /**
     * 保存库存流水信息
     * 
     * @param log
     */
    void insertWhSkuInventoryFlow(WhSkuInventoryLog log);

    /**
     * 通过创建时间段+仓库ID获取对应库存流水数据
     * 
     * @return
     */
    List<WmsSkuInventoryFlow> findWmsSkuInventoryFlowByCreateTime(String beginTime, String endTime, Long ouid);
}
