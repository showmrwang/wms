package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhFacilityRecPathCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.command.pda.collection.WorkCollectionCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * PDA-集货
 * @author jumbo
 *
 */
public interface PdaConcentrationManager extends BaseManager {
	
	/**
	 * 出库集货-获取推荐暂存库位
	 * @return
	 */
	WhTemporaryStorageLocationCommand getRecommendTemporaryStorageLocation(Long ouId);
	
	/**
	 * 通过容器号获取集货状态
	 * @param containerCode
	 * @param ouId
	 * @return
	 */
	WhSeedingCollectionCommand checkContainerInWhere(String containerCode, Integer type, Long ouId);
	
	/**
	 * 判断当前容器是否有推荐结果
	 * @param containerCode
	 * @param ouId
	 * @param ouId 
	 */
	WhFacilityRecPathCommand checkContainerHaveRecommendResult(String containerCode, String batch, Long userId, Long ouId);
	
	/**
	 * 判断是否达到可携带容量数量限制且小于播种墙容器上限
	 * @param facilityId	设施Id
	 * @param carryQty		已携带数量
	 * @param containerQty	功能定义数量
	 * @param upperLimit	播种墙上限
	 * @param batch			批次
	 * @param ouId
	 * @return
	 */
	boolean checkContainerQtyLimit(Long facilityId, Integer carryQty, Integer containerQty, Integer upperLimit, String batch, Long ouId);
	
	/**
	 * 得到缓存里面的一条推荐结果
	 * @param batch
	 */
	WhFacilityRecPathCommand popRecommendResultListHead(String batch, Long userId);
	
	/**
	 * 得到人为集货缓存里面的一条推荐结果
	 * @param batch
	 */
	WhFacilityRecPathCommand popManualRecommendResultListHead(Long userId);
	/**
	 * 判断小批次是否全部移动到播种墙
	 * @param batch
	 * @param ouId
	 * @return
	 */
	boolean checkBatchIsAllIntoSeedingWall(String batch, Long userId, Long ouId);
	
	/**
	 * 移动容器
	 * @param destinationType
	 */
	void updateContainerSkuInventory(WhFacilityRecPathCommand recCommand, Integer destinationType, Long ouId);
	
	/**
	 * 记录容器到播种墙上集货信息
	 * @param containerCode
	 * @param batch
	 */
	void updateContainerToDestination(WhFacilityRecPathCommand rec, Integer destinationType, Long ouId);
	
	/**
	 * 清除集货推荐缓存
	 * @param batch
	 * @param userId
	 */
	void removeRecommendResultListCache(String batch, Long userId);
	
    /**
     * [业务方法] 获取推荐路径
     * @param workCollectionCommand
     * @return RecFacilityPathCommand
     */
    WorkCollectionCommand recommendSeedingWall(WorkCollectionCommand workCollectionCommand);

    /**
     * [业务方法] 获取目标位置
     * @param command
     * @return
     */
    String findTargetPos(WorkCollectionCommand command);

    /**
     * [业务方法] 校验并且移动容器
     * @param workCollectionCommand
     * @return targetPos$containerCode
     */
    Boolean checkAndMoveContainer(WorkCollectionCommand workCollectionCommand);

    /**
     * [通用方法] 清理缓存: cache+userId, batch
     * @param workCollectionCommand
     */
    void cleanCache(WorkCollectionCommand workCollectionCommand);

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
	 * @param destinationCode
	 * @param ouId
	 * @return
	 */
	int getDestinationTypeByCode(String destinationCode, Long ouId);
	
	/**
	 * 容器移动到目的地
	 * @param containerCode
	 * @param destinationCode
	 * @param destinationType 目的地类型 1:播种墙 2:暂存库位 3:中转库位
	 * @return
	 */
	boolean manualMoveContainerToDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId);

	void removeManualContainerCodeFromCache(Long userId);
	
	/**
	 * 人为集货-应用系统推荐
	 * @param containerCode
	 * @param userId
	 * @param ouId
	 */
	void useSysRecommendResult(String containerCode, Long userId, Long ouId);

	void moveContainerToDestination(WhFacilityRecPathCommand recCommand, Integer destinationType, Boolean isManual, Long userId, Long ouId);
	
	/**
	 * 通过推荐结果判断容器去哪
	 * @return
	 */
	Integer checkDestinationByRecommendResult(WhFacilityRecPathCommand rec, Long ouId);

}
