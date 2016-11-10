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

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuLocation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;



public interface WorkTypeDao extends BaseDao<WorkType, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WorkType> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WorkType o);

    @CommonQuery
    int saveOrUpdateByVersion(WorkType o);
    
    WorkType findWorkTypeByworkCategory(@Param("workCategory") String workCategory, @Param("ouId") Long ouId);
}
