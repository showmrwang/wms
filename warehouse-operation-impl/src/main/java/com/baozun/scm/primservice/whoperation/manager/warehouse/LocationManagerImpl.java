package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.LocationProductVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.LocationSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationSkuVolume;

@Service("locationManagerProxy")
@Transactional
public class LocationManagerImpl extends BaseManagerImpl implements LocationManager {
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;
    @Autowired
    private LocationProductVolumeDao locationProductVolumeDao;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private LocationSkuVolumeDao locationSkuVolumeDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public LocationProductVolume getLocationProductVolumeByPcIdAndSize(Long pcId, String sizeType, Long ouId) {
        LocationProductVolume search = new LocationProductVolume();
        search.setOuId(ouId);
        search.setPcId(pcId);
        search.setSize(sizeType);
        search.setUnit(Constants.ALLOCATE_UNIT_PIECE);
        List<LocationProductVolume> result = this.locationProductVolumeDao.findListByParam(search);
        return (result == null || result.size() == 0) ? null : result.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> sortByIds(Set<Long> ids, Long ouId) {
        return this.whLocationDao.sortByIds(ids, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Location> findLocationWithStaticNoMix(Long ouId) {
        Location location = new Location();
        location.setOuId(ouId);
        location.setIsCheckLocation(false);
        location.setIsStatic(true);
        location.setIsMixStacking(false);

        return this.whLocationDao.findListByParam(location);
    }

    @Override
    public Long getBindedSkuByLocationId(Long locationId, Long ouId) {
        WhSkuLocationCommand command = new WhSkuLocationCommand();
        command.setLifecycle(Constants.LIFECYCLE_START);
        command.setLocationId(locationId);
        command.setOuId(ouId);
        List<WhSkuLocationCommand> list = this.whSkuLocationDao.findSkuLocationToShard(command);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0).getSkuId();
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public SkuRedisCommand findSkuMasterBySkuId(Long skuId, Long ouId, String logId) {
        return whSkuDao.findSkuAllInfoByParamExt(skuId, ouId);
    }

    @Override
    public List<Location> findCheckLocationWithStaticNoMix(Long ouId) {
        Location location = new Location();
        location.setOuId(ouId);
        location.setIsCheckLocation(true);
        location.setIsStatic(true);
        location.setIsMixStacking(false);

        return this.whLocationDao.findListByParam(location);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<LocationSkuVolume> findListByfacilityId(Long facilityId, Long ouId) {
        // 根据复核台id查出所对应的库位信息
        // 先找出所有库位id在根据库位id找到对应的库为名 和该库位下商品剩余数
        List<LocationSkuVolume> list = new ArrayList<>();
        try {
            List<Long> locationIds = locationSkuVolumeDao.findLocationIdsByfacilityId(facilityId, ouId);
            Long a = 1L;
            if (null != locationIds && locationIds.size() > 0) {
                for (Long locationId : locationIds) {

                    LocationSkuVolume locationSkuVolume = getLocationSkuVolumeByLocationIdAndOuid(ouId, locationId);
                    list.add(locationSkuVolume);
                    locationSkuVolume.setId(a);
                    a++;
                }
            }
            return list;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.SKU_IS_NULL_BY_ID);
        }
    }

    /**
     * 通过库位id和ouid获取库位库存信息
     * 
     * @param ouId
     * @param locationId
     * @return
     */
    private LocationSkuVolume getLocationSkuVolumeByLocationIdAndOuid(Long ouId, Long locationId) {
        LocationSkuVolume locationSkuVolume = locationSkuVolumeDao.findListBylocationId(locationId, ouId);
        Long OnHandQty = whSkuInventoryDao.findOnHandQtyByLocationId(locationId, ouId);
        locationSkuVolume.setOnHandQty(OnHandQty);
        return locationSkuVolume;
    }

}
