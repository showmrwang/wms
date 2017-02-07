package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityQueueDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityGroupDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhTemporaryStorageLocationDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityQueue;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacilityGroup;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;

@Service("whFacilityRecPathManager")
@Transactional
public class WhFacilityRecPathManagerImpl extends BaseManagerImpl implements WhFacilityRecPathManager {
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
    public void occupyFacilityAndlocationByFacilityGroup(WhOutboundFacilityGroup facilityGroup, WhFacilityRecPath prePath, RecFacilityPathCommand recFacilityPath, Warehouse wh) {
        // 这边的逻辑分三部分：
        // 第一部分：如果已经有了推荐路径，则直接插入数据库，没有则推荐暂存库位、中转库位、播种墙。
        // 第二部分：推荐成功，插入推荐成功路径表，返回推荐播种墙成功信息，等待后方后续处理
        // 第三部分：最后一箱逻辑


        Long ouId = wh.getId();
        String batch = recFacilityPath.getBatch();
        String pickingMode = recFacilityPath.getPickingMode();

        // 如果已经有推荐路径
        WhFacilityRecPath recPath = null;
        if (prePath != null) {
            recPath = new WhFacilityRecPath();
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
            this.whFacilityRecPathDao.insert(recPath);
        } else {

            WhTemporaryStorageLocation fromLocaiton = this.whTemporaryStorageLocationDao.findByIdAndOuId(recFacilityPath.getAreaId(), ouId);// 来源库位
            boolean facilityFlag, locationFlag = true;
            if (Constants.SEEDING_MODE_1.equals(pickingMode)) {
                facilityFlag = true;
            } else {
                facilityFlag = false;
            }
            // 暂存库位推荐
            WhTemporaryStorageLocation toLocation = this.getTopFreeStorageLocation(locationFlag, facilityGroup.getWorkingStorageSectionId(), ouId);
            if (toLocation == null) {
                throw new BusinessException(ErrorCodes.FACILITYMATCH_NO_TEMPORARYSTORAGELOCATION);
            }
            // 查询中转库位
            WhTemporaryStorageLocation transitLocation = new WhTemporaryStorageLocation();
            WhFacilityPath facilityPathSearch = new WhFacilityPath();
            facilityPathSearch.setOuId(ouId);
            facilityPathSearch.setFromAreaCode(fromLocaiton.getTemporaryStorageCode());
            facilityPathSearch.setToAreaCode(toLocation.getTemporaryStorageCode());
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
            // 设备
            WhOutboundFacility facility = null;
            if (facilityFlag) {
                facility = this.getTopFreeOutBoundFacility(facilityFlag, facilityGroup.getId(), ouId);
                if (facility == null) {
                    // #TODO 推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
                    recPath = new WhFacilityRecPath();
                    recPath.setBatch(recFacilityPath.getBatch());
                    recPath.setBatchContainerQty(1);
                    recPath.setContainerCode(recFacilityPath.getContainerCode());
                    recPath.setSeedingwallCode(null);
                    recPath.setSeedingwallUpperLimit(null);
                    recPath.setSeedingwallCheckCode(null);
                    recPath.setTemporaryStorageLocationCheckCode(toLocation.getCheckCode());
                    recPath.setTemporaryStorageLocationCode(toLocation.getTemporaryStorageCode());
                    recPath.setTransitLocationCheckCode(transitLocation.getCheckCode());
                    recPath.setTransitLocationCode(transitLocation.getTemporaryStorageCode());
                    recPath.setOuId(ouId);
                    this.whFacilityRecPathDao.insert(recPath);

                    WhFacilityQueue facilityQueue = new WhFacilityQueue();
                    facilityQueue.setBatch(recFacilityPath.getBatch());
                    facilityQueue.setTemporaryStorageLocationId(toLocation.getId());
                    facilityQueue.setCreateTime(new Date());
                    facilityQueue.setIsRec(false);
                    facilityQueue.setOuId(ouId);
                    this.whFacilityQueueDao.insert(facilityQueue);

                } else {
                    // #TODO 推荐成功，插入推荐成功路径表，返回推荐播种墙成功信息，等待后方后续处理
                    recPath = new WhFacilityRecPath();
                    recPath.setBatch(recFacilityPath.getBatch());
                    recPath.setBatchContainerQty(1);
                    recPath.setContainerCode(recFacilityPath.getContainerCode());
                    recPath.setSeedingwallCode(facility.getFacilityCode());
                    recPath.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
                    recPath.setSeedingwallCheckCode(facility.getCheckCode());
                    recPath.setTemporaryStorageLocationCheckCode(toLocation.getCheckCode());
                    recPath.setTemporaryStorageLocationCode(toLocation.getTemporaryStorageCode());
                    recPath.setTransitLocationCheckCode(transitLocation.getCheckCode());
                    recPath.setTransitLocationCode(transitLocation.getTemporaryStorageCode());
                    recPath.setOuId(ouId);
                    this.whFacilityRecPathDao.insert(recPath);
                }
            }
            // #TODO 推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
            // #TODO 推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
            recPath = new WhFacilityRecPath();
            recPath.setBatch(recFacilityPath.getBatch());
            recPath.setBatchContainerQty(1);
            recPath.setContainerCode(recFacilityPath.getContainerCode());
            recPath.setSeedingwallCode(null);
            recPath.setSeedingwallUpperLimit(null);
            recPath.setSeedingwallCheckCode(null);
            recPath.setTemporaryStorageLocationCheckCode(toLocation.getCheckCode());
            recPath.setTemporaryStorageLocationCode(toLocation.getTemporaryStorageCode());
            recPath.setTransitLocationCheckCode(transitLocation.getCheckCode());
            recPath.setTransitLocationCode(transitLocation.getTemporaryStorageCode());
            recPath.setOuId(ouId);
            this.whFacilityRecPathDao.insert(recPath);

            WhFacilityQueue facilityQueue = new WhFacilityQueue();
            facilityQueue.setBatch(recFacilityPath.getBatch());
            facilityQueue.setTemporaryStorageLocationId(toLocation.getId());
            facilityQueue.setCreateTime(new Date());
            facilityQueue.setIsRec(false);
            facilityQueue.setOuId(ouId);
            this.whFacilityQueueDao.insert(facilityQueue);
        }

        // 最后一箱逻辑
        if (recFacilityPath.isLastContainer()) {
            WhFacilityRecPath search = new WhFacilityRecPath();
            search.setOuId(ouId);
            search.setBatch(batch);

            List<WhFacilityRecPath> WhFacilityRecPathList = this.whFacilityRecPathDao.findListByParam(search);
            if (WhFacilityRecPathList == null || WhFacilityRecPathList.size() == 0) {
                throw new BusinessException(ErrorCodes.FACILITYMATCH_NO_FACILITYRECPATH);
            }
            int batchContainerQty = WhFacilityRecPathList.size();

            boolean isReleaseLocation = false;

            for (int i = 0; i < WhFacilityRecPathList.size(); i++) {
                WhFacilityRecPath rec = WhFacilityRecPathList.get(0);
                // 边拣货边播种
                if (Constants.SEEDING_MODE_1.equals(pickingMode)) {
                    // 占用的播种墙容量上限是否满足批次对应箱数
                    if (i == 0) {
                        isReleaseLocation = batchContainerQty <= rec.getSeedingwallUpperLimit() ? true : false;
                    }
                    if (isReleaseLocation) {
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

    }


    private WhTemporaryStorageLocation getTopFreeStorageLocation(boolean locationFlag, Long workingStorageSectionId, Long ouId) {
        WhTemporaryStorageLocation whTemporaryStorageLocation = null;
        while (locationFlag) {
            whTemporaryStorageLocation = this.whTemporaryStorageLocationDao.getTopFreeStorageLocationByWorkingStorageSectionId(workingStorageSectionId, ouId);
            if (whTemporaryStorageLocation == null) {
                locationFlag = false;
            } else {
                whTemporaryStorageLocation.setStatus(Constants.WH_GLOBAL_STATUS_2);
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

    private WhOutboundFacility getTopFreeOutBoundFacility(boolean facilityFlag, Long facilityGroupId, Long ouId) {
        WhOutboundFacility facility = null;
        // 播种墙推荐
        while (facilityFlag) {

            facility = this.WhOutboundFacilityDao.getTopFreeOutBoundFacilityByFacilityGroupId(facilityGroupId, ouId);
            if (facility == null) {
                facilityFlag = false;
            } else {
                facility.setStatus(Constants.WH_GLOBAL_STATUS_2);
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

}
