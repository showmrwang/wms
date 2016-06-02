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
package com.baozun.scm.primservice.whoperation.dao.system;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.command.system.SysDictionaryCommand;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;



public interface SysDictionaryDao extends BaseDao<SysDictionary, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<SysDictionary> findListByQueryMapWithPage(Page page, Sort[] sorts, SysDictionary o);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<SysDictionaryCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(SysDictionary o);

    List<SysDictionaryCommand> findGroupListbyParams(SysDictionaryCommand sysDictionaryCommand);

    int bacthUpdate(@Param("ids") List<Long> list, @Param("lifecycle") Integer lifecycle, @Param("operatorId") Long operatorId, @Param("modifyDate") Date modifyDate);

    @CommonQuery
    int saveOrUpdateByVersion(SysDictionary o);

    int checkUnique(SysDictionaryCommand o);

    SysDictionaryCommand getGroupbyId(Long id);

}
