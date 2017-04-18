package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.collection.WorkCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhFacilityRecPathCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationExecLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

/**
 * PDA-集货
 * 
 * @author jumbo
 * 
 */
public interface PdaConcentrationManager extends BaseManager {

    /**
     * [业务方法] 插入集货表
     * 
     * @param batch
     * @param workId
     * @param ouId
     */
    void insertIntoSeedingCollection(String batch, Long workId, Long ouId);

    /**
     * [业务方法] 插入集货表
     * 
     * @author lichuan
     * @param batch
     * @param execLineCommandList
     * @param ouId
     */
    void insertIntoSeedingCollection(String batch, Long workId, List<WhOperationExecLineCommand> execLineCommandList, Long ouId);

    /**
     * [业务方法] 插入集货明细表
     * 
     * @param whSeedingCollection
     */
    void insertIntoSeedingCollectionLine(WhSeedingCollection whSeedingCollection);

    /**
     * [业务方法] 插入复核台集货表
     * 
     * @param batch
     * @param workId
     * @param ouId
     */
    void insertIntoWorkingCollection(String batch, Long workId, Long ouId, WhWorkCommand work);

    /**
     * [业务方法] 插入复核台集货表
     * 
     * @author lichuan
     * @param batch
     * @param execLineCommandList
     * @param ouId
     * @param work
     */
    void insertIntoWorkingCollection(String batch, List<WhOperationExecLineCommand> execLineCommandList, Long ouId, WhWorkCommand work);


    /**
     * [通用方法] 批次号, 组织id, 是否是最后一个拣货容器
     * 
     * @param batch
     * @param ouId
     * @param isLastContainer
     * @return
     */
    WorkCollectionCommand createObject(String batch, Long workId, Long ouId, Boolean isLastContainer, Long scanContainerId);

    /**
     * 出库集货-获取推荐暂存库位
     * 
     * @return
     */
    WhTemporaryStorageLocationCommand getRecommendTemporaryStorageLocation(Long ouId);

    /**
     * 通过容器号获取集货状态
     * 
     * @param containerCode
     * @param ouId
     * @return
     */
    WhSeedingCollectionCommand checkContainerInWhere(String containerCode, Integer type, Long ouId);

    /**
     * 判断当前容器是否有推荐结果
     * 
     * @param containerCode
     * @param ouId
     * @param ouId
     */
    WhFacilityRecPathCommand checkContainerHaveRecommendResult(String containerCode, String batch, Long userId, Long ouId);

    /**
     * 判断是否达到可携带容量数量限制且小于播种墙容器上限
     * 
     * @param facilityId 设施Id
     * @param carryQty 已携带数量
     * @param containerQty 功能定义数量
     * @param upperLimit 播种墙上限
     * @param batch 批次
     * @param ouId
     * @return
     */
    boolean checkContainerQtyLimit(Long facilityId, Integer carryQty, Integer containerQty, Integer upperLimit, String batch, Long ouId);

    /**
     * 得到缓存里面的一条推荐结果
     * 
     * @param batch
     */
    WhFacilityRecPathCommand popRecommendResultListHead(String batch, Long userId);

    /**
     * 得到人为集货缓存里面的一条推荐结果
     * 
     * @param batch
     */
    WhFacilityRecPathCommand popManualRecommendResultListHead(Long userId);

    /**
     * 判断小批次是否全部移动到播种墙
     * 
     * @param batch
     * @param ouId
     * @return
     */
    boolean checkBatchIsAllIntoSeedingWall(String batch, Long userId, Long ouId);

    /**
     * 移动容器
     * 
     * @param destinationType
     */
    void updateContainerSkuInventory(WhFacilityRecPathCommand recCommand, Integer destinationType, Long ouId);

    /**
     * 记录容器到播种墙上集货信息
     * 
     * @param containerCode
     * @param batch
     */
    void updateContainerToDestination(WhFacilityRecPathCommand rec, Integer destinationType, Long ouId);

    /**
     * 清除集货推荐缓存
     * 
     * @param batch
     * @param userId
     */
    void removeRecommendResultListCache(String batch, Long userId);

    /**
     * [业务方法] 获取推荐路径
     * 
     * @param workCollectionCommand
     * @return RecFacilityPathCommand
     */
    WorkCollectionCommand recommendSeedingWall(WorkCollectionCommand workCollectionCommand);

    /**
     * [业务方法] 获取目标位置
     * 
     * @param command
     * @return
     */
    String findTargetPos(WorkCollectionCommand command);

    /**
     * [业务方法] 校验并且移动容器
     * 
     * @param workCollectionCommand
     * @return targetPos$containerCode
     */
    Boolean checkAndMoveContainer(WorkCollectionCommand workCollectionCommand);

    /**
     * [业务方法] 校验并且记录库存
     * 
     * @param workCollectionCommand
     * @return targetPos$containerCode
     */
    Boolean checkAndRecordInventory(WorkCollectionCommand workCollectionCommand);

    /**
     * [通用方法] 清理缓存: cache+userId, batch
     * 
     * @param workCollectionCommand
     */
    void cleanCache(WorkCollectionCommand workCollectionCommand);

    /**
     * [通用方法] 补偿机制: cache+userId, batch
     * 
     * @param workCollectionCommand
     */
    void compensationCache(WorkCollectionCommand workCollectionCommand);

    void addManualContainerCodeIntoCache(String containerCode, Long userId, Long ouId);

    /**
     * 判断推荐结果表中当前容器对应的小批次是否关联当前目的地
     */
    boolean checkContainerAssociatedWithDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId);

    /**
     * 判断人为集货进入目的地之前扫描容器不为null
     */
    boolean checkManualContainerCacheNotNull(Boolean isApplyFacility, Long userId);

    /**
     * 得到目的地类型
     * 
     * @param destinationCode
     * @param ouId
     * @return
     */
    int getDestinationTypeByCode(String destinationCode, Long ouId);

    /**
     * 容器移动到目的地
     * 
     * @param containerCode
     * @param destinationCode
     * @param destinationType 目的地类型 1:播种墙 2:暂存库位 3:中转库位
     * @return
     */
    boolean manualMoveContainerToDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId);

    void removeManualContainerCodeFromCache(Long userId);

    /**
     * 人为集货-应用系统推荐
     * 
     * @param containerCode
     * @param userId
     * @param ouId
     */
    void useSysRecommendResult(String containerCode, Long userId, Long ouId);

    void moveContainerToDestination(WhFacilityRecPathCommand recCommand, Integer destinationType, Boolean isManual, Long userId, Long ouId);

    /**
     * 通过推荐结果判断容器去哪
     * @param ouId 
     * 
     * @return
     */
    Integer checkDestinationByRecommendResult(WhFacilityRecPathCommand rec, Long userId, Long ouId);

    /**
     * 检测此批次在暂存库位上是否还有容器
     * @author kai.zhu
     * @version 2017年4月14日
     */
    boolean checkBatchInTemporaryStorageLocation(String batch, Long ouId);

}
