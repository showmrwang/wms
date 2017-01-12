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
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
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
}
