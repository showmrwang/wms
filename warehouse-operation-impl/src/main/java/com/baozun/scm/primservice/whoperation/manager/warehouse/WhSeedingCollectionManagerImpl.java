package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

@Transactional
@Service("whSeedingCollectionManager")
public class WhSeedingCollectionManagerImpl extends BaseManagerImpl implements WhSeedingCollectionManager {

    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;
    @Autowired
    private ContainerDao containerDao;

    @Override
    public void updateContainerToSeedingWall(String facilityCode, String containerCode, String batch, Long ouId) {
        WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(facilityCode, ouId);
        if (null == facility) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        ContainerCommand container = containerDao.getContainerByCode(containerCode, ouId);
        if (null == container) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        if (StringUtils.isEmpty(batch)) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        int updateCount = whSeedingCollectionDao.updateContainerToSeedingWall(facility.getId(), container.getId(), batch, ouId);
        if (updateCount != 1) {
            throw new BusinessException(ErrorCodes.UPDATE_FAILURE);
        }
    }

    /**
     * 获取播种墙集货信息
     *
     * @param facilityId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingCollection> getSeedingCollectionByFacilityId(Long facilityId, Long ouId) {
        return whSeedingCollectionDao.getSeedingCollectionByFacilityId(facilityId, ouId);
    }

}
