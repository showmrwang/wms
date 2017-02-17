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

import com.baozun.scm.primservice.whoperation.command.warehouse.WarehouseCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.auth.OperationUnit;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WarehouseMgmt;

public interface WarehouseManager extends BaseManager {

    WarehouseCommand checkNameOrCode(String name, String code);

    Warehouse saveOrUpdate(Warehouse warehouse, Long userId);

    Pagination<WarehouseCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params);

    Warehouse findWarehouseById(Long id);

    Warehouse findWarehouseByIdExt(Long id);

    void updateWhType(Long userId, Long whId, Integer lifecycle);

    int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid);

    List<Warehouse> findListByParam(Warehouse warehouse);

    List<Warehouse> findListByLifecycle(Integer lifecycle);

    OperationUnit saveOrUpdateBloc(OperationUnit operationUnit, Long userId);

    Warehouse findWarehouseByCode(String code);

    boolean syncWarehouse(OperationUnit ou);

    /**
     * [业务方法] 通过仓库id获取仓库参数
     * @param ouId
     * @return
     */
    WarehouseMgmt findWhMgmtByOuId(Long ouId);
}
