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

import com.baozun.scm.primservice.whoperation.command.warehouse.PlatformCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;

public interface PlatformDao extends BaseDao<Platform, Long> {

    Pagination<Platform> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Platform o);

    @CommonQuery
    int saveOrUpdateByVersion(Platform o);

    /**
     * 通过参数查询月台分页列表
     * 
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<PlatformCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 通过月台名称和编号检验月台是否存在
     * 
     * @author mingwei.xie
     * @param platform
     * @return
     */
    long checkUnique(Platform platform);


    /**
     * 根据id查找月台
     * 
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    Platform findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 根据月台类型查找月台信息
     *
     * @author mingwei.xie
     * @param platformType
     * @param ouId
     * @return
     */
    List<Platform> findListByPlatformType(@Param("platformType") String platformType, @Param("ouId") Long ouId, @Param("lifecycle") Integer lifecycle);

    /**
     * 手工指定月台
     *
     * @author mingwei.xie
     * @param platform
     * @return
     */
    Long assignPlatform(Platform platform);


    /**
     * 释放月台
     *
     * @author mingwei.xie
     * @param platform
     * @return
     */
    Long freePlatform(Platform platform);

    /**
     * 根据占用码查询月台
     *
     * @author mingwei.xie
     * @param occupationCode
     * @param ouId
     * @param lifecycle
     * @return
     */
    Platform findByOccupationCode(@Param("occupationCode") String occupationCode, @Param("ouId") Long ouId, @Param("lifecycle") Integer lifecycle);

}
