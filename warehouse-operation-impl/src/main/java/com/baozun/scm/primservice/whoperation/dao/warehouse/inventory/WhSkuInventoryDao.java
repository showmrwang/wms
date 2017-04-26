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

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingCollectionLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;
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
     * 
     * @author lichuan
     * @param ouid
     * @param outerContainerId
     * @return
     */
    List<WhSkuInventoryCommand> findContainerOnHandInventoryByOuterContainerId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId);

    /**
     * 根据外部容器查询对应待移入库存
     * 
     * @author lichuan
     * @param ouid
     * @param outerContainerId
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByOuterContainerId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId);

    /**
     * 根据外部容器查询对应待移入库存
     * 
     * @author lichuan
     * @param ouId
     * @param outerContainerId
     * @param locId
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByOuterContainerIdAndLocId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId, @Param("locId") Long locId);

    /**
     * 根据外部容器查询待移入数量
     * 
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
     * 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    List<WhSkuInventoryCommand> findContainerOnHandInventoryByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);

    /**
     * 通过内部容器号查询库位待移入库存信息
     * 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    List<WhSkuInventoryCommand> findLocToBeFilledInventoryByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);

    /**
     * 根据内部容器查询待移入数量
     * 
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
     * 
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
     * 通过UUIDLIST+OUID查询对应库存信息
     * 
     * @param ouid
     * @param uuid
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryByUuidList(@Param("ouId") Long ouid, @Param("uuidList") List<String> uuidList);

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
     * 根据外部容器获取库位库存记录数
     * 
     * @author lichuan
     * @param ouId
     * @param outerContainerId
     * @return
     */
    int findLocInventoryCountsByOuterContainerId(@Param("ouId") Long ouId, @Param("outerContainerId") Long outerContainerId);

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
     * 根据内部容器获取库位库存记录数
     * 
     * @author lichuan
     * @param ouId
     * @param insideContainerId
     * @return
     */
    int findLocInventoryCountsByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);

    /**
     * 根据内容容器获取库存总记录数
     * 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    int findToBefilledCountsByInsideContainerId(@Param("ouId") Long ouid, @Param("insideContainerId") Long insideContainerId);

    /**
     * 根据内容容器获取库存总记录数(待移入)
     * 
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
     * 根据内部容器获取收货库存数量(外部容器不为空)
     * 
     * @author lichuan
     * @param ouid
     * @param insideContainerid
     * @return
     */
    int findLocTobefilledInventoryCountsByInsideContainerId1(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);


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
     * 根据外部容器号查询所有待移入内部容器信息
     * 
     * @author lichuan
     * @param ouid
     * @param outerContainerid
     * @return
     */
    List<ContainerCommand> findTobefilledAllInsideContainerByOuterContainerId(@Param("ouid") Long ouid, @Param("outerContainerid") Long outerContainerid);

    /**
     * 通过内部容器号查询对应的库存信
     * 
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
    List<WhSkuInventory> findContainerInventoryCountsByOuterContainerId(@Param("outerContainerId") Long outerContainerId, @Param("ouId") Long ouId);


    /**
     * 根据容器号查询外部容器库存记录的数量(待验证,待删除)
     * 
     * @author lijun.shen
     * @param
     * @param
     * @return
     */
    List<WhSkuInventory> findContainerInventoryCountsByInsideContainerId(@Param("insideContainerId") Long insideContainerId, @Param("ouId") Long ouId);


    /**
     * 根据容器号查询外部容器库存记录的数量
     * 
     * @author lijun.shen
     * @param ouId
     * @param containerId
     * @return
     */
    List<WhSkuInventoryCommand> getSkuInvListByOutContainerID(@Param("ouId") Long ouId, @Param("containerId") Long containerId);


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
    // /**
    // * 根据外部容器id查询外部容器里的内部容器id值 (待验证)
    // *
    // * @author lijun.shen
    // * @param whSkuInventory
    // * @return
    // */
    // List<WhSkuInventory> findSkuInventoryByOutContainerId(WhSkuInventory whSkuInventory);


    /**
     * 通过内部/外部容器号查询对应容器库存
     * 
     * @param ouid
     * @return
     */
    public List<WhSkuInventory> findWhSkuInventoryByCId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId, @Param("outerContainerId") Long outerContainerId);

    /***
     * 根据内部容器id查询没有上架的记录(人工上架)
     * 
     * @param ouId
     * @param insideContainerId
     * @return
     */
    public int findWhSkuInventoryCountByInsideContainerId(@Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);


    /**
     * 根据容器id,查询已上架的库存信息
     * 
     * @param ouId
     * @param locationId
     * @return
     */
    public List<WhSkuInventory> findWhSkuInventoryByLocationId(@Param("ouId") Long ouId, @Param("locationId") Long locationId);


    public List<WhSkuInventoryCommand> findWhSkuInvCmdByLocation(@Param("ouId") Long ouId, @Param("locationId") Long locationId);

    /**
     * 根据库位查询一条库存记录
     * 
     * @author lichuan
     * @param ouId
     * @param locationId
     * @return
     */
    public WhSkuInventoryCommand findFirstWhSkuInvCmdByLocation(@Param("ouId") Long ouId, @Param("locationId") Long locationId);

    /***
     * 根据库位id查询内部容器Id，外部容器id都为空的库存记录
     * 
     * @param ouId
     * @param locationId
     * @return
     */
    public List<WhSkuInventoryCommand> findWhSkuInvCmdByLocationContainerIdIsNull(@Param("ouId") Long ouId, @Param("locationId") Long locationId);

    /**
     * 按入库时间顺序查找库存
     * 
     * @param whWaveLineCommand
     * @return
     */
    List<WhSkuInventoryCommand> findInventoryByInBoundTime(WhSkuInventoryCommand invCommand);

    /**
     * 按商品保质期时间顺序查找库存
     * 
     * @param whWaveLineCommand
     * @return
     */
    List<WhSkuInventoryCommand> findInventoryByExpTime(WhSkuInventoryCommand invCommand);

    /**
     * 按照库位上数量多少查找库存
     * 
     * @param whWaveLineCommand
     * @return
     */
    List<WhSkuInventoryCommand> findInventoryByLocation(WhSkuInventoryCommand invCommand);

    /**
     * 按照数量多少查找库存
     * 
     * @param whWaveLineCommand
     * @return
     */
    List<WhSkuInventoryCommand> findInventoryByAmount(WhSkuInventoryCommand invCommand);

    /**
     * 按照Uuid和其他条件查找库存
     * 
     * @param invCommand
     * @return
     */
    List<WhSkuInventoryCommand> findInventoryByUuidAndCondition(WhSkuInventoryCommand invCommand);

    List<WhSkuInventoryCommand> findInventoryUuidByInBoundTime(WhSkuInventoryCommand invCommand);

    List<WhSkuInventoryCommand> findInventoryUuidByExpTime(WhSkuInventoryCommand invCommand);

    List<WhSkuInventoryCommand> findInventoryUuidByLocation(WhSkuInventoryCommand invCommand);

    List<WhSkuInventoryCommand> findInventoryUuidByAmount(WhSkuInventoryCommand invCommand);

    List<WhSkuInventoryCommand> findInventoryUuidByBestMatch(WhSkuInventoryCommand invCommand);

    List<WhSkuInventoryCommand> findInventoryUuid(WhSkuInventoryCommand invCommand);

    /**
     * 根据占用码找占用的库存
     */
    List<WhSkuInventory> findOccupyInventory(@Param("occupyCode") String occupyCode, @Param("ouId") Long ouId);

    /**
     * 释放库存
     */
    int releaseInventoryOccupyCode(@Param("occupyCode") String occupyCode, @Param("ouId") Long ouId);

    /**
     * 根据外部容器库存查询待上架库存信息
     * 
     * @param pdaManMadePutawayCommand
     * @param
     * @param
     * @return
     */
    List<WhSkuInventory> findWhSkuInventoryCountsByContainerId(@Param("outerContainerId") Long outerContainerId, @Param("ouId") Long ouId, @Param("insideContainerId") Long insideContainerId);

    /**
     * 查找容器中放了哪些商品
     * 
     * @param insideContainerId
     * @param ouId
     * @return
     */
    List<Long> findSkuIdListFromInventory(@Param("insideContainerId") Long insideContainerId, @Param("ouId") Long ouId);

    /**
     * 库内移动查询出库存信息
     * 
     * @author qiming.liu
     * @param whSkuInventory
     * @return
     */
    List<WhSkuInventory> getSkuInvListByPramas(WhSkuInventory whSkuInventory);

    /**
     * 库内移动查询出库存信息
     * 
     * @author qiming.liu
     * @param whSkuInventoryCommand
     * @return
     */
    List<WhSkuInventoryCommand> findInvComLstByInWarehouseMove(WhSkuInventoryCommand whSkuInventoryCommand);

    /**
     * 根据库位Id集合查询库位库存信息
     * 
     * @param ouId
     * @param locIdList
     * @return
     */
    public List<WhSkuInventoryCommand> findWhSkuInventoryByLocationIds(@Param("ouId") Long ouId, @Param("locIdList") List<Long> locIdList);

    /**
     * 根据参数查询出库存信息并根据uuid分组--创拣货工作
     * 
     * @author qiming.liu
     * @param whSkuInventory
     * @return
     */
    List<WhSkuInventory> getSkuInvListGroupUuid(WhSkuInventory whSkuInventory);

    /**
     * 根据单据号查找库存
     * 
     * @param odoCode
     * @param ouId
     */
    List<WhSkuInventory> findbyOccupationCode(@Param("occupationCode") String odoCode, @Param("ouId") Long ouId);

    /**
     * 根据uuid查找库存可用量
     * 
     * @param uuidList
     * @param ouId
     * @return
     */
    Double getUseableQtyByUuidList(@Param("uuidList") List<String> uuidList, @Param("ouId") Long ouId);

    /**
     * 根据uuid查找库存可用量
     * 
     * @param uuidList
     * @param ouId
     * @return
     */
    Double getUseableQtyByUuid(@Param("uuid") String uuid, @Param("ouId") Long ouId);

    public List<WhSkuInventoryCommand> getWhSkuInventoryByOccupationLineId(@Param("locationId") Long locationId, @Param("ouId") Long ouId, @Param("operationId") Long operationId, @Param("outerContainerId") Long outerContainerId,
            @Param("insideContainerId") Long insideContainerId);

    List<WhSkuInventoryCommand> findSkuInvByLocationIds(@Param("locationIds") List<Long> locationIds, @Param("allocateUnitCodes") String unitCodes, @Param("ouId") Long ouId);

    // public List<WhSkuInventoryCommand>
    // getWhSkuInventoryTobefilledByOccupationLineId(@Param("ouId") Long ouId,@Param("operationId")
    // Long operationId);


    public List<WhSkuInventoryCommand> getWhSkuInventoryCommandByNoWave(@Param("skuId") Long skuId, @Param("replenishmentCode") String replenishmentCode, @Param("ouId") Long ouId, @Param("locationId") Long locationId,
            @Param("outerContainerId") Long outerContainerId, @Param("insideContainerId") Long insideContainerId);


    public List<WhSkuInventoryCommand> getWhSkuInventoryTobefilledByWave(@Param("ouId") Long ouId, @Param("turnoverBoxId") Long turnoverBoxId, @Param("locationId") Long locationId, @Param("operationId") Long operationId);

    /**
     * 校验容器库存(废除)
     * 
     * @param ouId
     * @param operationId
     * @return
     */
    public List<WhSkuInventoryCommand> checkReplenishmentInventory(@Param("ouId") Long ouId, @Param("operationId") Long operationId);


    public List<WhSkuInventoryCommand> findReplenishmentBylocationId(@Param("turnoverBoxId") Long turnoverBoxId,@Param("ouId") Long ouId, @Param("locationId") Long locationId, @Param("odoLineId") Long odoLineId, @Param("odoId") Long odoId);

    List<WhSeedingCollectionLine> findSeedingDataByContainerId(@Param("containerId") Long containerId, @Param("ouId") Long ouId);


    public List<WhSkuInventoryCommand> getWhSkuInventoryCmdByuuid(@Param("locationId") Long locationId, @Param("skuId") Long skuId, @Param("uuid") String uuid, @Param("ouId") Long ouId);


    public List<WhSkuInventoryCommand> findWhSkuInventoryCmdByuuid(@Param("outerContainerId") Long outerContainerId, @Param("insideContainerId") Long insideContainerId, @Param("skuId") Long skuId, @Param("uuid") String uuid, @Param("ouId") Long ouId);

    /**
     * 根据占用码查询库存
     * 
     * @author mingwei.xie
     * @param occupationCode
     * @param ouId
     * @return
     */
    public List<WhSkuInventoryCommand> findListByOccupationCode(@Param("occupationCode") String occupationCode, @Param("ouId") Long ouId);


    /**
     * 根据占用码查询库存
     * 
     * @author mingwei.xie
     * @param occLineIdList
     * @param ouId
     * @return
     */
    public List<WhSkuInventoryCommand> findListByOccLineIdListOrderByPickingSort(@Param("occLineIdList") List<Long> occLineIdList, @Param("ouId") Long ouId);

    List<WhSkuInventoryCommand> findInventoryByPo(@Param("poId") Long id, @Param("ouId") Long ouId);

    List<WhSkuInventoryCommand> findUseableInventoryByOuterContainerId(@Param("outerContainerId") Long outerContainerId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);

    List<WhSkuInventoryCommand> findUseableInventoryByInsideContainerId(@Param("insideContainerId") Long insideContainerId, @Param("skuId") Long skuId, @Param("ouId") Long ouId);

    List<WhSkuInventory> findSkuInventoryByContainer(WhSkuInventory inventory);

    /**
     * 查询库存信息
     * 
     * @author qiming.liu
     * @param occupationCode
     * @param occupationLineId
     * @param uuid
     * @param ouid
     * @return
     */
    WhSkuInventoryCommand findInvLstByOccupationCode(@Param("occupationCode") String occupationCode, @Param("occupationLineId") Long occupationLineId, @Param("uuid") String uuid, @Param("ouid") Long ouid);

    /**
     * [业务方法] 通过内部容器id查找容器中的库存明细并插入播种墙集货明细表
     * 
     * @param containerId
     * @param ouId
     * @return
     */
    List<WhSeedingCollectionLineCommand> findListByContainerId(@Param("containerId") Long containerId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过内部容器id查找容器中的库存明细并插入播种墙集货明细表 groupBy odoLine
     * 
     * @param containerId
     * @param ouId
     * @return
     */
    List<WhSeedingCollectionLineCommand> findListByContainerIdExt(@Param("containerId") Long containerId, @Param("ouId") Long ouId);

    /***
     * 补货查询已分配库存记录
     * 
     * @param ouId
     * @param operationId
     * @return
     */
    List<WhSkuInventoryCommand> getWhSkuInventoryCommandByOperationId(@Param("ouId") Long ouId, @Param("operationId") Long operationId, @Param("locationId") Long locationId, @Param("outerContainerId") Long outerContainerId,
            @Param("insideContainerId") Long insideContainerId);

    /**
     * 按照占用码删除库存，转换库存时使用
     * 
     * @param occupationCode
     * @param ouId
     * @return
     */
    int deleteByOccupationCode(@Param("occupationCode") String occupationCode, @Param("ouId") Long ouId);


    /***
     * 查询容器库存(按单复合)
     * 
     * @param ouId
     * @param operationId
     * @return
     */
    List<WhSkuInventoryCommand> getWhSkuInventoryCommandByOdo(@Param("odoLineId") Long odoLineId, @Param("odoId") Long odoId, @Param("ouId") Long ouId, @Param("containerId") Long containerId, @Param("containerLatticeNo") Integer containerLatticeNo,
            @Param("outboundbox") String outboundbox, @Param("turnoverBoxId") Long turnoverBoxId, @Param("seedingWallCode") String seedingWallCode);


    /**
     * 通过出库箱编码删除对应库存记录
     * 
     * @param outboundboxCode
     * @param ouid
     * @return
     */
    int deleteWhSkuInventoryByOutboundboxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);

    /**
     * 通过出库箱编码查询对应库存记录
     * 
     * @param outboundboxCode
     * @param ouid
     * @return
     */
    List<WhSkuInventory> findSkuInvByoutboundboxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 通过内部容器id 外部容器id 货格编码数 查找容器中的库存明细并插入复核集货明细表
     * 
     * @param containerId
     * @param outerContainerId
     * @param outboundboxCode
     * @param containerId
     * @param containerLatticeNo
     * @param ouId
     * @return
     */
    List<WhCheckingCollectionLine> findWhCheckingCollectionListByContainerId(@Param("insideContainerId") Long insideContainerId, @Param("outerContainerId") Long outerContainerId, @Param("containerLatticeNo") Integer containerLatticeNo,
            @Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);


    /***
     * 查询库位库存
     * 
     * @param ouId
     * @param operationId
     * @return
     */
    List<WhSkuInventoryCommand> findWhSkuInventoryCommandByReplish(@Param("ouId") Long ouId, @Param("locationId") Long locationId, @Param("outerContainerId") Long outerContainerId, @Param("insideContainerId") Long insideContainerId);



    public List<WhSkuInventory> findSeedingOdoSkuInvByOdoLineIdUuid(@Param("odoLineId") Long odoLineId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * 根据库位id查询库存量
     * 
     * @param ouId
     */
    Long findOnHandQtyByLocationId(@Param("locationId") Long locationId, @Param("ouId") Long ouId);
}
