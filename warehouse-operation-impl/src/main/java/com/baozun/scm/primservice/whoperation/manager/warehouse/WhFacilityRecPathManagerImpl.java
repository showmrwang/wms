package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityQueueDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityGroupDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhTemporaryStorageLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkingStorageSectionDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityQueue;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacilityGroup;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;

@Service("whFacilityRecPathManager")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WhFacilityRecPathManagerImpl extends BaseManagerImpl implements WhFacilityRecPathManager {
    protected static final Logger log = LoggerFactory.getLogger(WhFacilityRecPathManager.class);
    @Autowired
    private WhFacilityRecPathDao whFacilityRecPathDao;
    @Autowired
    private WhFacilityPathDao whFacilityPathDao;
    @Autowired
    private WhOutboundFacilityGroupDao whOutboundFacilityGroupDao;
    @Autowired
    private WhOutboundFacilityDao WhOutboundFacilityDao;
    @Autowired
    private WhFacilityQueueDao whFacilityQueueDao;
    @Autowired
    private WhTemporaryStorageLocationDao whTemporaryStorageLocationDao;
    @Autowired
    private WhWorkingStorageSectionDao whWorkingStorageSectionDao;
    @Autowired
    private AreaDao areaDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhFacilityRecPath> findWhFacilityRecPathByBatchAndContainer(String batch, String containerCode, Long ouId) {
        WhFacilityRecPath search = new WhFacilityRecPath();
        search.setOuId(ouId);
        search.setBatch(batch);
        search.setContainerCode(containerCode);
        return this.whFacilityRecPathDao.findListByParam(search);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityGroup findOutboundFacilityGroupById(Long outboundFacilityGroupId, Long ouId) {
        return whOutboundFacilityGroupDao.findByIdAndOuId(outboundFacilityGroupId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void occupyFacilityAndlocation(WhOutboundFacilityGroup facilityGroup, WhFacilityRecPath prePath, RecFacilityPathCommand recFacilityPath, Warehouse wh) {
        // 这边的逻辑分三部分：
        // 第一部分：如果已经有了推荐路径，则直接插入数据库，没有则推荐暂存库位、中转库位、播种墙。
        // 第二部分：推荐成功，插入推荐成功路径表，返回推荐播种墙成功信息，等待后方后续处理
        // 第三部分：最后一箱逻辑


        Long ouId = wh.getId();

        // 如果已经有推荐路径
        if (prePath != null) {
            this.occupyFacilityAndlocationByPrePath(prePath, recFacilityPath, ouId);
        } else {
            this.occupyFacilityAndlocationByFacilityGroup(facilityGroup, recFacilityPath, wh);
        }

        // 最后一箱逻辑
        if (recFacilityPath.isLastContainer()) {
            this.triggerForLastContainer(recFacilityPath, ouId);
        }

    }


    private void triggerForLastContainer(RecFacilityPathCommand recFacilityPath, Long ouId) {
        String batch = recFacilityPath.getBatch();
        String pickingMode = recFacilityPath.getPickingMode();

        WhFacilityRecPath search = new WhFacilityRecPath();
        search.setOuId(ouId);
        search.setBatch(batch);
        List<WhFacilityRecPath> WhFacilityRecPathList = this.whFacilityRecPathDao.findListByParam(search);
        if (WhFacilityRecPathList == null || WhFacilityRecPathList.size() == 0) {
            throw new BusinessException(ErrorCodes.FACILITYMATCH_NO_FACILITYRECPATH);
        }
        // 批次容器数量
        int batchContainerQty = WhFacilityRecPathList.size();

        // 当是边拣货边播种的时候，当播种墙上限满足当前批次容器数，是需要释放对应出库暂存库位
        // 边拣货变播种，同时占用到播种墙和出库暂存库位的时候 @mender yimin.lu 2017/4/14 修正逻辑：添加判断：边拣货变播种时候，同时占用到播种墙和出库暂存库位
        // 占用的播种墙容量上限是否满足批次对应箱数
        boolean isReleaseLocation = false;
        if (null == WhFacilityRecPathList.get(0).getSeedingwallUpperLimit()) {
            isReleaseLocation = false;
        } else {
            isReleaseLocation = true;
        }
        if (isReleaseLocation) {

            isReleaseLocation = batchContainerQty <= WhFacilityRecPathList.get(0).getSeedingwallUpperLimit() ? true : false;// 是否需要释放库存
        }

        for (int i = 0; i < WhFacilityRecPathList.size(); i++) {
            WhFacilityRecPath rec = WhFacilityRecPathList.get(i);
            // 边拣货边播种
            if (Constants.SEEDING_MODE_1.equals(pickingMode) && isReleaseLocation) {
                WhTemporaryStorageLocation tslocation = this.whTemporaryStorageLocationDao.findByCodeAndOuId(rec.getTemporaryStorageLocationCode(), ouId);
                tslocation.setStatus(Constants.WH_GLOBAL_STATUS_1);
                tslocation.setBatch(null);
                int tslocationUpdateCount = this.whTemporaryStorageLocationDao.saveOrUpdateByVersion(tslocation);
                if (tslocationUpdateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }

                rec.setTemporaryStorageLocationCheckCode(null);
                rec.setTemporaryStorageLocationCode(null);
            }
            rec.setBatchContainerQty(batchContainerQty);
            int recUpdateCount = this.whFacilityRecPathDao.saveOrUpdate(rec);
            if (recUpdateCount == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

        }
        // 运行播种墙推荐队列
        WhFacilityQueue queueSearch = new WhFacilityQueue();
        queueSearch.setOuId(ouId);
        queueSearch.setBatch(batch);
        queueSearch.setIsRec(false);
        List<WhFacilityQueue> queueList = this.whFacilityQueueDao.findListByParam(queueSearch);
        if (queueList != null && queueList.size() > 0) {
            for (WhFacilityQueue queue : queueList) {
                queue.setIsRec(true);
                int queueUpdateCount = this.whFacilityQueueDao.saveOrUpdate(queue);
                if (queueUpdateCount == 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }

    }

    private WhFacilityRecPath occupyFacilityAndlocationByFacilityGroup(WhOutboundFacilityGroup facilityGroup, RecFacilityPathCommand recFacilityPath, Warehouse wh) {
        Long ouId = wh.getId();
        String seedingMode = wh.getSeedingMode();
        String batch = recFacilityPath.getBatch();
        WhFacilityRecPath recPath = new WhFacilityRecPath();

        // 路径起点
        Area fromArea = this.areaDao.findByIdExt(recFacilityPath.getAreaId(), ouId);

        boolean facilityFlag, locationFlag = true;
        if (Constants.SEEDING_MODE_1.equals(seedingMode)) {
            facilityFlag = true;
        } else {
            facilityFlag = false;
        }
        // 暂存库位推荐
        WhTemporaryStorageLocation toLocation = this.getTopFreeStorageLocation(locationFlag, facilityGroup.getWorkingStorageSectionId(), batch, ouId);
        if (toLocation == null) {
            throw new BusinessException(ErrorCodes.FACILITYMATCH_NO_TEMPORARYSTORAGELOCATION);
        }
        // @mender yimin.lu 2017/4/13 中转库位逻辑修正
        /*
         * WhWorkingStorageSection section =
         * this.whWorkingStorageSectionDao.findByIdAndOuId(toLocation.getWorkingStorageSectionId(),
         * ouId); if (section == null) { throw new BusinessException(ErrorCodes.PARAM_IS_NULL); }
         */


        // 查询中转库位
        WhTemporaryStorageLocation transitLocation = new WhTemporaryStorageLocation();
        WhFacilityPath facilityPathSearch = new WhFacilityPath();
        facilityPathSearch.setOuId(ouId);
        facilityPathSearch.setFromAreaCode(fromArea.getAreaCode());
        facilityPathSearch.setFromAreaType(fromArea.getAreaType());
        facilityPathSearch.setToAreaType(facilityGroup.getFacilityGroupType());
        facilityPathSearch.setToAreaCode(facilityGroup.getFacilityGroupCode());
        List<WhFacilityPath> facilityPathList = this.whFacilityPathDao.findListByParam(facilityPathSearch);
        if (facilityPathList != null && facilityPathList.size() > 0) {
            WhTemporaryStorageLocation search = new WhTemporaryStorageLocation();
            search.setOuId(ouId);
            search.setTemporaryStorageCode(facilityPathList.get(0).getTransitLocationCode());
            List<WhTemporaryStorageLocation> locationList = this.whTemporaryStorageLocationDao.findListByParam(search);
            if (locationList == null || locationList.size() == 0) {

            } else {
                transitLocation = locationList.get(0);
            }
        }


        // 设备(播种墙)
        WhOutboundFacility facility = null;
        if (facilityFlag) {
            facility = this.getTopFreeOutBoundFacility(facilityFlag, facilityGroup.getId(), batch, ouId);
        }
        recPath = this.insertByPickingMode(recFacilityPath, facility, transitLocation, toLocation, ouId);
        return recPath;
    }

    private WhFacilityRecPath insertByPickingMode(RecFacilityPathCommand recFacilityPath, WhOutboundFacility facility, WhTemporaryStorageLocation transitLocation, WhTemporaryStorageLocation toLocation, Long ouId) {
        WhFacilityRecPath recPath = new WhFacilityRecPath();
        // #推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
        recPath = new WhFacilityRecPath();
        recPath.setBatch(recFacilityPath.getBatch());
        recPath.setBatchContainerQty(1);
        recPath.setContainerCode(recFacilityPath.getContainerCode());
        if (facility == null) {
            recPath.setSeedingwallCode(null);
            // @gianni 暂时设置一个上限 若为空会报错
            recPath.setSeedingwallUpperLimit(null);
            recPath.setSeedingwallCheckCode(null);
        } else {
            recPath.setSeedingwallCode(facility.getFacilityCode());
            recPath.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
            recPath.setSeedingwallCheckCode(facility.getCheckCode());
        }


        recPath.setTemporaryStorageLocationCheckCode(toLocation.getCheckCode());
        recPath.setTemporaryStorageLocationCode(toLocation.getTemporaryStorageCode());
        recPath.setTransitLocationCheckCode(transitLocation.getCheckCode());
        recPath.setTransitLocationCode(transitLocation.getTemporaryStorageCode());
        recPath.setOuId(ouId);
        // @Gianni 修改状态为新建 2017-04-10
        recPath.setStatus(1);
        this.whFacilityRecPathDao.insert(recPath);

        // 如果没有推荐到播种墙，则加入推荐队列
        if (facility == null) {
            WhFacilityQueue facilityQueue = new WhFacilityQueue();
            facilityQueue.setBatch(recFacilityPath.getBatch());
            facilityQueue.setTemporaryStorageLocationId(toLocation.getId());
            facilityQueue.setCreateTime(new Date());
            facilityQueue.setIsRec(false);
            facilityQueue.setOuId(ouId);
            this.whFacilityQueueDao.insert(facilityQueue);
        }
        return recPath;
    }

    /**
     * 如果已有推荐路径，复制过去就可以了
     * 
     * @param prePath
     * @param recFacilityPath
     * @param ouId
     * @return
     */
    private WhFacilityRecPath occupyFacilityAndlocationByPrePath(WhFacilityRecPath prePath, RecFacilityPathCommand recFacilityPath, Long ouId) {
        WhFacilityRecPath recPath = new WhFacilityRecPath();
        recPath.setBatch(recFacilityPath.getBatch());
        recPath.setBatchContainerQty(Constants.DEFAULT_INTEGER);
        recPath.setContainerCode(recFacilityPath.getContainerCode());
        recPath.setSeedingwallCode(prePath.getSeedingwallCode());
        recPath.setSeedingwallUpperLimit(prePath.getSeedingwallUpperLimit());
        recPath.setSeedingwallCheckCode(prePath.getSeedingwallCheckCode());
        recPath.setTemporaryStorageLocationCheckCode(prePath.getTemporaryStorageLocationCheckCode());
        recPath.setTemporaryStorageLocationCode(prePath.getTemporaryStorageLocationCode());
        recPath.setTransitLocationCheckCode(prePath.getTransitLocationCheckCode());
        recPath.setTransitLocationCode(prePath.getTransitLocationCode());
        recPath.setOuId(ouId);
        // @Gianni 修改状态为新建 2017-04-10
        recPath.setStatus(1);
        this.whFacilityRecPathDao.insert(recPath);
        return recPath;
    }

    private WhTemporaryStorageLocation getTopFreeStorageLocation(boolean locationFlag, Long workingStorageSectionId, String batch, Long ouId) {
        WhTemporaryStorageLocation whTemporaryStorageLocation = null;
        while (locationFlag) {
            whTemporaryStorageLocation = this.whTemporaryStorageLocationDao.getTopFreeStorageLocationByWorkingStorageSectionId(workingStorageSectionId, ouId);
            if (whTemporaryStorageLocation == null) {
                locationFlag = false;
            } else {
                whTemporaryStorageLocation.setStatus(Constants.WH_GLOBAL_STATUS_2);
                whTemporaryStorageLocation.setBatch(batch);
                int count = this.whTemporaryStorageLocationDao.saveOrUpdateByVersion(whTemporaryStorageLocation);
                if (count <= 0) {
                    locationFlag = true;
                } else {
                    locationFlag = false;
                }
            }
        }
        return whTemporaryStorageLocation;
    }

    private WhOutboundFacility getTopFreeOutBoundFacility(boolean facilityFlag, Long facilityGroupId, String batch, Long ouId) {
        WhOutboundFacility facility = null;
        // 播种墙推荐
        while (facilityFlag) {

            facility = this.WhOutboundFacilityDao.getTopFreeOutBoundFacilityByFacilityGroupId(facilityGroupId, ouId);
            if (facility == null) {
                facilityFlag = false;
            } else {
                facility.setStatus(Constants.WH_GLOBAL_STATUS_2);
                facility.setBatch(batch);
                int count = this.WhOutboundFacilityDao.saveOrUpdateByVersion(facility);
                if (count <= 0) {
                    facilityFlag = true;
                } else {
                    facilityFlag = false;
                }
            }
        }
        return facility;
    }

    private WhOutboundFacility getTopFreeOutBoundFacility(Long facilityGroupId, String batch, Long ouId) {
        WhOutboundFacility facility = this.WhOutboundFacilityDao.getTopFreeOutBoundFacilityByFacilityGroupId(facilityGroupId, ouId);
        // 播种墙推荐
        if (facility == null) {
            return facility;
        }
        facility.setStatus(Constants.WH_GLOBAL_STATUS_2);
        facility.setBatch(batch);
        int count = this.WhOutboundFacilityDao.saveOrUpdateByVersion(facility);
        if (count <= 0) {
            return null;
        }
        return facility;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void matchSeedingWalBySortQueue(WhFacilityQueue queue) {
        // 这边的逻辑：
        // 1.修改对应播种墙&状态并且把对应批次号进行绑定
        // 2.修改设施推荐路径对应批次数据
        // 3.删除对应播种墙推荐队列数据
        // 查找暂存库位-》暂存区域-》暂存区域对应的播种墙组-》按照优先级找到空闲的播种墙
        Long ouId = queue.getOuId();
        String batch = queue.getBatch();
        WhTemporaryStorageLocation storageLocation = this.whTemporaryStorageLocationDao.findByIdAndOuId(queue.getTemporaryStorageLocationId(), ouId);// 暂存库位
        WhOutboundFacilityGroup whOutboundFacilityGroup = new WhOutboundFacilityGroup();
        whOutboundFacilityGroup.setOuId(ouId);
        whOutboundFacilityGroup.setLifecycle(Constants.LIFECYCLE_START);
        whOutboundFacilityGroup.setWorkingStorageSectionId(storageLocation.getWorkingStorageSectionId());
        whOutboundFacilityGroup.setFacilityGroupType(Constants.FACILITY_GROUP_TYPE_SEEDINGWALL);
        List<WhOutboundFacilityGroup> groupList = this.whOutboundFacilityGroupDao.findListByParam(whOutboundFacilityGroup);
        if (groupList == null || groupList.size() == 0) {
            return;
        }
        WhOutboundFacilityGroup facilityGroup = groupList.get(0);// 暂存区域对应的播种墙组

        WhOutboundFacility facility = this.getTopFreeOutBoundFacility(facilityGroup.getId(), batch, ouId);// 寻找优先级最高的空闲的播种墙
        if (facility == null) {
            return;
        }

        // 更新推荐路径
        List<WhFacilityRecPath> recPathList = this.whFacilityRecPathDao.findWhFacilityRecPathByBatch(batch, ouId);
        if (recPathList != null && recPathList.size() > 0) {
            for (WhFacilityRecPath recPath : recPathList) {
                recPath.setSeedingwallCheckCode(facility.getCheckCode());
                recPath.setSeedingwallCode(facility.getFacilityCode());
                recPath.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
                int updateCount = this.whFacilityRecPathDao.saveOrUpdate(recPath);
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
        // 删除对应的推荐播种墙队列数据
        int delCount = this.whFacilityQueueDao.deleteExt(queue.getId(), queue.getOuId());
        if (delCount <= 0) {
            throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhFacilityQueue> getSortedQueue(Long ouId) {
        return this.whFacilityQueueDao.findSortedQueue(ouId);
    }

}
