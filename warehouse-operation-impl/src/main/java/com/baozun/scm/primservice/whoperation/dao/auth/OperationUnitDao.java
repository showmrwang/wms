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
package com.baozun.scm.primservice.whoperation.dao.auth;

import java.util.List;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.manager.auth.OpUnitTreeCommand;
import com.baozun.scm.primservice.whoperation.model.auth.OperationUnit;




public interface OperationUnitDao extends BaseDao<OperationUnit, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<OperationUnit> findListByQueryMapWithPage(Page page, Sort[] sorts, OperationUnit o);

    @CommonQuery
    int saveOrUpdate(OperationUnit o);

    @CommonQuery
    int saveOrUpdateByVersion(OperationUnit o);

    /**
     * 根据父ID获取子数据
     * 
     * @param parentId
     * @return
     */
    List<OpUnitTreeCommand> findListByParentId(@Param("parentId") Long parentId);

    /**
     * 根据用户ID获取与其相关的组织
     * 
     * @param userId
     * @return
     */
    List<OpUnitTreeCommand> findUnitTreeByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID获取与其相关的组织
     * 
     * @param userId
     * @return
     */
    List<OpUnitTreeCommand> findCommandList(OperationUnit o);
}
