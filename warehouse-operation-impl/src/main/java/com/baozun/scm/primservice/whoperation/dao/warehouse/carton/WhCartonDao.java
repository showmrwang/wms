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
package com.baozun.scm.primservice.whoperation.dao.warehouse.carton;

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

import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;

public interface WhCartonDao extends BaseDao<WhCarton, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhCarton> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhCarton> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhCarton> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhCarton o);

    @CommonQuery
    int saveOrUpdateByVersion(WhCarton o);

    /***
     * 通过ASN相关信息查询对用拆箱信息
     */
    List<WhCartonCommand> findWhCartonDevanningList(@Param("asnid") Long asnid, @Param("asnlineid") Long asnlineid, @Param("skuid") Long skuid, @Param("ouid") Long ouid);

    /**
     * 通过id+ouid查询对应拆箱信息
     * 
     * @return
     */
    WhCarton findWhCatonById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * 删除对应拆箱信息
     * 
     * @return
     */
    int deleteCartonById(@Param("id") Long id, @Param("ouid") Long ouid);


}
