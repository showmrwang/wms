package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.command.pda.collection.WorkCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhFunctionCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhTemporaryStorageLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("pdaConcentrationManager")
public class PdaConcentrationManagerImpl extends BaseManagerImpl implements PdaConcentrationManager {

    @Autowired
    private WhFacilityRecPathDao whFacilityRecPathDao;

    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;

    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    
    @Autowired
    private WhTemporaryStorageLocationDao whTemporaryStorageLocationDao;
    
    @Autowired
    private ContainerDao containerDao;
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WorkCollectionCommand recommendSeedingWall(WorkCollectionCommand workCollectionCommand) {
        String batch = workCollectionCommand.getBatch();
        Long userId = workCollectionCommand.getUserId();
        Boolean flag = false;
        // TODO 判断是否是播种模式
        if (!flag) {
            // TODO 调用播种墙推荐逻辑
            cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch, new ArrayList<RecFacilityPathCommand>(), CacheConstants.CACHE_ONE_YEAR);
        }
        workCollectionCommand.setIsSuccess(flag);
        return workCollectionCommand;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String findTargetPos(WorkCollectionCommand command) {
        RecFacilityPathCommand rfp = this.findRecFacilityPathCommandByIndex(command);
        if (null == rfp) {
            return "FINISH";
        }
        String targetPos = "";
        String containerCode = rfp.getContainerCode();
        String batch = command.getBatch();
        Long ouId = command.getOuId();
        WhFacilityRecPath whFacilityRecPath = whFacilityRecPathDao.findWhFacilityRecPathByBatchAndContainer(batch, containerCode, ouId);
        String transitLocationCode = whFacilityRecPath.getTransitLocationCheckCode(); // 中转库位编码
        String temporaryStorageLocationCode = whFacilityRecPath.getTemporaryStorageLocationCode(); // 暂存区域库位编码
        String seedingwallCode = whFacilityRecPath.getSeedingwallCode(); // 播种墙编码
        if (StringUtils.hasText(transitLocationCode)) {
            targetPos = (!command.getIsScanCheckCode()) ? Constants.TARGET_5 : Constants.TARGET_6;
        } else {
            if (!StringUtils.hasText(seedingwallCode)) {
                targetPos = (!command.getIsScanCheckCode()) ? Constants.TARGET_1 : Constants.TARGET_2;
            } else {
                Boolean flag = checkSeedingWallCapacity(seedingwallCode, batch, ouId);
                if (flag) {
                    targetPos = (!command.getIsScanCheckCode()) ? Constants.TARGET_1 : Constants.TARGET_2;
                } else {
                    targetPos = (!command.getIsScanCheckCode()) ? Constants.TARGET_3 : Constants.TARGET_4;
                }
            }
        }
        return targetPos + "$" + rfp.getContainerCode();
    }

