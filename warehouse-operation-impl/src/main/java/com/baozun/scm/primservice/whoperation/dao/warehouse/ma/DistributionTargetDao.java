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
package com.baozun.scm.primservice.whoperation.dao.warehouse.ma;

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

import com.baozun.scm.primservice.whoperation.command.warehouse.ma.DistributionTargetCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.DistributionTarget;



public interface DistributionTargetDao extends BaseDao<DistributionTarget, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<DistributionTarget> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<DistributionTarget> query(Page page, Sort[] sorts, QueryCondition cond);

    List<DistributionTarget> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(DistributionTarget o);

    @CommonQuery
    int saveOrUpdateByVersion(DistributionTarget o);


    @QueryPage("findListCountByQueryMapExt")
    Pagination<DistributionTargetCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 根据id查找配送对象
     * 
     * @author mingwei.xie
     * @param id
     * @return
     */
    DistributionTargetCommand findCommandById(@Param("id") Long id);

    /**
     * 验证配送对象名称编码是否唯一
     * 
     * @author mingwei.xie
     * @param distributionTargetCommand
     * @return
     */
    int checkUnique(DistributionTargetCommand distributionTargetCommand);

    DistributionTarget findDistributionTargetByCode(@Param("code") String outboundTarget);
}
