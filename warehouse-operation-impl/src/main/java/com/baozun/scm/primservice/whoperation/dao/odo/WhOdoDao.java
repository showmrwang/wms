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
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;

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
     * 
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    List<String> findOdoMergableIds(@Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType, @Param("store") String store,
            @Param("deliverGoodsTime") String deliverGoodsTime);

    /**
     * [业务方法] 波次中合并出库单-查找可以合并的出库单号
     * 
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    List<String> findWaveOdoMergableIds(@Param("waveCode") String waveCode, @Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType, @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType,
            @Param("store") String store, @Param("deliverGoodsTime") String deliverGoodsTime);

    /**
     * [业务方法] 合并订单-合并出库单
     * 
     * @param odoIdString
     * @param ouId
     * @param outboundCartonType
     * @param epistaticSystemsOrderType
     * @param store
     * @param deliverGoodsTime
     * @return
     */
    List<OdoMergeCommand> odoMerge(@Param("odoStatus") String odoStatus, @Param("odoIdString") String odoIdString, @Param("ouId") Long ouId, @Param("outboundCartonType") String outboundCartonType,
            @Param("epistaticSystemsOrderType") String epistaticSystemsOrderType, @Param("store") String store, @Param("deliverGoodsTime") String deliverGoodsTime);

    /**
     * [业务方法] 合并订单-合并出库单
     * 
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
     * 
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

    List<Long> findOdoIdListForWave(OdoSearchCommand command);

    List<WhOdo> findByIdsAndOuId(List<String> odoIds, Long ouId, Integer option);

    /**
     * [通用方法] 根据出库单编码和组织获取出库单
     * 
     * @param odoCode
     * @param ouId
     * @return
     */
    WhOdo findOdoByCodeAndOuId(@Param("odoCode") String odoCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 根据平台订单号和组织获取出库单
     * gianni
     * @param odoCode
     * @param ouId
     * @return
     */
    WhOdo findOdoByEcOrderCodeAndOuId(@Param("ecOrderCode") String ecOrderCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 根据外部对接编码和组织获取出库单
     * gianni
     * @param odoCode
     * @param ouId
     * @return
     */
    WhOdo findOdoByExtCodeAndOuId(@Param("extCode") String extCode, @Param("ouId") Long ouId);

    /**
     * 根据提供波次ID查找当中有波次明细未分配规则的出库单ID
     */
    List<OdoCommand> getNoRuleOdoIdList(@Param("waveIdList") List<Long> waveIdList, @Param("ouId") Long ouId);

    /**
     * 修改出库单waveCode为空,并添加失败原因
     */
    int updateOdoByAllocateFail(@Param("odoId") Long odoId, @Param("reason") String reason, @Param("ouId") Long ouId);

    /**
     * 修改出库单waveCode为空,并添加失败原因
     */
    int updateOdoByAllocateFailAndOdoIdList(@Param("odoIdList") List<Long> odoId, @Param("reason") String reason, @Param("ouId") Long ouId);

    /**
     * [通用方法]查找出库单列表
     * 
     * @param odo
     * @return
     */
    List<WhOdo> findListByParamExt(WhOdo odo);

    /**
     * 出库单导出
     * 
     * @param odoSearchCommand
     * @return
     */
    List<String> findExportExeclList(OdoSearchCommand odoSearchCommand);

    List<String> findDistinctCounterCode(@Param("ouId") Long ouId);

    /**
     * [业务方法]
     * 
     * @param counterCode
     * @param ouId
     * @return
     */
    List<Long> findOdoByCounterCode(@Param("counterCode") String counterCode, @Param("ouId") Long ouId);

    /**
     * [业务方法]
     * 
     * @param counterCode
     * @param ouId
     * @return
     */
    List<Long> findOdoByCounterCodeToCalcDistributeMode(@Param("counterCode") String counterCode, @Param("ouId") Long ouId);

    /**
     * [业务方法]
     * 
     * @param subList
     * @return
     */
    WaveCommand findWaveSumDatabyOdoIdList(@Param("odoIdList") List<Long> subList, @Param("ouId") Long ouId);

    int addOdoToWave(@Param("odoIdList") List<Long> subList, @Param("ouId") Long ouId, @Param("userId") Long userId, @Param("waveCode") String code, @Param("odoStatus") String odoStatus);

    List<Long> findOdoToBeAddedToWave(@Param("waveCode") String waveCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 根据波次号查找出库单，返回 odoId-counterCode
     * 
     * @param code
     * @param ouId
     * @return
     */
    List<String> findOdoIdCounterCodebyWaveCode(@Param("waveCode") String code, @Param("ouId") Long ouId);

    /**
     * 获取播种中出库单明细信息
     *
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhSeedingWallLatticeLine> getSeedingOdoLineInfo(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * 根据ID获取出库单列表
     *
     * @author mingwei.xie
     * @param odoIdList
     * @param ouId
     * @return
     */
    List<OdoCommand> getWhOdoListById(@Param("odoIdList") List<Long> odoIdList, @Param("ouId") Long ouId);

    /**
     * 查找非删除状态下的出库单
     * 
     * @param extOdoCode
     * @param ouId
     * @return
     */
    List<WhOdo> findByExtCodeOuIdNotCancel(@Param("extCode") String extOdoCode, @Param("dataSource") String dataSource, @Param("ouId") Long ouId);

    /**
     * 查询仓库下需要归档的出库单(状态为10和17的)
     * 
     * @author kai.zhu
     * @version 2017年3月29日
     */
    List<Long> findOdoArchivData(@Param("ouId") Long ouId);

    /**
     * 
     * @author kai.zhu
     * @version 2017年4月7日
     */
    int countInvoiceInfo(@Param("odoIdList") List<Long> odoIdList, @Param("ouId") Long ouId);

    /**
     * [业务方法] 查找出库单状态, 判断是否已完成复核
     * 
     * @param outboundBoxCode
     * @param waybillCode
     * @param ouId
     * @return
     */
    String checkOdoStatus(@Param("outboundBoxCode") String outboundBoxCode, @Param("waybillCode") String waybillCode, @Param("ouId") Long ouId);

    List<Long> findNewOdoIdList(@Param("odoIdList") List<Long> odoIdOriginalList, @Param("ouId") Long ouId);

    List<Long> getStoreIdByOdoIdList(@Param("odoIdList") List<Long> odoIdOriginalList, @Param("ouId") Long ouId);

    List<Long> findOdoIdListByStoreIdListAndOriginalIdList(@Param("odoIdList") List<Long> odoIdList, @Param("storeIdList") List<Long> storeIdList, @Param("ouId") Long ouId);
    
    /**
     * 更新出库单的odoIndex
     * @author kai.zhu
     * @version 2017年5月3日
     */
    int updateOdoIndexByOdoId(@Param("odoId") Long odoId, @Param("odoIndex") String odoIndex, @Param("ouId") Long ouId);

}
