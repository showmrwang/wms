package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.LocationTipCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationLineCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ScanTipSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CancelPattern;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.utilities.type.StringUtil;

@Service("pdaPickingWorkCacheManager")
@Transactional
public class PdaPickingWorkCacheManagerImpl extends BaseManagerImpl implements PdaPickingWorkCacheManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaPickingWorkCacheManagerImpl.class);
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhOperationLineDao whOperationLineDao;
    @Autowired
    private WhOperationDao whOperationDao;
    @Autowired
    private  WhOperationExecLineDao  whOperationExecLineDao;
    @Autowired
    private WhWorkDao whWorkDao;
    
    /***
     * 有小车，而且有出库箱的时候，提示出库箱
     * @param operatorLineList
     * @param operationId
     * @return
     */
    @Override
    public CheckScanResultCommand pdaPickingTipOutBounxBoxCode(List<WhOperationLineCommand> operatorLineList, Long operationId, Map<Integer, String> carStockToOutgoingBox) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipOutBounxBoxCode is start");
        CheckScanResultCommand cSRCmd = new CheckScanResultCommand();
        String outBounxBoxCode = null;
        for(WhOperationLineCommand operLineCmd:operatorLineList) {
            Integer useContainerLatticeNo = operLineCmd.getUseContainerLatticeNo(); 
            outBounxBoxCode = carStockToOutgoingBox.get(useContainerLatticeNo);
            OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
            if(null == tipLocationCmd) {
                OperationLineCacheCommand tipCmd = new OperationLineCacheCommand();
                ArrayDeque<String> tipOutBonxBoxIds = new ArrayDeque<String>();
                tipOutBonxBoxIds.addFirst(outBounxBoxCode);
                tipCmd.setTipOutBonxBoxIds(tipOutBonxBoxIds);
                cSRCmd.setOutBounxBoxCode(outBounxBoxCode);
                cSRCmd.setIsNeedScanOutBounxBox(true);
                cSRCmd.setUseContainerLatticeNo(useContainerLatticeNo);  //货格号
                cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
            }else{
                ArrayDeque<String> tipOutBonxBoxIds = tipLocationCmd.getTipOutBonxBoxIds();
                if(null == tipOutBonxBoxIds){
                    tipOutBonxBoxIds = new ArrayDeque<String>();
                    tipOutBonxBoxIds.addFirst(outBounxBoxCode);
                    tipLocationCmd.setTipOutBonxBoxIds(tipOutBonxBoxIds);
                    cSRCmd.setOutBounxBoxCode(outBounxBoxCode);
                    cSRCmd.setIsNeedScanOutBounxBox(true);
                    cSRCmd.setUseContainerLatticeNo(useContainerLatticeNo);  //货格号
                }else{
                     if(!tipOutBonxBoxIds.contains(outBounxBoxCode)) {
                         tipOutBonxBoxIds.addFirst(outBounxBoxCode);
                         cSRCmd.setOutBounxBoxCode(outBounxBoxCode);
                         cSRCmd.setIsNeedScanOutBounxBox(true);
                         cSRCmd.setUseContainerLatticeNo(useContainerLatticeNo);  //货格号
                         tipLocationCmd.setTipOutBonxBoxIds(tipOutBonxBoxIds);
                     }else{
                         cSRCmd.setIsNeedScanOutBounxBox(false);
                         continue;
                     }
               
                }
                cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
            }
            break;
        }
       
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipOutBounxBoxCode is end");
        return cSRCmd;
    }
    
