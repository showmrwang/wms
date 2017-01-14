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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

public interface WhSeedingCollectionDao extends BaseDao<WhSeedingCollection, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhSeedingCollection> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhSeedingCollection o);

    /**
     * [业务方法] 计算播种墙已由容器数量
     * @param seedingwallCode
     * @param batch
     * @param ouId
     */
    Integer countCapacityByParamExt(WhSeedingCollectionCommand whSeedingCollectionCommand);

    /**
     * [通用方法] 通过id和ouId获取集货信息
     * @param id
     * @param ouId
     * @return
     */
    WhSeedingCollection findByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [通用方法] 保存集货信息表
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(WhSeedingCollection o);

	Integer getSeedingNumFromFacility(@Param("fid") Long fid, @Param("batch") String batch, @Param("ouId") Long ouId);

	WhSeedingCollectionCommand checkContainerCodeInSeedingCollection(@Param("containerCode") String containerCode, @Param("ouId") Long ouId);

	int checkBatchIsAllIntoSeedingWall(@Param("batch") String batch, @Param("ouId") Long ouId);

	int updateContainerToSeedingWall(@Param("facilityId") Long facilityId, @Param("containerId") Long containerId, @Param("batch") String batch, @Param("ouId") Long ouId);

	int deleteContainerInSeedingWall(@Param("containerId") Long containerId, @Param("batch") String batch, @Param("ouId") Long ouId);

}
