package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.LocationProductVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;

@Service("locationManager")
@Transactional
public class LocationManagerImpl extends BaseManagerImpl implements LocationManager {
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;
    @Autowired
    private LocationProductVolumeDao locationProductVolumeDao;

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
        location.setIsStatic(true);
        location.setIsMixStacking(false);

        return this.whLocationDao.findListByParam(location);
    }

    @Override
    public Long getBindedSkuByLocationId(Long locationId,Long ouId) {
        WhSkuLocationCommand command=new WhSkuLocationCommand();
        command.setLifecycle(Constants.LIFECYCLE_START);
        command.setLocationId(locationId);
        command.setOuId(ouId);
        List<WhSkuLocationCommand> list = this.whSkuLocationDao.findSkuLocationToShard(command);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0).getSkuId();
    }
}