//    /***
//     * 统计要拣货的库位库存
//     * @param operationId
//     * @param locationId
//     * @param ouId
//     * @return
//     */
//    public List<WhSkuInventoryCommand> cacheLocationInventory(Long operationId,Long locationId,Long ouId,String operationWay) {
//        log.info("PdaPickingWorkCacheManagerImpl cacheLocationInventory is start");
//        List<WhSkuInventoryCommand> skuInvList = cacheManager.getObject(CacheConstants.CACHE_LOC_INVENTORY + operationId.toString()+locationId.toString());
//        if(null == skuInvList || skuInvList.size() == 0) {
//            if (Constants.PICKING_INVENTORY.equals(operationWay)) { //拣货
//                skuInvList = whSkuInventoryDao.getWhSkuInventoryByOccupationLineId(ouId, operationId);
//            }
//            if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) {//补货
//                skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOperationId(ouId, operationId);
//            }
//            if(null == skuInvList || skuInvList.size() == 0){
//                    throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
//            }
////            List<WhOperationLineCommand> operationLineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);   //当前工作下所有的作业明细集合
////            for(WhOperationLineCommand operLineCmd : operationLineList){
////                Long odoLineId = operLineCmd.getOdoLineId();  //出库单明细ID
////                for(WhSkuInventoryCommand skuInvCmd:skuInvList) {
////                    Long occupationLineId = skuInvCmd.getOccupationId();
////                    if(odoLineId.equals(occupationLineId)) {
////                        skuInvList.add(skuInvCmd); 
////                    }
////                }
////            }
//            cacheManager.setObject(CacheConstants.CACHE_LOC_INVENTORY + operationId.toString()+locationId.toString(), skuInvList, CacheConstants.CACHE_ONE_DAY);
//        }
//        log.info("PdaPickingWorkCacheManagerImpl cacheLocationInventory is end");
//        return skuInvList;
//    }
//    
    /***
     * 提示库位
     * @param command
     * @return
     */
    public CheckScanResultCommand tipLocation(Long operationId,List<Long> locationIds){
        log.info("PdaPickingWorkCacheManagerImpl containerPutawayCacheInsideContainer is start");
        CheckScanResultCommand scanResult = new CheckScanResultCommand();
        scanResult.setIsPicking(true);
        scanResult.setIsNeedTipLoc(false);
        Long tipLocationId = null;
        OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
        for(Long locationId:locationIds) {
            if(null == tipLocationCmd) {
                tipLocationId = locationId;
                scanResult.setIsNeedTipLoc(true);
                scanResult.setIsPicking(false);
                break;
            }else{
                ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
                if (null != tipLocationIds && !tipLocationIds.isEmpty()) {
                    if(tipLocationIds.contains(locationId)) {
                        continue;
                    }else{
                        tipLocationId = locationId;
                        scanResult.setIsNeedTipLoc(true);
                        scanResult.setIsPicking(false);
                        break;
                    }
                } else {
                    tipLocationId = locationId;
                    scanResult.setIsNeedTipLoc(true);
                    scanResult.setIsPicking(false);
                    break;
                 }
            }
        }
        scanResult.setTipLocationId(tipLocationId);
        log.info("PdaPickingWorkCacheManagerImpl containerPutawayCacheInsideContainer is start");
        return scanResult;
    }  


    /***
     * 缓存库位
     * @param operation
     * @param locationId
     */
    public void cacheLocation(Long operationId,Long locationId){
        OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
        if(null == tipLocationCmd){
             tipLocationCmd = new OperationLineCacheCommand();
             ArrayDeque<Long> locIds = new ArrayDeque<Long>();
             locIds.addFirst(locationId);
             tipLocationCmd.setTipLocationIds(locIds);
        }else{
            ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
            if(null == tipLocationIds || tipLocationIds.size() == 0){
                ArrayDeque<Long> locIds = new ArrayDeque<Long>();
                locIds.addFirst(locationId);
                tipLocationCmd.setTipLocationIds(locIds);
            }else{
                if(!tipLocationIds.contains(locationId)) {
                    tipLocationIds.addFirst(locationId);
                    tipLocationCmd.setTipLocationIds(tipLocationIds);
                }
            }
        }
        cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
    }


    /***
     * 提示小车
     * @param operationId
     * @return
     */
    @Override
    public String pdaPickingWorkTipOutContainer(Long operationId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        String tipOuterContainer = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> outerContainerIds = operatorLine.getOuterContainers();   //所有小车ids
        if(outerContainerIds.size() == 0) {
            throw new BusinessException(ErrorCodes.OUT_CONTAINER_IS_NO_NULL);   //推荐小车不能为空
        }
        for(Long id:outerContainerIds) {
            if(null != id) {
                Container container = containerDao.findByIdExt(id, ouId);
                if(null == container) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                // 验证容器Lifecycle是否有效
                if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
                    continue;
                }
                // 验证容器状态是否是
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_REC_OUTBOUNDBOX) || container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PICKING))) {
                    continue;
                }
                tipOuterContainer = container.getCode();
                break;
            }
        }
        if(StringUtil.isEmpty(tipOuterContainer)) {
            throw new BusinessException(ErrorCodes.OUT_CONTAINER_IS_NO_NULL);
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is end");
        return tipOuterContainer;
    }

    /***
     * 提示出库箱
     * @param operationId
     * @return
     */
    @Override
    public String pdaPickingWorkTipoutboundBox(Long operationId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        String outbounxBox = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<String> outbounxBoxCodes = operatorLine.getOutbounxBoxs();//需要确认是出库箱code还是出库箱Id
        if(outbounxBoxCodes.size() == 0) {
            throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL);   //推荐出库箱不能为空
        }
        for(String code:outbounxBoxCodes) {
            if(null != code) {
//                OutBoundBoxType o = outBoundBoxTypeDao.findByCode(code, ouId);
//                if(null == o) {
//                    throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
//                }
//                // 验证容器Lifecycle是否有效
//                if (!o.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
//                    continue;
//                }
                outbounxBox = code;
                break;
            }
        }
        if(StringUtil.isEmpty(outbounxBox)) {
            throw new BusinessException(ErrorCodes. OUT_BOUNDBOX_IS_NOT_NORMAL);
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipoutbounxBox is end");
        return outbounxBox;
    }

    /***
     * 提示周转箱
     * @param operationId
     * @return
     */
    @Override
    public String pdaPickingWorkTipTurnoverBox(Long operationId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is start");
        String turnoverBox = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> turnoverBoxIds = operatorLine.getTurnoverBoxs();
        if(turnoverBoxIds.size() == 0) {
            throw new BusinessException(ErrorCodes.TURNOVER_BOX_IS_NO_NULL);   //推荐周转箱不能为空
        }
        for(Long id:turnoverBoxIds) {
            if(null != id) {
                Container container = containerDao.findByIdExt(id, ouId);
                if(null == container) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                // 验证容器Lifecycle是否有效
                if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
                    continue;
                }
                // 验证容器状态是否是
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_REC_OUTBOUNDBOX) || container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PICKING))) {
                    continue;
                }
                turnoverBox = container.getCode();
                break;
            }
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is end");
        return turnoverBox;
    }

    /**
     * 缓存统计分析结果
     * 
     * @author qiming.liu
     * @param operationId
     * @param operatioLineStatisticsCommand
     * @return
     */
    @Override
    public void operatioLineStatisticsRedis(Long operationId, OperatioLineStatisticsCommand operatioLineStatisticsCommand) {
        cacheManager.setObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString(), operatioLineStatisticsCommand);
    }
    
    /**
     * 缓存统计分析结果
     * 
     * @author qiming.liu
     * @param operationId
     * @param operatioLineStatisticsCommand
     * @return
     */
    @Override
    public void operationExecStatisticsRedis(Long operationId, OperationExecStatisticsCommand operationExecStatisticsCommand) {
        cacheManager.setObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString(), operationExecStatisticsCommand);
    }

    /**
     * 根据作业ID和OUID获取统计分析结果
     * 
     * @author qiming.liu
     * @param operationId
     * @param ouId
     * @return
     */
    @Override
    public OperatioLineStatisticsCommand getOperatioLineStatistics(Long operationId, Long ouId) {
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        return operatorLine;
    }

    /***
     * pda拣货提示托盘
     * @author tangming
     * @param outerContainerIds
     * @param locationId
     * @return
     */
      public CheckScanResultCommand pdaPickingTipOuterContainer(Set<Long> outerContainerIds,Long locationId){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipOuterContainer is start");
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          scanResult.setIsNeedTipOutContainer(false); // 所有的外部容器已经扫描完毕
          Long tipOuterContainerId = null;
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          for(Long outerId:outerContainerIds){
              if(null == tipLocationCmd) {
                  tipOuterContainerId = outerId;
                  scanResult.setIsNeedTipOutContainer(true); 
                  break;
              }else{
                  Map<Long,ArrayDeque<Long>> tipLocOuterContainerIds = tipLocationCmd.getTipLocOuterContainerIds();
                  if(null == tipLocOuterContainerIds || tipLocOuterContainerIds.size() == 0) {
                      tipOuterContainerId = outerId;
                      scanResult.setIsNeedTipOutContainer(true); 
                      break;
                  }else{
                      ArrayDeque<Long> tipOuterContainerIds = tipLocOuterContainerIds.get(locationId);
                      if(tipOuterContainerIds == null || tipOuterContainerIds.size() == 0){
                             tipOuterContainerId = outerId;
                             scanResult.setIsNeedTipOutContainer(true); 
                             break;
                      }else{
                             if(tipOuterContainerIds.contains(outerId)){
                                        continue;
                      }else{
                              tipOuterContainerId = outerId;
                              scanResult.setIsNeedTipOutContainer(true); 
                              break;
                             }
                      }
                  }
              }
          }
          scanResult.setTipOuterContainerId(tipOuterContainerId);
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipOuterContainer is end");
          return scanResult;
      }
      
      
      /***
       * pda拣货提示货箱
       * @author tangming
       * @param insideContainerIds
       * @param operationId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipInsideContainer(Set<Long> insideContainerIds,Long locationId,Long outerContainerId){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipInsideContainer is start");
          Long tipInsideContainerId = null;
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          scanResult.setIsNeedTipInsideContainer(false);
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          for(Long insideId:insideContainerIds) {
                  if(null == tipLocationCmd) {
                      tipInsideContainerId = insideId;
                      scanResult.setIsNeedTipInsideContainer(true);
                      break;
                  }else{
                      ArrayDeque<Long> tipInsideContainerIds = null;
                      if(null != outerContainerId) {
                          Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = tipLocationCmd.getTipOuterInsideContainerIds();
                          tipInsideContainerIds = tipOuterInsideContainerIds.get(outerContainerId);
                      }else{
                          Map<Long,ArrayDeque<Long>> tipLocInsideContainerIds = tipLocationCmd.getTipLocInsideContainerIds();
                          tipInsideContainerIds = tipLocInsideContainerIds.get(locationId);
                      }
                      if(null != tipInsideContainerIds && tipInsideContainerIds.size() != 0) {
                          if(!tipInsideContainerIds.contains(insideId)) {
                                tipInsideContainerId =  insideId;
                                scanResult.setIsNeedTipInsideContainer(true);
                                break;
                          }
                      }else{
                          tipInsideContainerId =  insideId;
                          scanResult.setIsNeedTipInsideContainer(true);
                          break;
                      }
                  }
          }
          scanResult.setTipiInsideContainerId(tipInsideContainerId);
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipInsideContainer is end");
          return scanResult;
      }
      /***
       * pda拣货提示sku
       * @param insideContainerIds
       * @param operationId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipSku(Long outerContainerId,String operationWay,Set<Long> skuIds,Long operationId,Long locationId,Long ouId,Long insideContainerId,Map<Long, Map<String, Set<String>>> locskuAttrIdsSnDefect,Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipSku is start");
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          Long tipSkuId = null;
          scanResult.setIsNeedScanSku(false);
          ScanTipSkuCacheCommand tipScanSkuCmd = null;
          if(null != insideContainerId) { //有货箱
              tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +insideContainerId.toString());
          }else{//没有货箱
              tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +locationId.toString());
          }
          if(null == tipScanSkuCmd) {
                for(Long skuId:skuIds){
                    tipSkuId = skuId;
                    scanResult.setIsNeedScanSku(true);
                    break;
                }
          }else{
              ArrayDeque<Long> tipSkuIds = tipScanSkuCmd.getScanSkuIds();
              if(null == tipSkuIds || tipSkuIds.size() == 0) {
                  for(Long skuId:skuIds){
                      tipSkuId = skuId;
                      scanResult.setIsNeedScanSku(true);
                      break;
                  }
              }else{
                  if(this.isCacheAllExists(skuIds, tipSkuIds)) {
                      for(Long skuId:skuIds) {
                          //判断改外部容器id是否已经存在缓存中
                          if(tipSkuIds.contains(skuId)) {
                               continue;
                          }else{
                               tipSkuId = skuId;
                               scanResult.setIsNeedScanSku(true);
                               break;
                         }
                      }
                   }
              }
          }
          if(!scanResult.getIsNeedScanSku()) {
              return scanResult;
          }
          //  //拼装唯一sku及残次信息 缓存sku唯一标示
//          List<WhSkuInventoryCommand> list = this.cacheLocationInventory(operationId, locationId, ouId,operationWay);
          List<WhSkuInventoryCommand> skuInvList  = null;
          if (Constants.PICKING_INVENTORY.equals(operationWay)) { //拣货
              skuInvList = whSkuInventoryDao.getWhSkuInventoryByOccupationLineId(locationId,ouId, operationId,outerContainerId,insideContainerId);
          }
          if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) {//补货
              skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOperationId(ouId, operationId,locationId,outerContainerId,insideContainerId);
          }
          if(null == skuInvList || skuInvList.size() == 0){
                  throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
          }
          String skuAttrId = null;
          for(WhSkuInventoryCommand skuCmd:skuInvList) {
               if(null != insideContainerId) { //有货箱
                      if(insideContainerId.equals(skuCmd.getInsideContainerId()) && locationId.equals(skuCmd.getLocationId())) {
                          if(tipSkuId.longValue() == skuCmd.getSkuId().longValue()) {
                                 skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                                 skuAttrId = this.concatSkuAttrIdSn(skuAttrId, tipScanSkuCmd, locskuAttrIdsSnDefect, insideSkuAttrIdsSnDefect, insideContainerId, locationId);
                                 if(StringUtils.isEmpty(skuAttrId)) {
                                     continue;
                                 }else{
                                     break;
                                 }
                           }
                      }
                }else{//散装
                      if(null == skuCmd.getInsideContainerId() && tipSkuId.longValue() == skuCmd.getSkuId().longValue() && locationId.equals(skuCmd.getLocationId())) {
                          skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                          skuAttrId = this.concatSkuAttrIdSn(skuAttrId, tipScanSkuCmd, locskuAttrIdsSnDefect, insideSkuAttrIdsSnDefect, insideContainerId, locationId);
                          if(StringUtils.isEmpty(skuAttrId)) {
                              continue;
                          }else{
                              break;
                          }
                       }
                }
          }
          //拼装sn/残次信息
          scanResult.setTipSkuAttrId(skuAttrId);
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipSku is end");
          return scanResult;
      }
      
      //拼装skuAttrId ，sn(如果没有sn,直接返回唯一sku)
      private String concatSkuAttrIdSn(String skuAttrId,ScanTipSkuCacheCommand tipScanSkuCmd,Map<Long, Map<String, Set<String>>> locskuAttrIdsSnDefect,Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect,Long insideContainerId,Long locationId){
               Map<String, Set<String>>  skuAttrIdSnSet = new HashMap<String, Set<String>>();
               if(null != insideSkuAttrIdsSnDefect && insideSkuAttrIdsSnDefect.size() != 0) {
                   skuAttrIdSnSet = insideSkuAttrIdsSnDefect.get(insideContainerId);  //有货箱情况,唯一sku对应的sn/残次信息
               }else{
                   skuAttrIdSnSet = locskuAttrIdsSnDefect.get(locationId);
               }
               if(null == tipScanSkuCmd) {
                   if(null != skuAttrIdSnSet && skuAttrIdSnSet.size() != 0){
                       Set<String> skuSnSet = skuAttrIdSnSet.get(skuAttrId);
                       for(String insideSkuSn:skuSnSet){
                           skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuAttrId,insideSkuSn);
                           break;
                       }
                   }
              }else{
                  ArrayDeque<String> scanSkuAttrIds = tipScanSkuCmd.getScanSkuAttrIds();
                  if(null == scanSkuAttrIds || scanSkuAttrIds.size() == 0){
                      if(null != skuAttrIdSnSet && skuAttrIdSnSet.size() != 0){
                          Set<String> skuSnSet = skuAttrIdSnSet.get(skuAttrId);
                          for(String insideSkuSn:skuSnSet){
                              skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuAttrId,insideSkuSn);
                              break;
                          } 
                      }
                  }else{
                      if(null != skuAttrIdSnSet && skuAttrIdSnSet.size() != 0){
                          Set<String> skuSnSet = skuAttrIdSnSet.get(skuAttrId);
                          for(String insideSkuSn:skuSnSet){
                              skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuAttrId,insideSkuSn);
                              if(!scanSkuAttrIds.contains(skuAttrId)){
                                  break;  
                              } 
                              
                          } 
                      }else{
                          if(scanSkuAttrIds.contains(skuAttrId)){
                              return null;
                          }
                      }
                      
                  }
              }
           return skuAttrId;
      }
      /***
       * 唯一sku/sn/残次
       */
      public void cacheSkuAttrId(Long locationId,String skuAttrId,Long insideContainerId){
          ScanTipSkuCacheCommand tipScanSkuCmd = null;
          if(null == insideContainerId){
              tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +locationId.toString());
          }else{
              tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +insideContainerId.toString());
          }
          if(null == tipScanSkuCmd){
              tipScanSkuCmd = new ScanTipSkuCacheCommand();
              ArrayDeque<String> scanSkuAttrIds = new ArrayDeque<String>();
              scanSkuAttrIds.addFirst(skuAttrId);
              tipScanSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
          }else{
              ArrayDeque<String> scanSkuAttrIds = tipScanSkuCmd.getScanSkuAttrIds();
              if(null == scanSkuAttrIds || scanSkuAttrIds.size() == 0) {
                  scanSkuAttrIds = new ArrayDeque<String>();
                  scanSkuAttrIds.addFirst(skuAttrId);
                  tipScanSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
              }else{
                  if(!scanSkuAttrIds.contains(skuAttrId)) {
                      scanSkuAttrIds.addFirst(skuAttrId);
                      tipScanSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
                  }
              }
          }
          if(null == insideContainerId){
              cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +locationId.toString(),tipScanSkuCmd , CacheConstants.CACHE_ONE_DAY);
          }else{
              cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +insideContainerId.toString(),tipScanSkuCmd , CacheConstants.CACHE_ONE_DAY);
          }
          
      }
      
      /**
       * 缓存唯一sku
       * @param locationId
       * @param skuAttrId
       * @param insideContainerId
       */
      public void cacheSkuAttrIdNoSn(Long locationId,String skuAttrId,Long insideContainerId){
          ScanTipSkuCacheCommand tipScanSkuCmd = null;
          if(null == insideContainerId){
              tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +locationId.toString());
          }else{
              tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +insideContainerId.toString());
          }
          if(null == tipScanSkuCmd){
              tipScanSkuCmd = new ScanTipSkuCacheCommand();
              ArrayDeque<String> scanSkuAttrIdsNoSn = new ArrayDeque<String>();
              scanSkuAttrIdsNoSn.addFirst(skuAttrId);
              tipScanSkuCmd.setScanSkuAttrIdsNoSn(scanSkuAttrIdsNoSn);
          }else{
              ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
              if(null == scanSkuAttrIdsNoSn || scanSkuAttrIdsNoSn.size() == 0) {
                  scanSkuAttrIdsNoSn = new ArrayDeque<String>();
                  scanSkuAttrIdsNoSn.addFirst(skuAttrId);
                  tipScanSkuCmd.setScanSkuAttrIdsNoSn(scanSkuAttrIdsNoSn);
              }else{
                  if(!scanSkuAttrIdsNoSn.contains(skuAttrId)) {
                      scanSkuAttrIdsNoSn.addFirst(skuAttrId);
                      tipScanSkuCmd.setScanSkuAttrIdsNoSn(scanSkuAttrIdsNoSn);
                  }
              }
          }
          if(null == insideContainerId){
              cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +locationId.toString(),tipScanSkuCmd , CacheConstants.CACHE_ONE_DAY);
          }else{
              cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +insideContainerId.toString(),tipScanSkuCmd , CacheConstants.CACHE_ONE_DAY);
          }
      }
      
      
      
      /***
       * 
       * @param locationIds(一次作业的所有库位集合)
       * @param locSkuQty( 库位上每个sku总件数(sku不在任何容器内))
       * @param locationId(当前扫描的库位id)
       * @param locSkuIds(库位上所有sku(sku不在任何容器内))
       * @param outerContainerIds(库位上所有外部容器)
       * @param outerContainerCmd(外部容器)
       * @param operationId(作业id)
       * @param insideContainerSkuIdsQty(内部容器每个sku总件数)
       * @param insideContainerSkuIds( 内部容器对应所有sku)
       * @param locInsideContainerIds(库位上所有的内部容器(无外部容器情况))
       * @param insideContainerIds(库位上有外部容器的内部容器)
       * @param insideContainerCmd(扫描的内部容器)
       * @param skuCmd(扫描的sku)
       * @return
       */
      public CheckScanResultCommand pdaPickingyCacheSkuAndCheckContainer(Integer pickingWay,Map<String,Long> latticeSkuQty,Map<String,Long> latticeInsideSkuQty,String operationWay,Long ouId,
                                                                         Map<Long, Set<Long>> locSkuIds,Map<Long, Map<String, Set<String>>>     insideSkuAttrIdsSnDefect, Map<Long, Map<String, Set<String>>>    skuAttrIdsSnDefect,Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds,Map<Long, Map<Long, Map<String, Long>>> locSkuAttrIdsQty,String skuAttrId,Integer scanPattern,List<Long> locationIds, Map<Long, Long> locSkuQty,Long locationId,Set<Long> iSkuIds,Set<Long> outerContainerIds,ContainerCommand outerContainerCmd,Long operationId,Map<Long,Long> insideContainerSkuIdsQty,Map<Long, Set<Long>> insideContainerSkuIds,
                                                                         Set<Long> insideContainerIds,Set<Long> locInsideContainerIds,ContainerCommand insideContainerCmd,WhSkuCommand skuCmd){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingyCacheSkuAndCheckContainer is start");
          CheckScanResultCommand cssrCmd = new CheckScanResultCommand();
          if (null != outerContainerCmd) {  //有托盘的情况(如果货箱提示完毕，直接提示下一个托盘)
              Long insideContainerId = insideContainerCmd.getId();
              Long outerContainerId = outerContainerCmd.getId();
              Map<Long, Map<String, Long>> skuAttrIdsQty =  insideSkuAttrIds.get(insideContainerId);
              // 0.先判断当前内部容器是否在缓存中
              boolean icExists = false; 
              for (Long iId : insideContainerIds) {
                  if (0 == insideContainerId.compareTo(iId)) {
                      icExists = true;
                      break;
                  }
              }
              if (false == icExists) {
                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
              }
              Long skuId = skuCmd.getId();
              for (Long sId : iSkuIds) {
                  if (0 == skuId.compareTo(sId)) {
                      icExists = true;
                      break;
                  }
              }
              if (false == icExists) {
                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
              }
              //判断当前扫描的sku是否在缓存中
              // 1.当前的内部容器是不是提示容器队列的第一个
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
              ArrayDeque<Long> cacheInsideContainerIds = null;
              if (null != cacheContainerCmd) {
                  if(null == cacheContainerCmd.getTipOuterInsideContainerIds() ||  cacheContainerCmd.getTipOuterInsideContainerIds().size() == 0){
                      cacheInsideContainerIds = new ArrayDeque<Long>();
                  }else{
                      cacheInsideContainerIds = cacheContainerCmd.getTipOuterInsideContainerIds().get(outerContainerId);
                  }
              }
              if(null == cacheInsideContainerIds){
                  cacheInsideContainerIds = new ArrayDeque<Long>();
              }
              cacheInsideContainerIds.add(insideContainerId);
//              if (null != cacheInsideContainerIds && !cacheInsideContainerIds.isEmpty()) {
//                  Long value = cacheInsideContainerIds.peekFirst();// 队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(insideContainerId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
              OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
              ArrayDeque<Long> cacheLocaitionIds = null;
              if (null != operLineCacheCmd) {
                  cacheLocaitionIds = operLineCacheCmd.getTipLocationIds();
                  if(null == cacheLocaitionIds) {
                      cacheLocaitionIds = new  ArrayDeque<Long>();
                  }
              }
              if(null == cacheLocaitionIds) {
                  cacheLocaitionIds = new  ArrayDeque<Long>();
              }
              cacheLocaitionIds.add(locationId);
//              if (null != cacheLocaitionIds && !cacheLocaitionIds.isEmpty()) {
//                  Long value = cacheLocaitionIds.peekFirst();// 判断当前库位是否是队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(locationId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
              // 2.当前的外部容器是不是提示外部容器队列中的第一个
              ArrayDeque<Long> cacheOuterContainerIds = null;
              if(null != cacheContainerCmd) {
                  if(null == cacheContainerCmd.getTipLocOuterContainerIds()) {
                      cacheOuterContainerIds = new ArrayDeque<Long>();
                  }else{
                      cacheOuterContainerIds = cacheContainerCmd.getTipLocOuterContainerIds().get(locationId);
                  }
              }
              if(null == cacheOuterContainerIds){
                  cacheOuterContainerIds = new ArrayDeque<Long>();
              }
              cacheOuterContainerIds.add(outerContainerId);
//              if (null != cacheOuterContainerIds && !cacheOuterContainerIds.isEmpty()) {
//                  Long value = cacheOuterContainerIds.peekFirst();// 队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(outerContainerId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
              Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
              Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
              boolean isSnLine = false;
              if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
                  isSnLine = true;  //有sn/残次信息
              }else{
                  isSnLine = false; //没有sn/残次信息
              }
              // 3.得到当前内部容器的所有商品并复核商品
              Double skuQty = skuCmd.getScanSkuQty();
              Set<Long> icSkusIds = insideContainerSkuIds.get(insideContainerId);
              boolean skuExists = false;
              for (Long sId : icSkusIds) {
                  if (0 == skuId.compareTo(sId)) {
                       skuExists = true;
                       Long icSkuQty  = 0L;
                       if(Constants.PICKING_INVENTORY.equals(operationWay) && (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO)){ //小车+小车出库箱
                               icSkuQty = latticeInsideSkuQty.get(skuAttrId);
                       }else{
                           Map<String, Long> skuAttrQty = skuAttrIdsQty.get(skuId);
                           icSkuQty = skuAttrQty.get(skuAttrId);
                       }
                       if(true== isSnLine) {
                          long snCount =  cacheManager.incr(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                          if(snCount < skuQty) {
                              ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                              if(null == tipScanSkuCmd) {
                                   tipScanSkuCmd = new ScanTipSkuCacheCommand();
                              }
                              // 继续复核
                              String tipSkuAttrId = null;
                              if (false == isSnLine){ //没有sn/残次
                                  tipSkuAttrId = skuAttrId;
                              }else{  //存在sn/残次信息
                                  Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                  Set<String> snDefects = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                  ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                  if(null == skuAttrIdsSn){
                                      skuAttrIdsSn = new ArrayDeque<String>();
                                  }
                                  for(String snDe:snDefects) {
                                      String tipSkuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(skuAttrId,snDe);
                                      if(skuAttrIdsSn.contains(tipSkuAttrIdSnDefect)) {
                                          continue;
                                      }else{
                                          tipSkuAttrId =tipSkuAttrIdSnDefect;
                                          break;
                                      }
                                  }
                              }
                              cssrCmd.setIsNeedScanSkuSn(true);
                              cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                              return cssrCmd;
                          }
                       }
                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                          ArrayDeque<Long> oneByOneScanSkuIds = null;   //已经扫描的sku队列
                          if (null != tipScanSkuCmd) {
                              oneByOneScanSkuIds = tipScanSkuCmd.getOneByOneScanSkuIds();
                          }
                          if (null != oneByOneScanSkuIds && !oneByOneScanSkuIds.isEmpty()) {
                              boolean isExists = false;
                              Iterator<Long> iter = oneByOneScanSkuIds.iterator();
                              while (iter.hasNext()) {
                                  Long value = iter.next();
                                  if (null == value) value = -1L;
                                  if (0 == skuId.compareTo(new Long(value))) {
                                      isExists = true;  //判断当前sku是否已经扫描
                                      break;
                                  }
                              }
                              long value = 0L;
                              if (false == isExists) {
                                  oneByOneScanSkuIds.addFirst(skuId);// 先加入逐件扫描的队列
                                  tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneScanSkuIds);
                              } else {
                                  // 取到扫描的数量
                                  String cacheValue = cacheManager.getValue(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString());
                                  if (!StringUtils.isEmpty(cacheValue)) {
                                      value = new Long(cacheValue).longValue();
                                  }
                              }
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), skuQty.intValue());
                              String tipSkuAttrId = null;
                              if (cacheValue == icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){
                                      //先判断同一个货箱要拣货的数量是否放在多个货格内
                                      Long insideSkuQty = insideContainerSkuIdsQty.get(skuId);
                                      if(insideSkuQty.longValue() > icSkuQty.longValue()) { //当前货箱内统一种唯一sku 没有拣完，还要捡到别的货格中
//                                          ArrayDeque<Integer> latticeList  = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//                                          if(null == latticeList){
//                                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                                          }
//                                          //如果有sn残次信息,去掉sn/残次信息
//                                          Integer lattice = null;
//                                          Set<Integer> latticeNos = insideSkuAttrIdsLattice.get(skuAttrId);
//                                          Iterator<Integer> it = latticeNos.iterator();
//                                          while (it.hasNext()){
//                                              lattice = it.next();
//                                              if(latticeList.contains(lattice)) {
//                                                  continue;
//                                              }else{
//                                                  break;
//                                              }
//                                          }
//                                          //缓存货格号
//                                          latticeList.addFirst(lattice);
//                                          cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(),latticeList, CacheConstants.CACHE_ONE_DAY);
//                                          Long latticeQty = latticeInsideSkuQty.get(skuAttrId);
//                                          cssrCmd.setLatticeQty(latticeQty);
                                          //判断当前sku是否是sn商品
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(skuAttrId);
                                          }
                                          return cssrCmd;
                                      }
                                   }
                                  ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
                                  Map<String, Long> skuAttrIdQty =  skuAttrIdsQty.get(skuId);
                                  Set<String> skuAttrIds = skuAttrIdQty.keySet();
                                  if(isCacheAllExists2(skuAttrIds, scanSkuAttrIdsNoSn)){  //所有唯一sku是否扫描完毕
                                      //同一种sku有不同的库存属性
                                      for(String skuAttr:skuAttrIds){
                                           if(!scanSkuAttrIdsNoSn.contains(skuAttr)) {
                                               tipSkuAttrId = skuAttr;
                                               break;
                                           }
                                      }
                                      if(isSnLine) {//有sn/残次信息
                                          //有sn的
                                          Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                          Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                          ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                          for(String sn:snDefect) {
                                                  String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                  if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                      cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                      cssrCmd.setIsNeedScanSku(true);
                                                  }
                                          }
                                      }else{
                                        cssrCmd.setIsNeedScanSku(true);
                                        cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      }
                                  }else{
                                      ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                      if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                          cacheSkuIds = new ArrayDeque<Long>();
                                      }
                                      cacheSkuIds.addFirst(skuId);
                                      tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                      cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                      if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                          if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                              // 一个托盘拣货完毕,判断是否还有托盘
                                              if(!isCacheAllExists(outerContainerIds,cacheOuterContainerIds)){//托盘还没拣货完毕
                                                  Long tipOcId = null;
                                                  for(Long ocId:outerContainerIds){
                                                      if(!cacheOuterContainerIds.contains(ocId)) {
                                                          tipOcId = ocId;
                                                          break;
                                                      }
                                                  }
                                                  cssrCmd.setTipOuterContainerId(tipOcId);
                                                  cssrCmd.setIsNeedTipOutContainer(true);
                                                  //缓存上一个托盘内最后扫描的一个内部容器
                                                  this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                  //缓存上一个托盘
                                                  this.cacheOuterContainerCode(locationId, outerContainerId);
                                              } else{
                                                   //判断库位上有没有货箱
                                                   Long tipicId = null;
                                                   if(null != locInsideContainerIds){
                                                       for(Long id:locInsideContainerIds) {
                                                              tipicId = id;
                                                              break;
                                                       }
                                                       cssrCmd.setTipiInsideContainerId(tipicId);
                                                       cssrCmd.setIsNeedTipInsideContainer(true);
                                                       cssrCmd.setIsHaveOuterContainer(false);
                                                        //缓存上一个托盘内最后扫描的一个内部容器
                                                       this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                       //缓存上一个托盘
                                                       this.cacheOuterContainerCode(locationId, outerContainerId);
                                                   }else{//判断有没有散装的sku
                                                       //判断库位上是否有直接放的sku商品
                                                       if(null != locSkuIds) {
                                                           Set<Long> skuIds = locSkuIds.get(locationId);
                                                           if(skuIds != null) {
                                                               CheckScanResultCommand cmd =   this.pdaPickingTipSku(null,operationWay,skuIds, operationId, locationId, ouId, null, skuAttrIdsSnDefect, insideSkuAttrIdsSnDefect);
                                                               if(cmd.getIsNeedScanSku()) {//存在散装sku
                                                                   tipSkuAttrId = cmd.getTipSkuAttrId();
                                                                   cssrCmd.setIsNeedScanSku(true);
                                                                   cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                                                   cssrCmd.setIsHaveInsideContainer(false);
                                                                 //缓存上一个托盘内最后扫描的一个内部容器
                                                                   this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                                   //缓存上一个托盘
                                                                   this.cacheOuterContainerCode(locationId, outerContainerId);
                                                               }
                                                           }else{
                                                               cssrCmd.setIsPicking(true); 
                                                           }
                                                        }else{
                                                            cssrCmd.setIsPicking(true); 
                                                        }
                                                       if(cssrCmd.getIsPicking()){
                                                           //获取下一个库位
                                                           Long tipLocId = null;
                                                           for(Long lId:locationIds){
                                                                 if(!cacheLocaitionIds.contains(lId)){
                                                                     tipLocId  = lId;
                                                                 }
                                                           }
                                                           if(null != tipLocId){
                                                                 cssrCmd.setIsNeedTipLoc(true);
                                                                 cssrCmd.setIsPicking(false);
                                                                 cssrCmd.setTipLocationId(tipLocId);
                                                               //缓存上一个托盘内最后扫描的一个内部容器
                                                                 this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                                 //缓存上一个托盘
                                                                 this.cacheOuterContainerCode(locationId, outerContainerId);
                                                           }
                                                       }
                                                   }
                                              }
                                          } else {
                                              //提示下一个内部容器
                                              Long tipiInsideContainerId = null;
                                              for(Long icId:insideContainerIds){
                                                  if(!cacheInsideContainerIds.contains(icId)) {
                                                      tipiInsideContainerId = icId;
                                                      break;
                                                  }
                                              }
                                              cssrCmd.setIsNeedTipInsideContainer(true);
                                              cssrCmd.setTipiInsideContainerId(tipiInsideContainerId);
                                              //缓存上一个内部容器
                                              this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                          }
                                      } else {
                                          // 继续复核(一个货箱内有不同种sku)
                                          Long icsId = null;
                                          for(Long icSkuId:icSkusIds){
                                              if(!cacheSkuIds.contains(icSkuId)) {
                                                  icsId = icSkuId;
                                                  break;
                                              }
                                          }
                                          Map<String, Long> skuAttrIdQty1 =  skuAttrIdsQty.get(icsId);
                                          Set<String> skuAttrIds1 = skuAttrIdQty1.keySet();
                                          for(String skuAttrId1:skuAttrIds1){
                                              if(!cacheSkuIds.contains(skuAttrId1)){
                                                  tipSkuAttrId =  skuAttrId1;  
                                              }
                                          }
                                          cssrCmd.setIsNeedScanSku(true);
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              } 
                                          }else{
                                            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          }
                                      }
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  // 继续复核
                                  cssrCmd.setIsNeedScanSku(true);
                                  if(isSnLine) {
                                      //有sn的
                                      Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                      Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                      ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                      Boolean result = false;
                                      for(String sn:snDefect) {
                                              String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                              if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                  cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  result = true;
                                              }
                                      }
                                      if(!result) {
                                          log.error("tip container is not in cache server error, logId is[{}]", logId);
                                          throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                      }
                                  }else{
                                      cssrCmd.setTipSkuAttrId(skuAttrId);
                                  }
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          } else {
                              ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                              oneByOneCacheSkuIds.addFirst(skuId);
                              tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                              long value = 0L;
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), skuQty.intValue());
                              String tipSkuAttrId = null;
                              if (cacheValue == icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){
                                      //先判断同一个货箱要拣货的数量是否放在多个货格内
                                      Long insideSkuQty = insideContainerSkuIdsQty.get(skuId);
                                      if(insideSkuQty.longValue() > icSkuQty.longValue()) { //当前货箱内统一种唯一sku 没有拣完，还要捡到别的货格中
//                                          ArrayDeque<Integer> latticeList  = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//                                          if(null == latticeList){
//                                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                                          }
//                                          //如果有sn残次信息,去掉sn/残次信息
//                                          Integer lattice = null;
//                                          Set<Integer> latticeNos = insideSkuAttrIdsLattice.get(skuAttrId);
//                                          Iterator<Integer> it = latticeNos.iterator();
//                                          while (it.hasNext()){
//                                              lattice = it.next();
//                                              if(latticeList.contains(lattice)) {
//                                                  continue;
//                                              }else{
//                                                  break;
//                                              }
//                                          }
//                                          //缓存货格号
//                                          latticeList.addFirst(lattice);
//                                          cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(),latticeList, CacheConstants.CACHE_ONE_DAY);
//                                          Long latticeQty = latticeInsideSkuQty.get(skuAttrId);
//                                          cssrCmd.setLatticeQty(latticeQty);
                                          //判断当前sku是否是sn商品
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(skuAttrId);
                                          }
                                          return cssrCmd;
                                      }
                                   }
                                  ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
                                  Map<String, Long> skuAttrIdQty =  skuAttrIdsQty.get(skuId);
                                  Set<String> skuAttrIds = skuAttrIdQty.keySet();
                                  //
                                  if(isCacheAllExists2(skuAttrIds, scanSkuAttrIdsNoSn)){
                                      //同一种sku有不同的库存属性
                                      for(String skuAttr:skuAttrIds){
                                           if(!scanSkuAttrIdsNoSn.contains(skuAttr)) {
                                               tipSkuAttrId = skuAttr;
                                               break;
                                           }
                                      }
                                      // 继续复核
                                      cssrCmd.setIsNeedScanSku(true);
                                      if(isSnLine) {//有sn/残次信息
                                          //有sn的
                                          Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                          Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                          ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                          for(String sn:snDefect) {
                                                  String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                  if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                      cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  }
                                          }
                                      }else{
                                        cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      }
                                  }else{
                                      ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                      cacheSkuIds.addFirst(skuId);
                                      tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                      cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                      if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                          // 全部商品已复核完毕
                                          if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {
                                              // 一个托盘拣货完毕,判断是否还有托盘
                                              if(!isCacheAllExists(outerContainerIds,cacheOuterContainerIds)){//托盘还没拣货完毕
                                                  Long tipOcId = null;
                                                  for(Long ocId:outerContainerIds){
                                                      if(!cacheOuterContainerIds.contains(ocId)) {
                                                          tipOcId = ocId;
                                                          break;
                                                      }
                                                  }
                                                  cssrCmd.setTipOuterContainerId(tipOcId);
                                                  cssrCmd.setIsNeedTipOutContainer(true);
                                                 //缓存上一个托盘内最后扫描的一个内部容器
                                                  this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                  //缓存上一个托盘
                                                  this.cacheOuterContainerCode(locationId, outerContainerId);
                                              } else{
                                                  //判断库位上有没有货箱
                                                  Long tipicId = null;
                                                  if(null != locInsideContainerIds){
                                                      for(Long id:locInsideContainerIds) {
                                                             tipicId = id;
                                                             break;
                                                      }
                                                      cssrCmd.setTipiInsideContainerId(tipicId);
                                                      cssrCmd.setIsNeedTipInsideContainer(true);
                                                      cssrCmd.setIsHaveOuterContainer(true);
                                                      //缓存上一个托盘内最后扫描的一个内部容器
                                                      this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                      //缓存上一个托盘
                                                      this.cacheOuterContainerCode(locationId, outerContainerId);
                                                  }else{//判断有没有散装的sku
                                                      //判断库位上是否有直接放的sku商品
                                                      if(null != locSkuIds) {
                                                          Set<Long> skuIds = locSkuIds.get(locationId);
                                                          if(skuIds != null) {
                                                              CheckScanResultCommand cmd =   this.pdaPickingTipSku(null,operationWay,skuIds, operationId, locationId, ouId, null, skuAttrIdsSnDefect, insideSkuAttrIdsSnDefect);
                                                              if(cmd.getIsNeedScanSku()) {//存在散装sku
                                                                  tipSkuAttrId = cmd.getTipSkuAttrId();
                                                                  cssrCmd.setIsNeedScanSku(true);
                                                                  cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                                                  cssrCmd.setIsHaveInsideContainer(false);
                                                                 //缓存上一个托盘内最后扫描的一个内部容器
                                                                  this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                                  //缓存上一个托盘
                                                                  this.cacheOuterContainerCode(locationId, outerContainerId);
                                                              }
                                                          }else{
                                                              cssrCmd.setIsPicking(true);
                                                          }
                                                       }else{
                                                           cssrCmd.setIsPicking(true);
                                                       }
                                                       if(cssrCmd.getIsPicking()){
                                                          //获取下一个库位
                                                          Long tipLocId = null;
                                                          for(Long lId:locationIds){
                                                                if(!cacheLocaitionIds.contains(lId)){
                                                                    tipLocId  = lId;
                                                                }
                                                          }
                                                          if(null != tipLocId){
                                                                cssrCmd.setIsNeedTipLoc(true);
                                                                cssrCmd.setIsPicking(false);
                                                                cssrCmd.setTipLocationId(tipLocId);
                                                               //缓存上一个托盘内最后扫描的一个内部容器
                                                                this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                                                //缓存上一个托盘
                                                                this.cacheOuterContainerCode(locationId, outerContainerId);
                                                          }
                                                       }
                                                  }
                                              }
                                          } else {
                                              //提示下一个内部容器
                                              Long tipiInsideContainerId = null;
                                              for(Long icId:insideContainerIds){
                                                  if(!cacheInsideContainerIds.contains(icId)) {
                                                      tipiInsideContainerId = icId;
                                                      break;
                                                  }
                                              }
                                              cssrCmd.setIsNeedTipInsideContainer(true);
                                              cssrCmd.setTipiInsideContainerId(tipiInsideContainerId);
                                            //缓存上一个托盘内最后扫描的一个内部容器
                                              this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
                                          }
                                      } else {
                                          // 继续复核(一个货箱内有不同种sku)
                                          Long icsId = null;
                                          for(Long icSkuId:icSkusIds){
                                              if(!cacheSkuIds.contains(icSkuId)) {
                                                  icsId = icSkuId;
                                                  break;
                                              }
                                          }
                                          Map<String, Long> skuAttrIdQty1 =  skuAttrIdsQty.get(icsId);
                                          Set<String> skuAttrIds1 = skuAttrIdQty1.keySet();
                                          for(String skuAttrId1:skuAttrIds1){
                                              if(!cacheSkuIds.contains(skuAttrId1)){
                                                  tipSkuAttrId =  skuAttrId1;  
                                              }
                                          }
                                          cssrCmd.setIsNeedScanSku(true);
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              } 
                                          }else{
                                            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          }
                                      }
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  // 继续复核
                                  cssrCmd.setIsNeedScanSku(true);
                                  if(isSnLine) {//有sn/残次信息
                                      //有sn的
                                      Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                      Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                      ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                      Boolean result = false;
                                      for(String sn:snDefect) {
                                              String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                              if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                  cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  result = true;
                                              }
                                      }
                                      if(!result) {
                                          log.error("tip container is not in cache server error, logId is[{}]", logId);
                                          throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                      }
                                  }else{
                                    cssrCmd.setTipSkuAttrId(skuAttrId);
                                  }
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          }
                  }
              }
              if (false == skuExists) {
                  log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
              }
          } else if(null == outerContainerCmd && null != insideContainerCmd){
              cssrCmd.setIsHaveOuterContainer(false);
              Long insideContainerId = insideContainerCmd.getId();
              Map<Long, Map<String, Long>> skuAttrIdsQty =  insideSkuAttrIds.get(insideContainerId);
              // 0.先判断当前内部容器是否在缓存中(是否在当前库位上)
              boolean icExists = false;
              for (Long iId : locInsideContainerIds) {
                  if (0 == insideContainerId.compareTo(iId)) {
                      icExists = true;
                      break;
                  }
              }
              if (false == icExists) {
                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
              }
              Long skuId = skuCmd.getId();
              for (Long sId : iSkuIds) {
                  if (0 == skuId.compareTo(sId)) {
                      icExists = true;
                      break;
                  }
              }
              if (false == icExists) {
                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
              }
              OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
              ArrayDeque<Long> cacheLocaitionIds = null;
              if (null != operLineCacheCmd) {
                  cacheLocaitionIds = operLineCacheCmd.getTipLocationIds();
              }
              if(null == cacheLocaitionIds) {
                  cacheLocaitionIds = new ArrayDeque<Long>();
              }
              cacheLocaitionIds.add(locationId);
//              if (null != cacheLocaitionIds && !cacheLocaitionIds.isEmpty()) {
//                  Long value = cacheLocaitionIds.peekFirst();// 判断当前库位是否是队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(locationId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
             // 1.当前的内部容器是不是提示容器队列的第一个
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject( CacheConstants.CACHE_LOCATION  + locationId.toString());
              ArrayDeque<Long> cacheInsideContainerIds = null;
              if (null != cacheContainerCmd) {
                  if(null == cacheContainerCmd.getTipLocInsideContainerIds()){
                      cacheInsideContainerIds = new ArrayDeque<Long>();
                  }else{
                      cacheInsideContainerIds = cacheContainerCmd.getTipLocInsideContainerIds().get(locationId);
                  }
              }
              if(null == cacheInsideContainerIds){
                  cacheInsideContainerIds = new ArrayDeque<Long>();
              }
              cacheInsideContainerIds.add(insideContainerId);
//              if (null != cacheInsideContainerIds && !cacheInsideContainerIds.isEmpty()) {
//                  Long value = cacheInsideContainerIds.peekFirst();// 队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(insideContainerId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
              Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
              Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
              boolean isSnLine = false;
              if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
                  isSnLine = true;  //有sn/残次信息
              }else{
                  isSnLine = false; //没有sn/残次信息
              }
              // 2.得到当前内部容器的所有商品并复核商品
              Double skuQty = skuCmd.getScanSkuQty();
              boolean skuExists = false;
              for (Long sId : iSkuIds) {
                  if (0 == skuId.compareTo(sId)) {
                      skuExists = true;
                      Long icSkuQty = null;
                      if(Constants.PICKING_INVENTORY.equals(operationWay) && (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO)){ //小车+小车出库箱
                              icSkuQty = latticeInsideSkuQty.get(skuAttrId);
                      }else{
                          Map<String, Long> skuAttrQty = skuAttrIdsQty.get(skuId);
                          icSkuQty = skuAttrQty.get(skuAttrId);
                      }
                      if(true== isSnLine) {
                          long snCount =  cacheManager.incr(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                          if(snCount < skuQty) {
                              ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                              if(null == tipScanSkuCmd) {
                                   tipScanSkuCmd = new ScanTipSkuCacheCommand();
                              }
                              // 继续复核
                              String tipSkuAttrId = null;
                              if (false == isSnLine){ //没有sn/残次
                                  tipSkuAttrId = skuAttrId;
                              }else{  //存在sn/残次信息
                                  Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                  Set<String> snDefects = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                  ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                  if(null == skuAttrIdsSn){
                                      skuAttrIdsSn = new ArrayDeque<String>();
                                  }
                                  for(String snDe:snDefects) {
                                      String tipSkuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(skuAttrId,snDe);
                                      if(skuAttrIdsSn.contains(tipSkuAttrIdSnDefect)) {
                                          continue;
                                      }else{
                                          tipSkuAttrId =tipSkuAttrIdSnDefect;
                                          break;
                                      }
                                  }
                              }
                              cssrCmd.setIsNeedScanSkuSn(true);
                              cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                              return cssrCmd;
                          }
                      }
//                      if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {  //逐件扫描
                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                          ArrayDeque<Long> oneByOneScanSkuIds = null;
                          if (null != tipScanSkuCmd) {
                              oneByOneScanSkuIds = tipScanSkuCmd.getOneByOneScanSkuIds();
                          }else{
                              log.error("tip container is not in cache server error, logId is[{}]", logId);
                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                          }
                          if (null != oneByOneScanSkuIds && !oneByOneScanSkuIds.isEmpty()) {
                              boolean isExists = false;
                              Iterator<Long> iter = oneByOneScanSkuIds.iterator();
                              while (iter.hasNext()) {
                                  Long value = iter.next();
                                  if (null == value) value = -1L;
                                  if (0 == skuId.compareTo(new Long(value))) {
                                      isExists = true;
                                      break;
                                  }
                              }
                              long value = 0L;
                              if (false == isExists) {
                                  oneByOneScanSkuIds.addFirst(skuId);// 先加入逐件扫描的队列
                                  tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneScanSkuIds);
                              } else {
                                  // 取到扫描的数量
                                  String cacheValue = cacheManager.getValue(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString());
                                  if (!StringUtils.isEmpty(cacheValue)) {
                                      value = new Long(cacheValue).longValue();
                                  }
                              }
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){
                                      //先判断同一个货箱要拣货的数量是否放在多个货格内
                                      Long insideSkuQty = insideContainerSkuIdsQty.get(skuId);
                                      if(insideSkuQty.longValue() > icSkuQty.longValue()) { //当前货箱内统一种唯一sku 没有拣完，还要捡到别的货格中
//                                          ArrayDeque<Integer> latticeList  = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//                                          if(null == latticeList){
//                                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                                          }
//                                          //如果有sn残次信息,去掉sn/残次信息
//                                          Integer lattice = null;
//                                          Set<Integer> latticeNos = insideSkuAttrIdsLattice.get(skuAttrId);
//                                          Iterator<Integer> it = latticeNos.iterator();
//                                          while (it.hasNext()){
//                                              lattice = it.next();
//                                              if(latticeList.contains(lattice)) {
//                                                  continue;
//                                              }else{
//                                                  break;
//                                              }
//                                          }
//                                          //缓存货格号
//                                          latticeList.addFirst(lattice);
//                                          cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(),latticeList, CacheConstants.CACHE_ONE_DAY);
//                                          Long latticeQty = latticeInsideSkuQty.get(skuAttrId);
//                                          cssrCmd.setLatticeQty(latticeQty);
                                          //判断当前sku是否是sn商品
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(skuAttrId);
                                          }
                                          return cssrCmd;
                                      }
                                   }
                                  ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
                                  Map<String, Long> skuAttrIdQty =  skuAttrIdsQty.get(skuId);
                                  Set<String> skuAttrIds = skuAttrIdQty.keySet();
                                  String tipSkuAttrId = null;
                                  if(isCacheAllExists2(skuAttrIds,scanSkuAttrIdsNoSn)){
                                      //同一种sku有不同的库存属性
                                      for(String skuAttr:skuAttrIds){
                                           if(!scanSkuAttrIdsNoSn.contains(skuAttr)) {
                                               tipSkuAttrId = skuAttr;
                                               break;
                                           }
                                      }
                                      // 继续复核
                                      cssrCmd.setIsNeedScanSku(true);
//                                      cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      if(isSnLine) {//有sn/残次信息
                                          //有sn的
                                          Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                          Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                          ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                          for(String sn:snDefect) {
                                                  String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                  if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                      cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  }
                                          }
                                      }else{
                                        cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      }
                                  }else{
                                      ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                      if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                          cacheSkuIds = new ArrayDeque<Long>();
                                      }
                                      cacheSkuIds.addFirst(skuId);
                                      tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                      cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                      if (isCacheAllExists(iSkuIds, cacheSkuIds)) {
                                          //判断库位上是否还有货箱没有扫描
                                          if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                              if(null != locSkuIds) {
                                                  //判断库位上是否有直接放的sku商品
                                                  Set<Long> skuIds = locSkuIds.get(locationId);
                                                  if(skuIds != null) {
                                                      CheckScanResultCommand cmd =   this.pdaPickingTipSku(null,operationWay,skuIds, operationId, locationId, ouId, null, skuAttrIdsSnDefect, insideSkuAttrIdsSnDefect);
                                                      if(cmd.getIsNeedScanSku()) {//存在散装sku
                                                          tipSkuAttrId = cmd.getTipSkuAttrId();
                                                          cssrCmd.setIsNeedScanSku(true);
                                                          cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                                          cssrCmd.setIsHaveInsideContainer(false);
                                                        //缓存上一个托盘内最后扫描的一个内部容器
                                                          this.cacheInsideContainerCode(locationId, insideContainerId, null);
                                                      }
                                                  }else{
                                                      cssrCmd.setIsPicking(true);
                                                  }
                                                  
                                              }else{
                                                  cssrCmd.setIsPicking(true);
                                              }
                                              if(cssrCmd.getIsPicking()){
                                                  //获取下一个库位
                                                  Long tipLocId = null;
                                                  for(Long lId:locationIds){
                                                        if(!cacheLocaitionIds.contains(lId)){
                                                            tipLocId  = lId;
                                                        }
                                                  }
                                                  if(null != tipLocId){
                                                        cssrCmd.setIsNeedTipLoc(true);
                                                        cssrCmd.setIsPicking(false);
                                                        cssrCmd.setTipLocationId(tipLocId);
                                                        //缓存上一个托盘内最后扫描的一个内部容器
                                                        this.cacheInsideContainerCode(locationId, insideContainerId, null);
                                                  }
                                              }
                                          } else {
                                              //提示下一个货箱
                                              Long icId = null;
                                              for(Long insideId:insideContainerIds) {
                                                  if(!cacheInsideContainerIds.contains(insideId)){
                                                      icId = insideId;
                                                      break;
                                                  }
                                              }
                                              cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                              cssrCmd.setTipiInsideContainerId(icId);
                                              //缓存上一个托盘内最后扫描的一个内部容器
                                              this.cacheInsideContainerCode(locationId, insideContainerId, null);
                                          }
                                      } else {
                                         // 继续复核(一个货箱内有不同种sku)
                                          Long icsId = null;
                                          for(Long icSkuId:iSkuIds){
                                              if(!cacheSkuIds.contains(icSkuId)) {
                                                  icsId = icSkuId;
                                                  break;
                                              }
                                          }
                                          Map<String, Long> skuAttrIdQty1 =  skuAttrIdsQty.get(icsId);
                                          Set<String> skuAttrIds1 = skuAttrIdQty1.keySet();
                                          for(String skuAttrId1:skuAttrIds1){
                                              if(!cacheSkuIds.contains(skuAttrId1)){
                                                  tipSkuAttrId =  skuAttrId1;  
                                              }
                                          }
                                          cssrCmd.setIsNeedScanSku(true);
//                                          cssrCmd.setTipSkuAttrId(skuAttrId);
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                          break;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          }
                                      }
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  // 继续复核
                                  cssrCmd.setIsNeedScanSku(true);
                                  cssrCmd.setTipSkuAttrId(skuAttrId);
                                  if(isSnLine) {//有sn/残次信息
                                      //有sn的
                                      Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                      Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                      ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                      Boolean result = false;
                                      for(String sn:snDefect) {
                                              String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                              if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                  cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  result = true;
                                                  break;
                                              }
                                      }
                                      if(!result) {
                                          log.error("tip container is not in cache server error, logId is[{}]", logId);
                                          throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                      }
                                  }else{
                                    cssrCmd.setTipSkuAttrId(skuAttrId);
                                  }
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          } else {  //缓存skuId队列为空
                              ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                              oneByOneCacheSkuIds.addFirst(skuId);
                              tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                              long value = 0L;
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){
                                      //先判断同一个货箱要拣货的数量是否放在多个货格内
                                      Long insideSkuQty = insideContainerSkuIdsQty.get(skuId);
                                      if(insideSkuQty.longValue() > icSkuQty.longValue()) { //当前货箱内统一种唯一sku 没有拣完，还要捡到别的货格中
//                                          ArrayDeque<Integer> latticeList  = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//                                          if(null == latticeList){
//                                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                                          }
//                                          //如果有sn残次信息,去掉sn/残次信息
//                                          Integer lattice = null;
//                                          Set<Integer> latticeNos = insideSkuAttrIdsLattice.get(skuAttrId);
//                                          Iterator<Integer> it = latticeNos.iterator();
//                                          while (it.hasNext()){
//                                              lattice = it.next();
//                                              if(latticeList.contains(lattice)) {
//                                                  continue;
//                                              }else{
//                                                  break;
//                                              }
//                                          }
//                                          //缓存货格号
//                                          latticeList.addFirst(lattice);
//                                          cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(),latticeList, CacheConstants.CACHE_ONE_DAY);
//                                          Long latticeQty = latticeInsideSkuQty.get(skuAttrId);
//                                          cssrCmd.setLatticeQty(latticeQty);
                                          //判断当前sku是否是sn商品
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(skuAttrId);
                                          }
                                          return cssrCmd;
                                      }
                                   }
                                  ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
                                  Map<String, Long> skuAttrIdQty =  skuAttrIdsQty.get(skuId);
                                  Set<String> skuAttrIds = skuAttrIdQty.keySet();
                                  String tipSkuAttrId = null;
                                  if(isCacheAllExists2(skuAttrIds, scanSkuAttrIdsNoSn)){
                                      //同一种sku有不同的库存属性
                                      for(String skuAttr:skuAttrIds){
                                           if(!scanSkuAttrIdsNoSn.contains(skuAttr)) {
                                               tipSkuAttrId = skuAttr;
                                               break;
                                           }
                                      }
                                      // 继续复核
                                      cssrCmd.setIsNeedScanSku(true);
                                      if(isSnLine) {//有sn/残次信息
                                          //有sn的
                                          Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                          Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                          ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                          for(String sn:snDefect) {
                                                  String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                  if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                      cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  }
                                          }
                                      }else{
                                        cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      }
                                  }else{
                                      ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                      cacheSkuIds.addFirst(skuId);
                                      tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                      cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                      if (isCacheAllExists(iSkuIds, cacheSkuIds)) {
                                          //判断库位上是否还有货箱没有扫描
                                          if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                                //判断库位上是否有直接放的sku商品
                                              if(null != locSkuIds) {
                                                  Set<Long> skuIds = locSkuIds.get(locationId);
                                                  if(skuIds != null) {
                                                      CheckScanResultCommand cmd =   this.pdaPickingTipSku(null,operationWay,skuIds, operationId, locationId, ouId, null, skuAttrIdsSnDefect, insideSkuAttrIdsSnDefect);
                                                      if(cmd.getIsNeedScanSku()) {//存在散装sku
                                                          tipSkuAttrId = cmd.getTipSkuAttrId();
                                                          cssrCmd.setIsNeedScanSku(true);
                                                          cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                                          cssrCmd.setIsHaveInsideContainer(false);
                                                          //缓存上一个托盘内最后扫描的一个内部容器
                                                          this.cacheInsideContainerCode(locationId, insideContainerId, null);
                                                      }
                                                  }else{
                                                      cssrCmd.setIsPicking(true);
                                                  }
                                                  
                                              }else{
                                                  cssrCmd.setIsPicking(true);
                                              }
                                              if(cssrCmd.getIsPicking()){
                                                  //获取下一个库位
                                                  Long tipLocId = null;
                                                  for(Long lId:locationIds){
                                                        if(!cacheLocaitionIds.contains(lId)){
                                                            tipLocId  = lId;
                                                        }
                                                  }
                                                  if(null != tipLocId){
                                                        cssrCmd.setIsNeedTipLoc(true);
                                                        cssrCmd.setIsPicking(false);
                                                        cssrCmd.setTipLocationId(tipLocId);
                                                        //缓存上一个托盘内最后扫描的一个内部容器
                                                        this.cacheInsideContainerCode(locationId, insideContainerId, null);
                                                  }
                                              }
                                          } else {
                                              //提示下一个货箱
                                              Long icId = null;
                                              for(Long insideId:insideContainerIds) {
                                                  if(!cacheInsideContainerIds.contains(insideId)){
                                                      icId = insideId;
                                                      break;
                                                  }
                                              }
                                              cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                              cssrCmd.setTipiInsideContainerId(icId);
                                              //缓存上一个托盘内最后扫描的一个内部容器
                                              this.cacheInsideContainerCode(locationId, insideContainerId, null);
                                          }
                                      } else {
                                          // 继续复核(一个货箱内有不同种sku)
                                          Long icsId = null;
                                          for(Long icSkuId:iSkuIds){
                                              if(!cacheSkuIds.contains(icSkuId)) {
                                                  icsId = icSkuId;
                                                  break;
                                              }
                                          }
                                          Map<String, Long> skuAttrIdQty1 =  skuAttrIdsQty.get(icsId);
                                          Set<String> skuAttrIds1 = skuAttrIdQty1.keySet();
                                          for(String skuAttrId1:skuAttrIds1){
                                              if(!cacheSkuIds.contains(skuAttrId1)){
                                                  tipSkuAttrId =  skuAttrId1;  
                                              }
                                          }
                                          cssrCmd.setIsNeedScanSku(true);
//                                          cssrCmd.setTipSkuAttrId( tipSkuAttrId);
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                          break;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          }
                                      }
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + skuId.toString());
                                  // 继续复核
                                  cssrCmd.setIsNeedScanSku(true);
