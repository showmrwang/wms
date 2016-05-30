package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd.GeneralRcvdManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;

@Service("pdaRcvdManagerProxy")
public class PdaRcvdManagerProxyImpl extends BaseManagerImpl implements PdaRcvdManagerProxy {
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private AsnLineManager asnLineManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private GeneralRcvdManager generalRcvdManager;
    @Autowired
    private PoManager poManager;
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
        // 6.商品SN号序列缓存：TODO
        WhAsnCommand searchCommand = new WhAsnCommand();
        searchCommand.setId(occupationId);
        searchCommand.setOuId(ouId);
        WhAsn cacheAsn = this.asnManager.findWhAsnByIdToShard(occupationId, ouId);
        if (null == cacheAsn) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        if (PoAsnStatus.ASN_CANCELED == cacheAsn.getStatus()) {// ASN单状态校验：只校验了是未取消的数据
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        WhPoCommand poSearchCommand=new WhPoCommand();
        poSearchCommand.setOuId(ouId);
        poSearchCommand.setId(cacheAsn.getPoId());
        WhPoCommand po=this.poManager.findWhPoByIdToShard(poSearchCommand);
        
        String cacheRate = cacheManager.getMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString());
        if(StringUtils.isEmpty(cacheRate)){
            cacheRate = this.initAsnOverchargeRate(po.getOverChageRate(), cacheAsn.getOverChageRate(), cacheAsn.getStoreId(), cacheAsn.getOuId());
            cacheManager.setMapValue(CacheKeyConstant.CACHE_ASN_OVERCHARGE, occupationId.toString(), cacheRate, 365 * 24 * 60 * 60);
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
                for (WhAsnLine asnline : asnlineList) {// 缓存ASN明细信息
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, asnline.getId().toString(), asnline, 24 * 60 * 60);
                    int count = asnline.getQtyPlanned().intValue() - asnline.getQtyRcvd().intValue();// 未收货数量
                    int overchargeCount = (int) (asnline.getQtyRcvd().intValue() * Double.valueOf(cacheRate) / 100);// 可超收数量
                    cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, asnline.getId().toString(), overchargeCount, 24 * 60 * 60);
                    // 缓存ASN-商品数量
                    long asnLineSku = cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId());
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), (int) asnLineSku);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + cacheAsn.getId() + "_" + asnline.getId() + "_" + asnline.getSkuId(), count);
                    if (skuMap.containsKey(asnline.getSkuId())) {
                        skuMap.put(asnline.getSkuId(), skuMap.get(asnline.getSkuId()) + count);
                    } else {
                        skuMap.put(asnline.getSkuId(), count);
                    }
                }
                // Asn商品缓存列表
                Iterator<Entry<Long, Integer>> it = skuMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<Long, Integer> skuEntry = it.next();
                    long asnSku = cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey());
                    cacheManager.decrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey(), (int) asnSku);
                    cacheManager.incrBy(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + cacheAsn.getId() + "_" + skuEntry.getKey(), skuEntry.getValue());
                }
                // 缓存ASN头信息
                cacheManager.setMapObject(CacheKeyConstant.CACHE_ASN, occupationId.toString(), cacheAsn, 24 * 60 * 60);
                this.asnManager.updateByVersionForUnLock(occupationId, ouId);
            } else {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
        } catch (Exception e) {
            cacheManager.removeMapValue(CacheKeyConstant.CACHE_ASN, occupationId.toString());
            throw e;
        }

    }

    /**
     * 初始化Asn的超收比例
     * 
     * @param overChageRate
     * @param overChageRate2
     * @return
     */
    private String initAsnOverchargeRate(Double overChargeRatePo, Double overChargeRateAsn, Long storeId, Long ouId) {
        if (null == overChargeRatePo) {
            String storePo = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_STORE_PO_OVERCHARGE, storeId.toString());
            if (StringUtils.hasText(storePo)) {
                overChargeRatePo = Double.parseDouble(storePo);
            } else {
                String whPo = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_WAREHOUSE_PO_OVERCHARGE, ouId.toString());
                if (StringUtils.hasText(whPo)) {
                    overChargeRatePo = Double.parseDouble(whPo);
                }
            }
        }
        if (null == overChargeRatePo) {
            overChargeRatePo = Constants.DEFAULT_DOUBLE;
        }
        if (null == overChargeRateAsn) {
            String storeAsn = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_STORE_ASN_OVERCHARGE, storeId.toString());
            if (StringUtils.hasText(storeAsn)) {
                overChargeRateAsn = Double.parseDouble(storeAsn);
            } else {
                String whAsn = this.cacheManager.getMapValue(CacheKeyConstant.CACHE_WAREHOUSE_ASN_OVERCHARGE, ouId.toString());
                if (StringUtils.hasText(whAsn)) {
                    overChargeRateAsn = Double.parseDouble(whAsn);
                }
            }
        }

        if (null == overChargeRateAsn) {
            overChargeRateAsn = Constants.DEFAULT_DOUBLE;
        }
        return String.valueOf(overChargeRateAsn > overChargeRatePo ? overChargeRatePo : overChargeRateAsn);
    }

    @Override
    public void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commandList) {
        this.generalRcvdManager.saveScanedSkuWhenGeneralRcvdForPda(commandList);
    }

    @Override
    public void cacheScanedSkuWhenGeneralRcvd(RcvdCacheCommand rcvdCacheCommand) {
        String userId = rcvdCacheCommand.getCreatedId().toString();
        // String key = UUID.randomUUID().toString();
        try{
            long lessCount = cacheManager.decr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getSkuId());
            Integer overchargeCount = cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + rcvdCacheCommand.getOccupationId(), rcvdCacheCommand.getLineId().toString());
            if (lessCount + overchargeCount < 0 && false) {
                throw new BusinessException("商品已超收，请刷新页面重试");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("缓存数据失败，请重试！");
        }
        try{
            cacheManager.decr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getSkuId());
        } catch (Exception e) {
            cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getSkuId());
            throw new BusinessException("缓存数据失败，请重试！");
        }
        try {
            cacheManager.decr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getLineId() + "_" + rcvdCacheCommand.getSkuId());
        } catch (Exception e) {
            cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getLineId() + "_" + rcvdCacheCommand.getSkuId());
            cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getSkuId());
            throw new BusinessException("缓存数据失败，请重试！");
        }
        try {
            // 缓存
            // cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD_USER_PREFIX + userId, key,
            // rcvdCacheCommand, 300);
            List<RcvdCacheCommand> list = cacheManager.getMapObject(CacheKeyConstant.CACHE_RCVD, userId);
            if (null == list) {
                list = new ArrayList<RcvdCacheCommand>();
            }
            list.add(rcvdCacheCommand);
            cacheManager.setMapObject(CacheKeyConstant.CACHE_RCVD, userId, list, 300);
        } catch (Exception e) {
            cacheManager.incr(CacheKeyConstant.CACHE_ASN_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getSkuId());
            cacheManager.incr(CacheKeyConstant.CACHE_ASNLINE_SKU_PREFIX + rcvdCacheCommand.getOccupationId() + "_" + rcvdCacheCommand.getLineId() + "_" + rcvdCacheCommand.getSkuId());
            throw new BusinessException("缓存数据失败，请重试！");
        }
    }

    /**
     * 刷新缓存操作
     */
    @Override
    public void freshAsnCacheForGeneralReceiving(Long occupationId, Double newChargeRate, Double oldChargeRate) {
        try {
            Map<String, String> asnlineMap = this.cacheManager.getAllMap(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId);
            if (null == asnlineMap) {
                throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
            }
            Iterator<Entry<String, String>> it = asnlineMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                WhAsnLine line = this.cacheManager.getMapObject(CacheKeyConstant.CACHE_ASNLINE_PREFIX + occupationId, entry.getKey());
                Integer overchargeCount = (int) (line.getQtyPlanned() * newChargeRate);
                this.cacheManager.setMapObject(CacheKeyConstant.CACHE_ASNLINE_OVERCHARGE_PREFIX + occupationId, entry.getKey(), overchargeCount, 24 * 60 * 60);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.ASN_CACHE_ERROR);
        }
    }

}
