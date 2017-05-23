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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;



public interface WhOperationDao extends BaseDao<WhOperation, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOperation> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhOperation o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOperation o);

    /**
     * 获取作业头信息
     * 
     * @author qiming.liu
     * @param operationCode
     * @param ouId
     * @return
     */
    WhOperationCommand findOperationByCode(@Param("operationCode") String operationCode, @Param("ouId") Long ouId);

    /**
     * 根据工作Id获取作业头信息
     * 
     * @author qiming.liu
     * @param workId
     * @param ouId
     * @return
     */
    WhOperationCommand findOperationCommandByWorkId(@Param("workId") Long workId, @Param("ouId") Long ouId);

    /**
     * 根据Id获取作业头信息
     * 
     * @author qiming.liu
     * @param workId
     * @param ouId
     * @return
     */
    WhOperationCommand findOperationCommandById(@Param("id") Long id, @Param("ouId") Long ouId);

    /***
     * 查询作业信息
     * @param id
     * @param ouId
     * @return
     */
    public WhOperation findOperationByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /***
     * [通用方法] 根据批次号查找作业
     * @param batch
     * @param ouId
     * @return
     */
    public WhOperation findByBatch(@Param("batch") String batch, @Param("ouId") Long ouId);

    /**
     * 根据作业头Id和ouId,locationId获取作业明细信息
     * 
     * @author qiming.liu
     * @param odoId
     * @param isCreateWork
     * @param ouId
     * @return
     */
    List<WhOperationCommand> findOperationCommandByOdo(@Param("odoId") Long odoId, @Param("odoLineId") Long odoLineId, @Param("status") Integer status, @Param("ouId") Long ouId);
}