//                                  cssrCmd.setTipSkuAttrId(skuAttrId);
                                  if(isSnLine) {//有sn/残次信息
                                      //有sn的
                                      Map<String, Set<String>> skuAttrIdSnDefect = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                      Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                      ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                      Boolean result = false;
                                      for(String sn:snDefect) {
                                              String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                              if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                  cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  result = true;
                                                  break;
                                              }
                                      }
                                      if(!result) {
                                          log.error("tip container is not in cache server error, logId is[{}]", logId);
                                          throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                      }
                                  }else{
                                    cssrCmd.setTipSkuAttrId(skuAttrId);
                                  }
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          }
                  }
              }
              if (false == skuExists) {
                  log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
              }
          }else if(null == outerContainerCmd && null == insideContainerCmd){//  sku直接放在库位上
              cssrCmd.setIsHaveInsideContainer(false);
              Long skuId = skuCmd.getId();
              Set<Long> skuIds = locSkuIds.get(locationId);
              // 0.判断sku是否在缓存中
              boolean icExists = false; 
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
              Map<Long, Map<String, Long>> skuAttrIdsQty =  locSkuAttrIdsQty.get(locationId); //库位上每个sku对应的唯一sku及件数 (不在容器内，散装sku)
              Map<String, Set<String>>  skuSnDefect = skuAttrIdsSnDefect.get(locationId);   //库位上每个唯一sku对应的所有sn及残次条码
              OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
              ArrayDeque<Long> cacheLocaitionIds = null;
              if (null != operLineCacheCmd) {
                  cacheLocaitionIds = operLineCacheCmd.getTipLocationIds();
              }
              if(null == cacheLocaitionIds) {
                  cacheLocaitionIds = new ArrayDeque<Long>();
              }
              cacheLocaitionIds.add(locationId);
//              if (null != cacheLocaitionIds && !cacheLocaitionIds.isEmpty()) {
//                  Long value = cacheLocaitionIds.peekFirst();// 判断当前库位是否是队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(locationId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
              Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
              Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
              boolean isSnLine = false;
              if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
                  isSnLine = true;  //有sn/残次信息
              }else{
                  isSnLine = false; //没有sn/残次信息
              }
              Double skuQty = skuCmd.getScanSkuQty();
              boolean skuExists = false;
              for (Long sId : skuIds) {
                  if (0 == skuId.compareTo(sId)) {
                      skuExists = true;
                      Long icSkuQty = null;
                      if(Constants.PICKING_INVENTORY.equals(operationWay) && (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO)){ //小车+小车出库箱
                              icSkuQty = latticeSkuQty.get(skuAttrId);
                      }else{
                          Map<String, Long> skuAttrQty = skuAttrIdsQty.get(sId);
                          icSkuQty = skuAttrQty.get(skuAttrId);
                      }
                      if(true== isSnLine) {
                          long snCount =  cacheManager.incr(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + skuId.toString());
                          if(snCount < skuQty) {
                              ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                              if(null == tipScanSkuCmd) {
                                   tipScanSkuCmd = new ScanTipSkuCacheCommand();
                              }
                              // 继续复核
                              String tipSkuAttrId = null;
                              if (false == isSnLine){ //没有sn/残次
                                  tipSkuAttrId = skuAttrId;
                              }else{  //存在sn/残次信息
                                  Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                  Set<String> snDefects = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                  ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                  if(null == skuAttrIdsSn){
                                      skuAttrIdsSn = new ArrayDeque<String>();
                                  }
                                  for(String snDe:snDefects) {
                                      String tipSkuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(skuAttrId,snDe);
                                      if(skuAttrIdsSn.contains(tipSkuAttrIdSnDefect)) {
                                          continue;
                                      }else{
                                          tipSkuAttrId =tipSkuAttrIdSnDefect;
                                          break;
                                      }
                                  }
                              }
                              cssrCmd.setIsNeedScanSkuSn(true);
                              cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                              return cssrCmd;
                          }
                      }
//                      if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {  //逐件扫描
                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE +locationId.toString());
                          ArrayDeque<Long> oneByOneScanSkuIds = null;
                          if (null != tipScanSkuCmd) {
                              oneByOneScanSkuIds = tipScanSkuCmd.getOneByOneScanSkuIds();
                          }else{
                              log.error("tip container is not in cache server error, logId is[{}]", logId);
                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                          }
                          if (null != oneByOneScanSkuIds && !oneByOneScanSkuIds.isEmpty()) {
                              boolean isExists = false;
                              Iterator<Long> iter = oneByOneScanSkuIds.iterator();
                              while (iter.hasNext()) {
                                  Long value = iter.next();
                                  if (null == value) value = -1L;
                                  if (0 == skuId.compareTo(new Long(value))) {
                                      isExists = true;
                                      break;
                                  }
                              }
                              long value = 0L;
                              if (false == isExists) {
                                  oneByOneScanSkuIds.addFirst(skuId);// 先加入逐件扫描的队列
                                  tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneScanSkuIds);
                              } else {
                                  // 取到扫描的数量
                                  String cacheValue = cacheManager.getValue(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString() + skuId.toString());
                                  if (!StringUtils.isEmpty(cacheValue)) {
                                      value = new Long(cacheValue).longValue();
                                  }
                              }
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + skuId.toString());
                                  if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){
                                      //先判断同一个货箱要拣货的数量是否放在多个货格内
                                      Long lskuQty = locSkuQty.get(skuId);
                                      if(lskuQty.longValue() > icSkuQty.longValue()) { //当前库位上同一种唯一sku 没有拣完，还要捡到别的货格中
//                                          ArrayDeque<Integer> latticeList  = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//                                          if(null == latticeList){
//                                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                                          }
//                                          //如果有sn残次信息,去掉sn/残次信息
//                                          Integer lattice = null;
//                                          Set<Integer> latticeNos = skuAttrIdsLattice.get(skuAttrId);
//                                          Iterator<Integer> it = latticeNos.iterator();
//                                          while (it.hasNext()){
//                                              lattice = it.next();
//                                              if(latticeList.contains(lattice)) {
//                                                  continue;
//                                              }else{
//                                                  break;
//                                              }
//                                          }
//                                          //缓存货格号
//                                          latticeList.addFirst(lattice);
//                                          cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(),latticeList, CacheConstants.CACHE_ONE_DAY);
//                                          Long latticeQty = latticeInsideSkuQty.get(skuAttrId);
//                                          cssrCmd.setLatticeQty(latticeQty);
                                          //判断当前sku是否是sn商品
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                          break;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(skuAttrId);
                                          }
                                          return cssrCmd;
                                      }
                                   }
                                  //先判断同一种sku是否有不同唯一sku属性
