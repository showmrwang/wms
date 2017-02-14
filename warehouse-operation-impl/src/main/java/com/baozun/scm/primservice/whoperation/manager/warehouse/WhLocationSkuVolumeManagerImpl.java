package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhLocationSkuVolume;

@Transactional
@Service("whLocationSkuVolumeManager")
public class WhLocationSkuVolumeManagerImpl extends BaseManagerImpl implements WhLocationSkuVolumeManager {

    @Autowired
    private WhLocationSkuVolumeDao whLocationSkuVolumeDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhLocationSkuVolume findSkuByCheckLocation(Long locationId, Long ouId) {
        WhLocationSkuVolume whLocationSkuVolume = new WhLocationSkuVolume();
        List<WhLocationSkuVolume> whLocationSkuVolumeList = this.whLocationSkuVolumeDao.findListByParam(whLocationSkuVolume);
        if (null == whLocationSkuVolumeList || whLocationSkuVolumeList.isEmpty()) {
            throw new BusinessException("没有库位");
        }
        return whLocationSkuVolumeList.get(0);
    }
}
