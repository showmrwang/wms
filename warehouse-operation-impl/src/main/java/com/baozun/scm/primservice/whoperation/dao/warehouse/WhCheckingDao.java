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

import com.baozun.scm.primservice.whoperation.command.warehouse.CheckingDisplayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WeightingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;

public interface WhCheckingDao extends BaseDao<WhChecking, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhChecking> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhChecking> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhChecking> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhChecking o);

    @CommonQuery
    int saveOrUpdateByVersion(WhChecking o);

    WeightingCommand findByWaybillCode(@Param("waybillCode") String waybillCode, @Param("ouId") Long ouId);

    WeightingCommand findByOutboundBoxCode(@Param("outboundBoxCode") String outboundBoxCode, @Param("ouId") Long ouId);

    List<WeightingCommand> findByOutboundBoxCodeAndCheckingId(@Param("checkingId") Long checkingId, @Param("outboundBoxCode") String outboundBoxCode, @Param("outboundboxId") Long outboundboxId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 按单复核-根据设施查找复核数据
     * 
     * @param facilityCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findListByFacilityCode(@Param("facilityCode") String facilityCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 按单复核-根据小车查找复核数据
     * 
     * @param outerContainerCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findListByOuterContainerCode(@Param("outerContainerCode") String outerContainerCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 按单复核-根据周转箱查找复核数据
     * 
     * @param containerCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findListByContainerCode(@Param("containerCode") String containerCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 查找复核数据
     * 
     * @param checking
     * @return
     */
    List<WhCheckingCommand> findListByParamExt(WhCheckingCommand checking);

    public WhCheckingCommand findWhCheckingCommandByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    public WhChecking findWhCheckingByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法] 查找复核信息
     * 
     * @param batch
     * @param ouId
     * @return
     */
    public CheckingDisplayCommand findCheckingInfoByBatchAndOuId(@Param("batch") String batch, @Param("ouId") Long ouId);



    public WhCheckingCommand findWhCheckingByOutboundboxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);



    /**
     * 根据条件查找复核头
     * 
     * @author mingwei.xie
     * @param checkingSourceCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findCheckingBySourceCode(@Param("checkingSourceCode") String checkingSourceCode, @Param("ouId") Long ouId);

    /**
     * 获取小车上的所有复核信息
     *
     * @param trolleyCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findCheckingByTrolley(@Param("trolleyCode") String trolleyCode, @Param("ouId") Long ouId);

    /**
     * 获取播种墙上的所有复核信息
     *
     * @param facilityCode
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findCheckingBySeedingFacility(@Param("facilityCode") String facilityCode, @Param("ouId") Long ouId);


    /**
     * 获取播种墙上的所有复核信息
     *
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
    WhCheckingCommand findCheckingByOutboundBox(@Param("outboundBoxCode") String outboundBoxCode, @Param("ouId") Long ouId);

    /**
     * 获取播种墙上的所有复核信息
     *
     * @param turnoverBoxCode
     * @param ouId
     * @return
     */
    WhCheckingCommand findCheckingByTurnoverBox(@Param("turnoverBoxCode") String turnoverBoxCode, @Param("ouId") Long ouId);


    List<WhCheckingCommand> findCheckingByBoxCode(@Param("checkingSourceCode") String checkingSourceCode, @Param("checkingBoxCode") String checkingBoxCode, @Param("ouId") Long ouId);

    /**
     * 根据条件查找复核头
     * 
     * @author mingwei.xie
     * @param checkingCommand
     * @return
     */
    WhCheckingCommand findCheckingByParam(WhCheckingCommand checkingCommand);

    /**
     * 查找批次下所有的复核箱信息
     *
     * @author mingwei.xie
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhCheckingCommand> findCheckingByBatch(@Param("batchNo") String batchNo, @Param("ouId") Long ouId);

    /**
     * 统计批次下待复核总单数
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    int getCheckingOdoQtyByBatch(@Param("batchNo") String batchNo, @Param("ouId") Long ouId);

}
