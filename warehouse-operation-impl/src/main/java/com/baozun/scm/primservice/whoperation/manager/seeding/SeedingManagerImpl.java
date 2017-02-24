/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.seeding;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;

@Service("seedingManager")
public class SeedingManagerImpl extends BaseManagerImpl implements SeedingManager {
    public static final Logger log = LoggerFactory.getLogger(SeedingManagerImpl.class);

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;

    /**
     * 根据容器编码查找容器
     *
     * @param code
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand getContainerByCode(String code, Long ouId) {
        return containerDao.getContainerByCode(code, ouId);
    }

    /**
     * 获取播种周转箱的数据
     *
     * @param containerId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingCollectionLine> findSeedingDataByContainerId(Long containerId, Long ouId) {
        return whSkuInventoryDao.findSeedingDataByContainerId(containerId, ouId);
    }

    /**
     * 获取播种中出库单明细信息
     *
     * @param odoId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingWallLatticeLine> getSeedingOdoLineInfo(Long odoId, Long ouId) {
        return whOdoDao.getSeedingOdoLineInfo(odoId, ouId);
    }

    /**
     * 获取出库设施
     *
     * @param facilityCode
     * @param ouId
     * @return
     */
    @Override
    public WhOutboundFacilityCommand getOutboundFacilityByFacilityCode(String facilityCode, Long ouId) {
        return whOutboundFacilityDao.getOutboundFacilityByCode(facilityCode, null, ouId);
    }

    /**
     * 获取出库设施
     *
     * @param facilityCheckCode
     * @param ouId
     * @return
     */
    @Override
    public WhOutboundFacilityCommand getOutboundFacilityByFacilityCheckCode(String facilityCheckCode, Long ouId) {
        return whOutboundFacilityDao.getOutboundFacilityByCode(null, facilityCheckCode, ouId);
    }

    @Override
    public WhOutboundFacilityCommand getOutboundFacilityById(Long facilityId, Long ouId) {
        return whOutboundFacilityDao.findByIdExt(facilityId, ouId);
    }

    /**
     * 根据id 和ouId 获取出库箱类型
     *
     * @param id
     * @param ouId
     * @return
     */
    @Override
    public OutInvBoxTypeCommand findOutInventoryBoxType(Long id, Long ouId) {
        return outBoundBoxTypeDao.findOutInventoryBoxType(id, ouId);
    }
}
