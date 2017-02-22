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
package com.baozun.scm.primservice.whoperation.dao.odo.wave;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

public interface WhWaveLineDao extends BaseDao<WhWaveLine, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhWaveLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WaveLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhWaveLine o);

    @CommonQuery
    int saveOrUpdateByVersion(WhWaveLine o);

    /**
     * 得到所有硬阶段阶段需要分配规则或分配的波次名次行Id
     * @param allocatePhase 0:需分配规则  1:需硬分配
     * @param ouId
     * @return
     */
    List<WhWaveLine> getNeedAllocationRuleWhWaveLine(@Param("allocatePhase") Integer allocatePhase, @Param("ouId") Long ouId);

    /**
     * [业务方法] 软分配-获取波次明细列表
     * @param allocatePhase
     * @param ouId
     * @param waveStatus
     * @param lifecycle
     * @return
     */
    List<WhWaveLine> findSoftAllocationWhWaveLine(@Param("waveId") Long waveId, @Param("ouId") Long ouId, @Param("lifecycle") Integer lifecycle);

    /**
     * [业务方法] 软分配-剔除波次明细行
     * @param waveId
     * @param odoId
     * @param ouId
     */
    int removeWaveLineWhole(@Param("waveId") Long waveId, @Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 软分配-换区波次明细和商品关联列表
     * @param waveId
     * @param ouId
     * @param waveStatus
     * @param lifecycle
     * @return
     */
    List<SoftAllocationCommand> findWaveLineCommandByWaveIdAndStatus(@Param("waveId") Long waveId, @Param("ouId") Long ouId, @Param("lifecycle") Integer lifecycle);

    /**
     * [业务方法] 硬分配-添加规则Id到一批波次明细行
     * @param whWaveLine
     * @param ruleId
     * @param ouId
     * @return
     */
    int modifyRuleIntoWhWaveLine(@Param("whWaveLine") List<Long> whWaveLine, @Param("ruleId") Long ruleId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 硬分配-得到所有硬阶段阶段需要分配规则的出库单明细Id
     */
    List<Long> getOdoLinesByWaveIdList(@Param("waveIdList") List<Long> waveIdList, @Param("ouId") Long ouId);

    /**
     * [业务方法] 硬分配-得到所有硬阶段阶段需要分配库存的波次明细Id
     */
    List<WhWaveLine> getWhWaveLinesByWaveIdList(@Param("waveIdList") List<Long> waveIdList, @Param("ouId") Long ouId);

    /**
     * [业务方法] 硬分配-得到所有硬阶段阶段需要分配库存的波次明细Id
     */
    List<WhWaveLineCommand> getWhWaveLineCommandByWaveId(@Param("waveId") Long waveId, @Param("ouId") Long ouId);

    /**
     * [通用方法] 硬分配-根据id得到波次明细
     * @param id
     * @param ouId
     * @return
     */
    WhWaveLine findWhWaveLineById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法] 硬分配-得到波次明细没有分配好足够的库存的明细行
     */
    List<WhWaveLine> findNotEnoughAllocationQty(@Param("waveId") Long waveIdList, @Param("ouId") Long ouId);

    /**
     * [通用方法] 硬分配-根据waveid得到此波次包含的出库单id集合
     */
    List<Long> findOdoIdByWaveId(@Param("waveId") Long waveId, @Param("ouId") Long ouId);

    /**
     * [通用方法]删除波次明细
     * 
     * @param id
     * @param ouId
     */
    void deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法] 波次中合并订单-根据波次id和出库单明细行查找波次明细行
     * @param waveId
     * @param odoLineId
     * @param ouId
     */
    WhWaveLine findWaveLineByOdoLineIdAndWaveId(@Param("waveId") Long waveId, @Param("odoLineId") Long odoLineId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 波次中合并订单-查找优先级
     * @param waveId
     * @param odoIds
     * @param ouId
     * @return
     */
    WhWaveLine findHighestPriorityByOdoIds(@Param("waveId") Long waveId, @Param("odoIds") String odoIds, @Param("ouId") Long ouId);

    /**
     * 批量插入
     * 
     * @param waveLineList
     */
    int batchInsert(@Param("list") List<WhWaveLine> waveLineList);

    /**
     * 根据波次Id获取出库单Id
     *
     * @param waveId
     * @param ouId
     * @return
     */
    List<Long> getOdoIdListByWaveId(@Param("waveId") Long waveId, @Param("ouId") Long ouId);

    /**
     * 根据波次Id获取出库单Id
     *
     * @param waveIdList
     * @param ouId
     * @return
     */
    List<Long> getOdoIdListByWaveIdList(@Param("waveIdList") List<Long> waveIdList, @Param("ouId") Long ouId);

}
