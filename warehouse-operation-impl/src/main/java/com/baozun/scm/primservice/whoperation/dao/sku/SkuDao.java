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

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuCommand;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;



public interface SkuDao extends BaseDao<Sku, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<Sku> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Sku o);

    @CommonQuery
    int saveOrUpdateByVersion(Sku o);

    int updateByVersionExt(Sku o);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<SkuCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    // int insertCommand(@Param("skuCommand2Shared") SkuCommand2Shared skuCommand2Shared);

    /**
     * 根据条件查询sku信息
     * 
     * @author mingwei.xie
     * @param skuCommand
     * @return
     */
    List<SkuCommand> findListByParamShared(SkuCommand skuCommand);

    /**
     * 根据条件查询sku完整信息
     * 
     * @author mingwei.xie
     * @param skuCommand
     * @return
     */
    List<Sku> findSkuAllInfoListByParamShared(SkuCommand skuCommand);

    /**
     * 根据sku ID查找sku信息
     * 
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    Sku findByIdShared(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 新建sku信息
     * 
     * @author mingwei.xie
     * @param sku
     * @return
     */
    int insertShared(Sku sku);

    /**
     * 更新sku信息
     * 
     * @author mingwei.xie
     * @param sku
     * @return
     */
    int updateShared(Sku sku);

    int batchUpdateStatus(@Param("skuIds") List<Long> skuIds, @Param("operatorId") Long operatorId, @Param("lifecycle") Integer lifecycle, @Param("lastModifyDate") Date lastModifyDate);

    long uniqueCodeOrName(Sku sku);

    List<SkuCommand> findListByParamExt(SkuCommand skuCommand);

    List<Sku> findSkuAllInfoListByParamExt(SkuCommand skuCommand);

    /**
     * 根据customerId、ouId和多条码查询唯一的商品信息
     * 
     * @param barCode
     * @param customerId
     * @param ouId
     * @return
     */
    Sku findSkuAllByBarCodeCustomerIdOuId(@Param("barCode") String barCode, @Param("customerId") Long customerId, @Param("ouId") Long ouId);

    /**
     * 通过商品条码查找对应商品数据和多条码默认数量 格式skuid-qty
     * 
     * @param barCode
     * @return
     */
    List<String> getSkuIdAndBarCodeQtyByBarCode(String barCode);

}
