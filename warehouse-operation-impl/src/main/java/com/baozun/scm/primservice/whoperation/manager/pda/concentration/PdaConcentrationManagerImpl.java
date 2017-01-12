package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhTemporaryStorageLocationDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;

@Transactional
@Service("pdaConcentrationManager")
public class PdaConcentrationManagerImpl extends BaseManagerImpl implements PdaConcentrationManager {
	
	@Autowired
	private WhSeedingCollectionDao whSeedingCollectionDao;
	@Autowired
	private WhFacilityRecPathDao whFacilityRecPathDao;
	@Autowired
	private WhOutboundFacilityDao whOutboundFacilityDao;
	@Autowired
	private WhTemporaryStorageLocationDao whTemporaryStorageLocationDao;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private ContainerDao containerDao;
	
	/**
	 * 出库集货-获取系统推荐暂存库位
	 * @return
	 */
	@Override
	public WhTemporaryStorageLocationCommand getRecommendTemporaryStorageLocation(Long ouId) {
		// 找占用或者正在播种且对应暂存库位有容器的播种墙
		List<WhOutboundFacilityCommand> facilityList = whOutboundFacilityDao.getSeedingFacility(ouId);
		if (null == facilityList || facilityList.isEmpty()) {
			return whTemporaryStorageLocationDao.findByIdExt(12100012L, ouId);
		}
		WhOutboundFacilityCommand facility = null;
		for (WhOutboundFacilityCommand command : facilityList) {
			Long fid = command.getId();
			String batch = command.getBatch();
			// 获取正在播种墙上播种的数量
			Integer count = whSeedingCollectionDao.getSeedingNumFromFacility(fid, batch, ouId);
			// 判断是否有已占用未移动的播种墙
			if (count == 0) {
				facility = command;
				break;
			}
			command.setSeedingCount(count);
		}
		if (null != facility) {
			return getTemporaryStorageLocationBySeedingWall(facility);
		}
		// 循环播种墙计算达到容量下限的播种墙
		for (WhOutboundFacilityCommand command : facilityList) {
			Integer lowerLimit = command.getFacilityLowerLimit();	// 播种工作数量下限
			Integer seedingCount = command.getSeedingCount();		// 正在播种数
			if (seedingCount.intValue() < lowerLimit.intValue()) {
				if (null == facility) {
					facility = command;
					continue;
				}
				Integer count = lowerLimit - seedingCount;
				if (count.intValue() > facility.getFacilityLowerLimit().intValue() - facility.getSeedingCount().intValue()) {
					facility = command;
				}
			}
		}
		if (null != facility) {
			return getTemporaryStorageLocationBySeedingWall(facility);
		}
		// 循环播种墙计算容量没有达到上限的播种墙
		for (WhOutboundFacilityCommand command : facilityList) {
			Integer upperLimit = command.getFacilityUpperLimit();	// 播种工作数量上限
			Integer seedingCount = command.getSeedingCount();		// 正在播种数
			if (seedingCount.intValue() < upperLimit.intValue()) {
				if (null == facility) {
					facility = command;
					continue;
				}
				Integer count = upperLimit - seedingCount;
				if (count.intValue() > facility.getFacilityUpperLimit().intValue() - facility.getSeedingCount().intValue()) {
					facility = command;
				}
			}
		}
		if (null != facility) {
			return getTemporaryStorageLocationBySeedingWall(facility);
		}
		throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
	}
	
	private WhTemporaryStorageLocationCommand getTemporaryStorageLocationBySeedingWall(WhOutboundFacilityCommand facility) {
		WhTemporaryStorageLocationCommand command = whTemporaryStorageLocationDao.getTemporaryStorageLocationBySeedingWall(facility);
		return command;
	}
	
