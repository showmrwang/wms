package com.baozun.scm.primservice.whoperation.manager.seeding;


import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionSeedingWall;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

public interface SeedingManager extends BaseManager {

    /**
     * 根据容器编码查找容器
     *
     * @param code
     * @param ouId
     * @return
     */
    ContainerCommand getContainerByCode(String code, Long ouId);

    /**
     * 获取播种周转箱的数据
     * 
     * @param containerId
     * @param ouId
     * @return
     */
    public List<WhSeedingCollectionLine> findSeedingDataByContainerId(Long containerId, Long ouId);

    /**
     * 获取播种中出库单明细信息
     *
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhSeedingWallLatticeLine> getSeedingOdoLineInfo(Long odoId, Long ouId);

    /**
     * 获取出库设施
     * 
     * @param facilityCode
     * @param ouId
     * @return
     */
    WhOutboundFacilityCommand getOutboundFacilityByFacilityCode(String facilityCode, Long ouId);

    /**
     * 获取出库设施
     *
     * @param facilityCheckCode
     * @param ouId
     * @return
     */
    WhOutboundFacilityCommand getOutboundFacilityByFacilityCheckCode(String facilityCheckCode, Long ouId);


    WhOutboundFacilityCommand getOutboundFacilityById(Long facilityId, Long ouId);

    /**
     * 根据id 和ouId 获取出库箱类型
     * 
     * @param id
     * @param ouId
     * @return
     */
    public OutInvBoxTypeCommand findOutInventoryBoxType(Long id, Long ouId);

    /**
     * 查询播种墙功能配置
     * 
     * @param id
     * @param ouId
     * @return
     */
    public WhFunctionSeedingWall findFunctionById(Long id, Long ouId);

    /**
     *
     * @param seedingCollectionId
     * @param ouId
     * @return
     */
    public List<WhSeedingCollectionLineCommand> getSeedingCollectionLineByCollection(Long seedingCollectionId, Long ouId);

    public void batchFinishedSeedingWithException(Long facilityId, Long ouId);

    public void batchFinishedSeeding(Long facilityId, String batchNo, List<WhSeedingWallLattice> facilitySeedingOdoList, List<WhSkuInventory> facilitySeedingSkuInventoryList, Boolean isTabbInvTotal, Long userId, Long ouId, String logId);
}
