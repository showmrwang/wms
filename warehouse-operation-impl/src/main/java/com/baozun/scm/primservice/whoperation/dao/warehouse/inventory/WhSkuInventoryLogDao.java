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

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryLog;

public interface WhSkuInventoryLogDao extends BaseDao<WhSkuInventoryLog, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhSkuInventoryLog> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhSkuInventoryLog> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhSkuInventoryLog> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhSkuInventoryLog o);

    /**
     * 通过库存ID 查询出对应库存日志封装数据
     * 
     * @param skuInvId
     * @param ouid
     * @return
     */
    WhSkuInventoryLog findInventoryLogBySkuInvId(@Param("skuInvId") Long skuInvId, @Param("ouid") Long ouid);

    /**
     * 通过UUID 查询对应库存的所有库存记录
     * 
     * @param uuid
     * @param ouid
     * @return
     */
    Double sumSkuInvOnHandQty(@Param("uuid") String uuid, @Param("ouid") Long ouid);

}
