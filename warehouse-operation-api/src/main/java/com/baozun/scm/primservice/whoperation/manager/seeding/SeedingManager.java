package com.baozun.scm.primservice.whoperation.manager.seeding;


import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionSeedingWall;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
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

    public WhSeedingCollectionLine findSeedingCollectionLineById(Long seedingCollectionLineId, Long ouId);

    public int updateSeedingCollectionLineByVersion(WhSeedingCollectionLine seedingCollectionLine);

    public void batchFinishedSeedingWithException(WhOutboundFacilityCommand facilityCommand, List<WhSeedingCollectionCommand> seedingCollectionList, List<WhSeedingCollectionLineCommand> facilitySeedingCollectionLineList, Long logId);

    public void finishedSeedingByOutboundBox(Long facilityId, String batchNo, List<WhSeedingCollectionLineCommand> boxSeedingLineList, List<WhSkuInventory> odoOrgSkuInvList, List<WhSkuInventory> odoSeedingSkuInventoryList, Boolean isTabbInvTotal,
            Long userId, Long ouId, String logId);

    public void finishedSeedingByOdo(Long facilityId, String batchNo, List<WhSeedingCollectionLineCommand> odoSeedingLineList, WhSeedingWallLattice seedingWallLattice, List<WhSkuInventory> odoSeedingSkuInventoryList, Boolean isTabbInvTotal, Long userId,
            Long ouId, String logId);

    public List<WhSkuInventory> findSeedingOdoSkuInvByOdoLineIdUuid(Long odoLineId, Long ouId, String uuid);

    public void createOutboundBox(WhOutboundbox whOutboundbox, List<WhOutboundboxLine> whOutboundboxLineList);

    public int updateOutboundFacility(WhOutboundFacilityCommand facilityCommand, String logId);

    public Boolean isOutboundBoxAlreadyUsed(String outboundBoxCode, Long ouId, String logId);

    String liberateSeedingWall(String containerCode, Long ouId, Long userId, Long facilityId) throws Exception;
}