//                                  ArrayDeque<String> scanSkuAttrIds = tipScanSkuCmd.getScanSkuAttrIds();  //唯一sku/sn及残次信息
                                  ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
                                  Map<String, Long> skuAttrIdQty =  skuAttrIdsQty.get(skuId);  //唯一sku对应的sku数量
                                  Set<String> skuAttrIds = skuAttrIdQty.keySet();
                                  String tipSkuAttrId = null;
                                  if(isCacheAllExists2(skuAttrIds, scanSkuAttrIdsNoSn)){
                                      //同一种sku有不同的库存属性
                                      for(String skuAttr:skuAttrIds){
                                          Set<String> snDefectSet = skuSnDefect.get(skuAttr);
                                          //随机取库存属性
                                          for(String sn:snDefectSet){
                                             String skuAttrIdSn =  SkuCategoryProvider.concatSkuAttrId(skuAttr,sn);
                                             if(!scanSkuAttrIdsNoSn.contains(skuAttrIdSn)) {
                                                 tipSkuAttrId = skuAttrIdSn;
                                                 break;
                                             }
                                          }
                                          break;
                                      }
                                      // 继续复核
                                      cssrCmd.setIsNeedScanSku(true);
//                                      cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      if(isSnLine) {//有sn/残次信息
                                          //有sn的
                                          Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                          Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                          ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                          for(String sn:snDefect) {
                                                  String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                  if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                      cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  }
                                          }
                                      }else{
                                        cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      }
                                  }else{
                                      ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                      if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                          cacheSkuIds = new ArrayDeque<Long>();
                                      }
                                      cacheSkuIds.addFirst(skuId);
                                      tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                      cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                      if (isCacheAllExists(skuIds, cacheSkuIds)) {  //同一个库位不同种类的sku
                                          if (isCacheAllExists2(locationIds, cacheLocaitionIds)) {  //返回true ,两者相同
                                              //判断库位上是否有直接放的sku商品
                                              cssrCmd.setIsPicking(true);
                                          } else {
                                              //获取下一个库位
                                              Long tipLocId = null;
                                              for(Long lId:locationIds){
                                                  if(!cacheLocaitionIds.contains(lId)){
                                                      tipLocId  = lId;
                                                  }
                                              }
                                              if(null != tipLocId){
                                                  cssrCmd.setIsNeedTipLoc(true);
                                                  cssrCmd.setTipLocationId(tipLocId);
                                              }else{
                                                  cssrCmd.setIsPicking(true);
                                              }
                                          }
                                      } else {
                                          // 继续复核,提示下一种商品
                                          Long tsId = null;
                                          for(Long id:skuIds) {
                                                if(!cacheSkuIds.contains(id)){
                                                    tsId = id;
                                                    break;
                                                }
                                          }
                                          Map<String, Long> tipSkuAttrIdQty =  skuAttrIdsQty.get(tsId);
                                          Set<String> tipSkuAttrIds = tipSkuAttrIdQty.keySet();
                                          for(String tipAttrId:tipSkuAttrIds){
                                              Set<String> snDefectSet = skuSnDefect.get(tipAttrId);
                                              //随机取库存属性
                                              for(String sn:snDefectSet){
                                                 String skuAttrIdSn =  SkuCategoryProvider.concatSkuAttrId(tipAttrId,sn);
                                                 ArrayDeque<String> scanSkuAttrIds = tipScanSkuCmd.getScanSkuAttrIds();  //唯一sku/sn及残次信息
                                                 if(!scanSkuAttrIds.contains(skuAttrIdSn)) {
                                                     tipSkuAttrId = skuAttrIdSn;
                                                     break;
                                                 }
                                              }
                                              break;
                                          }
                                          //获取唯一的sku
                                          cssrCmd.setIsNeedScanSku(true);
//                                          cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                          break;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          }
                                      }
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + skuId.toString());
                                  // 继续复核
                                  cssrCmd.setIsNeedScanSku(true);
