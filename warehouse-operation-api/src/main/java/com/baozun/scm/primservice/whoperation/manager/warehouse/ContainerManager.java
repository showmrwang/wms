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

import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;


public interface ContainerManager extends BaseManager {

    /**
     * 通过参数查询容器分页列表
     * 
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    Pagination<ContainerCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 根据Id获取容器
     * 
     * @param id
     * @return
     */
    Container getContainerById(Long id, Long ouId);


    /**
     * 根据容器编码查找容器
     */
    ContainerCommand getContainerByCode(String code, Long ouId);

    /**
     * 根据容器编码查找容器 无判断
     */
    ContainerCommand findContainerByCode(String code, Long ouId);


    /**
     * updateByVersion
     * 
     * @param container
     * @return
     */
    int saveOrUpdateByVersion(Container container);
    
    /**
     * 检验容器
     * @author kai.zhu
     */
    ContainerCommand checkContainerStatus(String packingContainer, Integer target, Long ouId);

    ContainerCommand useOutBoundBoxToContainer(String containerCode, Long twoLevelType, Long userId, Long ouId);

}
