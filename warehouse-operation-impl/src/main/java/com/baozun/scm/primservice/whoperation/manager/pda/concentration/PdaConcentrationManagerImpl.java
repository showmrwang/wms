package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.command.pda.collection.WorkCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhFacilityRecPathCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhTemporaryStorageLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.WaveFacilityManagerProxy;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Transactional
@Service("pdaConcentrationManager")
public class PdaConcentrationManagerImpl extends BaseManagerImpl implements PdaConcentrationManager {

    protected static final Logger log = LoggerFactory.getLogger(PdaConcentrationManagerImpl.class);

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
    private WhWorkDao whWorkDao;

    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhTemporaryStorageLocationDao whTemporaryStorageLocationDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private WhLocationDao whLocationDao;
    
    @Autowired
    private WaveFacilityManagerProxy waveFacilityManagerProxy;

    @Autowired
    private WaveFacilityManagerProxy waveFacilityManagerProxy;


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WorkCollectionCommand recommendSeedingWall(WorkCollectionCommand workCollectionCommand) {
        // TODO 根据workId查出批次号以及是否是播种模式
        WhWorkCommand work = this.whWorkDao.findWorkByWorkCode(workCollectionCommand.getWorkCode(), workCollectionCommand.getOuId());
        WhOdo odo = new WhOdo();
        odo.setWaveCode(workCollectionCommand.getWorkCode());
        odo.setOuId(workCollectionCommand.getOuId());
        List<WhOdo> odoList = whOdoDao.findListByParam(odo);
        if (null == odoList || odoList.isEmpty()) {
            throw new BusinessException("no odo");
        }
        WhDistributionPatternRule whDistributionPatternRule = this.whDistributionPatternRuleDao.findByOdoIdAndOuId(odoList.get(0).getId(), workCollectionCommand.getOuId());
        Integer pickingMode = whDistributionPatternRule.getPickingMode(); // 拣货模式
        String batch = work.getBatch();
        workCollectionCommand.setBatch(batch);
        Long userId = workCollectionCommand.getUserId();
        Boolean flag = false;
        // TODO 判断是否是播种模式
        if (!flag) {
            Boolean hasRec = initRecFacilityPath(workCollectionCommand);
        }
        // TODO flag
        workCollectionCommand.setIsSuccess(flag);
        return workCollectionCommand;
    }

