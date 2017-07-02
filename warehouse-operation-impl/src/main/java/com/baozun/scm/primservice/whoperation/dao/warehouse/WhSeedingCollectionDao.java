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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

public interface WhSeedingCollectionDao extends BaseDao<WhSeedingCollection, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhSeedingCollection> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhSeedingCollection o);

    /**
     * [业务方法] 计算播种墙已由容器数量
     * 
     * @param seedingwallCode
     * @param batch
     * @param ouId
     */
    Integer countCapacityByParamExt(WhSeedingCollectionCommand whSeedingCollectionCommand);

    /**
     * [通用方法] 通过id和ouId获取集货信息
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhSeedingCollection findByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [通用方法] 保存集货信息表
     * 
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(WhSeedingCollection o);

    /**
     * [通用方法] 找到当前工作下所有未被推荐的容器
     * 
     * @param containerIdList
     * @param ouId
     * @return
     */
    List<Long> findNoRecByContainerList(@Param("containerIdList") List<Long> containerIdList, @Param("ouId") Long ouId);

    Integer getSeedingNumFromFacility(@Param("fid") Long fid, @Param("batch") String batch, @Param("ouId") Long ouId);

    /**
     * 通过容器号获取它的集货中状态
     * 
     * @param containerCode
     * @param ouId
     * @return
     */
    WhSeedingCollectionCommand getSeedingCollectionByContainerCode(@Param("containerCode") String containerCode, @Param("ouId") Long ouId);

    int checkBatchIsAllIntoSeedingWall(@Param("batch") String batch, @Param("ouId") Long ouId);

    int updateContainerToSeedingWall(@Param("facilityId") Long facilityId, @Param("containerId") Long containerId, @Param("batch") String batch, @Param("ouId") Long ouId);

    int deleteContainerInSeedingWall(@Param("containerId") Long containerId, @Param("batch") String batch, @Param("ouId") Long ouId);

    /**
     * [通用方法] 通过id删除集货表
     */
    int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    int checkCountInDestination(@Param("batch") String batch, @Param("destinationType") Integer destinationType, @Param("ouId") Long ouId);

    /**
     * 获取播种墙集货信息
     *
     * @param facilityId
     * @param ouId
     * @return
     */
    public List<WhSeedingCollectionCommand> getSeedingCollectionByFacilityId(@Param("facilityId") Long facilityId, @Param("ouId") Long ouId);


    public List<WhSeedingCollection> findSeedingCollection(@Param("facilityId") Long facilityId, @Param("collectionStatus") List<Integer> collectionStatus, @Param("ouId") Long ouId);

    /**
     * 获取播种墙绑定的批次
     *
     * @param facilityId
     * @param ouId
     * @param turnoverBoxCode
     * @return
     */
    public List<String> getFacilityBindBatch(@Param("facilityId") Long facilityId, @Param("ouId") Long ouId);

    public WhSeedingCollection findSeedingCollectionByContainerId(@Param("containerId") Long containerId, @Param("batch") String batch, @Param("ouId") Long ouId);

    public WhSeedingCollectionCommand getSeedingCollectionByTurnoverBox(@Param("facilityId") Long facilityId, @Param("turnoverBoxCode") String turnoverBoxCode, @Param("ouId") Long ouId);

    public WhSeedingCollectionCommand getSeedingCollectionById(@Param("seedingCollectionId") Long seedingCollectionId, @Param("ouId") Long ouId);

    public WhSeedingCollection findByIdExt(@Param("seedingCollectionId") Long seedingCollectionId, @Param("ouId") Long ouId);

    /**
     * 获取播种批次下的出库单信息，用于和播种墙货格绑定
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhSeedingWallLattice> getSeedingBatchOdoInfo(@Param("batchNo") String batchNo, @Param("ouId") Long ouId);

    /**
     * 通过小批次号获取对应集货信息
     * 
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhSeedingCollection> findWhSeedingCollectionByBatchNo(@Param("batchNo") String batchNo, @Param("ouid") Long ouid);

    /**
     * [业务方法] 通过播种墙code查找待播种数量
     * @param seedingWallCode
     * @param ouId
     * @return
     */
    int countOccupationByFacilityCode(@Param("seedingWallCode") String seedingWallCode, @Param("ouId") Long ouId);

    int countNotHaveFacilityIdByBatch(@Param("batch") String batch, @Param("ouId") Long ouId);

    int updateFacilityByBatch(@Param("batch") String batch, @Param("facilityId") Long facilityId, @Param("ouId") Long ouId);


}
