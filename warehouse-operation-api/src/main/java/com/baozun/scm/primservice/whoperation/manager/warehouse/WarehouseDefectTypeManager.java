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
package com.baozun.scm.primservice.whoperation.manager.warehouse;
import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


/**
 * @author lichuan
 * 
 */
public interface WarehouseDefectTypeManager extends BaseManager {
    
    /**
     * 通过识别参数查询仓库残次信息
     * @author lichuan
     * @param params
     * @return
     */
    WarehouseDefectTypeCommand findDefectTypeByIdParams(Map<String, Object> params);
    
    /**
     * 唯一校验
     * 
     * @author lichuan
     * @param wdtCmd
     * @param ouId
     * @return
     */
    boolean checkUnique(WarehouseDefectTypeCommand wdtCmd);
    
    /**
     * 新增或更新仓库残次信息
     * @author lichuan
     * @param wdtCmd
     * @param ouId
     * @param userId
     */
    void saveOrUpdate(WarehouseDefectTypeCommand wdtCmd, Long ouId, Long userId, String logId);
    
    /**
     * 更新仓库残次类型生命周期      
     * @author lichuan
     * @param wdtCmd
     * @param ouId
     * @param userId
     * @param logId
     */
    void updateDefectTypeLifecycle(WarehouseDefectTypeCommand wdtCmd, Long ouId, Long userId, String logId);
    
    /**
     * 通过参数查询仓库残次类型分页列表
     * 
     * @author lichuan
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<WarehouseDefectTypeCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 通过OUID查询对应仓库可用残次类型
     * 
     * @return
     */
    List<WarehouseDefectTypeCommand> findWarehouseDefectTypeByOuId(Long ouid, Integer lifecycle);


}