//                                  cssrCmd.setTipSkuAttrId(skuAttrId);
                                  if(isSnLine) {//有sn/残次信息
                                      //有sn的
                                      Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                      Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                      ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                      Boolean result = false;
                                      for(String sn:snDefect) {
                                              String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                              if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                  cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  result = true;
                                                  break;
                                              }
                                      }
                                      if(!result) {
                                          log.error("tip container is not in cache server error, logId is[{}]", logId);
                                          throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                      }
                                  }else{
                                    cssrCmd.setTipSkuAttrId(skuAttrId);
                                  }
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          } else {  //缓存skuId队列为空
                              ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                              oneByOneCacheSkuIds.addFirst(skuId);
                              tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                              long value = 0L;
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + skuId.toString());
                                  if(pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO){
                                      //先判断同一个货箱要拣货的数量是否放在多个货格内
                                      Long lskuQty = locSkuQty.get(skuId);
                                      if(lskuQty.longValue() > icSkuQty.longValue()) { //当前库位上同一种唯一sku 没有拣完，还要捡到别的货格中
//                                          ArrayDeque<Integer> latticeList  = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//                                          if(null == latticeList){
//                                              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                                          }
//                                          //如果有sn残次信息,去掉sn/残次信息
//                                          Integer lattice = null;
//                                          Set<Integer> latticeNos = skuAttrIdsLattice.get(skuAttrId);
//                                          Iterator<Integer> it = latticeNos.iterator();
//                                          while (it.hasNext()){
//                                              lattice = it.next();
//                                              if(latticeList.contains(lattice)) {
//                                                  continue;
//                                              }else{
//                                                  break;
//                                              }
//                                          }
//                                          //缓存货格号
//                                          latticeList.addFirst(lattice);
//                                          cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(),latticeList, CacheConstants.CACHE_ONE_DAY);
//                                          Long latticeQty = latticeInsideSkuQty.get(skuAttrId);
//                                          cssrCmd.setLatticeQty(latticeQty);
                                          //判断当前sku是否是sn商品
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                          break;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(skuAttrId);
                                          }
                                          return cssrCmd;
                                      }
                                   }
                                  ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                  cacheSkuIds.addFirst(skuId);
                                  tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
