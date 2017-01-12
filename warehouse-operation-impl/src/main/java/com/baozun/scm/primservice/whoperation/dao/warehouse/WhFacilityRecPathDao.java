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

import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;

public interface WhFacilityRecPathDao extends BaseDao<WhFacilityRecPath, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhFacilityRecPath> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhFacilityRecPath> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhFacilityRecPath> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhFacilityRecPath o);
    
    /**
     * 查询容器是否有推荐结果
     * @param containerCode
     * @param batch 
     * @param ouId
     * @return
     */
	WhFacilityRecPath getRecommendResultByContainerCode(@Param("containerCode") String containerCode, @Param("batch") String batch, @Param("ouId") Long ouId);
	
	/**
	 * 根据批次查询暂存库位编码
	 * @param batch
	 * @param ouId
	 * @return
	 */
	String findTemporaryStorageLocationCodeByBatch(@Param("batch") String batch, @Param("ouId") Long ouId);

    /**
     * [业务方法] 根据批次号和容器号查找推荐路径
     * @param batch
     * @param containerCode
     * @param ouId
     * @return
     */
    WhFacilityRecPath findWhFacilityRecPathByBatchAndContainer(String batch, String containerCode, Long ouId);

}
