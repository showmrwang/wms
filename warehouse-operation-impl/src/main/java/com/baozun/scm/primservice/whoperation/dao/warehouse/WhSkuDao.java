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

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSku;

import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhSkuDao extends BaseDao<WhSku, Long> {

 
    /**
     * 查询商品列表
     * @author shenlijun
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findwhSkuListCount")
    Pagination<WhSkuCommand> findwhSkuList(Page page, Sort[] sorts, Map<String, Object> params);

   
    /**
     * 查询商品扩展信息
     * @param params
     * @return
     */
    WhSkuCommand findWhSkuByParams(Map<String, Object> params);

    /**
     * 获取商品信息
     * @author lichuan
     * @param id
     * @param ouId
     * @return
     */
    WhSkuCommand findWhSkuByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
 
}
