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
package com.baozun.scm.primservice.whoperation.dao.odo;

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

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

public interface WhOdoDao extends BaseDao<WhOdo, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdo> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdo> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdo o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdo o);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdo findByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    OdoCommand findCommandByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    int existsSkuInOdo(@Param("odoId") Long odoId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 合并出库单-查找可以合并的出库单号
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    String findOdoMergableIds(@Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType, @Param("store") String store,
            @Param("deliverGoodsTime") String deliverGoodsTime);

    /**
     * [业务方法] 波次中合并出库单-查找可以合并的出库单号
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    String findWaveOdoMergableIds(@Param("waveCode") String waveCode, @Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType, @Param("store") String store,
            @Param("deliverGoodsTime") String deliverGoodsTime);

    /**
     * [业务方法] 合并订单-合并出库单
     * @param odoIdString
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    List<OdoMergeCommand> odoMerge(@Param("odoIdString") String odoIdString, @Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType,
            @Param("store") String store, @Param("deliverGoodsTime") String deliverGoodsTime);

    /**
     * [业务方法] 合并订单-合并出库单
     * @param odoIdString
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    List<OdoMergeCommand> waveOdoMerge(@Param("waveCode") String waveCode, @Param("odoIdString") String odoIdString, @Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType,
            @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType, @Param("store") String store, @Param("deliverGoodsTime") String deliverGoodsTime);

    List<OdoCommand> findOdoListByIdOuId(@Param("ids") String idString, @Param("ouId") Long ouId, @Param("odoStatus") String odoStatus);

    /**
     * [通用方法] 查找已合并订单
     * @param idString
     * @param ouId
     * @return
     */
    List<WhOdo> findMergeOdoListByIdOuId(@Param("ids") String idString, @Param("ouId") Long ouId);

    /**
     * [业务方法]删除出库单
     * 
     * @param id
     * @param ouId
     */
    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法]
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountForWaveByQueryMap")
    Pagination<OdoWaveGroupResultCommand> findOdoListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    List<OdoResultCommand> findCommandListForWave(OdoSearchCommand command);

    /**
     * [业务方法]获取创建波次的查询条件
     * 
     * @param command
     * @return
     */
    OdoWaveGroupResultCommand findOdoSummaryForWave(OdoWaveGroupSearchCommand command);

    List<WhOdo> findListForWave(OdoSearchCommand search);

    List<WhOdo> findByIdsAndOuId(List<String> odoIds, Long ouId, Integer option);

    /**
     * [通用方法] 根据出库单编码和组织获取出库单
     * @param odoCode
     * @param ouId
     * @return
     */
    WhOdo findOdoByCodeAndOuId(@Param("odoCode") String odoCode, @Param("ouId") Long ouId);

    /**
     * [通用方法]查找出库单列表
     * 
     * @param odo
     * @return
     */
    List<WhOdo> findListByParamExt(WhOdo odo);

}
