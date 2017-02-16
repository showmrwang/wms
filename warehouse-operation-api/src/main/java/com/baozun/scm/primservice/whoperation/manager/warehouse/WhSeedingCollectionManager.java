package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.seeding.OpOutBoundFacilityCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

public interface WhSeedingCollectionManager extends BaseManager {
	
	/**
	 * 记录容器集货信息
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
    public List<WhSeedingCollection> getSeedingCollectionByFacilityId(Long facilityId, Long ouId);
    
    /**
     * 判断周转箱是否绑定对应播种墙
     *
     * @param facilityId
     * @param containerId
     * @param collectionStatus
     * @param ouId
     * @return
     */
    public List<WhSeedingCollection> getSeedingCollectionLst(Long facilityId, String turnoverBoxCode, String collectionStatus, Long ouId);


    /***
     * 查询播种墙对应的周转箱的sku信息
     * @param facilityCode
     * @param checkCode
     * @param turnoverBoxCode
     * @return
     */
    public List<OpOutBoundFacilityCommand> findFacilityToSku(Long facilityId,String turnoverBoxCode,Long ouId);
    
    /***
     * 查询播种墙对应的周转箱信息
     * @param facilityCode
     * @param checkCode
     * @return
     */
    public List<OpOutBoundFacilityCommand> findFacilityToTurnoverBox(Long facilityId,Long ouId);
    
    /***
     * 
     * @param facilityCode
     * @param checkCode
     * @param latticeNo
     * @param ouId
     * @return
     */
    public List< OpOutBoundFacilityCommand> findFacilityToLatticeNo(Long facilityId,Long ouId, Integer latticeNo,String outboundBoxCode);
    
    /***
     * 缓存周转箱状态
     * @param turnoverBoxCode
     * @return
     */
    public Integer cahceTurnoverBoxStatus(String turnoverBoxCode);

    /**
     * 获取播种墙绑定的批次
     *
     * @param facilityId
     * @param ouId
     * @return
     */
    public List<String> getFacilityBindBatch(Long facilityId,Long ouId);
}
