package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacilityGroup;

public interface WhFacilityRecPathManager extends BaseManager {
    /**
     * 查找播种墙推荐路径
     * 
     * @param batch
     * @param containerCode
     * @param ouId
     * @return
     */
    List<WhFacilityRecPath> findWhFacilityRecPathByBatchAndContainer(String batch, String containerCode, Long ouId);

    /**
     * 查找播种墙组
     * 
     * @param outboundFacilityGroupId
     * @param ouId
     * @return
     */
    WhOutboundFacilityGroup findOutboundFacilityGroupById(Long outboundFacilityGroupId, Long ouId);

    /**
     * 占用设备逻辑
     * 
     * @param facilityGroup
     * @param prePath
     * @param recFacilityPath
     * @param wh
     * @return
     */
    void occupyFacilityAndlocationByFacilityGroup(WhOutboundFacilityGroup facilityGroup, WhFacilityRecPath prePath, RecFacilityPathCommand recFacilityPath, Warehouse wh);
}
