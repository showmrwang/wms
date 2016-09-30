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

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;


public interface ContainerDao extends BaseDao<Container, Long> {


    @QueryPage("findListCountByQueryMapExt")
    Pagination<ContainerCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Container o);

    @CommonQuery
    int saveOrUpdateByVersion(Container o);

    /**
     * 修改保存容器名称时校验名称是否存在
     * 
     * @param container
     * @return
     */
    long findListCountByParamAndId(Container container);

    /**
     * 批量编辑容器是否有效
     * 
     * @param ids
     * @param lifecycle
     * @param userid
     * @return
     */
    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("ouId") Long ouId);

    /**
     * 单个删除
     * 
     * @param id
     * @return
     */
    int deleteExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 通过多个id删除
     * 
     * @param ids
     * @return
     */
    int deleteByIdsExt(@Param("ids") List<Long> ids, @Param("ouId") Long ouId);

    /**
     * 通过id进行查询单个对象
     * 
     * @param id
     * @return
     */
    Container findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 更新
     * 
     * @param obj
     * @return
     */
    int updateExt(Container obj);

    /**
     * 容器批量生成
     * 
     * @param list
     * @return
     */
    int batchInsert(@Param("list") List<Container> list);

    /**
     * 根据容器编码查找容器
     * 
     * @param code
     * @param ouId
     * @return
     */
    ContainerCommand getContainerByCode(@Param("code") String code, @Param("ouId") Long ouId);

    /**
     * 根据容器编码查找容器
     * 
     * @param code
     * @param ouId
     * @return
     */
    int updateContainerStatusByCode(@Param("code") String code, @Param("ouId") Long ouId, @Param("lifecycle") Integer lifecycle, @Param("typeList") List<String> typeList);

    /**
     * 根据容器编码和容器类型查找匹配的容器
     * @param code
     * @param ouId
     * @param typeList
     * @return
     */
    List<ContainerCommand> getContainerByCodeAndType1(ContainerCommand command);


    List<ContainerCommand> getContainerByCodeAndType(ContainerCommand commnad);
}
