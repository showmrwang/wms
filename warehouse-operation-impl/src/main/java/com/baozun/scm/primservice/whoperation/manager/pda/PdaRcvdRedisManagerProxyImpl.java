package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

@Service("pdaRcvdRedisManagerProxy")
public class PdaRcvdRedisManagerProxyImpl implements PdaRcvdRedisManagerProxy {

    @Autowired
    private CacheManager cacheManager;
    @Override
    public void cacheAsnLine(Long occupationId, WhAsnLine asnline) {
        cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, asnline.getId().toString(), asnline, 24 * 60 * 60);
    }

    @Override
    public void cacheAsnLineOverchargeCount(Long occupationId, String asnlineId, int overchargeCount) {
        cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, asnlineId, overchargeCount, 24 * 60 * 60);

    }

    @Override
    public void cacheLineSkuCount(Long occupationId, Long asnLineId, Long skuId, int count) {
        long asnLineSku = cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + asnLineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId);
        cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + asnLineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, (int) asnLineSku);
        cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + asnLineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, count);

    }

    @Override
    public void cacheAsnLineSn(Long asnLineId, String[] snArray) {
        this.cacheManager.addSet(CacheKeyConstant.CACHE_ASNLINE_SN + asnLineId, snArray);
    }

    @Override
    public void cacheAsnSkuCount(Long asnId, Map<Long, Integer> skuMap) {
        Iterator<Entry<Long, Integer>> it = skuMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, Integer> skuEntry = it.next();
            long asnSku = cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asnId + CacheKeyConstant.CACHE_KEY_SPLIT + skuEntry.getKey());
            cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asnId + CacheKeyConstant.CACHE_KEY_SPLIT + skuEntry.getKey(), (int) asnSku);
            cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + asnId + CacheKeyConstant.CACHE_KEY_SPLIT + skuEntry.getKey(), skuEntry.getValue());

        }

    }

    @Override
    public void cacheAsn(WhAsn cacheAsn) {
        cacheManager.setObject(CacheKeyConstant.CACHE_ASN_PREFIX + cacheAsn.getId(), cacheAsn, 24 * 60 * 60);

    }

    @Override
    public void cachePo(WhPo po) {
        cacheManager.setObject(CacheKeyConstant.CACHE_PO_PREFIX + po.getId(), po, 24 * 60 * 60);

    }

    @Override
    public void freeAsn(Long occupationId) {
        cacheManager.remove(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);

    }

    @Override
    public List<RcvdCacheCommand> findAllRcvd(Long userId) {
        return this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);
    }

    @Override
    public void freeContainer(Long insideContainerId) {
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + insideContainerId);
    }

    @Override
    public void freeContainerSku(Long insideContainerId) {
        this.cacheManager.remonKeys(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + insideContainerId + "$*");

    }

    @Override
    public void freeRcvdSn(Long userId) {
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
    }

    @Override
    public void freeRcvd(Long userId) {
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PREFIX + userId);

    }

    @Override
    public String findLineSkuCount(Long occupationId, Long asnLineId, Long skuId) {
        String lineCount = this.cacheManager.getValue(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + asnLineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId);
        return lineCount;
    }

    @Override
    public long decrLineSkuCount(Long occupationId, Long lineId, Long skuId, Integer divCount) {
        if (divCount == null) {
            divCount = 1;
        }
        return cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
    }

    @Override
    public Integer findLineOverchargeCount(Long occupationId, String asnlineId) {
        return cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, asnlineId);
    }

    @Override
    public long incrLineSkuCount(Long occupationId, Long lineId, Long skuId, Integer divCount) {
        if (divCount == null) {
            divCount = 1;
        }
        return cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + lineId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
    }

    @Override
    public long decrSkuCount(Long occupationId, Long skuId, Integer divCount) {
        if (divCount == null) {
            divCount = 1;
        }
        return cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
    }

    @Override
    public long incrSkuCount(Long occupationId, Long skuId, Integer divCount) {
        if (divCount == null) {
            divCount = 1;
        }
        return this.cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId, divCount);
    }

    @Override
    public void rcvd(Long userId, RcvdCacheCommand rcvdCommand) {
        List<RcvdCacheCommand> list = this.findAllRcvd(userId);
        if (null == list) {
            list = new ArrayList<RcvdCacheCommand>();
        }
        list.add(rcvdCommand);
        cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId, list, 60 * 60);

    }

    @Override
    public List<RcvdSnCacheCommand> findRcvdSn(Long userId) {
        return this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId);
    }

    @Override
    public WhAsn findAsn(Long asnId) {
        return this.cacheManager.getObject(CacheKeyConstant.CACHE_ASN_PREFIX + asnId);
    }

    @Override
    public String findSkuCount(Long occupationId, Long skuId) {
        return cacheManager.getValue(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + occupationId + CacheKeyConstant.CACHE_KEY_SPLIT + skuId);
    }

    @Override
    public Set<String> findAllLineId(Long occupationId) {
        Map<String, String> lineIdSet = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId);
        if (lineIdSet == null || lineIdSet.size() == 0) {
            return null;
        }
        return lineIdSet.keySet();
    }

    @Override
    public WhAsnLine findLine(Long occupationId, String asnlineId) {
        return this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, asnlineId);
    }

    @Override
    public RcvdContainerCacheCommand findContainerSku(Long insideContainerId, Long skuId) {
        return this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + insideContainerId + "$" + skuId);
    }

    @Override
    public long countLineSn(String lineId) {
        return this.cacheManager.findSetCount(CacheKeyConstant.CACHE_ASNLINE_SN + lineId);
    }

    @Override
    public boolean eistsLineSn(String lineId, String sn) {
        return this.cacheManager.existsInSet(CacheKeyConstant.CACHE_ASNLINE_SN + lineId, sn);
    }

    @Override
    public void cacheRcvdSn(Long userId, List<RcvdSnCacheCommand> cacheSn) {
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_SN_PREFIX + userId, cacheSn);
    }

    @Override
    public void cacheContainerSku(Long insideContainerId, Long skuId, RcvdContainerCacheCommand rcvdContainerCacheCommand) {
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_PREFIX + insideContainerId + "$" + skuId, rcvdContainerCacheCommand);
    }

    @Override
    public void cacheContainer(Long insideContainerId, RcvdContainerCacheCommand cacheContainer) {
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + insideContainerId, cacheContainer);

    }

    @Override
    public void putPalletContainer(Long outerContainerId, Long insideContainerId) {
        this.cacheManager.pushToListHead(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId, insideContainerId.toString());

    }

    @Override
    public RcvdContainerCacheCommand findContainer(Long insideContainerId) {
        return this.cacheManager.getObject(CacheKeyConstant.CACHE_RCVD_CONTAINER_USER_PREFIX + insideContainerId);
    }

    @Override
    public void freeAllPalletContainer(Long outerContainerId) {
        this.cacheManager.remove(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId);
    }

    @Override
    public List<String> findAllPalletContainer(Long outerContainerId) {
        return this.cacheManager.findLists(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId, 0, this.cacheManager.listLen(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outerContainerId));
    }

    @Override
    public boolean existsUser(String userId) {
        return this.cacheManager.existsInSet(CacheKeyConstant.CACHE_OPERATOR_USER, userId);
    }

    @Override
    public void registerUser(String userId) {
        this.cacheManager.addSet(CacheKeyConstant.CACHE_OPERATOR_USER, new String[] {userId});

    }

    @Override
    public void popPalletContainer(Long outside) {
        this.cacheManager.popListHead(CacheKeyConstant.CACHE_RCVD_PALLET_PREFIX + outside);
    }

    @Override
    public void cacheRcvd(Long userId, List<RcvdCacheCommand> list) {
        this.cacheManager.setObject(CacheKeyConstant.CACHE_RCVD_PREFIX + userId, list, CacheKeyConstant.CACHE_ONE_HOUR);
    }

}
