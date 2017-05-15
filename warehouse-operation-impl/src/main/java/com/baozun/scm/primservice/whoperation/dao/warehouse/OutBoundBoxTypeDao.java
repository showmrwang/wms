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

import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;



public interface OutBoundBoxTypeDao extends BaseDao<OutBoundBoxType, Long> {


    @QueryPage("findListCountByQueryMapExt")
    Pagination<OutInvBoxTypeCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(OutBoundBoxType o);

    @CommonQuery
    public int saveOrUpdateByVersion(OutBoundBoxType o);


    /**
    * 根据id 和ouId 获取出库箱类型
    * @param id
    * @param ouId
    * @return
    */
    public OutInvBoxTypeCommand findOutInventoryBoxType(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 批量停用/启用
     * @param idList
     * @param lifeCycle
     * @param userId
     * @param ouId
     */
    public OutBoundBoxType findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);


    /**
     * 根据参数获取出库箱类型
     * @param o
     * @return
     */
    public Long checkUnique(OutInvBoxTypeCommand o);

    /**
     * 
     * @param code
     * @param ouId
     * @return
     */
    public OutBoundBoxType findByCode(@Param("code") String code, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过耗材条码查找耗材是否在出库箱类型表中
     * @param ouId
     * @param consumableSkuBarcode
     * @return
     */
    List<OutBoundBoxType> findBySkuBarcode(@Param("ouId") Long ouId, @Param("consumableSkuBarcode") String consumableSkuBarcode);

    /**
     * 复核用 查询耗材条码对应的出库箱类型
     *
     * @param skuIdList
     * @param ouId
     * @return
     */
    List<OutInvBoxTypeCommand> findBySkuId(@Param("skuIdList") List<Long> skuIdList, @Param("ouId") Long ouId);
}
