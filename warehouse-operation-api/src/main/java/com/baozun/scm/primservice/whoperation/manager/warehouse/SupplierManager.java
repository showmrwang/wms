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

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.SupplierCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Supplier;

public interface SupplierManager extends BaseManager {

    /**
     * 根据Id获取供应商
     * 
     * @param id
     * @return
     */
    Supplier getSupplierById(Long id);

    /**
     * 新增或者保存供应商信息
     * 
     * @param Supplier
     * @param userId
     * @return
     */
    Supplier saveOrUpdate(Supplier supplier, Long userId);

    /**
     * 通过参数查询供应商分页列表
     * 
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<SupplierCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 单个供应商编辑是否有效
     * 
     * @param userId
     * @param id
     * @param lifecycle
     */
    void updateSupplierType(Long userId, Long id, Integer lifecycle);

    /**
     * 批量编辑供应商是否有效
     * 
     * @param ids
     * @param lifeCycle
     * @param userid
     * @return
     */
    int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid);

    /**
     * @author yimin.lu
     * @param supplier
     * @return
     */
    List<Supplier> findListByParam(Supplier supplier);

    /**
     * @author yimin.lu
     * @param supplier
     * @return
     */
    List<Supplier> findListByLifecycle(Integer lifecycle);

    /**
     * 根据id集合获取名称集合
     * 
     * @author yimin.lu 2016/1/19
     * @param ids
     * @return
     */
    Map<Long, String> getNameIdMapByIds(List<Long> ids);

    /**
     * 通过供应商名称和编号检验供应商是否存在
     * 
     * @param supplier
     * @param ouId
     * @return
     */
    Boolean uniqueCodeOrName(Supplier supplier);

    /**
     * 获取供应商list
     * 
     * @param ids
     * @return
     */
    List<Supplier> findSupplierListByIds(List<Long> ids);

    /**
     * 全局日志表插入,globalSource
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
     * 查询权限下，所有启用的供应商
     * @param customerList
     * @param lifecycle
     * @return
     */
    public List<Supplier> getSupplierByIds(List<Long> customerList,Integer lifecycle);
    
    
}
