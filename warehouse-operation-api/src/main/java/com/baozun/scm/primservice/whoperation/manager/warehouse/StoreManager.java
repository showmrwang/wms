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

import com.baozun.scm.primservice.whoperation.command.warehouse.StoreCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;

public interface StoreManager extends BaseManager {
    /**
     * 通过参数查询店铺分页列表
     * 
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<StoreCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 根据Id获取店铺
     * 
     * @param id
     * @return
     */
    Store getStoreById(Long id);

    /**
     * 新增或者保存店铺信息
     * 
     * @param container
     * @param userId
     * @return
     */
    Store saveOrUpdate(Store store, Long userId);


    /**
     * 批量编辑店铺是否有效
     * 
     * @param ids
     * @param lifeCycle
     * @param userid
     * @return
     */
    int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid);

    List<Store> findStoreListByCustomerId(Long customerId);

    /**
     * 2016/1/12 根据用户Id和店铺Id查找店铺
     * 
     * @author yimin.lu
     * @param customerId
     * @param userId
     * @return
     */
    List<Store> findStoreListByUserIdCustomerId(Long customerId, Long userId);

    List<StoreCommand> findDataPrivilegeListByParams(Long userId);

    /**
     * 根据id集合获取名称集合
     * 
     * @author yimin.lu 2016/1/19
     * @param ids
     * @return
     */
    Map<Long, String> getNameIdMapByIds(List<Long> ids);

    /**
     * 通过店铺名称和编号检验店铺是否存在
     * 
     * @param container2ndCategory
     * @param ouId
     * @return
     */
    Boolean uniqueCodeOrName(Store store);

    /**
     * 获取店铺list
     * 
     * @param ids
     * @param lifeCycle
     * @param userId
     * @return
     */
    List<Store> findStoreListByIds(List<Long> ids);

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
     * 获取店铺
     * 
     * @return
     */
    Store findStoreById(Long id);

    /**
     * 获取店铺
     * 
     * @param storeCode
     * @return
     */
    Store findStoreByCode(String storeCode);

    /**
     * 校验用户是否有此店铺和客户的权限
     * 
     * @param customerId
     * @param storeId
     * @param userId
     * @return
     */
    public Boolean checkCustomerStoreUser(Long customerId, Long storeId, Long userId);


}
