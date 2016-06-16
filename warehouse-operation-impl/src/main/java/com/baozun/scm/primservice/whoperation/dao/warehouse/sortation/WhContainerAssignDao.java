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
package com.baozun.scm.primservice.whoperation.dao.warehouse.sortation;

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

import com.baozun.scm.primservice.whoperation.model.warehouse.sortation.WhContainerAssign;

public interface WhContainerAssignDao extends BaseDao<WhContainerAssign, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhContainerAssign> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhContainerAssign> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhContainerAssign> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhContainerAssign o);

    /**
     * 删除对应入库分拣相同商品属性对应目标容器表数据
     */
    int deleteWhContainerAssign(@Param("ouid") Long ouid, @Param("containerId") Long containerId);

    /**
     * 通过商品UUID或者用户ID查询对应目标容器信息
     * 
     * @return
     */
    WhContainerAssign findWhContainerAssignByUuidOrUserId(@Param("ouid") Long ouid, @Param("uuid") String uuid, @Param("ruleid") Long ruleid, @Param("userid") Long userid);

    /**
     * 通过容器ID查询对应
     * 
     * @param ouid
     * @param containerId
     * @return
     */
    List<WhContainerAssign> findWhContainerAssignByContainerId(@Param("ouid") Long ouid, @Param("containerId") Long containerId);

}