//                                  ArrayDeque<String> scanSkuAttrIds = tipScanSkuCmd.getScanSkuAttrIds();  //唯一sku/sn及残次信息
                                  ArrayDeque<String> scanSkuAttrIdsNoSn = tipScanSkuCmd.getScanSkuAttrIdsNoSn();
                                  Map<String, Long> skuAttrIdQty =  skuAttrIdsQty.get(skuId);  //唯一sku对应的sku数量
                                  Set<String> skuAttrIds = skuAttrIdQty.keySet();
                                  //同一种sku，不同种库存属性情况
                                  String tipSkuAttrId = null;
                                  if(isCacheAllExists2(skuAttrIds, scanSkuAttrIdsNoSn)){
                                      //同一种sku有不同的库存属性
                                      for(String skuAttr:skuAttrIds){
                                          Set<String> snDefectSet = skuSnDefect.get(skuAttr);
                                          //随机取库存属性
                                          for(String sn:snDefectSet){
                                             String skuAttrIdSn =  SkuCategoryProvider.concatSkuAttrId(skuAttr,sn);
                                             if(!scanSkuAttrIdsNoSn.contains(skuAttrIdSn)) {
                                                 tipSkuAttrId = skuAttrIdSn;
                                                 break;
                                             }
                                          }
                                          break;
                                      }
                                      // 继续复核
                                      cssrCmd.setIsNeedScanSku(true);
//                                      cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      if(isSnLine) {//有sn/残次信息
                                          //有sn的
                                          Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                          Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                          ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                          for(String sn:snDefect) {
                                                  String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                  if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                      cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  }
                                          }
                                      }else{
                                        cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                      }
                                  }else{ //同一个库位上不同种类的sku
                                      if (isCacheAllExists(skuIds, cacheSkuIds)) {
                                          if (isCacheAllExists2(locationIds, cacheLocaitionIds)) {  //返回true ,两者相同
                                              //判断库位上是否有直接放的sku商品
                                              cssrCmd.setIsPicking(true);
                                          } else {
                                              //获取下一个库位
                                              Long tipLocId = null;
                                              for(Long lId:locationIds){
                                                  if(!cacheLocaitionIds.contains(lId)){
                                                      tipLocId  = lId;
                                                  }
                                              }
                                              if(null != tipLocId){
                                                  cssrCmd.setIsNeedTipLoc(true);
                                                  cssrCmd.setTipLocationId(tipLocId);
                                              }else{
                                                  cssrCmd.setIsPicking(true);
                                              }
                                          }
                                      } else {
                                          // 继续复核,提示下一种商品
                                          Long tsId = null;
                                          for(Long id:skuIds) {
                                                if(!cacheSkuIds.contains(id)){
                                                    tsId = id;
                                                    break;
                                                }
                                          }
                                          Map<String, Long> tipSkuAttrIdQty =  skuAttrIdsQty.get(tsId);
                                          Set<String> tipSkuAttrIds = tipSkuAttrIdQty.keySet();
                                          for(String tipAttrId:tipSkuAttrIds){
                                              Set<String> snDefectSet = skuSnDefect.get(tipAttrId);
                                              //随机取库存属性
                                              for(String sn:snDefectSet){
                                                 String skuAttrIdSn =  SkuCategoryProvider.concatSkuAttrId(tipAttrId,sn);
                                                 ArrayDeque<String> scanSkuAttrIds = tipScanSkuCmd.getScanSkuAttrIds();  //唯一sku/sn及残次信息
                                                 if(!scanSkuAttrIds.contains(skuAttrIdSn)) {
                                                     tipSkuAttrId = skuAttrIdSn;
                                                     break;
                                                 }
                                              }
                                              break;
                                          }
                                          //获取唯一的sku
                                          cssrCmd.setIsNeedScanSku(true);
//                                          cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          if(isSnLine) {//有sn/残次信息
                                              //有sn的
                                              Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                              Set<String> snDefect = skuAttrIdSnDefect.get(tipSkuAttrId);  //获取sn/残次信息
                                              ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                              Boolean result = false;
                                              for(String sn:snDefect) {
                                                      String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(tipSkuAttrId,sn);
                                                      if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                          cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                          result = true;
                                                          break;
                                                      }
                                              }
                                              if(!result) {
                                                  log.error("tip container is not in cache server error, logId is[{}]", logId);
                                                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                              }
                                          }else{
                                            cssrCmd.setTipSkuAttrId(tipSkuAttrId);
                                          }
                                      }
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + skuId.toString());
                                  // 继续复核
                                  cssrCmd.setIsNeedScanSku(true);
