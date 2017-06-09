package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityGroupSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityLocationSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityGroupSkuVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityLocationSkuVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;

@Service("whFacilityGroupSkuVolumeManager")
@Transactional
public class WhFacilityGroupSkuVolumeManagerImpl extends BaseManagerImpl implements WhFacilityGroupSkuVolumeManager {

    @Autowired
    private WhFacilityGroupSkuVolumeDao whFacilityGroupSkuVolumeDao;

    @Autowired
    private WhFacilityLocationSkuVolumeDao whFacilityLocationSkuVolumeDao;

    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFacilityGroupSkuVolume findSkuByCheckLocationSerialNumber(Long lsvId, Integer serialNumber, Long ouId) {
        WhFacilityGroupSkuVolume whFacilityGroupSkuVolume = new WhFacilityGroupSkuVolume();
        WhFacilityLocationSkuVolume facilityLsv = whFacilityLocationSkuVolumeDao.findByLsvAndOuId(lsvId, ouId);
        if (null != facilityLsv) {
            Long facilityId = facilityLsv.getFacilityId();
            WhOutboundFacility whOutboundFacility = whOutboundFacilityDao.findByIdAndOuId(facilityId, ouId);
            if (null != whOutboundFacility) {
                Long facilityGroupId = whOutboundFacility.getFacilityGroup();
                WhFacilityGroupSkuVolume fgsv = new WhFacilityGroupSkuVolume();
                fgsv.setOuId(ouId);
                fgsv.setFacilityGroupId(facilityGroupId);
                fgsv.setSerialNumber(serialNumber);
                List<WhFacilityGroupSkuVolume> whFacilityGroupSkuVolumeList = this.whFacilityGroupSkuVolumeDao.findListByParam(fgsv);
                if (null != whFacilityGroupSkuVolumeList && !whFacilityGroupSkuVolumeList.isEmpty()) {
                    whFacilityGroupSkuVolume = whFacilityGroupSkuVolumeList.get(0);
                }
            }
        }
        return whFacilityGroupSkuVolume;
    }
}
