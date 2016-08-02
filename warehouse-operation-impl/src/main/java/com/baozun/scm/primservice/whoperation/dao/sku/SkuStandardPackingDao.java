package com.baozun.scm.primservice.whoperation.dao.sku;

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

import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuStandardPackingCommand;
import com.baozun.scm.primservice.whoperation.model.sku.SkuStandardPacking;



public interface SkuStandardPackingDao extends BaseDao<SkuStandardPacking, Long> {


    @QueryPage("findListCountByQueryMapExt")
    Pagination<SkuStandardPackingCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(SkuStandardPacking o);

    @CommonQuery
    int saveOrUpdateByVersion(SkuStandardPacking o);

    /**
     * 校验商品标准装箱唯一性
     * 
     * @param skuStandardPacking
     * @return
     */
    long uniqueSkuStandardPacking(SkuStandardPackingCommand skuStandardPackingCommand);

    /**
     * 批量更新商品标准装箱是否可用
     * 
     * @param ids
     * @param lifecycle
     * @param userid
     * @param ouId
     * @return
     */
    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("ouId") Long ouId);

    /**
     * 通过Id查询相应商品标准装箱记录
     * 
     * @param id
     * @param ouId
     * @return
     */
    SkuStandardPackingCommand findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /***
     * 根据skuid+container_type查询商品标准装箱记录
     */
    SkuStandardPackingCommand findSkuStandardPackingBySkuIdAndContainerType(@Param("skuid") Long skuid, @Param("containerType") Long containerType, @Param("ouid") Long ouid);

    /**
     * 查询此商品是否维护了装箱信息By商品外部对接码
     */
    List<SkuStandardPackingCommand> findSkuStandardPackingBySkuBarCode(@Param("skuBarCode") String skuBarCode, @Param("ouid") Long ouid, @Param("lifecycle") Integer lifecycle);

    List<SkuStandardPackingCommand> getContainerType(@Param("skuId") Long skuId, @Param("ouId") Long ouId);

}
