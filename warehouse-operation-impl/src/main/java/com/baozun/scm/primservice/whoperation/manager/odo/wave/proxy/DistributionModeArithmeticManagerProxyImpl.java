package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

@Service("distributionModeArithmeticManagerProxy")
public class DistributionModeArithmeticManagerProxyImpl extends BaseManagerImpl implements DistributionModeArithmeticManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(DistributionModeArithmeticManagerProxy.class);

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WarehouseManager warehouseManager;
    /**
     * 设置计数器编码
     * 
     * @param ouId
     * @param skuNumberOfPackages
     * @param qty
     * @param skuIdSet
     * @return
     */
    public String getCounterCodeForOdo(Long ouId, Integer skuNumberOfPackages, Double qty, Set<Long> skuIdSet) {
        if (ouId == null || skuNumberOfPackages == null || qty == null || skuIdSet == null || skuIdSet.size() == 0) {
            return "";
        }
        TreeSet<Long> skuIdSortSet = sortSet(skuIdSet);
        String counterCode = ouId + CacheKeyConstant.WAVE_ODO_SPLIT + skuNumberOfPackages + CacheKeyConstant.WAVE_ODO_SPLIT + qty.intValue() + CacheKeyConstant.WAVE_ODO_SPLIT + CacheKeyConstant.WAVE_ODO_SKU_SPLIT;
        Iterator<Long> it = skuIdSortSet.iterator();
        while (it.hasNext()) {
            counterCode += it.next() + CacheKeyConstant.WAVE_ODO_SKU_SPLIT;
        }
        return counterCode;
    }

    /**
     * 对Set元素进行排序
     * 
     * @param skuIdSet
     */
    private TreeSet<Long> sortSet(Set<Long> skuIdSet) {
        TreeSet<Long> result = new TreeSet<Long>();
        for (Long skuId : skuIdSet) {
            result.add(skuId);
        }
        return result;
    }

    /**
     * 添加到匹配配货模式的计算池中
     */
    public void addToWhDistributionModeArithmeticPool(String code, Long odoId) {
        if (StringUtils.isEmpty(code)) {
            throw new BusinessException("计数器编码为空");
        }
        if (!testCounterCount(code)) {
            throw new BusinessException("计数器编码格式异常[counterCode:" + code + ",odoId:" + odoId + "]");
        }
        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        Integer skuType = Integer.parseInt(codeArray[1]);
        Integer skuQty = Integer.parseInt(codeArray[2]);
        // 种类数和商品数一致时候，①添加到缓存池②进行匹配模式计算
        if (skuType.intValue() == skuQty.intValue()) {
            // this.cacheManager.incr(code);
            // 配货模式匹配
            DistributionModeArithmetic(code, odoId);
        }

    }

    private void DistributionModeArithmetic(String code, Long odoId) {
        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        Long ouId = Long.parseLong(codeArray[0]);
        // 仓库缓存
        Warehouse wh = this.warehouseManager.findWarehouseByIdExt(ouId);
        Integer skuType = Integer.parseInt(codeArray[1]);
        switch(skuType.intValue()){
            case 1:
                calcSeckill(code, wh, odoId);
                break;
            case 2:
                calcTwoSkuSuit(code, wh, odoId);
                break;
            default :
                calcSuits(code, wh, odoId);
                break;
        }

    }

    /**
     * 秒杀计算
     * 
     * @param code
     * @param seckillOdoQtys
     * @param odoId
     */
    private void calcSeckill(String code, Warehouse wh, Long odoId) {
        // 仓库秒杀配货模式计算的逻辑：
        // 秒杀计数器+1
        // 如果达到阙值，则进行以下判断
        // 如果第一次达到，则将所有同样CODE【计数器编码】的放入到秒杀出库单中
        // 如果超出则只需要将本出库单加入到秒杀出库单中
        // 同时移除出库单池中的缓存
        if (!wh.getIsCalcSeckill()) {
            return;
        }
        int seckillOdoQtys = wh.getSeckillOdoQtys();
        long seckill = this.cacheManager.incr(CacheKeyConstant.SECKILL_PREFIX + code);
        if (seckill == seckillOdoQtys) {
            List<String> keyList = this.cacheManager.Keys(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + "*");
            for (String key : keyList) {
                String[] keyArray = key.split("%");
                String keyCodeId = keyArray[2];
                String[] keyCodeArray = keyCodeId.split("\\|");
                this.cacheManager.setValue(CacheKeyConstant.SECKILL_ODO_PREFIX + keyCodeId, keyCodeArray[4]);
                this.cacheManager.remove(CacheKeyConstant.OU_ODO_PREFIX + keyCodeId);
            }
            this.cacheManager.setValue(CacheKeyConstant.SECKILL_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");

        } else if (seckill > seckillOdoQtys) {
            this.cacheManager.setValue(CacheKeyConstant.SECKILL_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        } else {
            this.cacheManager.setValue(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        }


    }

    /**
     * 主副品计算
     * 
     * @param code
     * @param twoSkuSuitOdoQtys
     * @param suitsOdoQtys
     * @param odoId
     */
    private void calcTwoSkuSuit(String code, Warehouse wh, Long odoId) {
        if (!wh.getIsCalcTwoSkuSuit()) {
            calcSuits(code, wh, odoId);
            return;
        }
        int twoSkuSuitOdoQtys = wh.getTwoSkuSuitOdoQtys();

        // 仓库主副品配货模式计算的逻辑
        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        String twoSkuSuitPrefix=codeArray[0]+CacheKeyConstant.WAVE_ODO_SPLIT+codeArray[1]+CacheKeyConstant.WAVE_ODO_SPLIT+codeArray[2];
        String[] skuIdArray = codeArray[3].substring(1, codeArray[3].length() - 1).split("\\" + CacheKeyConstant.WAVE_ODO_SKU_SPLIT);

        String skuIdA = skuIdArray[0];
        String skuIdB = skuIdArray[1];
        // 计算主副品
        long twoSkuSuitA = this.cacheManager.incr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA);
        long twoSkuSuitB = this.cacheManager.incr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB);
        long suits = this.cacheManager.incr(CacheKeyConstant.SUITS_PREFIX + code);

        if (twoSkuSuitA >= twoSkuSuitOdoQtys) {
            cacheTowSKuSuitOdo(code, odoId, twoSkuSuitPrefix, skuIdA, skuIdB);

        } else if (twoSkuSuitB >= twoSkuSuitOdoQtys) {
            cacheTowSKuSuitOdo(code, odoId, twoSkuSuitPrefix, skuIdB, skuIdA);
        } else {
            if (!wh.getIsCalcSuits()) {
                this.cacheManager.setValue(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
                return;
            }
            int suitsOdoQtys = wh.getSuitsOdoQtys();
            if (suits < suitsOdoQtys) {
                this.cacheManager.setValue(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
                return;
            }
            this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA);
            this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB);
            this.cacheManager.setValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
            this.cacheManager.setValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
            if (suits == suitsOdoQtys) {
                List<String> keyList = this.cacheManager.Keys(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + "*");
                for (String key : keyList) {
                    String[] keyArray = key.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
                    String idStr = this.cacheManager.getValue(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + keyArray[4]);
                    this.cacheManager.setValue(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + idStr, idStr);
                    // 移除
                    this.cacheManager.remove(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + idStr);
                }
                this.cacheManager.setValue(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
            } else if (suits > suitsOdoQtys) {
                this.cacheManager.setValue(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
            } else {
                this.cacheManager.setValue(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
            }
        }
    }

    /**
     * 套装计算
     * 
     * @param code
     * @param suitsOdoQtys
     * @param odoId
     */
    private void calcSuits(String code, Warehouse wh, Long odoId) {
        if (!wh.getIsCalcSuits()) {
            return;
        }

        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        Integer skuType = Integer.parseInt(codeArray[1]);
        if (skuType < 2 || skuType > wh.getSuitsMaxSkuCategorys()) {
            return;
        }

        long suitsOdoQtys = wh.getSuitsOdoQtys();
        // 计算套装
        long suits = this.cacheManager.incr(CacheKeyConstant.SUITS_PREFIX + code);
        if (suits == suitsOdoQtys) {
            List<String> keyList = this.cacheManager.Keys(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + "*");
            for (String key : keyList) {
                String[] keyArray = key.split("%");
                String keyCodeId = keyArray[2];
                String[] keyCodeIdArray = keyCodeId.split("\\|");
                this.cacheManager.setValue(CacheKeyConstant.SUITS_ODO_PREFIX + keyCodeId, keyCodeIdArray[4]);
                // 移除
                this.cacheManager.remove(CacheKeyConstant.OU_ODO_PREFIX + keyCodeId);
            }
            this.cacheManager.setValue(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        } else if (suits > suitsOdoQtys) {
            this.cacheManager.setValue(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        } else {
            this.cacheManager.setValue(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        }
    }

    /**
     * 主副品计算逻辑分支
     * 
     * @param code
     * @param odoId
     * @param twoSkuSuitPrefix
     * @param incrSkuId
     * @param decrSkuId
     */
    private void cacheTowSKuSuitOdo(String code, Long odoId, String twoSkuSuitPrefix, String incrSkuId, String decrSkuId) {
        this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + decrSkuId);
        this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);
        this.cacheManager.setValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + decrSkuId + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        this.cacheManager.setValue(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + decrSkuId + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");
        List<String> keyList = this.cacheManager.Keys(CacheKeyConstant.OU_ODO_PREFIX + twoSkuSuitPrefix + "*$" + incrSkuId + "$*");

        for (String key : keyList) {

            String[] keyArray = key.split("%");
            String keyCodeId = keyArray[2];
            String[] keyCodeOdoIdArray = keyCodeId.split("\\|");
            String keyOdoId = keyCodeOdoIdArray[4];
            this.cacheManager.setValue(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + incrSkuId + CacheKeyConstant.WAVE_ODO_SPLIT + keyOdoId, keyOdoId);
             //扣减集合
            String keyDecrSkuId = keyCodeOdoIdArray[3].replace("$" + incrSkuId + "$", "").replace("$", "");
            this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + keyDecrSkuId);
            this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);

            this.cacheManager.setValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix+CacheKeyConstant.WAVE_ODO_SPLIT +keyDecrSkuId+CacheKeyConstant.WAVE_ODO_SPLIT+keyOdoId,keyOdoId);
            this.cacheManager.setValue(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + keyDecrSkuId + CacheKeyConstant.WAVE_ODO_SPLIT + keyOdoId, keyOdoId);

            this.cacheManager.remove(CacheKeyConstant.OU_ODO_PREFIX + keyCodeId);


        }
        this.cacheManager.setValue(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + incrSkuId + CacheKeyConstant.WAVE_ODO_SPLIT + odoId, odoId + "");

    }

    @Override
    public void divFromOrderPool(String code, Long odoId) {
        if (!testCounterCount(code)) {
            throw new BusinessException("计数器编码格式异常[counterCode:" + code + ",odoId:" + odoId + "]");
        }
        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        Integer skuType = Integer.parseInt(codeArray[1]);
        Integer skuQty = Integer.parseInt(codeArray[2]);
        // 种类数和商品数一致时候，①计数器-
        if (skuType.intValue() == skuQty.intValue()) {
            boolean isExists = this.isExistsInOrderPool(code, odoId);
            if (isExists) {
                this.cacheManager.remove(CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
            }
            switch (skuType.intValue()) {
                case 1:

                    this.cacheManager.decr(CacheKeyConstant.SECKILL_PREFIX + code);
                    if (!isExists) {
                        this.cacheManager.remove(CacheKeyConstant.SECKILL_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    }
                    break;
                case 2:
                    String twoSkuSuitPrefix = codeArray[0] + CacheKeyConstant.WAVE_ODO_SPLIT + codeArray[1] + CacheKeyConstant.WAVE_ODO_SPLIT + codeArray[2];
                    String[] skuIdArray = codeArray[3].substring(1, codeArray[3].length() - 1).split("\\" + CacheKeyConstant.WAVE_ODO_SKU_SPLIT);

                    String skuIdA = skuIdArray[0];
                    String skuIdB = skuIdArray[1];

                    if (isExists) {
                        this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA);
                        this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB);
                        this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);
                    } else {
                        String result1 = this.cacheManager.getValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                        if (StringUtils.isEmpty(result1)) {
                            this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA);
                            this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                            // 还原扣减集合
                            this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                            this.cacheManager.remove(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + code);
                        } else {

                            String result2 = this.cacheManager.getValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                            if (StringUtils.isEmpty(result2)) {
                                this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB);
                                this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                                // 还原扣减集合
                                this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                                this.cacheManager.remove(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + code);
                            } else {

                                String result3 = this.cacheManager.getValue(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + code);
                                if (StringUtils.isEmpty(result3)) {
                                    this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);
                                    this.cacheManager.remove(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                                    // 还原扣减集合
                                    this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                                    this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                                }
                            }
                        }
                    }


                    break;
                default:
                    this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);
                    this.cacheManager.remove(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    break;
            }
        }
    }

    @Override
    public void AddToWave(Map<Long, String> codeOdoMap) {
        Iterator<Entry<Long, String>> it = codeOdoMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, String> entry = it.next();
            AddToWave(entry.getValue(), entry.getKey());
        }

    }

    @Override
    public void AddToWave(String code, Long odoId) {
        if (!testCounterCount(code)) {
            throw new BusinessException("计数器编码格式异常[counterCode:" + code + ",odoId:" + odoId + "]");
        }
        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        Integer skuType = Integer.parseInt(codeArray[1]);
        Integer skuQty = Integer.parseInt(codeArray[2]);
        // 种类数和商品数一致时候，①计数器-
        if (skuType.intValue() == skuQty.intValue()) {
            switch (skuType.intValue()) {
                case 1:
                    this.cacheManager.decr(CacheKeyConstant.SECKILL_PREFIX + code);
                    break;
                case 2:
                    String twoSkuSuitPrefix = codeArray[0] + CacheKeyConstant.WAVE_ODO_SPLIT + codeArray[1] + CacheKeyConstant.WAVE_ODO_SPLIT + codeArray[2];
                    String[] skuIdArray = codeArray[3].substring(1, codeArray[3].length() - 1).split("\\" + CacheKeyConstant.WAVE_ODO_SKU_SPLIT);

                    String skuIdA = skuIdArray[0];
                    String skuIdB = skuIdArray[1];

                    String result1=this.cacheManager.getValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    String result2 = this.cacheManager.getValue(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    String result3 = this.cacheManager.getValue(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + code+ CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    boolean flag = false;
                    if (StringUtils.isEmpty(result2) && StringUtils.isEmpty(result2) && StringUtils.isEmpty(result3)) {
                        flag = true;
                    }
                    if(StringUtils.isEmpty(result1)){
                        this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX+  twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA);
                        if (!flag) {
                            this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                        }
                    } else {
                        this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdA + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    }
                    if (StringUtils.isEmpty(result2)) {
                        this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB);
                        if (!flag) {
                            this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                        }
                    } else {
                        this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuIdB + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    }
                    if (StringUtils.isEmpty(result3)) {
                        this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);
                        if (!flag) {
                            this.cacheManager.remove(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                        }
                    }else{
                        this.cacheManager.remove(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    }
                    break;
                default:
                    this.cacheManager.decr(CacheKeyConstant.SUITS_PREFIX + code);
                    this.cacheManager.remove(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    this.cacheManager.remove(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
                    break;
            }
        }
    }

    @Override
    public boolean isExistsInOrderPool(String code, Long odoId) {
        String key = CacheKeyConstant.OU_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId;
        String value = this.cacheManager.getValue(key);
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return true;
    }

    @Override
    public void removeFromOrderPool(String code, Long odoId) {

    }

    @Override
    public void changeFromOrderPool(String oldCode, String newCode, Long odoId) {

    }

    @Override
    public void mergeOdo(String newCode, Long odoId, Map<Long, String> mergedOdoMap) {
        if (mergedOdoMap != null && mergedOdoMap.size() > 0) {
            Iterator<Entry<Long, String>> it = mergedOdoMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Long, String> entry = it.next();
                this.divFromOrderPool(entry.getValue(), entry.getKey());
            }
        }
        this.addToWhDistributionModeArithmeticPool(newCode, odoId);
    }

    private boolean testCounterCount(String counterCode) {
        try {
            String[] codeArray = counterCode.split("\\|");
            Integer skuType = Integer.parseInt(codeArray[1]);
            Integer skuQty = Integer.parseInt(codeArray[2]);
            String[] skuIdArray = codeArray[3].substring(1, codeArray[3].length() - 1).split("\\" + CacheKeyConstant.WAVE_ODO_SKU_SPLIT);
            if (skuIdArray == null || skuIdArray.length - skuType != 0) {
                return false;
            }
        } catch (Exception e) {
            log.error(e + "");
            return false;
        }
        return true;
    }

    @Override
    public void CancelFormergeOdo(String mergedCounterCode, Long odoId, Map<Long, String> reNewOdoMap) {
        this.divFromOrderPool(mergedCounterCode, odoId);
        if (reNewOdoMap != null) {
            Iterator<Entry<Long, String>> it = reNewOdoMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Long, String> entry = it.next();
                this.addToWhDistributionModeArithmeticPool(entry.getValue(), entry.getKey());
            }
        }
    }

    @Override
    public void addToPool(Map<Long, String> odoIdCounterCodeMap) {
        Iterator<Entry<Long, String>> it = odoIdCounterCodeMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, String> entry = it.next();
            this.addToWhDistributionModeArithmeticPool(entry.getValue(), entry.getKey());
        }
    }


}
