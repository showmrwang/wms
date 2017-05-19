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

import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;


public interface StoreDefectTypeManager extends BaseManager {


    /**
     * 通过店铺残次类型名称和编号检验是否存在
     * 
     * @param StoreDefectType
     * @return
     */
    Boolean uniqueCodeOrName(StoreDefectType storeDefectType);

    /**
     * 新增或者保存店铺残次类型信息
     * 
     * @param storeDefectType
     * @param userId
     * @return
     */
    StoreDefectType saveOrUpdate(StoreDefectType storeDefectType, Long userId);

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
     * 查询店铺残次类型列表
     * 
     * @param storeDefectType
     * @return
     */
    public List<StoreDefectTypeCommand> findStoreDefectTypeByParam(StoreDefectType storeDefectType);

    /**
     * 
     * 根据店铺ID查询对应残次类型
     * 
     * @return
     */
    List<StoreDefectTypeCommand> findStoreDefectTypesByStoreId(Long storeid);

    /**
     * 通过店铺Id查询残次类型Ids
     * 
     * @param storeId
     * @return
     */
    public List<Long> findStoreDefectTypeIdsByStoreId(Long storeId);

}
