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
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.StoreCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;




public interface StoreDao extends BaseDao<Store, Long> {

    /**
     * 通过参数查询店铺分页列表
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<StoreCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    @CommonQuery
    int saveOrUpdate(Store o);

    @CommonQuery
    int saveOrUpdateByVersion(Store o);

    /**
     * 批量编辑店铺是否有效
     * 
     * @param ids
     * @param lifecycle
     * @param userid
     * @return
     */
    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("lastModifyTime") Date lastModifyTime);

    /**
     * 
     * @param customerId
     * @param userId
     * @param lifecycle
     */
    List<Store> findStoreListByParams(StoreCommand command);

    List<StoreCommand> findDataPrivilegeListByParams(@Param("userId") Long userId);

    List<Store> getByIds(@Param("list") List<Long> ids);

    /**
     * 通过店铺名称和编号检验店铺是否存在
     * 
     * @param store
     * @return
     */
    long uniqueCodeOrName(Store store);
}
