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

import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;

public interface WhOdoTransportMgmtDao extends BaseDao<WhOdoTransportMgmt, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoTransportMgmt> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdoTransportMgmt> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdoTransportMgmt> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdoTransportMgmt o);

    /**
     * [通用方法]根据ODOID,OUID查找ODOTRANSPORTMGMT
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    WhOdoTransportMgmt findObject(WhOdoTransportMgmt o);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);


}
