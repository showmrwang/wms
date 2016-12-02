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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;



public interface WhLocationDao extends BaseDao<Location, Long> {

    @QueryPage("findLocationListCountByQueryMapExt")
    // Pagination<Location> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object>
    // params);
    Pagination<LocationCommand> findLocationCommandListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Location o);

    @CommonQuery
    int saveOrUpdateByVersion(Location o);

    int batchUpdateLifeCycle(@Param("locationIds") List<Long> locationIds, @Param("lifecycle") Integer lifecycle, @Param("userId") Long userId, @Param("modifyDate") Date modifyDate, @Param("ouId") Long ouId);

    LocationCommand findLocationCommandByParam(@Param("id") Long id, @Param("ouId") Long ouId);

    int batchInsert(@Param("list") List<Location> list);

    int deleteByParams(@Param("ouId") Long ouId, @Param("uuid") String uuid);

    int batchUpdateByVersion(@Param("location") Location location, @Param("list") List<String> list, @Param("isAllocated") Boolean isAllocated, @Param("isWorked") Boolean isWorked, @Param("isShelfed") Boolean isShelfed, @Param("ouId") Long ouId);

    List<Location> findWrongListbyCode(@Param("list") List<String> list, @Param("ouId") Long ouId);

    int batchDeleteByVersion(@Param("list") List<String> list, @Param("date") Date date, @Param("ouId") Long ouId);

    List<Location> findByCodeRange(@Param("list") List<String> list, @Param("ouId") Long ouId);

    int batchDeleteSelected(@Param("list") List<Long> list, @Param("date") Date date, @Param("ouId") Long ouId);

    List<Location> getByIds(@Param("list") List<Long> ids, @Param("ouId") Long ouId);
    
    List<Long> sortByIds(@Param("list") Set<Long> ids, @Param("ouId") Long ouId);

    Location findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    Location findLocationByCode(@Param("code") String code, @Param("ouId") Long ouId);

    List<LocationCommand> findListByParamExt(LocationCommand locationCommand);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);
    
    /**
     * 获取指定库区的所有空库位
     * @author lichuan
     * @param id
     * @param ouId
     * @return
     */
    List<LocationCommand> findAllEmptyLocsByAreaId(@Param("areaId") Long id, @Param("ouId") Long ouId, @Param("cSql") String cSql);
    
    /**
     * 获取指定库区的所有静态库位
     * @author lichuan
     * @param id
     * @param ouId
     * @return
     */
    List<LocationCommand> findAllStaticLocsByAreaId(@Param("areaId") Long id, @Param("ouId") Long ouId, @Param("cSql") String cSql);
    
    /**
     * 获取指定库区且库存属相相同的库位
     * @author lichuan
     * @param id
     * @param ouId
     * @param invAttrsSql
     * @return
     */
    List<LocationCommand> findAllInvLocsByAreaIdAndSameAttrs(@Param("areaId") Long id, @Param("ouId") Long ouId, @Param("cSql") String cSql);
    
    /**
     * 获取指定库区且库存属相不同的库位
     * @author lichuan
     * @param id
     * @param ouId
     * @param cSql
     * @return
     */
    List<LocationCommand> findAllInvLocsByAreaIdAndDiffAttrs(@Param("areaId") Long id, @Param("ouId") Long ouId, @Param("cSql") String cSql);
    
    /**
     * 获取指定库区的所有可用库位
     * @author lichuan
     * @param id
     * @param ouId
     * @param cSql
     * @return
     */
    List<LocationCommand> findAllAvailableLocsByAreaId(@Param("areaId") Long id, @Param("ouId") Long ouId, @Param("cSql") String cSql);

    /**
     * 根据库位条码查询库位是否存在
     * 
     * @author lijun.shen
     * @param barCode
     * @return
     */
    Location getLocationByBarcode(@Param("barCode")String barCode,@Param("ouId")Long ouId);

    /**
     * 根据参数查询库位信息
     * 
     * @author lijun.shen
     * @param location
     * @return
     */
    Location findLocationByParam(Location location);
    
}
