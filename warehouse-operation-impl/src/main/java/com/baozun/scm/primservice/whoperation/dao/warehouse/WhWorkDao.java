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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;



public interface WhWorkDao extends BaseDao<WhWork, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhWork> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhWork o);

    @CommonQuery
    int saveOrUpdateByVersion(WhWork o);

    int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
    * 根据code和ouId查找工作头信息
    * @author qiming.liu
    * @param code
    * @param ouId
    * @return
    */
    WhWorkCommand findWorkByWorkCode(@Param("code") String code, @Param("ouId") Long ouId);

    /**
     * [业务方法] 工作查询-根据拣货工作配置获取工作列表
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapForPda")
    Pagination<WhWorkCommand> findListByQueryMapWithPageForPda(Page page, Sort[] sorts, Map<String, Object> params);

    List<Long> getOdoIdListByBatch(@Param("batch") String batch, @Param("ouId") Long ouId);

    /**
     * 获取批次下的所有工作
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhWorkCommand> findWorkByBatch(@Param("batchNo") String batchNo, @Param("ouId") Long ouId);

    /**
     * 获取批次下的所有工作ext
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhWorkCommand> findWorkByBatchExt(@Param("batchNo") String batchNo, @Param("ouId") Long ouId);



    /**
     * [业务方法] 通过工作id查找工作
     * @param workId
     * @param ouId
     * @return
     */
    WhWork findWorkById(@Param("workId") Long workId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过工作id查找工作ext
     * @param workId
     * @param ouId
     * @return
     */
    WhWorkCommand findWorkByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
}
