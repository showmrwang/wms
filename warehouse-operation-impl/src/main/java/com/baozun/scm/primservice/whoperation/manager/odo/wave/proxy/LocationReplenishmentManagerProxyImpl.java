package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ReplenishmentMsgManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentMsg;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;

@Service("locationReplenishmentManagerProxy")
public class LocationReplenishmentManagerProxyImpl extends BaseManagerImpl implements LocationReplenishmentManagerProxy {
    private static final Logger log = LoggerFactory.getLogger(LocationReplenishmentManagerProxy.class);

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
    @Autowired
    private WhSkuInventoryManager whskuInventoryManager;
    @Autowired
    private ReplenishmentMsgManager replenishmentMsgManager;
    @Autowired
    private ReplenishedManagerProxy replenishedManagerProxy;


    @Override
    public void locationReplenishmentMsg(Warehouse wh, List<Location> locationList) {
        try {
            Long ouId = wh.getId();
            for (Location l : locationList) {
                Long skuId = this.whskuInventoryManager.findSkuInInventoryByLocation(l.getId(), ouId);
                if (skuId == null) {
                    skuId = this.locationManager.getBindedSkuByLocationId(l.getId(), ouId);
                }
                if (skuId == null) {
                    continue;
                }
                this.locationReplenishmentMsg(wh, l, skuId);
            }
        } catch (Exception e) {

        }
    }

    private void locationReplenishmentMsg(Warehouse wh, Location location, Long skuId) {
        // 计算是否需要补货
        Long ouId = wh.getId();
        String logId = "";
        Long locationId = location.getId();

        Long maxQty = Constants.DEFAULT_LONG;
        Long minQty = Constants.DEFAULT_LONG;

        Integer upBound = location.getUpBound();
        Integer downBound = location.getDownBound();
        if (upBound == null || downBound == null) {
            return;
        }

        SkuRedisCommand skuRedis = this.locationManager.findSkuMasterBySkuId(skuId, ouId, logId);
        Sku sku = skuRedis.getSku();
        Long locationQty = (long) Math.floor(location.getVolume() / sku.getVolume());
        if (StringUtils.hasText(location.getSizeType())) {
            WhSkuWhmgmt skuWhmgmt = skuRedis.getWhSkuWhMgmt();
            if (skuWhmgmt != null) {
                if (skuWhmgmt.getTypeOfGoods() != null) {
                    LocationProductVolume locationProductVolume = this.locationManager.getLocationProductVolumeByPcIdAndSize(skuWhmgmt.getTypeOfGoods(), location.getSizeType(), ouId);
                    if (locationProductVolume != null) {

                        locationQty = locationProductVolume.getVolume();
                    }
                }
            }
        }
        // 上下限数量
        maxQty = locationQty * upBound / 100;
        minQty = locationQty * downBound / 100;

        // 库位库存量=库位在库库存+库位待移入库存
        double invQty = this.whskuInventoryManager.findInventoryByLocation(locationId, ouId);

        if (invQty >= minQty) {
            return;
        }

        Long replenishmentQty = (long) Math.floor(maxQty - invQty);
        ReplenishmentMsg replenishmentMsg = this.replenishmentMsgManager.findMsgbyLocIdAndSkuId(locationId, skuId, ouId);
        if (replenishmentMsg == null) {
            replenishmentMsg = new ReplenishmentMsg();
            replenishmentMsg.setLocationId(locationId);
            replenishmentMsg.setSkuId(skuId);
            replenishmentMsg.setUpperLimitQty(replenishmentQty);
            replenishmentMsg.setOuId(ouId);
            replenishmentMsg.setCreateTime(new Date());
            replenishmentMsg.setLastModifyTime(new Date());
            this.replenishmentMsgManager.insert(replenishmentMsg);
            return;
        }
        if (replenishmentMsg.getUpperLimitQty() == null || replenishmentMsg.getUpperLimitQty().longValue() != replenishmentQty) {
            replenishmentMsg.setUpperLimitQty(replenishmentQty);
            this.replenishmentMsgManager.updateByVersion(replenishmentMsg);
        }
    }

