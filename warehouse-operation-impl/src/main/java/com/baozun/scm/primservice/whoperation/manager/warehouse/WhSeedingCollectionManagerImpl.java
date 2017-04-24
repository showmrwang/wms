package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;

@Transactional
@Service("whSeedingCollectionManager")
public class WhSeedingCollectionManagerImpl extends BaseManagerImpl implements WhSeedingCollectionManager {

    public static final Logger log = LoggerFactory.getLogger(WhSeedingCollectionManagerImpl.class);
    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private CacheManager cacheManager;

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
    public List<WhSeedingCollectionCommand> getSeedingCollectionByFacilityId(Long facilityId, Long ouId) {
        List<WhSeedingCollectionCommand> seedingCollList = whSeedingCollectionDao.getSeedingCollectionByFacilityId(facilityId, ouId);

        return seedingCollList;
    }




    /**
     * 获取播种的周转箱信息
     *
     * @param facilityId
     * @param ouId
     * @param turnoverBoxCode
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSeedingCollectionCommand getSeedingCollectionByTurnoverBox(Long facilityId, String turnoverBoxCode, Long ouId){
        return whSeedingCollectionDao.getSeedingCollectionByTurnoverBox(facilityId, turnoverBoxCode, ouId);
    }


    /**
     * 获取播种的周转箱信息
     *
     * @param seedingCollection
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateByVersion(WhSeedingCollection seedingCollection){
         whSeedingCollectionDao.saveOrUpdateByVersion(seedingCollection);
    }

    /**
     * 获取播种批次下的出库单信息，用于和播种墙货格绑定
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingWallLattice> getSeedingBatchOdoInfo(String batchNo, Long ouId){
        return whSeedingCollectionDao.getSeedingBatchOdoInfo(batchNo, ouId);
    }

}
