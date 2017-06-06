package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishScanTipSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;

@Service("pdaReplenishmentPutawayCacheManager")
@Transactional
public class PdaReplenishmentPutawayCacheManagerImpl extends BaseManagerImpl implements PdaReplenishmentPutawayCacheManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaReplenishmentPutawayCacheManagerImpl.class);
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    
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
    public ReplenishmentScanResultComamnd tipTurnoverBox(Set<Long> turnoverBoxIds, Long operationId,Long locationId) {
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
                Map<Long,ArrayDeque<Long>> tipMapTurnoverBoxIds = replenishment.getTipTurnoverBoxIds();
                if(null == tipMapTurnoverBoxIds) {
                     turnoverBoxId = id;
                     break;
                }else{
                    ArrayDeque<Long> tipTurnoverBoxIds = tipMapTurnoverBoxIds.get(locationId);
                    if(null == tipTurnoverBoxIds || tipTurnoverBoxIds.isEmpty()) {
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
     * 提示托盘
     * @param outerContainerIds
     * @param operationId
     * @param locationId
     * @return
     */
    public ReplenishmentScanResultComamnd tipOutContainer(Set<Long> outerContainerIds,Long operationId,Long locationId){
        ReplenishmentScanResultComamnd command = new ReplenishmentScanResultComamnd();
        Long tipOuterContainerId = null;
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        if(null == replenishment){
            replenishment = new ReplenishmentPutawayCacheCommand();
            for(Long outerContaierId:outerContainerIds){
                tipOuterContainerId = outerContaierId;
                break;
            }
        }else{
            for(Long outerContainerId:outerContainerIds){
                Map<Long,ArrayDeque<Long>>  palletIdsMap = replenishment.getTipPalletIds();
                if(null == palletIdsMap || palletIdsMap.size() == 0){
                    tipOuterContainerId = outerContainerId;
                    break;
                }else{
                    ArrayDeque<Long> palletIds = palletIdsMap.get(locationId);
                    if(null == palletIds || palletIds.isEmpty()) {
                        tipOuterContainerId = outerContainerId;
                        break;
                      }else{
                        if(palletIds.contains(outerContainerId)){
                            continue;
                        }else{
                            tipOuterContainerId = outerContainerId;
                            break;
                        }
                    }
                }
            }
        }
        if(null != tipOuterContainerId){
            command.setPalletId(tipOuterContainerId);
            command.setIsNeedScanPallet(true);
        }else{
            command.setIsNeedScanPallet(false);
        }
       return command;
    }
    
//    /**
//     * 扫描货箱--整箱
//     * 
//     * @author qiming.liu
//     * @param ReplenishmentPutawayCommand
//     * @return
//     */
//    @Override
//    public ReplenishmentScanResultComamnd tipContainer(Set<Long> insideContainerIds, Long operationId, Long locationId) {
//        log.info("PdaReplenishmentPutawayCacheManagerImpl tipContainer is start");
//        ReplenishmentScanResultComamnd command = new ReplenishmentScanResultComamnd();
//        Long insideContainerId = null;
//        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
//        if(null == replenishment) {
//            for(Long id : insideContainerIds) {
//                insideContainerId = id;
//                break;
//            }
//        }else{
//            for(Long id : insideContainerIds) {
//                Map<Long,ArrayDeque<Long>> tipContainerIdsMap = replenishment.getTipContainerIds();
//                if(null == tipContainerIdsMap) {
//                    insideContainerId = id;
//                     break;
//                }else{
//                    ArrayDeque<Long> tipContainerIds = tipContainerIdsMap.get(locationId);
//                    if(null == tipContainerIds || tipContainerIds.isEmpty()) {
//                        insideContainerId = id;
//                      break;
//                  }else{
//                      if(tipContainerIds.contains(id)){
//                          continue;
//                      }else{
//                          insideContainerId = id;
//                          break;
//                      }
//                  }
//                   
//                }
//            }
//        }
//        if(null != insideContainerId){
//            command.setContainerId(insideContainerId);
//            command.setIsNeedScanContainer(true);
//        }else{
//            command.setIsNeedScanContainer(false);
//        }
//        log.info("PdaReplenishmentPutawayCacheManagerImpl tipContainer is end");
//        return command;
//    }

    /***
     * 清楚补货上架缓存
     * @param operationId
     */
    @Override
     public void pdaReplenishPutwayRemoveAllCache(Long operationId,Long turnoverBoxId,Long locationId,Boolean isPutaway){
         log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is start");
         OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
         if(null == opExecLineCmd){
             throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
         }
         Map<String, Set<Long>> locSkuIds = new HashMap<String, Set<Long>>();
         locSkuIds = opExecLineCmd.getSkuIds();
         String key = locationId.toString()+turnoverBoxId;
         Set<Long> skuIds = locSkuIds.get(key);
         if(null != skuIds && skuIds.size() != 0){
             for(Long skuId:skuIds){
                 cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                 cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
                 cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
             }
         }
         if(null != turnoverBoxId){
             cacheManager.remove(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnoverBoxId.toString());
         }
         if(isPutaway) {
              cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION+operationId.toString());
              cacheManager.remove(CacheConstants.OPERATIONEXEC_STATISTICS+operationId.toString());
         }
         log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is end");
     }
     

        
    
    /**
     * 补货上架缓存周转箱
     * @param operationId
     * @param locationId
     */
    public void pdaReplenishPutwayCacheTurnoverBox(Long operationId,Long turnoverBoxId,Long locationId,Long ouId,Boolean isOnlyLocation){
            //先判断此周转箱是否还存在容器库存
            int allCounts = whSkuInventoryDao.findAllInventoryCountsByInsideContainerId(ouId,turnoverBoxId);
            if(allCounts == 0 || isOnlyLocation) {
                ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
                if(null == replenishment){
                    replenishment = new ReplenishmentPutawayCacheCommand();
                    Map<Long,ArrayDeque<Long>> tipMapTurnoverBoxIds = new HashMap<Long,ArrayDeque<Long>>();
                    ArrayDeque<Long> tipTurnoverBoxIds = new ArrayDeque<Long>();
                    tipTurnoverBoxIds.addFirst(turnoverBoxId);
                    tipMapTurnoverBoxIds.put(locationId, tipTurnoverBoxIds);
                    replenishment.setTipTurnoverBoxIds(tipMapTurnoverBoxIds);
                    cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                }else{
                    Map<Long,ArrayDeque<Long>> tipMapTurnoverBoxIds = replenishment.getTipTurnoverBoxIds();
                    if(null == tipMapTurnoverBoxIds || tipMapTurnoverBoxIds.size() == 0){
                        tipMapTurnoverBoxIds = new HashMap<Long,ArrayDeque<Long>>();
                        ArrayDeque<Long> tipTurnoverBoxIds = new ArrayDeque<Long>();
                        tipTurnoverBoxIds.addFirst(turnoverBoxId);
                        tipMapTurnoverBoxIds.put(locationId, tipTurnoverBoxIds);
                        replenishment.setTipTurnoverBoxIds(tipMapTurnoverBoxIds);
                        cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                    }else{
                        ArrayDeque<Long> tipTurnoverBoxIds = tipMapTurnoverBoxIds.get(locationId);
                        if(null == tipTurnoverBoxIds || tipTurnoverBoxIds.isEmpty()) {
                            tipTurnoverBoxIds = new ArrayDeque<Long>();
                            tipTurnoverBoxIds.addFirst(turnoverBoxId);
                            tipMapTurnoverBoxIds.put(locationId, tipTurnoverBoxIds);
                            replenishment.setTipTurnoverBoxIds(tipMapTurnoverBoxIds);
                            cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                        }else{
                            if(!tipTurnoverBoxIds.contains(turnoverBoxId)) {
                                tipTurnoverBoxIds.addFirst(turnoverBoxId);
                                tipMapTurnoverBoxIds.put(locationId, tipTurnoverBoxIds);
                                replenishment.setTipTurnoverBoxIds(tipMapTurnoverBoxIds);
                                cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
                            }
                        }
                    }
                }
            }
    }
    
    
    /**
     * 补货缓存托盘
     * @param operationId
     * @param outerContainerId
     * @param locationId
     * @param ouId
     * @param isOnlyLocation
     */
    public void pdaReplenishPutwayCacheOuterContainer(Long operationId,Long outerContainerId,Long locationId,Long ouId,Boolean isOnlyLocation){
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        if(null == replenishment){
             replenishment = new ReplenishmentPutawayCacheCommand();
             Map<Long,ArrayDeque<Long>> tipPalletIds = new HashMap<Long,ArrayDeque<Long>>();
             ArrayDeque<Long> outContainerIds = new ArrayDeque<Long>();
             outContainerIds.addFirst(outerContainerId);
             tipPalletIds.put(locationId, outContainerIds);
             replenishment.setTipPalletIds(tipPalletIds);
        }else{
            Map<Long,ArrayDeque<Long>> tipPalletIds = replenishment.getTipPalletIds();
            if(null == tipPalletIds || tipPalletIds.size() == 0){
                ArrayDeque<Long> outContainerIds = new ArrayDeque<Long>();
                outContainerIds.addFirst(outerContainerId);
                tipPalletIds.put(locationId, outContainerIds);
                replenishment.setTipPalletIds(tipPalletIds);
            }else{
                ArrayDeque<Long> outContainerIds = tipPalletIds.get(locationId);
                if(null == outContainerIds || outContainerIds.size() == 0){
                    outContainerIds = new ArrayDeque<Long>();
                    outContainerIds.addFirst(outerContainerId);
                    tipPalletIds.put(locationId, outContainerIds);
                    replenishment.setTipPalletIds(tipPalletIds);
                }else{
                    outContainerIds.addFirst(outerContainerId);
                    tipPalletIds.put(locationId, outContainerIds);
                    replenishment.setTipPalletIds(tipPalletIds); 
                }
            }
        }
        cacheManager.setObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString(), replenishment, CacheConstants.CACHE_ONE_DAY);
    }
    /***
     * 提示sku
     * @param skuIds
     * @param skuAttrIdsQty
     * @param skuAttrIdsSnDefect
     */
    public String pdaReplenishPutWayTipSku(Set<Long> skuIds, Map<Long, Map<String, Long>> skuAttrIdsQty,Map<String, Set<String>> skuAttrIdsSnDefect,Long locationId,Long turnoverBoxId){
        ReplenishScanTipSkuCacheCommand replenishment = cacheManager.getObject(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnoverBoxId.toString());
        if(null == replenishment){
            replenishment = new ReplenishScanTipSkuCacheCommand();
        }
        ArrayDeque<Long> scanSkuIds = replenishment.getScanSkuIds();
        ArrayDeque<String> scanSkuAttrIds = replenishment.getScanSkuAttrIds();
        ArrayDeque<String> scanSkuAttrIdSn = replenishment.getScanSkuAttrIdSn();
        Long tipSkuId = null;
        for(Long skuId:skuIds) {
              if(!scanSkuIds.contains(skuId)) {
                  tipSkuId = skuId;
                  break;
              }  
        }
        Map<String, Long> skuAttrIdQty = skuAttrIdsQty.get(tipSkuId);
        //获取当前唯一sku
        String tipSkuAttrIds = null;
        Set<String> skuAttrIds = skuAttrIdQty.keySet();
        for(String skuAttrId :skuAttrIds){
            if(!scanSkuAttrIds.contains(skuAttrId)) {
                tipSkuAttrIds = skuAttrId;
                break;
            }
        }
        //判断当前sku有没有sn/残次信息
        if(null != skuAttrIdsSnDefect && skuAttrIdsSnDefect.size() != 0) {
           Set<String> skuAttrIdsSd =  skuAttrIdsSnDefect.keySet();
           if(skuAttrIdsSd.contains(tipSkuAttrIds)){
               Set<String> snDefect = skuAttrIdsSnDefect.get(tipSkuAttrIds);
               for(String snd:snDefect){
                  String tipSkuAttrIdSn =  SkuCategoryProvider.concatSkuAttrId(tipSkuAttrIds,snd);
                  if(!scanSkuAttrIdSn.contains(tipSkuAttrIdSn)) {
                      tipSkuAttrIds = tipSkuAttrIdSn;
                      break;
                  }
               }
           }
        }
        
        return tipSkuAttrIds;
    }
    
    /***
     * 补货中上架提示sku或周转箱
     * @param turnoverIds
     * @param skuIds
     * @param skuAttrIdsQty
     * @param skuAttrIdsSnDefect
     * @param locationId
     * @return
     */
    public CheckScanResultCommand pdaReplenishPutWayTipSkuTurnoverBox(List<Long> locationIds,Long skuId,Double scanSkuQty,String skuAttrId,String skuAttrIdNoSn,Boolean isSnLine,Long operationId,Long turnoverBoxId,Set<Long> turnoverIds,Set<Long> skuIds, Map<Long, Map<String, Long>> skuAttrIdsQty,
                                                                      Map<String, Set<String>> skuAttrIdsSnDefect,Long locationId){
        CheckScanResultCommand cssrCmd = new CheckScanResultCommand();
        // 0.先判断当前内部容器是否在缓存中
        boolean icExists = false; 
        for (Long iId : turnoverIds) {
            if (0 == turnoverBoxId.compareTo(iId)) {
                icExists = true;
                break;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        for (Long sId : skuIds) {
            if (0 == skuId.compareTo(sId)) {
                icExists = true;
                break;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        //判断当前周转箱是否在缓存中
        ReplenishmentPutawayCacheCommand replenishment = cacheManager.getObject(CacheConstants.CACHE_PUTAWAY_LOCATION + operationId.toString());
        if(null == replenishment){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        ArrayDeque<Long> tipLocationIds = replenishment.getTipLocationIds();
        Map<Long,ArrayDeque<Long>> tipMapTurnoverBoxIds = replenishment.getTipTurnoverBoxIds();
        ArrayDeque<Long> scanTurnoverBoxIds = tipMapTurnoverBoxIds.get(locationId);
        if(null == scanTurnoverBoxIds || scanTurnoverBoxIds.size() == 0){
            scanTurnoverBoxIds = new ArrayDeque<Long>();
        }
        scanTurnoverBoxIds.addFirst(turnoverBoxId);
        Map<String, Long> skuAttrIdQty = skuAttrIdsQty.get(skuId);
        Long skuQty = skuAttrIdQty.get(skuAttrIdNoSn);
        ReplenishScanTipSkuCacheCommand  scanTipSkuCmd = cacheManager.getObject(CacheConstants.PDA_REPLENISH_PUTAWAY_SCAN_SKU + locationId.toString()+turnoverBoxId.toString());
        if(null == scanTipSkuCmd){
            log.error("scan container queue is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        ArrayDeque<String> scanSkuAttrIdSn = scanTipSkuCmd.getScanSkuAttrIdSn();  //包含sn/残次信息
        if(true== isSnLine) {  //有sn/残次信息,循环扫sn/残次信息
            long snCount =  cacheManager.incr(CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
            if(snCount < scanSkuQty.longValue()) {
               Set<String> snDefects =  skuAttrIdsSnDefect.get(skuAttrIdNoSn);
               for(String snDefect:snDefects){
                   String skuAttrIdsSn = SkuCategoryProvider.concatSkuAttrId(skuAttrIdNoSn,snDefect);
                   if(scanSkuAttrIdSn.contains(skuAttrIdsSn)){
                       continue;
                   }else{
                       cssrCmd.setIsContinueScanSn(true); 
                       cssrCmd.setTipSkuAttrId(skuAttrIdsSn);
                       return cssrCmd;
                   }
               }
           }
       }
       long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + locationId.toString()+ turnoverBoxId.toString() + skuId.toString(), scanSkuQty.intValue());
       if(cacheValue == skuQty.longValue()){
           cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
           //判断当前周转箱内相同sku有没有不同库存属性的sku
           ArrayDeque<String> scanSkuAttrIds = scanTipSkuCmd.getScanSkuAttrIds();
           Set<String> skuAttrIds = skuAttrIdQty.keySet();  //获取所有的唯一sku
           if(!isCacheAllExists2(skuAttrIds, scanSkuAttrIds)){  //所有唯一sku是否扫描完毕,相同返回true,不相同返回false
               String tipSkuAttrId = null;
               for(String skuAttr:skuAttrIds){
                   if(!scanSkuAttrIds.contains(skuAttr)){  //缓存中没有唯一sku
                       tipSkuAttrId = skuAttr;
                       break;
                   }
               }
               //拼接sn/残次信息(如果存在sn/残次信息)
               if(null != skuAttrIdsSnDefect && skuAttrIdsSnDefect.size() != 0 && null != tipSkuAttrId){
                   Set<String>  snDefects = skuAttrIdsSnDefect.get(tipSkuAttrId);
                   for(String snDefect:snDefects){
                       String skuAttrIdsSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,snDefect);
                       cssrCmd.setTipSkuAttrId(skuAttrIdsSn);
                       cssrCmd.setIsNeedScanSku(true);
                        break;
                   }
               }
           }else {
               ArrayDeque<Long> cacheSkuIds = scanTipSkuCmd.getScanSkuIds();
               if (isCacheAllExists(skuIds, cacheSkuIds)) {
                    //判断有没有下个周转箱
                   if (!isCacheAllExists(turnoverIds, scanTurnoverBoxIds)){//当前库位上有没有扫描的周转箱
                       for(Long tId:turnoverIds){
                           if(!scanTurnoverBoxIds.contains(tId)){
                               cssrCmd.setTipiInsideContainerId(tId);
                               cssrCmd.setIsNeedTipInsideContainer(true);  //周转箱是内部容器
                               break;
                           }
                       }
                   }else{ // 提示下一个库位
                       if(!isCacheAllExists(locationIds, tipLocationIds)){
                           for(Long locId:locationIds){
                               if(!tipLocationIds.contains(locId)){
                                   cssrCmd.setIsNeedTipLoc(true);
                                   cssrCmd.setTipLocationId(locId);
                                   break;
                               }
                           }
                       }else{
                           cssrCmd.setIsPutaway(true);
                       }
                   }
               }else{
                   //同一个周转箱有不同种类的sku
                   Long tipSkuId = null;
                   for(Long sId:skuIds) {
                       if(!cacheSkuIds.contains(sId)){
                           tipSkuId = sId;
                           break;
                       }
                   }
                   Map<String, Long> tipSkuAttrIdQty = skuAttrIdsQty.get(tipSkuId);
                   //获取当前唯一sku
                   String tipSkuAttrIds = null;
                   Set<String> skuAttrIdsTemp = tipSkuAttrIdQty.keySet();
                   for(String skuAttrIdTemp :skuAttrIdsTemp){
                       if(!scanSkuAttrIds.contains(skuAttrIdTemp)) {
                           tipSkuAttrIds = skuAttrId;
                           break;
                       }
                   }
                   //判断当前sku有没有sn/残次信息
                   if(null != skuAttrIdsSnDefect && skuAttrIdsSnDefect.size() != 0) {
                      Set<String> skuAttrIdsSd =  skuAttrIdsSnDefect.keySet();
                      if(skuAttrIdsSd.contains(tipSkuAttrIds)){
                          Set<String> snDefect = skuAttrIdsSnDefect.get(tipSkuAttrIds);
                          for(String snd:snDefect){
                             String tipSkuAttrIdSn =  SkuCategoryProvider.concatSkuAttrId(tipSkuAttrIds,snd);
                             if(!scanSkuAttrIdSn.contains(tipSkuAttrIdSn)) {
                                 tipSkuAttrIds = tipSkuAttrIdSn;
                                 break;
                             }
                          }
                      }
                   }
                   cssrCmd.setIsNeedScanSku(true);
                   cssrCmd.setTipSkuAttrId(tipSkuAttrIds);
               }
           }
       }else if(cacheValue < skuQty.longValue()){
           cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN_COUNT +locationId.toString()+ turnoverBoxId.toString() + skuId.toString());
           // 继续复核
           cssrCmd.setIsNeedScanSku(true);
           if(isSnLine) {
               //有sn的
               Set<String> snDefect = skuAttrIdsSnDefect.get(skuAttrIdNoSn);  //获取sn/残次信息
               Boolean result = false;
               for(String sn:snDefect) {
                       String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrIdNoSn,sn);
                       if(!scanSkuAttrIdSn.contains(skuAttrIdSn)){
                           cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                           result = true;
                       }
               }
               if(!result) {
                   log.error("tip container is not in cache server error, logId is[{}]", logId);
                   throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
               }
           }else{
               cssrCmd.setTipSkuAttrId(skuAttrIdNoSn);
           }
       }else{
           log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, skuQty, logId);
           throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
       }
      return cssrCmd;
    }
    
    private boolean isCacheAllExists(List<Long> ids, ArrayDeque<Long> cacheKeys) {
        boolean allExists = true;  //默认没有复合完毕   
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (Long id : ids) {
                boolean isExists = false;
                Iterator<Long> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    Long value = iter.next();
                    if (null == value) value = -1L;
                    if (0 == value.compareTo(id)) {
                        isExists = true;  //没有复合完毕
                        break;
                    }
                }
                if (false == isExists) {
                    allExists = false;
                }
            }
        } else {
            allExists = false;
        }
        return allExists;
    }
    
    private boolean isCacheAllExists(Set<Long> ids, ArrayDeque<Long> cacheKeys) {
        boolean allExists = true;  //默认没有复合完毕   
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (Long id : ids) {
                boolean isExists = false;
                Iterator<Long> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    Long value = iter.next();
                    if (null == value) value = -1L;
                    if (0 == value.compareTo(id)) {
                        isExists = true;  //没有复合完毕
                        break;
                    }
                }
                if (false == isExists) {
                    allExists = false;
                }
            }
        } else {
            allExists = false;
        }
        return allExists;
    }
    
    /**
     * 判断是值是否相同(如果相同返回true,不相同返回false)
     * @param ids
     * @param cacheKeys
     * @return
     */
    private boolean isCacheAllExists2(Set<String> ids, ArrayDeque<String> cacheKeys) {
        boolean allExists = true;
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                Iterator<String> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    String value = iter.next();
                    if (null == value) value = "-1";
                    if (value.equals(cId)) {
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    allExists = false;
                }
            }
        } else {
            allExists = false;
        }
        return allExists;
    }
}
