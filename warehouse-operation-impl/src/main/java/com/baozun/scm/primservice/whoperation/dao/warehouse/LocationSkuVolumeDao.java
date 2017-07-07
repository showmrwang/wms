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

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.warehouse.LocationSkuVolume;


public interface LocationSkuVolumeDao extends BaseDao<LocationSkuVolume, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<LocationSkuVolume> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<LocationSkuVolume> query(Page page, Sort[] sorts, QueryCondition cond);

    List<LocationSkuVolume> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(LocationSkuVolume o);

    List<Long> findLocationIdsByfacilityId(@Param("facilityId") Long facilityId, @Param("ouId") Long ouId);

    List<LocationSkuVolume> findListBylocationId(@Param("locationId") Long locationId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过商品和库位查找商品库位信息
     * 
     * @param skuId
     * @param locationId
     * @param ouId
     * @return
     */
    List<LocationSkuVolume> findBySkuIdAndFacilityId(@Param("skuId") Long skuId, @Param("facilityId") Long facilityId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过复核台组查找商品库位信息
     * @param skuId
     * @param facilityId
     * @param ouId
     * @return
     */
    List<LocationSkuVolume> findFromGroupBySkuIdAndFacilityId(@Param("skuId") Long skuId, @Param("facilityId") Long facilityId, @Param("ouId") Long ouId);
}
