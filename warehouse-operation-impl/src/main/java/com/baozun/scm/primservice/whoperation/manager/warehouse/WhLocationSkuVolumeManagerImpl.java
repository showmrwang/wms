package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhLocationSkuVolumeCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhLocationSkuVolume;

@Transactional
@Service("whLocationSkuVolumeManager")
public class WhLocationSkuVolumeManagerImpl extends BaseManagerImpl implements WhLocationSkuVolumeManager {
    private static final Logger log = LoggerFactory.getLogger(WhLocationSkuVolumeManager.class);

    @Autowired
    private WhLocationSkuVolumeDao whLocationSkuVolumeDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhLocationSkuVolume findSkuByCheckLocation(Long locationId, Long ouId) {
        log.info("WhLocationSkuVolumeManager.findSkuByCheckLocation start...");
        log.info("param: locationId: [{}], ouId: [{}]", locationId, ouId);
        WhLocationSkuVolume whLocationSkuVolume = new WhLocationSkuVolume();
        whLocationSkuVolume.setLocationId(locationId);
        whLocationSkuVolume.setOuId(ouId);
        List<WhLocationSkuVolume> whLocationSkuVolumeList = this.whLocationSkuVolumeDao.findListByParam(whLocationSkuVolume);
        if (null == whLocationSkuVolumeList || whLocationSkuVolumeList.isEmpty()) {
            log.error("location not found");
            throw new BusinessException(ErrorCodes.LOCATION_BARCODE_IS_ERROR);
        }
        log.info("WhLocationSkuVolumeManager.findSkuByCheckLocation finish...");
        return whLocationSkuVolumeList.get(0);
    }

    /**
     * 根据复核台ID查找库位商品容量信息
     *
     * @author mingwei.xie
     * @param facilityId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhLocationSkuVolumeCommand> findLocSkuVolumeByFacilityId(Long facilityId, Long ouId) {
        return whLocationSkuVolumeDao.findLocSkuVolumeByFacilityId(facilityId, ouId);
    }

    /**
     * 查找商品对应的库位容量信息
     *
     * @param skuId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhLocationSkuVolumeCommand> findFacilityLocSkuVolumeBySkuId(Long facilityId, Long skuId, Long ouId) {
        return whLocationSkuVolumeDao.findFacilityLocSkuVolumeBySkuId(facilityId, skuId, ouId);
    }

    /**
     * 查找商品对应的库位容量信息
     *
     * @param skuId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhLocationSkuVolumeCommand findFacilityLocSkuVolumeByLocSku(Long facilityId, String locationCode, Long skuId, Long ouId) {
        return whLocationSkuVolumeDao.findFacilityLocSkuVolumeByLocSku(facilityId, locationCode, skuId, ouId);
    }
}
