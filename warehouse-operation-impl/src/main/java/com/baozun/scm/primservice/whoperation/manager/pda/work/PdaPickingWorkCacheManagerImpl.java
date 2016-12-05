package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.LocationTipCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ScanTipSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaManmadePutawayCacheManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.utilities.type.StringUtil;

@Service("pdaPickingWorkCacheManager")
@Transactional
public class PdaPickingWorkCacheManagerImpl extends BaseManagerImpl implements PdaPickingWorkCacheManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaManmadePutawayCacheManagerImpl.class);
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
    
    
    /***
     * 统计要拣货的库位库存
     * @param operatorId
     * @param locationId
     * @param ouId
     * @return
     */
    public List<WhSkuInventoryCommand> cacheLocationInventory(Long operatorId,Long locationId,Long ouId) {
        log.info("pdaManmadePutawayCacheManager cacheLocationInventory is start");
        List<WhSkuInventoryCommand> skuInvList = cacheManager.getObject(CacheConstants.CACHE_LOC_INVENTORY + operatorId.toString());
        if(null == skuInvList) {
            List<WhSkuInventoryCommand> locInventoryList = new ArrayList<WhSkuInventoryCommand>();
            List<WhSkuInventoryCommand> list = cacheManager.getObject(CacheConstants.CACHE_LOC_INVENTORY + operatorId.toString());
            if(null == list || list.size() == 0) {
                List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findWhSkuInvCmdByLocationContainerIdIsNull(ouId,locationId);
                if(null == skuInvCmdList || skuInvCmdList.size() == 0) {
                    throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);   //库位库存不存在
                } 
            }
            List<WhOperationLineCommand> operationLineList = whOperationLineDao.findOperationLineByLocationId(operatorId,ouId,locationId);
            for(WhOperationLineCommand operLineCmd : operationLineList) {
                WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
                invSkuCmd.setSkuId(operLineCmd.getSkuId()); 
                invSkuCmd.setInvType(operLineCmd.getInvType());
                invSkuCmd.setBatchNumber(operLineCmd.getBatchNumber());
                invSkuCmd.setMfgDate(operLineCmd.getMfgDate());
                invSkuCmd.setExpDate(operLineCmd.getExpDate());
                invSkuCmd.setCountryOfOrigin(operLineCmd.getCountryOfOrigin());
                invSkuCmd.setInvAttr1(operLineCmd.getInvAttr1());
                invSkuCmd.setInvAttr2(operLineCmd.getInvAttr2());
                invSkuCmd.setInvAttr3(operLineCmd.getInvAttr3());
                invSkuCmd.setInvAttr4(operLineCmd.getInvAttr4());
                invSkuCmd.setInvAttr5(operLineCmd.getInvAttr5());
                invSkuCmd.setInvStatus(operLineCmd.getInvStatus());
                String invSkuCmdIds = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
                for(WhSkuInventoryCommand skuInvCmd:list) {
                    String skuInvCmdIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                    if(invSkuCmdIds.equals(skuInvCmdIds)) {   //库存属性相同,确定一条库位库存记录
                        if(!locInventoryList.contains(skuInvCmd)) {
                            locInventoryList.add(skuInvCmd);
                        }
                    }
                }
            }
            
            
            cacheManager.setObject(CacheConstants.CACHE_LOC_INVENTORY + operatorId.toString(), locInventoryList, CacheConstants.CACHE_ONE_DAY);
        }
        log.info("pdaManmadePutawayCacheManager cacheLocationInventory is end");
        return skuInvList;
    }
    /***
     * 缓存库位信息
     * @param command
     * @return
     */
    public CheckScanResultCommand locationTipcache(Long operatorId,Integer pickingType,List<Long> locationIds){
        log.info("pdaManmadePutawayCacheManager containerPutawayCacheInsideContainer is start");
        CheckScanResultCommand scanResult = new CheckScanResultCommand();
        Long tipLocationId = null;
        LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
        for(Long locationId:locationIds) {
            if(null == tipLocationCmd) {
                LocationTipCacheCommand tipCmd = new LocationTipCacheCommand();
                tipCmd.setPickingType(pickingType);
                ArrayDeque<Long> locIds = new ArrayDeque<Long>();
                locIds.addFirst(locationId);
                tipCmd.setTipLocationIds(locIds);
                cacheManager.setObject(CacheConstants.CACHE_LOCATION + operatorId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
                scanResult.setTipLocationId(tipLocationId);
                scanResult.setIsPicking(false);  // 没有上架结束
            }else{
                ArrayDeque<Long> tipLocationIds = tipLocationCmd.getTipLocationIds();
                if(this.isCacheAllExists2(locationIds, tipLocationIds)) {
                    if (null != tipLocationIds && !tipLocationIds.isEmpty()) {
                        //判断库位是否扫描完毕
                        if(tipLocationIds.contains(locationId)) {
                            continue;
                        }else{
                            tipLocationIds.addFirst(locationId);
                            cacheManager.setObject(CacheConstants.CACHE_LOCATION+ operatorId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                        }
                    } else {
                        ArrayDeque<Long> locIds = new ArrayDeque<Long>();
                        locIds.addFirst(locationId);
                        tipLocationCmd.setTipLocationIds(locIds);
                        cacheManager.setObject(CacheConstants.CACHE_LOCATION + operatorId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                    }
                    scanResult.setTipLocationId(tipLocationId);
                    scanResult.setIsPicking(false);  // 没有上架结束
                }else{
                    scanResult.setIsPicking(true); //所有库位已经扫描完毕
                }
                
            }
        }
        log.info("pdaManmadePutawayCacheManager containerPutawayCacheInsideContainer is start");
        return scanResult;
    }  
    /***
    
    /***
     * 提示小车
     * @param operatorId
     * @return
     */
    @Override
    public String pdaPickingWorkTipOutContainer(Long operatorId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        String tipOuterContainer = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
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
                if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    continue;
                }
                // 验证容器状态是否是待上架
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE))) {
                    continue;
                }
                tipOuterContainer = container.getCode();
                break;
            }
        }
        if(StringUtil.isEmpty(tipOuterContainer)) {
            throw new BusinessException(ErrorCodes.OUT_CONTAINER_IS_NOT_NORMAL);
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is end");
        return tipOuterContainer;
    }

    /***
     * 提示出库箱
     * @param operatorId
     * @return
     */
    @Override
    public String pdaPickingWorkTipoutboundBox(Long operatorId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        String outbounxBox = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
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
     * @param operatorId
     * @return
     */
    @Override
    public String pdaPickingWorkTipTurnoverBox(Long operatorId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is start");
        String turnoverBox = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
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
     * @param operatorId
     * @param operatioLineStatisticsCommand
     * @return
     */
    @Override
    public void operatioLineStatisticsRedis(Long operatorId, OperatioLineStatisticsCommand operatioLineStatisticsCommand) {
        cacheManager.setObject(operatorId.toString(), operatioLineStatisticsCommand);
    }

    /**
     * 提示库位
     * 
     * @author qiming.liu
     * @param operatorId
     * @param ouId
     * @return
     */
    @Override
    public OperatioLineStatisticsCommand pdaPickingWorkTipWholeCase(Long operatorId, Long ouId) {
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
        return operatorLine;
    }

    /***
     * pda拣货提示托盘
     * @author tangming
     * @param outerContainerIds
     * @param locationId
     * @return
     */
      public CheckScanResultCommand pdaPickingTipOuterContainer(Set<Long> outerContainerIds,Long operatorId){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipOuterContainer is start");
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          Long tipOuterContainerId = null;
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
          if(null == tipLocationCmd) {
              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
          }else{
              ArrayDeque<Long> tipOuterContainerIds = tipLocationCmd.getTipOuterContainerIds();
              if(this.isCacheAllExists(outerContainerIds, tipOuterContainerIds)) {//库位上托盘没有扫描结束
                  for (Long oc : outerContainerIds) {
                      Long ocId = oc;
                      if (null != ocId) {
                          if(null == tipOuterContainerIds) {
                              tipOuterContainerIds = new ArrayDeque<Long>();
                              tipOuterContainerIds.addFirst(ocId);
                          }else{
                              //判断改外部容器id是否已经存在缓存中
                              if(tipOuterContainerIds.contains(ocId)) {
                                  continue;
                              }
                              tipOuterContainerIds.addFirst(ocId);
                          }
                          //存入缓存
                          tipOuterContainerId = ocId;
                          cacheManager.setObject(CacheConstants.CACHE_LOCATION + operatorId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                          break;
                      }
                   }
                  scanResult.setIsNeedTipOutContainer(false); 
                  scanResult.setTipOuterContainerId(tipOuterContainerId);
              }else{
                  scanResult.setIsNeedTipOutContainer(true); // 所有的外部容器已经扫描完毕
              }
             
              
          }
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipOuterContainer is end");
          return scanResult;
      }
      
      
      /***
       * pda拣货提示货箱
       * @author tangming
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipInsideContainer(Set<Long> insideContainerIds,Long operatorId){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipInsideContainer is start");
          Long tipInsideContainerId = null;
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
          if(null == tipLocationCmd) {
              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
          }else{
              ArrayDeque<Long> tipInsideContainerIds = tipLocationCmd.getTipInsideContainerIds();
              if(this.isCacheAllExists(insideContainerIds, tipInsideContainerIds)) {
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
                      cacheManager.setObject(CacheConstants.CACHE_LOCATION + operatorId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                      break;
                  }
                  scanResult.setIsNeedTipInsideContainer(false);
                  scanResult.setTipiInsideContainerId(tipInsideContainerId);
              }else{  //所有的内部容器全部扫描完毕
                  scanResult.setIsNeedTipInsideContainer(true);
              }
              
          }
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipInsideContainer is end");
          return scanResult;
      }
      /***
       * pda拣货提示sku
       * @param insideContainerIds
       * @param operatorId
       * @return
       */
      public CheckScanResultCommand pdaPickingTipSku(Set<Long> skuIds,Long operatorId,Long locationId,Long ouId,Long insideContainerId){
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipSku is start");
          CheckScanResultCommand scanResult = new CheckScanResultCommand();
          //该库位要拣货的所有库存记录
          List<WhSkuInventoryCommand> list = this.cacheLocationInventory(operatorId, locationId, ouId);
          LocationTipCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
          if(null == tipLocationCmd) {
              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
          }else{
              ArrayDeque<Long> tipSkuIds = tipLocationCmd.getTipLocSkuIds();
              if(this.isCacheAllExists(skuIds, tipSkuIds)) {
                //缓存sku唯一标示
                  String skuAttrId = null;
                  for(Long skuId:skuIds) {
                      if(null == tipSkuIds || tipSkuIds.isEmpty()) {
                          tipSkuIds = new ArrayDeque<Long>();
                          tipSkuIds.addFirst(skuId);
                      }else{
                          //判断改外部容器id是否已经存在缓存中
                          if(tipSkuIds.contains(skuId)) {
                              continue;
                          }
                          tipSkuIds.addFirst(skuId);
                          for(WhSkuInventoryCommand skuCmd:list) {
                              if(null != insideContainerId) { //有货箱
                                  if(insideContainerId.equals(skuCmd.getInsideContainerId())) {
                                      if(skuId.longValue() == skuCmd.getSkuId().longValue()) {
                                             skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                                       }
                                  }
                              }else{//散装
                                  if(skuId.longValue() == skuCmd.getSkuId().longValue()) {
                                      skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
                                   }
                                  //把直接放在库位上的sku，放入缓存
                                  LocationTipCacheCommand cacheLocationCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
                                  if(null == cacheLocationCmd) {
                                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                                  }else{
                                      ArrayDeque<Long>  tipLocSkuIds = cacheLocationCmd.getTipLocSkuIds();
                                      if(null == tipLocSkuIds) {
                                          tipLocSkuIds = new ArrayDeque<Long>();
                                          tipLocSkuIds.addFirst(skuId);
                                      }else{
                                          tipLocSkuIds.addFirst(skuId);
                                      }
                                      cacheLocationCmd.setTipLocSkuIds(tipLocSkuIds);
                                      cacheManager.setObject(CacheConstants.CACHE_LOCATION + operatorId.toString(), cacheLocationCmd, CacheConstants.CACHE_ONE_DAY);
                                  }
                              }
                          }
                          ArrayDeque<String> skuAttrIds = cacheManager.getObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+skuId.toString());
                          if(null == skuAttrIds) {
                              skuAttrIds = new ArrayDeque<String>();
                              skuAttrIds.addFirst(skuAttrId);
                          }else{
                              skuAttrIds.addFirst(skuAttrId);
                          }
                          cacheManager.setObject(CacheConstants.CACHE_LOCATION + locationId.toString()+skuId.toString(), skuAttrIds, CacheConstants.CACHE_ONE_DAY);
                      }
                       //存入缓存
                      cacheManager.setObject(CacheConstants.CACHE_LOCATION + operatorId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
                      break;
                  }
                  scanResult.setIsNeedTipSku(true);
                  scanResult.setTipSkuAttrId(skuAttrId);
              }else{  //所有的内部容器全部扫描完毕
                  scanResult.setIsNeedTipSku(false);
              }
              
          }
          log.info("PdaPickingWorkCacheManagerImpl pdaPickingTipSku is end");
          return scanResult;
      }
      
      /***
       * 
       * @param locationIds(一次作业的所有库位集合)
       * @param locSkuQty( 库位上每个sku总件数(sku不在任何容器内))
       * @param locationId(当前扫描的库位id)
       * @param locSkuIds(库位上所有sku(sku不在任何容器内))
       * @param outerContainerIds(库位上所有外部容器)
       * @param outerContainerCmd(外部容器)
       * @param operatorId(作业id)
       * @param insideContainerSkuIdsQty(内部容器每个sku总件数)
       * @param insideContainerSkuIds( 内部容器对应所有sku)
       * @param locInsideContainerIds(库位上所有的内部容器(无外部容器情况))
       * @param insideContainerIds(库位上有外部容器的内部容器)
       * @param insideContainerCmd(扫描的内部容器)
       * @param skuCmd(扫描的sku)
       * @return
       */
      public CheckScanResultCommand pdaPickingyCacheSkuAndCheckContainer(List<Long> locationIds, Map<Long, Long> locSkuQty,Long locationId,Set<Long> locSkuIds,Set<Long> outerContainerIds,ContainerCommand outerContainerCmd,Long operatorId,Map<Long,Long> insideContainerSkuIdsQty,Map<Long, Set<Long>> insideContainerSkuIds,Set<Long> insideContainerIds,Set<Long> locInsideContainerIds,ContainerCommand insideContainerCmd,WhSkuCommand skuCmd){
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
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
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
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
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
                  }
              }
              if (false == skuExists) {
                  log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, skuId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
              }
          }else if(null == outerContainerCmd && null == insideContainerCmd){//  sku直接放在库位上
              Long skuId = skuCmd.getId();
              LocationTipCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.CACHE_LOCATION + operatorId.toString());
              ArrayDeque<Long> cacheLocaitionIds = null;
              if (null != cacheContainerCmd) {
                  cacheLocaitionIds = cacheContainerCmd.getTipLocationIds();
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
              ArrayDeque<Long> cacheTipSkuIds =  null;
              if(null != cacheContainerCmd) {
                  cacheTipSkuIds = cacheContainerCmd.getTipLocSkuIds();
                  Long value = cacheTipSkuIds.peekFirst();// 判断当前库位是否是队列的第一个
                  if (null == value) value = -1L;
                  if (0 != value.compareTo(locationId)) {
                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                  }
              }else {
                  log.error("scan container queue is exception, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

              }
              Double skuQty = skuCmd.getScanSkuQty();
              boolean skuExists = false;
              for (Long sId : locSkuIds) {
                  if (0 == skuId.compareTo(sId)) {
                      skuExists = true;
                      Long icSkuQty = locSkuQty.get(sId);
                          ScanTipSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operatorId.toString());
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
      private boolean isCacheAllExists2(List<Long> locaiotnIds, ArrayDeque<Long> cacheKeys) {
          boolean allExists = true;  //默认没有复合完毕
          if (null != cacheKeys && !cacheKeys.isEmpty()) {
              for (Long id : locaiotnIds) {
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
}