	/**
	 * 通过容器号获取集货状态
	 */
	@Override
	public WhSeedingCollectionCommand checkContainerInTemporaryLocation(String containerCode, String batch, Long ouId) {
		WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.checkContainerCodeInSeedingCollection(containerCode, batch, ouId);
		if (null == seedingCollection) {
			throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_DATA_NULL_ERROR, new Object[] { containerCode });
		}
		if (null == seedingCollection.getTemporaryLocationId()) {
			throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_NOT_IN_TEMPORARYLOCATION, new Object[] { containerCode });
		}
		return seedingCollection;
	}
	
	/**
	 * 判断当前容器是否有推荐结果
	 */
	@Override
	public WhFacilityRecPath checkContainerHaveRecommendResult(String containerCode, String batch, Long userId, Long ouId) {
		// cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		WhFacilityRecPath rec = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
		List<WhFacilityRecPath> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		if (null == recPathList) {
			recPathList = new ArrayList<WhFacilityRecPath>();
		}
		for (WhFacilityRecPath recPath : recPathList) {
			if (recPath.getContainerCode().equals(containerCode)) {
				throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] { containerCode });
			}
		}
		recPathList.add(rec);
		cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch, recPathList, CacheConstants.CACHE_ONE_DAY);
		return rec;
	}
	
	/**
	 * 判断是否达到可携带容量数量限制且小于播种墙容器上限
	 */
	@Override
	public boolean checkContainerQtyLimit(WhFacilityRecPath rec, WhFunctionCollectionCommand collectionFunc, Long ouId) {
		Integer carryQty = collectionFunc.getCarryQty();		// 操作人员实际携带数量
        Integer containerQty = collectionFunc.getContainerQty();// 功能定义携带数量
        Integer upperLimit = rec.getSeedingwallUpperLimit();	// 播种墙上线数量
        WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(rec.getSeedingwallCode(), ouId);
        Integer seedingCount = whSeedingCollectionDao.getSeedingNumFromFacility(facility.getId(), rec.getBatch(), ouId);
        if (carryQty.intValue() == containerQty.intValue() || (carryQty.intValue() + seedingCount.intValue()) == upperLimit.intValue()) {
			return true;
		}
		return false;
	}

	@Override
	public WhFacilityRecPath popRecommendResultListHead(String batch, Long userId) {
		WhFacilityRecPath rec = null;
		List<WhFacilityRecPath> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		if (null != recPathList && !recPathList.isEmpty()) {
			rec = recPathList.get(0);
			recPathList.remove(0);
			cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch, recPathList, CacheConstants.CACHE_ONE_DAY);
		}
		return rec;
	}
	
	/**
	 * 判断小批次是否全部移动到播种墙
	 */
	@Override
	public boolean checkBatchIsAllIntoSeedingWall(String batch, Long userId, Long ouId) {
		cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		int count = whSeedingCollectionDao.checkBatchIsAllIntoSeedingWall(batch, ouId);
		if (count == 0) {
			String temporaryStorageLocationCode = whFacilityRecPathDao.findTemporaryStorageLocationCodeByBatch(batch, ouId);
			// 全部移动到播种墙则释放暂存库位
			WhTemporaryStorageLocation location = whTemporaryStorageLocationDao.findByCodeAndOuId(temporaryStorageLocationCode, ouId);
			location.setStatus(1);
			location.setBatch(null);
			whTemporaryStorageLocationDao.saveOrUpdateByVersion(location);
			return true;
		}
		return false;
	}
	
	/**
	 * 记录容器到播种墙上集货信息
	 */
	@Override
	public void updateContainerToSeedingWall(String facilityCode, String containerCode, String batch, Long ouId) {
		WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(facilityCode, ouId);
		if (null == facility) {
			throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
		}
		ContainerCommand container = containerDao.getContainerByCode(containerCode, ouId);
		if (null == container) {
			throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
		}
		if (StringUtils.isEmpty(batch)) {
			throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
		}
		int updateCount = whSeedingCollectionDao.deleteContainerInSeedingWall(container.getId(), batch, ouId);
		if (updateCount != 1) {
			throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
		}
		WhSeedingCollection collection = new WhSeedingCollection();
		collection.setFacilityId(facility.getId());
		collection.setContainerId(container.getId());
		collection.setBatch(batch);
		collection.setOuId(ouId);
		collection.setCollectionStatus(CollectionStatus.TO_SEED);
		whSeedingCollectionDao.insert(collection);
	}

	@Override
	public void removeRecommendResultListCache(String batch, Long userId) {
		cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
	}
}
