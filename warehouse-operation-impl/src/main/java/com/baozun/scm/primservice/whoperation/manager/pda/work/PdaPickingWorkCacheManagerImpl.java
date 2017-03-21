package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CancalPattern;
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
                cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
                cSRCmd.setOutBounxBoxCode(outBounxBoxCode);
                cSRCmd.setIsNeedScanOutBounxBox(true);
                cSRCmd.setUseContainerLatticeNo(useContainerLatticeNo);  //货格号
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
    
    /***
     * 统计要拣货的库位库存
     * @param operationId
     * @param locationId
     * @param ouId
     * @return
     */
    public List<WhSkuInventoryCommand> cacheLocationInventory(Long operationId,Long locationId,Long ouId) {
        log.info("PdaPickingWorkCacheManagerImpl cacheLocationInventory is start");
        List<WhSkuInventoryCommand> skuInvList = cacheManager.getObject(CacheConstants.CACHE_LOC_INVENTORY + operationId.toString()+locationId.toString());
        if(null == skuInvList || skuInvList.size() == 0) {
            skuInvList = whSkuInventoryDao.getWhSkuInventoryByOccupationLineId(ouId, operationId);
            if(null == skuInvList || skuInvList.size() == 0){
                    throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
            }
            List<WhOperationLineCommand> operationLineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);   //当前工作下所有的作业明细集合
            for(WhOperationLineCommand operLineCmd : operationLineList){
                Long odoLineId = operLineCmd.getOdoLineId();  //出库单明细ID
                for(WhSkuInventoryCommand skuInvCmd:skuInvList) {
                    Long occupationLineId = skuInvCmd.getOccupationId();
                    if(odoLineId.equals(occupationLineId)) {
                        skuInvList.add(skuInvCmd); 
                    }
                }
            }
            cacheManager.setObject(CacheConstants.CACHE_LOC_INVENTORY + operationId.toString()+locationId.toString(), skuInvList, CacheConstants.CACHE_ONE_DAY);
        }
        log.info("PdaPickingWorkCacheManagerImpl cacheLocationInventory is end");
        return skuInvList;
    }
    
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
                        scanResult.setIsPicking(true);
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
                // 验证容器状态是否是待上架
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_SHEVLED) || container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PICKING))) {
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
                OutBoundBoxType o = outBoundBoxTypeDao.findByCode(code, ouId);
                if(null == o) {
                    throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
                }
                // 验证容器Lifecycle是否有效
                if (!o.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    continue;
                }
                outbounxBox = o.getCode();
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
                if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    continue;
                }
                // 验证容器状态是否是待上架
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE))) {
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
                  tipLocationCmd = new LocationTipCacheCommand();
                  ArrayDeque<Long> tipOuterContainerIds = new ArrayDeque<Long>();
                  tipOuterContainerIds.addFirst(outerId);
                  tipLocationCmd.setTipOuterContainerIds(tipOuterContainerIds);
                  scanResult.setIsNeedTipOutContainer(true); 
                  break;
              }else{
                  ArrayDeque<Long> tipOuterContainerIds = tipLocationCmd.getTipOuterContainerIds();
                  if(tipOuterContainerIds.isEmpty() || tipOuterContainerIds == null){
                      tipOuterContainerIds = new ArrayDeque<Long>();
                      tipOuterContainerIds.addFirst(outerId);
                      tipLocationCmd.setTipOuterContainerIds(tipOuterContainerIds);
                      scanResult.setIsNeedTipOutContainer(true); 
                      break;
                  }else{
                        if(tipOuterContainerIds.contains(outerId)){
                            continue;
                        }else{
                            tipOuterContainerId = outerId;
                            tipOuterContainerIds.addFirst(outerId);
                            tipLocationCmd.setTipOuterContainerIds(tipOuterContainerIds);
                            scanResult.setIsNeedTipOutContainer(true); 
                            break;
                        }
                  }
              }
          }
          cacheManager.setObject(CacheConstants.CACHE_LOCATION+locationId.toString(),tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
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
      public CheckScanResultCommand pdaPickingTipInsideContainer(Set<Long> insideContainerIds,Long locationId){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipInsideContainer is start");
          Long tipInsideContainerId = null;
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          if(null == tipLocationCmd) {
              tipLocationCmd = new LocationTipCacheCommand();
              ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
              for(Long icId:insideContainerIds) {
                  tipInsideContainerIds.addFirst(icId);
                  break;
              }
              tipLocationCmd.setTipInsideContainerIds(tipInsideContainerIds);
          }else{
              ArrayDeque<Long> tipInsideContainerIds = tipLocationCmd.getTipInsideContainerIds();
              if(!this.isCacheAllExists(insideContainerIds, tipInsideContainerIds)) {
                  for(Long icId:insideContainerIds) {
                      if(null == tipInsideContainerIds || tipInsideContainerIds.isEmpty()) {
                          tipInsideContainerIds = new ArrayDeque<Long>();
                          tipInsideContainerIds.addFirst(icId);
                      }else{
                          //判断改外部容器id是否已经存在缓存中
                          if(tipInsideContainerIds.contains(icId)) {
                              continue;
                          }
                          tipInsideContainerIds.addFirst(icId);
                      }
                    //存入缓存
                      tipInsideContainerId = icId;
                      cacheManager.setObject(CacheConstants.CACHE_LOCATION + locationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                      break;
                  }
                  scanResult.setIsNeedTipInsideContainer(true);
                  scanResult.setTipiInsideContainerId(tipInsideContainerId);
              }else{  //所有的内部容器全部扫描完毕
                  scanResult.setIsNeedTipInsideContainer(false);
              }
              
          }
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipInsideContainer is end");
          return scanResult;
      }
      /***
       * pda拣货提示sku
       * @param insideContainerIds
       * @param operationId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipSku(Set<Long> skuIds,Long operationId,Long locationId,Long ouId,Long insideContainerId,Map<Long, Map<String, Set<String>>> locskuAttrIdsSnDefect,Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipSku is start");
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          //该库位要拣货的所有库存记录
          List<WhSkuInventoryCommand> list = this.cacheLocationInventory(operationId, locationId, ouId);
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          Long tipSkuId = null;
          scanResult.setIsNeedTipSku(false);
          if(null == tipLocationCmd) {
                for(Long skuId:skuIds){
                    tipSkuId = skuId;
                    scanResult.setIsNeedTipSku(true);
                    break;
                }
          }else{
              ArrayDeque<Long> tipSkuIds = tipLocationCmd.getTipLocSkuIds();
              if(null == tipSkuIds || tipSkuIds.size() == 0) {
                  for(Long skuId:skuIds){
                      tipSkuId = skuId;
                      scanResult.setIsNeedTipSku(true);
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
                               scanResult.setIsNeedTipSku(true);
                               break;
                         }
                      }
                   }
              }
          }
          if(!scanResult.getIsNeedTipSku()) {
              return scanResult;
          }
          //  //拼装唯一sku及残次信息 缓存sku唯一标示
          String skuAttrId = null;
          ArrayDeque<String> skuAttrIds = cacheManager.getObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+tipSkuId.toString());
          if(null == skuAttrIds) {
              skuAttrIds = new ArrayDeque<String>();
          }
          //判断是否存在sn
          Boolean isExistSn = false;
          Set<String> snDefectSet = new HashSet<String>();  //sn及残次条码集合
          for(WhSkuInventoryCommand skuCmd:list) {
              List<WhSkuInventorySnCommand> listSnCmd =  skuCmd.getWhSkuInventorySnCommandList();
              if(null != listSnCmd && listSnCmd.size() != 0){ //
                  isExistSn = true;
                  if(null != insideContainerId) { //有货箱
                      if(insideContainerId.equals(skuCmd.getInsideContainerId()) && locationId.equals(skuCmd.getLocationId())) {
                          if(tipSkuId.longValue() == skuCmd.getSkuId().longValue()) {
                                 skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                                 Map<String,Set<String>> skuAttrIdSnDefect  = insideSkuAttrIdsSnDefect.get(insideContainerId);
                                 snDefectSet = skuAttrIdSnDefect.get(skuAttrId);  
                                 break;
                           }
                      }
                  }else{//散装
                      if(tipSkuId.longValue() == skuCmd.getSkuId().longValue() && locationId.equals(skuCmd.getLocationId())) {
                          skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                          Map<String,Set<String>> skuAttrIdSnDefect = locskuAttrIdsSnDefect.get(locationId);
                          snDefectSet = skuAttrIdSnDefect.get(skuAttrId);
                          break;
                       }
                  }
              }else{
                  if(null != insideContainerId) { //有货箱
                      if(insideContainerId.equals(skuCmd.getInsideContainerId()) && locationId.equals(skuCmd.getLocationId())) {
                          if(tipSkuId.longValue() == skuCmd.getSkuId().longValue()) {
                                 skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                                 break;
                           }
                      }
                  }else{//散装
                      if(tipSkuId.longValue() == skuCmd.getSkuId().longValue() && locationId.equals(skuCmd.getLocationId())) {
                          skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                          break;
                       }
                  }
              }
          }
          if(isExistSn){ //没有残次sn/残次信息
              for(String snDefect:snDefectSet) {
                  skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuAttrId,snDefect);
                  if(skuAttrIds.contains(skuAttrId)) {  //缓存中已经存在
                      continue;
                  }else{
                      skuAttrIds.addFirst(skuAttrId);
                      break;
                  }
              }
          }else{
              skuAttrIds.addFirst(skuAttrId);  
          }
          scanResult.setTipSkuAttrId(skuAttrId);
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipSku is end");
          return scanResult;
      }
      
      
      public void cacheSkuAttrId(Long locationId,Long skuId,String skuAttrId){
          ArrayDeque<String> skuAttrIds = cacheManager.getObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+skuId.toString());
          if(null == skuAttrIds) {
              skuAttrIds = new ArrayDeque<String>();
          }
          skuAttrIds.addFirst(skuAttrId);
          cacheManager.setObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+skuId.toString(),skuAttrIds, CacheConstants.CACHE_ONE_DAY);
          //缓存skuId
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
          if(null == tipLocationCmd) {
              tipLocationCmd = new LocationTipCacheCommand();
              ArrayDeque<Long> tipLocSkuIds = new ArrayDeque<Long>();
              tipLocSkuIds.addFirst(skuId);
              tipLocationCmd.setTipLocSkuIds(tipLocSkuIds);
          }else{
              ArrayDeque<Long> tipLocSkuIds = tipLocationCmd.getTipLocSkuIds();
              if(null == tipLocSkuIds || tipLocSkuIds.size() == 0) {
                  tipLocSkuIds = new ArrayDeque<Long>();
                  tipLocSkuIds.addFirst(skuId);
                  tipLocationCmd.setTipLocSkuIds(tipLocSkuIds);
              }else{
                  if(!tipLocSkuIds.contains(skuId)){
                      tipLocSkuIds = new ArrayDeque<Long>();
                      tipLocSkuIds.addFirst(skuId);
                      tipLocationCmd.setTipLocSkuIds(tipLocSkuIds);
                  }
              }
          }
          cacheManager.setObject(CacheConstants.CACHE_LOCATION + locationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
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
      public CheckScanResultCommand pdaPickingyCacheSkuAndCheckContainer(Integer scanPattern,List<Long> locationIds, Map<Long, Long> locSkuQty,Long locationId,Set<Long> locSkuIds,Set<Long> outerContainerIds,ContainerCommand outerContainerCmd,Long operationId,Map<Long,Long> insideContainerSkuIdsQty,Map<Long, Set<Long>> insideContainerSkuIds,Set<Long> insideContainerIds,Set<Long> locInsideContainerIds,ContainerCommand insideContainerCmd,WhSkuCommand skuCmd){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingyCacheSkuAndCheckContainer is start");
          CheckScanResultCommand cssrCmd = new CheckScanResultCommand();
          if (null != outerContainerCmd) {  //有托盘的情况(如果货箱提示完毕，直接提示下一个托盘)
              Long insideContainerId = insideContainerCmd.getId();
              Long outerContainerId = outerContainerCmd.getId();
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
              // 1.当前的内部容器是不是提示容器队列的第一个
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
              ArrayDeque<Long> cacheInsideContainerIds = null;
              if (null != cacheContainerCmd) {
                  cacheInsideContainerIds = cacheContainerCmd.getTipInsideContainerIds();
              }
              if (null != cacheInsideContainerIds && !cacheInsideContainerIds.isEmpty()) {
                  Long value = cacheInsideContainerIds.peekFirst();// 队列的第一个
                  if (null == value) value = -1L;
                  if (0 != value.compareTo(insideContainerId)) {
                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                  }
              } else {
                  log.error("scan container queue is exception, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

              }
              // 2.当前的外部容器是不是提示外部容器队列中的第一个
              ArrayDeque<Long> cacheOuterContainerIds = null;
              if(null != cacheContainerCmd) {
                  cacheOuterContainerIds = cacheContainerCmd.getTipOuterContainerIds();
              }
              if (null != cacheOuterContainerIds && !cacheOuterContainerIds.isEmpty()) {
                  Long value = cacheOuterContainerIds.peekFirst();// 队列的第一个
                  if (null == value) value = -1L;
                  if (0 != value.compareTo(outerContainerId)) {
                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                  }
              } else {
                  log.error("scan container queue is exception, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

              }
              // 3.得到当前内部容器的所有商品并复核商品
              Long skuId = skuCmd.getId();
              Double skuQty = skuCmd.getScanSkuQty();
              Set<Long> icSkusIds = insideContainerSkuIds.get(insideContainerId);
              boolean skuExists = false;
              for (Long sId : icSkusIds) {
                  if (0 == skuId.compareTo(sId)) {
                      skuExists = true;
                      Long icSkuQty = insideContainerSkuIdsQty.get(skuId);
                      if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {  //逐件扫描
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
                              if (cacheValue == icSkuQty.longValue()) {
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
                                          if(isCacheAllExists(outerContainerIds,cacheOuterContainerIds)){//托盘还没拣货完毕
                                              cssrCmd.setIsNeedTipOutContainer(true);
                                          } else{
                                              cssrCmd.setIsNeedTipOutContainer(false);  //库位上所有的托盘已经拣货完毕
                                          }
                                      } else {
                                          cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                      }
                                  } else {
                                      // 继续复核
                                      cssrCmd.setIsNeedTipSku(true);
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  // 继续复核
                                  cssrCmd.setIsNeedTipSku(true);
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          } else {
                              ScanTipSkuCacheCommand cacheSkuCmd = new ScanTipSkuCacheCommand();
                              cacheSkuCmd.setOuterContainerId(outerContainerCmd.getId());
                              cacheSkuCmd.setOuterContainerCode(outerContainerCmd.getCode());
                              cacheSkuCmd.setInsideContainerId(insideContainerCmd.getId());
                              cacheSkuCmd.setInsideContainerCode(insideContainerCmd.getCode());
                              ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                              oneByOneCacheSkuIds.addFirst(skuId);
                              cacheSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                              long value = 0L;
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                  cacheSkuIds.addFirst(skuId);
                                  cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                  if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                      // 全部商品已复核完毕
                                      if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {
                                          // 一个托盘拣货完毕,判断是否还有托盘
                                          if(isCacheAllExists(outerContainerIds,cacheOuterContainerIds)){//托盘还没拣货完毕
                                              cssrCmd.setIsNeedTipOutContainer(true);
                                          } else{
                                              cssrCmd.setIsNeedTipOutContainer(false);  //库位上所有的托盘已经拣货完毕
                                          }
                                      } else {
                                          cssrCmd.setIsNeedTipInsideContainer(true);
                                      }
                                  } else {
                                      // 继续复核
                                      cssrCmd.setIsNeedTipSku(true);
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  // 继续复核
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                  cssrCmd.setIsNeedTipSku(true);
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          }
                      }else{  //批量扫描
                          if (skuQty.longValue() == icSkuQty.longValue()) {
                              ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                            ArrayDeque<Long> scanSkuIds = null;
                            if (null != tipScanSkuCmd) {
                                scanSkuIds = tipScanSkuCmd.getScanSkuIds();// 取到已扫描商品队列
                            }
                            if (null != scanSkuIds && !scanSkuIds.isEmpty()) {
                                boolean isExists = false;
                                Iterator<Long> iter = scanSkuIds.iterator();
                                while (iter.hasNext()) {
                                    Long value = iter.next();
                                    if (null == value) value = -1L;
                                    if (0 == skuId.compareTo(new Long(value))) {
                                        isExists = true;
                                        break;
                                    }
                                }
                                if (false == isExists) {
                                    scanSkuIds.addFirst(skuId);// 加入队列
                                    tipScanSkuCmd.setScanSkuIds(scanSkuIds);
                                    cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                    if (isCacheAllExists(icSkusIds, scanSkuIds)) {
                                     // 全部商品已复核完毕
                                        if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {
                                            // 一个托盘拣货完毕,判断是否还有托盘
                                            if(isCacheAllExists(outerContainerIds,cacheOuterContainerIds)){//托盘还没拣货完毕
                                                cssrCmd.setIsNeedTipOutContainer(true);
                                            } else{
                                                cssrCmd.setIsNeedTipOutContainer(false);  //库位上所有的托盘已经拣货完毕
                                            }
                                        } else {
                                            cssrCmd.setIsNeedTipInsideContainer(true);
                                        }
                                    } else {
                                        // 继续复核
                                        cssrCmd.setIsNeedTipSku(true);
                                    }
                                    break;
                                } else {
                                    log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", outerContainerId, insideContainerId, skuId, logId);
                                    throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {insideContainerCmd.getCode()});
                                }
                            } else {
                                ScanTipSkuCacheCommand cacheSkuCmd = new ScanTipSkuCacheCommand();
                                cacheSkuCmd.setOuterContainerId(outerContainerCmd.getId());
                                cacheSkuCmd.setOuterContainerCode(outerContainerCmd.getCode());
                                cacheSkuCmd.setInsideContainerId(insideContainerCmd.getId());
                                cacheSkuCmd.setInsideContainerCode(insideContainerCmd.getCode());
                                ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                cacheSkuIds.addFirst(skuId);
                                cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                    // 全部商品已复核完毕
                                    if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {
                                        // 一个托盘拣货完毕,判断是否还有托盘
                                        if(isCacheAllExists(outerContainerIds,cacheOuterContainerIds)){//托盘还没拣货完毕
                                            cssrCmd.setIsNeedTipOutContainer(true);
                                        } else{
                                            cssrCmd.setIsNeedTipOutContainer(false);  //库位上所有的托盘已经拣货完毕
                                        }
                                    } else {
                                        cssrCmd.setIsNeedTipInsideContainer(true);
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setIsNeedTipSku(true);
                                }
                                break;
                            }
                        } 
                     else {
                            log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", outerContainerId, insideContainerId, skuId, logId);
                            throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {insideContainerCmd.getCode()});
                        }
                    }
                         
                  }
              }
              if (false == skuExists) {
                  log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
              }
          } else if(null == outerContainerCmd && null != insideContainerCmd){
              Long insideContainerId = insideContainerCmd.getId();
              // 0.先判断当前内部容器是否在缓存中
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
             // 1.当前的内部容器是不是提示容器队列的第一个
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
              ArrayDeque<Long> cacheInsideContainerIds = null;
              if (null != cacheContainerCmd) {
                  cacheInsideContainerIds = cacheContainerCmd.getTipInsideContainerIds();
              }
              if (null != cacheInsideContainerIds && !cacheInsideContainerIds.isEmpty()) {
                  Long value = cacheInsideContainerIds.peekFirst();// 队列的第一个
                  if (null == value) value = -1L;
                  if (0 != value.compareTo(insideContainerId)) {
                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                  }
              } else {
                  log.error("scan container queue is exception, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

              }
              ArrayDeque<Long> tipLocSkuIds = cacheContainerCmd.getTipLocSkuIds();    //直接放在库位上的sku队列
              // 2.得到当前内部容器的所有商品并复核商品
              Long skuId = skuCmd.getId();
              Double skuQty = skuCmd.getScanSkuQty();
              Set<Long> icSkusIds = insideContainerSkuIds.get(insideContainerId);
              boolean skuExists = false;
              for (Long sId : icSkusIds) {
                  if (0 == skuId.compareTo(sId)) {
                      skuExists = true;
                      Long icSkuQty = insideContainerSkuIdsQty.get(skuId);
                      if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {  //逐件扫描
                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                          ArrayDeque<Long> oneByOneScanSkuIds = null;
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
                                  ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                  if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                      cacheSkuIds = new ArrayDeque<Long>();
                                  }
                                  cacheSkuIds.addFirst(skuId);
                                  tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                  if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                      //判断库位上是否还有货箱没有扫描
                                      if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                          //判断库位上是否有直接放的sku商品
                                          if(!isCacheAllExists(locSkuIds,tipLocSkuIds)) {
                                              cssrCmd.setIsNeedScanSku(true);
                                          }
                                      } else {
                                          cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                      }
                                  } else {
                                      // 继续复核
                                      cssrCmd.setIsNeedTipSku(true);
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  // 继续复核
                                  cssrCmd.setIsNeedTipSku(true);
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          } else {  //缓存skuId队列为空
                              ScanTipSkuCacheCommand cacheSkuCmd = new ScanTipSkuCacheCommand();
                              cacheSkuCmd.setInsideContainerId(insideContainerCmd.getId());
                              cacheSkuCmd.setInsideContainerCode(insideContainerCmd.getCode());
                              ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                              oneByOneCacheSkuIds.addFirst(skuId);
                              cacheSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                              long value = 0L;
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                  cacheSkuIds.addFirst(skuId);
                                  cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                  if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                      //判断库位上是否还有货箱没有扫描
                                      if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                          //判断库位上是否有直接放的sku商品
                                          if(!isCacheAllExists(locSkuIds,tipLocSkuIds)) {
                                              cssrCmd.setIsNeedScanSku(true);
                                          }
                                      } else {
                                          cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                      }
                                  } else {
                                      // 继续复核
                                      cssrCmd.setIsNeedTipInsideContainer(true);
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  // 继续复核
                                  cssrCmd.setIsNeedTipSku(true);
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          }
                      }else{//批量扫描
                          if (skuQty.longValue() == icSkuQty.longValue()) {
                              ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString());
                            ArrayDeque<Long> scanSkuIds = null;
                            if (null != tipScanSkuCmd) {
                                scanSkuIds = tipScanSkuCmd.getScanSkuIds();// 取到已扫描商品队列
                            }
                            if (null != scanSkuIds && !scanSkuIds.isEmpty()) {
                                boolean isExists = false;
                                Iterator<Long> iter = scanSkuIds.iterator();
                                while (iter.hasNext()) {
                                    Long value = iter.next();
                                    if (null == value) value = -1L;
                                    if (0 == skuId.compareTo(new Long(value))) {
                                        isExists = true;
                                        break;
                                    }
                                }
                                if (false == isExists) {
                                    scanSkuIds.addFirst(skuId);// 加入队列
                                    tipScanSkuCmd.setScanSkuIds(scanSkuIds);
                                    cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                    if (isCacheAllExists(icSkusIds, scanSkuIds)) {
                                        //判断库位上是否还有货箱没有扫描
                                        if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                            //判断库位上是否有直接放的sku商品
                                            if(!isCacheAllExists(locSkuIds,tipLocSkuIds)) {
                                                cssrCmd.setIsNeedScanSku(true);
                                            }
                                        } else {
                                            cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                        }
                                    } else {
                                        // 继续复核
                                        cssrCmd.setIsNeedTipSku(true);
                                    }
                                    break;
                                } else {
                                    log.error("scan sku has already checked,  icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                                    throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {insideContainerCmd.getCode()});
                                }
                            } else {
                                ScanTipSkuCacheCommand cacheSkuCmd = new ScanTipSkuCacheCommand();
                                cacheSkuCmd.setOuterContainerCode(outerContainerCmd.getCode());
                                cacheSkuCmd.setInsideContainerId(insideContainerCmd.getId());
                                cacheSkuCmd.setInsideContainerCode(insideContainerCmd.getCode());
                                ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                cacheSkuIds.addFirst(skuId);
                                cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideContainerId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                    //判断库位上是否还有货箱没有扫描
                                    if (isCacheAllExists(insideContainerIds, cacheInsideContainerIds)) {  //返回true ,两者相同
                                        //判断库位上是否有直接放的sku商品
                                        if(!isCacheAllExists(locSkuIds,tipLocSkuIds)) {
                                            cssrCmd.setIsNeedScanSku(true);
                                        }
                                    } else {
                                        cssrCmd.setIsNeedTipInsideContainer(true);  //提示下一个内部容器
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setIsNeedTipSku(true);
                                }
                                break;
                            }
                        } 
                     else {
                            log.error("scan sku qty is not equal with rcvd inv qty,  icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                            throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {insideContainerCmd.getCode()});
                        }
                      }
                  }
              }
              if (false == skuExists) {
                  log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
              }
          }else if(null == outerContainerCmd && null == insideContainerCmd){//  sku直接放在库位上
              Long skuId = skuCmd.getId();
              OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
              ArrayDeque<Long> cacheLocaitionIds = null;
              if (null != operLineCacheCmd) {
                  cacheLocaitionIds = operLineCacheCmd.getTipLocationIds();
              }
              if (null != cacheLocaitionIds && !cacheLocaitionIds.isEmpty()) {
                  Long value = cacheLocaitionIds.peekFirst();// 判断当前库位是否是队列的第一个
                  if (null == value) value = -1L;
                  if (0 != value.compareTo(locationId)) {
                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                  }
              } else {
                  log.error("scan container queue is exception, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

              }
              Double skuQty = skuCmd.getScanSkuQty();
              boolean skuExists = false;
              for (Long sId : locSkuIds) {
                  if (0 == skuId.compareTo(sId)) {
                      skuExists = true;
                      Long icSkuQty = locSkuQty.get(sId);
                      if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {  //逐件扫描
                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString());
                          ArrayDeque<Long> oneByOneScanSkuIds = null;
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
                                  ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                  if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                      cacheSkuIds = new ArrayDeque<Long>();
                                  }
                                  cacheSkuIds.addFirst(skuId);
                                  tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                  if (isCacheAllExists(locSkuIds, cacheSkuIds)) {
                                      if (isCacheAllExists2(locationIds, cacheLocaitionIds)) {  //返回true ,两者相同
                                          //判断库位上是否有直接放的sku商品
                                          cssrCmd.setIsPicking(true);
                                      } else {
                                          cssrCmd.setIsNeedTipLoc(true);
                                      }
                                  } else {
                                      // 继续复核
                                      cssrCmd.setIsNeedTipSku(true);
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  // 继续复核
                                  cssrCmd.setIsNeedTipSku(true);
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          } else {  //缓存skuId队列为空
                              ScanTipSkuCacheCommand cacheSkuCmd = new ScanTipSkuCacheCommand();
                              ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                              oneByOneCacheSkuIds.addFirst(skuId);
                              cacheSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                              long value = 0L;
                              if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                              }
                              long cacheValue = cacheManager.incrBy(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString() + skuId.toString(), skuQty.intValue());
                              if (cacheValue == icSkuQty.longValue()) {
                                  ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                  cacheSkuIds.addFirst(skuId);
                                  cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                  cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                  if (isCacheAllExists(locSkuIds, cacheSkuIds)) {
                                      //判断库位上是否还有货箱没有扫描
                                      if (isCacheAllExists2(locationIds, cacheLocaitionIds)) {  //返回true ,两者相同
                                          //判断库位上是否有直接放的sku商品
                                          cssrCmd.setIsPicking(true);
                                      } else {
                                          cssrCmd.setIsNeedTipLoc(true);
                                      }
                                  } else {
                                      // 继续复核
                                      cssrCmd.setIsNeedTipInsideContainer(true);
                                  }
                                  break;
                              } else if (cacheValue < icSkuQty.longValue()) {
                                  // 继续复核
                                  cssrCmd.setIsNeedTipSku(true);
                                  break;
                              } else {
                                  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                              }
                          }
                      }else{   //批量扫描
                          if (skuQty.longValue() == icSkuQty.longValue()) {
                              ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString());
                            ArrayDeque<Long> scanSkuIds = null;
                            if (null != tipScanSkuCmd) {
                                scanSkuIds = tipScanSkuCmd.getScanSkuIds();// 取到已扫描商品队列
                            }
                            if (null != scanSkuIds && !scanSkuIds.isEmpty()) {
                                boolean isExists = false;
                                Iterator<Long> iter = scanSkuIds.iterator();
                                while (iter.hasNext()) {
                                    Long value = iter.next();
                                    if (null == value) value = -1L;
                                    if (0 == skuId.compareTo(new Long(value))) {
                                        isExists = true;
                                        break;
                                    }
                                }
                                if (false == isExists) {
                                    scanSkuIds.addFirst(skuId);// 加入队列
                                    tipScanSkuCmd.setScanSkuIds(scanSkuIds);
                                    cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                    if (isCacheAllExists(locSkuIds, scanSkuIds)) {
                                        if (isCacheAllExists2(locationIds, cacheLocaitionIds)) {  //返回true ,两者相同
                                            //判断库位上是否有直接放的sku商品
                                            cssrCmd.setIsPicking(true);
                                        } else {
                                            cssrCmd.setIsNeedTipLoc(true);
                                        }
                                    } else {
                                        // 继续复核
                                        cssrCmd.setIsNeedTipSku(true);
                                    }
                                    break;
                                } else {
                                    log.error("scan sku has already checked,  locationId is:[{}], scanSkuId is:[{}], logId is:[{}]", locationId, skuId, logId);
                                    throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {insideContainerCmd == null ? "" : insideContainerCmd.getCode()});
                                }
                            } else {
                                ScanTipSkuCacheCommand cacheSkuCmd = new ScanTipSkuCacheCommand();
                                ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                cacheSkuIds.addFirst(skuId);
                                cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(locSkuIds, cacheSkuIds)) {
                                    if (isCacheAllExists2(locationIds, cacheLocaitionIds)) {  //返回true ,两者相同
                                        //判断库位上是否有直接放的sku商品
                                        cssrCmd.setIsPicking(true);
                                    } else {
                                        cssrCmd.setIsNeedTipLoc(true);
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setIsNeedTipSku(true);
                                }
                                break;
                            }
                        } 
                     else {
                            log.error("scan sku qty is not equal with rcvd inv qty,  locationId is:[{}], scanSkuId is:[{}], logId is:[{}]", locationId, skuId, logId);
                            throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {insideContainerCmd == null ? "" : insideContainerCmd.getCode()});
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
//      
//      /***
//       * 缓存已经拣货作业id
//       * @param operationId
//       * @param skuAttrIds
//       * @param outerContainerId
//       * @param insideContainerId
//       * @param locationId
//       * @param ouId
//       * @param isShortPicking
//       * @param scanQty
//       */
//     public Long cachePickingOperLineId(Long operationId,String skuAttrIds,Long outerContainerId,Long insideContainerId,Long locationId,Long ouId,Boolean isShortPicking,Double scanQty) {
//          log.info("PdaPickingWorkCacheManagerImpl cachePickingOperLineId is start");
//          OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
//          List<WhOperationLineCommand> operationLineList = this.cacheOperationLine(operationId, ouId);
//          Long operationLineId = null;
//          for(WhOperationLineCommand operLineCmd:operationLineList) {
//              String invSkuCmdIds = SkuCategoryProvider.getSkuAttrIdByOperationLine(operLineCmd);
//             if(locationId == operLineCmd.getFromLocationId() && invSkuCmdIds.equals(skuAttrIds) && null != insideContainerId && insideContainerId == operLineCmd.getFromInsideContainerId() && null != outerContainerId && outerContainerId == operLineCmd.getFromOuterContainerId() ) {
//                 operationLineId  = operLineCmd.getId();
//                 //判断当前作业明细id是否在缓存中
//                 if(null == operLineCacheCmd) {
//                     operLineCacheCmd = new OperationLineCacheCommand();
//                     if(isShortPicking) {//短拣扫描
//                         Set<Long> shortPikcingOperIds =  new HashSet<Long>();
//                         shortPikcingOperIds.add(operationLineId);
//                     }else{ //非短拣扫描
//                         Set<Long> pickingOperIds =  new HashSet<Long>();
//                         pickingOperIds.add(operationLineId);
//                     }
//                     Map<Long,Double> operLineIdToQty = new HashMap<Long,Double>();
//                     operLineIdToQty.put(operationLineId, scanQty);
//                     break;
//                 }else{
//                     if(isShortPicking) {
//                         Set<Long> shortPikcingOperIds = operLineCacheCmd.getShortPikcingOperIds();
//                         if(null == shortPikcingOperIds) {
//                             shortPikcingOperIds = new HashSet<Long>();
//                         }else if(shortPikcingOperIds.contains(operationLineId)){
//                             continue;
//                         }
//                         shortPikcingOperIds.add(operationLineId);
//                     }else{
//                         Set<Long> pickingOperIds = operLineCacheCmd.getPickingOperIds();
//                         if(null == pickingOperIds){
//                             pickingOperIds = new HashSet<Long>();
//                         }else if(pickingOperIds.contains(operationLineId)) {
//                             continue;
//                         }
//                         pickingOperIds.add(operationLineId);
//                     }
//                     break;
//                 }
//                 
//             }
//          }
//          if(null == operationLineId){
//              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//          }
//          Map<Long,Double> operLineIdToQty =  operLineCacheCmd.getOperLineIdToQty();
//          if(null == operLineIdToQty) {
//              operLineIdToQty = new HashMap<Long,Double>();
//              operLineIdToQty.put(operationLineId, scanQty);
//          }else{
//              Double qty = operLineIdToQty.get(operationLineId);
//              operLineIdToQty.put(operationLineId, qty+scanQty);
//          }
//          cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString(), operLineCacheCmd, CacheConstants.CACHE_ONE_DAY);
//          log.info("PdaPickingWorkCacheManagerImpl cachePickingOperLineId is end");
//          return operationLineId;
//      }
      
      
      
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
           if(isAfterScanLocation) {  //一个库位流程拣货
               LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
               if(null == tipLocationCmd) {
                   throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
               }
               OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
               if(null == operatorLine) {
                   throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
               }
               Map<Long, Set<Long>> locInsideContainerIds = operatorLine.getInsideContainerIds();    //库位上所有的内部容器
               Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideContainerIds();  //内部容器对应所有sku
               Map<Long, Set<Long>> operSkuIds = operatorLine.getSkuIds();  //散装sku
               Set<Long> insideIds = locInsideContainerIds.get(locationId);
               //先清楚内部容器的sku
               for(Long insideId:insideIds) {
                   Set<Long> skuIds = insideSkuIds.get(insideId);   //当前内部容器内sku所有的sku
                   for(Long skuId:skuIds){
                       cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + insideId.toString() + skuId.toString());
                   }
               }
              //散装sku
               Set<Long> locSkuIds = operSkuIds.get(locationId); 
               for(Long skuId:locSkuIds) {
                   cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + locationId.toString() + skuId.toString());
                   cacheManager.remove(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString() + skuId.toString());
               }
               cacheManager.remove(CacheConstants.CACHE_LOCATION+locationId.toString());
               cacheManager.remove(CacheConstants.CACHE_LOC_INVENTORY+locationId.toString());    //单个库位的缓存
           }else{
             //清楚作业明细
               cacheManager.remove(CacheConstants.OPERATION_LINE+operationId.toString());
               cacheManager.remove(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
           }
           
           log.info("PdaPickingWorkCacheManagerImpl addPickingOperationExecLine is end");
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
        List<WhOperationExecLine>  operationExecLineList = whOperationExecLineDao.getOperationExecLine(operationId, ouId);
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
              if(cancelPattern == CancalPattern.TIP_CAR_CANCEL) {
                  cacheManager.remove(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());  //删除统计缓存
                  cacheManager.remove(CacheConstants.OPERATION_LINE + operationId.toString());   //删除作业明细
                  if(Constants.PICKING_WAY_TWO == pickingWay){
                      OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                      if(null != tipLocationCmd ) {
                          tipLocationCmd.setTipOutBonxBoxIds(null);
                          cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                      }
                  }
             }
             if(CancalPattern.TIP_LOC_CANCEL == cancelPattern){
                 OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                 if(null != tipLocationCmd ) {
                     ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
                     if(null != tipLocationIds){
                         tipLocationIds.removeFirst();
                     }
                     cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                 }
             }
             if(CancalPattern.SCAN_LOC_CANCEL == cancelPattern){
                 OperationLineCacheCommand operationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                 if(null != operationCmd){
                     operationCmd.setTipLocationIds(null);//提示库位队列
                     cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(),operationCmd, CacheConstants.CACHE_ONE_DAY);
                 }
                 
             }
             if(CancalPattern.TIP_OUTCONTAINER_CANCEL == cancelPattern){
                 //清除库位上的托盘
                 LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
                 if(null != tipLocCmd){
                     tipLocCmd.setTipOuterContainerIds(null);
                     cacheManager.setObject(CacheConstants.CACHE_LOCATION+locationId.toString(),tipLocCmd, CacheConstants.CACHE_ONE_DAY);
                 }
             }
             if(CancalPattern.TIP_INSIDECONTAINER_CANCEL == cancelPattern){ //提示货箱取消流程
                 LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
                 if(null != tipLocCmd){
                     tipLocCmd.setTipInsideContainerIds(null);
                     tipLocCmd.setTipLocSkuIds(null);
                     cacheManager.setObject(CacheConstants.CACHE_LOCATION+locationId.toString(),tipLocCmd, CacheConstants.CACHE_ONE_DAY);
                 }
             }
             if(CancalPattern.TIP_SKU_SCANCEL== cancelPattern){ //提示货箱取消流程){
                 LocationTipCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + locationId.toString());
                 if(null != tipLocCmd){
                     ArrayDeque<Long> tipLocSkuIds = tipLocCmd.getTipLocSkuIds();
                     if(null != tipLocSkuIds) {
                         tipLocSkuIds.removeFirst();
                     }
                     tipLocCmd.setTipLocSkuIds(tipLocSkuIds);
                     cacheManager.setObject(CacheConstants.CACHE_LOCATION+locationId.toString(),tipLocCmd, CacheConstants.CACHE_ONE_DAY);
                 }
                 //唯一sku
                 ArrayDeque<String> skuAttrIdsSnDef = cacheManager.getObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+tipSkuId.toString());
                 if(null != skuAttrIdsSnDef && skuAttrIdsSnDef.size() != 0){
                     skuAttrIdsSnDef.removeFirst();
                     cacheManager.setObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+tipSkuId.toString(),skuAttrIdsSnDef , CacheConstants.CACHE_ONE_DAY);
                 }
             }
             if(CancalPattern.SCAN_OUT_BOUNX_BOX == cancelPattern){
                 OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
                 if(null != tipLocationCmd ) {
                     tipLocationCmd.setTipOutBonxBoxIds(null);
                     cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE+ operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                 }
             }
          }
}
