package com.baozun.scm.primservice.whoperation.dao.warehouse;

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

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.Container2ndCategoryCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;


public interface Container2ndCategoryDao extends BaseDao<Container2ndCategory, Long> {


    @QueryPage("findListCountByQueryMapExt")
    Pagination<Container2ndCategoryCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Container2ndCategory o);

    @CommonQuery
    int saveOrUpdateByVersion(Container2ndCategory o);

    /**
     * 批量编辑容器是否有效
     * 
     * @param ids
     * @param lifecycle
     * @param userid
     * @return
     */
    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("ouId") Long ouId);

    List<Container2ndCategoryCommand> findC2ndCategoryByOuIdAndCid(Container2ndCategory container2ndCategory);

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
     * @param outerContainerCate
     * @return
     */
    Container2ndCategory findByIdExt(@Param("id") String outerContainerCate, @Param("ouId") Long ouId);

    /**
     * 更新
     * 
     * @param obj
     * @return
     */
    int updateExt(Container2ndCategory obj);
    
    /**
     * 通过二级容器名称和编号检验二级容器是否存在
     * @param container2ndCategory
     * @return
     */
    long uniqueCodeOrName(Container2ndCategory container2ndCategory);
    
}
