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
    public RecFacilityPathCommand occupyFacilityAndlocationByFacilityGroup(WhOutboundFacilityGroup facilityGroup, RecFacilityPathCommand recFacilityPath, Warehouse wh) {
        Long ouId = wh.getId();
        WhTemporaryStorageLocation fromLocaiton = this.whTemporaryStorageLocationDao.findByIdAndOuId(recFacilityPath.getAreaId(), ouId);// 来源库位
        String pickingMode = recFacilityPath.getPickingMode();
        boolean facilityFlag, locationFlag = true;
        if (Constants.SEEDING_MODE_1.equals(pickingMode)) {
            facilityFlag = true;
        } else {
            facilityFlag = false;
        }
        // 暂存库位推荐
        WhTemporaryStorageLocation toLocation = this.getTopFreeStorageLocation(locationFlag, facilityGroup.getWorkingStorageSectionId(), ouId);
        if (toLocation == null) {
            recFacilityPath.setStatus(0);
            return recFacilityPath;
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
               //#TODO 推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
                WhFacilityRecPath recPath = new WhFacilityRecPath();
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

                recFacilityPath.setStatus(1);
                return recFacilityPath;
            }else{
               //#TODO 推荐成功，插入推荐成功路径表，返回推荐播种墙成功信息，等待后方后续处理 
                WhFacilityRecPath recPath = new WhFacilityRecPath();
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



                recFacilityPath.setStatus(1);
                return recFacilityPath;
            }
        }
        //#TODO 推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
        // #TODO 推荐成功 插入推荐成功路径表，并且保存对应的设施队列列表信息
        WhFacilityRecPath recPath = new WhFacilityRecPath();
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

        recFacilityPath.setStatus(1);
        return recFacilityPath;
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
