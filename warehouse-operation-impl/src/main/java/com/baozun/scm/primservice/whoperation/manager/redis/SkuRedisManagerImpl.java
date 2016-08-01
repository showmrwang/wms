package com.baozun.scm.primservice.whoperation.manager.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("skuRedisManager")
@Transactional
public class SkuRedisManagerImpl extends BaseManagerImpl implements SkuRedisManager {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private WhSkuDao whSkuDao;

    public static final Logger log = LoggerFactory.getLogger(SkuRedisManagerImpl.class);

    /**
     * 通过SKU条码查询对应所有的SKU信息 如果redis缓存内存在对应信息就返回 如果不存在查询数据库 然后放入redis缓存
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Map<Long, Integer> findSkuByBarCode(String barCode, String logId) {
        log.info(this.getClass().getSimpleName() + ".findSkuByBarCode method begin! logid: " + logId);
        Map<Long, Integer> returnMap = new HashMap<Long, Integer>();
        Map<String, String> barCodeMap = new HashMap<String, String>();
        String redisBarCodeKey = CacheKeyConstant.WMS_CACHE_SKU_BARCODE + barCode;
        try {
            // 先查询redis缓存对应条码是否有信息
            barCodeMap = cacheManager.getAllMap(redisBarCodeKey);
        } catch (Exception e) {
            // redis出错只记录log
            log.error("findSkuByBarCode cacheManager.getAllMap() error logid: " + logId);
        }
        if (barCodeMap.size() == 0) {
            // 没有对应条码信息 查询对应数据
            List<String> skuQty = skuDao.getSkuIdAndBarCodeQtyByBarCode(barCode);
            if (skuQty.size() == 0) {
                // 条码对应商品不存在
                log.warn("findSkuByBarCode skuBarCode is null error logid: " + logId);
                throw new BusinessException(ErrorCodes.BARCODE_NOT_FOUND_SKU, new Object[] {barCode});
            }
            for (String s : skuQty) {
                // 存在对应数据 放入redis缓存 key:格式前缀+barcode field:skuid value:qty
                try {
                    cacheManager.setMapValue(redisBarCodeKey, s.split("-")[0], s.split("-")[1], CacheKeyConstant.CACHE_ONE_DAY);
                } catch (Exception e) {
                    // redis出错只记录log
                    log.error("findSkuByBarCode cacheManager.setMapValue() error logid: " + logId);
                }
                // 放入returnMap
                returnMap.put(Long.parseLong(s.split("-")[0]), Integer.parseInt(s.split("-")[1]));
            }
        } else {
            // 有对应信息 封装SKUID返回
            for (String skuid : barCodeMap.keySet()) {
                // 获取商品默认数量
                String qty = cacheManager.getMapValue(redisBarCodeKey, skuid);
                // 放入returnMap
                returnMap.put(Long.parseLong(skuid), Integer.parseInt(qty));
            }
        }
        log.info(this.getClass().getSimpleName() + ".findSkuByBarCode method end! logid: " + logId);
        return returnMap;
    }


    /**
     * 通过SKUID查询对应SKU主档信息 如果redis有直接返回 如果没有查询数据库后放入redis缓存
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public SkuRedisCommand findSkuMasterBySkuId(Long skuid, Long ouid, String logId) {
        log.info(this.getClass().getSimpleName() + ".findSkuMasterBySkuId method begin! logid: " + logId);
        SkuRedisCommand skuRedis = null;
        String redisSkuKey = CacheKeyConstant.WMS_CACHE_SKU_MASTER + skuid;
        try {
            // 获取对应redis数据
            skuRedis = cacheManager.getObject(redisSkuKey);
        } catch (Exception e) {
            // redis出错只记录log
            log.error("findSkuMasterBySkuId cacheManager.getObject() error logid: " + logId);
        }
        if (null == skuRedis) {
            // redis没有对应数据 查询数据库
            skuRedis = whSkuDao.findSkuAllInfoByParamExt(skuid, ouid);
            if (null == skuRedis) {
                // sku不存在 抛出异常
                log.warn("findSkuMasterBySkuId sku is null error logid: " + logId);
                throw new BusinessException(ErrorCodes.SKU_IS_NULL_BY_ID, new Object[] {skuid});
            }
            // 放入redis缓存
            try {
                cacheManager.setObject(redisSkuKey, skuRedis, CacheKeyConstant.CACHE_ONE_DAY);
            } catch (Exception e) {
                // redis出错只记录log
                log.error("findSkuMasterBySkuId cacheManager.setObject() error logid: " + logId);
            }
        }
        log.info(this.getClass().getSimpleName() + ".findSkuMasterBySkuId method end! logid: " + logId);
        return skuRedis;
    }
}
