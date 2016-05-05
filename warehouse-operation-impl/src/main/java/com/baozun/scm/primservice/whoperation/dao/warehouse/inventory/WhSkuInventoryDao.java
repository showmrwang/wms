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
package com.baozun.scm.primservice.whoperation.dao.warehouse.inventory;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventoryCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhSkuInventoryDao extends BaseDao<WhSkuInventory, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhSkuInventory> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhSkuInventory> query(Page page, Sort[] sorts, QueryCondition cond);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<InventoryCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    List<WhSkuInventory> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhSkuInventory o);

    /**
     * 根据库存ID+UUID+OUID查询对应库存
     */
    InventoryCommand findWhSkuInventoryByIdAndUuid(@Param("id") Long id, @Param("uuid") String uuid, @Param("ouid") Long ouid);

}
