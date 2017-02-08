package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

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

}
