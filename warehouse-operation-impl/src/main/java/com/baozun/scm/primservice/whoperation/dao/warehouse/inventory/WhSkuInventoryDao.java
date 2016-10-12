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

import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

public interface WhSkuInventoryDao extends BaseDao<WhSkuInventory, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhSkuInventory> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhSkuInventory> query(Page page, Sort[] sorts, QueryCondition cond);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhSkuInventorySnCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    List<WhSkuInventory> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhSkuInventory o);

    @CommonQuery
    int saveOrUpdateByVersion(WhSkuInventory o);

    /**
     * 根据库存ID+UUID+OUID查询对应库存
     */
    WhSkuInventoryCommand findWhSkuInventoryByIdAndUuid(@Param("id") Long id, @Param("uuid") String uuid, @Param("ouid") Long ouid);

    /**
     * 根据skuId uuid ouId统计商品库存
     * 
     * @author lichuan
     * @param skuId
     * @param uuid
     * @param ouId
     * @return
     */
    WhSkuInventoryCommand findWhSkuInventoryByIdGroupByUuid(@Param("skuId") Long skuId, @Param("uuid") String uuid, @Param("ouId") Long ouId);

    /**
     * 获取所有可用库存行
     * 
     * @author lichuan
     * @param skuId
     * @param uuid
     * @param ouId
     * @return
     */
    List<WhSkuInventoryCommand> findAllValidInventoryBySkuAndUuid(@Param("skuId") Long skuId, @Param("uuid") String uuid, @Param("ouId") Long ouId, @Param("expectQty") Double expectQty);

    /**
     * 占用库存
     * 
     * @author lichuan
     * @param eQty
     * @param occupyCode
     * @param id
     * @return
     */
    int occupyInvByCodeAndId(@Param("eQty") Double eQty, @Param("occupyCode") String occupyCode, @Param("id") Long id);

    /**
     * 占用库存校验
     * 
     * @author lichuan
     * @param eQty
     * @param occupyCode
     * @return
     */
    List<WhSkuInventoryCommand> validateOccupyByExpectQty(@Param("eQty") Double eQty, @Param("occupyCode") String occupyCode);

    /**
     * 释放库存存
     * 
     * @author lichuan
     * @param occupyCode
     * @return
     */
    long releaseOccupiedInventory(@Param("occupyCode") String occupyCode);

    /**
     * 通过容器号查询对用的库存信息
     * 
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);

    /**
     * 通过容器号查询SN库存信息
     *
     * @author mingwei.xie
     * @param ouId
     * @param insideContainerIdList
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryForRuleByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerIdList") List<Long> insideContainerIdList);

    /**
     * 通过外部容器号查询对用的库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByOuterContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);

    /**
     * 通过外部容器号查询库位待移入库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByOuterContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);

    /**
     * 通过内部容器号查询对应的库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByInsideContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);
    
    /**
     * 根据外部容器查询对应容器库存
     * @author lichuan
     * @param ouid
     * @param outerContainerId
     * @return
     */
    List<WhSkuInventoryCommand> findContainerOnHandInventoryByOuterContainerId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId);
    
    /**
     * 根据外部容器查询对应待移入库存
     * @author lichuan
     * @param ouid
     * @param outerContainerId
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByOuterContainerId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId);
    
    /**
     * 根据外部容器查询对应待移入库存
     * @author lichuan
     * @param ouId
     * @param outerContainerId
     * @param locId
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByOuterContainerIdAndLocId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId, @Param("locId") Long locId);
    
    /**
     * 根据外部容器查询待移入数量
     * @author lichuan
     * @param ouid
     * @param outerContainerId
     * @return
     */
    int findLocToBeFilledInventoryCountsByOuterContainerId(@Param("ouId") Long ouid, @Param("outerContainerId") Long outerContainerId);
    
    /**
     * 根据内部容器查询对应的容器库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findContainerOnHandInventoryByInsideContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);
    
    /**
     * 通过内部容器号查询库位待移入库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByInsideContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);
    
    
    /**
     * 根据内部容器查询对应的容器库存信息 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    List<WhSkuInventoryCommand> findContainerOnHandInventoryByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);
    
    /**
     * 通过内部容器号查询库位待移入库存信息
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);
    
    /**
     * 根据内部容器查询待移入数量
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    int findLocToBeFilledInventoryCountsByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);

    /**
     * 根据容器号及绑定库位查询对应的库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @param locid
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByInsideContainerCodeAndLoc(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList, @Param("locid") Long locid);

    /**
     * 根据容器号及绑定库位查询库位待移入库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @param locid
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByInsideContainerCodeAndLoc(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList, @Param("locid") Long locid);
    
    /**
     * 根据容器号及绑定库位查询库位待移入库存信息
     * @author lichuan
     * @param ouId
     * @param insideContainerId
     * @param locId
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByInsideContainerIdAndLocId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId, @Param("locId") Long locId);

    /**
     * 通过外部容器号查询对用的库存信息的所有库位
     * 
     * @author lichuan
     * @param ouid
     * @param containerList
     * @return
     */
    List<LocationCommand> findWhSkuInventoryLocByOuterContainerCode(@Param("ouid") Long ouid, @Param("containerList") List<String> containerList);

    /***
     * 通过内部容器号+SKUCODE查询对应库存信息
     * 
     * @param ouid
     * @param skuCode
     * @param containerId
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryBySkuCodeAndContainerId(@Param("ouid") Long ouid, @Param("skuCode") String skuCode, @Param("containerId") Long containerId);

    /**
     * 通过ID+OUID查询对应库存记录
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhSkuInventory findWhSkuInventoryById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * 通过UUID+OUID查询对应库存信息
     * 
     * @param ouid
     * @param uuid
     * @return
     */
    WhSkuInventory findWhSkuInventoryByUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * 通过id+ouid删除对应库存记录
     * 
     * @param id
     * @param ouid
     * @return
     */
    int deleteWhSkuInventoryById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * 根据容器ID获取容器去重的商品库存属性
     * 
     * @param insideContainerId 必填
     * @param skuId 可选
     * @param ouId 必填
     * @return
     */
    List<RcvdContainerCacheCommand> getUniqueSkuAttrFromWhSkuInventory(@Param("insideContainerId") Long insideContainerId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);

    /**
     * 获取容器混放SKU数
     * 
     * @param insideContainerId 必填
     * @param skuId 可选
     * @param ouId 必填
     * @return
     */
    long getUniqueSkuAttrCountFromWhSkuInventory(@Param("insideContainerId") Long insideContainerId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);

    /**
     * 根据sku+内部容器号或者外部容器号 查找容器库存数据 location is null
     * 
     * @param ouid
     * @param skuid
     * @param
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryBySkuIdAndContainerid(@Param("ouid") Long ouid, @Param("skuid") Long skuid, @Param("insideContainerid") Long insideContainerid, @Param("outerContainerid") Long outerContainerid);

    /**
     * 通过商品对应库存属性 查找对应库存记录
     * 
     * @param whSkuInventoryCommand
     * @return
     */
    WhSkuInventoryCommand findWhSkuInventoryBySkuAttr(WhSkuInventoryCommand whSkuInventoryCommand);

    /**
     * 根据外部容器获取收货库存记录数
     * 
     * @author lichuan
     * @param ouid
     * @param outerContainerid
     * @return
     */
    int findRcvdInventoryCountsByOuterContainerId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId);

    /**
     * 根据内部容器获取收货库存记录数
     * 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    int findRcvdInventoryCountsByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);
    
    /**
     * 根据内容容器获取库存总记录数
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    int findAllInventoryCountsByInsideContainerId(@Param("ouId") Long ouid, @Param("insideContainerId") Long insideContainerId);

    /**
     * 根据内部容器获取收货库存数量(外部容器不为空)
     * 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    int findRcvdInventoryCountsByInsideContainerId1(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);

    /**
     * 根据uuid查询所有库存记录行的在库数量总和
     * 
     * @author lichuan
     * @param ouid
     * @param uuid
     * @return
     */
    double findInventorysAllOnHandQtysByUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * 根据uuid查询所有库存记录行
     * 
     * @author lichuan
     * @param ouid
     * @param uuid
     * @return
     */
    List<WhSkuInventoryCommand> findInventorysByUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * 根据uuid查询所有已分配的外部容器库存
     * 
     * @author lichuan
     * @param ouid
     * @param uuid
     * @return
     */
    List<WhSkuInventoryCommand> findAllocatedContainerInventorysByUuid(@Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * 根据库位查找所有库存
     * 
     * @author lichuan
     * @param ouid
     * @param locid
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByLocIdAndOuId(@Param("ouid") Long ouid, @Param("locid") Long locid);

    /**
     * 根据外部容器号查询所有内部容器信息
     * 
     * @author lichuan
     * @param ouid
     * @param outerContainerid
     * @return
     */
    List<ContainerCommand> findAllInsideContainerByOuterContainerId(@Param("ouid") Long ouid, @Param("outerContainerid") Long outerContainerid);
    
    /**
     * 通过内部容器号查询对应的库存信
     * @author tangming
     * @param ouid
     * @param containerList
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByInsideContainerId(@Param("ouid") Long ouid, @Param("containerList") List<Long> containerList);

    /**
     * 根据容器号查询外部容器库存记录的数量
     * 
     * @author lijun.shen(待验证,待删除)
     * @param pdaManMadePutawayCommand
     * @param
     * @param
     * @return
     */
    List<WhSkuInventory> findContainerInventoryCountsByOuterContainerId(@Param("outerContainerId")Long outerContainerId,@Param("ouId") Long ouId);


    /**
     * 根据容器号查询外部容器库存记录的数量(待验证,待删除)
     * 
     * @author lijun.shen
     * @param
     * @param
     * @return
     */
    List<WhSkuInventory> findContainerInventoryCountsByInsideContainerId(@Param("insideContainerId") Long insideContainerId,@Param("ouId") Long ouId);


    /**
     * 根据容器号查询外部容器库存记录的数量
     * 
     * @author lijun.shen
     * @param ouId
     * @param containerId
     * @return
     */
    List<WhSkuInventoryCommand> getSkuInvListByOutContainerID(@Param("ouId")Long ouId,@Param("containerId") Long containerId);


    /**
     * 通过内部/外部容器号查询对应容器库存 location_id is null
     * 
     * @param ouid
     * @return
     */
    List<WhSkuInventory> findWhSkuInventoryByContainerIdLocationIsNull(@Param("ouid") Long ouid, @Param("insideContainerId") Long insideContainerId, @Param("outerContainerId") Long outerContainerId);


    /**
     * @author lijun.shen
     * @param inventory
     * @return
     */
    List<WhSkuInventory> findWhSkuInventoryByPramas(WhSkuInventory inventory);
//
//    /**
//     * 根据外部容器id查询外部容器里的内部容器id值 (待验证)
//     * 
//     * @author lijun.shen
//     * @param whSkuInventory
//     * @return
//     */
//    List<WhSkuInventory> findSkuInventoryByOutContainerId(WhSkuInventory whSkuInventory);
    
    
    /**
     * 通过内部/外部容器号查询对应容器库存 
     * 
     * @param ouid
     * @return
     */
    public List<WhSkuInventory> findWhSkuInventoryByCId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId, @Param("outerContainerId") Long outerContainerId);
    
    /***
     * 根据内部容器id查询没有上架的记录(人工上架)
     * @param ouId
     * @param insideContainerId
     * @return
     */
    public int findWhSkuInventoryCountByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);
    

}
