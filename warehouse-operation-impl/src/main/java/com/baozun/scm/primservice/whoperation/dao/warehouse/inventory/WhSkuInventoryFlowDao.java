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

import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryFlow;

public interface WhSkuInventoryFlowDao extends BaseDao<WhSkuInventoryFlow, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhSkuInventoryFlow> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhSkuInventoryFlow> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhSkuInventoryFlow> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhSkuInventoryFlow o);

    /**
     * 通过创建时间段+仓库ID获取对应库存流水数据
     * 
     * @return
     */
    List<WmsSkuInventoryFlow> findWmsSkuInventoryFlowByCreateTime(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("ouid") Long ouid,
            @Param("customerCode") String customerCode);

}
