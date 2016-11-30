package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;

@Service("locationReplenishmentManagerProxy")
public class LocationReplenishmentManagerProxyImpl extends BaseManagerImpl implements LocationReplenishmentManagerProxy {
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Override
    public void locationReplenishment(Warehouse wh, Location location, Long skuId) {
        // 计算是否需要补货
        Long ouId = wh.getId();
        String logId = "";

        Long maxQty = Constants.DEFAULT_LONG;
        Long upBound = 90l;
        Long downBound = 10l;
        Long minQty = (long) Math.floor(location.getVolume() * downBound / 100);

        SkuRedisCommand skuRedis = this.skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
        Sku sku = skuRedis.getSku();
        if (StringUtils.hasText(location.getSizeType())) {
            WhSkuWhmgmt skuWhmgmt = skuRedis.getWhSkuWhMgmt();
            if (skuWhmgmt != null) {
                if (skuWhmgmt.getTypeOfGoods() != null) {
                    LocationProductVolume locationProductVolume = this.locationManager.getLocationProductVolumeByPcIdAndSize(skuWhmgmt.getTwoLevelType(), location.getSizeType(), ouId);
                    maxQty = locationProductVolume.getVolume();
                }
            }
        }
        if (maxQty == Constants.DEFAULT_LONG) {

            maxQty = (long) Math.floor(location.getVolume() * (upBound / 100) / sku.getVolume());
        }


    }

}
