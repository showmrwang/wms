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
                break;
            }else{
                ArrayDeque<Long> tipLocationIds = replenishment.getTipLocationIds(); 
                if(null == tipLocationIds || tipLocationIds.isEmpty()) {
                    tipLocationId = id;
                    break;
                }else{
                    if(tipLocationIds.contains(id)) {
                        continue;
                    }
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
            for(Long id:turnoverBoxIds) {
                turnoverBoxId = id;
                break;
            }
        }else{
            for(Long id:turnoverBoxIds) {
                ArrayDeque<Long> tipTurnoverBoxIds = replenishment.getTipTurnoverBoxIds();
                if(null == tipTurnoverBoxIds) {
                     turnoverBoxId = id;
                     break;
                }else{
                    if(null == turnoverBoxIds || turnoverBoxIds.isEmpty()) {
                        turnoverBoxId = id;
                      break;
                  }else{
                      if(tipTurnoverBoxIds.contains(id)){
                          continue;
                      }else{
                          turnoverBoxId = id;
                          break;
                      }
                  }
                   
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
    @Override
     public void pdaReplenishPutwayRemoveAllCache(Long operationId){
         log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is start");
         cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION+operationId.toString());
         cacheManager.remove(CacheConstants.OPERATIONEXEC_STATISTICS+operationId.toString());
         log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is end");
     }
     

     /**
      * 补货上架缓存库位
      * @param operationId
      * @param locationId
      */
    @Override
     public void pdaReplenishPutwayCacheLoc(Long operationId,Long locationId){
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
            if(null == replenishment){
                replenishment = new ReplenishmentPutawayCacheCommand();
                ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
                tipLocationIds.addFirst(locationId);
                replenishment.setTipLocationIds(tipLocationIds);
                cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
            }else{
                ArrayDeque<Long> tipLocationIds = replenishment.getTipLocationIds(); 
                if(null == tipLocationIds || tipLocationIds.isEmpty()) {
                    tipLocationIds = new ArrayDeque<Long>();
                    tipLocationIds.addFirst(locationId);
                    replenishment.setTipLocationIds(tipLocationIds);
                    cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                }else{
                    if(!tipLocationIds.contains(locationId)) {
                        tipLocationIds.addFirst(locationId);
                        replenishment.setTipLocationIds(tipLocationIds);
                        cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                    }
                }
            }
     }
    
    
    /**
     * 补货上架缓存周转箱
     * @param operationId
     * @param locationId
     */
    public void pdaReplenishPutwayCacheTurnoverBox(Long operationId,Long turnoverBoxId){
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        if(null == replenishment){
            replenishment = new ReplenishmentPutawayCacheCommand();
            ArrayDeque<Long> tipTurnoverBoxIds = new ArrayDeque<Long>();
            tipTurnoverBoxIds.addFirst(turnoverBoxId);
            replenishment.setTipTurnoverBoxIds(tipTurnoverBoxIds);
            cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
        }else{
            ArrayDeque<Long> tipTurnoverBoxIds = replenishment.getTipTurnoverBoxIds();
            if(null == tipTurnoverBoxIds || tipTurnoverBoxIds.isEmpty()) {
                 tipTurnoverBoxIds = new ArrayDeque<Long>();
                tipTurnoverBoxIds.addFirst(turnoverBoxId);
                replenishment.setTipTurnoverBoxIds(tipTurnoverBoxIds);
                cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
            }else{
                if(!tipTurnoverBoxIds.contains(turnoverBoxId)) {
                    tipTurnoverBoxIds.addFirst(turnoverBoxId);
                    replenishment.setTipTurnoverBoxIds(tipTurnoverBoxIds);
                    cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                }
            }
        }
        
    }
}