    @Override
    public void locationReplenishmentTask(List<ReplenishmentMsg> msgList, Warehouse wh) {
        Long ouId = wh.getId();
        // 取得【商品】集合
        // 取得【商品-库位列表】
        Map<Long,List<Long>> skuLocationMap=new HashMap<Long,List<Long>>();
        Set<Long> skuIdSet = new HashSet<Long>();
        Map<Long, ReplenishmentMsg> locationMsgMap = new HashMap<Long, ReplenishmentMsg>();// [库位-补货信息]
        for (ReplenishmentMsg msg : msgList) {
            skuIdSet.add(msg.getSkuId());
            locationMsgMap.put(msg.getLocationId(), msg);
           if(skuLocationMap.containsKey(msg.getSkuId())){
               skuLocationMap.get(msg.getSkuId()).add(msg.getLocationId());
           }else{
               List<Long> list=new ArrayList<Long>();
               list.add(msg.getLocationId());
               skuLocationMap.put(msg.getSkuId(),list);
           }
        }

        /**
         * 查找SKUID对应的补货规则
         */
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setOuid(ouId);
        ruleAffer.setReplenishmentRuleSkuIdList(new ArrayList<Long>(skuIdSet));
        ruleAffer.setRuleType(Constants.RULE_TYPE_REPLENISHMENT_SKU);
        ruleAffer.setReplenishmentRuleRealTimeReplenish(true);

        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        if (export == null) {
            return;
        }
        // 取得【商品-规则列表】
        Map<Long, List<ReplenishmentRuleCommand>> skuRuleMap = export.getReplenishmentRuleSkuMatchListMap();
        if (skuRuleMap == null) {
            return;
        }
        
        Map<Long, ReplenishmentRuleCommand> locationRuleMap = new HashMap<Long, ReplenishmentRuleCommand>();
        Iterator<Entry<Long, List<ReplenishmentRuleCommand>>> skuRuleMapIt = skuRuleMap.entrySet().iterator();
        while (skuRuleMapIt.hasNext()) {
            Entry<Long, List<ReplenishmentRuleCommand>> entry = skuRuleMapIt.next();
            Long key = entry.getKey();
            List<ReplenishmentRuleCommand> values = entry.getValue();
            if (values == null || values.size() == 0) {
                continue;
            }
            List<Long> skuLocationList = skuLocationMap.get(key);
            for (ReplenishmentRuleCommand c : values) {

                RuleAfferCommand locationRuleAffer = new RuleAfferCommand();
                locationRuleAffer.setOuid(ouId);
                locationRuleAffer.setReplenishmentRuleCommand(c);
                locationRuleAffer.setReplenishmentRuleLocationIdList(skuLocationList);
                locationRuleAffer.setRuleType(Constants.RULE_TYPE_REPLENISHMENT_LOCATION);

                RuleExportCommand locationExport = ruleManager.ruleExport(locationRuleAffer);
                List<Long> matchLocationIdList = locationExport.getReplenishmentRuleLocationMatchList();
                if (matchLocationIdList == null || matchLocationIdList.size() == 0) {
                    continue;
                }
                for (Long matchLocationId : matchLocationIdList) {
                    locationRuleMap.put(matchLocationId, c);
                    // 移除
                    skuLocationList.remove(matchLocationId);
                }
            }
        }


        // 取得了【库位-规则】集合


        Iterator<Entry<Long, ReplenishmentMsg>> locationMsgIt = locationMsgMap.entrySet().iterator();
        while (locationMsgIt.hasNext()) {
            Entry<Long, ReplenishmentMsg> entry = locationMsgIt.next();
            Long locationId = entry.getKey();
            if (!locationRuleMap.containsKey(locationId)) {
                continue;
            }
            ReplenishmentMsg msg = entry.getValue();
            ReplenishmentRuleCommand rule = locationRuleMap.get(locationId);

            /**
             * 对库位进行补货
             */
            this.whskuInventoryManager.replenishmentToLocation(msg, rule, wh);


        }


    }

    @Override
    public List<ReplenishmentMsg> findReplenishmentMsgListByOuId(Long ouId) {
        return this.replenishmentMsgManager.findMsgByOuId(ouId);
    }

    @Override
    public boolean insertLocationReplenishmentErrorMsg(Map<String, List<ReplenishmentMsg>> map, String logId) {
        if (map == null || map.size() <= 0) {
            return false;
        }
        Iterator<Entry<String, List<ReplenishmentMsg>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<ReplenishmentMsg>> entry = it.next();
            String errorCode = entry.getKey();
            List<ReplenishmentMsg> msgList = entry.getValue();
            for (ReplenishmentMsg msg : msgList) {
                msg.setErrorCode(errorCode);
                try {

                    this.replenishmentMsgManager.updateByVersion(msg);
                } catch (Exception e) {
                    log.error(e + "");
                    return false;
                }
            }
        }

        return true;
    }



}
