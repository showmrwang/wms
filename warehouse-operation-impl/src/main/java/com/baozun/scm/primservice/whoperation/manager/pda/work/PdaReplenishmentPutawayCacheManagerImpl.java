package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

@Service("pdaReplenishmentPutawayCacheManager")
@Transactional
public class PdaReplenishmentPutawayCacheManagerImpl extends BaseManagerImpl implements PdaReplenishmentPutawayCacheManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaReplenishmentPutawayCacheManagerImpl.class);
    @Autowired
    private CacheManager cacheManager;
    
    @Override
    public ReplenishmentScanResultComamnd tipLocation(List<Long> locationIds, Long operationId) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayCacheManagerImpl tipLocation is start");
        ReplenishmentScanResultComamnd command = new ReplenishmentScanResultComamnd();
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        Long tipLocationId = null;
        for(Long id:locationIds) {
            if(null == replenishment){
                tipLocationId = id;
                replenishment = new ReplenishmentPutawayCacheCommand();
                ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
                tipLocationIds.addFirst(id);
                replenishment.setTipLocationIds(tipLocationIds);
                cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                break;
            }else{
                ArrayDeque<Long> tipLocationIds = replenishment.getTipLocationIds(); 
                if(null == tipLocationIds || tipLocationIds.isEmpty()) {
                    cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
                    ReplenishmentPutawayCacheCommand replenishmentCmd = new ReplenishmentPutawayCacheCommand();
                    ArrayDeque<Long> cacheLocationIds = new ArrayDeque<Long>();
                    cacheLocationIds.addFirst(id);
                    replenishmentCmd.setTipLocationIds(cacheLocationIds);
                    cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishmentCmd, CacheConstants.CACHE_ONE_DAY);
                    tipLocationId = id;
                    break;
                }else{
                    if(tipLocationIds.contains(id)) {
                        continue;
                    }else{
                        tipLocationIds.addFirst(id);
                    }
                    replenishment.setTipLocationIds(tipLocationIds);
                    cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                    tipLocationId = id;
                    break;
                }
            }
        }
        if(null != tipLocationId){
            command.setLocationId(tipLocationId);
            command.setIsNeedScanLocation(true);
        }else{
            command.setIsNeedScanLocation(false);   //所有库位已经扫完
        }
        log.info("PdaReplenishmentPutawayCacheManagerImpl tipLocation is end");
        return command;
    }

    @Override
    public ReplenishmentScanResultComamnd tipTurnoverBox(Set<Long> turnoverBoxIds, Long operationId) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayCacheManagerImpl tipTurnoverBox is start");
        ReplenishmentScanResultComamnd command = new ReplenishmentScanResultComamnd();
        Long turnoverBoxId = null;
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        if(null == replenishment) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        for(Long id:turnoverBoxIds) {
            ArrayDeque<Long> tipTurnoverBoxIds = replenishment.getTipTurnoverBoxIds();
            if(null == tipTurnoverBoxIds) {
                 tipTurnoverBoxIds = new ArrayDeque<Long>(); 
                 tipTurnoverBoxIds.addFirst(id);
                 replenishment.setTipTurnoverBoxIds(tipTurnoverBoxIds);
                 cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                 turnoverBoxId = id;
                 break;
            }else{
                if(tipTurnoverBoxIds.contains(id)){
                    continue;
                }else{
                    tipTurnoverBoxIds.addFirst(id);
                    replenishment.setTipTurnoverBoxIds(tipTurnoverBoxIds);
                    cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                    turnoverBoxId = id;
                    break;
                }
            }
        }
        if(null != turnoverBoxId){
            command.setTurnoverBoxId(turnoverBoxId);
            command.setIsNeedScanTurnoverBox(true);
        }else{
            command.setIsNeedScanTurnoverBox(false);
        }
        log.info("PdaReplenishmentPutawayCacheManagerImpl tipTurnoverBox is end");
        return command;
    }
    

    /***
     * 清楚补货上架缓存
     * @param operationId
     */
     public void pdaReplenishPutwayRemoveAllCache(Long operationId){
         log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is start");
         ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
         if(null == replenishment) {
             throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
         }
         cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION+operationId.toString());
         log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is end");
     }

}
