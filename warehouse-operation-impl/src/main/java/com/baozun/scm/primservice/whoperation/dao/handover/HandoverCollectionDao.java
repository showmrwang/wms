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
package com.baozun.scm.primservice.whoperation.dao.handover;

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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhHandoverStationCommand;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollection;


public interface HandoverCollectionDao extends BaseDao<HandoverCollection, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<HandoverCollection> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<HandoverCollection> query(Page page, Sort[] sorts, QueryCondition cond);

    List<HandoverCollection> query(QueryCondition cond);


    @CommonQuery
    int saveOrUpdate(HandoverCollection o);

    List<HandoverCollection> findByGroupCondition(@Param("groupCondition") String groupCondition, @Param("ouId") Long ouId);

    HandoverCollection findByOutboundboxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);

    List<HandoverCollection> findByHandoverStation(@Param("handoverStationId") Long handoverStationId, @Param("ouId") Long ouId);

    /***
     * 根据code查询交接库位
     * 
     * @param recommandHandoverStationCode
     * @return
     */
    WhHandoverStationCommand findStationByCode(@Param("code") String recommandHandoverStationCode);


    /***
     * 查询该库位当前容量
     * 
     * @param recommandHandoverStationCode
     * @param status
     * @param ouId
     * @return
     */
    Integer findCountByHandoverStationIdAndStatus(@Param("HandoverStationCode") Long long1, @Param("status") Integer status, @Param("ouId") Long ouId);

    /***
     * 查询当前交接批次
     * 
     * @param recommandHandoverStationCode
     * @param status
     * @return
     */
    String findBatchByHandoverStationIdAndStatus(@Param("HandoverStationCode") Long long1, @Param("status") Integer status, @Param("ouId") Long ouId);

    /***
     * 查询状态
     * 
     * @param recommandHandoverStationCode
     * @return
     */
    String findStatusByHandoverStationIdAndStatus(@Param("HandoverStationCode") Long long1, @Param("status") Integer status, @Param("ouId") Long ouId);

    /***
     * 
     * 查询指定交接批次下未交接的出库箱数
     * 
     * @param handoverBatch 交接批次
     * @param ouId
     * @param toHandover 状态
     * @return
     */
    Long findHandoverCollectionByBatchAndStatus(@Param("handoverBatch") String handoverBatch, @Param("status") String status, @Param("ouId") Long ouId);

    /***
     * 
     * 同批次的交接集货下的出库箱的客户店铺运输服务商codename是否相等 相等 结果为1
     * 
     * @param handoverStationId
     * @param ouId
     * @return
     */
    Integer isTheSameCodeAndName(@Param("handoverStationId") Long handoverStationId, @Param("ouId") Long ouId);
}
