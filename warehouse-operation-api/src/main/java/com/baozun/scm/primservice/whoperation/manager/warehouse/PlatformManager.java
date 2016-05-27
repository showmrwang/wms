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
package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.PlatformCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;

public interface PlatformManager extends BaseManager {

    /**
     * 通过参数查询月台分页列表
     * 
     * @author mingwei.xie
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<PlatformCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 通过月台名称和编号检验月台是否存在
     * 
     * @author mingwei.xie
     * @param platform
     * @param ouId
     * @return
     */
    Boolean checkUnique(Platform platform, Long ouId);

    /**
     * 新建/修改月台信息
     * 
     * @author mingwei.xie
     * @param platform
     * @param userId
     * @param ouId
     * @return
     */
    Platform saveOrUpdate(Platform platform, Long userId, Long ouId);

    /**
     * 启用/停用月台
     * 
     * @author mingwei.xie
     * @param ids
     * @param lifeCycle
     * @param userId
     * @param ouId
     * @return
     */
    void updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userId, Long ouId);

    /**
     * 根据id查询月台信息
     * 
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    Platform findPlatformById(Long id, Long ouId);

    /**
     * 根据月台类型查找月台信息
     *
     * @author mingwei.xie
     * @param platformType
     * @param ouId
     * @param lifecycle
     * @return
     */
    List<Platform> findListByPlatformType(Long platformType, Long ouId, Integer lifecycle);
}