    /**
     * [通用方法] 通过索引取得播种墙推荐路径
     * @param workCollectionCommand
     * @return
     */
    private RecFacilityPathCommand findRecFacilityPathCommandByIndex(WorkCollectionCommand workCollectionCommand) {
        Long userId = workCollectionCommand.getUserId();
        String batch = workCollectionCommand.getBatch();
        int index = workCollectionCommand.getIndex();
        List<RecFacilityPathCommand> rfpList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch); // 当前用户当前批次下的所有播种墙推荐逻辑
        if (index > (rfpList.size() - 1)) {
            return null;
        }
        RecFacilityPathCommand command = rfpList.get(workCollectionCommand.getIndex());
        return command;

    }

    /**
     * [业务方法] 判断推荐的播种墙剩余容量是否满足
     * @param seedingwallCode
     * @param batch
     * @param ouId
     * @return
     */
    private Boolean checkSeedingWallCapacity(String seedingwallCode, String batch, Long ouId) {
        Boolean flag = false;
        WhOutboundFacility whOutboundFacility = this.whOutboundFacilityDao.findByCodeAndOuId(seedingwallCode, ouId);
        Integer facilityUpperLimit = whOutboundFacility.getFacilityUpperLimit(); // 设施容量上限
        WhSeedingCollectionCommand whSeedingCollectionCommand = new WhSeedingCollectionCommand();
        whSeedingCollectionCommand.setBatch(batch);
        whSeedingCollectionCommand.setOuId(ouId);
        whSeedingCollectionCommand.setSeedingwallCode(seedingwallCode);
        whSeedingCollectionCommand.setCollectionStatus(CollectionStatus.FINISH);
        whSeedingCollectionCommand.setFacilityId(whOutboundFacility.getId());
        Integer cnt = this.whSeedingCollectionDao.countCapacityByParamExt(whSeedingCollectionCommand); // 播种墙已占有容量(集货表状态为非完成)
        int result = facilityUpperLimit.compareTo(cnt);
        if (result > 0) {
            // 播种墙容量满足
            flag = true;
        }
        return flag;
    }

    @Override
    public Boolean checkAndMoveContainer(WorkCollectionCommand workCollectionCommand) {
        String inputContainerCode = workCollectionCommand.getInputContainerCode();
        String containerCode = workCollectionCommand.getContainerCode();
        if (!containerCode.equals(inputContainerCode)) {
            throw new BusinessException("容器不匹配");
        }
        Boolean isRecordSuccess = this.recordSeedingCollection(workCollectionCommand);
        if (!isRecordSuccess) {
            throw new BusinessException("记录容器集货信息失败");
        }
        Boolean isMoveSuccess = this.moveContainer(workCollectionCommand);
        if (!isMoveSuccess) {
            throw new BusinessException("移动容器失败");
        }
        return null;
    }

    /**
     * [业务方法] 记录容器集货信息
     * @param workCollectionCommand
     * @return
     */
    private Boolean recordSeedingCollection(WorkCollectionCommand workCollectionCommand) {
        RecFacilityPathCommand command = this.findRecFacilityPathCommandByIndex(workCollectionCommand);
        if (null != command) {
            Long containerId = workCollectionCommand.getContainerId();
            Long ouId = workCollectionCommand.getOuId();
            WhSeedingCollection whSeedingCollection = new WhSeedingCollection();
            whSeedingCollection.setBatch(workCollectionCommand.getBatch());
            whSeedingCollection.setOuId(ouId);
            whSeedingCollection.setCollectionStatus(CollectionStatus.EXECUTING);
            whSeedingCollection.setContainerId(containerId);
            Integer targetType = workCollectionCommand.getTargetType();
            switch (targetType) {
                case Constants.SEEDING_WALL:
                    whSeedingCollection.setFacilityId(command.getFacilityId());
                    break;
                case Constants.TEMPORARY_STORAGE_LOCATION:
                    whSeedingCollection.setTemporaryLocationId(command.getAreaId());
                    break;
                case Constants.TRANSIT_LOCATION:
                    // TODO 接口需要添加
                    whSeedingCollection.setLocationId(1L);
                    break;
                default:
                    throw new BusinessException("error");
            }
            int cnt = this.whSeedingCollectionDao.saveOrUpdateByVersion(whSeedingCollection);
            if (0 >= cnt) {
                return false;
            }
        }
        return true;
    }

    /**
     * [业务方法] 移动容器
     * @param workCollectionCommand
     * @return
     */
    private Boolean moveContainer(WorkCollectionCommand workCollectionCommand) {
        Integer targetType = workCollectionCommand.getTargetType();
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setInsideContainerId(workCollectionCommand.getContainerId());
        skuInventory.setOuId(workCollectionCommand.getOuId());
        List<WhSkuInventory> invList = this.whSkuInventoryDao.findListByParam(skuInventory);
        RecFacilityPathCommand command = this.findRecFacilityPathCommandByIndex(workCollectionCommand);
        if (null != command) {
            if (null != invList && !invList.isEmpty()) {
                Boolean isMove = false;
                for (WhSkuInventory inv : invList) {
                    switch (targetType) {
                        case Constants.TEMPORARY_STORAGE_LOCATION:
                            isMove = true;
                            inv.setTemporaryLocationId(command.getAreaId());
                            break;
                        case Constants.TRANSIT_LOCATION:
                            isMove = true;
                            // TODO 接口需要添加
                            inv.setLocationId(1L);
                            break;
                        default:
                            break;
                    }
                    if (isMove) {
                        try {
                            inv.setUuid(SkuInventoryUuid.invUuid(inv));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            return false;
                        }
                        int cnt = this.whSkuInventoryDao.saveOrUpdateByVersion(inv);
                        if (0 >= cnt) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void cleanCache(WorkCollectionCommand workCollectionCommand) {
        Long userId = workCollectionCommand.getUserId();
        String batch = workCollectionCommand.getBatch();
        cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch);
    }
    
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
