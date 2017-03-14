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
package com.baozun.scm.primservice.whoperation.dao.warehouse.inventory;

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

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.InventorySnCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

public interface WhSkuInventorySnDao extends BaseDao<WhSkuInventorySn, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhSkuInventorySn> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<InventorySnCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhSkuInventorySn> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhSkuInventorySn> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhSkuInventorySn o);

    /**
     * 根据库存UUID查找对应SN/残次信息
     * 
     * @param ouid
     * @param uuid
     * @return
     */
    List<WhSkuInventorySnCommand> findWhSkuInventoryByUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * 根据库存UUID+SYS_UUID修改对应UUID
     * 
     * @return
     */
    int updateWhSkuInventorySnUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid, @Param("newuuid") String newuuid, @Param("sysuuid") String sysuuid);

    /**
     * 根据库存UUID+SN或者残次条码查找对应SN/残次信息
     * 
     * @param ouid
     * @param uuid
     * @param snCode
     * @return
     */
    WhSkuInventorySn findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(@Param("ouid") Long ouid, @Param("uuid") String uuid, @Param("snCode") String snCode);

    /**
     * 根据库存UUID+本次系统的UUID查找对应SN/残次信息
     * 
     * @return
     */
    List<WhSkuInventorySn> findWhSkuInventorySnByUuidAndSysUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid, @Param("sysuuid") String sysuuid);

    /**
     * 根据库存UUID+SYS_UUID修改对应UUID 关联外键表获取对应数据
     * 
     * @param ouid
     * @param uuid
     * @return
     */
    List<WhSkuInventorySnCommand> findWhSkuInventoryByUuidLeftJoinForeignKey(@Param("ouid") Long ouid, @Param("uuid") String uuid);
    
    /**
     * 查询sn数据
     * @author lichuan
     * @param id
     * @param ouId
     * @return
     */
    WhSkuInventorySnCommand findWhSkuInventorySnByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 查询所有对应容器号的库存信息
     * 
     * @author mingwei.xie
     * @param ouId 仓库组织ID
     * @param insideContainerIdList 内部容器ID列表
     * @return 返回的String有残次库存的是"invId,invSnId"，没有残次库存的是"invId,null"
     */
    List<String> findInvSnIdStrByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerIdList") List<Long> insideContainerIdList);
    
    /**
     * 
     * @author kai.zhu
     * @version 2017年3月10日
     * @param occupationCode
     * @param uuid
     * @return
     */
	List<WhSkuInventorySnCommand> findInvSnByAsnCodeAndUuid(@Param("asnCode") String occupationCode, @Param("uuid") String uuid, @Param("ouId") Long ouId);
}
