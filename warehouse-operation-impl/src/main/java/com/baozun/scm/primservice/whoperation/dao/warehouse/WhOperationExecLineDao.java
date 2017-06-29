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
import java.util.Set;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationExecLineCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;



public interface WhOperationExecLineDao extends BaseDao<WhOperationExecLine, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOperationExecLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhOperationExecLine o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOperationExecLine o);

    /**
     * [通用方法] 通过工作和组织找到执行明细列表
     * @param batch
     * @param ouId
     * @return
     */
    List<WhOperationExecLineCommand> findCommandByWorkId(@Param("workId") Long workId, @Param("ouId") Long ouId);

    /**
     * [通用方法] 通过批次号和传入容器查找当前工作工作下的所有作业执行明细
     * @param batch
     * @param containerId
     * @param ouId
     * @return
     */
    List<WhOperationExecLineCommand> findCommandByBatchAndContainer(@Param("batch") String batch, @Param("containerId") Long containerId, @Param("ouId") Long ouId);

    /****
     * 获得作业执行明细
     * @param operationId
     * @param ouId
     * @return
     */
    public List<WhOperationExecLine> checkOperationExecLine(@Param("ouId") Long ouId, @Param("operationId") Long operationId);
    
    
    /****
     * 获得作业明细
     * @param operationId
     * @param ouId
     * @return
     */
    public List<WhOperationExecLine> checkOperationLine(@Param("ouId") Long ouId,@Param("operationId") Long operationId);


    public WhOperationExecLine findOperationExecLine(@Param("ouId") Long ouId, @Param("id") Long id);

    /***
     * 获取当前作业下的所有执行明细
     * @param operationId
     * @param ouId
     * @return
     */
    public List<WhOperationExecLine> getOperationExecLine(@Param("operationId") Long operationId, @Param("ouId") Long ouId,@Param("outerContainerId") Long outerContainerId,@Param("insideContainerId") Long insideContainerId);


    /***
     * 得到容器库存信息
     * @param operationId
     * @param ouId
     * @param insideIdList
     * @param containerLatticeNoList
     * @return
     */
    public List<WhOperationExecLine> checkContainerInventory(@Param("invSkuIds") Set<Long> invSkuIds, @Param("ouId") Long ouId);
    
    
    /***
     *得到执行明细信息
     * @param operationId
     * @param ouId
     * @param insideIdList
     * @param containerLatticeNoList
     * @return
     */
    public List<WhOperationExecLine> getOperationExecLineByIds(@Param("ouId") Long ouId, @Param("execLineIds") Set<Long> execLineIds);

    Long getWorkIdByUseContainerId(@Param("batch") String batch, @Param("scanContainerId") Long scanContainerId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 根据工作号查找工作中使用的容器列表
     * @param workId
     * @param ouId
     * @return
     */
    public List<Long> findUseContainerListByWorkId(@Param("workId") Long workId, @Param("ouId") Long ouId);
    
    /***
     * 补货上架统计分析--获取作业执行明细数据
     * @param operationId
     * @param ouId
     * @return
     */
    public List<WhOperationExecLine> getOperationExecLineLst(@Param("operationId") Long operationId, @Param("ouId") Long ouId, @Param("isShortPicking") Boolean isShortPicking);
    
    /**
     * 
     * @param operationId
     * @param ouId
     * @param useContainerId
     * @return
     */
    public Long getOperationExecLineCount(@Param("operationId") Long operationId, @Param("ouId") Long ouId,@Param("useContainerId") Long useContainerId);
    
    /**
     * 
     * @param operationId
     * @param ouId
     * @param useContainerId
     * @return
     */
    public Long getOperationExecLineCountByOuterId(@Param("operationId") Long operationId, @Param("ouId") Long ouId,@Param("useOuterContainerId") Long useOuterContainerId);
    
    
    /***
     * 获取当前作业下的所有执行明细
     * @param operationId
     * @param ouId
     * @return
     */
    public List<WhOperationExecLine> findOperationExecLineByUseContainerId(@Param("useOuterContainerId") Long useOuterContainerId,@Param("locationId") Long locationId,@Param("operationId") Long operationId, @Param("ouId") Long ouId,@Param("useContainerId") Long useContainerId);

    
    public int countOperationExecLineByOutboundbox(@Param("outboundBoxCode") String outboundBoxCode,@Param("ouId") Long ouId,@Param("operationId") Long operationId);
    
}
