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

import org.apache.ibatis.annotations.Param;
import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;



public interface StoreDefectReasonsDao extends BaseDao<StoreDefectReasons, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<StoreDefectReasons> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(StoreDefectReasons o);

    @CommonQuery
    int saveOrUpdateByVersion(StoreDefectReasons o);


    /**
     * 通过店铺残次原因名称和编号检验是否存在
     * 
     * @param storeDefectReasons
     * @return
     */
    long uniqueCodeOrName(StoreDefectReasons storeDefectReasons);

    /**
     * 查询店铺残次原因列表
     * 
     * @param storeDefectReasons
     * @return
     */
    List<StoreDefectReasonsCommand> findStoreDefectReasonsByDefectTypeIds(@Param("storeDefectTypeIds") List<Long> storeDefectTypeIds);

    /**
     * 根据店铺残次类型查询对应残次原因
     * 
     * @return
     */
    List<StoreDefectReasonsCommand> findStoreDefectReasonsByDefectTypeId(Long defectTypeId);

}
