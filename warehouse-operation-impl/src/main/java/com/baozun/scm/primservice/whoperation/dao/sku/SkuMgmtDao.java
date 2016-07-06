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
package com.baozun.scm.primservice.whoperation.dao.sku;

import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;



public interface SkuMgmtDao extends BaseDao<SkuMgmt, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<SkuMgmt> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(SkuMgmt o);

    int updateBySkuId(SkuMgmt o);

    /**
     * 根据ID查找SkuMgmt信息
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    SkuMgmt findByIdShared(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 新建SkuMgmt信息
     * 
     * @author mingwei.xie
     * @param skuMgmt
     * @return
     */
    int insertShared(SkuMgmt skuMgmt);

    /**
     * 更新SkuMgmt信息
     *
     * @author mingwei.xie
     * @param skuMgmt
     * @return
     */
    int updateShared(SkuMgmt skuMgmt);

    SkuMgmt findSkuMgmtBySkuId(@Param("skuId") Long skuId);

    /**
     * 根据sku信息查询相应的SkuMgmt
     *
     * @author mingwei.xie
     * @param skuId
     * @param ouId
     * @return
     */
    SkuMgmt findSkuMgmtBySkuIdShared(@Param("skuId") Long skuId, @Param("ouId") Long ouId);
}
