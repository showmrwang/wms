package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityGroupSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityGroupSkuVolume;

@Service("whFacilityGroupSkuVolumeManager")
@Transactional
public class WhFacilityGroupSkuVolumeManagerImpl extends BaseManagerImpl implements WhFacilityGroupSkuVolumeManager {

    @Autowired
    private WhFacilityGroupSkuVolumeDao whFacilityGroupSkuVolumeDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFacilityGroupSkuVolume findSkuByCheckLocationSerialNumber(Integer serialNumber, Long ouId) {
        WhFacilityGroupSkuVolume whFacilityGroupSkuVolume = new WhFacilityGroupSkuVolume();
        List<WhFacilityGroupSkuVolume> whFacilityGroupSkuVolumeList = this.whFacilityGroupSkuVolumeDao.findListByParam(whFacilityGroupSkuVolume);
        if (null == whFacilityGroupSkuVolumeList || whFacilityGroupSkuVolumeList.isEmpty()) {
            throw new BusinessException("没有设施组");
        }
        return whFacilityGroupSkuVolumeList.get(0);
    }
}
