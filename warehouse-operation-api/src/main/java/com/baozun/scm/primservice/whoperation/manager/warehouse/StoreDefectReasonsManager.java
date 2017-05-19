package com.baozun.scm.primservice.whoperation.manager.warehouse;

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


import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;


public interface StoreDefectReasonsManager extends BaseManager {

    /**
     * 通过店铺残次原因名称和编号检验是否存在
     * 
     * @param storeDefectReasons
     * @return
     */
    Boolean uniqueCodeOrName(StoreDefectReasons storeDefectReasons);

    /**
     * 新增或者保存店铺残次原因信息
     * 
     * @param storeDefectType
     * @param userId
     * @return
     */
    StoreDefectReasons saveOrUpdate(StoreDefectReasons storeDefectReasons, Long userId);

    /**
     * 全局日志表插入,infoSource
     * 
     * @param userId
     * @param modifyTime
     * @param objectType
     * @param modifiedValues
     * @param type
     * @param ouId
     */
    void insertGlobalLog(Long userId, Date modifyTime, String objectType, String modifiedValues, String type, Long ouId);

    /**
     * 查询店铺残次原因列表
     * 
     * @param storeDefectType
     * @return
     */
    public List<StoreDefectReasonsCommand> findStoreDefectReasonsByDefectTypeIds(List<Long> storeDefectTypeIds);
}
