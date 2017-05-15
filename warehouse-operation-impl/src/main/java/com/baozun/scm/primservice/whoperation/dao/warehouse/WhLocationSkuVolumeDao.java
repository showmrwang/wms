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
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhLocationSkuVolumeCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhLocationSkuVolume;



public interface WhLocationSkuVolumeDao extends BaseDao<WhLocationSkuVolume, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhLocationSkuVolume> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhLocationSkuVolume o);

    @CommonQuery
    int saveOrUpdateByVersion(WhLocationSkuVolume o);

    /**
     * 根据复核台ID查找库位商品容量信息
     *
     * @author mingwei.xie
     * @param facilityId
     * @param ouId
     * @return
     */
    List<WhLocationSkuVolumeCommand> findLocSkuVolumeByFacilityId(@Param("facilityId") Long facilityId, @Param("ouId") Long ouId);

    /**
     * 查找商品对应的库位容量信息
     *
     * @param skuId
     * @param ouId
     * @return
     */
    List<WhLocationSkuVolumeCommand> findFacilityLocSkuVolumeBySkuId(@Param("facilityId") Long facilityId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);


}
