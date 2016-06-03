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
package com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;

/**
 * @author lichuan
 * 
 */
public interface WarehouseDefectReasonsDao extends BaseDao<WarehouseDefectReasons, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WarehouseDefectReasons> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WarehouseDefectReasons> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WarehouseDefectReasons> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WarehouseDefectReasons o);
    
    @CommonQuery
    int saveOrUpdateByVersion(WarehouseDefectReasons o);
    
    /**
     * 删除残次类型Id对应的所有残次原因
     * @author lichuan
     * @param typeId
     * @param ouId
     * @return
     */
    int deleteByTypeIdExt(@Param("typeId") Long typeId, @Param("ouId") Long ouId);

    /**
     * 通过残次类型ID查询对应残次原因
     * 
     * @return
     */
    List<WarehouseDefectReasonsCommand> findWarehouseDefectReasonsByTypeId(@Param("typeId") Long typeId, @Param("ouid") Long ouid);

}
