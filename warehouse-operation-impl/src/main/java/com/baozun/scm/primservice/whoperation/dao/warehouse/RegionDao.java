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

import com.baozun.scm.primservice.whoperation.command.warehouse.RegionCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Region;



public interface RegionDao extends BaseDao<Region, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<Region> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<RegionCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Region o);

    List<RegionCommand> findRegionByParentId(Long id);

    /**
     * 通过parenid查询对应区域列表 不包含删除区域
     * 
     * @param id
     * @return
     */
    List<RegionCommand> findRegionByParentIdNotDelete(Long id);

    @CommonQuery
    int saveOrUpdateByVersion(Region o);

    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid);

    RegionCommand checkNameOrCode(@Param("name") String name, @Param("code") String code, @Param("parentId") Long parentId);

    Region findRegionByCodeLifecycle(@Param("code") String code, @Param("lifecycle") Integer lifecycle);

    Integer getSortNoByParentId(Long parentId);

    Region findRegionByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId);

}
