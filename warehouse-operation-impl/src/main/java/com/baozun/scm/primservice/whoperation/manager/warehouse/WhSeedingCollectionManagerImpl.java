package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;








import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.seeding.OpOutBoundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;

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
    public List<WhSeedingCollection> getSeedingCollectionByFacilityId(Long facilityId, Long ouId) {
        return whSeedingCollectionDao.getSeedingCollectionByFacilityId(facilityId, ouId);
    }

    /**
     * 判断周转箱是否绑定对应播种墙
     *
     * @param facilityId
     * @param containerId
     * @param collectionStatus
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingCollection> getSeedingCollectionLst(Long facilityId,  String turnoverBoxCode, String collectionStatus, Long ouId) {
//        TODO Long containerId
//        List<WhSeedingCollection> whSeedingCollectionLst = whSeedingCollectionDao
//        return whSeedingCollectionLst;
        return whSeedingCollectionDao.getSeedingCollectionByFacilityId(facilityId, ouId);
    }

    /***
     * 查询播种墙对应的周转箱的sku信息(周转箱显示)
     * @param facilityCode
     * @param checkCode
     * @param turnoverBoxCode
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OpOutBoundFacilityCommand> findFacilityToSku(Long facilityId,String turnoverBoxCode,Long ouId){
        log.info("WhOutboundFacilityManagerImpl findFacilityToSku is start");
        WhOutboundFacility   whOutboundFacility =  whOutboundFacilityDao.findByIdAndOuId(facilityId, ouId);
        if(null == whOutboundFacility) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        String facilityCode = whOutboundFacility.getFacilityCode();
        Map<String,WhSeedingCollectionLine> seedingCollectionLineMap = cacheManager.getObject("SEEDING"+ouId+facilityCode+whOutboundFacility.getBatch()+turnoverBoxCode);
        if(null == seedingCollectionLineMap || seedingCollectionLineMap.size() == 0) {
//            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
              return null;
        }
        Set<String> keys = seedingCollectionLineMap.keySet();
        List<OpOutBoundFacilityCommand> facilityCmdList = new ArrayList<OpOutBoundFacilityCommand>();
        for(String key:keys) {
            OpOutBoundFacilityCommand facilityCmd = new OpOutBoundFacilityCommand();
            WhSeedingCollectionLine seedingCoLine = seedingCollectionLineMap.get(key);
            //获取已经播种的数量
            Long count = cacheManager.getObject("SEEDING"+ouId+facilityCode+whOutboundFacility.getBatch()+turnoverBoxCode+key);
            facilityCmd.setSkuCode(seedingCoLine.getSkuCode());
            facilityCmd.setSkuBarCode(seedingCoLine.getSkuBarCode());
            facilityCmd.setSkuExtCode(seedingCoLine.getSkuExtCode());
            facilityCmd.setFacilityQty(count);  //已经播种的数量
            facilityCmd.setSumQty(seedingCoLine.getQty());  //总数量
            facilityCmdList.add(facilityCmd);
        }
        log.info("WhOutboundFacilityManagerImpl findFacilityToSku is end");
        return facilityCmdList;
    }
    
    /***
     * 查询播种墙对应的周转箱信息
     * @param facilityCode
     * @param checkCode
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OpOutBoundFacilityCommand> findFacilityToTurnoverBox(Long facilityId,Long ouId){
        log.info("WhOutboundFacilityManagerImpl findFacilityToTurnoverBox is start");
        List<Integer> collstatusList = new ArrayList<Integer>();
        collstatusList.add(CollectionStatus.TO_SEED);  // 待播种
        collstatusList.add(CollectionStatus.SEEDING);   //播种中
        List<WhSeedingCollection> seedingCollectionList = whSeedingCollectionDao.findSeedingCollection(facilityId, collstatusList, ouId);
        if(null == seedingCollectionList || seedingCollectionList.size() == 0) {
//          throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
            return null;
        }
        List<OpOutBoundFacilityCommand> facilityCmdList = new ArrayList<OpOutBoundFacilityCommand >();
        for(WhSeedingCollection seedingColl:seedingCollectionList){
            OpOutBoundFacilityCommand facilityCmd = new OpOutBoundFacilityCommand();
            Long containerId = seedingColl.getContainerId();
            Container c = containerDao.findByIdExt(containerId, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
            }
            facilityCmd.setTurnoverBoxCode(c.getCode());
        }
        log.info("WhOutboundFacilityManagerImpl findFacilityToTurnoverBox is end");
        return facilityCmdList;
    }
    
    /***
     * 货格出库箱显示
     * @param facilityCode
     * @param checkCode
     * @param latticeNo
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List< OpOutBoundFacilityCommand> findFacilityToLatticeNo(Long facilityId,Long ouId, Integer latticeNo,String outboundBoxCode){
        WhOutboundFacility   whOutboundFacility =  whOutboundFacilityDao.findByIdAndOuId(facilityId, ouId);
        if(null == whOutboundFacility) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        String facilityCode = whOutboundFacility.getFacilityCode();
        String key = "SEEDING"+ouId+facilityCode+whOutboundFacility.getBatch()+latticeNo+"ODOCODE";
        Map<String,WhSeedingWallLatticeLine>  seedingWallLatticeLineMap = cacheManager.getObject(key);
        if(null == seedingWallLatticeLineMap || seedingWallLatticeLineMap.size() == 0) {
//            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            return null;
        }
        List<OpOutBoundFacilityCommand> outBoundFacilityCmdList = new ArrayList< OpOutBoundFacilityCommand>();
        Set<String>  seedingWallSet = seedingWallLatticeLineMap.keySet();
        for(String keys:seedingWallSet) {
            //获取已经播种的数量
            OpOutBoundFacilityCommand facilityCmd = new OpOutBoundFacilityCommand();
            Long count = cacheManager.getObject("SEEDING"+ouId+facilityCode+whOutboundFacility.getBatch()+outboundBoxCode+key);
            WhSeedingWallLatticeLine  seedingWallLatticeLine = seedingWallLatticeLineMap.get(keys);
            facilityCmd.setLatticeQty(count);
            facilityCmd.setSkuBarCode(seedingWallLatticeLine.getSkuBarCode());
            //设置周转箱状态缓存
            String turnoverBoxCode = "";
            Integer status =  cacheManager.getObject("CACHE_TURNOVERBOX_STATUS"+turnoverBoxCode);
            if(null == status) {   //如果为空统一设置为待播种
               cacheManager.setObject("CACHE_TURNOVERBOX_STATUS"+turnoverBoxCode, CollectionStatus.TO_SEED, CacheConstants.CACHE_ONE_DAY);
               status = CollectionStatus.TO_SEED;
            }
            facilityCmd.setTurnoverBoxStatus(status);
            outBoundFacilityCmdList.add(facilityCmd);
        }
        return outBoundFacilityCmdList;
    }
    
    public Integer cahceTurnoverBoxStatus(String turnoverBoxCode){
        log.info("WhOutboundFacilityManagerImpl cahceTurnoverBoxStatus is start");
        log.info("WhOutboundFacilityManagerImpl cahceTurnoverBoxStatus is end");
        return null;
    }
}
