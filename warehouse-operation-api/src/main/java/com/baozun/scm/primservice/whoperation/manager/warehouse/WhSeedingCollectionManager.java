package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

public interface WhSeedingCollectionManager extends BaseManager {

    /**
     * 记录容器集货信息
     * 
     * @param containerCode
     * @param batch
     * @param batch2
     * @param ouId
     */
    void updateContainerToSeedingWall(String facilityCode, String containerCode, String batch, Long ouId);

    /**
     * 获取播种墙集货信息
     *
     * @param facilityId
     * @param ouId
     * @return
     */
    public List<WhSeedingCollectionCommand> getSeedingCollectionByFacilityId(Long facilityId, Long ouId, Integer collectionStatus);



    /**
     * 获取播种的周转箱信息
     *
     * @param facilityId
     * @param ouId
     * @param turnoverBoxCode
     * @return
     */
    public WhSeedingCollectionCommand getSeedingCollectionByTurnoverBox(Long facilityId, String turnoverBoxCode, Long ouId);

    public WhSeedingCollectionCommand getSeedingCollectionById(Long seedingCollectionId, Long ouId);

    public WhSeedingCollection findByIdExt(Long seedingCollectionId, Long ouId);



    public int updateByVersion(WhSeedingCollection seedingCollection);

    /**
     * 获取播种批次下的出库单信息，用于和播种墙货格绑定
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    List<WhSeedingWallLattice> getSeedingBatchOdoInfo(String batchNo, Long ouId);
    
    /**
     * 获取播种批次下的出库单明细信息
     *
     * @param odoLineId
     * @param ouId
     * @return
     */
    List<WhSeedingWallLatticeLine> getSeedingBatchOdoLineInfo(Long odoLineId, Long ouId);
    
}
