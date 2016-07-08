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
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuBarcodeCommand;
import com.baozun.scm.primservice.whoperation.model.sku.SkuBarcode;



public interface SkuBarcodeDao extends BaseDao<SkuBarcode, Long> {

    @QueryPage("findListCountByQueryMapExt")
    Pagination<SkuBarcodeCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(SkuBarcode o);

    @CommonQuery
    int saveOrUpdateByVersion(SkuBarcode o);

    /**
     * 根据ID查找skuBarcode信息
     * 
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @return
     */
    SkuBarcode findByIdShared(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 新增条码信息
     * 
     * @author mingwei.xie
     * @param skuBarcode
     * @return
     */
    int insertShared(SkuBarcode skuBarcode);

    /**
     * 更新条码信息
     * 
     * @author mingwei.xie
     * @param skuBarcode
     * @return
     */
    int updateShared(SkuBarcode skuBarcode);

    /**
     * 批量新增条码信息
     * 
     * @author mingwei.xie
     * @param list
     * @return
     */
    int batchInsertShared(List<SkuBarcode> list);

    /**
     * 批量更新条码信息
     * 
     * @author mingwei.xie
     * @param list
     * @return
     */
    int batchUpdateShared(List<SkuBarcode> list);

    /**
     * 校验商品条码唯一性
     * 
     * @param skuBarcode
     * @return
     */
    long uniqueSkuBarCode(SkuBarcode skuBarcode);

    /**
     * 批量编辑商品条码是否有效
     * 
     * @param ids
     * @param lifecycle
     * @param userid
     * @return
     */
    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("lastModifyTime") Date lastModifyTime);


    /**
     * 通过Id查询相应的商品条码信息
     * 
     * @return
     */
    SkuBarcodeCommand findSkuBarcodeCommandById(Long id);

    /**
     * 根据IDS查询所有skubarcode列表
     * 
     * @param ids
     * @return
     */
    List<SkuBarcode> findSkuBarcodeByIds(@Param("ids") List<Long> ids);

    /**
     * 根据skuId查询所有skubarcode列表
     * 
     * @param ids
     * @return
     */
    List<SkuBarcode> findSkuBarcodeBySkuId(Long skuId);

    /**
     * 根据skuId查询相应仓库所有skubarcode列表
     * 
     * @param skuId
     * @param ouId
     * @return
     */
    List<SkuBarcode> findSkuBarcodeBySkuIdShared(@Param("skuId") Long skuId, @Param("ouId") Long ouId);

    /**
     * 根据skuid+barcode查询对应skubarcode信息
     * 
     * @return
     */
    SkuBarcode findSkuBarCodeBySkuIdAndBarCode(@Param("skuId") Long skuId, @Param("ouId") Long ouId, @Param("barCode") String barCode);

}