//                                  cssrCmd.setTipSkuAttrId(skuAttrId);
                                  if(isSnLine) {//有sn/残次信息
                                      //有sn的
                                      Map<String, Set<String>> skuAttrIdSnDefect = skuAttrIdsSnDefect.get(locationId);
                                      Set<String> snDefect = skuAttrIdSnDefect.get(skuAttrId);  //获取sn/残次信息
                                      ArrayDeque<String> skuAttrIdsSn = tipScanSkuCmd.getScanSkuAttrIds();
                                      Boolean result = false;
                                      for(String sn:snDefect) {
                                              String skuAttrIdSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId,sn);
                                              if(!skuAttrIdsSn.contains(skuAttrIdSn)){
                                                  cssrCmd.setTipSkuAttrId(skuAttrIdSn);
                                                  result = true;
                                                  break;
                                              }
                                      }
                                      if(!result) {
                                          log.error("tip container is not in cache server error, logId is[{}]", logId);
                                          throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                      }
                                  }else{
                                    cssrCmd.setTipSkuAttrId(skuAttrId);
                                  }
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                              
                          }
                  }
              }
              if (false == skuExists) {         
                  log.error("scan sku is not found in current location error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", locationId, skuId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {locationId});
              }
          }
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingyCacheSkuAndCheckContainer is end");
          
          return cssrCmd;
      }
      
      /***
       * 缓存托盘
       */
      private void cacheOuterContainerCode(Long locationId, Long outerId) {
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          if (null == tipLocationCmd) {
              tipLocationCmd = new LocationTipCacheCommand();
              Map<Long, ArrayDeque<Long>> tipLocOuterContainerIds = new HashMap<Long, ArrayDeque<Long>>();
              ArrayDeque<Long> tipOuterContainerIds = new ArrayDeque<Long>();
              tipOuterContainerIds.addFirst(outerId);
              tipLocOuterContainerIds.put(locationId, tipOuterContainerIds);
              tipLocationCmd.setTipLocOuterContainerIds(tipLocOuterContainerIds);
          } else {
              Map<Long, ArrayDeque<Long>> tipLocOuterContainerIds = tipLocationCmd.getTipLocOuterContainerIds();
              if (null == tipLocOuterContainerIds || tipLocOuterContainerIds.size() == 0) {
                  tipLocOuterContainerIds = new HashMap<Long, ArrayDeque<Long>>();
                  ArrayDeque<Long> tipOuterContainerIds = new ArrayDeque<Long>();
                  tipOuterContainerIds = new ArrayDeque<Long>();
                  tipOuterContainerIds.addFirst(outerId);
                  tipLocOuterContainerIds.put(locationId, tipOuterContainerIds);
                  tipLocationCmd.setTipLocOuterContainerIds(tipLocOuterContainerIds);
              } else {
                  ArrayDeque<Long> tipOuterContainerIds = tipLocOuterContainerIds.get(locationId);
                  if (tipOuterContainerIds == null || tipOuterContainerIds.size() == 0) {
                      tipOuterContainerIds = new ArrayDeque<Long>();
                      tipOuterContainerIds.addFirst(outerId);
                      tipLocOuterContainerIds.put(locationId, tipOuterContainerIds);
                      tipLocationCmd.setTipLocOuterContainerIds(tipLocOuterContainerIds);
                  } else {
                      if (!tipOuterContainerIds.contains(outerId)) {
                          tipOuterContainerIds.addFirst(outerId);
                          tipLocOuterContainerIds.put(locationId, tipOuterContainerIds);
                          tipLocationCmd.setTipLocOuterContainerIds(tipLocOuterContainerIds);
                      }
                  }
              }
          }
          cacheManager.setObject(CacheConstants.CACHE_LOCATION + locationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
      }
      
      /***
       * 缓存内部容器
       * @param locationId
       * @param insideId
       */
      private void cacheInsideContainerCode(Long locationId, Long insideId, Long outerContainerId) {
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          if (null != outerContainerId) { // 内部容器存在托盘
              if (null == tipLocationCmd) {
                  tipLocationCmd = new LocationTipCacheCommand();
                  Map<Long, ArrayDeque<Long>> tipOuterInsideContainerIds = new HashMap<Long, ArrayDeque<Long>>();
                  ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
                  tipInsideContainerIds.addFirst(insideId);
                  tipOuterInsideContainerIds.put(outerContainerId, tipInsideContainerIds);
                  tipLocationCmd.setTipOuterInsideContainerIds(tipOuterInsideContainerIds);
              } else {
                  Map<Long, ArrayDeque<Long>> tipOuterInsideContainerIds = tipLocationCmd.getTipOuterInsideContainerIds();
                  if (null == tipOuterInsideContainerIds || tipOuterInsideContainerIds.size() == 0) {
                      tipOuterInsideContainerIds = new HashMap<Long, ArrayDeque<Long>>();
                      ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
                      tipInsideContainerIds.addFirst(insideId);
                      tipOuterInsideContainerIds.put(outerContainerId, tipInsideContainerIds);
                      tipLocationCmd.setTipOuterInsideContainerIds(tipOuterInsideContainerIds);
                  } else {
                      ArrayDeque<Long> tipInsideContainerIds = tipOuterInsideContainerIds.get(outerContainerId);
                      if (null == tipInsideContainerIds || tipInsideContainerIds.size() == 0) {
                          tipInsideContainerIds = new ArrayDeque<Long>();
                          tipInsideContainerIds.addFirst(insideId);
                          tipOuterInsideContainerIds.put(outerContainerId, tipInsideContainerIds);
                          tipLocationCmd.setTipOuterInsideContainerIds(tipOuterInsideContainerIds);
                      } else {
                          if (!tipInsideContainerIds.contains(insideId)) {
                              tipInsideContainerIds.addFirst(insideId);
                              tipOuterInsideContainerIds.put(outerContainerId, tipInsideContainerIds);
                              tipLocationCmd.setTipOuterInsideContainerIds(tipOuterInsideContainerIds);
                          }
                      }
                  }
              }
          } else {// 内部容器直接放在库位上
              if (null == tipLocationCmd) {
                  tipLocationCmd = new LocationTipCacheCommand();
                  Map<Long, ArrayDeque<Long>> tipLocInsideContainerIds = new HashMap<Long, ArrayDeque<Long>>();
                  ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
                  tipInsideContainerIds.addFirst(insideId);
                  tipLocInsideContainerIds.put(locationId, tipInsideContainerIds);
                  tipLocationCmd.setTipLocInsideContainerIds(tipLocInsideContainerIds);
              } else {
                  Map<Long, ArrayDeque<Long>> tipLocInsideContainerIds = tipLocationCmd.getTipLocInsideContainerIds();
                  if (null == tipLocInsideContainerIds || tipLocInsideContainerIds.size() == 0) {
                      tipLocInsideContainerIds = new HashMap<Long, ArrayDeque<Long>>();
                      ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
                      tipInsideContainerIds.addFirst(insideId);
                      tipLocInsideContainerIds.put(locationId, tipInsideContainerIds);
                      tipLocationCmd.setTipLocInsideContainerIds(tipLocInsideContainerIds);
                  } else {
                      ArrayDeque<Long> tipInsideContainerIds = tipLocInsideContainerIds.get(locationId);
                      if (null == tipInsideContainerIds || tipInsideContainerIds.size() == 0) {
                          tipInsideContainerIds = new ArrayDeque<Long>();
                          tipInsideContainerIds.addFirst(insideId);
                          tipLocInsideContainerIds.put(locationId, tipInsideContainerIds);
                          tipLocationCmd.setTipLocInsideContainerIds(tipLocInsideContainerIds);
                      } else {
                          if (!tipInsideContainerIds.contains(insideId)) {
                              tipInsideContainerIds.addFirst(insideId);
                              tipLocInsideContainerIds.put(locationId, tipInsideContainerIds);
                              tipLocationCmd.setTipLocInsideContainerIds(tipLocInsideContainerIds);
                          }
                      }
                  }
              }
          }
          cacheManager.setObject(CacheConstants.CACHE_LOCATION + locationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
      }
      
      /***
       * 缓存作业明细
       * @tangming
       * @param operationId
       * @param ouId
       */
      public List<WhOperationLineCommand> cacheOperationLine(Long operationId,Long ouId){
          log.info("PdaPickingWorkCacheManagerImpl cacheOperationLine is start");
          List<WhOperationLineCommand> operationLineList = cacheManager.getObject(CacheConstants.OPERATION_LINE + operationId.toString());
          if(null ==  operationLineList ||  operationLineList.size() == 0) {
              operationLineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);
              cacheManager.setObject(CacheConstants.OPERATION_LINE + operationId.toString(),  operationLineList, CacheConstants.CACHE_ONE_DAY);
          }
          log.info("PdaPickingWorkCacheManagerImpl cacheOperationLine is end");
          
          return operationLineList;
      }
      
      
      
      
      
      /***
       * 清楚缓存(一个库位一个库位的清楚缓存)
       * @param operationId
       * @param isAfterScanLocation
       * @param skuId
       * @param insideContainerCmd
       * @param outerContainerCmd
       */
       public void pdaPickingRemoveAllCache(Long operationId,Boolean isAfterScanLocation,Long locationId){
           log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is start");
               OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
               if(null == operatorLine) {
                   throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
               }
               Map<Long, Set<Long>> operSkuIds = operatorLine.getSkuIds();  //散装sku
               Map<Long, Set<Long>> locInsideContainerIds = operatorLine.getInsideContainerIds();    //库位上所有的内部容器
               Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideSkuIds();  //内部容器对应所有sku
               Map<Long, Set<Long>> outerInsideId = operatorLine.getOuterToInside();
               Map<Long, Set<Long>> locOuterContainerIds = operatorLine.getOuterContainerIds();
               //先删除托盘上的
               if(null != locOuterContainerIds  && locOuterContainerIds.size() != 0) {
                   Set<Long> outerContainerIds = locOuterContainerIds.get(locationId);
                   if(null != outerContainerIds){
                       for(Long outerId:outerContainerIds){
                           Set<Long> insideIds = outerInsideId.get(outerId);
                           //先清楚内部容器的sku
                           for(Long insideId:insideIds) {
                               Set<Long> skuIds = insideSkuIds.get(insideId);   //当前内部容器内sku所有的sku
                               for(Long skuId:skuIds){
                                   cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_SN + insideId.toString()+skuId);
                                   cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideId.toString() + skuId.toString());
                                   cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideId.toString() + skuId.toString());
                               }
                               cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideId.toString());
                           }
                       }
                   }
               }
               //在删库位上的货箱
               if(null != locInsideContainerIds && locInsideContainerIds.size() != 0) {
                   Set<Long> insideIds = locInsideContainerIds.get(locationId);
                   if(null != insideIds) {
                       //先清楚内部容器的sku
                       for(Long insideId:insideIds) {
                           Set<Long> skuIds = insideSkuIds.get(insideId);   //当前内部容器内sku所有的sku
                           for(Long skuId:skuIds){
                               cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_SN + insideId.toString()+skuId);
                               cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideId.toString() + skuId.toString());
                               cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideId.toString() + skuId.toString());
                           }
                           cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideId.toString());
                       }
                   }
               }
               
               //散装sku
               if(null != operSkuIds && operSkuIds.size() != 0){
                   Set<Long> locSkuIds = operSkuIds.get(locationId); 
                   if(null != locSkuIds) {
                       for(Long skuId:locSkuIds) {
                           cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_SN + locationId.toString()+skuId);
                           cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + skuId.toString());
                           cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString() + skuId.toString());
                           cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE+ locationId.toString());
                       }
                   }
               }
               cacheManager.remove(CacheConstants.CACHE_LOC_INVENTORY+operationId.toString()+locationId.toString());    //单个库位的缓存
               cacheManager.remove(CacheConstants.CACHE_LOCATION+locationId.toString());
          if(isAfterScanLocation){
               //清楚作业明细
               cacheManager.remove(CacheConstants.OPERATION_LINE+operationId.toString());
               cacheManager.remove(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
               cacheManager.remove(CacheConstants.OPERATIONLINE_STATISTICS+ operationId.toString());
           }
           
           log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is end");
       }
       
       /**
        * 判断是值是否相同(如果不相同则,返回true,否则返回false)
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
                       if (!value.equals(cId)) {
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
       
      private boolean isCacheAllExists2(List<Long> ids, ArrayDeque<Long> cacheKeys) {
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

      
    /***
    * 修改工作/作业状态
    * @param operationId
    * @param workId
    */
    @Override
    public void pdaPickingUpdateStatus(Long operationId, String workCode,Long ouId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingUpdateStatus is start");
        WhOperation whOperation = whOperationDao.findOperationByIdExt(operationId, ouId);
        if(null == whOperation) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId,null,null);
        if(null == operationExecLineList || operationExecLineList.size()==0) {
            throw new BusinessException(ErrorCodes.OPERATION_EXEC_LINE_NO_EXIST);
        }
        //判断当前执行明细是否存在短拣sku
        Boolean exist = false;  //默认作业执行明细不存在短拣sku
        for(WhOperationExecLine operExecLine:operationExecLineList) {
             if(operExecLine.getIsShortPicking()) {  //当前执行明细是短拣sku
                 exist = true;
                 break;
             }
        }
        whOperation.setStatus(WorkStatus.FINISH);
        whOperation.setLastModifyTime(new Date());
        whOperation.setModifiedId(userId);
        whOperationDao.saveOrUpdateByVersion(whOperation);
        //修改拣货工作头状态
        WhWorkCommand whWorkCommand = whWorkDao.findWorkByWorkCode(workCode, ouId);
        if(null == whWorkCommand) {
            throw new BusinessException(ErrorCodes.WORK_NO_EXIST);
        }
        WhWork work = new WhWork();
        BeanUtils.copyProperties(whWorkCommand, work);
        if(exist) {  //存在短拣作业状态变为
            work.setStatus(WorkStatus.PARTLY_FINISH);
        }else{   //不存在变为10
            work.setStatus(WorkStatus.FINISH);
        }
        work.setLastModifyTime(new Date());
        work.setModifiedId(userId);
        whWorkDao.saveOrUpdateByVersion(work);
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingUpdateStatus is end");
        
    }
  
    /***
     * 将作业投标识为拣货完成
     * @param operationId
     * @param ouId
     */
    public void pdaReplenishmentUpdateOperation(Long operationId,Long ouId,Long userId){
        WhOperation operation = whOperationDao.findOperationByIdExt(operationId, ouId);
        if(null == operation){
            throw new BusinessException(ErrorCodes.OPATION_NO_EXIST);
        }
        operation.setIsPickingFinish(true);
        operation.setModifiedId(userId);
        whOperationDao.saveOrUpdateByVersion(operation);
    }
    
    /***
     * 拣货取消流程
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void cancelPattern(Long carId,Long outerContainerId,Long insideContainerId, int cancelPattern,int pickingWay,Long locationId,Long ouId,Long operationId,Long tipSkuId){
              if(cancelPattern == CancelPattern.PICKING_TIP_CAR_CANCEL) {
                  cacheManager.remove(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());  //删除统计缓存
                  cacheManager.remove(CacheConstants.OPERATION_LINE + operationId.toString());   //删除作业明细
                  if(Constants.PICKING_WAY_TWO == pickingWay){
                      OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                      if(null != tipLocationCmd ) {
                          tipLocationCmd.setTipOutBonxBoxIds(null);
                          cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                      }
                  }
                  OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                  if(null != tipLocationCmd ) {
                      ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
                      for(Long locId:tipLocationIds){
                          LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locId.toString());
                          if(null != tipLocCmd){
                              //先删除该库位上剩下的托盘和货箱
                              Map<Long,ArrayDeque<Long>> tipLocInsideContainerIds = tipLocCmd.getTipLocInsideContainerIds();
                              if(null != tipLocInsideContainerIds && tipLocInsideContainerIds.size() != 0){
                                  ArrayDeque<Long> tipInsideContainerIds = tipLocInsideContainerIds.get(locId); //直接放在库位上的其他货箱
                                  if(null != tipInsideContainerIds && tipInsideContainerIds.size() != 0){
                                      for(Long icId:tipInsideContainerIds){
                                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                          if(null != tipScanSkuCmd) {
                                                 ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                                                 if(null != scanSkuIds) {
                                                     for(Long skuId :scanSkuIds){
                                                         cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                                                         cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                                     }
                                                 }
                                          }
                                      }
                                  }
                              }
                              Map<Long,ArrayDeque<Long>> tipLocOuterContainerIds = tipLocCmd.getTipLocOuterContainerIds();
                              if(null != tipLocOuterContainerIds && tipLocOuterContainerIds.size() != 0){
                                  ArrayDeque<Long>tipOuterContainerIds = tipLocOuterContainerIds.get(locId);  //该库位上的其他托盘
                                  Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = tipLocCmd.getTipOuterInsideContainerIds();  //库位上其他托盘对应的内部货箱
                                  for(Long outerId:tipOuterContainerIds) {
                                      ArrayDeque<Long> insideContainerIds = tipOuterInsideContainerIds.get(outerId);
                                      for(Long icId:insideContainerIds){
                                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                          if(null != tipScanSkuCmd) {
                                                 ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                                                 if(null != scanSkuIds) {
                                                     for(Long skuId :scanSkuIds){
                                                         cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                                                         cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                                     }
                                                 }
                                          }
                                      }
                                  }
                              }
                             
                          }
                          cacheManager.remove(CacheConstants.CACHE_LOCATION + locId.toString());
                      }
                  }
                  cacheManager.remove(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
             }
             if(CancelPattern.PICKING_SCAN_LOC_CANCEL == cancelPattern){
                 LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
                 if(null != tipLocCmd){
                     //先删除该库位上剩下的托盘和货箱
                     Map<Long,ArrayDeque<Long>> tipLocInsideContainerIds = tipLocCmd.getTipLocInsideContainerIds();
                     if(null != tipLocInsideContainerIds && tipLocInsideContainerIds.size() != 0){
                         ArrayDeque<Long> tipInsideContainerIds = tipLocInsideContainerIds.get(locationId); //直接放在库位上的其他货箱
                         if(null != tipInsideContainerIds && tipInsideContainerIds.size() != 0){
                             for(Long icId:tipInsideContainerIds){
                                 ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                 if(null != tipScanSkuCmd) {
                                        ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                                        if(null != scanSkuIds) {
                                            for(Long skuId :scanSkuIds){
                                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                            }
                                        }
                                 }
                             }
                         }
                     }
                     Map<Long,ArrayDeque<Long>> tipLocOuterContainerIds = tipLocCmd.getTipLocOuterContainerIds();
                     if(null != tipLocOuterContainerIds && tipLocOuterContainerIds.size() != 0){
                         ArrayDeque<Long>tipOuterContainerIds = tipLocOuterContainerIds.get(locationId);  //该库位上的其他托盘
                         Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = tipLocCmd.getTipOuterInsideContainerIds();  //库位上其他托盘对应的内部货箱
                         for(Long outerId:tipOuterContainerIds) {
                             ArrayDeque<Long> insideContainerIds = tipOuterInsideContainerIds.get(outerId);
                             for(Long icId:insideContainerIds){
                                 ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                 if(null != tipScanSkuCmd) {
                                        ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                                        if(null != scanSkuIds) {
                                            for(Long skuId :scanSkuIds){
                                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                            }
                                        }
                                 }
                             }
                         }
                     }
                 }
                 OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
                 if(null != operatorLine) {
                     Map<Long, Set<Long>> locSkuIds = operatorLine.getSkuIds();
                     Set<Long> skuIds = locSkuIds.get(locationId);
                     if(null != skuIds) {
                         for(Long skuId:skuIds){
                             cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString()+skuId.toString());
                             cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                         }
                     }
                     OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                     if(null != tipLocationCmd ) {
                         ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
                         if(null != tipLocationIds && tipLocationIds.size() != 0){
                             tipLocationIds.removeFirst();
                             tipLocationCmd.setTipLocationIds(tipLocationIds);
                             cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                         }
                     }
                     //删除库位上的托盘货箱统计
                     cacheManager.remove(CacheConstants.CACHE_LOCATION + locationId.toString());
                 }
                
             }
             if(CancelPattern.PICKING_SCAN_OUTCONTAINER_CANCEL == cancelPattern){
                 //清除库位上的托盘
                 LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
                 if(null != tipLocCmd){
                     ArrayDeque<Long> tipOuterContainerIds = tipLocCmd.getTipLocOuterContainerIds().get(locationId);
                     if(null != tipOuterContainerIds && tipOuterContainerIds.size() != 0) {
                         Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = tipLocCmd.getTipOuterInsideContainerIds();
                         //删除该托盘对应的所有货箱
                         tipOuterInsideContainerIds.remove(outerContainerId);
                         tipLocCmd.setTipOuterInsideContainerIds(tipOuterInsideContainerIds);
                         tipOuterContainerIds.removeFirst();
                         cacheManager.setObject(CacheConstants.CACHE_LOCATION+locationId.toString(),tipLocCmd, CacheConstants.CACHE_ONE_DAY);
                     }
                 }
             }
             if(CancelPattern.PICKING_SCAN_INSIDECONTAINER_CANCEL == cancelPattern){ //提示货箱取消流程
                 LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
                 if(null != tipLocCmd){
                     ArrayDeque<Long> tipInsideContainerIds = null;
                     if(null != outerContainerId) {
                         tipInsideContainerIds = tipLocCmd.getTipOuterInsideContainerIds().get(outerContainerId);
                     }else{
                         tipInsideContainerIds = tipLocCmd.getTipLocInsideContainerIds().get(locationId);
                     }
                     if(null != tipInsideContainerIds && tipInsideContainerIds.size() != 0){
                         tipInsideContainerIds.removeFirst();  //删除当前内部容器
                         cacheManager.setObject(CacheConstants.CACHE_LOCATION+locationId.toString(),tipLocCmd, CacheConstants.CACHE_ONE_DAY);
                     }
                 }
                 ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                 if(null != tipScanSkuCmd) {
                        ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                        if(null != scanSkuIds) {
                            for(Long skuId :scanSkuIds){
                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString()+skuId.toString());
                            }
                        }
                 }
                 cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
             }
             if(CancelPattern.PICKING_SCAN_SKU_SCANCEL== cancelPattern){ //提示货箱取消流程){
                 if(null != insideContainerId) {
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString()+tipSkuId.toString());
                     cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipSkuId.toString());
                 }else{
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString()+tipSkuId.toString());
                     cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + tipSkuId.toString());
                 }
             }
             if(CancelPattern.PICKING_SCAN_SKU_DETAIL== cancelPattern){ //提示货箱取消流程){
                 if(null != insideContainerId) {
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString()+tipSkuId.toString());
                     cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipSkuId.toString());
                 }else{
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                     cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString()+tipSkuId.toString());
                     cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + locationId.toString() + tipSkuId.toString());
                 }
             }
             if(CancelPattern.PICKING_SCAN_OUT_BOUNX_BOX == cancelPattern){
                 OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                 if(null != tipLocationCmd ) {
                     tipLocationCmd.setTipOutBonxBoxIds(null);
                     cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                 }
                 //删除所有的库位信息
                 ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
                 for(Long locId:tipLocationIds){
                     LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locId.toString());
                     if(null != tipLocCmd){
                         //先删除该库位上剩下的托盘和货箱
                         Map<Long,ArrayDeque<Long>> tipLocInsideContainerIds = tipLocCmd.getTipLocInsideContainerIds();
                         if(null != tipLocInsideContainerIds && tipLocInsideContainerIds.size() != 0){
                             ArrayDeque<Long> tipInsideContainerIds = tipLocInsideContainerIds.get(locId); //直接放在库位上的其他货箱
                             if(null != tipInsideContainerIds && tipInsideContainerIds.size() != 0){
                                 for(Long icId:tipInsideContainerIds){
                                     ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                     if(null != tipScanSkuCmd) {
                                            ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                                            if(null != scanSkuIds) {
                                                for(Long skuId :scanSkuIds){
                                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                                }
                                            }
                                     }
                                 }
                             }
                         }
                         Map<Long,ArrayDeque<Long>> tipLocOuterContainerIds  = tipLocCmd.getTipLocOuterContainerIds();
                         if(null != tipLocOuterContainerIds && tipLocOuterContainerIds.size() != 0){
                             ArrayDeque<Long>tipouterContainerIds = tipLocOuterContainerIds.get(locId);  //该库位上的其他托盘
                             Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = tipLocCmd.getTipOuterInsideContainerIds();  //库位上其他托盘对应的内部货箱
                             for(Long outerId:tipouterContainerIds) {
                                 ArrayDeque<Long> insideContainerIds = tipOuterInsideContainerIds.get(outerId);
                                 for(Long icId:insideContainerIds){
                                     ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                     if(null != tipScanSkuCmd) {
                                            ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                                            if(null != scanSkuIds) {
                                                for(Long skuId :scanSkuIds){
                                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + icId.toString());
                                                }
                                            }
                                     }
                                 }
                             }
                         }
                        
                     }
                     cacheManager.remove(CacheConstants.CACHE_LOCATION + locId.toString());
                 }
             }
          }
    
    /***
     * 补货(拣货)取消流程
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void replenishmentCancelPattern(Long outerContainerId,Long insideContainerId, int cancelPattern,int pickingWay,Long locationId,Long ouId,Long operationId,Long tipSkuId){
        if(CancelPattern.PICKING_SCAN_LOC_CANCEL == cancelPattern){
            cacheManager.remove(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());  //删除统计缓存
            cacheManager.remove(CacheConstants.OPERATION_LINE + operationId.toString());   //删除作业明细
            cacheManager.remove(CacheConstants.CACHE_OPERATION_LINE+operationId.toString());
        }else if(CancelPattern.PICKING_TIP_CAR_CANCEL == cancelPattern){   //取消周转箱,删除当前库位缓存
            OperationLineCacheCommand  operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
            ArrayDeque<Long> tipLocationIds  = operLineCacheCmd.getTipLocationIds();
            if(null != tipLocationIds) {
                tipLocationIds.removeFirst();
            }
            cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString(), CacheConstants.CACHE_ONE_DAY);
        }else if(CancelPattern.PICKING_SCAN_OUTCONTAINER_CANCEL == cancelPattern){
        }else if(CancelPattern.PICKING_SCAN_INSIDECONTAINER_CANCEL == cancelPattern){
            ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
            if(null != tipScanSkuCmd) {
                   ArrayDeque<Long> scanSkuIds = tipScanSkuCmd.getScanSkuIds();
                   if(null != scanSkuIds) {
                       for(Long skuId :scanSkuIds){
                           cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString()+skuId.toString());
                       }
                   }
            }
            cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
        }else if(CancelPattern.PICKING_SCAN_SKU_SCANCEL == cancelPattern){
            if(null != insideContainerId) {
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString()+tipSkuId.toString());
            }else{
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString()+tipSkuId.toString());
            }
        }else if(CancelPattern.PICKING_SCAN_SKU_DETAIL == cancelPattern){
            if(null != insideContainerId) {
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString()+tipSkuId.toString());
            }else{
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString()+tipSkuId.toString());
            }
        }
    }
}
