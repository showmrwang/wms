package com.baozun.scm.primservice.whoperation.manager.redis;

import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface SkuRedisManager extends BaseManager {

    Map<Long, Integer> findSkuByBarCode(String barCode, String logId);

    SkuRedisCommand findSkuMasterBySkuId(Long skuid, Long ouid, String logId);

    void delSkuRedis(Long skuid, Long ouid);

    void delSkuBarCodeRedis(String barCode);
}
