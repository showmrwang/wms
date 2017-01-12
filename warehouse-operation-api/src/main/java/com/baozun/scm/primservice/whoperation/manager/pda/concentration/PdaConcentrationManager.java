package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;

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
	 * @param batch 
	 * @param ouId
	 * @return
	 */
	WhSeedingCollectionCommand checkContainerInTemporaryLocation(String containerCode, String batch, Long ouId);
	
	/**
	 * 判断当前容器是否有推荐结果
	 * @param containerCode
	 * @param ouId
	 * @param ouId 
	 */
	WhFacilityRecPath checkContainerHaveRecommendResult(String containerCode, String batch, Long userId, Long ouId);
	
	/**
	 * 判断是否达到可携带容量数量限制且小于播种墙容器上限
	 * @param rec
	 * @param collectionFunc
	 * @param ouId
	 * @return
	 */
	boolean checkContainerQtyLimit(WhFacilityRecPath rec, WhFunctionCollectionCommand collectionFunc, Long ouId);
	
	/**
	 * 得到缓存里面的一条推荐结果
	 * @param batch
	 * @param userId
	 * @return
	 */
	WhFacilityRecPath popRecommendResultListHead(String batch, Long userId);
	
	/**
	 * 判断小批次是否全部移动到播种墙
	 * @param batch
	 * @param ouId
	 * @return
	 */
	boolean checkBatchIsAllIntoSeedingWall(String batch, Long userId, Long ouId);
	
	/**
	 * 记录容器到播种墙上集货信息
	 * @param containerCode
	 * @param batch
	 * @param batch2 
	 * @param ouId
	 */
	void updateContainerToSeedingWall(String facilityCode, String containerCode, String batch, Long ouId);
	
	/**
	 * 清除集货推荐缓存
	 * @param batch
	 * @param userId
	 */
	void removeRecommendResultListCache(String batch, Long userId);

}
