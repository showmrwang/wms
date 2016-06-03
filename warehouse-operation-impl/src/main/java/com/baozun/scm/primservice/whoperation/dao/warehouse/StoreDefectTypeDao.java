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

import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;



public interface StoreDefectTypeDao extends BaseDao<StoreDefectType, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<StoreDefectType> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(StoreDefectType o);

    @CommonQuery
    int saveOrUpdateByVersion(StoreDefectType o);

    /**
     * 通过店铺残次类型名称和编号检验是否存在
     * 
     * @param store
     * @return
     */
    long uniqueCodeOrName(StoreDefectType storeDefectType);

    /**
     * 查询店铺残次类型列表
     * 
     * @param storeDefectType
     * @return
     */
    List<StoreDefectTypeCommand> findStoreDefectTypeByParam(StoreDefectType storeDefectType);

    /**
     * 根据店铺ID查询对应残次类型
     * 
     * @param storeid
     * @return
     */
    List<StoreDefectTypeCommand> findStoreDefectTypesByStoreId(Long storeid);

    /**
     * 通过店铺Id查询残次类型Ids
     * 
     * @param storeId
     * @return
     */
    List<Long> findStoreDefectTypeIdsByStoreId(@Param("storeId") Long storeId);
}
