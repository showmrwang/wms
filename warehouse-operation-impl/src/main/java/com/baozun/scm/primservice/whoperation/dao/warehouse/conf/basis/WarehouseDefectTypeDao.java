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
package com.baozun.scm.primservice.whoperation.dao.warehouse.conf.basis;

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

import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;

/**
 * @author lichuan
 * 
 */
public interface WarehouseDefectTypeDao extends BaseDao<WarehouseDefectType, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WarehouseDefectType> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WarehouseDefectType> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WarehouseDefectType> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WarehouseDefectType o);
    
    @CommonQuery
    int saveOrUpdateByVersion(WarehouseDefectType o);
    
    /**
     * 通过识别参数查询仓库残次信息
     * @author lichuan
     * @param params
     * @return
     */
    WarehouseDefectTypeCommand findDefectTypeByIdParams(Map<String, Object> params);
    
    /**
     * 唯一性校验
     * 
     * @author lichuan
     * @param wdtCmd
     * @return
     */
    long checkUnique(WarehouseDefectTypeCommand wdtCmd);

    /**
     * 通过识别参数查询仓库残次信息
     * 
     * @author lichuan
     * @param id
     * @param ouId
     * @return
     */
    WarehouseDefectType findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
    
    /**
     * 通过条件查询上架推荐规则分页数据
     * 
     * @author lichuan
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findCountByQueryMapWithPageExt")
    Pagination<WarehouseDefectTypeCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 通过OUID查询对应仓库残次类型
     */
    List<WarehouseDefectTypeCommand> findWarehouseDefectTypeByOuId(@Param("ouid") Long ouid, @Param("lifecycle") Integer lifecycle);

}
