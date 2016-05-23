package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;

public class PdaRcvdManagerProxyImpl implements PdaRcvdManagerProxy {
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private AsnLineManager asnLineManager;
    @Autowired
    private CacheManager cacheManager;
    /**
     * 扫描ASN时 初始化缓存
     * 
     * @author yimin.lu
     */
    /**
     * 初始化缓存
     * 
     * @param occupationId
     * @param ouId
     */
    public void initAsnCacheForGeneralReceiving(Long occupationId, Long ouId) {
        // 缓存：
        // 1.ASN头信息缓存: asn的ID为key，asn对象为value
        // 2.ASN明细的缓存:asn的ID，明细的ID为key,asnline对象为value
        // 3.ASN明细-商品-数量缓存： asn的ID，明细的ID，商品的ID 为key,可用数量为Value
        // 4.商品-数量缓存：asn的ID，商品的ID 为key，此商品对应明细所有的可用数量为value
        // 5.商品-明细缓存 ：asn的ID，商品的ID为key，此商品对应明细的ID（toString）的SET集合为value
        WhAsnCommand searchCommand = new WhAsnCommand();
        searchCommand.setId(occupationId);
        searchCommand.setOuId(ouId);
        WhAsn cacheAsn = this.asnManager.findWhAsnByIdToShard(occupationId, ouId);
        if (null == cacheAsn) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        try {
            int updateCount = this.asnManager.updateByVersionForLock(cacheAsn.getId(), ouId, cacheAsn.getLastModifyTime());
            if (1 == updateCount) {
                WhAsnLineCommand command = new WhAsnLineCommand();
                command.setAsnId(cacheAsn.getId());
                command.setOuId(ouId);
                WhAsnLine asnLine = new WhAsnLine();
                BeanUtils.copyProperties(command, asnLine);
                List<WhAsnLine> asnlineList = this.asnLineManager.findListByShard(asnLine);
                if (null == asnlineList || asnlineList.size() == 0) {
                    throw new BusinessException(ErrorCodes.ASN_NULL);
                }
                // 缓存明细的可用数量
                Map<Long, Integer> skuMap = new HashMap<Long, Integer>();
                Map<Long, Integer> skuQtyMap = new HashMap<Long, Integer>();
                for (WhAsnLine asnline : asnlineList) {
                    if (PoAsnStatus.ASNLINE_NOT_RCVD == asnline.getStatus() || PoAsnStatus.ASNLINE_RCVD == asnline.getStatus()) {
                        int count = asnline.getQtyPlanned().intValue() - asnline.getQtyRcvd().intValue();// 未收货数量
                        // 缓存ASN明细信息
                        cacheManager.setObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + cacheAsn.getId() + "_" + asnline.getId(), asnline);
                        // 缓存ASN-商品数量
                        cacheManager.setObject(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), count);
                        if (skuMap.containsKey(asnline.getSkuId())) {
                            skuMap.put(asnline.getSkuId(), skuMap.get(asnline.getSkuId()) + count);
                        } else {
                            skuMap.put(asnline.getSkuId(), count);
                        }
                        if (skuQtyMap.containsKey(asnline.getSkuId())) {
                            skuQtyMap.put(asnline.getSkuId(), skuQtyMap.get(asnline.getSkuId()) + asnline.getQtyPlanned().intValue());
                        } else {
                            skuQtyMap.put(asnline.getSkuId(), asnline.getQtyPlanned().intValue());
                        }
                        // 缓存商品-明细对应缓存
                        cacheManager.addSet(CacheKeyConstant.CACHE_ASN_SKU_ASN_LINE_PREFIX + cacheAsn.getId() + "_" + asnline.getSkuId(), new String[] {asnline.getId().toString()});
                    }
                }
                // Asn商品缓存列表
                Iterator<Entry<Long, Integer>> it = skuMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<Long, Integer> skuEntry = it.next();
                    cacheManager.setObject(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey(), skuEntry.getValue());
                }
                Iterator<Entry<Long, Integer>> itQty = skuQtyMap.entrySet().iterator();
                while (itQty.hasNext()) {
                    Entry<Long, Integer> skuEntry = it.next();
                    cacheManager.setObject(CacheKeyConstant.CACHE_ASN_SKU_QTY_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey(), skuEntry.getValue());
                }
                // 缓存ASN头信息
                cacheManager.setObject(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId, cacheAsn);
                this.asnManager.updateByVersionForUnLock(occupationId, ouId);
            } else {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
        } catch (Exception e) {
            cacheManager.remove(CacheKeyConstant.CACHE_ASN_PREFIX + occupationId);
        }

    }
}