    /**
     * [业务方法] 初始化推荐逻辑缓存
     * @param workCollectionCommand
     * @return [false:容器列表为空]/[true:容器列表不为空]
     */
    private Boolean initRecFacilityPath(WorkCollectionCommand workCollectionCommand) {
        Boolean hasSuccess = false;
        Long userId = workCollectionCommand.getUserId();
        Long ouId = workCollectionCommand.getOuId();
        String batch = workCollectionCommand.getBatch();
        List<String> containerList = workCollectionCommand.getContainerList();
        List<String> failContainerList = new ArrayList<String>();
        RecFacilityPathCommand recFacilityPath = new RecFacilityPathCommand();
        for (String containerCode : containerList) {
            recFacilityPath.setOuId(ouId);
            recFacilityPath.setBatch(batch);
            recFacilityPath.setContainerCode(containerCode);
            recFacilityPath.setOdoIdList(workCollectionCommand.getOdoIdList());
            // TODO 调用播种墙推荐逻辑
            RecFacilityPathCommand command = waveFacilityManagerProxy.matchOutboundFacility(recFacilityPath);
            if (1 == command.getStatus()) {
                // TODO 应该是是否推荐成功
                hasSuccess = true;
                WhFacilityRecPathCommand wrpc = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
                List<WhFacilityRecPathCommand> rfpList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch);
                if (null == rfpList || rfpList.isEmpty()) {
                    rfpList = new ArrayList<WhFacilityRecPathCommand>();
                } else {
                    rfpList.add(wrpc);
                }
                cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch, rfpList, CacheConstants.CACHE_ONE_YEAR);
            } else {
                failContainerList.add(containerCode);
                containerList.remove(containerCode);
            }
        }
        // 补偿机制:当有成功获取推荐路径 才会有补偿机制
        if (hasSuccess && workCollectionCommand.getIsCompensation()) {
            List<String> failList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId, batch);
            // if (null != failList) {
            // cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId,
            // batch);
            // }
            cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId, batch, failContainerList, CacheConstants.CACHE_ONE_YEAR);
        }
        if (containerList.isEmpty()) {
            // 容器列表为空
            return false;
        }
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String findTargetPos(WorkCollectionCommand command) {
        Boolean isRecPath = command.getIsRecPath();
        int containerQty = (null == command.getContainerList()) ? 0 : command.getContainerList().size();

        WhFacilityRecPathCommand rfp = this.findRecFacilityPathCommandByIndex(command);
        if (null == rfp) {
            return "FINISH";
        }
        String targetPosUrl = "";
        String targetPos = "";
        // String containerCode = rfp.getContainerCode();
        // String batch = command.getBatch();
        // Long ouId = command.getOuId();
        // WhFacilityRecPath whFacilityRecPath =
        // whFacilityRecPathDao.findWhFacilityRecPathByBatchAndContainer(batch, containerCode,
        // ouId);
        String transitLocationCode = rfp.getTransitLocationCheckCode(); // 中转库位编码
        String temporaryStorageLocationCode = rfp.getTemporaryStorageLocationCode(); // 暂存区域库位编码
        String seedingwallCode = rfp.getSeedingwallCode(); // 播种墙编码
        if (StringUtils.hasText(transitLocationCode)) {
            targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_5 : Constants.TARGET_6;
            targetPos = transitLocationCode;
        } else {
            if (!StringUtils.hasText(seedingwallCode)) {
                targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_3 : Constants.TARGET_4;
                targetPos = temporaryStorageLocationCode;
            } else {
                // isRecPath: 如果是集货操作, 设为false; 如果是推荐操作, 设为true
                int flag = checkSeedingWallCapacity(seedingwallCode, rfp.getBatch(), rfp.getOuId(), containerQty, isRecPath);
                if (Constants.SEEDING_WALL_SUFFICIENT == flag) {
                    Boolean isMoveAllSuccess = MoveAllContainer(command);
                    if (isMoveAllSuccess) {
                        this.compensationCache(command);
                        return findTargetPos(command);
                    }
                } else if (Constants.SEEDING_WALL_MOVE_ONE == flag) {
                    // 按箱移动到播种墙
                    targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_1 : Constants.TARGET_2;
                    targetPos = seedingwallCode;
                } else {
                    // 按箱移动到暂存库位
                    targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_3 : Constants.TARGET_4;
                    targetPos = temporaryStorageLocationCode;
                }
            }
        }
        // TODO 提示容器号 而不是编码
        return targetPosUrl + "$" + rfp.getContainerCode() + "$" + targetPos;
    }

    /**
     * [通用方法] 通过索引取得播种墙推荐路径
     * @param workCollectionCommand
     * @return
     */
    private WhFacilityRecPathCommand findRecFacilityPathCommandByIndex(WorkCollectionCommand workCollectionCommand) {
        Long userId = workCollectionCommand.getUserId();
        String batch = workCollectionCommand.getBatch();
        int index = workCollectionCommand.getIndex();
        List<WhFacilityRecPathCommand> rfpList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch); // 当前用户当前批次下的所有播种墙推荐逻辑
        if (index > (rfpList.size() - 1)) {
            return null;
        }
        WhFacilityRecPathCommand command = rfpList.get(workCollectionCommand.getIndex());
        return command;

    }

    /**
     * [业务方法] 判断推荐的播种墙剩余容量是否满足
     * @param seedingwallCode
     * @param batch
     * @param ouId
     * @return
     */
    private int checkSeedingWallCapacity(String seedingwallCode, String batch, Long ouId, int containerQty, Boolean isRecPath) {
        WhOutboundFacility whOutboundFacility = this.whOutboundFacilityDao.findByCodeAndOuId(seedingwallCode, ouId);
        Integer facilityUpperLimit = whOutboundFacility.getFacilityUpperLimit(); // 设施容量上限
        if (null == facilityUpperLimit) {
            throw new BusinessException("no upper limit");
        }
        WhSeedingCollectionCommand whSeedingCollectionCommand = new WhSeedingCollectionCommand();
        whSeedingCollectionCommand.setBatch(batch);
        whSeedingCollectionCommand.setOuId(ouId);
        whSeedingCollectionCommand.setSeedingwallCode(seedingwallCode);
        whSeedingCollectionCommand.setCollectionStatus(CollectionStatus.FINISH);
        whSeedingCollectionCommand.setFacilityId(whOutboundFacility.getId());
        int occupied = this.whSeedingCollectionDao.countCapacityByParamExt(whSeedingCollectionCommand); // 播种墙已占有容量(集货表状态为非完成)
        int capacity = facilityUpperLimit - occupied;
        if (capacity > 0) {
            // 播种墙容量足够
            int rest = capacity - containerQty;
            if (isRecPath && rest >= 0) {
                // 播种墙剩余容量满足一次移动所有容器到播种墙
                return Constants.SEEDING_WALL_SUFFICIENT;
            } else {
                // 每次移动一个容器到播种墙
                return Constants.SEEDING_WALL_MOVE_ONE;
            }
        } else {
            // 播种墙容量不足
            return Constants.SEEDING_WALL_NOT_SUFFICIENT;
        }
    }

    /**
     * [业务方法] 移动全部容器
     * @param workCollectionCommand
     * @return
     */
    private Boolean MoveAllContainer(WorkCollectionCommand workCollectionCommand) {
        List<String> containerList = workCollectionCommand.getContainerList();
        if (null != containerList && !containerList.isEmpty()) {
            for (int i = 0; i < containerList.size(); i++) {
                workCollectionCommand.setIndex(i);
                Boolean isRecordSuccess = this.recordSeedingCollection(workCollectionCommand);
                if (!isRecordSuccess) {
                    throw new BusinessException("记录容器集货信息失败");
                }
                Boolean isMoveSuccess = this.moveContainer(workCollectionCommand);
                if (!isMoveSuccess) {
                    throw new BusinessException("移动容器失败");
                }
            }
        }
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
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
        WhFacilityRecPathCommand command = this.findRecFacilityPathCommandByIndex(workCollectionCommand);
        if (null != command) {
            Long containerId = workCollectionCommand.getContainerId();
            Long ouId = workCollectionCommand.getOuId();
            WhSeedingCollection whSeedingCollection = new WhSeedingCollection();
            whSeedingCollection.setBatch(workCollectionCommand.getBatch());
            whSeedingCollection.setOuId(ouId);
            whSeedingCollection.setCollectionStatus(CollectionStatus.EXECUTING);
            whSeedingCollection.setContainerId(containerId);
            whSeedingCollection.setId(1L);
            Integer targetType = workCollectionCommand.getTargetType();
            switch (targetType) {
                case Constants.SEEDING_WALL:
                    whSeedingCollection.setFacilityId(command.getFacilityId());
                    break;
                case Constants.TEMPORARY_STORAGE_LOCATION:
                    whSeedingCollection.setTemporaryLocationId(command.getTemporaryStorageLocationId());
                    break;
                case Constants.TRANSIT_LOCATION:
                    // TODO 接口需要添加
                    whSeedingCollection.setLocationId(command.getTransitLocationId());
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
        WhFacilityRecPathCommand command = this.findRecFacilityPathCommandByIndex(workCollectionCommand);
        Integer targetType = workCollectionCommand.getTargetType();
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setInsideContainerId(command.getContainerId());
        skuInventory.setOuId(workCollectionCommand.getOuId());
        List<WhSkuInventory> invList = this.whSkuInventoryDao.findListByParam(skuInventory);
        if (null != command) {
            if (null != invList && !invList.isEmpty()) {
                Boolean isMove = false;
                for (WhSkuInventory inv : invList) {
                    switch (targetType) {
                        case Constants.TEMPORARY_STORAGE_LOCATION:
                            isMove = true;
                            inv.setTemporaryLocationId(command.getTemporaryStorageLocationId());
                            break;
                        case Constants.TRANSIT_LOCATION:
                            isMove = true;
                            // TODO 接口需要添加
                            inv.setLocationId(command.getTransitLocationId());
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

    @Override
    public void compensationCache(WorkCollectionCommand workCollectionCommand) {
        workCollectionCommand.setRestart(true);
        Long userId = workCollectionCommand.getUserId();
        String batch = workCollectionCommand.getBatch();
        List<String> containerList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId, batch);
        if (null != containerList && !containerList.isEmpty()) {
            workCollectionCommand.setContainerList(containerList);
            workCollectionCommand.setIsCompensation(false);
            initRecFacilityPath(workCollectionCommand);
        }
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
			return null;
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
	 * 检查容器号对应的位置是否存在
	 */
	@Override
	public WhSeedingCollectionCommand checkContainerInWhere(String containerCode, Integer type, Long ouId) {
		if (StringUtils.isEmpty(type)) {
			throw new BusinessException(ErrorCodes.PARAMS_ERROR);
		}
		WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
		if (null == seedingCollection) {
			throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_DATA_NULL_ERROR, new Object[] { containerCode });
		}
		switch (type) {
			case Constants.TEMPORARY_STORAGE_LOCATION:
				if (null == seedingCollection.getTemporaryLocationId()) {
					throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_NOT_IN_TEMPORARYLOCATION, new Object[] { containerCode });
				}
				break;
			case Constants.TRANSIT_LOCATION:
				if (null == seedingCollection.getLocationId()) {
					throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_NOT_IN_LOCATION, new Object[] { containerCode });
				}
				break;
			default:
				throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
		}
		return seedingCollection;
	}

	/**
	 * 判断当前容器是否有推荐结果
	 */
	@Override
	public WhFacilityRecPathCommand checkContainerHaveRecommendResult(String containerCode, String batch, Long userId, Long ouId) {
		// cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		if (StringUtils.isEmpty(containerCode)) {
			throw new BusinessException(ErrorCodes.PARAMS_ERROR);
		}
		WhFacilityRecPathCommand rec = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
		List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		if (null == recPathList) {
			recPathList = new ArrayList<WhFacilityRecPathCommand>();
		}
		if (!recPathList.isEmpty()) {
			for (WhFacilityRecPathCommand recPath : recPathList) {
				if (recPath.getContainerCode().equals(containerCode)) {
					throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] { containerCode });
				}
				if (!recPath.getBatch().equals(batch)) {
					throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_BATCH_DIFFERENCE, new Object[] { containerCode });
				}
			}
		}
		recPathList.add(0, rec);
		cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch, recPathList, CacheConstants.CACHE_ONE_DAY);
		return rec;
	}

	/**
	 * 判断是否达到可携带容量数量限制且小于播种墙容器上限
	 */
	@Override
	public boolean checkContainerQtyLimit(Long facilityId, Integer carryQty, Integer containerQty, Integer upperLimit, String batch, Long ouId) {
        Integer seedingCount = whSeedingCollectionDao.getSeedingNumFromFacility(facilityId, batch, ouId);
        if (carryQty.intValue() == containerQty.intValue() || (carryQty.intValue() + seedingCount.intValue()) == upperLimit.intValue()) {
			return true;
		}
		return false;
	}

	@Override
	public WhFacilityRecPathCommand popRecommendResultListHead(String batch, Long userId) {
		WhFacilityRecPathCommand rec = null;
		List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
		if (null != recPathList && !recPathList.isEmpty()) {
			rec = recPathList.get(0);
		}
		return rec;
	}
	
	@Override
	public WhFacilityRecPathCommand popManualRecommendResultListHead(Long userId) {
		WhFacilityRecPathCommand rec = null;
		List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC , userId.toString());
		if (null != recPathList && !recPathList.isEmpty()) {
			rec = recPathList.get(0);
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
			if (!StringUtils.isEmpty(temporaryStorageLocationCode)) {
				// 全部移动到播种墙则释放暂存库位
				WhTemporaryStorageLocation location = whTemporaryStorageLocationDao.findByCodeAndOuId(temporaryStorageLocationCode, ouId);
				location.setStatus(1);
				location.setBatch(null);
				whTemporaryStorageLocationDao.saveOrUpdateByVersion(location);
			}
			return true;
		}
		return false;
	}

	/**
	 * 记录容器到播种墙上集货信息
	 */
	@Override
	public void updateContainerToDestination(WhFacilityRecPathCommand rec, Integer destinationType, Long ouId) {
		Long containerId = rec.getContainerId();
		if (null == containerId) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
		String batch = rec.getBatch();
		if (StringUtils.isEmpty(batch)) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
		int updateCount = whSeedingCollectionDao.deleteContainerInSeedingWall(containerId, batch, ouId);
		if (updateCount != 1) {
			throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
		}
		WhSeedingCollection collection = new WhSeedingCollection();
		if (destinationType == Constants.SEEDING_WALL) {
			// 目标移到播种墙
			Long facilityId = rec.getFacilityId();
			if (null == facilityId) {
				throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
			}
			collection.setFacilityId(facilityId);
			collection.setCollectionStatus(CollectionStatus.TO_SEED);
		} else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION) {
			// 目标移到暂存库位
			Long temporaryStorageLocationId = rec.getTemporaryStorageLocationId();
			if (null == temporaryStorageLocationId) {
				throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
			}
			collection.setTemporaryLocationId(temporaryStorageLocationId);
			collection.setCollectionStatus(CollectionStatus.EXECUTING);
		} else if (destinationType == Constants.TRANSIT_LOCATION) {
			// 目标移到中转库位
			Long transitLocationId = rec.getTransitLocationId();
			if (null == transitLocationId) {
				throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
			}
			collection.setLocationId(transitLocationId);
			collection.setCollectionStatus(CollectionStatus.EXECUTING);
		}
		collection.setContainerId(containerId);
		collection.setBatch(batch);
		collection.setOuId(ouId);
		whSeedingCollectionDao.insert(collection);
		if (destinationType == Constants.SEEDING_WALL) {
			String seedingWallCode = rec.getSeedingwallCode();
			String containerCode = rec.getContainerCode();
			// 移到播种墙时保存redis数据
			// SEEDING_(仓库ID)_(播种墙CODE)_(批次号)_(容器CODE)=Map<SkuId_uuid, WhSeedingCollectionLine>
			addSeedingDataIntoCache(seedingWallCode, containerCode, containerId, batch, ouId);
		}
	}

	private void addSeedingDataIntoCache(String seedingWallCode, String containerCode, Long containerId, String batch, Long ouId) {
		if (StringUtils.isEmpty(seedingWallCode) || StringUtils.isEmpty(containerCode)) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
		StringBuilder sb = new StringBuilder(CacheConstants.CACHE_SEEDING);
		sb.append(ouId).append("_");
		sb.append(seedingWallCode).append("_");
		sb.append(batch).append("_");
		sb.append(containerCode);
		String cacheKey = sb.toString();
		
		// 查找容器库存
		List<WhSeedingCollectionLine> dataList = this.whSkuInventoryDao.findSeedingDataByContainerId(containerId, ouId);
		if (null != dataList && !dataList.isEmpty()) {
			// 封装集货sku数据
			Map<String, WhSeedingCollectionLine> seedingDataMap = new HashMap<String, WhSeedingCollectionLine>();
			for (WhSeedingCollectionLine data : dataList) {
				String mapKey = data.getSkuId() + "_" + data.getUuid();
				seedingDataMap.put(mapKey, data);
			}
			cacheManager.setObject(cacheKey, seedingDataMap, CacheConstants.CACHE_ONE_WEEK);
		}
	}

	@Override
	public void removeRecommendResultListCache(String batch, Long userId) {
		cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
	}
	
	/**
	 * 人为集货扫描容器验证
	 */
	@Override
	public void addManualContainerCodeIntoCache(String containerCode, Long userId, Long ouId) {
		ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
        if (null == containerCodeDeque) {
        	containerCodeDeque = new ArrayDeque<String>();
		}
        if (containerCodeDeque.contains(containerCode)) {
        	throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] { containerCode });
		}
        containerCodeDeque.addFirst(containerCode);
        cacheManager.setObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId, containerCodeDeque, CacheConstants.CACHE_ONE_DAY);
	}
	
	@Override
	public boolean checkManualContainerCacheNotNull(Boolean isApplyFacility, Long userId) {
		if (null != isApplyFacility && isApplyFacility) {
			List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
			if (null == recPathList) {
				throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_QTY_IS_NULL);
			}
		} else {
			ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
			if (null == containerCodeDeque || containerCodeDeque.isEmpty()) {
				throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_QTY_IS_NULL);
			}
		}
		return true;
	}
	
	/**
	 * 通过扫描的目的地编码得到集货位置的类型
	 */
	@Override
	public int getDestinationTypeByCode(String destinationCode, Long ouId) {
		if (StringUtils.isEmpty(destinationCode)) {
			throw new BusinessException(ErrorCodes.PARAMS_ERROR);
		}
		WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(destinationCode, ouId);
		if (null != facility) {
			return Constants.SEEDING_WALL;
		}
		WhTemporaryStorageLocation storageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(destinationCode, ouId);
		if (null != storageLocation) {
			return Constants.TEMPORARY_STORAGE_LOCATION;
		}
		Location location = whLocationDao.findLocationByCode(destinationCode, ouId);
		if (null != location) {
			return Constants.TRANSIT_LOCATION;
		}
		throw new BusinessException(ErrorCodes.COLLECTION_DESTINATION_NOT_RIGHT);
	}
	
	/**
	 * 判断推荐结果表中当前容器对应的小批次是否关联当前目的地
	 */
	@Override
	public boolean checkContainerAssociatedWithDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId) {
		if (StringUtils.isEmpty(containerCode) || StringUtils.isEmpty(destinationCode) || null == destinationType) {
			throw new BusinessException(ErrorCodes.PARAMS_ERROR);
		}
		ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
        if (!containerCodeDeque.contains(containerCode)) {
        	throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_IS_NOT_IN_SCAN_LIST, new Object[] { containerCode });
		}
		WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
		if (null == seedingCollection) {
			throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
		}
        String batch = seedingCollection.getBatch();
        List<WhFacilityRecPathCommand> recList = whFacilityRecPathDao.getRecommendDestinationByBatch(batch, ouId);
        if (null != recList && !recList.isEmpty()) {
        	for (WhFacilityRecPathCommand rec : recList) {
        		if (destinationType == Constants.SEEDING_WALL && destinationCode.equals(rec.getSeedingwallCode())) {
        			return true;
        		} else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION && destinationCode.equals(rec.getTemporaryStorageLocationCode())) {
        			return true;
        		} else if (destinationType == Constants.TRANSIT_LOCATION && destinationCode.equals(rec.getTransitLocationCode())) {
        			return true;
        		}
			}
		}
        // 判断目的地是否关联其他小批次
        List<String> batchList = whFacilityRecPathDao.getBatchListByDestinationCode(destinationCode, destinationType, ouId);
        if (null == batchList || batchList.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean manualMoveContainerToDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId) {
		if (StringUtils.isEmpty(containerCode) || StringUtils.isEmpty(destinationCode) || null == destinationType) {
			throw new BusinessException(ErrorCodes.PARAMS_ERROR);
		}
		WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
		ContainerCommand container = containerDao.getContainerByCode(containerCode, ouId);
		if (null == seedingCollection || null == container) {
			throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
		}
		// 1.将容器与目的地信息插入推荐结果表
		String batch = seedingCollection.getBatch();
		WhOutboundFacility facility = null;
		WhTemporaryStorageLocation storageLocation = null;
		Location location = null;
		WhFacilityRecPathCommand recCommand = new WhFacilityRecPathCommand();
		if (destinationType == Constants.SEEDING_WALL) {
			facility = whOutboundFacilityDao.findByCodeAndOuId(destinationCode, ouId);
			if (null == facility) {
				throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
			}
			recCommand.setFacilityId(facility.getId());
			recCommand.setSeedingwallCode(destinationCode);
			recCommand.setSeedingwallCheckCode(facility.getCheckCode());
			recCommand.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
		} else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION) {
			storageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(destinationCode, ouId);
			if (null == storageLocation) {
				throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
			}
			recCommand.setTemporaryStorageLocationId(storageLocation.getId());
			recCommand.setTemporaryStorageLocationCheckCode(storageLocation.getCheckCode());
			recCommand.setTemporaryStorageLocationCode(storageLocation.getTemporaryStorageCode());
		} else if (destinationType == Constants.TRANSIT_LOCATION) {
			location = whLocationDao.findLocationByCode(destinationCode, ouId);
			if (null == location) {
				throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
			}
			recCommand.setTransitLocationId(location.getId());
			recCommand.setTransitLocationCheckCode(location.getReplenishmentBarcode());
			recCommand.setTransitLocationCode(location.getCode());
		}
		recCommand.setBatch(batch);
		recCommand.setStatus(1);
		recCommand.setOuId(ouId);
		recCommand.setContainerCode(containerCode);
		recCommand.setContainerId(container.getId());
		
		WhFacilityRecPath rec = new WhFacilityRecPath();
		BeanUtils.copyProperties(recCommand, rec);
		whFacilityRecPathDao.insert(rec);
		
		// 2.将容器移动到目的地
		this.updateContainerSkuInventory(recCommand, destinationType, ouId);
		
		// 3.记录容器集货信息（到目的地）
		this.updateContainerToDestination(recCommand, destinationType, ouId);
    	
    	ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
    	if (null == containerCodeDeque || containerCodeDeque.isEmpty()) {
    		throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_QTY_IS_NULL);
    	}
    	containerCodeDeque.remove(containerCode);
    	cacheManager.setObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId, containerCodeDeque, CacheConstants.CACHE_ONE_DAY);
    	// 判断是否全部完成
    	if (containerCodeDeque.isEmpty()) {
			return true;
		}
    	return false;
	}
	
	@Override
	public void updateContainerSkuInventory(WhFacilityRecPathCommand recCommand, Integer destinationType, Long ouId) {
        WhSkuInventory skuInventory = new WhSkuInventory();
        skuInventory.setInsideContainerId(recCommand.getContainerId());
        skuInventory.setOuId(ouId);
        List<WhSkuInventory> invList = this.whSkuInventoryDao.findListByParam(skuInventory);
        if (null == invList || invList.isEmpty()) {
        	throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        for (WhSkuInventory inv : invList) {
            switch (destinationType) {
	            case Constants.SEEDING_WALL:
	                inv.setLocationId(null);
	                inv.setTemporaryLocationId(null);
	                break;
                case Constants.TEMPORARY_STORAGE_LOCATION:
                    inv.setLocationId(null);
                    inv.setTemporaryLocationId(recCommand.getTemporaryStorageLocationId());
                    break;
                case Constants.TRANSIT_LOCATION:
                    inv.setLocationId(recCommand.getTransitLocationId());
                    inv.setTemporaryLocationId(null);
                    break;
                default:
                    break;
            }
            try {
                inv.setUuid(SkuInventoryUuid.invUuid(inv));
            } catch (NoSuchAlgorithmException e) {
                log.error("SkuInventoryUuid.invUuid error");
            }
            int cnt = this.whSkuInventoryDao.saveOrUpdateByVersion(inv);
            if (0 >= cnt) {
            	throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
        }
	}

	@Override
	public void removeManualContainerCodeFromCache(Long userId) {
		try {
			cacheManager.remove(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
			cacheManager.removeMapValue(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
		} catch (Exception e) {
			log.error("redis error", e);
		}
	}

	@Override
	public void useSysRecommendResult(String containerCode, Long userId, Long ouId) {
		// 判断是否有播种推荐结果
		WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
		String batch = seedingCollection.getBatch();
		WhFacilityRecPathCommand rec = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
		if (null == rec) {
			// TODO 推荐播种墙逻辑,并判断是否推荐成功
			RecFacilityPathCommand recFacilityPath = new RecFacilityPathCommand();
			RecFacilityPathCommand command = waveFacilityManagerProxy.matchOutboundFacility(recFacilityPath);
			
			
		}
		// 缓存推荐结果
		List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
		if (null == recPathList) {
			recPathList = new ArrayList<WhFacilityRecPathCommand>();
		}
		if (!recPathList.isEmpty()) {
			for (WhFacilityRecPathCommand recPath : recPathList) {
				if (recPath.getContainerCode().equals(containerCode)) {
					throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] { containerCode });
				}
			}
		}
		recPathList.add(0, rec);
		cacheManager.setMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString(), recPathList, CacheConstants.CACHE_ONE_DAY);
	}
	
	@Override
	public void moveContainerToDestination(WhFacilityRecPathCommand recCommand, Integer destinationType, Boolean isManual, Long userId, Long ouId) {
		// 1.将容器移动到目的地
		this.updateContainerSkuInventory(recCommand, destinationType, ouId);
		
		// 2.记录容器集货信息（到目的地）
		this.updateContainerToDestination(recCommand, destinationType, ouId);
		
		// 3.清理缓存
		List<WhFacilityRecPathCommand> recPathList = null;
		if (null != isManual && isManual) {
			recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
			if (null != recPathList && !recPathList.isEmpty()) {
				recPathList.remove(0);
				cacheManager.setMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString(), recPathList, CacheConstants.CACHE_ONE_DAY);
			}
		} else {
			String batch = recCommand.getBatch();
			recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
			if (null != recPathList && !recPathList.isEmpty()) {
				recPathList.remove(0);
				cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch, recPathList, CacheConstants.CACHE_ONE_DAY);
			}
		}
	}

	@Override
	public Integer checkDestinationByRecommendResult(WhFacilityRecPathCommand rec, Long ouId) {
		if (null == rec) {
			throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
		}
		String containerCode = rec.getContainerCode();
		String batch = rec.getBatch();
		WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
		Integer position = 4;	// 当期容器位置, 0为还在集货区
		if (null != seedingCollection.getLocationId()) {
			position = Constants.TRANSIT_LOCATION;
		} else if (null != seedingCollection.getTemporaryLocationId()) {
			position = Constants.TEMPORARY_STORAGE_LOCATION;
		} else if (null != seedingCollection.getFacilityId()) {
			position = Constants.SEEDING_WALL;
		}
		
		if (!StringUtils.isEmpty(rec.getTransitLocationCode()) && position == 4) {
			return Constants.TRANSIT_LOCATION;
		}
		if (!StringUtils.isEmpty(rec.getSeedingwallCode())) {
			Long fid = rec.getFacilityId();
			Integer seedingNum = whSeedingCollectionDao.getSeedingNumFromFacility(fid, batch, ouId);
			if (rec.getSeedingwallUpperLimit().intValue() > seedingNum.intValue()) {
				return Constants.SEEDING_WALL;
			}
		}
		return Constants.TEMPORARY_STORAGE_LOCATION;
	}

    // TODO 待校验
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkAndRecordInventory(WorkCollectionCommand workCollectionCommand) {
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
}
